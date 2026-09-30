@purpose @nuovi-operatori @nuovi-operatori-off
Feature: Funzionalità della feature Nuovi Operatori in caso di feature flag disabilitato

  # PST: Scenario 33 - Caso 33.1
  @sad-path
  Scenario: [FFOFF_ASSEGNAZIONE_33_1_ASSIGN_INATTIVA] Valutatore - API assign inattiva
	Given l'utente è un "admin" di "PA2"
	When l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns"
	Then si ottiene status code 501

  # PST: Scenario 33 - Caso 33.2
  @sad-path
  Scenario: [FFOFF_COMPILAZIONE_33_2_FORM_INATTIVA] Valutatore - API form inattiva
	Given l'utente è un "reviewer" di "PA2"
	When compila l'analisi del rischio della finalità
	Then si ottiene status code 501

  # PST: Scenario 33 - Caso 33.3
  @sad-path
  Scenario: [FFOFF_RIFIUTO_33_3_REJECT_INATTIVA] Valutatore - API reject inattiva
	Given l'utente è un "reviewer" di "PA2"
	When l'utente rifiuta l'analisi del rischio della finalità
	Then si ottiene status code 501

  # PST: Scenario 33 - Caso 33.4
  @sad-path
  Scenario: [FFOFF_CONVALIDA_33_4_SIGN_INATTIVA] Valutatore - API sign inattiva
	Given l'utente è un "reviewer" di "PA2"
	When l'utente convalida l'analisi del rischio della finalità
	Then si ottiene status code 501

  # PST: Scenario 33 - Caso 33.5
  @sad-path
  Scenario: [FFOFF_SUBMIT_33_5_SUBMIT_INATTIVA] Valutatore - API submit inattiva
	Given l'utente è un "admin" di "PA2"
	When l'utente invia il submit dell'analisi del rischio della finalità
	Then si ottiene status code 501

  # PST: Scenario 33 - Caso 33.6
  @sad-path
  Scenario: [FFOFF_LETTURA_33_6_ASSIGNMENTS_INATTIVA] Valutatore - API assignments inattiva
	Given l'utente è un "reviewer" di "PA2"
	When tenta di interrogare l'endpoint delle assegnazioni del valutatore senza filtri
	Then si ottiene status code 501
