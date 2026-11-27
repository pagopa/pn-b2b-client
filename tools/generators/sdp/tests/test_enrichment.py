import io
import copy
from urllib.error import HTTPError
from contextlib import redirect_stdout, redirect_stderr
import os
from pathlib import Path
import sys
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from enrichment import enrich, read_json
from sdp_generator import load_manifest, render, main

ROOT = Path(__file__).resolve().parents[4]


class EnrichmentTests(unittest.TestCase):
    def setUp(self):
        self.release = load_manifest(ROOT / 'releases/template.yaml')
        self.item = self.release['work-items'][0]
        self.item.update(title=None, qa_epic='QA-123')

    def read(self, url, token, missing_ok=False):
        if 'api.github.com' in url:
            return {'type': 'file'}
        return {'fields': {'summary': '<Titolo> & Jira', 'status': {
            'name': 'In lavorazione', 'statusCategory': {'key': 'indeterminate'}}}}

    @patch.dict(os.environ, {'JIRA_API_TOKEN': 'secret', 'JIRA_CLOUD_ID': 'f985e071-a2d6-442d-a5f4-a425cf8beb72'})
    def test_enrichment_render_and_no_manifest_mutation(self):
        before = copy.deepcopy(self.release)
        result = enrich(self.release, self.read)
        self.assertEqual(self.release, before)
        self.assertEqual(result['work-items'][0]['_qa_status'], 'In lavorazione')
        for storage in (False, True):
            output = render(result, storage=storage)
            self.assertIn('In lavorazione', output)
            self.assertIn('&lt;Titolo&gt;', output)
            self.assertIn('pst-output/pst_QA-123.md', output)
            self.assertNotIn('Seq.', output)
            self.assertNotIn('Note / Rischi / Dipendenze', output)
            self.assertIn('Quadro di sintesi', output)
            self.assertIn('Note, rischi e dipendenze', output)
        self.assertIn('href="https://pagopa.atlassian.net/browse/QA-123"', render(result, storage=True))

    @patch.dict(os.environ, {'JIRA_API_TOKEN': 'secret', 'JIRA_CLOUD_ID': 'f985e071-a2d6-442d-a5f4-a425cf8beb72'})
    def test_missing_pst_and_cached_epic(self):
        duplicate = copy.deepcopy(self.item)
        duplicate.update(id='PN-999', title='Titolo esplicito')
        self.release['work-items'].append(duplicate)
        calls = []
        def read(url, token, missing_ok=False):
            calls.append(url)
            return None if missing_ok else self.read(url, token)
        result = enrich(self.release, read)
        self.assertNotIn('_pst_url', result['work-items'][0])
        self.assertEqual(sum('api.github.com' in u for u in calls), 1)
        self.assertEqual(sum('/issue/QA-123?' in u for u in calls), 1)
        self.assertEqual(result['work-items'][1]['title'], 'Titolo esplicito')

    @patch.dict(os.environ, {'JIRA_API_TOKEN': ''})
    def test_missing_credentials_fail(self):
        with self.assertRaisesRegex(ValueError, 'JIRA_API_TOKEN'):
            enrich(self.release, self.read)

    def test_summary_unknown_and_unestimated(self):
        self.item['size'] = None
        output = render(self.release)
        self.assertIn('0 gg/uomo su 0 attività; 1 da stimare', output)
        self.assertIn('| 1 | 0 | 0 | 0 | 1 |', output)

    def test_transport_errors_are_sanitized_and_only_404_is_missing(self):
        with patch('enrichment.request.build_opener') as opener:
            for code in (403, 404, 500):
                opener.return_value.open.side_effect = HTTPError('https://api.github.com/test', code, 'secret', {}, None)
                if code == 404:
                    self.assertIsNone(read_json('https://api.github.com/test', 'secret', missing_ok=True))
                else:
                    with self.assertRaisesRegex(ValueError, f'HTTP {code}') as raised:
                        read_json('https://api.github.com/test', 'secret', missing_ok=True)
                    self.assertNotIn('secret', str(raised.exception))

    def test_read_failure_prevents_publication(self):
        self.release['status'] = 'in-progress'
        with patch('sdp_generator.load_manifest', return_value=self.release), patch('enrichment.enrich', side_effect=ValueError('Jira unavailable')), patch('confluence.publish') as publish:
            with redirect_stdout(io.StringIO()), redirect_stderr(io.StringIO()):
                self.assertEqual(main(['unused.yaml']), 1)
            publish.assert_not_called()
