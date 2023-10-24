Feature: avanzamento notifiche asincrone b2b - controllo costi

  Scenario: [B2B_ASYNC_1] Notifica mono PF-Caso creazione Notifica di tipo non ASYNC, validazione non prevista
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | SYNC                        |
      | paFee              | NULL                        |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | payment_creditorTaxId | 77777777777 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED


  Scenario: [B2B_ASYNC_2] Notifica mono PF-Senza verifica amount GPD per notifica ASYNC e campo feePolicy a FLAT_RATE
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | FLAT_RATE                   |
      | pagoPaIntMode      | ASYNC                       |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED


  Scenario: [B2B_ASYNC_3] Notifica mono PF-Rifiuto in caso di notifiche che riportano l’indicazione di modalità asincrona di integrazione al cui interno risultano avvisi non abilitati alla modalità asincrona
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId        | CLMCST42R12D969Z   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  Scenario: [B2B_ASYNC_4] Notifica mono PF-Rifiuto caso notifiche che riportano l’indicazione di modalità asincrona di integrazione al cui interno risultano avvisi con pagamento già effettuato
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And l'avviso pagopa viene pagato correttamente
    Then viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777        |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then viene cancellata la posizione debitoria di "Cristoforo Colombo"


  Scenario: [B2B_ASYNC_5] Notifica mono PF-Senza verifica amount GPD per notifica ASYNC e campo paFee non popolato - Refused
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | NULL                        |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  Scenario: [B2B_ASYNC_6] Notifica multi PF/PG-Rifiuto di notifiche con modalità asincrona di integrazione al cui interno risultano avvisi non abilitati alla modalità asincrona
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777        |
    And destinatario Cucumber Society e:
      | payment_creditorTaxId | 77777777777      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  Scenario: [B2B_ASYNC_7] Notifica mono PG-Caso creazione Notifica di tipo non ASYNC, validazione non prevista
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | SYNC                        |
      | paFee              | NULL                        |
    And destinatario Cucumber Society e:
      | payment_creditorTaxId | 77777777777 |
    When la notifica viene inviata tramite api b2b con sha256 differente dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  Scenario: [B2B_ASYNC_8] Notifica mono PG-Rifiuto caso notifiche che riportano l’indicazione di modalità asincrona di integrazione al cui interno risultano avvisi con pagamento già effettuato
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario Cucumber Society e:
      | payment_creditorTaxId | 77777777777      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And l'avviso pagopa viene pagato correttamente
    Then viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario Cucumber Society e:
      | payment_creditorTaxId | 77777777777      |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_REFUSED"
    Then viene cancellata la posizione debitoria di "Cucumber Society"


  Scenario: [B2B_ASYNC_9] Notifica mono PF-Verifica amount GPD per notifica ASYNC in stato “REQUEST_REFUSED“
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                           |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | payment_creditorTaxId   | 77777777777        |
      | physicalAddress_address | 0000               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_REFUSED"
    And  lettura amount posizione debitoria di "Cristoforo Colombo"
    And  viene effettuato il controllo del amount di GPD = "100"
    Then viene cancellata la posizione debitoria di "Cristoforo Colombo"


  Scenario: [B2B_ASYNC_10] Notifica multi PF/PG-Verifica amount GPD in fase "REQUEST_REFUSED" costo aggiornato a 0
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    Then viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | payment_creditorTaxId   | 77777777777        |
      | physicalAddress_address | 0000               |
    And destinatario
      | denomination            | Cucumber Society |
      | taxId                   | 20517490320      |
      | payment_creditorTaxId   | 77777777777      |
      | physicalAddress_address | 0000             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi REFUSED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_REFUSED" per l'utente 0
    And  lettura amount posizione debitoria di "Cristoforo Colombo"
    And  viene effettuato il controllo del amount di GPD = "110"
    And  viene verificato il costo = "110" della notifica per l'utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_REFUSED" per l'utente 1
    And  lettura amount posizione debitoria di "Cucumber Society"
    And  viene effettuato il controllo del amount di GPD = "110"
    And  viene verificato il costo = "110" della notifica per l'utente 1
    Then viene cancellata la posizione debitoria di "Cristoforo Colombo"
    And viene cancellata la posizione debitoria di "Cucumber Society"



  Scenario: [B2B_ASYNC_11] Notifica mono PF-Aggiornamento costi KO per posizione debitoria non presente
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
    And viene verificato il costo = "100" della notifica


  Scenario: [B2B_ASYNC_12] Notifica mono PF-Verifica amount GPD per notifica ASYNC in stato “NOTIFICATION_CANCELLED“
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere annullata dal sistema tramite codice IUN
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then lettura amount posizione debitoria di "Mario Gherkin"
    And  viene effettuato il controllo del amount di GPD = "0"
    And viene verificato il costo = "0" della notifica
    Then viene cancellata la posizione debitoria di "Mario Gherkin"


  Scenario: [B2B_ASYNC_13] Notifica mono PF Multipagamento-Verifica amount GPD notifica async dopo pagamento di un solo pagamento poi annullata la notifica il secondo pagamento amount non azzerrato
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777        |
      | payment_pagoPaForm    | SI                 |
      | payment_f24flatRate   | NULL               |
      | payment_f24standard   | NULL               |
      | apply_cost_pagopa     | SI                 |
      | payment_multy_number  | 2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And viene cancellata la posizione debitoria del pagamento 0
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" e successivamente annullata
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And lettura amount posizione debitoria per pagamento 1
    And viene effettuato il controllo del amount di GPD = "110"
    And viene verificato il costo = "110" della notifica
    Then viene cancellata la posizione debitoria del pagamento 1


  Scenario: [B2B_ASYNC_14] Notifica mono PF Multipagamento-Verifica amount GPD notifica async dopo pagamento tutti i pagamenti poi annullata la notifica il secondo pagamento amount non azzerrato
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777        |
      | payment_pagoPaForm    | SI                 |
      | payment_f24flatRate   | NULL               |
      | payment_f24standard   | NULL               |
      | apply_cost_pagopa     | SI                 |
      | payment_multy_number  | 2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And l'avviso pagopa 1 viene pagato correttamente dall'utente 0
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" e successivamente annullata
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And viene verificato il costo = "110" della notifica
    Then vengono cancellate le posizioni debitorie


  Scenario: [B2B_ASYNC_15] Notifica mono PG-Verifica amount GPD per notifica ASYNC in stato “NOTIFICATION_CANCELLED“
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario Cucumber Society e:
      | digitalDomicile       | NULL        |
      | payment_creditorTaxId | 77777777777 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere annullata dal sistema tramite codice IUN
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Then lettura amount posizione debitoria di "Cucumber Society"
    And  viene effettuato il controllo del amount di GPD = "0"
    And viene verificato il costo = "0" della notifica
    Then viene cancellata la posizione debitoria di "Cucumber Society"

  Scenario: [B2B_ASYNC_16] Notifica mono PG Multipagamento-Verifica amount GPD notifica async dopo pagamento tutti i pagamenti poi annullata la notifica il secondo pagamento amount non azzerrato
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario Cucumber Society e:
      | payment_creditorTaxId | 77777777777        |
      | payment_pagoPaForm    | SI                 |
      | payment_f24flatRate   | NULL               |
      | payment_f24standard   | NULL               |
      | apply_cost_pagopa     | SI                 |
      | payment_multy_number  | 2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" e successivamente annullata
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And lettura amount posizione debitoria per pagamento 0
    And viene effettuato il controllo del amount di GPD = "0"
    Then lettura amount posizione debitoria per pagamento 1
    And viene effettuato il controllo del amount di GPD con amount notifica del utente 1
    And viene verificato il costo = "0" della notifica
    Then vengono cancellate le posizioni debitorie


  Scenario: [B2B_ASYNC_17] Notifica multi PF/PG-Verifica amount GPD per notifica ASYNC in stato “NOTIFICATION_CANCELLED“
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777        |
      | digitalDomicile       | NULL               |
    And destinatario Cucumber Society e:
      | digitalDomicile       | NULL        |
      | payment_creditorTaxId | 77777777777 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED" per l'utente 0
    And  lettura amount posizione debitoria di "Cristoforo Colombo"
    And  viene effettuato il controllo del amount di GPD = "0"
    And  viene verificato il costo = "0" della notifica per l'utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED" per l'utente 1
    And  lettura amount posizione debitoria di "Cucumber Society"
    And  viene effettuato il controllo del amount di GPD = "0"
    And  viene verificato il costo = "0" della notifica per l'utente 1
    Then vengono cancellate le posizioni debitorie


  Scenario: [B2B_ASYNC_18] Notifica multi PF/PG-Verifica amount GPD per notifica ASYNC per Multipagamento
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777        |
      | digitalDomicile       | NULL               |
      | payment_pagoPaForm    | SI                 |
      | payment_f24flatRate   | NULL               |
      | payment_f24standard   | NULL               |
      | apply_cost_pagopa     | SI                 |
      | payment_multy_number  | 2                  |
    And destinatario Cucumber Society e:
      | digitalDomicile       | NULL        |
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm    | SI          |
      | payment_f24flatRate   | NULL        |
      | payment_f24standard   | NULL        |
      | apply_cost_pagopa     | SI          |
      | payment_multy_number  | 2           |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene aggiunto il costo della notifica totale
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED" per l'utente 0
    And  lettura amount posizione debitoria per pagamento 0
    And  viene effettuato il controllo del amount di GPD con amount notifica del pagamento 0
    And  lettura amount posizione debitoria per pagamento 1
    And  viene effettuato il controllo del amount di GPD = "0"
    And  viene verificato il costo = "0" della notifica per l'utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED" per l'utente 1
    And  lettura amount posizione debitoria per pagamento 2
    And  viene effettuato il controllo del amount di GPD = "0"
    And  lettura amount posizione debitoria per pagamento 3
    And  viene effettuato il controllo del amount di GPD = "0"
    Then vengono cancellate le posizioni debitorie


  Scenario: [B2B_ASYNC_19] Notifica mono PF-Verifica amount GPD per notifica ASYNC per Multipagamento
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination          | Cristoforo Colombo |
      | taxId                 | CLMCST42R12D969Z   |
      | payment_creditorTaxId | 77777777777        |
      | digitalDomicile       | NULL               |
      | payment_pagoPaForm    | SI                 |
      | payment_f24flatRate   | NULL               |
      | payment_f24standard   | NULL               |
      | apply_cost_pagopa     | SI                 |
      | payment_multy_number  | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene aggiunto il costo della notifica totale
    Then viene effettuato il controllo del cambiamento del amount nella timeline "REQUEST_ACCEPTED" del pagamento 0
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And  lettura amount posizione debitoria per pagamento 0
    Then viene effettuato il controllo del cambiamento del amount nella timeline "SEND_SIMPLE_REGISTERED_LETTER" del pagamento 0
    And viene cancellata la posizione debitoria del pagamento 0


  Scenario: [B2B_ASYNC_20] Notifica mono PF-Verifica amount GPD per notifica ASYNC in stato “VALIDATION“
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                           |
    And destinatario
      | denomination            | Cristoforo Colombo    |
      | taxId                   | CLMCST42R12D969Z      |
      | payment_creditorTaxId   | 77777777777           |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | via@FAIL-Discovery_AR |
      | payment_pagoPaForm      | SI                    |
      | payment_f24flatRate     | NULL                  |
      | payment_f24standard     | NULL                  |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And viene aggiunto il costo della notifica totale
    Then  lettura amount posizione debitoria di "Cristoforo Colombo"
    And viene effettuato il controllo del cambiamento del amount nella timeline "REQUEST_ACCEPTED" del utente 0
    Then viene cancellata la posizione debitoria di "Cristoforo Colombo"


  Scenario: [B2B_ASYNC_21] Notifica mono PF-Verifica amount GPD per notifica ASYNC fino a "SEND_ANALOG_DOMICILE" al primo tentativo
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination            | Cristoforo Colombo    |
      | taxId                   | CLMCST42R12D969Z      |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | via@FAIL-Discovery_AR |
      | payment_creditorTaxId   | 77777777777           |
      | payment_pagoPaForm      | SI                    |
      | payment_f24flatRate     | NULL                  |
      | payment_f24standard     | NULL                  |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene aggiunto il costo della notifica totale
    When lettura amount posizione debitoria di "Cristoforo Colombo"
    Then viene effettuato il controllo del cambiamento del amount nella timeline "REQUEST_ACCEPTED" del utente 0
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    Then  lettura amount posizione debitoria di "Cristoforo Colombo"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_ANALOG_DOMICILE" del utente 0
      | details_sentAttemptMade | 0 |
    Then viene cancellata la posizione debitoria di "Cristoforo Colombo"


  Scenario: [B2B_ASYNC_22] Notifica mono PF-Verifica amount GPD per notifica ASYNC fino a "SEND_ANALOG_DOMICILE" al secondo tentativo
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                         |
    And destinatario
      | denomination            | Cristoforo Colombo    |
      | taxId                   | CLMCST42R12D969Z      |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | via@FAIL-Discovery_AR |
      | payment_creditorTaxId   | 77777777777           |
      | payment_pagoPaForm      | SI                    |
      | payment_f24flatRate     | NULL                  |
      | payment_f24standard     | NULL                  |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene aggiunto il costo della notifica totale
    And lettura amount posizione debitoria di "Cristoforo Colombo"
    Then viene effettuato il controllo del cambiamento del amount nella timeline "REQUEST_ACCEPTED" del utente 0
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    Then  lettura amount posizione debitoria di "Cristoforo Colombo"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_ANALOG_DOMICILE" del utente 0
      | details_sentAttemptMade | 0 |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    Then  lettura amount posizione debitoria di "Cristoforo Colombo"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_ANALOG_DOMICILE" del utente 0
      | details_sentAttemptMade | 1 |
    Then viene cancellata la posizione debitoria di "Cristoforo Colombo"


  Scenario: [B2B_ASYNC_23] Notifica mono PF-Verifica amount GPD per notifica ASYNC in stato “SEND_SIMPLE_REGISTERED_LETTER“
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@ok_RS          |
      | payment_creditorTaxId   | 77777777777        |
      | payment_pagoPaForm      | SI                 |
      | payment_f24flatRate     | NULL               |
      | payment_f24standard     | NULL               |
      | apply_cost_pagopa       | SI                 |
      | payment_multy_number    | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene aggiunto il costo della notifica totale
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    Then lettura amount posizione debitoria di "Cristoforo Colombo"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_SIMPLE_REGISTERED_LETTER" del utente 0
    Then viene cancellata la posizione debitoria di "Cristoforo Colombo"


  Scenario: [B2B_ASYNC_24] Notifica mono PF-Verifica amount GPD per notifica ASYNC in stato "VALIDATION" --> “SEND_SIMPLE_REGISTERED_LETTER“
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@ok_RS          |
      | payment_creditorTaxId   | 77777777777        |
      | payment_pagoPaForm      | SI                 |
      | payment_f24flatRate     | NULL               |
      | payment_f24standard     | NULL               |
      | apply_cost_pagopa       | SI                 |
      | payment_multy_number    | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene aggiunto il costo della notifica totale
    Then lettura amount posizione debitoria di "Cristoforo Colombo"
    And viene effettuato il controllo del cambiamento del amount nella timeline "REQUEST_ACCEPTED" del utente 0
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And  lettura amount posizione debitoria di "Cristoforo Colombo"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_SIMPLE_REGISTERED_LETTER" del utente 0
    Then viene cancellata la posizione debitoria di "Cristoforo Colombo"

  Scenario: [B2B_ASYNC_25] Notifica mono PG-Verifica amount GPD per notifica ASYNC fino a "SEND_ANALOG_DOMICILE" al secondo tentativo
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Cucumber Society e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | via@FAIL-Discovery_AR |
      | payment_creditorTaxId   | 77777777777           |
      | payment_pagoPaForm      | SI                    |
      | payment_f24flatRate     | NULL                  |
      | payment_f24standard     | NULL                  |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene aggiunto il costo della notifica totale
    Then lettura amount posizione debitoria di "Cucumber Society"
    And viene effettuato il controllo del cambiamento del amount nella timeline "REQUEST_ACCEPTED" del utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And  lettura amount posizione debitoria di "Cucumber Society"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_ANALOG_DOMICILE" del utente 0
      | details_sentAttemptMade | 0 |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And  lettura amount posizione debitoria di "Cucumber Society"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_ANALOG_DOMICILE" del utente 0
      | details_sentAttemptMade | 1 |
    Then viene cancellata la posizione debitoria di "Cucumber Society"


  Scenario: [B2B_ASYNC_26] Notifica multi PF/PG-Verifica amount GPD per notifica ASYNC in stato “SEND_SIMPLE_REGISTERED_LETTER“
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@ok_RS          |
      | payment_creditorTaxId   | 77777777777        |
      | payment_pagoPaForm      | SI                 |
      | payment_f24flatRate     | NULL               |
      | payment_f24standard     | NULL               |
      | apply_cost_pagopa       | SI                 |
      | payment_multy_number    | 1                  |
    And destinatario Cucumber Society e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | payment_creditorTaxId   | 77777777777  |
      | payment_pagoPaForm      | SI           |
      | payment_f24flatRate     | NULL         |
      | payment_f24standard     | NULL         |
      | apply_cost_pagopa       | SI           |
      | payment_multy_number    | 1            |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene aggiunto il costo della notifica totale
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And  lettura amount posizione debitoria di "Cristoforo Colombo"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_SIMPLE_REGISTERED_LETTER" del utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 1
    And  lettura amount posizione debitoria di "Cucumber Society"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_SIMPLE_REGISTERED_LETTER" del utente 1
    Then vengono cancellate le posizioni debitorie


  Scenario: [B2B_ASYNC_27] Notifica mono PF-Verifica amount GPD per notifica ASYNC cancellazione posizione debitoria dopo validazione della notifica
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | payment_creditorTaxId   | 77777777777        |
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@ok_RS          |
      | payment_pagoPaForm      | SI                 |
      | payment_f24flatRate     | NULL               |
      | payment_f24standard     | NULL               |
      | apply_cost_pagopa       | SI                 |
      | payment_multy_number    | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then lettura amount posizione debitoria di "Cristoforo Colombo"
    And viene effettuato il controllo del cambiamento del amount nella timeline "REQUEST_ACCEPTED" del utente 0
    And  viene cancellata la posizione debitoria di "Cristoforo Colombo"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene effettuato il controllo del cambiamento del amount nella timeline "SEND_SIMPLE_REGISTERED_LETTER" del utente 1

  Scenario: [B2B_ASYNC_28] Notifica mono PF-Verifica scarto notifica se applyCostFlag a false
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | payment_creditorTaxId   | 77777777777        |
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@ok_RS          |
      | payment_pagoPaForm      | SI                 |
      | payment_f24flatRate     | NULL               |
      | payment_f24standard     | NULL               |
      | apply_cost_pagopa       | NULL               |
      | payment_multy_number    | 2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi REFUSED
    Then vengono cancellate le posizioni debitorie

  Scenario: [B2B_ASYNC_29] Notifica mono PF/PG-Verifica scarto notifica se applyCostFlag a false
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@ok_RS          |
      | payment_pagoPaForm      | SI                 |
      | payment_f24flatRate     | NULL               |
      | payment_f24standard     | NULL               |
      | payment_multy_number    | 2                  |
    And destinatario Cucumber Society e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | payment_pagoPaForm      | SI           |
      | payment_f24flatRate     | NULL         |
      | payment_f24standard     | NULL         |
      | payment_multy_number    | 2            |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi REFUSED
    Then vengono cancellate le posizioni debitorie

  @version
  Scenario: [B2B_ASYNC_30] Creazione notifica ASYNC con V1 - Errore
    Given viene generata una nuova notifica V1
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Cucumber V1
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V1
    Then si verifica lo scarto dell' acquisizione della notifica V1

  @version
  Scenario: [B2B_ASYNC_31] Creazione notifica ASYNC con V2 - Errore
    Given viene generata una nuova notifica V2
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Cucumber V2
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED V2
    Then si verifica lo scarto dell' acquisizione della notifica V2

  @version
  Scenario: [B2B_ASYNC_32] Creazione notifica ASYNC con V2.1 e recupero tramite codice IUN V2.0 (p.fisica)_scenario negativo
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica non può essere recuperata dal sistema tramite codice IUN con OpenApi V20
    Then vengono cancellate le posizioni debitorie

    @testIntegrazione
  Scenario: [B2B_PROVA_INTEGRAZIONE_GPD] Viene creata una posizione debitoria, interrogata e cancellata
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber Society" con Piva "20517490320"
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cristoforo Colombo" con CF "CLMCST42R12D969Z"
    Then lettura amount posizione debitoria di "Cristoforo Colombo"
    And lettura amount posizione debitoria di "Cucumber Society"
    Then vengono cancellate le posizioni debitorie
