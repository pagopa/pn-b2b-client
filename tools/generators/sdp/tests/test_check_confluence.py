"""Offline regression tests for the read-only Confluence probe."""

from contextlib import redirect_stderr, redirect_stdout
from http.client import IncompleteRead
import io
import json
from pathlib import Path
import sys
import unittest
from unittest.mock import MagicMock, patch
from urllib import error, request

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import check_confluence


class ConfluenceCheckTests(unittest.TestCase):
    CLOUD = '12345678-1234-1234-1234-123456789abc'
    TOKEN = 'private-test-token'
    BODY = 'private-response-content'
    PAGE = '3459022926'

    def setUp(self):
        self.environment = patch.dict(check_confluence.os.environ, {
            'JIRA_CLOUD_ID': self.CLOUD, 'CONFLUENCE_API_TOKEN': self.TOKEN,
        }, clear=True)
        self.environment.start()
        self.addCleanup(self.environment.stop)
        config = patch.object(check_confluence.Path, 'read_text', return_value=
                              f'confluence:\n  parent_type: folder\n  parent_id: "{self.PAGE}"\n')
        config.start()
        self.addCleanup(config.stop)
        # The entire transport is replaced: no credentials or network needed.
        self.opener = MagicMock()
        builder = patch.object(check_confluence.request, 'build_opener', return_value=self.opener)
        self.builder = builder.start()
        self.addCleanup(builder.stop)

    def response(self, body):
        self.opener.open.return_value.__enter__.return_value = io.StringIO(body)

    def run_check(self, expected):
        out, err = io.StringIO(), io.StringIO()
        with redirect_stdout(out), redirect_stderr(err):
            self.assertEqual(check_confluence.main(), expected)
        combined = out.getvalue() + err.getvalue()
        for private in (self.TOKEN, self.BODY, 'Authorization', 'Traceback'):
            self.assertNotIn(private, combined)
        if expected:
            self.assertEqual(out.getvalue(), '')
            self.assertTrue(err.getvalue())
        else:
            self.assertEqual(err.getvalue(), '')
        return combined

    def test_valid_folder_uses_read_only_request(self):
        self.response(json.dumps({'id': self.PAGE, 'spaceId': '123', 'body': self.BODY}))
        output = self.run_check(0)
        self.assertIn('leggibile', output)
        self.assertIn('Permessi di scrittura non verificati', output)
        self.opener.open.assert_called_once()
        req = self.opener.open.call_args.args[0]
        self.assertEqual(req.get_method(), 'GET')
        self.assertIsNone(req.data)
        self.assertEqual(req.full_url, f'https://api.atlassian.com/ex/confluence/{self.CLOUD}/wiki/api/v2/folders/{self.PAGE}')
        self.assertEqual(req.get_header('Authorization'), f'Bearer {self.TOKEN}')
        self.assertEqual(self.opener.open.call_args.kwargs['timeout'], 30)
        self.assertIsInstance(self.builder.call_args.args[0], check_confluence.NoRedirect)

    def test_unexpected_page_responses(self):
        for page in ({'id': '999', 'spaceId': '123'}, {'id': self.PAGE},
                     {'id': self.PAGE, 'spaceId': None}, [], None):
            with self.subTest(page=page):
                self.response(json.dumps(page))
                self.assertIn('risposta JSON non valida', self.run_check(1))

    def test_invalid_json(self):
        self.response(self.BODY)
        self.assertIn('risposta JSON non valida', self.run_check(1))

    def test_http_failures_do_not_log_response_body(self):
        for status in (401, 403, 404, 429, 500):
            with self.subTest(status=status):
                self.opener.open.side_effect = error.HTTPError(
                    'https://api.atlassian.com', status, self.TOKEN,
                    {}, io.BytesIO(self.BODY.encode()))
                self.assertIn(f'HTTP {status}', self.run_check(1))

    def test_network_failure(self):
        self.opener.open.side_effect = error.URLError(self.TOKEN)
        self.assertIn('Connessione Confluence', self.run_check(1))

    def test_truncated_http_response(self):
        response = MagicMock()
        response.read.side_effect = IncompleteRead(self.BODY.encode(), 100)
        self.opener.open.return_value.__enter__.return_value = response
        self.assertIn('Connessione Confluence', self.run_check(1))

    def test_redirects_are_refused(self):
        handler = check_confluence.NoRedirect()
        director = request.OpenerDirector()
        director.add_handler(handler)
        director.add_handler(request.HTTPDefaultErrorHandler())
        director.open = MagicMock(side_effect=AssertionError('Redirect followed'))
        req = request.Request('https://api.atlassian.com', headers={
            'Authorization': f'Bearer {self.TOKEN}'})
        for status in (301, 302, 303, 307, 308):
            with self.subTest(status=status):
                # Exercise urllib's real redirect handling, without transport.
                with self.assertRaises(error.HTTPError) as raised:
                    director.error('http', req, io.BytesIO(self.BODY.encode()), status,
                                   'Redirect', {'location': 'https://example.org/'})
                self.assertEqual(raised.exception.code, status)
                director.open.assert_not_called()
                self.opener.open.side_effect = raised.exception
                self.assertIn(f'HTTP {status}', self.run_check(1))

    def test_missing_credentials_prevent_requests(self):
        with patch.dict(check_confluence.os.environ, {}, clear=True):
            self.run_check(1)
        self.builder.assert_not_called()

    def test_page_override(self):
        self.response(json.dumps({'id': '3494707940', 'spaceId': '123'}))
        with patch.dict(check_confluence.os.environ, {'CONFLUENCE_CHECK_PAGE_ID': '3494707940'}):
            self.assertIn('3494707940', self.run_check(0))
        self.assertTrue(self.opener.open.call_args.args[0].full_url.endswith('/pages/3494707940'))

    def test_invalid_page_override_prevents_request(self):
        with patch.dict(check_confluence.os.environ, {'CONFLUENCE_CHECK_PAGE_ID': '../other'}):
            self.run_check(1)
        self.builder.assert_not_called()

    def test_jira_token_is_not_used_as_fallback(self):
        with patch.dict(check_confluence.os.environ, {
            'JIRA_CLOUD_ID': self.CLOUD, 'JIRA_API_TOKEN': self.TOKEN,
        }, clear=True):
            self.assertIn('CONFLUENCE_API_TOKEN', self.run_check(1))
        self.builder.assert_not_called()


if __name__ == '__main__':
    unittest.main()
