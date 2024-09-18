Feature: Api b2b per destinatari strutturati

  @useB2B
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
    And si verifica che la risposta contenga lo status code: 201

  @useB2B
  Scenario: [Scenario-2] Viene invocatae l’API di creazione delega da delegante PG a delegato PG con l’accesso alle notifiche da parte di tutti gli enti
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega
    And si verifica che la risposta contenga lo status code: 201

  @useB2B
  Scenario: [Scenario-3] Viene invocatae l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di enti specifici
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "Mario Gherkin" viene delegato da "GherkinSrl" per comune "Comune_1"
    And "Mario Gherkin" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GherkinSrl |
      | senderDenomination | Comune di Milano          |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And si verifica che la risposta contenga status code: 201
    Given viene generata una nuova notifica
      | subject            | invio notifica GherkinSrl |
      | senderDenomination | Comune di Palermo         |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED


  @useB2B
  Scenario: [Scenario-4] Viene invocatae l’API di creazione delega da delegante PG a delegato PG con l’accesso alle notifiche da parte di enti specifici
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_1"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GherkinSrl |
      | senderDenomination | Comune di Milano          |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega
    And si verifica che la risposta contenga lo status code: 201
    Given viene generata una nuova notifica
      | subject            | invio notifica GherkinSrl |
      | senderDenomination | Comune di Palermo         |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED


  @useB2B
  Scenario Outline: [Scenario-5] Viene invocatae l’API di creazione delega da delegante PG a delegato PF con codice fiscale vuoto
    Given viene creato un user con parametri: displayName "<DISPLAYNAME>",firstName "<FIRSTNAME>",lastName "<LASTNAME>",fiscalCode "<FISCALCODE>"
    And "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "Mario Gherkin" viene delegato da "GherkinSrl"
    And si verifica che la risposta contenga lo status code: 400
    Examples:
      | DISPLAYNAME   | FIRSTNAME | LASTNAME                                                                     | FISCALCODE        |
      | Mario Gherkin | Mario     | Gherkin                                                                      | " "               |
      | Mario Gherkin | Mario     | Gherkin                                                                      | CLMC0T42R12D969Z5 |
      | Mario Gherkin | " "       | " "                                                                          | CLMCST42R12D969Z  |
      | Mario Gherkin | Mario     | PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP | CLMCST42R12D969Z  |
      | Mario Gherkin | 861       | ADELFIA                                                                      | CLMCST42R12D969Z  |









