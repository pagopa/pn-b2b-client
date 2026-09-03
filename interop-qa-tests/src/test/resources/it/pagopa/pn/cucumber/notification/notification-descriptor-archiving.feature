@bff-notification
@notification-manual-archiving-eservice
Feature: Notifiche relative all'archiviazione manuale di uno specifico descrittore

  Scenario: [AUTOMATIC_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui un descrittore viene archiviato poiché non è il descrittore più recente dell'e-service e poiché l'ultima richiesta di fruizione attiva nei suoi confronti viene archiviata
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When "PA2" ha già archiviato quella richiesta di fruizione
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è stata archiviata il giorno
    $DA_CONTESTO(TODAY) perché senza fruitori. Da ora non è più attiva.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.1] Erogatore e fruitore ricevono una notifica quando si avvia il processo di archiviazione di uno specifico descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 30 giorni di preavviso
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è in fase di archiviazione ma è ancora attiva.
    L'archiviazione avverrà il giorno $DA_CONTESTO(TODAY+30).
    """
    And l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è in fase di archiviazione ma è ancora attiva.
    L'archiviazione avverrà il giorno $DA_CONTESTO(TODAY+30). È disponibile una nuova versione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.2] Erogatore e fruitore ricevono una notifica quando si avvia il processo di archiviazione di uno specifico descrittore di e-service istanza di template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 30 giorni di preavviso
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è in fase di archiviazione ma è ancora attiva.
    L'archiviazione avverrà il giorno $DA_CONTESTO(TODAY+30).
    """
    And l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è in fase di archiviazione ma è ancora attiva.
    L'archiviazione avverrà il giorno $DA_CONTESTO(TODAY+30). È disponibile una nuova versione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.3] L'utente erogatore NON riceve una notifica nel momento in cui avvia il processo di archiviazione di uno specifico descrittore se le notifiche per il cambio di stato dell'e-service sono disabilitate
    Given l'utente è un "admin" di "PA1"
    And l'utente attiva le notifiche tranne il cambio di stato dell'e-service per l'erogatore
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then l'utente "admin" di "PA1" non ha ricevuto la notifica in-app
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è in fase di archiviazione ma è ancora attiva.
    L'archiviazione avverrà il giorno $DA_CONTESTO(TODAY+60).
    """

  @ignore
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.4] L'utente erogatore riceve una notifica nel momento in cui un suo descrittore viene archiviato a causa della scadenza del periodo di preavviso
    Then l'utente "admin" di "PA3" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è stata archiviata il giorno $DA_CONTESTO(TODAY).
    Da ora non è più attiva e i fruitori non potranno più scambiare dati.
    """

  @ignore
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.5] L'utente fruitore riceve una notifica nel momento in cui viene archiviato un descrittore per cui ha una richiesta di fruizione attiva
    Then l'utente "admin" di "PA3" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è stata archiviata il giorno $DA_CONTESTO(TODAY).
    Per continuare a scambiare dati con l’e-service, passa alla nuova versione.
    """

  @ignore
  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_NOTIFICATION_1.6] L'utente erogatore riceve una notifica di promemoria che la versione dell'e-service verrà archiviata fra N giorni
    Then l'utente "admin" di "PA3" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) sarà archiviata il giorno $DA_CONTESTO(TODAY).
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_CANCELLATION_NOTIFICATION_1.1] L'utente erogatore riceve una notifica quando annulla l'archiviazione in corso di un proprio descrittore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) non è più in fase di archiviazione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_CANCELLATION_NOTIFICATION_1.2] L'utente fruitore riceve una notifica quando il processo di archiviazione in corso di un descrittore, verso cui ha una richiesta di fruizione attiva, viene annullato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) non è più in fase di archiviazione. Se vuoi, è disponibile
    una nuova versione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.1] L'utente erogatore riceve una notifica nel momento in cui il suo descrittore in stato di archiviazione viene sospeso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 90 giorni di preavviso
    When l'utente sospende il vecchio descrittore in corso di archiviazione
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è al momento sospesa. Sarà archiviata il giorno
    $DA_CONTESTO(TODAY+90).
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.2] L'utente fruitore riceve una notifica nel momento in cui un descrittore per cui ha una richiesta di fruizione attiva viene sospeso
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 90 giorni di preavviso
    When l'utente sospende il vecchio descrittore in corso di archiviazione
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è al momento sospesa. L'archiviazione avverrà il giorno
    $DA_CONTESTO(TODAY+90). È disponibile una nuova versione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.3] L'utente erogatore riceve una notifica nel momento in cui un suo descrittore, in stato di archiviazione e sospeso, viene riattivato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 120 giorni di preavviso
    When l'utente attiva il vecchio descrittore in corso di archiviazione di quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è di nuovo attiva. Sarà archiviata il giorno
    $DA_CONTESTO(TODAY+120).
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.4] L'utente fruitore riceve una notifica quando viene riattivato un descrittore sospeso per cui ha una richiesta di fruizione attiva
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 120 giorni di preavviso
    When l'utente attiva il vecchio descrittore in corso di archiviazione di quell'e-service
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è di nuovo attiva. L'archiviazione avverrà il giorno
    $DA_CONTESTO(TODAY+120). È disponibile una nuova versione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.5] L'utente erogatore riceve una notifica nel momento in cui il suo descrittore di e-service istanza di template in stato di archiviazione viene sospeso
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 90 giorni di preavviso
    When l'utente sospende il vecchio descrittore in corso di archiviazione
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è al momento sospesa. Sarà archiviata il giorno
    $DA_CONTESTO(TODAY+90).
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.6] L'utente fruitore riceve una notifica nel momento in cui un descrittore di e-service istanza di template per cui ha una richiesta di fruizione attiva viene sospeso
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 90 giorni di preavviso
    When l'utente sospende il vecchio descrittore in corso di archiviazione
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è al momento sospesa. L'archiviazione avverrà il giorno
    $DA_CONTESTO(TODAY+90). È disponibile una nuova versione.
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.7] L'utente erogatore riceve una notifica nel momento in cui un suo descrittore di e-service istanza di template, in stato di archiviazione e sospeso, viene riattivato
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 120 giorni di preavviso
    When l'utente attiva il vecchio descrittore in corso di archiviazione di quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è di nuovo attiva. Sarà archiviata il giorno
    $DA_CONTESTO(TODAY+120).
    """

  Scenario: [MANUAL_ARCHIVING_DESCRIPTOR_SUSPENSION_NOTIFICATION_1.8] L'utente fruitore riceve una notifica quando viene riattivato un descrittore di e-service istanza di template sospeso per cui ha una richiesta di fruizione attiva
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 120 giorni di preavviso
    When l'utente attiva il vecchio descrittore in corso di archiviazione di quell'e-service
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    La versione 1 dell'e-service $DA_CONTESTO(eServiceName) è di nuovo attiva. L'archiviazione avverrà il giorno
    $DA_CONTESTO(TODAY+120). È disponibile una nuova versione.
    """

  Scenario: [NOTIFICA_ARCHIVIAZIONE_VIA_DELEGA_1.4] Un delegato all'erogazione richiede l'archiviazione di una versione di un e-service e viene approvata
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente richiede la creazione di una delega in erogazione per l'ente "PA2"
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 30 giorni di preavviso
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    L'ente delegato $DA_CONTESTO(delegateName) ha richiesto l'archiviazione della versione 1
    dell'e-service $DA_CONTESTO(eServiceName). Puoi confermare o rifiutare la richiesta.
    """
    When l'utente delegante accetta la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    L'ente delegante $DA_CONTESTO(producerName) ha approvato la tua richiesta di archiviazione della versione 1
    dell'e-service $DA_CONTESTO(eServiceName). L'archiviazione avverrà il giorno $DA_CONTESTO(TODAY+30).
    """

  Scenario: [NOTIFICA_ARCHIVIAZIONE_VIA_DELEGA_1.5] Un delegato all'erogazione annulla la richiesta di archiviazione di una versione di un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente richiede la creazione di una delega in erogazione per l'ente "PA2"
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 30 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app
    """
    L'ente delegato $DA_CONTESTO(delegateName) ha annullato la richiesta di archiviazione per la versione 1
    dell'e-service $DA_CONTESTO(eServiceName).
    """
    And l'utente "admin" di "PA2" ha ricevuto la notifica in-app
    """
    È stata annullata la richiesta di archiviazione per la versione 1 dell'e-service $DA_CONTESTO(eServiceName)
    inviata all'ente delegante $DA_CONTESTO(producerName).
    """

  Scenario: [NOTIFICA_ARCHIVIAZIONE_VIA_DELEGA_1.6] Un delegante all'erogazione rifiuta la richiesta di archiviazione di una versione di un e-service di un delegato
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente richiede la creazione di una delega in erogazione per l'ente "PA2"
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 30 giorni di preavviso
    When l'utente delegante rifiuta la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" con motivazione "Test di rifiuto di archiviazione"
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE
    """
    L'ente delegante $DA_CONTESTO(producerName) ha rifiutato la tua richiesta di archiviazione della versione 1
    dell'e-service $DA_CONTESTO(eServiceName).
    """
