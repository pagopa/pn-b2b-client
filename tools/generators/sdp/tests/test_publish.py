import copy
from contextlib import redirect_stdout
import io
import json
from pathlib import Path
import sys
import unittest
from unittest.mock import patch
from urllib.error import HTTPError

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from confluence import Client, publish
from sdp_generator import load_manifest, main, render

ROOT = Path(__file__).resolve().parents[4]


class FakeClient:
    def __init__(self):
        self.stored = []
        self.writes = []
        self.fail = False

    def pages(self, space):
        return copy.deepcopy(self.stored)

    def call(self, method, path, payload=None):
        if path == '/folders/3459022926':
            return {'id': '3459022926', 'spaceId': '10'}
        if method == 'GET':
            return copy.deepcopy(self.stored[0])
        self.writes.append((method, path, payload))
        if self.fail:
            raise ValueError('HTTP 503')
        if method == 'POST':
            self.stored.append(dict(payload, id='20', version={'number': 1}))
        else:
            self.stored[0].update(payload)
        # API response wraps the written body under storage.
        self.stored[0]['body'] = {'storage': {'value': payload['body']['value']}}
        return {'id': '20'}


class PublishTests(unittest.TestCase):
    def setUp(self):
        self.release = load_manifest(ROOT / 'releases/template.yaml')
        self.release['status'] = 'planned'
        self.client = FakeClient()

    def publish(self):
        return publish(self.release, render(self.release, storage=True), self.client)

    def test_create_then_update_renamed_page_preserves_identity_and_history(self):
        self.assertIn('creata', self.publish())
        first = self.client.writes[0]
        self.assertEqual(first[2]['parentId'], '3459022926')
        self.assertIn('<table>', first[2]['body']['value'])
        self.client.stored[0]['title'] = 'Titolo modificato a mano'
        self.release['description'] = 'Aggiornata'
        self.assertIn('aggiornata', self.publish())
        method, path, payload = self.client.writes[-1]
        self.assertEqual((method, path), ('PUT', '/pages/20'))
        self.assertEqual(payload['version']['number'], 2)
        self.assertEqual(payload['title'], 'Titolo modificato a mano')
        self.assertIn('Aggiornata', payload['body']['value'])
        self.assertEqual(len(self.client.stored), 1)

    def test_closed_retry_skip_and_reopen(self):
        self.release['status'] = 'closed'
        self.client.fail = True
        with self.assertRaises(ValueError):
            self.publish()
        self.assertEqual(self.client.stored, [])
        self.client.fail = False
        self.publish()
        count = len(self.client.writes)
        self.assertIn('già pubblicata', self.publish())
        self.assertEqual(len(self.client.writes), count)
        self.release['status'] = 'execution'
        self.publish()
        self.release['status'] = 'closed'
        self.publish()
        self.assertEqual(self.client.stored[0]['version']['number'], 3)

    def test_duplicate_identity_title_collision_and_moved_page_fail_closed(self):
        for problem in ('duplicate', 'title', 'moved', 'archived', 'missing_body'):
            with self.subTest(problem=problem):
                self.client = FakeClient()
                self.publish()
                page = self.client.stored[0]
                if problem == 'duplicate':
                    self.client.stored.append(copy.deepcopy(page))
                elif problem == 'title':
                    page['body']['storage']['value'] = '<p>Manual page</p>'
                elif problem == 'moved':
                    page['parentId'] = '99'
                elif problem == 'archived':
                    page['status'] = 'archived'
                else:
                    del page['body']
                with self.assertRaises(ValueError):
                    self.publish()
                self.assertEqual(len(self.client.writes), 1)

    def test_draft_and_dry_run_do_not_access_confluence(self):
        with patch('confluence.Client', side_effect=AssertionError('Network forbidden')):
            self.release['status'] = 'draft'
            self.assertIn('esclusa', publish(self.release, ''))
            with redirect_stdout(io.StringIO()) as output:
                self.assertEqual(main([str(ROOT / 'releases/template.yaml'), '--dry-run']), 0)
            self.assertIn('# Service Delivery Plan', output.getvalue())

    def test_cli_publishes_by_default(self):
        with patch('sdp_generator.load_manifest', return_value=self.release), patch('confluence.publish', return_value='OK') as call:
            with redirect_stdout(io.StringIO()):
                self.assertEqual(main(['unused.yaml']), 0)
            call.assert_called_once()
            self.assertIn('<table>', call.call_args.args[1])

    def test_storage_escapes_input(self):
        self.release['description'] = '<script>& "hello"\nnext'
        content = render(self.release, storage=True)
        self.assertNotIn('<script>', content)
        self.assertIn('&lt;script&gt;&amp; &quot;hello&quot;<br />next', content)

    def test_pagination_extracts_only_cursor_and_detects_loop(self):
        client = object.__new__(Client)
        responses = [{'results': [{'id': '1'}], '_links': {'next': 'https://other.invalid/x?cursor=abc'}},
                     {'results': [{'id': '2'}], '_links': {}}]
        with patch.object(client, 'call', side_effect=responses) as call:
            self.assertEqual([p['id'] for p in client.pages('10')], ['1', '2'])
            self.assertIn('cursor=abc', call.call_args.args[1])
            self.assertNotIn('other.invalid', call.call_args.args[1])
        with patch.object(client, 'call', return_value=responses[0]):
            with self.assertRaisesRegex(ValueError, 'Paginazione'):
                list(client.pages('10'))

    def test_transport_payload_and_sanitized_errors(self):
        env = {'JIRA_CLOUD_ID': 'f985e071-a2d6-442d-a5f4-a425cf8beb72', 'CONFLUENCE_API_TOKEN': 'secret'}
        with patch.dict('os.environ', env), patch('confluence.request.build_opener') as opener:
            client = Client()
            opener.return_value.open.return_value.__enter__.return_value = io.StringIO('{"id":"20"}')
            self.assertEqual(client.call('POST', '/pages', {'title': 'Piano'}), {'id': '20'})
            req = opener.return_value.open.call_args.args[0]
            self.assertEqual(req.get_method(), 'POST')
            self.assertEqual(json.loads(req.data), {'title': 'Piano'})
            opener.return_value.open.side_effect = HTTPError(req.full_url, 403, 'secret', {}, None)
            with self.assertRaisesRegex(ValueError, 'HTTP 403') as exc:
                client.call('POST', '/pages', {})
            self.assertNotIn('secret', str(exc.exception))


if __name__ == '__main__':
    unittest.main()
