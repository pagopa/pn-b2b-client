@eservice @eservice_async
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
    When "PA1" ha già creato un e-service asincrono con un descrittore in stato "DRAFT" con:
      | asyncExchangeProperties.responseTime          | 10   |
      | asyncExchangeProperties.resourceAvailableTime | 10   |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    Then l'e-service ha questa configurazione:
      | asyncExchangeProperties.responseTime          | 10   |
      | asyncExchangeProperties.resourceAvailableTime | 10   |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |

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
      # Nel PST mancano alcuni valori; poiché sono tutti obbligatori e dobbiamo testare casi negativi non validi, li aggiungiamo comunque.
      # va in 200
      | REST       | -30          | 10                    | 100          | true         | true  | 400            |
      # va in 200
      | REST       | 10           | 100000000             | 100          | true         | true  | 400            |
      # non si può assegnare 3000000000 ad un int
      | REST       | 10           | 10                    | 3000000000   | true         | true  | 400            |
      | SOAP       | %null        | 10                    | 100          | false        | false | 400            |
      | SOAP       | 10           | %null                 | 100          | false        | false | 400            |
      | SOAP       | 10           | 10                    | %null        | false        | false | 400            |
      | SOAP       | 10           | 10                    | 100          | true         | true  | 400            |
      | SOAP       | 10           | 10                    | 100          | false        | true  | 400            |

  Scenario: [ASYNC_ESERVICE_CREATION_2c] Errore configurazione parametri asincroni su e-service sincrono.
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
    Then si ottiene status code 400
