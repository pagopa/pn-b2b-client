Feature: Costi Notifica Fase 5
#SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2686713956/SRS+Costi+di+notifica+-+fase+5+BE#WI-CN-F5-3.3---Creazione-consumer-eventi-di-aggiornamento-costo-su-pn-notification-cost-service
#PST: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2849800311/DRAFT+PST+PN-18622+Costi+Notifica+BE+-+fase+5

  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario: [CNF5_ROBUSTEZZA_API_INSERIMENTO_COSTI] Verifica la robustezza dell'API di inizializzazione dei costi passando una serie di parametri
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | pagoPaIntMode      | SYNC                       |
      | feePolicy          | DELIVERY_MODE              |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it              |
      | physicalAddress_address | Via@ok_RS                 |
      | payment_creditorTaxId   | 77777777777               |
      | payment_pagoPaForm      | SI                        |
      | title_payment           | PaymentCostiNotificaFase5 |
      | apply_cost_pagopa       | SI                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And verifico il comportamento dell'API di inserimento costi passando in input "iun null"
    Then verifico il comportamento dell'API di inserimento costi passando in input "body null"
    And verifico il comportamento dell'API di inserimento costi passando in input "body vuoto"
    And verifico il comportamento dell'API di inserimento costi passando in input "recIndex null"
    And verifico il comportamento dell'API di inserimento costi passando in input "iuv null"
    And verifico il comportamento dell'API di inserimento costi passando in input "applyCost null"
    And verifico il comportamento dell'API di inserimento costi passando in input "iun invalido"
    And verifico il comportamento dell'API di inserimento costi passando in input "iun inesistente"
    And verifico il comportamento dell'API di inserimento costi passando in input "pagamenti vuoti"

  @costiNotificaFase5 @CNF5_FF_ENABLED
  Scenario: [CNF5_ROBUSTEZZA_API_RECUPERO_COSTI] Invocazione dell'api di recupero costi da Pn-PaymentInfo passando input errati
    Given viene generata una nuova notifica
      | subject            | test costi notifica fase 5 |
      | senderDenomination | Comune di palermo          |
      | feePolicy          | DELIVERY_MODE              |
      | vat                | 10                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it              |
      | physicalAddress_address | Via@ok_RS                 |
      | payment_creditorTaxId   | 77777777777               |
      | title_payment           | PaymentCostiNotificaFase5 |
      | payment_pagoPaForm      | SI                        |
      | apply_cost_pagopa       | SI                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_REQUEST"
    And verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"
    And verifico che per l'utente 0 il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente
    And verifico che per il destinatario 0 il record su Pn-NotificationDeliveryCost sia stato inserito e correttamente valorizzato
      | isDeleted | false |
    Then verifico che l'API di recupero costi da Pn-PaymentInfo produca un errore quando viene richiamata passando "creditorTaxId errato"
    And verifico che l'API di recupero costi da Pn-PaymentInfo produca un errore quando viene richiamata passando "noticeCode errato"
    And verifico che l'API di recupero costi da Pn-PaymentInfo produca un errore quando viene richiamata passando "creditorTaxId inesistente"
    And verifico che l'API di recupero costi da Pn-PaymentInfo produca un errore quando viene richiamata passando "noticeCode inesistente"