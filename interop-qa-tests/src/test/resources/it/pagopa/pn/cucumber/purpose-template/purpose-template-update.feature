Feature: finalità agevolata, purpose template UPDATE

  #10(OK)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_OK] Modifica di una finalità agevolata (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template creato
    Then si ottiene lo status code 200

  #11(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_ERROR_NO_PERSONAL_DATA_ANSWER] Modifica di una finalità agevolata senza specificare nell'analisi del rischio alcun valore per la risposta usesPersonalData (error 400)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template creato con errore di tipo NO PERSONAL DATA ANSWER
    Then si ottiene lo status code 400

  #11bis(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_ERROR_NO_PURPOSE_ANSWER] Modifica di una finalità agevolata senza specificare nell'analisi del rischio alcun valore per usesPersonalData (error 400)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template creato con errore di tipo NO PURPOSE ANSWER
    Then si ottiene lo status code 400

  #12(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario Outline: [UPDATE_PURPOSE_TEMPLATE_NOT_IN_DRAFT] Modifica di una finalità agevolata senza specificare nell'analisi del rischio alcun valore per purpose (error 400)
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
  Scenario Outline: [UPDATE_PURPOSE_TEMPLATE_NO_ADMIN] Modifica di una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And l'utente è un "<ruolo>" di "<ente>"
    When si aggiorna il purpose template creato
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

  #14(KO)
  # 27 01 2026: In osservanza a https://pagopa.atlassian.net/browse/PIN-8190 il codice restituito è stato mutato 403 -> 404
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_NO_CREATOR] Modifica di una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And si aggiorna il purpose template invisibile
    Then si ottiene lo status code 404

  #15(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_404] Modifica di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template inesistente
    Then si ottiene lo status code 404

  #16(KO)
  @purposeTemplate @purposeTemplateUpdate
  Scenario: [UPDATE_PURPOSE_TEMPLATE_EXISTING_VALUE] Modifica di una finalità agevolata con un titolo già associato ad un'altra finalità agevolata esistente (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template creato con errore di tipo UPDATE WITH EXISTING TITLE
    Then si ottiene lo status code 409
