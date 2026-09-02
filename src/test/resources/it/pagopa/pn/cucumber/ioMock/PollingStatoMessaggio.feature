@ioMock @polling
Feature: Polling dello Stato del Messaggio ed Evoluzione Temporale degli Snapshot
  Come connettore IO di SEND
  Voglio interrogare lo stato di recapito e lettura di una notifica su App IO
  Per verificare l'evoluzione cumulativa degli stati temporizzati o gestire eventuali errori di validazione

  @MOCK_IO_POLLING_03_1_A
  Scenario: [MOCK_IO_POLLING_03_1_A] Polling stato messaggio a T0 (elapsed < 5s) con stato PROCESSED
    Given una sequenza valida censita a sistema "OK_READ_THEN_PAID"
    And viene richiesta la sottomissione del messaggio
    And il messaggio viene preso in carico e viene generato un identificativo conforme per la sequenza "OK_READ_THEN_PAID"
    When viene richiesto lo stato del messaggio per il destinatario "RSSMRA80A01H5010"
    Then lo stato del messaggio risulta "PROCESSED"
    And lo stato di lettura del messaggio non è ancora disponibile
    And lo stato di pagamento del messaggio non è ancora disponibile
    And i metadati del messaggio contengono il codice fiscale "RSSMRA80A01H5010"

  @MOCK_IO_POLLING_03_1_B
  Scenario: [MOCK_IO_POLLING_03_1_B] Polling stato messaggio a T1 (5s <= elapsed < 15s) con snapshot cumulativo READ
    Given un messaggio inviato per la sequenza "std_read_paid" con tempo trascorso compreso tra 5 e 15 secondi
    When viene richiesto lo stato del messaggio per il destinatario "RSSMRA80A01H5010"
    Then lo stato del messaggio risulta "PROCESSED"
    And lo stato di lettura del messaggio risulta "READ"
    And lo stato di pagamento del messaggio non è ancora disponibile

  @MOCK_IO_POLLING_03_1_C
  Scenario: [MOCK_IO_POLLING_03_1_C] Polling stato messaggio a T2 (elapsed >= 15s) con snapshot cumulativo PAID
    Given un messaggio inviato per la sequenza "std_read_paid" con tempo trascorso superiore a 15 secondi
    When viene richiesto lo stato del messaggio per il destinatario "RSSMRA80A01H5010"
    Then lo stato del messaggio risulta "PROCESSED"
    And lo stato di lettura del messaggio risulta "READ"
    And lo stato di pagamento del messaggio risulta "PAID"

  @MOCK_IO_POLLING_03_2_A
  Scenario Outline: [MOCK_IO_POLLING_03_2_A] Rifiuto polling per codice fiscale destinatario formalmente non valido
    Given una richiesta di stato messaggio per il destinatario con codice fiscale non valido "<invalid_fiscal_code>"
    When viene richiesto lo stato del messaggio
    Then la richiesta di stato messaggio viene rifiutata per errore di validazione del codice fiscale

    Examples:
      | invalid_fiscal_code       |
      | INVALID_CF_FORMAT         |
      | 12345                     |
      | RSSMRA80A01H501!          |
      | RSSMRA80A01H5010_TOO_LONG |

  @MOCK_IO_POLLING_03_2_B
  Scenario Outline: [MOCK_IO_POLLING_03_2_B] Rifiuto polling per identificativo mock corrotto o non conforme
    Given una richiesta di stato messaggio con identificativo mock non valido "<invalid_id>"
    When viene richiesto lo stato del messaggio per il destinatario "RSSMRA80A01H5010"
    Then la richiesta di stato messaggio viene rifiutata per identificativo mock non valido

    Examples:
      | invalid_id                              |
      | MOCK-std_read_paid-notanumber-rand123   |
      | MOCK-INVALID                            |
      | MOCK--123-abc                           |
      | MOCK_malformed_underscore               |
      | MOCK-seq-123-invalid@char               |

  @MOCK_IO_POLLING_03_2_C
  Scenario: [MOCK_IO_POLLING_03_2_C] Rifiuto polling per sequenza non censita a sistema
    Given una richiesta di stato messaggio con identificativo mock avente sequenza non censita "unknown_sequence_ssm"
    When viene richiesto lo stato del messaggio per il destinatario "RSSMRA80A01H5010"
    Then la richiesta di stato messaggio viene rifiutata per sequenza non censita a sistema
