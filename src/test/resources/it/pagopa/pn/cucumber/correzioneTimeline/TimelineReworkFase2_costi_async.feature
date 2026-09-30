Feature: Correzione timeline fase 2 costi
  #SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2700673102/SRS+Correzione+timeline+-+Fase+3
  #https://pagopa.atlassian.net/wiki/spaces/PN/pages/2383118368/SRS+Correzione+timeline+-+FASE+2

  #FLAT_RATE ASYNC

  @timelineReworkF2_costi_async
  #OK, rework porta di nuovo a OK all'attempt0
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_OK_AR_1] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | Via@OK_AR                   |
      | physicalAddress_municipality | LAGO PATRIA                 |
      | physicalAddress_zip          | 80014                       |
      | physicalAddress_province     | NA                          |
      | payment_creditorTaxId        | 77777777777                 |
      | payment_pagoPaForm           | SI                          |
      | payment_f24                  | NULL                        |
      | title_payment                | PagoPa_mono_async_flat_rate |
      | apply_cost_pagopa            | NO                          |
      | payment_multy_number         | 1                           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 0
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #OK, rework porta a deceduto all'attempt0
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_OK_AR_2] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | Via@OK_AR                   |
      | physicalAddress_municipality | LAGO PATRIA                 |
      | physicalAddress_zip          | 80014                       |
      | physicalAddress_province     | NA                          |
      | payment_creditorTaxId        | 77777777777                 |
      | payment_pagoPaForm           | SI                          |
      | payment_f24                  | NULL                        |
      | title_payment                | PagoPa_mono_async_flat_rate |
      | apply_cost_pagopa            | NO                          |
      | payment_multy_number         | 1                           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C          | M02                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002A  | M02                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002B  |                      | Plico        |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 0
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #OK, rework porta a KO all'attempt0 e OK all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_OK_AR_3] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | Via@OK_AR                   |
      | physicalAddress_municipality | LAGO PATRIA                 |
      | physicalAddress_zip          | 80014                       |
      | physicalAddress_province     | NA                          |
      | payment_creditorTaxId        | 77777777777                 |
      | payment_pagoPaForm           | SI                          |
      | payment_f24                  | NULL                        |
      | title_payment                | PagoPa_mono_async_flat_rate |
      | apply_cost_pagopa            | NO                          |
      | payment_multy_number         | 1                           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M04                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 1
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #deceduto, rework porta di nuovo a deceduto all'attempt0
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_FAIL_DECEDUTO_AR_1] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | Via@FAIL_DECEDUTO_AR        |
      | physicalAddress_municipality | LAGO PATRIA                 |
      | physicalAddress_zip          | 80014                       |
      | physicalAddress_province     | NA                          |
      | payment_creditorTaxId        | 77777777777                 |
      | payment_pagoPaForm           | SI                          |
      | payment_f24                  | NULL                        |
      | title_payment                | PagoPa_mono_async_flat_rate |
      | apply_cost_pagopa            | NO                          |
      | payment_multy_number         | 1                           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C          | M02                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002A  | M02                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002B  |                      | Plico        |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 0
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #deceduto, rework porta a OK all'attempt0
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_FAIL_DECEDUTO_AR_2] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | Via@FAIL_DECEDUTO_AR        |
      | physicalAddress_municipality | LAGO PATRIA                 |
      | physicalAddress_zip          | 80014                       |
      | physicalAddress_province     | NA                          |
      | payment_creditorTaxId        | 77777777777                 |
      | payment_pagoPaForm           | SI                          |
      | payment_f24                  | NULL                        |
      | title_payment                | PagoPa_mono_async_flat_rate |
      | apply_cost_pagopa            | NO                          |
      | payment_multy_number         | 1                           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 0
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #deceduto, rework porta a KO all'attempt0 e OK all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_FAIL_DECEDUTO_AR_3] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | Via@FAIL_DECEDUTO_AR        |
      | physicalAddress_municipality | LAGO PATRIA                 |
      | physicalAddress_zip          | 80014                       |
      | physicalAddress_province     | NA                          |
      | payment_creditorTaxId        | 77777777777                 |
      | payment_pagoPaForm           | SI                          |
      | payment_f24                  | NULL                        |
      | title_payment                | PagoPa_mono_async_flat_rate |
      | apply_cost_pagopa            | NO                          |
      | payment_multy_number         | 1                           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M04                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 1
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-OK, rework dell'attempt1 porta di nuovo a OK all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_FAIL_DISCOVERY_AR_1] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | Via@FAIL-DISCOVERY_AR       |
      | physicalAddress_municipality | LAGO PATRIA                 |
      | physicalAddress_zip          | 80014                       |
      | physicalAddress_province     | NA                          |
      | payment_creditorTaxId        | 77777777777                 |
      | payment_pagoPaForm           | SI                          |
      | payment_f24                  | NULL                        |
      | title_payment                | PagoPa_mono_async_flat_rate |
      | apply_cost_pagopa            | NO                          |
      | payment_multy_number         | 1                           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 1
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-OK, rework dell'attempt1 porta a KO all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_FAIL_DISCOVERY_AR_2] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | Via@FAIL-DISCOVERY_AR       |
      | physicalAddress_municipality | LAGO PATRIA                 |
      | physicalAddress_zip          | 80014                       |
      | physicalAddress_province     | NA                          |
      | payment_creditorTaxId        | 77777777777                 |
      | payment_pagoPaForm           | SI                          |
      | payment_f24                  | NULL                        |
      | title_payment                | PagoPa_mono_async_flat_rate |
      | apply_cost_pagopa            | NO                          |
      | payment_multy_number         | 1                           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M04                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 1
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-OK, rework dell'attempt0 porta a OK all'attempt0
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_FAIL_DISCOVERY_AR_3] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | Via@FAIL-DISCOVERY_AR       |
      | physicalAddress_municipality | LAGO PATRIA                 |
      | physicalAddress_zip          | 80014                       |
      | physicalAddress_province     | NA                          |
      | payment_creditorTaxId        | 77777777777                 |
      | payment_pagoPaForm           | SI                          |
      | payment_f24                  | NULL                        |
      | title_payment                | PagoPa_mono_async_flat_rate |
      | apply_cost_pagopa            | NO                          |
      | payment_multy_number         | 1                           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 0
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-KO, rework dell'attempt1 porta di nuovo a KO all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_FAIL_DISCOVERY_IRREPERIBILE_AR_1] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                              |
      | physicalAddress_address      | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
      | physicalAddress_municipality | LAGO PATRIA                       |
      | physicalAddress_zip          | 80014                             |
      | physicalAddress_province     | NA                                |
      | payment_creditorTaxId        | 77777777777                       |
      | payment_pagoPaForm           | SI                                |
      | payment_f24                  | NULL                              |
      | title_payment                | PagoPa_mono_async_flat_rate       |
      | apply_cost_pagopa            | NO                                |
      | payment_multy_number         | 1                                 |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M04                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 1
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-KO, rework dell'attempt1 porta a OK all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_FAIL_DISCOVERY_IRREPERIBILE_AR_2] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                              |
      | physicalAddress_address      | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
      | physicalAddress_municipality | LAGO PATRIA                       |
      | physicalAddress_zip          | 80014                             |
      | physicalAddress_province     | NA                                |
      | payment_creditorTaxId        | 77777777777                       |
      | payment_pagoPaForm           | SI                                |
      | payment_f24                  | NULL                              |
      | title_payment                | PagoPa_mono_async_flat_rate       |
      | apply_cost_pagopa            | NO                                |
      | payment_multy_number         | 1                                 |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 1
    Then il baseCost è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-KO, rework dell'attempt0 porta a OK all'attempt0
  Scenario: [TR3_PAYMENTS_REWORK_FLATRATE_ASYNC_FAIL_DISCOVERY_IRREPERIBILE_AR_3] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                              |
      | physicalAddress_address      | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
      | physicalAddress_municipality | LAGO PATRIA                       |
      | physicalAddress_zip          | 80014                             |
      | physicalAddress_province     | NA                                |
      | payment_creditorTaxId        | 77777777777                       |
      | payment_pagoPaForm           | SI                                |
      | payment_f24                  | NULL                              |
      | title_payment                | PagoPa_mono_async_flat_rate       |
      | apply_cost_pagopa            | NO                                |
      | payment_multy_number         | 1                                 |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    When post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt 0
    Then il baseCost è uguale rispetto a prima del rework

  #DELIVERY_MODE ASYNC

  @timelineReworkF2_costi_async
  #OK, rework porta di nuovo a OK all'attempt0
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_OK_AR_1] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@OK_AR                |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_mono_async_d_mode |
      | apply_cost_pagopa            | SI                       |
      | payment_multy_number         | 1                        |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    And il record recuperato su pn-CostComponents è uguale rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #OK, rework porta a deceduto attempt0
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_OK_AR_2] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@OK_AR                |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_mono_async_d_mode |
      | apply_cost_pagopa            | SI                       |
      | payment_multy_number         | 1                        |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C          | M02                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002A  | M02                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002B  |                      | Plico        |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    And il record recuperato su pn-CostComponents è uguale rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #OK, rework porta a KO all'attempt0 e OK all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_OK_AR_3] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@OK_AR                |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_mono_async_d_mode |
      | apply_cost_pagopa            | SI                       |
      | payment_multy_number         | 1                        |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M04                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    And il record recuperato su pn-CostComponents è differente rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è differente rispetto a prima del rework

  @timelineReworkF2_costi_async
  #deceduto, rework porta di nuovo a deceduto all'attempt0
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_FAIL_DECEDUTO_AR_1] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@FAIL_DECEDUTO_AR     |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_mono_async_d_mode |
      | apply_cost_pagopa            | SI                       |
      | payment_multy_number         | 1                        |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C          | M02                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002A  | M02                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002B  |                      | Plico        |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    And il record recuperato su pn-CostComponents è uguale rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #deceduto, rework porta a OK all'attempt0
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_FAIL_DECEDUTO_AR_2] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@FAIL_DECEDUTO_AR     |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_mono_async_d_mode |
      | apply_cost_pagopa            | SI                       |
      | payment_multy_number         | 1                        |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    And il record recuperato su pn-CostComponents è uguale rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #deceduto, rework porta a KO all' attempt0 e OK all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_FAIL_DECEDUTO_AR_3] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@FAIL_DECEDUTO_AR     |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_mono_async_d_mode |
      | apply_cost_pagopa            | SI                       |
      | payment_multy_number         | 1                        |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M04                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    And il record recuperato su pn-CostComponents è differente rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è differente rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-OK, rework dell'attempt1 porta di nuovo a KO all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_FAIL_DISCOVERY_AR_1] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@FAIL-DISCOVERY_AR    |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_mono_async_d_mode |
      | apply_cost_pagopa            | SI                       |
      | payment_multy_number         | 1                        |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    And il record recuperato su pn-CostComponents è uguale rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-OK, rework dell'attempt1 porta a KO all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_FAIL_DISCOVERY_AR_2] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@FAIL-DISCOVERY_AR    |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_mono_async_d_mode |
      | apply_cost_pagopa            | SI                       |
      | payment_multy_number         | 1                        |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M04                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    And il record recuperato su pn-CostComponents è uguale rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-OK, rework dell'attempt1 porta a OK all'attempt0
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_FAIL_DISCOVERY_AR_3] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@FAIL-DISCOVERY_AR    |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_mono_async_d_mode |
      | apply_cost_pagopa            | SI                       |
      | payment_multy_number         | 1                        |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    And il record recuperato su pn-CostComponents è differente rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è differente rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-KO, rework dell'attempt1 porta di nuovo a KO all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_FAIL_DISCOVERY_IRREPERIBILE_AR_1] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                              |
      | physicalAddress_address      | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
      | physicalAddress_municipality | LAGO PATRIA                       |
      | physicalAddress_zip          | 80014                             |
      | physicalAddress_province     | NA                                |
      | payment_creditorTaxId        | 77777777777                       |
      | payment_pagoPaForm           | SI                                |
      | payment_f24                  | NULL                              |
      | title_payment                | PagoPa_mono_async_d_mode          |
      | apply_cost_pagopa            | SI                                |
      | payment_multy_number         | 1                                 |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M04                          | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M04                  |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002E  |                      | Plico        | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    And il record recuperato su pn-CostComponents è uguale rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-KO, rework dell'attempt1 porta a OK all'attempt1
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_FAIL_DISCOVERY_IRREPERIBILE_AR_2] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                              |
      | physicalAddress_address      | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
      | physicalAddress_municipality | LAGO PATRIA                       |
      | physicalAddress_zip          | 80014                             |
      | physicalAddress_province     | NA                                |
      | payment_creditorTaxId        | 77777777777                       |
      | payment_pagoPaForm           | SI                                |
      | payment_f24                  | NULL                              |
      | title_payment                | PagoPa_mono_async_d_mode          |
      | apply_cost_pagopa            | SI                                |
      | payment_multy_number         | 1                                 |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    And il record recuperato su pn-CostComponents è uguale rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è uguale rispetto a prima del rework

  @timelineReworkF2_costi_async
  #KO-KO, rework dell'attempt1 porta a OK all'attempt0 #BUG VALIDATION 18756
  Scenario: [TR3_PAYMENTS_REWORK_DELIVERY_MODE_ASYNC_FAIL_DISCOVERY_IRREPERIBILE_AR_3] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi rework    |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                              |
      | physicalAddress_address      | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
      | physicalAddress_municipality | LAGO PATRIA                       |
      | physicalAddress_zip          | 80014                             |
      | physicalAddress_province     | NA                                |
      | payment_creditorTaxId        | 77777777777                       |
      | payment_pagoPaForm           | SI                                |
      | payment_f24                  | NULL                              |
      | title_payment                | PagoPa_mono_async_d_mode          |
      | apply_cost_pagopa            | SI                                |
      | payment_multy_number         | 1                                 |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And pre rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 1
    And pre rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 1
    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason     |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C          |                              | reasonTest |
    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           | Indagine     |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post rework vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post rework vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt 0
    And post rework verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt 0
    And il record recuperato su pn-CostComponents è differente rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è differente rispetto a prima del rework