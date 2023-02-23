Feature: avanzamento notifiche b2b con workflow cartaceo

  @ignore
  Scenario: [B2B_TIMELINE_ANALOG_1] Invio notifica ed attesa elemento di timeline SCHEDULE_ANALOG_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"

  @ignore
  Scenario: [B2B_TIMELINE_ANALOG_2] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @ignore
  Scenario: [B2B_TIMELINE_ANALOG_3] Invio notifica ed attesa elemento di timeline SEND_ANALOG_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"

  @ignore
  Scenario: [B2B_TIMELINE_ANALOG_4] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"

  @dev
  Scenario: [B2B_TIMELINE_ANALOG_5] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

