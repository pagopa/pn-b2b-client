Feature: Api b2b per destinatari strutturati


  Scenario: [Scenario-1] Viene invocatae l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di tutti gli enti
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "Mario Gherkin" viene delegato da "GherkinSrl"
    And "Mario Gherkin" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega