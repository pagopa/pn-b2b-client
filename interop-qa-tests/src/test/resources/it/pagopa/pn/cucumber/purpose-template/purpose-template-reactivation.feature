Feature: finalità agevolata, purpose template REACTIVATION

  #51(OK)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    And il purpose template creato viene riattivato
    Then si ottiene lo status code 204

  #52(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario Outline: [PURPOSE_TEMPLATE_REACTIVATION_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene riattivato
    Then si ottiene lo status code 400
    Examples:
      | state    |
      | DRAFT    |
      | ARCHIVED |

  #53(OK)
  @purposeTemplate @purposeTemplateReactivation
  Scenario Outline: [PURPOSE_TEMPLATE_REACTIVATION_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene riattivato
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #54(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene riattivato
    Then si ottiene lo status code 403

  #55(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When il purpose template inesistente viene riattivato
    Then si ottiene lo status code 404

  #56(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_ALREADY_REACTIVATED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    And il purpose template creato viene riattivato
    When il purpose template creato viene riattivato
    Then si ottiene lo status code 409