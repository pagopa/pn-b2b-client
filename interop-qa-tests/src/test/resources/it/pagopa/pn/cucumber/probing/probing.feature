Feature: Probing

  Scenario Outline: [GET_STATUS] - Health probing-ms check
    Given il microservizio <ms> risulta attivo
    Then la response riporta lo status code 204
    Examples:
      | ms                       |
      | "probing-api"            |
      | "probing-statistics-api" |