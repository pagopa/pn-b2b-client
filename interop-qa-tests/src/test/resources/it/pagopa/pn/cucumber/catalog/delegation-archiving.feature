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
    And la richiesta di archiviazione delegata dell'e-service è in stato pending

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
    And la richiesta di archiviazione delegata del vecchio descrittore è in stato pending

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
    And l'utente è un "admin" di "<tenant>"
    When l'utente invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 403

    Examples:
      | tenant |
      | PA2    |
      | PA3    |

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
    And l'utente è un "admin" di "<tenant>"
    When l'utente invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 403

    Examples:
      | tenant |
      | PA2    |
      | PA3    |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_1.7] Un ente delegato NON può richiedere al delegante di avviare il processo di archiviazione di un e-service in stato ARCHIVING o ARCHIVING_SUSPENDED
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "<eserviceState>"
    And l'utente è un "admin" di "PA1"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual archiving" e 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 400

    Examples:
      | eserviceState |
      | PUBLISHED     |
      | SUSPENDED     |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_1.8] Un ente delegato NON può richiedere al delegante di avviare il processo di archiviazione di un descrittore diverso dal meno recente in stato ARCHIVING
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 400

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_1.9] Un ente delegato NON può richiedere al delegante di avviare il processo di archiviazione di un descrittore diverso dal meno recente in stato ARCHIVING_SUSPENDED
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 400

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_1.10] Un ente delegato NON può richiedere al delegante di avviare il processo di archiviazione di un descrittore diverso dal meno recente in stato ARCHIVED
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And "PA3" ha già archiviato quella richiesta di fruizione
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 400

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
    And l'utente è un "admin" di "<tenant>"
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
    And la richiesta di archiviazione delegata dell'e-service è stata rifiutata con successo

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
    And la richiesta di archiviazione delegata del vecchio descrittore è stata rifiutata con successo

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

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_3.5] Un ente diverso dal delegante NON può rifiutare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "<tenant>"
    When l'utente delegante rifiuta la richiesta di archiviazione delegata dell'e-service "%actual" con motivazione "QA test rejection delegation manual archiving"
    Then si ottiene response status code 403

    Examples:
      | tenant |
      | PA2    |
      | PA3    |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_3.6] Un ente diverso dal delegante NON può rifiutare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
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
    And l'utente è un "admin" di "<tenant>"
    When l'utente delegante rifiuta la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" con motivazione "QA test rejection delegation manual archiving"
    Then si ottiene response status code 403

    Examples:
      | tenant |
      | PA2    |
      | PA3    |

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_4.1] L'ente delegato può annullare la richiesta di archiviazione dell'e-service precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione dell'e-service "%actual"
    Then si ottiene response status code 204
    And la richiesta di archiviazione pendente dell'e-service è stata annullata con successo
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_4.2] L'ente delegato può annullare la richiesta di archiviazione del descrittore meno recente precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 204
    And la richiesta di archiviazione pendente del vecchio descrittore è stata annullata con successo
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_4.3] Un utente appartente all'ente delegato ma con ruolo non ammesso NON può annullare la richiesta di archiviazione dell'e-service precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato annulla la richiesta di archiviazione dell'e-service "%actual"
    Then si ottiene response status code 403
    And la richiesta di archiviazione delegata dell'e-service è in stato pending
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role     |
      | support  |
      | security |
      | reviewer |
      | viewer   |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_4.4] Un utente appartente all'ente delegato ma con ruolo non ammesso NON può annullare la richiesta di archiviazione del descrittore meno recente precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato annulla la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 403
    And la richiesta di archiviazione delegata del vecchio descrittore è in stato pending
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role     |
      | support  |
      | security |
      | reviewer |
      | viewer   |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_4.5] Un ente diverso dal delegato NON può annullare la richiesta di archiviazione dell'e-service precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    When l'utente delegato annulla la richiesta di archiviazione dell'e-service "%actual"
    Then si ottiene response status code 403
    And la richiesta di archiviazione delegata dell'e-service è in stato pending
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_4.6] Un ente diverso dal delegato NON può annullare la richiesta di archiviazione del descrittore meno recente precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    When l'utente delegato annulla la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 403
    And la richiesta di archiviazione delegata del vecchio descrittore è in stato pending
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_4.7] Un ente diverso dal delegato NON può annullare la richiesta di archiviazione dell'e-service già precedentemente accettata
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test manual archiving" e 60 giorni di preavviso
#     TODO: da sostituire con il Given
    And l'utente è un "admin" di "PA1"
    And l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "%actual"
    And si ottiene response status code 204
    And l'e-service è in stato "ARCHIVING"
    And l'utente è un "admin" di "PA2"
    When l'utente delegato annulla la richiesta di archiviazione dell'e-service "%actual"
    Then si ottiene response status code 409

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_5.1] Un ente delegato può inviare una richiesta di archiviazione dell'e-service al delegante a seguito dell'annullamento del processo di archiviazione
  Verifichiamo che l’ente delegato all’erogazione possa inviare una nuova richiesta
  di archiviazione dell’e-service dopo l’accettazione della richiesta precedente e l'annullamento
  del processo di archiviazione da parte dell'ente delegante
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test manual archiving" e 60 giorni di preavviso
#     TODO: da sostituire con il Given
    And l'utente è un "admin" di "PA1"
    And l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "%actual"
    And si ottiene response status code 204
    And l'e-service è in stato "ARCHIVING"
    And l'utente annulla il processo di archiviazione dell'e-service con id "%actual"
    And si ottiene response status code 204
    And l'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204
    And la richiesta di archiviazione delegata dell'e-service è in stato pending

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_5.2] Un ente delegato può inviare una richiesta di archiviazione del singolo descrittore diverso dal più recente al delegante a seguito dell'annullamento del suo processo di archiviazione
  Verifichiamo che l’ente delegato all’erogazione possa inviare una nuova richiesta
  di archiviazione dell’e-service dopo l’accettazione della richiesta precedente e l'annullamento
  del processo di archiviazione da parte dell'ente delegante
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
#     TODO: da sostituire con il Given
    And l'utente è un "admin" di "PA1"
    And l'utente ha già rifiutato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" con motivazione "QA test"
    And si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 204
    And la richiesta di archiviazione delegata del vecchio descrittore è in stato pending

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_TEMPLATE_INSTANCE_1.1] Un ente delegato può richiedere al delegante di avviare il processo di archiviazione di un e-service creato da template
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <eserviceState> a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204

    Examples:
      | eserviceState | role         |
      | PUBLISHED     | admin        |
      | PUBLISHED     | api          |
      | PUBLISHED     | api,security |
      | SUSPENDED     | admin        |
      | SUSPENDED     | api          |
      | SUSPENDED     | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_TEMPLATE_INSTANCE_1.2] Un ente delegato può richiedere al delegante di avviare il processo di archiviazione del descrittore meno recente di un e-service creato da template
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 204

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_TEMPLATE_INSTANCE_2.1] Un ente delegante può accettare la richiesta di avvio del processo di archiviazione di un e-service creato da template inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
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
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_TEMPLATE_INSTANCE_2.2] Un ente delegante può accettare la richiesta di avvio del processo di archiviazione del descrittore meno recente di un e-service creato da template inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
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

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_TEMPLATE_INSTANCE_3.1] Un ente delegante può rifiutare la richiesta di avvio del processo di archiviazione di un e-service creato da template inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
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
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_TEMPLATE_INSTANCE_3.2] Un ente delegante può rifiutare la richiesta di avvio del processo di archiviazione del descrittore meno recente di un e-service creato da template inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
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

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_TEMPLATE_INSTANCE_4.1] L'ente delegato può annullare la richiesta di archiviazione dell'e-service creato da template precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
    And l'utente è un "<role>" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione dell'e-service "%actual"
    Then si ottiene response status code 204
    And la richiesta di archiviazione pendente dell'e-service è stata annullata con successo
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_TEMPLATE_INSTANCE_4.2] L'ente delegato può annullare la richiesta di archiviazione del descrittore meno recente di un e-service creato da template precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente effettua l'aggiunta di una versione in stato PUBLISHED all'e-service con successo
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
    And l'utente è un "<role>" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 204
    And la richiesta di archiviazione pendente del vecchio descrittore è stata annullata con successo
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_1.1] Un ente delegato può richiedere al delegante di avviare il processo di archiviazione di un e-service asincrono in delega
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "<eserviceState>" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204

    Examples:
      | eserviceState | role         |
      | PUBLISHED     | admin        |
      | PUBLISHED     | api          |
      | PUBLISHED     | api,security |
      | SUSPENDED     | admin        |
      | SUSPENDED     | api          |
      | SUSPENDED     | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_1.2] Un ente delegato può richiedere al delegante di avviare il processo di archiviazione del descrittore meno recente di un e-service asincrono
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
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

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_2.1] Un ente delegante può accettare la richiesta di archiviazione di un e-service asincrono inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
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
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_2.2] Un ente delegante può accettare la richiesta di archiviazione del descrittore meno recente di un e-service asincrono inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
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

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_3.1] Un ente delegante può rifiutare la richiesta di archiviazione di un e-service asincrono inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
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
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_3.2] Un ente delegante può rifiutare la richiesta di archiviazione del descrittore meno recente di un e-service asincrono inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
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

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_4.1] L'ente delegato può annullare la richiesta di archiviazione dell'e-service asincrono precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione dell'e-service "%actual"
    Then si ottiene response status code 204
    And la richiesta di archiviazione pendente dell'e-service è stata annullata con successo
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_4.2] L'ente delegato può annullare la richiesta di archiviazione del descrittore meno recente di un e-service asincrono precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "PUBLISHED" con:
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service asincrono
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "<role>" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 204
    And la richiesta di archiviazione pendente del vecchio descrittore è stata annullata con successo
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_TEMPLATE_INSTANCE_1.1] Un ente delegato può richiedere al delegante di avviare il processo di archiviazione di un e-service asincrono creato da template in delega
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <descriptorState> a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
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
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_TEMPLATE_INSTANCE_1.2] Un ente delegato può richiedere al delegante di avviare il processo di archiviazione del descrittore meno recente di un e-service asincrono creato da template
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente crea una nuova versione dell'istanza del template con successo
    And l'utente specifica i metadati mancanti all'istanza del template asincrono con successo
    And l'utente tenta la pubblicazione di una nuova versione dell'istanza del template
    And si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
    And l'utente è un "<role>" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 204

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_TEMPLATE_INSTANCE_2.1] Un ente delegante può accettare la richiesta di archiviazione di un e-service asincrono creato da template inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
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
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_TEMPLATE_INSTANCE_2.2] Un ente delegante può accettare la richiesta di archiviazione del descrittore meno recente di un e-service asincrono creato da template inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente crea una nuova versione dell'istanza del template con successo
    And l'utente specifica i metadati mancanti all'istanza del template asincrono con successo
    And l'utente tenta la pubblicazione di una nuova versione dell'istanza del template
    And si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
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

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_TEMPLATE_INSTANCE_3.1] Un ente delegante può rifiutare la richiesta di archiviazione di un e-service asincrono creato da template inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
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
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_TEMPLATE_INSTANCE_3.2] Un ente delegante può rifiutare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente crea una nuova versione dell'istanza del template con successo
    And l'utente specifica i metadati mancanti all'istanza del template asincrono con successo
    And l'utente tenta la pubblicazione di una nuova versione dell'istanza del template
    And si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
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

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_TEMPLATE_INSTANCE_4.1] L'ente delegato può annullare la richiesta di archiviazione dell'e-service asincrono creato da template precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
    And l'utente è un "<role>" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione dell'e-service "%actual"
    Then si ottiene response status code 204
    And la richiesta di archiviazione pendente dell'e-service è stata annullata con successo
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_ESERVICE_ASYNC_TEMPLATE_INSTANCE_4.2] L'ente delegato può annullare la richiesta di archiviazione del descrittore meno recente di un e-service creato da template precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente crea una nuova versione dell'istanza del template con successo
    And l'utente specifica i metadati mancanti all'istanza del template asincrono con successo
    And l'utente tenta la pubblicazione di una nuova versione dell'istanza del template
    And si ottiene response status code 200
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And l'ente "PA1" ha una delega in erogazione attiva verso l'ente "PA2" per l'istanza dell'e-service template
    And l'utente è un "<role>" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 204
    And la richiesta di archiviazione pendente del vecchio descrittore è stata annullata con successo
    And la vecchia versione dell'e-service è in stato "DEPRECATED"
    And la versione più recente dell'e-service è in stato "PUBLISHED"

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |

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

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_NEW_VERSION_PUBLICATION_1.1] In presenza di una richiesta di archiviazione in attesa, un ente delegato può richiedere la pubblicazione di una nuova versione dell'e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And "PA2" tenta la creazione di una versione in DRAFT per quell'e-service
    And l'utente aggiorna alcuni parametri di quel descrittore
    And "PA2" ha già caricato un'interfaccia per quel descrittore
    When l'utente pubblica l'e-service
    Then si ottiene response status code 200
    And l'e-service è in stato "WAITING_FOR_APPROVAL"

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_NEW_VERSION_PUBLICATION_2.1] L’approvazione della nuova versione in stato `WAITING_FOR_APPROVAL` e successivamente della richiesta di archiviazione archivia correttamente l’e-service
  Verifichiamo che, dopo l’accettazione della pubblicazione di una nuova versione di un e-service
  e la successiva richiesta di archiviazione da parte del delegante,
  il processo di archiviazione dell’intero e-service venga avviato correttamente.
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And "PA2" tenta la creazione di una versione in DRAFT per quell'e-service
    And l'utente aggiorna alcuni parametri di quel descrittore
    And "PA2" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And l'e-service è in stato "WAITING_FOR_APPROVAL"
    And "PA1" approva la pubblicazione dell'e-service
    When l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 200
    And l'e-service è in stato "ARCHIVING"

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_NEW_VERSION_CANCELLATION_1.1] L'accettazione di una richiesta di archiviazione dell'e-service elimina l'ultima versione dell'e-service in stato `WAITING_FOR_APPROVAL`
  Verifichiamo che, in presenza di una richiesta di pubblicazione di una nuova versione dell'e-service in corso,
  l’avvio del processo di archiviazione dell’e-service (quindi l'accettazione del delegante di una richiesta
  di avvio del processo di archiviazione) comporti l’eliminazione della nuova versione ancora in attesa di pubblicazione.
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And "PA2" tenta la creazione di una versione in DRAFT per quell'e-service
    And l'utente aggiorna alcuni parametri di quel descrittore
    And "PA2" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And l'e-service è in stato "WAITING_FOR_APPROVAL"
    When l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 200
    And l'ultimo descrittore in stato WAITING_FOR_APPROVAL è stato cancellato
    And l'e-service è in stato "ARCHIVING"

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_NEW_VERSION_CANCELLATION_1.2] L'invio di una richiesta di archiviazione dell'e-service NON elimina l'ultima versione dell'e-service in stato `WAITING_FOR_APPROVAL`
  Verifichiamo che, in presenza di una richiesta di pubblicazione in corso,
  l’avvio del processo di archiviazione dell’e-service comporti l’eliminazione della nuova versione ancora in attesa di pubblicazione.
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And "PA2" tenta la creazione di una versione in DRAFT per quell'e-service
    And l'utente aggiorna alcuni parametri di quel descrittore
    And "PA2" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And l'e-service è in stato "WAITING_FOR_APPROVAL"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 200
    And l'e-service è in stato "WAITING_FOR_APPROVAL"
    And la richiesta di archiviazione delegata dell'e-service è in stato pending

  @happy-path
  Scenario: [DELEGATION_AUTOMATIC_ARCHIVING_DESCRIPTOR_1.1] L'archiviazione automatica di un descrittore elimina la relativa richiesta di archiviazione in pending
  Il descrittore meno recente di un e-service in delega ha una richiesta di archiviazione ancora in pending.
  Quando viene archiviata l'ultima richiesta di fruizione attiva verso il descrittore, questo passa
  automaticamente allo stato ARCHIVED e la richiesta in pending viene eliminata.
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
    When l'utente è un "admin" di "PA3"
    And l'utente richiede una operazione di archiviazione della richiesta di fruizione
    Then si ottiene response status code 204
    And la richiesta di archiviazione pendente del vecchio descrittore è stata eliminata
    And la vecchia versione dell'e-service è in stato "ARCHIVED"

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_REQUEST_REVOCATION_1.1] La revoca della delega elimina la richiesta di archiviazione in pending dell'e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    When l'ente "PA1" con ruolo "admin" revoca la delega in erogazione con successo
    Then si ottiene response status code 204
    And la richiesta pendente di archiviazione dell'e-service è stata eliminata

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_REQUEST_REVOCATION_1.2] La revoca della delega elimina la richiesta di archiviazione in pending del descrittore che non sia il più recente dell'e-service
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
    When l'ente "PA1" con ruolo "admin" revoca la delega in erogazione con successo
    Then si ottiene response status code 204
    And la richiesta pendente di archiviazione del vecchio descrittore è stata eliminata

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_REQUEST_REVOCATION_1.3] Un ente con delega revocata NON può richiedere l'archiviazione dell'e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'ente "PA1" con ruolo "admin" revoca la delega in erogazione con successo
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 409

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_REQUEST_REVOCATION_1.4] Un ente con delega revocata NON può richiedere l'archiviazione del descrittore che non sia il più recente dell'e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'ente "PA1" con ruolo "admin" revoca la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 409

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_REQUEST_REVOCATION_1.5] Dopo la revoca della delega, il delegante può avviare l'archiviazione dell'e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'ente "PA1" con ruolo "admin" revoca la delega in erogazione con successo
    And l'utente è un "admin" di "PA1"
    When l'utente avvia il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 204
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_REQUEST_REVOCATION_1.6] Dopo la revoca della delega, il delegante può avviare l'archiviazione del descrittore che non sia il più recente dell'e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'ente "PA1" con ruolo "admin" revoca la delega in erogazione con successo
    And l'utente è un "admin" di "PA1"
    When l'utente avvia la messa in archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore
    And la versione più recente dell'e-service è in stato "PUBLISHED"

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_STATE_1.1] Un e-service in stato ARCHIVING o ARCHIVING_SUSPENDED può essere dato in delega in erogazione
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "<eserviceState>"
    And l'utente è un "admin" di "PA1"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual archiving" e 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    When l'utente richiede la creazione di una delega in erogazione per l'ente "PA2"
    Then si ottiene response status code 200

    Examples:
      | eserviceState |
      | PUBLISHED     |
      | SUSPENDED     |

  @happy-path
  Scenario Outline: [DELEGATION_ARCHIVING_STATE_2.1] Un e-service in stato ARCHIVING o ARCHIVING_SUSPENDED può essere dato in delega in fruizione
    Given "PA1" ha già creato un e-service con un descrittore in stato "<eserviceState>" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'utente è un "admin" di "PA1"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual archiving" e 60 giorni di preavviso
    And l'ente delegante "PA3"
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    When l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    Then si ottiene response status code 200
    And l'utente è un "admin" di "PA1"

    Examples:
      | eserviceState |
      | PUBLISHED     |
      | SUSPENDED     |

  @sad-path
  Scenario Outline: [DELEGATION_ARCHIVING_CLONING_1.1] Ente delegato e delegante NON possono duplicare un e-service in delega in erogazione in stato PUBLISHED
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "<tenant>"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 403

    Examples:
      | tenant |
      | PA1    |
      | PA1    |

  @sad-path
  Scenario Outline: [DELEGATION_ARCHIVING_CLONING_1.2] Ente delegato e delegante NON possono duplicare un e-service in delega in erogazione in stato ARCHIVING o ARCHIVING_SUSPENDED
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<eserviceState>"
    And l'utente è un "admin" di "PA1"
    And l'utente ha già avviato il processo di archiviazione dell'e-service "%actual" specificando la motivazione "QA test manual-archiving" e 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "<tenant>"
    When l'utente tenta di clonare quell'e-service
    Then si ottiene response status code 403

    Examples:
      | tenant | eserviceState |
      | PA1    | PUBLISHED     |
      | PA1    | SUSPENDED     |
      | PA2    | PUBLISHED     |
      | PA2    | SUSPENDED     |

  @sad-path
  Scenario Outline: [DELEGATION_ARCHIVING_CLONING_1.3] Ente delegato e delegante NON possono duplicare descrittore di un e-service in delega in erogazione in stato DEPRECATED
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "<tenant>"
    When l'utente tenta di clonare la vecchia versione dell'e-service
    Then si ottiene response status code 403

    Examples:
      | tenant |
      | PA1    |
      | PA2    |

  @sad-path
  Scenario Outline: [DELEGATION_ARCHIVING_CLONING_1.4] Ente delegato e delegante NON possono duplicare descrittore di un e-service in delega in erogazione in stato ARCHIVED
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And la vecchia versione dell'e-service è in stato "ARCHIVED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "<tenant>"
    When l'utente tenta di clonare la vecchia versione dell'e-service
    Then si ottiene response status code 403

    Examples:
      | tenant |
      | PA1    |
      | PA2    |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_1.1] Specificando parametri errati o mancanti, un ente delegato NON può richiedere al delegante di avviare il processo di archiviazione di un e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "<eServiceId>" specificando la motivazione "<archivingReason>" e 60 giorni di preavviso
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | archivingReason                     | statusCode |
      | %null      | QA test delegation manual archiving | 400        |
      | %random    | QA test delegation manual archiving | 404        |
      | %actual    | %null                               | 400        |
      | %actual    | %blank                              | 400        |

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_1.2] Inserendo una motivazione di archivazione con lunghezza errata, un ente delegato NON può richiedere al delegante di avviare il processo di archiviazione di un e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando una motivazione di <archivingReasonLength> caratteri e 60 giorni di preavviso
    Then si ottiene response status code 400

    Examples:
      | archivingReasonLength |
      | 9                     |
      | 251                   |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_1.3] Un ente delegato NON può richiedere al delegante di avviare il processo di archiviazione di un e-service in delega se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And viene impostato per l'utente un token non valido
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_1.4] Specificando parametri errati o mancanti, un ente delegato NON può richiedere al delegante di avviare il processo di archiviazione del descrittore meno recente dell'e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "<descriptorId>" per l'e-service "<eServiceId>" impostando 60 giorni di preavviso
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | descriptorId | statusCode |
      | %null      | %actual      | 400        |
      | %random    | %actual      | 404        |
      | %actual    | %null        | 400        |
      | %actual    | %random      | 404        |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_1.5] Un ente delegato NON può richiedere al delegante di avviare il processo di archiviazione del descrittore meno recente dell'e-service in delega se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And viene impostato per l'utente un token non valido
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_2.1] Specificando parametri errati o mancanti, un ente delegante NON può accettare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    When l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "<eServiceId>"
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | statusCode |
      | %null      | 400        |
      | %random    | 404        |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_2.2] Un ente delegante NON può accettare la richiesta di archiviazione di un e-service inviata dall'ente delegato se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    And viene impostato per l'utente un token non valido
    When l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_2.3] Specificando parametri errati o mancanti, un ente delegante NON può accettare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    When l'utente delegante accetta la richiesta di archiviazione della vecchia versione identificata da "<descriptorId>" per l'e-service "<eServiceId>"
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | descriptorId | statusCode |
      | %null      | %actual      | 400        |
      | %random    | %actual      | 404        |
      | %actual    | %null        | 400        |
      | %actual    | %random      | 404        |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_2.4] Un ente delegante NON può accettare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    And viene impostato per l'utente un token non valido
    When l'utente delegante accetta la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_3.1] Specificando parametri errati o mancanti, un ente delegante NON può rifiutare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    When l'utente delegante rifiuta la richiesta di archiviazione delegata dell'e-service "<eServiceId>" con motivazione "<rejectionReason>"
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | rejectionReason                        | statusCode |
      | %null      | QA test rejection delegation archiving | 400        |
      | %random    | QA test rejection delegation archiving | 404        |
      | %actual    | %null                                  | 400        |
      | %actual    | %blank                                 | 400        |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_3.2] Un ente delegante NON può rifiutare la richiesta di archiviazione di un e-service inviata dall'ente delegato se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    And viene impostato per l'utente un token non valido
    When l'utente delegante rifiuta la richiesta di archiviazione delegata dell'e-service "%actual" con motivazione "QA test rejection delegation archiving"
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_3.3] Specificando parametri errati o mancanti, un ente delegante NON può rifiutare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    When l'utente delegante rifiuta la richiesta di archiviazione della vecchia versione identificata da "<descriptorId>" per l'e-service "<eServiceId>" con motivazione "<rejectionReason>"
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | descriptorId | rejectionReason                        | statusCode |
      | %null      | %actual      | QA test rejection delegation archiving | 400        |
      | %random    | %actual      | QA test rejection delegation archiving | 404        |
      | %actual    | %null        | QA test rejection delegation archiving | 400        |
      | %actual    | %random      | QA test rejection delegation archiving | 404        |
      | %actual    | %actual      | %null                                  | 400        |
      | %actual    | %actual      | %blank                                 | 400        |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_3.4] Un ente delegante NON può rifiutare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    And viene impostato per l'utente un token non valido
    When l'utente delegante rifiuta la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" con motivazione "QA test rejection delegation archiving"
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_4.1] Specificando parametri errati o mancanti, l'ente delegato NON può annullare la richiesta di archiviazione dell'e-service precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione dell'e-service "<eServiceId>"
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | statusCode |
      | %null      | 400        |
      | %random    | 404        |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_4.2] L'ente delegato NON può annullare la richiesta di archiviazione dell'e-service precedentemente inviata e ancora in pending se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And viene impostato per l'utente un token non valido
    When l'utente delegato annulla la richiesta di archiviazione dell'e-service "%actual"
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_4.3] Specificando parametri errati o mancanti, l'ente delegato NON può annullare la richiesta di archiviazione del descrittore meno recente precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    When l'utente delegato annulla la richiesta di archiviazione della vecchia versione identificata da "<descriptorId>" per l'e-service "<eServiceId>"
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | descriptorId | statusCode |
      | %null      | %actual      | 400        |
      | %random    | %actual      | 404        |
      | %actual    | %null        | 400        |
      | %actual    | %random      | 404        |

  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_CONTRACT_4.4] L'ente delegato NON può annullare la richiesta di archiviazione del descrittore meno recente precedentemente inviata e ancora in pending se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    And viene impostato per l'utente un token non valido
    When l'utente delegato annulla la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 401

  @sad-path
  Scenario: [DELEGATION_ARCHIVING_SINGLE_PENDING_REQUEST_1.1] In presenza di una richiesta pending per l'intero e-service, il delegato NON può richiedere l'archiviazione anche l'archiviazione di un singolo descrittore diverso dal più recente
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 409

  @sad-path
  Scenario: [DELEGATION_ARCHIVING_SINGLE_PENDING_REQUEST_1.2] In presenza di una richiesta pending per un singolo descrittore diverso dal più recente, il delegato NON può richiedere l'archiviazione dell'intero e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 409

  @sad-path
  Scenario: [DELEGATION_ARCHIVING_SINGLE_PENDING_REQUEST_1.3] In presenza di una richiesta pending per un singolo descrittore diverso dal più recente, il delegato NON può inviarne una nuova per lo stesso descrittore
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per il vecchio descrittore "%actual" dell'e-service "%actual" specificando 60 giorni di preavviso
    When l'utente delegato invia al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 409

  @happy-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_COMBINED_PROCESS_1.1] Il delegante può accettare la richiesta di archiviazione dell'intero e-service quando è già in corso l'archiviazione di uno dei suoi descrittori
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 90 giorni di preavviso
    And l'utente è un "admin" di "PA1"
    When l'utente delegante accetta la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And la versione più recente dell'e-service è in stato "ARCHIVING"
    And il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service


  @sad-path
  Scenario: [DELEGATION_MANUAL_ARCHIVING_COMBINED_PROCESS_1.2] Il delegato NON può richiedere l'archiviazione dell'intero e-service con un tempo di preavviso antecedente a quello di un suo descrittore con archiviazione già in corso
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'utente è un "admin" di "PA1"
    And l'utente ha già messo in archiviazione la vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    When l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 30 giorni di preavviso
    Then si ottiene response status code 400
    And la richiesta di archiviazione delegata dell'e-service non è stata creata
    And la vecchia versione dell'e-service è in stato "ARCHIVING"
    And la versione più recente dell'e-service è in stato "PUBLISHED"
    And il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service
