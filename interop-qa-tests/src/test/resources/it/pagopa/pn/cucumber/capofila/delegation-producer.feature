@capofila
Feature: Creazione di una delega in erogazione

  Background:
    Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe

  #TC-4: Un utente con ruolo admin può creare una delega
  #TC-5: Un utente con ruolo diverso da admin NON può creare una delega
  #TC-31: Una delega può essere creata dal delegante se delegato da la disponibilità a ricevere la delega
  @deleghe2
  Scenario Outline: [TC_CAPOFILA_4_5] Il richiamo dell’API di creazione di una delega possa essere compiuto da un utente di livello operatore amministrativo (admin)
    Given l'utente è un "<ruolo>" di "<delegante>"
    And "<delegante>" ha già creato e pubblicato 1 e-service
    And l'ente "<delegato>" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega per l'ente "<delegato>"
    Then si ottiene lo status code <statusCode>

    @happy-path
    Examples:
      | ruolo        | delegante | delegato | statusCode |
      | admin        | PA1       | PA2      |        200 |

    @sad-path
    Examples:
      | ruolo        | delegante | delegato | statusCode |
      | api          | PA1       | PA2      |        403 |
      | security     | PA1       | PA2      |        403 |
      | api,security | PA1       | PA2      |        403 |
      | support      | PA1       | PA2      |        403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ruolo        | delegante | delegato | statusCode |
      | reviewer     | PA2       | PA3      |        403 |
      | viewer       | PA2       | PA3      |        403 |

  @deleghe2
  Scenario Outline: [TC_CAPOFILA_RIFIUTO_PENDING] Il rifiuto di una delega in stato di pending possa essere compiuto solo da un utente con ruolo admin
    Given l'ente delegante "<delegante>"
    And l'ente delegato "<delegato>"
    And un utente dell'ente <funzione> con ruolo "<ruolo>"
    And "<delegante>" ha già creato e pubblicato 1 e-service
    And l'ente "<delegato>" concede la disponibilità a ricevere deleghe
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    When l'utente rifiuta la delega
    Then si ottiene lo status code <statusCode>

    @happy-path
    Examples:
      | ruolo        | funzione  | delegante | delegato | statusCode  |
      # Scenario: 26
      # Esito: coerente
      | admin        | delegato  | PA1       | PA2      | 200         |

    @sad-path
    Examples:
      | ruolo        | funzione  | delegante | delegato | statusCode  |
      # Scenario: 6
      # Esito: coerente
      | api          | delegato  | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | reviewer     | delegato  | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | viewer       | delegato  | PA1       | PA2      | 403         |
      # Scenario: 6
      # Esito: coerente
      | security     | delegato  | PA1       | PA2      | 403         |
      # Scenario: 6
      # Esito: coerente
      | api,security | delegato  | PA1       | PA2      | 403         |
      # Scenario: 6
      # Esito: coerente
      | support      | delegato  | PA1       | PA2      | 403         |
      # Scenario: 28
      # Esito: incoerente, 403, "Unauthorized"
      | admin        | delegante | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api          | delegante | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | reviewer     | delegante | PA2       | PA3      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | viewer       | delegante | PA2       | PA3      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | security     | delegante | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api,security | delegante | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | support      | delegante | PA1       | PA2      | 403         |

  @sad-path @deleghe2
  Scenario Outline: [TC_CAPOFILA_RIFIUTO_DELEGA_ACCETTATA] Il rifiuto di una delega già accettata non possa essere compiuto da nessun utente indipendentemente dal ruolo
    Given l'ente delegante "<delegante>"
    And l'ente delegato "<delegato>"
    And un utente dell'ente <funzione> con ruolo "<ruolo>"
    And "<delegante>" ha già creato e pubblicato 1 e-service
    And l'ente "<delegato>" concede la disponibilità a ricevere deleghe
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    And l'ente "<delegato>" accetta la delega
    When l'utente rifiuta la delega
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo        | funzione  | delegante | delegato | statusCode  |
      # Scenario: 29
      # Esito: coerente
      | admin        | delegato  | PA1       | PA2      | 409         |
      # Scenario: <mancante>
      # Esito: si ottiene 403
      | api          | delegato  | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403
      | reviewer     | delegato  | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403
      | viewer       | delegato  | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403
      | security     | delegato  | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403
      | api,security | delegato  | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403
      | support      | delegato  | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Operation restricted to delegate"
      | admin        | delegante | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api          | delegante | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | reviewer     | delegante | PA2       | PA3      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | viewer       | delegante | PA2       | PA3      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | security     | delegante | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api,security | delegante | PA1       | PA2      | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | support      | delegante | PA1       | PA2      | 403         |

  @sad-path @deleghe2
  Scenario: [TC_CAPOFILA_33] La creazione di una delega in erogazione NON può essere compiuto da un utente ADMIN se l’aderente non si è reso disponibile ad accettare deleghe
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 1 e-service
    When l'utente richiede la creazione di una delega per l'ente "PA2"
    Then si ottiene lo status code 403

  #TC-7: L'accettazione di una delega NON può essere fatta da un utente con ruolo diverso da ADMIN
  #TC-8: La revoca di una delega NON può essere fatta da un utente con ruolo diverso da ADMIN
  #TC-13: L'accettazione di una delega può essere fatta da un utente con ruolo ADMIN
  #TC-14: La revoca di una delega può essere fatta da un utente con ruolo ADMIN
  @deleghe2
  Scenario Outline: [TC_CAPOFILA_ACCETTA_REVOCA_DELEGA] L'accettazione e la revoca di una delega non può essere effettuata da un utente diverso da admin
    Given l'utente è un "<ruolo>" di "<delegato>"
    And "<delegante>" ha già creato e pubblicato 1 e-service
    And l'ente "<delegato>" concede la disponibilità a ricevere deleghe
    When l'ente "<delegante>" richiede la creazione di una delega per l'ente "<delegato>"
    And l'utente accetta la delega
    Then si ottiene lo status code <statusCode>
    When l'ente "<delegante>" con ruolo "<ruolo>" revoca la delega
    Then si ottiene lo status code <statusCode>

    @happy-path
    Examples:
      | ruolo        | delegante | delegato | statusCode |
      | admin        | PA1       | PA2      |        200 |

    @sad-path
    Examples:
      | ruolo        | delegante | delegato | statusCode |
      | api          | PA1       | PA2      |        403 |
      | security     | PA1       | PA2      |        403 |
      | api,security | PA1       | PA2      |        403 |
      | support      | PA1       | PA2      |        403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ruolo        | delegante | delegato | statusCode |
      | reviewer     | PA2       | PA3      |        403 |
      | viewer       | PA2       | PA3      |        403 |

  #TC-21: Delegato con ruolo admin non può revocare la delega
  @sad-path @deleghe2
  Scenario Outline: [TC_CAPOFILA_DELEGATO_REVOCA] La revoca di una delega in stato PENDING non può essere effettuata da un delegato con ruolo admin
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    And un utente dell'ente delegato con ruolo "<ruolo>"
    When l'ente "PA2" con ruolo "<ruolo>" revoca la delega
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | admin    |
      | api      |

    @nuovi-operatori-update
    Examples:
      | ruolo    |
      | reviewer |
      | viewer   |

  #TC-12: L'API di disponibilità NON puà essere invocata da un utente admin di un tenant NON PA
  @sad-path
  Scenario: [TC_CAPOFILA_PRIVATO] La revoca di una delega in stato PENDING non può essere effettuata da un delegato con ruolo admin
    Given l'ente delegante "PA1"
    And l'ente delegato "Privato"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "Privato" concede la disponibilità a ricevere deleghe
    Then si ottiene lo status code 403

  #TC-11: La disponibilità di una delega può essere fatta soltanto da un utente con ruolo ADMIN
  @deleghe2
  Scenario Outline: [TC_CAPOFILA_DISPONIBILITA_DELEGHE] L'accettazione e la revoca di una delega non può essere effettuata da un utente diverso da admin
    Given l'utente è un "<ruolo>" di "PA2"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente concede la disponibilità a ricevere le deleghe
    Then si ottiene lo status code <statusCode>

    @happy-path
    Examples:
      | ruolo        | statusCode |
      | admin        |        200 |

    @sad-path
    Examples:
      | ruolo        | statusCode |
      | api          |        403 |
      | security     |        403 |
      | api,security |        403 |
      | support      |        403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ruolo        | statusCode |
      | reviewer     |        403 |
      | viewer       |        403 |

  @sad-path @deleghe2
  Scenario: [TC_CAPOFILA_35] Un delegante può delegare un solo ente per volta per un e-service
    Given l'utente è un "admin" di "PA1"
    Given l'ente "PA3" rimuove la disponibilità a ricevere deleghe
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega per l'ente "PA2"
    And l'ente "PA2" accetta la delega
    And l'ente "PA3" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega per l'ente "PA3"
    Then si ottiene lo status code 409

  @happy-path
  Scenario: [TC_CAPOFILA_LISTA_DELEGHE_1] Viene recuperata la lista delle deleghe di un ente
    Given l'utente è un "admin" di "PA2"
    Given l'utente recupera le prime 5 pagine con la lista delle deleghe
    Then viene verificato che sono state ritornate le prime 5 pagine

  @happy-path
  Scenario: [TC_CAPOFILA_LISTA_DELEGHE_2] Viene recuperata la lista delle deleghe di un ente
    Given l'utente è un "admin" di "PA2"
    Given l'utente recupera la lista delle deleghe in stato ACTIVE e WAITING_FOR_APPROVAL
    Then viene verificato che le deleghe ritornate sono soltanto quelle in stato ACTIVE e WAITING_FOR_APPROVAL

  # NOTA 30/07/2025: aggiunto a posteriori, momentaneamente assente in SRS
  @deleghe2
  Scenario: [TC_CAPOFILA_PUB_1] La pubblicazione di un e-service da parte di un ente delegato all'erogazione conduce l'e-service allo stato WAITING_FOR_APPROVAL
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega
    And l'utente è un "admin" di "PA2"
    When l'utente pubblica l'e-service
    Then si ottiene lo status code 200
    And l'e-service è in stato "WAITING_FOR_APPROVAL"

  # NOTA 08/08/2025: aggiunto a posteriori, momentaneamente assente in SRS
  @deleghe2
  Scenario: [TC_CAPOFILA_APPROVE_1] Un utente ADMIN dell'ente delegante è in grado di effettuare l'approvazione dell'e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    When l'utente è un "admin" di "PA1"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 200
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "PUBLISHED"

  # NOTA 08/08/2025: aggiunto a posteriori, momentaneamente assente in SRS
  # DEV. NOTE 08/08/2025: evitato l'uso di Scenario Outline per eseguire una sola volta la creazione dell'e-service e il processo di delega
  # Ticket aperto: https://pagopa.atlassian.net/browse/PIN-7927
  @deleghe2
  @nuovi-operatori-update
  Scenario: [TC_CAPOFILA_APPROVE_2] Un utente dell'ente delegante di livello inappropriato NON è in grado di effettuare l'approvazione dell'e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato

    When l'utente è un "api" di "PA1"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

    When l'utente è un "security" di "PA1"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

    When l'utente è un "api,security" di "PA1"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

    When l'utente è un "support" di "PA1"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

    When l'utente è un "reviewer" di "PA2"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

    When l'utente è un "viewer" di "PA2"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

  # NOTA 08/08/2025: aggiunto a posteriori, momentaneamente assente in SRS
  # DEV. NOTE 08/08/2025: evitato l'uso di Scenario Outline per eseguire una sola volta la creazione dell'e-service e il processo di delega
  @deleghe2
  @nuovi-operatori-update
  Scenario: [TC_CAPOFILA_APPROVE_3] Un ente diverso dal delegante NON è in grado di effettuare l'approvazione dell'e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato

    When l'utente è un "api" di "PA2"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

    When l'utente è un "reviewer" di "PA2"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

    When l'utente è un "viewer" di "PA2"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

    When l'utente è un "api" di "GSP"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

    When l'utente è un "reviewer" di "GSP"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

    When l'utente è un "viewer" di "GSP"
    And l'utente approva la pubblicazione dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1"
    Then l'e-service è in stato "WAITING_FOR_APPROVAL"

  # NOTA 08/08/2025: aggiunto a posteriori, momentaneamente assente in SRS
  @deleghe2
  Scenario: [TC_CAPOFILA_APPROVE_3_B] Il delegante NON può sospendere l'e-service se una delega è in corso
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA1"
    And l'utente approva la pubblicazione dell'e-service
    When l'utente sospende quel descrittore
    Then si ottiene lo status code 403
    And l'e-service è in stato "PUBLISHED"

  @deleghe1
  @hotfix_QA-13870
  Scenario Outline: [TC_CAPOFILA_DELEGA_NON_PA_1] Verificare che la disponibilità alla delega in erogazione di un e-service NON possa essere data da un ente che non sia una pubblica amministrazione
    Given l'utente è un "admin" di "<ente>"
    When l'ente "<ente>" tenta di concedere la disponibilità a ricevere deleghe in erogazione
    Then si ottiene status code 403

    @happy-path
    Examples:
      | ente      |
      | GSP       |
      | Privato   |

  @hotfix_QA-13870
  Scenario Outline: [TC_CAPOFILA_DELEGA_NON_PA_2] Un ente che non sia una Pubblica Amministrazione non può fungere da delegante in erogazione per un proprio e-service
    Given l'utente è un "admin" di "<delegante>"
    And "<delegante>" ha già creato e pubblicato 1 e-service
    And l'ente "PA1" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega in erogazione per l'ente "PA1"
    Then si ottiene lo status code 403

    Examples:
      | delegante |
      | Privato   |
      | GSP       |
