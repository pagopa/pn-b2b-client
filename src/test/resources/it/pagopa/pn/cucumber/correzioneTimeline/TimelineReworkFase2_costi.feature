#Feature: Correzione timeline fase 2 costi
#  #SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2700673102/SRS+Correzione+timeline+-+Fase+3
#  #https://pagopa.atlassian.net/wiki/spaces/PN/pages/2383118368/SRS+Correzione+timeline+-+FASE+2
#
#  @timelineReworkF3_costi #11.9
#  Scenario Outline: [TR3_PAYMENTS_REWORK_9_FLATRATE_SYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
#    Given viene generata una nuova notifica
#      | subject               | test costi rework    |
#      | physicalCommunication | AR_REGISTERED_LETTER |
#      | senderDenomination    | Comune di palermo    |
#      | pagoPaIntMode         | SYNC                 |
#      | feePolicy             | FLAT_RATE            |
#      | paFee                 | 17                   |
#      | vat                   | 10                   |
#    And destinatario Mario Gherkin e:
#      | digitalDomicile              | NULL                       |
#      | physicalAddress_address      | <sequence>                 |
#      | physicalAddress_municipality | LAGO PATRIA                |
#      | physicalAddress_zip          | 80014                      |
#      | physicalAddress_province     | NA                         |
#      | payment_creditorTaxId        | 77777777777                |
#      | payment_pagoPaForm           | SI                         |
#      | payment_f24                  | NULL                       |
#      | title_payment                | PagoPa_mono_sync_flat_rate |
#      | apply_cost_pagopa            | NO                         |
#      | payment_multy_number         | 1                          |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
#    And verifico che pre rework per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attempt>
#    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
#      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode   | expectedDeliveryFailureCause | reason     |
#      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | <expectedStatusCode> | <failCode>                   | reasonTest |
#    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    When viene invocato il consolidatore per inserire tutti gli eventi previsti per il destinatario 0 che portano allo status code "<expectedStatusCode>" al tentativo <attemptRestart>
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And verifico che post rework per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt <attemptRestart>
#    And il baseCost è uguale rispetto a prima del rework
#    Examples:
#      | sequence             | finalEvent                         | finalStatus        | attempt | expectedStatusCode | failCode | finalEventRestart                  | finalStatusRestart | attemptRestart |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      #TODO ADD ALL SEQUENCES ONCE THEY ARE CREATED
#
#  @timelineReworkF3_costi #11.10
#  Scenario Outline: [TR3_PAYMENTS_REWORK_10_FLATRATE_ASYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate async) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
#    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
#    And viene generata una nuova notifica
#      | subject               | test costi rework    |
#      | physicalCommunication | AR_REGISTERED_LETTER |
#      | senderDenomination    | Comune di palermo    |
#      | pagoPaIntMode         | ASYNC                |
#      | feePolicy             | FLAT_RATE            |
#      | paFee                 | 17                   |
#      | vat                   | 10                   |
#    And destinatario Mario Gherkin e:
#      | digitalDomicile              | NULL                        |
#      | physicalAddress_address      | <sequence>                  |
#      | physicalAddress_municipality | LAGO PATRIA                 |
#      | physicalAddress_zip          | 80014                       |
#      | physicalAddress_province     | NA                          |
#      | payment_creditorTaxId        | 77777777777                 |
#      | payment_pagoPaForm           | SI                          |
#      | payment_f24                  | NULL                        |
#      | title_payment                | PagoPa_mono_async_flat_rate |
#      | apply_cost_pagopa            | NO                          |
#      | payment_multy_number         | 1                           |
#    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
#    And verifico che pre rework per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attempt>
#    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
#      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode   | expectedDeliveryFailureCause | reason     |
#      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | <expectedStatusCode> | <failCode>                   | reasonTest |
#    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    When viene invocato il consolidatore per inserire tutti gli eventi previsti per il destinatario 0 che portano allo status code "<expectedStatusCode>" al tentativo <attemptRestart>
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And verifico che post rework per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt <attemptRestart>
#    And il baseCost è uguale rispetto a prima del rework
#    Examples:
#      | sequence             | finalEvent                         | finalStatus        | attempt | expectedStatusCode | failCode | finalEventRestart                  | finalStatusRestart | attemptRestart |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      #TODO ADD ALL SEQUENCES ONCE THEY ARE CREATED
#
#  @timelineReworkF3_costi #11.11 dopo restart il baseCost non cambia, costi supplementari si
#  Scenario Outline: [TR3_PAYMENTS_REWORK_11_DELIVERY_MODE_SYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
#    Given viene generata una nuova notifica
#      | subject               | test costi rework    |
#      | physicalCommunication | AR_REGISTERED_LETTER |
#      | senderDenomination    | Comune di palermo    |
#      | pagoPaIntMode         | SYNC                 |
#      | feePolicy             | DELIVERY_MODE        |
#      | paFee                 | 17                   |
#      | vat                   | 10                   |
#    And destinatario Mario Gherkin e:
#      | digitalDomicile              | NULL                    |
#      | physicalAddress_address      | <sequence>              |
#      | physicalAddress_municipality | LAGO PATRIA             |
#      | physicalAddress_zip          | 80014                   |
#      | physicalAddress_province     | NA                      |
#      | payment_creditorTaxId        | 77777777777             |
#      | payment_pagoPaForm           | SI                      |
#      | payment_f24                  | NULL                    |
#      | title_payment                | PagoPa_mono_sync_d_mode |
#      | apply_cost_pagopa            | SI                      |
#      | payment_multy_number         | 1                       |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
#    And pre rework vengono recuperati i costi dall'api di delivery per il destinatario 0
#    And verifico che pre rework per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attempt>
#    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
#      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode   | expectedDeliveryFailureCause | reason     |
#      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | <expectedStatusCode> | <failCode>                   | reasonTest |
#    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    When viene invocato il consolidatore per inserire tutti gli eventi previsti per il destinatario 0 che portano allo status code "<expectedStatusCode>" al tentativo <attemptRestart>
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And post rework vengono recuperati i costi dall'api di delivery per il destinatario 0
#    And il valore dei costi restituiti dall'api di delivery è <deliveryCostEqual> rispetto a prima del rework
#    And verifico che post rework per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt <attemptRestart>
#    And il baseCost è uguale rispetto a prima del rework
#    Examples:
#      | sequence             | finalEvent                         | finalStatus        | attempt | expectedStatusCode | failCode | finalEventRestart                  | finalStatusRestart | attemptRestart | deliveryCostEqual |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              | uguale            |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              | differente        |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente        |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente        |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              | differente        |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              | uguale            |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente        |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente        |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              | differente        |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              | differente        |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | uguale            |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente        |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              | differente        |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              | differente        |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente        |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | uguale            |
#      #TODO ADD ALL SEQUENCES ONCE THEY ARE CREATED|
#
#    # il baseCost cambia
#    # firstAnalogCost immutato
#  @timelineReworkF3_costi #11.12
#  Scenario Outline: [TR3_PAYMENTS_REWORK_12_DELIVERY_MODE_ASYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode async) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al rework
#    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
#    And viene generata una nuova notifica
#      | subject               | test costi rework    |
#      | physicalCommunication | AR_REGISTERED_LETTER |
#      | senderDenomination    | Comune di palermo    |
#      | pagoPaIntMode         | ASYNC                |
#      | feePolicy             | DELIVERY_MODE        |
#      | paFee                 | 17                   |
#      | vat                   | 10                   |
#    And destinatario Mario Gherkin e:
#      | digitalDomicile              | NULL                     |
#      | physicalAddress_address      | <sequence>               |
#      | physicalAddress_municipality | LAGO PATRIA              |
#      | physicalAddress_zip          | 80014                    |
#      | physicalAddress_province     | NA                       |
#      | payment_creditorTaxId        | 77777777777              |
#      | payment_pagoPaForm           | SI                       |
#      | payment_f24                  | NULL                     |
#      | title_payment                | PagoPa_mono_async_d_mode |
#      | apply_cost_pagopa            | SI                       |
#      | payment_multy_number         | 1                        |
#    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
#    And verifico che pre rework per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attempt>
#    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
#      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode   | expectedDeliveryFailureCause | reason     |
#      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | <expectedStatusCode> | <failCode>                   | reasonTest |
#    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    When viene invocato il consolidatore per inserire tutti gli eventi previsti per il destinatario 0 che portano allo status code "<expectedStatusCode>" al tentativo <attemptRestart>
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And verifico che post rework per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt <attemptRestart>
#    And il baseCost è uguale rispetto a prima del rework
#    Examples:
#      | sequence             | finalEvent                         | finalStatus        | attempt | expectedStatusCode | failCode | finalEventRestart                  | finalStatusRestart | attemptRestart |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERY_AR             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
#      #TODO ADD ALL SEQUENCES ONCE THEY ARE CREATED
#
#
#  Scenario Outline: [TODO_REMOVE_DEBUG]
##    Given viene generata una nuova notifica
##      | subject               | test costi rework    |
##      | physicalCommunication | AR_REGISTERED_LETTER |
##      | senderDenomination    | Comune di palermo    |
##      | pagoPaIntMode         | SYNC                 |
##      | feePolicy             | FLAT_RATE            |
##      | paFee                 | 17                   |
##      | vat                   | 10                   |
##    And destinatario Mario Gherkin e:
##      | digitalDomicile              | NULL                       |
##      | physicalAddress_address      | <sequence>                 |
##      | physicalAddress_municipality | LAGO PATRIA                |
##      | physicalAddress_zip          | 80014                      |
##      | physicalAddress_province     | NA                         |
##      | payment_creditorTaxId        | 77777777777                |
##      | payment_pagoPaForm           | SI                         |
##      | payment_f24                  | NULL                       |
##      | title_payment                | PagoPa_mono_sync_flat_rate |
##      | apply_cost_pagopa            | NO                         |
##      | payment_multy_number         | 1                          |
##    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
##    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
##    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
##    And verifico che pre rework per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attempt>
#    Given imposto lo iun di SharedSteps a "MXDA-PWMD-VTYJ-202606-A-1" e la pa a "Comune_Multi"
#    When viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
#      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode   | expectedDeliveryFailureCause | reason     |
#      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | <expectedStatusCode> | <failCode>                   | reasonTest |
#    Then si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 130 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di rework effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    When viene invocato il consolidatore per inserire tutti gli eventi previsti per il destinatario 0 che portano allo status code "<expectedStatusCode>" al tentativo <attemptRestart>
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And si verifica che la richiesta di rework effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And verifico che post rework per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt <attemptRestart>
#    And il baseCost è uguale rispetto a prima del rework
#    Examples:
#      | sequence             | finalEvent                         | finalStatus        | attempt | expectedStatusCode | failCode | finalEventRestart       | finalStatusRestart | attemptRestart |
##      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
#      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW | EFFECTIVE_DATE     | 0              |
##      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@FAIL_DECEDUTO_AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN002C          | M02      | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
##      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
##      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN001C          |          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
##      | Via@OK_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | RECRN002F          | M01      | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |