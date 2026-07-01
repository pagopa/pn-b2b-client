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

