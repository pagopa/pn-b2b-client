Feature: Allineamento linee guida

  @llgg
  Scenario Outline: [LLGG_1] Creazione e-service in modalità "DELIVER" con diverse combinazioni di flagPersonalData
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

  @llgg
  Scenario Outline: [LLGG_1.1] Creazione e-service in modalità "DELIVER" con diverse combinazioni di flagPersonalData e verifica del descrittore
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

  @llgg
  Scenario Outline: [LLGG_1.2] Creazione e-service in modalità "DELIVER" con diverse combinazioni di flagPersonalData e verifica del dettaglio
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED" e flag dati personali a "<personalDataFlag>"
    Then si ottiene status code 200
    Then verifica che il flagPersonalData presente nell'eService sia "<personalDataFlag>"

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  @llgg
  Scenario Outline: [LLGG_2] Creazione e-service in modalità "RECEIVE" con diverse combinazioni di flagPersonalData
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

  @llgg
  Scenario Outline: [LLGG_2.1] Creazione e-service in modalità "RECEIVE" con diverse combinazioni di flagPersonalData e verifica del descrittore
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

  @llgg
  Scenario Outline: [LLGG_2.2] Creazione e-service in modalità "RECEIVE" con diverse combinazioni di flagPersonalData e verifica del dettaglio
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

  @llgg
  Scenario Outline: [LLGG_3] Aggiornamento descrittore in modalità DELIVER di un eService comprendente il flag dati
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

  @llgg
  Scenario Outline: [LLGG_4] Aggiornamento descrittore in modalità RECIVE di un eService comprendente il flag dati
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

  @llgg
  Scenario Outline: [LLGG_4.1] Aggiornamento descrittore in modalità RECIVE di un eService comprendente un flag dati incoerente tra risk analysis e eService
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT" e flag dati personali a "<personalDataFlag>"
    Then si ottiene status code 200
    When verifica che la versione 3.1 dell'analisi del rischio includa domande inerenti all'uso di dati personali
    When l'utente aggiunge un'analisi del rischio con un flag relativo ai dati personali impostato a "<riskAnalysisPersonalDataFlag>"
    Then si ottiene status code 400

    Examples:
      | personalDataFlag | riskAnalysisPersonalDataFlag |
      | false            | true                         |
      | true             | false                        |

  @llgg
  Scenario Outline: [LLGG_5] Setting flagPersonalData passando un eServiceId inesistente
    Given l'utente è un "admin" di "PA1"
    When viene settato il personalDataFlag a "<personalDataFlag>" passando un "eServiceId" inesistente
    Then si ottiene lo status code 404

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  @llgg
  Scenario Outline: [LLGG_6] Setting flagPersonalData usando un token invalido
    Given l'utente è un "admin" di "PA1"
    When viene impostato per l'utente un token non valido
    When viene settato il personalDataFlag a "<personalDataFlag>" passando un "eServiceId" inesistente
    Then si ottiene lo status code 401

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  @llgg
  Scenario Outline: [LLGG_7] Setting flagPersonalData usando ruoli differenti
    Given l'utente è un "<ruolo>" di "<ente>"
    When viene settato il personalDataFlag a "true" passando un "eServiceId" inesistente
    Then si ottiene lo status code <statusCode>

    Examples:
      | ente | ruolo    | statusCode |
      | GSP  | security | 403        |
      | GSP  | support  | 403        |
      | PA1  | security | 403        |
      | PA1  | support  | 403        |

  @llgg
  Scenario Outline: [LLGG_8] Creazione e-service in modalità "DELIVER" con diverse combinazioni di flagPersonalData e setting a posteriori del medesimo
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

  @llgg
  Scenario Outline: [LLGG_9] Creazione di un template in stato DRAFT impostando il flagPersonalData
    Given l'utente è un "<ruolo>" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT con flagPersonalData impostato a "<flagPersonalData>"
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    Then si ottiene response status code 200
    And l'utente effettua la pubblicazione dell'e-service template
    Then si ottiene response status code <statusCode>

    Examples:
      | ruolo | flagPersonalData | statusCode |
      | admin | true             | 200        |
      | admin | false            | 200        |
      | admin | undefined        | 400        |


  @llgg
  Scenario Outline: [LLGG_10] Creazione di un template in stato DRAFT impostando il flagPersonalData
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

  @llgg
  Scenario Outline: [LLGG_11] Modifica di un template in stato DRAFT con il flagPersonalData presente
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

  @llgg
  Scenario Outline: [LLGG_12] Setting flagPersonalData passando un eServiceTemplateId inesistente
    Given l'utente è un "admin" di "PA1"
    When viene settato il personalDataFlag a "<personalDataFlag>" passando un "eServiceTemplateId" inesistente
    Then si ottiene lo status code 404

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  @llgg
  Scenario Outline: [LLGG_13] Setting flagPersonalData passando un eServiceTemplateId inesistente
    Given l'utente è un "admin" di "PA1"
    When viene impostato per l'utente un token non valido
    When viene settato il personalDataFlag a "<personalDataFlag>" passando un "eServiceTemplateId" inesistente
    Then si ottiene lo status code 401

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  @llgg
  Scenario Outline: [LLGG_14] Setting flagPersonalData per eServiceTemplate usando ruoli differenti
    Given l'utente è un "<ruolo>" di "<ente>"
    When viene settato il personalDataFlag a "true" passando un "eServiceTemplateId" inesistente
    Then si ottiene lo status code <statusCode>

    Examples:
      | ente | ruolo    | statusCode |
      | GSP  | security | 403        |
      | GSP  | support  | 403        |
      | PA1  | security | 403        |
      | PA1  | support  | 403        |

  @llgg
  Scenario Outline: [LLGG_15] Creazione e-service-template con diverse combinazioni di flagPersonalData e setting a posteriori del medesimo
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

  @llgg
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

  @llgg
  Scenario Outline: [LLGG_17] Verifica del flag dati personali in una nuova versione del template eService
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

  @llgg
  Scenario Outline: [LLGG_18] Verifica che la pubblicazione di un e-service template sia effettuabile solo specificando il flag sui dati personali
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

  @llgg
  Scenario Outline: [LLGG_19] Verifica che in fase di creazione di una delega sia funzionante il filtro per flagPersonalData per gli eServices
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 e-services in catalogo in stato PUBLISHED o SUSPENDED e 0 in stato DRAFT impostando il flagPersonalData a "<flagPersonalData>"
    Then i 1 e-service recuperati hanno il flagPersonalData settato a "<flagPersonalData>"

    Examples:
      | flagPersonalData |
      | true             |
      | false            |

  @llgg
  Scenario Outline: [LLGG_20] Verifica che in fase di creazione di una delega sia funzionante il filtro per flagPersonalData per gli eServicesTemplate
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con flagPersonalData impostato a "<flagPersonalData>"
    Then i 1 e-service template recuperati hanno il flagPersonalData settato a "<flagPersonalData>"

    Examples:
      | flagPersonalData |
      | true             |
      | false            |

  @llgg
  @deleghe2 @ko-nrt-08072026
  Scenario Outline: [LLGG_21] Verifica la pubblicazione di un e-service da parte di un ente delegato all'erogazione settando il flag personal data
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    Given "PA1" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "DRAFT" e flag dati personali a "<personalDataFlag>"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega
    And l'utente è un "admin" di "PA2"
    When l'utente pubblica l'e-service
    Then si ottiene lo status code 200
    And l'e-service è in stato "WAITING_FOR_APPROVAL"

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  @llgg
  Scenario Outline: [LLGG_22] Verifica che la purpose sia attivata quando i valori del flagPersonalData coincidono
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED" e flag dati personali a "<personalDataFlag>"
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice con flagPersonalData impostato a "<personalDataFlag>"
    When l'utente attiva la finalità in stato "WAITING_FOR_APPROVAL" per quell'e-service
    Then si ottiene status code 200

    Examples:
      | personalDataFlag |
      | true             |
      | false            |

  @llgg
  Scenario Outline: [LLGG_23] Verifica che la purpose non sia attivata quando i valori del flagPersonalData non coincidono
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato un e-service in modalità "DELIVER" con un descrittore in stato "PUBLISHED" e flag dati personali a "<personalDataFlag>"
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice con flagPersonalData impostato a "<personalDataFlag2>"
    Then si ottiene status code 400

    Examples:
      | personalDataFlag | personalDataFlag2 |
      | true             | false             |
      | false            | true              |

