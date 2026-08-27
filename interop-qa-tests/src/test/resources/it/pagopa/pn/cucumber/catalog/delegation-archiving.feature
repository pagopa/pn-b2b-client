@delegation-manual-archiving-eservice
Feature: Gestione deleghe per archiviazione manuale e-service

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_1.1] Un ente delegato può richiedere al delegante di avviare il processo di archiviazione di un e-service in delega
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204

    Examples:
      | descriptorState | role         |
      | PUBLISHED       | admin        |
      | PUBLISHED       | api          |
      | PUBLISHED       | api,security |
      | SUSPENDED       | admin        |
      | SUSPENDED       | api          |
      | SUSPENDED       | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_1.2] Un ente delegato può richiedere al delegante di avviare il processo di archiviazione del descrittore meno recente di un e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 204

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_1.3] Un utente appartente all'ente delegato ma con ruolo non ammesso NON può richiedere al delegante di avviare il processo di archiviazione di un e-service in delega
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 403

    Examples:
      | role     |
      | support  |
      | security |
      | reviewer |
      | viewer   |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_1.4] Un utente appartente all'ente delegato ma con ruolo non ammesso NON può richiedere al delegante di avviare il processo di archiviazione del descrittore meno recente di un e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 403

    Examples:
      | role     |
      | support  |
      | security |
      | reviewer |
      | viewer   |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_1.5] Un ente diverso dal delegato NON può richiedere al delegante di avviare il processo di archiviazione di un e-service in delega
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    When l'utente invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 403

    Examples:
      | role     |
      | support  |
      | security |
      | reviewer |
      | viewer   |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_1.6] Un ente diverso dal delegato NON può richiedere al delegante di avviare il processo di archiviazione del descrittore meno recente di un e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    When l'utente invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 403

    Examples:
      | role     |
      | support  |
      | security |
      | reviewer |
      | viewer   |

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_2.1] Un ente delegante può accettare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "<role>" di "PA1"
    When l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 204

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_2.2] Un ente delegante può accettare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'utente è un "<role>" di "PA1"
    When l'utente delegante accetta la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 204

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_2.3] Un ente diverso dal delegante NON può accettare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "<tenant>"
    When l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 403

    Examples:
      | tenant |
      | PA2    |
      | PA3    |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_2.4] Un ente diverso dal delegante NON può accettare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'utente è un "<role>" di "<tenant>"
    When l'utente delegante accetta la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 403

    Examples:
      | tenant |
      | PA2    |
      | PA3    |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_2.5] Un utente appartente all'ente delegante ma con ruolo non ammesso NON può accettare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "<role>" di "PA1"
    When l'utente accetta la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 403

    Examples:
      | role     |
      | support  |
      | security |
      | reviewer |
      | viewer   |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_2.6] Un utente appartente all'ente delegante ma con ruolo non ammesso NON può accettare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'utente è un "<role>" di "PA1"
    When l'utente accetta la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 403

    Examples:
      | role     |
      | support  |
      | security |
      | reviewer |
      | viewer   |

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_3.1] Un ente delegante può rifiutare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "<role>" di "PA1"
    When l'utente delegante rifiuta la richiesta di archiviazione delegata dell'e-service "%actual" con motivazione "QA test rejection delegation manual archiving"
    Then si ottiene response status code 204

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_3.2] Un ente delegante può rifiutare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'utente è un "<role>" di "PA1"
    When l'utente delegante rifiuta la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" con motivazione "QA test rejection delegation manual archiving"
    Then si ottiene response status code 204

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_3.3] Un utente appartente all'ente delegante ma con ruolo non ammesso NON può rifiutare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "<role>" di "PA1"
    When l'utente delegante rifiuta la richiesta di archiviazione delegata dell'e-service "%actual" con motivazione "QA test rejection delegation manual archiving"
    Then si ottiene response status code 403

    Examples:
      | role     |
      | support  |
      | security |
      | reviewer |
      | viewer   |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_3.4] Un utente appartente all'ente delegato ma con ruolo non ammesso NON può rifiutare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'utente è un "<role>" di "PA1"
    When l'utente delegante rifiuta la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" con motivazione "QA test rejection delegation manual archiving"
    Then si ottiene response status code 403

    Examples:
      | role     |
      | support  |
      | security |
      | reviewer |
      | viewer   |

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CANCELLATION_1.1] Un ente delegante può annullare il processo di archiviazione di un e-service con una delega in erogazione attiva
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA1"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CANCELLATION_1.2] Un ente delegante può annullare il processo di archiviazione del descrittore meno recente di un e-service con una delega in erogazione attiva
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA1"
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'archiviazione manuale del singolo descrittore è stata annullata con successo
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_CANCELLATION_2.1] Un ente delegato NON può annullare il processo di archiviazione di un e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And l'annullamento dell'archiviazione manuale dell'intero e-service sul descrittore più recente, è fallita

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_CANCELLATION_2.2] Un ente delegato NON può annullare il processo di archiviazione del descrittore meno recente di un e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente annulla il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 403
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And l'annullamento dell'archiviazione manuale del vecchio descrittore è fallita
    And la versione più recente dell'e-service è in stato "PUBLISHED"
