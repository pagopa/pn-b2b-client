"""Resolve a product and release name to a YAML manifest under releases/."""

import argparse
import os
from pathlib import Path
import re


def resolve_release(root: Path, product: str, name: str) -> Path:
    if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9_-]*", product):
        raise ValueError("Enter a product folder name without a path")
    if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9._-]*", name) or name.endswith((".yaml", ".yml")):
        raise ValueError("Enter only the release name, without path or .yaml extension")
    folder = root / product.lower()
    if not folder.is_dir():
        raise ValueError(f"Product folder {folder} does not exist")
    matches = sorted(path for path in folder.iterdir()
                     if path.is_file() and path.suffix in (".yaml", ".yml") and path.stem == name)
    if not matches:
        raise ValueError(f"No manifest found for release {name!r} under {folder}")
    if len(matches) != 1:
        raise ValueError(f"Multiple manifest files for release {name!r}: "
                         + ", ".join(str(path) for path in matches))
    return matches[0]


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("product", help="Product folder, for example interop")
    parser.add_argument("name", help="Release name without .yaml, for example 2.26.0")
    args = parser.parse_args()
    root = Path(__file__).resolve().parents[3]
    try:
        path = resolve_release(root / "releases", args.product, args.name).relative_to(root)
    except (OSError, ValueError) as exc:
        parser.error(str(exc))
    print(path)
    if os.getenv("GITHUB_ENV"):
        with open(os.environ["GITHUB_ENV"], "a", encoding="utf-8") as env_file:
            env_file.write(f"RELEASE_MANIFEST={path}\n")


if __name__ == "__main__":
    main()
