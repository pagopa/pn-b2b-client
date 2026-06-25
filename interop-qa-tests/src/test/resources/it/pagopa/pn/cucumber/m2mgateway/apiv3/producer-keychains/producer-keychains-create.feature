@m2m-apiv3-producer-keychains
Feature: Creazione dei producer keychains - API v3

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_1] Creazione nuovo portachiavi erogatore per un utente m2m-admin
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un portachiavi erogatore per il tenant "PA1" con:
      | name    | description | members   |
      | %random | %random     | <members> |
    Then si ottiene response status code 200
    And l'oggetto ProducerKeychain restituito rispetta quanto atteso

    Examples:
      | members                              |
    # Members void
      | []                                   |

    # Populating members with roles
      | [api,security]                       |
      | [admin]                              |
      | [security]                           |
      | [api]                                |
      | [support]                            |
      | [admin, api,security, security, api] |
    # Il ruolo support non è stato inserito poichè in config ha lo stesso id di api,security e nell'array devono essere univoci
    # | %random | %random     | [admin, api,security, security, api, support] |

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_1b] Validazione input per un utente m2m-admin alla creazione di un portachiavi erogatore
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un portachiavi erogatore per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 200

    # Input pericolosi ma ammessi
    Examples:
      | name                           | description                    | members   |
      | !@#$%^&*()_+-=[]{};':",.<>/?\| | %random                        | [api]     |
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

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_2] Validazione input per un utente m2m-admin alla creazione di un portachiavi erogatore
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di creare un portachiavi erogatore per il tenant "PA1" con:
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
    # Description null (required)
      | %random                                                            | %null                                                                                                                                                                                                                                                                                                        | [admin]             |

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

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_3] Un utente m2m non può creare un portachiavi erogatore
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di creare un portachiavi erogatore per il tenant "PA1" con:
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
  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_4] Un portachiavi erogatore, creabile solo da un utente con ruolo m2m-admin, deve avere un nome univoco
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di creare un portachiavi erogatore per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    And si ottiene response status code 200
    When l'utente tenta di creare un portachiavi erogatore per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    And si ottiene response status code 409
    Then l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di creare un portachiavi erogatore per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    And si ottiene response status code 403

    # Required sad path
    Examples:
      | name            | description | members |
      | name_client_409 | %random     | []      |

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_5] Non è possibile creare un portachiavi erogatore se nella request non è presente l'header Authentication
    Given l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di creare un portachiavi erogatore per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 401

    Examples:
      | name    | description | members | role      |
      | %random | %random     | []      | m2m       |
      | %random | %random     | []      | m2m-admin |

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_6] Non è possibile creare un portachiavi erogatore se nella request non è presente l'header DPoP
    Given l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And viene rimosso l'header di autenticazione DPoP
    When l'utente tenta di creare un portachiavi erogatore per il tenant "PA1" con:
      | name   | description   | members   |
      | <name> | <description> | <members> |
    Then si ottiene response status code 400

    Examples:
      | name    | description | members | role      |
      | %random | %random     | []      | m2m       |
      | %random | %random     | []      | m2m-admin |



