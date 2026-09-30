@purpose @nuovi-operatori
Feature: Verifiche legacy della feature Nuovi Operatori su finalita senza workflow revisione

  # PST: Scenario 22 - Caso 22.1
  Scenario Outline: [LEGACY_WORKFLOW_22_1_STATO_VARIABILE] Verifica assenza workflow di revisione su finalita legacy
	Given "PA1" ha già creato e pubblicato 1 e-service
	And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
	And "PA2" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
	When l'utente è un "admin" di "PA2"
	And l'utente richiede la lettura della finalità
	Then si ottiene status code 200
	And non sussiste alcun workflow di revisione in corso

	Examples:
	  | statoFinalita |
	  | DRAFT         |
	  | ACTIVE        |
	  | SUSPENDED     |

