Feature: Probing

  Scenario Outline: [GET_STATUS] - Health probing-ms check
    Given il microservizio <ms> risulta attivo
    Then la response riporta lo status code 204
    Examples:
      | ms                       |
      | "probing-api"            |
      | "probing-statistics-api" |

  Scenario: [GET_PRODUCERS] - Recupero lista producers con paginazione e producerName
    When recupero la lista dei producers con limit 10 e offset 0 e producerName "PA1"
    Then la response riporta lo status code 200

  Scenario Outline: [GET_PRODUCERS] - Recupero lista producers con paginazione
    When recupero la lista dei producers con limit "<limit>" e offset "<offset>"
    Then la response riporta lo status code <statusCode>
    Examples:
      | limit | offset |statusCode|
      | null  | 0      | 400      |
      | 10    | null   | 400      |
      | 10    | 0      | 200      | 