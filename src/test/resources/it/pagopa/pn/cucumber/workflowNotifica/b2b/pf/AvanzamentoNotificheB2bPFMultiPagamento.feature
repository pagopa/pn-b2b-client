Feature: avanzamento notifiche b2b persona fisica multi pagamento


 #24 PA - inserimento notifica mono destinatario con un solo avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | payment_f24 | NULL |
      | apply_cost_pagopa | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_1] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e costi di notifica inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | payment_f24 | NULL |
      | apply_cost_pagopa | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_2] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | NO |
      | apply_cost_f24 | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24_3] PA - inserimento notifica mono destinatario con un solo avviso pagoPA e F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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


   #25 PA - inserimento notifica mono destinatario con un solo F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_25] PA - inserimento notifica mono destinatario con un solo avviso F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26_1] PA - inserimento notifica mono destinatario con più avvisi pagoPA (almeno 3) e nessun F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | payment_f24standard | 3 |

    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26_2] PA - inserimento notifica mono destinatario con più avvisi pagoPA (almeno 4) e nessun F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | payment_f24standard | 4 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26_3] PA - inserimento notifica mono destinatario con più avvisi pagoPA e  F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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


  #28  PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: ad ogni avviso pagoPA corrisponde un F24 [TA]
  @pagamentiMultipli @ignore
  Scenario: [B2B-PA-PAY_MULTI_28] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  #29  PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_29] PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost | NO |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_31] PA - inserimento notifica multi destinatario con un solo avviso pagoPA e costi di notifica  inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1


  #32 PA - inserimento notifica multi destinatario con un solo F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_32] PA - inserimento notifica multi destinatario con un solo F24 e costi di notifica non inclusi
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
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
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI   |
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
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI   |
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



 #TODO CHIEDERE INFO.............
  #39 PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_39] PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1


#TODO VECCHIO REQUISITO
  #40 Destinatario - pagamento notifica mono destinatario con un solo avviso pagoPA: verifica stato “In elaborazione”


 #TODO VECCHIO REQUISITO
  #41 Destinatario - visualizzazione box di pagamento su notifica mono destinatario pagata - verifica della presenza stato “Pagato”


  #TODO TEST MANUALE.........INFO ...VERIFICARE...
  #42 Notifica mono destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA]
  Scenario: [B2B-PA-PAY_MULTI_43] Notifica mono destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NO |
      | apply_cost_pagopa | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica
    #TODO Verificare la posizione Debitoria...........


  #TODO TEST MANUALE.........INFO
  #43 Destinatario - notifica mono destinatario con più avvisi pagoPA: pagamento di un avviso
  Scenario: [B2B-PA-PAY_MULTI_43] Destinatario - notifica mono destinatario con più avvisi pagoPA: pagamento di un avviso
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NO |
      | apply_cost_pagopa | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #TODO parametrizzare lo step per avviso di pagamento.......
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica

  #TODO TEST MANUALE.........INFO
  #44 Destinatario - notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
  Scenario: [B2B-PA-PAY_MULTI_43] Destinatario - notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | apply_cost_pagopa | SI |
      | apply_cost_pagopa | NO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #TODO parametrizzare lo step per avviso di pagamento.......
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica


  @pagamentiMultipli
    #TODO Non è possibile effettuare il pagamento lato Destinatario accertare il pagamento di un solo avviso...Chiudere la posizione debitoria
  Scenario: [B2B-PA-PAY_MULTI_44] Destinatario - notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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
  Scenario: [B2B-PA-PAY_MULTI_45] Destinatario PF: download allegato pagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When "Mario Cucumber" tenta il recupero dell'allegato "F24"
    Then il download si conclude correttamente


  #46 Destinatario - download allegato pagoPA
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_46] Destinatario PF: download allegato pagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download si conclude correttamente


  #47 Destinatario 1 - pagamento notifica multi destinatario con un solo avviso pagoPA
  #TODO Non è possibile effettuare il pagamento lato Destinatario
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_47] Destinatario 1 - pagamento notifica multi destinatario con un solo avviso pagoPA
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_47_1] Destinatario 1 - pagamento notifica multi destinatario con più avvisi pagoPA e con pagamento di un solo avviso
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0

  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_47_2] Destinatario 1 - pagamento notifica multi destinatario con più avvisi PagoPa e modello F24 e con pagamento di un solo avviso
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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
    And destinatario
      | denomination     | Ada Lovelace  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
    And destinatario
      | denomination     | Gaio Giulio Cesare  |
      | taxId | CSRGGL44L13H501E |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | NO |
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
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 0
    And si attende il corretto pagamento della notifica dell'utente 1


#SOLO TM
  #51 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_51] PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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




#TODO NO TEST...
  #65 Timeline: Verifica F24 (scenario negativo: deve essere riscontrata assenza di eventi di pagamento in timeline).. NO TEST...

#TODO NO TEST....
  #66 Timeline: Verifica PagoPa con più di un pagamento effettuato (presenza di più istanze di pagamento) [TA] .. NO TEST...





  #67 Timeline: Esecuzione di più pagamenti, sia F24 che PagoPa -> Verifica in timeline presenza solo dei pagamenti PagoPa [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_60] PA Timeline: Esecuzione di più pagamenti, sia F24 che PagoPa -> Verifica in timeline presenza solo dei pagamenti PagoPa [TA]
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
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
    #TODO Controllare che non ci sono eventi in timeline di pagamenti f24.......Chiedere...



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
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega



  #71 Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - PagoPa [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_71] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - PagoPa
    Given viene effettuato il pre-caricamento di un allegato
    Then viene effettuato un controllo sulla durata della retention del PAGOPA di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_71_1] Verifica retention allegati di pagamento (7gg precaricato) - PagoPa
    Given viene effettuato il pre-caricamento di un allegato
    Then viene effettuato un controllo sulla durata della retention di "PAGOPA" precaricato





  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_71_2] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - PagoPa
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"


  #72 Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_72] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | NULL |
      | payment_f24 | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    Then viene effettuato un controllo sulla durata della retention del F24 di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"



  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_72_1] Verifica retention allegati di pagamento (7gg precaricato) - F24
    Given  viene effettuato il pre-caricamento dei metadati f24
    Then viene effettuato un controllo sulla durata della retention di "F24" precaricato


  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_71_1] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
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
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
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
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
   # Then si verifica la corretta acquisizione della notifica con verifica sha256 del allegato di pagamento "F24"
    #viene fatta la stessa verifica sullo Sha256
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Cucumber"

#TODO SOLO TM....
  #75 PA -  Visualizzazione Box Allegati Modelli F24


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
