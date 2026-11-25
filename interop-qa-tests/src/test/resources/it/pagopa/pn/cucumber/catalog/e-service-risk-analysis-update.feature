@eservice
Feature: Aggiornamento di un'analisi del rischio ad un e-service
  Tutti gli utenti autenticati di enti erogatori possono aggiornare un'analisi del rischio ad un e-service se è in mode RECEIVE

  @nrt-minimal
  @eservice_risk_analysis_update1
  Scenario Outline: [ESERVICE_RISK_ANALYSIS_UPDATE_01] Per un e-service creato in modalità "RECEIVE", il quale non ha descrittori, è possibile aggiornare un'analisi del rischio precedentemente creata. L'analisi del rischio deve essere ben formattata ma non necessariamente completamente compilata. La richiesta va a buon fine se è un utente autorizzato
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service in modalità "RECEIVE" con un descrittore in DRAFT
    Given "<ente>" ha già aggiunto un'analisi del rischio a quell'e-service
    When l'utente aggiorna l'analisi del rischio di quell'e-service
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
  @eservice_risk_analysis_update2
  Scenario: [ESERVICE_RISK_ANALYSIS_UPDATE_02] Per un e-service creato in modalità "RECEIVE", il quale ha un solo descrittore in stato DRAFT, è possibile aggiornare un'analisi del rischio precedentemente creata. L'analisi del rischio deve essere ben formattata ma non necessariamente completamente compilata. La richiesta va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    Given "PA1" ha già aggiunto un'analisi del rischio a quell'e-service
    When l'utente aggiorna l'analisi del rischio di quell'e-service
    Then si ottiene status code 204
