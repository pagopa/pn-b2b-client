@manual-archiving-eservice
Feature: Archiviazione manuale di un e-service

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.1] Un ente erogatore di un e-service in stato PUBLISHED e seconda versione DEPRECATED, può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  Scenario: [MANUAL_ARCHIVING_ESERVICE_1.2] Un ente erogatore di un e-service in stato SUSPENDED e seconda versione DEPRECATED, può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_1.3] Un descrittore con stato ARCHIVED a cui viene applicato il processo di archiviazione manuale dell'e-service, mantiene lo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.4] Un utente con ruolo non autorizzato NON può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | role     |
      | security |
      | support  |

  Scenario: [MANUAL_ARCHIVING_ESERVICE_1.5] Un utente con token non valido NON può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And viene impostato per l'utente un token non valido
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 401

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.6] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale dell'e-service se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "<eserviceId>" specificando la motivazione "<archivingReason>" e 60 giorni di preavviso
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

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.7] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale dell'e-service se la stringa archivingReason non rispetta la lunghezza attesa
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando una motivazione di <archivingReasonLength> caratteri e 60 giorni di preavviso
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

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.8] Un ente erogatore di un e-service in stato PUBLISHED e seconda versione SUSPENDED, può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  Scenario: [MANUAL_ARCHIVING_ESERVICE_1.9] Un ente erogatore di un e-service in stato SUSPENDED e seconda versione SUSPENDED, può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_2.1] L'avvio del processo di archiviazione dell'e-service, causa l'eliminazione dell'ultimo descrittore in stato DRAFT, se presente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" tenta la creazione di una versione in DRAFT per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204
    And l'ultimo descrittore in stato DRAFT è stato cancellato
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_2.2] Avviare il processo di archiviazione di un e-service in WAITING_FOR_APPROVAL non è possibile
  Non è possibile avviare il processo di archiviazione di un e-service nel caso in cui sia attiva una delega in erogazione sull'e-service in questione
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" tenta la creazione di una versione in DRAFT per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente aggiorna alcuni parametri di quel descrittore
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega
    And l'utente è un "admin" di "PA2"
    And l'utente pubblica l'e-service
    And l'e-service è in stato "WAITING_FOR_APPROVAL"
    When l'utente è un "admin" di "PA1"
    And l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 409
    And la versione più recente dell'e-service è in stato "WAITING_FOR_APPROVAL"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la vecchia versione dell'e-service è in stato "PUBLISHED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_3.1] L'aggiornamento di un agreement nei confronti della versione più recente di un e-service NON va a buon fine nel caso quest'ultimo sia in archiviazione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Given l'utente è un "admin" di "PA2"
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene response status code 400
    And la richiesta di fruizione assume lo stato "ACTIVE"

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_1.1] Un ente erogatore di un e-service in stato ARCHIVING è in grado di sospendere l'e-service in questione e le richieste di fruizione attive non possono generare nuovi voucher
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" ha già creato 1 client "CONSUMER"
    And "PA2" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA2" ha già associato la finalità a quel client
    And un "admin" di "PA2" ha caricato una chiave pubblica nel client
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente sospende quel descrittore in corso di archiviazione
    Then si ottiene response status code 204
    And la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la richiesta di fruizione assume lo stato "ACTIVE"
    When l'utente è un "admin" di "PA2"
    And l'utente richiede la generazione del voucher
    Then la richiesta di generazione del Voucher non va a buon fine

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_1.2] Un utente con ruolo non autorizzato NON può sospendere un e-service in stato ARCHIVING
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente è un "<role>" di "PA1"
    And l'utente sospende quel descrittore in corso di archiviazione
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    Examples:
      | role     |
      | support  |
      | security |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_1.3] Un ente erogatore di un e-service in stato ARCHIVING_SUSPENDED è in grado di riattivare l'e-service in questione e le richieste di fruizione attive possono generare nuovi voucher
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" ha già creato 1 client "CONSUMER"
    And "PA2" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA2" ha già associato la finalità a quel client
    And un "admin" di "PA2" ha caricato una chiave pubblica nel client
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente attiva il descrittore di quell'e-service
    Then si ottiene response status code 204
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la richiesta di fruizione assume lo stato "ACTIVE"
    When l'utente è un "admin" di "PA2"
    And l'utente richiede la generazione del voucher
    Then si ottiene la corretta generazione del voucher

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_1.4] Un utente con ruolo non autorizzato NON può riattivare un e-service in stato ARCHIVING_SUSPENDED
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Given l'utente è un "<role>" di "PA1"
    When l'utente attiva il descrittore di quell'e-service
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | role     |
      | support  |
      | security |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.1] L'ente erogatore di un e-service in stato ARCHIVING può annullare il processo di archiviazione manuale di un e-service in corso
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.2] L'ente erogatore di un e-service in stato SUSPENDED può annullare il processo di archiviazione manuale di un e-service in corso
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "SUSPENDED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  Scenario: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.3] Un descrittore con stato ARCHIVED a cui viene applicato il processo di archiviazione manuale dell'e-service e poi viene annullato, mantiene lo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED ad ARCHIVED
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.4] Un utente con ruolo non autorizzato NON può annullare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente è un "<role>" di "PA1"
    And l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "ARCHIVING"

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

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.6] Un ente erogatore di un e-service NON può annullare il processo di archiviazione manuale dell'e-service se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "<eserviceId>"
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | eserviceId | statusCode |
      | %null      | 400        |
      | %random    | 404        |

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_1.7] L'ente erogatore di un e-service in stato ARCHIVING e prima versione in stato ARCHIVING_SUSPENDED può annullare il processo di archiviazione manuale di un e-service in corso
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "SUSPENDED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_1.1] Con archiviazione del primo e meno recente descrittore in corso in stato ARCHIVING. Se l'unica richiesta di fruizione attiva verso quel descrittore viene archiviata, questo sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_1.2] Con archiviazione dell'intero e-service in corso. Se l'unica richiesta di fruizione attiva verso il primo e meno recente descrittore viene archiviata, tale descrittore in stato ARCHIVING sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_1.3] Con archiviazione manuale dell'intero e-service e parallelamente del primo e meno recente descrittore tramite archiviazione del descrittore singolo in corso. Se l'unica richiesta di fruizione attiva verso il primo descrittore viene archiviata, tale descrittore sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_1.4] Con archiviazione del primo e meno recente descrittore in corso in stato ARCHIVING_SUSPENDED. Se l'unica richiesta di fruizione attiva verso quel descrittore viene archiviata, questo sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_1.5] Con archiviazione dell'intero e-service in corso. Se l'unica richiesta di fruizione attiva verso il primo e meno recente descrittore viene archiviata, tale descrittore in stato ARCHIVING_SUSPENDED sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_2.1] Avendo un e-service con un solo descrittore in stato PUBLISHED. Dopo la pubblicazione di un nuovo descrittore, il descrittore precedente, se non ha richieste di fruizione attive, passerà automaticamente allo stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    Then la vecchia versione dell'e-service è in stato "ARCHIVED"

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_3.1] Essendo in corso l'archiviazione di un e-service, se il descrittore più recente in stato ARCHIVING non ha più richieste di fruizioni attive, questo NON sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When "PA2" ha già archiviato quella richiesta di fruizione
    And l'utente è un "admin" di "PA1"
    Then la versione più recente dell'e-service è in stato "ARCHIVING"

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_3.2] Essendo in corso l'archiviazione di un e-service, se il descrittore più recente in stato ARCHIVING_SUSPENDED non ha più richieste di fruizioni attive, questo NON sarà archiviato in automatico
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When "PA2" ha già archiviato quella richiesta di fruizione
    And l'utente è un "admin" di "PA1"
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
    And l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 403
    And l'utente è un "admin" di "PA1"
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
    And l'utente è un "admin" di "PA1"
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
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
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

  Scenario: [DIFFERENT_TENANT_ARCHIVING_ESERVICE_2.1] Il processo di archiviazione dell'intero e-service NON può essere effettuato dall'ente erogatore se è presente una delega in erogazione attiva per l'e-service in questione
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA3" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA3"
    And l'utente è un "admin" di "PA3"
    And l'utente accetta la delega
    And l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 409
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  Scenario: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_1.1] Un ente erogatore può avviare il processo di archiviazione dell'intero e-service anche se l'archiviazione di uno specifico descrittore in stato ARCHIVING di quell'e-service è già in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_1.2] Un ente erogatore può avviare il processo di archiviazione dell'intero e-service anche se l'archiviazione di uno specifico descrittore in stato ARCHIVING_SUSPENDED di quell'e-service è già in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_2.1] Un ente erogatore può annullare il processo di archiviazione dello specifico descrittore in corso in stato ARCHIVING anche se è parallelamente in esecuzione l'archiviazione dell'intero e-service
  E' in corso un processo di archiviazione combinata. Ovvero archiviazione dell'intero e-service e del primo e meno recente descrittore contemporaneamente
  L'ente può annullare il processo di archiviazione del primo descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_2.2] Un ente erogatore può annullare il processo di archiviazione dello specifico descrittore in corso in stato ARCHIVING_SUSPENDED anche se è parallelamente in esecuzione l'archiviazione dell'intero e-service
  E' in corso un processo di archiviazione combinata. Ovvero archiviazione dell'intero e-service e del primo e meno recente descrittore contemporaneamente
  L'ente può annullare il processo di archiviazione del primo descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_3.1] Un ente erogatore può annullare il processo di archiviazione dell'intero e-service in corso anche se è parallelamente in esecuzione l'archiviazione di uno specifico descrittore in stato ARCHIVING
  E' in corso un processo di archiviazione combinata. Ovvero archiviazione dell'intero e-service e del primo e meno recente descrittore contemporaneamente
  L'ente può annullare il processo di archiviazione dell'intero e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_3.2] Un ente erogatore può annullare il processo di archiviazione dell'intero e-service in corso anche se è parallelamente in esecuzione l'archiviazione di uno specifico descrittore in stato ARCHIVING_SUSPENDED
  E' in corso un processo di archiviazione combinata. Ovvero archiviazione dell'intero e-service e del primo e meno recente descrittore contemporaneamente
  L'ente può annullare il processo di archiviazione dell'intero e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  Scenario: [COMBINED_ARCHIVING_ESERVICE_AND_DESCRIPTOR_4.1] Un ente erogatore NON può avviare il processo di archiviazione dello specifico descrittore se l'archiviazione dell'intero e-service è già in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 400
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  @happy-path
  Scenario Outline: [ARCHIVING_ESERVICE_BY_JOB_1.1] Raggiunta la data finale del tempo di preavviso per l'archiviazione di un e-service, questo risulterà correttamente archiviato
    Then l'utente è un "admin" di "PA1"
    #versione più recente
    And il descrittore con id "<secondDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "ARCHIVED"
    And il descrittore con id "<secondDescriptorId>" dell'e-service avente id "<eserviceId>" è stato correttamente archiviato tramite l'archiviazione manuale dell'intero e-service
    #versione meno recente
    And il descrittore con id "<firstDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "ARCHIVED"
    And il descrittore con id "<firstDescriptorId>" dell'e-service avente id "<eserviceId>" è stato correttamente archiviato tramite l'archiviazione manuale dell'intero e-service
    And la richiesta di fruizione con id "<agreementId>" assume lo stato "ACTIVE"

    Examples:
      | firstDescriptorId                    | secondDescriptorId                   | eserviceId                           | agreementId                          |
#      firstDescriptorId=descrittore in Archiving . secondDescriptorId=descrittore in Archiving
      | 7b379f88-31a6-4e39-9553-f7ad74afde4f | abde2f2c-be8d-46d9-b626-e19b1e7f9490 | 785023e5-9a73-42e1-a6e2-b633baf1618c | f58e0bbd-f58a-4b6b-b98e-231e490797f2 |
#      firstDescriptorId=descrittore in Archiving . secondDescriptorId=descrittore in Archiving_Suspended
      | 6d3174d4-f9bd-4223-a77b-daf2883790fc | 7c04c3c1-deac-4130-91df-805aab47ee12 | c211c470-daab-4b19-aa36-0a53ac5b7940 | a4e5e529-639e-4ada-83b0-e205e6c7b062 |
#      firstDescriptorId=descrittore in Archiving_Suspended . secondDescriptorId=descrittore in Archiving
      | ee9dbc42-289f-400d-86f8-51e8fb704933 | 3c63c85f-7dac-4cdb-a9fe-586442624677 | 45d822b9-066c-4a77-b4a1-331135496907 | acd777e8-8b76-46cb-b822-3656601302eb |

  @happy-path
  Scenario Outline: [ARCHIVING_ESERVICE_BY_JOB_1.2] Se la data finale del tempo di preavviso per l'archiviazione di un e-service non viene raggiunta, questo non risulterà ancora archiviato
    Then l'utente è un "admin" di "PA1"
    #versione più recente
    And il descrittore con id "<secondDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "<secondDescriptorState>"
    And il descrittore con id "<secondDescriptorId>" dell'e-service avente id "<eserviceId>" è in fase di archiviazione tramite l'archiviazione manuale dell'intero e-service
    #versione meno recente
    And il descrittore con id "<firstDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "<firstDescriptorState>"
    And il descrittore con id "<firstDescriptorId>" dell'e-service avente id "<eserviceId>" è in fase di archiviazione tramite l'archiviazione manuale dell'intero e-service
    And la richiesta di fruizione con id "<agreementId>" assume lo stato "ACTIVE"

    Examples:
      | firstDescriptorId                    | secondDescriptorId                   | eserviceId                           | firstDescriptorState | secondDescriptorState | agreementId                          |
#      firstDescriptorId=descrittore in Archiving . secondDescriptorId=descrittore in Archiving
      | 7747c0ab-68ca-4103-aa0a-2ee1784e485e | 2c9b69c0-06b0-4948-bebc-de6e0c21dc44 | c5bfac12-1600-42ab-9077-daf0c7eea70f | ARCHIVING            | ARCHIVING             | db0531d1-a0d5-4fff-a8ea-66d3925db639 |
#      firstDescriptorId=descrittore in Archiving . secondDescriptorId=descrittore in Archiving_Suspended
      | 122d73ef-bd9a-4669-91aa-405bb958eb62 | 00aa97e1-6571-4ed2-9d84-dbe099ecd015 | ac8e1db2-ff25-437c-b2fc-c99ddb56b999 | ARCHIVING            | ARCHIVING_SUSPENDED   | 7d4b8cf9-8aa9-4e16-ab56-e3a44ff48d48 |
#      firstDescriptorId=descrittore in Archiving_Suspended . secondDescriptorId=descrittore in Archiving
      | a629374c-a20d-4e19-b4f8-0e56a39a6e18 | fb6db58d-a4cc-4b84-b157-e2cdd1bf4a1b | 929c62d0-57a0-41e7-94b3-56c9d34277d5 | ARCHIVING_SUSPENDED  | ARCHIVING             | 857ecb8f-1a0a-46d8-95ac-1fc0873fdc84 |

  @happy-path
  Scenario Outline: [ARCHIVING_ESERVICE_BY_JOB_1.3] Dopo il periodo di preavviso, un e-service con primo descrittore archiviato automaticamente e secondo descrittore in archiviazione manuale dell'intero e-service risulta archiviato
    Then l'utente è un "admin" di "PA1"
    #versione più recente
    And il descrittore con id "<secondDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "ARCHIVED"
    And il descrittore con id "<secondDescriptorId>" dell'e-service avente id "<eserviceId>" è stato correttamente archiviato tramite l'archiviazione manuale dell'intero e-service
    #versione meno recente
    And il descrittore con id "<firstDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "ARCHIVED"
    And il descrittore con id "<firstDescriptorId>" dell'e-service avente id "<eserviceId>" NON è stato archiviato tramite archiviazione manuale

    Examples:
      | firstDescriptorId                    | secondDescriptorId                   | eserviceId                           |
#      firstDescriptorId=descrittore in Archived . secondDescriptorId=descrittore in Archiving
      | c59ecfe2-a4c5-488d-a5b9-a7d3602671a4 | 6d40805a-3c65-4363-8f4e-ba3b18b7c7b5 | febf05bc-2a8d-4012-ba69-73e90292f185 |

  @happy-path
  Scenario Outline: [ARCHIVING_ESERVICE_BY_JOB_1.4] Prima della fine del periodo di preavviso, un e-service con primo descrittore archiviato automaticamente e secondo descrittore in archiviazione manuale dell'intero e-service non risulta archiviato
    Then l'utente è un "admin" di "PA1"
    #versione più recente
    And il descrittore con id "<secondDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "<secondDescriptorState>"
    And il descrittore con id "<secondDescriptorId>" dell'e-service avente id "<eserviceId>" è in fase di archiviazione tramite l'archiviazione manuale dell'intero e-service
    #versione meno recente
    And il descrittore con id "<firstDescriptorId>" dell'e-service con id "<eserviceId>" è in stato "<firstDescriptorState>"
    And il descrittore con id "<firstDescriptorId>" dell'e-service avente id "<eserviceId>" NON è stato archiviato tramite archiviazione manuale

    Examples:
      | firstDescriptorId                    | secondDescriptorId                   | eserviceId                           | firstDescriptorState | secondDescriptorState |
#      firstDescriptorId=descrittore in Archived . secondDescriptorId=descrittore in Archiving
      | 50bc283a-4a63-445b-a042-2b545501c988 | 9900424b-c0da-49d8-92ef-6661a65d8898 | aaaff1d2-d964-41f8-b8ef-dcbd35ea6f4e | ARCHIVED             | ARCHIVING             |