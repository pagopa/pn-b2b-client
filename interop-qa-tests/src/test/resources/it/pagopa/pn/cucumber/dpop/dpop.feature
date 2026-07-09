@DPoPSuite
Feature: Test di sicurezza e funzionalità dei token interop con DPoP

  Scenario Outline: [DP01-API] Il campo "token_type" restituito è "DPoP" e il campo "cnf.jkt" è presente e corretto
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA1" genera una dpop proof con una chiave "<keyType>" e verifica i campi HTU,HTM
    And "PA1" cerca di ottenere un access token per il client "API" usando il dpop proof creato
    Then si ottiene lo status code 200
    And la response contiene:
      | tokenType   | DPoP     |
      | accessToken | non_null |
      | expiresIn   | non_null |
    And il campo cnf.jkt è presente e corretto
    Examples:
      | keyType |
      | EC      |
      | RSA     |

  Scenario Outline: [DP01] Il campo "token_type" restituito è "DPoP" e il campo "cnf.jkt" è presente e corretto (Scenario 1,6,7,12)
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave "RSA" pubblica nel client
    When "PA1" genera una dpop proof con una chiave "<keyType>" e verifica i campi HTU,HTM
    And "PA1" cerca di ottenere un access token per il client "CONSUMER" usando il dpop proof creato
    Then si ottiene lo status code 200
    And la response contiene:
      | tokenType   | DPoP     |
      | accessToken | non_null |
      | expiresIn   | non_null |
    And il campo cnf.jkt è presente e corretto
    Examples:
      | keyType |
      | EC      |
      | RSA     |

  Scenario: [DP02] La presenza di due header DPoP inibisce la creazione del token
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave "RSA" pubblica nel client
    When "PA1" genera una dpop proof con una chiave "EC"
    When "PA1" tenta di ottenere un access token per il client "CONSUMER" usando il dpop proof creato e inviando due header DPoP nella richiesta
    Then si ottiene lo status code 400
    And la response contiene:
      | tokenType   | null |
      | accessToken | null |
      | expiresIn   | null |

  @ko-nrt-08072026
  Scenario: [DP03] Access token generato senza header DPoP deve essere di tipo "Bearer"
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave "RSA" pubblica nel client
    When "PA1" tenta di ottenere un access token per il client "CONSUMER" senza includere l'header DPoP
    Then si ottiene lo status code 200
    And la response contiene:
      | tokenType   | Bearer   |
      | accessToken | non_null |
      | expiresIn   | non_null |

  Scenario: [DP04] Richiesta access token con JWT DPoP errato (typ diverso da "dpop+jwt") restituisce errore 400
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave "RSA" pubblica nel client
    When "PA1" genera una dpop proof con una chiave "EC" e campo typ errato
    And "PA1" cerca di ottenere un access token per il client "CONSUMER" usando il dpop proof creato
    Then si ottiene lo status code 400
    And la response contiene:
      | tokenType   | null |
      | accessToken | null |
      | expiresIn   | null |

  Scenario: [DP05] Il riutilizzo della stessa DPoP proof non consente la creazione di un secondo access token
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave "RSA" pubblica nel client
    When "PA1" genera una dpop proof con una chiave "EC"
    And "PA1" cerca di ottenere un access token per il client "CONSUMER" usando il dpop proof creato
    And si ottiene lo status code 200
    And la response contiene:
      | tokenType   | DPoP     |
      | accessToken | non_null |
      | expiresIn   | non_null |
    When "PA2" cerca di ottenere un access token per il client "CONSUMER" usando il dpop proof creato
    Then si ottiene lo status code 400
    And la response contiene:
      | tokenType   | null |
      | accessToken | null |
      | expiresIn   | null |

  Scenario: [DP06] Richiesta access token con metodo HTM errato nel JWT DPoP restituisce errore 400
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave "RSA" pubblica nel client
    When "PA1" genera una dpop proof con una chiave "EC" e metodo errato "GET"
    And "PA1" cerca di ottenere un access token per il client "CONSUMER" usando il dpop proof creato
    Then si ottiene lo status code 400
    And la response contiene:
      | tokenType   | null |
      | accessToken | null |
      | expiresIn   | null |

  Scenario: [DP07] Creazione token fallisce se il campo HTU contiene una URL non attesa
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave "RSA" pubblica nel client
    When "PA1" genera una dpop proof con una chiave "EC" e campo HTU errato
    And "PA1" cerca di ottenere un access token per il client "CONSUMER" usando il dpop proof creato
    Then si ottiene lo status code 400
    And la response contiene:
      | tokenType   | null |
      | accessToken | null |
      | expiresIn   | null |

  Scenario: [DP08] La richiesta access token con DPoP contenente un campo IAT scaduto restituisce errore 400
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave "RSA" pubblica nel client
    When "PA1" genera una dpop proof con una chiave "EC" scaduto rispetto il campo IAT
    And "PA1" cerca di ottenere un access token per il client "CONSUMER" usando il dpop proof creato
    Then si ottiene lo status code 400
    And la response contiene:
      | tokenType   | null |
      | accessToken | null |
      | expiresIn   | null |

  Scenario: [DP09] La DPoP proof è firmata con una chiave privata non corrispondente alla chiave pubblica dell’ente legittimo
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave "RSA" pubblica nel client
    When "PA1" genera una dpop proof usando la chiave pubblica "EC" di una key pair legittima ma firmando con una chiave privata diversa
    And "PA1" cerca di ottenere un access token per il client "CONSUMER" usando il dpop proof creato
    Then si ottiene lo status code 400
    And la response contiene:
      | tokenType   | null |
      | accessToken | null |
      | expiresIn   | null |
