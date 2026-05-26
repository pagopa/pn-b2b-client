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

  Scenario: [ASYNC_EXCHANGE_ESERVICE_CREATION_1] La creazione di un e-service in stato DRAFT in modalità asincrona, con le proprietà specificate nel descrittore, va a buon fine
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
