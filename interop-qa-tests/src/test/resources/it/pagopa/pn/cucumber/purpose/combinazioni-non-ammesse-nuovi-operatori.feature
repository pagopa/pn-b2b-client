# NOTA: molti degli scenari di questo file potrebbero essere compattati in degli Scenari Outline introducendo step del tipo "esiste un workflow di revisione della finalità in stato ..."
@purpose @nuovi-operatori
Feature: Test di operazioni non ammesse per i nuovi operatori

  # PST: Scenario 23 - Caso 23.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_23_1_DRAFT] Tentativo assegnazione reviewer con workflow gia esistente in stato DRAFT (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And lo stato della compilazione dell'analisi del rischio è "DRAFT"
	When l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns"
	Then si ottiene status code 409

  # PST: Scenario 23 - Caso 23.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_23_1_ASSIGNED] Tentativo assegnazione reviewer con workflow gia esistente in stato ASSIGNED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
	And lo stato della compilazione dell'analisi del rischio è "ASSIGNED"
	When l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns"
	Then si ottiene status code 409

  # PST: Scenario 23 - Caso 23.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_23_1_SUBMITTED] Tentativo assegnazione reviewer con workflow gia esistente in stato SUBMITTED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And lo stato della compilazione dell'analisi del rischio è "SUBMITTED"
	When l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns"
	Then si ottiene status code 409

  # PST: Scenario 23 - Caso 23.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_23_1_SIGNED] Tentativo assegnazione reviewer con workflow gia esistente in stato SIGNED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
	When l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns"
	Then si ottiene status code 409

  # PST: Scenario 23 - Caso 23.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_23_1_REJECTED] Tentativo assegnazione reviewer con workflow gia esistente in stato REJECTED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And un reviewer assegnato rifiuta l'analisi del rischio con successo
	When l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns"
	Then si ottiene status code 409

  # PST: Scenario 24 - Caso 24.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_24_1_SUBMITTED] Tentativo compilazione reviewer con workflow in stato SUBMITTED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	When il valutatore assegnato compila l'analisi del rischio della finalità
	Then si ottiene status code 409

  # PST: Scenario 24 - Caso 24.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_24_1_SIGNED] Tentativo compilazione reviewer con workflow in stato SIGNED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
	When il valutatore assegnato compila l'analisi del rischio della finalità
	Then si ottiene status code 409

  # PST: Scenario 24 - Caso 24.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_24_1_REJECTED] Tentativo compilazione reviewer con workflow in stato REJECTED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And un reviewer assegnato rifiuta l'analisi del rischio con successo
	When il valutatore assegnato compila l'analisi del rischio della finalità
	Then si ottiene status code 409

  # PST: Scenario 25 - Caso 25.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_25_1_DRAFT] Tentativo rifiuto reviewer con workflow in stato DRAFT (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	When un reviewer assegnato rifiuta l'analisi del rischio
	Then si ottiene status code 409

  # PST: Scenario 25 - Caso 25.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_25_1_SIGNED] Tentativo rifiuto reviewer con workflow in stato SIGNED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
	When un reviewer assegnato rifiuta l'analisi del rischio
	Then si ottiene status code 409

  # PST: Scenario 25 - Caso 25.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_25_1_REJECTED] Tentativo rifiuto reviewer con workflow in stato REJECTED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And un reviewer assegnato rifiuta l'analisi del rischio con successo
	When un reviewer assegnato rifiuta l'analisi del rischio
	Then si ottiene status code 409

  # PST: Scenario 26 - Caso 26.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_26_1_DRAFT] Tentativo firma reviewer con workflow in stato DRAFT (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	When il valutatore assegnato convalida l'analisi del rischio della finalità
	Then si ottiene status code 409

  # PST: Scenario 26 - Caso 26.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_26_1_SIGNED] Tentativo firma reviewer con workflow in stato SIGNED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
	When il valutatore assegnato convalida l'analisi del rischio della finalità
	Then si ottiene status code 409

  # PST: Scenario 26 - Caso 26.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_26_1_REJECTED] Tentativo firma reviewer con workflow in stato REJECTED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And un reviewer assegnato rifiuta l'analisi del rischio con successo
	When il valutatore assegnato convalida l'analisi del rischio della finalità
	Then si ottiene status code 409

  # PST: Scenario 27 - Caso 27.1
  # KO ticket aperto https://pagopa.atlassian.net/browse/PIN-10441
  @nuovi-operatori-ko
  @sad-path
  Scenario: [COMB_NON_AMMESSE_27_1_DRAFT] Tentativo attivazione admin con workflow in stato DRAFT (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	When l'utente attiva la finalità in stato "DRAFT" per quell'e-service
	Then si ottiene status code 409

  # PST: Scenario 27 - Caso 27.1
  # KO ticket aperto https://pagopa.atlassian.net/browse/PIN-10441
  @nuovi-operatori-ko
  @sad-path
  Scenario: [COMB_NON_AMMESSE_27_1_ASSIGNED] Tentativo attivazione admin con workflow in stato ASSIGNED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
	When l'utente attiva la finalità in stato "DRAFT" per quell'e-service
	Then si ottiene status code 409

  # PST: Scenario 27 - Caso 27.1
  # KO ticket aperto https://pagopa.atlassian.net/browse/PIN-10441
  @nuovi-operatori-ko
  @sad-path
  Scenario: [COMB_NON_AMMESSE_27_1_SUBMITTED] Tentativo attivazione admin con workflow in stato SUBMITTED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	When l'utente attiva la finalità in stato "DRAFT" per quell'e-service
	Then si ottiene status code 409

  # PST: Scenario 27 - Caso 27.1
  # KO ticket aperto https://pagopa.atlassian.net/browse/PIN-10441
  @nuovi-operatori-ko
  @sad-path
  Scenario: [COMB_NON_AMMESSE_27_1_REJECTED] Tentativo attivazione admin con workflow in stato REJECTED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And un reviewer assegnato rifiuta l'analisi del rischio con successo
	When l'utente attiva la finalità in stato "DRAFT" per quell'e-service
	Then si ottiene status code 409

  # PST: Scenario 28 - Caso 28.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_28_1_ASSIGNED] Tentativo compilazione admin con workflow in stato ASSIGNED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
	When compila l'analisi del rischio tramite endpoint generico introducendo una variazione
	Then si ottiene status code 409

  # PST: Scenario 28 - Caso 28.1
  # OK ticket chiuso https://pagopa.atlassian.net/browse/PIN-10435
  @sad-path
  Scenario: [COMB_NON_AMMESSE_28_1_SUBMITTED] Tentativo compilazione admin con workflow in stato SUBMITTED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	When compila l'analisi del rischio tramite endpoint generico introducendo una variazione
	Then si ottiene status code 409

  # PST: Scenario 28 - Caso 28.1
  # OK ticket chiuso https://pagopa.atlassian.net/browse/PIN-10438
  @sad-path
  Scenario: [COMB_NON_AMMESSE_28_1_SIGNED] Tentativo compilazione admin con workflow in stato SIGNED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
	When compila l'analisi del rischio tramite endpoint generico introducendo una variazione
	Then si ottiene status code 409

  # PST: Scenario 29 - Caso 29.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_29_1_ASSIGNED] Tentativo submit admin con workflow in stato ASSIGNED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
	When l'utente invia il submit dell'analisi del rischio della finalità
	Then si ottiene status code 409

  # PST: Scenario 29 - Caso 29.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_29_1_SUBMITTED] Tentativo submit admin con workflow in stato SUBMITTED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	When l'utente invia il submit dell'analisi del rischio della finalità
	Then si ottiene status code 409

  # PST: Scenario 29 - Caso 29.1
  @sad-path
  Scenario: [COMB_NON_AMMESSE_29_1_SIGNED] Tentativo submit admin con workflow in stato SIGNED (negativo)
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
	When l'utente invia il submit dell'analisi del rischio della finalità
	Then si ottiene status code 409

