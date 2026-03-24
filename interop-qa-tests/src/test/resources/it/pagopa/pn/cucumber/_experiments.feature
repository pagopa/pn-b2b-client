Feature: File feature unicamente per sperimentazione tecnica; non implementa alcuna logica di testing reale
  Scenario: Check status code variabile per set di api
    And si ottengono i seguenti response status codes: 200 per BFF V1
    And si ottengono i seguenti response status codes: 200 per BFF V1, 300 per M2M V2, 400 per M2M V3

  Scenario: Produzione tokens per utilità in test manuali
    And l'utente è un "admin" di "PA1"
    And l'utente è un "admin" di "PA2"
    And l'utente è un "admin" di "GSP"
    And l'utente è un "admin" di "GSP2"
    And l'utente è un "admin" di "Private"

  # Sperimentazione per test eventi m2m
  Scenario: trigger evento aggiornamento eservice con delega
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And ["PA1" prende nota dell'ultimo evento presente di tipo e-service]
    And "PA1" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC"
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    When "PA2" aggiorna quell'e-service

  Scenario: trigger evento aggiornamento eservice con delega compattato
    And "PA1" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC"
    Given l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2"
    When "PA2" aggiorna quell'e-service

  Scenario: Si ottiene l'ultimo event id per un certo canale
    And ["PA1" prende nota dell'ultimo evento presente di tipo e-service template]
    And l'ente "PA1" effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
