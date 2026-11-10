"""Collect a release's branch snapshots and resolve scenario versions by ancestry."""
from dataclasses import replace
import hashlib
import json
import re
from pathlib import Path
import subprocess

import yaml

from .core import parse_test_cases


class CollectionError(RuntimeError):
    pass


def validate_qa_epic(epic: str) -> None:
    if not isinstance(epic, str) or not re.fullmatch(r'QA-[0-9]+', epic):
        raise CollectionError(f"Invalid QA Epic {epic!r}; expected QA-<number>")


def source_repo_name(name: str | None) -> str:
    """Normalize the two supported release repositories."""
    if name in (None, '', 'pn-b2b-client'):
        return ''
    if name == 'qe_suit':
        return name
    raise CollectionError(f"Unsupported source repository {name!r}; expected pn-b2b-client or qe_suit")


def active_sources(repo: Path) -> dict[str, list[str]]:
    sources: dict[str, set[str]] = {}
    for path in sorted((repo / 'releases').rglob('*')):
        if path.suffix not in {'.yaml', '.yml'}:
            continue
        release = (yaml.safe_load(path.read_text(encoding='utf-8')) or {}).get('release', {})
        if release.get('status') != 'in-progress':
            continue
        own: dict[str, set[str]] = {}
        for item in release.get('work-items', []):
            epic = item.get('qa_epic')
            if epic:
                validate_qa_epic(epic)
                own.setdefault(epic, set())
                if item.get('branch'):
                    own[epic].add(item['branch'])
        # Explicit branches override the convention for their QA Epic.
        pool = {branch for epic, branches in own.items()
                for branch in (branches or {f'feature/{epic}'})}
        for epic in own:
            sources.setdefault(epic, set()).update(pool)
    return {epic: sorted(branches) for epic, branches in sorted(sources.items())}


def active_source_specs(repo: Path) -> dict[str, list[tuple[str, str]]]:
    """Return (repository, branch) sources; empty repository means this repo."""
    sources: dict[str, set[tuple[str, str]]] = {}
    for path in sorted((repo / 'releases').rglob('*')):
        if path.suffix not in {'.yaml', '.yml'}:
            continue
        release = (yaml.safe_load(path.read_text(encoding='utf-8')) or {}).get('release', {})
        if release.get('status') != 'in-progress':
            continue
        own: dict[str, set[tuple[str, str]]] = {}
        for item in release.get('work-items', []):
            epic = item.get('qa_epic')
            if epic:
                validate_qa_epic(epic)
                name = source_repo_name(item.get('repo'))
                own.setdefault(epic, set())
                if item.get('branch'):
                    own[epic].add((name, item['branch']))
        pool = {source for epic, explicit in own.items()
                for source in (explicit or {('', f'feature/{epic}')})}
        for epic in own:
            sources.setdefault(epic, set()).update(pool)
    return {epic: sorted(specs) for epic, specs in sorted(sources.items())}


def collect_from_sources(repo: Path, epic: str, specs: list[tuple[str, str]],
                         pattern: str, repo_paths: dict[str, Path] | None = None) -> list:
    validate_qa_epic(epic)
    grouped: dict[str, list[str]] = {}
    for name, branch in specs:
        grouped.setdefault(source_repo_name(name), []).append(branch)
    selected = {}
    for name, branches in grouped.items():
        if name:
            workspace_checkout = repo / 'source-repos' / name
            source_repo = (repo_paths or {}).get(
                name, workspace_checkout if workspace_checkout.is_dir() else repo.parent / name)
        else:
            source_repo = repo
        if not source_repo.is_dir():
            raise CollectionError(f"Repository '{name}' not found at {source_repo}")
        collector = ScenarioCollector(source_repo, pattern)
        cases = collector.with_last_change_authors(collector.collect(epic, branches))
        for case in cases:
            for source in case.sources:
                source['repo'] = name or repo.resolve().name
            previous = selected.get(case.test_id)
            if previous is None:
                selected[case.test_id] = case
            elif collector.fingerprint(previous) == collector.fingerprint(case):
                selected[case.test_id] = replace(previous,
                    sources=previous.sources + case.sources,
                    last_change_authors=sorted(set(previous.last_change_authors + case.last_change_authors)))
            else:
                raise CollectionError(f"{epic} · {case.test_id}: divergent scenario versions across repositories "
                                      f"{previous.sources[0]['repo']} and {name or repo.name}")
    return [selected[key] for key in sorted(selected)]


class ScenarioCollector:
    def __init__(self, repo: Path, pattern: str):
        self.repo, self.pattern = repo, pattern
        self.origins = {}
        self.parents = {}
        self.commit_authors = {}
        self.ancestry = {}
        self.refs = {}
        self.indexes = {}
        self.index_errors = {}
        self.blob_ids = {}
        self.blob_contents = {}
        self.parsed_blobs = {}
        self.blob_versions = {}
        self.fingerprints = {}
        self.graph_heads = set()
        self.history_paths = {}
        self.checked_history = False

    def git(self, *args, allowed=(0,), input=None, binary=False):
        result = subprocess.run(['git', '-C', str(self.repo), *args], input=input,
                                capture_output=True, text=not binary)
        if result.returncode not in allowed:
            message = result.stderr.decode('utf-8', errors='replace') if binary else result.stderr
            raise CollectionError(message.strip() or f"git {' '.join(args)} failed")
        return result

    def resolve(self, branch):
        if branch not in self.refs:
            for ref in (f'refs/heads/{branch}', f'refs/remotes/origin/{branch}'):
                result = self.git('rev-parse', '--verify', f'{ref}^{{commit}}', allowed=(0, 128))
                if result.returncode == 0:
                    self.refs[branch] = result.stdout.strip()
                    break
            else:
                raise CollectionError(f"Branch '{branch}' not found (local or origin)")
        return self.refs[branch]

    def fingerprint(self, case):
        # Replaced cases share the semantic node retained in parsed_blobs.
        key = id(case.semantic_content)
        if key not in self.fingerprints:
            content = json.dumps(case.semantic_content, sort_keys=True, ensure_ascii=False)
            self.fingerprints[key] = hashlib.sha256(content.encode('utf-8')).hexdigest()
        return self.fingerprints[key]

    def load_blobs(self, requests):
        """Resolve commit:path objects and read unique contents in bounded batches."""
        missing = list(dict.fromkeys(key for key in requests if key not in self.blob_ids))
        for start in range(0, len(missing), 4096):
            batch = missing[start:start + 4096]
            expressions = [f'{commit}:{path}' for commit, path in batch]
            if any('\n' in expression for expression in expressions):
                raise CollectionError('Feature paths containing newlines are unsupported')
            result = self.git('cat-file', '--batch-check', input='\n'.join(expressions) + '\n')
            lines = result.stdout.splitlines()
            if len(lines) != len(batch):
                raise CollectionError('Incomplete Git object response')
            for key, expression, line in zip(batch, expressions, lines):
                if line == f'{expression} missing':
                    self.blob_ids[key] = None
                else:
                    fields = line.split()
                    if len(fields) != 3 or fields[1] != 'blob':
                        raise CollectionError(f'Expected a feature blob: {expression}')
                    self.blob_ids[key] = fields[0]
        objects = sorted({self.blob_ids[key] for key in missing
                          if self.blob_ids[key] and self.blob_ids[key] not in self.blob_contents})
        for start in range(0, len(objects), 128):
            batch = objects[start:start + 128]
            data = self.git('cat-file', '--batch', binary=True,
                            input=('\n'.join(batch) + '\n').encode()).stdout
            offset = 0
            for oid in batch:
                end = data.index(b'\n', offset)
                header = data[offset:end].decode().split()
                if len(header) != 3 or header[:2] != [oid, 'blob']:
                    raise CollectionError(f'Invalid Git blob response: {oid}')
                size = int(header[2])
                offset = end + 1
                self.blob_contents[oid] = data[offset:offset + size]
                offset += size + 1

    def snapshot(self, commit, path):
        self.load_blobs([(commit, path)])
        oid = self.blob_ids[commit, path]
        if oid is None:
            return []
        if oid not in self.parsed_blobs:
            try:
                cases = parse_test_cases(self.blob_contents[oid].decode('utf-8'), self.pattern)
                self.parsed_blobs[oid] = cases
                versions = {}
                for case in cases:
                    versions.setdefault(case.test_id, set()).add(self.fingerprint(case))
                self.blob_versions[oid] = versions
            except Exception as exc:
                raise CollectionError(f"Cannot parse {commit}:{path}: {exc}") from exc
        return self.parsed_blobs[oid]

    def index(self, commit):
        if commit not in self.indexes:
            files = self.git('grep', '-l', '-z', '-F', '-e', '@work-item:', commit,
                             '--', '*.feature', allowed=(0, 1)).stdout
            paths = [entry.split(':', 1)[1] for entry in files.split('\0') if entry]
            self.load_blobs((commit, path) for path in paths)
            index, errors = {}, {}
            for path in paths:
                try:
                    cases = self.snapshot(commit, path)
                except CollectionError as exc:
                    # A malformed file affects only the QA Epics it references.
                    content = self.blob_contents[self.blob_ids[commit, path]].decode('utf-8', errors='replace')
                    for epic in re.findall(r'@work-item:([^\s]+)', content):
                        errors.setdefault(epic, []).append(str(exc))
                    continue
                for case in cases:
                    for tag in set(case.tags):
                        if tag.startswith('@work-item:'):
                            index.setdefault(tag[len('@work-item:'):], []).append((case, path))
            self.indexes[commit], self.index_errors[commit] = index, errors
        return self.indexes[commit]

    def load_graph(self, commit):
        """Read original parents in bulk; preserve every merge edge."""
        heads = set(self.refs.values()) | {commit}
        missing = heads - self.parents.keys()
        if missing:
            revisions = sorted(missing) + [f'^{head}' for head in sorted(self.graph_heads)]
            output = self.git('log', '--no-patch', '--no-show-signature',
                              '--format=%H %P%x00%an', '--stdin',
                              input='\n'.join(revisions) + '\n').stdout
            for line in output.splitlines():
                history, author = line.split('\0', 1)
                revision, *parents = history.split()
                self.parents[revision] = parents
                self.commit_authors[revision] = author
            self.graph_heads.update(heads)

    def load_history(self, commit, path):
        self.load_graph(commit)
        seen = self.history_paths.setdefault(path, set())
        pending, revisions = [commit], []
        while pending:
            revision = pending.pop()
            if revision in seen:
                continue
            seen.add(revision)
            revisions.append(revision)
            pending.extend(self.parents[revision])
        self.load_blobs((revision, path) for revision in revisions)

    def introducing_commits(self, commit, path, case):
        """Walk cached versions; unchanged copies and merges retain their origin."""
        self.load_history(commit, path)
        fingerprint = self.fingerprint(case)
        def key(revision):
            return revision, path, case.test_id, fingerprint
        def matches(parent):
            self.snapshot(parent, path)
            oid = self.blob_ids[parent, path]
            return fingerprint in self.blob_versions.get(oid, {}).get(case.test_id, ())
        pending = [(commit, None)]
        while pending:
            revision, matching = pending.pop()
            if key(revision) in self.origins:
                continue
            if matching is not None:
                self.origins[key(revision)] = set().union(*(self.origins[key(p)] for p in matching))
                continue
            matching = [parent for parent in self.parents[revision] if matches(parent)]
            if not matching:
                self.origins[key(revision)] = {revision}
            else:
                pending.append((revision, matching))
                pending.extend((parent, None) for parent in matching)
        return self.origins[key(commit)]

    def ancestor(self, older, newer):
        if (older, newer) not in self.ancestry:
            pending, seen = [newer], set()
            while pending:
                revision = pending.pop()
                if revision == older:
                    self.ancestry[older, newer] = True
                    break
                if revision not in seen:
                    seen.add(revision)
                    pending.extend(self.parents[revision])
            else:
                self.ancestry[older, newer] = False
        return self.ancestry[older, newer]

    def with_last_change_authors(self, cases):
        """Attribute the selected version, reusing the graph and blob caches."""
        enriched = []
        for case in cases:
            origins = set()
            for source in case.sources:
                origins.update(self.introducing_commits(source['commit'], source['path'], case))
            authors = sorted({self.commit_authors[commit] for commit in origins})
            enriched.append(replace(case, last_change_authors=authors))
        return enriched

    def collect(self, epic, branches):
        if not self.checked_history:
            if self.git('rev-parse', '--is-shallow-repository').stdout.strip() == 'true':
                raise CollectionError('Complete Git history is required; fetch with fetch-depth: 0')
            self.checked_history = True
        candidates = {}
        for branch in sorted(set(branches)):
            commit = self.resolve(branch)
            index = self.index(commit)
            if epic in self.index_errors[commit]:
                raise CollectionError('\n'.join(self.index_errors[commit][epic]))
            for case, path in index.get(epic, []):
                candidates.setdefault(case.test_id, []).append((case, branch, commit, path))
        selected, conflicts = [], []
        for test_id, copies in sorted(candidates.items()):
            versions = {}
            branch_versions = {}
            for item in copies:
                fingerprint = self.fingerprint(item[0])
                versions.setdefault(fingerprint, []).append(item)
                branch_versions.setdefault(item[1], set()).add(fingerprint)
            winner = None
            if len(versions) == 1:
                winner = next(iter(versions.values()))
            elif all(len(values) == 1 for values in branch_versions.values()):
                roots = {fingerprint: set().union(*(self.introducing_commits(commit, path, case)
                                                   for case, _, commit, path in items))
                         for fingerprint, items in versions.items()}
                winners = [fingerprint for fingerprint, newer in roots.items()
                           if all(other == fingerprint or all(
                               any(old != new and self.ancestor(old, new) for new in newer)
                               for old in older) for other, older in roots.items())]
                if len(winners) == 1:
                    winner = versions[winners[0]]
            if winner is None:
                details = '\n'.join(f'  - {branch} · {commit} · {path}' for _, branch, commit, path in copies)
                conflicts.append(f'{epic} · {test_id}: divergent scenario versions\n{details}')
            else:
                selected.append(replace(winner[0][0], sources=[
                    {'branch': branch, 'commit': commit, 'path': path}
                    for _, branch, commit, path in winner]))
        if conflicts:
            raise CollectionError('\n\n'.join(conflicts))
        return selected
