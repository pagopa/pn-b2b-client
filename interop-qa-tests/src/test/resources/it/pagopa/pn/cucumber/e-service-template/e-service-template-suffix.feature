@e-service-template
@e-service-template-instances-suffix
Feature: Test API of e-service template suffix

  Scenario: [ESERVICE_SUFFIX_NRT_1] La creazione di un'istanza e-service da un template privo di instanceLabel genera un'istanza valida
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente è un "admin" di "PA1"
    And l'utente tenta la creazione di un nuovo e-service con suffisso "%null" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "%null" è utilizzato correttamente nell'e-service

  Scenario: [ESERVICE_SUFFIX_NRT_2] La modifica di un'istanza e-service creata da un template priva di instanceLabel va a buon fine
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    When l'utente è un "admin" di "PA1"
    And l'utente tenta la modifica dei campi dell'istanza dell'e-service template
    Then si ottiene response status code 200
    And il suffisso "%null" è utilizzato correttamente nell'e-service

  Scenario Outline: [ESERVICE_SUFFIX_AVAILABILITY_1] Nella creazione di un e-service da template il nome completo di quest’ultimo deve essere disponibile
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service - OK"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "%null" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente è un "admin" di "PA1"
    And l'utente tenta la creazione di un nuovo e-service con suffisso "<suffix>" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code <statusCode>

    Examples:
      | suffix | statusCode |
      | OK_2   | 200        |
      | OK     | 409        |
      | ok     | 409        |

  Scenario Outline: [ESERVICE_SUFFIX_AVAILABILITY_2] Nella creazione di un e-service da template il nome completo di quest’ultimo deve essere disponibile
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome " - Label1"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "%null" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome ""
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente è un "admin" di "PA1"
    And l'utente tenta la creazione di un nuovo e-service con suffisso "<suffix>" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code <statusCode>

    Examples:
      | suffix | statusCode |
      | Label2 | 200        |
      | Label1 | 409        |

  Scenario Outline: [ESERVICE_SUFFIX_CREATION_1] Verifica che il suffisso sia utilizzato correttamente a seguito della creazione dell'istanza e-service
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente tenta la creazione di un nuovo e-service con suffisso "<suffix>" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code <statusCode>
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "<suffix>" è utilizzato correttamente nell'e-service

    Examples:
      | suffix | statusCode |
      | %null  | 200        |
      | %blank | 200        |
      | ABC    | 200        |
      | 123    | 200        |

  Scenario: [ESERVICE_SUFFIX_CREATION_2] Verifica che il suffisso sia utilizzato correttamente a seguito della creazione dell'istanza e-service
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente tenta la creazione di un nuovo e-service con suffisso "" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 400

  Scenario: [ESERVICE_SUFFIX_CREATION_3] Verifica che il suffisso sia utilizzato correttamente a seguito della creazione dell'istanza e-service
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "%null" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "%null" è utilizzato correttamente nell'e-service
    When l'utente tenta la creazione di un nuovo e-service con suffisso "" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 400

  Scenario: [ESERVICE_SUFFIX_MAX_LENGTH_1] Verifica che il suffisso rispetti la lunghezza massima consentita
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente tenta la creazione di un nuovo e-service con suffisso "123456789012" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "123456789012" è utilizzato correttamente nell'e-service

  Scenario: [ESERVICE_SUFFIX_MAX_LENGTH_2] Verifica che il suffisso rispetti la lunghezza massima consentita
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente tenta la creazione di un nuovo e-service con suffisso "1234567890123" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 400

  Scenario Outline: [ESERVICE_SUFFIX_DRAFT_UPDATE_1] Verifica che l'istanza dell'e-service sia modificabile quando si trova in stato DRAFT
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT con suffisso "<initialSuffix>" a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "%actual" in stato DRAFT con "<suffix>"
    Then si ottiene response status code <statusCode>
    And il suffisso "<suffix>" è utilizzato correttamente nell'e-service

    Examples:
      | initialSuffix | suffix     | statusCode |
      | suffisso 1    | %null      | 200        |
      | suffisso 1    | suffisso 2 | 200        |
      | %null         | suffisso 2 | 200        |

  Scenario: [ESERVICE_SUFFIX_DRAFT_UPDATE_2] Verifica che l'istanza dell'e-service non sia modificabile quando si trova in stato PUBLISHED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso1" a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "%actual" usando l'endpoint di update per lo stato DRAFT con "suffisso2"
    Then si ottiene response status code 400

  Scenario Outline: [ESERVICE_SUFFIX_PUBLISHED_UPDATE_1] Verifica che l'istanza dell'e-service sia modificabile solo quando si trova in stato PUBLISHED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <eServiceState> con suffisso "<initialSuffix>" a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "<idEserviceTemplateInstance>" in stato PUBLISHED con "<suffix>"
    Then si ottiene response status code <statusCode>
    And il suffisso "<suffix>" è utilizzato correttamente nell'e-service

    Examples:
      | idEserviceTemplateInstance | eServiceState | initialSuffix | suffix       | statusCode |
      | %actual                    | PUBLISHED     | %null         | %null        | 200        |
      | %actual                    | PUBLISHED     | %null         | A            | 200        |
      | %actual                    | PUBLISHED     | %null         | ABCDEFGHILMN | 200        |
      | %actual                    | PUBLISHED     | %null         | Test@ - 123  | 200        |
      | %actual                    | PUBLISHED     | suffisso1     | %null        | 200        |
      | %actual                    | PUBLISHED     | suffisso1     | suffisso2    | 200        |

  Scenario Outline: [ESERVICE_SUFFIX_PUBLISHED_UPDATE_1.2] Verifica che l'istanza dell'e-service sia modificabile solo quando si trova in stato PUBLISHED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <eServiceState> con suffisso "<initialSuffix>" a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "<idEserviceTemplateInstance>" in stato PUBLISHED con "<suffix>"
    Then si ottiene response status code <statusCode>

    Examples:
      | idEserviceTemplateInstance | eServiceState | initialSuffix | suffix       | statusCode |
      | %actual                    | DRAFT         | %null         | %null        | 409        |
      | %random                    | PUBLISHED     | suffisso1     | suffisso2    | 404        |
      | %null                      | PUBLISHED     | suffisso1     | suffisso2    | 400        |

  Scenario: [ESERVICE_SUFFIX_PUBLISHED_UPDATE_2] Verifica che nella modifica di un'istanza di e-service PUBLISHED il nuovo suffisso non sia stato già utilizzato in un'altra istanza
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso 2" a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "%actual" in stato PUBLISHED con "suffisso 1"
    Then si ottiene response status code 409

  Scenario: [ESERVICE_SUFFIX_PUBLISHED_UPDATE_3] Verifica che non sia possibile modificare il suffisso di un'istanza di e-service PUBLISHED appartenente ad un ente differente dal chiamante
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    When l'utente è un "admin" di "PA2"
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "%actual" in stato PUBLISHED con "suffisso 2"
    Then si ottiene response status code 403

  Scenario Outline: [ESERVICE_SUFFIX_PUBLISHED_UPDATE_4] Verifica che l'istanza dell'e-service in stato PUBLISHED sia modificabile dipendentemente dal ruolo dell'utente chiamante
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    Given l'utente è un "<role>" di "PA1"
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "%actual" in stato PUBLISHED con "suffisso 2"
    Then si ottiene response status code 403

    Examples:
      | role     |
      | security |
      | support  |

  Scenario: [ESERVICE_SUFFIX_NAME_UPDATED] Verifica che la modifica del nome di un template e-service venga riportata correttamente nel nome delle relative istanze e-service
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT con suffisso "suffisso 2" a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta la modifica del nome dell'e-service template
    And si ottiene response status code 204
    And la modifica del nome dell'e-service template è stata effettuata correttamente
    Then il nome del "penultimo" e-service creato è stato aggiornato correttamente con il nome dell'e-service template e con il suffisso "suffisso 1"
    And il nome del "ultimo" e-service creato è stato aggiornato correttamente con il nome dell'e-service template e con il suffisso "suffisso 2"

  Scenario: [ESERVICE_SUFFIX_DIFFERENT_TEMPLATE_CREATION_1] Verifica che sia possibile creare un'istanza di e-service in stato DRAFT con suffisso già utilizzato in e-service appartenenti ad un altro template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service1"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service2"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service

  Scenario: [ESERVICE_SUFFIX_DIFFERENT_TEMPLATE_CREATION_2] Verifica che sia possibile modificare un'istanza di e-service in stato PUBLISHED con suffisso già utilizzato in e-service appartenenti ad un altro template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service1"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service
    And il nuovo e-service è stato creato correttamente in stato PUBLISHED
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service2"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso 2" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 2" è utilizzato correttamente nell'e-service
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "%actual" in stato PUBLISHED con "suffisso 1"
    Then il suffisso "suffisso 1" è utilizzato correttamente nell'e-service

  Scenario: [ESERVICE_SUFFIX_DIFFERENT_TENANT_CREATION_1] Verifica che sia possibile creare un e-service in stato DRAFT con lo stesso suffisso di un altro e-service appartenente ad un tenant differente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT con suffisso "suffisso 2" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 2" è utilizzato correttamente nell'e-service
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And si ottiene response status code 200
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "%actual" in stato DRAFT con "suffisso 1"
    Then il suffisso "suffisso 1" è utilizzato correttamente nell'e-service

  Scenario: [ESERVICE_SUFFIX_DIFFERENT_TENANT_CREATION_2] Verifica che sia possibile modificare un e-service in stato PUBLISHED usando lo stesso suffisso di un altro e-service appartenente ad un tenant differente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service
    And il nuovo e-service è stato creato correttamente in stato PUBLISHED
    And l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso 2" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 2" è utilizzato correttamente nell'e-service
    And il nuovo e-service è stato creato correttamente in stato PUBLISHED
    And si ottiene response status code 200
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template "%actual" in stato PUBLISHED con "suffisso 1"
    Then il suffisso "suffisso 1" è utilizzato correttamente nell'e-service

  Scenario: [ESERVICE_SUFFIX_MY_INSTANCES_RETRIEVE_1] Verifica che sia possibile recuperare le istanze e-service template in DRAFT appartenenti all'ente chiamante
    Given l'utente è un "admin" di "GSP"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service
    And l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT con suffisso "suffisso 2" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 2" è utilizzato correttamente nell'e-service
    When l'utente recupera le proprie istanze e-service template create dall'e-service template "%actual"
    Then ottengo solo l'ultimo e-service creato dall'ente prodotti dall'e-service template

  Scenario: [ESERVICE_SUFFIX_MY_INSTANCES_RETRIEVE_2] Verifica che sia possibile recuperare le istanze e-service template in PUBLISHED appartenenti all'ente chiamante
    Given l'utente è un "admin" di "GSP"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service
    And l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "suffisso 2" a partire dal template con successo indicando tutte le specifiche
    And il suffisso "suffisso 2" è utilizzato correttamente nell'e-service
    When l'utente recupera le proprie istanze e-service template create dall'e-service template "%actual"
    Then ottengo solo l'ultimo e-service creato dall'ente prodotti dall'e-service template

  Scenario Outline: [ESERVICE_SUFFIX_MY_INSTANCES_RETRIEVE_3] Verifica che sia possibile recuperare le istanze e-service template in DRAFT in delega dal delegato
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And l'utente effettua la creazione di un nuovo e-service in stato <stato> con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And l'ente "PA1" ha una delega attiva verso l'ente "PA2" per l'istanza dell'e-service template
    When l'utente è un "admin" di "<ente>"
    And l'utente recupera le proprie istanze e-service template create dall'e-service template "%actual"
    Then ottengo solo l'ultimo e-service creato dall'ente prodotti dall'e-service template
    Examples:
      | stato     | ente      |
      #test per delegato (in caso di cambiamenti, nb di modificare anche il valore indicato nello step di creazione e attivazione delega)
      | DRAFT     | PA2       |
      | PUBLISHED | PA2       |

      #test per delegante
      | DRAFT     | PA1       |
      | PUBLISHED | PA1       |

  Scenario Outline: [ESERVICE_SUFFIX_MY_INSTANCES_RETRIEVE_8] Verifica non sia possibile accedere alle istanze di un e-service template indicando un id non valido
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT con suffisso "suffisso 1" a partire dal template con successo indicando tutte le specifiche
    And l'utente recupera le proprie istanze e-service template create dall'e-service template "<eServiceTemplateId>"
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceTemplateId | statusCode |
      | %null              | 400        |
      | %random            | 404        |