Feature: Gestione dei clients attraverso APIs M2M V2

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
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di ottenere le finalità associate ad un client inesistente
    Then si ottiene status code 401

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_CLIENTS_PURPOSES_3] Le finalità correlate ad un certo client non possono essere visualizzate specificando un id inesistente (Parte2#Scenario 31)
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di ottenere le finalità associate ad un client inesistente
    Then si ottiene status code 404

  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2M_CLIENTS_PURPOSES_CATALOG] - Consultazione finalità associate a un client (multi-filtro)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 2 e-service
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" ha già creato 1 client "CONSUMER"
    And l'utente associa la finalità al client con successo
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente associa la finalità al client con successo
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente associa la finalità al client con successo
    And "PA1" ha già rifiutato l'aggiornamento della stima di carico per quella finalità
    And l'utente sospende quella finalità in stato "REJECTED"
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente associa la finalità al client con successo
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    And l'utente associa la finalità al client con successo
    And "PA2" ha già creato 1 finalità in stato "ARCHIVED" per quell'eservice
    And l'utente associa la finalità al client con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When vengono recuperate le finalità associate al client con limit "<limit>" e offset "<offset>" e filtri eserviceIds "<eserviceIds>", states "<states>"
    Then si ottiene status code <statusCode>

    Examples:
    # Baseline / boundaries (no filters)
      | limit | offset | eserviceIds | states               | statusCode |
      | 10    | 0      | %null       | %null                | 200        |
      | 1     | 0      | %null       | %null                | 200        |
      | 50    | 0      | %null       | %null                | 200        |

    # Single filter
      | 10    | 0      | %actual     | %null                | 200        |
      | 10    | 0      | %null       | ACTIVE               | 200        |
      | 10    | 0      | %null       | DRAFT                | 200        |
      | 10    | 0      | %null       | WAITING_FOR_APPROVAL | 200        |
      | 10    | 0      | %null       | SUSPENDED            | 200        |
      | 10    | 0      | %null       | ARCHIVED             | 200        |

    # Multi-filter (AND)
      | 10    | 0      | %actual     | ARCHIVED             | 200        |

    # Required params missing
      | %null | 0      | %null       | %null                | 400        |
      | 10    | %null  | %null       | %null                | 400        |

    # Pagination invalid values
      | 0     | 0      | %null       | %null                | 400        |
      | 51    | 0      | %null       | %null                | 400        |
      | 10    | -1     | %null       | %null                | 400        |

    # Filter values edge cases (%blank treated as no filter)
      | 10    | 0      | %blank      | %null                | 200        |
      | 10    | 0      | %null       | %blank               | 200        |
