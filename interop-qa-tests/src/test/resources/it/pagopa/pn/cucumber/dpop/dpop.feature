Feature: Test di sicurezza e funzionalità dei token interop con DPoP

  Scenario: [DP01] Il campo "token_type" restituito è "DPoP" e il campo "cnf.jkt" è presente e corretto
    # Viene creato il client per PA1 e gli vengono associate le chiavi RSA, viene creata la client assertion e staccato il voucher. Il client di trova ora nel context
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-service

    # Viene creato il client per PA2 e gli vengono associate le chiavi RSA, viene creata la client assertion e staccato il voucher
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin

    #And "PA2" ha già creato un client DPop
    When "PA2" genera una dpop proof con algoritmo "EC" e cerca di ottenere un access token tramite richiesta con header DPoP
    Then si ottiene lo status code 200
