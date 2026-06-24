@client
Feature: Aggiunta di un membro ad un client
  Tutti gli admin possono associare un membro ad un client

  @nrt-minimal
  Scenario Outline: [CLIENT_USER_ADD_1] Un utente  admin, api, security, o support; appartenente all'ente che ha creato il client; il quale utente è già censito tra gli appartenenti all’ente ma non appartiene al client (anche se l’utente da aggiungere è l’utente stesso); associa un membro ad un client. L’operazione va a buon fine solo per il ruolo admin.
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato 1 client "CONSUMER"
    When l'utente richiede l'aggiunta di un admin di "<ente>" al client
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | admin        |        204 |
      | PA1  | admin        |        204 |

    @sad-path
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | api          |        403 |
      | GSP  | security     |        403 |
      | GSP  | support      |        403 |
      | GSP  | api,security |        403 |
      | PA1  | api          |        403 |
      | PA1  | security     |        403 |
      | PA1  | support      |        403 |
      | PA1  | api,security |        403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | reviewer     |        403 |
      | GSP  | viewer       |        403 |
      | PA2  | reviewer     |        403 |
      | PA2  | viewer       |        403 |

  @sad-path
  @nrt-minimal
  Scenario: [CLIENT_USER_ADD_2] Un utente con sufficienti permessi (admin); appartenente all'ente che ha creato il client; aggiunge al client un admin che è associato ad un altro ente. Ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    When l'utente richiede l'aggiunta di un admin di "PA2" al client
    Then si ottiene status code 403

  @nrt-minimal
  Scenario Outline: [CLIENT_USER_ROLE_ADD_1] Un utente  admin, api, security, o support; appartenente all'ente che ha creato il client; il quale utente è già censito tra gli appartenenti all’ente ma non appartiene al client (anche se l’utente da aggiungere è l’utente stesso); associa un membro ad un client. L’operazione va a buon fine solo per il ruolo admin.
    Given l'utente è un "admin" di "<ente>"
    Given "<ente>" ha già creato 1 client "CONSUMER"
    When l'utente richiede l'aggiunta di un "<ruolo>" di "<ente>" al client
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | admin        |        204 |
      | PA1  | admin        |        204 |
      | GSP  | security     |        204 |
      | GSP  | support      |        204 |
      | GSP  | api,security |        204 |
      | PA1  | security     |        204 |
      | PA1  | support      |        204 |
      | PA1  | api,security |        204 |

    @sad-path
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | api          |        403 |
      | PA1  | api          |        403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | reviewer     |        403 |
      | GSP  | viewer       |        403 |
      | PA2  | reviewer     |        403 |
      | PA2  | viewer       |        403 |
