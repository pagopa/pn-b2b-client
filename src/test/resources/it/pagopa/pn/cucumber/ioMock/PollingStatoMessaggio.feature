@ioMock @polling
Feature: Polling dello Stato del Messaggio ed Evoluzione Temporale degli Snapshot
  Come connettore IO di SEND
  Voglio interrogare lo stato di recapito e lettura di una notifica su App IO
  Per verificare l'evoluzione cumulativa degli stati temporizzati o gestire eventuali errori e routing

  @MOCK_IO_POLLING_03_1
  Scenario Outline: [MOCK_IO_POLLING_03_1] Evoluzione temporale dello snapshot del messaggio (<finestra_temporale>)
    Given un messaggio inviato per la sequenza "OK_READ_THEN_PAID" alla finestra temporale "<finestra_temporale>"
    When viene richiesto lo stato del messaggio per il destinatario "RSSMRA80A01H5010"
    Then lo stato del messaggio risulta "<status>"
    And lo stato di lettura del messaggio risulta "<read_status>"
    And lo stato di pagamento del messaggio risulta "<payment_status>"

    Examples:
      | finestra_temporale | status    | read_status     | payment_status  |
      | T0_ELAPSED_LESS_5S | PROCESSED | NON_DISPONIBILE | NON_DISPONIBILE |
      | T1_ELAPSED_5_15S   | PROCESSED | READ            | NON_DISPONIBILE |
      | T2_ELAPSED_OVER_15S| PROCESSED | READ            | PAID            |

  @MOCK_IO_POLLING_03_2_A
  Scenario Outline: [MOCK_IO_POLLING_03_2_A] Rifiuto polling per codice fiscale destinatario formalmente non valido
    Given una richiesta di stato messaggio con identificativo valido per la sequenza "OK_READ_THEN_PAID"
    When viene richiesto lo stato del messaggio per il destinatario "<invalid_fiscal_code>"
    Then la richiesta di stato messaggio viene rifiutata per errore di validazione del codice fiscale

    Examples:
      | invalid_fiscal_code       |
      | INVALID_CF_FORMAT         |
      | 12345                     |
      | RSSMRA80A01H501!          |
      | RSSMRA80A01H5010_TOO_LONG |

  @MOCK_IO_POLLING_03_2_B
  Scenario Outline: [MOCK_IO_POLLING_03_2_B] Rifiuto polling per identificativo mock non valido o sequenza non censita
    Given una richiesta di stato messaggio con identificativo mock non valido "<invalid_id>"
    When viene richiesto lo stato del messaggio per il destinatario "RSSMRA80A01H5010"
    Then la richiesta di stato messaggio viene rifiutata per identificativo mock non valido

    Examples:
      | invalid_id                              |
      | MOCK-INVALID                            |
      | MOCK-seq-123-invalid@char               |
      | MOCK-unknown_sequence_ssm-1000-rand1234 |

  @MOCK_IO_ROUTER_GET_04_1_A @router
  Scenario: [MOCK_IO_ROUTER_GET_04_1_A] Routing trasparente a IO reale per richiesta stato con ID standard privo di prefisso mock
    Given una richiesta di stato messaggio con identificativo standard privo di prefisso mock "01ARZ3NDEKTSV4RRFFQ69G5FAV"
    When viene richiesto lo stato del messaggio per il destinatario "RSSMRA80A01H5010"
    Then la richiesta viene instradata con successo verso l'ambiente reale di IO
