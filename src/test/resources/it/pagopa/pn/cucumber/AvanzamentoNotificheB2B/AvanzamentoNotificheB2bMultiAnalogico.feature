Feature: avanzamento b2b notifica multi destinatario analogico

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  Scenario: [B2B_TIMELINE_MULTI_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"

  Scenario: [B2B_TIMELINE_MULTI_RS_2] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok-Retry_RS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"

  @ignore
  Scenario: [B2B_TIMELINE_MULTI_RS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario negativo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con eventCode "KO" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "SEND_ANALOG_FEEDBACK" con eventCode "KO"

  Scenario: [B2B_TIMELINE_MULTI_RIS_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RIS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"

  @ignore
  Scenario: [B2B_TIMELINE_MULTI_RIS_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RIS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con eventCode "KO" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "SEND_ANALOG_FEEDBACK" con eventCode "KO"

  Scenario: [B2B_TIMELINE_MULTI_ANALOG_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "ANALOG_SUCCESS_WORKFLOW"

  Scenario: [B2B_TIMELINE_MULTI_ANALOG_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "ANALOG_SUCCESS_WORKFLOW"

  Scenario: [B2B_TIMELINE_MULTI_ANALOG_3] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RIR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "ANALOG_SUCCESS_WORKFLOW"

  Scenario: [B2B_TIMELINE_MULTI_ANALOG_4] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con eventCode "KO" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "SEND_ANALOG_FEEDBACK" con eventCode "KO"

  Scenario: [B2B_TIMELINE_MULTI_ANALOG_5] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_890 |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con eventCode "KO" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "SEND_ANALOG_FEEDBACK" con eventCode "KO"

  Scenario: [B2B_TIMELINE_MULTI_ANALOG_6] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_RIR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con eventCode "KO" per l'utente 0
    And vengono letti gli eventi e verificho che l'utente 1 non abbia associato un evento "SEND_ANALOG_FEEDBACK" con eventCode "KO"



