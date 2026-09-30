Feature: Costi Notifica Fase 5
#SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2686713956/SRS+Costi+di+notifica+-+fase+5+BE#WI-CN-F5-3.3---Creazione-consumer-eventi-di-aggiornamento-costo-su-pn-notification-cost-service
#PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2849800311/DRAFT+PST+PN-18622+Costi+Notifica+BE+-+fase+5

  @costiNotificaFase5 @CNF5_FF_ENABLED @sada0
  Scenario Outline: [CNF5_MULTI_DESTINATARIO_SEND_ANALOG_DOMICILE_ATTEMPT_0_PAGOPA_SYNC] Invio di una notifica multi-destinatario con pagamento/i PagoPA(sync) che preveda un elemento SEND_ANALOG_DOMICILE_ATTEMPT_0
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | SYNC                       |
      | feePolicy          | <feePolicy>                |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                   |
      | physicalAddress_address      | Via@ok_890             |
      | physicalAddress_municipality | LAGO PATRIA            |
      | physicalAddress_zip          | 80014                  |
      | physicalAddress_province     | NA                     |
      | payment_creditorTaxId        | 77777777777            |
      | payment_pagoPaForm           | SI                     |
      | payment_f24                  | NULL                   |
      | title_payment                | PagoPa_mono_sync_sada0 |
      | apply_cost_pagopa            | <applyCost>            |
      | payment_multy_number         | <paymentNumber>        |
    And destinatario Mario Cucumber e:
      | digitalDomicile              | NULL                   |
      | physicalAddress_address      | Via@ok_890             |
      | physicalAddress_municipality | LAGO PATRIA            |
      | physicalAddress_zip          | 80014                  |
      | physicalAddress_province     | NA                     |
      | payment_creditorTaxId        | 77777777777            |
      | payment_pagoPaForm           | SI                     |
      | payment_f24                  | NULL                   |
      | title_payment                | PagoPa_mono_sync_sada0 |
      | apply_cost_pagopa            | <applyCost>            |
      | payment_multy_number         | <paymentNumber>        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico che per l'utente 0 il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per l'utente 1 il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=0         |
      | phase    | phase=VALIDATION   |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    And verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=1         |
      | phase    | phase=VALIDATION   |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0" per l'utente 1
    Then verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=0                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0 |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=1                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0 |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted        | false           |
      | costoValorizzato | firstAnalogCost |
    And verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted        | false           |
      | costoValorizzato | firstAnalogCost |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 0 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 1 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    Examples:
      | feePolicy     | applyCost | paymentNumber |
      | DELIVERY_MODE | SI        | 1             |
      | FLAT_RATE     | NO        | 1             |
      | DELIVERY_MODE | SI        | 2             |
      | FLAT_RATE     | NO        | 2             |

  @costiNotificaFase5 @CNF5_FF_ENABLED @sada0
  Scenario Outline: [CNF5_MULTI_DESTINATARIO_SEND_ANALOG_DOMICILE_ATTEMPT_0_PAGOPA_ASYNC_MONO_PAY] Invio di una notifica multi-destinatario con pagamento singolo PagoPA(async) che preveda un elemento SEND_ANALOG_DOMICILE_ATTEMPT_0
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Cucumber" con CF "FRMTTR76M06B715E"
    And viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | ASYNC                      |
      | feePolicy          | <feePolicy>                |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                    |
      | physicalAddress_address      | Via@ok_890              |
      | physicalAddress_municipality | LAGO PATRIA             |
      | physicalAddress_zip          | 80014                   |
      | physicalAddress_province     | NA                      |
      | payment_creditorTaxId        | 77777777777             |
      | payment_pagoPaForm           | SI                      |
      | payment_f24                  | NULL                    |
      | title_payment                | PagoPa_mono_async_sada0 |
      | apply_cost_pagopa            | <applyCost>             |
      | payment_multy_number         | 1                       |
    And destinatario Mario Cucumber e:
      | digitalDomicile              | NULL                    |
      | physicalAddress_address      | Via@ok_890              |
      | physicalAddress_municipality | LAGO PATRIA             |
      | physicalAddress_zip          | 80014                   |
      | physicalAddress_province     | NA                      |
      | payment_creditorTaxId        | 77777777777             |
      | payment_pagoPaForm           | SI                      |
      | payment_f24                  | NULL                    |
      | title_payment                | PagoPa_mono_async_sada0 |
      | apply_cost_pagopa            | <applyCost>             |
      | payment_multy_number         | 1                       |
    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 0 per il suo pagamento alla posizione 0
    And al destinatario 1 viene associato lo iuv creato mediante partita debitoria alla posizione 1 per il suo pagamento alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico che per l'utente 0 il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per l'utente 1 il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=0         |
      | phase    | phase=VALIDATION   |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=1         |
      | phase    | phase=VALIDATION   |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    And verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0" per l'utente 1
    Then verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=0                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0 |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=1                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0 |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted        | false           |
      | costoValorizzato | firstAnalogCost |
    And verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted        | false           |
      | costoValorizzato | firstAnalogCost |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 0 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 1 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    Examples:
      | feePolicy     | applyCost |
      | DELIVERY_MODE | SI        |
      | FLAT_RATE     | NO        |

  @costiNotificaFase5 @CNF5_FF_ENABLED @sada0
  Scenario Outline: [CNF5_MULTI_DESTINATARIO_SEND_ANALOG_DOMICILE_ATTEMPT_0_PAGOPA_ASYNC_MULTI_PAY] Invio di una notifica multi-destinatario con due pagamenti PagoPA(async) che preveda un elemento SEND_ANALOG_DOMICILE_ATTEMPT_0
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Cucumber" con CF "FRMTTR76M06B715E"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Cucumber" con CF "FRMTTR76M06B715E"
    And viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | ASYNC                      |
      | feePolicy          | <feePolicy>                |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@ok_890               |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_multi_async_sada0 |
      | apply_cost_pagopa            | <applyCost>              |
      | payment_multy_number         | 2                        |
    And destinatario Mario Cucumber e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@ok_890               |
      | physicalAddress_municipality | LAGO PATRIA              |
      | physicalAddress_zip          | 80014                    |
      | physicalAddress_province     | NA                       |
      | payment_creditorTaxId        | 77777777777              |
      | payment_pagoPaForm           | SI                       |
      | payment_f24                  | NULL                     |
      | title_payment                | PagoPa_multi_async_sada0 |
      | apply_cost_pagopa            | <applyCost>              |
      | payment_multy_number         | 2                        |
    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 0 per il suo pagamento alla posizione 0
    And al destinatario 0 viene associato lo iuv creato mediante partita debitoria alla posizione 1 per il suo pagamento alla posizione 1
    And al destinatario 1 viene associato lo iuv creato mediante partita debitoria alla posizione 2 per il suo pagamento alla posizione 0
    And al destinatario 1 viene associato lo iuv creato mediante partita debitoria alla posizione 3 per il suo pagamento alla posizione 1
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico che per l'utente 0 il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per l'utente 1 il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=0         |
      | phase    | phase=VALIDATION   |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=1         |
      | phase    | phase=VALIDATION   |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    And verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0" per l'utente 1
    Then verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=0                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0 |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=1                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0 |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted        | false           |
      | costoValorizzato | firstAnalogCost |
    And verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted        | false           |
      | costoValorizzato | firstAnalogCost |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 0 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 1 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    Examples:
      | feePolicy     | applyCost |
      | DELIVERY_MODE | SI        |
      | FLAT_RATE     | NO        |

  @costiNotificaFase5 @CNF5_FF_ENABLED @sada0
  Scenario Outline: [CNF5_MULTI_DESTINATARIO_SEND_ANALOG_DOMICILE_ATTEMPT_0_F24] Invio di una notifica multi-destinatario con pagamento/i F24 che preveda un elemento SEND_ANALOG_DOMICILE_ATTEMPT_0
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | NONE                       |
      | feePolicy          | <feePolicy>                |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                |
      | physicalAddress_address      | Via@ok_890          |
      | physicalAddress_municipality | LAGO PATRIA         |
      | physicalAddress_zip          | 80014               |
      | physicalAddress_province     | NA                  |
      | payment_creditorTaxId        | 77777777777         |
      | payment_pagoPaForm           | NULL                |
      | apply_cost_pagopa            | NO                  |
      | payment_f24                  | <paymentF24>        |
      | title_payment                | f24_mono_sync_sada0 |
      | apply_cost_f24               | <applyCost>         |
      | payment_multy_number         | <paymentNumber>     |
    And destinatario Mario Cucumber e:
      | digitalDomicile              | NULL                |
      | physicalAddress_address      | Via@ok_890          |
      | physicalAddress_municipality | LAGO PATRIA         |
      | physicalAddress_zip          | 80014               |
      | physicalAddress_province     | NA                  |
      | payment_creditorTaxId        | 77777777777         |
      | payment_pagoPaForm           | NULL                |
      | apply_cost_pagopa            | NO                  |
      | payment_f24                  | <paymentF24>        |
      | title_payment                | f24_mono_sync_sada0 |
      | apply_cost_f24               | <applyCost>         |
      | payment_multy_number         | <paymentNumber>     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=0         |
      | phase    | phase=VALIDATION   |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=1         |
      | phase    | phase=VALIDATION   |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    And verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0" per l'utente 1
    Then verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=0                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0 |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                 |
      | tag      | AUD_NT_UPDATE_COST                   |
      | recIndex | recIndex=1                           |
      | phase    | phase=SEND_ANALOG_DOMICILE_ATTEMPT_0 |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted        | false           |
      | costoValorizzato | firstAnalogCost |
    And verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted        | false           |
      | costoValorizzato | firstAnalogCost |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 0 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 1 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    Examples:
      | feePolicy     | applyCost | paymentNumber | paymentF24           |
      | DELIVERY_MODE | SI        | 1             | PAYMENT_F24_STANDARD |
      | FLAT_RATE     | NO        | 1             | PAYMENT_F24_FLAT     |
      | DELIVERY_MODE | SI        | 2             | PAYMENT_F24_STANDARD |
      | FLAT_RATE     | NO        | 2             | PAYMENT_F24_FLAT     |