Feature: avanzamento notifiche asincrone b2b PG - controllo costi

  @Async
  Scenario: [B2B_ASYNC_1_PG] Notifica mono PG-Caso creazione Notifica di tipo non ASYNC, validazione non prevista
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | SYNC                        |
      | paFee              | NULL                        |
    And destinatario Cucumber Society e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm    | SI                 |
      | payment_f24flatRate   | NULL               |
      | payment_f24standard   | NULL               |
      | apply_cost_pagopa     | SI                 |
      | payment_multy_number  | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED


  @Async @ignore
  Scenario: [B2B_ASYNC_2_PG] Notifica mono PG-Rifiuto caso notifiche che riportano l’indicazione di modalità asincrona di integrazione al cui interno risultano avvisi con pagamento già effettuato
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Gherkin Analogic" con Piva "00749900049"
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario Gherkin Analogic e:
      | payment_creditorTaxId | 77777777777      |
      | payment_pagoPaForm    | SI                 |
      | payment_f24flatRate   | NULL               |
      | payment_f24standard   | NULL               |
      | apply_cost_pagopa     | SI                 |
      | payment_multy_number  | 1                  |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Gherkin Analogic" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And viene aggiunto il costo della notifica totale
    And lettura amount posizione debitoria di "Gherkin Analogic"
    And viene effettuato il controllo dell'aggiornamento del costo totale del utente 0
    And l'avviso pagopa viene pagato correttamente
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE" e successivamente annullata
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario Gherkin Analogic e:
      | payment_creditorTaxId | 77777777777      |
      | payment_pagoPaForm    | SI                 |
      | payment_f24flatRate   | NULL               |
      | payment_f24standard   | NULL               |
      | apply_cost_pagopa     | SI                 |
      | payment_multy_number  | 1                  |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Gherkin Analogic" alla posizione 0
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then viene cancellata la posizione debitoria di "Gherkin Analogic"


  @Async
  Scenario: [B2B_ASYNC_3_PG] Notifica mono PG-Verifica amount GPD per notifica ASYNC in stato “NOTIFICATION_CANCELLED“
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Gherkin Analogic" con Piva "00749900049"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario Gherkin Analogic e:
      | payment_creditorTaxId   | 77777777777           |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | payment_pagoPaForm      | SI                    |
      | payment_f24flatRate     | NULL                  |
      | payment_f24standard     | NULL                  |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Gherkin Analogicc" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then lettura amount posizione debitoria di "Gherkin Analogic"
    And  viene effettuato il controllo del amount di GPD = "100"
    Then viene cancellata la posizione debitoria di "Gherkin Analogic"

  @Async @ignore
  Scenario: [B2B_ASYNC_4_PG] Notifica mono PG Multipagamento-Verifica amount GPD notifica async dopo pagamento tutti i pagamenti poi annullata la notifica il secondo pagamento amount non azzerrato
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Gherkin Analogic" con Piva "00749900049"
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Gherkin Analogic" con Piva "00749900049"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario Gherkin Analogic e:
      | payment_creditorTaxId   | 77777777777  |
      | digitalDomicile_address | test@fail.it |
      | payment_pagoPaForm      | SI           |
      | payment_f24flatRate     | NULL         |
      | payment_f24standard     | NULL         |
      | apply_cost_pagopa       | SI           |
      | payment_multy_number    | 2            |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Gherkin Analogic" alla posizione 0
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Gherkin Analogic" alla posizione 1
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And viene aggiunto il costo della notifica totale
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE" e successivamente annullata
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And lettura amount posizione debitoria per pagamento 0
    And viene effettuato il controllo del amount di GPD = "100"
    Then lettura amount posizione debitoria per pagamento 1
    And viene effettuato il controllo del amount di GPD con amount notifica del utente 1
    Then vengono cancellate le posizioni debitorie


  @Async @bugSecondoTentativo_PN-8719
  Scenario: [B2B_ASYNC_5_PG] Notifica mono PG-Verifica amount GPD per notifica ASYNC fino a "SEND_ANALOG_DOMICILE" al secondo tentativo
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Gherkin Analogic" con Piva "05722930657"
    Given viene generata una nuova notifica
      | subject       | invio notifica con cucumber |
      | feePolicy     | DELIVERY_MODE               |
      | pagoPaIntMode | ASYNC                       |
      | paFee         | 10                          |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | via@FAIL-Irreperibile_AR |
      | payment_creditorTaxId   | 77777777777              |
      | payment_pagoPaForm      | SI                       |
      | payment_f24flatRate     | NULL                     |
      | payment_f24standard     | NULL                     |
      | apply_cost_pagopa       | SI                       |
      | payment_multy_number    | 1                        |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Cucumber Analogic" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED per controllo GPD
    And viene aggiunto il costo della notifica totale
    Then  lettura amount posizione debitoria di "Gherkin Analogic"
    And viene effettuato il controllo dell'aggiornamento del costo totale del utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And  lettura amount posizione debitoria di "Gherkin Analogic"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_ANALOG_DOMICILE" del utente 0 al tentativo:
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And  lettura amount posizione debitoria di "Gherkin Analogic"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_ANALOG_DOMICILE" del utente 0 al tentativo:
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 1        |
    Then viene cancellata la posizione debitoria di "Gherkin Analogic"
