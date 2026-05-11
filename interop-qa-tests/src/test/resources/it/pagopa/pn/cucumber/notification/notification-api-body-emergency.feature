@bff-notification
#@disable-notifications-hooks # FIXME usato per velocizzare l'esecuzione dei test in locale, rimuovere

# TODO fragilità: la regex di molti deeplink termina con .+, che catturerebbe anche indirizzi con più di un path variabile, quando ce ne si aspetterebbe soltanto 1. Correggere regex escludendo carattere '/'.
Feature: API Notifiche - verifica bodies (generato da excel)
  #Background:
  # eliminazione di ogni notifica presente per ogni ente ed utente (applicato con Cucumber Hook @Before)
  # abilitazione delle notifiche in-app per ogni ente ed utente (applicato con Cucumber Hook @Before)
  # disabilitazione delle notifiche in-app per ogni ente ed utente (applicato con Cucumber Hook @After)

  Scenario: [Nuova richiesta di fruizione per un tuo e-service] - Hai ricevuto una nuova richiesta di fruizione per l'e-service formulata da part…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    When "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    Hai ricevuto una nuova richiesta di fruizione per l'e-service $DA_CONTESTO(nome_e_service) formulata
    da parte di $DA_CONTESTO(nome_ente_fruitore).
    """

  Scenario: [Richiesta di fruizione accettata automaticamente] - È stata accettata una richiesta di fruizione per l'e-service formulata da parte…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA1" ha già approvato quella richiesta di fruizione
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    È stata accettata una richiesta di fruizione per l'e-service $DA_CONTESTO(nome_e_service) formulata
    da parte di $DA_CONTESTO(nome_ente_fruitore).
    """

  Scenario: [Richiesta di fruizione aggiornata per un tuo e-service] - L'ente ha aggiornato la propria richiesta di fruizione per l'e-service alla ver…
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
    L'ente $DA_CONTESTO(nome_ente_erogatore) ha aggiornato la propria richiesta di fruizione per l'e-service
    $DA_CONTESTO(nome_e_service) alla versione più recente.
    """

  Scenario: [Sospensione richiesta di fruizione da parte del fruitore] - L'ente ha sospeso la propria richiesta di fruizione per il suo e-service .
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA2" ha una richiesta di fruizione in stato "SUSPENDED" per quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(nome_ente_fruitore) ha sospeso la propria richiesta di fruizione per il suo e-service
    $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Sospensione richiesta di fruizione da parte della Piattaforma] - La Piattaforma PDND ha sospeso la richiesta di fruizione del fruitore per il tu…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    # FIXME serve che l'e-service richieda un attributo e che un qualche ente rimuova questo attributo
    # così la richiesta di fruizione (già fatta) viene sospesa per regole della piattaforma
    When "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    La Piattaforma PDND ha sospeso la richiesta di fruizione del fruitore $DA_CONTESTO(nome_ente_fruitore) per il tuo
    e-service $DA_CONTESTO(nome_e_service), in quanto l'ente fruitore non dispone più dei requisiti per poter fruire
    di questi dati.
    """

  Scenario: [Riattivazione richiesta di fruizione da parte del fruitore] - L'ente ha riattivato la propria richiesta di fruizione per il tuo e-service , p…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "SUSPENDED" per quell'e-service
    When "PA2" ha già attivato nuovamente quella richiesta di fruizione come CONSUMER
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(nome_ente_fruitore) ha riattivato la propria richiesta di fruizione per il tuo e-service
    $DA_CONTESTO(nome_e_service), precedentemente sospesa.
    """

  Scenario: [Riattivazione richiesta di fruizione da parte della Piattaforma] - La Piattaforma PDND ha riattivato la richiesta di fruizione del fruitore per il…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    La Piattaforma PDND ha riattivato la richiesta di fruizione del fruitore $DA_CONTESTO(nome_ente_fruitore) per
    il tuo e-service $DA_CONTESTO(nome_e_service), precedentemente sospesa.
    """

  Scenario: [Richiesta di fruizione archiviata dal fruitore] - Ti informiamo che il fruitore ha archiviato la sua richiesta di fruizione per i…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA2" ha una richiesta di fruizione in stato "ARCHIVED" per quell'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    Ti informiamo che il fruitore $DA_CONTESTO(nome_ente_fruitore) ha archiviato la sua richiesta di fruizione per
    il tuo e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Nuovo client associato a una finalità] - L'ente ha associato un proprio client ad una finalità per il tuo e-service
    Given "GSP" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato una nuova finalità attiva per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When "PA1" ha già associato quella nuova finalità a quel client
    Then l'utente "admin" di "GSP" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(nome_ente_fruitore) ha associato un proprio client alla finalità $DA_CONTESTO(id_finalita)
    per il tuo e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Client disassociato da una finalità] - L'ente ha disassociato un proprio client ad una finalità per il tuo e-service
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
    L'ente $DA_CONTESTO(nome_ente_fruitore) ha disassociato un proprio client dalla finalità $DA_CONTESTO(id_finalita)
    per il tuo e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Richiesta di adeguamento piano di carico] - L'ente ha richiesto un adeguamento del piano di carico per la finalità, asso…
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When "PA1" ha già richiesto l'aggiornamento della stima di carico superando i limiti di quell'e-service
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $DA_CONTESTO(nome_ente_fruitore) ha richiesto un adeguamento del piano di carico per la finalità
    $DA_CONTESTO(id_finalita), associata al tuo e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [NOTIF_001] - L'ente ha inviato una finalità che prevede un piano di carico superiore alla…
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
    L'ente $DA_CONTESTO(nome_ente_fruitore) ha inviato la finalità $DA_CONTESTO(id_finalita), che prevede un piano
    di carico superiore alla tua soglia, associata al tuo e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Finalità sospesa dal fruitore] - Ti informiamo che l'ente ha sospeso la finalità associata al tuo e-service
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    Ti informiamo che l'ente $DA_CONTESTO(nome_ente_fruitore) ha sospeso la finalità $DA_CONTESTO(id_finalita),
    associata al tuo e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Finalità riattivata dal fruitore] - Ti informiamo che l'ente ha riattivato la finalità associata al tuo e-service
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    When l'utente "admin" di "PA2" riattiva la finalità in stato "SUSPENDED" per quell'e-service
    #FALLISCE
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    Ti informiamo che l'ente $DA_CONTESTO(nome_ente_fruitore) ha riattivato la finalità $DA_CONTESTO(id_finalita),
    associata al tuo e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Finalità archiviata dal fruitore] - Ti informiamo che il fruitore ha archiviato la finalità associata al tuo e-service
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When "PA2" ha già archiviato quella finalità
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    Ti informiamo che l'ente $DA_CONTESTO(nome_ente_fruitore) ha archiviato la finalità $DA_CONTESTO(id_finalita),
    associata al tuo e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Hai sospeso un tuo template e-service] - È stato sospeso il tuo template...
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente è un "security" di "PA1"
    And l'utente effettua la sospensione dell'e-service template
    # FALLISCE
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link TEMPLATE_E_SERVICE_EROGAZIONE
    """
    È stato sospeso il tuo template $DA_CONTESTO(eServiceTemplateId).
    """

  Scenario: [Una versione di e-service è stata sospesa] - L'ente erogatore ha sospeso la versione dell'e-service a cui sei iscritto.
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
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha sospeso la versione $DA_CONTESTO(versione_e_service)
    dell'e-service $DA_CONTESTO(nome_e_service), a cui sei iscritto.
    """

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
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha riattivato la versione $DA_CONTESTO(versione_e_service)
    dell'e-service $DA_CONTESTO(nome_e_service), precedentemente sospesa.
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
    È disponibile una nuova versione ($DA_CONTESTO(versione_e_service)) per l'e-service $DA_CONTESTO(nome_e_service),
    pubblicato da $DA_CONTESTO(nome_ente_erogatore).
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
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha apportato delle modifiche alle soglie di carico della
    versione $DA_CONTESTO(versione_e_service) dell'e-service $DA_CONTESTO(nome_e_service) a cui sei iscritto.
    """

  Scenario: [Modifiche alla versione di e-service] - L'ente erogatore ha aggiunto un documento nella versione dell'e-service "" a cu…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha aggiunto un documento nella versione
    $DA_CONTESTO(versione_e_service) dell'e-service $DA_CONTESTO(nome_e_service) a cui sei iscritto.
    """

  Scenario: [Modifiche alla versione di e-service] - L'ente erogatore ha modificato la descrizione nella versione dell'e-service ""…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha modificato la descrizione nella versione
    $DA_CONTESTO(versione_e_service) dell'e-service $DA_CONTESTO(nome_e_service) a cui sei iscritto.
    """

  Scenario: [Modifiche alla versione di e-service] - L'ente erogatore ha aggiornato un documento nella versione dell'e-service a…
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e un documento già caricato
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente aggiorna il nome di quel documento
    # FALLISCE
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha aggiornato un documento $DA_CONTESTO(nome_documento) della
    versione $DA_CONTESTO(versione_e_service) dell'e-service $DA_CONTESTO(nome_e_service), a cui sei iscritto.
    """

  Scenario: [Nuovo livello di sicurezza per e-service] - Ti informiamo che l'ente erogatore ha aggiunto un nuovo livello di sicurezza (p…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    When l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service
    # FALLISCE
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    Ti informiamo che l'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha aggiunto un nuovo livello di sicurezza
    (portachiavi) all'e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [L'e-service è stato rinominato] - Ti informiamo che l'e-service è stato rinominato dall'ente erogatore…
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given l'utente è un "admin" di "PA2"
    When l'utente aggiorna il nome dell'e-service con un valore di lunghezza 60 caratteri
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    Ti informiamo che l'e-service $DA_CONTESTO(precedente_nome_e_service) è stato rinominato in
    $DA_CONTESTO(nome_e_service) dall'ente erogatore. La tua richiesta di fruizione rimane attiva e
    non sono richieste azioni da parte tua.
    """

  Scenario: [La tua richiesta per e-service è stata accettata] - L'ente erogatore ha accettato la richiesta di fruizione formulata dal tuo ente…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA1" ha già approvato quella richiesta di fruizione
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha accettato la richiesta di fruizione formulata dal tuo ente
    per l'e-service $DA_CONTESTO(nome_e_service). Puoi ora procedere alla creazione dei voucher per iniziare a
    interrogare le API.
    """

  Scenario: [La tua richiesta per e-service è stata rifiutata] - La richiesta di fruizione per l'e-service è stata rifiutata dall'ente erogat…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA1" ha già rifiutato quella richiesta di fruizione
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La richiesta di fruizione per l'e-service $DA_CONTESTO(nome_e_service) è stata rifiutata dall'ente erogatore.
    """

  Scenario: [Sospensione richiesta di fruizione per e-service] - L'ente erogatore ha sospeso la richiesta di fruizione formulata dal tuo ente pe…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha sospeso la richiesta di fruizione formulata dal tuo ente
    per l'e-service $DA_CONTESTO(nome_e_service). Non potrai utilizzare i voucher associati fino alla riattivazione.
    """

  Scenario: [NOTIFICATION_AGREEMENTS_5] - Sospensione richiesta di fruizione da parte della Piattaforma
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La Piattaforma PDND ha sospeso la richiesta di fruizione formulata dal tuo ente per l'e-service
    $DA_CONTESTO(nome_e_service), in quanto non risultano più soddisfatti i requisiti necessari.
    """

  Scenario: [La tua richiesta per e-service è stata riattivata] - L'ente erogatore ha riattivato la richiesta di fruizione formulata dal tuo ente…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha riattivato la richiesta di fruizione formulata dal tuo ente
    per l'e-service $DA_CONTESTO(nome_e_service), precedentemente sospesa. Puoi nuovamente utilizzare i voucher
    associati.
    """

  Scenario: [Riattivazione richiesta da parte della Piattaforma] - La Piattaforma PDND ha riattivato la richiesta di fruizione formulata dal tuo e…
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La Piattaforma PDND ha riattivato la richiesta di fruizione formulata dal tuo ente per l'e-service
    $DA_CONTESTO(nome_e_service), precedentemente sospesa.
    """

  Scenario: [Avviso: soglia di carico per finalità superata] - La stima di carico complessiva per le finalità associate all'e-service ha su…
    # TODO per l'ammontare delle chiamata API massimo, si può decidere a monte un numero basso e noto e poi usarlo
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    La stima di carico complessiva per le finalità associate all'e-service $DA_CONTESTO(nome_e_service) ha superato
    la soglia massima consentita dall'erogatore pari a $DA_CONTESTO(ammontare_chiamate_api) chiamate API giornaliere.
    """

  # Fallito, in quanto questa specifica notifica non è stata implementata (segnata come non-implementata nello sheet di riferimento). Test da togliere eventualmente.
  #Scenario: [Richiesta di adeguamento piano accettata] - L'ente erogatore ha accettato la richiesta di adeguamento del piano di carico f…
  #  Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
  #  """
  #  L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha accettato la richiesta di adeguamento del piano di carico
  #  formulata dal tuo ente per la finalità $DA_CONTESTO(id_finalita), associata all'e-service
  #  $DA_CONTESTO(nome_e_service).
  #  """

  Scenario: [Richiesta di adeguamento piano rifiutata] - L'ente erogatore ha rifiutato la richiesta di adeguamento del piano di carico f…
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha rifiutato la richiesta di adeguamento del piano di carico
    formulata dal tuo ente per la finalità $DA_CONTESTO(id_finalita), associata all'e-service
    $DA_CONTESTO(nome_e_service).
    """

  Scenario: [La tua finalità è stata approvata] - L'ente erogatore ha approvato la finalità che hai richiesto per l'e-service…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha approvato la finalità $DA_CONTESTO(id_finalita) che hai
    richiesto per l'e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [La tua finalità è stata rifiutata] - L'ente erogatore ha rifiutato la finalità che il tuo ente ha inoltrato per l…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha rifiutato la finalità $DA_CONTESTO(id_finalita) che il tuo
    ente ha inoltrato per l'e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Sospensione della finalità] - L'ente erogatore ha sospeso la finalità, associata all'e-service.
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha sospeso la finalità $DA_CONTESTO(id_finalita), associata
    all'e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Riattivazione della finalità] - L'ente erogatore ha riattivato la finalità, associata all'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $DA_CONTESTO(nome_ente_erogatore) ha riattivato la finalità $DA_CONTESTO(id_finalita), associata
    all'e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Nuova versione del template] - L'ente ha pubblicato una nuova versione del template
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'ente $DA_CONTESTO(nome_ente_erogatore) ha pubblicato una nuova versione $DA_CONTESTO(versione_template) del
    template $DA_CONTESTO(nome_template_e_service).
    """

  Scenario: [Aggiornamento nome del template] - Ti informiamo che il tuo e-service è stato rinominato in in quanto è stato modi…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    Ti informiamo che il tuo e-service $DA_CONTESTO(precedente_nome_e_service) è stato rinominato in
    $DA_CONTESTO(nome_e_service) in quanto è stato modificato il template e-service da cui lo hai generato.
    """

  Scenario: [Sospensione del template] - L'ente ha sospeso un template e-service da cui il tuo ente ha generato l'e-service
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link TEMPLATE_E_SERVICE_EROGAZIONE
    """
    L'ente $DA_CONTESTO(nome_ente_erogatore) ha sospeso il template "$DA_CONTESTO(nome_template)", da cui il tuo ente
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
    #Ti informiamo che l'ente $DA_CONTESTO(nome_ente_erogatore) ha accettato la delega $DA_CONTESTO(id_delega) che il
    #tuo ente gli ha conferito per l'e-service $DA_CONTESTO(nome_e_service). La delega è ora attiva.
    #"""
    # FALLISCE
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $DA_CONTESTO(nome_ente_erogatore) ha approvato la delega $DA_CONTESTO(id_delega) che il
    tuo ente gli ha conferito per l'e-service $DA_CONTESTO(nome_e_service). La delega è ora attiva.
    """

  Scenario: [La tua richiesta di delega è stata rifiutata] - Ti informiamo che l'ente ha rifiutato la delega che il tuo ente gli ha conferito…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $DA_CONTESTO(nome_ente_delegato) ha rifiutato la delega $DA_CONTESTO(id_delega) che il
    tuo ente gli ha conferito per l'e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Richiesta di approvazione per una nuova versione] - L'ente delegato richiede la tua approvazione per pubblicare una nuova versione…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegato $DA_CONTESTO(nome_ente_delegato) richiede la tua approvazione per pubblicare una nuova versione
    dell'e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Approvata la pubblicazione della nuova versione] - L'ente delegante ha approvato la pubblicazione della nuova versione dell'e-serv…
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegante $DA_CONTESTO(nome_ente_delegante) ha approvato la pubblicazione della nuova versione dell'e-service
    $DA_CONTESTO(nome_e_service) che gestisci tramite delega.
    """

  Scenario: [Rifiutata la pubblicazione della nuova versione] - L'ente delegante ha rifiutato la pubblicazione della nuova versione dell'e-serv…
    Then l'utente "admin" di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegante $DA_CONTESTO(nome_ente_delegante) ha rifiutato la pubblicazione della nuova versione dell'e-service
    $DA_CONTESTO(nome_e_service) che gestisci tramite delega.
    """

  Scenario: [Hai ricevuto una richiesta di delega] - Hai ricevuto una richiesta di delega per "" dall'ente per l'e-service
    # originale
    # Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    # """
    # Hai ricevuto una richiesta di delega per $DA_CONTESTO(id_delega) dall'ente $DA_CONTESTO(nome_ente_delegante) per
    # l'e-service $DA_CONTESTO(nome_e_service).
    # """
    # adattato al messaggio ottenuto
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Hai ricevuto una richiesta di delega $DA_CONTESTO(id_delega) dall'ente $DA_CONTESTO(nome_ente_delegante) per
    l'e-service $DA_CONTESTO(nome_e_service).
    """

  Scenario: [Una delega che gestivi è stata revocata] - Ti informiamo che l'ente ha revocato la delega per l'e-service che ti aveva…
    Then l'utente "admin" di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $DA_CONTESTO(nome_ente_delegante) ha revocato la delega $DA_CONTESTO(id_delega) per
    l'e-service $DA_CONTESTO(nome_e_service) che ti aveva conferito.
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
