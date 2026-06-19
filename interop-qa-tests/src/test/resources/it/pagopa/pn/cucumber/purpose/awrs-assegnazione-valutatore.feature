@purpose @nuovi-operatori
Feature: Assegnazione reviewer in modalita AdminWritesReviewerSigns
  Un amministratore assegna i reviewer per l'analisi del rischio associata a una finalita.

  # PST: Scenario 16 - Caso 16.1
  Scenario: [AWRS_ASSEGNAZIONE_16_1_ADMIN] Assegnazione reviewer con utente admin (positivo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns"
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "DRAFT"

  # PST: Scenario 16 - Caso 16.1
  @sad-path
  Scenario: [AWRS_ASSEGNAZIONE_16_1_NON_ADMIN] Assegnazione reviewer con utente non amministratore (negativo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "api" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns"
    Then si ottiene status code 403



