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
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | eserviceId | archivingReason          | archivingReason | statusCode |
      | %null      | QA test manual-archiving | %actual         | 400        |
      | %actual    | %null                    | %actual         | 400        |
      | %null      | %null                    | %actual         | 400        |
      | %actual    | %empty                   | %actual         | 400        |
      | %random    | QA test manual-archiving | %actual         | 404        |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.7] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale dell'e-service se la stringa archivingReason non rispetta la lunghezza attesa
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione composta da <archivingReasonLength> caratteri
    Then si ottiene response status code 400
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
       #min lenght = 10 , max lenght = 250
      | archivingReasonLength |
      | 9                     |
      | 251                   |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.1] L'ente erogatore di un e-service in stato PUBLISHED può annullare il processo di archiviazione manuale di un e-service in corso
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED. Poi , avviato il processo di archiviazione, diventerà ARCHIVING
    Examples:
      | role         | initialFirstDescriptorState | finalFirstDescriptorState |
      | admin        | PUBLISHED                   | DEPRECATED                |
      | api          | PUBLISHED                   | DEPRECATED                |
      | api,security | PUBLISHED                   | DEPRECATED                |
    #primo descrittore in stato SUSPENDED
      | admin        | SUSPENDED                   | SUSPENDED                 |
      | api          | SUSPENDED                   | SUSPENDED                 |
      | api,security | SUSPENDED                   | SUSPENDED                 |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.2] L'ente erogatore di un e-service in stato SUSPENDED può annullare il processo di archiviazione manuale di un e-service in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And la versione più recente dell'e-service è in stato "SUSPENDED"

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED. Poi , avviato il processo di archiviazione, diventerà ARCHIVING
    Examples:
      | initialFirstDescriptorState | finalFirstDescriptorState |
      | PUBLISHED                   | DEPRECATED                |
    #primo descrittore in stato SUSPENDED
      | SUSPENDED                   | SUSPENDED                 |

  Scenario: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.3] Un descrittore con stato ARCHIVED a cui viene applicato il processo di archiviazione manuale dell'e-service e poi viene annullato, mantiene lo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED ad ARCHIVED
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.4] Un utente con ruolo non autorizzato NON può annullare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role     |
      | security |
      | support  |

  Scenario : [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.5] Un utente con token non valido NON può annullare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    And viene impostato per l'utente un token non valido
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 401
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.6] Un ente erogatore di un e-service NON può annullare il processo di archiviazione manuale dell'e-service se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "<eserviceId>"
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    Examples:
      | eserviceId | statusCode |
      | %null      | 400        |
      | %random    | 404        |

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_2.1] Avendo un e-service con un solo descrittore in stato PUBLISHED. Dopo la pubblicazione di un nuovo descrittore, il descrittore precedente, se non ha richieste di fruizione attive, passerà automaticamente allo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"

  Scenario: [MANUAL_ARCHIVING_ESERVICE_3.1] Essendo in corso l'archiviazione di un e-service, se il descrittore più recente in stato ARCHIVING non ha più richieste di fruizioni attive, questo NON sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario: [MANUAL_ARCHIVING_ESERVICE_3.2] Essendo in corso l'archiviazione di un e-service, se il descrittore più recente in stato ARCHIVING_SUSPENDED non ha più richieste di fruizioni attive, questo NON sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"