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
