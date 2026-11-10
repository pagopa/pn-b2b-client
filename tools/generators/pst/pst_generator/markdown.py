import re

from .core import Step, TestCase


def replace_quoted_values(text: str, value_terms: dict[str, str]) -> str:
    """Apply exact value mappings inside straight or typographic double quotes."""
    if not value_terms:
        return text
    text = re.sub(r'"([^"]+)"',
                  lambda match: f'"{value_terms.get(match.group(1), match.group(1))}"', text)
    return re.sub(r'“([^”]+)”',
                  lambda match: f'“{value_terms.get(match.group(1), match.group(1))}”', text)


def _render_table(step: Step) -> list[str]:
    if not step.table:
        return []
    table = step.table
    if table.headers is None:
        lines = [""]
        lines.extend("| " + " | ".join(row) + " |" for row in table.rows)
        return lines
    lines = ["", "| " + " | ".join(table.headers) + " |", "| " + " | ".join("---" for _ in table.headers) + " |"]
    lines.extend("| " + " | ".join(row) + " |" for row in table.rows)
    return lines


def render_markdown(test: TestCase, mode: str = "deterministic") -> str:
    groups: dict[str, list[Step]] = {"Given": [], "When": [], "Then": []}
    current = "Given"
    for step in test.steps:
        keyword = step.keyword.split()[0]
        if keyword in groups:
            current = keyword
        groups[current].append(step)

    lines = [f"# {test.test_id} — {test.title}", "", "## Obiettivo", "", test.title, "", "## Metadati", ""]
    lines.append(f"- Modalità: `{mode}`")
    lines.append(f"- Tag: {', '.join(test.tags) if test.tags else 'nessuno'}")
    lines.extend(["", "## Precondizioni", ""])
    lines.extend(_render_steps(groups["Given"]))
    lines.extend(["", "## Step e combinazioni", ""])
    lines.extend(_render_steps(groups["When"]))
    lines.extend(["", "## Esito atteso", ""])
    lines.extend(_render_steps(groups["Then"]))
    lines.extend(["", "## Tracciabilità", "", f"- Test ID: `{test.test_id}`", "- Riferimenti: righe del feature file riportate per ogni step.", ""])
    return "\n".join(lines)


def render_document(
    tests: list[TestCase],
    mode: str = "deterministic",
    business_terms: dict[str, str] | None = None,
    value_terms: dict[str, str] | None = None,
    status_terms: dict[str, str] | None = None,
    include_technical_codes: bool = False,
    layout: str = "cards",
    technical_details: str = "none",
    qa_epic: str | None = None,
) -> str:
    business_terms = business_terms or {}
    value_terms = value_terms or {}
    status_terms = status_terms or {}
    if layout != "cards":
        raise ValueError(f"Unsupported layout: {layout}")
    if technical_details not in {"none", "compact", "full"}:
        raise ValueError(f"Unsupported technical_details mode: {technical_details}")
    title = f"PST — {qa_epic}" if qa_epic else "PST — Casi di test"
    lines = [f"# {title}", "", "## Panoramica", "", f"**{len(tests)} casi di test**", "", "## Casi di test", ""]
    for test in tests:
        groups = _group_steps(test.steps)
        preconditions = _format_steps(groups["Given"])
        operations = _format_steps(groups["When"])
        expected = _format_steps(groups["Then"], status_terms, include_technical_codes)
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
        if groups["Given"]:
            lines.extend(["**Precondizioni**", ""])
            lines.extend(_format_step_list(groups["Given"], status_terms, include_technical_codes, value_terms))
        if data != "—":
            lines.extend(["", "**Dati rilevanti**", "", data])
        if groups["When"]:
            lines.extend(["", "**Operazione**", ""])
            lines.extend(_format_step_list(groups["When"], status_terms, include_technical_codes, value_terms))
        if groups["Then"]:
            lines.extend(["", "**Risultato atteso**", ""])
            lines.extend(_format_step_list(groups["Then"], status_terms, include_technical_codes, value_terms))
        if test.examples:
            lines.extend(["", "**Combinazioni**", "",
                          _render_examples(test.examples, business_terms, value_terms), "", "---", ""])
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


def _format_steps(
    steps: list[Step],
    status_terms: dict[str, str] | None = None,
    include_technical_codes: bool = False,
) -> str:
    if not steps:
        return "—"
    status_terms = status_terms or {}
    return "<br>".join(_format_step(step.text, status_terms, include_technical_codes).replace("|", "\\|") for step in steps)


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


def _render_steps(steps: list[Step]) -> list[str]:
    lines: list[str] = []
    for step in steps:
        lines.append(f"- **{step.keyword}** {step.text} _(feature line {step.line})_")
        lines.extend(_render_table(step))
    return lines or ["_Nessun elemento rilevato._"]
