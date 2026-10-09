@eservice
Feature: Clonazione di un e-service
  Tutti gli utenti autorizzati di enti erogatori possono clonare un proprio e-service e il relativo descrittore in stato PUBLISHED, SUSPENDED

  @nrt-minimal
  @eservice_cloning1
  @nrt-minimal
  Scenario Outline: [ESERVICE_CLONING_1] Per un e-service che ha 2 descrittori, l'ultimo dei quali è in stato PUBLISHED/SUSPENDED, alla richiesta di clonazione, viene creato un nuovo e-service che ha un solo descrittore in stato DRAFT. Sia il nuovo e-service che il suo descrittore hanno esattamente le stesse caratteristiche dell'e-service e descrittore di partenza (ad eccezione del nome dell'e-service al quale viene aggiunto un " - clone" alla fine;
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    Given "<ente>" ha già creato una versione in "<statoDescrittore>" per quell'e-service
    When l'utente tenta di clonare quell'e-service
    Then si ottiene status code <risultato>

    @happy-path
    Examples: # Test sui ruoli
      | ente | ruolo        | statoDescrittore | risultato |
      | GSP  | admin        | PUBLISHED        | 200       |
      | GSP  | api          | PUBLISHED        | 200       |
      | GSP  | api,security | PUBLISHED        | 200       |
      | PA1  | admin        | PUBLISHED        | 200       |
      | PA1  | api          | PUBLISHED        | 200       |
      | PA1  | api,security | PUBLISHED        | 200       |

    @sad-path
    Examples: # Test sui ruoli
      | ente | ruolo    | statoDescrittore | risultato |
      | GSP  | security | PUBLISHED        | 403       |
      | GSP  | support  | PUBLISHED        | 403       |
      | PA1  | security | PUBLISHED        | 403       |
      | PA1  | support  | PUBLISHED        | 403       |

    @sad-path
    @nuovi-operatori-update
    Examples: # Test sui ruoli
      | ente | ruolo    | statoDescrittore | risultato |
      | GSP  | reviewer | PUBLISHED        | 403       |
      | GSP  | viewer   | PUBLISHED        | 403       |
      | PA2  | reviewer | PUBLISHED        | 403       |
      | PA2  | viewer   | PUBLISHED        | 403       |

    @happy-path
    Examples: # Test sugli stati
      | ente | ruolo | statoDescrittore | risultato |
      | PA1  | admin | SUSPENDED        | 200       |

  Scenario: [ESERVICE_CLONING_2] La clonazione di un e-service con un nome di lunghezza massima (60 caratteri) genera un nuovo e-service con un nome che non supera i 60 caratteri, aggiungendo al nome originale ' - clone - ' seguito dalla data e ora della clonazione;
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente aggiorna il nome dell'e-service con un valore di lunghezza 60 caratteri
    When l'utente clona quell'e-service
    Then si ottiene status code 200
    And il nome del nuovo e-service non supera i 60 caratteri
    And il nome del nuovo e-service contiene " - clone - " seguito dalla data e ora della clonazione

  Scenario: [ESERVICE_CLONING_3] La clonazione di un e-service asincrono già pubblicato genera un nuovo e-service che
  mantiene le stesse configurazioni del descrittore originario.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    When l'utente clona quell'e-service
    And si ottiene status code 200
    Then il nome del nuovo e-service contiene " - clone - " seguito dalla data e ora della clonazione
    And l'e-service ha questa configurazione:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |

  @happy-path
  @delegation-duplication
  Scenario Outline: [ESERVICE_CLONING_PRODUCER_DELEGATION_1.1] L'ente delegante può duplicare un e-service in delega in erogazione in stato PUBLISHED o SUSPENDED
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<eserviceState>"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'e-service è in stato "<eserviceState>"
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 204
    And la delega in erogazione è ancora associata all'e-service oggetto della duplicazione
    And l'e-service è stato clonato con successo
    And l'e-service clonato non ha ereditato la delega in erogazione dall'e-service oggetto della duplicazione

    Examples:
      | eserviceState |
      | PUBLISHED     |
      | SUSPENDED     |

  @happy-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_1.2] L'ente delegante può duplicare un e-service in stato PUBLISHED con richiesta di delega in erogazione pending
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 200
    And l'e-service è stato clonato con successo

  @sad-path
  @delegation-duplication
  Scenario Outline: [ESERVICE_CLONING_PRODUCER_DELEGATION_2.1] L'ente delegato NON può duplicare un e-service in delega in erogazione in stato ARCHIVING o ARCHIVING_SUSPENDED
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<eserviceState>"
    And l'utente è un "admin" di "PA1"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 403

    Examples:
      | eserviceState |
      | PUBLISHED     |
      | SUSPENDED     |

  @happy-path
  @delegation-duplication
  Scenario Outline: [ESERVICE_CLONING_PRODUCER_DELEGATION_2.2] L'ente delegante può duplicare un e-service in delega in erogazione in stato ARCHIVING o ARCHIVING_SUSPENDED
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<eserviceState>"
    And l'utente è un "admin" di "PA1"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 200
    And l'e-service è stato clonato con successo

    Examples:
      | eserviceState |
      | PUBLISHED     |
      | SUSPENDED     |

  @sad-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_2.3] L'ente delegato NON può duplicare la vecchia versione in stato DEPRECATED di un e-service in delega in erogazione
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente tenta di clonare la vecchia versione dell'e-service
    Then si ottiene response status code 403

  @happy-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_2.4] L'ente delegante può duplicare la vecchia versione in stato DEPRECATED di un e-service in delega in erogazione
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di clonare la vecchia versione dell'e-service
    Then si ottiene response status code 200
    And l'e-service è stato clonato con successo

  @sad-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_2.5] L'ente delegato NON può duplicare la vecchia versione in stato ARCHIVED di un e-service in delega in erogazione
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente tenta di clonare la vecchia versione dell'e-service
    Then si ottiene response status code 403

  @happy-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_2.6] L'ente delegante può duplicare la vecchia versione in stato ARCHIVED di un e-service in delega in erogazione
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di clonare la vecchia versione dell'e-service
    Then si ottiene response status code 200
    And l'e-service è stato clonato con successo

  @sad-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_2.7] L'ente delegante NON può duplicare una versione in stato WAITING_FOR_APPROVAL di un e-service in delega in erogazione
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 409

  @sad-path
  @delegation-duplication
  Scenario Outline: [ESERVICE_CLONING_PRODUCER_DELEGATION_3.1] L'ente delegato o un ente terzo NON può duplicare un e-service in stato PUBLISHED o SUSPENDED con delega in erogazione attiva
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<eserviceState>"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "<tenant>"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 403

    Examples:
      | tenant | eserviceState |
      | PA2    | PUBLISHED     |
      | PA2    | SUSPENDED     |
      | PA3    | PUBLISHED     |
      | PA3    | SUSPENDED     |

  @sad-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_3.2] L'ente delegato NON può duplicare un e-service in stato PUBLISHED con richiesta di delega in erogazione pending
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'utente è un "admin" di "PA2"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 403

  @happy-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_REVOCATION_1.1] L'erogatore può duplicare un e-service in stato PUBLISHED dopo aver revocato la delega in erogazione precedentemente attiva
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'ente "PA1" con ruolo "admin" revoca la delega in erogazione con successo
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 200
    And l'e-service è stato clonato con successo

  @sad-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_REVOCATION_1.2] L'ente ex-delegato NON può duplicare un e-service in stato PUBLISHED dopo la revoca della delega in erogazione precedentemente attiva
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'ente "PA1" con ruolo "admin" revoca la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 403

  @sad-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_REJECTION_1.1] Il potenziale delegato NON può duplicare un e-service in stato PUBLISHED dopo aver rifiutato la richiesta di delega in erogazione
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" rifiuta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 403

  @happy-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_PRODUCER_DELEGATION_REJECTION_1.2] L'erogatore può duplicare un e-service in stato PUBLISHED dopo il rifiuto della richiesta di delega in erogazione da parte del potenziale delegato
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" rifiuta la delega in erogazione con successo
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 200
    And l'e-service è stato clonato con successo

  @sad-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_CONSUMER_DELEGATION_1.1] Il delegante in fruizione NON può duplicare un e-service in stato PUBLISHED con delega in fruizione attiva
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" di "PA3"
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato con successo
    And l'ente "PA3" accetta la delega in fruizione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 403

  @sad-path
  @delegation-duplication
  Scenario: [ESERVICE_CLONING_CONSUMER_DELEGATION_1.2] Il potenziale delegante in fruizione NON può duplicare un e-service in stato PUBLISHED con richiesta di delega in fruizione pending
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" di "PA3"
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato con successo
    And l'utente è un "admin" di "PA2"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 403
