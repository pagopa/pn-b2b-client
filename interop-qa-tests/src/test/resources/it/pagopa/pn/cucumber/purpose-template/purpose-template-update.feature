Feature: finalità agevolata, purpose template UPDATE

  #10(OK)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template creato
    Then si ottiene lo status code 200

  #11(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_ERROR_NO_PERSONAL_DATA_ANSWER]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template creato con errore di tipo NO PERSONAL DATA ANSWER
    Then si ottiene lo status code 400

  #11bis(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_ERROR_NO_PURPOSE_ANSWER]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template creato con errore di tipo NO PURPOSE ANSWER
    Then si ottiene lo status code 400

  #12(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario Outline: [UPDATE_PURPOSE_TEMPLATE_NOT_IN_DRAFT]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When si aggiorna il purpose template creato
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | PUBLISHED |
      | ARCHIVED  |
      | SUSPENDED |

  #13(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario Outline: [UPDATE_PURPOSE_TEMPLATE_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And l'utente è un "<ruolo>" di "PA1"
    When si aggiorna il purpose template creato
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #14(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And si aggiorna il purpose template creato
    Then si ottiene lo status code 403

  #15(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template inesistente
    Then si ottiene lo status code 404

  #16(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_EXISTING_VALUE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template creato con errore di tipo UPDATE WITH EXISTING TITLE
    Then si ottiene lo status code 409
