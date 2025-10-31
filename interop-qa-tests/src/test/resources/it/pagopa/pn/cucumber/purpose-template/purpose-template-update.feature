Feature: finalità agevolata, purpose template UPDATE
  
  #9(OK) 11(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario Outline: [UPDATE_PURPOSE_TEMPLATE_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And l'utente è un "<ruolo>" di "PA1"
    When si aggiorna il purpose template creato
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 200        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |

  #10(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario Outline: [UPDATE_PURPOSE_TEMPLATE_KO_NOT_IN_DRAFT]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When si aggiorna il purpose template creato
    Then si ottiene lo status code <statusCode>
    Examples:
      | statusCode | state     |
      | 400        | PUBLISHED |
      | 400        | ARCHIVED  |
      | 400        | SUSPENDED |

  #12(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_KO_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And si aggiorna il purpose template creato
    Then si ottiene lo status code 403

  #13(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_KO_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template inesistente
    Then si ottiene lo status code 404

  #14(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_KO_ALREADY_UPDATED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And si aggiorna il purpose template creato
    When si aggiorna il purpose template creato
    Then si ottiene lo status code 409
