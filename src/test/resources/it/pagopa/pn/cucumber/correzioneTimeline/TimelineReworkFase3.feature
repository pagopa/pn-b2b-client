Feature: Correzione timeline fase 3
  #SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2700673102/SRS+Correzione+timeline+-+Fase+3
  #PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3002826778/PST+-+Correzione+Timeline+-+FASE+3

  @timelineReworkF3
  Scenario Outline: [TR3_RESTART_CREATION_OK] Tentativi di creazione di una restart con o senza specificare il recIndex nella request
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_0 | <recIndex> | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    Examples:
      | recIndex   |
      | RECINDEX_0 |
      |            |

    #iun invalido
    #iun inesistente
    #iun null
    #attemptId null
    #reason null (task null ininfluente)
    #recIndex superiore a numero destinatari
    #attemptIndex superiore a numero tentativi
    #restart già creata
  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_KO] Tentativi di creazione di una restart con: iun invalido/inesistente/null, attemptId null, reason null, recIndex superiore a numero destinatari, attempt superiore a numero tentativi in timeline, restart già creata
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun         | attemptId | recIndex   | reason     | task       |
      | INVALID-IUN | ATTEMPT_0 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun                       | attemptId | recIndex   | reason     | task       |
      | INEX-ISTE-NTIU-123456-N-1 | ATTEMPT_0 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 404
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun  | attemptId | recIndex   | reason     | task       |
      | null | ATTEMPT_0 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     |           | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason | task |
      |     | ATTEMPT_0 | RECINDEX_0 |        |      |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason | task       |
      |     | ATTEMPT_0 | RECINDEX_1 |        | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    Then si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason | task       |
      |     | ATTEMPT_1 | RECINDEX_0 |        | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi
    #dopo tutti i KO, ne invochiamo una che va a buon fine
    When viene invocata una richiesta di restart per la notifica appena creata
    And si verifica che la richiesta di restart effettuata sia in stato "CREATED"
    #dopo la precedente creazione andata a buon fine, ne invoco una seconda per ottenere un 409
    And viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 409

  @timelineReworkF3
  Scenario: [TR3_NOTIFICATION_REWORKED_RESTART_FAIL] Verificare l'impossibilità di effettuare un restart per una notifica avente un rework in status diverso da ERROR o DONE
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And viene invocata una richiesta di rework per la notifica appena creata
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400

  @timelineReworkF3
  Scenario: [TR3_RESTART_UPDATE_KO] Verificare l'impossibilità di poter eseguire l'update di un'operazione di restart
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And viene invocata una richiesta di restart per la notifica appena creata
    And si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    When viene aggiornata la richiesta di restart con i seguenti dati:
      | iun | reworkId | expectedStatusCode | expectedDeliveryFailureCause |
      |     |          | RECRN002F          | M123                         |
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_REFUSED_MONODEST] Tentativi di creazione di una restart per una notifica mono-destinatario andata in refused
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | payment                 | NULL                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_REFUSED_MULTIDEST] Tentativi di creazione di una restart per una notifica multi-destinatario andata in refused
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | payment                 | NULL                  |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | payment                 | NULL                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_STILL_IN_VALIDATION_MONODEST] Tentativi di creazione di una restart per una notifica mono-destinatario ancora in stato di validazione
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_STILL_IN_VALIDATION_MULTIDEST] Tentativi di creazione di una restart per una notifica multi-destinatario ancora in stato di validazione
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_AFTER_ACCEPTATION_MONODEST] Tentativi di creazione di una restart per una notifica mono-destinatario appena questa raggiunge lo stato accepted
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_AFTER_ACCEPTATION_MULTIDEST] Tentativi di creazione di una restart per una notifica multi-destinatario appena questa raggiunge lo stato accepted
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_IN_DELIVERING_MONODEST] Tentativi di creazione di una restart per una notifica mono-destinatario in stato delivering
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_IN_DELIVERING_MULTIDEST] Tentativi di creazione di una restart per una notifica multi-destinatario in stato delivering
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_DELIVERED_MONODEST] Tentativi di creazione di una restart per una notifica mono-destinatario in stato delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_DELIVERED_MULTIDEST] Tentativi di creazione di una restart per una notifica multi-destinatario in stato delivered
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_PAID_MONODEST] Tentativi di creazione di una restart per una notifica mono-destinatario con avviso di pagamento PAID
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
      | payment_pagoPaForm      | SI        |
      | apply_cost_pagopa       | SI        |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_PAID_MULTIDEST] Tentativi di creazione di una restart per una notifica multi-destinatario con avviso di pagamento PAID
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
      | payment_pagoPaForm      | SI        |
      | apply_cost_pagopa       | SI        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
      | payment_pagoPaForm      | SI        |
      | apply_cost_pagopa       | SI        |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_CANCELLED_MONODEST] Tentativi di creazione di una restart per una notifica mono-destinatario in stato cancelled
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_CANCELLED_MULTIDEST] Tentativi di creazione di una restart per una notifica multi-destinatario in stato cancelled
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3 @checkRestart #6.1
  Scenario: [TR3_RESTART_MONODEST_ATTEMPT0_OK_RESTART_ATTEMPT0_KO] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 in OK, il restart va in KO all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in OK, andrà in ok al nuovo attempt 0
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
#      | payment_pagoPaForm      | NOALLEGATO |
#      | apply_cost_pagopa       | SI         |
#      | payment_multy_number    | 1          |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" al tentativo "REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" al tentativo "REWORK_0"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"

  @timelineReworkF3 @checkRestart #6.2
  Scenario: [TR3_RESTART_MONODEST_ATTEMPT0_OK_RESTART_ATTEMPT0_OK] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 in KO, il restart va in OK all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO, andrà in ok al nuovo attempt 0
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" al tentativo ".NEW"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" al tentativo ".NEW"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"

  @timelineReworkF3 @checkRestart #6.3
  Scenario: [TR3_RESTART_MONODEST_ATTEMPT0_KO_ATTEMPT1_OK_RESTART_ATTEMPT0_OK] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 in KO e attempt 1 in OK, il restart va in OK all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO e tentativo 1 in OK, andrà in ok al nuovo attempt 0
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"

  @timelineReworkF3 @checkRestart #6.4
  Scenario: [TR3_RESTART_MONODEST_ATTEMPT0_KO_ATTEMPT1_KO_RESTART_ATTEMPT0_OK] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 e 1 in KO, il restart va in OK all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO e tentativo 1 in KO, andrà in OK al nuovo attempt 0
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"

  @timelineReworkF3 @checkRestart #6.5
  Scenario: [TR3_RESTART_MONODEST_ATTEMPT0_KO_ATTEMPT1_KO_RESTART_ATTEMPT0_OK_2] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 e 1 in KO, il restart va in OK all'attempt 1
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO e tentativo 1 in KO, andrà in OK al nuovo attempt 1
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"

  @timelineReworkF3 @checkRestart #6.6
  Scenario: [TR3_RESTART_MONODEST_ATTEMPT0_KO_ATTEMPT1_OK_RESTART_ATTEMPT1_KO] Viene effettuata un'operazione di restart dell'attempt 1 per una notifica mono-destinatario con attempt 0 in KO e 1 in OK, il restart va in KO all'attempt 1
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO e tentativo 1 in OK, andrà in KO al nuovo attempt 1
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | digitalDomicile         | NULL                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"

  @timelineReworkF3 @checkRestart #6.7
  Scenario: [TR3_RESTART_MONODEST_ATTEMPT0_KO_ATTEMPT1_OK_RESTART_ATTEMPT1_DECEASED] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 in KO e 1 in OK, il restart va in returned to sender all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in KO e tentativo 1 in OK, andrà in returned to sender al nuovo attempt 0
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | digitalDomicile         | NULL                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"

  @timelineReworkF3 @checkRestart #6.8
  Scenario: [TR3_RESTART_MONODEST_ATTEMPT0_DECESED_RESTART_ATTEMPT0_OK] Viene effettuata un'operazione di restart dell'attempt 0 per una notifica mono-destinatario con attempt 0 in KO e 1 in OK, il restart va in returned to sender all'attempt 0
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
    #TODO: change, ci sarà una sequence che a fronte di un tentativo 0 in DECEDUTO, andrà in OK al nuovo attempt 0
      | physicalAddress_address | Via@FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #TODO: bisogna mettere il thread in pausa per aspettare i nuovi eventi del rework ???
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"