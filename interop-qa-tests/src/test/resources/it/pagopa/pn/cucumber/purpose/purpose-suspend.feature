@purpose
Feature: Sospensione di una finalità
  Tutti gli utenti autorizzati possono sospendere una propria finalità

  @nrt-minimal
  @purpose_suspend1
  Scenario Outline: [PURPOSE_SUSPEND_1] Per una finalità precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE, alla richiesta di sospensione da parte di un utente con sufficienti permessi dell’ente fruitore, che non coincide con l’ente erogatore, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "<ente>" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente sospende quella finalità in stato "ACTIVE"
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente    | ruolo        | risultato |
      | PA1     | admin        |       200 |
      | GSP     | admin        |       200 |
      | Privato | admin        |       200 |

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

  @happy-path
  @nrt-minimal
  @purpose_suspend2
  Scenario: [PURPOSE_SUSPEND_2] Per una finalità precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE, alla richiesta di sospensione da parte di un utente con sufficienti permessi dell’ente erogatore, che non coincide con l’ente fruitore, va a buon fine
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente sospende quella finalità in stato "ACTIVE"
    Then si ottiene status code 200

  @happy-path
  @nrt-minimal
  @purpose_suspend3
  Scenario Outline: [PURPOSE_SUSPEND_3] Per una finalità precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE o SUSPENDED, alla richiesta di sospensione da parte di un utente con sufficienti permessi dell’ente erogatore, che coincide con l’ente fruitore, va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
    When l'utente sospende quella finalità in stato "<statoFinalita>"
    Then si ottiene status code 200

    Examples: 
      | statoFinalita |
      | ACTIVE        |
      | SUSPENDED     |

  @sad-path
  @nrt-minimal
  @purpose_suspend4a
  Scenario Outline: [PURPOSE_SUSPEND_4A] Per una finalità precedentemente creata da un fruitore, la quale è in stato WAITING_FOR_APPROVAL, DRAFT o ARCHIVED, alla richiesta di sospensione da parte di un utente con sufficienti permessi, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
    When l'utente sospende quella finalità in stato "<statoFinalita>"
    Then si ottiene status code 400

    Examples: 
      | statoFinalita        |
      | WAITING_FOR_APPROVAL |
      | DRAFT                |
      | ARCHIVED             |

  @sad-path
  @nrt-minimal
  @purpose_suspend4b
  Scenario: [PURPOSE_SUSPEND_4B] Per una finalità precedentemente creata da un fruitore, la quale è in stato REJECTED, alla richiesta di sospensione da parte di un utente con sufficienti permessi, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    Given "PA2" ha già rifiutato l'aggiornamento della stima di carico per quella finalità
    When l'utente sospende quella finalità in stato "REJECTED"
    Then si ottiene status code 400
