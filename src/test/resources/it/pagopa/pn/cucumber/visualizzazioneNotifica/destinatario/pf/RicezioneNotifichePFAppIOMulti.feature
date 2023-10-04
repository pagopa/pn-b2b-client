Feature: recupero notifiche tramite api AppIO b2b

  @appIo
  Scenario: [B2B-PA-APP-IO_6] Invio notifica con api b2b e recupero tramite AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere recuperata tramite AppIO

  @appIo
  Scenario: [B2B-PA-APP-IO_7] Invio notifica con api b2b paProtocolNumber e idemPotenceToken e recupero tramite AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | idempotenceToken | AME2E3626070001.1  |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere recuperata tramite AppIO
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.2"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere recuperata tramite AppIO

  @appIo
  Scenario: [B2B-PA-APP-IO_8] Invio notifica con api b2b uguale creditorTaxId e diverso codice avviso recupero tramite AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere recuperata tramite AppIO
    And viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere recuperata tramite AppIO

  @ignore @tbc
  Scenario: [B2B-PA-APP-IO_9] Invio notifica con api b2b e recupero documento notificato con AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then "Mario Cucumber" recupera il documento notificato tramite AppIO

  @ignore @tbc
  Scenario: [B2B-PA-APP-IO_10] Invio notifica con api b2b e recupero documento notificato con AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then "Mario Gherkin" recupera il documento notificato tramite AppIO

  @appIo
  Scenario: [B2B-PA-APP-IO_11] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then "Mario Cucumber" recupera la notifica tramite AppIO

  @appIo
  Scenario: [B2B-PA-APP-IO_12] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then "Mario Gherkin" recupera la notifica tramite AppIO







