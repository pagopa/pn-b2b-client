Feature: finalità agevolata, purpose template ACTIVATION

  #38(OK)
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 204

  #39(KO)
  #non implementabile, vedi note su scenario SRS

  #40(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario Outline: [PURPOSE_TEMPLATE_ACTIVATION_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | SUSPENDED |
      | ARCHIVED  |

  #41(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario Outline: [PURPOSE_TEMPLATE_ACTIVATION_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #42(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 403

  #43(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When il purpose template inesistente viene spostato in stato PUBLISHED
    Then si ottiene lo status code 404

  #44(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_ALREADY_PUBLISHED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    When il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 409