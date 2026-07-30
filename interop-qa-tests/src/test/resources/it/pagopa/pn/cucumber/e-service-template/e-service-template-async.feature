@e-service-template @e-service-template-async
Feature: Configurazione e gestione di template e-service per scambi asincroni e massivi

  Scenario Outline: [ASYNC_TEMPLATE_ESERVICE_CREATION_1] La creazione di un e-service template asincrono in stato DRAFT
  in modalità erogazione va a buon fine
    Given l'utente è un "admin" di "PA1"
    When l'utente effettua la creazione di un e-service template <asyncExchange> in modalità <mode> con tecnologia "<technology>" in stato di DRAFT
    Then si ottiene response status code 200
    And l'e-service template creato è configurato come <asyncExchange>

    Examples:
      | technology | mode       | asyncExchange |
      | REST       | erogazione | asincrono     |
      | SOAP       | erogazione | asincrono     |
      | REST       | erogazione | sincrono      |
      | SOAP       | erogazione | sincrono      |

  Scenario Outline: [ASYNC_TEMPLATE_ESERVICE_CREATION_2] La creazione di un e-service template asincrono in stato DRAFT
  in modalità ricezione non va a buon fine
    Given l'utente è un "admin" di "PA1"
    When l'utente effettua la creazione di un e-service template <asyncExchange> in modalità <mode> con tecnologia "<technology>" in stato di DRAFT
    Then si ottiene response status code 400

    Examples:
      | technology | mode      | asyncExchange |
      | REST       | ricezione | asincrono     |
      | SOAP       | ricezione | asincrono     |

  Scenario Outline: [ASYNC_TEMPLATE_ESERVICE_UPDATE_1] Aggiornamento specifiche tecniche di exchange per un e-service
  template asincrono in stato DRAFT.
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "<technology>" in stato di DRAFT
    And si ottiene response status code 200
    And l'e-service template creato è configurato come asincrono
    When l'utente modifica la versione dell'e-service template con:
      | voucherLifespan                               | 6000                    |
      | asyncExchangeProperties.responseTime          | <responseTime>          |
      | asyncExchangeProperties.resourceAvailableTime | <resourceAvailableTime> |
      | asyncExchangeProperties.maxResultSet          | <maxResultSet>          |
      | asyncExchangeProperties.confirmation          | <confirmation>          |
      | asyncExchangeProperties.bulk                  | <bulk>                  |
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

  Scenario: [ASYNC_TEMPLATE_ESERVICE_UPDATE_2] Aggiornamento specifiche tecniche di exchange per un e-service
  template sincrono in stato DRAFT.
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template sincrono in modalità erogazione con tecnologia "REST" in stato di DRAFT
    And si ottiene response status code 200
    And l'e-service template creato è configurato come sincrono
    When l'utente modifica la versione dell'e-service template con:
      | voucherLifespan                               | 6000 |
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.maxResultSet          | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
    Then si ottiene status code 200

  Scenario: [ASYNC_TEMPLATE_ESERVICE_UPDATE_3] La creazione di un'istanza e-service asincrona a partire da un template
  configurato come asincrono va a buon.
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di DRAFT
    And si ottiene response status code 200
    And l'utente modifica la versione dell'e-service template con:
      | voucherLifespan                               | 6000 |
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.maxResultSet          | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
    And si ottiene response status code 200
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua l'aggiunta di un documento di tipo ASYNC_EXCHANGE_CALLBACK_INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua la pubblicazione dell'e-service template
    And si ottiene status code 200
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando solo le specifiche strettamente necessarie e impostando l'e-service come asincrono e con:
      | responseTime          | 200 |
      | resourceAvailableTime | 200 |
      | maxResultSet          | 200 |
    And si ottiene status code 200

  Scenario Outline: [ASYNC_TEMPLATE_ESERVICE_UPDATE_4] Aggiornamento delle specifiche tecniche di scambio asincrono su
  un'istanza di e-service creata da un template pubblicato.
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "<technology>" in stato di DRAFT
    And si ottiene response status code 200
    And l'e-service template creato è configurato come asincrono
    And l'utente modifica la versione dell'e-service template con:
      | voucherLifespan                               | 6000 |
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.maxResultSet          | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
    And si ottiene status code 200
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua l'aggiunta di un documento di tipo ASYNC_EXCHANGE_CALLBACK_INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua la pubblicazione dell'e-service template
    And si ottiene status code 200
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando solo le specifiche strettamente necessarie e impostando l'e-service come asincrono e con:
      | responseTime          | <responseTime>          |
      | resourceAvailableTime | <resourceAvailableTime> |
      | maxResultSet          | <maxResultSet>          |
    Then si ottiene response status code 200

    Examples:
      | technology | responseTime | resourceAvailableTime | maxResultSet |
      | REST       | 200          | 200                   | 100          |
      | REST       | :null        | 200                   | 100          |
      | REST       | 200          | :null                 | 100          |
      | REST       | 200          | 200                   | :null        |

  Scenario: [ASYNC_TEMPLATE_ESERVICE_UPDATE_5] La pubblicazione di un e-service template fallisce se non viene
  specificata l'interfaccia di callback.
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di DRAFT
    And si ottiene response status code 200
    And l'e-service template creato è configurato come asincrono
    And l'utente modifica la versione dell'e-service template con:
      | voucherLifespan                               | 6000 |
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.maxResultSet          | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
    And si ottiene status code 200
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua la pubblicazione dell'e-service template
    And si ottiene status code 400



  # TODO tentativo di formalizzazione TA del bug rilevato da Silvano
  Scenario: [TMP] La pubblicazione di una seconda versione di un'istanza di template va a buon fine
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente pubblica una nuova versione dell'istanza del template asincrono con successo
    Then si ottiene response status code 200

