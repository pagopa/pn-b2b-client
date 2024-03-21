Feature: avanzamento notifiche b2b persona fisica multi pagamento validation

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_5] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità FLAT_RATE applyCost true (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI               |
      | payment_f24          | PAYMENT_F24_FLAT |
      | apply_cost_pagopa    | SI               |
      | payment_multy_number | 1                |
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
      | payment_pagoPaForm   | SI               |
      | payment_f24          | PAYMENT_F24_FLAT |
      | apply_cost_pagopa    | NO               |
      | payment_multy_number | 1                |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"


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
      | payment_f24          | NULL               |
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
      | payment_f24          | NULL               |
      | apply_cost_pagopa    | NO                 |
      | payment_multy_number | 1                  |
      | payment_noticeCode   | 302011697026785049 |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24          | NULL               |
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
      | payment_f24          | NULL |
      | apply_cost_pagopa    | NO   |
      | payment_multy_number | 1    |
    And destinatario
      | denomination         | Gaio Giulio Cesare |
      | taxId                | CSRGGL44L13H501E   |
      | payment_pagoPaForm   | SI                 |
      | payment_f24          | NULL               |
      | apply_cost_pagopa    | SI                 |
      | payment_multy_number | 1                  |

    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"



  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_PG_24_5] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità FLAT_RATE applyCost true (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario
      | denomination         | Convivio Spa |
      | recipientType        | PG           |
      | taxId                | 27957814470  |
      | payment_pagoPaForm   | SI           |
      | payment_f24          | NULL         |
      | apply_cost_pagopa    | SI           |
      | payment_multy_number | 1            |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"



     #64 Test di Validazione degli oggetti di pagamento ricevuti: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_PG_64] Test di Validazione degli oggetti di pagamento ricevuti: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario
      | denomination         | Convivio Spa       |
      | recipientType        | PG                 |
      | taxId                | 27957814470        |
      | payment_pagoPaForm   | SI                 |
      | payment_f24          | NULL               |
      | apply_cost_pagopa    | NO                 |
      | payment_multy_number | 2                  |
      | payment_noticeCode   | 302011697026785044 |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_PG_64_1] Test di Validazione degli oggetti di pagamento ricevuti multidestinatario: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario
      | denomination         | Convivio Spa       |
      | recipientType        | PG                 |
      | taxId                | 27957814470        |
      | payment_pagoPaForm   | SI                 |
      | payment_f24          | NULL               |
      | apply_cost_pagopa    | NO                 |
      | payment_multy_number | 1                  |
      | payment_noticeCode   | 302011697026785045 |
    And destinatario
      | denomination         | DivinaCommedia Srl |
      | recipientType        | PG                 |
      | taxId                | 70412331207        |
      | payment_pagoPaForm   | SI                 |
      | payment_f24          | NULL               |
      | apply_cost_pagopa    | NO                 |
      | payment_multy_number | 1                  |
      | payment_noticeCode   | 302011697026785045 |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"



  #-------------------------------------------------------------------------------------------------
  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_83_4] PA - inserimento notifica mono destinatario con un solo F24 SEMPLIFICATO DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi)-Invalid tax code: it not corresponds to other personal data.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm   | NULL                        |
      | apply_cost_pagopa    | NULL                        |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24          | PAYMENT_F24_SIMPLIFIED_ERR1 |
      #-------------------------------------------
      | title_payment        | F24_STANDARD_SEMPLIFICATO   |
      | apply_cost_f24       | SI                          |
      | payment_multy_number | 1                           |
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
      | payment_pagoPaForm   | NULL                        |
      | apply_cost_pagopa    | NULL                        |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24          | PAYMENT_F24_SIMPLIFIED_ERR2 |
      #-------------------------------------------
      | title_payment        | F24_STANDARD_SEMPLIFICATO   |
      | apply_cost_f24       | SI                          |
      | payment_multy_number | 1                           |
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
      | payment_pagoPaForm   | NULL                        |
      | apply_cost_pagopa    | NULL                        |
      #F24 completo a sezioni modalità Delivery - costi inclusi--
      | payment_f24          | PAYMENT_F24_SIMPLIFIED_ERR3 |
      #-------------------------------------------
      | title_payment        | F24_STANDARD_SEMPLIFICATO   |
      | apply_cost_f24       | SI                          |
      | payment_multy_number | 1                           |
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
      | payment_pagoPaForm   | NULL                                   |
      | apply_cost_pagopa    | NULL                                   |
      | payment_f24          | PAYMENT_F24_STANDARD_INPS_ERR          |
      | title_payment        | F24_STANDARD_INPS_ERR_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                                     |
      | payment_multy_number | 1                                      |
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
      | payment_pagoPaForm   | NULL                                    |
      | apply_cost_pagopa    | NULL                                    |
      | payment_f24          | PAYMENT_F24_STANDARD_INPS_ERR_1         |
      | title_payment        | F24_STANDARD_INPS_ERR1_CLMCST42R12D969Z |
      | apply_cost_f24       | SI                                      |
      | payment_multy_number | 1                                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_88_5] PA - inserimento notifica mono destinatario con un solo F24 TREASURY_AE FLAT_RATE  e controllo coerenza dei dati del modello F24 Agenzia delle Entrate (Costi di notifica non inclusi e credit e debit valorizzati sullo stesso record - scenario negativo).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | FLAT_RATE                   |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm   | NULL                                      |
      | apply_cost_pagopa    | NULL                                      |
      | payment_f24          | PAYMENT_F24_STANDARD_TREASURY_AE_ERR_FLAT |
      | title_payment        | F24_STANDARD_TREASURY_AE_CLMCST42R12D969Z |
      | apply_cost_f24       | NO                                        |
      | payment_multy_number | 1                                         |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_94] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (scenario positivo) - senza allegato Avviso PagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NOALLEGATO |
      | payment_f24          | NULL       |
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
      | payment_f24          | NULL       |
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
      | payment_pagoPaForm   | NULL                            |
      | apply_cost_pagopa    | NULL                            |
      | payment_f24          | PAYMENT_F24_STANDARD_VALID_ANAG |
      | title_payment        | F24_STANDARD_AE                 |
      | apply_cost_f24       | SI                              |
      | payment_multy_number | 1                               |
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
      | payment_pagoPaForm   | NULL                               |
      | apply_cost_pagopa    | NULL                               |
      | payment_f24          | PAYMENT_F24_STANDARD_NO_VALID_ANAG |
      | title_payment        | F24_STANDARD_AE                    |
      | apply_cost_f24       | SI                                 |
      | payment_multy_number | 1                                  |
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
      | payment_pagoPaForm   | NULL                                 |
      | apply_cost_pagopa    | NULL                                 |
      | payment_f24          | PAYMENT_F24_STANDARD_NO_VALID_ANAG_1 |
      | title_payment        | F24_STANDARD_AE                      |
      | apply_cost_f24       | SI                                   |
      | payment_multy_number | 1                                    |
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
      | payment_pagoPaForm   | NULL                                 |
      | apply_cost_pagopa    | NULL                                 |
      | payment_f24          | PAYMENT_F24_STANDARD_NO_VALID_ANAG_2 |
      | title_payment        | F24_STANDARD_AE                      |
      | apply_cost_f24       | SI                                   |
      | payment_multy_number | 1                                    |
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
      | payment_pagoPaForm   | NULL                                 |
      | apply_cost_pagopa    | NULL                                 |
      | payment_f24          | PAYMENT_F24_STANDARD_NO_VALID_ANAG_3 |
      | title_payment        | F24_STANDARD_AE                      |
      | apply_cost_f24       | SI                                   |
      | payment_multy_number | 1                                    |
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
      | payment_pagoPaForm   | NULL                                 |
      | apply_cost_pagopa    | NULL                                 |
      | payment_f24          | PAYMENT_F24_STANDARD_NO_VALID_ANAG_4 |
      | title_payment        | F24_STANDARD_AE                      |
      | apply_cost_f24       | SI                                   |
      | payment_multy_number | 1                                    |
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
      | payment_pagoPaForm   | NULL                                 |
      | apply_cost_pagopa    | NULL                                 |
      | payment_f24          | PAYMENT_F24_STANDARD_NO_VALID_FORMAT |
      | title_payment        | F24_STANDARD_AE                      |
      | apply_cost_f24       | SI                                   |
      | payment_multy_number | 1                                    |
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
      | payment_pagoPaForm   | NULL                                |
      | apply_cost_pagopa    | NULL                                |
      | payment_f24          | PAYMENT_F24_STANDARD_NO_VALID_LENGH |
      | title_payment        | F24_STANDARD_AE                     |
      | apply_cost_f24       | SI                                  |
      | payment_multy_number | 1                                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED


  @pagamentiMultipli @f24
  Scenario: [B2B-PA-PAY_MULTI_96] PA - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi e senza paFee - PN-8906
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | NULL                        |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | NULL                          |
      | payment_f24          | PAYMENT_F24_STANDARD          |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | NO                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

    #When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    #Then viene richiesto il download del documento "F24"
    #And il download non ha prodotto errori

  @pagamentiMultipli @f24 @dev
  Scenario: [B2B-PA-PAY_MULTI_97] PA - inserimento notifica mono destinatario con un solo F24 SEMPLIFICATO DELIVERY_MODE  e controllo coerenza dei dati del modello F24 (Costi di notifica inclusi)-Only one type of tax payer is allowed. - PN-9070
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm   | NULL                                 |
      | apply_cost_pagopa    | NULL                                 |
      | payment_f24          | PAYMENT_F24_STANDARD_NO_VALID_ANAG_5 |
      | title_payment        | F24_STANDARD_SEMPLIFICATO            |
      | apply_cost_f24       | SI                                   |
      | payment_multy_number | 1                                    |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED

  @pagamentiMultipli @f24 @dev
  Scenario: [B2B-PA-PAY_MULTI_98] PA - inserimento notifica mono destinatario con un solo F24 STANDARD LOCAL VALID (Lunghezza e formato TEFA-TEFN-TEFZ)  e controllo coerenza dei dati del modello F24 TARI (Costi di notifica  inclusi).-PN-9143
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm   | NULL                                     |
      | apply_cost_pagopa    | NULL                                     |
      #F24 local-------------------------------
      | payment_f24          | PAYMENT_F24_DELIVERY_STANDARD_LOCAL_TEFA |
      #-------------------------------------------
      | title_payment        | F24_STANDARD_LOCAL_TARI                  |
      | apply_cost_f24       | SI                                       |
      | payment_multy_number | 1                                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


    #------------------PN-9330------------------
  @pagamentiMultipli @f24 @dev
  Scenario: [B2B-PA-PAY_MULTI_99] PA - inserimento notifica mono destinatario con un solo F24 STANDARD  VALID MINIMAL e controllo coerenza dei dati del modello F24 (Costi di notifica  inclusi).-PN-9330
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm   | NULL                                        |
      | apply_cost_pagopa    | NULL                                        |
      #F24 local-------------------------------
      | payment_f24          | METADATO_CORRETTO_STAND_MINIMAL             |
      #-------------------------------------------
      | title_payment        | PAYMENT_F24_METADATO_CORRETTO_STAND_MINIMAL |
      | apply_cost_f24       | SI                                          |
      | payment_multy_number | 1                                           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24 @dev
  Scenario: [B2B-PA-PAY_MULTI_100] PA - inserimento notifica mono destinatario con un solo F24 SIMPLIFIED  VALID MINIMAL e controllo coerenza dei dati del modello F24 (Costi di notifica  inclusi).-PN-9330
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm   | NULL                                        |
      | apply_cost_pagopa    | NULL                                        |
      #F24 local-------------------------------
      | payment_f24          | METADATO_CORRETTO_SIMPL_MINIMAL             |
      #-------------------------------------------
      | title_payment        | PAYMENT_F24_METADATO_CORRETTO_SIMPL_MINIMAL |
      | apply_cost_f24       | SI                                          |
      | payment_multy_number | 1                                           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli @f24 @dev
  Scenario: [B2B-PA-PAY_MULTI_101] PA - inserimento notifica mono destinatario con un solo F24 EXCISE  VALID MINIMAL e controllo coerenza dei dati del modello F24 (Costi di notifica  inclusi).-PN-9330
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm   | NULL                                         |
      | apply_cost_pagopa    | NULL                                         |
      #F24 local-------------------------------
      | payment_f24          | METADATO_CORRETTO_EXCISE_MINIMAL             |
      #-------------------------------------------
      | title_payment        | PAYMENT_F24_METADATO_CORRETTO_EXCISE_MINIMAL |
      | apply_cost_f24       | SI                                           |
      | payment_multy_number | 1                                            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


  @pagamentiMultipli @f24 @dev
  Scenario: [B2B-PA-PAY_MULTI_102] PA - inserimento notifica mono destinatario con un solo F24 ELID  VALID MINIMAL e controllo coerenza dei dati del modello F24 (Costi di notifica  inclusi).-PN-9330
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      #Sezione PagoPA-----------------------------
      | payment_pagoPaForm   | NULL                                       |
      | apply_cost_pagopa    | NULL                                       |
      #F24 local-------------------------------
      | payment_f24          | METADATO_CORRETTO_ELID_MINIMAL             |
      #-------------------------------------------
      | title_payment        | PAYMENT_F24_METADATO_CORRETTO_ELID_MINIMAL |
      | apply_cost_f24       | SI                                         |
      | payment_multy_number | 1                                          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"










