@e-service-template
@pin-9920
Feature: Test della creazione di interfacce REST e SOAP per istanze di template
  Scenario: [PIN-9920 PST 1.2] Verifica istanziazione tramite API (REST e SOAP)

  @happy-path
  Scenario: Creazione interfaccia template instance REST con tutti i parametri corretti
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | field                     | value                  |
      | contactName               | Mario Rossi            |
      | contactEmail              | mario@example.it       |
      | contactUrl                | https://example.it     |
      | termsAndConditionsUrl     | https://tos.example.it |
      | serverUrls[0].url         | https://api.example.it |
      | serverUrls[0].description | API Server             |
    Then si ottiene response status code 200
    And l'interfaccia template instance "REST" è stata registrata correttamente con i valori:
      | field                     | value                  |
      | contactName               | Mario Rossi            |
      | contactEmail              | mario@example.it       |
      | contactUrl                | https://example.it     |
      | termsAndConditionsUrl     | https://tos.example.it |
      | serverUrls[0].url         | https://api.example.it |
      | serverUrls[0].description | API Server             |

  @happy-path
  Scenario: Creazione interfaccia template instance REST con contactName al limite di lunghezza
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | field             | value                                                                        |
      | contactName       | 12345678901234567890123456789012345678901234567890123456789012345678901234567890 |
      | contactEmail      | test@example.it                                                              |
      | serverUrls[0].url | https://api.example.it                                                       |
    Then si ottiene response status code 200

  @sad-path
  Scenario: Creazione interfaccia template instance REST con contactName mancante
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | field             | value                  |
      | contactName       | %null                  |
      | contactEmail      | test@example.it        |
      | serverUrls[0].url | https://api.example.it |
    Then si ottiene response status code 400

  @sad-path
  Scenario: Creazione interfaccia template instance REST con contactEmail non valida
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | field             | value                  |
      | contactName       | Mario Rossi            |
      | contactEmail      | invalid-email          |
      | serverUrls[0].url | https://api.example.it |
    Then si ottiene response status code 400

  @sad-path
  Scenario: Creazione interfaccia template instance REST con contactUrl non valido
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | field             | value                  |
      | contactName       | Mario Rossi            |
      | contactEmail      | test@example.it        |
      | contactUrl        | not-a-valid-url        |
      | serverUrls[0].url | https://api.example.it |
    Then si ottiene response status code 400

  @sad-path
  Scenario: Creazione interfaccia template instance REST con serverUrls assente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | field        | value           |
      | contactName  | Mario Rossi     |
      | contactEmail | test@example.it |
    Then si ottiene response status code 400

  @happy-path
  Scenario: Creazione interfaccia template instance SOAP con tutti i parametri corretti
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" con:
      | field                 | value                  |
      | serverUrls[0].url     | https://soap.example.it |
      | serverUrls[0].description | SOAP Server            |
    Then si ottiene response status code 200
    And l'interfaccia template instance "SOAP" è stata registrata correttamente con i valori:
      | field                 | value                  |
      | serverUrls[0].url     | https://soap.example.it |
      | serverUrls[0].description | SOAP Server            |

  @happy-path
  Scenario: Creazione interfaccia template instance SOAP con serverUrls description assente (opzionale)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" con:
      | field             | value                  |
      | serverUrls[0].url | https://soap.example.it |
    Then si ottiene response status code 200

  @sad-path
  Scenario: Creazione interfaccia template instance SOAP con serverUrls assente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" con:
      | field | value |
    Then si ottiene response status code 400

  @sad-path
  Scenario: Creazione interfaccia template instance REST con eServiceId inesistente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con "eServiceId" "%random" e:
      | field             | value                  |
      | contactName       | Mario Rossi            |
      | contactEmail      | test@example.it        |
      | serverUrls[0].url | https://api.example.it |
    Then si ottiene response status code 404

  @sad-path
  Scenario: Creazione interfaccia template instance REST con descriptorId inesistente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con "descriptorId" "%random" e:
      | field             | value                  |
      | contactName       | Mario Rossi            |
      | contactEmail      | test@example.it        |
      | serverUrls[0].url | https://api.example.it |
    Then si ottiene response status code 404

  @sad-path
  Scenario: Creazione interfaccia template instance SOAP con eServiceId inesistente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" con "eServiceId" "%random" e:
      | field             | value                  |
      | serverUrls[0].url | https://soap.example.it |
    Then si ottiene response status code 404

  @sad-path
  Scenario: Creazione interfaccia template instance SOAP con descriptorId inesistente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" con "descriptorId" "%random" e:
      | field             | value                  |
      | serverUrls[0].url | https://soap.example.it |
    Then si ottiene response status code 404
