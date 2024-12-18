Feature: Creazione di una delega

  Background:
    Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe

  Scenario Outline: Il richiamo dell’API di creazione di una delega possa essere compiuto da un utente di livello operatore amministrativo (admin)
    Given l'utente è un "<ruolo>" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega per l'ente "PA2"
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo        | statusCode |
      | admin        |        200 |
      | api          |        403 |
      | security     |        403 |
      | api,security |        403 |
      | support      |        403 |