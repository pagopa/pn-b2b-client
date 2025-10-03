@purpose
Feature: Lettura singola finalità
  Tutti gli utenti possono leggere una finalità, l'analisi del rischio è disponibile solo per admin fruitori o erogatori di quella finalità.

  @happy-path
  @purpose_read1
  @wait_for_fix
  Scenario Outline: Per una finalità precedentemente creata dal fruitore, la quale prima versione è in qualsiasi stato (DRAFT, WAITING_FOR_APPROVAL, ACTIVE, SUSPENDED, ARCHIVED), alla richiesta di lettura, va a buon fine, l’analisi del rischio è disponibile solo per gli admin
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "<ente>" ha già creato 1 finalità in stato "<statoFinalità>" per quell'eservice
    When l'utente richiede la lettura della finalità
    Then si ottiene status code 200 ma l'analisi del rischio solo per admin

    Examples: # Test sui ruoli
      | ente    | ruolo        | statoFinalità |
      | PA1     | admin        | ACTIVE        |
      | PA1     | api          | ACTIVE        |
      | PA1     | security     | ACTIVE        |
      | PA1     | api,security | ACTIVE        |
      | PA1     | support      | ACTIVE        |
      | GSP     | admin        | ACTIVE        |
      | GSP     | api          | ACTIVE        |
      | GSP     | security     | ACTIVE        |
      | GSP     | api,security | ACTIVE        |
      | GSP     | support      | ACTIVE        |
      | Privato | admin        | ACTIVE        |
      | Privato | api          | ACTIVE        |
      | Privato | security     | ACTIVE        |
      | Privato | api,security | ACTIVE        |
      | Privato | support      | ACTIVE        |

    Examples: # Test sugli stati
      | ente | ruolo | statoFinalità        |
      | PA1  | admin | WAITING_FOR_APPROVAL |
      | PA1  | admin | SUSPENDED            |
      | PA1  | admin | ARCHIVED             |
      | PA1  | admin | DRAFT                |

  @happy-path
  @purpose_read2 @no-parallel
  Scenario Outline: Per una finalità precedentemente creata da un fruitore, la quale prima versione è in stato NON DRAFT (WAITING_FOR_APPROVAL, ACTIVE, SUSPENDED, ARCHIVED), alla richiesta di lettura da parte dell’erogatore, va a buon fine
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
    When l'utente richiede la lettura della finalità
    Then si ottiene status code 200

    Examples: 
      | statoFinalita        |
      | ACTIVE               |
      | SUSPENDED            |
      | WAITING_FOR_APPROVAL |
      | ARCHIVED             |

  @sad-path
  @purpose_read3 @resource_intensive @wait_for_clarification @PIN-4805
  Scenario Outline: Per una finalità precedentemente creata da un fruitore, la quale prima versione è in qualsiasi stato (DRAFT, WAITING_FOR_APPROVAL, ACTIVE, SUSPENDED, ARCHIVED), alla richiesta di lettura da parte di un ente che non è né l'erogatore, né il fruitore, va a buon fine ma non ottiene l'analisi del rischio
    Given l'utente è un "admin" di "GSP"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
    When l'utente richiede la lettura della finalità
    Then si ottiene status code 403

    Examples: 
      | statoFinalita        |
      | DRAFT                |
      | ACTIVE               |
      | SUSPENDED            |
      | WAITING_FOR_APPROVAL |
      | ARCHIVED             |
