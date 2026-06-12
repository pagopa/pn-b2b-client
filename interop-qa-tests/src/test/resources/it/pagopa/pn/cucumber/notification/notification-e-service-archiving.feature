@bff-notification
@notification-manual-archiving-eservice
Feature: Notifiche relative all'archiviazione manuale di un e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui avvia il processo di archiviazione dell'intero e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    Il tuo e-service $DA_CONTESTO(eServiceName) è in fase di archiviazione, ma risulta ancora attivo. L'archiviazione
    avverrà il giorno $DA_CONTESTO(TODAY+GRACE_PERIOD).
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.2] L'utente fruitore riceve una notifica nel momento in cui viene avviato il processo di archiviazione dell'intero e-service per cui ha una richiesta di fruizione attiva
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha avviato il processo di archiviazione dell'e-service
    $DA_CONTESTO(eServiceName) a cui sei iscritto. Hai tempo fino al $DA_CONTESTO(TODAY+GRACE_PERIOD) per concludere
    ordinatamente le chiamate.
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.3] L'utente erogatore NON riceve una notifica nel momento in cui avvia il processo di archiviazione dell'intero e-service se le notifiche per il cambio di stato dell'e-service sono disabilitate
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.4] L'utente erogatore riceve una notifica nel momento in cui il suo e-service viene archiviato a causa della scadenza del periodo di preavviso

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.5] L'utente fruitore riceve una notifica nel momento in cui viene archiviato l'e-service per cui ha una richiesta di fruizione attiva

  Scenario: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui il suo e-service in stato di archiviazione viene sospeso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente sospende quel descrittore in corso di archiviazione

  Scenario: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_NOTIFICATION_1.2] L'utente fruitore riceve una notifica nel momento in cui un e-service per cui ha una richiesta di fruizione attiva viene sospeso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente avvia il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    And l'utente sospende quel descrittore in corso di archiviazione

