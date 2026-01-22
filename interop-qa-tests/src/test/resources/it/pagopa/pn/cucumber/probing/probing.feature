Feature: Probing

  Scenario Outline: [GET_STATUS] - Health probing-ms check
    Given il microservizio <ms> risulta attivo
    Then la response riporta lo status code 204

    Examples:
      | ms                       |
      | "probing-api"            |
      | "probing-statistics-api" |

  Scenario: [GET_PRODUCERS_1] - Recupero lista producers con paginazione e producerName
    When recupero la lista dei producers con limit 10 e offset 0 e producerName "PA1"
    Then la response riporta lo status code 200

  Scenario Outline: [GET_PRODUCERS_2] - Recupero lista producers con paginazione
    When recupero la lista dei producers con limit "<limit>" e offset "<offset>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | limit | offset | statusCode |
      | null  | 0      | 400        |
      | 10    | null   | 400        |
      | 10    | 0      | 200        |

  Scenario: [GET_ESERVICES_1] - Inserimento nuovo e-service (EServiceAdded) e visibilità in catalogo
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene recuperato l'intero catalogo degli e-service relativo a probing
    Then l'eservice creato è presente nei risultati
    And si ottiene status code 200

  Scenario Outline: [GET_ESERVICES_2] - Inserimento nuovo e-service (EServiceAdded) e visibilità in catalogo mediante filtri personalizzati
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene recuperato nel catalogo di probing l'eservice creato filtrando per "<filter>"
    Then l'eservice creato è presente nei risultati
    And si ottiene status code 200

    Examples:
      | filter   |
      | producer |
      | name     |

  Scenario Outline: [UPDATE_FREQUENCY] - Aggiornamento frequency e finestra temporale per e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    And vengono settati i parametri di probing di default per l'e-service
    When aggiorno i parametri di probing dell'e-service con eserviceId "<eserviceId>" e versionId "<versionId>" impostando frequency "<frequency>", startDate "<startDate>", endDate "<endDate>"
    Then la response riporta lo status code <statusCode>
    And se lo status code è 204 verifica che i parametri di probing recuperati coincidano con quelli attesi

    Examples:
    # Happy paths
      | eserviceId | versionId | frequency | startDate | endDate | statusCode |
      | corretto   | corretto  | keep      | keep      | keep    | 204        |
      | corretto   | corretto  | 10        | keep      | keep    | 204        |
      | corretto   | corretto  | keep      | now+1h    | now+2h  | 204        |

    # Frequency invalid
      | corretto   | corretto  | -1        | keep      | keep    | 400        |
      | corretto   | corretto  | 0         | keep      | keep    | 400        |

    # Window invalid
      | corretto   | corretto  | keep      | now+2h    | now+1h  | 400        |
      | corretto   | corretto  | keep      | null      | now+2h  | 400        |
      | corretto   | corretto  | keep      | now+1h    | null    | 400        |

    # Not found (wrong ids)
      | null       | corretto  | 10        | keep      | keep    | 400        |
      | corretto   | null      | 10        | keep      | keep    | 400        |
      | random     | corretto  | 10        | keep      | keep    | 404        |
      | corretto   | random    | 10        | keep      | keep    | 404        |
    #ToDo Aggiunta Authentication Token

  Scenario Outline: [UPDATE_PROBING_STATE_1] - Modifica stato di probing di un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato di probing dell'e-service creato in "<probingEnabled>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | probingEnabled | statusCode |
      | true           | 204        |
      | false          | 204        |
      | null           | 400        |
    #ToDo Aggiunta Authentication Token

  Scenario Outline: [UPDATE_PROBING_STATE_2] - Modifica stato di probing con combinazioni id/versione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato di probing dell'e-service con id "<eserviceId>" e id versione "<versionId>" in "true"
    Then la response riporta lo status code <statusCode>
    #ToDo Aggiunta Authentication Token

    Examples:
      | eserviceId | versionId | statusCode |
      | corretto   | random    | 404        |
      | random     | corretto  | 404        |

  Scenario Outline: [GET_ESERVICES_CATALOG] - Consultazione e-service presenti nel catalogo probing
    When vengono recuperati dal catalogo gli e-service con valori di paginazione limit "<limit>" e offset "<offset>" e filtro di tipo "<filter>" con valore "<filterValue>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | limit | offset | filter        | filterValue | statusCode |
      | 10    | 0      | null          | null        | 200        |
      | 10    | 0      | eserviceName  | EService1   | 200        |
      | null  | 0      | producerName  | PA1         | 200        |
      | 10    | null   | versionNumber | 1           | 200        |
      | 10    | null   | state         | ONLINE      | 200        |
      | 10    | null   | null          | null        | 400        |
      | null  | 0      | null          | null        | 400        |

  Scenario Outline: [UPDATE_OPERATIONAL_STATE] - Modifica stato operativo di un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato operativo dell'e-service creato in "<eserviceState>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | eserviceState | statusCode |
      | ACTIVE        | 204        |
      | INACTIVE      | 204        |
      | null          | 400        |
    #ToDo Aggiunta Authentication Token

  Scenario Outline: [UPDATE_OPERATIONAL_STATE] - Modifica stato operativo con combinazioni id/versione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato operativo dell'e-service con id "<eserviceId>" e id versione "<versionId>" in "ACTIVE"
    Then la response riporta lo status code <statusCode>
    #ToDo Aggiunta Authentication Token

    Examples:
      | eserviceId | versionId | statusCode |
      | null       | random    | 404        |
      | random     | null      | 404        |

  Scenario: [GET_PRODUCERS_1] - Recupero lista producers con paginazione e producerName
    When recupero la lista dei producers con limit 10 e offset 0 e producerName "PA1"
    Then la response riporta lo status code 200

  Scenario Outline: [GET_PRODUCERS_2] - Recupero lista producers con paginazione
    When recupero la lista dei producers con limit "<limit>" e offset "<offset>"
    Then la response riporta lo status code <statusCode>
    Examples:
      | limit | offset | statusCode |
      | null  | 0      | 400        |
      | 10    | null   | 400        |
      | 10    | 0      | 200        |