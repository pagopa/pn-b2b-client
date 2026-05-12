Feature: : Debugger Client Assertion Sync Bearer
  Come Aderente in possesso di un client di tipo API
  Voglio validare la mia Client Assertion standard
  Al fine di identificare errori strutturali, temporali o crittografici nelle tre fasi di validazione (Formale, Recupero Chiave, Firma)

  Scenario: [VALIDATION_SUCCESS_API_CLIENT] Dato un client API valido, quando viene inviata una client assertion corretta allora tutte le fasi di validazione risultano PASSED
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" crea una client assertion valida per un client di tipo API
    When "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED | []     |
      | publicKeyRetrieve                    | PASSED | []     |
      | clientAssertionSignatureVerification | PASSED | []     |
      | platformStatesVerification           | PASSED | []     |

  Scenario Outline: [VALIDATION_INVALID_TYPE_ERROR_API_CLIENT] Dato un client API valido, quando il grant_type è <grant_type> e la client_assertion_type è <client_assertion_type>  allora la validazione formale fallisce con errore <expectedError>
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion valida per un client di tipo API
    And "PA1" richiede la validazione della client assertion appena creata specificando client_assertion_type="<client_assertion_type>" e grant_type="<grant_type>"
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [<expectedError>] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |
      | platformStatesVerification           | SKIPPED | []                |

    Examples:
      | client_assertion_type                                  | grant_type         | expectedError                          |
      | invalid_type                                           | client_credentials | invalidAssertionType                   |
      | urn:ietf:params:oauth:client-assertion-type:jwt-bearer | authorization_code | invalidGrantType                       |
      | invalid_type                                           | authorization_code | invalidAssertionType, invalidGrantType |

  Scenario: [VALIDATION_INVALID_AUD_ERROR_API_CLIENT] Dato un client API valido, quando l'audience è invalida allora la validazione formale fallisce con errore 0004
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim | value            |
      | aud   | invalid_audience |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [invalidAudience] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |
      | platformStatesVerification           | SKIPPED | []                |

  Scenario Outline: [VALIDATION_NOT_FOUND_ERROR_API_CLIENT] Dato un client API valido, quando il claim <claimToRemove> non è presente allora la validazione formale fallisce con errore <expectedError>"
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim    | value           |
      | __remove | <claimToRemove> |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [<expectedError>] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |
      | platformStatesVerification           | SKIPPED | []                |

    Examples:
      | claimToRemove | expectedError    |
      | aud           | audienceNotFound |
      | jti           | jtiNotFound      |
      | iat           | issuedAtNotFound |
      | exp           | expNotFound      |
      | iss           | issuerNotFound   |
      | sub           | subjectNotFound  |

  # TODO In this scenario validation fails for "clientAssertionValidation" with "invalidClientAssertionFormat"
  Scenario: [VALIDATION_ERROR_COMBINED_NOT_FOUND_ERROR_API_CLIENT] Dato un client API valido, quando i claim jti, iat, aud non sono presenti allora la validazione formale fallisce con i rispettivi errori
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
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
      | platformStatesVerification           | SKIPPED | []                                                                                              |

  #TODO: da passare ai test per la fase 3, rieseguire per verificare cosa fa platformStatesVerification
  Scenario: [VALIDATION_EXPIRED_ERROR_API_CLIENT] Dato un client API valido, quando il token è scaduto allora la validazione formale fallisce con errore 0017
   Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
   When "PA1" crea una client assertion per un client di tipo API con:
     | claim | value     |
     | exp   | now-10800 |
   And "PA1" richiede la validazione della client assertion appena creata
   And si ottiene response status code 200
   Then i risultati di validazione sono:
     | step                                 | result  | errors              |
     | clientAssertionValidation            | PASSED  | []                  |
     | publicKeyRetrieve                    | PASSED  | []                  |
     | clientAssertionSignatureVerification | FAILED  | [tokenExpiredError] |
     | platformStatesVerification           | SKIPPED | []                  |

  #TODO: l'errore restituito è invalidClientAssertionFormat, verificare se è possibile riprodurre jsonWebTokenError
  Scenario: [VALIDATION_JWT_ERROR_API_CLIENT] Dato un client API valido, quando il JWT non è interpretabile allora la validazione formale fallisce con errore jsonWebTokenError
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim        | value       |
      | __rawPayload | invalid_jwt |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors              |
      | clientAssertionValidation            | FAILED  | [jsonWebTokenError] |
      | publicKeyRetrieve                    | SKIPPED | []                  |
      | clientAssertionSignatureVerification | SKIPPED | []                  |
      | platformStatesVerification           | SKIPPED | []                  |

  Scenario: [VALIDATION_INVALID_FORMAT_ERROR_API_CLIENT] Dato un client API valido, quando la client assertion è malformata allora la validazione formale fallisce con errore invalidClientAssertionFormat
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim        | value          |
      | __rawPayload | malformed_json |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                         |
      | clientAssertionValidation            | FAILED  | [invalidClientAssertionFormat] |
      | publicKeyRetrieve                    | SKIPPED | []                             |
      | clientAssertionSignatureVerification | SKIPPED | []                             |
      | platformStatesVerification           | SKIPPED | []                             |

  Scenario: [VALIDATION_ERROR_CODE_0019_API_CLIENT] Dato un client API valido, quando il claim nbf è nel futuro allora la validazione formale fallisce con errore clientAssertionInvalidClaims
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim | value    |
      | nbf   | now+3600 |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors           |
      | clientAssertionValidation            | FAILED  | [clientAssertionInvalidClaims] |
      | publicKeyRetrieve                    | SKIPPED | []               |
      | clientAssertionSignatureVerification | SKIPPED | []               |
      | platformStatesVerification           | SKIPPED | []               |

  #Bug aperto: https://pagopa.atlassian.net/browse/PIN-9993
  Scenario Outline: [VALIDATION_INVALID_CLAIM_API_CLIENT] Dato un client API valido, quando il claim <claim> non è in formato valido allora la validazione formale fallisce con errore <expectedError>
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim   | value   |
      | <claim> | <value> |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors            |
      | clientAssertionValidation            | FAILED  | [<expectedError>] |
      | publicKeyRetrieve                    | SKIPPED | []                |
      | clientAssertionSignatureVerification | SKIPPED | []                |
      | platformStatesVerification           | SKIPPED | []                |

    Examples:
      | claim | value      | expectedError         |
      | iss   | not-a-uuid | invalidClientIdFormat |
      | sub   | not-a-uuid | invalidSubjectFormat  |

  #TODO: dipende da https://pagopa.atlassian.net/browse/PIN-9993
  Scenario: [VALIDATION_INVALID_CLAIMS_API_CLIENT] Dato un client API valido, quando diversi claims sono in formato valido allora la validazione formale fallisce con errore clientAssertionInvalidClaims
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
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
      | platformStatesVerification           | SKIPPED | []                             |

  # TODO clientAssertionValidation -> kidNotFound
  Scenario: [KEY_RETRIEVE_KID_NOT_FOUND_API_CLIENT] Dato un client API valido, quando il claim kid non è presente allora il recupero della chiave pubblica fallisce con errore kidNotFound
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim          | value |
      | __removeHeader | kid   |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors        |
      | clientAssertionValidation            | FAILED  | [kidNotFound] |
      | publicKeyRetrieve                    | SKIPPED | []            |
      | clientAssertionSignatureVerification | SKIPPED | []            |
      | platformStatesVerification           | SKIPPED | []            |

  #Bug aperto: https://pagopa.atlassian.net/browse/PIN-9998
  Scenario: [KEY_RETRIEVE_INVALID_KID_FORMAT_API_CLIENT] Dato un client API valido, quando il claim kid non è in formato valido allora il recupero della chiave pubblica fallisce con errore invalidKidFormat
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim      | value                  |
      | header.kid | not-a-valid-kid-format |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors             |
      | clientAssertionValidation            | PASSED  | []                 |
      | publicKeyRetrieve                    | FAILED  | [invalidKidFormat] |
      | clientAssertionSignatureVerification | SKIPPED | []                 |
      | platformStatesVerification           | SKIPPED | []                 |

  Scenario: [KEY_RETRIEVE_INVALID_SUBJECT_API_CLIENT] Dato un client API valido, quando il subject non corrisponde al client atteso allora il recupero della chiave pubblica fallisce con errore invalidSubject
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim | value                                |
      | sub   | 00000000-0000-0000-0000-000000000000 |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors           |
      | clientAssertionValidation            | FAILED  | [invalidSubject] |
      | publicKeyRetrieve                    | SKIPPED | []               |
      | clientAssertionSignatureVerification | SKIPPED | []               |
      | platformStatesVerification           | SKIPPED | []               |

  #Bug aperto: https://pagopa.atlassian.net/browse/PIN-9999
  Scenario: [KEY_RETRIEVE_PURPOSE_ID_NOT_PROVIDED_API_CLIENT] Dato un client API valido, quando il claim purposeId non è presente allora il recupero della chiave pubblica fallisce con errore purposeIdNotProvided
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim    | value     |
      | __remove | purposeId |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                 |
      | clientAssertionValidation            | PASSED  | []                     |
      | publicKeyRetrieve                    | FAILED  | [purposeIdNotProvided] |
      | clientAssertionSignatureVerification | SKIPPED | []                     |
      | platformStatesVerification           | SKIPPED | []                     |

  Scenario: [KEY_RETRIEVE_INVALID_PURPOSE_ID_FORMAT_API_CLIENT] Dato un client API valido, quando il claim purposeId non è in formato UUID allora il recupero della chiave pubblica fallisce con errore invalidPurposeIdClaimFormat
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API con:
      | claim     | value      |
      | purposeId | not-a-uuid |
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                        |
      | clientAssertionValidation            | FAILED  | [invalidPurposeIdClaimFormat] |
      | publicKeyRetrieve                    | SKIPPED | []                            |
      | clientAssertionSignatureVerification | SKIPPED | []                            |
      | platformStatesVerification           | SKIPPED | []                            |

  # TODO Questo scenario ha senso per API? La purpose ID viene passata soltanto se client type è consumer
  # Scenario: [KEY_RETRIEVE_INVALID_AGREEMENT_STATE_API_CLIENT] Dato un client API valido, quando l'agreement è in stato non valido allora il recupero della chiave pubblica fallisce con errore invalidAgreementState
  #   Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
  #   And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
  #   And l'utente è un "admin" di "PA1"
  #   When l'utente richiede una operazione di sospensione di quella richiesta di fruizione
  #   And "PA1" crea una client assertion valida per un client di tipo API
  #   And "PA1" richiede la validazione della client assertion appena creata
  #   And si ottiene response status code 200
  #   Then i risultati di validazione sono:
  #     | step                                 | result | errors                  |
  #     | clientAssertionValidation            | PASSED | []                      |
  #     | publicKeyRetrieve                    | PASSED | []                      |
  #     | clientAssertionSignatureVerification | PASSED | []                      |
  #     | platformStatesVerification           | FAILED | [invalidAgreementState] |

  Scenario: [KEY_RETRIEVE_INVALID_ESERVICE_STATE_API_CLIENT] Dato un client API valido, quando l'e-service è in stato non valido allora il recupero della chiave pubblica fallisce con errore invalidEServiceState
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'utente è un "admin" di "PA2"
    When l'utente sospende quel descrittore
    And "PA1" crea una client assertion valida per un client di tipo API
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                 |
      | clientAssertionValidation            | PASSED | []                     |
      | publicKeyRetrieve                    | PASSED | []                     |
      | clientAssertionSignatureVerification | PASSED | []                     |
      | platformStatesVerification           | FAILED | [invalidEServiceState] |

  Scenario: [KEY_RETREIVE_ALGORITHM_NOT_ALLOWED_API_CLIENT] Dato un client API valido, quando l'e-service è in stato non valido allora il recupero della chiave pubblica fallisce con errore algorithmNotAllowed
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API utilizzando una chiave "EC" di lunghezza 1024
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                |
      | clientAssertionValidation            | FAILED  | [algorithmNotAllowed] |
      | publicKeyRetrieve                    | SKIPPED | []                    |
      | clientAssertionSignatureVerification | SKIPPED | []                    |
      | platformStatesVerification           | SKIPPED | []                    |

  Scenario: [KEY_RETREIVE_INVALID_SIGNATURE_API_CLIENT] Dato un client API valido, quando l'e-service è in stato non valido allora il recupero della chiave pubblica fallisce con errore invalidSignature
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    When "PA1" crea una client assertion per un client di tipo API utilizzando una chiave "RSA" di lunghezza 2048
    And "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors             |
      | clientAssertionValidation            | PASSED  | []                 |
      | publicKeyRetrieve                    | PASSED  | []                 |
      | clientAssertionSignatureVerification | FAILED  | [invalidSignature] |
      | platformStatesVerification           | SKIPPED | []                 |

  Scenario: [KEY_RETRIEVE_ALGORITHM_NOT_FOUND_API_CLIENT] Dato un client API valido, quando il claim alg non è valido allora il recupero della chiave pubblica fallisce con errore algorithmNotFound
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" crea una client assertion per un client di tipo API con:
      | claim          | value |
      | __removeHeader | alg   |
    When "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors              |
      | clientAssertionValidation            | FAILED  | [algorithmNotFound] |
      | publicKeyRetrieve                    | SKIPPED | []                  |
      | clientAssertionSignatureVerification | SKIPPED | []                  |
      | platformStatesVerification           | SKIPPED | []                  |

  Scenario: [KEY_RETRIEVE_INVALID_DIGEST_API_CLIENT] Dato un client API valido, quando il claim DIGEST non è valido allora il recupero della chiave pubblica fallisce con errore invalidDigestClaim
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" crea una client assertion per un client di tipo API con:
      | claim  | value                               |
      | digest | {"alg":"SHA256","invalidProp":true} |
    When "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors               |
      | clientAssertionValidation            | FAILED  | [invalidDigestClaim] |
      | publicKeyRetrieve                    | SKIPPED | []                   |
      | clientAssertionSignatureVerification | SKIPPED | []                   |
      | platformStatesVerification           | SKIPPED | []                   |

  Scenario: [KEY_RETRIEVE_INVALID_HASH_LENGTH_API_CLIENT] Dato un client API valido, quando il claim DIGEST ha un value diverso da 64 caratteri allora il recupero della chiave pubblica fallisce con errore invalidHashLength
    Given l'admin del fruitore "PA1" ha già creato un client di tipo API aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" crea una client assertion per un client di tipo API con:
      | claim  | value                                                                                      |
      | digest | {"alg":"SHA256","value":"5db26201b684761d2b970329ab8596773164ba1b43b1559980e20045941b806"} |
    When "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors              |
      | clientAssertionValidation            | FAILED  | [invalidHashLength] |
      | publicKeyRetrieve                    | SKIPPED | []                  |
      | clientAssertionSignatureVerification | SKIPPED | []                  |
      | platformStatesVerification           | SKIPPED | []                  |
