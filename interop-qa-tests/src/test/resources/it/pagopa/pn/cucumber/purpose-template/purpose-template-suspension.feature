Feature: finalità agevolata, purpose template SUSPENSION

  #42-44(OK-KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario Outline: [PURPOSE_TEMPLATE_SUSPENSION]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato ACTIVE
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 204        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |

  #43(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario Outline: [PURPOSE_TEMPLATE_SUSPENSION_KO_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 400
    Examples:
      | state    |
      | DRAFT    |
      | ARCHIVED |

  #45(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_KO_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato ACTIVE
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 403

  #46(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_KO_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato ACTIVE
    When il purpose template inesistente viene spostato in stato SUSPENDED
    Then si ottiene lo status code 404

  #47(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_ALREADY_SUSPENDED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato ACTIVE
    And il purpose template creato viene spostato in stato SUSPENDED
    When il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 409