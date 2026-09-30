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
      | GSP  | admin        | 200       |
      | GSP  | api          | 200       |
      | GSP  | api,security | 200       |
      | PA1  | admin        | 200       |
      | PA1  | api          | 200       |
      | PA1  | api,security | 200       |

    @sad-path
    Examples:
      | ente | ruolo    | risultato |
      | GSP  | security | 403       |
      | GSP  | support  | 403       |
      | PA1  | security | 403       |
      | PA1  | support  | 403       |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo    | risultato |
      | GSP  | reviewer | 403       |
      | GSP  | viewer   | 403       |
      | PA2  | reviewer | 403       |
      | PA2  | viewer   | 403       |

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
  @happy-path
  Scenario Outline: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_01] Per un e-service precedentemente creato,in stato NON DRAFT, è possibile modificare i flag di delega
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    When l'utente imposta la delega amministrativa come "<isConsumerDelegable>" e la delega tecnica come "<isClientAccessDelegable>" per la fruizione dell'e-service "<eServiceId>"
    Then si ottiene status code 200
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable | eServiceId |
      | PUBLISHED       | true                | true                    | %actual    |
      | PUBLISHED       | false               | false                   | %actual    |
      | PUBLISHED       | true                | false                   | %actual    |
      | SUSPENDED       | true                | true                    | %actual    |
      | SUSPENDED       | false               | false                   | %actual    |
      | SUSPENDED       | true                | false                   | %actual    |
      | DEPRECATED      | true                | true                    | %actual    |
      | DEPRECATED      | false               | false                   | %actual    |
      | DEPRECATED      | true                | false                   | %actual    |

  @eservice_published_delegation
  @sad-path
  Scenario Outline: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_02] Per un e-service precedentemente creato, in stato NON DRAFT, NON è possibile modificare i flag di delega nel caso di parametri obbligatori mancanti o errati
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    When l'utente imposta la delega amministrativa come "<isConsumerDelegable>" e la delega tecnica come "<isClientAccessDelegable>" per la fruizione dell'e-service "<eServiceId>"
    Then si ottiene status code <statusCode>
    And le flag di delega dell'e-service non hanno subito modifiche
    Examples:
    #in tal caso persistono i valori assegnati alla creazione (consumerDelegableState=false, clientAccessDelegableState=false)
      | descriptorState | isConsumerDelegable | isClientAccessDelegable | statusCode | eServiceId |
    #parametri mancanti
      | PUBLISHED       | %null               | false                   | 400        | %actual    |
      | SUSPENDED       | %null               | false                   | 400        | %actual    |
      | DEPRECATED      | %null               | false                   | 400        | %actual    |
      | PUBLISHED       | true                | %null                   | 400        | %actual    |
      | SUSPENDED       | true                | %null                   | 400        | %actual    |
      | DEPRECATED      | true                | %null                   | 400        | %actual    |
    #test cui chiamata al server non viene al momento effettuata
      | PUBLISHED       | true                | false                   | 400        | %null      |
      | SUSPENDED       | true                | false                   | 400        | %null      |
      | DEPRECATED      | true                | false                   | 400        | %null      |
    #eServiceId casuale
      | PUBLISHED       | true                | false                   | 404        | %random    |
      | SUSPENDED       | true                | false                   | 404        | %random    |
      | DEPRECATED      | true                | false                   | 404        | %random    |

  @eservice_published_delegation
  @sad-path
  Scenario Outline: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_03] Per un e-service precedentemente creato, in stato NON DRAFT, NON è possibile modificare i flag di delega nella combinazione non permessa
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    When l'utente imposta la delega amministrativa come "<isConsumerDelegable>" e la delega tecnica come "<isClientAccessDelegable>" per la fruizione dell'e-service "<eServiceId>"
    Then si ottiene status code 400
    And le flag di delega dell'e-service non hanno subito modifiche
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable | eServiceId |
      #casi in cui si tenta di violare l'invariante di dominio (consumerDelegableState=false, clientAccessDelegableState=true)
      | PUBLISHED       | false               | true                    | %actual    |
      | SUSPENDED       | false               | true                    | %actual    |
      | DEPRECATED      | false               | true                    | %actual    |

  @eservice_published_delegation
  @sad-path
  Scenario: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_04] Per un e-service precedentemente creato, in stato DRAFT, NON è possibile modificare i flag di delega
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT" e impostando delega amministrativa a "false" e delega tecnica a "false"
    When l'utente imposta la delega amministrativa come "true" e la delega tecnica come "false" per la fruizione dell'e-service "%actual"
    Then si ottiene status code 409
    And le flag di delega dell'e-service non hanno subito modifiche

  @sad-path
  @eservice_published_delegation
  Scenario: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_05] Per un e-service precedentemente creato con descrittore in stato NON DRAFT, la modifica dei flag di delega con token non valido restituisce errore
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And viene impostato per l'utente un token non valido
    When l'utente imposta la delega amministrativa come "true" e la delega tecnica come "false" per la fruizione dell'e-service "%actual"
    Then si ottiene status code 401

  @eservice_published_delegation
  @happy-path
  Scenario Outline: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_06] Per un e-service creato dall'ente delegante, il quale ha un solo descrittore in stato NON DRAFT, è possibile modificare i flag di delega da parte dell'ente delegato in erogazione
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente imposta la delega amministrativa come "<isConsumerDelegable>" e la delega tecnica come "<isClientAccessDelegable>" per la fruizione dell'e-service "<eServiceId>"
    Then si ottiene status code 200
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable | eServiceId |
      | PUBLISHED       | true                | true                    | %actual    |
      | PUBLISHED       | false               | false                   | %actual    |
      | PUBLISHED       | true                | false                   | %actual    |
      | SUSPENDED       | true                | true                    | %actual    |
      | SUSPENDED       | false               | false                   | %actual    |
      | SUSPENDED       | true                | false                   | %actual    |
      | DEPRECATED      | true                | true                    | %actual    |
      | DEPRECATED      | false               | false                   | %actual    |
      | DEPRECATED      | true                | false                   | %actual    |

  @eservice_published_delegation
  @sad-path
  Scenario Outline: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_07] Per un e-service creato dall'ente delegante, in stato NON DRAFT,NON è possibile modificare i flag di delega da parte dell'ente delegato in erogazione nel caso di parametri obbligatori mancanti o errati
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente imposta la delega amministrativa come "<isConsumerDelegable>" e la delega tecnica come "<isClientAccessDelegable>" per la fruizione dell'e-service "<eServiceId>"
    Then si ottiene status code <statusCode>
    And le flag di delega dell'e-service non hanno subito modifiche
    Examples:
    #in tal caso persistono i valori assegnati alla creazione (consumerDelegableState=false, clientAccessDelegableState=false)
      | descriptorState | isConsumerDelegable | isClientAccessDelegable | statusCode | eServiceId |
    #parametri mancanti
      | PUBLISHED       | %null               | false                   | 400        | %actual    |
      | SUSPENDED       | %null               | false                   | 400        | %actual    |
      | DEPRECATED      | %null               | false                   | 400        | %actual    |
      | PUBLISHED       | true                | %null                   | 400        | %actual    |
      | SUSPENDED       | true                | %null                   | 400        | %actual    |
      | DEPRECATED      | true                | %null                   | 400        | %actual    |
    #eServiceId casuale
      | PUBLISHED       | true                | false                   | 404        | %random    |
      | SUSPENDED       | true                | false                   | 404        | %random    |
      | DEPRECATED      | true                | false                   | 404        | %random    |

  @eservice_published_delegation
  @sad-path
  Scenario Outline: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_08] Per un e-service creato dall'ente delegante, in stato NON DRAFT,NON è possibile modificare i flag di delega da parte dell'ente delegato in erogazione nella combinazione non permessa
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente imposta la delega amministrativa come "<isConsumerDelegable>" e la delega tecnica come "<isClientAccessDelegable>" per la fruizione dell'e-service "<eServiceId>"
    Then si ottiene status code 400
    And le flag di delega dell'e-service non hanno subito modifiche
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable | eServiceId |
    #casi in cui si tenta di violare l'invariante di dominio (consumerDelegableState=false, clientAccessDelegableState=true)
      | PUBLISHED       | false               | true                    | %actual    |
      | SUSPENDED       | false               | true                    | %actual    |
      | DEPRECATED      | false               | true                    | %actual    |

  @eservice_published_delegation
  @sad-path
  Scenario: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_09] Per un e-service creato dall'ente delegante, in stato DRAFT,NON è possibile modificare i flag di delega da parte dell'ente delegato in erogazione
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente imposta la delega amministrativa come "true" e la delega tecnica come "false" per la fruizione dell'e-service "%actual"
    Then si ottiene status code 409
    And le flag di delega dell'e-service non hanno subito modifiche

  @eservice_published_delegation
  @sad-path
  Scenario: [ESERVICE_PUBLISHED_UPDATE_DELEGATION_10] La modifica del flag di delega di un e-service non è possibile da parte di un ente che non sia il proprietario dell'e-service e non sia delegato all'erogazione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA2"
    When l'utente imposta la delega amministrativa come "true" e la delega tecnica come "true" per la fruizione dell'e-service "%actual"
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1"
    And le flag di delega dell'e-service non hanno subito modifiche