@eservice
Feature: Cancellazione di un e-service
  Tutti gli utenti autorizzati di enti erogatori possono cancellare un proprio e-service con un solo descrittore in DRAFT

  @nrt-minimal
  @eservice_delete1
  Scenario Outline: [ESERVICE_DELETE_1] Per un e-service precedentemente creato, con un solo descrittore, la cancellazione dell'e-service avviene correttamente per i ruoli autorizzati
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in DRAFT
    When l'utente cancella quell'e-service
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | admin        |       204 |
      | GSP  | api          |       204 |
      | GSP  | api,security |       204 |
      | PA1  | admin        |       204 |
      | PA1  | api          |       204 |
      | PA1  | api,security |       204 |

    @sad-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | security     |       403 |
      | GSP  | support      |       403 |
      | PA1  | security     |       403 |
      | PA1  | support      |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | reviewer     |       403 |
      | GSP  | viewer       |       403 |
      | PA2  | reviewer     |       403 |
      | PA2  | viewer       |       403 |

  @sad-path
  @nrt-minimal
  @eservice_delete2
  Scenario Outline: [ESERVICE_DELETE_2] Per un e-service che ha un solo descrittore, il quale è in qualsiasi stato NON DRAFT (PUBLISHED, SUSPENDED, DEPRECATED, ARCHIVED), la cancellazione dell'e-service restituisce errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoDescrittore>"
    When l'utente cancella quell'e-service
    Then si ottiene status code 409

    Examples: 
      | statoDescrittore |
      | PUBLISHED        |
      | SUSPENDED        |
      | DEPRECATED       |
      | ARCHIVED         |

  @happy-path
  @nrt-minimal
  @eservice_delete3
  Scenario: [ESERVICE_DELETE_3] Per un e-service che ha un solo descrittore, il quale è in stato DRAFT, la cancellazione dell'e-service va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente cancella quell'e-service
    Then si ottiene status code 204

