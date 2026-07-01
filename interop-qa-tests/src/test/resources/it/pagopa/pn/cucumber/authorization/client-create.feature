@client
Feature: Creazione di un client
  Tutti gli admin possono creare un client

  @nrt-minimal
  Scenario Outline: [CREATE_CLIENT_1] Un utente con sufficienti permessi (admin); inserisce nome e descrizione, e crea un nuovo client di tipo CONSUMER. L'operazione va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    When l'utente richiede la creazione di un client "CONSUMER"
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | admin        |        200 |
      | PA1  | admin        |        200 |

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

  @nrt-minimal
  Scenario Outline: [CREATE_CLIENT_2] Un utente con sufficienti permessi (admin); inserisce nome e descrizione, e crea un nuovo client di tipo API. L'operazione va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    When l'utente richiede la creazione di un client "API"
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | admin        |        200 |
      | PA1  | admin        |        200 |

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
