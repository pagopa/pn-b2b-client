Feature: Correzione timeline fase 3
  #SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2700673102/SRS+Correzione+timeline+-+Fase+3
  #PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/3002826778/PST+-+Correzione+Timeline+-+FASE+3

  ###DECEDUTO, poi restart

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
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
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
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0     |
      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX_0 |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0     |
      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX_0 |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_2] Restart di notifica che va in RETURNED TO SENDER (al restart va in OK all'attempt 0)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_DEC_RESTART_CONS_AR |
      | digitalDomicile         | NULL                       |
      | payment_creditorTaxId   | 77777777777                |
      | payment_pagoPaForm      | SI                         |
      | payment_f24             | NULL                       |
      | title_payment           | PagoPa_testRestart         |
      | apply_cost_pagopa       | SI                         |
      | payment_multy_number    | 1                          |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0     |
      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX_0 |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_3] Restart di notifica che va in RETURNED TO SENDER (al restart va in KO all'attempt 0 e in OK all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_DEC_RESTART_CONS_ATT1_AR |
      | digitalDomicile         | NULL                            |
      | payment_creditorTaxId   | 77777777777                     |
      | payment_pagoPaForm      | SI                              |
      | payment_f24             | NULL                            |
      | title_payment           | PagoPa_testRestart              |
      | apply_cost_pagopa       | SI                              |
      | payment_multy_number    | 1                               |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0     |
      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX_0 |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_4] Restart di notifica che va in RETURNED TO SENDER (al restart va in KO all'attempt 0 e all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_DEC_RESTART_IRR_AR |
      | digitalDomicile         | NULL                      |
      | payment_creditorTaxId   | 77777777777               |
      | payment_pagoPaForm      | SI                        |
      | payment_f24             | NULL                      |
      | title_payment           | PagoPa_testRestart        |
      | apply_cost_pagopa       | SI                        |
      | payment_multy_number    | 1                         |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0     |
      | element2 | ANALOG_WORKFLOW_RECIPIENT_DECEASED;RECINDEX_0 |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |

  ###OK attempt 0, poi restart

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_5] Restart di notifica che va in OK all'attempt 0 (anche al restart va in OK all'attempt 0)
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
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
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
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_6] Restart di notifica che va in OK all'attempt 0 (al restart va in KO all'attempt 0 e OK all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_RESTART_CONS_ATT1_AR |
      | digitalDomicile         | NULL                        |
      | payment_creditorTaxId   | 77777777777                 |
      | payment_pagoPaForm      | SI                          |
      | payment_f24             | NULL                        |
      | title_payment           | PagoPa_testRestart          |
      | apply_cost_pagopa       | SI                          |
      | payment_multy_number    | 1                           |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_7] Restart di notifica che va in OK all'attempt 0 (al restart va in KO all'attempt 0 e KO all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_RESTART_IRR_AR |
      | digitalDomicile         | NULL                  |
      | payment_creditorTaxId   | 77777777777           |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | title_payment           | PagoPa_testRestart    |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_8] Restart di notifica che va in OK all'attempt 0 (al restart va in RETURNED_TO_SENDER)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_RESTART_DEC_AR |
      | digitalDomicile         | NULL                  |
      | payment_creditorTaxId   | 77777777777           |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | title_payment           | PagoPa_testRestart    |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_5_VIEWED] Restart di notifica che va in OK all'attempt 0 (anche al restart va in OK all'attempt 0)
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
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 240 secondi controllando ogni 5 secondi
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_6_VIEWED] Restart di notifica che va in OK all'attempt 0 (al restart va in KO all'attempt 0 e OK all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_RESTART_CONS_ATT1_AR |
      | digitalDomicile         | NULL                        |
      | payment_creditorTaxId   | 77777777777                 |
      | payment_pagoPaForm      | SI                          |
      | payment_f24             | NULL                        |
      | title_payment           | PagoPa_testRestart          |
      | apply_cost_pagopa       | SI                          |
      | payment_multy_number    | 1                           |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_7_VIEWED] Restart di notifica che va in OK all'attempt 0 (al restart va in KO all'attempt 0 e KO all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_RESTART_IRR_AR |
      | digitalDomicile         | NULL                  |
      | payment_creditorTaxId   | 77777777777           |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | title_payment           | PagoPa_testRestart    |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_8_VIEWED] Restart di notifica che va in OK all'attempt 0 (al restart va in RETURNED_TO_SENDER)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK_RESTART_DEC_AR |
      | digitalDomicile         | NULL                  |
      | payment_creditorTaxId   | 77777777777           |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | title_payment           | PagoPa_testRestart    |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                    |
      | tag             | AUD_NT_UPDATE_COST                                                      |
      | recIndex        | recIndex=0                                                              |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0                                    |
      | invalidatedCost | FirstAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  ###OK attempt 1, poi restart

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_9] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (anche al restart va in KO all'attempt 0 e in OK all'attempt 1)
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
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And si verifica che la richiesta di restart effettuata sia in stato "DONE" entro 240 secondi controllando ogni 5 secondi
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_10] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in OK all'attempt 0)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_RESTART_CONS_AR |
      | digitalDomicile         | NULL                          |
      | payment_creditorTaxId   | 77777777777                   |
      | payment_pagoPaForm      | SI                            |
      | payment_f24             | NULL                          |
      | title_payment           | PagoPa_testRestart            |
      | apply_cost_pagopa       | SI                            |
      | payment_multy_number    | 1                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_11] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in KO all'attempt 0 e all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_RESTART_IRR_AR |
      | digitalDomicile         | NULL                         |
      | payment_creditorTaxId   | 77777777777                  |
      | payment_pagoPaForm      | SI                           |
      | payment_f24             | NULL                         |
      | title_payment           | PagoPa_testRestart           |
      | apply_cost_pagopa       | SI                           |
      | payment_multy_number    | 1                            |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
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
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_12] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in RETURNED TO SENDER)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_RESTART_DEC_AR |
      | digitalDomicile         | NULL                         |
      | payment_creditorTaxId   | 77777777777                  |
      | payment_pagoPaForm      | SI                           |
      | payment_f24             | NULL                         |
      | title_payment           | PagoPa_testRestart           |
      | apply_cost_pagopa       | SI                           |
      | payment_multy_number    | 1                            |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_9_VIEWED] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (anche al restart va in KO all'attempt 0 e in OK all'attempt 1)
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
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
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
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 240 secondi controllando ogni 5 secondi
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_10_VIEWED] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in OK all'attempt 0)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_RESTART_CONS_AR |
      | digitalDomicile         | NULL                          |
      | payment_creditorTaxId   | 77777777777                   |
      | payment_pagoPaForm      | SI                            |
      | payment_f24             | NULL                          |
      | title_payment           | PagoPa_testRestart            |
      | apply_cost_pagopa       | SI                            |
      | payment_multy_number    | 1                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
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
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_11_VIEWED] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in KO all'attempt 0 e all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_RESTART_IRR_AR |
      | digitalDomicile         | NULL                         |
      | payment_creditorTaxId   | 77777777777                  |
      | payment_pagoPaForm      | SI                           |
      | payment_f24             | NULL                         |
      | title_payment           | PagoPa_testRestart           |
      | apply_cost_pagopa       | SI                           |
      | payment_multy_number    | 1                            |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
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
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_12_VIEWED] Restart di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in RETURNED TO SENDER)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_RESTART_DEC_AR |
      | digitalDomicile         | NULL                         |
      | payment_creditorTaxId   | 77777777777                  |
      | payment_pagoPaForm      | SI                           |
      | payment_f24             | NULL                         |
      | title_payment           | PagoPa_testRestart           |
      | apply_cost_pagopa       | SI                           |
      | payment_multy_number    | 1                            |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
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
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  ###KO attempt 1, poi restart

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_13] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (anche al restart va in KO all'attempt 0 e all'attempt 1)
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
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
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
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart #TODO RENAME SEQUENCE
  Scenario: [TR3_RESTART_MONODEST_14] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in OK all'attempt 0)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_IRREP_RESTART_1_CONS_AT1_AR |
      | digitalDomicile         | NULL                                 |
      | payment_creditorTaxId   | 77777777777                          |
      | payment_pagoPaForm      | SI                                   |
      | payment_f24             | NULL                                 |
      | title_payment           | PagoPa_testRestart                   |
      | apply_cost_pagopa       | SI                                   |
      | payment_multy_number    | 1                                    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_15] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in KO all'attempt 0 e in OK all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_IRR_RESTART_CONS_ATT1_AR |
      | digitalDomicile         | NULL                                   |
      | payment_creditorTaxId   | 77777777777                            |
      | payment_pagoPaForm      | SI                                     |
      | payment_f24             | NULL                                   |
      | title_payment           | PagoPa_testRestart                     |
      | apply_cost_pagopa       | SI                                     |
      | payment_multy_number    | 1                                      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_16] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in RETURNED TO SENDER)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_IRR_RESTART_DEC_AR |
      | digitalDomicile         | NULL                             |
      | payment_creditorTaxId   | 77777777777                      |
      | payment_pagoPaForm      | SI                               |
      | payment_f24             | NULL                             |
      | title_payment           | PagoPa_testRestart               |
      | apply_cost_pagopa       | SI                               |
      | payment_multy_number    | 1                                |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_13_VIEWED] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (anche al restart va in KO all'attempt 0 e all'attempt 1)
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
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    When viene invocata una richiesta di restart per la notifica appena creata
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
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
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 240 secondi controllando ogni 5 secondi
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart #TODO RENAME SEQUENCE
  Scenario: [TR3_RESTART_MONODEST_14_VIEWED] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in OK all'attempt 0)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_IRREP_RESTART_1_CONS_AT1_AR |
      | digitalDomicile         | NULL                                 |
      | payment_creditorTaxId   | 77777777777                          |
      | payment_pagoPaForm      | SI                                   |
      | payment_f24             | NULL                                 |
      | title_payment           | PagoPa_testRestart                   |
      | apply_cost_pagopa       | SI                                   |
      | payment_multy_number    | 1                                    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
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
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_15_VIEWED] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in KO all'attempt 0 e in OK all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_IRR_RESTART_CONS_ATT1_AR |
      | digitalDomicile         | NULL                                   |
      | payment_creditorTaxId   | 77777777777                            |
      | payment_pagoPaForm      | SI                                     |
      | payment_f24             | NULL                                   |
      | title_payment           | PagoPa_testRestart                     |
      | apply_cost_pagopa       | SI                                     |
      | payment_multy_number    | 1                                      |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
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
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_16_VIEWED] Restart di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in RETURNED TO SENDER)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_IRR_RESTART_DEC_AR |
      | digitalDomicile         | NULL                             |
      | payment_creditorTaxId   | 77777777777                      |
      | payment_pagoPaForm      | SI                               |
      | payment_f24             | NULL                             |
      | title_payment           | PagoPa_testRestart               |
      | apply_cost_pagopa       | SI                               |
      | payment_multy_number    | 1                                |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
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
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_0;RECINDEX_0 |
      | element2 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element3 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  ###Restart dell'attempt 1

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_17] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (anche al restart va in KO all'attempt 0 e in OK all'attempt 1)
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
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
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
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_18] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in KO all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_RESTART_1_IRREP_AR |
      | digitalDomicile         | NULL                             |
      | payment_creditorTaxId   | 77777777777                      |
      | payment_pagoPaForm      | SI                               |
      | payment_f24             | NULL                             |
      | title_payment           | PagoPa_testRestart               |
      | apply_cost_pagopa       | SI                               |
      | payment_multy_number    | 1                                |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_19] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e all'attempt 1 (anche al restart va in KO all'attempt 1)
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
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
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
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_20] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in OK all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_IRREP_RESTART_1_CONS_AT2_AR |
      | digitalDomicile         | NULL                                 |
      | payment_creditorTaxId   | 77777777777                          |
      | payment_pagoPaForm      | SI                                   |
      | payment_f24             | NULL                                 |
      | title_payment           | PagoPa_testRestart                   |
      | apply_cost_pagopa       | SI                                   |
      | payment_multy_number    | 1                                    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1.REWORK_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And la timeline contiene elementi con la stringa "REWORK_"
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_17_VIEWED] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (anche al restart va in OK all'attempt 1)
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
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 240 secondi controllando ogni 5 secondi
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_18_VIEWED] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e in OK all'attempt 1 (al restart va in KO all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DISC_RESTART_1_IRREP_AR |
      | digitalDomicile         | NULL                             |
      | payment_creditorTaxId   | 77777777777                      |
      | payment_pagoPaForm      | SI                               |
      | payment_f24             | NULL                             |
      | title_payment           | PagoPa_testRestart               |
      | apply_cost_pagopa       | SI                               |
      | payment_multy_number    | 1                                |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_19_VIEWED] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e all'attempt 1 (anche al restart va in KO all'attempt 1)
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
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    When viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | recIndex   | reason     | task       |
      |     | ATTEMPT_1 | RECINDEX_0 | reasonTest | TEST-12345 |
    Then si verifica che la richiesta di restart effettuata sia in stato "CREATED" entro 60 secondi controllando ogni 5 secondi
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 130 secondi controllando ogni 5 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And si verifica che la richiesta di restart effettuata sia in stato "READY" entro 240 secondi controllando ogni 5 secondi
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |

  @timelineReworkF3 @checkRestart
  Scenario: [TR3_RESTART_MONODEST_20_VIEWED] Restart all'attempt 1 di notifica che va in KO all'attempt 0 e all'attempt 1 (al restart va in OK all'attempt 1)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | pagoPaIntMode         | SYNC                        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 17                          |
      | vat                   | 10                          |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_IRREP_RESTART_1_CONS_AT2_AR |
      | digitalDomicile         | NULL                                 |
      | payment_creditorTaxId   | 77777777777                          |
      | payment_pagoPaForm      | SI                                   |
      | payment_f24             | NULL                                 |
      | title_payment           | PagoPa_testRestart                   |
      | apply_cost_pagopa       | SI                                   |
      | payment_multy_number    | 1                                    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Gherkin" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    And vengono letti gli eventi fino allo stato della notifica "VIEWED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun             | auto                                                                     |
      | tag             | AUD_NT_UPDATE_COST                                                       |
      | recIndex        | recIndex=0                                                               |
      | phase           | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1                                     |
      | invalidatedCost | SecondAnalogCostEntity(super=AnalogCostEntity(cost=0, productType=null)) |
    And vengono effettuati i controlli sugli elementi invalidati usando la lista "ESTESA"
    Then controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato INVALIDATED
      | element1 | SEND_ANALOG_DOMICILE;ATTEMPT_1;RECINDEX_0 |
      | element2 | REFINEMENT;RECINDEX_0                     |
    And controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato NEW
      | noElementsExpected |  |