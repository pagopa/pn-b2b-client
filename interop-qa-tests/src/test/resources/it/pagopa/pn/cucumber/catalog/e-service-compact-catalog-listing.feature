@catalog
Feature: Listing catalogo compatto e-services
  Tutti gli utenti autenticati di enti PA, GSP e privati possono ottenere la lista compatta di e-services

  @happy-path
  @nrt-minimal
  Scenario Outline: [COMPACT_CATALOG_LISTING_1] Restituisce gli e-service nel catalogo compatto
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA1" ha già creato 5 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo compatto
    Then si ottiene status code 200 e la lista di 5 e-services dal catalogo compatto

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
  Scenario: [COMPACT_CATALOG_LISTING_2] A fronte di 5 e-service in db e una richiesta di 3 e-service, restituisce solo i primi 3 risultati
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 4 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo compatto limitata ai primi 3 e-services
    Then si ottiene status code 200 e la lista di 3 e-services dal catalogo compatto

  @happy-path
  @nrt-minimal
  Scenario: [COMPACT_CATALOG_LISTING_3] A fronte di 5 e-service in db e una richiesta di offset 2, restituisce solo 3 risultati
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 5 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo compatto con offset 2
    Then si ottiene status code 200 e la lista di 3 e-services dal catalogo compatto

  @happy-path
  @nrt-minimal
  Scenario: [COMPACT_CATALOG_LISTING_5] Restituisce gli e-service a catalogo compatto per i quali lo specifico fruitore ha almeno un agreement in stato ACTIVE
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato 3 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    And "PA1" ha un agreement attivo con un e-service di "PA2"
    When l'utente richiede la lista del catalogo compatto per gli e-services per i quali ha almeno un agreement attivo
    Then si ottiene status code 200 e la lista di 1 e-service dal catalogo compatto

  @happy-path
  @nrt-minimal
  Scenario: [COMPACT_CATALOG_LISTING_6] Restituisce gli e-service nel catalogo compatto che contengono la keyword "test" all'interno del nome, con ricerca case insensitive
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 2 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    Given "PA1" ha già creato e pubblicato un e-service contenente la keyword "test"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per la keyword "test"
    Then si ottiene status code 200 e la lista di 1 e-service dal catalogo compatto

  @happy-path
  @nrt-minimal
  Scenario: [COMPACT_CATALOG_LISTING_7] Restituisce un insieme vuoto di e-service nel catalogo compatto per una ricerca che non porta risultati
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato 10 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per la keyword "unknown"
    Then si ottiene status code 200 e la lista di 0 e-services dal catalogo compatto
