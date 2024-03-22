Feature: verifica feature pagamenti multipli con deleghe

  @pagamentiMultipli @deleghe2
  Scenario: [B2B-PA-PAY_MULTI_PG_69] Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante e Delegato scaricano correttamenta l'avviso pagoPA e F24
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm   | SI                        |
      | payment_f24          | PAYMENT_F24_STANDARD      |
      | title_payment        | F24_STANDARD_GHERKING_SRL |
      | apply_cost_pagopa    | SI                        |
      | apply_cost_f24       | SI                        |
      | payment_multy_number | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "CucumberSpa" con delega
    And "GherkinSrl" tenta il recupero dell'allegato "PAGOPA"
    And il download non ha prodotto errori
    And l'allegato "F24" può essere correttamente recuperato da "CucumberSpa" con delega
    And "GherkinSrl" tenta il recupero dell'allegato "F24"
    And il download non ha prodotto errori


  #70 Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso1 (Stesso avviso - pagoPA)
  @pagamentiMultipli @deleghe2
  Scenario: [B2B-PA-PAY_MULTI_PG_70] Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante e Delegato scaricano correttamenta l'avviso pagoPA
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm   | SI   |
      | payment_f24          | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "CucumberSpa" con delega
    And "GherkinSrl" tenta il recupero dell'allegato "PAGOPA"
    And il download non ha prodotto errori
