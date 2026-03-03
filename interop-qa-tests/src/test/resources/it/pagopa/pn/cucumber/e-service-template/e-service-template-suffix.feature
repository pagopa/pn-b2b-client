@e-service-template
Feature: Test API of e-service template suffix

  @nrt-minimal
  # @happy-path
  # @e-service-template-create
  # @e-service-suffix  <-- usiamo questo?
  Scenario: [ESERVICE_SUFFIX_NRT_1] La creazione di un'istanza e-service da un template privo di instanceLabel genera un'istanza valida
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    When l'utente è un "admin" di "PA1"
    And l'utente tenta la creazione di un nuovo e-service con suffisso "" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    And il suffisso "" è stato utilizzato correttamente nell'e-service

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
    And il suffisso "" è stato utilizzato correttamente nell'e-service

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
    And il suffisso "<suffix>" è stato utilizzato correttamente nell'e-service

    Examples:
      | suffix  | statusCode |
      | %null   | 200        |
      | %space  | 200        |
      |         | 200        |
      | ABC     | 200        |
      | 123     | 200        |

  Scenario: [ESERVICE_SUFFIX_CREATION_2] Verifica che il suffisso sia utilizzato correttamente a seguito della creazione dell'istanza e-service
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service"
    And si ottiene response status code 200
    And l'e-service template è in stato di PUBLISHED
    And l'utente tenta la creazione di un nuovo e-service con suffisso "%null" a partire dal template indicando tutte le specifiche
    And si ottiene response status code 200
    When l'utente tenta la creazione di un nuovo e-service con suffisso "" a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 400
