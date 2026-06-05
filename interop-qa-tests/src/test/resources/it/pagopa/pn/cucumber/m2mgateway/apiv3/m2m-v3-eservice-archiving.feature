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

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.4] Un utente con ruolo M2M non autorizzato NON può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
#    And viene avviato processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "PUBLISHED"
#    And il processo di archiviazione della più recente versione dell'e-service è fallita

  Scenario : [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.5] Un utente M2M con token non valido NON può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
#    And viene avviato processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 401
    And la versione più recente dell'e-service è in stato "PUBLISHED"
#    And il processo di archiviazione della più recente versione dell'e-service è fallita

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.6] Un ente erogatore M2M di un e-service NON può avviare il processo di archiviazione manuale dell'e-service se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene avviato il processo di archiviazione dell'e-service con id "<eserviceId>" e specificando la motivazione "<archivingReason>"
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    #    And il processo di archiviazione della più vecchia versione dell'e-service è fallita
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    #    And il processo di archiviazione della più recente versione dell'e-service è fallita

    Examples:
      | eserviceId | archivingReason          | statusCode |
      | %null      | QA test manual-archiving | 400        |
      | %actual    | %null                    | 400        |
      | %null      | %null                    | 400        |
      | %actual    | %empty                   | 400        |
      | %random    | QA test manual-archiving | 404        |

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.7] Un ente erogatore M2M di un e-service NON può avviare il processo di archiviazione manuale dell'e-service se la stringa archivingReason non rispetta la lunghezza attesa
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione composta da <archivingReasonLength> caratteri
    Then si ottiene response status code 400
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
       #min lenght = 10 , max lenght = 250
      | archivingReasonLength |
      | 9                     |
      | 251                   |

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.1] L'ente erogatore M2M di un e-service in stato PUBLISHED può annullare il processo di archiviazione manuale di un e-service in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene annullato il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
#    And il processo di archiviazione della vecchia versione dell'e-service è avvenuto con successo
    And la versione più recente dell'e-service è in stato "PUBLISHED"
#    And il processo di archiviazione della più recente versione dell'e-service è avvenuto con successo

    Examples:
      | initialFirstDescriptorState | finalFirstDescriptorState |
      | PUBLISHED                   | DEPRECATED                |
      | SUSPENDED                   | SUSPENDED                 |

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.2] L'ente erogatore M2M di un e-service in stato SUSPENDED può annullare il processo di archiviazione manuale di un e-service in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene annullato il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
#    And il processo di archiviazione della vecchia versione dell'e-service è avvenuto con successo
    And la versione più recente dell'e-service è in stato "SUSPENDED"
    #    And il processo di archiviazione della più recente versione dell'e-service è avvenuto con successo

    Examples:
      | initialFirstDescriptorState | finalFirstDescriptorState |
      | PUBLISHED                   | DEPRECATED                |
      | SUSPENDED                   | SUSPENDED                 |

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.3] Un descrittore con stato ARCHIVED a cui viene applicato il processo di archiviazione manuale dell'e-service e poi viene annullato, manterrà lo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED ad ARCHIVED
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene annullato il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.4] Un utente con ruolo M2M NON può annullare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
#    And viene annullato il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.5] Un utente M2M con token non valido NON può annullare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
#    And viene annullato il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 401
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.6] Un ente erogatore M2M di un e-service NON può annullare il processo di archiviazione manuale dell'e-service se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene annullato il processo di archiviazione dell'e-service con id "<eserviceId>"
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    Examples:
      | eserviceId | statusCode |
      | %null      | 400        |
      | %random    | 404        |

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_SUSPENSION_1.2] Un ente erogatore M2M di un e-service in stato ARCHIVING_SUSPENDED è in grado di riattivare l'e-service in questione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And l'utente tenta di effettuare la riattivazione dell'e-service
    Then si ottiene response status code <statusCode>
    And la versione più recente dell'e-service è in stato "<finalDescriptorState>"

    Examples:
      | role      | finalDescriptorState | statusCode |
      | m2m-admin | ARCHIVING            | 200        |
      | m2m       | ARCHIVING_SUSPENDED  | 403        |
