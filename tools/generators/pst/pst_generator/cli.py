import argparse
from datetime import date
import html
import os
import re
import subprocess
import sys
from pathlib import Path

import yaml

from .core import parse_test_cases
from .collection import ScenarioCollector, CollectionError, active_source_specs, collect_from_sources, validate_qa_epic
from .markdown import _group_steps, render_document, replace_quoted_values
from .state import input_fingerprint, is_current, save_state


def generate_pst(repo: Path, qa_epic: str, branch: str | list[str], config_path: Path,
                 collector: ScenarioCollector | None = None, include_html: bool = False,
                 output_dir: Path | None = None, cases: list | None = None) -> int:
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
    if include_html:
        generate_structured_html(output.with_suffix(".html"), qa_epic,
                                 work_item_ids_for_qa_epic(repo, qa_epic), cases,
                                 config.get("business_terms", {}), config.get("value_terms", {}),
                                 config.get("status_terms", {}),
                                 output_config.get("include_technical_codes", False))
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


def generate_structured_html(html_path: Path, qa_epic: str, work_item_ids: list[str],
                             cases: list, business_terms: dict[str, str] | None = None,
                             value_terms: dict[str, str] | None = None,
                             status_terms: dict[str, str] | None = None,
                             include_technical_codes: bool = False) -> None:
    def text(value: str) -> str:
        return render_inline(value, value_terms)

    def steps(values: list) -> str:
        def label(step) -> str:
            value = step.text
            match = re.search(r"(?:codice di stato|status code|HTTP)\s+(\d{3})", value, re.IGNORECASE)
            if match and (status_terms or {}).get(match.group(1)):
                code = match.group(1)
                value = (f"{status_terms[code]} — HTTP {code}" if include_technical_codes
                         else status_terms[code])
            return text(value)
        return "<ul>" + "".join(f"<li>{label(step)}</li>" for step in values) + "</ul>"

    def examples(table) -> str:
        labels = "".join(f"<th>{html.escape((business_terms or {}).get(header, header))}</th>"
                         for header in table.headers or [])
        rows = "".join("<tr>" + "".join(f"<td>{text((value_terms or {}).get(value, value))}</td>"
                                       for value in row) + "</tr>"
                       for row in table.rows)
        return f"<table class='examples-table'><thead><tr>{labels}</tr></thead><tbody>{rows}</tbody></table>"

    parts = ["<!doctype html><html lang='it'><head><meta charset='utf-8'><title>PST</title>", """<style>
    body{max-width:1200px;margin:0 auto;padding:28px 42px;font-family:Arial,sans-serif;color:#292d32;line-height:1.4}h1{font-size:27px;border-bottom:3px solid #06c;padding-bottom:12px}h2{margin-top:30px;border-bottom:1px solid #d2d6da;padding-bottom:8px}h3{margin:26px 0 8px;color:#292d32}.header{display:flex;gap:12px;align-items:center;border-bottom:3px solid #06c;padding-bottom:15px}.check{background:#69b34c;color:white;border-radius:6px;padding:3px 9px;font-size:25px}.toc a{display:block;color:#06c;margin:5px 0}.info,.objective{background:#eaf3ff;border-radius:5px;padding:14px 18px;margin:12px 0 18px}.disclaimer{background:#fff4d6;border-left:5px solid #e0a800;border-radius:5px;padding:14px 18px;margin:16px 0 22px}#general table{border-collapse:collapse;width:100%;margin:10px 0 20px}#general th,#general td{border:1px solid #d2d6da;padding:8px 10px;text-align:left}#general th{background:#f7f8f8}.test-case{border-top:2px solid #d2d6da;padding-top:18px;margin-top:30px}.test-table{border-collapse:collapse;width:100%;table-layout:fixed;margin:10px 0 28px;font-size:16px}.test-table.with-combinations th:nth-child(1),.test-table.with-combinations td:nth-child(1){width:30%}.test-table.with-combinations th:nth-child(2),.test-table.with-combinations td:nth-child(2){width:23%}.test-table.with-combinations th:nth-child(3),.test-table.with-combinations td:nth-child(3){width:25%}.test-table.with-combinations th:nth-child(4),.test-table.with-combinations td:nth-child(4){width:22%}.test-table.without-combinations th:nth-child(1),.test-table.without-combinations td:nth-child(1){width:38%}.test-table.without-combinations th:nth-child(2),.test-table.without-combinations td:nth-child(2){width:30%}.test-table.without-combinations th:nth-child(3),.test-table.without-combinations td:nth-child(3){width:32%}.test-table th,.test-table td{border:1px solid #d2d6da;padding:12px;vertical-align:top;text-align:left}.test-table th{background:#f0f1f2;font-size:16px}.test-table th em{display:block;font-size:13px;font-weight:normal;color:#68736d;margin-top:7px}.test-table ul{margin:0;padding-left:20px}.test-table li{margin-bottom:8px}.outcome{font-weight:700;margin-bottom:8px}.outcome-positive{color:#2f8f3a}.outcome-negative{color:#d64545}.value{font-weight:700;color:#174a3a}.variable{font-weight:700;color:#0066cc;background:#eaf3ff;padding:1px 4px;border-radius:3px}code{background:#f3f4f4;padding:2px 4px;border-radius:3px}</style></head><body>""", "<div class='header'><div class='check'>✓</div><h1>PST - QA Validation Report</h1></div>", "<div class='toc'><h2>Sommario</h2><a href='#general'>Informazioni generali</a><a href='#cases'>Casi di test</a></div>", "<section id='general'><h2>Informazioni generali</h2><table><tr><th>Campo</th><th>Valore</th><th>Note</th></tr>", f"<tr><td>Work-item</td><td>{text(', '.join(work_item_ids) or 'Da definire')}</td><td>Work-item associati</td></tr><tr><td>QA Epic</td><td>{text(qa_epic)}</td><td>Release attiva</td></tr><tr><td>Responsabile</td><td>Da definire</td><td>Fonte da definire</td></tr><tr><td>Data aggiornamento</td><td>{date.today().strftime('%d/%m/%Y')}</td><td>Generata automaticamente</td></tr><tr><td>Scenari</td><td>{len(cases)}</td><td></td></tr></table></section>", "<div class='disclaimer'><b>DISCLAIMER</b><br>Le verifiche descritte in questo e in tutti i documenti di progettazione coprono esclusivamente le funzionalità applicative lato software. Operazioni che coinvolgono hardware dei dispositivi (es. camera, lettura QR code, NFC, sensori, risoluzioni) vengono eseguite se possibile, in funzione del device disponibile al momento, ma non sono testate su dispositivi diversi.</div>", "<section id='cases'><h2>Casi di test</h2>", "<div class='toc'>" + "".join(f"<a href='#{text(case.test_id)}'>{text(case.test_id)}</a>" for case in cases) + "</div>"]
    parts[1] = parts[1].replace('</style>',
        '.test-table .examples-table{width:100%;table-layout:auto;border-collapse:collapse;margin:0;font-size:13px}'
        '.test-table .examples-table th,.test-table .examples-table td{width:auto!important;padding:5px 6px;overflow-wrap:anywhere}'
        '.test-table .examples-table th{font-size:13px}'
        '.test-table > tbody > tr > td{font-size:14px;line-height:1.28;overflow-wrap:anywhere}'
        '.test-table > tbody > tr > td ul{padding-left:18px}'
        '.test-table > tbody > tr > td li{margin-bottom:4px}'
        '.quoted-value,.version-value{font-size:.95em;font-weight:600;padding:0 1px;border-radius:2px}'
        '.quoted-value{background:#fff1c2;color:#674600}'
        '.version-value{background:#e8defd;color:#4b2980}</style>')
    parts[0] = parts[0].replace('<title>PST</title>', f'<title>PST — {html.escape(qa_epic)}</title>')
    parts[2] = parts[2].replace('<h1>PST - QA Validation Report</h1>',
                                f'<h1>PST — {html.escape(qa_epic)}</h1>')
    for case in cases:
        groups = _group_steps(case.steps)
        expected = steps(groups['Then'])
        expected_code = " ".join(step.text for step in groups["Then"])
        if re.search(r"\b(200|201)\b", expected_code):
            expected = "<div class='outcome outcome-positive'>🟢 Positivo</div>" + expected
        elif re.search(r"\b(403|409|400)\b", expected_code):
            expected = "<div class='outcome outcome-negative'>🔴 Negativo</div>" + expected
        headers, cells = [], []
        if groups['Given']:
            headers.append("<th>Precondizioni<em>(Given)</em></th>")
            cells.append(f"<td>{steps(groups['Given'])}</td>")
        if groups['When']:
            headers.append("<th>Azione<em>(When)</em></th>")
            cells.append(f"<td>{steps(groups['When'])}</td>")
        if case.examples and case.examples.rows:
            headers.append("<th>Combinazioni<em>(Examples)</em></th>")
            cells.append(f"<td>{examples(case.examples)}</td>")
        if groups['Then']:
            headers.append("<th>Esito atteso<em>(Then)</em></th>")
            cells.append(f"<td>{expected}</td>")
        table_class = ('with-combinations' if len(headers) == 4 else
                       'without-combinations' if len(headers) == 3 and all(groups.values()) else
                       'partial')
        authors = ("<p><b>Autori dell’ultima modifica:</b> " +
                   html.escape(", ".join(case.last_change_authors)) + "</p>"
                   if case.last_change_authors else "")
        description = ("<p>" +
                       "<br>".join(text(line) for line in case.description.splitlines()) +
                       "</p>" if case.description else "")
        escaped_id = html.escape(case.test_id, quote=True)
        parts.extend([f"<div class='test-case' id='{escaped_id}'><b>Test Automatici - <code>{escaped_id}</code></b><div class='objective'><b>{text(case.title)}</b>{description}</div>{authors}", f"<table class='test-table {table_class}'><thead><tr>{''.join(headers)}</tr></thead><tbody>", f"<tr>{''.join(cells)}</tr></tbody></table></div>"])
        if case.sources:
            parts.append("<details><summary>Fonti</summary><ul>" + "".join(
                f"<li>{html.escape(source.get('repo', ''))} · {html.escape(source['branch'])} · <code>{html.escape(source['commit'])}</code> · {html.escape(source['path'])}</li>"
                for source in case.sources) + "</ul></details>")
    parts.append("</section></body></html>")
    document = "\n".join(parts)
    if not html_path.exists() or html_path.read_text(encoding="utf-8") != document:
        html_path.write_text(document, encoding="utf-8")
    print(f"Generated {html_path}")


def render_inline(value: str, value_terms: dict[str, str] | None = None) -> str:
    value = replace_quoted_values(value, value_terms or {})
    value = html.unescape(value)
    value = html.escape(value)
    def highlight_quoted(match: re.Match[str]) -> str:
        content = match.group(1) or match.group(2)
        style = 'version-value' if re.fullmatch(r'[vV]\d+(?:\.\d+)*', content) else 'quoted-value'
        return f"<span class='{style}'>{match.group(0)}</span>"
    value = re.sub(r'&quot;(.+?)&quot;|“(.+?)”', highlight_quoted, value)
    value = re.sub(r"&lt;([^&]+)&gt;|(%[A-Za-z_][A-Za-z0-9_]*)", lambda match: f"<span class='variable'>{match.group(0)}</span>", value)
    value = re.sub(r"\b(PA\d+|PUBLISHED|SUSPENDED|ARCHIVING(?:_SUSPENDED)?|DEPRECATED|HTTP\s+\d{3})\b", r"<span class='value'>\1</span>", value)
    value = re.sub(r"`([^`]+)`", r"<code>\1</code>", value)
    value = re.sub(r"\*\*([^*]+)\*\*", r"<strong>\1</strong>", value)
    return value


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


def generate_active(repo: Path, config: Path, include_html: bool = False,
                    output_dir: Path | None = None,
                    repo_paths: dict[str, Path] | None = None,
                    use_state: bool = False) -> int:
    """Generate independent PSTs, preserving the existing output on conflicts."""
    settings = yaml.safe_load(config.read_text(encoding="utf-8")) or {}
    collector = ScenarioCollector(repo, settings.get("input", {}).get("id_pattern", r"\[([^\]]+)\]"))
    results = []
    failed = False
    destination = output_dir if output_dir is not None else repo / 'pst'
    for epic, specs in active_source_specs(repo).items():
        try:
            fingerprint = (input_fingerprint(repo, config, epic, specs, repo_paths, include_html)
                           if use_state else None)
            if fingerprint and is_current(destination, epic, fingerprint, include_html):
                results.append(f"- {epic}: invariato")
                continue
            cases = collect_from_sources(repo, epic, specs, collector.pattern, repo_paths)
            count = generate_pst(repo, epic, [], config, collector, include_html, output_dir, cases)
            if fingerprint:
                save_state(destination, epic, fingerprint, include_html)
            results.append(f"- {epic}: generato ({count} scenari)")
        except CollectionError as exc:
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
        parser.add_argument("--html", action="store_true", help="Also generate standalone HTML files")
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
        return generate_active(args.repo, args.config, args.html, args.output_dir, paths, args.use_state)

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
        parser.add_argument("--html", action="store_true", help="Also generate a standalone HTML file")
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
        settings = yaml.safe_load(args.config.read_text(encoding='utf-8')) or {}
        pattern = settings.get('input', {}).get('id_pattern', r'\[([^\]]+)\]')
        print(f"Fonti configurate per {args.qa_epic}: {specs}")
        cases = collect_from_sources(args.repo, args.qa_epic, specs, pattern, paths)
        count = generate_pst(args.repo, args.qa_epic, [], args.config,
                             include_html=args.html, output_dir=args.output_dir, cases=cases)
        return 0 if count else 1

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
