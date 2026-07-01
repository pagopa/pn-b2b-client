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
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_0 | RECINDEX_1 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And verifico la che il reworkId del restart generato sia corretto, con rework 0 try 0 e recIndex 1
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And verifico la che il reworkId del restart generato sia corretto, con rework 0 try 0 e recIndex 0
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi
    #dopo tutti i KO, ne invochiamo una che va a buon fine
    When viene invocata una richiesta di restart per la notifica appena creata
    And si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And verifico la che il reworkId del restart generato sia corretto, con rework 0 try 1 e recIndex 0
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
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
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 409

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
      | payment | NULL |
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
      | payment | NULL |
    And destinatario Mario Cucumber e:
      | payment | NULL |
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
    And destinatario Gherkin Irreperibile e:
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
      | digitalDomicile         | NULL                                         |
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
    And destinatario Gherkin Irreperibile e:
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
      | digitalDomicile         | NULL                                         |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_UNREACHABLE_MONODEST] Tentativi di creazione di una restart per una notifica mono-destinatario andata in completely unreachable
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | Test AR Fail             |
      | taxId                   | MNTMRA03M71C615V         |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
      | digitalDomicile         | NULL                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 0
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_UNREACHABLE_MULTIDEST] Tentativi di creazione di una restart per una notifica multi-destinatario andata in completely unreachable
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denominatation          | Test Recipient One       |
      | taxId                   | MNTMRA03M71C615V         |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
      | digitalDomicile         | NULL                     |
    And destinatario Gherkin Irreperibile e:
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
      | digitalDomicile         | NULL                                         |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 1
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi

  @timelineReworkF3
  Scenario: [TR3_RESTART_CREATION_PAID_MONODEST] Tentativi di creazione di una restart per una notifica mono-destinatario con avviso di pagamento PAID
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR          |
      | digitalDomicile         | NULL               |
      | apply_cost_pagopa       | SI                 |
      | payment_creditorTaxId   | 77777777777        |
      | payment_pagoPaForm      | SI                 |
      | payment_f24             | NULL               |
      | title_payment           | PagoPa_testRestart |
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
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR          |
      | digitalDomicile         | NULL               |
      | apply_cost_pagopa       | SI                 |
      | payment_creditorTaxId   | 77777777777        |
      | payment_pagoPaForm      | SI                 |
      | payment_f24             | NULL               |
      | title_payment           | PagoPa_testRestart |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR          |
      | digitalDomicile         | NULL               |
      | apply_cost_pagopa       | SI                 |
      | payment_creditorTaxId   | 77777777777        |
      | payment_pagoPaForm      | SI                 |
      | payment_f24             | NULL               |
      | title_payment           | PagoPa_testRestart |
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

  @timelineReworkF3
  Scenario: [TR3_RESTART_CHECK_TIMELINE_WITH_VERSION] A fronte di un restart, verificare che NOTIFICATION_TIMELINE_REWORKED sia visibile solo dalla v2.8 in poi
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
      | digitalDomicile         | NULL                  |
      | payment_creditorTaxId   | 77777777777           |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | title_payment           | PagoPa_testRestart    |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
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
    And recuperando la fullSentNotification con la versione b2b "V24" non è presente l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED"
    And recuperando la fullSentNotification con la versione b2b "V23" non è presente l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED"
    And recuperando la fullSentNotification con la versione b2b "V2" non è presente l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED"
    And controllo la correttezza dei timelineElementId degli elementi di timeline della fullSentNotification con versione b2b "V24"
    And controllo la correttezza dei timelineElementId degli elementi di timeline della fullSentNotification con versione b2b "V23"
    And controllo la correttezza dei timelineElementId degli elementi di timeline della fullSentNotification con versione b2b "V2"

  @timelineReworkF3 @cleanWebhook @precondition @webhookV29
  Scenario: [TR3_RESTART_CHECK_TIMELINE_STREAM_WITH_VERSION] Lettura nuovo evento di timeline dallo stream
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
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si predispone 1 nuovo stream denominato "stream-testLast" con eventType "TIMELINE" con versione "più recente"
    And si predispone 1 nuovo stream denominato "stream-testV28" con eventType "TIMELINE" con versione "V28"
    And si predispone 1 nuovo stream denominato "stream-testV25" con eventType "TIMELINE" con versione "V25"
    And si predispone 1 nuovo stream denominato "stream-testV23" con eventType "TIMELINE" con versione "V23"
    And si predispone 1 nuovo stream denominato "stream-testV10" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "più recente"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V28"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V25"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "più recente"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V28"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V25"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
    When viene invocata una richiesta di restart per la notifica appena creata
    And si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si invoca l'api Webhook versione "più recente" per ottenere gli elementi di timeline di tale notifica
    And si invoca l'api Webhook versione "V28" per ottenere gli elementi di timeline di tale notifica
    And si invoca l'api Webhook versione "V25" per ottenere gli elementi di timeline di tale notifica
    And si invoca l'api Webhook versione "V23" per ottenere gli elementi di timeline di tale notifica
    And si invoca l'api Webhook versione "V10" per ottenere gli elementi di timeline di tale notifica
    Then la category "NOTIFICATION_TIMELINE_REWORKED" è presente in almeno un elemento di timeline restituito dalla consumeStream con versione "più recente"
    And la category "NOTIFICATION_TIMELINE_REWORKED" non è presente in nessun elemento di timeline restituito dalla consumeStream con versione "V28"
    And la category "NOTIFICATION_TIMELINE_REWORKED" non è presente in nessun elemento di timeline restituito dalla consumeStream con versione "V25"
    And la category "NOTIFICATION_TIMELINE_REWORKED" non è presente in nessun elemento di timeline restituito dalla consumeStream con versione "V23"
    And la category "NOTIFICATION_TIMELINE_REWORKED" non è presente in nessun elemento di timeline restituito dalla consumeStream con versione "V10"

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_EXTERNAL_REGISTRY_API_VALIDATION] Restart di notifica che va in OK all'attempt 0 (anche al restart va in OK all'attempt 0)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR          |
      | digitalDomicile         | NULL               |
      | payment_creditorTaxId   | 77777777777        |
      | payment_pagoPaForm      | SI                 |
      | payment_f24             | NULL               |
      | title_payment           | PagoPa_testRestart |
      | apply_cost_pagopa       | SI                 |
      | payment_multy_number    | 1                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When invoco l'api di external-registry per l'invalidazione dei costi con "iun null"
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
#    When invoco l'api di external-registry per l'invalidazione dei costi con "iun non valido"
#    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
#    When invoco l'api di external-registry per l'invalidazione dei costi con "iun inesistente"
#    Then si verifica che la chiamata sia andata in errore con il seguente status code: 404
    When invoco l'api di external-registry per l'invalidazione dei costi con "vat null"
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
#    When invoco l'api di external-registry per l'invalidazione dei costi con "vat non valido"
#    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
    When invoco l'api di external-registry per l'invalidazione dei costi con "costPhases null"
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
#    When invoco l'api di external-registry per l'invalidazione dei costi con "paymentsInfo null"
#    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_NOTIFICATION_COST_API_VALIDATION] Restart di notifica che va in OK all'attempt 0 (anche al restart va in OK all'attempt 0)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_AR          |
      | digitalDomicile         | NULL               |
      | payment_creditorTaxId   | 77777777777        |
      | payment_pagoPaForm      | SI                 |
      | payment_f24             | NULL               |
      | title_payment           | PagoPa_testRestart |
      | apply_cost_pagopa       | SI                 |
      | payment_multy_number    | 1                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When invoco l'api di notification-cost per l'invalidazione dei costi con "iun non valido"
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
#    When invoco l'api di notification-cost per l'invalidazione dei costi con "iun inesistente"
#    Then si verifica che la chiamata sia andata in errore con il seguente status code: 404
    When invoco l'api di notification-cost per l'invalidazione dei costi con "recIndex null"
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
    When invoco l'api di notification-cost per l'invalidazione dei costi con "recIndex non presente"
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 404
    When invoco l'api di notification-cost per l'invalidazione dei costi con "recIndex non valido"
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400
    When invoco l'api di notification-cost per l'invalidazione dei costi con "costPhases null"
    Then si verifica che la chiamata sia andata in errore con il seguente status code: 400

  @timelineReworkF3 @checkRestart
  Scenario Outline: [TR3_CHECK_REWORK_TIMESTAMP_BUG_20292_RESTART]
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | <sequence>         |
      | digitalDomicile         | NULL               |
      | payment_creditorTaxId   | 77777777777        |
      | payment_pagoPaForm      | SI                 |
      | payment_f24             | NULL               |
      | title_payment           | PagoPa_testRestart |
      | apply_cost_pagopa       | SI                 |
      | payment_multy_number    | 1                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    Then si controlla che il timestamp dell'elemento NOTIFICATION_TIMELINE_REWORKED coincida con quello presente su DynamoDb, basato sulla SEND_ANALOG_DOMICILE all'attempt <attempt>
    Examples:
      | sequence                         | attempt |
      | Via@FAIL_DISC_RESTART_CONS_AR    | 0       |
      | Via@FAIL_DISC_RESTART_1_IRREP_AR | 1       |