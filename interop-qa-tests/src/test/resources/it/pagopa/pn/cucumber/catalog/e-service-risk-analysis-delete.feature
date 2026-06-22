@eservice
Feature: Cancellazione di un'analisi del rischio ad un e-service
  Tutti gli utenti autorizzati di enti erogatori possono cancellare un'analisi del rischio di un e-service se è in mode RECEIVE

  @nrt-minimal
  @eservice_risk_analysis_delete1
  Scenario Outline: [ESERVICE_RISK_ANALYSIS_DELETE_01] Per un e-service creato in modalità "RECEIVE", con un descrittore in DRAFT, è possibile cancellare un'analisi del rischio precedentemente creata. L'analisi del rischio deve essere ben formattata ma non necessariamente completamente compilata. La richiesta va a buon fine se effettuata da un utente autorizzato
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service in modalità "RECEIVE" con un descrittore in DRAFT
    Given "<ente>" ha già aggiunto un'analisi del rischio a quell'e-service
    When l'utente cancella quell'analisi del rischio di quell'e-service
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

  @happy-path
  @nrt-minimal
  @eservice_risk_analysis_delete2
  Scenario: [ESERVICE_RISK_ANALYSIS_DELETE_02] Per un e-service creato in modalità "RECEIVE", il quale ha un solo descrittore in stato DRAFT, è possibile cancellare un'analisi del rischio precedentemente creata. L'analisi del rischio deve essere ben formattata ma non necessariamente completamente compilata. La richiesta va a buon fine se effettuata da un utente autorizzato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    Given "PA1" ha già aggiunto un'analisi del rischio a quell'e-service
    When l'utente cancella quell'analisi del rischio di quell'e-service
    Then si ottiene status code 204

  @adeguamento-analisi-rischio
  Scenario Outline: [DESCRIPTOR_TK_RA_DELETE_1] A seguito del cambiamento di tenant kind si tenta eliminare una risk analysis associata a un proprio e-service
    Given l'utente è un "admin" di "<ente>"
    And "<ente>" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    And "<ente>" ha già aggiunto un'analisi del rischio a quell'e-service
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind>"
    When l'utente cancella quell'analisi del rischio di quell'e-service
    Then si ottiene status code 200
    Examples:
      | ente    | kind        |
      | PA4     | PRIVATE     |
      | PA4     | GSP         |
      | GSP2    | PA          |
      | Privato | PA          |

  # FIXME utile solo a innescare re-allineamento dei tenant kinds, rimuovere
  @tenant-kind-alignment
  Scenario: allinea kinds
    Given l'utente è un "admin" di "PA4"

    # FIXME utile solo per debug locale, rimuovere
  @debug-adeguamento-analisi-rischio
  Scenario: Cambio kind manuale
    And il tenant kind dell'ente "PA4" viene impostato a "PRIVATE"
