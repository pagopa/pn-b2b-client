# QA-18002: verifica locale del lifecycle Cucumber/Spring, senza servizi remoti.
Feature: Condivisione del risultato HTTP e isolamento degli scenari successivi

  Scenario Outline: [QA-18002-LOCAL-SCOPE] I passi condividono il risultato solo nello stesso scenario
    Given lo scenario locale "<scenario>" non ha ancora effettuato chiamate
    When lo scenario locale "<scenario>" riceve una risposta con status <status>
    Then un altro passo dello scenario locale "<scenario>" legge status <status> e lo stesso body

    Examples:
      | scenario          | status |
      | sequential-first  | 201    |
      | sequential-second | 202    |
