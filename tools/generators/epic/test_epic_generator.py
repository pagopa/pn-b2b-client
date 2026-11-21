import tempfile
from pathlib import Path
import unittest

from epic_generator import apply, preview, update_manifest


class PreviewTest(unittest.TestCase):
    def manifest_path(self, directory, product="interop"):
        folder = Path(directory) / product
        folder.mkdir()
        return folder / "release.yaml"

    def test_preview_skips_existing_epics_and_links_new_ones(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text(
                "release:\n  version: 2.26.0\n  status: in-progress\n  work-items:\n"
                "    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n      title: Service update\n      size: 2.5\n      qa_epic:\n      branch:\n"
                "    - id: PIN-2\n      repo: pn-b2b-client\n      sequence: 2\n      qa_epic: QA-123\n      branch:\n", encoding="utf-8")
            requests = preview(manifest)
        self.assertEqual(len(requests), 1)
        create, link, comment = requests[0]["calls"]
        self.assertEqual(create["body"]["fields"]["issuetype"]["name"], "QA Epic")
        self.assertEqual(create["body"]["fields"]["summary"],
                         "QA validation for PIN-1 - Service update")
        self.assertEqual(create["body"]["fields"]["labels"],
                         ["release-2.26.0-PIN-1-1", "2.26.0"])
        self.assertEqual(create["body"]["fields"]["fixVersions"],
                         [{"id": "<QA_PROJECT_VERSION_ID>"}])
        self.assertEqual(create["body"]["fields"]["customfield_10648"], {"value": "INTEROP"})
        self.assertEqual(create["body"]["fields"]["timetracking"],
                         {"originalEstimate": "2.5d"})
        self.assertEqual(link["body"]["outwardIssue"]["key"], "PIN-1")
        self.assertEqual(link["body"]["type"]["name"], "tests")
        self.assertIn("2.26.0", comment["body"]["body"]["content"][0]["content"][0]["text"])

    def test_send_product_and_unknown_folder(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory, "send")
            manifest.write_text("release:\n  version: test\n  status: in-progress\n"
                                "  work-items:\n    - id: PN-1\n      repo: pn-b2b-client\n      sequence: 1\n      qa_epic:\n      branch:\n", encoding="utf-8")
            self.assertEqual(preview(manifest)[0]["calls"][0]["body"]["fields"]
                             ["customfield_10648"], {"value": "SEND"})
            self.assertEqual(preview(manifest)[0]["calls"][0]["body"]["fields"]
                             ["labels"], ["release-test-PN-1-1", "test"])
            other = self.manifest_path(directory, "unknown")
            with self.assertRaisesRegex(ValueError, "releases/interop or releases/send"):
                preview(other)

    def test_invalid_item_prevents_all_requests(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text(
                "release:\n  version: 2.26.0\n  status: in-progress\n  work-items:\n"
                "    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n      qa_epic:\n      branch:\n"
                "    - sequence: 2\n      qa_epic:\n", encoding="utf-8")
            with self.assertRaisesRegex(ValueError, "Work item 2"):
                preview(manifest)

    def test_blank_title_is_invalid(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text("release:\n  version: test\n  status: in-progress\n"
                                "  work-items:\n    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n"
                                "      title: '  '\n      qa_epic:\n      branch:\n", encoding="utf-8")
            with self.assertRaisesRegex(ValueError, "title must be a non-empty string"):
                preview(manifest)

    def test_repo_is_required(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text("release:\n  version: test\n  status: in-progress\n"
                                "  work-items:\n    - id: PN-1\n      sequence: 1\n",
                                encoding="utf-8")
            with self.assertRaisesRegex(ValueError, "repo must be qe_suit or pn-b2b-client"):
                preview(manifest)

    def test_qa_epic_and_branch_lines_are_required_even_when_empty(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            base = ("release:\n  version: test\n  status: in-progress\n"
                    "  work-items:\n    - id: PIN-1\n      repo: qe_suit\n"
                    "      sequence: 1\n")
            for fields, missing in (("", "qa_epic"), ("      qa_epic:\n", "branch")):
                with self.subTest(missing=missing):
                    manifest.write_text(base + fields, encoding="utf-8")
                    with self.assertRaisesRegex(ValueError, f"must include {missing}"):
                        preview(manifest)
            manifest.write_text(base + "      qa_epic:\n      branch:\n", encoding="utf-8")
            self.assertEqual(len(preview(manifest)), 1)

    def test_size_must_be_positive_days(self):
        for value in ("S", "0", "-1", "true"):
            with self.subTest(value=value), tempfile.TemporaryDirectory() as directory:
                manifest = self.manifest_path(directory)
                manifest.write_text("release:\n  version: test\n  status: in-progress\n"
                                    "  work-items:\n    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n"
                                    f"      size: {value}\n      qa_epic:\n      branch:\n", encoding="utf-8")
                with self.assertRaisesRegex(ValueError, "positive number of days"):
                    preview(manifest)

    def test_sequence_required_and_pair_unique_before_preview(self):
        for items, message in [
            ("    - id: PIN-1\n      repo: pn-b2b-client\n      qa_epic:\n", "positive integer sequence"),
            ("    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: true\n      qa_epic:\n      branch:\n", "positive integer sequence"),
            ("    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n      qa_epic:\n      branch:\n"
             "    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n      qa_epic:\n      branch:\n", "Duplicate \\(id, sequence\\)"),
        ]:
            with self.subTest(items=items), tempfile.TemporaryDirectory() as directory:
                manifest = self.manifest_path(directory)
                manifest.write_text(
                    "release:\n  version: 2.26.0\n  status: in-progress\n  work-items:\n"
                    + items, encoding="utf-8")
                with self.assertRaisesRegex(ValueError, message):
                    preview(manifest)

    def test_apply_uses_created_key_for_link_and_comment(self):
        class FakeClient:
            def __init__(self):
                self.calls = []

            def call(self, method, path, body=None):
                self.calls.append((method, path, body))
                if path.endswith("issueLinkType"):
                    return {"issueLinkTypes": [
                        {"name": "Test", "outward": "tests", "inward": "is tested by"}]}
                if path.endswith("/project/QA/versions"):
                    return [{"id": "20001", "name": "2.26.0"}]
                if method == "GET" and "?fields=summary" in path:
                    return {"fields": {"summary": "Service update"}}
                if method == "POST" and path.endswith("/issue"):
                    return {"key": "QA-123"}
                return {}

        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text(
                "release:\n  version: 2.26.0\n  status: in-progress\n  work-items:\n"
                "    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n      qa_epic:\n      branch:\n", encoding="utf-8")
            requests = preview(manifest)
        client = FakeClient()
        self.assertEqual(apply(requests, client),
                         [{"work_item": "PIN-1", "sequence": 1, "qa_epic": "QA-123",
                           "source": "created"}])
        self.assertEqual(client.calls[2][:2],
                         ("GET", "/rest/api/3/issue/PIN-1?fields=summary"))
        self.assertEqual(client.calls[3][2]["jql"],
                         'project = QA AND labels = "release-2.26.0-PIN-1-1"')
        self.assertEqual(client.calls[4][2]["fields"]["summary"],
                         "QA validation for PIN-1 - Service update")
        self.assertEqual(client.calls[4][2]["fields"]["fixVersions"], [{"id": "20001"}])
        self.assertEqual(client.calls[5][2]["outwardIssue"], {"key": "PIN-1"})
        self.assertEqual(client.calls[5][2]["inwardIssue"], {"key": "QA-123"})
        self.assertEqual(client.calls[5][2]["type"], {"name": "Test"})
        self.assertEqual(client.calls[6][1], "/rest/api/3/issue/QA-123/comment")

    def test_apply_prefers_manifest_title_without_reading_jira_summary(self):
        class FakeClient:
            def __init__(self):
                self.calls = []

            def call(self, method, path, body=None):
                self.calls.append((method, path, body))
                if path.endswith("issueLinkType"):
                    return {"issueLinkTypes": [
                        {"name": "Test", "outward": "tests", "inward": "is tested by"}]}
                if path.endswith("/project/QA/versions"):
                    return [{"id": "20001", "name": "test"}]
                if method == "POST" and path.endswith("/issue"):
                    return {"key": "QA-123"}
                return {}

        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text("release:\n  version: test\n  status: in-progress\n"
                                "  work-items:\n    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n"
                                "      title: Specific QA scope\n      qa_epic:\n      branch:\n", encoding="utf-8")
            requests = preview(manifest)
        client = FakeClient()
        apply(requests, client)
        self.assertEqual(client.calls[2][:2],
                         ("GET", "/rest/api/3/issue/PIN-1?fields=key"))
        self.assertEqual(client.calls[4][2]["fields"]["summary"],
                         "QA validation for PIN-1 - Specific QA scope")
        self.assertEqual(client.calls[4][2]["fields"]["fixVersions"], [{"id": "20001"}])

    def test_existing_label_reconciles_without_creation(self):
        class FakeClient:
            def __init__(self):
                self.calls = []

            def call(self, method, path, body=None):
                self.calls.append((method, path, body))
                if path.endswith("issueLinkType"):
                    return {"issueLinkTypes": [
                        {"name": "Test", "outward": "tests", "inward": "is tested by"}]}
                if path.endswith("/project/QA/versions"):
                    return [{"id": "20001", "name": "2.26.0"}]
                if method == "GET" and "?fields=summary" in path:
                    return {"fields": {"summary": "Service update"}}
                if path.endswith("search/jql"):
                    return {"issues": [{"key": "QA-123", "fields": {
                        "customfield_10648": {"value": "INTEROP"}}}]}
                if "?fields=project,issuetype,labels,issuelinks" in path:
                    return {"key": "QA-123", "fields": {
                        "project": {"key": "QA"}, "issuetype": {"name": "QA Epic"},
                        "customfield_10648": {"value": "INTEROP"},
                        "labels": ["release-2.26.0-PIN-1-1"],
                        "issuelinks": [{"type": {"name": "Test"},
                                        "outwardIssue": {"key": "PIN-1"}}]}}
                if path.endswith("/comment?maxResults=100"):
                    return {"comments": []}
                return {}

        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text(
                "release:\n  version: 2.26.0\n  status: in-progress\n  work-items:\n"
                "    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n      qa_epic:\n      branch:\n", encoding="utf-8")
            requests = preview(manifest)
        client = FakeClient()
        self.assertEqual(apply(requests, client),
                         [{"work_item": "PIN-1", "sequence": 1, "qa_epic": "QA-123",
                           "source": "existing"}])
        self.assertFalse(any(path.endswith("/issue") and method == "POST"
                             for method, path, _ in client.calls))
        self.assertTrue(any(path.endswith("/QA-123/comment") and method == "POST"
                            for method, path, _ in client.calls))

    def test_same_label_from_another_product_is_not_reused(self):
        class FakeClient:
            def __init__(self):
                self.calls = []

            def call(self, method, path, body=None):
                self.calls.append((method, path, body))
                if path.endswith("issueLinkType"):
                    return {"issueLinkTypes": [{"name": "Test", "outward": "tests",
                                                "inward": "is tested by"}]}
                if path.endswith("/project/QA/versions"):
                    return [{"id": "1", "name": "2.26.0"}]
                if path.endswith("search/jql"):
                    return {"issues": [{"key": "QA-100", "fields": {
                        "customfield_10648": {"value": "INTEROP"}}}]}
                if method == "POST" and path.endswith("/issue"):
                    return {"key": "QA-200"}
                return {}

        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory, "send")
            manifest.write_text("release:\n  version: 2.26.0\n  status: in-progress\n"
                                "  work-items:\n    - id: PN-1\n      repo: qe_suit\n"
                                "      sequence: 1\n      title: Scope\n      qa_epic:\n"
                                "      branch:\n", encoding="utf-8")
            client = FakeClient()
            self.assertEqual(apply(preview(manifest), client)[0]["qa_epic"], "QA-200")
            self.assertTrue(any(method == "POST" and path.endswith("/issue")
                                for method, path, _ in client.calls))

    def test_pending_epic_rejects_populated_branch_before_jira(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text("release:\n  version: test\n  status: in-progress\n"
                                "  work-items:\n    - id: PN-1\n      repo: qe_suit\n"
                                "      sequence: 1\n      qa_epic:\n"
                                "      branch: feature/old\n", encoding="utf-8")
            with self.assertRaisesRegex(ValueError, "leave branch empty"):
                preview(manifest)

    def test_long_summary_stops_before_creating_any_issue(self):
        class FakeClient:
            def __init__(self):
                self.calls = []

            def call(self, method, path, body=None):
                self.calls.append((method, path))
                if path.endswith("issueLinkType"):
                    return {"issueLinkTypes": [{"name": "Test", "outward": "tests",
                                                "inward": "is tested by"}]}
                if path.endswith("/project/QA/versions"):
                    return [{"id": "1", "name": "test"}]
                return {}

        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text("release:\n  version: test\n  status: in-progress\n"
                                "  work-items:\n    - id: PIN-1\n      repo: qe_suit\n"
                                "      sequence: 1\n      title: Short\n      qa_epic:\n"
                                "      branch:\n    - id: PIN-2\n      repo: qe_suit\n"
                                "      sequence: 2\n      title: " + "X" * 255 + "\n"
                                "      qa_epic:\n      branch:\n", encoding="utf-8")
            client = FakeClient()
            with self.assertRaisesRegex(ValueError, "summary exceeds 255 characters for PIN-2"):
                apply(preview(manifest), client)
            self.assertFalse(any(path.endswith("/issue") for method, path in client.calls))

    def test_manifest_update_preserves_other_text(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text("# Release\nrelease:\n  version: test\n"
                                "  work-items:\n    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n"
                                "      qa_epic: # pending\n      branch: feature/foo\n"
                                "    - id: PIN-2\n      repo: pn-b2b-client\n      sequence: 1\n      branch: feature/bar\n",
                                encoding="utf-8")
            changed = update_manifest(manifest, [
                {"work_item": "PIN-1", "sequence": 1, "qa_epic": "QA-123"},
                {"work_item": "PIN-2", "sequence": 1, "qa_epic": "QA-124"}])
            self.assertTrue(changed)
            self.assertEqual(manifest.read_text(encoding="utf-8"),
                             "# Release\nrelease:\n  version: test\n"
                             "  work-items:\n    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n"
                             "      qa_epic: QA-123 # pending\n      branch: feature/foo\n"
                             "    - id: PIN-2\n      repo: pn-b2b-client\n      sequence: 1\n      qa_epic: QA-124\n"
                             "      branch: feature/bar\n")

    def test_branch_update_preserves_existing_epic_without_branch(self):
        from epic_generator import fill_manifest_field

        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            original = ("release:\n  work-items:\n"
                        "    - id: PIN-1\n      sequence: 1\n      qa_epic: QA-123\n"
                        "    - id: PIN-2\n      sequence: 1\n      qa_epic: QA-124\n")
            manifest.write_text(original, encoding="utf-8")
            self.assertTrue(fill_manifest_field(manifest, [
                {"work_item": "PIN-2", "sequence": 1, "branch": "feature/QA-124"}
            ], "branch"))
            self.assertEqual(manifest.read_text(encoding="utf-8"),
                             original.replace("      qa_epic: QA-124\n",
                                              "      branch: feature/QA-124\n"
                                              "      qa_epic: QA-124\n"))

    def test_manifest_update_accepts_uppercase_yaml_null(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text("release:\n  work-items:\n    - id: PIN-1\n"
                                "      sequence: 1\n      qa_epic: NULL\n"
                                "      branch: Null\n", encoding="utf-8")
            self.assertTrue(update_manifest(manifest, [{"work_item": "PIN-1",
                                                        "sequence": 1, "qa_epic": "QA-123"}]))
            self.assertIn("qa_epic: QA-123", manifest.read_text())
            from epic_generator import fill_manifest_field
            self.assertTrue(fill_manifest_field(manifest, [{"work_item": "PIN-1",
                                                            "sequence": 1,
                                                            "branch": "feature/QA-123"}], "branch"))

    def test_manifest_update_accepts_quoted_empty_fields(self):
        from epic_generator import fill_manifest_field

        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text("release:\n  work-items:\n    - id: PIN-1\n"
                                "      sequence: 1\n      qa_epic: \"\"\n"
                                "      branch: ''\n", encoding="utf-8")
            self.assertTrue(update_manifest(manifest, [{"work_item": "PIN-1",
                                                        "sequence": 1, "qa_epic": "QA-123"}]))
            self.assertTrue(fill_manifest_field(manifest, [{"work_item": "PIN-1",
                                                            "sequence": 1,
                                                            "branch": "feature/QA-123"}], "branch"))
            self.assertIn("qa_epic: QA-123", manifest.read_text())
            self.assertIn("branch: feature/QA-123", manifest.read_text())

    def test_reconciliation_rejects_wrong_link_direction(self):
        class FakeClient:
            def call(self, method, path, body=None):
                if path.endswith("issueLinkType"):
                    return {"issueLinkTypes": [{"name": "Test", "outward": "tests",
                                                "inward": "is tested by"}]}
                if path.endswith("/project/QA/versions"):
                    return [{"id": "20001", "name": "test"}]
                if path.endswith("search/jql"):
                    return {"issues": [{"key": "QA-123", "fields": {
                        "customfield_10648": {"value": "INTEROP"}}}]}
                if "?fields=project,issuetype,labels,issuelinks" in path:
                    return {"key": "QA-123", "fields": {
                        "project": {"key": "QA"}, "issuetype": {"name": "QA Epic"},
                        "customfield_10648": {"value": "INTEROP"},
                        "labels": ["release-test-PIN-1-1"],
                        "issuelinks": [{"type": {"name": "Test"},
                                        "inwardIssue": {"key": "PIN-1"}}]}}
                return {}

        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text("release:\n  version: test\n  status: in-progress\n"
                                "  work-items:\n    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n"
                                "      title: Scope\n      qa_epic:\n      branch:\n", encoding="utf-8")
            with self.assertRaisesRegex(RuntimeError, "wrong way"):
                apply(preview(manifest), FakeClient())

    def test_manifest_update_rejects_populated_key(self):
        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            content = ("release:\n  work-items:\n    - id: PIN-1\n"
                       "      sequence: 1\n      qa_epic: QA-123\n")
            manifest.write_text(content, encoding="utf-8")
            with self.assertRaisesRegex(ValueError, "already populated"):
                update_manifest(manifest, [{"work_item": "PIN-1", "sequence": 1,
                                            "qa_epic": "QA-124"}])
            self.assertEqual(manifest.read_text(encoding="utf-8"), content)

    def test_missing_fix_version_blocks_creation(self):
        class FakeClient:
            def __init__(self):
                self.calls = []

            def call(self, method, path, body=None):
                self.calls.append((method, path))
                if path.endswith("issueLinkType"):
                    return {"issueLinkTypes": [
                        {"name": "Test", "outward": "tests", "inward": "is tested by"}]}
                if path.endswith("/project/QA/versions"):
                    return [{"id": "20001", "name": "other"}]
                return {}

        with tempfile.TemporaryDirectory() as directory:
            manifest = self.manifest_path(directory)
            manifest.write_text("release:\n  version: 2.26.0\n  status: in-progress\n"
                                "  work-items:\n    - id: PIN-1\n      repo: pn-b2b-client\n      sequence: 1\n      qa_epic:\n      branch:\n",
                                encoding="utf-8")
            requests = preview(manifest)
        client = FakeClient()
        with self.assertRaisesRegex(ValueError, "Fix version '2.26.0' must already exist"):
            apply(requests, client)
        self.assertEqual(len(client.calls), 2)


if __name__ == "__main__":
    unittest.main()
