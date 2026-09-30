@purpose
Feature: Cancellazione finalità
  Tutti gli admin possono cancellare una propria finalità in stato DRAFT o WAITING_FOR_APPROVAL.

  @nrt-minimal
  @purpose_delete1
  Scenario Outline: [PURPOSE_DELETE_1] Per una finalità precedentemente creata dall’ente, la quale prima versione è in stato DRAFT, alla richiesta di cancellazione da parte di un utente con sufficienti permessi (admin), va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "<ente>" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente richiede la cancellazione della finalità
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente    | ruolo        | risultato |
      | PA1     | admin        |       204 |
      | GSP     | admin        |       204 |
      | Privato | admin        |       204 |

    @sad-path
    Examples:
      | ente    | ruolo        | risultato |
      | PA1     | api          |       403 |
      | PA1     | security     |       403 |
      | PA1     | api,security |       403 |
      | PA1     | support      |       403 |
      | GSP     | api          |       403 |
      | GSP     | security     |       403 |
      | GSP     | api,security |       403 |
      | GSP     | support      |       403 |
      | Privato | api          |       403 |
      | Privato | security     |       403 |
      | Privato | api,security |       403 |
      | Privato | support      |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente    | ruolo        | risultato |
      | PA2     | reviewer     |       403 |
      | PA2     | viewer       |       403 |
      | GSP     | reviewer     |       403 |
      | GSP     | viewer       |       403 |
      | Privato | reviewer     |       403 |
      | Privato | viewer       |       403 |

  @nrt-minimal
  @purpose_delete2
  Scenario Outline: [PURPOSE_DELETE_2] Per una finalità precedentemente creata dall’ente, la quale prima versione è in stato ACTIVE, SUSPENDED, WAITING_FOR_APPROVAL o ARCHIVED, alla richiesta di cancellazione da parte di un utente con sufficienti permessi (admin), ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
    When l'utente richiede la cancellazione della finalità
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | statoFinalita        | risultato |
      | WAITING_FOR_APPROVAL |       204 |

    @sad-path
    Examples:
      | statoFinalita        | risultato |
      | ACTIVE               |       409 |
      | SUSPENDED            |       409 |
      | ARCHIVED             |       409 |

  @sad-path
  @nrt-minimal
  @purpose_delete3
  Scenario: [PURPOSE_DELETE_3] Per una finalità precedentemente creata dall’ente, la quale prima versione è in stato DRAFT, alla richiesta di cancellazione da parte di un utente con sufficienti permessi (admin), che non è il fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente richiede la cancellazione della finalità
    Then si ottiene status code 403

  @adeguamento-analisi-rischio
  Scenario Outline: [PURPOSE_DELETE_TK_1] A seguito del cambiamento di tenant kind si tenta di eliminare una finalità
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "<ente>" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind>"
    When l'utente è un "admin" di "<ente>"
    And l'utente richiede la cancellazione della finalità
    Then si ottiene status code 200
    Examples:
      | ente    | kind        |
      | PA4     | PRIVATE     |
      | PA4     | GSP         |
      | GSP2    | PA          |
      | Privato | PA          |