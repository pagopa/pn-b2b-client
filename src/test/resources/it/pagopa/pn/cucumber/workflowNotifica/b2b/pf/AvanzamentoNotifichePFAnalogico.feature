Feature: avanzamento notifiche b2b con workflow cartaceo

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

    #ok_RS
    #ok-Retry_RS
    #fail_RS

  @dev
  Scenario: [B2B_TIMELINE_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"

  @dev @testLite
  Scenario: [B2B_TIMELINE_RS_2] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok-Retry_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"

  @dev @ignore
  Scenario: [B2B_TIMELINE_RS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "KO"

    #ok_RIS
    #fail_RIS

  @dev @testLite
  Scenario: [B2B_TIMELINE_RIS_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"

  @dev @ignore
  Scenario: [B2B_TIMELINE_RIS_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "KO"

    #ok_AR
    #ok_890
    #ok_RIR

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_2] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_3] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

    #fail_AR
    #fail_890
    #fail_RIR

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_4] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "KO"

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_5] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_890_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "KO"

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_6] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_RIR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "KO"

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_7] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_fail_890_NR_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_8] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_fail_AR_NR_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @dev
  Scenario: [B2B_TIMELINE_ANALOG_9] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail-Discovery_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @dev
  Scenario: [B2B_TIMELINE_ANALOG_10] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_890_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail-Discovery_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

   #@ok_AR
   #@ok_890
  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_11] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Giovanna D'Arco |
      | taxId | DRCGNN12A46A326K |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_12] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Test 890 ok |
      | taxId | PRTCAE90A01D086M |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

      #fail_AR
      #fail_890
  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_13] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail |
      | taxId | MNDLCU98T68C933T |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "KO"

  @dev @ignore
  Scenario: [B2B_TIMELINE_ANALOG_14] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Test 890 Fail |
      | taxId | PRVMNL80A01F205M |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "KO"

  @dev @ignore
  Scenario: [B2B_TIMELINE_ANALOG_15] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @dev
  Scenario: [B2B_TIMELINE_ANALOG_16] Attesa elemento di timeline SEND_ANALOG_FEEDBACK e verifica campo SEND_ANALOG_FEEDBACK positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And viene verificato il campo sendRequestId dell' evento di timeline "SEND_ANALOG_FEEDBACK"

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_17] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campi municipalityDetails e foreignState positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And viene verificato che nell'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE" siano configurati i campi municipalityDetails e foreignState

  @dev @testLite
  Scenario: [B2B_TIMELINE_ANALOG_18] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campi municipalityDetails e foreignState positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Cucumber Analogic e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato che nell'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" siano configurati i campi municipalityDetails e foreignState