Feature: Probing

  Scenario Outline: [GET_STATUS] - Health probing-ms check
    Given il microservizio <ms> risulta attivo
    Then la response riporta lo status code 204
    Examples:
      | ms                       |
      | "probing-api"            |
      | "probing-statistics-api" |

  Scenario: [GET_ESERVICES] - Inserimento nuovo e-service (EServiceAdded) e visibilità in catalogo
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene recuperato l'intero catalogo degli e-service relativo a probing
    Then l'eservice creato è presente nei risultati
    And si ottiene status code 200


  Scenario Outline: [GET_ESERVICES] - Inserimento nuovo e-service (EServiceAdded) e visibilità in catalogo mediante filtri personalizzati
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene recuperato nel catalogo di probing l'eservice creato filtrando per "<filter>"
    Then l'eservice creato è presente nei risultati
    And si ottiene status code 200

    Examples:
      | filter   |
      | producer |
      | name     |