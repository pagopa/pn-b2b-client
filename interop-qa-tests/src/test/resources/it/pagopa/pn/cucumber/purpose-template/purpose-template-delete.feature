Feature: finalità agevolata, purpose template DELETE

  #17(OK)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_OK] Eliminazione di una finalità agevolata (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    Then si cancella il purpose template creato

  #18(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario Outline: [DELETE_PURPOSE_TEMPLATE_NOT_IN_DRAFT] Eliminazione di una finalità agevolata in stato diverso da DRAFT (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <stato>
    Then si cancella il purpose template creato
    Then si ottiene lo status code 409
    Examples:
      | stato     |
      | PUBLISHED |
      | ARCHIVED  |
      | SUSPENDED |

  #19(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_ALREADY_DELETED] Eliminazione di una finalità agevolata già eliminata in precedenza (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    And si cancella il purpose template creato
    When si cancella il purpose template creato
    Then si ottiene lo status code 404

  #20(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario Outline: [DELETE_PURPOSE_TEMPLATE_NO_ADMIN] Eliminazione di una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "<ente>"
    And si cancella il purpose template creato
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

  #21(KO)
  # 27 01 2026: In osservanza a https://pagopa.atlassian.net/browse/PIN-8190 il codice restituito è stato mutato 403 -> 404
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_NO_CREATOR] Eliminazione di una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And si cancella il purpose template creato
    Then si ottiene lo status code 404

  #22(KO)
  @purposeTemplate @purposeTemplateDelete
  Scenario: [DELETE_PURPOSE_TEMPLATE_404] Eliminazione di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato DRAFT
    When si cancella il purpose template inesistente
    Then si ottiene lo status code 404