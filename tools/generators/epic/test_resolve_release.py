"""Checks for release-name resolution used by the manual workflow."""

import tempfile
import unittest
from pathlib import Path

from resolve_release import resolve_release


class ResolveReleaseTest(unittest.TestCase):
    def test_unique_name(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            (root / "interop").mkdir()
            manifest = root / "interop" / "2.26.0.yaml"
            manifest.write_text("release: {}\n")
            self.assertEqual(resolve_release(root, "INTEROP", "2.26.0"), manifest)

    def test_same_name_in_two_products_is_unambiguous(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            for product in ("interop", "send"):
                (root / product).mkdir()
                (root / product / "2.26.0.yaml").touch()
            self.assertEqual(resolve_release(root, "send", "2.26.0"),
                             root / "send" / "2.26.0.yaml")

    def test_missing_and_path_inputs_fail(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            (root / "interop").mkdir()
            with self.assertRaisesRegex(ValueError, "No manifest"):
                resolve_release(root, "interop", "2.26.0")
            with self.assertRaisesRegex(ValueError, "only the release name"):
                resolve_release(root, "interop", "interop/2.26.0.yaml")
            with self.assertRaisesRegex(ValueError, "product folder name"):
                resolve_release(root, "../send", "2.26.0")


if __name__ == "__main__":
    unittest.main()
