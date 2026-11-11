import re
from datetime import date

from .core import Step, TestCase


DISCLAIMER = ("Le verifiche descritte in questo e in tutti i documenti di progettazione "
              "coprono esclusivamente le funzionalità applicative lato software. "
              "Operazioni che coinvolgono hardware dei dispositivi (es. camera, lettura "
              "QR code, NFC, sensori, risoluzioni) vengono eseguite se possibile, in "
              "funzione del device disponibile al momento, ma non sono testate su "
              "dispositivi diversi.")


def replace_quoted_values(text: str, value_terms: dict[str, str]) -> str:
    """Apply exact value mappings inside straight or typographic double quotes."""
    if not value_terms:
        return text
    text = re.sub(r'"([^"]+)"',
                  lambda match: f'"{value_terms.get(match.group(1), match.group(1))}"', text)
    return re.sub(r'“([^”]+)”',
                  lambda match: f'“{value_terms.get(match.group(1), match.group(1))}”', text)


def render_document(
    tests: list[TestCase],
    business_terms: dict[str, str] | None = None,
    value_terms: dict[str, str] | None = None,
    status_terms: dict[str, str] | None = None,
    include_technical_codes: bool = False,
    layout: str = "cards",
    technical_details: str = "none",
    qa_epic: str | None = None,
    work_item_ids: list[str] | None = None,
    updated_on: date | None = None,
) -> str:
    business_terms = business_terms or {}
    value_terms = value_terms or {}
    status_terms = status_terms or {}
    if layout != "cards":
        raise ValueError(f"Unsupported layout: {layout}")
    if technical_details not in {"none", "compact", "full"}:
        raise ValueError(f"Unsupported technical_details mode: {technical_details}")
    title = f"PST — {qa_epic}" if qa_epic else "PST — Casi di test"
    lines = [f"# {title}", "", "## Informazioni generali", "",
             "| Campo | Valore | Note |", "| --- | --- | --- |",
             f"| Work-item | {', '.join(work_item_ids or []) or 'Da definire'} | Work-item associati |",
             f"| QA Epic | {qa_epic or 'Da definire'} | Release attiva |",
             "| Responsabile | Da definire | Fonte da definire |",
             f"| Data aggiornamento | {(updated_on or date.today()).strftime('%d/%m/%Y')} | Generata automaticamente |",
             f"| Scenari | {len(tests)} | |", "", "**DISCLAIMER**", "", DISCLAIMER,
             "", "## Casi di test", ""]
    for test in tests:
        groups = _group_steps(test.steps)
        data_steps = test.steps if technical_details == "full" else groups["Given"]
        data = _format_tables(data_steps, business_terms, value_terms)
        short_id = _compact_id(test.test_id)
        lines.extend([f"### {short_id}", "", f"> {replace_quoted_values(test.title, value_terms)}"])
        if test.description:
            lines.append(">")
            lines.extend(f"> {_escape_markdown(replace_quoted_values(line, value_terms))}" if line else ">"
                         for line in test.description.splitlines())
        lines.append("")
        if test.last_change_authors:
            authors = ", ".join(_escape_markdown(author) for author in test.last_change_authors)
            lines.extend([f"**Autori dell’ultima modifica:** {authors}", ""])
        columns = []
        if groups["Given"]:
            given = _step_cell(groups["Given"], status_terms, include_technical_codes, value_terms)
            if data != "—":
                given += f"<br><br>**Dati rilevanti:** {data}"
            columns.append(("Precondizioni<br><small>(Given)</small>", given))
        if groups["When"]:
            columns.append(("Azione<br><small>(When)</small>",
                            _step_cell(groups["When"], status_terms, include_technical_codes, value_terms)))
        if test.examples and test.examples.rows:
            columns.append(("Combinazioni<br><small>(Examples)</small>",
                            _examples_cell(test.examples, business_terms, value_terms)))
        if groups["Then"]:
            expected_text = " ".join(step.text for step in groups["Then"])
            outcome = ("🟢 **Positivo**<br>" if re.search(r"\b(200|201)\b", expected_text) else
                       "🔴 **Negativo**<br>" if re.search(r"\b(403|409|400)\b", expected_text) else "")
            columns.append(("Esito atteso<br><small>(Then)</small>", outcome +
                            _step_cell(groups["Then"], status_terms, include_technical_codes, value_terms)))
        lines.extend(["| " + " | ".join(label for label, _ in columns) + " |",
                      "| " + " | ".join("---" for _ in columns) + " |",
                      "| " + " | ".join(value for _, value in columns) + " |", ""])
        lines.extend(
            [
                "<details>",
                "<summary>Tracciabilità</summary>",
                "",
                f"- Test ID: `{test.test_id}`",
                f"- Tag: {', '.join(f'`{tag}`' for tag in test.tags) if test.tags else 'nessuno'}",
                "",
                "</details>",
                "",
                "---",
                "",
            ]
        )
    return "\n".join(lines).rstrip() + "\n"


def _table_cell(value: str) -> str:
    return value.replace("|", "\\|").replace("\n", "<br>")


def _step_cell(steps: list[Step], status_terms: dict[str, str],
               include_technical_codes: bool, value_terms: dict[str, str]) -> str:
    return "<br>".join("• " + _table_cell(_format_step(step.text, status_terms,
                                    include_technical_codes, value_terms)) for step in steps)


def _examples_cell(table, business_terms: dict[str, str], value_terms: dict[str, str]) -> str:
    headers = [business_terms.get(header, header) for header in table.headers or []]
    return "<br>".join("; ".join(f"**{_table_cell(header)}:** {_table_cell(value_terms.get(value, value))}"
                                  for header, value in zip(headers, row)) for row in table.rows)


def _render_examples(table, business_terms: dict[str, str], value_terms: dict[str, str]) -> str:
    headers = [business_terms.get(header, header) for header in table.headers or []]
    lines = ["| " + " | ".join(headers) + " |", "| " + " | ".join("---" for _ in headers) + " |"]
    lines.extend("| " + " | ".join(value_terms.get(value, value) for value in row) + " |"
                 for row in table.rows)
    return "\n".join(lines)


def _compact_id(test_id: str) -> str:
    if "-TC" in test_id:
        return "TC" + test_id.split("-TC", 1)[1]
    return test_id


def _group_steps(steps: list[Step]) -> dict[str, list[Step]]:
    groups: dict[str, list[Step]] = {"Given": [], "When": [], "Then": []}
    current = "Given"
    for step in steps:
        keyword = step.keyword.split()[0]
        if keyword in groups:
            current = keyword
        groups[current].append(step)
    return groups


def _format_step_list(
    steps: list[Step],
    status_terms: dict[str, str] | None = None,
    include_technical_codes: bool = False,
    value_terms: dict[str, str] | None = None,
) -> list[str]:
    if not steps:
        return ["_Nessun elemento rilevato._"]
    return [
        f"- {_format_step(step.text, status_terms or {}, include_technical_codes, value_terms or {}).replace('|', '\\|')}"
        for step in steps
    ]


def _format_step(text: str, status_terms: dict[str, str], include_technical_codes: bool,
                 value_terms: dict[str, str] | None = None) -> str:
    text = replace_quoted_values(text, value_terms or {})
    match = re.search(r"(?:codice di stato|status code|HTTP)\s+(\d{3})", text, re.IGNORECASE)
    if not match:
        return _escape_markdown(text)
    code = match.group(1)
    description = status_terms.get(code)
    if not description:
        return _escape_markdown(text)
    return f"{_escape_markdown(description)} — HTTP {code}" if include_technical_codes else _escape_markdown(description)


def _escape_markdown(text: str) -> str:
    return text.replace("<", "&lt;").replace(">", "&gt;")


def _format_tables(steps: list[Step], business_terms: dict[str, str], value_terms: dict[str, str]) -> str:
    values = []
    for step in steps:
        if not step.table:
            continue
        for field, value in step.table.key_values:
            values.append(_format_value(field, value, business_terms, value_terms))
    return "<br>".join(value.replace("|", "\\|") for value in values) or "—"


def _format_value(field: str, value: str, business_terms: dict[str, str], value_terms: dict[str, str]) -> str:
    label = business_terms.get(field, _humanize_field(field))
    if "," in value:
        return _format_list(label, value, value_terms)
    normalized = value_terms.get(value, value)
    if normalized == value:
        for source, target in value_terms.items():
            normalized = normalized.replace(source, target)
    if normalized in {"vuoto", "composto solo da spazi"}:
        return f"{label} è {normalized}"
    if normalized == "non valorizzato":
        return f"{label} non è valorizzato"
    if normalized != value:
        return f"{label} = {normalized}"
    return f'{label} = "{normalized}"'


def _format_list(label: str, value: str, value_terms: dict[str, str]) -> str:
    descriptions = []
    for item in value.split(","):
        item = item.strip()
        normalized = value_terms.get(item, item)
        if normalized == "vuoto":
            descriptions.append("un elemento vuoto")
        elif normalized == "non valorizzato":
            descriptions.append("un elemento non valorizzato")
        elif normalized == "composto solo da spazi":
            descriptions.append("un elemento composto solo da spazi")
        elif re.match(r"https?://", item, re.IGNORECASE):
            descriptions.append("un URL valido")
        else:
            descriptions.append(f'un elemento valorizzato con "{normalized}"')
    prefix = f"La lista degli {label}" if label.lower().startswith("url ") else f"La lista di {label}"
    return f"{prefix} contiene " + _join_descriptions(descriptions)


def _join_descriptions(descriptions: list[str]) -> str:
    if len(descriptions) == 1:
        return descriptions[0]
    if len(descriptions) == 2:
        return f"{descriptions[0]} e {descriptions[1]}"
    return ", ".join(descriptions[:-1]) + " e " + descriptions[-1]


def _humanize_field(field: str) -> str:
    words = re.sub(r"(?<!^)(?=[A-Z])", " ", field).replace("_", " ").lower()
    return words
