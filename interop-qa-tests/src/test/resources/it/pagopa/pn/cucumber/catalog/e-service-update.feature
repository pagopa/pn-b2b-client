@eservice
Feature: Aggiornamento di un e-service non pubblicato
  Tutti gli utenti autorizzati di enti erogatori possono aggiornare un proprio e-service non pubblicato

  @nrt-minimal
  @eservice_updating1
  Scenario Outline: [ESERVICE_UPDATING_01] Per un e-service precedentemente creato, il quale non ha descrittori, l'aggiornamento dei campi dell'e-service avviene correttamente
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in DRAFT
    When l'utente aggiorna quell'e-service
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | admin        |       200 |
      | GSP  | api          |       200 |
      | GSP  | api,security |       200 |
      | PA1  | admin        |       200 |
      | PA1  | api          |       200 |
      | PA1  | api,security |       200 |

    @sad-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | security     |       403 |
      | GSP  | support      |       403 |
      | PA1  | security     |       403 |
      | PA1  | support      |       403 |

  @happy-path
  @nrt-minimal
  @eservice_updating2
  Scenario: [ESERVICE_UPDATING_02] Per un e-service precedentemente creato, il quale ha un solo descrittore in stato DRAFT, l’aggiornamento dei campi dell’e-service avviene correttamente
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente aggiorna quell'e-service
    Then si ottiene status code 200

  @sad-path
  @nrt-minimal
  @eservice_updating3
  Scenario Outline: [ESERVICE_UPDATING_03] Per un e-service precedentemente creato, il quale ha un solo descrittore in stato NON DRAFT (PUBLISHED, SUSPENDED, DEPRECATED, ARCHIVED), l’aggiornamento dei campi dell’e-service restituisce errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoDescrittore>"
    When l'utente aggiorna quell'e-service
    Then si ottiene status code 400

    Examples: 
      | statoDescrittore |
      | PUBLISHED        |
      | SUSPENDED        |
      | DEPRECATED       |
      | ARCHIVED         |

  @eservice_published_delegation
  Scenario Outline: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_01] Per un e-service precedentemente creato, il quale ha un solo descrittore in stato NON DRAFT (PUBLISHED, SUSPENDED, DEPRECATED, ARCHIVED), è possibile modificare i flag di delega
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>"
    When l'utente imposta la delega amministrativa come "<consumerDelegableState>" e la delega tecnica come "<clientAccessDelegableState>" per la fruizione dell'e-service "<eServiceId>"
    Then si ottiene status code <statusCode>
    And la delega amministrativa è "<consumerDelegableState>" e la delega tecnica è "<clientAccessDelegableState>" per la fruizione dell'e-service

    @happy-path
    Examples:
      | descriptorState | consumerDelegableState | clientAccessDelegableState | statusCode | eServiceId |
      | PUBLISHED       | true                   | true                       | 200        | %actual    |
      | PUBLISHED       | false                  | false                      | 200        | %actual    |
      | PUBLISHED       | true                   | false                      | 200        | %actual    |
      | SUSPENDED       | true                   | true                       | 200        | %actual    |
      | SUSPENDED       | false                  | false                      | 200        | %actual    |
      | SUSPENDED       | true                   | false                      | 200        | %actual    |
      | DEPRECATED      | true                   | true                       | 200        | %actual    |
      | DEPRECATED      | false                  | false                      | 200        | %actual    |
      | DEPRECATED      | true                   | false                      | 200        | %actual    |

    @sad-path
    Examples:
      | descriptorState | consumerDelegableState | clientAccessDelegableState | statusCode | eServiceId |
      | PUBLISHED       | false                  | true                       | 400        | %actual    |
      | SUSPENDED       | false                  | true                       | 400        | %actual    |
      | DEPRECATED      | false                  | true                       | 400        | %actual    |
