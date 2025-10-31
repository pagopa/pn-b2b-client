Feature: finalità agevolata, purpose template ACTIVATION

  #36-38(OK-KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario Outline: [PURPOSE_TEMPLATE_ACTIVATION]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 204        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |

  #37(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario Outline: [PURPOSE_TEMPLATE_ACTIVATION_KO_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 400
    Examples:
      | state     |
      | SUSPENDED |
      | ARCHIVED  |

  #39(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_KO_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 403

  #40(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_KO_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When il purpose template inesistente viene spostato in stato PUBLISHED
    Then si ottiene lo status code 404

  #41(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_ALREADY_PUBLISHED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    When il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 409