@manual-archiving-eservice
Feature: Archiviazione manuale di un descrittore

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.1] Un ente erogatore di un e-service può avviare il processo di archiviazione manuale del primo e meno recente descrittore dell'e-service in questione
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente archivia la vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalDescriptorState>"

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | role         | initialDescriptorState | finalDescriptorState |
      | admin        | PUBLISHED              | ARCHIVING            |
      | api          | PUBLISHED              | ARCHIVING            |
      | api,security | PUBLISHED              | ARCHIVING            |
      | admin        | SUSPENDED              | ARCHIVING_SUSPENDED  |
      | api          | SUSPENDED              | ARCHIVING_SUSPENDED  |
      | api,security | SUSPENDED              | ARCHIVING_SUSPENDED  |

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.2] Un utente con ruolo non autorizzato NON può avviare il processo di archiviazione manuale del descrittore
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente archivia la vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "DEPRECATED"

    Examples:
      | role     |
      | security |
      | support  |

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_1.3] Un utente con token non valido NON può avviare il processo di archiviazione manuale del descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And viene impostato per l'utente un token non valido
    When l'utente archivia la vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 401
    And la vecchia versione dell'e-service è in stato "DEPRECATED"

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.4] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un descrittore se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente archivia la vecchia versione con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "DEPRECATED"

    Examples:
      | descriptorId | eserviceId | statusCode |
      | %null        | %actual    | 400        |
      | %actual      | %null      | 400        |
      | %null        | %null      | 400        |
      | %random      | %actual    | 404        |
      | %actual      | %random    | 404        |
      | %random      | %random    | 404        |

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_1.5] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un suo descrittore se quest'ultimo è già stato già archiviato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente archivia la vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 409
    And la vecchia versione dell'e-service è in stato "ARCHIVING"

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.6] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un suo descrittore se quest'ultimo è già in stato di archiviazione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente archivia la vecchia versione con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code 409
    And la vecchia versione dell'e-service è in stato "<finalDescriptorState>"

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | initialDescriptorState | finalDescriptorState |
      | PUBLISHED              | ARCHIVING            |
      | SUSPENDED              | ARCHIVING_SUSPENDED  |

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_2.1] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un suo descrittore se quest'ultimo è il più recente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente archivia la versione più recente dell'e-service
    Then si ottiene response status code 400
    And la versione più recente dell'e-service è in stato "DEPRECATED"

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_ELIMINATION_1.1] L'ente erogatore di un e-service può annullare il processo di archiviazione manuale del primo e meno recente descrittore se l'archiviazione è in corso
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalDescriptorState>"

    Examples:
      | role         | initialDescriptorState | finalDescriptorState |
      | admin        | PUBLISHED              | DEPRECATED           |
      | api          | PUBLISHED              | DEPRECATED           |
      | api,security | PUBLISHED              | DEPRECATED           |
      | admin        | SUSPENDED              | SUSPENDED            |
      | api          | SUSPENDED              | SUSPENDED            |
      | api,security | SUSPENDED              | SUSPENDED            |

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_ELIMINATION_1.2] Un utente con ruolo non autorizzato NON può annullare il processo di archiviazione manuale del descrittore
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "DEPRECATED"

    Examples:
      | role     |
      | security |
      | support  |

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_ELIMINATION_1.3] Un utente con token non valido NON può annullare il processo di archiviazione manuale del descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    And viene impostato per l'utente un token non valido
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 401
    And la vecchia versione dell'e-service è in stato "DEPRECATED"

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_ELIMINATION_1.4] Un ente erogatore di un e-service NON può annullare il processo di archiviazione manuale di un descrittore se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente annulla il processo di archiviazione della vecchia versione con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "DEPRECATED"

    Examples:
      | descriptorId | eserviceId | statusCode |
      | %null        | %actual    | 400        |
      | %actual      | %null      | 400        |
      | %null        | %null      | 400        |
      | %random      | %actual    | 404        |
      | %actual      | %random    | 404        |
      | %random      | %random    | 404        |

  Scenario: [DIFFERENT_TENANT_ARCHIVING_DESCRIPTOR_1.1] Il processo di archiviazione dello specifico descrittore NON può essere eseguito da un ente differente dall'erogatore dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA3"
    And l'utente archivia la vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  Scenario: [DIFFERENT_TENANT_ARCHIVING_DESCRIPTOR_1.2] Il processo di archiviazione dello specifico descrittore NON può essere annullato da un ente differente dall'erogatore dell'e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente è un "admin" di "PA3"
    And l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  Scenario: [DIFFERENT_TENANT_ARCHIVING_DESCRIPTOR_2.1] Il processo di archiviazione  dello specifico descrittore NON può essere annullato da un ente differente dall'erogatore dell'e-service anche se questo è delegato all'erogazione
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA3" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA3"
    And l'utente è un "admin" di "PA3"
    And l'utente accetta la delega
    When l'utente archivia la vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"
