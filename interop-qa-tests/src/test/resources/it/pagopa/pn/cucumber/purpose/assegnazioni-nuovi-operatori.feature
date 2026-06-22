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
	And compila l'analisi del rischio tramite endpoint generico con successo
	And l'utente invia il submit dell'analisi del rischio della finalità con successo
	And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
	When l'utente è un "admin" di "PA2"
	And l'utente aggiorna il titolo della finalità
	Then si ottiene status code 200
	And il titolo della finalità è stato aggiornato correttamente
	And lo stato della compilazione dell'analisi del rischio è "SIGNED"

  # PST: Scenario 32 - Caso 32.1
  @happy-path
  Scenario: [AWRS_LETTURA_32_1_NESSUN_FILTRO] Lettura assegnazioni del valutatore senza filtri
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	# Seconda finalità: nuovo e-service + nuovo agreement + workflow SUBMITTED
	And "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
 	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
 	And compila l'analisi del rischio tramite endpoint generico con successo
 	And l'utente invia il submit dell'analisi del rischio della finalità con successo
 	When l'utente è un "reviewer" di "PA2"
 	And tenta di interrogare l'endpoint delle assegnazioni del valutatore senza filtri
 	Then si ottiene status code 200
 	And vengono restituite 2 finalità attese nelle assegnazioni

  # PST: Scenario 32 - Caso 32.1 (con filtro stato)
  @happy-path
  Scenario: [AWRS_LETTURA_32_1_FILTRO_STATO] Lettura assegnazioni del valutatore con filtro stato=ASSIGNED
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	# Seconda finalità: nuovo e-service + nuovo agreement + workflow SUBMITTED
	And "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
 	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
 	And compila l'analisi del rischio tramite endpoint generico con successo
 	And l'utente invia il submit dell'analisi del rischio della finalità con successo
 	When l'utente è un "reviewer" di "PA2"
 	And interroga l'endpoint delle assegnazioni del valutatore con filtro stato "ASSIGNED"
 	Then si ottiene status code 200
 	And viene restituita una sola finalità in stato "ASSIGNED" nelle assegnazioni

  # PST: Scenario 32 - Caso 32.1 (con parametro offset)
  @happy-path
  Scenario: [AWRS_LETTURA_32_1_OFFSET] Lettura assegnazioni del valutatore con parametro offset=1
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	# Seconda finalità: nuovo e-service + nuovo agreement + workflow SUBMITTED
	And "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
 	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
 	And compila l'analisi del rischio tramite endpoint generico con successo
 	And l'utente invia il submit dell'analisi del rischio della finalità con successo
 	When l'utente è un "reviewer" di "PA2"
 	And interroga l'endpoint delle assegnazioni del valutatore con parametro offset 1
 	Then si ottiene status code 200
 	And viene restituita una sola finalità nelle assegnazioni

  # PST: Scenario 32 - Caso 32.1 (con parametro limit)
  @happy-path
  Scenario: [AWRS_LETTURA_32_1_LIMIT] Lettura assegnazioni del valutatore con parametro limit=1
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	# Seconda finalità: nuovo e-service + nuovo agreement + workflow SUBMITTED
	And "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
 	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
 	And compila l'analisi del rischio tramite endpoint generico con successo
 	And l'utente invia il submit dell'analisi del rischio della finalità con successo
 	When l'utente è un "reviewer" di "PA2"
 	And interroga l'endpoint delle assegnazioni del valutatore con parametro limit 1
 	Then si ottiene status code 200
 	And viene restituita una sola finalità nelle assegnazioni

  # PST: Scenario 32 - Caso 32.1 (con filtro e-service)
  @happy-path
  Scenario: [AWRS_LETTURA_32_1_FILTRO_ESERVICE] Lettura assegnazioni del valutatore con filtro per e-service specifico
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
	# Seconda finalità: nuovo e-service + nuovo agreement + workflow SUBMITTED
	And "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
	And l'utente è un "admin" di "PA2"
 	And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
 	And compila l'analisi del rischio tramite endpoint generico con successo
 	And l'utente invia il submit dell'analisi del rischio della finalità con successo
 	When l'utente è un "reviewer" di "PA2"
 	And interroga l'endpoint delle assegnazioni del valutatore filtrando per il primo e-service creato
 	Then si ottiene status code 200
 	And viene restituita una sola finalità associata al primo e-service creato nelle assegnazioni

  # PST: Scenario 32 - Caso 32.2
  @happy-path
  Scenario: [AWRS_LETTURA_32_2_NESSUNA_ASSEGNAZIONE] Lettura assegnazioni quando valutatore non è assegnato a nessuna finalità
	When l'utente è un "reviewer" di "PA2"
	And tenta di interrogare l'endpoint delle assegnazioni del valutatore senza filtri
	Then si ottiene status code 200
	And la risposta contiene 0 risultati nelle assegnazioni

  # PST: Scenario 32 - Caso 32.3
  @sad-path
  Scenario: [AWRS_LETTURA_32_3_ACCESSO_NEGATO] Accesso negato per utente non valutatore all'endpoint delle assegnazioni
	When l'utente è un "api" di "PA2"
	And tenta di interrogare l'endpoint delle assegnazioni del valutatore senza filtri
	Then si ottiene status code 403
