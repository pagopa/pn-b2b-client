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

  # PST: Scenario 17 - Caso 17.1
  Scenario: [AWRS_COMPILAZIONE_17_1_ADMIN] Compilazione analisi del rischio da parte dell'amministratore (positivo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    When compila l'analisi del rischio tramite endpoint generico
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "DRAFT"

  # PST: Scenario 17 - Caso 17.1
  @sad-path
  Scenario: [AWRS_COMPILAZIONE_17_1_NON_ADMIN] Compilazione analisi del rischio da parte di utente non amministratore (negativo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    When l'utente è un "api" di "PA2"
    And compila l'analisi del rischio tramite endpoint generico
    Then si ottiene status code 403

  # PST: Scenario 17 - Caso 17.1
  @sad-path
  Scenario: [AWRS_COMPILAZIONE_17_1_REVIEWER] Compilazione analisi del rischio da parte di reviewer (negativo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    When l'utente è un "reviewer" di "PA2"
    And compila l'analisi del rischio tramite endpoint generico
    Then si ottiene status code 403

  # PST: Scenario 17 - Caso 17.1
  @sad-path
  Scenario: [AWRS_COMPILAZIONE_17_1_ENDPOINT_NON_PREVISTO] Compilazione analisi del rischio tramite endpoint non previsto (negativo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    When compila l'analisi del rischio della finalità
    Then si ottiene status code 403

  # TO TEST: l'ultima prova effettuava riscontrava un problema invalidante allo step -> Given "PA1" ha già creato e pubblicato 1 e-service
  # per cui non veniva mai trovato l'e-service appena creato (continuo 404).
  # PST: Scenario 17 - Caso 17.2
  Scenario: [AWRS_COMPILAZIONE_17_2_DOPO_RIFIUTO] Nuova compilazione amministratore dopo rifiuto reviewer (positivo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    And l'utente invia il submit dell'analisi del rischio della finalità
    And un reviewer assegnato rifiuta l'analisi del rischio
    When l'utente è un "admin" di "PA2"
    And compila l'analisi del rischio tramite endpoint generico
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "REJECTED"



