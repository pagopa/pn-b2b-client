Feature: finalità agevolata, purpose template ARCHIVIATION

  #57(OK)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario Outline: [PURPOSE_TEMPLATE_ARCHIVIATION_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <ptState>
    And il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 204
    Examples:
      | ptState   |
      | PUBLISHED |
      | SUSPENDED |

  #58(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    When il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 409

  #59(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario Outline: [PURPOSE_TEMPLATE_ARCHIVIATION_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #60(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 403

  #61(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When il purpose template inesistente viene spostato in stato ARCHIVED
    Then si ottiene lo status code 404

  #62(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_ALREADY_ARCHIVED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    And il purpose template creato viene spostato in stato ARCHIVED
    When il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 409