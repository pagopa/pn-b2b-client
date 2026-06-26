Feature: : Debugger Client Assertion Sync Bearer

  @devToolsClientAssertion
  Scenario: [CLIENT_ASSERTION_CONTRACT_VALIDATION_MISSING_AUTH] Dato un client CONSUMER valido, quando la richiesta di validazione non contiene un token di autenticazione valido la chiamata fallisce
    Given l'admin del fruitore "PA1" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "PA1" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "PA1" crea una client assertion valida per un client di tipo CONSUMER
    When "PA1" richiede la validazione della client assertion appena creata con un token di autorizzazione non valido
    Then si ottiene response status code 401

  # https://pagopaspa.slack.com/archives/C0A7AMD53MM/p1778754777127059
  @devToolsClientAssertion
  Scenario Outline: [CLIENT_ASSERTION_CONTRACT_VALIDATION_AUTHORIZED_ROLES_1] la richiesta di validazione della client assertion da parte di un utente
    va a buon fine soltanto se ha ruolo admin, support oppure se ha ruolo security ed è l'owner della chiave.
    Se il ruolo dell'utente è api la richiesta non va a buon fine.
    Given l'admin del fruitore "<ente>" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "<ente>" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "<ente>" crea una client assertion valida per un client di tipo CONSUMER
    When un "<ruolo>" di "<ente>" richiede la validazione della client assertion appena creata
    Then si ottiene response status code <risultato>

    Examples:
      | ente | ruolo        | risultato |
      | PA1  | admin        | 200       |
      | PA1  | api          | 403       |
      | PA1  | api,security | 403       |
      | PA1  | security     | 403       |
      | PA1  | support      | 200       |
      | GSP  | admin        | 200       |
      | GSP  | api          | 403       |
      | GSP  | api,security | 403       |
      | GSP  | security     | 403       |
      | GSP  | support      | 200       |

  @devToolsClientAssertion
  Scenario Outline: [CLIENT_ASSERTION_CONTRACT_VALIDATION_AUTHORIZED_ROLES_2] la richiesta di validazione della client assertion da parte di un utente che appartiene ad un ruolo autorizzato
    ed è l'owner della chiave va a buon fine
    Given un "<ruolo>" del fruitore "<ente>" ha già creato un client di tipo CONSUMER aggiungendo se stesso come membro e caricando una coppia di chiavi
    And l'admin dell'erogatore "PA2" ha creato un eservice e l'admin del fruitore "<ente>" ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client
    And il tenant fruitore "<ente>" crea una client assertion valida per un client di tipo CONSUMER
    When un "<ruolo>" di "<ente>" richiede la validazione della client assertion appena creata
    Then si ottiene response status code <risultato>

    Examples:
      | ente | ruolo        | risultato |
      | PA1  | admin        | 200       |
      | PA1  | security     | 200       |
