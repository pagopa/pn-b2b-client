Feature: avanzamento notifiche b2b persona fisica multi pagamento


 #24 PA - inserimento notifica mono destinatario con un solo avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | apply_cost_f24       | NO   |
      | payment_multy_number | 2    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_1] PA - inserimento notifica mono destinatario con un due avvisi pagoPA e costi di notifica inclusi  modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 2    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_2] PA - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | NO                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_3] PA - inserimento notifica multi destinatario con un solo avviso pagoPA e F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_4] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità FLAT_RATE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | SI   |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_5] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità FLAT_RATE applyCost true (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | SI   |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_6] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE applyCost false (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | SI   |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 1    |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"


   #25 PA - inserimento notifica mono destinatario con un solo F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_25] PA - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_25_1] PA - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica inclusi più paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


 #26 PA - inserimento notifica mono destinatario con più avvisi pagoPA e nessun F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26] PA - inserimento notifica mono destinatario con più avvisi pagoPA (almeno 2)  e nessun F24 con costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 2    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26_1] PA - inserimento notifica mono destinatario con più avvisi pagoPA (almeno 3) e nessun F24 e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 3    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26_2] PA - inserimento notifica mono destinatario con più avvisi pagoPA (almeno 4) e nessun F24 e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 4    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26_3] PA - inserimento notifica mono destinatario con più avvisi pagoPA e  F24 (almeno 2) e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 2                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene verificato il costo = "100" della notifica


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26_4] PA - inserimento notifica mono destinatario con più avvisi pagoPA (almeno 2) e nessun F24 con costi non inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 2    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica




 #27  PA - inserimento notifica mono destinatario con più F24 e nessun avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_27] PA - inserimento notifica mono destinatario con più F24 (Almeno 2) e nessun avviso pagoPA e costi  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 2                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_27_1] PA - inserimento notifica mono destinatario con più F24 (Almeno 2) e nessun avviso pagoPA e costi non  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | SI                            |
      | payment_f24standard  | NULL                          |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | NO                            |
      | payment_multy_number | 2                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  #TODO Vecchio Requisito
  #28  PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
  @pagamentiMultipli @ignore
  Scenario: [B2B-PA-PAY_MULTI_28] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  #29  PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_29] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA] costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_29_1] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA] costi non inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica


  #30 PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_30] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA] e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_30_1] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA] e costi non inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | SI                            |
      | payment_f24standard  | NULL                          |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | NO                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_30_2] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA] e costi inclusi più paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"




  #31 PA - inserimento notifica multi destinatario con un solo avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_31] PA - inserimento notifica multi destinatario con un solo avviso pagoPA e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_31_1] PA - inserimento notifica multi destinatario con un solo avviso pagoPA e costi di notifica non  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 1    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | NO                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1


  #32 PA - inserimento notifica multi destinatario con un solo F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_32] PA - inserimento notifica multi destinatario con un solo F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

    #32 PA - inserimento notifica multi destinatario con un solo F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_32_1] PA - inserimento notifica multi destinatario con un solo F24 e costi di notifica non  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | SI                            |
      | payment_f24standard  | NULL                          |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | NO                            |
      | payment_multy_number | 1                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | SI                            |
      | payment_f24standard  | NULL                          |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | NO                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_32_2] PA - inserimento notifica multi destinatario con il destinatario 1 con un solo F24 e destinatario 2 con solo avviso pagoPa e costi di notifica inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 1


  #33 PA - inserimento notifica multi destinatario con più avvisi pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_33] PA - inserimento notifica multi destinatario con più avvisi pagoPA e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 2    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1




  #34 PA - inserimento notifica multi destinatario con più F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_34] PA - inserimento notifica multi destinatario con un solo F24 e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"



  #35 PA - download modello F24
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_35] PA - inserimento notifica mono destinatario con un solo F24 e costi inclusi -  download modello F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | NO                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When si verifica la corretta acquisizione della notifica
    Then viene richiesto il download del documento "F24"
    And il download non ha prodotto errori

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_35_1] PA - inserimento notifica multi destinatario con un solo F24 e costi inclusi - download modello F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | NO                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_pagopa    | NO                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When si verifica la corretta acquisizione della notifica
    Then viene richiesto il download del documento "F24" per il destinatario 0
    And il download non ha prodotto errori
    And viene richiesto il download del documento "F24" per il destinatario 1
    And il download non ha prodotto errori


  #36 PA - download allegato pagoPA
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_36] PA - inserimento notifica mono destinatario con un solo pagoPA e costi inclusi - download allegato pagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente


   #TODO CHIEDERE INFO.............Vecchio Requisito
  #37 PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: ad ogni avviso pagoPA corrisponde un F24 [TA]




  #38 PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_38] PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA] costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 2    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1



 #TODO CHIEDERE INFO.............
  #39 PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_39] PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]  costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


#TODO VECCHIO REQUISITO
  #40 Destinatario - pagamento notifica mono destinatario con un solo avviso pagoPA: verifica stato “In elaborazione”


 #TODO VECCHIO REQUISITO
  #41 Destinatario - visualizzazione box di pagamento su notifica mono destinatario pagata - verifica della presenza stato “Pagato”



  #42 Notifica mono destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_42] Notifica mono destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA] costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica


  #TODO TEST MANUALE
  #43 Destinatario - notifica mono destinatario con più avvisi pagoPA: pagamento di un avviso
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_43] Destinatario - notifica mono destinatario con più avvisi pagoPA: pagamento di un avviso costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 2    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_43_1] Destinatario - notifica mono destinatario con più avvisi pagoPA: pagamento di più avvisi costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 2    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And l'avviso pagopa 1 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 1 dal destinatario 0

  #TODO TEST MANUALE
  #44 Destinatario - notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_44] Destinatario - notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa) costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | apply_cost_pagopa    | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica


    #TODO Non è possibile effettuare il pagamento lato Destinatario accertare il pagamento di un solo avviso...Chiudere la posizione debitoria
  @pagamentiMultipli @ignore
  Scenario: [B2B-PA-PAY_MULTI_44_1] Destinatario - notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | NO                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #TODO parametrizzare lo step per avviso di pagamento.......
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica


  #45 Destinatario - download modello F24
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_45] Destinatario PF: inserimento notifica mono destinatario con solo F24 e costi inclusi - download allegato F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NULL               |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_pagopa    | NO                 |
      | apply_cost_f24       | SI                 |
      | payment_multy_number | 1                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When si verifica la corretta acquisizione della notifica
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Cucumber"


  #46 Destinatario - download allegato pagoPA
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_46] Destinatario PF: inserimento notifica mono destinatario con solo pagoPA e costi inclusi - download allegato pagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When si verifica la corretta acquisizione della notifica
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Cucumber"


  #47 Destinatario 1 - pagamento notifica multi destinatario con un solo avviso pagoPA
  #TODO Non è possibile effettuare il pagamento lato Destinatario quindi si può solo lato PA verificare la posizione Debitoria del Destinatario
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_47] Destinatario 1 - pagamento notifica multi destinatario con un solo avviso pagoPA e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_47_1] Destinatario 1 - pagamento notifica multi destinatario con più avvisi pagoPA (almeno 2) e con pagamento di un solo avviso e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 2    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And l'avviso pagopa 0 viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 1

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_47_2] Destinatario 1 - pagamento notifica multi destinatario con più avvisi PagoPa e modello F24 e con pagamento di un solo avviso e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 2                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 2                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And l'avviso pagopa 0 viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 1


  #48 Notifica multi destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_48] Notifica multi destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA] e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And l'avviso pagopa 0 viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1


  #49 Destinatario 1 - notifica multi destinatario con più avvisi pagoPA: pagamento di un avviso
  #TODO Modificare il metodo che verifica il pagamento di un solo avviso......
  #TODO Non è possibile effettuare il pagamento lato Destinatario
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_49] Destinatario 1 - notifica multi destinatario con più avvisi pagoPA: verifica costo della notifica del destinatario 1 e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_pagoPaForm_1 | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 2    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_pagoPaForm_1 | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 2                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0

  #50 Destinatario 1 - notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
  #TODO Non è possibile effettuare il pagamento lato Destinatario
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_50] Destinatario 1 - notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: verifica costo della notifica del destinatario 1/2 e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | apply_cost_pagopa    | SI                            |
      | payment_multy_number | 1                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1



#SOLO TM
  #51 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_51] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica

#SOLO TM
  #52 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_52] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica
#SOLO TM
  #53 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_53] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | SI                            |
      | payment_f24standard  | NULL                          |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | NO                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


#SOLO TM
  #54 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_54] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


#SOLO TM
  #55 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_55] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | SI                            |
      | payment_f24standard  | NULL                          |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | NO                            |
      | apply_cost_f24       | NO                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0


#SOLO TM
  #56 PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_56] PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And l'avviso pagopa 0 viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 1

#SOLO TM
  #57 PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_57] PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 1    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | NO                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 1

#SOLO TM
  #58 PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_58] PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | SI                            |
      | payment_f24standard  | NULL                          |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | NO                            |
      | payment_multy_number | 1                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | SI                            |
      | payment_f24standard  | NULL                          |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24       | NO                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

#SOLO TM
  #59 PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_59] PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

#SOLO TM
  #60 PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_60] PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 1    |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | SI                            |
      | payment_f24standard  | NULL                          |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24       | NO                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0




#TODO SOLO TM
  #61 Destinatario - visualizzazione box di pagamento su notifica mono destinatario pagata (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#TODO SOLO TM
  #62 Documento PagoPa: Inserimento dati pagamento e relativa verifica dei dati nel documento generato di avviso PagoPA (es. amount, description, expirationDate, status, ecc.) [TA]

#TODO SOLO TM
  #63 Documento F24: Inserimento dati pagamento e costruzione del documento F24 e relativa verifica dei dati nel documento generato F24 [TA]




  #64 Test di Validazione degli oggetti di pagamento ricevuti: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_64] Test di Validazione degli oggetti di pagamento ricevuti: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | NO                 |
      | payment_multy_number | 2                  |
      | payment_noticeCode   | 302011697026785044 |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_64_1] Test di Validazione degli oggetti di pagamento ricevuti multidestinatario: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | NO                 |
      | payment_multy_number | 1                  |
      | payment_noticeCode   | 302011697026785049 |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | NO                 |
      | payment_multy_number | 1                  |
      | payment_noticeCode   | 302011697026785049 |

    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_64_2] Test di Validazione degli oggetti di pagamento ricevuti multidestinatario: istanze di pagamento non coerenti feePolicy FLAT_RATE e  destinatario2 con applyCostTrue
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 1    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 1                  |

    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_64_3] Test di Validazione degli oggetti di pagamento ricevuti multidestinatario: istanze di pagamento non coerenti feePolicy DELIVERY_MODE e destinatario1 con applyCost false -  destinatario2 con applyCost true
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 1    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 1                  |

    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_64_4] Test di Validazione degli oggetti di pagamento ricevuti multidestinatario: istanze di pagamento non coerenti (destinatario1 più pagamenti e destinatario2 un solo pagamento)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 4    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | NULL               |
      | apply_cost_pagopa    | NO                 |
      | payment_multy_number | 1                  |

    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"




#TODO NO TEST...
  #65 Timeline: Verifica F24 (scenario negativo: deve essere riscontrata assenza di eventi di pagamento in timeline).. NO TEST...

#TODO NO TEST....
  #66 Timeline: Verifica PagoPa con più di un pagamento effettuato (presenza di più istanze di pagamento) [TA] .. NO TEST...
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_66] PA Timeline: Esecuzione di più pagamenti, PagoPa -> Verifica in timeline presenza solo dei pagamenti PagoPa [TA]  e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 2    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then gli avvisi PagoPa vengono pagati correttamente dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 1 dal destinatario 0
    And verifica presenza in Timeline dei solo pagamenti di avvisi PagoPA del destinatario 0




  #67 Timeline: Esecuzione di più pagamenti, sia F24 che PagoPa -> Verifica in timeline presenza solo dei pagamenti PagoPa [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_67] PA Timeline: Esecuzione di più pagamenti, sia F24 che PagoPa -> Verifica in timeline presenza solo dei pagamenti PagoPa [TA] e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24       | SI                            |
      | apply_cost_pagopa    | SI                            |
      | payment_multy_number | 2                             |
    And destinatario
      | denomination         | Gaio Giulio Cesare            |
      | taxId                | CSRGGL44L13H501E              |
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24       | SI                            |
      | apply_cost_pagopa    | SI                            |
      | payment_multy_number | 2                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then gli avvisi PagoPa vengono pagati correttamente dal destinatario 0
    And gli avvisi PagoPa vengono pagati correttamente dal destinatario 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1
    And verifica presenza in Timeline dei solo pagamenti di avvisi PagoPA del destinatario 0


#TODO da Verificare........
  #68 Pagamenti in FAILURE: Verifica di tutti i possibili KO [TA]:

 # 'PAYMENT_UNAVAILABLE', // Technical Error
 # 'PAYMENT_UNKNOWN', // Payment data error
 # 'DOMAIN_UNKNOWN', // Creditor institution error
 # 'PAYMENT_ONGOING', // Payment on going
 # 'PAYMENT_EXPIRED', // Payment expired
 # 'PAYMENT_CANCELED', // Payment canceled
 # 'PAYMENT_DUPLICATED', // Payment duplicated
 # 'GENERIC_ERROR'
  @pagamentiMultipli @mockCheckout
  Scenario: [B2B-PA-PAY_MULTI_68] Pagamenti in FAILURE: Verifica di tutti i possibili KO [TA] - Pagamento da effettuare  e status REQUIRED
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | payment_creditorTaxId | 77777777777        |
      | payment_noticeCode    | 302000100000019421 |
    Then verifica stato pagamento di una notifica con status "REQUIRED"

  #TODO al momento non è possibile effettuare pagamenti..
  @pagamentiMultipli @ignore @mockCheckout
  Scenario: [B2B-PA-PAY_MULTI_68_1] Pagamenti in FAILURE: Verifica di tutti i possibili KO [TA] - Pagamento effettuato  errore 409 PAGAMENTO_DUPLICATO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | payment_creditorTaxId | 77777777777        |
      | payment_noticeCode    | 302191689948151964 |
    #Then l'avviso pagopa viene pagato correttamente su checkout con errore "409"
    Then verifica stato pagamento di una notifica con status "REQUIRED"

  @pagamentiMultipli @mockCheckout
  Scenario: [B2B-PA-PAY_MULTI_68_2] Pagamenti in FAILURE: Verifica di tutti i possibili KO [TA] - Pagamento sconosciuto  errore 404  e status FAILURE PAGAMENTO_SCONOSCIUTO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | payment_creditorTaxId | 77777777777        |
      | payment_noticeCode    | 302300118000019102 |
    Then verifica stato pagamento di una notifica con status "FAILURE"

  @pagamentiMultipli @mockCheckout
  Scenario: [B2B-PA-PAY_MULTI_68_3_1] Pagamenti in FAILURE: Verifica di tutti i possibili KO [TA] - PA sconosciuta  errore 400 e status FAILURE  stazione int PA sconosciuta
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | payment_creditorTaxId | 77777777777        |
      | payment_noticeCode    | 402000118000019102 |
    Then verifica stato pagamento di una notifica con status "FAILURE"



  #69 Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso2
  #TODO Modificare lo scenario..................
  #Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante e delegato possono scaricare l'avviso
  @pagamentiMultipli @deleghe1 @ignore
  Scenario: [B2B-PA-PAY_MULTI_69] Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante e Delegato scaricano correttamenta l'avviso pagoPA e F24
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_pagopa    | SI                 |
      | apply_cost_f24       | SI                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Gherkin" con delega
    And "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    And il download non ha prodotto errori
    And l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega
    And "Mario Cucumber" tenta il recupero dell'allegato "F24"
    And il download non ha prodotto errori


  #70 Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso1 (Stesso avviso - pagoPA)
  @pagamentiMultipli @deleghe1 @ignore
  Scenario: [B2B-PA-PAY_MULTI_70] Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante e Delegato scaricano correttamenta l'avviso pagoPA
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Gherkin" con delega
    And "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    And il download non ha prodotto errori



  #71 Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - PagoPa [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_71] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - PagoPa
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"

    #STESSO TEST B2B-PA-PAY_MULTI_71 IMPLEMENTATO DIVERSAMENTE
  @pagamentiMultipli @ignore
  Scenario: [B2B-PA-PAY_MULTI_71_2] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - PagoPa
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    Then viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene effettuato un controllo sulla durata della retention del PAGOPA di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details          | NOT_NULL |
      | details_recIndex | 0        |
   # Then viene effettuato un controllo sulla durata della retention del PAGOPA di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_71_1] Verifica retention allegati di pagamento (7gg precaricato) - PagoPa
    Given viene effettuato il pre-caricamento di un allegato
    Then viene effettuato un controllo sulla durata della retention di "PAGOPA" precaricato



  #72 Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
  @pagamentiMultipli @mockPec
  Scenario: [B2B-PA-PAY_MULTI_72] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm      | NULL               |
      | payment_f24flatRate     | NULL               |
      | payment_f24standard     | SI                 |
      | digitalDomicile_address | test@pecOk.it      |
      | title_payment           | F24_STANDARD_MARIO |
      | apply_cost_f24          | SI                 |
      | payment_multy_number    | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene effettuato un controllo sulla durata della retention del F24 di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  @pagamentiMultipli @realPec
  Scenario: [B2B-PA-PAY_MULTI_72_4] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm      | NULL                  |
      | payment_f24flatRate     | NULL                  |
      | payment_f24standard     | SI                    |
      | digitalDomicile_address | pectest@pec.pagopa.it |
      | title_payment           | F24_STANDARD_MARIO    |
      | apply_cost_f24          | SI                    |
      | payment_multy_number    | 1                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline     | true     |
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene effettuato un controllo sulla durata della retention del F24 di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details          | NOT_NULL |
      | details_recIndex | 0        |


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_72_1] Verifica retention allegati di pagamento (7gg precaricato) - F24
    Given  viene effettuato il pre-caricamento dei metadati f24
    Then viene effettuato un controllo sulla durata della retention di "F24" precaricato


  @pagamentiMultipli @ignore
  Scenario: [B2B-PA-PAY_MULTI_72_3] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NULL               |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_f24       | SI                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "F24"


  #73 PA -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata
  #Verifica dello sha256 non possibile perché il file viene. generato on demand
  @pagamentiMultipli @ignore
  Scenario: [B2B-PA-PAY_MULTI_73] PA -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NULL               |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_f24       | SI                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    #Then si verifica la corretta acquisizione della notifica con verifica sha256 del allegato di pagamento "F24"
    When viene richiesto il download del documento "F24"
    Then il download si conclude correttamente



  #74 Destinatario -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata
  #Verifica dello sha256 non possibile perché il file viene. generato on demand
  @pagamentiMultipli @ignore
  Scenario: [B2B-PA-PAY_MULTI_74]  Destinatario -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NULL               |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_f24       | SI                 |
      | payment_multy_number | 1                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
   # Then si verifica la corretta acquisizione della notifica con verifica sha256 del allegato di pagamento "F24"
    #viene fatta la stessa verifica sullo Sha256
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Cucumber"

#TODO SOLO TM....
  #75 PA -  Visualizzazione Box Allegati Modelli F24


   #76 Destinatario -  Download PAGOPA/F24 con AppIO
  @pagamentiMultipli @appIo
  Scenario: [B2B-PA-PAY_MULTI_76] Invio notifica con api b2b e recupero documento di pagamento PAGOPA con AppIO
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_f24       | NO   |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO da "Mario Cucumber"
    And il download non ha prodotto errori

  @pagamentiMultipli @appIo
  Scenario: [B2B-PA-PAY_MULTI_76_1] Invio notifica con api b2b e recupero documento di pagamento F24 con AppIO
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI                 |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_f24       | SI                 |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 1                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then il documento di pagamento "F24" può essere recuperata tramite AppIO da "Mario Cucumber"
    And il download non ha prodotto errori
   # And la notifica può essere recuperata tramite AppIO

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_77] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento PagoPA (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_77_1] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento F24 (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NULL               |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_f24       | SI                 |
      | payment_multy_number | 1                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When "Mario Cucumber" tenta il recupero dell'allegato "F24"
    Then il download ha prodotto un errore con status code "404"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_78] Destinatario Multi PF: dettaglio notifica annullata - download bollettini di pagamento PagoPA (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NULL |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"
    And "Mario Gherkin" tenta il recupero dell'allegato "PAGOPA"
    And il download ha prodotto un errore con status code "404"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_78_1] Destinatario Multi PF: dettaglio notifica annullata - download bollettini di pagamento F24 (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NULL               |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_f24       | SI                 |
      | payment_multy_number | 1                  |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                 |
      | payment_f24flatRate  | NULL                 |
      | payment_f24standard  | SI                   |
      | title_payment        | F24_STANDARD_GHERKIN |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When "Mario Cucumber" tenta il recupero dell'allegato "F24"
    Then il download ha prodotto un errore con status code "404"
    And "Mario Gherkin" tenta il recupero dell'allegato "F24"
    And il download ha prodotto un errore con status code "404"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_79] PA mittente: dettaglio notifica annullata - download bollettini di pagamento PagoPA (scenari positivi)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI   |
      | payment_f24flatRate  | NULL |
      | payment_f24standard  | NUL  |
      | apply_cost_pagopa    | SI   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And viene verificato il costo = "100" della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_79_1] PA mittente: dettaglio notifica annullata - download bollettini di pagamento F24 (scenari positivi)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NULL               |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_f24       | SI                 |
      | payment_multy_number | 1                  |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When viene richiesto il download del documento "F24"
    Then l'operazione ha generato un errore con status code "404"



#-------------------------TEST CARTACEO---------------------------------



  @pagamentiMultipli @cartaceoF24 @mockPec @failPecUat
  Scenario: [B2B-PA-PAY_MULTI_80] PA - Invio RS DELIVERY_MODE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via Roma     |
      | payment_pagoPaForm      | SI           |
      | payment_f24flatRate     | NULL         |
      | payment_f24standard     | NULL         |
      | apply_cost_pagopa       | SI           |
      | apply_cost_f24          | NO           |
      | payment_multy_number    | 1            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"


  @pagamentiMultipli @cartaceoF24 @mockPec @failPecUat
  Scenario: [B2B-PA-PAY_MULTI_80_1_1] PA - Invio RS DELIVERY_MODE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it    |
      | physicalAddress_address | Via@ok-Retry_RS |
      | payment_pagoPaForm      | SI              |
      | payment_f24flatRate     | NULL            |
      | payment_f24standard     | NULL            |
      | apply_cost_pagopa       | SI              |
      | apply_cost_f24          | NO              |
      | payment_multy_number    | 1               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"


  @pagamentiMultipli @cartaceoF24 @mockPec @failPecUat
  Scenario: [B2B-PA-PAY_MULTI_80_2] PA - Invio RS DELIVERY_MODE con Costi inclusi - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via Roma     |
      | payment_pagoPaForm      | SI           |
      | payment_f24flatRate     | NULL         |
      | payment_f24standard     | NULL         |
      | apply_cost_pagopa       | SI           |
      | apply_cost_f24          | NO           |
      | payment_multy_number    | 1            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"


  @pagamentiMultipli @cartaceoF24 @mockPec @failPecUat
  Scenario: [B2B-PA-PAY_MULTI_80_3] PA - Invio RS FLAT_RATE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità FLAT_RATE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via Roma     |
      | payment_pagoPaForm      | SI           |
      | payment_f24flatRate     | NULL         |
      | payment_f24standard     | NULL         |
      | apply_cost_pagopa       | NO           |
      | apply_cost_f24          | NO           |
      | payment_multy_number    | 1            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"


  @pagamentiMultipli @cartaceoF24 @mockPec @failPecUat
  Scenario: [B2B-PA-PAY_MULTI_80_4] PA - Invio RS DELIVERY_MODE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it                  |
      | physicalAddress_address | Via Roma                      |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"


  @pagamentiMultipli @cartaceoF24 @mockPec @failPecUat
  Scenario: [B2B-PA-PAY_MULTI_80_5] PA - Invio RS DELIVERY_MODE Costi inclusi - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi più paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it                  |
      | physicalAddress_address | Via Roma                      |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"


  @pagamentiMultipli @cartaceoF24 @mockPec @failPecUat
  Scenario: [B2B-PA-PAY_MULTI_80_6] PA - Invio RS DELIVERY_MODE Costi inclusi - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it                  |
      | physicalAddress_address | Via@ok-Retry_RS               |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"


  @pagamentiMultipli @cartaceoF24 @mockPec @failPecUat
  Scenario: [B2B-PA-PAY_MULTI_80_7] PA - Invio RS FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it              |
      | physicalAddress_address | Via Roma                  |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"


  @pagamentiMultipli @cartaceoF24 @mockPec @failPecUat
  Scenario: [B2B-PA-PAY_MULTI_80_8] PA - Invio RS FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi più paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it              |
      | physicalAddress_address | Via Roma                  |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81] PA - Invio AR DELIVERY_MODE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
      | payment_pagoPaForm      | SI        |
      | payment_f24flatRate     | NULL      |
      | payment_f24standard     | NULL      |
      | apply_cost_pagopa       | SI        |
      | apply_cost_f24          | NO        |
      | payment_multy_number    | 1         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_1] PA - Invio AR DELIVERY_MODE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
      | payment_pagoPaForm      | SI        |
      | payment_f24flatRate     | NULL      |
      | payment_f24standard     | NULL      |
      | apply_cost_pagopa       | SI        |
      | apply_cost_f24          | NO        |
      | payment_multy_number    | 1         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_2] PA - Invio AR FLAT_RATE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità FLAT_RATE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
      | payment_pagoPaForm      | SI        |
      | payment_f24flatRate     | NULL      |
      | payment_f24standard     | NULL      |
      | apply_cost_pagopa       | NO        |
      | apply_cost_f24          | NO        |
      | payment_multy_number    | 1         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_3] PA - Invio AR DELIVERY_MODE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via@ok_AR                     |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_4] PA - Invio AR DELIVERY_MODE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via@ok_AR                     |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_5] PA - Invio AR FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@ok_AR                 |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_6] PA - Invio AR FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non  inclusi più paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@ok_AR                 |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_7] PA - Invio AR FLAT_RATE  -  Invio ad indirizzo fisico fallimento al primo tentativo e successo al secondo tentativo costi di notifica non inclusi più paFee
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | FLAT_RATE                   |
      | paFee                 | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | via@fail-Discovery_AR     |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_8] PA - Invio AR FLAT_RATE -  Invio ad indirizzo fisico fallimento al primo tentativo e successo al secondo tentativo e costi di notifica non inclusi
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | FLAT_RATE                   |
      | paFee                 | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | via@fail-Discovery_AR     |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_9] PA - Invio AR DELIVERY_MODE -  Invio ad indirizzo fisico fallimento al primo tentativo e successo al secondo tentativo e costi di notifica inclusi più paFee
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | via@fail-Discovery_AR         |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_10] PA - Invio AR DELIVERY_MODE  -  Invio ad indirizzo fisico fallimento al primo tentativo e successo al secondo tentativo e costi di notifica inclusi
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | via@fail-Discovery_AR         |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_11] PA - Invio AR DELIVERY_MODE  -  Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Irreperibile_AR_scenario positivo e costi di notifica inclusi
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di Palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | paFee                 | 0                               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | via@fail-Discovery_AR         |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_82] PA - Invio 890 DELIVERY_MODE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | payment_f24flatRate     | NULL       |
      | payment_f24standard     | NULL       |
      | apply_cost_pagopa       | SI         |
      | apply_cost_f24          | NO         |
      | payment_multy_number    | 1          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_82_1] PA - Invio 890 DELIVERY_MODE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | payment_f24flatRate     | NULL       |
      | payment_f24standard     | NULL       |
      | apply_cost_pagopa       | SI         |
      | apply_cost_f24          | NO         |
      | payment_multy_number    | 1          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_82_2] PA - Invio 890 FLAT_RATE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità FLAT_RATE (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | payment_f24flatRate     | NULL       |
      | payment_f24standard     | NULL       |
      | apply_cost_pagopa       | NO         |
      | apply_cost_f24          | NO         |
      | payment_multy_number    | 1          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_82_3] PA - Invio 890 DELIVERY_MODE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via@ok_890                    |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_82_4] PA - Invio 890 DELIVERY_MODE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi più paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via@ok_890                    |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_82_5] PA - Invio 890 FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@ok_890                |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_82_6] PA - Invio 890 FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non  inclusi più paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@ok_890                |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_83] PA - inserimento notifica mono destinatario con un solo F24 SEMPLIFICATO DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                       |
      | apply_cost_pagopa                 | NULL                                       |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                       |
      | payment_f24standard               | NULL                                       |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | SI                                         |
      | payment_f24standard_inps          | NULL                                       |
      | payment_f24standard_local         | NULL                                       |
      | payment_f24standard_region        | NULL                                       |
      | payment_f24standard_treasury      | NULL                                       |
      | payment_f24standard_social        | NULL                                       |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                       |
      | payment_f24standard_inps_flat     | NULL                                       |
      | payment_f24standard_local_flat    | NULL                                       |
      | payment_f24standard_region_flat   | NULL                                       |
      | payment_f24standard_treasury_flat | NULL                                       |
      | payment_f24standard_social_flat   | NULL                                       |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_SEMPLIFICATO_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                         |
      | payment_multy_number              | 1                                          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_83_1] PA - inserimento notifica mono destinatario con un solo F24 SEMPLIFICATO DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi + paFee ).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                       |
      | apply_cost_pagopa                 | NULL                                       |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                       |
      | payment_f24standard               | NULL                                       |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | SI                                         |
      | payment_f24standard_inps          | NULL                                       |
      | payment_f24standard_local         | NULL                                       |
      | payment_f24standard_region        | NULL                                       |
      | payment_f24standard_treasury      | NULL                                       |
      | payment_f24standard_social        | NULL                                       |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                       |
      | payment_f24standard_inps_flat     | NULL                                       |
      | payment_f24standard_local_flat    | NULL                                       |
      | payment_f24standard_region_flat   | NULL                                       |
      | payment_f24standard_treasury_flat | NULL                                       |
      | payment_f24standard_social_flat   | NULL                                       |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_SEMPLIFICATO_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                         |
      | payment_multy_number              | 1                                          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_83_2] PA - inserimento notifica mono destinatario con un solo F24 SEMPLIFICATO DELIVERY_FLAT  e controllo coerenza dei dati del modello F24 (Costi di notifica non inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                            |
      | apply_cost_pagopa                 | NULL                                            |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                            |
      | payment_f24standard               | NULL                                            |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                            |
      | payment_f24standard_inps          | NULL                                            |
      | payment_f24standard_local         | NULL                                            |
      | payment_f24standard_region        | NULL                                            |
      | payment_f24standard_treasury      | NULL                                            |
      | payment_f24standard_social        | NULL                                            |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | SI                                              |
      | payment_f24standard_inps_flat     | NULL                                            |
      | payment_f24standard_local_flat    | NULL                                            |
      | payment_f24standard_region_flat   | NULL                                            |
      | payment_f24standard_treasury_flat | NULL                                            |
      | payment_f24standard_social_flat   | NULL                                            |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_SEMPLIFICATO_FLAT_CLMCST42R12D969Z |
      | apply_cost_f24                    | NO                                              |
      | payment_multy_number              | 1                                               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_83_3] PA - inserimento notifica mono destinatario con un solo F24 SEMPLIFICATO DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi)-ID code empty if the tax code is present.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm       | NULL                      |
      | apply_cost_pagopa        | NULL                      |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified_1 | SI                        |
      #-------------------------------------------
      | title_payment            | F24_STANDARD_SEMPLIFICATO |
      | apply_cost_f24           | SI                        |
      | payment_multy_number     | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_83_4] PA - inserimento notifica mono destinatario con un solo F24 SEMPLIFICATO DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi)-Invalid tax code: it not corresponds to other personal data.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm          | NULL                      |
      | apply_cost_pagopa           | NULL                      |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified_err1 | SI                        |
      #-------------------------------------------
      | title_payment               | F24_STANDARD_SEMPLIFICATO |
      | apply_cost_f24              | SI                        |
      | payment_multy_number        | 1                         |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_83_5] PA - inserimento notifica mono destinatario con un solo F24 SEMPLIFICATO DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi)-Invalid tax code: it not corresponds to other personal data.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm          | NULL                      |
      | apply_cost_pagopa           | NULL                      |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified_err2 | SI                        |
      #-------------------------------------------
      | title_payment               | F24_STANDARD_SEMPLIFICATO |
      | apply_cost_f24              | SI                        |
      | payment_multy_number        | 1                         |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_83_6] PA - inserimento notifica mono destinatario con un solo F24 SEMPLIFICATO DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi)-taxCode e comune non cogruente.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm          | NULL                      |
      | apply_cost_pagopa           | NULL                      |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified_err3 | SI                        |
      #-------------------------------------------
      | title_payment               | F24_STANDARD_SEMPLIFICATO |
      | apply_cost_f24              | SI                        |
      | payment_multy_number        | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_84] PA - inserimento notifica mono destinatario con un solo F24 INPS DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                               |
      | apply_cost_pagopa                 | NULL                               |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                               |
      | payment_f24standard               | NULL                               |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                               |
      | payment_f24standard_inps          | SI                                 |
      | payment_f24standard_local         | NULL                               |
      | payment_f24standard_region        | NULL                               |
      | payment_f24standard_treasury      | NULL                               |
      | payment_f24standard_social        | NULL                               |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                               |
      | payment_f24standard_inps_flat     | NULL                               |
      | payment_f24standard_local_flat    | NULL                               |
      | payment_f24standard_region_flat   | NULL                               |
      | payment_f24standard_treasury_flat | NULL                               |
      | payment_f24standard_social_flat   | NULL                               |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_INPS_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                 |
      | payment_multy_number              | 1                                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_84_1] PA - inserimento notifica mono destinatario con un solo F24 INPS DELIVERY_FLAT  e controllo coerenza dei dati del modello F24 (Costi di notifica non inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                    |
      | apply_cost_pagopa                 | NULL                                    |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                    |
      | payment_f24standard               | NULL                                    |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                    |
      | payment_f24standard_inps          | NULL                                    |
      | payment_f24standard_local         | NULL                                    |
      | payment_f24standard_region        | NULL                                    |
      | payment_f24standard_treasury      | NULL                                    |
      | payment_f24standard_social        | NULL                                    |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                    |
      | payment_f24standard_inps_flat     | SI                                      |
      | payment_f24standard_local_flat    | NULL                                    |
      | payment_f24standard_region_flat   | NULL                                    |
      | payment_f24standard_treasury_flat | NULL                                    |
      | payment_f24standard_social_flat   | NULL                                    |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_INPS_FLAT_CLMCST42R12D969Z |
      | apply_cost_f24                    | NO                                      |
      | payment_multy_number              | 1                                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_84_2] PA - inserimento notifica mono destinatario con un solo F24 INPS DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi e applyCost=true su tutti i record) scenario negativo.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                   |
      | apply_cost_pagopa                 | NULL                                   |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                   |
      | payment_f24standard               | NULL                                   |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                   |
      | payment_f24standard_inps          | NULL                                   |
      | payment_f24standard_inps_err      | SI                                     |
      | payment_f24standard_local         | NULL                                   |
      | payment_f24standard_region        | NULL                                   |
      | payment_f24standard_treasury      | NULL                                   |
      | payment_f24standard_social        | NULL                                   |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                   |
      | payment_f24standard_inps_flat     | NULL                                   |
      | payment_f24standard_local_flat    | NULL                                   |
      | payment_f24standard_region_flat   | NULL                                   |
      | payment_f24standard_treasury_flat | NULL                                   |
      | payment_f24standard_social_flat   | NULL                                   |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_INPS_ERR_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                     |
      | payment_multy_number              | 1                                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_84_3] PA - inserimento notifica mono destinatario con un solo F24 INPS DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi e applyCost=false su tutti i record ) scenario negativo.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                    |
      | apply_cost_pagopa                 | NULL                                    |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                    |
      | payment_f24standard               | NULL                                    |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                    |
      | payment_f24standard_inps          | NULL                                    |
      | payment_f24standard_inps_err      | NULL                                    |
      | payment_f24standard_inps_err1     | SI                                      |
      | payment_f24standard_local         | NULL                                    |
      | payment_f24standard_region        | NULL                                    |
      | payment_f24standard_treasury      | NULL                                    |
      | payment_f24standard_social        | NULL                                    |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                    |
      | payment_f24standard_inps_flat     | NULL                                    |
      | payment_f24standard_local_flat    | NULL                                    |
      | payment_f24standard_region_flat   | NULL                                    |
      | payment_f24standard_treasury_flat | NULL                                    |
      | payment_f24standard_social_flat   | NULL                                    |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_INPS_ERR1_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                      |
      | payment_multy_number              | 1                                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_85] PA - inserimento notifica mono destinatario con un solo F24 LOCAL DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                |
      | apply_cost_pagopa                 | NULL                                |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                |
      | payment_f24standard               | NULL                                |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                |
      | payment_f24standard_inps          | NULL                                |
      | payment_f24standard_local         | SI                                  |
      | payment_f24standard_region        | NULL                                |
      | payment_f24standard_treasury      | NULL                                |
      | payment_f24standard_social        | NULL                                |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                |
      | payment_f24standard_inps_flat     | NULL                                |
      | payment_f24standard_local_flat    | NULL                                |
      | payment_f24standard_region_flat   | NULL                                |
      | payment_f24standard_treasury_flat | NULL                                |
      | payment_f24standard_social_flat   | NULL                                |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_LOCAL_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                  |
      | payment_multy_number              | 1                                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_85_1] PA - inserimento notifica mono destinatario con un solo F24 LOCAL DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi + paFee ).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                |
      | apply_cost_pagopa                 | NULL                                |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                |
      | payment_f24standard               | NULL                                |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                |
      | payment_f24standard_inps          | NULL                                |
      | payment_f24standard_local         | SI                                  |
      | payment_f24standard_region        | NULL                                |
      | payment_f24standard_treasury      | NULL                                |
      | payment_f24standard_social        | NULL                                |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                |
      | payment_f24standard_inps_flat     | NULL                                |
      | payment_f24standard_local_flat    | NULL                                |
      | payment_f24standard_region_flat   | NULL                                |
      | payment_f24standard_treasury_flat | NULL                                |
      | payment_f24standard_social_flat   | NULL                                |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_LOCAL_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                  |
      | payment_multy_number              | 1                                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_85_2] PA - inserimento notifica mono destinatario con un solo F24 LOCAL DELIVERY_FLAT  e controllo coerenza dei dati del modello F24 (Costi di notifica non inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                     |
      | apply_cost_pagopa                 | NULL                                     |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                     |
      | payment_f24standard               | NULL                                     |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                     |
      | payment_f24standard_inps          | NULL                                     |
      | payment_f24standard_local         | NULL                                     |
      | payment_f24standard_region        | NULL                                     |
      | payment_f24standard_treasury      | NULL                                     |
      | payment_f24standard_social        | NULL                                     |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                     |
      | payment_f24standard_inps_flat     | NULL                                     |
      | payment_f24standard_local_flat    | SI                                       |
      | payment_f24standard_region_flat   | NULL                                     |
      | payment_f24standard_treasury_flat | NULL                                     |
      | payment_f24standard_social_flat   | NULL                                     |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_LOCAL_FLAT_CLMCST42R12D969Z |
      | apply_cost_f24                    | NO                                       |
      | payment_multy_number              | 1                                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_86] PA - inserimento notifica mono destinatario con un solo F24 REGION DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                 |
      | apply_cost_pagopa                 | NULL                                 |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                 |
      | payment_f24standard               | NULL                                 |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                 |
      | payment_f24standard_inps          | NULL                                 |
      | payment_f24standard_local         | NULL                                 |
      | payment_f24standard_region        | SI                                   |
      | payment_f24standard_treasury      | NULL                                 |
      | payment_f24standard_social        | NULL                                 |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                 |
      | payment_f24standard_inps_flat     | NULL                                 |
      | payment_f24standard_local_flat    | NULL                                 |
      | payment_f24standard_region_flat   | NULL                                 |
      | payment_f24standard_treasury_flat | NULL                                 |
      | payment_f24standard_social_flat   | NULL                                 |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_REGION_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                   |
      | payment_multy_number              | 1                                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_86_1] PA - inserimento notifica mono destinatario con un solo F24 REGION DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi + paFee ).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                 |
      | apply_cost_pagopa                 | NULL                                 |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                 |
      | payment_f24standard               | NULL                                 |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                 |
      | payment_f24standard_inps          | NULL                                 |
      | payment_f24standard_local         | NULL                                 |
      | payment_f24standard_region        | SI                                   |
      | payment_f24standard_treasury      | NULL                                 |
      | payment_f24standard_social        | NULL                                 |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                 |
      | payment_f24standard_inps_flat     | NULL                                 |
      | payment_f24standard_local_flat    | NULL                                 |
      | payment_f24standard_region_flat   | NULL                                 |
      | payment_f24standard_treasury_flat | NULL                                 |
      | payment_f24standard_social_flat   | NULL                                 |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_REGION_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                   |
      | payment_multy_number              | 1                                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_86_2] PA - inserimento notifica mono destinatario con un solo F24 REGION DELIVERY_FLAT  e controllo coerenza dei dati del modello F24 (Costi di notifica non inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                      |
      | apply_cost_pagopa                 | NULL                                      |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                      |
      | payment_f24standard               | NULL                                      |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                      |
      | payment_f24standard_inps          | NULL                                      |
      | payment_f24standard_local         | NULL                                      |
      | payment_f24standard_region        | NULL                                      |
      | payment_f24standard_treasury      | NULL                                      |
      | payment_f24standard_social        | NULL                                      |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                      |
      | payment_f24standard_inps_flat     | NULL                                      |
      | payment_f24standard_local_flat    | NULL                                      |
      | payment_f24standard_region_flat   | SI                                        |
      | payment_f24standard_treasury_flat | NULL                                      |
      | payment_f24standard_social_flat   | NULL                                      |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_REGION_FLAT_CLMCST42R12D969Z |
      | apply_cost_f24                    | NO                                        |
      | payment_multy_number              | 1                                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

#--------------------------------------TEST VERIFICA SCRITTURA F24-------------------------


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_87] PA - inserimento notifica mono destinatario con un solo F24 SOCIAL DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                 |
      | apply_cost_pagopa                 | NULL                                 |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                 |
      | payment_f24standard               | NULL                                 |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                 |
      | payment_f24standard_inps          | NULL                                 |
      | payment_f24standard_local         | NULL                                 |
      | payment_f24standard_region        | NULL                                 |
      | payment_f24standard_treasury      | NULL                                 |
      | payment_f24standard_social        | SI                                   |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                 |
      | payment_f24standard_inps_flat     | NULL                                 |
      | payment_f24standard_local_flat    | NULL                                 |
      | payment_f24standard_region_flat   | NULL                                 |
      | payment_f24standard_treasury_flat | NULL                                 |
      | payment_f24standard_social_flat   | NULL                                 |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_SOCIAL_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                   |
      | payment_multy_number              | 1                                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_87_1] PA - inserimento notifica mono destinatario con un solo F24 SOCIAL DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi + paFee ).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                 |
      | apply_cost_pagopa                 | NULL                                 |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                 |
      | payment_f24standard               | NULL                                 |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                 |
      | payment_f24standard_inps          | NULL                                 |
      | payment_f24standard_local         | NULL                                 |
      | payment_f24standard_region        | NULL                                 |
      | payment_f24standard_treasury      | NULL                                 |
      | payment_f24standard_social        | SI                                   |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                 |
      | payment_f24standard_inps_flat     | NULL                                 |
      | payment_f24standard_local_flat    | NULL                                 |
      | payment_f24standard_region_flat   | NULL                                 |
      | payment_f24standard_treasury_flat | NULL                                 |
      | payment_f24standard_social_flat   | NULL                                 |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_SOCIAL_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                   |
      | payment_multy_number              | 1                                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_87_2] PA - inserimento notifica mono destinatario con un solo F24 SOCIAL DELIVERY_FLAT  e controllo coerenza dei dati del modello F24 (Costi di notifica non inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                      |
      | apply_cost_pagopa                 | NULL                                      |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                      |
      | payment_f24standard               | NULL                                      |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                      |
      | payment_f24standard_inps          | NULL                                      |
      | payment_f24standard_local         | NULL                                      |
      | payment_f24standard_region        | NULL                                      |
      | payment_f24standard_treasury      | NULL                                      |
      | payment_f24standard_social        | NULL                                      |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                      |
      | payment_f24standard_inps_flat     | NULL                                      |
      | payment_f24standard_local_flat    | NULL                                      |
      | payment_f24standard_region_flat   | NULL                                      |
      | payment_f24standard_treasury_flat | NULL                                      |
      | payment_f24standard_social_flat   | SI                                        |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_SOCIAL_FLAT_CLMCST42R12D969Z |
      | apply_cost_f24                    | NO                                        |
      | payment_multy_number              | 1                                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_88] PA - inserimento notifica mono destinatario con un solo F24 TREASURY DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                   |
      | apply_cost_pagopa                 | NULL                                   |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                   |
      | payment_f24standard               | NULL                                   |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                   |
      | payment_f24standard_inps          | NULL                                   |
      | payment_f24standard_local         | NULL                                   |
      | payment_f24standard_region        | NULL                                   |
      | payment_f24standard_treasury      | SI                                     |
      | payment_f24standard_social        | NULL                                   |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                   |
      | payment_f24standard_inps_flat     | NULL                                   |
      | payment_f24standard_local_flat    | NULL                                   |
      | payment_f24standard_region_flat   | NULL                                   |
      | payment_f24standard_treasury_flat | NULL                                   |
      | payment_f24standard_social_flat   | NULL                                   |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_TREASURY_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                     |
      | payment_multy_number              | 1                                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_88_1] PA - inserimento notifica mono destinatario con un solo F24 TREASURY DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi + paFee ).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                   |
      | apply_cost_pagopa                 | NULL                                   |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                   |
      | payment_f24standard               | NULL                                   |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                   |
      | payment_f24standard_inps          | NULL                                   |
      | payment_f24standard_local         | NULL                                   |
      | payment_f24standard_region        | NULL                                   |
      | payment_f24standard_treasury      | SI                                     |
      | payment_f24standard_social        | NULL                                   |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                   |
      | payment_f24standard_inps_flat     | NULL                                   |
      | payment_f24standard_local_flat    | NULL                                   |
      | payment_f24standard_region_flat   | NULL                                   |
      | payment_f24standard_treasury_flat | NULL                                   |
      | payment_f24standard_social_flat   | NULL                                   |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_TREASURY_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                     |
      | payment_multy_number              | 1                                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_88_2] PA - inserimento notifica mono destinatario con un solo F24 TREASURY DELIVERY_FLAT  e controllo coerenza dei dati del modello F24 (Costi di notifica non inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                        |
      | apply_cost_pagopa                 | NULL                                        |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                        |
      | payment_f24standard               | NULL                                        |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                                        |
      | payment_f24standard_inps          | NULL                                        |
      | payment_f24standard_local         | NULL                                        |
      | payment_f24standard_region        | NULL                                        |
      | payment_f24standard_treasury      | NULL                                        |
      | payment_f24standard_social        | NULL                                        |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                                        |
      | payment_f24standard_inps_flat     | NULL                                        |
      | payment_f24standard_local_flat    | NULL                                        |
      | payment_f24standard_region_flat   | NULL                                        |
      | payment_f24standard_treasury_flat | SI                                          |
      | payment_f24standard_social_flat   | NULL                                        |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_TREASURY_FLAT_CLMCST42R12D969Z |
      | apply_cost_f24                    | NO                                          |
      | payment_multy_number              | 1                                           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_88_3] PA - inserimento notifica mono destinatario con un solo F24 TREASURY_AE DELIVERY_MODE  e controllo coerenza dei dati del modello F24 Agenzia delle Entrate (Costi di notifica inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                                      |
      | apply_cost_pagopa                 | NULL                                      |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                                      |
      | payment_f24standard               | NULL                                      |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | f24_delivery_standard_treasury_ae | SI                                        |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_TREASURY_AE_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                        |
      | payment_multy_number              | 1                                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_88_4] PA - inserimento notifica mono destinatario con un solo F24 TREASURY_AE FLAT_RATE  e controllo coerenza dei dati del modello F24 Agenzia delle Entrate (Costi di notifica non inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                     | NULL                                      |
      | apply_cost_pagopa                      | NULL                                      |
      #F24 completo-------------------------------
      | payment_f24flatRate                    | NULL                                      |
      | payment_f24standard                    | NULL                                      |
      #F24 completo a sezioni modalità Delivery - costi non inclusi--
      | f24_delivery_standard_treasury_ae_flat | SI                                        |
      #-------------------------------------------
      | title_payment                          | F24_STANDARD_TREASURY_AE_CLMCST42R12D969Z |
      | apply_cost_f24                         | NO                                        |
      | payment_multy_number                   | 1                                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_88_5] PA - inserimento notifica mono destinatario con un solo F24 TREASURY_AE FLAT_RATE  e controllo coerenza dei dati del modello F24 Agenzia delle Entrate (Costi di notifica non inclusi e credit e debit valorizzati sullo stesso record - scenario negativo).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                         | NULL                                      |
      | apply_cost_pagopa                          | NULL                                      |
      #F24 completo-------------------------------
      | payment_f24flatRate                        | NULL                                      |
      | payment_f24standard                        | NULL                                      |
      #F24 completo a sezioni modalità Delivery - costi non inclusi--
      | f24_delivery_standard_treasury_ae_err_flat | SI                                        |
      #-------------------------------------------
      | title_payment                              | F24_STANDARD_TREASURY_AE_CLMCST42R12D969Z |
      | apply_cost_f24                             | NO                                        |
      | payment_multy_number                       | 1                                         |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @pagamentiMultipli @ignore
  Scenario: [B2B-PA-PAY_MULTI_90] PA - inserimento notifica mono destinatario con più avvisi pagoPA e  F24 (almeno 2) e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario
      | denomination         | Matteo Rossi                  |
      | taxId                | AAAAAA00A00A000C              |
      | payment_pagoPaForm   | SI                            |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_AAAAAA00A00A000C |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And viene verificato il costo = "100" della notifica

  @pagamentiMultipli @ignore
  Scenario: [B2B-PA-PAY_MULTI_90_1] PA - inserimento notifica mono destinatario con più avvisi pagoPA e  F24 (almeno 2) e costi inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario
      | denomination         | Matteo Rossi              |
      | taxId                | AAAAAA00A00A000C          |
      | payment_pagoPaForm   | NULL                      |
      | payment_f24flatRate  | NULL                      |
      | payment_f24standard  | SI                        |
      | title_payment        | F24_TARI_AAAAAA00A00A000C |
      | apply_cost_pagopa    | SI                        |
      | apply_cost_f24       | SI                        |
      | payment_multy_number | 4                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
   # And viene verificato il costo = "100" della notifica


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_91] PA - inserimento notifica mono destinatario con un solo F24 INPS DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi + paFee ).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL                               |
      | apply_cost_pagopa                 | NULL                               |
      #F24 completo-------------------------------
      | payment_f24flatRate               | NULL                               |
      | payment_f24standard               | NULL                               |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified            | NULL                               |
      | payment_f24standard_inps          | SI                                 |
      | payment_f24standard_local         | NULL                               |
      | payment_f24standard_region        | NULL                               |
      | payment_f24standard_treasury      | NULL                               |
      | payment_f24standard_social        | NULL                               |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat       | NULL                               |
      | payment_f24standard_inps_flat     | NULL                               |
      | payment_f24standard_local_flat    | NULL                               |
      | payment_f24standard_region_flat   | NULL                               |
      | payment_f24standard_treasury_flat | NULL                               |
      | payment_f24standard_social_flat   | NULL                               |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_INPS_CLMCST42R12D969Z |
      | apply_cost_f24                    | SI                                 |
      | payment_multy_number              | 1                                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_91_1] PA - inserimento notifica mono destinatario con un solo F24 INPS DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi + paFee ).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                      | NULL                                  |
      | apply_cost_pagopa                       | NULL                                  |
      #F24 completo-------------------------------
      | payment_f24flatRate                     | NULL                                  |
      | payment_f24standard                     | NULL                                  |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified                  | NULL                                  |
      | payment_f24standard_inps                | NULL                                  |
      | payment_f24standard_inps_debit_credit   | SI                                    |
      | payment_f24standard_inps_debit_credit_1 | NULL                                  |
      | payment_f24standard_local               | NULL                                  |
      | payment_f24standard_region              | NULL                                  |
      | payment_f24standard_treasury            | NULL                                  |
      | payment_f24standard_social              | NULL                                  |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat             | NULL                                  |
      | payment_f24standard_inps_flat           | NULL                                  |
      | payment_f24standard_local_flat          | NULL                                  |
      | payment_f24standard_region_flat         | NULL                                  |
      | payment_f24standard_treasury_flat       | NULL                                  |
      | payment_f24standard_social_flat         | NULL                                  |
      #-------------------------------------------
      | title_payment                           | F24_STANDARD_INPS_DC_CLMCST42R12D969Z |
      | apply_cost_f24                          | SI                                    |
      | payment_multy_number                    | 1                                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_92] PA - inserimento notifica mono destinatario con un solo F24 INPS DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi + paFee su debit di importo 0 ).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                      | NULL                                   |
      | apply_cost_pagopa                       | NULL                                   |
      #F24 completo-------------------------------
      | payment_f24flatRate                     | NULL                                   |
      | payment_f24standard                     | NULL                                   |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24_simplified                  | NULL                                   |
      | payment_f24standard_inps                | NULL                                   |
      | payment_f24standard_inps_debit_credit   | NULL                                   |
      | payment_f24standard_inps_debit_credit_1 | SI                                     |
      | payment_f24standard_local               | NULL                                   |
      | payment_f24standard_region              | NULL                                   |
      | payment_f24standard_treasury            | NULL                                   |
      | payment_f24standard_social              | NULL                                   |
      #F24 completo a sezioni modalità Flat------ costi non inclusi--
      | payment_f24_simplified_flat             | NULL                                   |
      | payment_f24standard_inps_flat           | NULL                                   |
      | payment_f24standard_local_flat          | NULL                                   |
      | payment_f24standard_region_flat         | NULL                                   |
      | payment_f24standard_treasury_flat       | NULL                                   |
      | payment_f24standard_social_flat         | NULL                                   |
      #-------------------------------------------
      | title_payment                           | F24_STANDARD_INPS_DC1_CLMCST42R12D969Z |
      | apply_cost_f24                          | SI                                     |
      | payment_multy_number                    | 1                                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

      #--------------------------DIGITALE---------------------------------
  @pagamentiMultipli @digitaleF24 @mockPec
  Scenario: [B2B-PA-PAY_MULTI_93] PA - Invio pec DELIVERY_MODE  -   Invio notifica DIGITAL_DELIVERY_scenario positivo e costi di notifica inclusi
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it                 |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @pagamentiMultipli @digitaleF24 @realPec
  Scenario: [B2B-PA-PAY_MULTI_93_1] PA - Invio pec DELIVERY_MODE  -   Invio notifica  DIGITAL_DELIVERY_scenario positivo e costi di notifica inclusi
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | pectest@pec.pagopa.it         |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @pagamentiMultipli @digitaleF24 @mockPec
  Scenario: [B2B-PA-PAY_MULTI_93_2] PA - Invio pec DELIVERY_MODE  -   Invio notifica  DIGITAL_DELIVERY_scenario positivo  e costi di notifica inclusi più paFee
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it                 |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @pagamentiMultipli @digitaleF24 @realPec
  Scenario: [B2B-PA-PAY_MULTI_93_3] PA - Invio pec DELIVERY_MODE  -   Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo  e costi di notifica inclusi più paFee
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | feePolicy             | DELIVERY_MODE               |
      | paFee                 | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | pectest@pec.pagopa.it         |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24flatRate     | NULL                          |
      | payment_f24standard     | SI                            |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @pagamentiMultipli @digitaleF24 @mockPec
  Scenario: [B2B-PA-PAY_MULTI_93_4] PA - Invio pec FLAT_RATE  -   Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo  e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | digitalDomicile_address | test@pecOk.it             |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @pagamentiMultipli @digitaleF24 @realPec
  Scenario: [B2B-PA-PAY_MULTI_93_5] PA - Invio pec FLAT_RATE  -   Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo  e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | digitalDomicile_address | pectest@pec.pagopa.it     |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @pagamentiMultipli @digitaleF24 @mockPec
  Scenario: [B2B-PA-PAY_MULTI_93_6] PA - Invio pec  FLAT_RATE  -   Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo  e costi di notifica non inclusi più paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@pecOk.it             |
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @pagamentiMultipli @digitaleF24 @realPec
  Scenario: [B2B-PA-PAY_MULTI_93_7] PA - Invio pec  FLAT_RATE  -   Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo  e costi di notifica non inclusi più paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | pectest@pec.pagopa.it     |
      | title_payment           | F24_FLAT_CLMCST42R12D969Z |
      | payment_pagoPaForm      | NULL                      |
      | payment_f24flatRate     | SI                        |
      | payment_f24standard     | NULL                      |
      | apply_cost_pagopa       | NO                        |
      | apply_cost_f24          | NO                        |
      | payment_multy_number    | 1                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_94] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo) - senza allegato Avviso PagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NOALLEGATO |
      | payment_f24flatRate  | NULL       |
      | payment_f24standard  | NULL       |
      | apply_cost_pagopa    | SI         |
      | apply_cost_f24       | NO         |
      | payment_multy_number | 1          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_94_1] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo) - senza allegato Avviso PagoPA e verifica costo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NOALLEGATO |
      | payment_f24flatRate  | NULL       |
      | payment_f24standard  | NULL       |
      | apply_cost_pagopa    | SI         |
      | apply_cost_f24       | NO         |
      | payment_multy_number | 1          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_95] PA - inserimento notifica mono destinatario con un solo F24 STANDARD COMPLETO VALID (Lunghezza e formato)  e controllo coerenza dei dati del modello F24 Agenzia delle Entrate (Costi di notifica  inclusi).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm             | NULL            |
      | apply_cost_pagopa              | NULL            |
      #F24 completo-------------------------------
      | payment_f24standard_valid_anag | SI              |
      #-------------------------------------------
      | title_payment                  | F24_STANDARD_AE |
      | apply_cost_f24                 | SI              |
      | payment_multy_number           | 1               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_95_1] PA - inserimento notifica mono destinatario con un solo F24 STANDARD COMPLETO VALID (Lunghezza e formato) -Invalid tax code: it not corresponds to other personal data (taxcode e birthDate non congruenti ).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                | NULL            |
      | apply_cost_pagopa                 | NULL            |
      #F24 completo-------------------------------
      | payment_f24standard_no_valid_anag | SI              |
      #-------------------------------------------
      | title_payment                     | F24_STANDARD_AE |
      | apply_cost_f24                    | SI              |
      | payment_multy_number              | 1               |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_95_2] PA - inserimento notifica mono destinatario con un solo F24 STANDARD COMPLETO VALID (Lunghezza e formato) -Invalid tax code: Argument 'municipality' is not valid .
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                  | NULL            |
      | apply_cost_pagopa                   | NULL            |
      #F24 completo-------------------------------
      | payment_f24standard_no_valid_anag_1 | SI              |
      #-------------------------------------------
      | title_payment                       | F24_STANDARD_AE |
      | apply_cost_f24                      | SI              |
      | payment_multy_number                | 1               |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_95_3] PA - inserimento notifica mono destinatario con un solo F24 STANDARD COMPLETO VALID (Lunghezza e formato) -Debit Numerico di 15 - Numeric value (730927309273092) out of range of int (-2147483648 - 2147483647) .
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                  | NULL            |
      | apply_cost_pagopa                   | NULL            |
      #F24 completo-------------------------------
      | payment_f24standard_no_valid_anag_2 | SI              |
      #-------------------------------------------
      | title_payment                       | F24_STANDARD_AE |
      | apply_cost_f24                      | SI              |
      | payment_multy_number                | 1               |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_95_4] PA - inserimento notifica mono destinatario con un solo F24 STANDARD COMPLETO VALID (Lunghezza e formato) - f24Standard.inps.records[0].office must match "^[0-9]{3,4}$" .
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                  | NULL            |
      | apply_cost_pagopa                   | NULL            |
      #F24 completo-------------------------------
      | payment_f24standard_no_valid_anag_3 | SI              |
      #-------------------------------------------
      | title_payment                       | F24_STANDARD_AE |
      | apply_cost_f24                      | SI              |
      | payment_multy_number                | 1               |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_95_5] PA - inserimento notifica mono destinatario con un solo F24 STANDARD COMPLETO VALID (Lunghezza e formato) -f24Standard.localTax.records[0].municipality must match "^[0-9A-Z]{4}$" .
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                  | NULL            |
      | apply_cost_pagopa                   | NULL            |
      #F24 completo-------------------------------
      | payment_f24standard_no_valid_anag_4 | SI              |
      #-------------------------------------------
      | title_payment                       | F24_STANDARD_AE |
      | apply_cost_f24                      | SI              |
      | payment_multy_number                | 1               |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_95_6] PA - inserimento notifica mono destinatario con un solo F24 STANDARD COMPLETO VALID (Lunghezza e formato) -No valid Format scenario negativo" .
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                  | NULL            |
      | apply_cost_pagopa                   | NULL            |
      #F24 completo-------------------------------
      | payment_f24standard_no_valid_format | SI              |
      #-------------------------------------------
      | title_payment                       | F24_STANDARD_AE |
      | apply_cost_f24                      | SI              |
      | payment_multy_number                | 1               |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_95_7] PA - inserimento notifica mono destinatario con un solo F24 STANDARD COMPLETO VALID (Lunghezza e formato) -No valid Lengh scenario negativo" .
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                 | NULL            |
      | apply_cost_pagopa                  | NULL            |
      #F24 completo-------------------------------
      | payment_f24standard_no_valid_lengh | SI              |
      #-------------------------------------------
      | title_payment                      | F24_STANDARD_AE |
      | apply_cost_f24                     | SI              |
      | payment_multy_number               | 1               |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_96] PA - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi e senza paFee - PN-8906
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | NULL                        |
    And destinatario
      | denomination         | Ada                           |
      | taxId                | LVLDAA85T50G702B              |
      | payment_pagoPaForm   | NULL                          |
      | payment_f24flatRate  | NULL                          |
      | payment_f24standard  | SI                            |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | NO                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then viene richiesto il download del documento "F24"
    And il download non ha prodotto errori


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_97] PA - inserimento notifica mono destinatario con un solo F24 SEMPLIFICATO DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi)-Only one type of tax payer is allowed. - PN-9070
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm                  | NULL                      |
      | apply_cost_pagopa                   | NULL                      |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24standard_no_valid_anag_5 | SI                        |
      #-------------------------------------------
      | title_payment                       | F24_STANDARD_SEMPLIFICATO |
      | apply_cost_f24                      | SI                        |
      | payment_multy_number                | 1                         |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_98] PA - inserimento notifica mono destinatario con un solo F24 STANDARD LOCAL VALID (Lunghezza e formato TEFA-TEFN-TEFZ)  e controllo coerenza dei dati del modello F24 TARI (Costi di notifica  inclusi).-PN-9143
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm               | NULL                    |
      | apply_cost_pagopa                | NULL                    |
      #F24 local-------------------------------
      | f24_delivery_standard_local_tefa | SI                      |
      #-------------------------------------------
      | title_payment                    | F24_STANDARD_LOCAL_TARI |
      | apply_cost_f24                   | SI                      |
      | payment_multy_number             | 1                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"




