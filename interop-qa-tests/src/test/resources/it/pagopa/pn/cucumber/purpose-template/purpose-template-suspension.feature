Feature: finalità agevolata, purpose template SUSPENSION

  #45(OK)
  @purposeTemplate @purposeTemplateSuspension
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_OK] Sospensione di una finalità agevolata (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    And il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 204

  #46(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario Outline: [PURPOSE_TEMPLATE_SUSPENSION_WRONG_STATE] Sospensione di una finalità agevolata in stato diverso da PUBLISHED (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 409
    Examples:
      | state    |
      | DRAFT    |
      | ARCHIVED |

  #47(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario Outline: [PURPOSE_TEMPLATE_SUSPENSION_NO_ADMIN] Sospensione di una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    When l'utente è un "<ruolo>" di "<ente>"
    And il purpose template creato viene spostato in stato SUSPENDED
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

  #48(KO)
  @purposeTemplate @purposeTemplateSuspension @ko-nrt-08072026
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_NO_CREATOR] Sospensione di una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 403

  #49(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_404] Sospensione di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    When il purpose template inesistente viene spostato in stato SUSPENDED
    Then si ottiene lo status code 404

  #50(KO)
  @purposeTemplate @purposeTemplateSuspension
  Scenario: [PURPOSE_TEMPLATE_SUSPENSION_ALREADY_SUSPENDED] Sospensione di una finalità agevolata che risulta già in stato SUSPENDED (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato PUBLISHED
    And il purpose template creato viene spostato in stato SUSPENDED
    When il purpose template creato viene spostato in stato SUSPENDED
    Then si ottiene lo status code 409
