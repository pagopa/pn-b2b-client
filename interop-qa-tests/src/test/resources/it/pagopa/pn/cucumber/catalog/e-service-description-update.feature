@eservice
Feature: Aggiornamento della descrizione di un e-service
  Tutti gli utenti autorizzati di enti erogatori possono aggiornare la descrizione un proprio e-service a catalogo

  @nrt-minimal
  @eservice_description_update1
  @nrt-minimal
  @eservice_description_update1
  Scenario Outline: [ESERVICE_DESCRIPTION_UPDATE_1] A fronte di una richiesta aggiornamento della descrizione di un e-service da parte di un utente autorizzato dell'ente che lo eroga, va a buon fine solo per un e-service con descrittori in stato PUBLISHED, SUSPENDED e DEPRECATED
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in stato "<statoDescrittore>"
    When l'utente aggiorna la descrizione di quell'e-service
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | statoDescrittore | risultato |
      | PA1  | admin        | PUBLISHED        |       200 |
      | PA1  | api          | PUBLISHED        |       200 |
      | PA1  | api,security | PUBLISHED        |       200 |
      | GSP  | admin        | PUBLISHED        |       200 |
      | GSP  | api          | PUBLISHED        |       200 |
      | GSP  | api,security | PUBLISHED        |       200 |

    @sad-path
    Examples:
      | ente | ruolo        | statoDescrittore | risultato |
      | PA1  | security     | PUBLISHED        |       403 |
      | PA1  | support      | PUBLISHED        |       403 |
      | GSP  | security     | PUBLISHED        |       403 |
      | GSP  | support      | PUBLISHED        |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | statoDescrittore | risultato |
      | GSP  | reviewer     | PUBLISHED        |       403 |
      | GSP  | viewer       | PUBLISHED        |       403 |
      | PA2  | reviewer     | PUBLISHED        |       403 |
      | PA2  | viewer       | PUBLISHED        |       403 |

    @happy-path
    Examples: # ARCHIVED non viene testato in quanto non è possibile avere un eservice con un singolo descrittore in stato ARCHIVED
      | ente | ruolo | statoDescrittore | risultato |
      | PA1  | admin | SUSPENDED        |       200 |
      | PA1  | admin | DEPRECATED       |       200 |

    @sad-path
    Examples: # ARCHIVED non viene testato in quanto non è possibile avere un eservice con un singolo descrittore in stato ARCHIVED
      | ente | ruolo | statoDescrittore | risultato |
      | PA1  | admin | DRAFT            |       409 |

  @sad-path
  @nrt-minimal
  @eservice_description_update2
  Scenario: [ESERVICE_DESCRIPTION_UPDATE_2] A fronte di una richiesta aggiornamento della descrizione di un e-service da parte di un utente autorizzato dell’ente che lo eroga, per un e-service con un descrittore in DRAFT, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in DRAFT
    When l'utente aggiorna la descrizione di quell'e-service
    Then si ottiene status code 409

  @eservice_description_max_length
  Scenario: [ESERVICE_DESCRIPTION_UPDATE_MAXLENGTH_1] Un utente aggiorna un e-service utilizzando la descrizione della lunghezza massima possibile
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente aggiorna la descrizione di quell'e-service in stato "PUBLISHED" con un valore di 400 caratteri
    And si ottiene status code 200
    Then l'e-service creato ha una descrizione di 400 caratteri

  @eservice_description_max_length
  Scenario: [ESERVICE_DESCRIPTION_UPDATE_MAXLENGTH_2] L'aggiornamento dell'e-service non va a buon fine se viene superata la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente aggiorna la descrizione di quell'e-service in stato "PUBLISHED" con un valore di 401 caratteri
    Then si ottiene status code 400

  @eservice_description_max_length
  Scenario: [ESERVICE_DESCRIPTION_UPDATE_MAXLENGTH_3] Un utente aggiorna un e-service in DRAFT utilizzando la descrizione della lunghezza massima possibile
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente aggiorna la descrizione di quell'e-service in stato "DRAFT" con un valore di 400 caratteri
    And si ottiene status code 200
    Then l'e-service creato ha una descrizione di 400 caratteri

  @eservice_description_max_length
  Scenario: [ESERVICE_DESCRIPTION_UPDATE_MAXLENGTH_4] L'aggiornamento dell'e-service in DRAFT non va a buon fine se viene superata la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente aggiorna la descrizione di quell'e-service in stato "DRAFT" con un valore di 401 caratteri
    And si ottiene status code 400
