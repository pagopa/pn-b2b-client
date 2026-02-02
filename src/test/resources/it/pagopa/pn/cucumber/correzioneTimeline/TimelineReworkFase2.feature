Feature: Test relativi al SRS di correzione timeline Fase 2


  #@timelineReworkF2
  Scenario: [TIMELINE_REWORK_1] Verificare che, per una notifica inviata da oltre 120 giorni, l’operazione di invalidazione degli elementi di timeline vada a buon fine, a fronte del recupero dei documenti
    Given imposto lo iun di SharedSteps a "***" e la pa a "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON111 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi


  @timelineReworkF2
  Scenario: [TIMELINE_REWORK_2] Verificare che, per una notifica mono destinatario con ATTEMPT_0 in OK, la richiesta di rework vada in ERROR se viene passato un pcRetry = PCRETRY_1
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_1 | RECINDEX_0 | RECRN002F          | M01                          | REASON221 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi


  @timelineReworkF2 #rif 3.1
  Scenario: [TIMELINE_REWORK_3] ATTEMPT_0 in KO e ATTEMPT_1 in OK, la prima richiesta di rework vada in DONE passando ATTEMPT_1 da OK in KO, mentre la seconda richiesta di rework modifichi l’ATTEMPT_1 in KO con diversa motivazione, senza che parta un ulteriore tentativo di invio
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON331 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M01                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Indagine     |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi

#    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
#      | details                 | NOT_NULL |
#      | details_recIndex        | 0        |
#      | details_sentAttemptMade | 1        |
#      | details_responseStatus  | KO       |
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | REASON331 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    Then viene resettato il timestamp
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
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    Then verifico la non presenza di elementi di timeline con stringa "ATTEMPT_1"


  @timelineReworkF2 # rif 3.2
  Scenario: [TIMELINE_REWORK_4] ATTEMPT_0 in KO e ATTEMPT_1 in OK, la prima richiesta di rework vada in DONE passando ATTEMPT_0 da KO in OK, mentre la seconda richiesta di rework modifichi l’ATTEMPT_0 in KO
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON332 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON331 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    Then viene resettato il timestamp
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
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |
      | details_responseStatus  | KO       |


  @timelineReworkF2 #rif 3.3
  Scenario: [TIMELINE_REWORK_5] ATTEMPT_0 in KO e ATTEMPT_1 in OK, la prima richiesta di rework vada in DONE passando ATTEMPT_1 da OK in KO, mentre la seconda richiesta di rework modifichi l’ATTEMPT_1 in KO con diversa motivazione, senza che parta un ulteriore tentativo di invio
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON333 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M01                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Indagine     |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | REASON333 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    Then viene resettato il timestamp
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
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    Then verifico la non presenza di elementi di timeline con stringa "ATTEMPT_1"


  @timelineReworkF2 #rif 3.4
  Scenario: [TIMELINE_REWORK_6] ATTEMPT_0 in KO e ATTEMPT_1 in OK, la prima richiesta di rework vada in DONE passando ATTEMPT_0 da KO in OK e la seconda richiesta di rework modifichi l’ATTEMPT_0 in OK con destinatario deceduto
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON332 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI002           | M02                          | REASON332 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    Then viene resettato il timestamp
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002   | M02                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002B  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C  |                      |              |              |
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi


  @timelineReworkF2 #TODO solo Manuale
  Scenario: [TIMELINE_REWORK_4_1] in caso di invalidazione della timeline su notifica multi destinatario, il reworkId sia valorizzato correttamente

  @timelineReworkF2 #TODO solo Manuale
  Scenario: [TIMELINE_REWORK_4_2] in caso di invalidazione della timeline su notifica multi destinatario, per ogni RECINDEX, il valore TRY e REWORK siano correttamente incrementati


  @timelineReworkF2 #rif 5.1
  Scenario: [TIMELINE_REWORK_7] verificare che non sia possibile effettuare la correzione di una timeline su una notifica visualizzata dal destinatario, in cui è presente un ATTEMPT_1 in OK da invalidare completamente
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
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"

    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C          |                              | REASON551 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi


  @timelineReworkF2 #rif 5.2
  Scenario: [TIMELINE_REWORK_8] verificare che non sia possibile effettuare la correzione di una timeline su una notifica visualizzata dal destinatario, in cui è presente un ATTEMPT_1 in KO da invalidare completamente
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And "Mario Cucumber" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON552 |
    And si verifica che la richiesta di rework effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi


  @timelineReworkF2 #rif 5.3
  Scenario: [TIMELINE_REWORK_9]   effettuare la correzione di una timeline su una notifica in cui è presente un ATTEMPT_1 in OK da invalidare completamente, nel caso in cui il destinatario non abbia ancora effettuato la visualizzazione
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON553 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    #***lista elementi invalidati


  @timelineReworkF2 #rif 5.4
  Scenario: [TIMELINE_REWORK_10] ATTEMPT_1 in KO da invalidare completamente, nel caso in cui il destinatario non abbia ancora effettuato la visualizzazione
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON554 |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
       #***lista elementi invalidati





  @timelineReworkF2 #rif 6.2
  Scenario: [TIMELINE_REWORK_11] verificare che il nuovo elemento di timeline generato in seguito ad una richiesta di correzione (con suffisso _reworked) non sia visibile, per la lettura notifiche lato mittente, utilizzando una versione delle api precedente alla 2.8
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON553 |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M01                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Indagine     |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi

    Then recuperando la fullSentNotification con la versione b2b "V24" non è presente l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED"
    Then recuperando la fullSentNotification con la versione b2b "V23" non è presente l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED"
    Then recuperando la fullSentNotification con la versione b2b "V2" non è presente l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED"

    Then controllo la correttezza dei timelineElementId degli elementi di timeline della fullSentNotification con versione b2b "V24"
    Then controllo la correttezza dei timelineElementId degli elementi di timeline della fullSentNotification con versione b2b "V23"
    Then controllo la correttezza dei timelineElementId degli elementi di timeline della fullSentNotification con versione b2b "V2"


  @timelineReworkF2 #rif 6.3
  Scenario: [TIMELINE_REWORK_12] Lettura nuovo evento di timeline dallo stream
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "più recente"
    And si crea il nuovo stream per il "Comune_Multi" con versione "più recente"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON771 |

    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"

    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED" con la versione "più recente"


  @timelineReworkF2 #todo altre versioni
  Scenario: [TIMELINE_REWORK_13] Lettura nuovo evento di timeline dallo stream
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And si crea il nuovo stream per il "Comune_2" con versione "V23" e filtro status "DEFAULT"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON772 |

    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si invoca l'api Webhook versione "V23" per ottenere gli elementi di timeline di tale notifica
    Then la category "NOTIFICATION_TIMELINE_REWORKED" non è presente in nessun elemento di timeline restituito dalla consumeStream con versione "V23"
    #***Then verifico la presenza di elementi di timeline con stringa "REWORK_"   ****versioni precedenti?


  @timelineReworkF2 #rif 5,6,7,8 Errore
  Scenario Outline: [TIMELINE_REWORK_9_1] Verificare che l’API di aggiornamento richiesta di correzione timeline risponda secondo l’esito atteso Errore
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON221 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi

    And viene aggiornata la richiesta di rework con i seguenti dati:
      | iun   | reworkId   | expectedStatusCode   | expectedDeliveryFailureCause   |
      | <iun> | <reworkId> | <expectedStatusCode> | <expectedDeliveryFailureCause> |
    And si verifica che la chiamata sia andata in errore con il seguente status code: <statusCode>
    Examples:
      | iun         | reworkId    | expectedStatusCode | expectedDeliveryFailureCause | statusCode |
      |             |             |                    |                              | 400        |
      |             |             | RECAG001B          | M123                         | 400        |
      |             |             | RECAG003C          |                              |            |
      | inesistente |             | RECRN002F          | M02                          | 404        |
      |             | inesistente | RECRN002F          | M02                          | 404        |

  @timelineReworkF2 #rif 1 204
  Scenario: [TIMELINE_REWORK_9_2] Verificare che l’API di aggiornamento richiesta di correzione timeline risponda secondo l’esito atteso con successo
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON221 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And viene correttamente aggiornata la richiesta di rework con i seguenti dati:
      | iun | reworkId | expectedStatusCode | expectedDeliveryFailureCause |
      |     |          | RECRN001C          |                              |


  @timelineReworkF2 #rif 2 204
  Scenario: [TIMELINE_REWORK_9_3] Verificare che l’API di aggiornamento richiesta di correzione timeline risponda secondo l’esito atteso con successo
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON221 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And viene correttamente aggiornata la richiesta di rework con i seguenti dati:
      | iun | reworkId | expectedStatusCode | expectedDeliveryFailureCause |
      |     |          | RECRN002F          | M03                          |


  @timelineReworkF2 #rif 3
  Scenario: [TIMELINE_REWORK_9_3] ATTEMPT_1 in KO da invalidare completamente, nel caso in cui il destinatario non abbia ancora effettuato la visualizzazione
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON554 |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And si verifica che la richiesta di rework effettuata sia in stato "IN_PROGRESS" entro 180 secondi controllando ogni 5 secondi
    And viene aggiornata la richiesta di rework con i seguenti dati:
      | iun | reworkId | expectedStatusCode | expectedDeliveryFailureCause |
      |     |          | RECRN002F          | M01                          |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 400


  @timelineReworkF2 #rif done
  Scenario: [TIMELINE_REWORK_9_] ATTEMPT_1 in KO da invalidare completamente, nel caso in cui il destinatario non abbia ancora effettuato la visualizzazione
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | REASON554 |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And viene aggiornata la richiesta di rework con i seguenti dati:
      | iun | reworkId | expectedStatusCode | expectedDeliveryFailureCause |
      |     |          | RECRN002F          | M01                          |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 400














  #------------ TEST
  Scenario Outline: [TIMELINE_REWORK_x0] Rework notifica monodestinatario.
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>            |
      | senderDenomination    | Comune di Palermo    |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Examples: | SUBJECT                     |
    | invio notifica con cucumber |
    | invio notifica con cucumber |
    | invio notifica con cucumber |
    | invio notifica con cucumber |
    | invio notifica con cucumber |

  Scenario Outline: [TIMELINE_REWORK_x1] Rework notifica monodestinatario.
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>            |
      | senderDenomination    | Comune di Palermo    |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Examples: | SUBJECT                     |
    | invio notifica con cucumber |
    | invio notifica con cucumber |
    | invio notifica con cucumber |
    | invio notifica con cucumber |
    | invio notifica con cucumber |

  Scenario Outline: [TIMELINE_REWORK_x2] Rework notifica monodestinatario.
    Given viene generata una nuova notifica
      | subject               | <SUBJECT>            |
      | senderDenomination    | Comune di Palermo    |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Examples: | SUBJECT                     |
    | invio notifica con cucumber |
    | invio notifica con cucumber |
    | invio notifica con cucumber |
    | invio notifica con cucumber |
    | invio notifica con cucumber |


  Scenario: [TIMELINE_REWORK_x0] Rework notifica monodestinatario deceduto.
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex    | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0> | RECRN002F          | M01                          | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    #Then recuperando la fullSentNotification con la versione b2b {string} {is} presente l'elemento di timeline {string}
      #Then fullsentnotification
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"


  #@timelineRework
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
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #And si verifica che la richiesta di rework effettuata sia in stato "READY"
    #**Then vengono effettuati i controlli sugli elementi invalidati


  #@timelineRework
  Scenario Outline: [TIMELINE_x] Rework notifica monodestinatario deceduto.
    Given imposto lo iun di SharedSteps a "LYQA-QZQZ-AQNR-202601-Q-1" e la pa a "Comune_Multi"
#    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
#      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason    |
#      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON221 |
#    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#
    And viene aggiornata la richiesta di rework con i seguenti dati:
      | iun   | reworkId   | expectedStatusCode   | expectedDeliveryFailureCause   |
      | <iun> | <reworkId> | <expectedStatusCode> | <expectedDeliveryFailureCause> |
    And si verifica che la chiamata sia andata in errore con il seguente status code: <statusCode>
    Examples:
      | iun         | reworkId    | expectedStatusCode | expectedDeliveryFailureCause | statusCode |
      |             |             |                    |                              | 400        |
      |             |             | RECRN002F          | M123                         | 400        |
      |             |             | RECAG003C          |                              |            |
      | inesistente |             | RECRN002F          | M01                          | 404        |
      |             | inesistente | RECRN002F          | M01                          | 404        |

