Feature: Debugger Client Assertion Sync DPoP Async Errors

  @devToolsClientAssertion @ko-nrt-08072026
  Scenario: [VALIDATION_SUCCESS_CONSUMER_CLIENT_DPOP_ASYNC] Validazione DPoP e client assertion asincrona a buon fine.
  Verifica che tutte le fasi di sicurezza (firma, chiavi, stati e DPoP proof) siano superate con successo durante la
  validazione asincrona di un client CONSUMER.

    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And "PA2" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED | []     |
      | publicKeyRetrieve                    | PASSED | []     |
      | clientAssertionSignatureVerification | PASSED | []     |
      | platformStatesVerification           | PASSED | []     |
      | dpopValidation                       | PASSED | []     |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_2] Fallimento validazione client assertion per assenza di scope.
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And "PA2" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors             |
      | clientAssertionValidation            | FAILED  | [scopeNotProvided] |
      | publicKeyRetrieve                    | SKIPPED | []                 |
      | clientAssertionSignatureVerification | SKIPPED | []                 |
      | platformStatesVerification           | SKIPPED | []                 |
      | dpopValidation                       | PASSED  | []                 |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_3] Fallimento validazione per formato scope non valido.
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | invalid_claim_format      |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And "PA2" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                    |
      | clientAssertionValidation            | FAILED  | [invalidScopeClaimFormat] |
      | publicKeyRetrieve                    | SKIPPED | []                        |
      | clientAssertionSignatureVerification | SKIPPED | []                        |
      | platformStatesVerification           | SKIPPED | []                        |
      | dpopValidation                       | PASSED  | []                        |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_4] Fallimento validazione per formato interactionId non valido.
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    When il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim         | value                     |
      | scope         | callback_invocation       |
      | urlCallback   | https://www.hostname.com/ |
      | entityNumber  | 10                        |
      | interactionId | invalid_interaction_id    |
    And "PA1" crea una DPoP proof per la client assertion
    And il tenant erogatore "PA1" richiede un voucher asincrono per l'e-service
    And l'erogatore "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                            |
      | clientAssertionValidation            | FAILED  | [invalidInteractionIdClaimFormat] |
      | publicKeyRetrieve                    | SKIPPED | []                                |
      | clientAssertionSignatureVerification | SKIPPED | []                                |
      | platformStatesVerification           | SKIPPED | []                                |
      | dpopValidation                       | PASSED  | []                                |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_5] Fallimento validazione per formato urlCallback non valido.
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                |
      | scope       | start_interaction    |
      | urlCallback | invalid_url_callback |
    And "PA2" crea una DPoP proof per la client assertion
    And "PA2" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                          |
      | clientAssertionValidation            | FAILED  | [invalidUrlCallbackClaimFormat] |
      | publicKeyRetrieve                    | SKIPPED | []                              |
      | clientAssertionSignatureVerification | SKIPPED | []                              |
      | platformStatesVerification           | SKIPPED | []                              |
      | dpopValidation                       | PASSED  | []                              |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_6] Fallimento validazione per formato entityNumber non valido.
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    When il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value                     |
      | scope        | callback_invocation       |
      | urlCallback  | https://www.hostname.com/ |
      | entityNumber | invalid_entity_number     |
    And "PA1" crea una DPoP proof per la client assertion
    And il tenant erogatore "PA1" richiede un voucher asincrono per l'e-service
    And l'erogatore "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                           |
      | clientAssertionValidation            | FAILED  | [invalidEntityNumberClaimFormat] |
      | publicKeyRetrieve                    | SKIPPED | []                               |
      | clientAssertionSignatureVerification | SKIPPED | []                               |
      | platformStatesVerification           | SKIPPED | []                               |
      | dpopValidation                       | PASSED  | []                               |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_7] Fallimento validazione per e-service asyncExchange non supportato.
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And "PA2" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                                                   |
      | clientAssertionValidation            | PASSED | []                                                       |
      | publicKeyRetrieve                    | PASSED | []                                                       |
      | clientAssertionSignatureVerification | PASSED | []                                                       |
      | platformStatesVerification           | FAILED | [asyncExchangeNotEnabled, platformStateValidationFailed] |
      | dpopValidation                       | PASSED | []                                                       |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_8] Fallimento validazione per urlCallback mancante.
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim | value             |
      | scope | start_interaction |
    And "PA2" crea una DPoP proof per la client assertion
    And "PA2" richiede la validazione della client assertion e della DPoP Proof appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                   |
      | clientAssertionValidation            | FAILED  | [urlCallbackNotProvided] |
      | publicKeyRetrieve                    | SKIPPED | []                       |
      | clientAssertionSignatureVerification | SKIPPED | []                       |
      | platformStatesVerification           | SKIPPED | []                       |
      | dpopValidation                       | PASSED  | []                       |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_9] Fallimento validazione per interactionId mancante.
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    When il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value                     |
      | scope        | callback_invocation       |
      | urlCallback  | https://www.hostname.com/ |
      | entityNumber | 10                        |
      | __remove     | interactionId             |
    And "PA1" crea una DPoP proof per la client assertion
    And il tenant erogatore "PA1" richiede un voucher asincrono per l'e-service
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And l'erogatore "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                     |
      | clientAssertionValidation            | FAILED  | [interactionIdNotProvided] |
      | publicKeyRetrieve                    | SKIPPED | []                         |
      | clientAssertionSignatureVerification | SKIPPED | []                         |
      | platformStatesVerification           | SKIPPED | []                         |
      | dpopValidation                       | PASSED  | []                         |

  @devToolsClientAssertion @ko-nrt-08072026
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_10] Fallimento validazione per entityNumber mancante.
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    When il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | callback_invocation       |
      | urlCallback | https://www.hostname.com/ |
    And "PA1" crea una DPoP proof per la client assertion
    And l'erogatore "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result  | errors                    |
      | clientAssertionValidation            | FAILED  | [entityNumberNotProvided] |
      | publicKeyRetrieve                    | SKIPPED | []                        |
      | clientAssertionSignatureVerification | SKIPPED | []                        |
      | platformStatesVerification           | SKIPPED | []                        |
      | dpopValidation                       | PASSED  | []                        |

  @devToolsClientAssertion @ko-nrt-08072026
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_11] Fallimento validazione per entityNumber non valido.
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    When il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value                     |
      | scope        | callback_invocation       |
      | urlCallback  | https://www.hostname.com/ |
      | entityNumber | 51                        |
    And "PA1" crea una DPoP proof per la client assertion
    And l'erogatore "PA1" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                |
      | clientAssertionValidation            | PASSED | []                    |
      | publicKeyRetrieve                    | PASSED | []                    |
      | clientAssertionSignatureVerification | PASSED | []                    |
      | platformStatesVerification           | FAILED | [invalidEntityNumber] |
      | dpopValidation                       | PASSED | []                    |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_12] Dato un client CONSUMER valido, quando l'agreement è in stato non valido allora il recupero della chiave pubblica fallisce con errore invalidAgreementState
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di sospensione di quella richiesta di fruizione
    And si ottiene response status code 200
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And "PA2" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                  |
      | clientAssertionValidation            | PASSED | []                      |
      | publicKeyRetrieve                    | PASSED | []                      |
      | clientAssertionSignatureVerification | PASSED | []                      |
      | platformStatesVerification           | FAILED | [invalidAgreementState] |
      | dpopValidation                       | PASSED | []                      |

  @devToolsClientAssertion
  Scenario: [VALIDATION_ERROR_CONSUMER_CLIENT_DPOP_ASYNC_12b] Dato un client CONSUMER valido, quando la finalità è in stato
  non valido allora il recupero della chiave pubblica fallisce con errore invalidPurposeState
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente è un "admin" di "PA2"
    And l'utente sceglie la validazione asincrona
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And "PA2" richiede la validazione della client assertion appena creata
    And si ottiene response status code 200
    Then i risultati di validazione sono:
      | step                                 | result | errors                |
      | clientAssertionValidation            | PASSED | []                    |
      | publicKeyRetrieve                    | PASSED | []                    |
      | clientAssertionSignatureVerification | PASSED | []                    |
      | platformStatesVerification           | FAILED | [invalidPurposeState] |
      | dpopValidation                       | PASSED | []                    |
