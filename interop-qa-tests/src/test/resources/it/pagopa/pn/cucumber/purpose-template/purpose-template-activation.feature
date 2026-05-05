Feature: finalità agevolata, purpose template ACTIVATION

  #38(OK)
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_OK] Attivazione di una finalità agevolata (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 204

  #39(KO)
  #non implementabile, vedi note su scenario SRS

  #40(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario Outline: [PURPOSE_TEMPLATE_ACTIVATION_WRONG_STATE] Attivazione di una finalità agevolata in stato diverso da DRAFT (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | SUSPENDED |
      | ARCHIVED  |

  @purposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CHANGE_STATUS_WRONG] Una purpose template non può essere spostata in stato <stato> se si trova in stato <stato> (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene correttamente spostato in stato <stato>
    And si ottiene lo status code 200
    And il purpose template creato viene spostato in stato <stato>
    Then si ottiene lo status code 409
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  #41(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario Outline: [PURPOSE_TEMPLATE_ACTIVATION_NO_ADMIN] Attivazione di una finalità agevolata da parte di un utente NON admin (error 403)
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
  # 27 01 2026: In osservanza a https://pagopa.atlassian.net/browse/PIN-8190 il codice restituito è stato mutato 403 -> 404
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_NO_CREATOR] Attivazione di una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 404

  #43(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_404] Attivazione di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When il purpose template inesistente viene spostato in stato PUBLISHED
    Then si ottiene lo status code 404

  #44(KO)
  @purposeTemplate @purposeTemplateActivation
  Scenario: [PURPOSE_TEMPLATE_ACTIVATION_ALREADY_PUBLISHED] Attivazione di una finalità agevolata che risulta già in stato PUBLISHED (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    When il purpose template creato viene spostato in stato PUBLISHED
    Then si ottiene lo status code 409