Feature: finalità agevolata, purpose template GET

  #1
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_BY_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get by creator di tutti i purpose template in stato "ANY"
    Then si ottiene lo status code 200
    Examples:
      | ruolo    |
      | admin    |
      | api      |
      | support  |
      | security |

  #2
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_CATALOG]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get di tutti i purpose template con titolo "ANY"
    Then si ottiene lo status code 200
    Examples:
      | ruolo    |
      | admin    |
      | api      |
      | support  |
      | security |

  #105-106
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_CATALOG_WITH_PERSONAL_DATA]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si effettua la get di tutti i purpose template con titolo "ANY" e handlePersonalData <personalData>
    Then si ottiene lo status code 200
    Examples:
      | personalData |
      | true         |
      | false        |
      | null         |

  #3-4
  @purposeTemplate @purposeTemplateCreate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_WITH_PERSONAL_DATA]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData <personalData>
    Then si ottiene lo status code <statusCode>
    Examples:
      | personalData | statusCode |
      | true         | 201        |
      | false        | 201        |
      | null         | 400        |

  #5(KO)
  @purposeTemplate @purposeTemplateCreate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_NO_ADMIN]
    Given l'utente è un "<ruolo>" di "PA1"
    And viene creato un nuovo purpose template
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #6
  @purposeTemplate @purposeTemplateCreate
  Scenario: [PURPOSE_TEMPLATE_CREATE_ANSWER_OVER_250]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con errore di tipo ANSWER OVER 250
    Then si ottiene lo status code 400

  #7
  @purposeTemplate @purposeTemplateCreate
  Scenario: [PURPOSE_TEMPLATE_CREATE_ERROR_NO_PERSONAL_DATA_ANSWER]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con errore di tipo NO PERSONAL DATA ANSWER
    Then si ottiene lo status code 400

  #7bis
  @purposeTemplate @purposeTemplateCreate
  Scenario: [PURPOSE_TEMPLATE_CREATE_ERROR_NO_PURPOSE_ANSWER]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con errore di tipo NO PURPOSE ANSWER
    Then si ottiene lo status code 400

  #8(OK)
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_BY_ID]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get del purpose template creato
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 200        |
      | api      | 200        |
      | support  | 200        |
      # gli operatori security non possono fare la getById dei purposeTemplate in DRAFT
      | security | 403        |

  #9(KO)
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get del purpose template inesistente
    Then si ottiene lo status code 404
    Examples:
      | ruolo    |
      | admin    |
      | api      |
      | support  |
      | security |