@m2m-v3-delegation-manual-archiving-eservice
Feature: (M2M v3) Gestione deleghe per archiviazione manuale e-service

  @happy-path
  Scenario Outline: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_1.1] Un ente delegato può richiedere via M2M v3 al delegante di avviare il processo di archiviazione di un e-service in delega
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 200

    Examples:
      | descriptorState |
      | PUBLISHED       |
      | SUSPENDED       |

  @happy-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_1.2] Un ente delegato può richiedere via M2M v3 al delegante di avviare il processo di archiviazione del descrittore meno recente di un e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 200

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_1.3] Un utente con ruolo m2m NON può richiedere via M2M v3 al delegante di avviare il processo di archiviazione di un e-service in delega
    Given l'ente delegato "PA2"
    And l'ente delegante "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 403

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_1.4] Un utente con ruolo m2m NON può richiedere via M2M v3 al delegante di avviare il processo di archiviazione del descrittore meno recente di un e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 403

  @happy-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_2.1] Un ente delegante può accettare via M2M v3 la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente delegante accetta via M2M v3 la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 200

  @happy-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_2.2] Un ente delegante può accettare via M2M v3 la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
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
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente delegante accetta via M2M v3 la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 200

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_2.3] Un utente con ruolo m2m NON può accettare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente accetta via M2M v3 la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 403

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_2.4] Un utente con ruolo m2m NON può accettare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
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
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente accetta via M2M v3 la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 403

  @happy-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_3.1] Un ente delegante può rifiutare via M2M v3 la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente delegante rifiuta via M2M v3 la richiesta di archiviazione delegata dell'e-service "%actual" con motivazione "QA test rejection delegation manual archiving"
    Then si ottiene response status code 200

  @happy-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_3.2] Un ente delegante può rifiutare via M2M v3 la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
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
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente delegante rifiuta via M2M v3 la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" con motivazione "QA test rejection delegation manual archiving"
    Then si ottiene response status code 200

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_3.3] Un utente con ruolo m2m NON può rifiutare la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente delegato invia al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente delegante rifiuta via M2M v3 la richiesta di archiviazione delegata dell'e-service "%actual" con motivazione "QA test rejection delegation manual archiving"
    Then si ottiene response status code 403

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_3.4] Un utente con ruolo m2m NON può rifiutare la richiesta di archiviazione del descrittore meno recente inviata dall'ente delegato
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
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente delegante rifiuta via M2M v3 la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" con motivazione "QA test rejection delegation manual archiving"
    Then si ottiene response status code 403

  @happy-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_4.1] L'ente delegato può annullare via M2M v3 la richiesta di archiviazione dell'e-service precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente delegato annulla via M2M v3 la richiesta di archiviazione dell'e-service "%actual"
    Then si ottiene response status code 200

  @happy-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_4.2] L'ente delegato può annullare via M2M v3 la richiesta di archiviazione del descrittore meno recente precedentemente inviata e ancora in pending
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
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente delegato annulla via M2M v3 la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 200

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_4.3] Un utente con ruolo m2m NON può annullare via M2M v3 la richiesta di archiviazione dell'e-service precedentemente inviata e ancora in pending
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente delegato annulla via M2M v3 la richiesta di archiviazione dell'e-service "%actual"
    Then si ottiene response status code 403

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_4.4] Un utente con ruolo m2m NON può annullare via M2M v3 la richiesta di archiviazione del descrittore meno recente precedentemente inviata e ancora in pending
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
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente delegato annulla via M2M v3 la richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual"
    Then si ottiene response status code 403

  @sad-path
  Scenario Outline: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_CONTRACT_1.1] Specificando parametri errati o mancanti, un ente delegato NON può richiedere via M2M v3 al delegante di avviare il processo di archiviazione di un e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione dell'e-service "<eServiceId>" specificando la motivazione "<archivingReason>" e 60 giorni di preavviso
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | archivingReason                     | statusCode |
      | %null      | QA test delegation manual archiving | 400        |
      | %random    | QA test delegation manual archiving | 404        |
      | %actual    | %null                               | 400        |
      | %actual    | %blank                              | 400        |

  @sad-path
  Scenario Outline: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_CONTRACT_1.2] Inserendo una motivazione di archivazione con lunghezza errata, un ente delegato NON può richiedere via M2M v3 al delegante di avviare il processo di archiviazione di un e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione dell'e-service "%actual" specificando una motivazione di <archivingReasonLength> caratteri e 60 giorni di preavviso
    Then si ottiene response status code 400

    Examples:
      | archivingReasonLength |
      | 9                     |
      | 251                   |

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_CONTRACT_1.3] Un ente delegato NON può richiedere via M2M v3 al delegante di avviare il processo di archiviazione di un e-service in delega se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione dell'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_CONTRACT_1.4] Specificando parametri errati o mancanti, un ente delegato NON può richiedere via M2M v3 al delegante di avviare il processo di archiviazione del descrittore meno recente dell'e-service in delega
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione della vecchia versione identificata da "<descriptorId>" per l'e-service "<eServiceId>" impostando 60 giorni di preavviso
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | descriptorId | statusCode |
      | %null      | %actual      | 400        |
      | %random    | %actual      | 404        |
      | %actual    | %null        | 400        |
      | %actual    | %random      | 404        |

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_CONTRACT_1.5] Un ente delegato NON può richiedere via M2M v3 al delegante di avviare il processo di archiviazione del descrittore meno recente dell'e-service in delega se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente delegato invia via M2M v3 al delegante una richiesta di archiviazione della vecchia versione identificata da "%actual" per l'e-service "%actual" impostando 60 giorni di preavviso
    Then si ottiene response status code 401

  @sad-path
  Scenario Outline: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_CONTRACT_2.1] Specificando parametri errati o mancanti, un ente delegante NON può accettare via M2M v3 la richiesta di archiviazione di un e-service inviata dall'ente delegato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente delegante accetta via M2M v3 la richiesta di archiviazione relativa all'e-service "<eServiceId>"
    Then si ottiene response status code <statusCode>

    Examples:
      | eServiceId | statusCode |
      | %null      | 400        |
      | %random    | 404        |

  @sad-path
  Scenario: [M2M_V3_DELEGATION_MANUAL_ARCHIVING_CONTRACT_2.2] Un ente delegante NON può accettare via M2M v3 la richiesta di archiviazione di un e-service inviata dall'ente delegato se il token di accesso utilizzato non è valido
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente ha già inviato la richiesta di archiviazione per l'e-service "%actual" specificando la motivazione "QA test delegation manual archiving" e 60 giorni di preavviso
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente delegante accetta via M2M v3 la richiesta di archiviazione relativa all'e-service "%actual"
    Then si ottiene response status code 401
