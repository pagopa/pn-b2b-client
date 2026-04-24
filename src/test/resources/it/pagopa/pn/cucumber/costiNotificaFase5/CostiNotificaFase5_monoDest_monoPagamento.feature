Feature: Costi Notifica Fase 5
#SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2686713956/SRS+Costi+di+notifica+-+fase+5+BE#WI-CN-F5-3.3---Creazione-consumer-eventi-di-aggiornamento-costo-su-pn-notification-cost-service
#PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2849800311/DRAFT+PST+PN-18622+Costi+Notifica+BE+-+fase+5

  Scenario Outline: [CNF5_MULTI_DESTINATARIO_SEND_SIMPLE_REGISTERED_LETTER_PAGOPA_SYNC] Invio di una notifica multi-destinatario e mono-pagamento PagoPA(sync) che preveda un elemento SEND_SIMPLE_REGISTERED_LETTER
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
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it          |
      | physicalAddress_address | Via@ok_RS             |
      | payment_creditorTaxId   | 77777777777           |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | title_payment           | PagoPa_mono_sync_ssrl |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    And verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    And verifico su DynamoDB la presenza in timeline dell'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=0         |
      | phase    | phase=VALIDATION   |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto               |
      | tag      | AUD_NT_UPDATE_COST |
      | recIndex | recIndex=1         |
      | phase    | phase=VALIDATION   |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 1
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted   | false |
      | productType | RS    |
    Then verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted   | false |
      | productType | RS    |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                |
      | tag      | AUD_NT_UPDATE_COST                  |
      | recIndex | recIndex=0                          |
      | phase    | phase=SEND_SIMPLE_REGISTERED_LETTER |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                                |
      | tag      | AUD_NT_UPDATE_COST                  |
      | recIndex | recIndex=1                          |
      | phase    | phase=SEND_SIMPLE_REGISTERED_LETTER |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 0 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | true        |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 1 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | true        |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    When la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted | true |
    And verifico che per il destinatario 1 il record su Pn-NotificationDeliveryCost sia stato modificato e correttamente valorizzato
      | isDeleted | true |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                         |
      | tag      | AUD_NT_UPDATE_COST           |
      | recIndex | recIndex=0                   |
      | phase    | phase=NOTIFICATION_CANCELLED |
    And verifico la presenza di un audit log su "/aws/ecs/pn-notification-cost-service" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun      | auto                         |
      | tag      | AUD_NT_UPDATE_COST           |
      | recIndex | recIndex=1                   |
      | phase    | phase=NOTIFICATION_CANCELLED |
    Examples:
      | feePolicy     |
      | DELIVERY_MODE |
      | FLAT_RATE     |

  Scenario Outline: MOCK_CONFRONTO_COSTI
    Given imposto lo iun di SharedSteps a "<iun>" e la pa a "Comune_Multi"
    And verifico che i valori restituiti dalle nuove api di recupero costi per l'utente 0 coincidano con quelli restituiti da delivery-push
      | paFee     | 17          |
      | applyCost | <applyCost> |
      | vat       | 10          |
      | feePolicy | <feePolicy> |
    Examples:
      | iun                       | feePolicy     | applyCost |
      | UJGT-PDVN-NLWL-202604-V-1 | DELIVERY_MODE | SI        |
      | LKRD-VNAJ-ZNZD-202604-N-1 | FLAT_RATE     | NO        |
      | ZKMJ-KXKH-ZPQK-202604-G-1 | DELIVERY_MODE | SI        |
      | EDQN-LZPZ-VDLD-202604-M-1 | FLAT_RATE     | NO        |
      | VRAE-TKJG-VDTA-202604-Q-1 | DELIVERY_MODE | SI        |
      | EHTQ-DVMY-LXPA-202604-R-1 | FLAT_RATE     | NO        |
      | DRDR-AWME-LPMX-202604-A-1 | DELIVERY_MODE | SI        |
      | PVRD-TZGK-NQAM-202604-T-1 | FLAT_RATE     | NO        |
      | KYAD-DKEA-NYHR-202604-G-1 | DELIVERY_MODE | SI        |
      | EAPQ-VEUE-UZYX-202604-P-1 | FLAT_RATE     | NO        |
      | MGNE-GHDV-NTNX-202604-Z-1 | DELIVERY_MODE | SI        |
      | DXHV-XQPW-KJYK-202604-R-1 | FLAT_RATE     | NO        |
