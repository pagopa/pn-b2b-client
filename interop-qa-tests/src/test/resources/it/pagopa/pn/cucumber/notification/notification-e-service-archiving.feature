@bff-notification
@notification-manual-archiving-eservice
Feature: Notifiche relative all'archiviazione manuale di un e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui avvia il processo di archiviazione dell'intero e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    Il tuo e-service $DA_CONTESTO(eServiceName) è in fase di archiviazione, ma risulta ancora attivo. L'e-service sarà
    archiviato il giorno $DA_CONTESTO(TODAY+GRACE_PERIOD).
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.2] L'utente fruitore riceve una notifica nel momento in cui viene avviato il processo di archiviazione dell'intero e-service per cui ha una richiesta di fruizione attiva
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'e-service $DA_CONTESTO(eServiceName) sarà archiviato il giorno $DA_CONTESTO(TODAY+GRACE_PERIOD). Dopo questa data
    non potrai più scambiare dati con l’e-service.
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.3] L'utente erogatore NON riceve una notifica nel momento in cui avvia il processo di archiviazione dell'intero e-service se le notifiche per il cambio di stato dell'e-service sono disabilitate
    Given l'utente è un "admin" di "PA1"
    And l'utente attiva le notifiche tranne il cambio di stato dell'e-service per l'erogatore
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then l'utente "admin" di "PA1" non ha ricevuto la notifica in-app
    """
    Il tuo e-service $DA_CONTESTO(eServiceName) è in fase di archiviazione, ma risulta ancora attivo. L'archiviazione
    avverrà il giorno $DA_CONTESTO(TODAY+GRACE_PERIOD).
    """

  @ignore
  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.4] L'utente erogatore riceve una notifica nel momento in cui il suo e-service viene archiviato a causa della scadenza del periodo di preavviso
    Then l'utente "admin" di "PA3" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'e-service $DA_CONTESTO(eServiceName) è stato archiviato e non è più attivo. È stato rimosso dal catalogo e i
    fruitori non potranno più inviare richieste di fruizione o scambiare dati.
    """

  @ignore
  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.5] L'utente fruitore riceve una notifica nel momento in cui viene archiviato l'e-service per cui ha una richiesta di fruizione attiva
    Then l'utente "admin" di "PA3" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'e-service $DA_CONTESTO(eServiceName) è stato archiviato definitivamente il giorno $DA_CONTESTO(archivedStateDay)
    e non può più essere utilizzato.
    """

  @ignore
  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.6] L'utente erogatore riceve una notifica di promemoria che l'e-service verrà archiviato fra N giorni
    Then l'utente "admin" di "PA3" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    Il tuo e-service $DA_CONTESTO(eServiceName) sarà archiviato il giorno $DA_CONTESTO(TODAY).
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui il suo e-service in stato di archiviazione viene sospeso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente sospende quel descrittore in corso di archiviazione
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è al momento sospesa. L'e-service sarà archiviato il giorno
    $DA_CONTESTO(TODAY+GRACE_PERIOD).
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_NOTIFICATION_1.2] L'utente fruitore riceve una notifica nel momento in cui un e-service per cui ha una richiesta di fruizione attiva viene sospeso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    And l'utente sospende quel descrittore in corso di archiviazione
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è al momento sospesa. L'archiviazione avverrà il giorno
    $DA_CONTESTO(TODAY+GRACE_PERIOD).
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_NOTIFICATION_1.3] L'utente erogatore riceve una notifica nel momento in cui il suo e-service, in stato di archiviazione e sospeso, viene riattivato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente attiva il descrittore di quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è di nuovo attiva. L'e-service sarà archiviato il giorno
    $DA_CONTESTO(TODAY+GRACE_PERIOD).
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_NOTIFICATION_1.4] L'utente fruitore riceve una notifica quando viene riattivato un e-service sospeso per cui ha una richiesta di fruizione attiva
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente attiva il descrittore di quell'e-service
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è di nuovo attiva. L'e-service sarà archiviato il giorno
    $DA_CONTESTO(TODAY+GRACE_PERIOD).
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_NOTIFICATION_1.1] L'utente erogatore riceve una notifica quando annulla l'archiviazione in corso di un proprio e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'e-service $DA_CONTESTO(eServiceName) non è più in fase di archiviazione.
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_NOTIFICATION_1.2] L'utente fruitore riceve una notifica quando il processo di archiviazione in corso di un e-service, verso cui ha una richiesta di fruizione attiva, viene annullato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service con id "%actual" e specificando la motivazione "QA test manual-archiving"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'e-service $DA_CONTESTO(eServiceName) non è più in fase di archiviazione.
    """
