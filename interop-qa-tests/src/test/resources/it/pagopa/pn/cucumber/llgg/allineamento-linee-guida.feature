Feature: Allineamento linee guida

  Scenario Outline: [LLGG_1] Creazione e-service con flagPersonalData a true (Scenario 1,2,3)
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