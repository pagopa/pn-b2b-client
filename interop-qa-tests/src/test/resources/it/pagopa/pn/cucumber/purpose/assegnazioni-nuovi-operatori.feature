# NOTA: considerare di compattare gli scenari di questo file in uno Scenario Outline
@purpose @nuovi-operatori
Feature: Consultazione delle assegnazioni e modifica della finalità per i Nuovi Operatori

  # PST: Scenario 31 - Caso 31.1
  @happy-path
  Scenario: [AWRS_MODIFICA_31_1_SUBMITTED] Modifica parti della finalità con workflow di revisione in stato SUBMITTED
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	When l'utente è un "admin" di "PA2"
	And l'utente aggiorna il titolo della finalità
	Then si ottiene status code 200
	And il titolo della finalità è stato aggiornato correttamente
	And lo stato della compilazione dell'analisi del rischio è "SUBMITTED"

  # PST: Scenario 31 - Caso 31.1
  @happy-path
  Scenario: [AWRS_MODIFICA_31_1_SIGNED] Modifica parti della finalità con workflow di revisione in stato SIGNED
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	And compila l'analisi del rischio tramite endpoint generico
	And l'utente invia il submit dell'analisi del rischio della finalità
	And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
	When l'utente è un "admin" di "PA2"
	And l'utente aggiorna il titolo della finalità
	Then si ottiene status code 200
	And il titolo della finalità è stato aggiornato correttamente
	And lo stato della compilazione dell'analisi del rischio è "SIGNED"

