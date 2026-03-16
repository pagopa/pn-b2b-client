@descriptor
Feature: Aggiornamento di un descrittore in bozza
  Tutti gli utenti autorizzati di enti erogatori possono aggiornare tutti i parametri di un descrittore in bozza.

  @nrt-minimal
  @descriptor_draft_update1
  Scenario Outline: [DESCRIPTOR_DRAFT_UPDATE_1] Per un e-service che ha un solo descrittore, il quale è in stato DRAFT, all’aggiornamento da parte di un utente autorizzato di alcuni parametri del descrittore, ben formattati, la bozza viene aggiornata correttamente
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente aggiorna alcuni parametri di quel descrittore
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

  @sad-path
  @nrt-minimal
  @descriptor_draft_update2
  Scenario Outline: [DESCRIPTOR_DRAFT_UPDATE_2] Per un e-service che ha un solo descrittore, il quale è in stato NON DRAFT (PUBLISHED, SUSPENDED, DEPRECATED, ARCHIVED), all’aggiornamento di alcuni parametri del descrittore, ben formattati, l’aggiornamento della bozza restituisce errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoVersione>"
    When l'utente aggiorna alcuni parametri di quel descrittore
    Then si ottiene status code 400

    Examples: 
      | statoVersione |
      | PUBLISHED     |
      | SUSPENDED     |
      | DEPRECATED    |
      | ARCHIVED      |

  @dailyCallsThreshold
  Scenario Outline: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD] Per un e-service in stato DRAFT è possibile modificare dailyCallsPerConsumer all'interno degli attributi certificati
    Given l'utente è un "admin" di "PA2"
    And PA2 ha già creato 1 attributo CERTIFIED
    And l'utente assegna a "PA1" l'attributo certificato precedentemente creato
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC"
    When l'utente modifica dailyCallsPerConsumer con <dailyCallsPerConsumer> per l'attributo certificato appena creato
    Then si ottiene status code <statusCode>

    Examples:
      | dailyCallsPerConsumer | statusCode |
      | 100                   | 200        |
      | 0                     | 400        |
      | 1000000000            | 200        |
      | 1000000001            | 400        |
