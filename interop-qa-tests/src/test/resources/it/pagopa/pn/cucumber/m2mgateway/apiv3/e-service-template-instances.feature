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

  Scenario: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_LABEL_UPDATE_1] La modifica dell'etichetta di un'istanza in stato PUBLISHED di un e-service template può essere effettuata da un ente con ruolo M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso1" a partire dal template con successo indicando tutte le specifiche
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m v3 tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "%actual" in stato PUBLISHED con "suffisso2"
    Then si ottiene response status code 200
    And il suffisso "suffisso2" è utilizzato correttamente nell'e-service

  Scenario: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_LABEL_UPDATE_2] La modifica dell'etichetta di un'istanza in stato PUBLISHED di un e-service template NON può essere effettuata da un ente con ruolo M2M
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED con nome "E-Service"
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso1" a partire dal template con successo indicando tutte le specifiche
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente m2m v3 tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "%actual" in stato PUBLISHED con "suffisso2"
    Then si ottiene response status code 403

  Scenario Outline: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_DESCRIPTOR_CREATION_1] La creazione di una nuova versione in bozza di un'istanza di e-service template può essere effettuata da un ente con ruolo M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m v3 tenta la creazione di una nuova versione in bozza dell'istanza dell'e-service template utilizzando i dati del descrittore precedente
    Then si ottiene status code 200 e il descrittore contiene i campi del precedente

    Examples:
      | mode       |
      | erogazione |
      | ricezione  |

  Scenario: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_DESCRIPTOR_CREATION_2] La creazione di una nuova versione in bozza di un'istanza di e-service template NON può essere effettuata da un ente con ruolo M2M
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente m2m v3 tenta la creazione di una nuova versione in bozza dell'istanza dell'e-service template utilizzando i dati del descrittore precedente
    Then si ottiene response status code 403

  Scenario Outline: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_DESCRIPTOR_UPDATE_1] La modifica del descriptor in stato DRAFT di un'istanza di e-service template può essere effettuata da un ente con ruolo M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m v3 tenta la modifica del descriptor in stato DRAFT dell'istanza dell'e-service template
    Then si ottiene response status code 200
    And il descriptor dell'istanza in stato DRAFT dell'e-service template è stato modificato correttamente

    Examples:
      | mode       |
      | erogazione |
      | ricezione  |

  Scenario: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_DESCRIPTOR_UPDATE_2] La modifica del descriptor in stato DRAFT di un'istanza di e-service template NON può essere effettuata da un ente con ruolo M2M
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente m2m v3 tenta la modifica del descriptor in stato DRAFT dell'istanza dell'e-service template
    Then si ottiene response status code 403
