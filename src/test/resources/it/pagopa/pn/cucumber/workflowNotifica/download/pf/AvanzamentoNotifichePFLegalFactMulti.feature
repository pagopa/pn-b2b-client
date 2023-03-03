Feature: Download legalFact multi destinatario

  @ignore @tbc
  Scenario: [B2B_WEB-MULTI-RECIPIENT_LEGALFACT_1] Invio notifica multi destinatario_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    And "Mario Cucumber" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then sono presenti 2 attestazioni opponibili RECIPIENT_ACCESS

