@m2m-apiv3-client-consumer
Feature: Creazione dei client di tipo consumer - API v3

  Scenario Outline: [CREATE_CLIENT_CONSUMER_1] Creazione nuovo client di tipo consumer per un utente m2m-admin
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 200
    And l'oggetto Client restituito rispetta quanto atteso

    # Happy path
    Examples:
      | name                                                               | description                                                               | members                                            |
      | %random                                                            | %random                                                                   | []                                                 |
      | %random                                                            | %blank                                                                    | [%admin]                                           |
      | NomeMoltoLungoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA | %null                                                                     | [%api,security]                                    |
      | %random                                                            | DescrizioneMoltoLungaBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB | [%security]                                        |
      | %random                                                            | %random                                                                   | [%api]                                             |
      | %random                                                            | %random                                                                   | [%admin, %api,security, %security, %api, %support] |

  Scenario Outline: [CREATE_CLIENT_CONSUMER_2] Validazione input per un utente m2m-admin alla creazione di un client consumer
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 400

    # Required sad path
    Examples:
      | name    | description | members |
      | %null   | %random     | []      |
      | %random | %random     | %null   |

    # Input pericolosi ma potenzialmente ammessi
    Examples:
      | name                           | description                    | members    |
      | !@#$%^&*()_+-=[]{};':",.<>/?\| | %random                        | [%admin]   |
      | %random                        | !@#$%^&*()_+-=[]{};':",.<>/?\| | [%admin]   |
      | ' OR '1'='1                    | SQL injection test             | [%admin]   |
      | admin'; DROP TABLE users; --   | SQL destructive                | [%api]     |
      | 😀😎🔥💥                       | emoji unicode                  | [%support] |
      | 名字测试                           | chinese unicode                | [%admin]   |
      | тестовоеИмя                    | cyrillic unicode               | [%api]     |
      | اسم_اختبار                     | arabic unicode                 | [%support] |
      | <script>alert(1)</script>      | XSS script                     | [%admin]   |
      | <img src=x onerror=alert(1)>   | XSS img                        | [%api]     |
      | name\nnewline                  | escaped newline                | [%support] |
      | name\ttab                      | escaped tab                    | [%admin]   |
      | name\\backslash                | escaped backslash              | [%api]     |
      | name\"quote                    | escaped quote                  | [%support] |
      | leadingSpace                   | leading space                  | [%admin]   |
      | trailingSpace                  | trailing space                 | [%api]     |
      | multiple   spaces              | multiple spaces                | [%support] |
      | name\u0000test                 | null byte                      | [%admin]   |
      | name\u200Btest                 | zero width space               | [%api]     |
      | "name":"value"                 | json injection                 | [%support] |
      | }{ malformed                   | broken json                    | [%admin]   |
      | api/security                   | slash inside                   | [%api]     |
      | user@domain.com                | email-like                     | [%support] |
      | tenant#123                     | hash char                      | [%admin]   |

    # Stringhe di 256 char
    Examples:
      | name                                                                                                                                                                                                                                                             | description                                                                                                                                                                                                                                                      | members    |
      | AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA | %random                                                                                                                                                                                                                                                          | [%support] |
      | %random                                                                                                                                                                                                                                                          | BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB | [%support] |

  Scenario Outline: [CREATE_CLIENT_CONSUMER_3] Un utente m2m non può creare un client di tipo consumer
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 403

    # Required sad path
    Examples:
      | name    | description | members |
      | %null   | %random     | []      |
      | %random | %random     | []      |

    #Il 409 non è sicuro che sarà implementato
  Scenario Outline: [CREATE_CLIENT_CONSUMER_4] Un client di tipo consumer, creabile solo da un utente con ruolo m2m-admin, deve avere un nome univoco
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    And si ottiene response status code 200
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    And si ottiene response status code 409
    Then l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    And si ottiene response status code 403

    # Required sad path
    Examples:
      | name            | description | members |
      | name_client_409 | %random     | []      |

  Scenario Outline: [CREATE_CLIENT_CONSUMER_5] Non è possibile creare un client consumer se nella request non è presente l'header Authentication
    Given l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 401

    Examples:
      | name    | description | members | role      |
      | %random | %random     | []      | m2m       |
      | %random | %random     | []      | m2m-admin |

  Scenario Outline: [CREATE_CLIENT_CONSUMER_6] Non è possibile creare un client consumer se nella request non è presente l'header DPoP
    Given l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And viene rimosso l'header di autenticazione DPoP
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 401

    Examples:
      | name    | description | members | role      |
      | %random | %random     | []      | m2m       |
      | %random | %random     | []      | m2m-admin |



