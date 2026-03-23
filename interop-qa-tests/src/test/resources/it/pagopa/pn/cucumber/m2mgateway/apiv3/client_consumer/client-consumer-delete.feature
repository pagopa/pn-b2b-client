@m2m-apiv3-client-consumer
Feature: Eliminazione dei client di tipo consumer - API v3

  Scenario Outline: [DELETE_CLIENT_CONSUMER_1] Eliminazione di un client di tipo consumer per un utente m2m-admin
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    And si ottiene response status code 200
    And l'oggetto Client restituito rispetta quanto atteso
    Then l'utente tenta l'eliminazione del client "%actual" di tipo CONSUMER
    And si ottiene status code 200

    # Happy path
    Examples:
      | name    | description | members                                            |
      | %random | %random     | []                                                 |
      | %random | %blank      | [%admin]                                           |
      | %random | %null       | [%api,security]                                    |
      | %random | %random     | [%security]                                        |
      | %random | %random     | [%api]                                             |
      | %random | %random     | [%admin, %api,security, %security, %api, %support] |

  Scenario Outline: [DELETE_CLIENT_CONSUMER_2] Eliminazione di un client di tipo consumer con id invalido e/o ruolo non autorizzato
    Given l'utente è un "admin" di "PA1" con ruolo M2M <role>
    When l'utente tenta l'eliminazione del client "<clientId>" di tipo CONSUMER
    Then si ottiene status code <statusCode>

    Examples:
      | clientId | role      | statusCode |
    # Random id
      | %random  | m2m-admin | 404        |

    # Invalid id
      | %null    | m2m-admin | 400        |

    # Role not authorized
      | %random  | m2m       | 403        |
      | %null    | m2m       | 403        |

  Scenario Outline: [DELETE_CLIENT_CONSUMER_5] Non è possibile eliminare un client consumer se nella request non è presente l'header Authentication
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name    | description | members |
      | %random | %random     | []      |
    And si ottiene response status code 200
    And l'oggetto Client restituito rispetta quanto atteso
    Given l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And viene impostato per l'utente un token m2m non valido
    Then l'utente tenta l'eliminazione del client "%actual" di tipo CONSUMER
    Then si ottiene response status code 401

    Examples:
      | role      |
      | m2m       |
      | m2m-admin |

  Scenario Outline: [DELETE_CLIENT_CONSUMER_5] Non è possibile eliminare un client consumer se nella request non è presente l'header Authentication
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name    | description | members |
      | %random | %random     | []      |
    And si ottiene response status code 200
    And l'oggetto Client restituito rispetta quanto atteso
    Given l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And viene rimosso l'header di autenticazione DPoP
    Then l'utente tenta l'eliminazione del client "%actual" di tipo CONSUMER
    Then si ottiene response status code 401

    Examples:
      | role      |
      | m2m       |
      | m2m-admin |

