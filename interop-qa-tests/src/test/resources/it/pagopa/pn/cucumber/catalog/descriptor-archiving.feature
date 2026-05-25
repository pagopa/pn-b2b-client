@manual-archiving-eservice
Feature: Archiviazione manuale di un descrittore

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.1] Un ente erogatore di un e-service può avviare il processo di archiviazione manuale del primo e meno recente descrittore dell'e-service in questione
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
#    When l'utente archivia il primo(qui possiamo inserire una variabile) e meno recente descrittore con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code 204
#    And il primo(qui possiamo inserire una variabile) descrittore è in stato "<finalDescriptorState>" (ARCHIVING)

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | role         | initialDescriptorState | finalDescriptorState |
      | admin        | PUBLISHED              | ARCHIVING            |
      | api          | PUBLISHED              | ARCHIVING            |
      | api,security | PUBLISHED              | ARCHIVING            |
      | admin        | SUSPENDED              | ARCHIVING_SUSPENDED  |
      | api          | SUSPENDED              | ARCHIVING_SUSPENDED  |
      | api,security | SUSPENDED              | ARCHIVING_SUSPENDED  |

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_1.2] Un utente con ruolo security NON può avviare il processo di archiviazione manuale del descrittore
    Given l'utente è un "security" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
#    When l'utente archivia il primo(qui possiamo inserire una variabile) e meno recente descrittore con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 401
#    And il primo(qui possiamo inserire una variabile) descrittore è in stato "DEPRECATED"

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_1.3] Un utente con token non valido NON può avviare il processo di archiviazione manuale del descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And viene impostato per l'utente un token non valido
#    When l'utente archivia il primo(qui possiamo inserire una variabile) e meno recente descrittore con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 401
#    And il primo(qui possiamo inserire una variabile) descrittore è in stato "DEPRECATED"

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.4] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un descrittore se gli attributi obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
#    When l'utente archivia il primo(qui possiamo inserire una variabile) e meno recente descrittore con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code <statusCode>
#    And il primo(qui possiamo inserire una variabile) descrittore è in stato "DEPRECATED"

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
#    When l'utente archivia il primo(qui possiamo inserire una variabile) e meno recente descrittore con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code 409
#    And il primo(qui possiamo inserire una variabile) descrittore è in stato "<finalDescriptorState>" (ARCHIVING)

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1.6] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un suo descrittore se quest'ultimo è già in stato di archiviazione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    #Nel seguente Step And dobbiamo aggiungere il Polling
#    And l'utente archivia il primo(qui possiamo inserire una variabile) e meno recente descrittore con id "<descriptorId>" dell'e-service con id "<eserviceId>"
  #  When l'utente archivia il primo(qui possiamo inserire una variabile) e meno recente descrittore con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code 409
#    And il primo(qui possiamo inserire una variabile) descrittore è in stato "<finalDescriptorState>" (ARCHIVING)

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | initialDescriptorState | finalDescriptorState |
      | PUBLISHED              | ARCHIVING            |
      | SUSPENDED              | ARCHIVING_SUSPENDED  |

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_2.1] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un suo descrittore se quest'ultimo è il più recente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
#    When l'utente archivia il secondo(qui possiamo inserire una variabile) e più recente descrittore con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 400
#    And il secondo(qui possiamo inserire una variabile) descrittore è in stato "<finalDescriptorState>" (ARCHIVING)