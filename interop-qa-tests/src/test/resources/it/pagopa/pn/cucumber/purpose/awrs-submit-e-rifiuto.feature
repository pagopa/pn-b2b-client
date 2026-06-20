@purpose @nuovi-operatori
Feature: Submit e Rifiuto in modalita AdminWritesReviewerSigns
  Un amministratore invia il submit dell'analisi del rischio e un reviewer la rifiuta.

  # PST: Scenario 18 - Caso 18.1
  @happy-path
  Scenario: [AWRS_SUBMIT_18_1_ADMIN] Submit dell'amministratore (positivo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    When l'utente invia il submit dell'analisi del rischio della finalità
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "SUBMITTED"

  # PST: Scenario 18 - Caso 18.1
  @sad-path
  Scenario Outline: [AWRS_SUBMIT_18_1_NON_ADMIN] Submit da parte di utente non amministratore (negativo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    When l'utente è un "<ruolo>" di "PA2"
    And l'utente invia il submit dell'analisi del rischio della finalità
    Then si ottiene status code 403

    Examples:
      | ruolo    |
      | api      |
      | reviewer |

  # PST: Scenario 18 - Caso 18.2
  @happy-path
  Scenario: [AWRS_SUBMIT_18_2_CON_VARIAZIONE] Submit con variazione nell'analisi del rischio (positivo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    When l'utente invia il submit dell'analisi del rischio della finalità introducendo una variazione
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "SUBMITTED"
    And la variazione nell'analisi del rischio è stata persistita

  # PST: Scenario 18 - Caso 18.3
  @happy-path
  Scenario: [AWRS_SUBMIT_18_3_DOPO_RIFIUTO] Submit dopo rifiuto e nuova compilazione (positivo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    And l'utente invia il submit dell'analisi del rischio della finalità
    And un reviewer assegnato rifiuta l'analisi del rischio
    And l'utente è un "admin" di "PA2"
    And compila l'analisi del rischio tramite endpoint generico
    When l'utente invia il submit dell'analisi del rischio della finalità
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "SUBMITTED"


  # PST: Scenario 19 - Caso 19.1
  @happy-path
  Scenario: [AWRS_RIFIUTO_19_1_REVIEWER_ASSEGNATO] Rifiuto dell'analisi del rischio da parte del reviewer assegnato (positivo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    And l'utente invia il submit dell'analisi del rischio della finalità
    When un reviewer assegnato rifiuta l'analisi del rischio
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "REJECTED"

  # PST: Scenario 19 - Caso 19.1
  @sad-path
  Scenario: [AWRS_RIFIUTO_19_1_SENZA_MOTIVAZIONE] Rifiuto senza motivazione (negativo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    And l'utente invia il submit dell'analisi del rischio della finalità
    When un reviewer assegnato tenta di rifiutare l'analisi del rischio senza motivazione
    Then si ottiene status code 400



