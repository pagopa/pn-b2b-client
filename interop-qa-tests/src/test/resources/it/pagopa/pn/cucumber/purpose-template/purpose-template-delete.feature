Feature: finalità agevolata, purpose template DELETE

  #17(OK)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    Then si cancella per la prima volta il purpose template creato

  #18(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario Outline: [DELETE_PURPOSE_TEMPLATE_NOT_IN_DRAFT]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <stato>
    Then si cancella per la prima volta il purpose template creato
    Then si ottiene lo status code 409
    Examples:
      | stato     |
      | PUBLISHED |
      | ARCHIVED  |
      | SUSPENDED |

  #19(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_ALREADY_DELETED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    And si cancella per la prima volta il purpose template creato
    When si cancella nuovamente il purpose template creato
    Then si ottiene lo status code 404

  #20(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario Outline: [DELETE_PURPOSE_TEMPLATE_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si cancella per la prima volta il purpose template creato
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #21(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And si cancella per la prima volta il purpose template creato
    Then si ottiene lo status code 403

  #22(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    When si cancella per la prima volta il purpose template inesistente
    Then si ottiene lo status code 404