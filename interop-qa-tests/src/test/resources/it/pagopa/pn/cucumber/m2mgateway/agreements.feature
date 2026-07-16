@m2m-agreements
Feature: Gestione degli agreements attraverso APIs M2M V2

  @happy-path
  Scenario Outline: [M2M_AGREEMENTS_LIST_1] La lista degli agreements può essere visionata da un utente con ruolo M2M o M2M-ADMIN
    Given "PA1" ha già creato e pubblicato 5 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    And "<ente>" ha un agreement m2m attivo per ciascun e-service di "PA1"
    When l'utente tenta di recuperare una lista di 5 agreements creati
    Then si ottiene lo status code 200
    And sono stati visualizzati correttamente 5 agreements creati
    Examples:
      | ruolo-m2m |
      | m2m-admin |
      | m2m       |

  @sad-path
  Scenario: [M2M_AGREEMENTS_LIST_2] La lista degli agreements NON può essere visionata da un utente che ha presentato un token m2m scaduto
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha un agreement m2m attivo per ciascun e-service di "PA1"
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare una lista di 1 agreements creati
    Then si ottiene lo status code 401

  @happy-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_APPROVE_1] Una richiesta di fruizione in stato PENDING può essere approvata da un utente con ruolo M2M-ADMIN dell'ente erogatore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "ACTIVE"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario Outline: [M2M_AGREEMENTS_APPROVE_2] L'approvazione di una richiesta di fruizione con id non valido restituisce errore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id "<agreementId>"
    Then si ottiene status code <statusCode>
    And la richiesta di fruizione si trova in stato "PENDING"

    Examples:
      | agreementId | statusCode |
      | %null       | 400        |
      | %random     | 404        |

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario Outline: [M2M_AGREEMENTS_APPROVE_3] L'approvazione di una richiesta di fruizione in stato differente da PENDING restituisce errore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato <agreementStatus> per quell'e-service
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 409
    And la richiesta di fruizione si trova in stato <agreementStatus>

    Examples:
      | agreementStatus |
      | "ACTIVE"        |
      | "ARCHIVED"      |
      | "SUSPENDED"     |

  @happy-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_APPROVE_4] Una richiesta di fruizione in stato PENDING può essere approvata da un utente M2M-ADMIN dell'ente delegato in erogazione
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And "PA3" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And l'utente è un m2m-admin dell'ente delegato
    When l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id "%actual" e delegationId "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "ACTIVE"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_APPROVE_5] L'approvazione di una richiesta di fruizione con ruolo M2M restituisce errore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 403
    And la richiesta di fruizione si trova in stato "PENDING"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_APPROVE_6] L'approvazione di una richiesta di fruizione con stato REJECTED restituisce errore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "GSP" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    And "PA1" ha già rifiutato quella richiesta di fruizione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 409
    And la richiesta di fruizione si trova in stato "REJECTED"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario Outline: [M2M_AGREEMENTS_APPROVE_7] L'approvazione di una richiesta di fruizione con stato MISSING_CERTIFIED_ATTRIBUTES restituisce errore
    Given l'utente è un "admin" di "<enteErogatore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 409
    And la richiesta di fruizione si trova in stato "MISSING_CERTIFIED_ATTRIBUTES"

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @happy-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_APPROVE_8] Una richiesta di fruizione in stato PENDING non può essere approvata da un ente diverso dall'erogatore o dal delegato all'erogazione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And l'utente è un "admin" di "PA3" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 403
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And la richiesta di fruizione si trova in stato "PENDING"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_APPROVE_9] Un ente delegato con delega in erogazione non attiva non può approvare una richiesta di fruizione in stato PENDING per conto dell'erogatore
    Given "PA2" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    And l'ente delegato "PA1"
    And l'ente "PA1" concede la disponibilità a ricevere deleghe
    And l'ente delegante "PA2"
    And l'ente "PA2" richiede la creazione di una delega per l'ente "PA1"
    And "GSP" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And l'utente è un m2m-admin dell'ente delegato
    When l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id "%actual" e delegationId "%actual"
    Then si ottiene status code 403
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And la richiesta di fruizione si trova in stato "PENDING"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_APPROVE_10] Un delegato all'erogazione con delega revocata NON può attivare una richiesta di fruizione m2m in stato PENDING per conto dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And l'ente "PA2" accetta la delega
    And "PA3" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And l'ente "PA1" con ruolo "admin" revoca la delega
    And l'utente è un m2m-admin dell'ente delegato
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual" e delegationId "%actual"
    Then si ottiene status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And la richiesta di fruizione si trova in stato "PENDING"

  @happy-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_APPROVE_11] Una richiesta di fruizione m2m in stato PENDING NON può essere approvata da un ente con delega in erogazione non valida
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA3" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And l'utente è un m2m-admin dell'ente delegato
    When l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id "%actual" e delegationId "%random"
    Then si ottiene status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And la richiesta di fruizione si trova in stato "PENDING"

  @happy-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario Outline: [M2M_AGREEMENTS_UNSUSPEND_1] Una richiesta di fruizione sospesa dal'erogatore o dal fruitore può essere riattivata può essere riattivata e tornare ACTIVE
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "<suspendingTenant>" ha già sospeso quella richiesta di fruizione come <suspendedBy>
    And l'utente è un "admin" di "<reactivatingTenant>" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "ACTIVE"

    Examples:
      | suspendingTenant | suspendedBy | reactivatingTenant |
      | PA1              | PRODUCER    | PA1                |
      | PA2              | CONSUMER    | PA2                |

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario Outline: [M2M_AGREEMENTS_UNSUSPEND_2] La riattivazione di una richiesta di fruizione con id non valido restituisce errore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente "admin" di "PA1" richiede una operazione di sospensione di quella richiesta di fruizione con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "<agreementId>"
    Then si ottiene status code <statusCode>
    And la richiesta di fruizione si trova in stato "SUSPENDED"

    Examples:
      | agreementId | statusCode |
      | %null       | 400        |
      | %random     | 404        |

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario Outline: [M2M_AGREEMENTS_UNSUSPEND_3A] La riattivazione di una richiesta di fruizione in stato differente da SUSPENDED restituisce errore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato <agreementStatus> per quell'e-service
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 409
    And la richiesta di fruizione si trova in stato <agreementStatus>

    Examples:
      | agreementStatus |
      | "ACTIVE"        |
      | "ARCHIVED"      |

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_3B] La riattivazione di una richiesta di fruizione m2m in stato PENDING restituisce errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 409
    And la richiesta di fruizione si trova in stato "PENDING"

  @happy-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_4] Una richiesta di fruizione sospesa dall'erogatore dell'e-service può essere riattivata da un utente M2M-ADMIN dell'ente delegato in erogazione
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'ente delegato richiede una operazione di sospensione di quella richiesta di fruizione
    And l'utente è un m2m-admin dell'ente delegato
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual" e delegationId "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "ACTIVE"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_5] La riattivazione di una richiesta di fruizione con ruolo M2M restituisce errore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente "admin" di "PA1" richiede una operazione di sospensione di quella richiesta di fruizione con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 403
    And la richiesta di fruizione si trova in stato "SUSPENDED"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_6] La riattivazione di una richiesta di fruizione con stato REJECTED restituisce errore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "GSP" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    And "PA1" ha già rifiutato quella richiesta di fruizione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 409
    And la richiesta di fruizione si trova in stato "REJECTED"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario Outline: [M2M_AGREEMENTS_UNSUSPEND_7] La riattivazione di una richiesta di fruizione con stato MISSING_CERTIFIED_ATTRIBUTES restituisce errore
    Given l'utente è un "admin" di "<enteErogatore>"
    And "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    And "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    And "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 409
    And la richiesta di fruizione si trova in stato "MISSING_CERTIFIED_ATTRIBUTES"

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_8] Se una richiesta di fruizione m2m viene sospesa dall'erogatore dell'e-service e il fruitore tenta di riattivarla, questa rimarrà in stato SUSPENDED
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "SUSPENDED"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_9] Se una richiesta di fruizione m2m viene sospesa dall'erogatore e dal fruitore dell'e-service e il fruitore tenta di riattivarla, questa rimarrà in stato SUSPENDED
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    And "PA2" ha già sospeso quella richiesta di fruizione come CONSUMER
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "SUSPENDED"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_10] Se una richiesta di fruizione m2m viene sospesa dal fruitore e l'erogatore dell'e-service tenta di riattivarla, questa rimarrà in stato SUSPENDED
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già sospeso quella richiesta di fruizione come CONSUMER
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "SUSPENDED"

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_11] Se una richiesta di fruizione m2m viene sospesa dal fruitore e dall'erogatore e l'erogatore dell'e-service tenta di riattivarla, questa rimarrà in stato SUSPENDED
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    And "PA2" ha già sospeso quella richiesta di fruizione come CONSUMER
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "SUSPENDED"

  @happy-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_12] Un delegato alla fruizione riattiva una richiesta di fruizione m2m in stato SUSPENDED per conto del fruitore
    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione automatica
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'ente delegato richiede una operazione di sospensione di quella richiesta di fruizione
    And l'utente è un m2m-admin dell'ente delegato
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual" e delegationId "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "ACTIVE"

  @happy-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario Outline: [M2M_AGREEMENTS_UNSUSPEND_13] Per una richiesta di fruizione m2m precedentemente creata da un fruitore, la quale è in stato SUSPENDED (riattivazione), con uno o più attributi richiesti non posseduti dal fruitore, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, va a buon fine ma la richiesta di fruizione resta in stato "SUSPENDED"
    Given l'utente è un "admin" di "<enteErogatore>"
    And "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    And "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    And "<enteFruitore>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    And la richiesta di fruizione è passata in stato "SUSPENDED"
    And l'utente è un "admin" di "<enteErogatore>" con ruolo M2M m2m-admin
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "SUSPENDED"

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_14] Un delegato all'erogazione con delega revocata NON può riattivare una richiesta di fruizione m2m in stato SUSPENDED per conto dell'erogatore
    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione automatica
    And l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And l'ente "PA2" accetta la delega
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'ente delegato richiede una operazione di sospensione di quella richiesta di fruizione
    And l'ente "PA1" con ruolo "admin" revoca la delega
    And l'utente è un m2m-admin dell'ente delegato
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual" e delegationId "%actual"
    Then si ottiene status code 403
    And l'utente è un m2m-admin dell'ente delegante
    And la richiesta di fruizione si trova in stato "SUSPENDED"

  @happy-path
  @m2m-agreement-approve-unsuspend-refactor
  Scenario: [M2M_AGREEMENTS_UNSUSPEND_15] Una richiesta di fruizione m2m sospesa dall'erogatore dell'e-service NON può essere riattivata da un ente con delega in erogazione non valida
    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione automatica
    And l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA3" ha già sospeso quella richiesta di fruizione come CONSUMER
    And l'utente è un m2m-admin dell'ente delegato
    When l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id "%actual" e delegationId "%random"
    Then si ottiene status code 403
    And l'utente è un m2m-admin dell'ente delegante
    And la richiesta di fruizione si trova in stato "SUSPENDED"

  # Da qui in poi test di "API V2 Parte 2" https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1812562407/DRAFT+SRS+API+V2+Parte+2#Scenari-di-test
  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2M_AGREEMENTS_PURPOSES_1] La lista delle finalità correlate a un agreement può essere visualizzata da un utente con ruolo M2M-ADMIN o M2M (Parte2#Scenario 12)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 2 finalità in stato "ACTIVE" per quell'eservice
    When l'utente è un "admin" di "PA2" con ruolo M2M <ruolo-m2m>
    And l'utente tenta di ottenere la lista delle finalità correlate alla richiesta di fruizione
    Then si ottiene status code 200
    And le finalità vengono correttamente visualizzate
    Examples:
      | ruolo-m2m |
      | m2m-admin |
      | m2m       |

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_AGREEMENTS_PURPOSES_2] La lista delle finalità correlate a un agreement non può essere visualizzata specificando un token non valido (Parte2#Scenario 14)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di ottenere la lista delle finalità correlate a una richiesta di fruizione inesistente
    Then si ottiene status code 401

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_AGREEMENTS_PURPOSES_3] La lista delle finalità correlate a un agreement non può essere visualizzata specificando un id inesistente (Parte2#Scenario 15)
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di ottenere la lista delle finalità correlate a una richiesta di fruizione inesistente
    Then si ottiene status code 404

  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2M_AGREEMENTS_DOCUMENTS_1] La lista dei documenti correlati a un agreement può essere visualizzata da un utente con ruolo M2M-ADMIN o M2M (Parte2#Scenario 16)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And l'utente carica un documento allegato a quella richiesta di fruizione con successo
    When l'utente è un "admin" di "PA2" con ruolo M2M <ruolo-m2m>
    And l'utente tenta di ottenere la lista dei documenti correlati alla richiesta di fruizione
    Then si ottiene status code 200
    And i documenti vengono correttamente visualizzati
    Examples:
      | ruolo-m2m |
      | m2m-admin |
      | m2m       |

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_AGREEMENTS_DOCUMENTS_2] La lista dei documenti correlati a un agreement non può essere visualizzata specificando un token non valido (Parte2#Scenario 18)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di ottenere la lista dei documenti correlati a una richiesta di fruizione inesistente
    Then si ottiene status code 401

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_AGREEMENTS_DOCUMENTS_3] La lista dei documenti correlati a un agreement non può essere visualizzata specificando un id inesistente (Parte2#Scenario 19)
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di ottenere la lista dei documenti correlati a una richiesta di fruizione inesistente
    Then si ottiene status code 404
