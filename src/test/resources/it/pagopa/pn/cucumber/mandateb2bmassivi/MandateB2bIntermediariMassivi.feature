Feature: Pn-mandate api b2b per intermediari massivi

  Scenario: [Scenario-1] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante
    Given viene popolata la richiesta con:
      | citizen       | VALID_CITIZEN |
    When viene effettuata la chiamata all'api b2b
    Then si verifica che la risposta contenga status code: 200
    And si verifica che la delega è stata creata con stato pending

  Scenario: [Scenario-2] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con codice fiscale vuoto
    Given viene popolata la richiesta con:
      | citizen       | EMPTY_FISCAL_CODE  |
    When viene effettuata la chiamata all'api b2b
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario: [Scenario-3] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con codice fiscale non valido
    Given viene popolata la richiesta con:
      | citizen       | INVALID_FISCAL_CODE |
    When viene effettuata la chiamata all'api b2b
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario: [Scenario-4] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega formalmente non valida
    Given viene popolata la richiesta con:
      | endTo         | INVALID_FORMAT |
    When viene effettuata la chiamata all'api b2b
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario: [Scenario-5] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega antecedente alla data odierna
    Given viene popolata la richiesta con:
      | endTo         | PAST_DATE |
    When viene effettuata la chiamata all'api b2b
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario: [Scenario-6] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con data di fine delega vuota
    Given viene popolata la richiesta con:
      | endTo         | EMPTY_DATE |
    When viene effettuata la chiamata all'api b2b
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario: [Scenario-7] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con campo nome e cognome vuoti
    Given viene popolata la richiesta con:
      | citizen       | EMPTY_NAME |
    When viene effettuata la chiamata all'api b2b
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata

  Scenario Outline: [Scenario-8] Viene invocata l'API di creazione delega dal delegato (intermediario massivo) verso se stesso a nome del delegante con campo nome e cognome con lunghezza maggiore di 80 caratteri
    Given viene popolata la richiesta con:
      | citizen       | <citizen> |
    When viene effettuata la chiamata all'api b2b
    Then si verifica che la risposta contenga status code: 400
    And si verifica che la delega non è stata creata
    Examples:
      | citizen |
      | FIRST_NAME_NOT_VALID |
      | LAST_NAME_NOT_VALID  |