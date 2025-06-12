@m2m-agreements
Feature: Gestione degli agreements

  Scenario Outline: [M2M_AGREEMENTS_LIST_1] La lista degli agreements può essere visionata da un utente con ruolo M2M o M2M-ADMIN
    Given "PA1" ha già creato e pubblicato 5 e-services
    And l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    And "<ente>" ha un agreement m2m attivo per ciascun e-service di "PA1"
    When l'utente tenta di recuperare una lista di 5 agreements creati
    Then si ottiene lo status code 200
    And sono stati visualizzati correttamente 5 agreements creati
    Examples:
      | ruolo        | ruolo-m2m  |
      | admin        | m2m-admin  |
      | admin        | m2m        |

  Scenario: [M2M_AGREEMENTS_LIST_2] La lista degli agreements NON può essere visionata da un utente che ha presentato un token m2m scaduto
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "<ente>" ha un agreement m2m attivo per ciascun e-service di "PA1"
    And viene impostato per l'utente un token m2m scaduto
    When l'utente tenta di recuperare una lista di 1 agreements creati
    Then si ottiene lo status code 401

