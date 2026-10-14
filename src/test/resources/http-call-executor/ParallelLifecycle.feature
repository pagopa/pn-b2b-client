# QA-18002: entrambi gli scenari sono attivi e conservano la propria risposta.
Feature: Isolamento del risultato HTTP durante scenari paralleli

  Scenario Outline: [QA-18002-LOCAL-PARALLEL] Le chiamate locali parallele mantengono risultati distinti
    Given lo scenario locale "<scenario>" non ha ancora effettuato chiamate
    And i due scenari locali sono attivi contemporaneamente
    When lo scenario locale "<scenario>" riceve una risposta con status <status>
    And i due scenari locali hanno completato la chiamata
    Then un altro passo dello scenario locale "<scenario>" legge status <status> e lo stesso body

    Examples:
      | scenario        | status |
      | parallel-first  | 201    |
      | parallel-second | 202    |
