Feature: Creazione di una delega

  Background:
    Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe

  Scenario Outline: Il richiamo dell’API di creazione di una delega possa essere compiuto da un utente di livello operatore amministrativo (admin)
    Given l'utente è un "<ruolo>" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega per l'ente "PA2"
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo        | statusCode |
      | admin        |        200 |
      | api          |        403 |
      | security     |        403 |
      | api,security |        403 |
      | support      |        403 |


  Scenario Outline: Il rifiuto di una delega in stato di pending possa essere compiuto solo da un utente con ruolo admin
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And un utente dell'ente <funzione> con ruolo "<ruolo>"
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegato concede la disponibilità a ricevere deleghe
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    And la delega è stata creata correttamente
    When l'utente rifiuta la delega
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo        | funzione  | statusCode  |

      # Scenario: 26
      # Esito: coerente
      | admin        | delegato  | 200         |

      # Scenario: 6
      # Esito: coerente
      | api          | delegato  | 403         |

      # Scenario: 6
      # Esito: coerente
      | security     | delegato  | 403         |

      # Scenario: 6
      # Esito: coerente
      | api,security | delegato  | 403         |

      # Scenario: 6
      # Esito: coerente
      | support      | delegato  | 403         |

      # Scenario: 28
      # Esito: incoerente, 403, "Unauthorized"
      | admin        | delegante | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api          | delegante | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | security     | delegante | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api,security | delegante | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | support      | delegante | 403         |

  Scenario Outline: Il rifiuto di una delega già accettata non possa essere compiuto da nessun utente indipentendemente dal ruolo
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And un utente dell'ente <funzione> con ruolo "<ruolo>"
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegato concede la disponibilità a ricevere deleghe
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    And la delega è stata creata correttamente
    And l'ente delegato ha accettato la delega
    When l'utente rifiuta la delega
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo        | funzione  | statusCode  |

      # Scenario: 29
      # Esito: coerente
      | admin        | delegato  | 409         |

      # Scenario: <mancante>
      # Esito: si ottiene 403
      | api          | delegato  | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403
      | security     | delegato  | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403
      | api,security | delegato  | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403
      | support      | delegato  | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403 "Operation restricted to delegate"
      | admin        | delegante | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api          | delegante | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | security     | delegante | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api,security | delegante | 403         |

      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | support      | delegante | 403         |