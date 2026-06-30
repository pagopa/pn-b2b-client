Feature: Correzione timeline fase 3 costi
  #SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2700673102/SRS+Correzione+timeline+-+Fase+3
  #PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3002826778/PST+-+Correzione+Timeline+-+FASE+3

  @timelineReworkF3_costi_async
  Scenario: [TR3_PAYMENTS_RESTART_1_MONOPAY]
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi restart   |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                    |
      | physicalAddress_address      | Via@OK_AR               |
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

  @timelineReworkF3_costi_async
  Scenario: [TR3_PAYMENTS_RESTART_2_MULTIPAY]
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi restart   |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                    |
      | physicalAddress_address      | Via@OK_AR               |
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "ERROR" entro 130 secondi controllando ogni 5 secondi
    And viene verificato che l'elemento di timeline "NOTIFICATION_TIMELINE_REWORKED" non esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  #@timelineReworkF3_costi_async #11.3 ??? attemptId = ATTEMPT_0 in KO per destinatario deceduto DIVENTA MANUALE
#  Scenario: [TR3_PAYMENTS_RESTART_3_MIXED_APPLY_COST]
#    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
#    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
#    And viene generata una nuova notifica
#      | subject               | test costi restart   |
#      | physicalCommunication | AR_REGISTERED_LETTER |
#      | senderDenomination    | Comune di palermo    |
#      | pagoPaIntMode         | ASYNC                |
#      | feePolicy             | DELIVERY_MODE        |
#      | paFee                 | 17                   |
#      | vat                   | 10                   |
#    And destinatario Mario Gherkin e:
#      | digitalDomicile              | NULL                    |
#      | physicalAddress_address      | Via@FAIL_DECEDUTO_AR    |
#      | physicalAddress_municipality | LAGO PATRIA             |
#      | physicalAddress_zip          | 80014                   |
#      | physicalAddress_province     | NA                      |
#      | payment_creditorTaxId        | 77777777777             |
#      | payment_pagoPaForm           | SI                      |
#      | payment_f24                  | NULL                    |
#      | title_payment                | PagoPa_mono_async_sada0 |
#      | apply_cost_pagopa            | SI                      |
#      | payment_multy_number         | 2                       |
#    And al destinatario 0 viene settato l'applyCost del pagamento PagoPa alla posizione 1 a false
#    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 0 per il suo pagamento alla posizione 0
#    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 1 per il suo pagamento alla posizione 1
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
#    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
#    Then l'avviso pagopa 1 viene pagato correttamente dall'utente 0
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"

  @timelineReworkF3_costi_async #11.5
  Scenario Outline: [TR3_PAYMENTS_RESTART_5_FLATRATE_ASYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate async) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi restart   |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
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
    And pre restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attempt>
    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And post restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attemptRestart>
    And il baseCost è uguale rispetto a prima del rework
    Examples:
      | sequence                               | finalEvent                         | finalStatus        | attempt | finalEventRestart                  | finalStatusRestart | attemptRestart |
      | Via@OK_DEC_RESTART_CONS_AR             | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
      | Via@OK_DEC_RESTART_CONS_ATT1_AR        | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@OK_DEC_RESTART_IRR_AR              | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@OK_RESTART_CONS_ATT1_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@OK_RESTART_IRR_AR                  | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@OK_RESTART_DEC_AR                  | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
      | Via@FAIL_DISC_RESTART_CONS_AR          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
      | Via@FAIL_DISC_RESTART_IRR_AR           | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@FAIL_DISC_RESTART_DEC_AR           | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
      | Via@FAIL_IRREP_RESTART_1_CONS_AT1_AR   | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
      | Via@FAIL_DISC_IRR_RESTART_CONS_ATT1_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@FAIL_DISC_IRR_RESTART_DEC_AR       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
      #Restart all'attempt 1
      | Via@FAIL_DISC_RESTART_1_IRREP_AR       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@FAIL_IRREP_RESTART_1_CONS_AT2_AR   | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |

  @timelineReworkF3_costi_async #11.5
  Scenario Outline: [TR3_PAYMENTS_RESTART_SAME_5_FLATRATE_ASYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate async) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi restart   |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
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
    And pre restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attempt>
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId   | recIndex   | reason     | task       |
      |     | <attemptId> | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di restart effettuata sia in stato "IN_PROGRESS" entro 300 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attemptRestart>
    And il baseCost è uguale rispetto a prima del rework
    Examples:
      | sequence                          | attemptId | finalEvent                         | finalStatus        | attempt | finalEventRestart                  | finalStatusRestart | attemptRestart |
      | Via@FAIL_DECEDUTO_AR              | ATTEMPT_0 | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
      | Via@OK_AR                         | ATTEMPT_0 | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
      | Via@FAIL-DISCOVERY_AR             | ATTEMPT_0 | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ATTEMPT_0 | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      #Restart all'attempt 1
      | Via@FAIL-DISCOVERY_AR             | ATTEMPT_1 | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ATTEMPT_1 | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |

  # il baseCost cambia
  # firstAnalogCost immutato
  @timelineReworkF3_costi_async #11.7
  Scenario Outline: [TR3_PAYMENTS_RESTART_7_DELIVERY_MODE_ASYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode async) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi restart   |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | <sequence>               |
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
    And pre restart vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre restart vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt <attempt>
    And pre restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attempt>
    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
    Then la timeline contiene elementi con la stringa "REWORK_"
    And post restart vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post restart vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt <attemptRestart>
    And post restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attemptRestart>
    And il record recuperato su pn-CostComponents è <costCompare> rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è <costCompare> rispetto a prima del rework
    Examples:
      | sequence                               | finalEvent                         | finalStatus        | attempt | finalEventRestart                  | finalStatusRestart | attemptRestart | costCompare |
      | Via@OK_DEC_RESTART_CONS_AR             | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              | uguale      |
      | Via@OK_DEC_RESTART_CONS_ATT1_AR        | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente  |
      | Via@OK_DEC_RESTART_IRR_AR              | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente  |
      | Via@OK_RESTART_CONS_ATT1_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente  |
      | Via@OK_RESTART_IRR_AR                  | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente  |
      | Via@OK_RESTART_DEC_AR                  | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              | uguale      |
      | Via@FAIL_DISC_RESTART_CONS_AR          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              | differente  |
      | Via@FAIL_DISC_RESTART_IRR_AR           | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | uguale      |
      | Via@FAIL_DISC_RESTART_DEC_AR           | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              | differente  |
      | Via@FAIL_IRREP_RESTART_1_CONS_AT1_AR   | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              | differente  |
      | Via@FAIL_DISC_IRR_RESTART_CONS_ATT1_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | uguale      |
      | Via@FAIL_DISC_IRR_RESTART_DEC_AR       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              | differente  |
      #Restart all'attempt 1                 |
      | Via@FAIL_DISC_RESTART_1_IRREP_AR       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | uguale      |
      | Via@FAIL_IRREP_RESTART_1_CONS_AT2_AR   | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | uguale      |

  # il baseCost cambia
  # firstAnalogCost immutato
  @timelineReworkF3_costi_async #11.7
  Scenario Outline: [TR3_PAYMENTS_RESTART_SAME_7_DELIVERY_MODE_ASYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode async) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject               | test costi restart   |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | ASYNC                |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | <sequence>               |
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
    And pre restart vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And pre restart vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt <attempt>
    And pre restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attempt>
    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId   | recIndex   | reason     | task       |
      |     | <attemptId> | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And si verifica che la richiesta di restart effettuata sia in stato "IN_PROGRESS" entro 300 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
    And la timeline contiene elementi con la stringa "REWORK_"
    And post restart vengono recuperati i valori dei costi notifica relativi all'utente 0 sulla tabella pn-CostComponents
    And post restart vengono recuperati i valori dei costi notifica relativi al pagamento 0 dell'utente 0 sulla tabella pn-CostUpdateResult fino all'attempt <attemptRestart>
    And post restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attemptRestart>
    And il record recuperato su pn-CostComponents è uguale rispetto a prima del rework
    And il valore del notification cost dei record su pn-CostUpdateResult è uguale rispetto a prima del rework
    And il baseCost è uguale rispetto a prima del rework
    Examples:
      | sequence                          | attemptId | finalEvent                         | finalStatus        | attempt | finalEventRestart                  | finalStatusRestart | attemptRestart |
      | Via@FAIL_DECEDUTO_AR              | ATTEMPT_0 | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              |
      | Via@OK_AR                         | ATTEMPT_0 | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              |
      | Via@FAIL-DISCOVERY_AR             | ATTEMPT_0 | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ATTEMPT_0 | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      #Restart all'attempt 1
      | Via@FAIL-DISCOVERY_AR             | ATTEMPT_1 | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              |
      | Via@FAIL-DISCOVERYIRREPERIBILE_AR | ATTEMPT_1 | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              |