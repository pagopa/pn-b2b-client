@timelineReworkF2 @work-item:QA-123
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
