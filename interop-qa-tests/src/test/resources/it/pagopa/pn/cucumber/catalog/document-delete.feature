@document
Feature: Cancellazione di un documento
  Tutti gli utenti autorizzati di enti erogatori possono cancellare un documento dai propri descrittori

  @nrt-minimal
  @document_delete1
  Scenario Outline: [DESCRIPTOR_DELETE_1] Per un e-service che ha un solo descrittore, il quale è in uno dei sequenti stati: (PUBLISHED, DRAFT, DEPRECATED, SUSPENDED), alla richiesta di cancellazione di un documento precedentemente caricato, l'operazione va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in stato "<statoDescrittore>" e un documento già caricato
    When l'utente cancella quel documento
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | statoDescrittore | risultato |
      | GSP  | admin        | DRAFT            |       204 |
      | GSP  | api          | DRAFT            |       204 |
      | GSP  | api,security | DRAFT            |       204 |
      | PA1  | admin        | DRAFT            |       204 |
      | PA1  | api          | DRAFT            |       204 |
      | PA1  | api,security | DRAFT            |       204 |

    @sad-path
    Examples:
      | ente | ruolo        | statoDescrittore | risultato |
      | GSP  | security     | DRAFT            |       403 |
      | GSP  | support      | DRAFT            |       403 |
      | PA1  | security     | DRAFT            |       403 |
      | PA1  | support      | DRAFT            |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | statoDescrittore | risultato |
      | GSP  | reviewer     | DRAFT            |       403 |
      | GSP  | viewer       | DRAFT            |       403 |
      | PA2  | reviewer     | DRAFT            |       403 |
      | PA2  | viewer       | DRAFT            |       403 |

    @happy-path
    Examples: # Test sugli stati
      | ente | ruolo | statoDescrittore | risultato |
      | GSP  | admin | PUBLISHED        |       204 |
      | GSP  | admin | SUSPENDED        |       204 |
      | GSP  | admin | DEPRECATED       |       204 |

  @sad-path
  @nrt-minimal
  @document_delete2
  Scenario: [DESCRIPTOR_DELETE_2] Per un e-service che ha un solo descrittore, il quale è in stato ARCHIVED alla richiesta di cancellazione di un documento precedentemente caricato, si ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "ARCHIVED" e un documento già caricato
    When l'utente cancella quel documento
    Then si ottiene status code 400

  @happy-path
  @nrt-minimal
  @document_delete3
  Scenario: [DESCRIPTOR_DELETE_3] Per un e-service che ha un solo descrittore, il quale è in stato DRAFT, alla richiesta di cancellazione di un'interfaccia precedentemente caricata, l'operazione va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT" con un'interfaccia già caricata
    When l'utente cancella quell'interfaccia
    Then si ottiene status code 204

  @sad-path
  @nrt-minimal
  @document_delete4
  Scenario Outline: [DESCRIPTOR_DELETE_4] Per un e-service che ha un solo descrittore, il quale è in uno dei sequenti stati: (PUBLISHED, ARCHIVED, DEPRECATED, SUSPENDED), alla richiesta di cancellazione di un'interfaccia precedentemente caricata, si ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoDescrittore>" con un'interfaccia già caricata
    When l'utente cancella quell'interfaccia
    Then si ottiene status code 400

    Examples: # Test sugli stati
      | statoDescrittore |
      | PUBLISHED        |
      | SUSPENDED        |
      | DEPRECATED       |
      | ARCHIVED         |
