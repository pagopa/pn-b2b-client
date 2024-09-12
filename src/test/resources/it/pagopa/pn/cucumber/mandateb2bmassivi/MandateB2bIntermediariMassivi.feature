Feature: Pn-mandate api b2b per intermediari massivi

  Scenario: [Scenario-1] Viene invocata l'API di creazione delega dal  delegato (intermediario massivo) verso se stesso a nome del delegante
    Given viene popolata la richiesta
    When viene effettuata la chiamata all'api b2b
    Then si verifica che la risposta contenga status code: 200
    And si verifica che la delega è stata creata con stato pending