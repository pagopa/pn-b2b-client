Feature: finalità agevolata, purpose from purpose template

  #107 (OK) 108 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_WITH_PERSONAL_DATA]
    Given "PA2" ha già creato e pubblicato 1 e-service con personalData <personalDataEservice>
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData <personalDataTemplate>
    And il purpose template creato viene spostato in stato PUBLISHED
    When si crea una finalità a partire dal purpose template creato
    Then si ottiene response status code <statusCode>
    Examples:
      | personalDataTemplate | personalDataEservice | statusCode |
      | true                 | true                 | 200        |
      | false                | false                | 200        |
      | true                 | false                | 400        |
      | false                | true                 | 400        |

  #109 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_WRONG_STATE]
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When si crea una finalità a partire dal purpose template sospeso
    Then si ottiene response status code 404

  #110 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_WRONG_INPUT]
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    When si crea una finalità a partire dal purpose template creato passando "DATI NULL"
    Then si ottiene response status code 400

  #111 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_404]
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    When si crea una finalità a partire dal purpose template inesistente
    Then si ottiene response status code 404

  #112 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_NO_ADMIN]
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
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
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente passando "TITOLO ESISTENTE"
    When si crea una finalità a partire dal purpose template esistente passando "TITOLO ESISTENTE"
    Then si ottiene response status code 409

  #114 (OK)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_OK]
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    When si modifica la finalità creata
    Then si ottiene response status code 200

  #115 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_WRONG_STATE]
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And la finalità viene portata in stato <state>
    When si modifica la finalità creata
    Then si ottiene response status code 409
    Examples:
      | state     |
      | ACTIVE    |
      | SUSPENDED |
      | ARCHIVED  |

  #116 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_NULL_VALUES]
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    When si modifica la finalità creata passando "<problemType>"
    Then si ottiene response status code 400
    Examples:
      | problemType      |
#      | VALORI NULL      |
      | EMPTY TITLE      |
      | ZERO DAILY CALLS |

  #117 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_404]
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    When si modifica la finalità inesistente
    Then si ottiene response status code 404

  #118 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_NO_ADMIN]
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
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
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    When l'utente è un "admin" di "GSP"
    And si modifica la finalità creata
    Then si ottiene response status code 403

  #120 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_EXISTING_TITLE]
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When si modifica la finalità creata passando "TITLE ESISTENTE"
    Then si ottiene response status code 409