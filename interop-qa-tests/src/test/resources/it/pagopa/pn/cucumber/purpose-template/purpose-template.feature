Feature: finalità agevolata, purpose template


  Scenario Outline: [CREATE_PURPOSE_TEMPLATE]
    Given l'utente è un "<ruolo>" di "PA1"
    And viene creato un nuovo purpose template
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 200        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |
      | m2m      | 403        |
#      | m2m-admin | 403   | ---------> TODO vale come != admin ???

  Scenario Outline: [GET_PURPOSE_TEMPLATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And si ottiene lo status code <statusCode>
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get del purpose template "creato"
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 200        |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |
      | m2m      | 403        |
#      | m2m-admin | 403   | ---------> TODO vale come != admin ???