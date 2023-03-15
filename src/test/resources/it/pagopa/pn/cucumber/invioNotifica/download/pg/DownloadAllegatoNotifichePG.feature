Feature: Download da persona giuridica
  @testLigth
  Scenario: [B2B-DOWN-PG_1] download documento notificato_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "NOTIFICA"
    Then il download si conclude correttamente

  @testLigth
  Scenario: [B2B-DOWN-PG_2] download documento pagopa_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente

  Scenario: [B2B-DOWN-PG_3] download documento f24_standard_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Gherkin spa e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI   |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente

  Scenario: [B2B-DOWN-PG_4] download documento notificato_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | document           | SI                          |
    And destinatario Cucumber Society
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "NOTIFICA"
    Then il download si conclude correttamente


  Scenario: [B2B-DOWN-PG_5] download documento pagopa_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Cucumber Society e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente


  Scenario: [B2B-DOWN-PG_6] download documento f24_standard_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Cucumber Society e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI   |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente
