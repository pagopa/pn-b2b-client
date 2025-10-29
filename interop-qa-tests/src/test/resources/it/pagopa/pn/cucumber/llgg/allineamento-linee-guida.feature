Feature: Allineamento linee guida

  Scenario Outline: [LLGG_1] Creazione e-service in modalità "DELIVER" con diverse combinazioni di flagPersonalData (Scenario 1,2,3)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "DRAFT" e flag dati personali a "<personalDataFlag>"
    Then si ottiene status code 200
    Given "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'utente pubblica quel descrittore
    Then si ottiene status code <statusCode>

    Examples:
      | personalDataFlag | statusCode |
      | true             | 200        |
      | false            | 200        |
      | undefined        | 400        |

  Scenario Outline: [LLGG_1.1] Creazione e-service in modalità "DELIVER" con diverse combinazioni di flagPersonalData e verifica del descrittore (Scenario 39)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "DRAFT" e flag dati personali a "<personalDataFlag>"
    Then si ottiene status code 200
    Given "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'utente pubblica quel descrittore
    Then si ottiene status code 200
    Then verifica che il flagPersonalData presente nell'eService con il descrittore appena creato sia "<personalDataFlag>"

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  Scenario Outline: [LLGG_1.2] Creazione e-service in modalità "DELIVER" con diverse combinazioni di flagPersonalData e verifica del dettaglio (Scenario 40)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED" e flag dati personali a "<personalDataFlag>"
    Then si ottiene status code 200
    Then verifica che il flagPersonalData presente nell'eService sia "<personalDataFlag>"

    Examples:
      | personalDataFlag |
      | true             |
      | false            |


  Scenario Outline: [LLGG_2] Creazione e-service in modalità "RECEIVE" con diverse combinazioni di flagPersonalData (Scenario 4, 5, 6, 7)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT" e flag dati personali a "<eServicePersonalDataFlag>"
    Then si ottiene status code 200
    When verifica che la versione 3.1 dell'analisi del rischio includa domande inerenti all'uso di dati personali
    When l'utente aggiunge un'analisi del rischio con un flag relativo ai dati personali impostato a "<riskAnalysisPersonalDataFlag>"
    Then si ottiene status code <statusCodeRiskAnalysis>
    Given "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'utente pubblica quel descrittore
    Then si ottiene status code <descriptorStatusCode>

    Examples:
      | eServicePersonalDataFlag | descriptorStatusCode | riskAnalysisPersonalDataFlag | statusCodeRiskAnalysis |
      | undefined                | 400                  | false                        | 204                    |
      | undefined                | 400                  | true                         | 204                    |
      | false                    | 200                  | false                        | 204                    |
      | true                     | 400                  | false                        | 400                    |
      | true                     | 200                  | true                         | 204                    |
      | false                    | 400                  | true                         | 400                    |

  Scenario Outline: [LLGG_2.1] Creazione e-service in modalità "RECEIVE" con diverse combinazioni di flagPersonalData e verifica del descrittore (Scenario 39)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT" e flag dati personali a "<personalDataFlag>"
    Then si ottiene status code 200
    When verifica che la versione 3.1 dell'analisi del rischio includa domande inerenti all'uso di dati personali
    When l'utente aggiunge un'analisi del rischio con un flag relativo ai dati personali impostato a "<personalDataFlag>"
    Then si ottiene status code 204
    Given "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'utente pubblica quel descrittore
    Then si ottiene status code 200
    Then verifica che il flagPersonalData presente nell'eService con il descrittore appena creato sia "<personalDataFlag>"

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  Scenario Outline: [LLGG_2.2] Creazione e-service in modalità "RECEIVE" con diverse combinazioni di flagPersonalData e verifica del dettaglio (Scenario 40)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT" e flag dati personali a "<personalDataFlag>"
    Then si ottiene status code 200
    When verifica che la versione 3.1 dell'analisi del rischio includa domande inerenti all'uso di dati personali
    When l'utente aggiunge un'analisi del rischio con un flag relativo ai dati personali impostato a "<personalDataFlag>"
    Then si ottiene status code 204
    Given "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'utente pubblica quel descrittore
    Then si ottiene status code 200
    Then verifica che il flagPersonalData presente nell'eService sia "<personalDataFlag>"

    Examples:
      | personalDataFlag |
      | true             |
      | false            |


  Scenario Outline: [LLGG_3] Aggiornamento descrittore in modalità DELIVER di un eService comprendente il flag dati (Scenario 8,9,10)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "DRAFT" e flag dati personali a "<personalDataFlag>"
    Then si ottiene status code 200
    Then si ottiene status code 204
    Given "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'utente aggiorna alcuni parametri di quel descrittore
    Then si ottiene status code <updateStatusCode>
    When l'utente pubblica quel descrittore
    Then si ottiene status code <publishStatusCode>

    Examples:
      | personalDataFlag | updateStatusCode | publishStatusCode |
      | false            | 200              | 200               |
      | true             | 200              | 200               |
      | undefined        | 200              | 400               |

    #TODO: Gli ultimi due scenari non sono coerenti, dovrebbe esserci un refuso nella progettazione dei test
  Scenario Outline: [LLGG_4] Aggiornamento descrittore in modalità RECIVE di un eService comprendente il flag dati (Scenario 11, 12, 13)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT" e flag dati personali a "<personalDataFlag>"
    Then si ottiene status code 200
    When verifica che la versione 3.1 dell'analisi del rischio includa domande inerenti all'uso di dati personali
    When l'utente aggiunge un'analisi del rischio con un flag relativo ai dati personali impostato a "<riskAnalysisPersonalDataFlag>"
    Then si ottiene status code 204
    Given "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'utente aggiorna alcuni parametri di quel descrittore
    Then si ottiene status code <updateStatusCode>
    When l'utente pubblica quel descrittore
    Then si ottiene status code <publishStatusCode>

    Examples:
      | personalDataFlag | riskAnalysisPersonalDataFlag | updateStatusCode | publishStatusCode |
      | false            | false                        | 200              | 200               |
      | true             | true                         | 200              | 200               |
      | undefined        | true                         | 200              | 400               |
      | undefined        | false                        | 200              | 400               |
      | false            | true                         | 200              | 400               |
      | true             | false                        | 200              | 400               |

  Scenario Outline: [LLGG_5] Setting flagPersonalData passando un eServiceId inesistente (Scenario 17)
    Given l'utente è un "admin" di "PA1"
    When viene settato il personalDataFlag a "<personalDataFlag>" passando un "eServiceId" inesistente
    Then si ottiene lo status code 404

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  Scenario Outline: [LLGG_6] Setting flagPersonalData usando un token invalido (Scenario 19)
    Given l'utente è un "admin" di "PA1"
    When viene settato il personalDataFlag a "<personalDataFlag>" passando un "eServiceId" inesistente e un token invalido
    Then si ottiene lo status code 401

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

    #TODO: per i ruoli api parte correttamente la chiamata, ho verificato con Roberto, bisogna correggere lo scenario
  Scenario Outline: [LLGG_7] Setting flagPersonalData usando ruoli differenti (Scenario 20)
    Given l'utente è un "<ruolo>" di "<ente>"
    When viene settato il personalDataFlag a "true" passando un "eServiceId" inesistente
    Then si ottiene lo status code <statusCode>

    Examples:
      | ente | ruolo    | statusCode |
      | GSP  | security | 403        |
      | GSP  | support  | 403        |
      | PA1  | security | 403        |
      | PA1  | support  | 403        |

  Scenario Outline: [LLGG_8] Creazione e-service in modalità "DELIVER" con diverse combinazioni di flagPersonalData e setting a posteriori del medesimo (Scenario 21)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "<statoVersione>" e flag dati personali a "true"
    Then si ottiene status code 200
    Then viene settato il personalDataFlag a "false" nell'eservice appena creato
    Then si ottiene status code 409

    Examples:
      | statoVersione |
      | PUBLISHED     |
      | SUSPENDED     |
      | DEPRECATED    |
      | ARCHIVED      |


  Scenario Outline: [LLGG_9] Creazione di un template in stato DRAFT impostando il flagPersonalData (Scenario 22, 23, 24)
    Given l'utente è un "<ruolo>" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità <modo> in stato di DRAFT con flagPersonalData impostato a "<flagPersonalData>"
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    Then si ottiene response status code 200
    And l'utente effettua la pubblicazione dell'e-service template
    Then si ottiene response status code <statusCode>

    Examples:
      | ruolo | modo       | flagPersonalData | statusCode |
      | admin | erogazione | true             | 200        |
      | admin | erogazione | false            | 200        |
      | admin | erogazione | undefined        | 400        |

  Scenario Outline: [LLGG_10] Creazione di un template in stato DRAFT impostando il flagPersonalData (Scenario 25)
    Given l'utente è un "<ruolo>" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità <modo> in stato di DRAFT con flagPersonalData impostato a "<flagPersonalData>"
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    Then si ottiene response status code 200
    And l'utente effettua la pubblicazione dell'e-service template
    Then si ottiene response status code <statusCode>

    Examples:
      | ruolo | modo       | flagPersonalData | statusCode |
      | admin | erogazione | true             | 200        |
      | admin | erogazione | false            | 200        |
      | admin | erogazione | undefined        | 400        |

    #TODO: la modifica restituisce 204 non 200, bisogna correggere lo scenario
  Scenario Outline: [LLGG_11] Modifica di un template in stato DRAFT con il flagPersonalData presente (Scenario 25, 26, 27)
    Given l'utente è un "<ruolo>" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità <modo> in stato di DRAFT con flagPersonalData impostato a "<flagPersonalData>"
    Then si ottiene response status code 200
    And l'utente tenta delle modifiche alla versione dell'e-service template
    And le modifiche alla versione sono state applicate correttamente
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua la pubblicazione dell'e-service template
    Then si ottiene response status code <statusCode>

    Examples:
      | ruolo | modo       | flagPersonalData | statusCode |
      | admin | erogazione | true             | 200        |
      | admin | erogazione | false            | 200        |
      | admin | erogazione | undefined        | 400        |

  Scenario Outline: [LLGG_12] Setting flagPersonalData passando un eServiceTemplateId inesistente (Scenario 30)
    Given l'utente è un "admin" di "PA1"
    When viene settato il personalDataFlag a "<personalDataFlag>" passando un "eServiceTemplateId" inesistente
    Then si ottiene lo status code 404

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  Scenario Outline: [LLGG_13] Setting flagPersonalData passando un eServiceTemplateId inesistente (Scenario 32)
    Given l'utente è un "admin" di "PA1"
    When viene settato il personalDataFlag a "<personalDataFlag>" passando un "eServiceTemplateId" inesistente e un token invalido
    Then si ottiene lo status code 401

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  Scenario Outline: [LLGG_14] Setting flagPersonalData per eServiceTemplate usando ruoli differenti (Scenario 33)
    Given l'utente è un "<ruolo>" di "<ente>"
    When viene settato il personalDataFlag a "true" passando un "eServiceTemplateId" inesistente
    Then si ottiene lo status code <statusCode>

    Examples:
      | ente | ruolo    | statusCode |
      | GSP  | security | 403        |
      | GSP  | support  | 403        |
      | PA1  | security | 403        |
      | PA1  | support  | 403        |

  Scenario Outline: [LLGG_15] Creazione e-service-template con diverse combinazioni di flagPersonalData e setting a posteriori del medesimo (Scenario 34)
    Given l'utente è un "admin" di "PA1"
    Given l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <statoVersione> con flagPersonalData impostato a "true"
    Then si ottiene status code 200
    Then viene settato il personalDataFlag a "false" nell'eservice template appena creato
    Then si ottiene status code 409

    Examples:
      | statoVersione |
      | PUBLISHED     |
      | SUSPENDED     |
      | DEPRECATED    |

  Scenario Outline: [LLGG_16] Istanza di un eservice a partire da un template in cui è stato settato il flag relativo ai dati personali
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con flagPersonalData impostato a "<personalDataFlag>"
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    Then si ottiene status code 200
    And verifica che il flagPersonalData presente nell'istanza dell'eServiceTemplate coincida con quanto specificato nel template

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  Scenario Outline: [LLGG_17] Verifica del flag dati personali in una nuova versione del template eService (Scenario 41)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT con flagPersonalData impostato a "<personalDataFlag>"
    And l'utente tenta delle modifiche alla versione dell'e-service template
    Then si ottiene response status code 200
    And le modifiche alla versione sono state applicate correttamente
    And verifica che il flagPersonalData presente nella nuova versione dell'eServiceTemplate sia "<personalDataFlag>"

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  Scenario Outline: [LLGG_18] Verifica che la pubblicazione di un e-service template sia effettuabile solo specificando il flag sui dati personali (Scenario 42)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT con flagPersonalData impostato a "<flagPersonalData>"
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua la pubblicazione dell'e-service template
    Then si ottiene status code <statusCode>

    Examples:
      | flagPersonalData | statusCode |
      | true             | 200        |
      | false            | 200        |
      | undefined        | 400        |