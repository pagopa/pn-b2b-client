@client
Feature: Lettura di una chiave pubblica contenuta in un client
  Tutti gli utenti autenticati possono recuperare le informazioni di una chiave pubblica contenuta in un client 

  @nrtC-waitForFix
  @nrt-minimal
  Scenario Outline: [CLIENT_KEY_READ_05] Un utente, il quale è appartenente all’ente al quale è associato un client; il quale utente NON è membro del client; per il quale client c'è una chiave, caricata da un altro utente; richiede la lettura delle informazioni della chiave pubblica. L'operazione va a buon fine solo per admin, support, security
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato 1 client "CONSUMER"
    Given "<ente>" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "<ente>" ha caricato una chiave pubblica nel client
    When l'utente richiede la lettura della chiave pubblica
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | admin        |        200 |
      | GSP  | support      |        200 |
      | PA1  | admin        |        200 |
      | PA1  | support      |        200 |

    @sad-path
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | api          |        403 |
      | GSP  | security     |        403 |
      | GSP  | api,security |        403 |
      | PA1  | api          |        403 |
      | PA1  | api,security |        403 |
      | PA1  | security     |        403 |

  @nrtC-waitForFix
  @nrt-minimal
  Scenario Outline: [CLIENT_KEY_READ_06] Un utente con permessi di security; il quale è appartenente all'ente al quale è associato un client; il quale utente è membro del client; per il quale client c'è una chiave, caricata da un altro utente; richiede la lettura delle informazioni della chiave pubblica. L'operazione va a buon fine.
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato 1 client "CONSUMER"
    Given "<ente>" ha già inserito l'utente con ruolo "security" come membro di quel client
    Given "<ente>" ha già inserito l'utente con ruolo "api,security" come membro di quel client
    Given "<ente>" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "<ente>" ha caricato una chiave pubblica in quel client
    When l'utente richiede la lettura della chiave pubblica
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | admin        |        200 |
      | GSP  | support      |        200 |
      | GSP  | api,security |        200 |
      | GSP  | security     |        200 |
      | PA1  | admin        |        200 |
      | PA1  | support      |        200 |
      | PA1  | api,security |        200 |
      | PA1  | security     |        200 |

    @sad-path
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | api          |        403 |
      | PA1  | api          |        403 |
