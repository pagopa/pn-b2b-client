Feature: avanzamento notifiche b2b con workflow cartaceo verifica

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @con020 @con020success
  Scenario: [B2B_ANALOG_VERIFICA_2] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-WO_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON020 |
      | details_sentAttemptMade | 0 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"

  @giacenza890Complex
  Scenario: [B2B_ANALOG_VERIFICA_3] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-WO-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @giacenza890Complex @con020 @con020success
  Scenario: [B2B_ANALOG_VERIFICA_4] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-WO-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON020 |
      | details_sentAttemptMade | 0 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"

  Scenario: [B2B_ANALOG_VERIFICA_5] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-WO_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "OK"


  Scenario: [B2B_ANALOG_VERIFICA_6] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-WO-DoubleRECAG012 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  Scenario: [B2B_ANALOG_VERIFICA_7] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-WO-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


    #*********************************************

  Scenario: [B2B_ANALOG_VERIFICA_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"