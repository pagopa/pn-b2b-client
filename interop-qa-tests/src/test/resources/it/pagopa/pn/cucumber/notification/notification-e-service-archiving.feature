@bff-notification
@notification-manual-archiving-eservice
Feature: Notifiche relative all'archiviazione manuale di un e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui avvia il processo di archiviazione dell'intero e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"


  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.2] L'utente fruitore riceve una notifica nel momento in cui viene avviato il processo di archiviazione dell'intero e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"

