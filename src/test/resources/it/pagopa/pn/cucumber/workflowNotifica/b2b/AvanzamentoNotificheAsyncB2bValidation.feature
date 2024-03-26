Feature: avanzamento notifiche asincrone b2b PF - controllo costi


  @Async
  Scenario: [B2B_ASYNC_3_PF] Notifica mono PF-Rifiuto in caso di notifiche che riportano l’indicazione di modalità asincrona di integrazione al cui interno risultano avvisi non abilitati alla modalità asincrona
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm    | SI          |
      | payment_f24           | NULL        |
      | apply_cost_pagopa     | SI          |
      | payment_multy_number  | 1           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @Async
  Scenario: [B2B_ASYNC_5_PF] Notifica mono PF-Senza verifica amount GPD per notifica ASYNC e campo paFee non popolato - Refused
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | NULL                        |
    And destinatario Mario Gherkin e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm    | SI          |
      | payment_f24           | NULL        |
      | apply_cost_pagopa     | SI          |
      | payment_multy_number  | 1           |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  @Async
  Scenario: [B2B_ASYNC_7_PF] Notifica mono PF-Aggiornamento costi KO per posizione debitoria non presente
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm    | SI          |
      | payment_f24           | NULL        |
      | apply_cost_pagopa     | SI          |
      | payment_multy_number  | 1           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @Async
  Scenario: [B2B_ASYNC_18_PF] Notifica mono PF-Verifica scarto notifica se applyCostFlag a false
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | payment_creditorTaxId   | 77777777777           |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | apply_cost_pagopa       | NO                    |
      | payment_multy_number    | 1                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then vengono cancellate le posizioni debitorie


  @Async
  Scenario: [B2B_ASYNC_19_PF] Notifica mono PF-Verifica amount GPD per notifica ASYNC in stato "NOTIFICATION_CANCELLED"
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777778" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | payment_creditorTaxId   | 77777777777  |
      | payment_pagoPaForm      | SI           |
      | payment_f24             | NULL         |
      | apply_cost_pagopa       | SI           |
      | payment_multy_number    | 1            |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @bugNoto
  Scenario: [B2B_ASYNC_20_PF] Notifica mono PF-Verifica amount GPD per notifica ASYNC con posizione debitoria con CF diverso
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "FRMTTR76M06B715E"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | SYNC                        |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | payment_creditorTaxId   | 77777777777  |
      | payment_pagoPaForm      | SI           |
      | payment_f24             | NULL         |
      | apply_cost_pagopa       | SI           |
      | payment_multy_number    | 1            |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @version @Async
  Scenario: [B2B_ASYNC_2_VERSIONAMENTO] Creazione notifica ASYNC con V2.1 e recupero tramite codice IUN V1 (p.fisica)_scenario negativo
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | payment_creditorTaxId   | 77777777777           |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica non può essere recuperata dal sistema tramite codice IUN con OpenApi V10 generando un errore
    And l'operazione ha prodotto un errore con status code "400"
    Then vengono cancellate le posizioni debitorie

  @version @Async
  Scenario: [B2B_ASYNC_3_VERSIONAMENTO] Creazione notifica ASYNC con V2.1 e recupero tramite codice IUN V2.0 (p.fisica)_scenario negativo
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | payment_creditorTaxId   | 77777777777           |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | apply_cost_pagopa       | SI                    |
      | payment_multy_number    | 1                     |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica non può essere recuperata dal sistema tramite codice IUN con OpenApi V20 generando un errore
    And l'operazione ha prodotto un errore con status code "400"
    Then vengono cancellate le posizioni debitorie


  @Async
  Scenario: [B2B_ASYNC_1_MULTI] Notifica multi PF/PG-Rifiuto di notifiche con modalità asincrona di integrazione al cui interno risultano avvisi non abilitati alla modalità asincrona
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm    | SI          |
      | payment_f24           | NULL        |
      | apply_cost_pagopa     | SI          |
      | payment_multy_number  | 1           |
    And destinatario Cucumber Society e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm    | SI          |
      | payment_f24           | NULL        |
      | apply_cost_pagopa     | SI          |
      | payment_multy_number  | 1           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @Async
  Scenario: [B2B_ASYNC_6_MULTI] Notifica mono PF/PG-Verifica scarto notifica se applyCostFlag a false
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber_Society" con Piva "20517490320"
    And viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Cucumber_Society" con Piva "20517490320"
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | apply_cost_pagopa       | NO                    |
      | payment_multy_number    | 2                     |
    And destinatario Cucumber Society e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
      | payment_pagoPaForm      | SI                    |
      | payment_f24             | NULL                  |
      | apply_cost_pagopa       | NO                    |
      | payment_multy_number    | 2                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Then vengono cancellate le posizioni debitorie




