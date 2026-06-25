@client
Feature: Lettura client singolo
  Tutti gli utenti autenticati possono leggere un singolo client

  @nrt-minimal
  Scenario Outline: [CLIENT_READ_1] Tutti gli utenti possono leggere un client appartenente al proprio ente. La richiesta va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato 1 client "CONSUMER"
    When l'utente richiede una operazione di lettura di quel client
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ente    | ruolo        | statusCode |
      | GSP     | admin        |        200 |
      | GSP     | security     |        200 |
      | GSP     | support      |        200 |
      | GSP     | api,security |        200 |
      | PA1     | admin        |        200 |
      | PA1     | security     |        200 |
      | PA1     | support      |        200 |
      | PA1     | api,security |        200 |
      | Privato | admin        |        200 |
      | Privato | security     |        200 |
      | Privato | support      |        200 |
      | Privato | api,security |        200 |

    @sad-path
    Examples:
      | ente    | ruolo        | statusCode |
      | GSP     | api          |        403 |
      | PA1     | api          |        403 |
      | Privato | api          |        403 |

  @happy-path @nrt-minimal
  Scenario: [CLIENT_READ_2] Un utente admin legge un client appartenente al proprio ente. La richiesta va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    When l'utente richiede una operazione di lettura di quel client
    Then si ottiene status code 200

  @happy-path
  @nrt-minimal
  Scenario: [CLIENT_READ_3] Un utente security legge un client del quale è membro. La richiesta va a buon fine
    Given l'utente è un "security" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    Given "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    When l'utente richiede una operazione di lettura di quel client
    Then si ottiene status code 200

  @sad-path
  @nrt-minimal
  @wait_for_fix
  Scenario: [CLIENT_READ_4] Un utente security legge un client di un ente al quale è stato associato, ma del quale non è membro. Ottiene un errore
    Given l'utente è un "security" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    When l'utente richiede una operazione di lettura di quel client
    Then si ottiene status code 400

  @happy-path
  @nrt-minimal
  Scenario: [CLIENT_READ_5] Un utente support legge un client di un ente per il quale sta operando. La richiesta va a buon fine
    Given l'utente è un "support" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    When l'utente richiede una operazione di lettura di quel client
    Then si ottiene status code 200

  @sad-path
  @nrt-minimal
  Scenario: [CLIENT_READ_6] Un utente api legge un client dell'ente al quale è associato. Ottiene un errore
    Given l'utente è un "api" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    When l'utente richiede una operazione di lettura di quel client
    Then si ottiene status code 403

  @nrtC-waitForFix
  @happy-path
  @nrt-minimal
  Scenario: [CLIENT_READ_7] A fronte di una richiesta di lettura da parte di un ente, di un client creato da un altro ente, la richiesta NON va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato 1 client "CONSUMER"
    When l'utente richiede una operazione di lettura di quel client
    Then si ottiene status code 404