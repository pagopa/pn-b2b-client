Feature: Probing

  Scenario Outline: [GET_STATUS] - Health probing-ms check
    Given il microservizio <ms> risulta attivo
    Then la response riporta lo status code 204

    Examples:
      | ms                       |
      | "probing-api"            |
      | "probing-statistics-api" |

  Scenario Outline: [GET_ESERVICES_CATALOG] - Consultazione e-service presenti nel catalogo probing (multi-filtro)
    When vengono recuperati dal catalogo gli e-service con limit "<limit>" e offset "<offset>" e filtri eserviceName "<eserviceName>", producerName "<producerName>", versionNumber "<versionNumber>", state "<state>"
    Then la response riporta lo status code <statusCode>

    Examples:
    # --- Baseline / boundaries (no filters) ---
      | limit | offset | eserviceName | producerName | versionNumber | state  | statusCode |
      | 10    | 0      | %null        | %null        | %null         | %null  | 200        |
      | 1     | 0      | %null        | %null        | %null         | %null  | 200        |
      | 100   | 0      | %null        | %null        | %null         | %null  | 200        |

    # --- Single filter ---
      | 10    | 0      | EService1    | %null        | %null         | %null  | 200        |
      | 10    | 0      | %null        | PA1          | %null         | %null  | 200        |
      | 10    | 0      | %null        | %null        | 1             | %null  | 200        |
      | 10    | 0      | %null        | %null        | %null         | ONLINE | 200        |

    # --- Multi-filter (AND) ---
      | 10    | 0      | EService1    | PA1          | %null         | %null  | 200        |
      | 10    | 0      | %null        | PA1          | 1             | %null  | 200        |
      | 10    | 0      | EService1    | PA1          | 1             | ONLINE | 200        |

    # --- Required params missing ---
      | %null | 0      | %null        | %null        | %null         | %null  | 400        |
      | 10    | %null  | %null        | %null        | %null         | %null  | 400        |

    # --- Pagination invalid values ---
      | 0     | 0      | %null        | %null        | %null         | %null  | 400        |
      | 101   | 0      | %null        | %null        | %null         | %null  | 400        |
      | 10    | -1     | %null        | %null        | %null         | %null  | 400        |

    # --- Filter values edge cases (%blank treated as no filter) ---
      | 10    | 0      | %blank       | %null        | %null         | %null  | 200        |
      | 10    | 0      | %null        | %blank       | %null         | %null  | 200        |

    # --- versionNumber invalid ---
      | 10    | 0      | %null        | %null        | 0             | %null  | 400        |
      | 10    | 0      | %null        | %null        | -1            | %null  | 400        |

  Scenario Outline: [UPDATE_FREQUENCY] - Aggiornamento frequency e finestra temporale per e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    And vengono settati i parametri di probing di default per l'e-service
    When aggiorno i parametri di probing dell'e-service con eserviceId "<eserviceId>" e versionId "<versionId>" impostando frequency "<frequency>", startDate "<startDate>", endDate "<endDate>"
    Then la response riporta lo status code <statusCode>

    Examples:
    # Happy paths
      | eserviceId | versionId | frequency | startDate | endDate | statusCode |
      | %actual    | %actual   | %keep     | %keep     | %keep   | 204        |
      | %actual    | %actual   | 10        | %keep     | %keep   | 204        |
      | %actual    | %actual   | %keep     | now+1h    | now+2h  | 204        |

    # Frequency invalid
      | %actual    | %actual   | -1        | %keep     | %keep   | 400        |
      | %actual    | %actual   | 0         | %keep     | %keep   | 400        |

    # Window invalid
      | %actual    | %actual   | %keep     | now+2h    | now+1h  | 400        |
      | %actual    | %actual   | %keep     | %null     | now+2h  | 400        |
      | %actual    | %actual   | %keep     | now+1h    | %null   | 400        |

    # Not found (wrong ids)
      | %null      | %actual   | 10        | %keep     | %keep   | 400        |
      | %actual    | %null     | 10        | %keep     | %keep   | 400        |
      | %random    | %actual   | 10        | %keep     | %keep   | 404        |
      | %actual    | %random   | 10        | %keep     | %keep   | 404        |
    #ToDo Aggiunta Authentication Token

  Scenario Outline: [UPDATE_PROBING_STATE] - Modifica stato di probing con combinazioni id/versione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato di probing dell'e-service con id "<eserviceId>" e id versione "<versionId>" in "<probingEnabled>"
    Then la response riporta lo status code <statusCode>
    #ToDo Aggiunta Authentication Token

    Examples:
    # Happy paths
      | eserviceId | versionId | probingEnabled | statusCode |
      | %actual    | %actual   | true           | 204        |
      | %actual    | %actual   | false          | 204        |

    # ProbingEnabled invalid
      | %actual    | %actual   | %null          | 400        |

     # Not found (wrong ids)
      | %actual    | %null     | true           | 404        |
      | %null      | %actual   | true           | 404        |
      | %actual    | %random   | true           | 404        |
      | %random    | %actual   | true           | 404        |

  Scenario Outline: [UPDATE_OPERATIONAL_STATE] - Modifica stato operativo con combinazioni id/versione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato operativo dell'e-service con id "<eserviceId>" e id versione "<versionId>" in "<eserviceState>"
    Then la response riporta lo status code <statusCode>
    #ToDo Aggiunta Authentication Token

    Examples:
    # Happy paths
      | eserviceId | versionId | eserviceState | statusCode |
      | %actual    | %actual   | ACTIVE        | 204        |
      | %actual    | %actual   | INACTIVE      | 204        |

    # EserviceState invalid
      | %actual    | %actual   | %null         | 400        |

    # Not found (wrong ids)
      | %actual    | %null     | ACTIVE        | 404        |
      | %null      | %actual   | ACTIVE        | 404        |
      | %actual    | %random   | ACTIVE        | 404        |
      | %random    | %actual   | ACTIVE        | 404        |

  Scenario Outline: [GET_PRODUCERS] - Recupero lista producers con paginazione
    When recupero la lista dei producers con limit "<limit>" e offset "<offset>" e producerName "<producer>"
    Then la response riporta lo status code <statusCode>

    Examples:
    # Happy paths
      | limit | offset | producer              | statusCode |
      | 30    | 0      | PA1                   | 200        |
      | 1     | 0      | PA1                   | 200        |
      | 100   | 0      | PA1                   | 200        |
      | 30    | 10     | PA1                   | 200        |
      | 30    | 0      | %null                 | 200        |
      | 30    | 0      | %blank                | 200        |

    # Required params missing
      | %null | 0      | PA1                   | 400        |
      | 10    | %null  | PA1                   | 400        |

    # Pagination invalid values
      | 0     | 0      | PA1                   | 400        |
      | -1    | 0      | PA1                   | 400        |
      | 101   | 0      | PA1                   | 400        |
      | 10    | -1     | PA1                   | 400        |
      | 0     | -1     | PA1                   | 400        |

    # producerName edge cases (should not 400)
      | 30    | 0      | NOT_EXISTING_PRODUCER | 200        |

  Scenario Outline: [GET_ESERVICE_PROFILE_DATA] - Recupera i metadati anagrafici di un e-service tramite il suo eserviceRecordId
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When vengono recuperati i metadati anagrafici dell'e-service con eserviceRecordId "<eserviceRecordId>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | eserviceRecordId | statusCode |
      | %actual          | 200        |
      | %null            | 400        |
      | -1               | 400        |
      | %random          | 404        |


  Scenario Outline: [GET_ESERVICE_PROBING_DATA] - Recupera i dati di probing di un e-service tramite il suo eserviceRecordId
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When vengono recuperati i dati di probing dell'e-service con eserviceRecordId "<eserviceRecordId>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | eserviceRecordId | statusCode |
      | %actual          | 200        |
      | %null            | 400        |
      | -1               | 400        |
      | %random          | 404        |

  Scenario Outline: [GET_ESERVICE_PUBLIC_TELEMETRY] - Recupera la telemetria pubblica di un e-service tramite il suo eserviceRecordId
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    And vengono settati i parametri di probing di default per l'e-service
    When viene recuperata la telemetria pubblica dell'e-service con eserviceRecordId "<eserviceRecordId>" e pollingFrequency "<frequency>"
    Then la response riporta lo status code <statusCode>

    Examples:
    # Happy paths
      | eserviceRecordId | frequency | statusCode |
      | %actual          | %actual   | 200        |

    # Frequency invalid values
      | %actual          | %null     | 400        |
      | %actual          | -1        | 400        |

    # eserviceRecordId invalid values
      | %null            | %actual   | 400        |
      | %random          | %actual   | 400        |

  Scenario Outline: [GET_ESERVICE_TELEMETRY] - Recupera la telemetria di un e-service tramite il suo eserviceRecordId e filtro temporale
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    And vengono settati i parametri di probing di default per l'e-service
    When viene recuperata la telemetria dell'e-service con eserviceRecordId "<eserviceRecordId>" e impostando pollingFrequency "<frequency>" , startDate "<startDate>" , endDate "<endDate>"
    Then la response riporta lo status code <statusCode>

    Examples:
    # Happy paths
      | eserviceRecordId | frequency | startDate | endDate | statusCode |
      | %actual          | %actual   | now+1h    | now+2h  | 200        |

    # eserviceRecordId invalid values
      | %null            | %actual   | now+1h    | now+2h  | 400        |
      | %random          | %actual   | now+1h    | now+2h  | 400        |
      | -1               | %actual   | now+1h    | now+2h  | 400        |

    # frequency invalid values
      | %actual          | %null     | now+1h    | now+2h  | 400        |
      | %actual          | -1        | now+1h    | now+2h  | 400        |

    # startDate invalid values
      | %actual          | %actual   | %null     | now+2h  | 400        |
      | %actual          | %actual   | abc       | now+2h  | 400        |

    # endDate invalid values
      | %actual          | %actual   | now+1h    | %null   | 400        |
      | %actual          | %actual   | now+1h    | abc     | 400        |
      #ToDo Aggiunta Authentication Token