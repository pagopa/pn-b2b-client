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
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | role         | initialFirstDescriptorState | finalFirstDescriptorState |
      | admin        | PUBLISHED                   | ARCHIVING                 |
      | api          | PUBLISHED                   | ARCHIVING                 |
      | api,security | PUBLISHED                   | ARCHIVING                 |
    #primo descrittore in stato SUSPENDED
#      | admin        | SUSPENDED                   | ARCHIVING_SUSPENDED       |
#      | api          | SUSPENDED                   | ARCHIVING_SUSPENDED       |
#      | api,security | SUSPENDED                   | ARCHIVING_SUSPENDED       |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.2] Un ente erogatore di un e-service in stato SUSPENDED può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

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
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.4] Un utente con ruolo non autorizzato NON può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | role     |
      | security |
      | support  |

  Scenario: [MANUAL_ARCHIVING_ESERVICE_1.5] Un utente con token non valido NON può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And viene impostato per l'utente un token non valido
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 401
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.6] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale dell'e-service se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "<eserviceId>" e specificando la motivazione "<archivingReason>"
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
      | %actual    | %empty                   | 400        |
      | %random    | QA test manual-archiving | 404        |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.7] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale dell'e-service se la stringa archivingReason non rispetta la lunghezza attesa
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione composta da <archivingReasonLength> caratteri
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

  Scenario: [MANUAL_ARCHIVING_ESERVICE_2.1] L'avvio del processo di archiviazione dell'e-service, causa l'eliminazione dell'ultimo descrittore in stato DRAFT, se presente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già creato una versione in "DRAFT" per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 204
    And l'ultimo descrittore in stato DRAFT è stato cancellato
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_3.1] L'aggiornamento di un agreement nei confronti della versione più recente di un e-service NON va a buon fine nel caso quest'ultimo sia in archiviazione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene response status code 400
    And la richiesta di fruizione assume lo stato "ACTIVE"

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_1.1] Un ente erogatore di un e-service in stato ARCHIVING è in grado di sospendere l'e-service in questione
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente sospende quel descrittore
    Then si ottiene response status code <statusCode>
    And la versione più recente dell'e-service è in stato "<finalDescriptorState>"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | role         | finalDescriptorState | statusCode |
      | admin        | ARCHIVING_SUSPENDED  | 204        |
      | api          | ARCHIVING_SUSPENDED  | 204        |
      | api,security | ARCHIVING_SUSPENDED  | 204        |
      | support      | ARCHIVING            | 403        |
      | security     | ARCHIVING            | 403        |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_1.2] Un ente erogatore di un e-service in stato ARCHIVING_SUSPENDED è in grado di riattivare l'e-service in questione
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente attiva il descrittore di quell'e-service
    Then si ottiene response status code <statusCode>
    And la versione più recente dell'e-service è in stato "<finalDescriptorState>"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | role         | finalDescriptorState | statusCode |
      | admin        | ARCHIVING            | 204        |
      | api          | ARCHIVING            | 204        |
      | api,security | ARCHIVING            | 204        |
      | support      | ARCHIVING_SUSPENDED  | 403        |
      | security     | ARCHIVING_SUSPENDED  | 403        |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.1] L'ente erogatore di un e-service in stato PUBLISHED può annullare il processo di archiviazione manuale di un e-service in corso
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

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
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "SUSPENDED"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

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

  Scenario: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.5] Un utente con token non valido NON può annullare il processo di archiviazione manuale dell'e-service
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

  Scenario Outline: [AUTOMATIC_ARCHIVING_ESERVICE_1.1] Con archiviazione del primo e meno recente descrittore in corso. Se l'unica richiesta di fruizione attiva verso quel descrittore viene archiviata, questo sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | initialFirstDescriptorState |
      | PUBLISHED                   |
      | PUBLISHED                   |

  Scenario Outline: [AUTOMATIC_ARCHIVING_ESERVICE_1.2] Con archiviazione dell'intero e-service in corso. Se l'unica richiesta di fruizione attiva verso il primo e meno recente descrittore viene archiviata, tale descrittore sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    Examples:
      | initialFirstDescriptorState |
      | PUBLISHED                   |
      | PUBLISHED                   |

  Scenario Outline: [AUTOMATIC_ARCHIVING_ESERVICE_1.3] Con archiviazione manuale dell'intero e-service e parallelamente del primo e meno recente descrittore in corso. Se l'unica richiesta di fruizione attiva verso il primo descrittore viene archiviata, tale descrittore sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    Examples:
      | initialFirstDescriptorState |
      | PUBLISHED                   |
      | PUBLISHED                   |

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_2.1] Avendo un e-service con un solo descrittore in stato PUBLISHED. Dopo la pubblicazione di un nuovo descrittore, il descrittore precedente, se non ha richieste di fruizione attive, passerà automaticamente allo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_3.1] Essendo in corso l'archiviazione di un e-service, se il descrittore più recente in stato ARCHIVING non ha più richieste di fruizioni attive, questo NON sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_3.2] Essendo in corso l'archiviazione di un e-service, se il descrittore più recente in stato ARCHIVING_SUSPENDED non ha più richieste di fruizioni attive, questo NON sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_4.1] Con archiviazione dell'e-service in corso. Se avviene un archiviazione automatica data dall'archiviazione dell'ultima richiesta di fruizione e a seguito viene annullato il processo di archiviazione, il descrittore archiviato in automatico rimarrà in stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    And "PA2" ha già archiviato quella richiesta di fruizione
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  Scenario: [DIFFERENT_TENANT_ARCHIVING_ESERVICE_1.1] Il processo di archiviazione dell'intero e-service NON può essere eseguito da un ente differente dall'erogatore dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA3"
    And l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  Scenario: [DIFFERENT_TENANT_ARCHIVING_ESERVICE_1.2] Il processo di archiviazione dell'intero e-service NON può essere annullato da un ente differente dall'erogatore dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente è un "admin" di "PA3"
    And l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario: [DIFFERENT_TENANT_ARCHIVING_ESERVICE_1.3] Il processo di archiviazione dell'intero e-service NON può essere effettuato da un ente differente dall'erogatore dell'e-service anche se questo è delegato all'erogazione
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA3" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA3"
    And l'utente è un "admin" di "PA3"
    And l'utente accetta la delega
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  Scenario: [DIFFERENT_TENANT_ARCHIVING_ESERVICE_1.4] Il processo di archiviazione dell'intero e-service NON può essere annullato da un ente differente dall'erogatore dell'e-service anche se questo è delegato all'erogazione
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    And l'ente "PA3" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA3"
    And l'utente è un "admin" di "PA3"
    And l'utente accetta la delega
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario: [DIFFERENT_TENANT_ARCHIVING_ESERVICE_2.1] Il processo di archiviazione dell'intero e-service può essere effettuato dall'ente erogatore senza dover prima revocare la delega in erogazione attiva verso il delegato dell'e-service in questione
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA3" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA3"
    And l'utente è un "admin" di "PA3"
    And l'utente accetta la delega
    And l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario Outline: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_1.1] Un ente erogatore può avviare il processo di archiviazione dell'intero e-service anche se l'archiviazione di uno specifico descrittore di quell'e-service è già in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    Examples:
      | initialFirstDescriptorState | finalFirstDescriptorState |
      | PUBLISHED                   | ARCHIVING                 |
      | SUSPENDED                   | ARCHIVING_SUSPENDED       |

  Scenario Outline: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_2.1] Un ente erogatore può annullare il processo di archiviazione dello specifico descrittore in corso anche se è parallelamente in esecuzione l'archiviazione dell'intero e-service
  E' in corso un processo di archiviazione combinata. Ovvero archiviazione dell'intero e-service e del primo e meno recente descrittore contemporaneamente
  L'ente può annullare il processo di archiviazione del primo descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    Examples:
      | initialFirstDescriptorState | finalFirstDescriptorState |
      | PUBLISHED                   | ARCHIVING                 |
      | SUSPENDED                   | ARCHIVING_SUSPENDED       |

  Scenario Outline: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_3.1] Un ente erogatore può annullare il processo di archiviazione dell'intero e-service in corso anche se è parallelamente in esecuzione l'archiviazione di uno specifico descrittore
  E' in corso un processo di archiviazione combinata. Ovvero archiviazione dell'intero e-service e del primo e meno recente descrittore contemporaneamente
  L'ente può annullare il processo di archiviazione dell'intero e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione della vecchia versione con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | initialFirstDescriptorState | finalFirstDescriptorState |
      | PUBLISHED                   | ARCHIVING                 |
      | SUSPENDED                   | ARCHIVING_SUSPENDED       |

  Scenario Outline: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_4.1] Un ente erogatore NON può avviare il processo di archiviazione dello specifico descrittore se l'archiviazione dell'intero e-service è già in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente archivia la vecchia versione con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code 409
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    Examples:
      | initialFirstDescriptorState | finalFirstDescriptorState |
      | PUBLISHED                   | ARCHIVING                 |
      | SUSPENDED                   | ARCHIVING_SUSPENDED       |