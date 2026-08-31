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
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service senza riferimenti al template

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

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
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service senza riferimenti al template

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    When "PA1" ha già pubblicato una nuova versione per quell'e-service
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

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
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    When "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service senza riferimenti al template

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    When "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

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
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    When "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service senza riferimenti al template

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    When "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service senza riferimenti al template

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_12] Verifica info template su e-service sincrono istanza di template
  Verifica che le informazioni Template Reference siano presenti nei descrittori di un e-service sincrono creato da
  un e-service template attraverso i possibili stati che un descrittore può avere sul catalogo.

    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente tenta delle modifiche alla versione dell'e-service template
    When l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente crea una versione in bozza per quell'e-service istanza di template
    And la versione più recente dell'e-service è in stato "DRAFT"
    And l'utente specifica i metadati mancanti all'istanza del template sincrono con successo
    When l'utente pubblica quel descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service con riferimenti al template

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    And l'utente crea una versione in bozza per quell'e-service istanza di template
    And la versione più recente dell'e-service è in stato "DRAFT"
    And l'utente specifica i metadati mancanti all'istanza del template sincrono con successo
    When l'utente pubblica quel descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service con riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_13] Verifica info template su e-service sincrono aggiornato tramite e-service template
  Verifica che le informazioni Template Reference siano presenti nei descrittori di un e-service sincrono creato da
  un e-service template e aggiornato tramite aggiornamento del template passando attraverso i possibili stati che
  un descrittore può avere sul catalogo.

    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "ref-test" a partire dal template con successo indicando tutte le specifiche
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua delle modifiche alla versione dell'e-service template con successo
    And l'utente tenta la pubblicazione della versione dell'e-service template
    When la pubblicazione della versione dell'e-service template è stata effettuata correttamente
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template e dati:
      | isNewTemplateVersionAvailable | true |

    When l'utente tenta l'aggiornamento dell'istanza dell'e-service template all'ultima versione
    And la versione più recente dell'e-service è in stato "DRAFT"
    And l'utente tenta di associare un'interfaccia template instance "REST" con:
      | contactName               | Mario Rossi            |
      | contactEmail              | mario@example.it       |
      | contactUrl                | https://example.it     |
      | termsAndConditionsUrl     | https://tos.example.it |
      | serverUrls[0].url         | https://api.example.it |
      | serverUrls[0].description | API Server             |
    And l'interfaccia template instance "REST" è stata registrata correttamente con i valori:
      | contactName               | Mario Rossi            |
      | contactEmail              | mario@example.it       |
      | contactUrl                | https://example.it     |
      | termsAndConditionsUrl     | https://tos.example.it |
      | serverUrls[0].url         | https://api.example.it |
      | serverUrls[0].description | API Server             |
    And l'utente tenta la modifica del descriptor in stato DRAFT dell'istanza dell'e-service template
    And il descriptor dell'istanza in stato DRAFT dell'e-service template è stato modificato correttamente
    And l'utente pubblica quel descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua delle modifiche alla versione dell'e-service template con successo
    And l'utente tenta la pubblicazione della versione dell'e-service template
    And la pubblicazione della versione dell'e-service template è stata effettuata correttamente
    And l'utente tenta l'aggiornamento dell'istanza dell'e-service template all'ultima versione
    And la versione più recente dell'e-service è in stato "DRAFT"
    And l'utente tenta di associare un'interfaccia template instance "REST" con:
      | contactName               | Mario Rossi            |
      | contactEmail              | mario@example.it       |
      | contactUrl                | https://example.it     |
      | termsAndConditionsUrl     | https://tos.example.it |
      | serverUrls[0].url         | https://api.example.it |
      | serverUrls[0].description | API Server             |
    And l'interfaccia template instance "REST" è stata registrata correttamente con i valori:
      | contactName               | Mario Rossi            |
      | contactEmail              | mario@example.it       |
      | contactUrl                | https://example.it     |
      | termsAndConditionsUrl     | https://tos.example.it |
      | serverUrls[0].url         | https://api.example.it |
      | serverUrls[0].description | API Server             |
    And l'utente tenta la modifica del descriptor in stato DRAFT dell'istanza dell'e-service template
    And il descriptor dell'istanza in stato DRAFT dell'e-service template è stato modificato correttamente
    When l'utente pubblica quel descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service con riferimenti al template
      | isNewTemplateVersionAvailable | true |

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template
      | isNewTemplateVersionAvailable | true |

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua delle modifiche alla versione dell'e-service template con successo
    And l'utente tenta la pubblicazione della versione dell'e-service template
    And la pubblicazione della versione dell'e-service template è stata effettuata correttamente
    And l'utente tenta l'aggiornamento dell'istanza dell'e-service template all'ultima versione
    And la versione più recente dell'e-service è in stato "DRAFT"
    And l'utente tenta di associare un'interfaccia template instance "REST" con:
      | contactName               | Mario Rossi            |
      | contactEmail              | mario@example.it       |
      | contactUrl                | https://example.it     |
      | termsAndConditionsUrl     | https://tos.example.it |
      | serverUrls[0].url         | https://api.example.it |
      | serverUrls[0].description | API Server             |
    And l'interfaccia template instance "REST" è stata registrata correttamente con i valori:
      | contactName               | Mario Rossi            |
      | contactEmail              | mario@example.it       |
      | contactUrl                | https://example.it     |
      | termsAndConditionsUrl     | https://tos.example.it |
      | serverUrls[0].url         | https://api.example.it |
      | serverUrls[0].description | API Server             |
    And l'utente tenta la modifica del descriptor in stato DRAFT dell'istanza dell'e-service template
    And il descriptor dell'istanza in stato DRAFT dell'e-service template è stato modificato correttamente
    When l'utente pubblica quel descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service con riferimenti al template
      | isNewTemplateVersionAvailable | true |

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template
      | isNewTemplateVersionAvailable | true |

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template
      | isNewTemplateVersionAvailable | true |

  @happy-path
  @nrt-minimal
  Scenario: [CATALOG_LISTING_14] Verifica info template su e-service asincrono istanza di template
  Verifica che le informazioni Template Reference siano presenti nei descrittori di un e-service asincrono creato da
  un e-service template attraverso i possibili stati che un descrittore può avere sul catalogo.

    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    When l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente crea una versione in bozza per quell'e-service istanza di template
    And la versione più recente dell'e-service è in stato "DRAFT"
    And l'utente specifica i metadati mancanti all'istanza del template asincrono con successo
    When l'utente pubblica quel descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service con riferimenti al template

    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    When l'utente sospende quel descrittore
    And l'e-service è in stato "SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template

    Given l'utente è un "admin" di "PA1"
    And l'utente attiva il descrittore di quell'e-service
    And l'utente crea una versione in bozza per quell'e-service istanza di template
    And la versione più recente dell'e-service è in stato "DRAFT"
    And l'utente specifica i metadati mancanti all'istanza del template asincrono con successo
    When l'utente pubblica quel descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo il vecchio descrittore e-service con riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 30 giorni di preavviso
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template

    Given l'utente è un "admin" di "PA1"
    When "PA1" ha già sospeso quell'e-service
    And l'e-service è in stato "ARCHIVING_SUSPENDED"
    And l'utente è un "admin" di "PA2"
    Then l'utente legge da catalogo l'ultimo descrittore e-service con riferimenti al template
