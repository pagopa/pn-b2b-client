@agreement
Feature: Rifiuto di una richiesta di fruizione
  Tutti gli utenti autorizzati di enti PA e GSP possono rifiutare una richiesta di fruizione verso un proprio e-service

  @nrt-minimal
  @agreement_rejection1
  Scenario Outline: [AGREEMENT_REJECTION_01] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato PENDING, alla richiesta di rifiuto con messaggio da parte di un utente con sufficienti permessi (admin) dell’ente erogatore, va a buon fine

    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente richiede una operazione di rifiuto di quella richiesta di fruizione con messaggio
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | admin        |       200 |
      | PA1  | admin        |       200 |

    @sad-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | api          |       403 |
      | GSP  | security     |       403 |
      | GSP  | support      |       403 |
      | GSP  | api,security |       403 |
      | PA1  | api          |       403 |
      | PA1  | security     |       403 |
      | PA1  | support      |       403 |
      | PA1  | api,security |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | reviewer     |       403 |
      | GSP  | viewer       |       403 |

  @sad-path
  @nrt-minimal
  @agreement_rejection2
  Scenario: [AGREEMENT_REJECTION_02] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato PENDING, alla richiesta di rifiuto SENZA messaggio da parte di un utente con sufficienti permessi dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente richiede una operazione di rifiuto di quella richiesta di fruizione senza messaggio
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @agreement_rejection3a
  Scenario Outline: [AGREEMENT_REJECTION_03A] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato ACTIVE, SUSPENDED o ARCHIVED, alla richiesta di rifiuto con messaggio da parte di un utente con sufficienti permessi dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente richiede una operazione di rifiuto di quella richiesta di fruizione con messaggio
    Then si ottiene status code 400

    Examples:
      | statoAgreement |
      | ACTIVE         |
      | SUSPENDED      |
      | ARCHIVED       |

  @sad-path
  @nrt-minimal
  @agreement_rejection3b
  Scenario: [AGREEMENT_REJECTION_03B] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato REJECTED, alla richiesta di rifiuto con messaggio da parte di un utente con sufficienti permessi dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Given "PA2" ha già rifiutato quella richiesta di fruizione
    When l'utente richiede una operazione di rifiuto di quella richiesta di fruizione con messaggio
    Then si ottiene status code 400