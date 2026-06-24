@purpose @nuovi-operatori
Feature: Messa in atto dei flusso AdminWritesReviewerSigns della feature Nuovi Operatori.

  # OK
  # PST: Scenario 11 - Caso 11.1
  @happy-path
  Scenario: [RWRS_ASSEGNAZIONE_11_1_RUOLO] Assegnazione valutatore con utente admin (positivo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns"
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "ASSIGNED"

  # OK
  # PST: Scenario 11 - Caso 11.1
  @sad-path
  Scenario: [RWRS_ASSEGNAZIONE_11_1_RUOLO_NON_AUTORIZZATO] Assegnazione valutatore con utente non amministratore (negativo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "api" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns"
    Then si ottiene status code 403

  # KO ticket aperto https://pagopa.atlassian.net/browse/PIN-10404
  # PST: Scenario 11 - Caso 11.1
  @nuovi-operatori-ko
  @sad-path
  Scenario Outline: [RWRS_ASSEGNAZIONE_11_1_RUOLO_DIVERSO_DA_VALUTATORE] Negativo: solo un utente con ruolo Valutatore può essere assegnato alla revisione
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" specificando un utente con ruolo "<ruolo>"
    Then si ottiene status code 400
    Examples:
      | ruolo  |
      | admin  |
      | api    |

  # OK
  # PST: Scenario 11 - Caso 11.1
  Scenario: [RWRS_ASSEGNAZIONE_11_1_MODALITA_MANCANTE] Assegnazione valutatore senza modalità
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità senza specificare la modalità
    Then si ottiene status code 400

  # OK
  # PST: Scenario 11 - Caso 11.1
  Scenario: [RWRS_ASSEGNAZIONE_11_1_VALUTATORE_MANCANTE] Assegnazione valutatore senza utenti valutatori
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" senza specificare utenti valutatori
    Then si ottiene status code 400

  # PST: Scenario 11 - Caso 11.1
  # 19/06/2026 Al momento impossibile da eseguire poiché si ha a disposizione una sola utenza reviewer
  @wait_for_fix
  Scenario: [RWRS_ASSEGNAZIONE_11_1_MULTIPLO] Assegnazione valutatore con più utenti valutatori
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" specificando più di un utente valutatore
    Then si ottiene status code 400

  # OK
  # PST: Scenario 12 - Caso 12.1
  Scenario: [RWRS_COMPILAZIONE_12_1_REVISORE] Compilazione analisi del rischio da parte del valutatore assegnato (positivo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
    When l'utente è un "admin" di "PA2"
    And il valutatore assegnato compila l'analisi del rischio della finalità
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "ASSIGNED"

  # OK
  # PST: Scenario 12 - Caso 12.1
  Scenario: [RWRS_COMPILAZIONE_12_1_NON_REVIEWER] Compilazione analisi del rischio da parte di utente non reviewer (negativo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
    When l'utente è un "admin" di "PA2"
    And compila l'analisi del rischio della finalità
    Then si ottiene status code 403

  # PST: Scenario 12 - Caso 12.1
  # 19/06/2026 Al momento impossibile da eseguire poiché si ha a disposizione una sola utenza reviewer
  @wait_for_fix
  Scenario: [RWRS_COMPILAZIONE_12_1_REVIEWER_NON_ASSEGNATO] Compilazione analisi del rischio da parte di reviewer non assegnato (negativo)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
    When l'utente è il numero 2 ad avere ruolo "reviewer" di "PA2"
    And compila l'analisi del rischio della finalità
    Then si ottiene status code 400

   # PST: Scenario 12 - Caso 12.1
   # OK ticket chiuso https://pagopa.atlassian.net/browse/PIN-10405
   Scenario: [RWRS_COMPILAZIONE_12_1_ENDPOINT_GENERICO] Compilazione analisi del rischio tramite endpoint generico (negativo)
     Given "PA1" ha già creato e pubblicato 1 e-service
     And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
     And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
     And l'utente è un "admin" di "PA2"
     And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
     When l'utente è un "admin" di "PA2"
     And compila l'analisi del rischio tramite endpoint generico introducendo una variazione
     Then si ottiene status code 409

   # PST: Scenario 12 - Caso 12.3
   @sad-path
   Scenario: [RWRS_COMPILAZIONE_12_3_SENZA_ASSEGNAZIONE] Tentativo compilazione senza valutatore assegnato (negativo)
     Given "PA1" ha già creato e pubblicato 1 e-service
     And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
     And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
     When l'utente è un "reviewer" di "PA2"
     And compila l'analisi del rischio della finalità
     Then si ottiene status code 404

   # PST: Scenario 13 - Caso 13.1
   @sad-path
   Scenario: [RWRS_RIFIUTO_13_1_PROPRIA_COMPILAZIONE] Il reviewer assegnato non può rifiutare la propria compilazione (negativo)
     Given "PA1" ha già creato e pubblicato 1 e-service
     And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
     And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
     And l'utente è un "admin" di "PA2"
     And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
     And il valutatore assegnato compila l'analisi del rischio della finalità con successo
     When il valutatore assegnato rifiuta la propria compilazione dell'analisi del rischio
     Then si ottiene status code 409
     And lo stato della compilazione dell'analisi del rischio è "ASSIGNED"

    # PST: Scenario 14 - Caso 14.1
    Scenario: [RWRS_CONVALIDA_14_1_POSITIVO] Il valutatore assegnato convalida l'analisi del rischio (positivo)
      Given "PA1" ha già creato e pubblicato 1 e-service
      And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
      And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
      And l'utente è un "admin" di "PA2"
      And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
      And il valutatore assegnato compila l'analisi del rischio della finalità con successo
      When il valutatore assegnato convalida l'analisi del rischio della finalità
      Then si ottiene status code 200
      And lo stato della compilazione dell'analisi del rischio è "SIGNED"

    # PST: Scenario 14 - Caso 14.1
    @sad-path
    Scenario: [RWRS_CONVALIDA_14_1_NON_VALUTATORE] Convalida analisi del rischio da parte di utente non valutatore (negativo)
      Given "PA1" ha già creato e pubblicato 1 e-service
      And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
      And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
      And l'utente è un "admin" di "PA2"
      And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
      And il valutatore assegnato compila l'analisi del rischio della finalità con successo
      When l'utente è un "admin" di "PA2"
      And l'utente convalida l'analisi del rischio della finalità
      Then si ottiene status code 403
      And lo stato della compilazione dell'analisi del rischio è "ASSIGNED"

    # PST: Scenario 14 - Caso 14.1
    # 19/06/2026 Al momento impossibile da eseguire poiché si ha a disposizione una sola utenza reviewer
    @wait_for_fix
    @sad-path
    Scenario: [RWRS_CONVALIDA_14_1_VALUTATORE_NON_ASSEGNATO] Convalida analisi del rischio da parte di valutatore non assegnato (negativo)
      Given "PA1" ha già creato e pubblicato 1 e-service
      And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
      And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
      And l'utente è un "admin" di "PA2"
      And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
      And il valutatore assegnato compila l'analisi del rischio della finalità con successo
      When l'utente è il numero 2 ad avere ruolo "reviewer" di "PA2"
      And l'utente convalida l'analisi del rischio della finalità
      Then si ottiene status code 400

    # PST: Scenario 14 - Caso 14.2
    # 19/06/2026 Al momento impossibile da eseguire poiché si ha a disposizione una sola utenza reviewer
    @wait_for_fix
    Scenario: [RWRS_CONVALIDA_14_2_ALTRO_REVIEWER_ASSEGNATO] Convalida analisi del rischio da parte di altro reviewer assegnato (positivo)
      Given "PA1" ha già creato e pubblicato 1 e-service
      And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
      And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
      And l'utente è un "admin" di "PA2"
      And l'utente assegna i reviewer previsti alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
      And uno dei reviewer assegnati compila l'analisi del rischio della finalità
      When un altro reviewer assegnato convalida l'analisi del rischio della finalità
      Then si ottiene status code 200
      And lo stato della compilazione dell'analisi del rischio è "SIGNED"

    # PST: Scenario 15 - Caso 15.1
    Scenario: [RWRS_ATTIVAZIONE_15_1_ADMIN] Attivazione finalità da parte di utente amministratore dopo convalida reviewer (positivo)
      Given "PA1" ha già creato e pubblicato 1 e-service
      And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
      And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
      And l'utente è un "admin" di "PA2"
      And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
      And il valutatore assegnato compila l'analisi del rischio della finalità con successo
      And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
      When l'utente è un "admin" di "PA2"
      And l'utente attiva la finalità in stato "DRAFT" per quell'e-service
      Then si ottiene status code 200 e la finalità in stato "ACTIVE"
      And lo stato della compilazione dell'analisi del rischio è "SIGNED"

    # PST: Scenario 15 - Caso 15.1
    @sad-path
    Scenario: [RWRS_ATTIVAZIONE_15_1_REVIEWER] Attivazione finalità da parte di utente reviewer (negativo)
      Given "PA1" ha già creato e pubblicato 1 e-service
      And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
      And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
      And l'utente è un "admin" di "PA2"
      And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
      And il valutatore assegnato compila l'analisi del rischio della finalità con successo
      And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
      When l'utente è un "reviewer" di "PA2"
      And l'utente attiva la finalità in stato "DRAFT" per quell'e-service
      Then si ottiene status code 403

    # PST: Scenario 15 - Caso 15.2
    Scenario: [RWRS_ATTIVAZIONE_15_2_ALTRO_ADMIN] Attivazione finalità da parte di un altro amministratore (positivo)
      Given "PA1" ha già creato e pubblicato 1 e-service
      And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
      And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
      And l'utente è un "admin" di "PA2"
      And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
      And il valutatore assegnato compila l'analisi del rischio della finalità con successo
      And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
      When l'utente è il numero 2 ad avere ruolo "admin" di "PA2"
      And l'utente attiva la finalità in stato "DRAFT" per quell'e-service
      Then si ottiene status code 200 e la finalità in stato "ACTIVE"
      And lo stato della compilazione dell'analisi del rischio è "SIGNED"

