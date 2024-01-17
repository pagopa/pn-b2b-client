Feature: avanzamento notifiche b2b persona fisica pagamento

  @workflowDigitale
  Scenario: [B2B-PA-PAY_1] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI   |
      | payment_f24        | NULL |
      | apply_cost_pagopa  | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    Then vengono verificati costo = "100" e data di perfezionamento della notifica

  @workflowDigitale
  Scenario: [B2B-PA-PAY_2] Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario
      | taxId              | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI               |
      | payment_f24        | NULL             |
      | apply_cost_pagopa  | SI               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @workflowDigitale
  Scenario: [B2B-PA-PAY_3] Invio notifica FLAT e verifica amount
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | FLAT_RATE                   |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica

  @workflowDigitale
  Scenario: [B2B-PA-PAY_4] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario
      | denomination       | Ada              |
      | taxId              | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI               |
      | apply_cost_pagopa  | SI               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica

  @ignore
  Scenario: [B2B-PA-PAY_5] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | apply_cost_pagopa  | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then il modello f24 viene pagato correttamente
    And si attende il corretto pagamento della notifica

  @workflowDigitale
  Scenario: [B2B-PA-PAY_6] Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | apply_cost_pagopa  | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @ignore
  Scenario: [B2B-PA-PAY_7] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm    | SI          |
      | payment_f24           | NULL        |
      | apply_cost_pagopa     | SI          |
      | payment_creditorTaxId | 77777777777 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica

  @workflowDigitale
  Scenario: [B2B-PA-PAY_8] Comunicazione da parte della PA dell'avvenuto pagamento di tipo PagoPA  7741
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario
      | denomination       | Ada              |
      | taxId              | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI               |
      | payment_f24        | NULL             |
      | apply_cost_pagopa  | SI               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #Questa API è a disposizione della Pubblica Amministrazione per inviare eventi di chiusura di una o più posizioni debitorie di tipo PagoPA.
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica

