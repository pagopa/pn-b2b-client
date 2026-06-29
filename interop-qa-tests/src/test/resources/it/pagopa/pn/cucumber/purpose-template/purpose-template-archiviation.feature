Feature: finalità agevolata, purpose template ARCHIVIATION

  #57(OK)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario Outline: [PURPOSE_TEMPLATE_ARCHIVIATION_OK] Archiviazione di una finalità agevolata (OK)
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
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_WRONG_STATE] Archiviazione di una finalità agevolata in stato diverso da PUBLISHED (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    When il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 409

  #59(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario Outline: [PURPOSE_TEMPLATE_ARCHIVIATION_NO_ADMIN] Archiviazione di una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When l'utente è un "<ruolo>" di "<ente>"
    And il purpose template creato viene spostato in stato ARCHIVED
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

  #60(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_NO_CREATOR] Archiviazione di una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 403

  #61(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_404] Archiviazione di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When il purpose template inesistente viene spostato in stato ARCHIVED
    Then si ottiene lo status code 404

  #62(KO)
  @purposeTemplate @purposeTemplateArchiviation
  Scenario: [PURPOSE_TEMPLATE_ARCHIVIATION_ALREADY_ARCHIVED] Archiviazione di una finalità agevolata che risulta già in stato ARCHIVED (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    And il purpose template creato viene spostato in stato ARCHIVED
    When il purpose template creato viene spostato in stato ARCHIVED
    Then si ottiene lo status code 409