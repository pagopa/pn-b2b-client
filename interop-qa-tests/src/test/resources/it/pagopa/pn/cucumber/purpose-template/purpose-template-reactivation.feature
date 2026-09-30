Feature: finalità agevolata, purpose template REACTIVATION

  #51(OK)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_OK] Riattivazione di una finalità agevolata sospesa (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    And il purpose template creato viene riattivato
    Then si ottiene lo status code 204

  #52(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario Outline: [PURPOSE_TEMPLATE_REACTIVATION_WRONG_STATE] Riattivazione di una finalità agevolata in stato diverso da SUSPENDED (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene riattivato
    Then si ottiene lo status code 409
    Examples:
      | state    |
      | DRAFT    |
      | ARCHIVED |

  #53(OK)
  @purposeTemplate @purposeTemplateReactivation
  Scenario Outline: [PURPOSE_TEMPLATE_REACTIVATION_NO_ADMIN] Riattivazione di una finalità agevolata sospesa da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When l'utente è un "<ruolo>" di "<ente>"
    And il purpose template creato viene riattivato
    Then si ottiene lo status code 403
    Examples:
      | ente | ruolo    |
      | PA1  | api      |
      | PA1  | support  |
      | PA1  | security |

    @nuovi-operatori-update
    Examples:
      | ente | ruolo    |
      | PA2  | reviewer |
      | PA2  | viewer   |

  #54(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_NO_CREATOR] Riattivazione di una finalità agevolata sospesa da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene riattivato
    Then si ottiene lo status code 403

  #55(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_404] Riattivazione di una finalità agevolata sospesa passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When il purpose template inesistente viene riattivato
    Then si ottiene lo status code 404

  #56(KO)
  @purposeTemplate @purposeTemplateReactivation
  Scenario: [PURPOSE_TEMPLATE_REACTIVATION_ALREADY_REACTIVATED] Riattivazione di una finalità agevolata sospesa che risulta essere stata già riattivata (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    And il purpose template creato viene riattivato
    When il purpose template creato viene riattivato
    Then si ottiene lo status code 409