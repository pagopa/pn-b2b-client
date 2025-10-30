Feature: finalità agevolata, purpose template

  #1
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_BY_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    #TODO MATTEO, probabilmente serve aggiungere parametro al filtro query per specificare la PA
    And si effettua la get by creator di tutti i purpose template in stato "ANY"
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 200        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |

  #2
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_CATALOG]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get di tutti i purpose template con titolo "ANY"
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 200        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |


  #5(OK) - 6(KO)
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE]
    Given l'utente è un "<ruolo>" di "PA1"
    And viene creato un nuovo purpose template
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 201        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |

  #7(OK)
  Scenario Outline: [PURPOSE_TEMPLATE_GET_BY_ID]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get del purpose template creato
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 200        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |

  #8(KO)
  Scenario Outline: [PURPOSE_TEMPLATE_GET_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get del purpose template inesistente
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 404        |
      | api      | 404        |
      | support  | 404        |
      | security | 404        |

  #9(OK) 11(KO)
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
  #TODO dopo aver creato il template, portarlo nello status specificato
  Scenario Outline: [UPDATE_PURPOSE_TEMPLATE_KO_NOT_IN_DRAFT]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template creato che è in stato "<state>"
    Then si ottiene lo status code <statusCode>
    Examples:
      | statusCode | state     |
      | 400        | ACTIVE    |
      | 400        | ARCHIVED  |
      | 400        | SUSPENDED |

  #12(KO)
  Scenario: [UPDATE_PURPOSE_TEMPLATE_KO_IS_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And si aggiorna il purpose template creato
    Then si ottiene lo status code 403

  #13(KO)
  Scenario: [UPDATE_PURPOSE_TEMPLATE_KO_INEXISTENT_PURPOSE_TEMPLATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si aggiorna il purpose template inesistente
    Then si ottiene lo status code 404

  #14(KO)
  Scenario: [UPDATE_PURPOSE_TEMPLATE_KO_EXISTENT_VALUE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    #TODO: qua andrebbe fatto un update ripetendo i dati già presenti
    When si aggiorna il purpose template creato
    Then si ottiene lo status code 409
