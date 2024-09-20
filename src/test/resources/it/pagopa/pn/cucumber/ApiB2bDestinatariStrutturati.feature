Feature: Api b2b per destinatari strutturati

  @useB2B
  Scenario: [Scenario-1] Viene invocata l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di tutti gli enti
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "Mario Gherkin" viene delegato da "GherkinSrl"
    And "Mario Gherkin" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega


  @useB2B
  Scenario: [Scenario-2] Viene invocata l’API di creazione delega da delegante PG a delegato PG con l’accesso alle notifiche da parte di tutti gli enti
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega


  @useB2B
  Scenario: [Scenario-3] Viene invocata l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di enti specifici
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "Mario Gherkin" viene delegato da "GherkinSrl" per comune "Comune_1"
    And "Mario Gherkin" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GherkinSrl |
      | senderDenomination | Comune di Milano          |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED


  @useB2B
  Scenario: [Scenario-4] Viene invocata l’API di creazione delega da delegante PG a delegato PG con l’accesso alle notifiche da parte di enti specifici
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_1"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GherkinSrl |
      | senderDenomination | Comune di Milano          |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega


  @useB2B
  Scenario Outline: [Scenario-5-6-9-10-11-12] Viene invocata l’API di creazione delega da delegante PG a delegato PF con campi non conformi
    Given viene creato un user con i seguenti valori:
      | displayName | <DISPLAYNAME> |
      | firstName   | <FIRSTNAME>   |
      | lastName    | <LASTNAME>    |
      | fiscalCode  | <FISCALCODE>  |
    And "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "Mario Gherkin" viene delegato da "GherkinSrl"
    Then si verifica che lo status code sia: 400
    Examples:
      | DISPLAYNAME                                                                  | FIRSTNAME                                                                    | LASTNAME                                                                     | FISCALCODE        |
      | Mario Gherkin                                                                | Mario                                                                        | Gherkin                                                                      | " "               |
      | Mario Gherkin                                                                | Mario                                                                        | Gherkin                                                                      | CLMC0T42R12D969Z5 |
      | Mario Gherkin                                                                | ""                                                                           | Gherkin                                                                      | CLMCST42R12D969Z  |
      | Mario Gherkin                                                                | Mario                                                                        | ""                                                                           | CLMCST42R12D969Z  |
      | Mario Gherkin                                                                | ""                                                                           | ""                                                                           | CLMCST42R12D969Z  |
      | Mario Gherkin                                                                | Mario                                                                        | PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP | CLMCST42R12D969Z  |
      | Mario Gherkin                                                                | PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP | Gherkin                                                                      | CLMCST42R12D969Z  |
      | ""                                                                           | Mario                                                                        | Gherkin                                                                      | CLMCST42R12D969Z  |
      | PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP | Mario                                                                        | Gherkin                                                                      | CLMCST42R12D969Z  |


  @useB2B
  Scenario: [Scenario-8] Viene invocata l’API di creazione delega da delegante PG a delegato PF con data di fine delega antecedente a quella di inizio
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "Mario Gherkin" viene delegato da "GherkinSrl" con data di fine delega antecedente a quella di inizio
    Then si verifica che lo status code sia: 400

  ##TODO
  @useB2B
  Scenario: [Scenario-7] Viene invocata l’API di creazione delega da delegante PG a delegato PF con data di fine delega antecedente a quella di inizio
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "Mario Gherkin" viene delegato da "GherkinSrl" con data di fine delega antecedente a quella di inizio
    Then si verifica che lo status code sia: 400

  @useB2B
  Scenario: [Scenario-13] Viene invocata l’API di creazione delega da delegante PG a se stesso
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "GherkinSrl" viene delegato da "GherkinSrl"

  @useB2B
  Scenario: [Scenario-14] Viene invocata l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di tutti gli enti
    Given "Mario Gherkin" viene delegato da "GherkinSrl"
    And "Mario Gherkin" accetta la delega "GherkinSrl"
    And "Mario Gherkin" viene delegato da "GherkinSrl"
    Then si verifica che lo status code sia: 400










