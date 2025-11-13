Feature: finalità agevolata, purpose from purpose template

  #107 (OK) 108 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_WITH_PERSONAL_DATA]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData <personalData>
    And il purpose template creato viene spostato in stato PUBLISHED
    When si crea una finalità a partire dal purpose template creato
    # Nel body specificare valori validi per i campi obbligatori:
    #eserviceId
    #consumerId
    #riskAnalysisForm
    #title
    Then si ottiene response status code <statusCode>
    Examples:
      | personalData | statusCode |
      | "true"       | 200        |
      | "false"      | 400        |

  #109 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_WITH_PERSONAL_DATA_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato <state>
    When si crea una finalità a partire dal purpose template creato
    # Nel body specificare valori validi per i campi obbligatori:
    #eserviceId
    #consumerId
    #riskAnalysisForm
    #title
    Then si ottiene response status code 404
    Examples:
      | state     |
      | DRAFT     |
      | SUSPENDED |
      | ARCHIVED  |

  #110 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_WRONG_INPUT]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    When si crea una finalità a partire dal purpose template creato passando "DATI NULL"
    Then si ottiene response status code 400

  #111 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    When si crea una finalità a partire dal purpose template inesistente
    Then si ottiene response status code 404

  #112 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    When l'utente è un "<ruolo>" di "PA1"
    And si crea una finalità a partire dal purpose template inesistente
    Then si ottiene response status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #113 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_DUPLICATED_TITLE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente passando "TITOLO ESISTENTE"
    When si crea una finalità a partire dal purpose template esistente passando "TITOLO ESISTENTE"
    Then si ottiene response status code 409

  #114 (OK)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    When si modifica la finalità creata
    Then si ottiene response status code 200

  #115 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And la finalità viene portata in stato <state>
    When si modifica la finalità creata
    Then si ottiene response status code 409
    Examples:
      | state                |
      | DRAFT                |
      | SUSPENDED            |
      | ARCHIVED             |
      | REJECTED             |
      | WAITING_FOR_APPROVAL |

  #116 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_NULL_VALUES]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And la finalità viene portata in stato ACTIVE
    When si modifica la finalità creata passando "VALORI NULL"
    Then si ottiene response status code 400

  #117 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And la finalità viene portata in stato ACTIVE
    When si modifica la finalità inesistente
    Then si ottiene response status code 404

  #118 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And la finalità viene portata in stato ACTIVE
    When l'utente è un "<ruolo>" di "PA1"
    And si modifica la finalità creata
    Then si ottiene response status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #119 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And la finalità viene portata in stato ACTIVE
    When l'utente è un "admin" di "GSP"
    And si modifica la finalità creata
    Then si ottiene response status code 403

  #120 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_ALREADY_PATCHED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData "true"
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And la finalità viene portata in stato ACTIVE
    When si modifica la finalità creata passando "TITLE ESISTENTE"
    And si modifica la finalità creata passando "TITLE ESISTENTE"
    Then si ottiene response status code 409