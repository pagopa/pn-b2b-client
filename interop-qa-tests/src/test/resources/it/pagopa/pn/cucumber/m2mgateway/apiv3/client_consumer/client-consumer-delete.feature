@m2m-apiv3-client-consumer
Feature: Eliminazione dei client di tipo consumer - API v3

  Scenario Outline: [DELETE_CLIENT_CONSUMER_1] Eliminazione di un client di tipo consumer per un utente m2m-admin
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name    | description | members   |
      | %random | %random     | <members> |
    And si ottiene response status code 200
    And l'oggetto Client restituito rispetta quanto atteso
    Then l'utente tenta l'eliminazione del client di tipo CONSUMER con id "%actual"
    And si ottiene status code 200

    Examples:
      | members        |
      | []             |
      | [admin]        |
      | [api,security] |
      | [security]     |
      | [api]          |
      | [support]      |

  Scenario Outline: [DELETE_CLIENT_CONSUMER_2] Eliminazione di un client di tipo consumer con id invalido o inesistente e/o ruolo non autorizzato
    Given l'utente è un "admin" di "PA1" con ruolo M2M <role>
    When l'utente tenta l'eliminazione del client di tipo CONSUMER con id "<clientId>"
    Then si ottiene status code <statusCode>

    Examples:
      | clientId | role      | statusCode |
    # Invalid id
      | %null    | m2m-admin | 400        |
      | %null    | m2m       | 400        |

    # Random id
      | %random  | m2m-admin | 404        |

    # Role not authorized
      | %random  | m2m       | 403        |
    # Test non eseguibile per via del controllo implicito sul nullble di keychainId nel client openapi generato
    #  | %null      | m2m       | 403        |

  Scenario Outline: [DELETE_CLIENT_CONSUMER_3] Non è possibile eliminare un client consumer se nella request non è presente l'header Authentication
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name    | description | members |
      | %random | %random     | []      |
    And si ottiene response status code 200
    And l'oggetto Client restituito rispetta quanto atteso
    Given l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And viene impostato per l'utente un token m2m non valido
    Then l'utente tenta l'eliminazione del client di tipo CONSUMER con id "%actual"
    Then si ottiene response status code 401

    Examples:
      | role      |
      | m2m       |
      | m2m-admin |

  Scenario Outline: [DELETE_CLIENT_CONSUMER_4] Non è possibile eliminare un client consumer se nella request non è presente l'header Authentication
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name    | description | members |
      | %random | %random     | []      |
    And si ottiene response status code 200
    And l'oggetto Client restituito rispetta quanto atteso
    Given l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And viene rimosso l'header di autenticazione DPoP
    Then l'utente tenta l'eliminazione del client di tipo CONSUMER con id "%actual"
    Then si ottiene response status code 400

    Examples:
      | role      |
      | m2m       |
      | m2m-admin |

