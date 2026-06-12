@bff-notification
@notification-manual-archiving-eservice
Feature: Notifiche relative all'archiviazione manuale di uno specifico descrittore

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