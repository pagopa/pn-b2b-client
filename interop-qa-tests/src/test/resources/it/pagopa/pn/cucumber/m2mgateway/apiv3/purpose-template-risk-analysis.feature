@m2m-purpose-template
Feature: Op. di gestione risk analysis dei purpose template

  @purpose-template-risk-analysis
  Scenario Outline: [PURPOSE_TEMPLATE_UPDATE_WITH_URL] Modifica di una finalità agevolata indicando un URL come indirizzo dell'informativa sul trattamento dei dati personali
    Given l'utente è un "admin" di "PA2"
    And viene creato un nuovo purpose template destinato a enti "<kind>" indicando un URL come indirizzo dell'informativa sul trattamento dei dati personali
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And viene modificato il purpose template destinato a enti "<kind>" indicando un URL casuale come indirizzo dell'informativa sul trattamento dei dati personali
    Then si ottiene status code 200
    Examples:
      | kind    |
      | PA1     |
      | GSP     |
      | Privato |