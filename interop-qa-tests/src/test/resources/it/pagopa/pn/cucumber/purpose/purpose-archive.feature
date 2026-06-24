@purpose
Feature: Archiviazione di una finalità
  Tutti gli utenti autorizzati possono archiviare una propria finalità

  @nrt-minimal
  @purpose_archive1
  Scenario Outline: [PURPOSE_ARCHIVE_1] Per una finalità precedentemente creata da un fruitore, la quale è in stato ACTIVE o SUSPENDED, alla richiesta di archiviazione da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given  "<ente>" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente archivia quella finalità in stato "ACTIVE"
    Then si ottiene status code <risultato>

    @happy-path
    Examples: # Test sui ruoli
      | ente    | ruolo        | risultato |
      | PA1     | admin        |       200 |
      | GSP     | admin        |       200 |
      | Privato | admin        |       200 |

    @sad-path
    Examples: # Test sui ruoli
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
    Examples: # Test sui ruoli
      | ente    | ruolo        | risultato |
      | PA2     | reviewer     |       403 |
      | PA2     | viewer       |       403 |
      | GSP     | reviewer     |       403 |
      | GSP     | viewer       |       403 |
      | Privato | reviewer     |       403 |
      | Privato | viewer       |       403 |

    @happy-path
    Examples: # Test sugli stati
      | ente | ruolo | statoFinalita | risultato |
      | PA1  | admin | SUSPENDED     |       200 |

  @happy-path
  @nrt-minimal
  @purpose_archive2 @wait_for_fix @IMN-402
  Scenario Outline: [PURPOSE_ARCHIVE_2] Per una finalità precedentemente creata da un fruitore, la quale è in stato ACTIVE o SUSPENDED, con una versione di finalità successiva in stato WAITING_FOR_APPROVAL alla richiesta di archiviazione da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
    Given "PA1" ha già richiesto l'aggiornamento della stima di carico superando i limiti di quell'e-service
    When l'utente archivia quella finalità in stato "<statoFinalita>"
    Then si ottiene status code 200

    Examples: 
      | statoFinalita |
      | ACTIVE        |
      | SUSPENDED     |

  @sad-path
  @nrt-minimal
  @purpose_archive3
  Scenario Outline: [PURPOSE_ARCHIVE_3] Per una finalità precedentemente creata da un fruitore, la quale è in stato ACTIVE o SUSPENDED, alla richiesta di archiviazione da parte di un utente con sufficienti permessi (admin) dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 1 e-service
    Given "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA2" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
    When l'utente archivia quella finalità in stato "<statoFinalita>"
    Then si ottiene status code 403

    Examples: 
      | statoFinalita |
      | ACTIVE        |
      | SUSPENDED     |

  @sad-path
  @nrt-minimal
  @purpose_archive4a @wait_for_fix @IMN-402
  Scenario Outline: [PURPOSE_ARCHIVE_4A] Per una finalità precedentemente creata da un fruitore, la quale è in stato WAITING_FOR_APPROVAL, DRAFT o ARCHIVED, alla richiesta di archiviazione da parte di un utente con sufficienti permessi (admin) dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
    When l'utente archivia quella finalità in stato "<statoFinalita>"
    Then si ottiene status code 400

    Examples: 
      | statoFinalita        |
      | WAITING_FOR_APPROVAL |
      | DRAFT                |
      | ARCHIVED             |

  @sad-path
  @nrt-minimal
  @purpose_archive4b @fixed_in_node
  Scenario: [PURPOSE_ARCHIVE_4B] Per una finalità precedentemente creata da un fruitore, la quale è in stato REJECTED, alla richiesta di archiviazione da parte di un utente con sufficienti permessi (admin) dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    Given "PA2" ha già rifiutato l'aggiornamento della stima di carico per quella finalità
    When l'utente archivia quella finalità in stato "REJECTED"
    Then si ottiene status code 400
