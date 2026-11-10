import contextlib
import io
import os
from pathlib import Path
import subprocess
import sys
from tempfile import TemporaryDirectory
import unittest
from collections import Counter
from unittest.mock import patch

from pst_generator.collection import (ScenarioCollector, CollectionError,
                                      active_source_specs, collect_from_sources)
from pst_generator.cli import generate_active, generate_pst, generate_structured_html
from pst_generator.core import parse_test_cases
from pst_generator.markdown import render_document


class CollectionTest(unittest.TestCase):
    def test_no_tagged_scenarios_do_not_create_empty_pst(self):
        config = self.repo / 'config.yaml'
        config.write_text('{}', encoding='utf-8')
        with self.assertRaisesRegex(CollectionError, '@work-item:QA-999'):
            generate_pst(self.repo, 'QA-999', ['base'], config, include_html=True)
        self.assertFalse((self.repo / 'pst' / 'pst_QA-999.md').exists())

    def test_invalid_epic_cannot_escape_output_directory(self):
        config = self.repo / 'config.yaml'
        config.write_text('{}', encoding='utf-8')
        cases = self.collector().collect('QA-123', ['base'])
        output = self.repo / 'pst'
        with self.assertRaisesRegex(CollectionError, 'Invalid QA Epic'):
            generate_pst(self.repo, 'QA-123/../../escaped', [], config,
                         output_dir=output, cases=cases)
        self.assertFalse(output.exists())
        self.assertFalse((self.repo / 'escaped.md').exists())

    def test_invalid_epic_in_release_is_rejected(self):
        release = self.repo / 'releases/product/1.yaml'
        release.parent.mkdir(parents=True, exist_ok=True)
        release.write_text('release:\n  status: in-progress\n  work-items:\n'
                           '    - qa_epic: QA-123/../../escaped\n'
                           '      branch: base\n', encoding='utf-8')
        with self.assertRaisesRegex(CollectionError, 'Invalid QA Epic'):
            active_source_specs(self.repo)

    def setUp(self):
        self.temp = TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.repo = Path(self.temp.name)
        self.git('init', '-b', 'base')
        self.git('config', 'user.email', 'test@example.invalid')
        self.git('config', 'user.name', 'PST test')
        self.write_scenario('case.feature', 'CASE_1', 'base')
        self.commit('initial')

    def git(self, *args):
        return subprocess.run(['git', '-C', str(self.repo), *args], check=True,
                              capture_output=True, text=True).stdout.strip()

    def commit(self, message):
        self.git('add', '.')
        self.git('commit', '-m', message)

    def write_scenario(self, path, test_id, result, epic='QA-123'):
        (self.repo / path).write_text(
            f'Feature: Test\n  @work-item:{epic}\n  Scenario: [{test_id}] Example\n'
            f'    Given ready\n    Then {result}\n', encoding='utf-8')

    def collector(self):
        return ScenarioCollector(self.repo, r'\[([^\]]+)\]')

    def release(self):
        path = self.repo / 'releases' / 'product' / '1.yaml'
        path.parent.mkdir(parents=True)
        path.write_text('release:\n  status: in-progress\n  work-items:\n'
                        '    - qa_epic: QA-123\n      branch: feature/anna\n'
                        '    - qa_epic: QA-456\n      branch: feature/luca\n', encoding='utf-8')

    def test_cross_epic_branch_pool_and_identical_deduplication(self):
        self.git('checkout', '-b', 'feature/anna')
        self.git('checkout', '-b', 'feature/luca')
        self.write_scenario('second.feature', 'CASE_2', 'second')
        self.commit('second scenario for QA-123 on QA-456 branch')
        self.release()
        sources = active_source_specs(self.repo)
        self.assertEqual(sources['QA-123'], [('', 'feature/anna'), ('', 'feature/luca')])
        cases = self.collector().collect('QA-123', [branch for _, branch in sources['QA-123']])
        self.assertEqual([case.test_id for case in cases], ['CASE_1', 'CASE_2'])
        self.assertEqual(len(cases[0].sources), 2)

    def test_descendant_scenario_wins_even_with_unrelated_newer_branch_commit(self):
        self.git('checkout', '-b', 'feature/anna')
        self.write_scenario('case.feature', 'CASE_1', 'updated')
        self.commit('update scenario')
        self.git('checkout', '-b', 'feature/luca', 'base')
        (self.repo / 'unrelated.txt').write_text('newer branch activity')
        self.commit('unrelated')
        cases = self.collector().collect('QA-123', ['feature/anna', 'feature/luca'])
        self.assertEqual(cases[0].steps[-1].text, 'updated')
        self.assertEqual(cases[0].sources[0]['branch'], 'feature/anna')

    def divergent(self):
        self.git('checkout', '-b', 'feature/anna')
        self.write_scenario('case.feature', 'CASE_1', 'anna')
        self.commit('anna independent edit')
        self.git('checkout', '-b', 'feature/luca', 'base')
        self.write_scenario('case.feature', 'CASE_1', 'luca')
        self.write_scenario('second.feature', 'CASE_2', 'ok', 'QA-456')
        self.commit('luca independent edit')

    def test_independent_edits_conflict_despite_commit_dates(self):
        self.divergent()
        with self.assertRaises(CollectionError) as raised:
            self.collector().collect('QA-123', ['feature/anna', 'feature/luca'])
        for text in ['QA-123', 'CASE_1', 'feature/anna', 'feature/luca', 'case.feature']:
            self.assertIn(text, str(raised.exception))

    def test_merge_carrying_older_copy_does_not_hide_conflict(self):
        self.divergent()
        self.git('merge', '-s', 'ours', '--no-edit', 'feature/anna')
        with self.assertRaises(CollectionError):
            self.collector().collect('QA-123', ['feature/anna', 'feature/luca'])

    def test_resolved_merge_version_wins(self):
        self.divergent()
        self.git('merge', '-s', 'ours', '--no-edit', 'feature/anna')
        self.write_scenario('case.feature', 'CASE_1', 'combined')
        self.commit('resolve combined behavior')
        cases = self.collector().collect('QA-123', ['feature/anna', 'feature/luca'])
        self.assertEqual(cases[0].steps[-1].text, 'combined')

    def test_batch_preserves_conflicted_pst_and_generates_other_epic_with_summary(self):
        self.divergent()
        self.release()
        output = self.repo / 'pst'
        output.mkdir()
        previous = output / 'pst_QA-123.md'
        previous.write_text('previous valid document')
        previous.with_suffix('.html').write_text('previous valid HTML')
        config = self.repo / 'config.yaml'
        config.write_text('{}')
        summary = self.repo / 'summary.md'
        with patch.dict('os.environ', {'GITHUB_STEP_SUMMARY': str(summary)}):
            with contextlib.redirect_stdout(io.StringIO()), contextlib.redirect_stderr(io.StringIO()):
                status = generate_active(self.repo, config, include_html=True)
        self.assertEqual(status, 1)
        self.assertEqual(previous.read_text(), 'previous valid document')
        self.assertEqual(previous.with_suffix('.html').read_text(), 'previous valid HTML')
        self.assertIn('CASE_2', (output / 'pst_QA-456.md').read_text())
        self.assertIn(self.git('rev-parse', 'feature/luca'), (output / 'pst_QA-456.md').read_text())
        self.assertIn(self.git('rev-parse', 'feature/luca'), (output / 'pst_QA-456.html').read_text())
        self.assertIn('QA-123: ERRORE', summary.read_text())
        self.assertIn('QA-456: generato', summary.read_text())

    def test_multiple_branches_for_same_epic_and_release_isolation(self):
        self.release()
        path = self.repo / 'releases/product/1.yaml'
        path.write_text(path.read_text().replace('QA-456', 'QA-123'))
        other = path.with_name('2.yaml')
        other.write_text('release:\n  status: in-progress\n  work-items:\n    - qa_epic: QA-789\n')
        inactive = path.with_name('3.yaml')
        inactive.write_text('release:\n  status: done\n  work-items:\n    - qa_epic: QA-999\n')
        self.assertEqual(active_source_specs(self.repo), {
            'QA-123': [('', 'feature/anna'), ('', 'feature/luca')],
            'QA-789': [('', 'feature/QA-789')]})

    def test_ci_command_exports_only_current_successes(self):
        self.divergent()
        self.release()
        old = self.repo / 'pst'
        old.mkdir()
        (old / 'pst_QA-123.md').write_text('old document')
        config = self.repo / 'config.yaml'
        config.write_text('{}')
        artifacts = self.repo / 'artifacts' / 'generated'
        summary = self.repo / 'summary.md'
        result = subprocess.run([
            sys.executable, '-m', 'pst_generator.cli', 'generate-all',
            '--repo', str(self.repo), '--config', str(config),
            '--output-dir', str(artifacts), '--html',
        ], cwd=Path(__file__).resolve().parents[1], capture_output=True, text=True,
           env={**os.environ, 'GITHUB_STEP_SUMMARY': str(summary)})
        self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
        self.assertEqual(sorted(path.name for path in artifacts.iterdir()),
                         ['pst_QA-456.html', 'pst_QA-456.md'])
        self.assertEqual((old / 'pst_QA-123.md').read_text(), 'old document')
        self.assertIn('QA-123: ERRORE', summary.read_text())
        self.assertIn('QA-456: generato', summary.read_text())

    def test_remote_branch_and_missing_branch(self):
        self.git('update-ref', 'refs/remotes/origin/feature/remote', 'HEAD')
        self.assertEqual(len(self.collector().collect('QA-123', ['feature/remote'])), 1)
        with self.assertRaisesRegex(CollectionError, 'not found'):
            self.collector().collect('QA-123', ['feature/missing'])

    def test_named_repository_source_with_untagged_scenario_without_id(self):
        external = self.repo / 'source-repos' / 'qe_suit'
        external.parent.mkdir()
        subprocess.run(['git', 'clone', '-q', str(self.repo), str(external)], check=True)
        subprocess.run(['git', '-C', str(external), 'checkout', '-q', '-b', 'feature/external'], check=True)
        (external / 'case.feature').write_text(
            'Feature: Test\n  Scenario: temporary helper\n    Given ready\n'
            '  @work-item:QA-123\n  Scenario: [CASE_1] Example\n'
            '    Given ready\n    Then external\n', encoding='utf-8')
        subprocess.run(['git', '-C', str(external), 'add', '.'], check=True)
        subprocess.run(['git', '-C', str(external), '-c', 'user.email=test@example.invalid',
                        '-c', 'user.name=PST test', 'commit', '-qm', 'external'], check=True)
        release = self.repo / 'releases/product/1.yaml'
        release.parent.mkdir(parents=True, exist_ok=True)
        release.write_text('release:\n  status: in-progress\n  work-items:\n'
                           '    - qa_epic: QA-123\n      repo: qe_suit\n'
                           '      branch: feature/external\n', encoding='utf-8')
        specs = active_source_specs(self.repo)['QA-123']
        self.assertEqual(specs, [('qe_suit', 'feature/external')])
        cases = collect_from_sources(self.repo, 'QA-123', specs, r'\[([^\]]+)\]')
        self.assertEqual([case.test_id for case in cases], ['CASE_1'])
        self.assertEqual(cases[0].sources[0]['repo'], 'qe_suit')

    def test_default_repository_name_is_normalized(self):
        release = self.repo / 'releases/product/1.yaml'
        release.parent.mkdir(parents=True, exist_ok=True)
        release.write_text('release:\n  status: in-progress\n  work-items:\n'
                           '    - qa_epic: QA-123\n      repo: pn-b2b-client\n'
                           '      branch: base\n', encoding='utf-8')
        self.assertEqual(active_source_specs(self.repo)['QA-123'], [('', 'base')])

    def test_rejects_unlisted_repository_before_resolving_its_path(self):
        with self.assertRaisesRegex(CollectionError, 'Unsupported source repository'):
            collect_from_sources(self.repo, 'QA-123', [('../other', 'base')], r'\[([^\]]+)\]')
        release = self.repo / 'releases/product/1.yaml'
        release.parent.mkdir(parents=True, exist_ok=True)
        release.write_text('release:\n  status: in-progress\n  work-items:\n'
                           '    - qa_epic: QA-123\n      repo: ../other\n'
                           '      branch: base\n', encoding='utf-8')
        with self.assertRaisesRegex(CollectionError, 'Unsupported source repository'):
            active_source_specs(self.repo)

    def test_conflicting_duplicate_ids_in_same_branch_are_not_overwritten(self):
        self.write_scenario('duplicate.feature', 'CASE_1', 'different')
        self.commit('ambiguous ID')
        with self.assertRaisesRegex(CollectionError, 'CASE_1'):
            self.collector().collect('QA-123', ['base'])

    def test_docstring_changes_are_part_of_version_comparison(self):
        self.git('checkout', '-b', 'feature/anna')
        path = self.repo / 'case.feature'
        path.write_text(path.read_text() + '      """\n      anna\n      """\n')
        self.commit('anna docstring')
        self.git('checkout', '-b', 'feature/luca', 'base')
        path.write_text(path.read_text() + '      """\n      luca\n      """\n')
        self.commit('luca docstring')
        with self.assertRaises(CollectionError):
            self.collector().collect('QA-123', ['feature/anna', 'feature/luca'])

    def test_many_epics_share_branch_scan_and_identical_blob_parsing(self):
        source = 'Feature: Many epics\n'
        for index in range(30):
            source += (f'  @work-item:QA-{index}\n  Scenario: [CASE_{index}] Example\n'
                       '    Given ready\n    Then accepted\n')
        (self.repo / 'case.feature').write_text(source)
        self.commit('30 QA Epics')
        common = self.git('rev-parse', 'HEAD')
        branches = [f'feature/person-{index}' for index in range(4)]
        for branch in branches:
            self.git('checkout', '-b', branch, common)
            self.git('commit', '--allow-empty', '-m', f'unrelated work {branch}')
        collector = self.collector()
        with patch.object(collector, 'git', wraps=collector.git) as git_calls:
            with patch('pst_generator.collection.parse_test_cases', wraps=parse_test_cases) as parse:
                for index in range(30):
                    cases = collector.collect(f'QA-{index}', branches)
                    self.assertEqual([case.test_id for case in cases], [f'CASE_{index}'])
                    self.assertEqual(len(cases[0].sources), 4)
        counts = Counter(call.args[0] for call in git_calls.call_args_list)
        self.assertEqual(counts['grep'], 4)
        self.assertEqual(parse.call_count, 1)
        self.assertLess(sum(counts.values()), 25)

    def test_long_history_uses_batched_git_reads(self):
        self.git('checkout', '-b', 'feature/anna')
        self.write_scenario('case.feature', 'CASE_1', 'updated')
        self.commit('scenario updated')
        tree = self.git('rev-parse', 'base^{tree}')
        head = self.git('rev-parse', 'base')
        for index in range(120):
            head = self.git('commit-tree', tree, '-p', head, '-m', f'unrelated {index}')
        self.git('branch', 'feature/luca', head)
        collector = self.collector()
        with patch.object(collector, 'git', wraps=collector.git) as git_calls:
            with patch('pst_generator.collection.parse_test_cases', wraps=parse_test_cases) as parse:
                cases = collector.collect('QA-123', ['feature/anna', 'feature/luca'])
        self.assertEqual(cases[0].steps[-1].text, 'updated')
        self.assertEqual(parse.call_count, 2)
        self.assertLess(git_calls.call_count, 20)
        # Loading a later branch extends the cached graph without losing parents.
        self.write_scenario('case.feature', 'CASE_1', 'latest')
        self.commit('next scenario version')
        self.git('branch', 'feature/new')
        cases = collector.collect('QA-123', ['feature/luca', 'feature/new'])
        self.assertEqual(cases[0].steps[-1].text, 'latest')

    def test_invalid_file_for_another_epic_does_not_poison_shared_index(self):
        (self.repo / 'broken.feature').write_text('Feature: Broken\n  @work-item:QA-456\n  not Gherkin\n')
        self.commit('broken unrelated feature')
        collector = self.collector()
        self.assertEqual(len(collector.collect('QA-123', ['base'])), 1)
        with self.assertRaisesRegex(CollectionError, 'Cannot parse'):
            collector.collect('QA-456', ['base'])

    def test_last_author_ignores_committer_unrelated_changes_and_merge(self):
        self.git('checkout', '-b', 'feature/anna')
        self.write_scenario('case.feature', 'CASE_1', 'updated')
        self.git('add', '.')
        self.git('commit', '--author=Anna Example <anna@example.invalid>', '-m', 'scenario update')
        # Another scenario in the same file must not change CASE_1 attribution.
        path = self.repo / 'case.feature'
        path.write_text(path.read_text() + '\n  @work-item:QA-456\n  Scenario: [CASE_2] Other\n    Then ok\n')
        self.commit('another scenario')
        self.git('checkout', '-b', 'feature/merged', 'base')
        self.git('merge', '--no-ff', '--no-edit', 'feature/anna')
        collector = self.collector()
        cases = collector.with_last_change_authors(collector.collect('QA-123', ['feature/merged']))
        self.assertEqual(cases[0].last_change_authors, ['Anna Example'])
        self.assertIn('**Autori dell’ultima modifica:** Anna Example', render_document(cases))
        output = self.repo / 'authors.html'
        with contextlib.redirect_stdout(io.StringIO()):
            generate_structured_html(output, 'QA-123', [], cases)
        self.assertIn('<b>Autori dell’ultima modifica:</b> Anna Example', output.read_text())
        with patch.object(collector, 'git', wraps=collector.git) as git_calls:
            collector.with_last_change_authors(cases)
        self.assertEqual(git_calls.call_count, 0)

    def test_deletion_is_an_effective_modification(self):
        path = self.repo / 'case.feature'
        path.write_text(path.read_text().replace('    Given ready\n', ''))
        self.git('add', '.')
        self.git('commit', '--author=Anna Example <anna@example.invalid>', '-m', 'remove precondition')
        collector = self.collector()
        cases = collector.with_last_change_authors(collector.collect('QA-123', ['base']))
        self.assertEqual(cases[0].last_change_authors, ['Anna Example'])

    def test_identical_independent_versions_keep_both_authors(self):
        for branch, author in [('feature/anna', 'Anna'), ('feature/luca', 'Luca')]:
            self.git('checkout', '-b', branch, 'base')
            self.write_scenario('case.feature', 'CASE_1', 'same update')
            self.git('add', '.')
            self.git('commit', f'--author={author} <{author.lower()}@example.invalid>', '-m', 'update')
        collector = self.collector()
        cases = collector.with_last_change_authors(collector.collect('QA-123', ['feature/anna', 'feature/luca']))
        self.assertEqual(len(cases), 1)
        self.assertEqual(cases[0].last_change_authors, ['Anna', 'Luca'])
