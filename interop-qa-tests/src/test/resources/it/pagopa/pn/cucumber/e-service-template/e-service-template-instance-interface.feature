@e-service-template
@document-url-description
Feature: Test della creazione di interfacce REST e SOAP per istanze di template

  # TODO 14/07/2026 al momento il test ha un'imperfezione: non viene usato il corretto descriptorId al passo
  # "l'utente tenta di associare un'interfaccia template instance "REST" con:" . Si attende la risoluzione
  # del ticket https://pagopa.atlassian.net/browse/PIN-10534 per poter applicare una patch pulita.
  # Nel frattempo, il test è stato eseguito manualmente senza riscontrare anomalie.
  @happy-path
  Scenario: [EST_INT_1] Creazione interfaccia template instance REST con tutti i parametri corretti
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | contactName               | Mario Rossi            |
      | contactEmail              | mario@example.it       |
      | contactUrl                | https://example.it     |
      | termsAndConditionsUrl     | https://tos.example.it |
      | serverUrls[0].url         | https://api.example.it |
      | serverUrls[0].description | API Server             |
    Then si ottiene response status code 200
    And l'interfaccia template instance "REST" è stata registrata correttamente con i valori:
      | contactName               | Mario Rossi            |
      | contactEmail              | mario@example.it       |
      | contactUrl                | https://example.it     |
      | termsAndConditionsUrl     | https://tos.example.it |
      | serverUrls[0].url         | https://api.example.it |
      | serverUrls[0].description | API Server             |

  # TODO 14/07/2026 stessa questione di [EST_INT_1], anche qui eseguito a mano senza rilevare anomalie
  @happy-path
  Scenario: [EST_INT_2] Creazione interfaccia template instance REST con contactName al limite di lunghezza
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | contactName       | 12345678901234567890123456789012345678901234567890123456789012345678901234567890 |
      | contactEmail      | test@example.it                                                              |
      | serverUrls[0].url | https://api.example.it                                                       |
    Then si ottiene response status code 200

  @sad-path
  Scenario: [EST_INT_3] Creazione interfaccia template instance REST con contactName mancante
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | contactName       | %null                  |
      | contactEmail      | test@example.it        |
      | serverUrls[0].url | https://api.example.it |
    Then si ottiene response status code 400

  @sad-path
  Scenario: [EST_INT_4] Creazione interfaccia template instance REST con contactEmail non valida
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | contactName       | Mario Rossi            |
      | contactEmail      | invalid-email          |
      | serverUrls[0].url | https://api.example.it |
    Then si ottiene response status code 400

  @sad-path
  Scenario: [EST_INT_5] Creazione interfaccia template instance REST con contactUrl non valido
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | contactName       | Mario Rossi            |
      | contactEmail      | test@example.it        |
      | contactUrl        | not-a-valid-url        |
      | serverUrls[0].url | https://api.example.it |
    Then si ottiene response status code 400

  # TODO 14/07/2026 stessa questione di [EST_INT_1], anche qui eseguito a mano senza rilevare anomalie
  @sad-path
  Scenario: [EST_INT_6] Creazione interfaccia template instance REST con serverUrls assente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con:
      | contactName  | Mario Rossi     |
      | contactEmail | test@example.it |
    Then si ottiene response status code 400

  # TODO 14/07/2026 stessa questione di [EST_INT_1], anche qui eseguito a mano senza rilevare anomalie
  @happy-path
  Scenario: [EST_INT_7] Creazione interfaccia template instance SOAP con tutti i parametri corretti
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template sincrono in modalità erogazione con tecnologia "SOAP" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" con:
      | serverUrls[0].url     | https://soap.example.it |
      | serverUrls[0].description | SOAP Server            |
    Then si ottiene response status code 200
    And l'interfaccia template instance "SOAP" è stata registrata correttamente con i valori:
      | serverUrls[0].url     | https://soap.example.it |
      | serverUrls[0].description | SOAP Server            |

  # Ticket aperto https://pagopa.atlassian.net/browse/PIN-10642
  @sad-path
  Scenario: [EST_INT_7b] Creazione interfaccia template instance SOAP da un template REST
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template sincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" con:
      | serverUrls[0].url     | https://soap.example.it |
      | serverUrls[0].description | SOAP Server            |
    Then si ottiene response status code 409

  # TODO 14/07/2026 stessa questione di [EST_INT_1], anche qui eseguito a mano senza rilevare anomalie
  @happy-path
  Scenario: [EST_INT_8] Creazione interfaccia template instance SOAP con serverUrls description assente (opzionale)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template sincrono in modalità erogazione con tecnologia "SOAP" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" con:
      | serverUrls[0].url | https://soap.example.it |
    Then si ottiene response status code 200

  # TODO 14/07/2026 stessa questione di [EST_INT_1], anche qui eseguito a mano senza rilevare anomalie
  @sad-path
  Scenario: [EST_INT_9] Creazione interfaccia template instance SOAP con serverUrls assente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template sincrono in modalità erogazione con tecnologia "SOAP" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" senza specifiche
    Then si ottiene response status code 400

  @sad-path
  Scenario: [EST_INT_10] Creazione interfaccia template instance REST con eServiceId inesistente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con "eServiceId" "%random" e:
      | contactName       | Mario Rossi            |
      | contactEmail      | test@example.it        |
      | serverUrls[0].url | https://api.example.it |
    Then si ottiene response status code 404

  @sad-path
  Scenario: [EST_INT_11] Creazione interfaccia template instance REST con descriptorId inesistente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "REST" con "descriptorId" "%random" e:
      | contactName       | Mario Rossi            |
      | contactEmail      | test@example.it        |
      | serverUrls[0].url | https://api.example.it |
    Then si ottiene response status code 404

  @sad-path
  Scenario: [EST_INT_12] Creazione interfaccia template instance SOAP con eServiceId inesistente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" con "eServiceId" "%random" e:
      | serverUrls[0].url | https://soap.example.it |
    Then si ottiene response status code 404

  @sad-path
  Scenario: [EST_INT_13] Creazione interfaccia template instance SOAP con descriptorId inesistente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche
    When l'utente tenta di associare un'interfaccia template instance "SOAP" con "descriptorId" "%random" e:
      | serverUrls[0].url | https://soap.example.it |
    Then si ottiene response status code 404
