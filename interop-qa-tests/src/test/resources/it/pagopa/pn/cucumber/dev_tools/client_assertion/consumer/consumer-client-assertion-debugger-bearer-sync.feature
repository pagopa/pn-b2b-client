Feature: : Debugger Client Assertion Sync Bearer
  Come Aderente in possesso di un client di tipo CONSUMER
  Voglio validare la mia Client Assertion standard
  Al fine di identificare errori strutturali, temporali o crittografici nelle tre fasi di validazione (Formale, Recupero Chiave, Firma)

  Scenario: [VALIDATION_SUCCESS_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando viene inviata una client assertion corretta allora tutte le fasi di validazione risultano PASSED
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED | []     |
      | publicKeyRetrieve                    | PASSED | []     |
      | clientAssertionSignatureVerification | PASSED | []     |

  Scenario Outline: [VALIDATION_INVALID_TYPE_ERROR_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando il grant_type è <grant_type> e la client_assertion_type è <client_assertion_type>  allora la validazione formale fallisce con errore <expectedError>
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion valida per un client di tipo CONSUMER
    And "PA1" richiede la validazione della client assertion appena creata specificando client_assertion_type="<client_assertion_type>" e grant_type="<grant_type>"
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [<expectedError>] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |

    Examples:
      | client_assertion_type                                  | grant_type         | expectedError                          |
      | invalid_type                                           | client_credentials | invalidAssertionType                   |
      | urn:ietf:params:oauth:client-assertion-type:jwt-bearer | authorization_code | invalidGrantType                       |
      | invalid_type                                           | authorization_code | invalidAssertionType, invalidGrantType |

  Scenario: [VALIDATION_INVALID_AUD_ERROR_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando l'audience è invalida allora la validazione formale fallisce con errore 0004
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim | value            |
      | aud   | invalid_audience |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [invalidAudience] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |

  Scenario Outline: [VALIDATION_NOT_FOUND_ERROR_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando il claim <claimToRemove> non è presente allora la validazione formale fallisce con errore <expectedError>"
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim    | value           |
      | __remove | <claimToRemove> |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [<expectedError>] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |

    Examples:
      | claimToRemove | expectedError    |
      | aud           | audienceNotFound |
      | jti           | jtiNotFound      |
      | iat           | issuedAtNotFound |
      | exp           | expNotFound      |
      | iss           | issuerNotFound   |
      | sub           | subjectNotFound  |

  Scenario: [VALIDATION_ERROR_COMBINED_NOT_FOUND_ERROR_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando i claim jti, iat, aud non sono presenti allora la validazione formale fallisce con i rispettivi errori
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim    | value |
      | __remove | jti   |
      | __remove | iat   |
      | __remove | aud   |
      | __remove | exp   |
      | __remove | iss   |
      | __remove | sub   |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                                                                                          |
      | clientAssertionValidation            | FAILED  | [jtiNotFound, issuedAtNotFound, audienceNotFound, expNotFound, issuerNotFound, subjectNotFound] |
      | publicKeyRetrieve                    | SKIPPED | []                                                                                              |
      | clientAssertionSignatureVerification | SKIPPED | []                                                                                              |

    #TODO: da passare ai test per la fase 3
  Scenario: [VALIDATION_EXPIRED_ERROR_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando il token è scaduto allora la validazione formale fallisce con errore 0017
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim | value     |
      | exp   | now-10800 |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors              |
      | clientAssertionValidation            | PASSED | []                  |
      | publicKeyRetrieve                    | PASSED | []                  |
      | clientAssertionSignatureVerification | FAILED | [tokenExpiredError] |

    #TODO: l'errore restituito è invalidClientAssertionFormat, verificare se è possibile riprodurre jsonWebTokenError
  Scenario: [VALIDATION_JWT_ERROR_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando il JWT non è interpretabile allora la validazione formale fallisce con errore jsonWebTokenError
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value       |
      | __rawPayload | invalid_jwt |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors              |
      | clientAssertionValidation            | FAILED  | [jsonWebTokenError] |
      | publicKeyRetrieve                    | SKIPPED | []                  |
      | clientAssertionSignatureVerification | SKIPPED | []                  |

  Scenario: [VALIDATION_INVALID_FORMAT_ERROR_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando la client assertion è malformata allora la validazione formale fallisce con errore invalidClientAssertionFormat
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value          |
      | __rawPayload | malformed_json |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                         |
      | clientAssertionValidation            | FAILED  | [invalidClientAssertionFormat] |
      | publicKeyRetrieve                    | SKIPPED | []                             |
      | clientAssertionSignatureVerification | SKIPPED | []                             |

    #TODO: lo scenario fallisce per errore clientAssertionInvalidClaims, nbf è però un claim standarnd, verificare se è corretto
  Scenario: [VALIDATION_ERROR_CODE_0019_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando il claim nbf è nel futuro allora la validazione formale fallisce con errore notBeforeError
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim | value    |
      | nbf   | now+3600 |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors           |
      | clientAssertionValidation            | FAILED  | [notBeforeError] |
      | publicKeyRetrieve                    | SKIPPED | []               |
      | clientAssertionSignatureVerification | SKIPPED | []               |

  #Bug aperto: https://pagopa.atlassian.net/browse/PIN-9993
  Scenario Outline: [VALIDATION_INVALID_CLAIM_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando il claim <claim> non è in formato valido allora la validazione formale fallisce con errore <expectedError>
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim   | value   |
      | <claim> | <value> |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [<expectedError>] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |

    Examples:
      | claim | value      | expectedError         |
      | iss   | not-a-uuid | invalidClientIdFormat |
      | sub   | not-a-uuid | invalidSubjectFormat  |

    #TODO: dipende da https://pagopa.atlassian.net/browse/PIN-9993
  Scenario: [VALIDATION_INVALID_CLAIMS_CONSUMER_CLIENT] Dato un client CONSUMER valido, quando diversi claims sono in formato valido allora la validazione formale fallisce con errore clientAssertionInvalidClaims
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    When "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim | value      |
      | iss   | not-a-uuid |
      | sub   | not-a-uuid |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                         |
      | clientAssertionValidation            | FAILED  | [clientAssertionInvalidClaims] |
      | publicKeyRetrieve                    | SKIPPED | []                             |
      | clientAssertionSignatureVerification | SKIPPED | []                             |