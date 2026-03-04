@e-service-template
Feature: Test API of e-service template suffix

  @nrt-minimal
  Scenario: [ESERVICE_SUFFIX_NRT_1] La creazione di un'istanza e-service da un template privo di instanceLabel genera un'istanza valida
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente è un "admin" di "PA1"
    And l'utente tenta la creazione di un nuovo e-service con suffisso "" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "" è utilizzato correttamente nell'e-service

  @nrt-minimal
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
    And il suffisso "" è utilizzato correttamente nell'e-service

  Scenario Outline: [ESERVICE_SUFFIX_AVAILABILITY_1] Nella creazione di un e-service da template il nome completo di quest’ultimo deve essere disponibile
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service - OK"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "" a partire dal template indicando tutte le specifiche
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
      | OK     | 400        |
      | ok     | 400        |

  Scenario Outline: [ESERVICE_SUFFIX_AVAILABILITY_2] Nella creazione di un e-service da template il nome completo di quest’ultimo deve essere disponibile
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service - Label1"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente è un "admin" di "PA1"
    And l'utente tenta la creazione di un nuovo e-service con suffisso "<suffix>" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code <statusCode>

    Examples:
      | suffix  | statusCode |
      | Label_2 | 200        |
      | Label_1 | 400        |

  Scenario Outline: [ESERVICE_SUFFIX_CREATION_1] Verifica che il suffisso sia utilizzato correttamente a seguito della creazione dell'istanza e-service
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente tenta la creazione di un nuovo e-service con suffisso "<suffix>" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code <statusCode>
    And il suffisso "<suffix>" è utilizzato correttamente nell'e-service

    Examples:
      | suffix | statusCode |
      | %null  | 200        |
      | %space | 200        |
      |        | 200        |
      | ABC    | 200        |
      | 123    | 200        |

  Scenario: [ESERVICE_SUFFIX_CREATION_2] Verifica che il suffisso sia utilizzato correttamente a seguito della creazione dell'istanza e-service
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "%null" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    When l'utente tenta la creazione di un nuovo e-service con suffisso "" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 400

  Scenario Outline: [ESERVICE_SUFFIX_MAX_LENGTH] Verifica che il suffisso rispetti la lunghezza massima consentita
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente tenta la creazione di un nuovo e-service con suffisso "<suffix>" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code <statusCode>

    Examples:
      | suffix        | statusCode |
      | 123456789012  | 200        |
      | 1234567890123 | 400        |

  Scenario Outline: [ESERVICE_SUFFIX_DRAFT_UPDATE_1] Verifica che l'istanza dell'e-service sia modificabile quando si trova in stato DRAFT
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "<initialSuffix>" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template in stato DRAFT con "<suffix>"
    Then si ottiene response status code <statusCode>
    And il suffisso "" è utilizzato correttamente nell'e-service

    Examples:
      | initialSuffix | suffix     | statusCode |
      | suffisso 1    |            | 200        |
      | suffisso 1    | suffisso 2 | 200        |
      |               | suffisso 2 | 200        |
      | suffisso 1    | suffisso 1 | 400        |

  Scenario: [ESERVICE_SUFFIX_DRAFT_UPDATE_2] Verifica che l'istanza dell'e-service non sia modificabile quando si trova in stato PUBLISHED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "suffisso1" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template in stato DRAFT con "suffisso2"
    Then si ottiene response status code 400

  Scenario Outline: [ESERVICE_SUFFIX_PUBLISHED_UPDATE_1] Verifica che l'istanza dell'e-service sia modificabile solo quando si trova in stato PUBLISHED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "<initialSuffix>" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And l'utente effettua l'aggiunta di una versione in stato <eServiceState> all'e-service con successo
    And il nuovo e-service è stato creato correttamente in stato <eServiceState>
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template in stato PUBLISHED con "<suffix>"
    Then si ottiene response status code <statusCode>
    And il suffisso "<suffix>" è utilizzato correttamente nell'e-service

    Examples:
      | eServiceState | initialSuffix | suffix       | statusCode |
      | PUBLISHED     |               |              | 200        |
      | PUBLISHED     |               | A            | 200        |
      | PUBLISHED     |               | ABCDEFGHILMN | 200        |
      | PUBLISHED     |               | Test@ - 123  | 200        |
      | PUBLISHED     | suffisso1     |              | 200        |
      | PUBLISHED     | suffisso1     | suffisso2    | 200        |
      | DRAFT         |               |              | 400        |

  Scenario: [ESERVICE_SUFFIX_PUBLISHED_UPDATE_2] Verifica che nella modifica di un'istanza di e-service PUBLISHED il nuovo suffisso non sia stato già utilizzato in un'altra istanza
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "suffisso 1" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'utente tenta la creazione di un nuovo e-service con suffisso "suffisso 2" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template in stato PUBLISHED con "suffisso 1"
    Then si ottiene response status code 400

  Scenario: [ESERVICE_SUFFIX_NAME_UPDATED] Verifica che la modifica del nome di un template e-service venga riportata correttamente nel nome delle relative istanze e-service
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "suffisso 1" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    When l'utente tenta la modifica del nome dell'e-service template
    And si ottiene response status code 200
    And la modifica del nome dell'e-service template è stata effettuata correttamente
    Then il nome del nuovo e-service è stato aggiornato correttamente con il nome dell'e-service template e con il suffisso "suffisso 1"

  Scenario: [ESERVICE_SUFFIX_DIFFERENT_TEMPLATE_CREATION_1] Verifica che sia possibile creare un'istanza di e-service in stato DRAFT con suffisso già utilizzato in e-service appartenenti ad un altro template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service1"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "suffisso 1" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service2"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente tenta la creazione di un nuovo e-service con suffisso "suffisso 1" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service

  Scenario: [ESERVICE_SUFFIX_DIFFERENT_TEMPLATE_CREATION_2] Verifica che sia possibile modificare un'istanza di e-service in stato PUBLISHED con suffisso già utilizzato in e-service appartenenti ad un altro template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service1"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "suffisso 1" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And il nuovo e-service è stato creato correttamente in stato PUBLISHED
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service2"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "suffisso 2" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "suffisso 2" è utilizzato correttamente nell'e-service
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And il nuovo e-service è stato creato correttamente in stato PUBLISHED
    When l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template in stato PUBLISHED con "suffisso 1"
    Then si ottiene response status code 200
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service

  Scenario: [ESERVICE_SUFFIX_DIFFERENT_TENANT_CREATION_1] Verifica che sia possibile creare un e-service in stato DRAFT con lo stesso suffisso di un altro e-service appartenente ad un tenant differente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "suffisso 1" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la creazione di un nuovo e-service con suffisso "suffisso 1" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "suffisso 1" è utilizzato correttamente nell'e-service