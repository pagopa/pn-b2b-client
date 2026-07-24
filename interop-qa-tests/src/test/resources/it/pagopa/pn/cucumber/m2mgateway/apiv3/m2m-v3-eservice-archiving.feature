@m2m-v3-manual-archiving-eservice
Feature: (M2M v3) Archiviazione manuale di un e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.1] Un ente erogatore di un e-service con prima versione in stato PUBLISHED e seconda in stato DEPRECATED può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene avviata l'archiviazione dell'e-service "%actual" indicando la motivazione "QA test manual-archiving" e un preavviso di 60 giorni
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.2] Un ente erogatore di un e-service con prima versione in stato SUSPENDED e seconda in stato DEPRECATED può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene avviata l'archiviazione dell'e-service "%actual" indicando la motivazione "QA test manual-archiving" e un preavviso di 60 giorni
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.3] Un descrittore con stato ARCHIVED a cui viene applicato il processo di archiviazione manuale dell'e-service, mantiene lo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED ad ARCHIVED
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene avviata l'archiviazione dell'e-service "%actual" indicando la motivazione "QA test manual-archiving" e un preavviso di 60 giorni
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.4] Un utente con ruolo M2M non autorizzato NON può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene avviata l'archiviazione dell'e-service "%actual" indicando la motivazione "QA test manual-archiving" e un preavviso di 60 giorni
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.5] Un utente M2M con token non valido NON può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    And viene avviata l'archiviazione dell'e-service "%actual" indicando la motivazione "QA test manual-archiving" e un preavviso di 60 giorni
    Then si ottiene response status code 401
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.6] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale dell'e-service se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene avviata l'archiviazione dell'e-service "<eserviceId>" indicando la motivazione "<archivingReason>" e un preavviso di 60 giorni
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | eserviceId | archivingReason          | statusCode |
      | %null      | QA test manual-archiving | 400        |
      | %actual    | %null                    | 400        |
      | %null      | %null                    | 400        |
      | %actual    | %blank                   | 400        |
      | %random    | QA test manual-archiving | 404        |

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.7] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale dell'e-service se la stringa archivingReason non rispetta la lunghezza attesa
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene avviata l'archiviazione dell'e-service "%actual" indicando una motivazione di <archivingReasonLength> caratteri e un preavviso di 60 giorni
    Then si ottiene response status code 400
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
       #min lenght = 10 , max lenght = 250
      | archivingReasonLength |
      | 9                     |
      | 251                   |

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.8] Un ente erogatore di un e-service con prima versione in stato PUBLISHED e seconda in stato SUSPENDED può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene avviata l'archiviazione dell'e-service "%actual" indicando la motivazione "QA test manual-archiving" e un preavviso di 60 giorni
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_1.9] Un ente erogatore di un e-service con prima versione in stato SUSPENDED e seconda in stato SUSPENDED può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene avviata l'archiviazione dell'e-service "%actual" indicando la motivazione "QA test manual-archiving" e un preavviso di 60 giorni
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.1] L'ente erogatore di un e-service con prima versione in stato ARCHIVING e seconda in stato ARCHIVING può annullare il processo di archiviazione manuale di un e-service in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene annullato il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.2] L'ente erogatore di un e-service con prima versione in stato ARCHIVING_SUSPENDED e seconda in stato ARCHIVING_SUSPENDED può annullare il processo di archiviazione manuale di un e-service in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene annullato il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "SUSPENDED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "SUSPENDED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.3] Un descrittore con stato ARCHIVED a cui viene applicato il processo di archiviazione manuale dell'e-service e poi viene annullato, manterrà lo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED ad ARCHIVED
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene annullato il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.4] Un utente con ruolo M2M NON può annullare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene annullato il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.5] Un utente con token non valido NON può annullare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    And viene annullato il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 401
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.6] Un ente erogatore di un e-service NON può annullare il processo di archiviazione manuale dell'e-service se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene annullato il processo di archiviazione dell'e-service con id "<eserviceId>"
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | eserviceId | statusCode |
      | %null      | 400        |
      | %random    | 404        |

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_SUSPENSION_1.1] Un ente erogatore di un e-service in stato ARCHIVING è in grado di sospendere l'e-service in questione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And l'utente tenta di sospende quel descrittore
    Then si ottiene response status code <statusCode>
    And la versione più recente dell'e-service è in stato "<finalDescriptorState>"

    Examples:
      | role      | finalDescriptorState | statusCode |
      | m2m-admin | ARCHIVING_SUSPENDED  | 200        |
      | m2m       | ARCHIVING            | 403        |

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_ESERVICE_SUSPENSION_1.2] Un ente erogatore di un e-service in stato ARCHIVING_SUSPENDED è in grado di riattivare l'e-service in questione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And l'utente tenta di effettuare la riattivazione dell'e-service
    Then si ottiene response status code <statusCode>
    And la versione più recente dell'e-service è in stato "<finalDescriptorState>"

    Examples:
      | role      | finalDescriptorState | statusCode |
      | m2m-admin | ARCHIVING            | 200        |
      | m2m       | ARCHIVING_SUSPENDED  | 403        |
