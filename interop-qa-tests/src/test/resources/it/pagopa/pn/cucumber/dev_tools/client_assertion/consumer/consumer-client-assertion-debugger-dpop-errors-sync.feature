Feature: Debugger Client Assertion Sync DPoP Errors
  Come Aderente in possesso di un client di tipo CONSUMER
  Voglio validare la mia DPoP Proof legata a una Client Assertion
  Al fine di verificare il binding di sicurezza (HTM/HTU/JWK) e identificare errori specifici DPoP durante la quarta fase di validazione

  @devToolsClientAssertion @ko-nrt-08072026
  Scenario Outline: [VALIDATION_NOT_FOUND_ERROR_CONSUMER_CLIENT_DPOP_PAYLOAD] Dato un client API valido, quando il claim <claimToRemove> non è presente nel payload allora la validazione formale fallisce con errore <expectedError>
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim    | value           |
      | __remove | <claimToRemove> |
    And "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors            |
      | clientAssertionValidation            | PASSED | []                |
      | publicKeyRetrieve                    | PASSED | []                |
      | clientAssertionSignatureVerification | PASSED | []                |
      | platformStatesVerification           | PASSED | []                |
      | dpopValidation                       | FAILED | [<expectedError>] |

    Examples:
      | claimToRemove | expectedError   |
      | jti           | dpopJtiNotFound |
      | iat           | dpopIatNotFound |
      | htu           | dpopHtuNotFound |
      | htm           | dpopHtmNotFound |

  @devToolsClientAssertion @ko-nrt-08072026
  Scenario Outline: [VALIDATION_NOT_FOUND_ERROR_CONSUMER_CLIENT_DPOP_PAYLOAD] Dato un client CONSUMER valido, quando il claim <claimToRemove> non è presente nel payload allora la validazione formale fallisce con errore <expectedError>
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim    | value           |
      | __remove | <claimToRemove> |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors            |
      | clientAssertionValidation            | PASSED | []                |
      | publicKeyRetrieve                    | PASSED | []                |
      | clientAssertionSignatureVerification | PASSED | []                |
      | platformStatesVerification           | PASSED | []                |
      | dpopValidation                       | FAILED | [<expectedError>] |

    Examples:
      | claimToRemove | expectedError   |
      | jti           | dpopJtiNotFound |
      | iat           | dpopIatNotFound |
      | htu           | dpopHtuNotFound |
      | htm           | dpopHtmNotFound |

  @devToolsClientAssertion @ko-nrt-08072026
  Scenario Outline: [VALIDATION_NOT_FOUND_ERROR_CONSUMER_CLIENT_DPOP_HEADER] Dato un client CONSUMER valido, quando il claim <claimToRemove> non è presente nell'header allora la validazione formale fallisce con errore <expectedError>
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim          | value           |
      | __removeHeader | <claimToRemove> |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors            |
      | clientAssertionValidation            | PASSED | []                |
      | publicKeyRetrieve                    | PASSED | []                |
      | clientAssertionSignatureVerification | PASSED | []                |
      | platformStatesVerification           | PASSED | []                |
      | dpopValidation                       | FAILED | [<expectedError>] |

    Examples:
      | claimToRemove | expectedError         |
      | typ           | dpopTypNotFound       |
      | jwk           | dpopJwkNotFound       |
      | alg           | dpopAlgorithmNotFound |

  @devToolsClientAssertion @ko-nrt-08072026
  Scenario: [VALIDATION_INVALID_FORMAT_ERROR_CONSUMER_CLIENT_DPOP_HEADER] Dato un client CONSUMER valido, quando il payload non è valido allora la validazione formale fallisce con errore invalidDPoPProofFormat
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim        | value           |
      | __rawPayload | invalid_payload |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                   |
      | clientAssertionValidation            | PASSED | []                       |
      | publicKeyRetrieve                    | PASSED | []                       |
      | clientAssertionSignatureVerification | PASSED | []                       |
      | platformStatesVerification           | PASSED | []                       |
      | dpopValidation                       | FAILED | [invalidDPoPProofFormat] |

  @devToolsClientAssertion
  Scenario: [VALIDATION_UNEXPECTED_DPOP_PROOF_ERROR_CONSUMER_CLIENT_DPOP_HEADER] Dato un client CONSUMER valido, quando l'header non è valido allora la validazione formale fallisce con errore unexpectedDPoPProofError
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim       | value          |
      | __rawHeader | invalid_header |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                     |
      | clientAssertionValidation            | PASSED | []                         |
      | publicKeyRetrieve                    | PASSED | []                         |
      | clientAssertionSignatureVerification | PASSED | []                         |
      | platformStatesVerification           | PASSED | []                         |
      | dpopValidation                       | FAILED | [unexpectedDPoPProofError] |

  @devToolsClientAssertion
  Scenario: [VALIDATION_INVALID_TYP_ERROR_CONSUMER_CLIENT_DPOP_HEADER] Dato un client CONSUMER valido, quando l'header contiene un typ non valido allora la validazione formale fallisce con errore invalidDPoPTyp
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim      | value       |
      | header.typ | invalid_typ |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors           |
      | clientAssertionValidation            | PASSED | []               |
      | publicKeyRetrieve                    | PASSED | []               |
      | clientAssertionSignatureVerification | PASSED | []               |
      | platformStatesVerification           | PASSED | []               |
      | dpopValidation                       | FAILED | [invalidDPoPTyp] |

  @devToolsClientAssertion
  Scenario: [VALIDATION_INVALID_HTM_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il payload contiene un htm non valido allora la validazione formale fallisce con errore invalidDPoPHtm
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim | value       |
      | htm   | invalid_htm |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors           |
      | clientAssertionValidation            | PASSED | []               |
      | publicKeyRetrieve                    | PASSED | []               |
      | clientAssertionSignatureVerification | PASSED | []               |
      | platformStatesVerification           | PASSED | []               |
      | dpopValidation                       | FAILED | [invalidDPoPHtm] |

  @devToolsClientAssertion
  Scenario: [VALIDATION_INVALID_HTU_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il payload contiene un htu non valido allora la validazione formale fallisce con errore invalidDPoPHtu
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim | value       |
      | htu   | invalid_htu |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors           |
      | clientAssertionValidation            | PASSED | []               |
      | publicKeyRetrieve                    | PASSED | []               |
      | clientAssertionSignatureVerification | PASSED | []               |
      | platformStatesVerification           | PASSED | []               |
      | dpopValidation                       | FAILED | [invalidDPoPHtu] |

  @devToolsClientAssertion
  Scenario: [VALIDATION_EXPIRED_DPOP_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il payload contiene un claim iat scaduto allora la validazione formale fallisce con errore expiredDPoPProof
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim | value    |
      | iat   | 20250101 |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors             |
      | clientAssertionValidation            | PASSED | []                 |
      | publicKeyRetrieve                    | PASSED | []                 |
      | clientAssertionSignatureVerification | PASSED | []                 |
      | platformStatesVerification           | PASSED | []                 |
      | dpopValidation                       | FAILED | [expiredDPoPProof] |

  @devToolsClientAssertion
  Scenario: [VALIDATION_NOT_YET_VALID_DPOP_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il payload contiene un claim iat scaduto allora la validazione formale fallisce con errore notYetValidDPoPProof
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim | value    |
      | iat   | now+3600 |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                 |
      | clientAssertionValidation            | PASSED | []                     |
      | publicKeyRetrieve                    | PASSED | []                     |
      | clientAssertionSignatureVerification | PASSED | []                     |
      | platformStatesVerification           | PASSED | []                     |
      | dpopValidation                       | FAILED | [notYetValidDPoPProof] |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ALG_NOT_ALLOWED_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando l'algoritmo non è valido allora la validazione formale fallisce con errore dpopAlgorithmNotAllowed
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim      | value |
      | header.alg | ABC   |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                    |
      | clientAssertionValidation            | PASSED | []                        |
      | publicKeyRetrieve                    | PASSED | []                        |
      | clientAssertionSignatureVerification | PASSED | []                        |
      | platformStatesVerification           | PASSED | []                        |
      | dpopValidation                       | FAILED | [dpopAlgorithmNotAllowed] |

  @devToolsClientAssertion
  Scenario: [VALIDATION_INVALID_CLAIM_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il DPoPProof contiene un claim non valido allora la validazione formale fallisce con errore dpopProofInvalidClaims
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion con:
      | claim        | value         |
      | invalidClaim | invalid_claim |
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                   |
      | clientAssertionValidation            | PASSED | []                       |
      | publicKeyRetrieve                    | PASSED | []                       |
      | clientAssertionSignatureVerification | PASSED | []                       |
      | platformStatesVerification           | PASSED | []                       |
      | dpopValidation                       | FAILED | [dpopProofInvalidClaims] |

  @devToolsClientAssertion
  Scenario: [VALIDATION_INVALID_SIGNATURE_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il DPoPProof è firmato in modo non valido la validazione formale fallisce con errore invalidDPoPSignature
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA11" crea una DPoP proof con firma non valida
    When "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                 |
      | clientAssertionValidation            | PASSED | []                     |
      | publicKeyRetrieve                    | PASSED | []                     |
      | clientAssertionSignatureVerification | PASSED | []                     |
      | platformStatesVerification           | PASSED | []                     |
      | dpopValidation                       | FAILED | [invalidDPoPSignature] |
