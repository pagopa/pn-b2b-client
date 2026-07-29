Feature: Correzione timeline fase 3 costi
  #SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2700673102/SRS+Correzione+timeline+-+Fase+3
  #PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3002826778/PST+-+Correzione+Timeline+-+FASE+3

  @timelineReworkF3_costi_sync #11.4
  Scenario Outline: [TR3_PAYMENTS_RESTART_4_FLATRATE_SYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene generata una nuova notifica
      | subject               | test costi restart   |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | SYNC                 |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
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

  @timelineReworkF3_costi_sync #11.4
  Scenario Outline: [TR3_PAYMENTS_RESTART_SAME_4_FLATRATE_SYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(flat rate sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene generata una nuova notifica
      | subject               | test costi restart   |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | SYNC                 |
      | feePolicy             | FLAT_RATE            |
      | paFee                 | 17                   |
      | vat                   | 10                   |
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

  @timelineReworkF3_costi_sync #11.6 dopo restart il baseCost non cambia, costi supplementari potrebbero cambiare
  Scenario Outline: [TR3_PAYMENTS_RESTART_6_DELIVERY_MODE_SYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
#    Given viene generata una nuova notifica
#      | subject               | test costi restart   |
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
#    And pre restart vengono recuperati i costi dall'api di delivery per il destinatario 0
#    And pre restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati inseriti e correttamente valorizzati fino all'attempt <attempt>
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
    Given imposto lo iun di SharedSteps a "KLRA-VJTQ-LTRU-202607-H-1" e la pa a "Comune_Multi"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEventRestart>"
#    And vengono letti gli eventi fino allo stato della notifica "<finalStatusRestart>"
#    Then la timeline contiene elementi con la stringa "REWORK_"
#    And post restart vengono recuperati i costi dall'api di delivery per il destinatario 0
#    And il valore dei costi restituiti dall'api di delivery è <deliveryCost> rispetto a prima del rework
#    And post restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt <attemptRestart>
#    And il baseCost è uguale rispetto a prima del rework
    Examples:
      | sequence                               | finalEvent                         | finalStatus        | attempt | finalEventRestart                  | finalStatusRestart | attemptRestart | deliveryCost |
      | Via@OK_DEC_RESTART_CONS_AR             | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              | differente   |
      | Via@OK_DEC_RESTART_CONS_ATT1_AR        | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente   |
      | Via@OK_DEC_RESTART_IRR_AR              | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente   |
      | Via@OK_RESTART_CONS_ATT1_AR            | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente   |
      | Via@OK_RESTART_IRR_AR                  | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | differente   |
      | Via@OK_RESTART_DEC_AR                  | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              | differente   |
      | Via@FAIL_DISC_RESTART_CONS_AR          | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              | differente   |
      | Via@FAIL_DISC_RESTART_IRR_AR           | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | uguale       |
      | Via@FAIL_DISC_RESTART_DEC_AR           | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              | differente   |
      | Via@FAIL_IRREP_RESTART_1_CONS_AT1_AR   | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 0              | differente   |
      | Via@FAIL_DISC_IRR_RESTART_CONS_ATT1_AR | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | uguale       |
      | Via@FAIL_DISC_IRR_RESTART_DEC_AR       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_WORKFLOW_RECIPIENT_DECEASED | RETURNED_TO_SENDER | 0              | differente   |
      #Restart all'attempt 1                 |
      | Via@FAIL_DISC_RESTART_1_IRREP_AR       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1              | uguale       |
      | Via@FAIL_IRREP_RESTART_1_CONS_AT2_AR   | ANALOG_FAILURE_WORKFLOW            | EFFECTIVE_DATE     | 1       | ANALOG_SUCCESS_WORKFLOW            | EFFECTIVE_DATE     | 1              | uguale       |

  @timelineReworkF3_costi_sync #11.6 dopo restart il baseCost non cambia, costi supplementari potrebbero cambiare
  Scenario Outline: [TR3_PAYMENTS_RESTART_SAME_6_DELIVERY_MODE_SYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(delivery mode sync) e controllo della corretta valorizzazione dei dati su pn-notificationDeliveryCost in seguito al restart
    Given viene generata una nuova notifica
      | subject               | test costi restart   |
      | physicalCommunication | AR_REGISTERED_LETTER |
      | senderDenomination    | Comune di palermo    |
      | pagoPaIntMode         | SYNC                 |
      | feePolicy             | DELIVERY_MODE        |
      | paFee                 | 17                   |
      | vat                   | 10                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                    |
      | physicalAddress_address      | <sequence>              |
      | physicalAddress_municipality | LAGO PATRIA             |
      | physicalAddress_zip          | 80014                   |
      | physicalAddress_province     | NA                      |
      | payment_creditorTaxId        | 77777777777             |
      | payment_pagoPaForm           | SI                      |
      | payment_f24                  | NULL                    |
      | title_payment                | PagoPa_mono_sync_d_mode |
      | apply_cost_pagopa            | SI                      |
      | payment_multy_number         | 1                       |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<finalEvent>"
    And vengono letti gli eventi fino allo stato della notifica "<finalStatus>"
    And pre restart vengono recuperati i costi dall'api di delivery per il destinatario 0
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
    And post restart vengono recuperati i costi dall'api di delivery per il destinatario 0
    And il valore dei costi restituiti dall'api di delivery è uguale rispetto a prima del rework
    And post restart verifico che per il destinatario 0 i record su Pn-NotificationDeliveryCost siano stati modificati e correttamente valorizzati fino all'attempt <attemptRestart>
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