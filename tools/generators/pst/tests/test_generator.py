import unittest
from datetime import date
from pathlib import Path
from tempfile import TemporaryDirectory

from pst_generator.cli import generate_structured_html, render_inline
from pst_generator.core import parse_test_cases
from pst_generator.markdown import render_document


def parse_test_case(source: str, pattern: str):
    cases = parse_test_cases(source, pattern)
    assert len(cases) == 1
    return cases[0]


class GeneratorTest(unittest.TestCase):
    def test_markdown_includes_html_report_information(self):
        source = """Feature: Example
  Scenario: [CASE_1] Rejected request
    Given a ready service
    Then response status code 403
"""
        cases = parse_test_cases(source, r"\[([^\]]+)\]")
        markdown = render_document(cases, qa_epic="QA-123",
                                   work_item_ids=["PN-456"], updated_on=date(2026, 11, 11))
        self.assertIn("| Work-item | PN-456 | Work-item associati |", markdown)
        self.assertIn("| QA Epic | QA-123 | Release attiva |", markdown)
        self.assertIn("| Data aggiornamento | 11/11/2026 |", markdown)
        self.assertIn("| Scenari | 1 |", markdown)
        self.assertIn("**DISCLAIMER**", markdown)
        self.assertIn("🔴 **Negativo**", markdown)
        self.assertIn("| Precondizioni<br><small>(Given)</small> | Esito atteso<br><small>(Then)</small> |", markdown)

    def test_existing_dictionaries_cover_quoted_values_and_example_headers(self):
        source = """Feature: Example
  Scenario Outline: [CASE_<variante>] Payment
    Given il "Comune_Multi" ha una notifica
    Then response status code 403
    Examples:
      | variante |
      | A        |
"""
        cases = parse_test_cases(source, r"\[([^\]]+)\]")
        business_terms = {"variante": "Variante"}
        value_terms = {"Comune_Multi": "Mittente"}
        status_terms = {"403": "operazione non consentita"}
        markdown = render_document(cases, business_terms=business_terms,
                                   value_terms=value_terms, status_terms=status_terms)
        self.assertIn('• il <small><code>Mittente</code></small> ha una notifica', markdown)
        self.assertIn('**Variante:** A', markdown)
        self.assertIn('• operazione non consentita', markdown)
        self.assertIn('"Comune_Multi"', cases[0].steps[0].text)
        with TemporaryDirectory() as directory:
            output = Path(directory) / "pst.html"
            generate_structured_html(output, "QA-123", [], cases,
                                     business_terms, value_terms, status_terms)
            html = output.read_text(encoding="utf-8")
            self.assertIn("<th>Variante</th>", html)
            self.assertIn("<span class='quoted-value'>&quot;Mittente&quot;</span>", html)
            self.assertIn("<li>operazione non consentita</li>", html)

    def test_placeholder_in_case_id_does_not_break_html_attribute(self):
        source = """Feature: Example
  Scenario Outline: [PAYMENT_EVENTS_RACE_02_1_<variante>] Payment order
    When <primo> pays
    Then one payment is recorded
    Examples:
      | variante | primo  | secondo |
      | A        | pagoPA | App IO  |
      | B        | App IO | pagoPA  |
"""
        cases = parse_test_cases(source, r"\[([^\]]+)\]")
        with TemporaryDirectory() as directory:
            output = Path(directory) / "pst.html"
            generate_structured_html(output, "QA-123", [], cases,
                {"variante": "Variante", "primo": "Prima comunicazione",
                 "secondo": "Seconda comunicazione"})
            html = output.read_text(encoding="utf-8")
            self.assertIn("id='PAYMENT_EVENTS_RACE_02_1_&lt;variante&gt;'", html)
            self.assertIn("<code>PAYMENT_EVENTS_RACE_02_1_&lt;variante&gt;</code>", html)
            self.assertNotIn("id='PAYMENT_EVENTS_RACE_02_1_<span", html)
            self.assertIn("<table class='examples-table'><thead><tr><th>Variante</th>"
                          "<th>Prima comunicazione</th><th>Seconda comunicazione</th>", html)
            self.assertIn("<tr><td>A</td><td>pagoPA</td><td>App IO</td></tr>", html)
            self.assertIn("<tr><td>B</td><td>App IO</td><td>pagoPA</td></tr>", html)

    def test_quoted_values_are_highlighted_without_changing_the_text(self):
        rendered = render_inline('Il "Comune_Multi" usa “V30” e "v10"; l\'ente resta invariato')
        self.assertIn("<span class='quoted-value'>&quot;Comune_Multi&quot;</span>", rendered)
        self.assertIn("<span class='version-value'>“V30”</span>", rendered)
        self.assertIn("<span class='version-value'>&quot;v10&quot;</span>", rendered)
        self.assertIn("l&#x27;ente resta invariato", rendered)
        self.assertEqual(rendered.count("quoted-value"), 1)
        self.assertEqual(rendered.count("version-value"), 2)

    def test_partial_scenarios_show_only_their_present_columns(self):
        source = """Feature: Example
  Scenario: [CASE_1] Verification only
    Then the service is connected

  Scenario: [CASE_2] Setup and verification
    Given a connected service
    Then delivery succeeds
"""
        cases = parse_test_cases(source, r"\[([^\]]+)\]")
        markdown = render_document(cases)
        first = markdown.split("### CASE_1", 1)[1].split("### CASE_2", 1)[0]
        self.assertNotIn("Precondizioni<br>", first)
        self.assertNotIn("Azione<br>", first)
        self.assertIn("Esito atteso<br>", first)
        with TemporaryDirectory() as directory:
            output = Path(directory) / "pst.html"
            generate_structured_html(output, "QA-123", [], cases)
            html = output.read_text(encoding="utf-8")
            first = html.split("id='CASE_1'", 1)[1].split("</table>", 1)[0]
            second = html.split("id='CASE_2'", 1)[1].split("</table>", 1)[0]
            self.assertEqual(first.count("<th>"), 1)
            self.assertEqual(first.count("<td"), 1)
            self.assertEqual(second.count("<th>"), 2)
            self.assertEqual(second.count("<td>"), 2)
            self.assertNotIn("<ul></ul>", html)
            self.assertIn(".test-table > tbody > tr > td{font-size:14px;line-height:1.28;overflow-wrap:anywhere}", html)

    def test_optional_scenario_description_is_shown_without_replacing_steps(self):
        source = """Feature: Example
  Scenario: [CASE_1] Receive service
    Dato un servizio pubblicato
    allora l'associazione è negata
    Given a published service
    Then the association is denied

  Scenario: [CASE_2] No description
    Given ready
    Then done
"""
        cases = parse_test_cases(source, r"\[([^\]]+)\]")
        markdown = render_document(cases)
        self.assertEqual(cases[0].description,
                         "Dato un servizio pubblicato\nallora l'associazione è negata")
        self.assertNotIn("**Descrizione dello scenario**", markdown)
        self.assertIn("> Receive service\n>\n> Dato un servizio pubblicato", markdown)
        self.assertIn("• a published service", markdown)
        with TemporaryDirectory() as directory:
            output = Path(directory) / "pst.html"
            generate_structured_html(output, "QA-123", [], cases)
            html = output.read_text(encoding="utf-8")
            self.assertIn("<div class='objective'><b>Receive service</b><p>Dato un servizio pubblicato<br>", html)

    def test_multiple_scenarios_are_rendered_in_compact_document(self):
        source = """Feature: Example
  Scenario: [CASE_1] First case
    Given the system is ready
    Then the first result is visible

  Scenario: [CASE_2] Second case
    Given the system is ready
    Then the second result is visible
"""
        cases = parse_test_cases(source, r"\[([^\]]+)\]")
        output = render_document(cases)

        self.assertEqual([case.test_id for case in cases], ["CASE_1", "CASE_2"])
        self.assertIn("### CASE_1", output)
        self.assertIn("> First case", output)
        self.assertIn("### CASE_2", output)
        self.assertIn("> Second case", output)
        self.assertIn("Precondizioni<br>", output)
        self.assertIn("the first result is visible", output)

    def test_business_terms_normalize_field_and_special_value(self):
        source = """Feature: Example
  Scenario: [CASE_3] Empty request ID
    Given a request with parameters:
      | requestId |
      | [EMPTY]   |
    When the request is sent
    Then the response is rejected
"""
        case = parse_test_case(source, r"\[([^\]]+)\]")
        output = render_document(
            [case],
            {"requestId": "ID della richiesta"},
            {"[EMPTY]": "vuoto"},
        )

        self.assertIn("<small><strong>ID della richiesta:</strong> vuoto</small>", output)

    def test_status_code_gets_business_description(self):
        source = """Feature: Example
  Scenario: [CASE_4] Invalid request
    Given a request is initialized
    When the request is sent
    Then an error is returned with status code 400
"""
        case = parse_test_case(source, r"\[([^\]]+)\]")
        output = render_document([case], status_terms={"400": "richiesta rifiutata per dati non validi"})

        self.assertIn("richiesta rifiutata per dati non validi", output)
        self.assertNotIn("HTTP 400", output)

        technical_output = render_document(
            [case],
            status_terms={"400": "richiesta rifiutata per dati non validi"},
            include_technical_codes=True,
        )
        self.assertIn("richiesta rifiutata per dati non validi — HTTP 400", technical_output)

    def test_list_values_are_described_semantically(self):
        source = """Feature: Example
  Scenario: [CASE_5] Invalid attachment list
    Given a request with parameters:
      | attachmentUrls                       |
      | [EMPTY],https://TestServer/allegato1 |
    When the request is sent
    Then the request is rejected
"""
        case = parse_test_case(source, r"\[([^\]]+)\]")
        output = render_document(
            [case],
            business_terms={"attachmentUrls": "URL degli allegati"},
            value_terms={"[EMPTY]": "vuoto"},
        )

        self.assertIn("La lista degli URL degli allegati contiene un elemento vuoto e un URL valido", output)

    def test_two_column_tables_are_key_value_rows(self):
        source = """Feature: Example
  Scenario: [CASE_6] Key value table
    Given a notification is initialized
      | subject               | example notification |
      | senderDenomination    | Example municipality |
      | physicalCommunication | REGISTERED_LETTER_890 |
    When the notification is sent
    Then the notification is accepted
"""
        case = parse_test_case(source, r"\[([^\]]+)\]")
        output = render_document(
            [case],
            business_terms={"senderDenomination": "denominazione del mittente"},
        )

        self.assertIn('<small><strong>subject:</strong> <code>example notification</code></small>', output)
        self.assertIn('<small><strong>denominazione del mittente:</strong> <code>Example municipality</code></small>', output)

    def test_assertion_tables_are_hidden_by_default(self):
        source = """Feature: Example
  Scenario: [CASE_7] Timeline event
    Given a notification is initialized
      | senderDenomination | Example municipality |
    When the notification is sent
    Then the timeline event exists
      | details | NOT_NULL |
      | code    | CON080   |
"""
        case = parse_test_case(source, r"\[([^\]]+)\]")
        output = render_document([case])

        self.assertIn('<small><strong>sender denomination:</strong> <code>Example municipality</code></small>', output)
        self.assertIn("the timeline event exists", output)
        self.assertNotIn("CON080", output)

    def test_step_tables_keep_recipient_context_and_duplicate_checks(self):
        source = """Feature: Example
  Scenario: [CASE_8] Two recipients
    Given recipient Mario:
      | digitalDomicile         | NULL  |
      | physicalAddress_address | Via 1 |
    And recipient Anna:
      | digitalDomicile         | NULL  |
      | physicalAddress_address | Via 2 |
    Then event "AAR_GENERATION" exists
    And event "AAR_GENERATION" exists
"""
        case = parse_test_case(source, r"\[([^\]]+)\]")
        output = render_document([case], business_terms={
            "digitalDomicile": "Domicilio digitale",
            "physicalAddress_address": "Indirizzo fisico",
        })
        self.assertIn("recipient Mario:<br>&nbsp;&nbsp;<small><strong>Domicilio digitale:</strong> <code>NULL</code></small><br>&nbsp;&nbsp;<small><strong>Indirizzo fisico:</strong> <code>Via 1</code></small>", output)
        self.assertIn("recipient Anna:<br>&nbsp;&nbsp;<small><strong>Domicilio digitale:</strong> <code>NULL</code></small><br>&nbsp;&nbsp;<small><strong>Indirizzo fisico:</strong> <code>Via 2</code></small>", output)
        self.assertIn("event <small><code>AAR_GENERATION</code></small> exists <small>(verifica 1/2)</small>", output)
        self.assertIn("event <small><code>AAR_GENERATION</code></small> exists <small>(verifica 2/2)</small>", output)

    def test_excluded_field_matches_exact_key_only(self):
        source = """Feature: Example
  Scenario: [CASE_9] Timeline details
    Given a timeline event exists
      | details                    | NOT_NULL |
      | details_deliveryDetailCode | CON020   |
    Then delivery succeeds
"""
        case = parse_test_case(source, r"\[([^\]]+)\]")
        output = render_document([case], excluded_fields=["details"])
        self.assertNotIn("NOT_NULL", output)
        self.assertIn("CON020", output)
        self.assertIn("details delivery detail code", output)

    def test_excluded_fields_requires_a_list(self):
        with self.assertRaisesRegex(ValueError, "output.exclude_fields"):
            render_document([], excluded_fields="details")

    def test_field_value_phrase_prefers_exact_field_over_global_value(self):
        source = """Feature: Example
  Scenario: [CASE_10] Missing values
    Given a recipient is prepared
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
      | subject         | Test |
    Then the request is ready
"""
        case = parse_test_case(source, r"\[([^\]]+)\]")
        output = render_document(
            [case],
            business_terms={"digitalDomicile": "Domicilio digitale",
                            "physicalAddress": "Indirizzo fisico", "subject": "Oggetto"},
            field_value_phrases={"*": {"NULL": "{field}: non configurato"},
                                 "digitalDomicile": {"NULL": "Senza domicilio digitale configurato"}},
        )
        self.assertIn("<small>Senza domicilio digitale configurato</small>", output)
        self.assertIn("<small>Indirizzo fisico: non configurato</small>", output)
        self.assertIn("<small><strong>Oggetto:</strong> <code>Test</code></small>", output)
        self.assertNotIn("<code>NULL</code>", output)

    def test_timeline_wait_phrase_keeps_optional_detail_code(self):
        source = """Feature: Example
  Scenario: [CASE_11] Timeline polling
    Given a notification exists
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION" per l'utente 0
    Then done
"""
        case = parse_test_case(source, r"\[([^\]]+)\]")
        output = render_document(
            [case],
            business_terms={"deliveryDetailCode": "codice di dettaglio"},
            step_phrases=[{
                "source": 'vengono letti gli eventi fino all\'elemento di timeline della notifica "{element}"',
                "target": 'Si attende e si verifica la presenza dell’evento di timeline "{element}"',
            }],
        )
        self.assertIn("Si attende e si verifica la presenza dell’evento di timeline <small><code>AAR_GENERATION</code></small>", output)
        self.assertIn("Si attende e si verifica la presenza dell’evento di timeline <small><code>SEND_ANALOG_PROGRESS</code></small> con codice di dettaglio <small><code>CON020</code></small>", output)
        self.assertIn("vengono letti gli eventi fino all'elemento di timeline della notifica <small><code>AAR_GENERATION</code></small> per l'utente 0", output)
