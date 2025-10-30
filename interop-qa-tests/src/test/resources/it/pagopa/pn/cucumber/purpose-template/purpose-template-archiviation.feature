Feature: finalità agevolata, purpose template ARCHIVIATION

  #54-56(OK-KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario Outline: [PURPOSE_TEMPLATE_ARCHIVIATION]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 204        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |

  #55(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario Outline: [PURPOSE_TEMPLATE_ARCHIVIATION_KO_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 400
    Examples:
      | state  |
      | DRAFT  |
      | ACTIVE |

  #57(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_KO_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 403

  #58(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_KO_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When il purpose template inesistente viene spostato in stato ARCHIVED
    Then si ottiene lo status code 404

  #59(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_ALREADY_SUSPENDED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    And il purpose template creato viene spostato in stato ARCHIVED
    When il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 409