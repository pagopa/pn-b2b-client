Feature: Download legalFact per la persona giuridica

  @testLigth
  Scenario: [B2B_PA_LEGALFACT_PG_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then la PA richiede il download dell'attestazione opponibile "SENDER_ACK"

  Scenario: [B2B_PA_LEGALFACT_PG_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @testLigth
  Scenario: [B2B_PA_LEGALFACT_PG_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then la PA richiede il download dell'attestazione opponibile "PEC_RECEIPT"

  Scenario: [B2B_PA_LEGALFACT_KEY_PG_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then viene verificato che la chiave dell'attestazione opponibile "SENDER_ACK" è "PN_LEGAL_FACTS"

  Scenario: [B2B_PA_LEGALFACT_KEY_PG_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then viene verificato che la chiave dell'attestazione opponibile "DIGITAL_DELIVERY" è "PN_LEGAL_FACTS"

  Scenario: [B2B_PA_LEGALFACT_KEY_PG_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then viene verificato che la chiave dell'attestazione opponibile "PEC_RECEIPT" è "PN_EXTERNAL_LEGAL_FACTS"

