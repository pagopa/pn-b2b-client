@eservice
Feature: Lettura di un'analisi del rischio di un eservice
  Tutti gli utenti autenticati possono leggere l'analisi del rischio di un eservice

  @happy-path
  @nrt-minimal
  @eservice_risk_analysis_read1
  Scenario Outline: [ESERVICE_RISK_ANALYSIS_READ_01] Per un e-service precedentemente creato e pubblicato in modalità "RECEIVE", alla richiesta di lettura di una sua analisi del rischio, l'operazione va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "PUBLISHED"
    When l'utente legge un'analisi del rischio di quell'e-service
    Then si ottiene status code 200

    Examples: 
      | ente | ruolo        |
      | GSP  | admin        |
      | GSP  | api          |
      | GSP  | security     |
      | GSP  | api,security |
      | GSP  | support      |
      | PA1  | admin        |
      | PA1  | api          |
      | PA1  | security     |
      | PA1  | api,security |
      | PA1  | support      |

  # Ticket aperto: https://pagopa.atlassian.net/browse/PIN-10265
  @adeguamento-analisi-rischio
  Scenario Outline: [DESCRIPTOR_TK_RA_READ_1] A seguito del cambiamento di tenant kind si tenta di reperire una risk analysis associata a un proprio e-service
    Given l'utente è un "admin" di "<ente>"
    And "<ente>" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "PUBLISHED"
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind>"
    When l'utente legge un'analisi del rischio di quell'e-service
    Then si ottiene status code 200
    Examples:
      | ente    | kind        |
      | PA4     | PRIVATE     |
      | PA4     | GSP         |
      | GSP2    | PA          |
      | Privato | PA          |
