Feature: Correzione timeline fase 3
  #SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2700673102/SRS+Correzione+timeline+-+Fase+3
  #PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3002826778/PST+-+Correzione+Timeline+-+FASE+3

  ###Via@FAIL_DECEDUTO_AR

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_1] Restart di notifica che va in RETURNED TO SENDER (anche al restart va in RETURNED TO SENDER)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                 |
      | payment_creditorTaxId   | 77777777777          |
      | payment_pagoPaForm      | SI                   |
      | payment_f24             | NULL                 |
      | title_payment           | PagoPa_testRestart   |
      | apply_cost_pagopa       | SI                   |
      | payment_multy_number    | 1                    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0    |
      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX0 |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0    |
      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX0 |

#  @timelineReworkF3 @checkRestart
#  Scenario: [TR3_RESTART_MONODEST_2] Restart di notifica che va in RETURNED TO SENDER (al restart va in OK all'attempt 0)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@FAIL_DECEDUTO_AR |
#      | digitalDomicile         | NULL                 |
#      | payment_creditorTaxId   | 77777777777          |
#      | payment_pagoPaForm      | SI                   |
#      | payment_f24             | NULL                 |
#      | title_payment           | PagoPa_testRestart   |
#      | apply_cost_pagopa       | SI                   |
#      | payment_multy_number    | 1                    |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
#    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0    |
#      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX0 |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0    |
#
#  @timelineReworkF3 @checkRestart
#  Scenario: [TR3_RESTART_MONODEST_3] Restart di notifica che va in RETURNED TO SENDER (al restart va in KO all'attempt 0 e in OK all'attempt 1)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@FAIL_DECEDUTO_AR |
#      | digitalDomicile         | NULL                 |
#      | payment_creditorTaxId   | 77777777777          |
#      | payment_pagoPaForm      | SI                   |
#      | payment_f24             | NULL                 |
#      | title_payment           | PagoPa_testRestart   |
#      | apply_cost_pagopa       | SI                   |
#      | payment_multy_number    | 1                    |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
#    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0    |
#      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX0 |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#
#  @timelineReworkF3 @checkRestart
#  Scenario: [TR3_RESTART_MONODEST_4] Restart di notifica che va in RETURNED TO SENDER (al restart va in KO all'attempt 0 e all'attempt 1)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@FAIL_DECEDUTO_AR |
#      | digitalDomicile         | NULL                 |
#      | payment_creditorTaxId   | 77777777777          |
#      | payment_pagoPaForm      | SI                   |
#      | payment_f24             | NULL                 |
#      | title_payment           | PagoPa_testRestart   |
#      | apply_cost_pagopa       | SI                   |
#      | payment_multy_number    | 1                    |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
#    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0    |
#      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX0 |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#
#  @timelineReworkF3 @checkRestart
#  Scenario: [TR3_RESTART_MONODEST_5] Restart di notifica che va in RETURNED TO SENDER (al restart va in KO all'attempt 0 e all'attempt 1 con COMPLETELY_UNREACHABLE)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@FAIL_DECEDUTO_AR |
#      | digitalDomicile         | NULL                 |
#      | payment_creditorTaxId   | 77777777777          |
#      | payment_pagoPaForm      | SI                   |
#      | payment_f24             | NULL                 |
#      | title_payment           | PagoPa_testRestart   |
#      | apply_cost_pagopa       | SI                   |
#      | payment_multy_number    | 1                    |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
#    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0    |
#      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX0 |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |

  ###Via@OK_AR

  @timelineReworkF3 @checkRestart
  Scenario Outline: [TR3_RESTART_MONODEST_6] Restart di notifica che va in OK all'attempt 0 (anche al restart va in OK all'attempt 0)
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
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_7] Restart di notifica che va in OK all'attempt 0 (al restart va in KO all'attempt 0 e OK all'attempt 1)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@OK_AR          |
#      | digitalDomicile         | NULL               |
#      | payment_creditorTaxId   | 77777777777        |
#      | payment_pagoPaForm      | SI                 |
#      | payment_f24             | NULL               |
#      | title_payment           | PagoPa_testRestart |
#      | apply_cost_pagopa       | SI                 |
#      | payment_multy_number    | 1                  |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |
#
#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_8] Restart di notifica che va in OK all'attempt 0 (al restart va in KO all'attempt 0 e KO all'attempt 1)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@OK_AR          |
#      | digitalDomicile         | NULL               |
#      | payment_creditorTaxId   | 77777777777        |
#      | payment_pagoPaForm      | SI                 |
#      | payment_f24             | NULL               |
#      | title_payment           | PagoPa_testRestart |
#      | apply_cost_pagopa       | SI                 |
#      | payment_multy_number    | 1                  |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |
#
#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_9] Restart di notifica che va in OK all'attempt 0 (al restart va in RETURNED_TO_SENDER)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@OK_AR          |
#      | digitalDomicile         | NULL               |
#      | payment_creditorTaxId   | 77777777777        |
#      | payment_pagoPaForm      | SI                 |
#      | payment_f24             | NULL               |
#      | title_payment           | PagoPa_testRestart |
#      | apply_cost_pagopa       | SI                 |
#      | payment_multy_number    | 1                  |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |
#
#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_10] Restart di notifica che va in OK all'attempt 0 (al restart va in KO all'attempt 0 e KO all'attempt 1 con COMPLETELY_UNREACHABLE)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario
#      | denomination            | Test AR Fail       |
#      | taxId                   | MNTMRA03M71C615V   |
#      | physicalAddress_address | Via@OK_AR          |
#      | digitalDomicile         | NULL               |
#      | payment_creditorTaxId   | 77777777777        |
#      | payment_pagoPaForm      | SI                 |
#      | payment_f24             | NULL               |
#      | title_payment           | PagoPa_testRestart |
#      | apply_cost_pagopa       | SI                 |
#      | payment_multy_number    | 1                  |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |

  ###Via@FAIL-DISCOVERY_AR

  @timelineReworkF3 @checkRestart
  Scenario Outline: [TR3_RESTART_MONODEST_11] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (anche al restart va in KO all'attempt 0 e in OK all'attempt 1)
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
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_12] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in OK all'attempt 0)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
#      | digitalDomicile         | NULL                  |
#      | payment_creditorTaxId   | 77777777777           |
#      | payment_pagoPaForm      | SI                    |
#      | payment_f24             | NULL                  |
#      | title_payment           | PagoPa_testRestart    |
#      | apply_cost_pagopa       | SI                    |
#      | payment_multy_number    | 1                     |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                     |
#      | tag             | AUD_NT_UPDATE_COST                                                       |
#      | recIndex        | recIndex=0                                                               |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
#      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |
#
#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_13] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in KO all'attempt 0 e all'attempt 1)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
#      | digitalDomicile         | NULL                  |
#      | payment_creditorTaxId   | 77777777777           |
#      | payment_pagoPaForm      | SI                    |
#      | payment_f24             | NULL                  |
#      | title_payment           | PagoPa_testRestart    |
#      | apply_cost_pagopa       | SI                    |
#      | payment_multy_number    | 1                     |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                     |
#      | tag             | AUD_NT_UPDATE_COST                                                       |
#      | recIndex        | recIndex=0                                                               |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
#      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |
#
#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_14] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in RETURNED TO SENDER)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
#      | digitalDomicile         | NULL                  |
#      | payment_creditorTaxId   | 77777777777           |
#      | payment_pagoPaForm      | SI                    |
#      | payment_f24             | NULL                  |
#      | title_payment           | PagoPa_testRestart    |
#      | apply_cost_pagopa       | SI                    |
#      | payment_multy_number    | 1                     |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                     |
#      | tag             | AUD_NT_UPDATE_COST                                                       |
#      | recIndex        | recIndex=0                                                               |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
#      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |
#
#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_15] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in KO all'attempt 0 e all'attempt 1 con COMPLETELY_UNREACHABLE)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario
#      | denomination            | Test AR Fail          |
#      | taxId                   | MNTMRA03M71C615V      |
#      | physicalAddress_address | Via@FAIL-DISCOVERY_AR |
#      | digitalDomicile         | NULL                  |
#      | payment_creditorTaxId   | 77777777777           |
#      | payment_pagoPaForm      | SI                    |
#      | payment_f24             | NULL                  |
#      | title_payment           | PagoPa_testRestart    |
#      | apply_cost_pagopa       | SI                    |
#      | payment_multy_number    | 1                     |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                     |
#      | tag             | AUD_NT_UPDATE_COST                                                       |
#      | recIndex        | recIndex=0                                                               |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
#      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |

  ###Via@FAIL-DISCOVERYIRREPERIBILE_AR

  @timelineReworkF3 @checkRestart
  Scenario Outline: [TR3_RESTART_MONODEST_16] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (anche al restart va in KO all'attempt 0 e all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
      | digitalDomicile         | NULL                              |
      | payment_creditorTaxId   | 77777777777                       |
      | payment_pagoPaForm      | SI                                |
      | payment_f24             | NULL                              |
      | title_payment           | PagoPa_testRestart                |
      | apply_cost_pagopa       | SI                                |
      | payment_multy_number    | 1                                 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_17] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in OK all'attempt 0)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
#      | digitalDomicile         | NULL                              |
#      | payment_creditorTaxId   | 77777777777                       |
#      | payment_pagoPaForm      | SI                                |
#      | payment_f24             | NULL                              |
#      | title_payment           | PagoPa_testRestart                |
#      | apply_cost_pagopa       | SI                                |
#      | payment_multy_number    | 1                                 |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                     |
#      | tag             | AUD_NT_UPDATE_COST                                                       |
#      | recIndex        | recIndex=0                                                               |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
#      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |
#
#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_18] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in KO all'attempt 0 e in OK all'attempt 1)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
#      | digitalDomicile         | NULL                              |
#      | payment_creditorTaxId   | 77777777777                       |
#      | payment_pagoPaForm      | SI                                |
#      | payment_f24             | NULL                              |
#      | title_payment           | PagoPa_testRestart                |
#      | apply_cost_pagopa       | SI                                |
#      | payment_multy_number    | 1                                 |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                     |
#      | tag             | AUD_NT_UPDATE_COST                                                       |
#      | recIndex        | recIndex=0                                                               |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
#      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |
#
#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_19] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in RETURNED TO SENDER)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario Mario Gherkin e:
#      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
#      | digitalDomicile         | NULL                              |
#      | payment_creditorTaxId   | 77777777777                       |
#      | payment_pagoPaForm      | SI                                |
#      | payment_f24             | NULL                              |
#      | title_payment           | PagoPa_testRestart                |
#      | apply_cost_pagopa       | SI                                |
#      | payment_multy_number    | 1                                 |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                     |
#      | tag             | AUD_NT_UPDATE_COST                                                       |
#      | recIndex        | recIndex=0                                                               |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
#      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |
#
#  @timelineReworkF3 @checkRestart
#  Scenario Outline: [TR3_RESTART_MONODEST_20] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in KO all'attempt 0 e all'attempt 1 con COMPLETELY_UNREACHABLE)
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#      | pagoPaIntMode         | SYNC                        |
#      | feePolicy             | DELIVERY_MODE               |
#      | paFee                 | 17                          |
#      | vat                   | 10                          |
#    And destinatario
#      | denomination            | Test AR Fail                      |
#      | taxId                   | MNTMRA03M71C615V                  |
#      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
#      | digitalDomicile         | NULL                              |
#      | payment_creditorTaxId   | 77777777777                       |
#      | payment_pagoPaForm      | SI                                |
#      | payment_f24             | NULL                              |
#      | title_payment           | PagoPa_testRestart                |
#      | apply_cost_pagopa       | SI                                |
#      | payment_multy_number    | 1                                 |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Gherkin" <reads> la notifica
#    When viene invocata una richiesta di restart per la notifica appena creata
#    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
#    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                    |
#      | tag             | AUD_NT_UPDATE_COST                                                      |
#      | recIndex        | recIndex=0                                                              |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
#      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun             | auto                                                                     |
#      | tag             | AUD_NT_UPDATE_COST                                                       |
#      | recIndex        | recIndex=0                                                               |
#      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
#      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" al tentativo "REWORK_0"
#    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
#    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
#    And la timeline contiene elementi con la stringa "REWORK_"
#    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
#    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
#    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
#      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
#      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
#      | element3 | REFINEMENT;RECINDEX_0                     |
#    Examples:
#      | reads     |
#      | legge     |
#      | non legge |

  ###Restart dell'attempt 1

  @timelineReworkF3 @checkRestart
  Scenario Outline: [TR3_RESTART_MONODEST_21] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (anche al restart va in OK all'attempt 1)
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
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart
  Scenario Outline: [TR3_RESTART_MONODEST_22] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in KO all'attempt 1)
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
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart
  Scenario Outline: [TR3_RESTART_MONODEST_23] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in KO all'attempt 1 con COMPLETELY_UNREACHABLE)
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart
  Scenario Outline: [TR3_RESTART_MONODEST_24] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e all'attempt 1 (anche al restart va in KO all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
      | digitalDomicile         | NULL                              |
      | payment_creditorTaxId   | 77777777777                       |
      | payment_pagoPaForm      | SI                                |
      | payment_f24             | NULL                              |
      | title_payment           | PagoPa_testRestart                |
      | apply_cost_pagopa       | SI                                |
      | payment_multy_number    | 1                                 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart
  Scenario Outline: [TR3_RESTART_MONODEST_25] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in OK all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
      | digitalDomicile         | NULL                              |
      | payment_creditorTaxId   | 77777777777                       |
      | payment_pagoPaForm      | SI                                |
      | payment_f24             | NULL                              |
      | title_payment           | PagoPa_testRestart                |
      | apply_cost_pagopa       | SI                                |
      | payment_multy_number    | 1                                 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |

  @timelineReworkF3 @checkRestart
  Scenario Outline: [TR3_RESTART_MONODEST_26] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in KO all'attempt 1 con COMPLETELY_UNREACHABLE)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILE_AR |
      | digitalDomicile         | NULL                              |
      | payment_creditorTaxId   | 77777777777                       |
      | payment_pagoPaForm      | SI                                |
      | payment_f24             | NULL                              |
      | title_payment           | PagoPa_testRestart                |
      | apply_cost_pagopa       | SI                                |
      | payment_multy_number    | 1                                 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" <reads> la notifica
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" al tentativo "REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" al tentativo "REWORK_0"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    When vengono recuperati i record relativi agli elementi di timeline affetti dal rework
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    Examples:
      | reads     |
      | legge     |
      | non legge |