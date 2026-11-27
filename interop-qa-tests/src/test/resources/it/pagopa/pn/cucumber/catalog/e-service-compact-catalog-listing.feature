@compact-catalog
Feature: Listing catalogo compatto e-services
  Tutti gli utenti autenticati di enti PA, GSP e privati possono ottenere la lista di e-services

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_1] Consultazione del catalogo compatto e visibilità per ruolo ed ente
    Verifica che gli utenti autenticati di enti PA, GSP e privati possano consultare il catalogo compatto degli e-service.
    L'ente erogatore visualizza tutti i propri e-service inclusi quelli in bozza (DRAFT), mentre gli altri enti visualizzano unicamente gli e-service pubblici (PUBLISHED o SUSPENDED).
    Given l'utente è un "<ruolo>" di "<ente>"
    And "PA1" ha già creato 5 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo compatto
    Then si ottiene status code 200 e la lista di <risultati> e-services dal catalogo compatto

    Examples:
      | ente    | ruolo        | risultati |
      | GSP     | admin        | 5         |
      | GSP     | api          | 5         |
      | GSP     | security     | 5         |
      | GSP     | support      | 5         |
      | GSP     | api,security | 5         |
      | PA1     | admin        | 6         |
      | PA1     | api          | 6         |
      | PA1     | security     | 5         |
      | PA1     | support      | 6         |
      | PA1     | api,security | 6         |
      | Privato | admin        | 5         |
      | Privato | api          | 5         |
      | Privato | security     | 5         |
      | Privato | support      | 5         |
      | Privato | api,security | 5         |

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario: [COMPACT_CATALOG_LISTING_2] Paginazione con limite sul numero di e-service restituiti
    Verifica il corretto funzionamento del parametro di paginazione limit nel listing del catalogo compatto.
    A fronte di 5 e-service complessivamente visibili per l'ente erogatore, specificando un limite pari a 3 la risposta restituisce con successo solo i primi 3 risultati.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 4 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo compatto limitata ai primi 3 e-services
    Then si ottiene status code 200 e la lista di 3 e-services dal catalogo compatto

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario: [COMPACT_CATALOG_LISTING_3] Paginazione con offset nel listing del catalogo compatto
    Verifica il corretto funzionamento del parametro di paginazione offset nel catalogo compatto.
    A fronte di 5 e-service complessivamente visibili, impostando un offset pari a 2 la risposta salta i primi due elementi e restituisce i restanti 3 risultati.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 4 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo compatto con offset 2
    Then si ottiene status code 200 e la lista di 3 e-services dal catalogo compatto

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario: [COMPACT_CATALOG_LISTING_5] Filtraggio per e-service associati ad agreement attivi del fruitore
    Verifica che un ente fruitore possa filtrare il catalogo compatto richiedendo esclusivamente gli e-service per cui possiede almeno una richiesta di fruizione in stato ACTIVE.
    Tra tutti gli e-service erogati da enti terzi, vengono restituiti solo quelli per cui l'ente chiamante ha un agreement attivo.
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato 3 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    And "PA1" ha un agreement attivo con un e-service di "PA2"
    When l'utente richiede la lista del catalogo compatto per gli e-services per i quali ha almeno un agreement attivo
    Then si ottiene status code 200 e la lista di 1 e-service dal catalogo compatto

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario: [COMPACT_CATALOG_LISTING_6] Ricerca per parola chiave nel nome con corrispondenza case-insensitive
    Verifica la funzionalità di ricerca testuale sul catalogo compatto filtrando per keyword all'interno del nome dell'e-service.
    La ricerca non è sensibile alle maiuscole/minuscole e deve restituire esclusivamente l'e-service che include il termine di ricerca specificato.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 2 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    And "PA1" ha già creato e pubblicato un e-service contenente la keyword "test"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per la keyword "TEST"
    Then si ottiene status code 200 e la lista di 1 e-service dal catalogo compatto

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario: [COMPACT_CATALOG_LISTING_7] Ricerca testuale con parola chiave senza corrispondenze a catalogo
    Verifica il comportamento del catalogo compatto quando la ricerca per parola chiave non individua alcun e-service corrispondente.
    Anche in presenza di molteplici e-service a catalogo, una ricerca con termine non presente restituisce esito positivo HTTP 200 con un elenco vuoto di risultati.
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato 10 e-services in catalogo in stato PUBLISHED o SUSPENDED e 1 in stato DRAFT
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per la keyword "unknown"
    Then si ottiene status code 200 e la lista di 0 e-services dal catalogo compatto

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_8] Filtraggio del catalogo compatto per modalità di erogazione
    Verifica la capacità di filtrare gli e-service nel catalogo compatto in base alla modalità di erogazione (DELIVER o RECEIVE).
    I risultati del listing includono o escludono gli e-service coerentemente con la modalità impostata nel filtro di ricerca.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 2 e-service in modalità "<modeCreato>" con un descrittore in stato "PUBLISHED"
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
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_9] Filtraggio per delegabilità in fruizione dell'e-service
    Verifica il funzionamento del filtro sul flag isConsumerDelegable nel catalogo compatto.
    Permette di distinguere e selezionare gli e-service in base alla possibilità per i fruitori di delegare terze parti, restituendo il numero atteso di risultati.
    Given l'utente è un "admin" di "PA1"
    And "PA3" ha già creato e pubblicato 2 e-service delegabile in fruizione
    And "PA3" ha già creato e pubblicato 1 e-service
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per isConsumerDelegable "<isConsumerDelegable>"
    Then si ottiene status code 200 e la lista di <risultati> e-services dal catalogo compatto

    Examples:
      | isConsumerDelegable | risultati |
      | true                | 2         |
      | false               | 1         |

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_10] Filtraggio in base alla presenza e definizione di dati personali
    Verifica l'applicazione del filtro sui dati personali (personalData) con valori TRUE, FALSE e DEFINED nel catalogo compatto.
    Consente di visualizzare solo gli e-service con trattamento di dati personali abilitato, disabilitato o comunque esplicitamente definito.
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 2 e-services con personalData true
    And "PA2" ha già creato e pubblicato 1 e-service con personalData false
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per personalData "<personalData>"
    Then si ottiene status code 200 e la lista di <risultati> e-services dal catalogo compatto

    Examples:
      | personalData | risultati |
      | TRUE         | 2         |
      | FALSE        | 1         |
      | DEFINED      | 3         |

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_10.2] Il filtro personalData esclude e-service senza flag impostato
    Verifica che i filtri TRUE, FALSE e DEFINED escludano un e-service con personalData non impostato.
    L'e-service resta in DRAFT perché la piattaforma non consente di pubblicarlo senza specificare personalData; il listing viene eseguito dall'erogatore.
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato un e-service in modalità "<mode>" con un descrittore in stato "DRAFT" e flag dati personali a "undefined"
    And verifica che il flagPersonalData presente nell'eService con il descrittore appena creato sia "undefined"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per personalData "<personalDataFilter>"
    Then si ottiene status code 200
    And il risultato del catalogo compatto non include l'ultimo e-service creato

    Examples:
      | mode    | personalDataFilter |
      | DELIVER | TRUE               |
      | DELIVER | FALSE              |
      | DELIVER | DEFINED            |
      | RECEIVE | TRUE               |
      | RECEIVE | FALSE              |
      | RECEIVE | DEFINED            |

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_10.3] Il catalogo compatto include e-service senza personalData se il filtro non è specificato
    Verifica che il catalogo compatto includa un e-service con personalData non impostato quando non viene applicato il filtro personalData.
    L'e-service resta in DRAFT perché la piattaforma non consente di pubblicarlo senza specificare personalData; il listing viene eseguito dall'erogatore.
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato un e-service in modalità "<mode>" con un descrittore in stato "DRAFT" e flag dati personali a "undefined"
    And verifica che il flagPersonalData presente nell'eService con il descrittore appena creato sia "undefined"
    When l'utente richiede una operazione di listing sul catalogo compatto
    Then si ottiene status code 200
    And il risultato del catalogo compatto include l'ultimo e-service creato

    Examples:
      | mode    |
      | DELIVER |
      | RECEIVE |

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_10.1] Correlazione tra filtro dati personali e modalità di erogazione
    Verifica la compatibilità del filtro personalData su e-service creati sia in modalità DELIVER sia in modalità RECEIVE.
    Il risultato del listing include o esclude gli e-service a seconda della corrispondenza tra il valore del filtro e il flag effettivamente presente sull'e-service.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 2 e-service in modalità "<mode>" con un descrittore in stato "PUBLISHED" e flag dati personali a "<personalDataFlag>"
    And verifica che il flagPersonalData presente nell'eService con il descrittore appena creato sia "<personalDataFlag>"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per personalData "<personalDataFilter>"
    Then si ottiene status code 200
    And il risultato del catalogo compatto <esito> gli e-service con id "%actual"

    Examples:
      | mode    | personalDataFlag | personalDataFilter | esito       |
      | DELIVER | true             | TRUE               | include     |
      | DELIVER | true             | FALSE              | non include |
      | DELIVER | true             | DEFINED            | include     |
      | DELIVER | false            | TRUE               | non include |
      | DELIVER | false            | FALSE              | include     |
      | DELIVER | false            | DEFINED            | include     |
      | RECEIVE | true             | TRUE               | include     |
      | RECEIVE | true             | FALSE              | non include |
      | RECEIVE | true             | DEFINED            | include     |
      | RECEIVE | false            | TRUE               | non include |
      | RECEIVE | false            | FALSE              | include     |
      | RECEIVE | false            | DEFINED            | include     |

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_11] Filtraggio per stato del descrittore da parte di erogatore e terzi
    Verifica le regole di visibilità e filtraggio per stato del descrittore nel catalogo compatto (DRAFT, PUBLISHED, SUSPENDED, DEPRECATED).
    L'ente erogatore può visualizzare tutti i propri stati compreso DRAFT, mentre gli enti terzi possono consultare solo descrittori in stati visibili a catalogo.
    Given "PA1" ha già creato 2 e-services in stato <statoCreato>
    And l'utente è un "admin" di "<enteChiamante>"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per stati "<statoFiltro>"
    Then si ottiene status code 200
    And il risultato del catalogo compatto <esito> gli e-service con id "%actual"

    Examples:
      | statoCreato | enteChiamante | statoFiltro | esito       |
      # Prospettiva ente erogatore (PA1): può vedere tutti gli stati, incluso DRAFT
      | DRAFT       | PA1           | DRAFT       | include     |
      | PUBLISHED   | PA1           | PUBLISHED   | include     |
      | SUSPENDED   | PA1           | SUSPENDED   | include     |
      | DEPRECATED  | PA1           | DEPRECATED  | include     |
      # Prospettiva ente terzo (PA3): vede gli stati attivi/visibili a catalogo, ma NON vede DRAFT
      | DRAFT       | PA3           | DRAFT       | non include |
      | PUBLISHED   | PA3           | PUBLISHED   | include     |
      | SUSPENDED   | PA3           | SUSPENDED   | include     |
      | DEPRECATED  | PA3           | DEPRECATED  | include     |
      # Filtri non corrispondenti allo stato dell'e-service
      | PUBLISHED   | PA3           | SUSPENDED   | non include |
      | SUSPENDED   | PA3           | PUBLISHED   | non include |

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_11.1] Filtraggio per descrittori in archiviazione da parte di erogatore e terzi
    Verifica la visibilità degli e-service con descrittori in stato ARCHIVING o ARCHIVING_SUSPENDED.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato <statoIniziale>
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    And la versione più recente dell'e-service è in stato "<statoCreato>"
    And "PA1" ha già creato un e-service in stato <statoIniziale>
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    And la versione più recente dell'e-service è in stato "<statoCreato>"
    And l'utente è un "admin" di "<enteChiamante>"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per stati "<statoCreato>"
    Then si ottiene status code 200
    And il risultato del catalogo compatto include gli e-service con id "%actual"

    Examples:
      | statoIniziale | statoCreato         | enteChiamante |
      | PUBLISHED     | ARCHIVING           | PA1           |
      | SUSPENDED     | ARCHIVING_SUSPENDED | PA1           |
      | PUBLISHED     | ARCHIVING           | PA3           |
      | SUSPENDED     | ARCHIVING_SUSPENDED | PA3           |

  @happy-path
  @nrt-minimal
  @deleghe2
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_12] Visibilità e filtro su descrittori in attesa di approvazione con delega
    Verifica il filtro su e-service aventi un descrittore in stato WAITING_FOR_APPROVAL gestito tramite delega all'erogazione.
    Gli e-service devono risultare visibili all'ente delegante (PA1) e all'ente delegato (PA2), mentre devono essere esclusi per qualsiasi ente terzo (PA3).
    Given "PA1" ha già creato 2 e-services con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
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
  @work-item:QA-19033
  Scenario Outline: [COMPACT_CATALOG_LISTING_13] Filtraggio per stato ARCHIVED e verifica degli e-service restituiti
    Verifica il risultato del filtro ARCHIVED sul catalogo compatto, senza filtro sul nome.
    Il risultato deve contenere almeno un e-service e nessuno deve avere un descrittore attivo.
    Given l'utente è un "admin" di "<enteChiamante>"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per stati "ARCHIVED" senza filtro sul nome
    Then si ottiene status code 200
    And il risultato del catalogo compatto contiene almeno un e-service e nessuno ha un descrittore attivo

    Examples:
      | enteChiamante |
      | PA1           |
      | PA3           |

  @happy-path
  @nrt-minimal
  @work-item:QA-19033
  Scenario: [COMPACT_CATALOG_LISTING_14] Filtraggio simultaneo per stati multipli con verifica cumulativa
    Verifica la possibilità di specificare più stati contemporaneamente separati da virgola (es. PUBLISHED,SUSPENDED) nel filtro di listing.
    Il catalogo compatto restituisce con successo l'insieme cumulativo di tutti gli e-service corrispondenti agli stati indicati.
    Given "PA1" ha già creato 2 e-services in stato PUBLISHED
    And "PA1" ha già creato 2 e-services in stato SUSPENDED
    And l'utente è un "admin" di "PA3"
    When l'utente richiede una operazione di listing sul catalogo compatto filtrando per stati "PUBLISHED,SUSPENDED"
    Then si ottiene status code 200
    And il risultato del catalogo compatto include gli e-service con id "%actual"
