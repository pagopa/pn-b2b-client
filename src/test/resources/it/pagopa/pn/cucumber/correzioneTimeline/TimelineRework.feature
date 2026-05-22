Feature: Test relativi al SRS di correzione timeline


  @timelineRework
  Scenario: [TIMELINE_REWORK_1] Viene creata correttamente una richiesta di rework.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And si verifica che in fase di rework non ci sono richieste appese in stato diverso da DONE o ERROR
    Then viene invocata una richiesta di rework per la notifica appena creata
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"

  @timelineRework
  Scenario Outline: [TIMELINE_REWORK_2] Viene invocata con errori la chiamata di rework
    Then viene invocata una richiesta di rework con eccezione per la notifica appena creata con i seguenti parametri:
      | iun   | attemptId   | pcRetry   | recIndex   | expectedStatusCode   | expectedDeliveryFailureCause   | reason   |
      | <iun> | <attemptId> | <pcRetry> | <recIndex> | <expectedStatusCode> | <expectedDeliveryFailureCause> | <reason> |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 400
    Examples: :
  | iun                                | attemptId             | pcRetry         | recIndex         | expectedStatusCode | expectedDeliveryFailureCause  | reason         |
  | IUN-NOT_VALID                      | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | RECRI003C          | EMPTY_STRING                  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | EMPTY_STRING          | PCRETRY_0       | RECINDEX_0       | RECRI003C          | EMPTY_STRING                  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | EMPTY_STRING    | RECINDEX_0       | RECRI003C          | EMPTY_STRING                  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | RETRY_0         | RECINDEX_0       | RECRI003C          | EMPTY_STRING                  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | INDEX_0          | RECRI003C          | EMPTY_STRING                  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | EMPTY_STRING       | EMPTY_STRING                  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | RECRI003C          | EMPTY_STRING                  | EMPTY_STRING   |

  @timelineRework
  Scenario: [TIMELINE_REWORK_3] Viene verificata che la richiesta di rework viene rifiutata quando è associata ad uno IUN non valido
    Then viene invocata una richiesta di rework con eccezione per la notifica appena creata con i seguenti parametri:
      | iun                       |
      | ZJUJ-AAAA-AAAA-202512-X-1 |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 404


  @timelineRework
  Scenario: [TIMELINE_REWORK_4] Verifica che non sia possibile creare una nuova richiesta di rework quando ne esiste già una associata alla stessa notifica in stato diverso da DONE o ERROR
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then viene invocata una richiesta di rework per la notifica appena creata
    Then viene invocata una richiesta di rework per la notifica appena creata
    And si verifica che la chiamata sia andata in errore con il seguente status code: 409

  @timelineRework
  Scenario Outline: [TIMELINE_REWORK_5] Viene invocata l'API di rework passando degli statusCode non validi e si ci aspetta uno status code di tipo 404
    Then viene invocata una richiesta di rework con eccezione per la notifica appena creata con i seguenti parametri:
      | iun   | attemptId   | pcRetry   | recIndex   | expectedStatusCode   | expectedDeliveryFailureCause   | reason   |
      | <iun> | <attemptId> | <pcRetry> | <recIndex> | <expectedStatusCode> | <expectedDeliveryFailureCause> | <reason> |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 404
    Examples: :
  | iun                                | attemptId             | pcRetry         | recIndex         | expectedStatusCode | expectedDeliveryFailureCause  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | NOT_VALID          | EMPTY_STRING                  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | RECRI003C          | NOT_VALID                     | reason         |


    #  FASE1 Automtizzati

  @timelineRework
  Scenario: [TIMELINE_REWORK_8] Rework notifica monodestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato IN_VALIDATION.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason  |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON8 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_9] Rework notifica monodestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato ACCEPTED.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason  |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON9 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_11] Rework notifica monodestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato DELIVERING.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then Viene verificato che non sia arrivato un evento di "SEND_ANALOG_DOMICILE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON11 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_12] Rework notifica monodestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato DELIVERED
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON12 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_13] Rework notifica monodestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato UNREACHABLE.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON13 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_14] Rework notifica monodestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato CANCELLED.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON14 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_15] Rework notifica ATTEMPT_0 in OK, passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno attemptId inesistente.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON15 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_16] Rework monodestinatario ATTEMPT_0: verifica transizione a ERROR con statusCode errato.expectedStatusCode appartenente ad un prodotto postale diverso
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework con eccezione per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG003F          | M01                          | REASON16 |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 400

  @timelineRework
  Scenario: [TIMELINE_REWORK_18] Rework notifica multidestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato IN_VALIDATION.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON18 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_19] Rework notifica multidestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato ACCEPTED.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON19 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi
    Then Viene verificato che non sia arrivato un evento di "NOTIFICATION_TIMELINE_REWORKED"

  @timelineRework
  Scenario: [TIMELINE_REWORK_21] Rework notifica multidestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato DELIVERING.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON21 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi
    Then Viene verificato che non sia arrivato un evento di "NOTIFICATION_TIMELINE_REWORKED"

  @timelineRework
  Scenario: [TIMELINE_REWORK_23] Rework notifica multidestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato UNREACHABLE.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILEBADCAP_890 |
      | digitalDomicile         | NULL                                     |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL                                     |
      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILEBADCAP_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG003F          | M01                          | REASON23 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_24] Rework notifica multidestinatario passi allo stato ERROR qualora, giunta allo stato CREATED, la verifica asincrona rilevi uno IUN nello stato CANCELLED.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON24 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_25] Rework notifica multidestinatario ATTEMPT_0 RECINDEX_0: verifica ERROR con attemptId inesistente.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON25 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi
    Then Viene verificato che non sia arrivato un evento di "NOTIFICATION_TIMELINE_REWORKED"

  @timelineRework
  Scenario: [TIMELINE_REWORK_26] Rework notifica multidestinatario ATTEMPT_0 RECINDEX_0: verifica ERROR con statusCode errato.expectedStatusCode appartenente ad un prodotto postale diverso
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    Then viene invocata una richiesta di rework con eccezione per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG003F          | M01                          | REASON26 |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 400

  @timelineRework
  Scenario: [TIMELINE_REWORK_27] Rework notifica multidestinatario in stato EFFETIVE_DATE raggiunga correttamente lo stato di rework READY.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON27 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"

  #@timelineRework questo scenario è coperto dai test di fase 2 con esiti attesi aggiornati
  Scenario: [TIMELINE_REWORK_28] Rework notifica multidestinatario in stato VIEWED raggiunga correttamente lo stato di rework READY.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "VIEWED"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON28 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"

  @timelineRework
  Scenario: [TIMELINE_REWORK_29] Rework notifica multidestinatario in stato RETURN_TO_SENDER raggiunga correttamente lo stato di rework READY.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                 |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL                 |
      | physicalAddress_address | Via@FAIL_DECEDUTO_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON29 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON30 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"

  @timelineRework
  Scenario: [TIMELINE_REWORK_31] Rework notifica monodestinatario visualizzato.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "VIEWED"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON30 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"

  @timelineRework
  Scenario: [TIMELINE_REWORK_32] Rework notifica monodestinatario deceduto.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON32 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"

  @timelineRework
  Scenario: [TIMELINE_REWORK_33] Rework notifica monodestinatario perfezionata stato in progress.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON33 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M01                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Indagine     |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"

  @timelineRework
  Scenario: [TIMELINE_REWORK_34] Rework notifica monodestinatario furto smarrimento deterioramento.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | FAIL_CON996_PCRETRY_FURTO_AR |
      | digitalDomicile         | NULL                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON34 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"




#                                     Rework su Attempt

  @timelineRework
  Scenario: [TIMELINE_REWORK_38] Verifica che la correzione di un ATTEMPT_0 da KO in OK, quando è presente un ATTEMPT_1 in KO
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then raccolgo gli elementId della timeline contenenti "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON38 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifica che gli elementi appena raccolti siano nella lista di quelli invalidati


  @timelineRework
  Scenario: [TIMELINE_REWORK_39] Verifica che la correzione di un ATTEMPT_0 da KO in KO con diverse motivazioni, quando è presente un ATTEMPT_1 in KO, non sia possibile e porti ad un invalidazione asincrona.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M03                          | REASON39 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_40] Verifica che la correzione di un ATTEMPT_0 da KO in un identico KO, quando è presente un ATTEMPT_1 in KO, non sia possibile e porti ad un invalidazione asincrona.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON40 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_41] Verifica che la correzione di un ATTEMPT_1 da KO in OK
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON41 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"

  @timelineRework
  Scenario: [TIMELINE_REWORK_42] Verifica che la correzione di un ATTEMPT_1 da KO in KO con diverse motivazioni
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | REASON42 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M04                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Indagine     |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002E"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                      | NOT_NULL  |
      | details_deliveryDetailCode   | RECRN002F |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 1         |
      | details_deliveryFailureCause | M04       |
      | details_responseStatus       | KO        |
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

  @timelineRework
  Scenario: [TIMELINE_REWORK_43] Verifica che la correzione di un ATTEMPT_1 da KO in un KO identico produca un invalidazione asincrona
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON43 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_44] Verifica che la correzione di un ATTEMPT_0 da KO in OK, quando è presente un ATTEMPT_1 in OK
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then raccolgo gli elementId della timeline contenenti "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON44 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"

  @timelineRework
  Scenario: [TIMELINE_REWORK_45] Verifica che la correzione di un ATTEMPT_0 da KO in KO con motivazioni diverse, quando è presente un ATTEMPT_1 in OK, produca un invalidazione asincrona
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M03                          | REASON45 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_46] Verifica che la correzione di un ATTEMPT_0 da KO in un KO identico, quando è presente un ATTEMPT_1 in OK, produca un invalidazione asincrona
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON46 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  #@timelineRework questo scenario è coperto dai test di fase 2 con esiti attesi aggiornati
  Scenario: [TIMELINE_REWORK_47] Verifica che la correzione di un ATTEMPT_1 da OK, con evento di visualizzazione da parte dell’utente ed assenza dell’evento REFINEMENT, a KO, essendo in realtà il destinatario risultato irreperibile,
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And "Mario Cucumber" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M03                          | REASON47 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M03                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Indagine     |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"

  @timelineRework
  Scenario: [TIMELINE_REWORK_49] Verifica che la correzione di un ATTEMPT_1 da OK ad un OK identico produca un invalidazione asincrona.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON49 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_50] Verifica che la correzione ATTEMPT_0 da OK a KO: verifica comportamento e generazione timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON50 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M01                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Indagine     |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"

  @timelineRework
  Scenario: [TIMELINE_REWORK_51] Verifica che la correzione ATTEMPT_0 da OK a KO: verifica notifica con destinatari multipli
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And esiste l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And esiste l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 1
    And esiste l'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And esiste l'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON51 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M01                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Indagine     |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"

  @timelineRework
  Scenario: [TIMELINE_REWORK_52] Verifica che la correzione ATTEMPT_0 da OK (quindi nessun ATTEMPT_1 presente) in KO, nel caso in cui il destinatario sia irreperibile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M03                          | REASON52 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M03                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Indagine     |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "BASE"

  @timelineRework
  Scenario: [TIMELINE_REWORK_53] Verifica che la correzione di un ATTEMPT_0 da OK (quindi nessun ATTEMPT_1 presente) ad un OK identico produca un invalidazione asincrona.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON53 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 3 secondi

  @timelineRework
  Scenario: [TIMELINE_REWORK_55] Verifica che la Correzione ATTEMPT_0 multidestinatario EFFECTIVE_DATE
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And esiste l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And esiste l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 1
    And esiste l'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And esiste l'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 3 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M01                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Indagine     |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    