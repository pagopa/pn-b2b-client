@m2m-v3-manual-archiving-eservice
Feature: (M2M v3) Archiviazione manuale di un e-service

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.1] Un ente erogatore M2M di un e-service in stato PUBLISHED può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene avviato processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    #    And il processo di archiviazione della vecchia versione dell'e-service è avvenuto con successo
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    #    And il processo di archiviazione della più recente versione dell'e-service è avvenuto con successo

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | initialFirstDescriptorState | finalFirstDescriptorState |
      | PUBLISHED                   | ARCHIVING                 |
    #primo descrittore in stato SUSPENDED
      | SUSPENDED                   | ARCHIVING_SUSPENDED       |

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.2] Un ente erogatore M2M di un e-service in stato SUSPENDED può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene avviato processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    #    And il processo di archiviazione della vecchia versione dell'e-service è avvenuto con successo
    And la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"
    #    And il processo di archiviazione della più recente versione dell'e-service è avvenuto con successo

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | initialFirstDescriptorState | finalFirstDescriptorState |
      | PUBLISHED                   | ARCHIVING                 |
    #primo descrittore in stato SUSPENDED
      | SUSPENDED                   | ARCHIVING_SUSPENDED       |

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.3] Un descrittore con stato ARCHIVED a cui viene applicato il processo di archiviazione manuale dell'e-service, mantiene lo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED ad ARCHIVED
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene avviato processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "ARCHIVING"