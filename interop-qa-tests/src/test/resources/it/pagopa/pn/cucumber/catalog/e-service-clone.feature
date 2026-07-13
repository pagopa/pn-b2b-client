@eservice
Feature: Clonazione di un e-service
  Tutti gli utenti autorizzati di enti erogatori possono clonare un proprio e-service e il relativo descrittore in stato PUBLISHED, SUSPENDED

  @nrt-minimal
  @eservice_cloning1
  @nrt-minimal
  @eservice_cloning1
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
