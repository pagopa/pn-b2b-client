import tempfile
from pathlib import Path
import unittest

from create_branches import create_branches


class CreateBranchesTest(unittest.TestCase):
    def test_creates_in_selected_repos_and_updates_only_branches(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = Path(directory) / "release.yaml"
            manifest.write_text(
                "# release\nrelease:\n  work-items:\n"
                "    - id: PN-1\n      sequence: 1\n      repo: pn-b2b-client\n"
                "      qa_epic: QA-123\n      branch:\n"
                "    - id: PN-2\n      sequence: 1\n      repo: qe_suit\n"
                "      qa_epic: QA-124\n", encoding="utf-8")
            calls = []

            def fake_call(token, method, path, body=None):
                calls.append((token, method, path, body))
                if path.endswith("/heads/develop"):
                    return {"object": {"sha": "abc123"}}
                if method == "GET":
                    return {"missing": True}
                return {"ref": body["ref"]}

            results = [{"work_item": "PN-1", "sequence": 1, "qa_epic": "QA-123"},
                       {"work_item": "PN-2", "sequence": 1, "qa_epic": "QA-124"}]
            create_branches(manifest, results, {"pn-b2b-client": "own",
                                                 "qe_suit": "other"}, fake_call)
            content = manifest.read_text(encoding="utf-8")
            self.assertIn("      branch: feature/QA-123\n", content)
            self.assertIn("      branch: feature/QA-124\n", content)
            self.assertTrue(content.startswith("# release\n"))
            self.assertEqual({(token, body["ref"]) for token, method, _, body in calls
                              if method == "POST"},
                             {("own", "refs/heads/feature/QA-123"),
                              ("other", "refs/heads/feature/QA-124")})

    def test_missing_cross_repo_token_fails_before_mutations(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = Path(directory) / "release.yaml"
            manifest.write_text("release:\n  work-items:\n    - id: PN-1\n"
                                "      sequence: 1\n      repo: qe_suit\n"
                                "      qa_epic: QA-123\n", encoding="utf-8")
            with self.assertRaisesRegex(ValueError, "Missing GitHub token for qe_suit"):
                create_branches(manifest, [{"work_item": "PN-1", "sequence": 1,
                                            "qa_epic": "QA-123"}], {},
                                lambda *args: self.fail("Unexpected GitHub call"))

    def test_reuses_existing_feature_branch(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = Path(directory) / "release.yaml"
            manifest.write_text("release:\n  work-items:\n    - id: PN-1\n"
                                "      sequence: 1\n      repo: qe_suit\n"
                                "      qa_epic: QA-123\n      branch:\n", encoding="utf-8")
            calls = []

            def fake_call(token, method, path, body=None):
                calls.append((method, path))
                return {"ref": "refs/heads/feature/QA-123"}

            create_branches(manifest, [{"work_item": "PN-1", "sequence": 1,
                                        "qa_epic": "QA-123"}], {"qe_suit": "token"},
                            fake_call)
            self.assertEqual([method for method, _ in calls], ["GET"])
            self.assertIn("branch: feature/QA-123", manifest.read_text())


if __name__ == "__main__":
    unittest.main()
