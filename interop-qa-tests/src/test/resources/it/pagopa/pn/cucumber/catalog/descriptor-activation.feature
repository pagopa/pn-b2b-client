@descriptor
Feature: Attivazione di un descrittore
  Tutti gli utenti autorizzati di enti erogatori possono attivare un descrittore in stato SUSPENDED

  @nrt-minimal
  @descriptor_activation1
  Scenario Outline: [DESCRIPTOR_ACTIVATION_1] Per un e-service che ha un solo descrittore, il quale è in stato SUSPENDED, all'attivazione del descrittore, torna allo stato PUBLISHED
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in stato "SUSPENDED"
    When l'utente attiva il descrittore di quell'e-service
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
  @descriptor_activation2
  # Ticket aperto https://pagopa.atlassian.net/browse/PIN-9006
  Scenario Outline: [DESCRIPTOR_ACTIVATION_2] Per un e-service che ha un solo descrittore, il quale non si trova in stato SUSPENDED, alla riattivazione del descrittore, si ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoDescrittore>"
    When l'utente attiva il descrittore di quell'e-service
    Then si ottiene status code 409

    Examples:
      | statoDescrittore |
      | ARCHIVED         |
      | DRAFT            |
      | DEPRECATED       |
      | PUBLISHED        |

  @debug
  @adeguamento-analisi-rischio
  Scenario Outline: [DESCRIPTOR_TK_ACTIVATION_1] A seguito del cambiamento di tenant kind si tenta di ri-attivare un proprio e-service sospeso
    Given l'utente è un "admin" di "<ente>"
    And "<ente>" ha già creato un e-service con un descrittore in stato "SUSPENDED"
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind>"
    When l'utente attiva il descrittore di quell'e-service
    Then si ottiene status code 200
    And il descrittore risulta in stato "PUBLISHED"
    Examples:
      | ente    | kind        |
      | PA4     | PRIVATE     |
      | PA4     | GSP         |
      | GSP2    | PA          |
      | Privato | PA          |

