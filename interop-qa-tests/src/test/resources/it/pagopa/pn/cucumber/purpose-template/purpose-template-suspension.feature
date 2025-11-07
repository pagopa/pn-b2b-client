Feature: finalità agevolata, purpose template SUSPENSION

  #45(OK)
  @purposeTemplate @purposeTemplateSuspension
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    And il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 204

  #46(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario Outline: [PURPOSE_TEMPLATE_SUSPENSION_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 400
    Examples:
      | state    |
      | DRAFT    |
      | ARCHIVED |

  #47(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario Outline: [PURPOSE_TEMPLATE_SUSPENSION_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #48(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 403

  #49(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    When il purpose template inesistente viene spostato in stato SUSPENDED
    Then si ottiene lo status code 404

  #50(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_ALREADY_SUSPENDED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    And il purpose template creato viene spostato in stato SUSPENDED
    When il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 409