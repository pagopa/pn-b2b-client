import copy
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from sdp_generator import load_manifest, render, validate

ROOT = Path(__file__).resolve().parents[4]
SCRIPT = Path(__file__).resolve().parents[1] / 'sdp_generator.py'


class SDPTests(unittest.TestCase):
    def setUp(self):
        self.release = load_manifest(ROOT / 'releases/template.yaml')

    def test_template_and_all_release_states(self):
        for status in ('draft', 'planned', 'in-progress', 'execution', 'closed'):
            self.release['status'] = status
            validate(self.release)
            output = render(self.release)
            self.assertIn(f'Stato release:** {status}', output)
            self.assertIn('QA Epic da creare', output)
            self.assertIn('PN-21118', output)

    def test_duplicate_and_overlapping_work_items(self):
        for kind in ('sequence', 'epic', 'excluded', 'overlap'):
            with self.subTest(kind=kind):
                r = copy.deepcopy(self.release)
                if kind in ('sequence', 'epic'):
                    item = r['work-items'][0]
                    item['qa_epic'] = 'QA-123'
                    duplicate = copy.deepcopy(item)
                    if kind == 'epic':
                        duplicate['sequence'] = 2
                    r['work-items'].append(duplicate)
                elif kind == 'excluded':
                    r['out_of_scope'].append(r['out_of_scope'][0])
                else:
                    r['out_of_scope'][0]['id'] = r['work-items'][0]['id']
                with self.assertRaises(ValueError):
                    validate(r)

    def test_invalid_data(self):
        for field, value in [('size', True), ('size', 0), ('size', float('nan')),
                             ('strategies', ['UNKNOWN']), ('resources', [{}]),
                             ('dependencies', 'not a list'), ('dependencies', 0),
                             ('dependencies', False), ('dependencies', {}),
                             ('dependencies', [123]), ('status', 'done')]:
            with self.subTest(field=field, value=value):
                r = copy.deepcopy(self.release)
                r['work-items'][0][field] = value
                with self.assertRaises(ValueError):
                    validate(r)
        self.release['delivery']['test'] = '2026-02-30'
        with self.assertRaises(ValueError):
            validate(self.release)

    def test_optional_dependencies(self):
        for value in (None, [], ['Ambiente disponibile']):
            with self.subTest(value=value):
                r = copy.deepcopy(self.release)
                for item in r['work-items']:
                    item['dependencies'] = value
                validate(r)
                output = render(r)
                if value:
                    self.assertIn('Dipendenze:** Ambiente disponibile', output)
                else:
                    self.assertNotIn('Dipendenze:', output)
        for item in r['work-items']:
            item.pop('dependencies')
        validate(r)
        self.assertNotIn('Dipendenze:', render(r))

    def test_missing_optional_data_and_exclusions_only(self):
        self.release['work-items'] = []
        self.release.pop('delivery')
        validate(self.release)
        self.assertIn('Nessuna attività QA pianificata.', render(self.release))

    def test_jira_status_not_invented_and_text_escaped(self):
        item = self.release['work-items'][0]
        item['qa_epic'] = 'QA-123'
        item['title'] = None
        item['notes'] = 'a | b\n<script>bad</script>'
        output = render(self.release)
        self.assertIn('Jira non interrogato', output)
        self.assertIn('Titolo da recuperare da Jira', output)
        self.assertIn('a &#124; b<br>&lt;script&gt;', output)
        self.assertNotIn('<script>', output)

    def test_duplicate_yaml_keys_and_cli_errors(self):
        with tempfile.TemporaryDirectory() as tmp:
            path = Path(tmp) / 'invalid.yaml'
            path.write_text('release:\n  status: draft\n  status: closed\n')
            with self.assertRaisesRegex(ValueError, 'duplicata'):
                load_manifest(path)
            result = subprocess.run([sys.executable, str(SCRIPT), str(path)], capture_output=True, text=True)
            self.assertEqual(result.returncode, 1)
            self.assertEqual(result.stdout, '')
            self.assertIn('Errore SDP:', result.stderr)

    def test_cli_template(self):
        result = subprocess.run([sys.executable, str(SCRIPT), str(ROOT / 'releases/template.yaml'), '--dry-run'], capture_output=True, text=True)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertTrue(result.stdout.startswith('**Trimestre:**'))


if __name__ == '__main__':
    unittest.main()
