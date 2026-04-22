Feature: Costi Notifica Fase 5
#SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2686713956/SRS+Costi+di+notifica+-+fase+5+BE#WI-CN-F5-3.3---Creazione-consumer-eventi-di-aggiornamento-costo-su-pn-notification-cost-service
#PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2849800311/DRAFT+PST+PN-18622+Costi+Notifica+BE+-+fase+5

  @costiNotificaFase5 @CNF5_FF_ENABLED @sada1
  Scenario Outline: [CNF5_MONO_DESTINATARIO_SEND_ANALOG_DOMICILE_ATTEMPT_1_PAGOPA_SYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(sync) che preveda un elemento SEND_ANALOG_DOMICILE_ATTEMPT_1
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | SYNC                       |
      | feePolicy          | <feePolicy>                |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                   |
      | physicalAddress_address      | Via@FAIL-Discovery_AR  |
      | physicalAddress_municipality | NAPOLI                 |
      | physicalAddress_province     | NA                     |
      | physicalAddress_zip          | 80124                  |
      | payment_creditorTaxId        | 77777777777            |
      | payment_pagoPaForm           | SI                     |
      | payment_f24                  | NULL                   |
      | title_payment                | PagoPa_mono_sync_sada1 |
      | apply_cost_pagopa            | <applyCost>            |
      | payment_multy_number         | <paymentNumber>        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    And verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=0         |
      | phase    | phase=VALIDATION   |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted   | false |
      | productType | AR    |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=0                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1 |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 0 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
#    When la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
#    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
#      | isDeleted | true |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
#      | iun      | auto                         |
#      | tag      | AUD_NT_UPDATE_COST           |
#      | recIndex | recIndex=0                   |
#      | phase    | phase=NOTIFICATION_CANCELLED |
    Examples:
      | feePolicy     | applyCost | paymentNumber |
      | DELIVERY_MODE | SI        | 1             |
      | FLAT_RATE     | NO        | 1             |
      | DELIVERY_MODE | SI        | 3             |
      | FLAT_RATE     | NO        | 3             |

  @costiNotificaFase5 @CNF5_FF_ENABLED @sada1
  Scenario Outline: [CNF5_MONO_DESTINATARIO_SEND_ANALOG_DOMICILE_ATTEMPT_1_PAGOPA_ASYNC] Invio di una notifica mono-destinatario con pagamento/i PagoPA(async) che preveda un elemento SEND_ANALOG_DOMICILE_ATTEMPT_1
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | ASYNC                      |
      | feePolicy          | <feePolicy>                |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                    |
      | physicalAddress_address      | Via@FAIL-Discovery_AR   |
      | physicalAddress_municipality | NAPOLI                  |
      | physicalAddress_province     | NA                      |
      | physicalAddress_zip          | 80124                   |
      | payment_creditorTaxId        | 77777777777             |
      | payment_pagoPaForm           | SI                      |
      | payment_f24                  | NULL                    |
      | title_payment                | PagoPa_mono_async_sada1 |
      | apply_cost_pagopa            | <applyCost>             |
      | payment_multy_number         | <paymentNumber>         |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    And verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=0         |
      | phase    | phase=VALIDATION   |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted   | false |
      | productType | AR    |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=0                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1 |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 0 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
#    When la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
#    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
#      | isDeleted | true |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
#      | iun      | auto                         |
#      | tag      | AUD_NT_UPDATE_COST           |
#      | recIndex | recIndex=0                   |
#      | phase    | phase=NOTIFICATION_CANCELLED |
    Examples:
      | feePolicy     | applyCost | paymentNumber |
      | DELIVERY_MODE | SI        | 1             |
      | FLAT_RATE     | NO        | 1             |
      | DELIVERY_MODE | SI        | 3             |
      | FLAT_RATE     | NO        | 3             |

  @costiNotificaFase5 @CNF5_FF_ENABLED @sada1
  Scenario Outline: [CNF5_MONO_DESTINATARIO_SEND_ANALOG_DOMICILE_ATTEMPT_1_F24] Invio di una notifica mono-destinatario con pagamento/i F24 che preveda un elemento SEND_ANALOG_DOMICILE_ATTEMPT_1
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | SYNC                       |
      | feePolicy          | <feePolicy>                |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                  |
      | physicalAddress_address      | Via@FAIL-Discovery_AR |
      | physicalAddress_municipality | NAPOLI                |
      | physicalAddress_province     | NA                    |
      | physicalAddress_zip          | 80124                 |
      | payment_creditorTaxId        | 77777777777           |
      | payment_f24                  | <paymentF24>          |
      | title_payment                | f24_mono_sync_sada1   |
      | apply_cost_f24               | <applyCost>           |
      | payment_multy_number         | <paymentNumber>       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    And verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=0         |
      | phase    | phase=VALIDATION   |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted   | false |
      | productType | AR    |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=0                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_1 |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 0 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
#    When la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
#    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
#      | isDeleted | true |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
#      | iun      | auto                         |
#      | tag      | AUD_NT_UPDATE_COST           |
#      | recIndex | recIndex=0                   |
#      | phase    | phase=NOTIFICATION_CANCELLED |
    Examples:
      | feePolicy     | applyCost | paymentNumber | paymentF24           |
      | DELIVERY_MODE | SI        | 1             | PAYMENT_F24_STANDARD |
      | FLAT_RATE     | NO        | 1             | PAYMENT_F24_FLAT     |
      | DELIVERY_MODE | SI        | 3             | PAYMENT_F24_STANDARD |
      | FLAT_RATE     | NO        | 3             | PAYMENT_F24_FLAT     |
