Feature: avanzamento notifiche b2b persona fisica

  @testLite
  Scenario: [B2B_TIMELINE_1] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "ACCEPTED"

  @testLite
  Scenario: [B2B_TIMELINE_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @testLite
  Scenario: [B2B_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"

  @testLite
  Scenario: [B2B_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"

  @testLite
  Scenario: [B2B_TIMELINE_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"

  @testLite
  Scenario: [B2B_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"

  @ignore @tbc
  Scenario: [B2B_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And "Mario Cucumber" legge la notifica ricevuta
    Then si verifica che la notifica abbia lo stato VIEWED

  @testLite
  Scenario: [B2B_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And "Mario Cucumber" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"

  @SmokeTest @testLite
  Scenario: [B2B_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
      | payment_pagoPaForm | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @testLite
  Scenario: [B2B_TIMELINE_10] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And "Mario Gherkin" legge la notifica ricevuta
    Then si verifica che la notifica abbia lo stato VIEWED

  @testLite
  Scenario: [B2B_TIMELINE_11] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And "Mario Gherkin" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"

  @dev @ignore
  Scenario: [B2B_TIMELINE_12] Invio notifica digitale ed attesa elemento di timeline PREPARE_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_SIMPLE_REGISTERED_LETTER"

  @dev @ignore
  Scenario: [B2B_TIMELINE_13] Invio notifica digitale ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"

  @dev @testLite
  Scenario: [B2B_TIMELINE_14] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK"

  @dev @testLite
  Scenario: [B2B_TIMELINE_15] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"

  @dev @testLite
  Scenario: [B2B_TIMELINE_16] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"

  @dev @testLite
  Scenario: [B2B_TIMELINE_17] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"

  Scenario: [B2B_TIMELINE_18] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campo deliveryDetailCode positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK"
    And viene verificato che nell'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" sia presente il campo deliveryDetailCode

  @svil
  Scenario: [B2B_TIMELINE_19] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@gmail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "KO"
    And viene verificato che nell'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "KO" sia presente i campi deliveryDetailCode e deliveryFailureCause

  Scenario: [B2B_TIMELINE_20] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile_address | DSRDNI00A01A225I@pnpagopa.postecert.local |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And viene verificato che nell'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE" e' presente il campo Digital Address di piattaforma
    #Serve un CF con indirizzo digitale in piattaforma
  
  @dev @ignore
  Scenario: [B2B_TIMELINE_21] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo il campo Digital Address positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@gmail.it |
      | taxId | FLPCPT69A65Z336P |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_DIGITAL_DOMICILE"
    And viene verificato che nell'elemento di timeline della notifica "PREPARE_DIGITAL_DOMICILE" sia presente il campo Digital Address
    #Il campo è stato messo come facoltativo, dato che non sempre è presente. Da capire assieme se ci sono casi in cui dovrebbe esserci ma non compare.
  @dev @ignore
  Scenario: [B2B_TIMELINE_22] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo il campo Digital Address positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@gmail.it |
      | taxId | FLPCPT69A65Z336P |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_DIGITAL_WORKFLOW"
    And viene verificato che nell'elemento di timeline della notifica "SCHEDULE_DIGITAL_WORKFLOW" sia presente il campo Digital Address
    #Il campo è stato messo come facoltativo, dato che non sempre è presente. Da capire assieme se ci sono casi in cui dovrebbe esserci ma non compare.

  @dev @ignore
  Scenario: [B2B_TIMELINE_23] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE sia presente il campo Digital Address scenario positivo PN-5992
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry

    @dev @ignore
    Scenario: [B2B_TIMELINE_24] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo
      Given viene generata una nuova notifica
        | subject | invio notifica con cucumber |
        | senderDenomination | Comune di milano |
      And destinatario Mario Gherkin e:
        | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
      When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED

      Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
      And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"
      And "Mario Gherkin" legge la notifica ricevuta
      Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
      And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"
