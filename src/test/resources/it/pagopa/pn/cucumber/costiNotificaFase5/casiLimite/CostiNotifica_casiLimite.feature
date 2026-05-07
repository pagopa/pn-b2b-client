Feature: Costi Notifica Fase 5
#SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2686713956/SRS+Costi+di+notifica+-+fase+5+BE#WI-CN-F5-3.3---Creazione-consumer-eventi-di-aggiornamento-costo-su-pn-notification-cost-service
#PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2849800311/DRAFT+PST+PN-18622+Costi+Notifica+BE+-+fase+5

  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario: [CNF5_MONO_DESTINATARIO_REFUSED] Invio di una notifica mono-destinatario e mono-pagamento che va in REFUSED a seguito dell'invio
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | payment                 | NULL                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then verifico che su DynamoDB non è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che su DynamoDB non è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"

  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario: [CNF5_ANNULLAMENTO_IMMEDIATO] Invio di una notifica mono-destinatario e mono-pagamento e immediato annullamento di essa
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | SYNC                       |
      | feePolicy          | DELIVERY_MODE              |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it                  |
      | physicalAddress_address | Via@ok_RS                     |
      | payment_creditorTaxId   | 77777777777                   |
      | payment_pagoPaForm      | SI                            |
      | payment_f24             | NULL                          |
      | title_payment           | PagoPa_mono_sync_annullamento |
      | apply_cost_pagopa       | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    Then verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico che per l'utente 0 il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=0         |
      | phase    | phase=VALIDATION   |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                         |
      | tag      | AUD_NT_UPDATE_COST           |
      | recIndex | recIndex=0                   |
      | phase    | phase=NOTIFICATION_CANCELLED |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted | true |

  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario Outline: [CNF5_ANNULLAMENTO_POST_AGGIORNAMENTO_COSTI] Invio di una notifica mono-destinatario con pagamento/i PagoPA(sync) che preveda un elemento SEND_SIMPLE_REGISTERED_LETTER e successivo annullamento e azzeramento dei costi
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | SYNC                       |
      | feePolicy          | <feePolicy>                |
      | paFee              | 17                         |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it          |
      | physicalAddress_address | Via@ok_RS             |
      | payment_creditorTaxId   | 77777777777           |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | title_payment           | PagoPa_mono_sync_ssrl |
      | apply_cost_pagopa       | <applyCost>           |
      | payment_multy_number    | 1                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico che per l'utente 0 il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=0         |
      | phase    | phase=VALIDATION   |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    Then verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                |
      | tag      | AUD_NT_UPDATE_COST                  |
      | recIndex | recIndex=0                          |
      | phase    | phase=SEND_SIMPLE_REGISTERED_LETTER |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted        | false                      |
      | costoValorizzato | simpleRegisteredLetterCost |
      | productType      | RS                         |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 0 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    When la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                         |
      | tag      | AUD_NT_UPDATE_COST           |
      | recIndex | recIndex=0                   |
      | phase    | phase=NOTIFICATION_CANCELLED |
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted | true |
    Examples:
      | feePolicy     | applyCost |
      | DELIVERY_MODE | SI        |
      | FLAT_RATE     | NO        |