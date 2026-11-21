"""Preview the Jira requests needed for QA Epics in a release manifest."""

import argparse
import json
import math
import os
from pathlib import Path
import re
from urllib import error, parse, request

import yaml


ISSUE_KEY = re.compile(r"[A-Z][A-Z0-9_]*-[1-9][0-9]*\Z")
RELEASE_LABEL_PART = re.compile(r"[A-Za-z0-9][A-Za-z0-9._-]*\Z")
PRODUCT_BY_RELEASE_FOLDER = {"interop": "INTEROP", "send": "SEND"}
QA_REPOSITORIES = {"qe_suit", "pn-b2b-client"}


def adf(message: str) -> dict:
    return {
        "type": "doc", "version": 1,
        "content": [{"type": "paragraph", "content": [{"type": "text", "text": message}]}],
    }


def preview(path: Path) -> list[dict]:
    product = PRODUCT_BY_RELEASE_FOLDER.get(path.parent.name.lower())
    if product is None:
        raise ValueError("Release manifest must be under releases/interop or releases/send")
    document = yaml.safe_load(path.read_text(encoding="utf-8"))
    if not isinstance(document, dict) or not isinstance(document.get("release"), dict):
        raise ValueError("The manifest must contain a release mapping")
    release = document["release"]
    if release.get("status") != "in-progress":
        raise ValueError("Release status must be in-progress")
    version = release.get("version")
    if not isinstance(version, str) or not RELEASE_LABEL_PART.fullmatch(version):
        raise ValueError("Release version must be a label-safe non-empty string")
    items = release.get("work-items")
    if not isinstance(items, list) or not items:
        raise ValueError("Release work-items must be a non-empty list")

    seen = set()
    seen_nodes = set()
    for index, item in enumerate(items, start=1):
        if not isinstance(item, dict):
            raise ValueError(f"Work item {index} must be a mapping")
        key = item.get("id")
        if not isinstance(key, str) or not ISSUE_KEY.fullmatch(key):
            raise ValueError(f"Work item {index} must have a Jira issue key in id")
        if not isinstance(item.get("repo"), str) or item["repo"] not in QA_REPOSITORIES:
            raise ValueError(f"Work item {index} repo must be qe_suit or pn-b2b-client")
        sequence = item.get("sequence")
        if type(sequence) is not int or sequence < 1:
            raise ValueError(f"Work item {index} must have a positive integer sequence")
        node = (key, sequence)
        if node in seen_nodes:
            raise ValueError(f"Duplicate (id, sequence) pair: {key}, {sequence}")
        seen_nodes.add(node)
        epic = item.get("qa_epic")
        title = item.get("title")
        if title is not None and (not isinstance(title, str) or not title.strip()):
            raise ValueError(f"Work item {index} title must be a non-empty string")
        size = item.get("size")
        if size is not None and (type(size) not in (int, float)
                                 or not math.isfinite(size) or size <= 0):
            raise ValueError(f"Work item {index} size must be a positive number of days")
        if epic is not None and epic != "":
            if not isinstance(epic, str) or not re.fullmatch(r"QA-[1-9][0-9]*", epic):
                raise ValueError(f"Work item {index} has an invalid qa_epic")
            pair = (key, epic)
            if pair in seen:
                raise ValueError(f"Duplicate (id, qa_epic) pair: {key}, {epic}")
            seen.add(pair)
        for field in ("qa_epic", "branch"):
            if field not in item:
                raise ValueError(f"Work item {index} must include {field}, even if empty")

    requests = []
    for index, item in enumerate(items, start=1):
        if item.get("qa_epic"):
            continue
        product_key = item["id"]
        label = f"release-{version}-{product_key}-{item['sequence']}"
        title = item.get("title")
        summary = f"QA validation for {product_key} - " + (
            title.strip() if title is not None else f"<JIRA_SUMMARY_OF_{product_key}>")
        placeholder = f"<QA_EPIC_KEY_FROM_CREATE_{index}>"
        requests.append({
            "work_item": product_key,
            "repo": item["repo"],
            "sequence": item["sequence"],
            "label": label,
            "release_version": version,
            "title": title.strip() if title is not None else None,
            "size": item.get("size"),
            "position": index,
            "calls": [
                {"method": "POST", "path": "/rest/api/3/issue", "body": {
                    "fields": {"project": {"key": "QA"},
                               "issuetype": {"name": "QA Epic"}, "summary": summary,
                               "labels": [label, version],
                               "fixVersions": [{"id": "<QA_PROJECT_VERSION_ID>"}],
                               "customfield_10648": {"value": product},
                               **({"timetracking": {"originalEstimate": f"{float(item['size']):g}d"}}
                                  if item.get("size") is not None else {})}}},
                {"method": "POST", "path": "/rest/api/3/issueLink", "body": {
                    "type": {"name": "tests"},
                    "outwardIssue": {"key": product_key},
                    "inwardIssue": {"key": placeholder}}},
                {"method": "POST", "path": f"/rest/api/3/issue/{placeholder}/comment",
                 "body": {"body": adf(
                     f"Created by the Release as Code workflow for release {version}.")}},
            ],
        })
    return requests


class JiraClient:
    def __init__(self, cloud_id: str, token: str):
        if not re.fullmatch(r"[0-9a-fA-F-]{36}", cloud_id) or not token:
            raise ValueError("JIRA_CLOUD_ID and JIRA_API_TOKEN are required for --apply")
        self.base = f"https://api.atlassian.com/ex/jira/{cloud_id}"
        self.token = token

    def call(self, method: str, path: str, body: dict | None = None) -> dict:
        if not path.startswith("/rest/api/3/"):
            raise ValueError("Unexpected Jira API path")
        data = json.dumps(body).encode("utf-8") if body is not None else None
        req = request.Request(
            self.base + path, data=data, method=method,
            headers={"Authorization": f"Bearer {self.token}",
                     "Accept": "application/json", "Content-Type": "application/json"},
        )
        try:
            with request.urlopen(req, timeout=30) as response:
                raw = response.read()
        except error.HTTPError as exc:
            detail = exc.read().decode("utf-8", errors="replace")[:500]
            raise RuntimeError(f"Jira {method} {path} failed ({exc.code}): {detail}") from exc
        except error.URLError as exc:
            raise RuntimeError(f"Jira {method} {path} failed: {exc.reason}") from exc
        return json.loads(raw) if raw else {}


def apply(requests: list[dict], client: JiraClient) -> list[dict]:
    if not requests:
        return []
    link_types = client.call("GET", "/rest/api/3/issueLinkType")["issueLinkTypes"]
    tests_links = [link for link in link_types if link.get("outward") == "tests"
                   and link.get("inward") == "is tested by"]
    if len(tests_links) != 1:
        raise ValueError("Expected one Jira link type with labels 'tests' / 'is tested by'")
    versions = client.call("GET", "/rest/api/3/project/QA/versions")
    version_name = requests[0]["release_version"]
    matches = [version for version in versions if version.get("name") == version_name]
    if len(matches) != 1 or not matches[0].get("id"):
        raise ValueError(f"Fix version {version_name!r} must already exist in Jira project QA")
    version_id = matches[0]["id"]
    summaries = {}
    for item in requests:
        issue = client.call("GET", "/rest/api/3/issue/" + parse.quote(item["work_item"])
                            + ("?fields=key" if item["title"] else "?fields=summary"))
        if item["title"] is None:
            summary = issue.get("fields", {}).get("summary")
            if not isinstance(summary, str) or not summary.strip():
                raise ValueError(f"Missing Jira summary for {item['work_item']}")
            summaries[item["work_item"]] = summary.strip()
    existing = {}
    for item in requests:
        search = client.call("POST", "/rest/api/3/search/jql", {
            "jql": f'project = QA AND labels = "{item["label"]}"',
            "fields": ["key", "issuetype", "labels"], "maxResults": 2,
        })
        matches = search.get("issues", [])
        if len(matches) > 1:
            raise RuntimeError(f"Multiple QA issues for {item['label']}: "
                               + ", ".join(issue["key"] for issue in matches))
        if matches:
            existing[item["position"]] = matches[0]["key"]

    results = []
    for item in requests:
        create, link, comment = item["calls"]
        fields = {**create["body"]["fields"],
                  "fixVersions": [{"id": version_id}],
                  "summary": f"QA validation for {item['work_item']} - "
                             f"{item['title'] or summaries[item['work_item']]}"}
        key = existing.get(item["position"])
        if key:
            issue = client.call("GET", "/rest/api/3/issue/" + parse.quote(key)
                                + "?fields=project,issuetype,labels,issuelinks")
            issue_fields = issue.get("fields", {})
            if (issue.get("key") != key
                    or issue_fields.get("project", {}).get("key") != "QA"
                    or issue_fields.get("issuetype", {}).get("name") != "QA Epic"
                    or item["label"] not in issue_fields.get("labels", [])):
                raise RuntimeError(f"Existing issue {key} does not match {item['label']}")
            links = [entry for entry in issue_fields.get("issuelinks", [])
                     if entry.get("type", {}).get("name") == tests_links[0]["name"]
                     and (entry.get("outwardIssue", {}).get("key") == item["work_item"]
                          or entry.get("inwardIssue", {}).get("key") == item["work_item"])]
            if len(links) > 1:
                raise RuntimeError(f"Multiple Test links from {key} to {item['work_item']}")
            if links and links[0].get("outwardIssue", {}).get("key") != item["work_item"]:
                raise RuntimeError(f"Test link on {key} points the wrong way")
            if not links:
                link_body = {**link["body"], "type": {"name": tests_links[0]["name"]},
                             "inwardIssue": {"key": key}}
                client.call(link["method"], link["path"], link_body)
            print(f"Reconciled {key} for {item['work_item']}", flush=True)
        else:
            result = client.call(create["method"], create["path"], {"fields": fields})
            key = result.get("key")
        if not isinstance(key, str) or not ISSUE_KEY.fullmatch(key):
            raise RuntimeError(f"Jira did not return a valid key for {item['work_item']}")
        if not existing.get(item["position"]):
            print(f"Created {key} for {item['work_item']}", flush=True)
            link_body = {**link["body"], "type": {"name": tests_links[0]["name"]},
                         "inwardIssue": {"key": key}}
            client.call(link["method"], link["path"], link_body)
        comment_path = comment["path"].replace(
            f"<QA_EPIC_KEY_FROM_CREATE_{item['position']}>", key)
        if existing.get(item["position"]):
            comments = client.call("GET", "/rest/api/3/issue/" + key
                                   + "/comment?maxResults=100")
            if comments.get("total", 0) > len(comments.get("comments", [])):
                raise RuntimeError(f"Cannot verify all comments on {key}")
            expected = comment["body"]["body"]
            if not any(entry.get("body") == expected for entry in comments.get("comments", [])):
                client.call(comment["method"], comment_path, comment["body"])
        else:
            client.call(comment["method"], comment_path, comment["body"])
        results.append({"work_item": item["work_item"], "sequence": item["sequence"],
                        "qa_epic": key, "source": "existing" if existing.get(item["position"])
                        else "created"})
    return results


def fill_manifest_field(path: Path, results: list[dict], field: str) -> bool:
    """Fill only empty fields, preserving all other YAML text."""
    if field not in {"qa_epic", "branch"}:
        raise ValueError("Unsupported manifest field")
    if not results:
        return False
    original = path.read_text(encoding="utf-8")
    document = yaml.safe_load(original)
    nodes = yaml.compose(original)
    release_node = next(value for key, value in nodes.value if key.value == "release")
    items_node = next(value for key, value in release_node.value
                      if key.value == "work-items")
    items = document["release"]["work-items"]
    replacements = {(result["work_item"], result["sequence"]): result[field]
                    for result in results}
    if len(replacements) != len(results):
        raise ValueError("Duplicate result for a work item")
    wanted = replacements.copy()
    edits = []
    for item, node in zip(items, items_node.value, strict=True):
        identity = (item["id"], item["sequence"])
        if identity not in wanted:
            continue
        if item.get(field) not in (None, ""):
            raise ValueError(f"{field} already populated for {identity}")
        fields = {key.value: (key, value) for key, value in node.value}
        if field in fields:
            key_node, value_node = fields[field]
            if not isinstance(value_node, yaml.ScalarNode) or value_node.value not in ("", "null", "~"):
                raise ValueError(f"Cannot safely edit {field} for {identity}")
            prefix = " " if value_node.start_mark.index == value_node.end_mark.index else ""
            edits.append((value_node.start_mark.index, value_node.end_mark.index,
                          prefix + wanted[identity]))
        else:
            key_node = fields["sequence"][0]
            line_end = original.index("\n", key_node.start_mark.index) + 1
            indent = " " * key_node.start_mark.column
            edits.append((line_end, line_end, f"{indent}{field}: {wanted[identity]}\n"))
        del wanted[identity]
    if wanted:
        raise ValueError(f"Results not found in manifest: {list(wanted)}")
    updated = original
    for start, end, replacement in sorted(edits, reverse=True):
        updated = updated[:start] + replacement + updated[end:]
    parsed = yaml.safe_load(updated)
    for old, new in zip(items, parsed["release"]["work-items"], strict=True):
        identity = (old["id"], old["sequence"])
        expected = {**old, field: replacements[identity]} if identity in replacements else old
        if new != expected:
            raise ValueError(f"Manifest edit changed fields other than {field}")
    path.write_text(updated, encoding="utf-8")
    return updated != original


def update_manifest(path: Path, results: list[dict]) -> bool:
    return fill_manifest_field(path, results, "qa_epic")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("manifest", type=Path, help="YAML file under releases/")
    parser.add_argument("--apply", action="store_true",
                        help="Create or reconcile Jira issues and fill qa_epic in the manifest")
    parser.add_argument("--result-file", type=Path,
                        help="With --apply, save resolved QA Epic keys for the branch step")
    args = parser.parse_args()
    if args.result_file and not args.apply:
        parser.error("--result-file requires --apply")
    path = args.manifest.resolve()
    releases = Path(__file__).resolve().parents[3] / "releases"
    if not path.is_file() or releases not in path.parents or path.suffix not in {".yaml", ".yml"}:
        parser.error("Select an existing YAML manifest under releases/")
    try:
        result = preview(path)
    except (OSError, yaml.YAMLError, ValueError) as exc:
        parser.error(str(exc))
    if args.apply:
        try:
            client = JiraClient(os.getenv("JIRA_CLOUD_ID", ""), os.getenv("JIRA_API_TOKEN", ""))
            created = apply(result, client)
            update_manifest(path, created)
            if args.result_file:
                args.result_file.write_text(json.dumps(created), encoding="utf-8")
        except (ValueError, RuntimeError, KeyError) as exc:
            parser.error(str(exc))
        print(json.dumps({"created": created}, ensure_ascii=False, indent=2))
    else:
        print(json.dumps({"manifest": str(path.relative_to(releases.parent)),
                          "requests": result}, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
