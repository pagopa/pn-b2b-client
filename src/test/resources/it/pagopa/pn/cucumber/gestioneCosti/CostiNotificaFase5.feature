Feature: Costi Notifica Fase 5
#SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2686713956/SRS+Costi+di+notifica+-+fase+5+BE#WI-CN-F5-3.3---Creazione-consumer-eventi-di-aggiornamento-costo-su-pn-notification-cost-service
#PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2849800311/DRAFT+PST+PN-18622+Costi+Notifica+BE+-+fase+5

  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario Outline: [CNF5_MONO_DESTINATARIO_SEND_SIMPLE_REGISTERED_LETTER] Invio di una notifica mono-destinatario e mono-pagamento che preveda un elemento SEND_SIMPLE_REGISTERED_LETTER
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | feePolicy          | <notificationFeePolicy>    |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it              |
      | physicalAddress_address | Via@ok_RS                 |
      | payment_creditorTaxId   | 77777777777               |
      | payment_pagoPaForm      | <paymentPagoPa>           |
      | payment_f24             | <paymentF24>              |
      | title_payment           | PaymentCostiNotificaFase5 |
#      | apply_cost_pagopa       | <applyCostPagoPa>         |
#      | apply_cost_f24          | <applyCostF24>            |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
#    Given imposto lo iun di SharedSteps a "RDPE-VETJ-PVWM-202604-V-1" e la pa a "Comune_Multi"
#    Then verifico su DynamoDB il mancato inserimento in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted             | false                   |
      | baseCost              | TODO                    |
      | notificationFeePolicy | <notificationFeePolicy> |
      | vat                   | 10                      |
      | PagoPaIntMode         | <pagoPaIntMode>         |
    And verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted       | false                |
      | productType     | TODO: RS/RIS         |
      | firstAnalogCost | TODO: costoImpostato |
    And verifico la presenza di audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_UPDATECOST"
    When la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted | true |
    And verifico la presenza di audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_UPDATECOST"
    Examples:
      | pagoPaIntMode | notificationFeePolicy | paymentPagoPa | paymentF24           | applyCostPagoPa | applyCostF24 |
#      | SYNC          | DELIVERY_MODE         | SI            | NULL                 | SI              | NO           |
#      | ASYNC         | DELIVERY_MODE         | SI            | NULL                 | SI              | NO           |
#      | NONE          | DELIVERY_MODE         | NULL          | PAYMENT_F24_STANDARD | NO              | SI           |
      | SYNC          | FLAT_RATE             | SI            | NULL                 | NULL            | NO           |
      | ASYNC         | FLAT_RATE             | SI            | NULL                 | NULL            | NO           |
      | NONE          | FLAT_RATE             | NULL          | PAYMENT_F24_STANDARD | NO              | NULL         |

  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario Outline: [CNF5_MONO_DESTINATARIO_SEND_ANALOG_DOMICILE_ATTEMPT_0] Invio di una notifica mono-destinatario e mono-pagamento che preveda un elemento SEND_ANALOG_DOMICILE al primo tentativo
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | feePolicy          | <notificationFeePolicy>    |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@ok_AR                 |
      | payment_creditorTaxId   | 77777777777               |
      | payment_pagoPaForm      | <paymentPagoPa>           |
      | payment_f24             | <paymentF24>              |
      | title_payment           | PaymentCostiNotificaFase5 |
      | apply_cost_pagopa       | <applyCostPagoPa>         |
      | apply_cost_f24          | <applyCostF24>            |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted             | false                   |
      | baseCost              | TODO                    |
      | notificationFeePolicy | <notificationFeePolicy> |
      | vat                   | 10                      |
      | PagoPaIntMode         | <pagoPaIntMode>         |
    And verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted       | false                |
      | productType     | TODO: 890/RIR/AR     |
      | firstAnalogCost | TODO: costoImpostato |
    And verifico la presenza di audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_UPDATECOST"
    When la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted | true |
    And verifico la presenza di audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_UPDATECOST"
    Examples:
      | pagoPaIntMode | notificationFeePolicy | paymentPagoPa | paymentF24           | applyCostPagoPa | applyCostF24 |
      | SYNC          | DELIVERY_MODE         | SI            | NULL                 | SI              | NO           |
      | SYNC          | FLAT_RATE             | SI            | NULL                 | SI              | NULL         |
      | ASYNC         | DELIVERY_MODE         | SI            | NULL                 | SI              | NO           |
      | ASYNC         | FLAT_RATE             | SI            | NULL                 | SI              | NULL         |
      | NONE          | DELIVERY_MODE         | NULL          | PAYMENT_F24_STANDARD | NO              | SI           |
      | NONE          | FLAT_RATE             | NULL          | PAYMENT_F24_STANDARD | NO              | NULL         |

  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario Outline: [CNF5_MONO_DESTINATARIO_SEND_ANALOG_DOMICILE_ATTEMPT_1] Invio di una notifica mono-destinatario e mono-pagamento che preveda un elemento SEND_ANALOG_DOMICILE al primo tentativo
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | feePolicy          | <notificationFeePolicy>    |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-IRREPERIBILE_890 |
      | payment_creditorTaxId   | 77777777777               |
      | payment_pagoPaForm      | <paymentPagoPa>           |
      | payment_f24             | <paymentF24>              |
      | title_payment           | PaymentCostiNotificaFase5 |
      | apply_cost_pagopa       | <applyCostPagoPa>         |
      | apply_cost_f24          | <applyCostF24>            |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted             | false                   |
      | notificationFeePolicy | <notificationFeePolicy> |
      | vat                   | 10                      |
      | PagoPaIntMode         | <pagoPaIntMode>         |
    And verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted       | false                |
      | productType     | TODO: 890/RIR/AR     |
      | firstAnalogCost | TODO: costoImpostato |
    And verifico la presenza di audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_UPDATECOST"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted       | false                |
      | productType     | TODO: 890/RIR/AR     |
      | firstAnalogCost | TODO: costoImpostato |
    And verifico la presenza di audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_UPDATECOST"
    When la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted | true |
    And verifico la presenza di audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_UPDATECOST"
    Examples:
      | pagoPaIntMode | notificationFeePolicy | paymentPagoPa | paymentF24           | applyCostPagoPa | applyCostF24 |
      | SYNC          | DELIVERY_MODE         | SI            | NULL                 | SI              | NO           |
      | SYNC          | FLAT_RATE             | SI            | NULL                 | SI              | NULL         |
      | ASYNC         | DELIVERY_MODE         | SI            | NULL                 | SI              | NO           |
      | ASYNC         | FLAT_RATE             | SI            | NULL                 | SI              | NULL         |
      | NONE          | DELIVERY_MODE         | NULL          | PAYMENT_F24_STANDARD | NO              | SI           |
      | NONE          | FLAT_RATE             | NULL          | PAYMENT_F24_STANDARD | NO              | NULL         |

  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario Outline: [CNF5_MONO_DESTINATARIO_ANNULLAMENTO] Invio di una notifica mono-destinatario e mono-pagamento che viene successivamente annullata
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | feePolicy          | <notificationFeePolicy>    |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | <physicalAddress>         |
      | payment_creditorTaxId   | 77777777777               |
      | payment_pagoPaForm      | <paymentPagoPa>           |
      | payment_f24             | <paymentF24>              |
      | title_payment           | PaymentCostiNotificaFase5 |
      | apply_cost_pagopa       | <applyCostPagoPa>         |
      | apply_cost_f24          | <applyCostF24>            |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted             | false                   |
      | baseCost              | TODO                    |
      | notificationFeePolicy | <notificationFeePolicy> |
      | vat                   | 10                      |
      | PagoPaIntMode         | <pagoPaIntMode>         |
    And verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted       | false                |
      | productType     | TODO: 890/RIR/AR     |
      | firstAnalogCost | TODO: costoImpostato |
    And verifico la presenza di audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_UPDATECOST"
    When la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted | true |
    And verifico la presenza di audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_UPDATECOST"
    Examples:
      | pagoPaIntMode | notificationFeePolicy | physicalAddress           |
      | SYNC          | DELIVERY_MODE         | Via@ok_RS                 |
      | SYNC          | DELIVERY_MODE         | Via@ok_AR                 |
      | SYNC          | DELIVERY_MODE         | Via@FAIL-IRREPERIBILE_890 |


  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario: [CNF5_MONO_DESTINATARIO_ANNULLAMENTO] Invio di una notifica mono-destinatario e mono-pagamento e immediato annullamento di essa
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | feePolicy          | <notificationFeePolicy>    |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it              |
      | physicalAddress_address | Via@ok_RS                 |
      | payment_creditorTaxId   | 77777777777               |
      | payment_pagoPaForm      | <paymentPagoPa>           |
      | payment_f24             | <paymentF24>              |
      | title_payment           | PaymentCostiNotificaFase5 |
      | apply_cost_pagopa       | <applyCostPagoPa>         |
      | apply_cost_f24          | <applyCostF24>            |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    Then verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | baseCost              | 0                       |
      | notificationFeePolicy | <notificationFeePolicy> |
      | PagoPaIntMode         | <pagoPaIntMode>         |
      | isDeleted             | true                    |
    And verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico la presenza di audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_UPDATECOST"

  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario: [CNF5_MONO_DESTINATARIO_REFUSED] Invio di una notifica mono-destinatario e mono-pagamento che va in REFUSED a seguito dell'invio
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber |
#      | senderDenomination | Comune di milano            |
#      | feePolicy          | DELIVERY_MODE               |
#      | pagoPaIntMode      | ASYNC                       |
#      | paFee              | 10                          |
#    And destinatario Mario Gherkin e:
#      | digitalDomicile         | NULL                  |
#      | physicalAddress_address | Via@FAIL-Discovery_AR |
#      | payment                 | NULL                  |
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Given imposto lo iun di SharedSteps a "ZTUN-EZMJ-LPGZ-202604-U-1" e la pa a "Comune_Multi"
    Then verifico su DynamoDB il mancato inserimento in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    Then verifico su DynamoDB il mancato inserimento in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"

  Scenario: TODO NotificationDeliveryCost
    #notifica di test
#    Given imposto lo iun di SharedSteps a "XKQL-LUYR-PDRZ-202604-A-1" e la pa a "Comune_Multi"
#    And verifico su DynamoDB la presenza in timeline dell'elemento "REQUEST_ACCEPTED"
    #test, isDeleted false
    Given imposto lo iun di SharedSteps a "EHTA-ZPUE-HPGN-202604-W-1" e la pa a "Comune_Multi"
    #test, isDeleted true
#    Given imposto lo iun di SharedSteps a "KWPM-LDKZ-KXUQ-202603-K-1" e la pa a "Comune_Multi"
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | iun | auto |

  Scenario: TEST_CLOUDWATCH
    #12:28
    And verifico la presenza di audit log su "/aws/ecs/pn-delivery" negli ultimi 15 minuti riportanti il messaggio "AUD_NT_SEARCH_RCP"