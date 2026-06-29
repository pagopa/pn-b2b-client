@bff-notification
#@disable-notifications-hooks # FIXME usato per velocizzare l'esecuzione dei test in locale, rimuovere

Feature: API Notifiche - verifica bodies (generato da excel)
  #Background:
  # eliminazione di ogni notifica presente per ogni ente ed utente (applicato con Cucumber Hook @Before)
  # abilitazione delle notifiche in-app per ogni ente ed utente (applicato con Cucumber Hook @Before)
  # disabilitazione delle notifiche in-app per ogni ente ed utente (applicato con Cucumber Hook @After)

  Scenario: [Notifica nuova richiesta di fruizione] - Ricezione nuova richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    When "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    Hai ricevuto una nuova richiesta di fruizione per l'e-service $DA_CONTESTO(eServiceName) formulata
    da parte di $DA_CONTESTO(consumerName).
    """

  Scenario: [Notifica richiesta di fruizione accettata] - Accettazione automatica di una richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    È stata accettata una richiesta di fruizione per l'e-service $DA_CONTESTO(eServiceName) formulata
    da parte di $DA_CONTESTO(consumerName).
    """

  Scenario: [Notifica richiesta di fruizione aggiornata] - Il fruitore aggiorna la richiesta di fruizione per la nuova versione dell'e-service pubblicata dall'erogatore
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    And "PA2" ha già pubblicato una nuova versione per quell'e-service
    When "PA1" ha già aggiornato la richiesta di fruizione all'ultima versione dell'eservice
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(producerName) ha aggiornato la propria richiesta di fruizione per l'e-service
    $DA_CONTESTO(eServiceName) alla versione più recente.
    """

  Scenario: [Notifica richiesta di fruizione sospesa e riattivata] - Il fruitore sospende e riattiva la richiesta di fruizione per l'e-service dell'erogatore

    # Scenario: [Notifica richiesta di fruizione sospesa] - Il fruitore sospende la richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA2" ha una richiesta di fruizione in stato "SUSPENDED" per quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(consumerName) ha sospeso la propria richiesta di fruizione per il suo e-service
    $DA_CONTESTO(eServiceName).
    """

    # Scenario: [Notifica richiesta di fruizione riattivata] - Il fruitore riattiva la richiesta di fruizione per l'e-service dell'erogatore
    When "PA2" ha già attivato nuovamente quella richiesta di fruizione come CONSUMER
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(consumerName) ha riattivato la propria richiesta di fruizione per il tuo e-service
    $DA_CONTESTO(eServiceName), precedentemente sospesa.
    """

  Scenario: [Notifica richiesta di fruizione sospesa dalla Piattaforma] - La Piattaforma PDND sospende la richiesta di fruizione del fruitore causa perdita dei requisiti
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    And "PA2" ha già approvato quella richiesta di fruizione
    When l'utente è un "admin" di "PA2"
    And l'utente revoca l'attributo precedentemente verificato
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    La Piattaforma PDND ha sospeso la richiesta di fruizione del fruitore $DA_CONTESTO(consumerName) per il tuo
    e-service $DA_CONTESTO(eServiceName), in quanto l'ente fruitore non dispone più dei requisiti per poter fruire
    di questi dati.
    """

  Scenario: [Notifica richiesta di fruizione riattivata dalla Piattaforma] - La Piattaforma PDND riattiva la richiesta di fruizione del fruitore per riottenimento dei requisiti
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    And "PA2" ha già approvato quella richiesta di fruizione
    When l'utente è un "admin" di "PA2"
    And l'utente revoca l'attributo precedentemente verificato
    # TODO Capire come riassegnare l'attributo revocato
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    La Piattaforma PDND ha riattivato la richiesta di fruizione del fruitore $DA_CONTESTO(consumerName) per
    il tuo e-service $DA_CONTESTO(eServiceName), precedentemente sospesa.
    """

  Scenario: [Notifica richiesta di fruizione archiviata] - Il fruitore archivia la richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA2" ha una richiesta di fruizione in stato "ARCHIVED" per quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    Ti informiamo che il fruitore $DA_CONTESTO(consumerName) ha archiviato la sua richiesta di fruizione per
    il tuo e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Notifica client associato a una finalità] - Il fruitore associa un proprio client ad una finalità dell'e-service dell'erogatore
    Given "GSP" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato una nuova finalità attiva per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When "PA1" ha già associato quella nuova finalità a quel client
    Then l'utente "admin" di "GSP" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(consumerName) ha associato un proprio client alla finalità $DA_CONTESTO(id_finalita)
    per il tuo e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Notifica client disassociato da una finalità] - Il fruitore disassocia un proprio client ad una finalità dell'e-service dell'erogatore
    Given "GSP" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato una nuova finalità attiva per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When "PA1" ha già associato quella nuova finalità a quel client
    When l'utente "admin" di "PA1" richiede la disassociazione della finalità dal client con successo
    # FALLISCE
    Then l'utente "admin" di "GSP" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(consumerName) ha disassociato un proprio client dalla finalità $DA_CONTESTO(id_finalita)
    per il tuo e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Notifica richiesta adeguamento piano di carico] - Il fruitore chiede un adeguamento del piano di carico della finalità associata all'e-service
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When "PA1" ha già richiesto l'aggiornamento della stima di carico superando i limiti di quell'e-service
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(consumerName) ha richiesto un adeguamento del piano di carico per la finalità
    $DA_CONTESTO(id_finalita), associata al tuo e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Notifica piano di carico sopra soglia] - Il fruitore chiede un adeguamento del piano di carico della finalità superiore alla soglia dell'e-service
    Given "GSP" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato una nuova finalità attiva per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    And l'utente è un "admin" di "PA1"
    When l'utente aggiorna la stima di carico per quella finalità superando la soglia
    And "PA1" ha già associato quella nuova finalità a quel client
    # FALLISCE
    Then l'utente "admin" di "GSP" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(consumerName) ha inviato la finalità $DA_CONTESTO(id_finalita), che prevede un piano
    di carico superiore alla tua soglia, associata al tuo e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Notifica finalità sospesa dal fruitore] - Il fruitore sospende la finalità associata all'e-service dell'erogatore
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    Ti informiamo che l'ente $DA_CONTESTO(consumerName) ha sospeso la finalità $DA_CONTESTO(id_finalita),
    associata al tuo e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Notifica finalità riattivata dal fruitore] - Il fruitore riattiva la finalità associata all'e-service dell'erogatore
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    When l'utente "admin" di "PA2" riattiva la finalità in stato "SUSPENDED" per quell'e-service
    #FALLISCE
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    Ti informiamo che l'ente $DA_CONTESTO(consumerName) ha riattivato la finalità $DA_CONTESTO(id_finalita),
    associata al tuo e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Notifica finalità archiviata dal fruitore] - Il fruitore archivia la finalità associata all'e-service dell'erogatore
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When "PA2" ha già archiviato quella finalità
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    Ti informiamo che l'ente $DA_CONTESTO(consumerName) ha archiviato la finalità $DA_CONTESTO(id_finalita),
    associata al tuo e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Notifica e-service template sospeso] - Il creatore sospende il proprio e-service template e l'erogatore viene notificato
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service - OK"
    Given l'utente è un "admin" di "PA2"
    And l'utente tenta la creazione di un nuovo e-service con suffisso "OK_2" a partire dal template indicando tutte le specifiche
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la sospensione dell'e-service template
    # FALLISCE
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link TEMPLATE_E_SERVICE_EROGAZIONE
    """
    È stato sospeso il tuo template $DA_CONTESTO(eServiceTemplateId).
    """

  Scenario: [Notifica versione e-service sospesa] - L'erogatore sospende la versione dell'e-service a cui il fruitore è iscritto
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    And "PA2" ha già pubblicato una nuova versione per quell'e-service
    When "PA2" ha già sospeso la vecchia versione di quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha sospeso la versione $DA_CONTESTO(versione_e_service)
    dell'e-service $DA_CONTESTO(eServiceName), a cui sei iscritto.
    """

#    # TODO andrebbero unificati questi test per ottimizzare tempi di esecuzione, però il 1° non passa più... perché?
#    When "PA2" ha già attivato nuovamente la vecchia versione quell'e-service
#    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
#    """
#    L'ente erogatore $DA_CONTESTO(producerName) ha riattivato la versione $DA_CONTESTO(versione_e_service)
#    dell'e-service $DA_CONTESTO(eServiceName), precedentemente sospesa.
#    """

  # TODO Da qui in avanti scrivere meglio ID e titoli degli scenari
  Scenario: [Una versione di e-service è stata riattivata] - L'ente erogatore ha riattivato la versione dell'e-service precedentemente s…
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    And "PA2" ha già pubblicato una nuova versione per quell'e-service
    And "PA2" ha già sospeso la vecchia versione di quell'e-service
    When "PA2" ha già attivato nuovamente la vecchia versione quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha riattivato la versione $DA_CONTESTO(versione_e_service)
    dell'e-service $DA_CONTESTO(eServiceName), precedentemente sospesa.
    """

  Scenario: [Nuova versione disponibile per e-service] - È disponibile una nuova versione per l'e-service pubblicato da...
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When "PA2" ha già pubblicato una nuova versione per quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    È disponibile una nuova versione ($DA_CONTESTO(versione_e_service)) per l'e-service $DA_CONTESTO(eServiceName),
    pubblicato da $DA_CONTESTO(producerName).
    """

  Scenario: [Modifiche alla versione di e-service - L'ente erogatore ha apportato delle modifiche alle soglie di carico della versi…
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente aggiorna la stima di carico per quella finalità restando entro la soglia
    # Come aggiornare la stima di carico ad una versione precedente?
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha apportato delle modifiche alle soglie di carico della
    versione $DA_CONTESTO(versione_e_service) dell'e-service $DA_CONTESTO(eServiceName) a cui sei iscritto.
    """

  Scenario: [Modifiche alla versione di e-service] - L'ente erogatore ha aggiunto un documento nella versione dell'e-service "" a cu…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha aggiunto un documento nella versione
    $DA_CONTESTO(versione_e_service) dell'e-service $DA_CONTESTO(eServiceName) a cui sei iscritto.
    """

  Scenario: [Modifiche alla versione di e-service] - L'ente erogatore ha modificato la descrizione nella versione dell'e-service ""…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha modificato la descrizione nella versione
    $DA_CONTESTO(versione_e_service) dell'e-service $DA_CONTESTO(eServiceName) a cui sei iscritto.
    """

  Scenario: [Modifiche alla versione di e-service] - L'ente erogatore ha aggiornato un documento nella versione dell'e-service a…
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e un documento già caricato
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente aggiorna il nome di quel documento
    # FALLISCE
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha aggiornato un documento $DA_CONTESTO(nome_documento) della
    versione $DA_CONTESTO(versione_e_service) dell'e-service $DA_CONTESTO(eServiceName), a cui sei iscritto.
    """

  Scenario: [Nuovo livello di sicurezza per e-service] - Ti informiamo che l'ente erogatore ha aggiunto un nuovo livello di sicurezza (p…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    When l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service
    # FALLISCE
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    Ti informiamo che l'ente erogatore $DA_CONTESTO(producerName) ha aggiunto un nuovo livello di sicurezza
    (portachiavi) all'e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [L'e-service è stato rinominato] - Ti informiamo che l'e-service è stato rinominato dall'ente erogatore…
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given l'utente è un "admin" di "PA2"
    When l'utente aggiorna il nome dell'e-service con un valore di lunghezza 60 caratteri
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    Ti informiamo che l'e-service $DA_CONTESTO(precedente_eServiceName) è stato rinominato in
    $DA_CONTESTO(eServiceName) dall'ente erogatore. La tua richiesta di fruizione rimane attiva e
    non sono richieste azioni da parte tua.
    """

  Scenario: [La tua richiesta per e-service è stata accettata] - L'ente erogatore ha accettato la richiesta di fruizione formulata dal tuo ente…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA1" ha già approvato quella richiesta di fruizione
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha accettato la richiesta di fruizione formulata dal tuo ente
    per l'e-service $DA_CONTESTO(eServiceName). Puoi ora procedere alla creazione dei voucher per iniziare a
    interrogare le API.
    """

  Scenario: [La tua richiesta per e-service è stata rifiutata] - La richiesta di fruizione per l'e-service è stata rifiutata dall'ente erogat…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA1" ha già rifiutato quella richiesta di fruizione
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La richiesta di fruizione per l'e-service $DA_CONTESTO(eServiceName) è stata rifiutata dall'ente erogatore.
    """

  Scenario: [Sospensione richiesta di fruizione per e-service] - L'ente erogatore ha sospeso la richiesta di fruizione formulata dal tuo ente pe…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha sospeso la richiesta di fruizione formulata dal tuo ente
    per l'e-service $DA_CONTESTO(eServiceName). Non potrai utilizzare i voucher associati fino alla riattivazione.
    """

  Scenario: [NOTIFICATION_AGREEMENTS_5] - Sospensione richiesta di fruizione da parte della Piattaforma
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La Piattaforma PDND ha sospeso la richiesta di fruizione formulata dal tuo ente per l'e-service
    $DA_CONTESTO(eServiceName), in quanto non risultano più soddisfatti i requisiti necessari.
    """

  Scenario: [La tua richiesta per e-service è stata riattivata] - L'ente erogatore ha riattivato la richiesta di fruizione formulata dal tuo ente…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha riattivato la richiesta di fruizione formulata dal tuo ente
    per l'e-service $DA_CONTESTO(eServiceName), precedentemente sospesa. Puoi nuovamente utilizzare i voucher
    associati.
    """

  Scenario: [Riattivazione richiesta da parte della Piattaforma] - La Piattaforma PDND ha riattivato la richiesta di fruizione formulata dal tuo e…
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La Piattaforma PDND ha riattivato la richiesta di fruizione formulata dal tuo ente per l'e-service
    $DA_CONTESTO(eServiceName), precedentemente sospesa.
    """

  Scenario: [Avviso: soglia di carico per finalità superata] - La stima di carico complessiva per le finalità associate all'e-service ha su…
    # TODO per l'ammontare delle chiamata API massimo, si può decidere a monte un numero basso e noto e poi usarlo
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    La stima di carico complessiva per le finalità associate all'e-service $DA_CONTESTO(eServiceName) ha superato
    la soglia massima consentita dall'erogatore pari a $DA_CONTESTO(ammontare_chiamate_api) chiamate API giornaliere.
    """

  # Fallito, in quanto questa specifica notifica non è stata implementata (segnata come non-implementata nello sheet di riferimento). Test da togliere eventualmente.
  #Scenario: [Richiesta di adeguamento piano accettata] - L'ente erogatore ha accettato la richiesta di adeguamento del piano di carico f…
  #  Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
  #  """
  #  L'ente erogatore $DA_CONTESTO(producerName) ha accettato la richiesta di adeguamento del piano di carico
  #  formulata dal tuo ente per la finalità $DA_CONTESTO(id_finalita), associata all'e-service
  #  $DA_CONTESTO(eServiceName).
  #  """

  Scenario: [Richiesta di adeguamento piano rifiutata] - L'ente erogatore ha rifiutato la richiesta di adeguamento del piano di carico f…
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha rifiutato la richiesta di adeguamento del piano di carico
    formulata dal tuo ente per la finalità $DA_CONTESTO(id_finalita), associata all'e-service
    $DA_CONTESTO(eServiceName).
    """

  Scenario: [La tua finalità è stata approvata] - L'ente erogatore ha approvato la finalità che hai richiesto per l'e-service…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha approvato la finalità $DA_CONTESTO(id_finalita) che hai
    richiesto per l'e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [La tua finalità è stata rifiutata] - L'ente erogatore ha rifiutato la finalità che il tuo ente ha inoltrato per l…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha rifiutato la finalità $DA_CONTESTO(id_finalita) che il tuo
    ente ha inoltrato per l'e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Sospensione della finalità] - L'ente erogatore ha sospeso la finalità, associata all'e-service.
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha sospeso la finalità $DA_CONTESTO(id_finalita), associata
    all'e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Riattivazione della finalità] - L'ente erogatore ha riattivato la finalità, associata all'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(producerName) ha riattivato la finalità $DA_CONTESTO(id_finalita), associata
    all'e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Nuova versione del template] - L'ente ha pubblicato una nuova versione del template
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'ente $DA_CONTESTO(producerName) ha pubblicato una nuova versione $DA_CONTESTO(versione_template) del
    template $DA_CONTESTO(nome_template_e_service).
    """

  Scenario: [Aggiornamento nome del template] - Ti informiamo che il tuo e-service è stato rinominato in in quanto è stato modi…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    Ti informiamo che il tuo e-service $DA_CONTESTO(precedente_eServiceName) è stato rinominato in
    $DA_CONTESTO(eServiceName) in quanto è stato modificato il template e-service da cui lo hai generato.
    """

  Scenario: [Sospensione del template] - L'ente ha sospeso un template e-service da cui il tuo ente ha generato l'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link TEMPLATE_E_SERVICE_EROGAZIONE
    """
    L'ente $DA_CONTESTO(producerName) ha sospeso il template "$DA_CONTESTO(nome_template)", da cui il tuo ente
    ha generato l'e-service.
    """

  Scenario: [La tua richiesta di delega è stata accettata] - Ti informiamo che l'ente ha accettato la delega che il tuo ente gli ha conferit…
#    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
#    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
#    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
#    And l'ente "PA2" accetta la delega in erogazione con successo
    # FALLISCE
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    # THEN precedentemente commentato:
    #Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    #"""
    #Ti informiamo che l'ente $DA_CONTESTO(producerName) ha accettato la delega $DA_CONTESTO(id_delega) che il
    #tuo ente gli ha conferito per l'e-service $DA_CONTESTO(eServiceName). La delega è ora attiva.
    #"""
    # FALLISCE
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $DA_CONTESTO(producerName) ha approvato la delega $DA_CONTESTO(id_delega) che il
    tuo ente gli ha conferito per l'e-service $DA_CONTESTO(eServiceName). La delega è ora attiva.
    """

  Scenario: [La tua richiesta di delega è stata rifiutata] - Ti informiamo che l'ente ha rifiutato la delega che il tuo ente gli ha conferito…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $DA_CONTESTO(nome_ente_delegato) ha rifiutato la delega $DA_CONTESTO(id_delega) che il
    tuo ente gli ha conferito per l'e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Richiesta di approvazione per una nuova versione] - L'ente delegato richiede la tua approvazione per pubblicare una nuova versione…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegato $DA_CONTESTO(nome_ente_delegato) richiede la tua approvazione per pubblicare una nuova versione
    dell'e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Approvata la pubblicazione della nuova versione] - L'ente delegante ha approvato la pubblicazione della nuova versione dell'e-serv…
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegante $DA_CONTESTO(nome_ente_delegante) ha approvato la pubblicazione della nuova versione dell'e-service
    $DA_CONTESTO(eServiceName) che gestisci tramite delega.
    """

  Scenario: [Rifiutata la pubblicazione della nuova versione] - L'ente delegante ha rifiutato la pubblicazione della nuova versione dell'e-serv…
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegante $DA_CONTESTO(nome_ente_delegante) ha rifiutato la pubblicazione della nuova versione dell'e-service
    $DA_CONTESTO(eServiceName) che gestisci tramite delega.
    """

  Scenario: [Hai ricevuto una richiesta di delega] - Hai ricevuto una richiesta di delega per "" dall'ente per l'e-service
    # originale
    # Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    # """
    # Hai ricevuto una richiesta di delega per $DA_CONTESTO(id_delega) dall'ente $DA_CONTESTO(nome_ente_delegante) per
    # l'e-service $DA_CONTESTO(eServiceName).
    # """
    # adattato al messaggio ottenuto
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Hai ricevuto una richiesta di delega $DA_CONTESTO(id_delega) dall'ente $DA_CONTESTO(nome_ente_delegante) per
    l'e-service $DA_CONTESTO(eServiceName).
    """

  Scenario: [Una delega che gestivi è stata revocata] - Ti informiamo che l'ente ha revocato la delega per l'e-service che ti aveva…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $DA_CONTESTO(nome_ente_delegante) ha revocato la delega $DA_CONTESTO(id_delega) per
    l'e-service $DA_CONTESTO(eServiceName) che ti aveva conferito.
    """

  Scenario: [Hai ricevuto un nuovo attributo certificato] - L'ente certificatore ha conferito al tuo ente l'attributo certificato. Puoi…
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA2" ha già verificato l'attributo verificato a "PA1"
    # FIXME perché non trova la notifica? è presente
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    L'ente certificatore $DA_CONTESTO(nome_ente) ha conferito al tuo ente l'attributo certificato
    $DA_CONTESTO(id_attributo). Puoi ora utilizzarlo nelle richieste di fruizione.
    """

  Scenario: [Un tuo attributo certificato è stato revocato] - Ti informiamo che l'ente certificatore ha revocato l'attributo certificato…
    Given "PA2" ha già creato un attributo verificato
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "PA2" ha già verificato l'attributo verificato a "PA1"
    Given "GSP" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA1" ha un'altra richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "GSP" ha già verificato l'attributo verificato a "PA1" sull'altra richiesta di fruizione
    Given l'utente è un "admin" di "PA2"
    When l'utente revoca l'attributo precedentemente verificato
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    Ti informiamo che l'ente certificatore $DA_CONTESTO(nome_ente) ha revocato l'attributo certificato
    $DA_CONTESTO(id_attributo). Tutte le richieste di fruizione che utilizzano tale attributo subiranno una sospensione.
    Non potrai più utilizzare questo attributo per le future richieste di fruizione.
    """

  Scenario: [Hai ricevuto un nuovo attributo verificato] - L'ente certificatore ha conferito al tuo ente l'attributo verificato "". Puoi o…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    L'ente certificatore $DA_CONTESTO(nome_ente) ha conferito al tuo ente l'attributo verificato
    $DA_CONTESTO(id_attributo). Puoi ora utilizzarlo nelle richieste di fruizione.
    """

  Scenario: [Un tuo attributo verificato è stato revocato] - Ti informiamo che l'ente certificatore ha revocato l'attributo verificato "". T…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    Ti informiamo che l'ente certificatore $DA_CONTESTO(nome_ente) ha revocato l'attributo verificato
    $DA_CONTESTO(id_attributo). Tutte le richieste di fruizione che utilizzano tale attributo subiranno una sospensione.
    Non potrai più utilizzare questo attributo per le future richieste di fruizione.
    """

  # trigger: it/pagopa/pn/cucumber/authorization/client-key-delete.feature:7
  Scenario: [Una chiave di e-service è stata rimossa] - L'utente ha rimosso una chiave di e-service dal client. Assicurati che l'ope…
    Given l'utente è un "admin" di "PA1"
    And "GSP" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha aggiunto una nuova chiave pubblica al client
    When "PA1" rimuove quella nuova chiave dal client
    # FIXME Perché seppure la notifica è presente non viene vista? Quale delle due versioni di Then è quella giusta per il test?
    #Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    #"""
    #L'utente $DA_CONTESTO(nome_ruolo) ha rimosso una chiave di e-service dal client $DA_CONTESTO(id_client). Assicurati
    #che l'operatività non sia compromessa.
    #"""
    Then l'utente "admin" di "GSP" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    La chiave $DA_CONTESTO(id_chiave) è stata rimossa dal client $DA_CONTESTO(id_client). Assicurati che l'operatività
    non sia compromessa.
    """

  Scenario: [Attenzione: una chiave non è più sicura] - Una chiave associata al client non è più considerata sicura, in quanto l'ope…
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    And un "security" di "PA1" ha caricato una chiave pubblica nel client
    When "PA1" ha già rimosso l'utente con ruolo "security" dai membri di quel client
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    Una chiave associata al client $DA_CONTESTO(id_client) non è più considerata sicura, in quanto l'operatore che l'ha
    caricata non è più attivo. La chiave deve essere sostituita per garantire la sicurezza e l'operatività.
    """

  Scenario: [Nuova chiave aggiunta al client] - Ti informiamo che è stata aggiunta una nuova chiave e-service al client
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    When un "security" di "PA1" ha caricato una chiave pubblica nel client
    # FALLISCE
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    Ti informiamo che è stata aggiunta una nuova chiave e-service al client $DA_CONTESTO(id_client).
    """

  Scenario: [Una chiave di e-service è stata rimossa] - L'utente ha rimosso una chiave dal portachiavi erogatore. Assicurati che l'o…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service
    When l'utente "admin" di "PA1" rimuove tutte le chiavi dal portachiavi erogatore
    # FALLISCE
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link PORTACHIAVI_EROGAZIONE
    """
    La chiave $DA_CONTESTO(id_chiave) è stata rimossa dal portachiavi erogatore $DA_CONTESTO(nome_portachiavi).
    Assicurati che l'operatività non sia compromessa.
    """

  Scenario: [Attenzione: una chiave non è più sicura] - Una chiave associata al portachiavi erogatore non è più considerata sicura…
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    And l'utente "security" di "PA1" aggiunge una chiave al portachiavi erogatore
    When l'utente "admin" di "PA1" rimuove l'utente "security" dal portachiavi erogatore
    # FALLISCE: c'è solo il messaggio di chiave aggiunta al portachiavi
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link PORTACHIAVI_EROGAZIONE
    """
    Una chiave associata al portachiavi erogatore $DA_CONTESTO(nome_portachiavi) non è più considerata sicura,
    in quanto l'operatore che l'ha caricata non è più attivo. La chiave deve essere sostituita per garantire la
    sicurezza e l'operatività.
    """

  Scenario: [Nuova chiave aggiunta al portachiavi erogatore] - Ti informiamo che è stata aggiunta una nuova chiave al portachiavi erogatore
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    When l'utente "security" di "PA1" aggiunge una chiave al portachiavi erogatore
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link PORTACHIAVI_EROGAZIONE
    """
    Ti informiamo che è stata aggiunta una nuova chiave al portachiavi erogatore $DA_CONTESTO(nome_portachiavi).
    """
