Feature: Probing

    #BUG: https://pagopa.atlassian.net/browse/PIN-9074
  Scenario Outline: [GET_STATUS] - Health probing-ms check
    Given il microservizio <ms> risulta attivo
    Then la response riporta lo status code 204

    Examples:
      | ms                       |
      | "probing-api"            |
      | "probing-statistics-api" |

  Scenario Outline: [GET_ESERVICES_CATALOG] - Consultazione e-service presenti nel catalogo probing (multi-filtro)
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
    When vengono recuperati dal catalogo gli e-service con limit "<limit>" e offset "<offset>" e filtri eserviceName "<eserviceName>", producerName "<producerName>", versionNumber "<versionNumber>", state "<state>"
    Then la response riporta lo status code <statusCode>

    Examples:
    # --- Baseline / boundaries (no filters) ---
      | limit | offset | eserviceName | producerName | versionNumber | state  | statusCode |
      | 10    | 0      | %null        | %null        | %null         | %null  | 200        |
      | 1     | 0      | %null        | %null        | %null         | %null  | 200        |
      | 100   | 0      | %null        | %null        | %null         | %null  | 200        |

    # --- Single filter ---
      | 10    | 0      | %expected    | %null        | %null         | %null  | 200        |
      | 10    | 0      | %null        | %expected    | %null         | %null  | 200        |
      | 10    | 0      | %null        | %null        | 1             | %null  | 200        |
      | 10    | 0      | %null        | %null        | %null         | ACTIVE | 200        |

    # --- Multi-filter (AND) ---
      | 10    | 0      | %expected    | %expected    | %null         | %null  | 200        |
      | 10    | 0      | %null        | %expected    | 1             | %null  | 200        |
      | 10    | 0      | %expected    | %expected    | 1             | ACTIVE | 200        |

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

    #BUG: https://pagopa.atlassian.net/browse/PIN-9078
  Scenario Outline: [UPDATE_FREQUENCY] - Aggiornamento frequency e finestra temporale per e-service
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
    When vengono aggiornati i parametri di probing dell'e-service con eserviceId "<eserviceId>" e versionId "<versionId>" impostando frequency "<frequency>", startDate "<startDate>", endDate "<endDate>" e si verifica che coincidano con quanto atteso
    Then la response riporta lo status code <statusCode>

    Examples:
    # Happy paths
      | eserviceId | versionId | frequency  | startDate | endDate | statusCode |
      | %actual    | %actual   | %actual    | %actual   | %actual | 204        |
      | %actual    | %actual   | %actual+10 | %actual   | %actual | 204        |
      | %actual    | %actual   | %actual    | now+1h    | now+2h  | 204        |

    # Frequency invalid
      | %actual    | %actual   | -1         | %actual   | %actual | 400        |
      | %actual    | %actual   | 0          | %actual   | %actual | 400        |

    # Window invalid
      | %actual    | %actual   | %actual    | now+2h    | now+1h  | 400        |
      | %actual    | %actual   | %actual    | %null     | now+2h  | 400        |
      | %actual    | %actual   | %actual    | now+1h    | %null   | 400        |

    # Not found (wrong ids)
      | %null      | %actual   | %actual    | %actual   | %actual | 400        |
      | %actual    | %null     | %actual    | %actual   | %actual | 400        |
      | %random    | %actual   | %actual    | %actual   | %actual | 404        |
      | %actual    | %random   | %actual    | %actual   | %actual | 404        |

  Scenario Outline: [UPDATE_PROBING_STATE] - Modifica stato di probing con combinazioni id/versione
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
    When viene modificato lo stato di probing dell'e-service con id "<eserviceId>" e id versione "<versionId>" in "<probingEnabled>" e si verifica che coincida con quanto atteso
    Then la response riporta lo status code <statusCode>

    Examples:
    # Happy paths
      | eserviceId | versionId | probingEnabled | statusCode |
      | %actual    | %actual   | true           | 204        |
      | %actual    | %actual   | false          | 204        |

    # ProbingEnabled invalid
      | %actual    | %actual   | %null          | 400        |

    # eserviceId/versionId invalid
      | %actual    | %null     | true           | 400        |
      | %null      | %actual   | true           | 400        |

     # Not found (wrong ids)
      | %actual    | %random   | true           | 404        |
      | %random    | %actual   | true           | 404        |

  Scenario Outline: [UPDATE_OPERATIONAL_STATE] - Modifica stato operativo con combinazioni id/versione
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
    When viene modificato lo stato operativo dell'e-service con id "<eserviceId>" e id versione "<versionId>" in "<eserviceState>" e si verifica che coincida con quanto atteso
    Then la response riporta lo status code <statusCode>

    Examples:
    # Happy paths
      | eserviceId | versionId | eserviceState | statusCode |
      | %actual    | %actual   | ACTIVE        | 204        |
      | %actual    | %actual   | INACTIVE      | 204        |

    # EserviceState invalid
      | %actual    | %actual   | %null         | 400        |

    # eserviceId/versionId invalid
      | %actual    | %null     | ACTIVE        | 400        |
      | %null      | %actual   | ACTIVE        | 400        |

    # Not found (wrong ids)
      | %actual    | %random   | ACTIVE        | 404        |
      | %random    | %actual   | ACTIVE        | 404        |

  Scenario Outline: [GET_PRODUCERS] - Recupero lista producers con paginazione
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
    When recupero la lista dei producers con limit "<limit>" e offset "<offset>" e producerName "<producer>"
    Then la response riporta lo status code <statusCode>

    Examples:
    # Happy paths
      | limit | offset | producer              | statusCode |
      | 30    | 0      | %expected             | 200        |
      | 1     | 0      | %expected             | 200        |
      | 100   | 0      | %expected             | 200        |
      | 30    | 10     | %expected             | 200        |
      | 30    | 0      | %null                 | 200        |
      | 30    | 0      | %blank                | 200        |

    # Required params missing
      | %null | 0      | %expected             | 400        |
      | 10    | %null  | %expected             | 400        |

    # Pagination invalid values
      | 0     | 0      | %expected             | 400        |
      | -1    | 0      | %expected             | 400        |
      | 101   | 0      | %expected             | 400        |
      | 10    | -1     | %expected             | 400        |
      | 0     | -1     | %expected             | 400        |

    # producerName edge cases (should not 400)
      | 30    | 0      | NOT_EXISTING_PRODUCER | 200        |

  Scenario Outline: [GET_ESERVICE_MAIN_DATA] - Recupera i metadati anagrafici di un e-service tramite il suo eserviceRecordId
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
    When vengono recuperati i main data dell'e-service con eserviceRecordId "<eserviceRecordId>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | eserviceRecordId | statusCode |
      | %actual          | 200        |
      | %null            | 400        |
      | -1               | 400        |
      | %random          | 404        |

  Scenario Outline: [GET_ESERVICE_PROBING_DATA] - Recupera i dati di probing di un e-service tramite il suo eserviceRecordId
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
    When vengono recuperati i dati di probing dell'e-service con eserviceRecordId "<eserviceRecordId>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | eserviceRecordId | statusCode |
      | %actual          | 200        |
      | %null            | 400        |
      | -1               | 400        |
      | %random          | 404        |

  Scenario Outline: [GET_ESERVICE_PUBLIC_TELEMETRY] - Recupera la telemetria pubblica di un e-service tramite il suo eserviceRecordId
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
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
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
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

  Scenario Outline: [SCHEDULING] - Update frequency aggiorna lo scheduling
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
    When vengono aggiornati i parametri di probing dell'e-service con eserviceId "%expected" e versionId "%expected" impostando frequency "<frequency>", startDate "<startDate>", endDate "<endDate>" e si verifica che coincidano con quanto atteso
    And viene modificato lo stato di probing dell'e-service con id "%expected" e id versione "%expected" in "true" e si verifica che coincida con quanto atteso
    Then verifica che la responseReceived sia aggiornata coerentemente rispetto la frequency "<frequency>", startDate "<startDate>", endDate "<endDate>"
    And viene modificato lo stato di probing dell'e-service con id "%expected" e id versione "%expected" in "false" e si verifica che coincida con quanto atteso

    Examples:
    # BEFORE window (inizia tra poco)
      | frequency | startDate | endDate |
      | 1         | now+1m    | now+10m |

    # IN window (già dentro)
      | 1         | now-5m    | now+10m |

    # AFTER window (finita da poco)
      | 1         | now-10m   | now-1m  |

    # Boundary start (start = now)
      | 1         | now       | now+10m |

    # Boundary end (end = now)
      | 1         | now-10m   | now     |

    # Start & stop nello stesso test
      | 1         | now       | now+2m  |

    # Window shorter than period
      | 5         | now       | now+2m  |

  Scenario Outline: [SCHEDULING] - Probing disabled non aggiorna mai
    Given vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo
    And viene modificato lo stato di probing dell'e-service con id "%expected" e id versione "%expected" in "false" e si verifica che coincida con quanto atteso
    When vengono aggiornati i parametri di probing dell'e-service con eserviceId "%expected" e versionId "%expected" impostando frequency "<frequency>", startDate "<startDate>", endDate "<endDate>" e si verifica che coincidano con quanto atteso
    Then verifica che la responseReceived NON sia aggiornata quando probing è disabilitato

    Examples:
      | frequency | startDate | endDate |
      | 1         | now-5m    | now+10m |
      | 1         | now+1m    | now+10m |
      | 1         | now-10m   | now-1m  |
      | 5         | now       | now+2m  |


