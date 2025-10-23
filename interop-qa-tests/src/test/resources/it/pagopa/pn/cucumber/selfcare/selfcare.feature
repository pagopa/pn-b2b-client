@selfcare
Feature: Recupero utenze

  @selfcare1
  Scenario: Viene recuperato la lista di utenti di una stessa organizzazione del chiamante
    Given l'utente è un "admin" di "PA1"
    And viene invocata l'API di recupero utenze per l'istituzione: "PA1"
    Then si verifica che la chiamata a selfcare abbia ritornato uno status code: 200

  @selfcare2
  Scenario: Viene recuperato la lista di utenti per una organizzazione diversa dal chiamante - KO
    Given l'utente è un "admin" di "PA1"
    When viene invocata l'API di recupero utenze per l'istituzione: "GSP"
    Then si verifica che la chiamata a selfcare abbia ritornato uno status code: 403