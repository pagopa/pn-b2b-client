Feature: avanzamento notifiche b2b persona fisica multi pagamento


 #24 PA - inserimento notifica mono destinatario con un solo avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | DELIVERY_MODE       |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

    #Comune Palermo WMAE-WQUX-RTVH-202310-M-1 --PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (paFee=0 costo 100)

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_1] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica inclusi  modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "200" della notifica
        #Comune Palermo QZEH-UTHW-WVTK-202310-T-1 --PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica  inclusi modalità DELIVERY_MODE (paFee=100 costo 200)

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_2] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_3] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_4] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità FLAT_RATE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy             | FLAT_RATE      |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_5] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità FLAT_RATE applyCost true (scenario negativo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy             | FLAT_RATE      |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"


   #25 PA - inserimento notifica mono destinatario con un solo F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_25] PA - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_f24 | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_25_1] PA - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_f24 | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica


 #26 PA - inserimento notifica mono destinatario con più avvisi pagoPA e nessun F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26] PA - inserimento notifica mono destinatario con più avvisi pagoPA e nessun F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 2 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  #Comune Palermo UHRL-ZVPK-EJET-202310-N-1 --PA - inserimento notifica mono destinatario con più avvisi pagoPA e nessun F24

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26_1] PA - inserimento notifica mono destinatario con più avvisi pagoPA (almeno 3) e nessun F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 3 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26_2] PA - inserimento notifica mono destinatario con più avvisi pagoPA (almeno 4) e nessun F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 4 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26_3] PA - inserimento notifica mono destinatario con più avvisi pagoPA e  F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica



 #27  PA - inserimento notifica mono destinatario con più F24 e nessun avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_27] PA - inserimento notifica mono destinatario con più F24 (Almeno 2) e nessun avviso pagoPA
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_pagoPaForm_1 | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | payment_f24standard_1 | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0


  #28  PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_28] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  #29  PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_29] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica


  #30 PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_30] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica


  #31 PA - inserimento notifica multi destinatario con un solo avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_31] PA - inserimento notifica multi destinatario con un solo avviso pagoPA e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_31_1] PA - inserimento notifica multi destinatario con un solo avviso pagoPA e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "200" della notifica per l'utente 0
    And viene verificato il costo = "200" della notifica per l'utente 1


  #32 PA - inserimento notifica multi destinatario con un solo F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_32] PA - inserimento notifica multi destinatario con un solo F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_f24 | NO |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_32_1] PA - inserimento notifica multi destinatario con un solo F24 e costi di notifica inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_f24 | SI |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1


  #33 PA - inserimento notifica multi destinatario con più avvisi pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_33] PA - inserimento notifica multi destinatario con più avvisi pagoPA
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1




  #34 PA - inserimento notifica multi destinatario con più F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_32] PA - inserimento notifica multi destinatario con un solo F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | payment_f24standard_1 | SI |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | payment_f24standard_1 | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1



  #35 PA - download modello F24
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_35] PA - download modello F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI   |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "F24"
    Then il download si conclude correttamente


  #36 PA - download allegato pagoPA
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_36] PA - download allegato pagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL   |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente


   #TODO CHIEDERE INFO.............Vecchio Requisito
  #37 PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: ad ogni avviso pagoPA corrisponde un F24 [TA]




  #38 PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_38] PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1



 #TODO CHIEDERE INFO.............
  #39 PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_39] PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_F24 | SI |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_F24 | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1


#TODO VECCHIO REQUISITO
  #40 Destinatario - pagamento notifica mono destinatario con un solo avviso pagoPA: verifica stato “In elaborazione”


 #TODO VECCHIO REQUISITO
  #41 Destinatario - visualizzazione box di pagamento su notifica mono destinatario pagata - verifica della presenza stato “Pagato”



  #42 Notifica mono destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA]
  Scenario: [B2B-PA-PAY_MULTI_42] Notifica mono destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NO |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica


  #TODO TEST MANUALE.........INFO
  #43 Destinatario - notifica mono destinatario con più avvisi pagoPA: pagamento di un avviso
  Scenario: [B2B-PA-PAY_MULTI_43] Destinatario - notifica mono destinatario con più avvisi pagoPA: pagamento di un avviso
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NO |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 2 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0

  #TODO TEST MANUALE.........INFO
  #44 Destinatario - notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
  Scenario: [B2B-PA-PAY_MULTI_44] Destinatario - notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | SI |
      | apply_cost_pagopa | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica


  @pagamentiMultipli
    #TODO Non è possibile effettuare il pagamento lato Destinatario accertare il pagamento di un solo avviso...Chiudere la posizione debitoria
  Scenario: [B2B-PA-PAY_MULTI_44_1] Destinatario - notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #TODO parametrizzare lo step per avviso di pagamento.......
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica


  #45 Destinatario - download modello F24
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_45] Destinatario PF: download allegato F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When "Mario Cucumber" tenta il recupero dell'allegato "F24"
    Then il download non ha prodotto errori


  #46 Destinatario - download allegato pagoPA
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_46] Destinatario PF: download allegato pagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download non ha prodotto errori
    #Then il download si conclude correttamente


  #47 Destinatario 1 - pagamento notifica multi destinatario con un solo avviso pagoPA
  #TODO Non è possibile effettuare il pagamento lato Destinatario quindi si può solo lato PA verificare la posizione Debitoria del Destinatario
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_47] Destinatario 1 - pagamento notifica multi destinatario con un solo avviso pagoPA
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_47_1] Destinatario 1 - pagamento notifica multi destinatario con più avvisi pagoPA e con pagamento di un solo avviso
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And l'avviso pagopa 0 viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 1

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_47_2] Destinatario 1 - pagamento notifica multi destinatario con più avvisi PagoPa e modello F24 e con pagamento di un solo avviso
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0


  #48 Notifica multi destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_48] Notifica multi destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And l'avviso pagopa 0 viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1
    #TODO Verifica posizione debitoria...

  #49 Destinatario 1 - notifica multi destinatario con più avvisi pagoPA: pagamento di un avviso
  #TODO Modificare il metodo che verifica il pagamento di un solo avviso......
  #TODO Non è possibile effettuare il pagamento lato Destinatario
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_49] Destinatario 1 - notifica multi destinatario con più avvisi pagoPA: pagamento di un avviso
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 2 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 2 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1

  #50 Destinatario 1 - notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
  #TODO Non è possibile effettuare il pagamento lato Destinatario
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_50] Destinatario 1 - notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_f24 | NO |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0



#SOLO TM
  #51 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_51] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica

#SOLO TM
  #52 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_52] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica
#SOLO TM
  #53 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_53] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NO |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica

#SOLO TM
  #54 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_54] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica


#SOLO TM
  #55 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_54] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica

#SOLO TM
  #56 PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_56] PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1

#SOLO TM
  #57 PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_57] PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1

#SOLO TM
  #58 PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_58] PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_f24 | NO |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_f24 | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1
#SOLO TM
  #59 PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_59] PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1

#SOLO TM
  #60 PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_60] PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_f24 | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1



#TODO SOLO TM
  #61 Destinatario - visualizzazione box di pagamento su notifica mono destinatario pagata (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#TODO SOLO TM
  #62 Documento PagoPa: Inserimento dati pagamento e relativa verifica dei dati nel documento generato di avviso PagoPA (es. amount, description, expirationDate, status, ecc.) [TA]

#TODO SOLO TM
  #63 Documento F24: Inserimento dati pagamento e costruzione del documento F24 e relativa verifica dei dati nel documento generato F24 [TA]




  #64 Test di Validazione degli oggetti di pagamento ricevuti: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]
  Scenario: [B2B-PA-PAY_MULTI_64] Test di Validazione degli oggetti di pagamento ricevuti: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | FLAT_RATE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
      | payment_multy_number | 2 |
      | payment_noticeCode | 302011697026785044 |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  Scenario: [B2B-PA-PAY_MULTI_64_1] Test di Validazione degli oggetti di pagamento ricevuti multidestinatario: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | FLAT_RATE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
      | payment_multy_number | 1 |
      | notice_code | 302011697026785045 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
      | payment_multy_number | 1 |
      | notice_code | 302011697026785045 |

    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"



#TODO NO TEST...
  #65 Timeline: Verifica F24 (scenario negativo: deve essere riscontrata assenza di eventi di pagamento in timeline).. NO TEST...

#TODO NO TEST....
  #66 Timeline: Verifica PagoPa con più di un pagamento effettuato (presenza di più istanze di pagamento) [TA] .. NO TEST...





  #67 Timeline: Esecuzione di più pagamenti, sia F24 che PagoPa -> Verifica in timeline presenza solo dei pagamenti PagoPa [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_67] PA Timeline: Esecuzione di più pagamenti, sia F24 che PagoPa -> Verifica in timeline presenza solo dei pagamenti PagoPa [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1
    And verifica presenza in Timeline dei solo pagamenti di avvisi PagoPA del destinatario 0
    And verifica non presenza in Timeline di pagamenti con avvisi F24 del destinatario 0
    #TODO Controllare che non ci sono eventi in timeline di pagamenti f24.......Chiedere...

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_67_1] PA Timeline: Esecuzione di più pagamenti, PagoPa -> Verifica in timeline presenza solo dei pagamenti PagoPa [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 2 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then gli avvisi PagoPa vengono pagati correttamente dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 1 dal destinatario 0
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



  #69 Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso2
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_69] Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso2
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Gherkin" con delega



  #70 Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso1 (Stesso avviso - pagoPA)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_70] Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso2
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega



  #71 Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - PagoPa [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_71] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - PagoPa
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"

    #STESSO TEST B2B-PA-PAY_MULTI_71 IMPLEMENTATO DIVERSAMENTE
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_71_2] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - PagoPa
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention del PAGOPA di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |
   # Then viene effettuato un controllo sulla durata della retention del PAGOPA di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_71_1] Verifica retention allegati di pagamento (7gg precaricato) - PagoPa
    Given viene effettuato il pre-caricamento di un allegato
    Then viene effettuato un controllo sulla durata della retention di "PAGOPA" precaricato



  #72 Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_72] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | apply_cost_F24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"



  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_72_1] Verifica retention allegati di pagamento (7gg precaricato) - F24
    Given  viene effettuato il pre-caricamento dei metadati f24
    Then viene effettuato un controllo sulla durata della retention di "F24" precaricato


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_71_1] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | apply_cost_F24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "F24"



#TODO Chiedere Info.....Modificare il test
  #73 PA -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata
  @dev
  Scenario: [B2B-PA-PAY_MULTI_73] PA -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | apply_cost_F24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    #Then si verifica la corretta acquisizione della notifica con verifica sha256 del allegato di pagamento "F24"
    When viene richiesto il download del documento "F24"
    Then il download si conclude correttamente



#TODO Chiedere Info....Modificare il test
  #74 Destinatario -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata
  Scenario: [B2B-PA-PAY_MULTI_74]  Destinatario -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | apply_cost_F24 | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
   # Then si verifica la corretta acquisizione della notifica con verifica sha256 del allegato di pagamento "F24"
    #viene fatta la stessa verifica sullo Sha256
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Cucumber"

#TODO SOLO TM....
  #75 PA -  Visualizzazione Box Allegati Modelli F24


  #TODO aggiornare le openAPI AppIO
   #76 Destinatario -  Download PAGOPA/F24 con AppIO
  @ignore
  Scenario: [B2B-PA-PAY_MULTI_75] Invio notifica con api b2b e recupero documento di pagamento PAGOPA con AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | NULL |
      | apply_cost_f24 | NO |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then il documento di pagamento "PAGOPA" può essere recuperata tramite AppIO da "Mario Cucumber"

  Scenario: [B2B-PA-PAY_MULTI_75_1] Invio notifica con api b2b e recupero documento di pagamento F24 con AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then il documento di pagamento "F24" può essere recuperata tramite AppIO da "Mario Cucumber"



    #---------------------------------------------------Test di Prova...................

  Scenario: [B2B-PA-SEND_21] Invio notifica digitale mono destinatario senza pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    When la notifica viene inviata dal "Comune_Multi"
    Then si verifica la corretta acquisizione della richiesta di invio notifica

  Scenario: [B2B-PA-SEND_22] Invio notifica digitale mono destinatario senza pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
      | amount | 2550 |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    And l'importo della notifica è 2550



  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_17_1] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @Annullamento
  Scenario: [B2B-PF-ANNULLAMENTO_17_2] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"


  @Annullamento  #Da Verificare Manualmente
  Scenario:  [B2B-PA-ANNULLAMENTO_23] PA mittente: notifica con pagamento in stato “Annullata” - inserimento nuova notifica con stesso IUV [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #And si verifica la coretta cancellazione da tabella pn-NotificationsCost
    When viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
    Then la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED


  @Annullamento @ignore #Conflitto 409 Conflict
  Scenario:  [B2B-PA-ANNULLAMENTO_23_1] PA mittente: notifica con pagamento non in stato “Annullata” - inserimento nuova notifica con stesso IUV [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
    Then la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED