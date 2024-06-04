Feature: avanzamento notifiche b2b persona fisica pagamento

  #TODO SONO STATI SPOSTATI IN INVIO DIGITALE SPECIALE....


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