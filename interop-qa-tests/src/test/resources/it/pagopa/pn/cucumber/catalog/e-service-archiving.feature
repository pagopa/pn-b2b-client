@manual-archiving-eservice
Feature: Archiviazione manuale di un e-service

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.1] Un ente erogatore di un e-service in stato PUBLISHED può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | role         | initialFirstDescriptorState | finalFirstDescriptorState |
      | admin        | PUBLISHED                   | ARCHIVING                 |
      | api          | PUBLISHED                   | ARCHIVING                 |
      | api,security | PUBLISHED                   | ARCHIVING                 |
    #primo descrittore in stato SUSPENDED
      | admin        | SUSPENDED                   | ARCHIVING_SUSPENDED       |
      | api          | SUSPENDED                   | ARCHIVING_SUSPENDED       |
      | api,security | SUSPENDED                   | ARCHIVING_SUSPENDED       |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.2] Un ente erogatore di un e-service in stato SUSPENDED può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | initialFirstDescriptorState | finalFirstDescriptorState |
      | PUBLISHED                   | ARCHIVING                 |
    #primo descrittore in stato SUSPENDED
      | SUSPENDED                   | ARCHIVING_SUSPENDED       |

  Scenario: [MANUAL_ARCHIVING_ESERVICE_1.3] Un descrittore con stato ARCHIVED a cui viene applicato il processo di archiviazione manuale dell'e-service, mantiene lo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED ad ARCHIVED
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.4] Un utente con ruolo non autorizzato NON può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    Examples:
      | role     |
      | security |
      | support  |

  Scenario : [MANUAL_ARCHIVING_ESERVICE_1.5] Un utente con token non valido NON può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And viene impostato per l'utente un token non valido
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 401
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.6] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale dell'e-service se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "<eserviceId>" e specificando la motivazione "<archivingReason>"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | eserviceId | archivingReason          | archivingReason |
      | %null      | QA test manual-archiving | %actual         |
      | %actual    | %null                    | %actual         |
      | %null      | %null                    | %actual         |
      | %random    | QA test manual-archiving | %actual         |
      | %actual    | %empty                   | %actual         |
