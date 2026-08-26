@delegation-manual-archiving-eservice
Feature: Gestione deleghe per archiviazione manuale e-service

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_1.1] Un ente delegato può richiedere al delegante di avviare il processo di archiviazione di un e-service in delega
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204

    Examples:
      | descriptorState | role         |
      | PUBLISHED       | admin        |
      | PUBLISHED       | api          |
      | PUBLISHED       | api,security |
      | SUSPENDED       | admin        |
      | SUSPENDED       | api          |
      | SUSPENDED       | api,security |


