"""Persistent input and output fingerprints for generated PSTs."""

import hashlib
import json
from pathlib import Path

from .collection import CollectionError, ScenarioCollector, source_repo_name


def digest(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def source_path(repo: Path, name: str, overrides: dict[str, Path] | None) -> Path:
    name = source_repo_name(name)
    if not name:
        return repo
    embedded = repo / 'source-repos' / name
    return (overrides or {}).get(name, embedded if embedded.is_dir() else repo.parent / name)


def input_fingerprint(repo: Path, config: Path, epic: str, specs: list[tuple[str, str]],
                      overrides: dict[str, Path] | None, include_html: bool) -> str:
    generator_dir = Path(__file__).resolve().parent
    tool_dir = generator_dir.parent
    code = {path.name: digest(path.read_bytes()) for path in sorted(generator_dir.glob('*.py'))}
    code['requirements.txt'] = digest((tool_dir / 'requirements.txt').read_bytes())
    sources = []
    for name, branch in specs:
        checkout = source_path(repo, name, overrides)
        if not checkout.is_dir():
            raise CollectionError(f"Repository '{name}' not found at {checkout}")
        collector = ScenarioCollector(checkout, '')
        revision = collector.resolve(branch)
        result = collector.git('ls-tree', '-r', '-z', revision)
        feature_blobs = sorted(record for record in result.stdout.split('\0')
                               if record and record.partition('\t')[2].endswith('.feature'))
        sources.append((name or repo.name, branch, revision, digest('\0'.join(feature_blobs).encode())))
    from .cli import work_item_ids_for_qa_epic
    value = {'schema': 1, 'epic': epic, 'sources': sources,
             'work_items': work_item_ids_for_qa_epic(repo, epic),
             'config': digest(config.read_bytes()), 'generator': code, 'html': include_html}
    return digest(json.dumps(value, sort_keys=True).encode())


def document_fingerprint(repo: Path, config: Path, epic: str) -> str:
    from .cli import work_item_ids_for_qa_epic
    value = (2, digest(config.read_bytes()), work_item_ids_for_qa_epic(repo, epic))
    return digest(json.dumps(value, sort_keys=True).encode())


def state_path(output: Path, epic: str) -> Path:
    return output / '.state' / f'{epic}.json'


def version_history(output: Path, epic: str) -> list[dict]:
    try:
        history = json.loads(state_path(output, epic).read_text(encoding='utf-8')).get('versions', [])
        return history if isinstance(history, list) else []
    except (OSError, ValueError, TypeError):
        return []


def is_current(output: Path, epic: str, fingerprint: str, include_html: bool) -> bool:
    return matches_state(output, epic, 'input', fingerprint, include_html)


def matches_state(output: Path, epic: str, field: str, fingerprint: str,
                  include_html: bool) -> bool:
    path = state_path(output, epic)
    try:
        state = json.loads(path.read_text(encoding='utf-8'))
        expected = state['outputs']
        files = [f'pst_{epic}.md'] + ([f'pst_{epic}.html'] if include_html else [])
        return (include_html or not (output / f'pst_{epic}.html').exists()) and state.get(field) == fingerprint and all(
            digest((output / file).read_bytes()) == expected[file] for file in files)
    except (OSError, KeyError, TypeError, ValueError):
        return False


def save_state(output: Path, epic: str, fingerprint: str, include_html: bool,
               scenarios: str | None = None, document: str | None = None,
               versions: list[dict] | None = None) -> None:
    if not include_html:
        (output / f'pst_{epic}.html').unlink(missing_ok=True)
    files = [f'pst_{epic}.md'] + ([f'pst_{epic}.html'] if include_html else [])
    state = {'schema': 1, 'input': fingerprint,
             'outputs': {file: digest((output / file).read_bytes()) for file in files}}
    if scenarios is not None:
        state['scenarios'] = scenarios
    if document is not None:
        state['document'] = document
    if versions is not None:
        state['versions'] = versions
    path = state_path(output, epic)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(state, indent=2, sort_keys=True) + '\n', encoding='utf-8')
