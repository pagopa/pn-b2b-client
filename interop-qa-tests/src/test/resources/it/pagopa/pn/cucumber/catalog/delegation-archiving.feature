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

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_1.2] Un ente delegato può richiedere al delegante di avviare il processo di archiviazione del descrittore meno recente di un e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 204

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_2.1] Un ente delegante può accettare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "<role>" di "PA1"
    When l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 204

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

