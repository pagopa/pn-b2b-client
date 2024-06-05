Feature: avanzamento b2b notifica difgitale con indirizzo speciale

  @workflowDigitale
  #B2B_TIMELINE_MULTI_PF_PG_01
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_1] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"


  @workflowDigitale
  #[B2B_TIMELINE_MULTI_PF_PF_06]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_2] Invio notifica multidestinatario con pagamento destinatario 0 e 1 scenario  positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 1
    #pagamento doppio


  @workflowDigitale
 # [B2B_TIMELINE_MULTI_PF_PF_07]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_3] Invio notifica multidestinatario con pagamento destinatario 0 e non del destinatario 1 scenario  positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    And non vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 1



  @workflowDigitale
  #[B2B_TIMELINE_MULTI_PF_PF_08]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_4]  Invio notifica multidestinatario con pagamento destinatario 1 e non del destinatario 0 scenario  positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 1
    And non vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0

  @workflowDigitale
  #[B2B_TIMELINE_MULTI_PF_PF_08]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_5] Invio notifica multidestinatario con pagamento destinatario 1 e non del destinatario 0 scenario  positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 1
    And non vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0

  @workflowDigitale
  #[B2B_TIMELINE_1]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_6] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "ACCEPTED"

  @workflowDigitale
  #[B2B_TIMELINE_2]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_7] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @workflowDigitale
  #[B2B_TIMELINE_3]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_8] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"

  @workflowDigitale
  #[B2B_TIMELINE_4]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_9] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"

  @workflowDigitale
  #[B2B_TIMELINE_5]
  Scenario:[B2B_TIMELINE_DIGITAL_SPECIAL_10]  Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"

  @workflowDigitale
  #[B2B_TIMELINE_6]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_11] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"

  @tbc @workflowDigitale
  #[B2B_TIMELINE_7]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_12]Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And "Mario Cucumber" legge la notifica ricevuta
    Then si verifica che la notifica abbia lo stato VIEWED

  @workflowDigitale
  #[B2B_TIMELINE_8]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_13] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And "Mario Cucumber" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"

  @SmokeTest @workflowDigitale @mockPec
   #[B2B_TIMELINE_9]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_14] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pec.pagopa.it |
      | payment     | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @workflowDigitale @mockPec
   #B2B_TIMELINE_10]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_15] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And "Mario Gherkin" legge la notifica ricevuta
    Then si verifica che la notifica abbia lo stato VIEWED

  @workflowDigitale @mockPec
   #[B2B_TIMELINE_11]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_16] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And "Mario Gherkin" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"


  @workflowDigitale
   #[B2B_TIMELINE_18]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_17] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campo deliveryDetailCode positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK"
    And viene verificato che nell'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" sia presente il campo deliveryDetailCode

  @workflowDigitale
   #[B2B_TIMELINE_DIGITAL_UAT_10000]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_18] Invio notifica ed attesa elemento di  positivo
    Given viene generata una nuova notifica
      | subject | notifica digitale con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @workflowDigitale
   #[B2B_TIMELINE_25]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_19] Invio notifica digitale con verifica uguaglianza tra scheduleDate di SCHEDULE_REFINEMENT e timestamp del REFINEMENT PN-9059
    Given viene generata una nuova notifica
      | subject            | invio notifica multi cucumber |
      | senderDenomination | Comune di milano              |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0

      #Test in ignore per il discorso che a volte arriva prima EFFECTIVE_DATE di un destinario rispetto al'altro destinatario (Caso di test comunque coperto da altri test mono destinatario)
  @workflowDigitale
   #[B2B-TIMELINE_MULTI_1]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_20]Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @workflowDigitale
   #[B2B-TIMELINE_MULTI_2]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_21] Invio notifica multi destinatario_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"

  @workflowDigitale
   #[B2B-TIMELINE_MULTI_3]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_22]Invio notifica multi destinatario_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"

  @workflowDigitale
   #[B2B_TIMELINE_MULTI_7]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_23] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"

  @workflowDigitale
   #[B2B_TIMELINE_MULTI_9]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_24] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"

  @workflowDigitale
   #[B2B_TIMELINE_MULTI_10]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_25] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"

  @workflowDigitale
   #[B2B_TIMELINE_14]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_26] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK"


  @workflowDigitale
  # [B2B_TIMELINE_15]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_27] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"


  @workflowDigitale
   #[B2B_TIMELINE_HOTFIX-BINGQ2.2_1]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_28] Invio notifica digitale ed attesa stato DELIVERED con invio mail a pec mittente (bug HOTFIX-BINGQ2.2)
    Given si invia una email alla pec mittente e si attendono 6 minuti
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @workflowDigitale
   #[B2B_TIMELINE_PG_3]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_29] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"

  @workflowDigitale
   #[B2B_TIMELINE_PG-CF_3]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_30] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @workflowDigitale
   #[B2B_TIMELINE_MULTI_PG_5]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_31] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"

   #[B2B_TIMELINE_MULTI_PG_6]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_32] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"

        #Test in ignore per il discorso che a volte arriva prima EFFECTIVE_DATE di un destinario rispetto al'altro destinatario (Caso di test comunque coperto da altri test mono destinatario)
  @workflowDigitale
   #[B2B_TIMELINE_MULTI_PG_7]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_33] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"


    #TODO Chiedere se conviene spostare...

  @workflowDigitale
   #[B2B-PA-PAY_1]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_34] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI   |
      | payment_f24        | NULL |
      | apply_cost_pagopa  | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    Then vengono verificati costo = "100" e data di perfezionamento della notifica

  @workflowDigitale
   #[B2B-PA-PAY_2]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_35] Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | NULL             |
      | apply_cost_pagopa  | SI               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica

  @workflowDigitale
   #[B2B-PA-PAY_3]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_36] Invio notifica FLAT e verifica amount
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | FLAT_RATE                   |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica

  @workflowDigitale
   #[B2B-PA-PAY_4]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_37] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | apply_cost_pagopa  | SI               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica

  @workflowDigitale
   #[B2B-PA-PAY_6]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_38]Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | apply_cost_pagopa  | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica


  @workflowDigitale
   #[B2B-PA-PAY_8]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_39] Comunicazione da parte della PA dell'avvenuto pagamento di tipo PagoPA  7741
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | NULL             |
      | apply_cost_pagopa  | SI               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #Questa API è a disposizione della Pubblica Amministrazione per inviare eventi di chiusura di una o più posizioni debitorie di tipo PagoPA.
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica


  @workflowDigitale
   #[B2B-PA-PAY_9]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_40] Verifica restituzione data di visualizzazione a quella del NOTIFICATION_VIEWED_CREATION_REQUEST per la chiamata retrieveNotificationPrice - PN-8970
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@fail-Discovery_890 |
      | payment_pagoPaForm      | SI                     |
      | payment_f24             | NULL                   |
      | apply_cost_pagopa       | SI                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then "Mario Gherkin" legge la notifica
    Then viene verificato data corretta del destinatario 0


   #[B2B_TIMELINE_MULTI_PF_PG_02]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_41] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"

  # [B2B_TIMELINE_MULTI_PF_PG_03]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_42] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"

  # [B2B_TIMELINE_MULTI_PF_PG_04]
  Scenario:[B2B_TIMELINE_DIGITAL_SPECIAL_43]  Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"

   #[B2B_TIMELINE_MULTI_PF_PG_05]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_44] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

   #[B2B_TIMELINE_MULTI_5]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_45] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "ACCEPTED"

   #[B2B_TIMELINE_MULTI_6]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_46] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


   #[B2B_TIMELINE_MULTI_8]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_47] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"


  @dev
   #[B2B_TIMELINE_24]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_48] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED

    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"
    And "Mario Gherkin" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

   #[B2B_TIMELINE_PG_14]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_49] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campo deliveryDetailCode positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK"
    And viene verificato che nell'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" sia presente il campo deliveryDetailCode


  #[B2B_TIMELINE_PG_1]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_50] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "ACCEPTED"

  #[B2B_TIMELINE_PG_2]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_51] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  #[B2B_TIMELINE_PG_4]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_52] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"

  #[B2B_TIMELINE_PG_5]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_53] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"

  #[B2B_TIMELINE_PG_6]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_54] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"

  #[B2B_TIMELINE_PG_7]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_55] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"


  @dev
  #[B2B_TIMELINE_PG_10]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_56] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK"

  @dev
  #[B2B_TIMELINE_PG_11]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_57] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"




  @testLite
  #[B2B_TIMELINE_MULTI_PG_1]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_58] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "ACCEPTED"

  #[B2B_TIMELINE_MULTI_PG_2]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_59] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @testLite
  #[B2B_TIMELINE_MULTI_PG_3]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_60] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"

  #[B2B_TIMELINE_MULTI_PG_4]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_61] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"

  #[B2B_TIMELINE_MULTI_PG_8]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_62] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  #[B2B_TIMELINE_MULTI_PG-CF_1]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_63] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"

  #[B2B_TIMELINE_MULTI_PG-CF_2]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_64] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  #[B2B_TIMELINE_MULTI_PG-CF_3]
  Scenario: [B2B_TIMELINE_DIGITAL_SPECIAL_65] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"