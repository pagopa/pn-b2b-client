@purpose @nuovi-operatori
Feature: Messa in atto dei flusso AdminWritesReviewerSigns della feature Nuovi Operatori.

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
     And compila l'analisi del rischio tramite endpoint generico con successo
     And l'utente invia il submit dell'analisi del rischio della finalità con successo
     And un reviewer assegnato rifiuta l'analisi del rischio con successo
     When l'utente è un "admin" di "PA2"
     And compila l'analisi del rischio tramite endpoint generico
     Then si ottiene status code 200
     And lo stato della compilazione dell'analisi del rischio è "REJECTED"

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
     And compila l'analisi del rischio tramite endpoint generico con successo
     And l'utente invia il submit dell'analisi del rischio della finalità con successo
     And un reviewer assegnato rifiuta l'analisi del rischio con successo
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

  # PST: Scenario 20 - Caso 20.1
  @happy-path
  Scenario: [AWRS_FIRMA_20_1_REVIEWER_ASSEGNATO] Firma dell'analisi del rischio da parte del reviewer assegnato (positivo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    And l'utente invia il submit dell'analisi del rischio della finalità
    When il valutatore assegnato convalida l'analisi del rischio della finalità
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "SIGNED"

   # PST: Scenario 21 - Caso 21.1
   @happy-path
   Scenario: [AWRS_ATTIVAZIONE_21_1_ADMIN] Attivazione finalità da parte dell'amministratore dopo firma reviewer (positivo)
     Given "PA1" ha già creato e pubblicato 1 e-service
     And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
     And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
     And l'utente è un "admin" di "PA2"
     And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
     And compila l'analisi del rischio tramite endpoint generico con successo
     And l'utente invia il submit dell'analisi del rischio della finalità con successo
     And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
     When l'utente è un "admin" di "PA2"
     And l'utente attiva la finalità in stato "DRAFT" per quell'e-service
     Then si ottiene status code 200 e la finalità in stato "ACTIVE"
