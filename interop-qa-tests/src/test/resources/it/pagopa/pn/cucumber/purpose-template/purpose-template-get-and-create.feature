Feature: finalità agevolata, purpose template GET

  #1
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_BY_CREATOR] Recupero di una finalità agevolata da un membro della PA creatrice della finalità agevolata (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get by creator di tutti i purpose template in stato "ANY"
    Then si ottiene lo status code 200
    Examples:
      | ruolo    |
      | admin    |
      | api      |
      | support  |
      | security |

  #2
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_CATALOG] Recupero di tutte le finalità agevolata, con possibilità di specificare filtri per la ricerca (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get di tutti i purpose template con titolo "ANY"
    Then si ottiene lo status code 200
    Examples:
      | ruolo    |
      | admin    |
      | api      |
      | support  |
      | security |

  #105-106
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_CATALOG_WITH_PERSONAL_DATA] Recupero di una finalità agevolata con flag personalData pari al valore passato in input (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When si effettua la get di tutti i purpose template con titolo "ANY" e handlePersonalData <personalData>
    Then si ottiene lo status code 200
    Examples:
      | personalData |
      | true         |
      | false        |
      | null         |

  #3-4
  @purposeTemplate @purposeTemplateCreate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_WITH_PERSONAL_DATA] Creazione di una finalità agevolata specificando il valore del flag personalData (OK-KO)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData <personalData>
    Then si ottiene lo status code <statusCode>
    Examples:
      | personalData | statusCode |
      | true         | 201        |
      | false        | 201        |
      | null         | 400        |

  #5(KO)
  @purposeTemplate @purposeTemplateCreate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_NO_ADMIN] Creazione di una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "<ruolo>" di "PA1"
    And viene creato un nuovo purpose template
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #6
  @purposeTemplate @purposeTemplateCreate
  Scenario: [PURPOSE_TEMPLATE_CREATE_ANSWER_OVER_250] Creazione di una finalità agevolata specificando una risposta dell'analisi del rischio oltre i 250 caratteri (error 400)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con errore di tipo ANSWER OVER 250
    Then si ottiene lo status code 400

  #7
  @purposeTemplate @purposeTemplateCreate
  Scenario: [PURPOSE_TEMPLATE_CREATE_ERROR_NO_PERSONAL_DATA_ANSWER] Creazione di una finalità agevolata senza specificare nell'analisi del rischio una risposta per usesPersonalData (error 400)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con errore di tipo NO PERSONAL DATA ANSWER
    Then si ottiene lo status code 400

  #7bis
  @purposeTemplate @purposeTemplateCreate
  Scenario: [PURPOSE_TEMPLATE_CREATE_ERROR_NO_PURPOSE_ANSWER] Creazione di una finalità agevolata senza specificare nell'analisi del rischio una risposta per purpose (error 400)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con errore di tipo NO PURPOSE ANSWER
    Then si ottiene lo status code 400

  #8(OK)
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_BY_ID] Recupero di una finalità agevolata (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get del purpose template creato
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | admin    | 200        |
      | api      | 200        |
      | support  | 200        |

  @purposeTemplate @purposeTemplateGet
  Scenario: [PURPOSE_TEMPLATE_GET_BY_ID_B] Recupero di una finalità agevolata (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "security" di "PA1"
    And si effettua la get del purpose template
    Then si ottiene lo status code 404

  #9(KO)
  @purposeTemplate @purposeTemplateGet
  Scenario Outline: [PURPOSE_TEMPLATE_GET_404] Recupero di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And si effettua la get del purpose template inesistente
    Then si ottiene lo status code 404
    Examples:
      | ruolo    |
      | admin    |
      | api      |
      | support  |
      | security |

  @purposeTemplate @purposeTemplateGet-filtered
  Scenario Outline: [M2M_GET_PURPOSE_TEMPLATES] - Recupera i purpose templates con filtri opzionali
    Given l'utente è un "admin" di "PA1"
    And esistono purpose templates di test creati tramite data preparation
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When vengono recuperati i purpose templates con offset "<offset>", limit "<limit>", purposeTitle "<purposeTitle>", creatorIds "<creatorIds>", eserviceIds "<eserviceIds>", states "<states>", targetTenantKind "<targetTenantKind>", handlesPersonalData "<handlesPersonalData>"
    Then si ottiene lo status code <statusCode>

    Examples:
    # Happy paths - filtri singoli + combinazioni base
      | offset | limit | purposeTitle | creatorIds | eserviceIds | states  | targetTenantKind | handlesPersonalData | statusCode |

    # Nessun filtro (solo paginazione) - deve andare a 200
      | 0      | 10    | %null        | %null      | %null       | %null   | %null            | %null               | 200        |

    # Filtro singolo: purposeTitle
      | 0      | 10    | %actual      | %null      | %null       | %null   | %null            | %null               | 200        |

    # Filtro singolo: creatorIds
      | 0      | 10    | %null        | %actual    | %null       | %null   | %null            | %null               | 200        |

    # Filtro singolo: eserviceIds
      | 0      | 10    | %null        | %null      | %actual     | %null   | %null            | %null               | 200        |

    # Filtro singolo: states
      | 0      | 10    | %null        | %null      | %null       | %actual | %null            | %null               | 200        |

    # Filtro singolo: targetTenantKind
      | 0      | 10    | %null        | %null      | %null       | %null   | %actual          | %null               | 200        |

    # Filtro singolo: handlesPersonalData
      | 0      | 10    | %null        | %null      | %null       | %null   | %null            | true                | 200        |

    # Combinazione completa
      | 0      | 10    | %actual      | %actual    | %actual     | %actual | %actual          | true                | 200        |

    # offset invalid - disattivato perché al momento non abbiamo modo di specificarlo NULL senza che l'oggetto client blocchi la chiamata
    #  | %null  | 10    | %actual      | %actual    | %actual     | %actual | %actual          | true                | 400        |
      | -1     | 10    | %actual      | %actual    | %actual     | %actual | %actual          | true                | 400        |

    # limit invalid
      | 0      | %null | %actual      | %actual    | %actual     | %actual | %actual          | true                | 400        |
      | 0      | -1    | %actual      | %actual    | %actual     | %actual | %actual          | true                | 400        |

    # purposeTitle blank
      | 0      | 10    | %blank       | %actual    | %actual     | %actual | %actual          | true                | 200        |

    # handlesPersonalData blank
      | 0      | 10    | %actual      | %actual    | %actual     | %actual | %actual          | %blank              | 200        |
