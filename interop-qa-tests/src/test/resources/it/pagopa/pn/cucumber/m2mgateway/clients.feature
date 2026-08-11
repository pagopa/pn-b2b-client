@hotfix-2.15
Feature: Gestione dei clients attraverso APIs M2M V2

  @m2m-client
  Scenario Outline: [M2M_CLIENTS_GET_1] Un client di tipo API non è recuperabile tramite API M2M
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare il client
    Then si ottiene status code 404
    Examples:
      | ruolo-m2m |
      | m2m-admin |
      | m2m       |

  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2M_CLIENTS_PURPOSES_1] Le finalità correlate ad un certo client possono essere visualizzate da un utente con ruolo M2M-ADMIN o M2M (Parte2#Scenario 28)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" ha già creato 1 client "CONSUMER"
    And l'utente associa la finalità al client con successo
    When l'utente è un "admin" di "PA2" con ruolo M2M <ruolo-m2m>
    And l'utente tenta di ottenere le finalità associate al client
    Then si ottiene status code 200
    And le finalità associate al client sono state correttamente visualizzate
    Examples:
      | ruolo-m2m |
      | m2m-admin |
      | m2m       |

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_CLIENTS_PURPOSES_2] Le finalità correlate ad un certo client non possono essere visualizzate specificando un token non valido (Parte2#Scenario 30)
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di ottenere le finalità associate ad un client inesistente
    Then si ottiene status code 401

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_CLIENTS_PURPOSES_3] Le finalità correlate ad un certo client non possono essere visualizzate specificando un id inesistente (Parte2#Scenario 31)
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di ottenere le finalità associate ad un client inesistente
    Then si ottiene status code 404

  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2M_CLIENTS_PURPOSES_CATALOG_1] Consultazione finalità associate a un client (multi-filtro) con ruolo m2m-admin
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" ha già creato 1 client "CONSUMER"
    And l'utente associa la finalità al client con successo
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    And l'utente associa la finalità al client con successo
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And vengono recuperate le finalità associate al client "<client>" con limit "<limit>" e offset "<offset>" e filtri eserviceIds "<eserviceIds>", states "<states>"
    Then si ottiene status code <statusCode>

    Examples:
    # Baseline / boundaries (no filters)
      | client  | limit | offset | eserviceIds | states               | statusCode |
      | %actual | 10    | 0      | %null       | %null                | 200        |
      | %actual | 1     | 0      | %null       | %null                | 200        |
      | %actual | 50    | 0      | %null       | %null                | 200        |

    # Single filter
      | %actual | 10    | 0      | %actual     | %null                | 200        |
      | %actual | 10    | 0      | %null       | ACTIVE               | 200        |
      | %actual | 10    | 0      | %null       | DRAFT                | 200        |
      | %actual | 10    | 0      | %null       | WAITING_FOR_APPROVAL | 200        |
      | %actual | 10    | 0      | %null       | SUSPENDED            | 200        |
      | %actual | 10    | 0      | %null       | ARCHIVED             | 200        |

    # Multi-filter (AND)
      | %actual | 10    | 0      | %actual     | ARCHIVED             | 200        |

    # Required params missing
      | %actual | %null | 0      | %null       | %null                | 400        |
      | %actual | 10    | %null  | %null       | %null                | 400        |

    # Invalid client
      | %random | 10    | 0      | %null       | %null                | 404        |
      | %null   | 10    | 0      | %null       | %null                | 400        |

    # Pagination invalid values
      | %actual | 0     | 0      | %null       | %null                | 400        |
      | %actual | 51    | 0      | %null       | %null                | 400        |
      | %actual | 10    | -1     | %null       | %null                | 400        |

    # Filter values edge cases (%blank treated as no filter)
      | %actual | 10    | 0      | %blank      | %null                | 200        |
      | %actual | 10    | 0      | %null       | %blank               | 200        |

  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2M_CLIENTS_PURPOSES_CATALOG_2] Consultazione finalità associate a un client (multi-filtro) con ruolo m2m
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" ha già creato 1 client "CONSUMER"
    And l'utente associa la finalità al client con successo
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    And l'utente associa la finalità al client con successo
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m
    And vengono recuperate le finalità associate al client "<client>" con limit "<limit>" e offset "<offset>" e filtri eserviceIds "<eserviceIds>", states "<states>"
    Then si ottiene status code <statusCode>

    Examples:
    # Baseline / boundaries (no filters)
      | client  | limit | offset | eserviceIds | states               | statusCode |
      | %actual | 10    | 0      | %null       | %null                | 200        |
      | %actual | 1     | 0      | %null       | %null                | 200        |
      | %actual | 50    | 0      | %null       | %null                | 200        |

    # Single filter
      | %actual | 10    | 0      | %actual     | %null                | 200        |
      | %actual | 10    | 0      | %null       | ACTIVE               | 200        |
      | %actual | 10    | 0      | %null       | DRAFT                | 200        |
      | %actual | 10    | 0      | %null       | WAITING_FOR_APPROVAL | 200        |
      | %actual | 10    | 0      | %null       | SUSPENDED            | 200        |
      | %actual | 10    | 0      | %null       | ARCHIVED             | 200        |

    # Multi-filter (AND)
      | %actual | 10    | 0      | %actual     | ARCHIVED             | 200        |

    # Required params missing
      | %actual | %null | 0      | %null       | %null                | 400        |
      | %actual | 10    | %null  | %null       | %null                | 400        |

    # Invalid client
      | %random | 10    | 0      | %null       | %null                | 404        |
      | %null   | 10    | 0      | %null       | %null                | 400        |

    # Pagination invalid values
      | %actual | 0     | 0      | %null       | %null                | 400        |
      | %actual | 51    | 0      | %null       | %null                | 400        |
      | %actual | 10    | -1     | %null       | %null                | 400        |

    # Filter values edge cases (%blank treated as no filter)
      | %actual | 10    | 0      | %blank      | %null                | 200        |
      | %actual | 10    | 0      | %null       | %blank               | 200        |

  @m2m-purpose-client
  Scenario: [M2M_CLIENTS_PURPOSES_CATALOG_3] Consultazione finalità associate a un client (multi-filtro) con token invalido
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" ha già creato 1 client "CONSUMER"
    And l'utente associa la finalità al client con successo
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    And vengono recuperate le finalità associate al client "%actual" con limit "10" e offset "0" e filtri eserviceIds "%null", states "%null"
    Then si ottiene status code 401

  # FIXME il numero di finalità associate era 150, e di client 50; li si riduce rispettivamente a 5 e 3 per rendere più facile il debug. Ripristinare non appena il test è stabile.
  @hotfix-2.22
  @m2m-purpose-client
  Scenario: [M2M_CLIENTS_PURPOSES_CATALOG_4] Recupero dell'insieme di finalità associate a un client
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA2"

    # TODO verificare necessità ed eventualmente eliminare
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service

    And "PA2" ha già creato 1 client "CONSUMER"
    And "PA2" ha già creato 5 finalità in stato "ACTIVE" per quell'eservice
    And l'utente associa le ultime 5 finalità create al client con successo

    And "PA2" ha già creato 3 client "CONSUMER"
    And l'utente associa l'ultima finalità agli ultimi 3 client creati con successo

    And "PA2" ha già creato 1 client "CONSUMER"
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente associa le ultime 1 finalità create al client con successo

    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente recupera tutte le finalità associate al primo client creato
    Then si ottiene status code 200
    And vengono recuperate 5 finalità associate al client
    And le finalità restituite sono tutte e sole le prime 5 finalità create

    And [si fa pulizia dei client e delle finalità create per il test]