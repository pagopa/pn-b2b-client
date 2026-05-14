Feature: : Debugger Client Assertion Sync Bearer

  @devToolsClientAssertion
  Scenario: [CLIENT_ASSERTION_CONTRACT_VALIDATION_MISSING_AUTH] Dato un client CONSUMER valido, quando la richiesta di validazione non contiene un token di autenticazione valido la chiamata fallisce
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And "PA1" crea una client assertion valida per un client di tipo CONSUMER
    When "PA1" richiede la validazione della client assertion appena creata con un token di autorizzazione non valido
    Then si ottiene response status code 401

  @wait_for_fix
  @devToolsClientAssertion
  Scenario Outline: [CLIENT_ASSERTION_CONTRACT_VALIDATION_AUTHORIZED_ROLES] la richiesta di validazione della client assertion da parte di un utente che appartiene ad un ruolo autorizzato va a buon fine
    Given l'admin del fruitore "<ente>" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "<ente>" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And "<ente>" crea una client assertion valida per un client di tipo CONSUMER
    When un "<ruolo>" di "<ente>" richiede la validazione della client assertion appena creata
    Then si ottiene response status code <risultato>

    Examples:
      | ente | ruolo        | risultato |
      | PA1  | admin        | 200       |
      | PA1  | api          | 200       |
      | PA1  | api,security | 200       |
      | PA1  | security     | 200       |
      | PA1  | support      | 200       |
      | GSP  | admin        | 200       |
      | GSP  | api          | 200       |
      | GSP  | api,security | 200       |
      | GSP  | security     | 200       |
      | GSP  | support      | 200       |
