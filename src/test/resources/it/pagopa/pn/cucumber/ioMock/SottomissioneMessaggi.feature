@ioMock @messages
Feature: Sottomissione messaggio e generazione IoMessageId su POST /messages (Mock IO e Lambda Router)
  Verifica della validazione formale, instradamento trasparente verso IO reale e routing verso il microservizio Mock con generazione di ioMessageId per sequenze censite a sistema.

  @MOCK_IO_SUBMIT_02_1_A
  Scenario: [MOCK_IO_SUBMIT_02_1_A] Instradamento verso Mock e formattazione conforme IoMessageId con marker di sequenza censita nel subject
    Given una sequenza valida censita a sistema "OK_READ_THEN_PAID"
    When invoco endpoint "POST /messages"
    Then verifico che lo status code della risposta sia 201 o 200
    And l'ID restituito deve essere un ioMessageId conforme per la sequenza "OK_READ_THEN_PAID"

  @MOCK_IO_SUBMIT_02_1_B
  Scenario: [MOCK_IO_SUBMIT_02_1_B] Routing trasparente a IO reale per richiesta con subject privo di marker sequenza
    Given preparo una richiesta di invio messaggio con subject ordinario privo di marker
    When invoco endpoint "POST /messages"
    Then verifico che lo status code della risposta sia 201 o 200
    And verifico che la richiesta sia stata inoltrata in modo trasparente a IO reale
    And verifico che gli header originali tra cui "Ocp-Apim-Subscription-Key" siano stati preservati
    And verifico che l'ID restituito non contenga il prefisso "MOCK-"

  @MOCK_IO_SUBMIT_02_2_A
  Scenario: [MOCK_IO_SUBMIT_02_2_A] [Variante 1] Errore 400 Bad Request per richiesta priva del campo obbligatorio fiscal_code
    Given preparo una richiesta di invio messaggio senza il campo "fiscal_code"
    When invoco endpoint "POST /messages"
    Then verifico che lo status code della risposta sia 400
    And verifico che il body della risposta contenga i dettagli di errore di validazione schema

  @MOCK_IO_SUBMIT_02_2_A
  Scenario: [MOCK_IO_SUBMIT_02_2_A] [Variante 2] Errore 400 Bad Request per richiesta priva dell'oggetto obbligatorio content
    Given preparo una richiesta di invio messaggio senza il campo "content"
    When invoco endpoint "POST /messages"
    Then verifico che lo status code della risposta sia 400
    And verifico che il body della risposta contenga i dettagli di errore di validazione schema

  @MOCK_IO_SUBMIT_02_2_A
  Scenario: [MOCK_IO_SUBMIT_02_2_A] [Variante 3] Errore 400 Bad Request per richiesta priva del sotto-campo obbligatorio subject in content
    Given preparo una richiesta di invio messaggio senza il sotto-campo "subject" in "content"
    When invoco endpoint "POST /messages"
    Then verifico che lo status code della risposta sia 400
    And verifico che il body della risposta contenga i dettagli di errore di validazione schema

  @MOCK_IO_SUBMIT_02_2_A
  Scenario: [MOCK_IO_SUBMIT_02_2_A] [Variante 4] Errore 400 Bad Request per richiesta priva del sotto-campo obbligatorio markdown in content
    Given preparo una richiesta di invio messaggio senza il sotto-campo "markdown" in "content"
    When invoco endpoint "POST /messages"
    Then verifico che lo status code della risposta sia 400
    And verifico che il body della risposta contenga i dettagli di errore di validazione schema

  @MOCK_IO_SUBMIT_02_2_A
  Scenario: [MOCK_IO_SUBMIT_02_2_A] [Variante 5] Errore 400 Bad Request per payload contenente campi extra non ammessi da OpenAPI
    When l'utente invia una richiesta POST a "/messages" con payload:
      """
      {
        "fiscal_code": "RSSMRA80A01H5010",
        "feature_level_type": "ADVANCED",
        "content": {
          "subject": "Comunicazione istituzionale @io:OK_READ_THEN_PAID",
          "markdown": "# Messaggio PagoPA\nTesto della notifica...",
          "payment_data": null,
          "invalid_extra_content": "not_allowed"
        },
        "unauthorized_extra_field": "unexpected_value"
      }
      """
    Then verifico che lo status code della risposta sia 400
    And verifico che il body della risposta contenga i dettagli di errore di validazione schema

  @MOCK_IO_SUBMIT_02_2_B
  Scenario Outline: [MOCK_IO_SUBMIT_02_2_B] Errore 400 Bad Request per codice fiscale destinatario formalmente non valido
    Given preparo una richiesta di invio messaggio con codice fiscale formalmente non valido "<invalid_fiscal_code>"
    When invoco endpoint "POST /messages"
    Then verifico che lo status code della risposta sia 400
    And verifico che il body della risposta contenga l'errore di formato del destinatario

    Examples:
      | invalid_fiscal_code |
      |                     |
      | INVALID_CF_FORMAT   |
      | 12345               |
      | RSSMRA80A01H5010_TOO_LONG |
      | RSSMRA80A01H501!    |

  @MOCK_IO_SUBMIT_02_2_C
  Scenario: [MOCK_IO_SUBMIT_02_2_C] Errore 400 Bad Request per marker contenente sequenza non censita o inesistente a sistema
    Given preparo una richiesta di invio messaggio con marker di sequenza non censita "unknown_seq"
    When invoco endpoint "POST /messages"
    Then verifico che lo status code della risposta sia 400
    And verifico che il body della risposta contenga l'errore di sequenza non configurata a sistema
