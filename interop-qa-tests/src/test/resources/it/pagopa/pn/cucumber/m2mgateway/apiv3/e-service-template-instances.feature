@m2m-apiv3-eservice-template-instances
Feature: (M2M v3) Gestione delle istanze di e-service template

  @happy-path
  Scenario Outline: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_CREATION_1] La creazione di un nuovo e-service a partire da un template attivo può essere effettuata da un ente con ruolo M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di PUBLISHED
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m v3 tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie
    Then si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT

    Examples:
      | mode       |
      | erogazione |
      | ricezione  |

  @sad-path
  Scenario: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_CREATION_2] La creazione di un nuovo e-service a partire da un template attivo NON può essere effettuata da un ente con ruolo M2M
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente m2m v3 tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie
    Then si ottiene response status code 403

  @happy-path
  Scenario Outline: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_UPDATE_1] La modifica dei campi di un'istanza in stato DRAFT di un e-service template può essere effettuata da un ente con ruolo M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m v3 tenta la modifica dei campi dell'istanza dell'e-service template
    Then si ottiene response status code 200
    And i campi dell'istanza dell'e-service template sono stati modificati correttamente

    Examples:
      | mode       |
      | erogazione |
      | ricezione  |

  @sad-path
  Scenario: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_UPDATE_2] La modifica dei campi di un'istanza in stato DRAFT di un e-service template NON può essere effettuata da un ente con ruolo M2M
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente m2m v3 tenta la modifica dei campi dell'istanza dell'e-service template
    Then si ottiene response status code 403
