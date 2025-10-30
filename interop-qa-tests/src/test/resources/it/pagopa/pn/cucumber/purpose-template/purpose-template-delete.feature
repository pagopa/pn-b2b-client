Feature: finalità agevolata, purpose template DELETE

  #15(OK)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    Then si cancella il purpose template creato

  #16(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario Outline: [DELETE_PURPOSE_TEMPLATE_KO_NOT_IN_DRAFT]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <stato>
    Then si cancella il purpose template creato
    Then si ottiene lo status code 409
    Examples:
      | stato     |
      | PUBLISHED |
      | ARCHIVED  |
      | SUSPENDED |

  #17(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_OK_DELETE_TWICE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    And si cancella il purpose template creato
    When si cancella il purpose template creato
    Then si ottiene lo status code 404

  #18(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario Outline: [DELETE_PURPOSE_TEMPLATE_KO_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si cancella il purpose template creato
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #19(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_KO_IS_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And si cancella il purpose template creato
    Then si ottiene lo status code 403

  #20(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    When si cancella il purpose template inesistente
    Then si ottiene lo status code 404