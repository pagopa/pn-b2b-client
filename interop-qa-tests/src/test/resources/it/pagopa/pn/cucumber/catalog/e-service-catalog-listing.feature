@catalog
Feature: Listing catalogo e-services
  Tutti gli utenti autenticati di enti PA, GSP e privati possono ottenere la lista di e-services

  @happy-path
  @nrt-minimal
  @catalog_listing1
  Scenario Outline: [CATALOG_LISTING_1] Restituisce gli e-service a catalogo
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA1" ha già creato 5 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo
    Then si ottiene status code 200 e la lista di 5 e-services dal catalogo

    Examples: 
      | ente    | ruolo        |
      | GSP     | admin        |
      | GSP     | api          |
      | GSP     | security     |
      | GSP     | support      |
      | GSP     | api,security |
      | PA1     | admin        |
      | PA1     | api          |
      | PA1     | security     |
      | PA1     | support      |
      | PA1     | api,security |
      | Privato | admin        |
      | Privato | api          |
      | Privato | security     |
      | Privato | support      |
      | Privato | api,security |

  @happy-path
  @nrt-minimal
  @catalog_listing2
  Scenario: [CATALOG_LISTING_2] A fronte di 5 e-service in db e una richiesta di 3 e-service, restituisce solo i primi 3 risultati
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 4 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo limitata ai primi 3 e-services
    Then si ottiene status code 200 e la lista di 3 e-services dal catalogo

  @happy-path
  @nrt-minimal
  @catalog_listing3
  Scenario: [CATALOG_LISTING_3] A fronte di 5 e-service in db e una richiesta di offset 2, restituisce solo 3 risultati
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 5 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo con offset 2
    Then si ottiene status code 200 e la lista di 3 e-services dal catalogo

  @happy-path
  @nrt-minimal
  @catalog_listing4
  Scenario: [CATALOG_LISTING_4] Restituisce gli e-service a catalogo erogati da almeno uno degli erogatori specifici
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato 2 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    Given "GSP" ha già creato 2 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing degli e-services dell'erogatore "PA2"
    Then si ottiene status code 200 e la lista di 2 e-services dal catalogo

  @happy-path
  @nrt-minimal
  @catalog_listing5
  Scenario: [CATALOG_LISTING_5] Restituisce gli e-service a catalogo per i quali lo specifico fruitore ha almeno un agreement in stato ACTIVE
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato 3 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    And "PA1" ha un agreement attivo con un e-service di "PA2"
    When l'utente richiede la lista di e-services per i quali ha almeno un agreement attivo
    Then si ottiene status code 200 e la lista di 1 e-service dal catalogo

  @happy-path
  @nrt-minimal
  @catalog_listing6
  Scenario: [CATALOG_LISTING_6] Restituisce gli e-service a catalogo che contengono la keyword "test" all'interno del nome, con ricerca case insensitive
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 2 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    Given "PA1" ha già creato e pubblicato un e-service contenente la keyword "test"
    When l'utente richiede una operazione di listing sul catalogo filtrando per la keyword "test"
    Then si ottiene status code 200 e la lista di 1 e-service dal catalogo

  @happy-path
  @nrt-minimal
  @catalog_listing7
  Scenario: [CATALOG_LISTING_7] Restituisce un insieme vuoto di e-service a catalogo per una ricerca che non porta risultati
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato 10 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo filtrando per la keyword "unknown"
    Then si ottiene status code 200 e la lista di 0 e-services dal catalogo

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_8] Verifica assenza di info template su e-service sincrono non creato da template
    Verifica che le informazioni Template Reference non siano presenti nei descrittori di un e-service sincrono non
    creato da un e-service template attraverso i possibili stati che un descrittore può avere sul catalogo.

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura del vecchio descrittore e non trova riferimenti al template

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura del vecchio descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_9] Verifica assenza di info template su e-service sincrono clonato
  Verifica che le informazioni Template Reference non siano presenti nei descrittori di un e-service sincrono
  clonato da un e-service non creato da template attraverso i possibili stati che un descrittore può avere sul catalogo.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente clona quell'e-service
    And l'utente pubblica l'e-service
    And l'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura del vecchio descrittore e non trova riferimenti al template

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura del vecchio descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_10] Verifica assenza di info template su e-service asincrono non creato da template
  Verifica che le informazioni Template Reference non siano presenti nei descrittori di un e-service asincrono non
  creato da un e-service template attraverso i possibili stati che un descrittore può avere sul catalogo.

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 10   |
      | asyncExchangeProperties.resourceAvailableTime | 10   |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    When "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura del vecchio descrittore e non trova riferimenti al template

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    When "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura del vecchio descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_11] Verifica assenza di info template su e-service asincrono clonato
  Verifica che le informazioni Template Reference non siano presenti nei descrittori di un e-service asincrono
  clonato da un e-service non creato da template attraverso i possibili stati che un descrittore può avere sul catalogo.

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 10   |
      | asyncExchangeProperties.resourceAvailableTime | 10   |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And l'utente clona quell'e-service
    And l'utente pubblica l'e-service
    And l'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    When "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura del vecchio descrittore e non trova riferimenti al template

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    When "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura del vecchio descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente fruitore richiede la lettura dell'ultimo descrittore e non trova riferimenti al template

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_12] Verifica info template su e-service sincrono istanza di template

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_13] Verifica info template su e-service sincrono aggiornato tramite e-service template

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_14] Verifica info template su e-service asincrono istanza di template

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_15] Verifica info template su e-service asincrono aggiornato tramite e-service template
