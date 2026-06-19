@purpose @nuovi-operatori
Feature: Assegnazione valutatore in modalità ReviewerWritesReviewerSigns
  Un amministratore assegna il valutatore per l'analisi del rischio associata a una finalità.

  # PST: Scenario 11 - Caso 11.1
  Scenario Outline: [RWRS_ASSEGNAZIONE_11_1_RUOLO] Assegnazione valutatore con utente <ruolo>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "<ruolo>" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns"
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ruolo | risultato |
      | admin |       200 |

    @sad-path
    Examples:
      | ruolo | risultato |
      | api   |       403 |

  # PST: Scenario 11 - Caso 11.1
  Scenario: [RWRS_ASSEGNAZIONE_11_1_MODALITA_MANCANTE] Assegnazione valutatore senza modalità
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità senza specificare la modalità
    Then si ottiene status code 400

  # PST: Scenario 11 - Caso 11.1
  Scenario: [RWRS_ASSEGNAZIONE_11_1_VALUTATORE_MANCANTE] Assegnazione valutatore senza utenti valutatori
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" senza specificare utenti valutatori
    Then si ottiene status code 400

  # PST: Scenario 11 - Caso 11.1
  Scenario: [RWRS_ASSEGNAZIONE_11_1_MULTIPLO] Assegnazione valutatore con più utenti valutatori
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" specificando più di un utente valutatore
    Then si ottiene status code 400

  # PST: Scenario 12 - Caso 12.1
  Scenario: [RWRS_COMPILAZIONE_12_1_REVISORE] Compilazione analisi del rischio da parte del valutatore assegnato
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns"
    Then si ottiene status code 200
    When l'utente è un "admin" di "PA2"
    And il valutatore assegnato compila l'analisi del rischio della finalità
    Then si ottiene status code 200

