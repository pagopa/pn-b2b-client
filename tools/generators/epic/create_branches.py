"""Create QA feature branches for resolved Epic keys and record them in the manifest."""

import argparse
import json
import os
from pathlib import Path
from urllib import error, parse, request

import yaml

from epic_generator import ISSUE_KEY, fill_manifest_field


def github_call(token: str, method: str, path: str, body: dict | None = None) -> dict:
    data = json.dumps(body).encode("utf-8") if body is not None else None
    req = request.Request("https://api.github.com" + path, data=data, method=method,
                          headers={"Authorization": f"Bearer {token}",
                                   "Accept": "application/vnd.github+json",
                                   "Content-Type": "application/json"})
    try:
        with request.urlopen(req, timeout=30) as response:
            return json.loads(response.read())
    except error.HTTPError as exc:
        if method == "GET" and exc.code == 404:
            return {"missing": True}
        raise RuntimeError(f"GitHub {method} {path} failed ({exc.code}): "
                           f"{exc.read().decode('utf-8', errors='replace')[:400]}") from exc


def create_branches(path: Path, results: list[dict], tokens: dict[str, str], call=github_call) -> list[dict]:
    document = yaml.safe_load(path.read_text(encoding="utf-8"))
    items = {(item["id"], item["sequence"]): item
             for item in document["release"]["work-items"]}
    requested = []
    for result in results:
        identity = (result["work_item"], result["sequence"])
        item = items.get(identity)
        if item is None or item.get("qa_epic") != result["qa_epic"]:
            raise ValueError(f"Manifest and Jira result differ for {identity}")
        repo = item.get("repo")
        if repo not in ("qe_suit", "pn-b2b-client"):
            raise ValueError(f"Missing or invalid repo for {identity}")
        key = result["qa_epic"]
        if not ISSUE_KEY.fullmatch(key) or not key.startswith("QA-"):
            raise ValueError(f"Invalid QA Epic key: {key}")
        branch = f"feature/{key}"
        if item.get("branch"):
            if item["branch"] != branch:
                raise ValueError(f"Existing branch differs from {branch} for {identity}")
            continue
        requested.append({**result, "repo": repo, "branch": branch})

    if not requested:
        return []
    for repo in {item["repo"] for item in requested}:
        if not tokens.get(repo):
            raise ValueError(f"Missing GitHub token for {repo}")

    for item in requested:
        repo = item["repo"]
        token = tokens[repo]
        base = f"/repos/pagopa/{repo}/git/ref/heads/"
        ref = call(token, "GET", base + parse.quote(item["branch"], safe="/"))
        if ref.get("missing"):
            develop = call(token, "GET", base + "develop")
            sha = develop.get("object", {}).get("sha")
            if not sha:
                raise RuntimeError(f"Cannot resolve develop in pagopa/{repo}")
            call(token, "POST", f"/repos/pagopa/{repo}/git/refs",
                 {"ref": f"refs/heads/{item['branch']}", "sha": sha})
            print(f"Created {repo}:{item['branch']}", flush=True)
        else:
            print(f"Reused {repo}:{item['branch']}", flush=True)
    fill_manifest_field(path, requested, "branch")
    return requested


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("manifest", type=Path)
    parser.add_argument("results", type=Path)
    args = parser.parse_args()
    root = Path(__file__).resolve().parents[3] / "releases"
    path = args.manifest.resolve()
    if root not in path.parents or path.suffix not in {".yaml", ".yml"}:
        parser.error("Select a release manifest under releases/")
    try:
        results = json.loads(args.results.read_text(encoding="utf-8"))
        create_branches(path, results, {
            "pn-b2b-client": os.getenv("GITHUB_TOKEN", ""),
            "qe_suit": os.getenv("QE_SUIT_TOKEN", ""),
        })
    except (OSError, ValueError, RuntimeError, KeyError, yaml.YAMLError) as exc:
        parser.error(str(exc))


if __name__ == "__main__":
    main()
