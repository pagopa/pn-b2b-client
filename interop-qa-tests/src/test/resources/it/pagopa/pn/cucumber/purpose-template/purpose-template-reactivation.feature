Feature: finalità agevolata, purpose template REACTIVATION

  #48-50(OK-KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario Outline: [PURPOSE_TEMPLATE_REACTIVATION]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene riattivato
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 204        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |

  #49(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario Outline: [PURPOSE_TEMPLATE_REACTIVATION_KO_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene riattivato
    Then si ottiene lo status code 400
    Examples:
      | state    |
      | DRAFT    |
      | ARCHIVED |

  #51(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_KO_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene riattivato
    Then si ottiene lo status code 403

  #52(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_KO_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When il purpose template inesistente viene riattivato
    Then si ottiene lo status code 404

  #53(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_ALREADY_SUSPENDED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    And il purpose template creato viene riattivato
    When il purpose template creato viene riattivato
    Then si ottiene lo status code 409