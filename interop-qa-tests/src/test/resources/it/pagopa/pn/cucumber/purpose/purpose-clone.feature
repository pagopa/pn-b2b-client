@purpose
Feature: Clonazione di una finalità
  Tutti gli utenti autorizzati di enti fruitori possono clonare una propria finalità

  @nrt-minimal
  @purpose_clone1
  Scenario Outline: [CLONAZIONE_FINALITA_1] Un utente con sufficienti permessi (admin); il cui ente ha già una finalità in stato ACTIVE, per una versione di e-service, il quale ha mode = RECEIVE, clona una finalità. La richiesta va a buon fine.
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    Given "PA2" ha già creato un'analisi del rischio per quell'e-service
    Given "PA2" ha già caricato un'interfaccia per quel descrittore
    Given "PA2" ha già pubblicato quella versione di e-service
    Given "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "<ente>" ha già creato una finalità in stato "<statoFinalità>" per quell'eservice associando quell'analisi del rischio creata dall'erogatore
    When l'utente richiede una operazione di clonazione della finalità
    Then si ottiene status code <risultato>

    @happy-path
    Examples: # Test sui ruoli
      | ente    | ruolo        | statoFinalità | risultato |
      | PA1     | admin        | ACTIVE        |       200 |
      | GSP     | admin        | ACTIVE        |       200 |
      | Privato | admin        | ACTIVE        |       200 |

    @sad-path
    Examples: # Test sui ruoli
      | ente    | ruolo        | statoFinalità | risultato |
      | PA1     | api          | ACTIVE        |       403 |
      | PA1     | security     | ACTIVE        |       403 |
      | PA1     | api,security | ACTIVE        |       403 |
      | PA1     | support      | ACTIVE        |       403 |
      | GSP     | api          | ACTIVE        |       403 |
      | GSP     | security     | ACTIVE        |       403 |
      | GSP     | api,security | ACTIVE        |       403 |
      | GSP     | support      | ACTIVE        |       403 |
      | Privato | api          | ACTIVE        |       403 |
      | Privato | security     | ACTIVE        |       403 |
      | Privato | api,security | ACTIVE        |       403 |
      | Privato | support      | ACTIVE        |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples: # Test sui ruoli
      | ente    | ruolo        | statoFinalità | risultato |
      | PA2     | reviewer     | ACTIVE        |       403 |
      | PA2     | viewer       | ACTIVE        |       403 |
      | GSP     | reviewer     | ACTIVE        |       403 |
      | GSP     | viewer       | ACTIVE        |       403 |
      | Privato | reviewer     | ACTIVE        |       403 |
      | Privato | viewer       | ACTIVE        |       403 |

    @happy-path
    Examples: # Test sugli stati
      | ente | ruolo | statoFinalità        | risultato |
      | PA1  | admin | WAITING_FOR_APPROVAL |       200 |
      | PA1  | admin | SUSPENDED            |       200 |

  @sad-path
  @nrt-minimal
  @purpose_clone2
  Scenario Outline: [CLONAZIONE_FINALITA_2] Un utente con sufficienti permessi (admin); il cui ente ha già una finalità in stato DRAFT, o ARCHIVED per una versione di e-service, il quale ha mode = RECEIVE, clona una finalità. Ottiene un errore se la finalità è in stato DRAFT, successo se è in stato ARCHIVED
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    Given "PA2" ha già creato un'analisi del rischio per quell'e-service
    Given "PA2" ha già caricato un'interfaccia per quel descrittore
    Given "PA2" ha già pubblicato quella versione di e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato una finalità in stato "<statoFinalita>" per quell'eservice associando quell'analisi del rischio creata dall'erogatore
    When l'utente richiede una operazione di clonazione della finalità
    Then si ottiene status code <status>

    Examples:
      | statoFinalita | status |
      | DRAFT         |  409   |
      | ARCHIVED      |  409   |

  @happy-path
  @nrt-minimal
  @purpose_clone3
  Scenario Outline: [CLONAZIONE_FINALITA_3] Un utente con sufficienti permessi (admin); il cui ente ha già una finalità in stato WAITING_FOR_APPROVAL, ACTIVE, o SUSPENDED per una versione di e-service, il quale ha mode = DELIVER, clona una finalità. La richiesta va a buon fine. Spiega: visto che ci sono problemi legati all’analisi del rischio in erogazione inversa, non è possibile clonare una finalità che faccia riferimento a un e-service in erogazione inversa.
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
    When l'utente richiede una operazione di clonazione della finalità
    Then si ottiene status code 200

    Examples:
      | statoFinalita        |
      | ACTIVE               |
      | WAITING_FOR_APPROVAL |
      | SUSPENDED            |
