@m2m-apiv3-client-consumer
Feature: Creazione dei client di tipo consumer - API v3

  Scenario Outline: [CREATE_CLIENT_CONSUMER_1] Creazione nuovo client di tipo consumer per un utente m2m-admin
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 200
    And l'oggetto Client restituito rispetta quanto atteso

    Examples:
      | name    | description | members                              |
    # Members void
      | %random | %random     | []                                   |
    # Description null
      | %random | %null       | []                                   |
    # Populating members with roles
      | %random | %null       | [api,security]                       |
      | %random | %random     | [admin]                              |
      | %random | %random     | [security]                           |
      | %random | %random     | [api]                                |
      | %random | %random     | [support]                            |
      | %random | %random     | [admin, api,security, security, api] |

  Scenario Outline: [CREATE_CLIENT_CONSUMER_1b] Validazione input per un utente m2m-admin alla creazione di un client consumer
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 200

    # Input pericolosi ma potenzialmente ammessi
    Examples:
      | name                           | description                    | members   |
      | !@#$%^&*()_+-=[]{};':",.<>/?\| | %random                        | [admin]   |
      | %random                        | !@#$%^&*()_+-=[]{};':",.<>/?\| | [admin]   |
      | ' OR '1'='1                    | SQL injection test             | [admin]   |
      | admin'; DROP TABLE users; --   | SQL destructive                | [api]     |
      | 😀😎🔥💥                       | emoji unicode                  | [support] |
      | 名字测试试                          | chinese unicode                | [admin]   |
      | тестовоеИмя                    | cyrillic unicode               | [api]     |
      | اسم_اختبار                     | arabic unicode                 | [support] |
      | name\nnewline                  | escaped newline                | [support] |
      | name\ttab                      | escaped tab                    | [admin]   |
      | name\\backslash                | escaped backslash              | [api]     |
      | name\"quote                    | escaped quote                  | [support] |
      | leadingSpace                   | leading space                  | [admin]   |
      | trailingSpace                  | trailing space                 | [api]     |
      | multiple   spaces              | multiple spaces                | [support] |
      | name\u0000test                 | null byte--                    | [admin]   |
      | name\u200Btest                 | zero width space               | [api]     |
      | "name":"value"                 | json injection                 | [support] |
      | }{ malformed                   | broken json                    | [admin]   |
      | api/security                   | slash inside                   | [api]     |
      | user@domain.com                | email-like                     | [support] |
      | tenant#123                     | hash char--                    | [admin]   |

  Scenario Outline: [CREATE_CLIENT_CONSUMER_2] Validazione input per un utente m2m-admin alla creazione di un client consumer
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 400

    Examples:
      | name                                                               | description                                                                                                                                                                                                                                                                                                  | members             |
    # Description < min (10 char)
      | %random                                                            | < min                                                                                                                                                                                                                                                                                                        | [admin]             |
    # Description > max (250 char)
      | %random                                                            | Questa è una descrizione estesa utilizzata per testare la creazione di un client di tipo consumer all'interno del sistema. Include dettagli aggiuntivi per verificare la corretta gestione dei campi testuali, la persistenza dei dati e il comportamento dell'API in presenza di input lunghi e articolati. | [admin]             |
    # Description blank
      | %random                                                            | %blank                                                                                                                                                                                                                                                                                                       | [admin]             |

    # Name > max (60 char)
      | NomeMoltoLungoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA | %null                                                                                                                                                                                                                                                                                                        | [api,security]      |
    # Name < min (5 char)
      | Nome                                                               | %null                                                                                                                                                                                                                                                                                                        | [api,security]      |
    # Name null
      | %null                                                              | %random                                                                                                                                                                                                                                                                                                      | []                  |

    # Members null
      | %random                                                            | %random                                                                                                                                                                                                                                                                                                      | %null               |
    # Members with duplicate user
      | %random                                                            | %random                                                                                                                                                                                                                                                                                                      | [admin, admin]      |
      | %random                                                            | %random                                                                                                                                                                                                                                                                                                      | [admin, api, admin] |

  Scenario Outline: [CREATE_CLIENT_CONSUMER_3] Un utente m2m non può creare un client di tipo consumer
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di creare un client di tipo CONSUMER per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 403

    # Required sad path
    Examples:
      | name    | description | members        |
      | %random | %random     | []             |
      | %random | %random     | [admin]        |
      | %random | %random     | [admin, admin] |

    #Il 409 non è sicuro che sarà implementato
  @ignore
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
    Then si ottiene response status code 400

    Examples:
      | name    | description | members | role      |
      | %random | %random     | []      | m2m       |
      | %random | %random     | []      | m2m-admin |



