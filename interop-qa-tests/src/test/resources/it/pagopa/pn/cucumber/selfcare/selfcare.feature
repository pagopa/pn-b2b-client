@selfcare
Feature: Recupero utenze

  @selfcare1
  Scenario: [BROKEN_ACCESS_CONTROL_1] Viene recuperato la lista di utenti di una stessa organizzazione del chiamante
    Given l'utente è un "admin" di "PA1"
    And viene invocata l'API di recupero utenze per l'istituzione: "PA1"
    Then si verifica che la chiamata a selfcare abbia ritornato uno status code: 200

  @selfcare2
  Scenario: [BROKEN_ACCESS_CONTROL_2] Viene recuperato la lista di utenti per una organizzazione diversa dal chiamante - KO
    Given l'utente è un "admin" di "PA1"
    When viene invocata l'API di recupero utenze per l'istituzione: "GSP"
    Then si verifica che la chiamata a selfcare abbia ritornato uno status code: 403

  # PST: Scenario 30 - Casi 30.1 e 30.2
  @nuovi-operatori
  Scenario Outline: [NUOVI_OPERATORI_30] Recupero utenti filtrati per ruolo
    Given l'utente è un "admin" di "PA2"
    When viene invocata l'API di recupero utenze per l'istituzione: "PA2" filtrando per ruolo: "<ruolo>"
    Then si verifica che la chiamata a selfcare abbia ritornato uno status code: 200
    And si verifica che la risposta contenga esattamente 1 utente con ruolo "<ruolo>" dell'istituzione: "PA2"

    Examples:
      | ruolo    |
      | reviewer |
      | viewer   |

  Scenario Outline: [USER_ROLES_BROKEN_ACCESS_CONTROL_1] Viene recuperata la lista di utenti da un utente non admin - KO
    Given l'utente è un "<ruolo>" di "PA1"
    When viene invocata l'API di recupero utenze per l'istituzione: "PA1"
    Then si verifica che la chiamata a selfcare abbia ritornato uno status code: 403

    Examples:
      | ruolo        |
      | api          |
      | security     |
      | api,security |
      | support      |