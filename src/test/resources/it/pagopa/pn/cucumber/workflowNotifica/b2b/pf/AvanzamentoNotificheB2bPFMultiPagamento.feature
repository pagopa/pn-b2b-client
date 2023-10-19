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
  Scenario: [B2B-PA-PAY_MULTI_24_2] PA - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
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
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_3] PA - inserimento notifica multi destinatario con un solo avviso pagoPA e F24 e costi di notifica non inclusi
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
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
   # Then viene verificato il costo = "100" della notifica per l'utente 0
    #And viene verificato il costo = "100" della notifica per l'utente 1


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
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_25_1] PA - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


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
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 2 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"



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
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | payment_multy_number | 2 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  #TODO Vecchio Requisito
  #28  PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
  @pagamentiMultipli @ignore
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
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
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
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


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
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
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
    Then viene verificato il costo = "100" della notifica per l'utente 1


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_32_1] PA - inserimento notifica multi destinatario con un solo F24 e costi di notifica inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
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
   # Then viene verificato il costo = "200" della notifica per l'utente 0
    Then viene verificato il costo = "200" della notifica per l'utente 1


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
      | payment_multy_number | 2 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 2 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1




  #34 PA - inserimento notifica multi destinatario con più F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_34] PA - inserimento notifica multi destinatario con un solo F24
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
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"



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
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When si verifica la corretta acquisizione della notifica
    Then viene richiesto il download del documento "F24"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_35_1] PA - download modello F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy          | DELIVERY_MODE               |
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
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When si verifica la corretta acquisizione della notifica
    Then viene richiesto il download del documento "F24" per il destinatario 0
    And viene richiesto il download del documento "F24" per il destinatario 1


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
      | payment_multy_number | 2 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 2 |
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
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


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
  @pagamentiMultipli
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
    And l'avviso pagopa 1 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 1 dal destinatario 0

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_43_1] Destinatario - notifica mono destinatario con più avvisi pagoPA: pagamento di più avvisi
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
    And l'avviso pagopa 1 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 1 dal destinatario 0

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
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica


  @pagamentiMultipli @ignore
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
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
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
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_MARIO |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When si verifica la corretta acquisizione della notifica
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Cucumber"


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
    When si verifica la corretta acquisizione della notifica
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Cucumber"


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
      | payment_multy_number | 2 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 2 |
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
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 2 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 2 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And l'avviso pagopa 0 viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 1


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
    Then viene verificato il costo = "100" della notifica per l'utente 0

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
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 0



#SOLO TM
  #51 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_51] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
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
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
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
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


#SOLO TM
  #54 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_54] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


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
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0


#SOLO TM
  #56 PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_56] PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
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
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And l'avviso pagopa 0 viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 1

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
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 1

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
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

#SOLO TM
  #59 PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_59] PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

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
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
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
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    #And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    #And si attende il corretto pagamento della notifica dell'utente 1
    And verifica presenza in Timeline dei solo pagamenti di avvisi PagoPA del destinatario 0
    And verifica non presenza in Timeline di pagamenti con avvisi F24 del destinatario 0
    #TODO Controllare che non ci sono eventi in timeline di pagamenti f24.......Chiedere...

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
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24 | SI |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 2 |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_CSRGGL44L13H501E |
      | apply_cost_f24 | SI |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 2 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then gli avvisi PagoPa vengono pagati correttamente dal destinatario 0
    And gli avvisi PagoPa vengono pagati correttamente dal destinatario 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1
    And verifica presenza in Timeline dei solo pagamenti di avvisi PagoPA del destinatario 0

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
  #TODO ....Alessandro deve fornire lo iuv per restituire gli errori sopra.......
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_68] Test di Validazione degli oggetti di pagamento ricevuti multidestinatario: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then gli avvisi PagoPa vengono pagati correttamente dal destinatario 0
    #TODO utilizzando iun predisposti  controllare il tipo di errore restituisto
    And si attende il non corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    #And l'operazione ha prodotto un errore con status code "400"






  #69 Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso2
  #TODO Modificare lo scenario..................
  #Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante e delegato possono scaricare l'avviso
  @pagamentiMultipli @deleghe1
  Scenario: [B2B-PA-PAY_MULTI_69] Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso2
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
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
      | title_payment | F24_STANDARD_MARIO |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Gherkin" con delega
    And "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    And il download non ha prodotto errori
    And l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega
    And "Mario Cucumber" tenta il recupero dell'allegato "F24"
    And il download non ha prodotto errori


  #70 Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso1 (Stesso avviso - pagoPA)
  @pagamentiMultipli @deleghe1
  Scenario: [B2B-PA-PAY_MULTI_70] Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso1 (Stesso avviso - pagoPA)
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
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
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Gherkin" con delega
    And "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    And il download non ha prodotto errori



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
    Then viene verificato che l'elemento di timeline "REFINEMENT" esista
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
      | title_payment | F24_STANDARD_MARIO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention del F24 di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |



  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_72_1] Verifica retention allegati di pagamento (7gg precaricato) - F24
    Given  viene effettuato il pre-caricamento dei metadati f24
    Then viene effettuato un controllo sulla durata della retention di "F24" precaricato


  @pagamentiMultipli @ignore
  Scenario: [B2B-PA-PAY_MULTI_72_3] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_MARIO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "F24"


  #73 PA -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata
  #Verifica dello sha256 non possibile perché il file viene. generato on demand
  @pagamentiMultipli @ignore
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
      | title_payment | F24_STANDARD_MARIO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
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
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_MARIO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
   # Then si verifica la corretta acquisizione della notifica con verifica sha256 del allegato di pagamento "F24"
    #viene fatta la stessa verifica sullo Sha256
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Cucumber"

#TODO SOLO TM....
  #75 PA -  Visualizzazione Box Allegati Modelli F24


   #76 Destinatario -  Download PAGOPA/F24 con AppIO
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_76] Invio notifica con api b2b e recupero documento di pagamento PAGOPA con AppIO
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

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_76_1] Invio notifica con api b2b e recupero documento di pagamento F24 con AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_MARIO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then il documento di pagamento "F24" può essere recuperata tramite AppIO da "Mario Cucumber"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_77] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento PagoPA (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di palermo            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_77_1] Destinatario PF: dettaglio notifica annullata - download bollettini di pagamento F24 (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di palermo            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_MARIO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When "Mario Cucumber" tenta il recupero dell'allegato "F24"
    Then il download ha prodotto un errore con status code "404"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_78] Destinatario Multi PF: dettaglio notifica annullata - download bollettini di pagamento PagoPA (scenario negativo)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di palermo            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
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
      | senderDenomination | comune di palermo            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_MARIO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_GHERKIN |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
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
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | NUL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene verificato il costo = "100" della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_79_1] PA mittente: dettaglio notifica annullata - download bollettini di pagamento F24 (scenari positivi)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | NULL   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_MARIO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    When viene richiesto il download del documento "F24"
    Then l'operazione ha generato un errore con status code "404"



#-------------------------TEST CARTACEO---------------------------------



  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_80] PA - Invio RS DELIVERY_MODE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | DELIVERY_MODE       |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_80_1_1] PA - Invio RS DELIVERY_MODE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | DELIVERY_MODE       |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok-Retry_RS |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_80_2] PA - Invio RS DELIVERY_MODE con Costi inclusi - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | DELIVERY_MODE       |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_80_3] PA - Invio RS FLATE_RATE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | FLAT_RATE       |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"



  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81] PA - Invio RS DELIVERY_MODE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_1] PA - Invio RS DELIVERY_MODE Costi inclusi - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_1_1] PA - Invio RS DELIVERY_MODE Costi inclusi - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok-Retry_RS |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_2] PA - Invio RS FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | FLAT_RATE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_81_3] PA - Invio RS FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | FLAT_RATE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok-Retry_RS |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"



  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_82] PA - Invio RS DELIVERY_MODE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | DELIVERY_MODE       |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_82_1] PA - Invio RS DELIVERY_MODE con Costi inclusi - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | DELIVERY_MODE       |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_82_3] PA - Invio RS FLATE_RATE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | FLAT_RATE       |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_83] PA - Invio RS DELIVERY_MODE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_83_1] PA - Invio RS DELIVERY_MODE Costi inclusi - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_83_2] PA - Invio RS FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | FLAT_RATE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_83_3] PA - Invio RS FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | FLAT_RATE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_84] PA - Invio RS DELIVERY_MODE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | DELIVERY_MODE       |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_84_1] PA - Invio RS DELIVERY_MODE con Costi inclusi - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | DELIVERY_MODE       |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_84_3] PA - Invio RS FLATE_RATE - inserimento notifica  mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi modalità DELIVERY_MODE (scenario positivo)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy          | FLAT_RATE       |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_85] PA - Invio RS DELIVERY_MODE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_85_1] PA - Invio RS DELIVERY_MODE Costi inclusi - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_85_2] PA - Invio RS FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | FLAT_RATE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_85_3] PA - Invio RS FLAT_RATE - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | FLAT_RATE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

  @e2e  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_86] PA - Invio FLAT_RATE costi inclusi -  Invio ad indirizzo fisico fallimento al primo tentativo e successo al secondo tentativo
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy | FLAT_RATE |
      | paFee | 100 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@FAIL-Discovery_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @e2e  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_87] PA - Invio FLAT_RATE costi non inclusi -  Invio ad indirizzo fisico fallimento al primo tentativo e successo al secondo tentativo
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy | FLAT_RATE |
      | paFee | 0 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@FAIL-Discovery_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | title_payment | F24_FLAT_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @e2e  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_88] PA - Invio DELIVERY_MODE costi inclusi -  Invio ad indirizzo fisico fallimento al primo tentativo e successo al secondo tentativo
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@FAIL-Discovery_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @e2e  @pagamentiMultipli @cartaceoF24
  Scenario: [B2B-PA-PAY_MULTI_89] PA - Invio DELIVERY_MODE costi non inclusi -  Invio ad indirizzo fisico fallimento al primo tentativo e successo al secondo tentativo
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@FAIL-Discovery_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @dev @workflowAnalogico
  Scenario: [B2B-PA-PAY_MULTI_90] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@FAIL-Discovery_AR |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  Scenario: [B2B-PA-PAY_MULTI_91] PA - Invio DELIVERY_MODE costi non inclusi -   Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      |payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  Scenario: [B2B-PA-PAY_MULTI_92] PA - Invio DELIVERY_MODE costi  inclusi -   Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      |payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_LVLDAA85T50G702B |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  Scenario: [B2B-PA-PAY_MULTI_93] PA - Invio FLAT_RATE costi non inclusi -   Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | FLAT_RATE |
      | paFee | 0 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      |payment_pagoPaForm | NULL |
      | title_payment | F24_FLAT_LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  Scenario: [B2B-PA-PAY_MULTI_94] PA - Invio FLAT_RATE costi  inclusi -   Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | FLAT_RATE |
      | paFee | 100 |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      |payment_pagoPaForm | NULL |
      | title_payment | F24_FLAT_LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"