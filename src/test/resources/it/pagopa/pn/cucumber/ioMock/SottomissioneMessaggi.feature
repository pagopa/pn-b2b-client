@ioMock @messages
Feature: Sottomissione del Messaggio e Generazione dell'Identificativo
  Come connettore IO di SEND
  Voglio inviare messaggi di notifica verso App IO
  Per instradare le comunicazioni verso il Mock con generazione di ioMessageId o verso l'ambiente reale

  @MOCK_IO_SUBMIT_02_1_A
  Scenario: [MOCK_IO_SUBMIT_02_1_A] Sottomissione con sequenza censita e generazione ioMessageId conforme (Mock)
    Given una sequenza valida censita a sistema "OK_READ_THEN_PAID"
    When viene richiesta la sottomissione del messaggio
    Then il messaggio viene preso in carico e viene generato un identificativo conforme per la sequenza "OK_READ_THEN_PAID"

  @MOCK_IO_SUBMIT_02_1_B
  Scenario: [MOCK_IO_SUBMIT_02_1_B] Routing trasparente a IO reale per richiesta con subject ordinario privo di marker
    Given una richiesta di invio messaggio con subject ordinario privo di marker
    When viene richiesta la sottomissione del messaggio
    Then la richiesta viene instradata con successo verso l'ambiente reale di IO
    And l'identificativo restituito non contiene il prefisso di mock

  @MOCK_IO_SUBMIT_02_2_A
  Scenario Outline: [MOCK_IO_SUBMIT_02_2_A] Rifiuto sottomissione per richiesta priva di campo obbligatorio
    Given una richiesta di invio messaggio priva del campo obbligatorio "<campo_mancante>"
    When viene richiesta la sottomissione del messaggio
    Then la richiesta viene rifiutata per errore di validazione formale

    Examples:
      | campo_mancante   |
      | fiscal_code      |
      | content          |
      | content.subject  |
      | content.markdown |

  @MOCK_IO_SUBMIT_02_2_A_EXTRA
  Scenario: [MOCK_IO_SUBMIT_02_2_A_EXTRA] Rifiuto sottomissione per payload contenente campi non ammessi dalle specifiche
    Given una richiesta di invio messaggio contenente campi non definiti nelle specifiche OpenAPI
    When viene richiesta la sottomissione del messaggio
    Then la richiesta viene rifiutata per errore di validazione formale

  @MOCK_IO_SUBMIT_02_2_B
  Scenario Outline: [MOCK_IO_SUBMIT_02_2_B] Rifiuto sottomissione per codice fiscale destinatario formalmente non valido
    Given una richiesta di invio messaggio con codice fiscale formalmente non valido "<invalid_fiscal_code>"
    When viene richiesta la sottomissione del messaggio
    Then la richiesta viene rifiutata per errore nel formato del destinatario

    Examples:
      | invalid_fiscal_code       |
      |                           |
      | INVALID_CF_FORMAT         |
      | 12345                     |
      | RSSMRA80A01H5010_TOO_LONG |
      | RSSMRA80A01H501!          |

  @MOCK_IO_SUBMIT_02_2_C
  Scenario: [MOCK_IO_SUBMIT_02_2_C] Rifiuto sottomissione per marker contenente sequenza non censita a sistema
    Given una richiesta di invio messaggio con marker di sequenza non censita "unknown_seq"
    When viene richiesta la sottomissione del messaggio
    Then la richiesta viene rifiutata per sequenza non censita a sistema
