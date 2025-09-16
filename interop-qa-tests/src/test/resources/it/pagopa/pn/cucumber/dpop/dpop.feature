Feature: Test di sicurezza e funzionalità dei token interop con DPoP

  Scenario: [DP01] Il campo "token_type" restituito è "DPoP" e il campo "cnf.jkt" è presente e corretto
    Given un tenant "PA1" configurato correttamente
    And genero un client M2M con chiave EC P-256 e lo registro per il tenant "PA1"
    When genero un access token tramite richiesta con header DPoP
    Then la risposta contiene lo status 200
    And la risposta JSON contiene il campo "token_type" con valore "DPoP"
    And il campo "cnf.jkt" è presente nel token ed è uguale al thumbprint della chiave pubblica registrata
