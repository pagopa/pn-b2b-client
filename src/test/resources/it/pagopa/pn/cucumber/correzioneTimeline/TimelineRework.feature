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
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
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
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
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
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun   | attemptId   | pcRetry   | recIndex   | expectedStatusCode   | expectedDeliveryFailureCause   | reason   |
      | <iun> | <attemptId> | <pcRetry> | <recIndex> | <expectedStatusCode> | <expectedDeliveryFailureCause> | <reason> |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 404
    Examples: :
  | iun                                | attemptId             | pcRetry         | recIndex         | expectedStatusCode | expectedDeliveryFailureCause  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | NOT_VALID          | EMPTY_STRING                  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | RECRI003C          | NOT_VALID                     | reason         |


    #-------------------------------------------------------------------

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.

  @timelineRework
  Scenario: [TIMELINE_REWORK_30] Rework notifica monodestinatario perfezionata.



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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRI003C          |                              | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #And si verifica che la richiesta di rework effettuata sia in stato "READY"
    #Then vengono effettuati i controlli sugli elementi invalidati

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
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "VIEWED"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN001C          |                              | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "READY"
    #Then vengono effettuati i controlli sugli elementi invalidati

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
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> |                    |                              | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #And si verifica che la richiesta di rework effettuata sia in stato "READY"
    #**Then vengono effettuati i controlli sugli elementi invalidati

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
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN002F          | M01                          | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "READY"
    #*** wait for?
    And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS"
    #***Then vengono effettuati i controlli sugli elementi invalidati
    #***Then vengono effettuati i controlli sugli eventi scritti con suffisso "_REWORK_{n}"

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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN001C          |                              | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "READY"
        #*** wait for?
    And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS"
        #***Then vengono effettuati i controlli sugli elementi invalidati
   #***Then vengono effettuati i controlli sugli eventi scritti con suffisso "_REWORK_{n}" negativo

  @timelineRework #invio correttivo non atteso
  Scenario: [TIMELINE_REWORK_35] Rework notifica monodestinatario evento correttivo non atteso.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN001C          |                              | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "READY"
        #*** wait for?
    And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS"
        #***Then vengono effettuati i controlli sugli elementi invalidati
  #Then vengono effettuati i controlli sugli eventi scritti con suffisso "_REWORK_{n}" //negativo


  @timelineRework #invio duplicato approf
  Scenario: [TIMELINE_REWORK_36] Rework notifica monodestinatario evento correttivo duplicato.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN002F          | M01                          | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "READY"
        #*** wait for?
    And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS"
        #Then vengono effettuati i controlli sugli elementi invalidati
        #Then vengono effettuati i controlli sugli eventi scritti con suffisso "_REWORK_{n}" //negativo


  Scenario: [TIMELINE_REWORK_37] Rework notifica monodestinatario con errore di dichiarazione.





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
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN001C          |                              | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"

    #***Then vengono effettuati i controlli sugli elementi invalidati

    #Sono invalidati tutti gli eventi finali e tutti gli eventi relativi all’ATTEMPT_1
      # e che non siano invalidati eventuali eventi di timeline scaturiti da un’azione esplicita dell’utente, ovvero quelli dovuti a visualizzazioni o pagamenti

    #And si verifica che la richiesta di rework effettuata sia in stato "READY"

    #Then vengono effettuati i controlli sugli eventi scritti con suffisso "_REWORK_{n}" //positivo

    #And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS"

    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "ATTEMPT_1"

    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

    #And assenza elementi attemp_1




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
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN002F          | M03                          | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR"
    #nessuna altro elemento creato a fronte del refinement







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
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN002F          | M01                          | REASON |
        And si verifica che la richiesta di rework effettuata sia in stato "ERROR"
    #nessuna altro elemento creato a fronte del refinement


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
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0> | RECRN001C          |                              | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"

    #***Then vengono effettuati i controlli sugli elementi invalidati

    #Sono invalidati tutti gli eventi finali e tutti gli eventi relativi all’ATTEMPT_1
      # e che non siano invalidati eventuali eventi di timeline scaturiti da un’azione esplicita dell’utente, ovvero quelli dovuti a visualizzazioni o pagamenti

    #And si verifica che la richiesta di rework effettuata sia in stato "READY"

    #Then vengono effettuati i controlli sugli eventi scritti con suffisso "_REWORK_{n}" //positivo

    #And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS"

    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "ATTEMPT_1"

    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

    #And assenza elementi attemp_1



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
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0> | RECRN002F          |         M01                     | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"

    #***Then vengono effettuati i controlli sugli elementi invalidati

    #Sono invalidati tutti gli eventi finali e tutti gli eventi relativi all’ATTEMPT_1
      # e che non siano invalidati eventuali eventi di timeline scaturiti da un’azione esplicita dell’utente, ovvero quelli dovuti a visualizzazioni o pagamenti

    #And si verifica che la richiesta di rework effettuata sia in stato "READY"

    #Then vengono effettuati i controlli sugli eventi scritti con suffisso "_REWORK_{n}" //positivo

    #And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS"

    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_1"

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
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0> | RECRN002F          | M03                          | REASON |
        And si verifica che la richiesta di rework effettuata sia in stato "ERROR"
    #nessuna altro elemento creato a fronte del refinement


  @timelineRework
  Scenario: [TIMELINE_REWORK_44] Verifica che la correzione di un ATTEMPT_0 da KO in OK, quando è presente un ATTEMPT_1 in OK
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_0"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN001C          |                             | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"

    #***Then vengono effettuati i controlli sugli elementi invalidati

    #Sono invalidati tutti gli eventi finali e tutti gli eventi relativi all’ATTEMPT_1
      # e che non siano invalidati eventuali eventi di timeline scaturiti da un’azione esplicita dell’utente, ovvero quelli dovuti a visualizzazioni o pagamenti

    #And si verifica che la richiesta di rework effettuata sia in stato "READY"

    #Then vengono effettuati i controlli sugli eventi scritti con suffisso "_REWORK_{n}" //positivo

    #And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS"

    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_0"

    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_1" negativo



  @timelineRework
  Scenario: [TIMELINE_REWORK_45] Verifica che la correzione di un ATTEMPT_0 da KO in KO con motivazioni diverse, quando è presente un ATTEMPT_1 in OK, produca un invalidazione asincrona
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN002F          | M03                          | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR"
    #nessuna altro elemento creato a fronte del refinement





  @timelineRework
  Scenario: [TIMELINE_REWORK_46] Verifica che la correzione di un ATTEMPT_0 da KO in un KO identico, quando è presente un ATTEMPT_1 in OK, produca un invalidazione asincrona
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN002F          | M01                          | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR"
    #nessuna altro elemento creato a fronte del refinement





  @timelineRework
  Scenario: [TIMELINE_REWORK_47] Verifica che la correzione di un ATTEMPT_1 da OK, con evento di visualizzazione da parte dell’utente ed assenza dell’evento REFINEMENT, a KO, essendo in realtà il destinatario risultato irreperibile,
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@@FAIL-DISCOVERY_A |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    And "Mario Cucumber" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"

    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0> | RECRN002F          |      M03                       | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"

    #***Then vengono effettuati i controlli sugli elementi invalidati

    #Sono invalidati tutti gli eventi finali e tutti gli eventi relativi all’ATTEMPT_1
      # e che non siano invalidati eventuali eventi di timeline scaturiti da un’azione esplicita dell’utente, ovvero quelli dovuti a visualizzazioni o pagamenti

    #And si verifica che la richiesta di rework effettuata sia in stato "READY"

    #Then vengono effettuati i controlli sugli eventi scritti con suffisso "_REWORK_{n}" //positivo

    #And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS"

    #In timeline è presente l’elemento SEND_ANALOG_FEEDBACK con deliveryFailureCause: M01 relativo all’ATTEMPT_1

    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_0"

    #stato unreachable

    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"








  @timelineRework
  Scenario: [TIMELINE_REWORK_48] Verifica che la correzione di un ATTEMPT_1 da OK a OK con diverse motivazioni
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "ATTEMPT_0"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0> | RECRN001C          |                             | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"

    #***Then vengono effettuati i controlli sugli elementi invalidati

    #Sono invalidati tutti gli eventi finali e tutti gli eventi relativi all’ATTEMPT_1
      # e che non siano invalidati eventuali eventi di timeline scaturiti da un’azione esplicita dell’utente, ovvero quelli dovuti a visualizzazioni o pagamenti

    #And si verifica che la richiesta di rework effettuata sia in stato "READY"

    #Then vengono effettuati i controlli sugli eventi scritti con suffisso "_REWORK_{n}" //positivo

    #And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS"

    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_0"

    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "ATTEMPT_1" negativo





  @timelineRework
  Scenario: [TIMELINE_REWORK_49] Verifica che la correzione di un ATTEMPT_1 da OK ad un OK identico produca un invalidazione asincrona.
  | subject               | invio notifica con cucumber |
  | senderDenomination    | Comune di Palermo           |
  | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "ATTEMPT_1"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN001C          | M01                          | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR"
    #nessuna altro elemento creato a fronte del refinement


  #------------

  @timelineRework
  Scenario: [TIMELINE_REWORK_x] Rework notifica monodestinatario deceduto.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN002F          | M01                          | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #And si verifica che la richiesta di rework effettuata sia in stato "READY"
    #**Then vengono effettuati i controlli sugli elementi invalidati


  @timelineRework
  Scenario: [TIMELINE_K_x] Rework notifica monodestinatario deceduto.
    Given imposto lo iun di SharedSteps a "LWZT-NTWX-ZEJN-202601-M-1" e la pa a "Comune_Multi"

    Then verifico la presenza di elementi di timeline con stringa "REWORK_"
    Then verifico la non presenza di elementi di timeline con stringa "REWORK_"






    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #And si verifica che la richiesta di rework effettuata sia in stato "READY"
    #**Then vengono effettuati i controlli sugli elementi invalidati