@bff-notification
@notification-manual-archiving-eservice
Feature: Notifiche relative all'archiviazione manuale di un e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui avvia il processo di archiviazione dell'intero e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.2] L'utente fruitore riceve una notifica nel momento in cui viene avviato il processo di archiviazione dell'intero e-service per cui ha una richiesta di fruizione attiva
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.3] L'utente erogatore NON riceve una notifica nel momento in cui avvia il processo di archiviazione dell'intero e-service se le notifiche per il cambio di stato dell'e-service sono disabilitate
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.4] L'utente erogatore riceve una notifica nel momento in cui il suo e-service viene archiviato a causa della scadenza del periodo di preavviso

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.5] L'utente fruitore riceve una notifica nel momento in cui viene archiviato l'e-service per cui ha una richiesta di fruizione attiva

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui un descrittore viene archiviato a causa della pubblicazione di un nuovo descrittore e della mancanza di richieste di fruizioni attive nei suoi confronti
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When "PA1" ha già pubblicato una nuova versione per quell'e-service

  Scenario: [AUTOMATIC_ARCHIVING_ESERVICE_NOTIFICATION_1.2] L'utente erogatore riceve una notifica nel momento in cui un descrittore viene archiviato poichè non è il descrittore più recente dell'e-service e poichè l'ultima richiesta di fruizione attiva nei suoi confronti viene archiviata
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When "PA2" ha già archiviato quella richiesta di fruizione


