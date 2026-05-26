@voucher_async
Feature: Validazione delle Client Assertion ed emissione dei Token PDND per scambi asincroni e massivi

  Come Piattaforma PDND e Aderenti (Erogatori e Fruitori)
  voglio governare l'intero ciclo di vita degli e-service asincroni attraverso l'emissione, il controllo degli stati e la validazione di quattro tipologie di Access Token dedicati
  Al fine di garantire la sicurezza delle interazioni complesse (time-consuming o large response), assicurare il rispetto dei vincoli temporali (SLA) e gestire in modo centralizzato e standardizzato gli errori di conformità dei claim.

  Scenario: [ASYNC_TOKEN_RETRIEVE] La richiesta di un token in modalità asincrona va a buon fine
    Given l'admin del fruitore "PA2" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 10   |
      | asyncExchangeProperties.resourceAvailableTime | 10   |
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
    And "PA2" crea una client assertion per un client di tipo CONSUMER con:
      | claim       | value                     |
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
    When l'utente "admin" di "PA2" richiede un voucher asincrono per l'e-service
    And si ottiene status code 200
    Then il voucher contiene i seguenti dati:
      | scope       | start_interaction         |
      | urlCallback | https://www.hostname.com/ |
