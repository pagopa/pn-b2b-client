Feature: avanzamento b2b persona giuridica pagamento


  Scenario: [B2B-PA-PG-PAY_1] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario CucumberSpa e:
      | payment_pagoPaForm | SI   |
      | payment_f24        | NULL |
      | apply_cost_pagopa  | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "CucumberSpa" legge la notifica ricevuta
    Then vengono verificati costo = "100" e data di perfezionamento della notifica

  Scenario: [B2B-PA-PG-PAY_2] Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario CucumberSpa e:
      | payment_pagoPaForm | SI   |
      | payment_f24        | NULL |
      | apply_cost_pagopa  | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  Scenario: [B2B-PA-PG-PAY_3] Invio notifica FLAT e verifica amount
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | FLAT_RATE                   |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica


  Scenario: [B2B-PA-PG-PAY_4] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario CucumberSpa e:
      | payment_pagoPaForm | SI   |
      | payment_f24        | NULL |
      | apply_cost_pagopa  | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    Then vengono verificati costo = "100" e data di perfezionamento della notifica


  Scenario: [B2B-PA-PG-PAY_6] Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Cucumber srl e:
      | payment_pagoPaForm | SI   |
      | payment_f24        | NULL |
      | apply_cost_pagopa  | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica



