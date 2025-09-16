Feature: Test di sicurezza e funzionalità dei token interop con DPoP


  Scenario: [DP01] Il campo "token_type" restituito è "DPoP" e il campo "cnf.jkt" è presente e corretto
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    Given "PA1" ha già creato 1 client "CONSUMER"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given "PA1" ha già associato la finalità a quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When "PA2" genera una dpop proof con algoritmo "EC" e cerca di ottenere un access token tramite richiesta con header DPoP
    Then si ottiene lo status code 200
