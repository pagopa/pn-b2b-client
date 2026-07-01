Feature: Gestione della callback interface per gli e-service asincroni

  Scenario: [ASYNC_ESERVICE_CALLBACK_INTERFACE_UPLOAD_SUCCESS] Il caricamento dell'interfaccia di callback per un e-service
  asincrono può essere fatta da un utente con ruolo m2m-admin.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | REST    |
      | mode       | DELIVER |
    And si ottiene response status code 200
    And l'utente aggiorna alcuni parametri di quel descrittore con:
      | voucherLifespan                               | 60        |
      | dailyCallsPerConsumer                         | 50        |
      | dailyCallsTotal                               | 2000      |
      | audience                                      | pagopa.it |
      | agreementApprovalPolicy                       | AUTOMATIC |
      | asyncExchangeProperties.responseTime          | 100       |
      | asyncExchangeProperties.resourceAvailableTime | 100       |
      | asyncExchangeProperties.confirmation          | true      |
      | asyncExchangeProperties.bulk                  | true      |
      | asyncExchangeProperties.maxResultSet          | 100       |
    And si ottiene response status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente carica un'interfaccia di callback di scambio asincrono per quel descrittore
    And l'utente pubblica l'e-service
    Then si ottiene response status code 200

  Scenario: [ASYNC_ESERVICE_CALLBACK_INTERFACE_UPLOAD_UNAUTHORIZED] Il caricamento dell'interfaccia di callback per un e-service
  asincrono non può essere fatta da un utente con ruolo m2m.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | REST    |
      | mode       | DELIVER |
    And si ottiene response status code 200
    And l'utente aggiorna alcuni parametri di quel descrittore con:
      | voucherLifespan                               | 60        |
      | dailyCallsPerConsumer                         | 50        |
      | dailyCallsTotal                               | 2000      |
      | audience                                      | pagopa.it |
      | agreementApprovalPolicy                       | AUTOMATIC |
      | asyncExchangeProperties.responseTime          | 100       |
      | asyncExchangeProperties.resourceAvailableTime | 100       |
      | asyncExchangeProperties.confirmation          | true      |
      | asyncExchangeProperties.bulk                  | true      |
      | asyncExchangeProperties.maxResultSet          | 100       |
    And si ottiene response status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente carica un'interfaccia di callback di scambio asincrono per quel descrittore
    Then si ottiene response status code 403

  Scenario: [ASYNC_ESERVICE_CALLBACK_INTERFACE_UPLOAD_INVALID_AUTH] Il caricamento dell'interfaccia di callback per un e-service
  asincrono fallisce se l'utente non ha un token valido.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | REST    |
      | mode       | DELIVER |
    And si ottiene response status code 200
    And l'utente aggiorna alcuni parametri di quel descrittore con:
      | voucherLifespan                               | 60        |
      | dailyCallsPerConsumer                         | 50        |
      | dailyCallsTotal                               | 2000      |
      | audience                                      | pagopa.it |
      | agreementApprovalPolicy                       | AUTOMATIC |
      | asyncExchangeProperties.responseTime          | 100       |
      | asyncExchangeProperties.resourceAvailableTime | 100       |
      | asyncExchangeProperties.confirmation          | true      |
      | asyncExchangeProperties.bulk                  | true      |
      | asyncExchangeProperties.maxResultSet          | 100       |
    And si ottiene response status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente carica un'interfaccia di callback di scambio asincrono per quel descrittore
    Then si ottiene response status code 401

  Scenario: [ASYNC_ESERVICE_CALLBACK_INTERFACE_UPLOAD_INVALID_ESERVICE] Il caricamento dell'interfaccia di callback per un e-service
  asincrono fallisce se l'e-service non esiste.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | REST    |
      | mode       | DELIVER |
    And si ottiene response status code 200
    And l'utente aggiorna alcuni parametri di quel descrittore con:
      | voucherLifespan                               | 60        |
      | dailyCallsPerConsumer                         | 50        |
      | dailyCallsTotal                               | 2000      |
      | audience                                      | pagopa.it |
      | agreementApprovalPolicy                       | AUTOMATIC |
      | asyncExchangeProperties.responseTime          | 100       |
      | asyncExchangeProperties.resourceAvailableTime | 100       |
      | asyncExchangeProperties.confirmation          | true      |
      | asyncExchangeProperties.bulk                  | true      |
      | asyncExchangeProperties.maxResultSet          | 100       |
    And si ottiene response status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento di un'interfaccia di callback di scambio asincrono per un e-service inesistente
    Then si ottiene response status code 404

  Scenario: [ASYNC_ESERVICE_CALLBACK_INTERFACE_UPLOAD_INVALID_DESCRIPTOR] Il caricamento dell'interfaccia di callback per un e-service
  asincrono fallisce se il descrittore dell'e-service non esiste.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | REST    |
      | mode       | DELIVER |
    And si ottiene response status code 200
    And l'utente aggiorna alcuni parametri di quel descrittore con:
      | voucherLifespan                               | 60        |
      | dailyCallsPerConsumer                         | 50        |
      | dailyCallsTotal                               | 2000      |
      | audience                                      | pagopa.it |
      | agreementApprovalPolicy                       | AUTOMATIC |
      | asyncExchangeProperties.responseTime          | 100       |
      | asyncExchangeProperties.resourceAvailableTime | 100       |
      | asyncExchangeProperties.confirmation          | true      |
      | asyncExchangeProperties.bulk                  | true      |
      | asyncExchangeProperties.maxResultSet          | 100       |
    And si ottiene response status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento di un'interfaccia di callback di scambio asincrono per un descrittore inesistente di un e-service
    Then si ottiene response status code 404

  Scenario: [ASYNC_ESERVICE_CALLBACK_INTERFACE_UPLOAD_ALREADY_LOADED] Il caricamento dell'interfaccia di callback fallisce
  se il descriptor dell'e-service asincrono ne possiede già una.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | REST    |
      | mode       | DELIVER |
    And si ottiene response status code 200
    And l'utente aggiorna alcuni parametri di quel descrittore con:
      | voucherLifespan                               | 60        |
      | dailyCallsPerConsumer                         | 50        |
      | dailyCallsTotal                               | 2000      |
      | audience                                      | pagopa.it |
      | agreementApprovalPolicy                       | AUTOMATIC |
      | asyncExchangeProperties.responseTime          | 100       |
      | asyncExchangeProperties.resourceAvailableTime | 100       |
      | asyncExchangeProperties.confirmation          | true      |
      | asyncExchangeProperties.bulk                  | true      |
      | asyncExchangeProperties.maxResultSet          | 100       |
    And si ottiene response status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente carica un'interfaccia di callback di scambio asincrono per quel descrittore
    When l'utente carica un'interfaccia di callback di scambio asincrono per quel descrittore
    Then si ottiene response status code 409

  Scenario: [ASYNC_ESERVICE_CALLBACK_INTERFACE_OWNERSHIP] Il caricamento dell'interfaccia di callback fallisce se l'utente
  non è il proprietario dell'e-service asincrono.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | REST    |
      | mode       | DELIVER |
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente carica un'interfaccia di callback di scambio asincrono per quel descrittore
    Then si ottiene lo status code 404

  Scenario Outline: [ASYNC_ESERVICE_CALLBACK_INTERFACE_INVALID] Il caricamento dell'interfaccia di callback per un e-serive
  asincrono fallisce se il file YAML non è valido.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | REST    |
      | mode       | DELIVER |
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento di un'interfaccia di callback di scambio asincrono di tipo YAML "<versionState>"
    Then si ottiene lo status code 400
    Examples:
      | versionState          |
      | senza versione        |
      | con versione obsoleta |

  Scenario Outline: [ASYNC_ESERVICE_CALLBACK_INTERFACE_TYPE] Per un e-service asincrono che eroga con una determinata tecnologia e
  che è in stato DRAFT, alla richiesta di caricamento di un'interfaccia di callback coerente con la tecnologia, da parte di un
  utente autorizzato, l'operazione avrà successo altrimenti restituirà errore.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | <technology> |
      | mode       | DELIVER      |
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente carica un'interfaccia di callback di scambio asincrono di tipo "<type>"
    Then si ottiene lo status code <expectedResult>

    Examples:
      | technology | type | expectedResult |
      | REST       | yaml | 200            |
      | REST       | json | 200            |
      # KO
      | SOAP       | wsdl | 200            |
      # KO
      | SOAP       | xml  | 200            |
      #KO
      | REST       | wsdl | 400            |
      #KO
      | REST       | xml  | 400            |
      | SOAP       | yaml | 400            |
      | SOAP       | json | 400            |

  Scenario Outline: [ASYNC_ESERVICE_CALLBACK_INTERFACE_INVALID_NAME] Il caricamento di un'interfaccia di callback per un e-service
  asincrono fallisce se il nome del file contiene il termine "localhost"

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | <technology> |
      | mode       | DELIVER      |
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente carica un'interfaccia di callback di scambio asincrono di tipo "<type>" che contiene il termine localhost
    Then si ottiene lo status code 403

    Examples:
      | technology | type |
      | REST       | yaml |
      | REST       | json |
      | SOAP       | wsdl |
      | SOAP       | xml  |
