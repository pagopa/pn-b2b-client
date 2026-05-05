Feature: : Debugger Client Assertion Sync Bearer
  Come Aderente in possesso di un client di tipo API
  Voglio validare la mia Client Assertion standard
  Al fine di identificare errori strutturali, temporali o crittografici nelle tre fasi di validazione (Formale, Recupero Chiave, Firma)

  Scenario: [VALIDATION_SUCCESS_API_CLIENT] Dato un client API valido, quando viene inviata una client assertion corretta allora tutte le fasi di validazione risultano PASSED
    And l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" crea una client assertion valida per un client di tipo API
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED | []     |
      | publicKeyRetrieve                    | PASSED | []     |
      | clientAssertionSignatureVerification | PASSED | []     |

  Scenario: [VALIDATION_ERROR_CODE_0002_API_CLIENT] Dato un client API valido, quando il client_assertion_type è invalido allora la validazione formale fallisce con errore 0002
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion valida per un client di tipo API
    And "PA1" richiede la validazione della client assertion appena creata specificando client_assertion_type="invalid_type" e grant_type="client_credentials"
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors |
      | clientAssertionValidation            | FAILED  | [0002] |
      | publicKeyRetrieve                    | SKIPPED | []     |
      | clientAssertionSignatureVerification | SKIPPED | []     |
