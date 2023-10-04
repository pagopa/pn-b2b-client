Feature: avanzamento notifiche b2b persona fisica multi pagamento


 #24 PA - inserimento notifica mono destinatario con un solo avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24] PA - inserimento notifica mono destinatario con un solo avviso pagoPA
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

   #25 PA - inserimento notifica mono destinatario con un solo F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_24] PA - inserimento notifica mono destinatario con un solo avviso pagoPA
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

 #25 PA - inserimento notifica mono destinatario con un solo F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_25] PA - inserimento notifica mono destinatario con un solo F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then il modello f24 viene pagato correttamente
    And si attende il corretto pagamento della notifica

 #26 PA - inserimento notifica mono destinatario con più avvisi pagoPA e nessun F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_26] PA - inserimento notifica mono destinatario con più avvisi pagoPA e nessun F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_pagoPaForm_1 | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica


 #27  PA - inserimento notifica mono destinatario con più F24 e nessun avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_27] PA - inserimento notifica mono destinatario con più F24 e nessun avviso pagoPA
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_pagoPaForm_1 | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
      | payment_f24standard_1 | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  #28  PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: ad ogni avviso pagoPA corrisponde un F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_28] Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

#TODO CHIEDERE INFO.............
  #29  PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]



#TODO CHIEDERE INFO.............
  #30 PA - inserimento notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]





  #31 PA - inserimento notifica multi destinatario con un solo avviso pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_31] PA - inserimento notifica multi destinatario con un solo avviso pagoPA
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica


  #32 PA - inserimento notifica multi destinatario con un solo F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_32] PA - inserimento notifica multi destinatario con un solo F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica



  #33 PA - inserimento notifica multi destinatario con più avvisi pagoPA [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_33] PA - inserimento notifica multi destinatario con più avvisi pagoPA
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
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
    Then viene verificato il costo = "100" della notifica




  #34 PA - inserimento notifica multi destinatario con più F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_32] PA - inserimento notifica multi destinatario con un solo F24
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
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
    Then viene verificato il costo = "100" della notifica



  #35 PA - download modello F24
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_35] PA - download modello F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI   |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "F24_STANDARD"
    Then il download si conclude correttamente


  #36 PA - download allegato pagoPA
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_36] PA - download allegato pagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI   |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente


   #TODO CHIEDERE INFO.............
  #37 PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: ad ogni avviso pagoPA corrisponde un F24 [TA]




 #TODO CHIEDERE INFO.............
  #38 PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include l’avviso pagoPA ma non il modello F24 [TA]




 #TODO CHIEDERE INFO.............
  #39 PA - inserimento notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: un istanza di pagamento include il modello F24 ma non l’avviso pagoPA [TA]





  #40 Destinatario - pagamento notifica mono destinatario con un solo avviso pagoPA: verifica stato “In elaborazione”


  #TODO TEST MANUALE.........INFO
  #41 Destinatario - visualizzazione box di pagamento su notifica mono destinatario pagata - verifica della presenza stato “Pagato”

  #TODO TEST MANUALE.........INFO ...VERIFICARE...
  #42 Notifica mono destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA]


  #TODO TEST MANUALE.........INFO
  #43 Destinatario - notifica mono destinatario con più avvisi pagoPA: pagamento di un avviso

  #TODO TEST MANUALE.........INFO
  #44 Destinatario - notifica mono destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)


  #45 Destinatario - download modello F24
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_45] Destinatario PF: download allegato pagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When "Mario Cucumber" tenta il recupero dell'allegato "F24_STANDARD"
    Then il download si conclude correttamente


  #46 Destinatario - download allegato pagoPA
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_46] Destinatario PF: download allegato pagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When "Mario Cucumber" tenta il recupero dell'allegato "PAGOPA"
    Then il download si conclude correttamente


  #47 Destinatario 1 - pagamento notifica multi destinatario con un solo avviso pagoPA

  #48 Notifica multi destinatario pagata - verifica posizione debitoria (IUV) dopo aver effettuato il pagamento [TA]

  #49 Destinatario 1 - notifica multi destinatario con più avvisi pagoPA: pagamento di un avviso

  #50 Destinatario 1 - notifica multi destinatario con presenza contemporanea di avviso pagoPA e F24: pagamento di uno degli avvisi (PagoPa)



#SOLO TM
  #51 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#SOLO TM
  #52 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#SOLO TM
  #53 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#SOLO TM
  #54 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)
#SOLO TM
  #55 PA - visualizzazione box di pagamento su notifica mono destinatario pagata  solo con avviso PagoPa e modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#SOLO TM
  #56 PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#SOLO TM
  #57 PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con avviso PagoPa e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#SOLO TM
  #58 PA - visualizzazione box di pagamento su notifica multi destinatario pagata  solo con modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#SOLO TM
  #59 PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#SOLO TM
  #60 PA - visualizzazione box di pagamento su notifica multi destinatario pagata solo con avviso PagoPa e modello F24 e costi di notifica non inclusi (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)

#SOLO TM
  #61 Destinatario - visualizzazione box di pagamento su notifica mono destinatario pagata (scenario dedicato alla verifica della coerenza con il Figma, da eseguire solo tramite test manuali)



  #62 Documento PagoPa: Inserimento dati pagamento e relativa verifica dei dati nel documento generato di avviso PagoPA (es. amount, description, expirationDate, status, ecc.) [TA]





  #63 Documento F24: Inserimento dati pagamento e costruzione del documento F24 e relativa verifica dei dati nel documento generato F24 [TA]





  #64 Test di Validazione degli oggetti di pagamento ricevuti: Univocità istanza di pagamento e sue alternative (scenario negativo, se presenti più istanze uguali devo ricevere KO) [TA]





  #65 Timeline: Verifica F24 (scenario negativo: deve essere riscontrata assenza di eventi di pagamento in timeline)



  #66 Timeline: Verifica PagoPa con più di un pagamento effettuato (presenza di più istanze di pagamento) [TA]





  #67 Timeline: Esecuzione di più pagamenti, sia F24 che PagoPa -> Verifica in timeline presenza solo dei pagamenti PagoPa [TA]





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
      | senderDenomination | comune di milano            |
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
      | senderDenomination | comune di milano            |
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
    Then viene effettuato un controllo sulla durata della retention di "PAGOPA" precaricato




  #72 Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24 [TA]
  @pagamentiMultipli
  Scenario: [B2B-PA-PAY_MULTI_72] Verifica retention allegati di pagamento (120gg da data perfezionamento Notifica) - F24
    Given viene effettuato il pre-caricamento di un allegato
    Then viene effettuato un controllo sulla durata della retention di "F24_STANDARD" precaricato



#TODO Chiedere Info....
  #73 PA -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata



#TODO Chiedere Info....
  #74 Destinatario -  Verifica presenza SHA F24 su attestazione opponibile a terzi notifica depositata


#SOLO TM
  #75 PA -  Visualizzazione Box Allegati Modelli F24


  @SmokeTest @testLite @workflowDigitale
  Scenario: [B2B-PA-PAY_1] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    Then vengono verificati costo = "100" e data di perfezionamento della notifica

  @testLite @workflowDigitale
  Scenario: [B2B-PA-PAY_2] Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @testLite @workflowDigitale
  Scenario: [B2B-PA-PAY_3] Invio notifica FLAT e verifica amount
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | FLAT_RATE |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica

  @workflowDigitale
  Scenario: [B2B-PA-PAY_4] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica

  @workflowDigitale
  Scenario: [B2B-PA-PAY_411] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | AAA  |
      | taxId | AAAAAA00A00A000C |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica

  @ignore
  Scenario: [B2B-PA-PAY_5] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then il modello f24 viene pagato correttamente
    And si attende il corretto pagamento della notifica

  @testLite @workflowDigitale
  Scenario: [B2B-PA-PAY_6] Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @ignore
  Scenario: [B2B-PA-PAY_7] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
      | payment_creditorTaxId | 77777777777 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica

  @workflowDigitale
  Scenario: [B2B-PA-PAY_8] Comunicazione da parte della PA dell'avvenuto pagamento di tipo PagoPA  7741
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination     | Ada  |
      | taxId | LVLDAA85T50G702B |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #Questa API è a disposizione della Pubblica Amministrazione per inviare eventi di chiusura di una o più posizioni debitorie di tipo PagoPA.
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica