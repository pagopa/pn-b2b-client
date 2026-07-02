@eservice
Feature: Aggiunta di un'analisi del rischio ad un e-service
  Tutti gli utenti autorizzati di enti erogatori possono aggiungere un'analisi del rischio all'e-service se è in mode RECEIVE

  @nrt-minimal
  @eservice_risk_analysis_addition1
  Scenario Outline: [ESERVICE_RISK_ANALYSIS_ADDITION_01] Per un e-service creato in modalità "RECEIVE", il quale ha un descrittore in DRAFT, è possibile inserire una nuova analisi del rischio. L'analisi del rischio deve essere ben formattata ma non necessariamente completamente compilata. La richiesta va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service in modalità "RECEIVE" con un descrittore in DRAFT
    When l'utente aggiunge un'analisi del rischio
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | admin        |       204 |
      | GSP  | api          |       204 |
      | GSP  | api,security |       204 |
      | PA1  | admin        |       204 |
      | PA1  | api          |       204 |
      | PA1  | api,security |       204 |

    @sad-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | security     |       403 |
      | GSP  | support      |       403 |
      | PA1  | security     |       403 |
      | PA1  | support      |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | reviewer     |       403 |
      | GSP  | viewer       |       403 |
      | PA2  | reviewer     |       403 |
      | PA2  | viewer       |       403 |

  @happy-path
  @nrt-minimal
  @eservice_risk_analysis_addition2
  Scenario: [ESERVICE_RISK_ANALYSIS_ADDITION_02] Per un e-service creato in modalità "RECEIVE", il quale ha un solo descrittore in stato DRAFT, è possibile inserire una nuova analisi del rischio. L'analisi del rischio deve essere ben formattata ma non necessariamente completamente compilata. La richiesta va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    When l'utente aggiunge un'analisi del rischio
    Then si ottiene status code 204

  @sad-path
  @nrt-minimal
  @eservice_risk_analysis_addition3
  Scenario: [ESERVICE_RISK_ANALYSIS_ADDITION_03] Per un e-service creato in modalità "DELIVER", il quale non ha descrittori, alla richiesta di inserimento di un'analisi del rischio, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in DRAFT
    When l'utente aggiunge un'analisi del rischio
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @eservice_risk_analysis_addition4
  Scenario: [ESERVICE_RISK_ANALYSIS_ADDITION_04] Per un e-service creato in modalità "DELIVER", il quale ha un solo descrittore in stato DRAFT, alla richiesta di inserimento di un'analisi del rischio, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "DRAFT"
    When l'utente aggiunge un'analisi del rischio
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @eservice_risk_analysis_addition5
  Scenario: [ESERVICE_RISK_ANALYSIS_ADDITION_05] Per un e-service creato in modalità "RECEIVE", il quale non ha descrittori, alla richiesta di inserimento di un'analisi del rischio ben formattata e dell'ultima versione per quella tipologia di ente ma della tipologia errata, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in DRAFT
    When l'utente aggiunge un'analisi del rischio non corretta per la tipologia di ente
    Then si ottiene status code 400

  @nrt-minimal
  @eservice_risk_analysis_addition6
  Scenario: [ESERVICE_RISK_ANALYSIS_ADDITION_06] Per un e-service creato in modalità "RECEIVE", il quale ha un solo descrittore in stato DRAFT, alla richiesta di inserimento di un'analisi del rischio ben formattata e della versione corretta per quella tipologia di ente ma della tipologia errata, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    When l'utente aggiunge un'analisi del rischio non corretta per la tipologia di ente
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @eservice_risk_analysis_addition7
  Scenario: [ESERVICE_RISK_ANALYSIS_ADDITION_07] Per un e-service creato in modalità "RECEIVE", il quale non ha descrittori, alla richiesta di inserimento di un'analisi del rischio ben formattata e della tipologia corretta per quella tipologia di ente ma in una versione che non è la “latest”, l’ultima disponibile, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in DRAFT
    When l'utente aggiunge un'analisi del rischio con versione template non aggiornata
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @eservice_risk_analysis_addition8
  Scenario: [ESERVICE_RISK_ANALYSIS_ADDITION_08] Per un e-service creato in modalità "RECEIVE", il quale ha un solo descrittore in stato DRAFT, alla richiesta di inserimento di un'analisi del rischio ben formattata e della tipologia corretta per quella tipologia di ente ma in una versione che non è la “latest”, l’ultima disponibile, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    When l'utente aggiunge un'analisi del rischio con versione template non aggiornata
    Then si ottiene status code 400

  # 05/06/2026: In fase di creazione, la risk analysis è sottoposta ad una fase di pre-convalida, che precede quella che poi verrà
  # fatta al momento della pubblicazione. In questo fase, se vengono rilevati - per esempio - degli attributi non
  # previsti, la creazione fallisce. Avendo le RA di tipo "PA" il campo in più "isRequestOnBehalfOfThirdParties"
  # rispetto ai tipi PRIVATE e GSP, è previsto che la generazione della RA fallisca già come bozza.
  # Inoltre, corrispondendo a GSP la versione "2.0" e a PA la versione "3.1", è previsto che anche il caso con kind
  # iniziale GSP fallisca.
  @adeguamento-analisi-rischio
  Scenario Outline: [DESCRIPTOR_TK_RA_ADD_1_A] A seguito del cambiamento di tenant kind si tenta di aggiungere una risk analysis coerente con il precedente tenant kind ad un proprio e-service in bozza
    Given l'utente è un "admin" di "<ente>"
    And "<ente>" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind_target>"
    When l'utente aggiunge un'analisi del rischio coerente con il tenant kind "<kind_iniziale>"
    Then si ottiene status code 400
    Examples:
      | ente    | kind_iniziale | kind_target |
      | PA4     | PA            | PRIVATE     |
      | PA4     | PA            | GSP         |
      | GSP2    | GSP           | PA          |

  # 05/06/2026: Diversamente da [DESCRIPTOR_TK_RA_ADD_1_A], tra "PRIVATE" e "PA" non ci sono incoerenze rilevabili
  # già in fase di bozza (essendo anche le due versioni di RA coincidenti con 3.1), per cui è previsto il successo.
  @adeguamento-analisi-rischio
  Scenario: [DESCRIPTOR_TK_RA_ADD_1_B] A seguito del cambiamento di tenant kind si tenta di aggiungere una risk analysis coerente con il precedente tenant kind ad un proprio e-service in bozza
    Given l'utente è un "admin" di "Privato"
    And "Privato" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    And il tenant kind dell'ente "Privato" viene impostato a "PA"
    When l'utente aggiunge un'analisi del rischio coerente con il tenant kind "PRIVATE"
    Then si ottiene status code 200

  @adeguamento-analisi-rischio
  Scenario Outline: [DESCRIPTOR_TK_RA_ADD_2] A seguito del cambiamento di tenant kind si tenta di aggiungere una risk analysis coerente con il nuovo tenant kind ad un proprio e-service in bozza
    Given l'utente è un "admin" di "<ente>"
    And "<ente>" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind_target>"
    When l'utente aggiunge un'analisi del rischio coerente con il tenant kind "<kind_target>"
    Then si ottiene status code 200
    Examples:
      | ente    | kind_target |
      | PA4     | PRIVATE     |
      | PA4     | GSP         |
      | GSP2    | PA          |
      | Privato | PA          |