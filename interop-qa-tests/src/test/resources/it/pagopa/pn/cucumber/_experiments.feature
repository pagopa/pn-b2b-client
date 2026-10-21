Feature: File feature unicamente per sperimentazione tecnica; non implementa alcuna logica di testing reale
  Scenario: Check status code variabile per set di api
    And si ottengono i seguenti response status codes: 200 per BFF V1
    And si ottengono i seguenti response status codes: 200 per BFF V1, 300 per M2M V2, 400 per M2M V3

  Scenario: [DP_MANY_PURPOSES_1] Crea un client ID con N finalità associate e ciascuna associata ad un e-service
    Given l'utente è un "admin" di "PA2"
    And l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And PA1 pubblica un e-service e PA2 inoltra una finalità che associa al client e si ripete il flusso 50 volte
