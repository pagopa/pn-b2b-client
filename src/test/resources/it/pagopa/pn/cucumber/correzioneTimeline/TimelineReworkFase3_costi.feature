Feature: Correzione timeline fase 3 costi
  #SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2700673102/SRS+Correzione+timeline+-+Fase+3
  #PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3002826778/PST+-+Correzione+Timeline+-+FASE+3

  @timelineReworkF3_costi
  Scenario: [TR3_PAYMENTS_RESTART_1_MONOPAY]
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | ASYNC                      |
      | feePolicy          | DELIVERY_MODE              |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                    |
      | physicalAddress_address      | Via@OK_890              |
      | physicalAddress_municipality | LAGO PATRIA             |
      | physicalAddress_zip          | 80014                   |
      | physicalAddress_province     | NA                      |
      | payment_creditorTaxId        | 77777777777             |
      | payment_pagoPaForm           | SI                      |
      | payment_f24                  | NULL                    |
      | title_payment                | PagoPa_mono_async_sada0 |
      | apply_cost_pagopa            | SI                      |
      | payment_multy_number         | 1                       |
    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 0 per il suo pagamento alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi
    And viene verificato che l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED" non esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  @timelineReworkF3_costi
  Scenario: [TR3_PAYMENTS_RESTART_2_MULTIPAY]
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | ASYNC                      |
      | feePolicy          | DELIVERY_MODE              |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                    |
      | physicalAddress_address      | Via@OK_890              |
      | physicalAddress_municipality | LAGO PATRIA             |
      | physicalAddress_zip          | 80014                   |
      | physicalAddress_province     | NA                      |
      | payment_creditorTaxId        | 77777777777             |
      | payment_pagoPaForm           | SI                      |
      | payment_f24                  | NULL                    |
      | title_payment                | PagoPa_mono_async_sada0 |
      | apply_cost_pagopa            | SI                      |
      | payment_multy_number         | 2                       |
    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 0 per il suo pagamento alla posizione 0
    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 1 per il suo pagamento alla posizione 1
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi
    And viene verificato che l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED" non esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  @timelineReworkF3_costi #11.3 ??? attemptId = ATTEMPT_0 in KO per destinatario deceduto
  Scenario: [TR3_PAYMENTS_RESTART_3_MIXED_APPLY_COST]
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | ASYNC                      |
      | feePolicy          | DELIVERY_MODE              |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                    |
      | physicalAddress_address      | Via@FAIL_DECEDUTO_890   |
      | physicalAddress_municipality | LAGO PATRIA             |
      | physicalAddress_zip          | 80014                   |
      | physicalAddress_province     | NA                      |
      | payment_creditorTaxId        | 77777777777             |
      | payment_pagoPaForm           | SI                      |
      | payment_f24                  | NULL                    |
      | title_payment                | PagoPa_mono_async_sada0 |
      | apply_cost_pagopa            | SI                      |
      | payment_multy_number         | 2                       |
    And al destinatario 0 viene settato l'applyCost del pagamento PagoPa alla posizione 1 a false
    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 0 per il suo pagamento alla posizione 0
    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 1 per il suo pagamento alla posizione 1
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"

  @timelineReworkF3_costi #11.4
  Scenario Outline: [TR3_PAYMENTS_RESTART_4_FLATRATE_SYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | SYNC                       |
      | feePolicy          | FLAT_RATE                  |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                       |
      | physicalAddress_address      | <sequence>                 |
      | physicalAddress_municipality | LAGO PATRIA                |
      | physicalAddress_zip          | 80014                      |
      | physicalAddress_province     | NA                         |
      | payment_creditorTaxId        | 77777777777                |
      | payment_pagoPaForm           | SI                         |
      | payment_f24                  | NULL                       |
      | title_payment                | PagoPa_mono_sync_flat_rate |
      | apply_cost_pagopa            | NO                         |
      | payment_multy_number         | 1                          |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
    And verifico che pre restart per il destinatario 0 con indirizzo "<sequence>" i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di restart effettuata sia in stato "IN_PROGRESS" entro 300 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
    And la timeline contiene elementi con la stringa "REWORK_"
    And verifico che post restart per il destinatario 0 con indirizzo "<sequence>" i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati
    And il baseCost è uguale rispetto a prima del rework
    Examples:
      | sequence                           | finalEvent                         | finalStatus        | finalEventRestart                  | finalStatusRestart |
      | Via@OK_890                         | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     |
      | Via@FAIL-DISCOVERY_890             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     |
      | Via@FAIL_DECEDUTO_890              | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER |
      | Via@FAIL-DISCOVERYIRREPERIBILE_890 | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     |
      #TODO ADD ALL SEQUENCES ONCE THEY ARE CREATED

  @timelineReworkF3_costi #11.5
  Scenario Outline: [TR3_PAYMENTS_RESTART_5_FLATRATE_ASYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate async) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | ASYNC                      |
      | feePolicy          | FLAT_RATE                  |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | <sequence>                  |
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
    And verifico che pre restart per il destinatario 0 con indirizzo "<sequence>" i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di restart effettuata sia in stato "IN_PROGRESS" entro 300 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
    And la timeline contiene elementi con la stringa "REWORK_"
    And verifico che post restart per il destinatario 0 con indirizzo "<sequence>" i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati
    And il baseCost è uguale rispetto a prima del rework
    Examples:
      | sequence                           | finalEvent                         | finalStatus        | finalEventRestart                  | finalStatusRestart |
      | Via@OK_890                         | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     |
      | Via@FAIL-DISCOVERY_890             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     |
      | Via@FAIL_DECEDUTO_890              | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER |
      | Via@FAIL-DISCOVERYIRREPERIBILE_890 | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     |
      #TODO ADD ALL SEQUENCES ONCE THEY ARE CREATED

  @timelineReworkF3_costi #11.6 dopo restart il baseCost non cambia, costi supplementari si
  Scenario Outline: [TR3_PAYMENTS_RESTART_6_DELIVERY_MODE_SYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | SYNC                       |
      | feePolicy          | DELIVERY_MODE              |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                       |
      | physicalAddress_address      | <sequence>                 |
      | physicalAddress_municipality | LAGO PATRIA                |
      | physicalAddress_zip          | 80014                      |
      | physicalAddress_province     | NA                         |
      | payment_creditorTaxId        | 77777777777                |
      | payment_pagoPaForm           | SI                         |
      | payment_f24                  | NULL                       |
      | title_payment                | PagoPa_mono_sync_flat_rate |
      | apply_cost_pagopa            | SI                         |
      | payment_multy_number         | 1                          |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
    And verifico che pre restart per il destinatario 0 con indirizzo "<sequence>" i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di restart effettuata sia in stato "IN_PROGRESS" entro 300 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
    And la timeline contiene elementi con la stringa "REWORK_"
    And verifico che post restart per il destinatario 0 con indirizzo "<sequence>" i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati
    And il baseCost è uguale rispetto a prima del rework
    Examples:
      | sequence                           | finalEvent                         | finalStatus        | finalEventRestart                  | finalStatusRestart |
      | Via@OK_890                         | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     |
      | Via@FAIL-DISCOVERY_890             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     |
      | Via@FAIL_DECEDUTO_890              | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER |
      | Via@FAIL-DISCOVERYIRREPERIBILE_890 | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     |
      #TODO ADD ALL SEQUENCES ONCE THEY ARE CREATED

    # il baseCost cambia
    # firstAnalogCost immutato
  @timelineReworkF3_costi #11.7
  Scenario Outline: [TR3_PAYMENTS_RESTART_7_DELIVERY_MODE_ASYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode async) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | ASYNC                      |
      | feePolicy          | DELIVERY_MODE              |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                        |
      | physicalAddress_address      | <sequence>                  |
      | physicalAddress_municipality | LAGO PATRIA                 |
      | physicalAddress_zip          | 80014                       |
      | physicalAddress_province     | NA                          |
      | payment_creditorTaxId        | 77777777777                 |
      | payment_pagoPaForm           | SI                          |
      | payment_f24                  | NULL                        |
      | title_payment                | PagoPa_mono_async_flat_rate |
      | apply_cost_pagopa            | SI                          |
      | payment_multy_number         | 1                           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
    And verifico che pre restart per il destinatario 0 con indirizzo "<sequence>" i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di restart effettuata sia in stato "IN_PROGRESS" entro 300 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
    And la timeline contiene elementi con la stringa "REWORK_"
    And verifico che post restart per il destinatario 0 con indirizzo "<sequence>" i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati
    And il baseCost è uguale rispetto a prima del rework

    Examples:
      | sequence                           | finalEvent                         | finalStatus        | finalEventRestart                  | finalStatusRestart |
      | Via@OK_890                         | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     |
      | Via@FAIL-DISCOVERY_890             | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     |
      | Via@FAIL_DECEDUTO_890              | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER |
      | Via@FAIL-DISCOVERYIRREPERIBILE_890 | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     |
      #TODO ADD ALL SEQUENCES ONCE THEY ARE CREATED
