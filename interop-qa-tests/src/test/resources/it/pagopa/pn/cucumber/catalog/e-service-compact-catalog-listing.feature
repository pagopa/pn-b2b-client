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
  Scenario: [COMPACT_CATALOG_LISTING_2] A fronte di 5 e-service esistenti e una richiesta di 3 e-service, restituisce solo i primi 3 risultati
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 4 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo compatto limitata ai primi 3 e-services
    Then si ottiene status code 200 e la lista di 3 e-services dal catalogo compatto

  @happy-path
  @nrt-minimal
  Scenario: [COMPACT_CATALOG_LISTING_3] A fronte di 5 e-service esistenti e una richiesta di offset 2, restituisce solo 3 risultati
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

  @happy-path
  @nrt-minimal
  Scenario Outline: [COMPACT_CATALOG_LISTING_8] Filtra gli e-service nel catalogo compatto per modalità di erogazione (mode)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 2 e-service in modalità "<modeCreato>" con un descrittore in stato "PUBLISHED"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per modalità "<modeFiltro>"
    Then si ottiene status code 200
    And il risultato del catalogo compatto <esito> gli e-service con id "%actual"

    Examples:
      | modeCreato | modeFiltro | esito       |
      | DELIVER    | DELIVER    | include     |
      | RECEIVE    | RECEIVE    | include     |
      | DELIVER    | RECEIVE    | non include |
      | RECEIVE    | DELIVER    | non include |

  @happy-path
  @nrt-minimal
  Scenario Outline: [COMPACT_CATALOG_LISTING_9] Filtra gli e-service nel catalogo compatto per delegabilità in fruizione (isConsumerDelegable)
    Given l'utente è un "admin" di "PA1"
    Given "PA3" ha già creato e pubblicato 2 e-services delegabili in fruizione
    Given "PA3" ha già creato e pubblicato 1 e-service
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per isConsumerDelegable "<isConsumerDelegable>"
    Then si ottiene status code 200 e la lista di <risultati> e-services dal catalogo compatto

    Examples:
      | isConsumerDelegable | risultati |
      | true                | 2         |
      | false               | 1         |

  @happy-path
  @nrt-minimal
  Scenario Outline: [COMPACT_CATALOG_LISTING_10] Filtra gli e-service nel catalogo compatto per flag dati personali (personalData: TRUE, FALSE, DEFINED)
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 2 e-services con personalData true
    Given "PA2" ha già creato e pubblicato 1 e-service con personalData false
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per personalData "<personalData>"
    Then si ottiene status code 200 e la lista di <risultati> e-services dal catalogo compatto

    Examples:
      | personalData | risultati |
      | TRUE         | 2         |
      | FALSE        | 1         |
      | DEFINED      | 3         |

  @happy-path
  @nrt-minimal
  Scenario Outline: [COMPACT_CATALOG_LISTING_10.1] Verifica il filtro personalData su e-service creati per modalità DELIVER e RECEIVE
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 2 e-service in modalità "<mode>" con un descrittore in stato "PUBLISHED" e flag dati personali a "<personalDataFlag>"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per personalData "<personalDataFilter>"
    Then si ottiene status code 200
    And il risultato del catalogo compatto <esito> gli e-service con id "%actual"

    Examples:
      | mode    | personalDataFlag | personalDataFilter | esito       |
      | DELIVER | true             | TRUE               | include     |
      | DELIVER | false            | FALSE              | include     |
      | DELIVER | true             | DEFINED            | include     |
      | DELIVER | false            | DEFINED            | include     |
      | DELIVER | true             | FALSE              | non include |
      | DELIVER | false            | TRUE               | non include |
      | RECEIVE | true             | TRUE               | include     |
      | RECEIVE | false            | FALSE              | include     |
      | RECEIVE | true             | DEFINED            | include     |

  @happy-path
  @nrt-minimal
  Scenario Outline: [COMPACT_CATALOG_LISTING_11] Filtra gli e-service nel catalogo compatto per stato del descrittore (prospettiva ente erogatore ed ente terzo)
    Given "PA1" ha già creato 2 e-service in stato <statoCreato>
    And l'utente è un "admin" di "<enteChiamante>"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per stati "<statoFiltro>"
    Then si ottiene status code 200
    And il risultato del catalogo compatto <esito> gli e-service con id "%actual"

    Examples:
      | statoCreato         | enteChiamante | statoFiltro         | esito       |
      # Prospettiva ente erogatore (PA1): può vedere tutti gli stati, incluso DRAFT
      | DRAFT               | PA1           | DRAFT               | include     |
      | PUBLISHED           | PA1           | PUBLISHED           | include     |
      | SUSPENDED           | PA1           | SUSPENDED           | include     |
      | DEPRECATED          | PA1           | DEPRECATED          | include     |
      | ARCHIVING           | PA1           | ARCHIVING           | include     |
      | ARCHIVING_SUSPENDED | PA1           | ARCHIVING_SUSPENDED | include     |
      # Prospettiva ente terzo (PA3): vede gli stati attivi/visibili a catalogo, ma NON vede DRAFT
      | DRAFT               | PA3           | DRAFT               | non include |
      | PUBLISHED           | PA3           | PUBLISHED           | include     |
      | SUSPENDED           | PA3           | SUSPENDED           | include     |
      | DEPRECATED          | PA3           | DEPRECATED          | include     |
      | ARCHIVING           | PA3           | ARCHIVING           | include     |
      | ARCHIVING_SUSPENDED | PA3           | ARCHIVING_SUSPENDED | include     |
      # Filtri non corrispondenti allo stato dell'e-service
      | PUBLISHED           | PA3           | SUSPENDED           | non include |
      | SUSPENDED           | PA3           | PUBLISHED           | non include |

  @happy-path
  @nrt-minimal
  @deleghe2
  Scenario Outline: [COMPACT_CATALOG_LISTING_12] Filtra gli e-service nel catalogo compatto per stato WAITING_FOR_APPROVAL (visibile a delegante e delegato, non visibile a ente terzo)
    Given "PA1" ha già creato 2 e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "<enteChiamante>"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per stati "<statoFiltro>"
    Then si ottiene status code 200
    And il risultato del catalogo compatto <esito> gli e-service con id "%actual"

    Examples:
      | enteChiamante | statoFiltro          | esito       |
      | PA1           | WAITING_FOR_APPROVAL | include     |
      | PA2           | WAITING_FOR_APPROVAL | include     |
      | PA3           | WAITING_FOR_APPROVAL | non include |
      | PA1           | PUBLISHED            | non include |

  @happy-path
  @nrt-minimal
  Scenario Outline: [COMPACT_CATALOG_LISTING_13] Filtra gli e-service nel catalogo compatto per stato ARCHIVED verificando la presenza dell'e-service preesistente archiviato
    Given l'utente è un "admin" di "<enteChiamante>"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per stati "<statoFiltro>"
    Then si ottiene status code 200
    And il risultato del catalogo compatto <esito> gli e-service con id "45d822b9-066c-4a77-b4a1-331135496907"

    Examples:
      | enteChiamante | statoFiltro | esito       |
      | PA1           | ARCHIVED    | include     |
      | PA3           | ARCHIVED    | include     |
      | PA1           | PUBLISHED   | non include |
      | PA3           | PUBLISHED   | non include |

  @happy-path
  @nrt-minimal
  Scenario: [COMPACT_CATALOG_LISTING_14] Filtra gli e-service nel catalogo compatto per più stati contemporaneamente verificando più id nel risultato
    Given "PA1" ha già creato 2 e-service in stato PUBLISHED
    And l'utente è un "admin" di "PA3"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per stati "PUBLISHED,ARCHIVED"
    Then si ottiene status code 200
    And il risultato del catalogo compatto include gli e-service con id "%actual,45d822b9-066c-4a77-b4a1-331135496907"
