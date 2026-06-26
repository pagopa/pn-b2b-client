@voucher_async
Feature: Validazione delle Client Assertion ed emissione dei Token PDND per scambi asincroni e massivi

  Come Piattaforma PDND e Aderenti (Erogatori e Fruitori)
  voglio governare l'intero ciclo di vita degli e-service asincroni attraverso l'emissione, il controllo degli stati e la validazione di quattro tipologie di Access Token dedicati
  Al fine di garantire la sicurezza delle interazioni complesse (time-consuming o large response), assicurare il rispetto dei vincoli temporali (SLA) e gestire in modo centralizzato e standardizzato gli errori di conformità dei claim.

  Scenario: [ASYNC_DPOP_TOKEN_RETRIEVE] Generazione corretta dei token asincroni.
    Configurati i prerequisiti tra erogatore e fruitore, si verifica il rilascio con successo dei voucher asincroni per
    tutti gli scope previsti dal flusso (start_interaction, callback_invocation, get_resource, confirmation).

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
    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    Then si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |

    When il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value                     |
      | scope        | callback_invocation       |
      | urlCallback  | https://www.hostname.com/ |
      | entityNumber | 10                        |
    And "PA1" crea una DPoP proof per la client assertion
    And il tenant erogatore "PA1" richiede un voucher asincrono per l'e-service
    Then si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | callback_invocation       |

    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value        |
      | scope       | get_resource |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    Then si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | get_resource |

    When il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value        |
      | scope       | confirmation |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    Then si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | confirmation |

  Scenario: [ASYNC_DPOP_TOKEN_RETRIEVE_2] Errore richiesta token (confirmation disabilitata)
    Verifica il rilascio dei voucher per gli scope validi e il fallimento per lo scope confirmation, disattivato nelle
    proprietà dell'e-service.

    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100   |
      | asyncExchangeProperties.resourceAvailableTime | 100   |
      | asyncExchangeProperties.confirmation          | false |
      | asyncExchangeProperties.bulk                  | true  |
      | asyncExchangeProperties.maxResultSet          | 50    |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |

    And il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value                     |
      | scope        | callback_invocation       |
      | urlCallback  | https://www.hostname.com/ |
      | entityNumber | 10                        |
    And "PA1" crea una DPoP proof per la client assertion
    And il tenant erogatore "PA1" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | callback_invocation       |

    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value        |
      | scope       | get_resource |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | get_resource |

    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value        |
      | scope       | confirmation |
    And "PA2" crea una DPoP proof per la client assertion
    When il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    Then si ottiene status code 400

  Scenario: [ASYNC_DPOP_TOKEN_RETRIEVE_3] Generazione corretta token asincroni (bulk disabilitato)
    Verifica il rilascio con successo di tutti i voucher del flusso asincrono, inclusa la doppia richiesta per lo scope
    get_resource dovuta alla modalità bulk disattivata.

    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100   |
      | asyncExchangeProperties.resourceAvailableTime | 100   |
      | asyncExchangeProperties.confirmation          | true  |
      | asyncExchangeProperties.bulk                  | false |
      | asyncExchangeProperties.maxResultSet          | 50    |
    And si ottiene status code 200
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA2" associa la finalità al client creato con successo
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con successo
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |

    And il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value                     |
      | scope        | callback_invocation       |
      | urlCallback  | https://www.hostname.com/ |
      | entityNumber | 10                        |
    And "PA1" crea una DPoP proof per la client assertion
    And il tenant erogatore "PA1" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | callback_invocation       |

    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value        |
      | scope       | get_resource |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | get_resource |

    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value        |
      | scope       | get_resource |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | get_resource |

    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value        |
      | scope       | confirmation |
    And "PA2" crea una DPoP proof per la client assertion
    When il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    Then il voucher contiene i seguenti dati:
      | scope       | confirmation |

  Scenario: [ASYNC_DPOP_TOKEN_RETRIEVE_4] Errore richiesta token (get_resource anticipato)
    Verifica il corretto rilascio del voucher per start_interaction e il conseguente fallimento se il fruitore richiede
    lo scope get_resource prima della notifica di callback dell'erogatore.

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
    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |

    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value        |
      | scope       | get_resource |
    And "PA2" crea una DPoP proof per la client assertion
    When il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    Then si ottiene status code 400

  Scenario: [ASYNC_DPOP_TOKEN_RETRIEVE_5] Richiesta token confirmation fuori sequenza
  A seguito del rilascio di un voucher con scope start_interaction e di uno con scope callback_invocation,
  se il fruitore richiede un voucher con scope confirmation senza aver prima richiesto uno con scope get_resource,
  l'operazione va a buon fine.

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
    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |

    And il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value                     |
      | scope        | callback_invocation       |
      | urlCallback  | https://www.hostname.com/ |
      | entityNumber | 10                        |
    And "PA1" crea una DPoP proof per la client assertion
    And il tenant erogatore "PA1" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | callback_invocation       |

    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value        |
      | scope       | confirmation |
    And "PA2" crea una DPoP proof per la client assertion
    When il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    Then si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | confirmation |

  Scenario: [ASYNC_DPOP_TOKEN_RETRIEVE_6] Errore richiesta token per timeout (responseTime scaduto)
    Verifica il corretto rilascio del voucher per start_interaction e il fallimento della richiesta per lo scope
    callback_invocation a causa del superamento del tempo massimo di risposta (responseTime).

    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 10  |
      | asyncExchangeProperties.resourceAvailableTime | 10  |
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
    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |

    And il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value                     |
      | scope        | callback_invocation       |
      | urlCallback  | https://www.hostname.com/ |
      | entityNumber | 10                        |
    And "PA1" crea una DPoP proof per la client assertion
    And il tentant erogatore "PA1" attende la scadenza di responseTime di 15 secondi
    When il tenant erogatore "PA1" richiede un voucher asincrono per l'e-service
    Then si ottiene status code 400

  Scenario: [ASYNC_DPOP_TOKEN_RETRIEVE_7] Errore richiesta token per timeout (resourceAvailableTime scaduto)
    Verifica il rilascio dei voucher fino a callback_invocation e il fallimento per lo scope get_resource dovuto al
    superamento del tempo massimo di disponibilità della risorsa (resourceAvailableTime).

    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 10  |
      | asyncExchangeProperties.resourceAvailableTime | 10  |
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
    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    And "PA2" crea una DPoP proof per la client assertion
    And il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |

    And il tenant erogatore "PA1" crea una client assertion per un client di tipo CONSUMER con:
      | claim        | value                     |
      | scope        | callback_invocation       |
      | entityNumber | 10                        |
    And "PA1" crea una DPoP proof per la client assertion
    And il tenant erogatore "PA1" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    And il voucher contiene i seguenti dati:
      | scope       | callback_invocation       |

    And il tenant fruitore "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value        |
      | scope       | get_resource |
    And "PA2" crea una DPoP proof per la client assertion
    And il tentant fruitore "PA2" attende la scadenza di resourceAvailableTime di 15 secondi
    When il tenant fruitore "PA2" richiede un voucher asincrono per l'e-service
    Then si ottiene status code 400
