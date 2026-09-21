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
    Given una richiesta di invio messaggio con subject ordinario privo di marker verso destinatario whitelist "PLVLRT86R24Z112H"
    When viene richiesta la sottomissione del messaggio
    Then la richiesta viene instradata con successo verso l'ambiente reale di IO

  @MOCK_IO_SUBMIT_02_2_A
  Scenario Outline: [MOCK_IO_SUBMIT_02_2_A] Rifiuto sottomissione per payload non conforme alle specifiche OpenAPI
    Given una richiesta di invio messaggio non conforme per "<tipo_anomalia>"
    When viene richiesta la sottomissione del messaggio
    Then la richiesta viene rifiutata per errore di validazione formale

    Examples:
      | tipo_anomalia      |
      | SENZA_FISCAL_CODE  |
      | SENZA_CONTENT      |
      | SENZA_SUBJECT      |
      | SENZA_MARKDOWN     |
      | JSON_MALFORMATO    |

  @MOCK_IO_SUBMIT_02_2_B
  Scenario: [MOCK_IO_SUBMIT_02_2_B] Rifiuto invio a IO reale per destinatario non whitelistato privo di marker
    Given una richiesta di invio messaggio con subject ordinario senza marker per destinatario ordinario "RSSMRA80A01H5010"
    When viene richiesta la sottomissione del messaggio
    Then la richiesta viene rifiutata per destinatario non abilitato all'inoltro senza marker

  @MOCK_IO_SUBMIT_02_2_C
  Scenario: [MOCK_IO_SUBMIT_02_2_C] Rifiuto sottomissione per marker contenente sequenza non censita a sistema
    Given una richiesta di invio messaggio con marker di sequenza non censita "unknown_seq"
    When viene richiesta la sottomissione del messaggio
    Then la richiesta viene rifiutata per sequenza non censita a sistema
