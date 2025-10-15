@eservice
Feature: Lettura di un e-service
  Tutti gli utenti autorizzati di enti erogatori possono leggere un proprio e-service

  @eservice_read1
  Scenario Outline: Per un e-service precedentemente creato dall’ente, il quale non ha descrittori, la richiesta per ottenere i dettagli dell'e-service va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in DRAFT
    When l'utente richiede la lettura di quell'e-service
    Then si ottiene status code <risultato>

    @happy-path
    Examples: 
      | ente | ruolo        | risultato |
      | GSP  | admin        |       200 |
      | GSP  | api          |       200 |
      | GSP  | api,security |       200 |
      | GSP  | support      |       200 |
      | PA1  | admin        |       200 |
      | PA1  | api          |       200 |
      | PA1  | api,security |       200 |
      | PA1  | support      |       200 |

    @sad-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | security     |       404 |
      | PA1  | security     |       404 |

  Scenario: [ES-READ-02] Un e-service può essere visionato anche da un ente diverso dal creatore
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente è un "admin" di "PA2"

    When l'utente richiede la lettura di quell'e-service
    Then si ottiene status code 200

    When l'utente richiede la lettura di quel descrittore
    Then si ottiene status code 200