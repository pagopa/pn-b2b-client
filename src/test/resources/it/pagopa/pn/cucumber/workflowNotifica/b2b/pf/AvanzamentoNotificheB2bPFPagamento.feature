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
    And destinatario Mario Gherkin e:
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
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | apply_cost_pagopa  | SI               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
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
    

  @workflowDigitale
  Scenario: [B2B-PA-PAY_8] Comunicazione da parte della PA dell'avvenuto pagamento di tipo PagoPA  7741
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | NULL             |
      | apply_cost_pagopa  | SI               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #Questa API è a disposizione della Pubblica Amministrazione per inviare eventi di chiusura di una o più posizioni debitorie di tipo PagoPA.
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica


  @workflowDigitale
  Scenario: [B2B-PA-PAY_9] Verifica restituzione data di visualizzazione a quella del NOTIFICATION_VIEWED_CREATION_REQUEST per la chiamata retrieveNotificationPrice - PN-8970
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@fail-Discovery_890 |
      | payment_pagoPaForm      | SI                     |
      | payment_f24             | NULL                   |
      | apply_cost_pagopa       | SI                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then "Mario Gherkin" legge la notifica
    Then viene verificato data corretta del destinatario 0


    Scenario: [B2B-PA-PAY_CHECKOUT_1] creazione posizione debitoria per pagamento da checkout con noticeCode con restituzione errore 422 - PN-9128
    Given si richiama checkout con restituzione errore
      | fiscalCode  | 77777777777                              |
      | noticeCode  | 302000194484209682                       |
      | amount      | 10                                       |
      | description | PIPPO                                    |
      | companyName | PLUTO                                    |
      | returnUrl   | https://www.pagopa.gov.it/it/assistenza/ |
      Then l'operazione ha prodotto un errore con status code "422"


  Scenario: [B2B-PA-PAY_CHECKOUT_2] invio notifica con posizione debiotoria collegata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@ok_890  |
      | payment_creditorTaxId   | 77777777777 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Given si richiama checkout con dati:
      | amount      | 10                                       |
      | description | PIPPO                                    |
      | companyName | PLUTO                                    |
      | returnUrl   | https://www.pagopa.gov.it/it/assistenza/ |