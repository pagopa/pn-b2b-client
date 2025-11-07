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

  #105-106
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_CATALOG_WITH_PERSONAL_DATA]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si effettua la get di tutti i purpose template con titolo "ANY" e handlePersonalData <personalData>
    Then si ottiene lo status code 200
    Examples:
      | personalData |
      | "true"       |
      | "false"      |
      | "null"       |
      | "any"        |

  #3-4
  @purposeTemplate @purposeTemplateCreate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_WITH_PERSONAL_DATA]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData <personalData>
    Then si ottiene lo status code <statusCode>
    Examples:
      | personalData | statusCode |
      | "true"       | 201        |
      | "false"      | 201        |
      | "null"       | 400        |
      | "any"        | 400        |

  #5(KO)
  @purposeTemplate @purposeTemplateCreate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_NO_ADMIN]
    Given l'utente è un "<ruolo>" di "PA1"
    And viene creato un nuovo purpose template
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #6 TODO MATTEO
  @purposeTemplate @purposeTemplateCreate
  Scenario: [PURPOSE_TEMPLATE_CREATE_ANSWER_OVER_250]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con answer length over 250
    Then si ottiene lo status code 400

  #7 TODO MATTEO
  @purposeTemplate @purposeTemplateCreate
  Scenario: [PURPOSE_TEMPLATE_CREATE_NO_ANSWERS]
    Given l'utente è un "admin" di "PA1"
    # Nel body specificare l’answer "usesThirdPartyPersonalData" e non specificare answer "usesPersonalData"
    # Implementare lo stesso scenario anche con answer "institutionalPurpose" senza specificare answer "purpose"
    Then si ottiene lo status code 400

  #8(OK)
  @purposeTemplate @purposeTemplateGet
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

  #9(KO)
  @purposeTemplate @purposeTemplateGet
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