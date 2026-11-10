from dataclasses import dataclass, field
import re
from textwrap import dedent
from typing import Any


@dataclass(frozen=True)
class Table:
    headers: list[str] | None
    rows: list[list[str]]

    @property
    def key_values(self) -> list[tuple[str, str]]:
        if self.headers is not None:
            return list(zip(self.headers, self.rows[0])) if self.rows else []
        return [(row[0], row[1]) for row in self.rows if len(row) >= 2]


@dataclass(frozen=True)
class Step:
    keyword: str
    text: str
    line: int
    table: Table | None = None


@dataclass(frozen=True)
class TestCase:
    test_id: str
    title: str
    tags: list[str]
    steps: list[Step]
    examples: Table | None = None
    semantic_content: dict = field(default_factory=dict)
    sources: list[dict[str, str]] = field(default_factory=list)
    last_change_authors: list[str] = field(default_factory=list)
    description: str = ""


def _semantic_node(value):
    """Drop parser bookkeeping while retaining all Gherkin content."""
    if isinstance(value, dict):
        return {key: _semantic_node(item) for key, item in value.items()
                if key not in {"id", "location", "comments"}}
    if isinstance(value, list):
        return [_semantic_node(item) for item in value]
    return value


def _table(node: dict[str, Any] | None) -> Table | None:
    if not node:
        return None
    rows = [[cell["value"] for cell in row["cells"]] for row in node["rows"]]
    if not rows:
        return None
    # Two-column tables in the project are key/value rows. Wider tables use
    # the first row as a header, as in a conventional Gherkin data table.
    if all(len(row) == 2 for row in rows):
        return Table(headers=None, rows=rows)
    return Table(headers=rows[0], rows=rows[1:])


def parse_test_cases(source: str, id_pattern: str) -> list[TestCase]:
    try:
        from gherkin.parser import Parser
        from gherkin.token_scanner import TokenScanner
    except ImportError as exc:
        raise RuntimeError("Install dependencies with: pip install -r requirements.txt") from exc

    # Some repositories keep reusable scenario fragments without a Feature line.
    # The official parser requires the document root, so wrap that fragment in a
    # synthetic Feature while retaining source-line traceability.
    source_offset = 0
    if not re.search(r"(?m)^\s*Feature:\s*", source):
        source = "Feature: Generated PST input\n" + source
        source_offset = 1
    document = Parser().parse(TokenScanner(source))
    feature = document.get("feature")
    if not feature:
        raise ValueError("The feature file does not contain a Feature")

    scenarios = [child["scenario"] for child in feature["children"] if child.get("scenario")]
    if not scenarios:
        raise ValueError("The feature file does not contain a Scenario")

    cases = []
    for scenario in scenarios:
        tags = [tag["name"] for tag in feature.get("tags", []) + scenario.get("tags", [])]
        match = re.search(id_pattern, scenario["name"])
        if not match:
            if not any(tag.startswith('@work-item:') for tag in tags):
                continue
            raise ValueError(f"Scenario title must contain a test ID: {scenario['name']}")
        steps = [
            Step(
                keyword=step["keyword"].strip(),
                text=step["text"],
                line=step["location"]["line"] - source_offset,
                table=_table(step.get("dataTable")),
            )
            for step in scenario["steps"]
        ]
        title = scenario["name"]
        title = title[: match.start()] + title[match.end() :]
        title = title.strip(" \t:-—")
        examples = None
        if scenario.get("examples"):
            example = scenario["examples"][0]
            headers = [cell["value"] for cell in example["tableHeader"]["cells"]]
            rows = [[cell["value"] for cell in row["cells"]] for row in example.get("tableBody", [])]
            examples = Table(headers=headers, rows=rows)
        content = _semantic_node({
            "scenario": scenario,
            "feature_tags": feature.get("tags", []),
            "backgrounds": [child["background"] for child in feature["children"] if "background" in child],
        })
        cases.append(TestCase(match.group(1), title, tags, steps, examples, content,
                              description=dedent(scenario.get("description", "")).strip()))
    return cases
