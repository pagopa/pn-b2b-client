@bff-notification
@notification-manual-archiving-eservice
Feature: Notifiche relative all'archiviazione manuale di un e-service

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.1] Erogatore e fruitore ricevono una notifica quando si avvia il processo di archiviazione dell'intero e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    Il tuo e-service $DA_CONTESTO(eServiceName) è in fase di archiviazione, ma risulta ancora attivo. L'e-service sarà
    archiviato il giorno $DA_CONTESTO(TODAY+30).
    """
    And l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'e-service $DA_CONTESTO(eServiceName) sarà archiviato il giorno $DA_CONTESTO(TODAY+30). Dopo questa data
    non potrai più scambiare dati con l’e-service.
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_NOTIFICATION_1.2] Erogatore e fruitore ricevono una notifica quando si avvia il processo di archiviazione dell'intero e-service istanza di un template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    Il tuo e-service $DA_CONTESTO(eServiceName) è in fase di archiviazione, ma risulta ancora attivo. L'e-service sarà
    archiviato il giorno $DA_CONTESTO(TODAY+60).
    """
    And l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'e-service $DA_CONTESTO(eServiceName) sarà archiviato il giorno $DA_CONTESTO(TODAY+60). Dopo questa data
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
    avverrà il giorno $DA_CONTESTO(TODAY+60).
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

  Scenario: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_NOTIFICATION_1.1] Erogatore e fruitore ricevono una notifica quando l'e-service in stato di archiviazione viene sospeso e quando viene riattivato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 90 giorni di preavviso
    When l'utente sospende quel descrittore in corso di archiviazione
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è al momento sospesa. L'e-service sarà archiviato il giorno
    $DA_CONTESTO(TODAY+90).
    """
    And l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è al momento sospesa. L'archiviazione avverrà il giorno
    $DA_CONTESTO(TODAY+90).
    """

    When l'utente attiva il descrittore di quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è di nuovo attiva. L'e-service sarà archiviato il giorno
    $DA_CONTESTO(TODAY+90).
    """
    And l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è di nuovo attiva. L'e-service sarà archiviato il giorno
    $DA_CONTESTO(TODAY+90).
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_SUSPENSION_NOTIFICATION_1.2] Erogatore e fruitore ricevono una notifica quando l'e-service istanza di un template in stato di archiviazione viene sospeso
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 120 giorni di preavviso
    When l'utente sospende quel descrittore in corso di archiviazione
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è al momento sospesa. L'e-service sarà archiviato il giorno
    $DA_CONTESTO(TODAY+120).
    """
    And l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è al momento sospesa. L'archiviazione avverrà il giorno
    $DA_CONTESTO(TODAY+120).
    """

    When l'utente attiva il descrittore di quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è di nuovo attiva. L'e-service sarà archiviato il giorno
    $DA_CONTESTO(TODAY+120).
    """
    And l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è di nuovo attiva. L'e-service sarà archiviato il giorno
    $DA_CONTESTO(TODAY+120).
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_NOTIFICATION_1.1] L'utente erogatore riceve una notifica quando annulla l'archiviazione in corso di un proprio e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'e-service $DA_CONTESTO(eServiceName) non è più in fase di archiviazione.
    """

  Scenario: [MANUAL_ARCHIVING_ESERVICE_CANCELLATION_NOTIFICATION_1.2] L'utente fruitore riceve una notifica quando il processo di archiviazione in corso di un e-service, verso cui ha una richiesta di fruizione attiva, viene annullato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'e-service $DA_CONTESTO(eServiceName) non è più in fase di archiviazione.
    """

  Scenario: [NOTIFICA_ARCHIVIAZIONE_VIA_DELEGA_1.1] Un delegato all'erogazione richiede l'archiviazione di un e-service e viene approvata
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente richiede la creazione di una delega in erogazione per l'ente "PA2"
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "Test richiesta di archiviazione" e 30 giorni di preavviso
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'ente delegato $DA_CONTESTO(delegateName) ha richiesto l'archiviazione dell'e-service
    $DA_CONTESTO(eServiceName). Puoi confermare o rifiutare la richiesta.
    """
    When l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "%actual"
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'ente delegante $DA_CONTESTO(producerName) ha approvato la tua richiesta di archiviazione dell'e-service
    $DA_CONTESTO(eServiceName). L'archiviazione avverrà il giorno $DA_CONTESTO(TODAY+30).
    """

  Scenario: [NOTIFICA_ARCHIVIAZIONE_VIA_DELEGA_1.2] Un delegato all'erogazione annulla la richiesta di archiviazione di un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente richiede la creazione di una delega in erogazione per l'ente "PA2"
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "Richiesta di archiviazione" e 30 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione dell'e-service "%actual"
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app
    """
    L'ente delegato $DA_CONTESTO(delegateName) ha annullato la richiesta di archiviazione
    per l'e-service $DA_CONTESTO(eServiceName).
    """
    And l'utente "admin" di "PA2" ha ricevuto la notifica in-app
    """
    È stata annullata la richiesta di archiviazione per l'e-service $DA_CONTESTO(eServiceName)
    inviata all'ente delegante $DA_CONTESTO(producerName).
    """

  Scenario: [NOTIFICA_ARCHIVIAZIONE_VIA_DELEGA_1.3] Un delegante all'erogazione rifiuta la richiesta di archiviazione di un e-service di un delegato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente richiede la creazione di una delega in erogazione per l'ente "PA2"
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "Richiesta di archiviazione" e 30 giorni di preavviso
    When l'utente delegante rifiuta la richiesta di archiviazione delegata dell'e-service "%actual" con motivazione "Test di rifiuto di archiviazione"
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'ente delegante $DA_CONTESTO(producerName) ha rifiutato la tua richiesta di archiviazione
    dell'e-service $DA_CONTESTO(eServiceName).
    """
