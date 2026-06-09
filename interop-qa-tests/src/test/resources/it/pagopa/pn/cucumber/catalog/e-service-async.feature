@eservice @e-service-async
Feature: Configurazione e gestione di E-Service per scambi asincroni e massivi

  Come Erogatore di PDND
  voglio poter definire e configurare un e-service per lo scambio massivo e asincrono di dati (pattern [NONBLOCK_PUSH_*] ed eventualmente [BULK_RESOURCE_REST])
  al fine di gestire in modo ottimale le richieste "time consuming" o "large response", delegando il recupero al Fruitore e garantendo la corretta emissione degli Access Token dedicati da parte della piattaforma.

  Contesto di configurazione (Obiettivi minimi dell'Erogatore):
  - [REQ_INTERFACE_CALLBACK]: Definizione dell'interfaccia (IDL) della callback che il Fruitore deve implementare.
  - [REQ_MAX_RESPONSE]: Definizione della numerosità massima delle entità fornite nella risposta (maximum_response_size).
  - [REQ_INTERACTION_TIME]: Definizione dei tempi massimi di interazione (response_time e resource_availability).
  - [REQ_RECOVERY]: Assegnazione al Fruitore dell'onere di recupero della risposta.

  Opzionalità attivabili a discrezione dell'Erogatore:
  - [OPT_CONFIRMATION]: Richiesta al Fruitore dell'evidenza di avvenuta ricezione/recupero.
  - [OPT_BULK]: Abilitazione del recupero della risposta a blocchi (applicazione del pattern [BULK_RESOURCE_REST]).

  Scenario: [ASYNC_ESERVICE_CREATION_1] La creazione di un e-service in stato DRAFT in modalità asincrona con
  le proprietà specificate nel descrittore va a buon fine
    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 10   |
      | asyncExchangeProperties.resourceAvailableTime | 10   |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    Then l'e-service ha questa configurazione:
      | asyncExchangeProperties.responseTime          | 10   |
      | asyncExchangeProperties.resourceAvailableTime | 10   |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |

  Scenario Outline: [ASYNC_ESERVICE_CREATION_1b] La creazione di un e-service in stato DRAFT in modalità asincrona non
  è consentita per la modalità ricezione
    Given l'utente è un "admin" di "PA1"
    When l'utente crea un e-service asincrono "<technology>" in modalità <mode>
    Then si ottiene response status code <expectedResult>

    Examples:
      | technology | mode       | expectedResult |
      | REST       | erogazione | 200            |
      | REST       | ricezione  | 400            |
      | SOAP       | erogazione | 200            |
      | SOAP       | ricezione  | 400            |

  Scenario Outline: [ASYNC_ESERVICE_CREATION_2a] Aggiornamento corretto dei parametri asincroni di un e-service
  in DRAFT del descrittore testando diverse combinazioni.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | <technology> |
    When l'utente aggiorna alcuni parametri di quel descrittore con:
      | voucherLifespan                               | 60                      |
      | dailyCallsPerConsumer                         | 50                      |
      | dailyCallsTotal                               | 2000                    |
      | agreementApprovalPolicy                       | AUTOMATIC               |
      | asyncExchangeProperties.responseTime          | <responseTime>          |
      | asyncExchangeProperties.resourceAvailableTime | <resourceAvailableTime> |
      | asyncExchangeProperties.confirmation          | <confirmation>          |
      | asyncExchangeProperties.bulk                  | <bulk>                  |
      | asyncExchangeProperties.maxResultSet          | <maxResultSet>          |
    And si ottiene status code 200
    Then l'e-service ha questa configurazione:
      | asyncExchangeProperties.responseTime          | <responseTime>          |
      | asyncExchangeProperties.resourceAvailableTime | <resourceAvailableTime> |
      | asyncExchangeProperties.confirmation          | <confirmation>          |
      | asyncExchangeProperties.bulk                  | <bulk>                  |
      | asyncExchangeProperties.maxResultSet          | <maxResultSet>          |

    Examples:
      | technology | responseTime | resourceAvailableTime | maxResultSet | confirmation | bulk  |
      | REST       | 10           | 10                    | 100          | false        | false |
      | REST       | 10           | 10                    | 100          | true         | false |
      | REST       | 10           | 10                    | 100          | false        | false |
      | REST       | 10           | 10                    | 100          | true         | true  |
      | SOAP       | 10           | 10                    | 100          | false        | false |
      | SOAP       | 10           | 10                    | 100          | true         | false |

  Scenario Outline: [ASYNC_ESERVICE_CREATION_2b] Errore aggiornamento dei parametri asincroni di un e-service
  in DRAFT del descrittore testando diverse combinazioni.
  Verifica che l'inserimento di valori nulli nei campi obbligatori di asyncExchangeProperties provochi il fallimento della richiesta.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | <technology> |
    When l'utente aggiorna alcuni parametri di quel descrittore con:
      | voucherLifespan                               | 60                      |
      | dailyCallsPerConsumer                         | 50                      |
      | dailyCallsTotal                               | 2000                    |
      | agreementApprovalPolicy                       | AUTOMATIC               |
      | asyncExchangeProperties.responseTime          | <responseTime>          |
      | asyncExchangeProperties.resourceAvailableTime | <resourceAvailableTime> |
      | asyncExchangeProperties.confirmation          | <confirmation>          |
      | asyncExchangeProperties.bulk                  | <bulk>                  |
      | asyncExchangeProperties.maxResultSet          | <maxResultSet>          |
    Then si ottiene status code <expectedResult>

    Examples:
      | technology | responseTime | resourceAvailableTime | maxResultSet | confirmation | bulk  | expectedResult |
      | REST       | %null        | 10                    | 100          | false        | false | 400            |
      | REST       | 10           | %null                 | 100          | false        | false | 400            |
      | REST       | 10           | 10                    | %null        | false        | false | 400            |
      | REST       | -30          | 10                    | 100          | true         | true  | 400            |
      | REST       | 10           | -30                   | 100          | true         | true  | 400            |
      | REST       | 10           | 30                    | -100         | true         | true  | 400            |
      | REST       | 999999       | 10                    | 100          | true         | true  | 200            |
      | REST       | 1000000      | 10                    | 100          | true         | true  | 400            |
      | REST       | 10           | 999999                | 100          | true         | true  | 200            |
      | REST       | 10           | 1000000               | 100          | true         | true  | 400            |
      | REST       | 10           | 10                    | 99999        | true         | true  | 200            |
      | REST       | 10           | 10                    | 100000       | true         | true  | 400            |
      | SOAP       | %null        | 10                    | 100          | false        | false | 400            |
      | SOAP       | 10           | %null                 | 100          | false        | false | 400            |
      | SOAP       | 10           | 10                    | %null        | false        | false | 400            |
      | SOAP       | 10           | 10                    | 100          | true         | true  | 400            |
      | SOAP       | 10           | 10                    | 100          | false        | true  | 400            |

  # https://pagopa.atlassian.net/browse/PIN-10289
  # https://pagopaspa.slack.com/archives/C0A7AMD53MM/p1780923086687109
  Scenario: [ASYNC_ESERVICE_CREATION_2c] La configurazione dei parametri asincroni su un e-service sincrono non genera errori.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service sincrono in stato "DRAFT" con:
      | technology | REST |
    When l'utente aggiorna alcuni parametri di quel descrittore con:
      | voucherLifespan                               | 60        |
      | dailyCallsPerConsumer                         | 50        |
      | dailyCallsTotal                               | 2000      |
      | agreementApprovalPolicy                       | AUTOMATIC |
      | asyncExchangeProperties.responseTime          | 10        |
      | asyncExchangeProperties.resourceAvailableTime | 10        |
      | asyncExchangeProperties.confirmation          | true      |
      | asyncExchangeProperties.bulk                  | true      |
      | asyncExchangeProperties.maxResultSet          | 100       |
    Then si ottiene status code 200

  Scenario Outline: [ASYNC_ESERVICE_UPDATE_1] Aggiornamento modalità ed exchange di un e-service asincrono in stato DRAFT.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | <technology> |
      | mode       | <mode>       |
    And "PA1" aggiorna quell'e-service con:
      | technology    | <technology>       |
      | mode          | <newMode>          |
      | asyncExchange | <newAsyncExchange> |
    Then si ottiene status code <expectedResult>

    Examples:
      | technology | mode    | newMode | newAsyncExchange | expectedResult |
      | REST       | DELIVER | DELIVER | false            | 200            |
      | REST       | DELIVER | RECEIVE | true             | 400            |
      | REST       | DELIVER | RECEIVE | false            | 200            |
      | SOAP       | DELIVER | DELIVER | false            | 200            |
      | SOAP       | DELIVER | RECEIVE | true             | 400            |
      | SOAP       | DELIVER | RECEIVE | false            | 200            |

  Scenario: [ASYNC_ESERVICE_UPDATE_PUBLISHED] Fallimento dell'aggiornamento dei parametri di configurazione su un e-service
  asincrono già pubblicato.
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
    And "PA1" ha già caricato un'interfaccia di callback per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene response status code 200
    When "PA1" aggiorna quell'e-service con:
      | technology    | REST    |
      | mode          | DELIVER |
      | asyncExchange | false   |
    Then si ottiene status code 400

  Scenario Outline: [ASYNC_ESERVICE_TECH_SPEC_UPDATE_1] Aggiornamento specifiche tecniche di exchange per un e-service
  asincrono in stato DRAFT.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono in stato "DRAFT" con:
      | technology | <technology> |
    When l'utente aggiorna alcuni parametri di quel descrittore con:
      | voucherLifespan                               | 60                      |
      | dailyCallsPerConsumer                         | 50                      |
      | dailyCallsTotal                               | 2000                    |
      | agreementApprovalPolicy                       | AUTOMATIC               |
      | asyncExchangeProperties.responseTime          | <responseTime>          |
      | asyncExchangeProperties.resourceAvailableTime | <resourceAvailableTime> |
      | asyncExchangeProperties.confirmation          | <confirmation>          |
      | asyncExchangeProperties.bulk                  | <bulk>                  |
      | asyncExchangeProperties.maxResultSet          | <maxResultSet>          |
    Then si ottiene status code <expectedResult>

    Examples:
      | technology | responseTime | resourceAvailableTime | maxResultSet | confirmation | bulk  | expectedResult |
      | REST       | %null        | 200                   | 100          | false        | false | 400            |
      | REST       | 200          | %null                 | 100          | false        | false | 400            |
      | REST       | 200          | 200                   | %null        | false        | false | 400            |
      | REST       | 200          | 200                   | 200          | false        | false | 200            |
      | REST       | 200          | 200                   | 200          | true         | false | 200            |
      | REST       | 200          | 200                   | 200          | false        | true  | 200            |
      | REST       | 200          | 200                   | 200          | true         | true  | 200            |
      | REST       | -30          | 200                   | 200          | true         | true  | 400            |
      | REST       | 999999       | 200                   | 200          | true         | true  | 200            |
      | REST       | 1000000      | 200                   | 200          | true         | true  | 400            |
      | REST       | 200          | 999999                | 200          | true         | true  | 200            |
      | REST       | 200          | 1000000               | 200          | true         | true  | 400            |
      | REST       | 200          | 200                   | 99999        | true         | true  | 200            |
      | REST       | 200          | 200                   | 100000       | true         | true  | 400            |
      | SOAP       | %null        | 200                   | 100          | false        | false | 400            |
      | SOAP       | 200          | %null                 | 100          | false        | false | 400            |
      | SOAP       | 200          | 200                   | %null        | false        | false | 400            |
      | SOAP       | 200          | 200                   | 200          | false        | false | 200            |
      | SOAP       | 200          | 200                   | 200          | true         | true  | 400            |
      | SOAP       | 200          | 200                   | 200          | false        | true  | 400            |

  Scenario: [ASYNC_ESERVICE_CALLBACK_INTERFACE_REQUIRED] Fallimento della pubblicazione di un e-service asincrono senza
  aver caricato un interfaccia di callback.
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
    And l'utente pubblica l'e-service
    And si ottiene response status code 400
