Feature: finalità agevolata, purpose template GET

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