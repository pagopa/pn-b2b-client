Feature: avanzamento notifiche b2b persona fisica

  @workflowDigitale
  Scenario: [B2B_TIMELINE_1] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "ACCEPTED"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"

  @ignore @tbc @workflowDigitale
  Scenario: [B2B_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And "Mario Cucumber" legge la notifica ricevuta
    Then si verifica che la notifica abbia lo stato VIEWED

  @workflowDigitale
  Scenario: [B2B_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And "Mario Cucumber" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"

  @SmokeTest @workflowDigitale @mockPec
  Scenario: [B2B_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pec.pagopa.it |
      | payment     | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @workflowDigitale @mockPec
  Scenario: [B2B_TIMELINE_10] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo
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
  Scenario: [B2B_TIMELINE_11] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pec.pagopa.it |
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

  @workflowDigitale
  Scenario: [B2B_TIMELINE_14] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_15] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_16] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_17] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"

  @workflowDigitale
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
    And destinatario
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile_address | test@gmail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "KO"
    And viene verificato che nell'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "KO" sia presente i campi deliveryDetailCode e deliveryFailureCause

  @workflowDigitale @mockPec
  Scenario: [B2B_TIMELINE_20] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK e controllo campi deliveryDetailCode e deliveryFailureCause positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile_address | DSRDNI00A01A225I@pec.pagopa.it |
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
        | digitalDomicile_address | CLMCST42R12D969Z@pec.pagopa.it |
      When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED

      Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
      And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"
      And "Mario Gherkin" legge la notifica ricevuta
      Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
      And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"


  @workflowDigitale
  Scenario: [B2B_TIMELINE_DIGITAL_UAT_10000] Invio notifica ed attesa elemento di  positivo
    Given viene generata una nuova notifica
      | subject | notifica digitale con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"

  @workflowDigitale
  Scenario: [B2B_TIMELINE_25] Invio notifica digitale con verifica uguaglianza tra scheduleDate di SCHEDULE_REFINEMENT e timestamp del REFINEMENT PN-9059
    Given viene generata una nuova notifica
      | subject            | invio notifica multi cucumber |
      | senderDenomination | Comune di milano              |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0

  @workflowDigitale
  Scenario: [B2B_TIMELINE_26] Invio notifica digitale che va in DIGITAL_FAILURE con verifica uguaglianza tra scheduleDate di SCHEDULE_REFINEMENT e timestamp del REFINEMENT PN-9059
    Given viene generata una nuova notifica
      | subject            | invio notifica multi cucumber |
      | senderDenomination | Comune di milano              |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0


  Scenario: [B2B_TIMELINE_27] Invio notifica digitale e lettura notifica da un utente con token scaduto PN-9110
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "utente token scaduto" tenta di leggere la notifica ricevuta
    Then l'operazione ha prodotto un errore con status code "403"


