import io
import json
import unittest
from unittest.mock import patch

from pst_generator.jira import issue_metadata


class JiraTest(unittest.TestCase):
    def test_reads_base_url_and_issue_statuses(self):
        responses = iter([
            {'baseUrl': 'https://example.atlassian.net'},
            {'fields': {'status': {'name': 'In Progress'}}},
            {'fields': {'status': {'name': 'Done'}}},
        ])

        class Response(io.BytesIO):
            def __enter__(self):
                return self

            def __exit__(self, *args):
                self.close()

        with patch.dict('os.environ', {'JIRA_CLOUD_ID': 'cloud', 'JIRA_API_TOKEN': 'token'}, clear=True), \
             patch('pst_generator.jira.request.urlopen',
                   side_effect=lambda *args, **kwargs: Response(json.dumps(next(responses)).encode())) as get:
            base, statuses, warning = issue_metadata(['QA-123', 'PN-456', 'QA-123'])
        self.assertEqual(base, 'https://example.atlassian.net')
        self.assertEqual(statuses, {'QA-123': 'In Progress', 'PN-456': 'Done'})
        self.assertIsNone(warning)
        self.assertEqual(get.call_count, 3)

    def test_missing_credentials_does_not_call_jira(self):
        with patch.dict('os.environ', {'JIRA_BASE_URL': 'https://example.atlassian.net'}, clear=True), \
             patch('pst_generator.jira.request.urlopen') as get:
            base, statuses, warning = issue_metadata(['QA-123'])
        self.assertEqual(base, 'https://example.atlassian.net')
        self.assertEqual(statuses, {})
        self.assertIn('non disponibili', warning)
        get.assert_not_called()

    def test_one_issue_failure_keeps_other_statuses(self):
        from urllib.error import HTTPError

        class Response(io.BytesIO):
            def __enter__(self):
                return self

            def __exit__(self, *args):
                self.close()

        def respond(req, timeout):
            if 'PN-456' in req.full_url:
                raise HTTPError(req.full_url, 404, 'Not found', {}, None)
            return Response(b'{"fields":{"status":{"name":"Done"}}}')

        with patch.dict('os.environ', {'JIRA_CLOUD_ID': 'cloud', 'JIRA_API_TOKEN': 'token',
                                      'JIRA_BASE_URL': 'https://example.atlassian.net'}, clear=True), \
             patch('pst_generator.jira.request.urlopen', side_effect=respond):
            base, statuses, warning = issue_metadata(['QA-123', 'PN-456'])
        self.assertEqual(base, 'https://example.atlassian.net')
        self.assertEqual(statuses, {'QA-123': 'Done'})
        self.assertIn('PN-456', warning)

    def test_truncated_response_becomes_warning(self):
        from http.client import IncompleteRead

        with patch.dict('os.environ', {'JIRA_CLOUD_ID': 'cloud', 'JIRA_API_TOKEN': 'token',
                                      'JIRA_BASE_URL': 'https://example.atlassian.net'}, clear=True), \
             patch('pst_generator.jira.request.urlopen', side_effect=IncompleteRead(b'partial')):
            base, statuses, warning = issue_metadata(['QA-123'])
        self.assertEqual(base, 'https://example.atlassian.net')
        self.assertEqual(statuses, {})
        self.assertIn('QA-123', warning)
