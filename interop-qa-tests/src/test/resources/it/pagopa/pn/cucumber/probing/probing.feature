Feature: Probing

  Scenario Outline: [GET_STATUS] - Health probing-ms check
    Given il microservizio <ms> risulta attivo
    Then la response riporta lo status code 204

    Examples:
      | ms                       |
      | "probing-api"            |
      | "probing-statistics-api" |

  Scenario: [GET_PRODUCERS] - Recupero lista producers con paginazione e producerName
    When recupero la lista dei producers con limit 10 e offset 0 e producerName "PA1"
    Then la response riporta lo status code 200

  Scenario Outline: [GET_PRODUCERS] - Recupero lista producers con paginazione
    When recupero la lista dei producers con limit "<limit>" e offset "<offset>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | limit | offset | statusCode |
      | null  |      0 |        400 |
      |    10 | null   |        400 |
      |    10 |      0 |        200 |

  Scenario: [GET_ESERVICES] - Inserimento nuovo e-service (EServiceAdded) e visibilità in catalogo
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene recuperato l'intero catalogo degli e-service relativo a probing
    Then l'eservice creato è presente nei risultati
    And si ottiene status code 200

  Scenario Outline: [GET_ESERVICES] - Inserimento nuovo e-service (EServiceAdded) e visibilità in catalogo mediante filtri personalizzati
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

  Scenario Outline: [UPDATE_PROBING_STATE] - Modifica stato di probing di un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato di probing dell'e-service creato in "<probingEnabled>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | probingEnabled | statusCode |
      | true           |        204 |
      | false          |        204 |
      | null           |        400 |
    #ToDo Aggiunta Authentication Token

  Scenario: [UPDATE_PROBING_STATE] - Modifica stato di probing di un e-service esistente con versione inesistente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato di probing dell'e-service con id versione "random" in "true"
    Then la response riporta lo status code 404
    #ToDo Aggiunta Authentication Token

  Scenario: [UPDATE_PROBING_STATE] - Modifica stato di probing di un e-service inesistente con versione esistente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato di probing dell'e-service con id "random" e versione valida in "true"
    Then la response riporta lo status code 404
    #ToDo Aggiunta Authentication Token

  Scenario Outline: [GET_ESERVICES_CATALOG] - Consultazione e-service presenti nel catalogo probing
    When vengono recuperati dal catalogo gli e-service con valori di paginazione limit "<limit>" e offset "<offset>" e filtro di tipo "<filter>" con valore "<filterValue>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | limit | offset | filter        | filterValue | statusCode |
      |    10 |      0 | null          | null        |        200 |
      |    10 |      0 | eserviceName  | EService1   |        200 |
      | null  |      0 | producerName  | PA1         |        200 |
      |    10 | null   | versionNumber |           1 |        200 |
      |    10 | null   | state         | ONLINE      |        200 |
      |    10 | null   | null          | null        |        400 |
      | null  |      0 | null          | null        |        400 |

  Scenario Outline: [UPDATE_OPERATIONAL_STATE] - Modifica stato operativo di un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato operativo dell'e-service creato in "<eserviceState>"
    Then la response riporta lo status code <statusCode>

    Examples:
      | eserviceState | statusCode |
      | ACTIVE        |        204 |
      | INACTIVE      |        204 |
      | null          |        400 |
    #ToDo Aggiunta Authentication Token

  Scenario: [UPDATE_OPERATIONAL_STATE] - Modifica stato operativo di un e-service esistente con versione inesistente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato operativo dell'e-service con id versione "random" in "ACTIVE"
    Then la response riporta lo status code 404
    #ToDo Aggiunta Authentication Token

  Scenario: [UPDATE_OPERATIONAL_STATE] - Modifica stato operativo di un e-service inesistente con versione esistente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED"
    And si ottiene status code 200
    When viene modificato lo stato operativo dell'e-service con id "random" e versione valida in "ACTIVE"
    Then la response riporta lo status code 404
    #ToDo Aggiunta Authentication Token


  Scenario: [GET_PRODUCERS] - Recupero lista producers con paginazione e producerName
    When recupero la lista dei producers con limit 10 e offset 0 e producerName "PA1"
    Then la response riporta lo status code 200

  Scenario Outline: [GET_PRODUCERS] - Recupero lista producers con paginazione
    When recupero la lista dei producers con limit "<limit>" e offset "<offset>"
    Then la response riporta lo status code <statusCode>
    Examples:
      | limit | offset |statusCode|
      | null  | 0      | 400      |
      | 10    | null   | 400      |
      | 10    | 0      | 200      |