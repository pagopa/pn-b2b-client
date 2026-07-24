@bff-notification
@notification-manual-archiving-eservice
Feature: Notifiche relative all'archiviazione manuale di uno specifico descrittore

  Scenario: [AUTOMATIC_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui un descrittore viene archiviato poiché non è il descrittore più recente dell'e-service e poiché l'ultima richiesta di fruizione attiva nei suoi confronti viene archiviata
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) è stata archiviata il giorno
    $EUROPE_TODAY() perché senza fruitori. Da ora non è più attiva.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui avvia il processo di archiviazione di uno specifico descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente archivia la vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) è in fase di archiviazione ma è ancora attiva.
    L'archiviazione avverrà il giorno $EUROPE_DATE_ADD(+2D).
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.2] L'utente fruitore riceve una notifica nel momento in cui viene avviato il processo di archiviazione di un descrittore per cui ha una richiesta di fruizione attiva
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente archivia la vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) è in fase di archiviazione ma è ancora attiva.
    L'archiviazione avverrà il giorno $EUROPE_DATE_ADD(+2D). È disponibile una nuova versione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.3] L'utente erogatore NON riceve una notifica nel momento in cui avvia il processo di archiviazione di uno specifico descrittore se le notifiche per il cambio di stato dell'e-service sono disabilitate
    Given l'utente è un "admin" di "PA1"
    And admin di "PA1" attiva le notifiche in-app eccetto:
      | eserviceStateChangedToProducer |
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente archivia la vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then admin di "PA1" non ha ricevuto la notifica in-app
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) è in fase di archiviazione ma è ancora attiva.
    L'archiviazione avverrà il giorno $EUROPE_DATE_ADD(+2D).
    """

  @ignore
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.4] L'utente erogatore riceve una notifica nel momento in cui un suo descrittore viene archiviato a causa della scadenza del periodo di preavviso
    Then admin di "PA3" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) è stata archiviata il giorno $EUROPE_TODAY().
    Da ora non è più attiva e i fruitori non potranno più scambiare dati.
    """

  @ignore
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.5] L'utente fruitore riceve una notifica nel momento in cui viene archiviato un descrittore per cui ha una richiesta di fruizione attiva
    Then admin di "PA3" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) è stata archiviata il giorno $EUROPE_TODAY().
    Per continuare a scambiare dati con l’e-service, passa alla nuova versione.
    """

  @ignore
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.6] L'utente erogatore riceve una notifica di promemoria che la versione dell'e-service verrà archiviata fra N giorni
    Then admin di "PA3" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) sarà archiviata il giorno $EUROPE_DATE_ADD(+2D).
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_CANCELLATION_NOTIFICATION_1.1] L'utente erogatore riceve una notifica quando annulla l'archiviazione in corso di un proprio descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) non è più in fase di archiviazione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_CANCELLATION_NOTIFICATION_1.2] L'utente fruitore riceve una notifica quando il processo di archiviazione in corso di un descrittore, verso cui ha una richiesta di fruizione attiva, viene annullato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) non è più in fase di archiviazione.
    Se vuoi, è disponibile una nuova versione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui il suo descrittore in stato di archiviazione viene sospeso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente sospende il vecchio descrittore in corso di archiviazione
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) è al momento sospesa.
    Sarà archiviata il giorno $EUROPE_DATE_ADD(+2D).
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.2] L'utente fruitore riceve una notifica nel momento in cui un descrittore per cui ha una richiesta di fruizione attiva viene sospeso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente sospende il vecchio descrittore in corso di archiviazione
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) è al momento sospesa. L'archiviazione avverrà il giorno
    $EUROPE_DATE_ADD(+2D). È disponibile una nuova versione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.3] L'utente erogatore riceve una notifica nel momento in cui un suo descrittore, in stato di archiviazione e sospeso, viene riattivato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente attiva il vecchio descrittore in corso di archiviazione di quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) è di nuovo attiva.
    Sarà archiviata il giorno $EUROPE_DATE_ADD(+2D).
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.4] L'utente fruitore riceve una notifica quando viene riattivato un descrittore sospeso per cui ha una richiesta di fruizione attiva
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione con id "%actual" dell'e-service con id "%actual"
    When l'utente attiva il vecchio descrittore in corso di archiviazione di quell'e-service
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $CONTEXT(eServiceName) è di nuovo attiva. L'archiviazione avverrà il giorno
    $EUROPE_DATE_ADD(+2D). È disponibile una nuova versione.
    """
