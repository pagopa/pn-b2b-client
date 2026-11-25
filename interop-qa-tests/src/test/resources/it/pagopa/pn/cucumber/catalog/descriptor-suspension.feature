@descriptor
Feature: Sospensione di un descrittore
  Tutti gli utenti autorizzati di enti erogatori possono sospendere i propri descrittori

  @nrt-minimal
  @descriptor_suspension1
  Scenario Outline: [DESCRIPTOR_SUSPENSION_1] Per un e-service che ha un descrittore in stato PUBLISHED o DEPRECATED, alla richiesta di sospensione da parte di un utente autorizzato, la sospensione va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in stato "<statoVersione>"
    When l'utente sospende quel descrittore
    Then si ottiene status code <risultato>

    @happy-path
    Examples: # Test sui ruoli
      | ente | ruolo        | statoVersione | risultato |
      | GSP  | admin        | PUBLISHED     |       204 |
      | GSP  | api,security | PUBLISHED     |       204 |
      | PA1  | admin        | PUBLISHED     |       204 |
      | PA1  | api,security | PUBLISHED     |       204 |

    @sad-path
    Examples: # Test sui ruoli
      | ente | ruolo        | statoVersione | risultato |
      | GSP  | security     | PUBLISHED     |       403 |
      | GSP  | support      | PUBLISHED     |       403 |
      | PA1  | security     | PUBLISHED     |       403 |
      | PA1  | support      | PUBLISHED     |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples: # Test sui ruoli
      | ente | ruolo        | statoVersione | risultato |
      | GSP  | reviewer     | PUBLISHED     |       403 |
      | GSP  | viewer       | PUBLISHED     |       403 |
      | PA2  | reviewer     | PUBLISHED     |       403 |
      | PA2  | viewer       | PUBLISHED     |       403 |

    @happy-path
    Examples: # Test sugli stati
      | ente | ruolo | statoVersione | risultato |
      | PA1  | admin | DEPRECATED    |       204 |

  @sad-path
  @nrt-minimal
  @descriptor_suspension2
  Scenario Outline: [DESCRIPTOR_SUSPENSION_2] Per un e-service che ha un descrittore in stato ARCHIVED, DRAFT o SUSPENDED, alla richiesta di sospensione, si ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoVersione>"
    When l'utente sospende quel descrittore
    Then si ottiene status code 400

    Examples: 
      | statoVersione |
      | DRAFT         |
      | SUSPENDED     |
      | ARCHIVED      |
