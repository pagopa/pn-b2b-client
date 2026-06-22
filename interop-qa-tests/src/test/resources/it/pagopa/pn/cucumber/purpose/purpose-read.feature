@purpose
Feature: Lettura singola finalità
  Tutti gli utenti possono leggere una finalità, l'analisi del rischio è disponibile solo per admin fruitori o erogatori di quella finalità.

  @happy-path
  @nrt-minimal
  @purpose_read1
  @wait_for_fix
  # Ticket aperto https://pagopa.atlassian.net/browse/PIN-7087
  Scenario Outline: [LETTURA_FINALITA_1] Per una finalità precedentemente creata dal fruitore, la quale prima versione è in qualsiasi stato (DRAFT, WAITING_FOR_APPROVAL, ACTIVE, SUSPENDED, ARCHIVED), alla richiesta di lettura, va a buon fine, l’analisi del rischio è disponibile solo per gli admin
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
  @nrt-minimal
  @purpose_read2 @no-parallel
  Scenario Outline: [LETTURA_FINALITA_2] Per una finalità precedentemente creata da un fruitore, la quale prima versione è in stato NON DRAFT (WAITING_FOR_APPROVAL, ACTIVE, SUSPENDED, ARCHIVED), alla richiesta di lettura da parte dell’erogatore, va a buon fine
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
  @nrt-minimal
  @purpose_read3 @resource_intensive @wait_for_clarification @PIN-4805
  Scenario Outline: [LETTURA_FINALITA_3] Per una finalità precedentemente creata da un fruitore, la quale prima versione è in qualsiasi stato (DRAFT, WAITING_FOR_APPROVAL, ACTIVE, SUSPENDED, ARCHIVED), alla richiesta di lettura da parte di un ente che non è né l'erogatore, né il fruitore, va a buon fine ma non ottiene l'analisi del rischio
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

  # Ticket aperto: https://pagopa.atlassian.net/browse/PIN-10265
  @adeguamento-analisi-rischio
  # TODO in tutte le descrizioni degli scenari di questa feature si sta descrivendo quello che si tenta di fare senza parlare dei risultati attesi. Correggere.
  # NOTE 04/06/2026: al momento l'analisi del rischio viene restituita anche per ruolo API. Chiesto conferma https://pagopaspa.slack.com/archives/C069AP16WG7/p1780565330577099
  Scenario Outline: [LETTURA_FINALITA_TK_1] A seguito del cambiamento di tenant kind si tenta di reperire una finalità attiva
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "<ente>" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind>"
    When l'utente è un "admin" di "<ente>"
    And l'utente richiede la lettura della finalità
    Then si ottiene status code 200
    When l'utente è un "api" di "<ente>"
    And l'utente richiede la lettura della finalità
    Then si ottiene status code 200
    Examples:
      | ente    | kind        |
      | PA4     | PRIVATE     |
      | PA4     | GSP         |
      | GSP2    | PA          |
      | Privato | PA          |