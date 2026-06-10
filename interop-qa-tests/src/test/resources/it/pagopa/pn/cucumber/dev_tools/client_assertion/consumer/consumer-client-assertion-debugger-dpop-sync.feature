Feature: Debugger Client Assertion Sync DPoP
  Come Aderente in possesso di un client di tipo CONSUMER
  Voglio validare la mia DPoP Proof legata a una Client Assertion
  Al fine di verificare il binding di sicurezza (HTM/HTU/JWK) e identificare errori specifici DPoP durante la quarta fase di validazione

  @devToolsClientAssertion
  Scenario: [VALIDATION_SUCCESS_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando viene inviata una client assertion corretta allora tutte le fasi di validazione risultano PASSED
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED | []     |
      | publicKeyRetrieve                    | PASSED | []     |
      | clientAssertionSignatureVerification | PASSED | []     |
      | platformStatesVerification           | PASSED | []     |
      | dpopValidation                       | PASSED | []     |

  @devToolsClientAssertion
  Scenario Outline: [VALIDATION_INVALID_TYPE_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il grant_type è <grant_type> e la client_assertion_type è <client_assertion_type>  allora la validazione formale fallisce con errore <expectedError>
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata specificando client_assertion_type="<client_assertion_type>" e grant_type="<grant_type>"
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [<expectedError>] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |
      | platformStatesVerification           | SKIPPED | []                |
      | dpopValidation                       | PASSED  | []                |

    Examples:
      | client_assertion_type                                  | grant_type         | expectedError                          |
      | invalid_type                                           | client_credentials | invalidAssertionType                   |
      | urn:ietf:params:oauth:client-assertion-type:jwt-bearer | authorization_code | invalidGrantType                       |
      | invalid_type                                           | authorization_code | invalidAssertionType, invalidGrantType |

  @devToolsClientAssertion
  Scenario: [VALIDATION_INVALID_AUD_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando l'audience è invalida allora la validazione formale fallisce con errore 0004
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim | value            |
      | aud   | invalid_audience |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [invalidAudience] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |
      | platformStatesVerification           | SKIPPED | []                |
      | dpopValidation                       | PASSED  | []                |

  @devToolsClientAssertion
  Scenario Outline: [VALIDATION_NOT_FOUND_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim <claimToRemove> non è presente allora la validazione formale fallisce con errore <expectedError>"
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim    | value           |
      | __remove | <claimToRemove> |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [<expectedError>] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |
      | platformStatesVerification           | SKIPPED | []                |
      | dpopValidation                       | PASSED  | []                |

    Examples:
      | claimToRemove | expectedError    |
      | aud           | audienceNotFound |
      | jti           | jtiNotFound      |
      | iat           | issuedAtNotFound |
      | exp           | expNotFound      |
      | iss           | issuerNotFound   |
      | sub           | subjectNotFound  |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_COMBINED_NOT_FOUND_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando i claim jti, iat, aud non sono presenti allora la validazione formale fallisce con i rispettivi errori
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim    | value |
      | __remove | jti   |
      | __remove | iat   |
      | __remove | aud   |
      | __remove | exp   |
      | __remove | iss   |
      | __remove | sub   |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                                                                                          |
      | clientAssertionValidation            | FAILED  | [jtiNotFound, issuedAtNotFound, audienceNotFound, expNotFound, issuerNotFound, subjectNotFound] |
      | publicKeyRetrieve                    | SKIPPED | []                                                                                              |
      | clientAssertionSignatureVerification | SKIPPED | []                                                                                              |
      | platformStatesVerification           | SKIPPED | []                                                                                              |
      | dpopValidation                       | PASSED  | []                                                                                              |

  @devToolsClientAssertion
  Scenario: [VALIDATION_EXPIRED_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il token è scaduto allora la validazione formale fallisce con errore 0017
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim | value     |
      | exp   | now-10800 |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors              |
      | clientAssertionValidation            | PASSED  | []                  |
      | publicKeyRetrieve                    | PASSED  | []                  |
      | clientAssertionSignatureVerification | FAILED  | [tokenExpiredError] |
      | platformStatesVerification           | SKIPPED | []                  |
      | dpopValidation                       | PASSED  | []                  |

  # Errore non riproducibile con la configurazione usata per l'ambiente
  @ignore
  @devToolsClientAssertion
  Scenario: [VALIDATION_JWT_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il JWT non è interpretabile allora la validazione formale fallisce con errore jsonWebTokenError
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value       |
      | __rawPayload | invalid_jwt |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors              |
      | clientAssertionValidation            | FAILED  | [jsonWebTokenError] |
      | publicKeyRetrieve                    | SKIPPED | []                  |
      | clientAssertionSignatureVerification | SKIPPED | []                  |
      | platformStatesVerification           | SKIPPED | []                  |
      | dpopValidation                       | PASSED  | []                  |

  @devToolsClientAssertion
  Scenario: [VALIDATION_UNEXPECTED_PAYLOAD_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il JWT ha un payload non atteso allora la validazione formale fallisce con errore unexpectedClientAssertionPayload
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value          |
      | __rawHeader | invalid_header |
    And "PA1" crea una DPoP proof per la client assertion
    When "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                      |
      | clientAssertionValidation            | FAILED  | [unexpectedClientAssertion] |
      | publicKeyRetrieve                    | SKIPPED | []                          |
      | clientAssertionSignatureVerification | SKIPPED | []                          |
      | platformStatesVerification           | SKIPPED | []                          |
      | dpopValidation                       | PASSED  | []                          |

  @devToolsClientAssertion
  Scenario: [VALIDATION_INVALID_FORMAT_ERROR_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando la client assertion è malformata allora la validazione formale fallisce con errore invalidClientAssertionFormat
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value          |
      | __rawPayload | malformed_json |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                         |
      | clientAssertionValidation            | FAILED  | [invalidClientAssertionFormat] |
      | publicKeyRetrieve                    | SKIPPED | []                             |
      | clientAssertionSignatureVerification | SKIPPED | []                             |
      | platformStatesVerification           | SKIPPED | []                             |
      | dpopValidation                       | PASSED  | []                             |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CODE_0019_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim nbf è nel futuro allora la validazione formale fallisce con errore clientAssertionInvalidClaims
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim | value    |
      | nbf   | now+3600 |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                         |
      | clientAssertionValidation            | FAILED  | [clientAssertionInvalidClaims] |
      | publicKeyRetrieve                    | SKIPPED | []                             |
      | clientAssertionSignatureVerification | SKIPPED | []                             |
      | platformStatesVerification           | SKIPPED | []                             |
      | dpopValidation                       | PASSED  | []                             |

  #Bug aperto: https://pagopa.atlassian.net/browse/PIN-9993
  # 2026-05-08 per il momento non verrà applicato nessun fix, vedi https://pagopa.atlassian.net/browse/PIN-9540
  @devToolsClientAssertion
  Scenario Outline: [VALIDATION_INVALID_CLAIM_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim <claim> non è in formato valido allora la validazione formale fallisce con errore <expectedError>
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim   | value   |
      | <claim> | <value> |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [<expectedError>] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |
      | platformStatesVerification           | SKIPPED | []                |
      | dpopValidation                       | PASSED  | []                |

    @wait_for_fix
    Examples:
      | claim | value      | expectedError         |
      | iss   | not-a-uuid | invalidClientIdFormat |

    Examples:
      | claim | value      | expectedError        |
      | sub   | not-a-uuid | invalidSubjectFormat |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_KID_NOT_FOUND_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim kid non è presente allora il recupero della chiave pubblica fallisce con errore kidNotFound
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim          | value |
      | __removeHeader | kid   |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors        |
      | clientAssertionValidation            | FAILED  | [kidNotFound] |
      | publicKeyRetrieve                    | SKIPPED | []            |
      | clientAssertionSignatureVerification | SKIPPED | []            |
      | platformStatesVerification           | SKIPPED | []            |
      | dpopValidation                       | PASSED  | []            |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_INVALID_KID_FORMAT_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim kid non è in formato valido allora il recupero della chiave pubblica fallisce con errore invalidKidFormat
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim      | value                  |
      | header.kid | not-a-valid-kid-format |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors             |
      | clientAssertionValidation            | FAILED  | [invalidKidFormat] |
      | publicKeyRetrieve                    | SKIPPED | []                 |
      | clientAssertionSignatureVerification | SKIPPED | []                 |
      | platformStatesVerification           | SKIPPED | []                 |
      | dpopValidation                       | PASSED  | []                 |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_INVALID_SUBJECT_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il subject non corrisponde al client atteso allora il recupero della chiave pubblica fallisce con errore invalidSubject
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim | value                                |
      | sub   | 00000000-0000-0000-0000-000000000000 |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors           |
      | clientAssertionValidation            | FAILED  | [invalidSubject] |
      | publicKeyRetrieve                    | SKIPPED | []               |
      | clientAssertionSignatureVerification | SKIPPED | []               |
      | platformStatesVerification           | SKIPPED | []               |
      | dpopValidation                       | PASSED  | []               |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_PURPOSE_ID_NOT_PROVIDED_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim purposeId non è presente allora il recupero della chiave pubblica fallisce con errore purposeIdNotProvided
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim    | value     |
      | __remove | purposeId |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                 |
      | clientAssertionValidation            | PASSED  | []                     |
      | publicKeyRetrieve                    | FAILED  | [purposeIdNotProvided] |
      | clientAssertionSignatureVerification | SKIPPED | []                     |
      | platformStatesVerification           | SKIPPED | []                     |
      | dpopValidation                       | PASSED  | []                     |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_INVALID_PURPOSE_ID_FORMAT_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim purposeId non è in formato UUID allora il recupero della chiave pubblica fallisce con errore invalidPurposeIdClaimFormat
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim     | value      |
      | purposeId | not-a-uuid |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                        |
      | clientAssertionValidation            | FAILED  | [invalidPurposeIdClaimFormat] |
      | publicKeyRetrieve                    | SKIPPED | []                            |
      | clientAssertionSignatureVerification | SKIPPED | []                            |
      | platformStatesVerification           | SKIPPED | []                            |
      | dpopValidation                       | PASSED  | []                            |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_INVALID_PURPOSE_STATE_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando la finalità è in stato non valido allora il recupero della chiave pubblica fallisce con errore invalidPurposeState
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato una finalità in stato "SUSPENDED" a quel client
    When il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                |
      | clientAssertionValidation            | PASSED | []                    |
      | publicKeyRetrieve                    | PASSED | []                    |
      | clientAssertionSignatureVerification | PASSED | []                    |
      | platformStatesVerification           | FAILED | [invalidPurposeState] |
      | dpopValidation                       | PASSED | []                    |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_INVALID_AGREEMENT_STATE_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando l'agreement è in stato non valido allora il recupero della chiave pubblica fallisce con errore invalidAgreementState
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And l'utente è un "admin" di "PA1"
    When l'utente richiede una operazione di sospensione di quella richiesta di fruizione
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                  |
      | clientAssertionValidation            | PASSED | []                      |
      | publicKeyRetrieve                    | PASSED | []                      |
      | clientAssertionSignatureVerification | PASSED | []                      |
      | platformStatesVerification           | FAILED | [invalidAgreementState] |
      | dpopValidation                       | PASSED | []                      |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_INVALID_ESERVICE_STATE_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando l'e-service è in stato non valido allora il recupero della chiave pubblica fallisce con errore invalidEServiceState
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And l'utente è un "admin" di "PA2"
    When l'utente sospende quel descrittore
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                 |
      | clientAssertionValidation            | PASSED | []                     |
      | publicKeyRetrieve                    | PASSED | []                     |
      | clientAssertionSignatureVerification | PASSED | []                     |
      | platformStatesVerification           | FAILED | [invalidEServiceState] |
      | dpopValidation                       | PASSED | []                     |

  @devToolsClientAssertion
  Scenario: [KEY_RETREIVE_ALGORITHM_NOT_ALLOWED_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando l'e-service è in stato non valido allora il recupero della chiave pubblica fallisce con errore algorithmNotAllowed
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER utilizzando una chiave "EC" di lunghezza 1024
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                |
      | clientAssertionValidation            | FAILED  | [algorithmNotAllowed] |
      | publicKeyRetrieve                    | SKIPPED | []                    |
      | clientAssertionSignatureVerification | SKIPPED | []                    |
      | platformStatesVerification           | SKIPPED | []                    |
      | dpopValidation                       | PASSED  | []                    |

  @devToolsClientAssertion
  Scenario: [KEY_RETREIVE_INVALID_SIGNATURE_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando l'e-service è in stato non valido allora il recupero della chiave pubblica fallisce con errore invalidSignature
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER utilizzando una chiave "RSA" di lunghezza 2048
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                             |
      | clientAssertionValidation            | PASSED  | []                                 |
      | publicKeyRetrieve                    | FAILED  | [clientAssertionPublicKeyNotFound] |
      | clientAssertionSignatureVerification | SKIPPED | []                                 |
      | platformStatesVerification           | SKIPPED | []                                 |
      | dpopValidation                       | PASSED  | []                                 |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_ALGORITHM_NOT_FOUND_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim alg non è valido allora il recupero della chiave pubblica fallisce con errore algorithmNotFound
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim          | value |
      | __removeHeader | alg   |
    And "PA1" crea una DPoP proof per la client assertion
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors              |
      | clientAssertionValidation            | FAILED  | [algorithmNotFound] |
      | publicKeyRetrieve                    | SKIPPED | []                  |
      | clientAssertionSignatureVerification | SKIPPED | []                  |
      | platformStatesVerification           | SKIPPED | []                  |
      | dpopValidation                       | PASSED  | []                  |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_INVALID_DIGEST_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim DIGEST non è valido allora il recupero della chiave pubblica fallisce con errore invalidDigestClaim
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim  | value                               |
      | digest | {"alg":"SHA256","invalidProp":true} |
    And "PA1" crea una DPoP proof per la client assertion
    When "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors               |
      | clientAssertionValidation            | FAILED  | [invalidDigestClaim] |
      | publicKeyRetrieve                    | SKIPPED | []                   |
      | clientAssertionSignatureVerification | SKIPPED | []                   |
      | platformStatesVerification           | SKIPPED | []                   |
      | dpopValidation                       | PASSED  | []                   |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_INVALID_HASH_LENGTH_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim DIGEST ha un value diverso da 64 caratteri allora il recupero della chiave pubblica fallisce con errore invalidHashLength
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim  | value                                                                                      |
      | digest | {"alg":"SHA256","value":"5db26201b684761d2b970329ab8596773164ba1b43b1559980e20045941b806"} |
    And "PA1" crea una DPoP proof per la client assertion
    When "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors              |
      | clientAssertionValidation            | FAILED  | [invalidHashLength] |
      | publicKeyRetrieve                    | SKIPPED | []                  |
      | clientAssertionSignatureVerification | SKIPPED | []                  |
      | platformStatesVerification           | SKIPPED | []                  |
      | dpopValidation                       | PASSED  | []                  |

  @devToolsClientAssertion
  Scenario: [KEY_RETRIEVE_INVALID_HASH_ALGORITHM_CONSUMER_CLIENT_DPOP] Dato un client CONSUMER valido, quando il claim DIGEST ha un algoritmo non valido allora il recupero della chiave pubblica fallisce con errore invalidHashAlgorithm
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim  | value                                                                                       |
      | digest | {"alg":"SHA512","value":"5db26201b684761d2b970329ab8596773164ba1b43b1559980e20045941b8063"} |
    And "PA1" crea una DPoP proof per la client assertion
    When "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                 |
      | clientAssertionValidation            | FAILED  | [invalidHashAlgorithm] |
      | publicKeyRetrieve                    | SKIPPED | []                     |
      | clientAssertionSignatureVerification | SKIPPED | []                     |
      | platformStatesVerification           | SKIPPED | []                     |
      | dpopValidation                       | PASSED  | []                     |
