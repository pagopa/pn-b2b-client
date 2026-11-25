import argparse
from datetime import date
import json
import os
import subprocess
import sys
from pathlib import Path

import yaml

from .core import parse_test_cases
from .collection import ScenarioCollector, CollectionError, active_source_specs, collect_from_sources, validate_qa_epic
from .markdown import render_document
from .jira import issue_metadata
from .state import digest, document_fingerprint, input_fingerprint, is_current, matches_state, save_state, version_history


def generate_pst(repo: Path, qa_epic: str, branch: str | list[str], config_path: Path,
                 collector: ScenarioCollector | None = None,
                 output_dir: Path | None = None, cases: list | None = None,
                 jira_base_url: str = '', jira_statuses: dict[str, str] | None = None,
                 versions: list[dict] | None = None) -> int:
    validate_qa_epic(qa_epic)
    config = yaml.safe_load(config_path.read_text(encoding="utf-8")) or {}
    input_config = config.get("input", {})
    pattern = input_config.get("id_pattern", r"\[([^\]]+)\]")
    output_config = config.get("output", {})
    if cases is None:
        collector = collector or ScenarioCollector(repo, pattern)
        cases = collector.with_last_change_authors(
            collector.collect(qa_epic, [branch] if isinstance(branch, str) else branch))
    if not cases:
        raise CollectionError(f"No scenarios tagged @work-item:{qa_epic} were found in the configured sources")
    document = render_document(
        cases,
        config.get("business_terms", {}),
        config.get("value_terms", {}),
        config.get("status_terms", {}),
        output_config.get("include_technical_codes", False),
        output_config.get("layout", "cards"),
        output_config.get("technical_details", "none"),
        qa_epic=qa_epic,
        work_item_ids=work_item_ids_for_qa_epic(repo, qa_epic),
        excluded_fields=output_config.get("exclude_fields", []),
        field_value_phrases=config.get("field_value_phrases", {}),
        step_phrases=config.get("step_phrases", []),
        jira_base_url=jira_base_url, jira_statuses=jira_statuses, versions=versions,
    )
    source_count = sum(len(case.sources) for case in cases)
    document += f"\n<details>\n<summary>Fonti ({source_count})</summary>\n\n"
    for case in cases:
        for source in case.sources:
            document += (f"- `{case.test_id}` · `{source.get('repo', repo.resolve().name)}` / "
                         f"`{source['branch']}` · `{source['commit']}` · `{source['path']}`\n")
    document += "\n</details>\n"
    output = (output_dir if output_dir is not None else repo / "pst") / f"pst_{qa_epic}.md"
    output.parent.mkdir(parents=True, exist_ok=True)
    if not output.exists() or output.read_text(encoding="utf-8") != document:
        output.write_text(document, encoding="utf-8")
    print(f"Generated {output} with {len(cases)} scenario(s)")
    return len(cases)


def work_item_ids_for_qa_epic(repo: Path, qa_epic: str) -> list[str]:
    ids = []
    for path in sorted(repo.glob("releases/**/*.yaml")) + sorted(repo.glob("releases/**/*.yml")):
        document = yaml.safe_load(path.read_text(encoding="utf-8")) or {}
        release = document.get("release", {})
        if release.get("status") != "in-progress":
            continue
        ids.extend(item["id"] for item in release.get("work-items", []) if item.get("qa_epic") == qa_epic and item.get("id"))
    return sorted(set(ids))


def discover_active_releases(repo: Path, ref: str | None) -> int:
    if ref:
        current = subprocess.run(["git", "-C", str(repo), "branch", "--show-current"],
                                 check=True, capture_output=True, text=True).stdout.strip()
        if current != ref:
            raise CollectionError(f"Repository is on '{current}', expected '{ref}'")
    sources = active_source_specs(repo)
    failed = False
    for epic, specs in sources.items():
        print(f"{epic}: {', '.join(f'{name or repo.name}:{branch}' for name, branch in specs)}")
        try:
            for case in collect_from_sources(repo, epic, specs, r"\[([^\]]+)\]"):
                print(f"  - {case.test_id}")
        except CollectionError as exc:
            print(str(exc), file=sys.stderr)
            failed = True
    return int(failed)


def generate_active(repo: Path, config: Path,
                    output_dir: Path | None = None,
                    repo_paths: dict[str, Path] | None = None,
                    use_state: bool = False,
                    selected_sources: dict[str, list[tuple[str, str]]] | None = None,
                    missing_branch_is_error: bool = False) -> int:
    """Generate independent PSTs, preserving the existing output on conflicts."""
    settings = yaml.safe_load(config.read_text(encoding="utf-8")) or {}
    collector = ScenarioCollector(repo, settings.get("input", {}).get("id_pattern", r"\[([^\]]+)\]"))
    results = []
    failed = False
    destination = output_dir if output_dir is not None else repo / 'pst'
    for legacy_html in destination.glob('pst_*.html'):
        legacy_html.unlink()
    for epic, specs in (selected_sources if selected_sources is not None else active_source_specs(repo)).items():
        try:
            fingerprint = input_fingerprint(repo, config, epic, specs, repo_paths)
            if use_state and is_current(destination, epic, fingerprint):
                results.append(f"- {epic}: invariato")
                continue
            cases = collect_from_sources(repo, epic, specs, collector.pattern, repo_paths)
            scenarios = digest(json.dumps(
                [(case.test_id, case.semantic_content) for case in cases],
                sort_keys=True, ensure_ascii=False).encode('utf-8'))
            document = document_fingerprint(repo, config, epic)
            if (use_state and
                    matches_state(destination, epic, 'scenarios', scenarios) and
                    matches_state(destination, epic, 'document', document)):
                save_state(destination, epic, fingerprint, scenarios, document,
                           version_history(destination, epic))
                results.append(f"- {epic}: invariato (scenari invariati)")
                continue
            versions = version_history(destination, epic)
            description = 'Prima generazione' if not versions else 'Aggiornamento del PST'
            versions.append({'version': len(versions) + 1, 'date': date.today().strftime('%d/%m/%Y'),
                             'description': description})
            jira_base_url, jira_statuses, jira_warning = issue_metadata(
                [epic, *work_item_ids_for_qa_epic(repo, epic)])
            count = generate_pst(repo, epic, [], config, collector, output_dir, cases,
                                 jira_base_url, jira_statuses, versions)
            save_state(destination, epic, fingerprint, scenarios, document, versions)
            results.append(f"- {epic}: generato ({count} scenari)" +
                           (f"; {jira_warning}" if jira_warning else ""))
        except CollectionError as exc:
            if "not found (local or origin)" in str(exc):
                if missing_branch_is_error:
                    failed = True
                existing = (destination / f"pst_{epic}.md").is_file()
                outcome = "PST esistente conservato" if existing else "PST non generato"
                results.append(f"- {epic}: {outcome}, branch mancante: {exc}")
                continue
            failed = True
            results.append(f"- {epic}: ERRORE\n\n```text\n{exc}\n```\n")
            print(str(exc), file=sys.stderr)
    summary = "## Generazione PST\n\n" + ("\n".join(results) or "Nessuna QA Epic nelle release in corso.") + "\n"
    print(summary)
    if os.environ.get("GITHUB_STEP_SUMMARY"):
        with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as stream:
            stream.write(summary)
    return int(failed)


def main() -> int:
    if len(sys.argv) > 1 and sys.argv[1] == "generate-all":
        parser = argparse.ArgumentParser(description="Generate PSTs for all active releases")
        parser.add_argument("generate-all")
        parser.add_argument("--repo", type=Path, required=True)
        parser.add_argument("--output-dir", type=Path, help="Output directory (default: <repo>/pst)")
        parser.add_argument("--config", type=Path, default=Path("config.yaml"))
        parser.add_argument("--use-state", action="store_true", help="Reuse intact PSTs with matching inputs")
        parser.add_argument("--source-repo", action="append", default=[], metavar="NAME=PATH",
                            help="Override the local path of a named source repository")
        args = parser.parse_args()
        paths = {}
        for override in args.source_repo:
            name, separator, path = override.partition('=')
            if not separator or not name or not path:
                parser.error("--source-repo requires NAME=PATH")
            paths[name] = Path(path)
        return generate_active(args.repo, args.config, args.output_dir, paths, args.use_state)

    if len(sys.argv) > 1 and sys.argv[1] == "discover":
        parser = argparse.ArgumentParser(description="Inspect active releases and QA Epics")
        parser.add_argument("discover")
        parser.add_argument("--repo", type=Path, required=True)
        parser.add_argument("--ref", default=None)
        args = parser.parse_args()
        return discover_active_releases(args.repo, args.ref)

    if len(sys.argv) > 1 and sys.argv[1] == "generate":
        parser = argparse.ArgumentParser(description="Generate a PST for a QA Epic")
        parser.add_argument("generate")
        parser.add_argument("--repo", type=Path, required=True)
        parser.add_argument("--output-dir", type=Path, help="Output directory (default: <repo>/pst)")
        parser.add_argument("--qa-epic", required=True)
        parser.add_argument("--branch", help="Optional branch override for local debugging")
        parser.add_argument("--source-repo", action="append", default=[], metavar="NAME=PATH",
                            help="Override the local path of a named source repository")
        parser.add_argument("--config", type=Path, default=Path("config.yaml"))
        args = parser.parse_args()
        specs = [('', args.branch)] if args.branch else active_source_specs(args.repo).get(args.qa_epic, [])
        if not specs:
            raise CollectionError(f"No active QA Epic '{args.qa_epic}' was found")
        paths = {}
        for override in args.source_repo:
            name, separator, path = override.partition('=')
            if not separator or not name or not path:
                parser.error("--source-repo requires NAME=PATH")
            paths[name] = Path(path)
        print(f"Fonti configurate per {args.qa_epic}: {specs}")
        return generate_active(args.repo, args.config, args.output_dir, paths,
                               selected_sources={args.qa_epic: specs}, missing_branch_is_error=True)

    parser = argparse.ArgumentParser(description="Generate a PST Markdown document from a Gherkin scenario")
    parser.add_argument("feature", type=Path)
    parser.add_argument("-c", "--config", type=Path, default=Path("config.yaml"))
    parser.add_argument("-o", "--output", type=Path, default=Path("pst.md"))
    args = parser.parse_args()

    config = yaml.safe_load(args.config.read_text(encoding="utf-8")) or {}
    pattern = config.get("input", {}).get("id_pattern", r"\[([^\]]+)\]")
    business_terms = config.get("business_terms", {})
    value_terms = config.get("value_terms", {})
    status_terms = config.get("status_terms", {})
    output_config = config.get("output", {})
    include_technical_codes = output_config.get("include_technical_codes", False)
    layout = output_config.get("layout", "cards")
    technical_details = output_config.get("technical_details", "none")
    tests = parse_test_cases(args.feature.read_text(encoding="utf-8"), pattern)
    args.output.write_text(
        render_document(
            tests,
            business_terms,
            value_terms,
            status_terms,
            include_technical_codes,
            layout,
            technical_details,
            excluded_fields=output_config.get("exclude_fields", []),
            field_value_phrases=config.get("field_value_phrases", {}),
            step_phrases=config.get("step_phrases", []),
        ),
        encoding="utf-8",
    )
    print(f"Generated {args.output} for {len(tests)} test case(s)")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except CollectionError as exc:
        print(str(exc), file=sys.stderr)
        raise SystemExit(1)
