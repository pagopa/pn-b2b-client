@m2m-v3-manual-archiving-eservice
Feature: Archiviazione manuale di un descrittore

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_1.1] Un ente erogatore di un e-service può avviare via M2M v3 il processo di archiviazione manuale del primo e meno recente descrittore in stato DEPRECATED
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente avvia l'archiviazione della vecchia versione "%actual" dell'e-service "%actual" prevedendo <gracePeriod> giorni di preavviso
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

    Examples:
      | gracePeriod |
      | 30          |
      | 60          |
      | 90          |
      | 120         |

  Scenario: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_1.2] Un utente con ruolo M2M NON può avviare il processo di archiviazione manuale del descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente avvia l'archiviazione della vecchia versione "%actual" dell'e-service "%actual" prevedendo 60 giorni di preavviso
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  Scenario: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_1.3] Un utente con token non valido NON può avviare il processo di archiviazione manuale del descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    And l'utente avvia l'archiviazione della vecchia versione "%actual" dell'e-service "%actual" prevedendo 60 giorni di preavviso
    Then si ottiene response status code 401
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_1.4] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un descrittore se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente avvia l'archiviazione della vecchia versione "<descriptorId>" dell'e-service "<eserviceId>" prevedendo <gracePeriod> giorni di preavviso
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

    Examples:
      | descriptorId | eserviceId | gracePeriod | statusCode |
      | %null        | %actual    | 60          | 400        |
      | %actual      | %null      | 60          | 400        |
      | %null        | %null      | 60          | 400        |
      | %random      | %actual    | 60          | 404        |
      | %actual      | %random    | 60          | 404        |
      | %random      | %random    | 60          | 404        |

  @happy-path
  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_1.5] Un ente erogatore di un e-service può avviare via M2M v3 il processo di archiviazione manuale del primo e meno recente descrittore in stato SUSPENDED
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente avvia l'archiviazione della vecchia versione "%actual" dell'e-service "%actual" prevedendo <gracePeriod> giorni di preavviso
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "ARCHIVING_SUSPENDED"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

    Examples:
      | gracePeriod |
      | 30          |
      | 60          |
      | 90          |
      | 120         |

  Scenario: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_2.1] Un ente erogatore di un e-service NON può avviare il processo di archiviazione manuale di un suo descrittore se quest'ultimo è il più recente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente avvia l'archiviazione della versione più recente dell'e-service prevedendo 60 giorni di preavviso
    Then si ottiene response status code 400
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore

  Scenario: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_CANCELLATION_1.1] L'ente erogatore di un e-service può annullare il processo di archiviazione manuale del primo e meno recente descrittore in stato ARCHIVING se l'archiviazione è in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di annullare il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'archiviazione manuale del singolo descrittore è stata annullata con successo

  Scenario: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_CANCELLATION_1.2] Un utente con ruolo M2M NON può annullare il processo di archiviazione manuale del descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di annullare il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And l'annullamento dell'archiviazione manuale del vecchio descrittore è fallita

  Scenario: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_CANCELLATION_1.3] Un utente con token non valido NON può annullare il processo di archiviazione manuale del descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    And l'utente tenta di annullare il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 401
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And l'annullamento dell'archiviazione manuale del vecchio descrittore è fallita

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_CANCELLATION_1.4] Un ente erogatore di un e-service NON può annullare il processo di archiviazione manuale di un descrittore se i parametri obbligatori non sono presenti o corretti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di annullare il processo di archiviazione della vecchia versione con id "<descriptorId>" dell'e-service con id "<eserviceId>"
    Then si ottiene response status code <statusCode>
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And l'annullamento dell'archiviazione manuale del vecchio descrittore è fallita

    Examples:
      | descriptorId | eserviceId | statusCode |
      | %null        | %actual    | 400        |
      | %actual      | %null      | 400        |
      | %null        | %null      | 400        |
      | %random      | %actual    | 404        |
      | %actual      | %random    | 404        |
      | %random      | %random    | 404        |

  Scenario: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_CANCELLATION_1.5] L'ente erogatore di un e-service può annullare il processo di archiviazione manuale del primo e meno recente descrittore in stato ARCHIVING_SUSPENDED se l'archiviazione è in corso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di annullare il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "SUSPENDED"
    And l'archiviazione manuale del singolo descrittore è stata annullata con successo