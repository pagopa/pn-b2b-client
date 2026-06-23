@client_admin
Feature: Associazione di un admin ad un client

  @happy-path
  @client_admin_create
  Scenario: [ADMIN_CLIENT_1] Un utente admin può impostare se stesso come amministratore di un client API
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    When l'utente tenta la modifica dell'amministratore del client indicando se stesso
    Then si ottiene status code 200
    And l'amministratore del client è stato modificato correttamente

  @sad-path
  @client_admin_create
  Scenario: [ADMIN_CLIENT_2] Un utente admin non può impostare se stesso come amministratore di un client inesistente
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la modifica dell'amministratore di un client inesistente
    Then si ottiene status code 404

  @sad-path
  @client_admin_create
  @nuovi-operatori-update
  Scenario Outline: [ADMIN_CLIENT_3] Un utente non admin non può impostare se stesso come amministratore di un client API
    Given l'utente è un "<ruolo>" di "<ente>"
    And "<ente>" ha già creato 1 client "API"
    When l'utente tenta la modifica dell'amministratore del client indicando se stesso
    Then si ottiene status code 403
    Examples:
      | ruolo     | ente |
      | api       | PA1  |
      | reviewer  | PA2  |
      | viewer    | PA2  |
      | support   | PA1  |
      | security  | PA1  |

  @sad-path
  @client_admin_create
  Scenario: [ADMIN_CLIENT_4] Un utente admin non può impostare se stesso come amministratore di un client indicando delle specifiche vuote
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    When l'utente tenta la modifica dell'amministratore indicando delle specifiche vuote
    Then si ottiene status code 400

  @sad-path
  @client_admin_create
  @nuovi-operatori-update
  Scenario Outline: [ADMIN_CLIENT_5] Un utente admin non può impostare un altro utente non-admin come amministratore di un client API
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato 1 client "API"
    When l'utente tenta di impostare "PA2" con ruolo "<ruolo>" come amministratore del client
    Then si ottiene status code 403
    Examples:
      | ruolo    |
      | api      |
      | reviewer |
      | viewer   |

  @sad-path
  @client_admin_create
  Scenario: [ADMIN_CLIENT_6] Un utente non può impostare un utente come amministratore del client se fa capo ad un ente diverso da quello creatore del client
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica dell'amministratore del client indicando se stesso
    Then si ottiene status code 403

  @sad-path
  @client_admin_create
  Scenario: [ADMIN_CLIENT_7] Un utente admin non può impostare se stesso come amministratore di un client CONSUMER
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    When l'utente tenta la modifica dell'amministratore del client indicando se stesso
    Then si ottiene status code 403

  # NOTA 09/05/2025: si usa PA2 perché al momento è l'unico ente avente 2 utenti con ruolo admin
  @happy-path
  @client_admin_update
  Scenario: [ADMIN_CLIENT_8] Un utente admin può sostituire un amministratore di un client API indicando un altro utente amministratore
    Given l'utente è il numero 1 ad avere ruolo "admin" di "PA2"
    And "PA2" ha già creato 1 client "API"
    When l'utente tenta la modifica dell'amministratore del client indicando l'admin numero 2 del suo ente
    Then si ottiene status code 200
    And l'amministratore del client è stato modificato correttamente

  @sad-path
  @client_admin_update
  Scenario: [ADMIN_CLIENT_9] Un utente admin non può sostituire un amministratore di un client API indicando lo stesso utente amministratore pre-esistente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    And l'utente effettua la modifica dell'amministratore del client indicando se stesso con successo
    When l'utente tenta la modifica dell'amministratore del client indicando se stesso
    Then si ottiene status code 409

  @happy-path
  @client_admin_delete
  Scenario: [ADMIN_CLIENT_10] Un utente admin può rimuovere un utente dal ruolo di amministratore di un client API
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    And l'utente effettua la modifica dell'amministratore del client indicando se stesso con successo
    When l'utente tenta la rimozione dell'amministratore del client
    Then si ottiene status code 200
    And l'amministratore del client è stato rimosso correttamente

  @sad-path
  @client_admin_delete
  Scenario: [ADMIN_CLIENT_11] Un utente admin non può rimuovere un utente dal ruolo di amministratore di un client API specificando un clientId inesistente
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la rimozione dell'amministratore del client specificando un clientId inesistente ed il proprio adminId
    Then si ottiene status code 404

  @sad-path
  @client_admin_delete
  Scenario: [ADMIN_CLIENT_12] Un utente admin non può rimuovere un utente dal ruolo di amministratore di un client API specificando un adminId inesistente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    When l'utente tenta la rimozione dell'amministratore del client specificando un adminId inesistente
    Then si ottiene status code 400

  @sad-path
  @client_admin_delete
  @nuovi-operatori-update
  Scenario Outline: [ADMIN_CLIENT_13] Un utente non-admin non può rimuovere un utente dal ruolo di amministratore di un client API
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato 1 client "API"
    And l'utente effettua la modifica dell'amministratore del client indicando se stesso con successo
    When l'utente è un "<ruolo>" di "PA2"
    And l'utente tenta la rimozione dell'amministratore del client
    Then si ottiene status code 403
    Examples:
      | ruolo     |
      | api       |
      | reviewer  |
      | viewer    |
      | support   |
      | security  |

  @sad-path
  @client_admin_delete
  Scenario: [ADMIN_CLIENT_14] Un utente admin non può rimuovere un utente dal ruolo di amministratore di un client API indicando delle specifiche vuote
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    And l'utente effettua la modifica dell'amministratore del client indicando se stesso con successo
    When l'utente tenta la rimozione dell'amministratore del client indicando delle specifiche vuote
    Then si ottiene status code 400

  @sad-path
  @client_admin_delete
  @nuovi-operatori-update
  Scenario Outline: [ADMIN_CLIENT_15] Un utente admin non può rimuovere un utente dal ruolo di amministratore di un client API se fa capo ad un ente diverso da quello creatore del client
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato 1 client "API"
    And l'utente effettua la modifica dell'amministratore del client indicando se stesso con successo
    When l'utente è un "<ruolo>" di "GSP"
    And l'utente tenta la rimozione dell'amministratore del client
    Then si ottiene status code 403
    Examples:
      | ruolo    |
      | admin    |
      | api      |
      | reviewer |
      | viewer   |

  # NOTA 16/05/2025: "figlio" dello scenario [ADMIN_CLIENT_7], poiché non potendo impostare
  # un altro admin come amministratore del client, non si può nemmeno rimuovere. Per cui
  # questo test tenterà sempre di rimuovere un admin inesisten, portando allo stesso errore
  # previsto dallo scenario [ADMIN_CLIENT_12]
  @sad-path
  @client_admin_delete
  Scenario: [ADMIN_CLIENT_16] Un utente admin non può rimuovere un utente dal ruolo di amministratore di un client CONSUMER
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    When l'utente tenta la rimozione dell'amministratore del client
    Then si ottiene status code 400