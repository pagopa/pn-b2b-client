@agreement
Feature: Attivazione richiesta di fruizione
  Tutti gli utenti autorizzati di enti PA e GSP possono attivare una richiesta di fruizione

  @nrt-minimal
  @agreement_activate1 @resource_intensive @certifiedAttribute @agreement-activate-refactor
  Scenario Outline: [AGREEMENT_ACTIVATE_01] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato PENDING (prima attivazione), con tutti gli attributi richiesti certificati, tutti gli attributi richiesti dichiarati dal fruitore, e tutti gli attributi richiesti verificati dall’erogatore, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, va a buon fine
    Given l'utente è un "<ruolo>" di "<enteErogatore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteFruitore>" ha già dichiarato un attributo
    Given "<enteFruitore>" ha già creato un attributo verificato
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "<enteErogatore>" ha già verificato l'attributo verificato a "<enteFruitore>"
    When l'utente richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo | statusCode |
      | PA1          | PA2               | GSP           | admin | 200        |
      | GSP          | PA2               | PA1           | admin | 200        |

    @sad-path
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo        | statusCode |
      | PA1          | PA2               | GSP           | api          | 403        |
      | PA1          | PA2               | GSP           | security     | 403        |
      | PA1          | PA2               | GSP           | support      | 403        |
      | PA1          | PA2               | GSP           | api,security | 403        |
      | GSP          | PA2               | PA1           | api          | 403        |
      | GSP          | PA2               | PA1           | security     | 403        |
      | GSP          | PA2               | PA1           | support      | 403        |
      | GSP          | PA2               | PA1           | api,security | 403        |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo    | statusCode |
      | PA1          | PA2               | GSP           | reviewer | 403        |
      | PA1          | PA2               | GSP           | viewer   | 403        |

  @happy-path @nrt-minimal
  @agreement_activate2 @no-parallel @certifiedAttribute @agreement-activate-refactor
  Scenario Outline: [AGREEMENT_ACTIVATE_02] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato SUSPENDED (riattivazione), con tutti gli attributi richiesti certificati, tutti gli attributi richiesti dichiarati dal fruitore, e tutti gli attributi richiesti verificati dall’erogatore, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, va a buon fine.
    Given l'utente è un "<ruolo>" di "<enteErogatore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteFruitore>" ha già dichiarato un attributo
    Given "<enteFruitore>" ha già creato un attributo verificato
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC"
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "<enteErogatore>" ha già verificato l'attributo verificato a "<enteFruitore>"
    Given "<enteErogatore>" ha già approvato quella richiesta di fruizione
    Given "<enteErogatore>" ha già sospeso quella richiesta di fruizione come PRODUCER
    Given "<enteFruitore>" ha già sospeso quella richiesta di fruizione come CONSUMER
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo | statusCode |
      | PA1          | PA2               | GSP           | admin | 200        |

    @sad-path
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo        | statusCode |
      | PA1          | PA2               | GSP           | api          | 403        |
      | PA1          | PA2               | GSP           | security     | 403        |
      | PA1          | PA2               | GSP           | support      | 403        |
      | PA1          | PA2               | GSP           | api,security | 403        |

  @happy-path @nrt-minimal
  @agreement_activate3 @no-parallel @agreement-activate-refactor
  Scenario Outline: [AGREEMENT_ACTIVATE_03] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato PENDING o SUSPENDED; con tutti gli attributi richiesti certificati, i quali sono due gruppi di due, dei quali il fruitore ne possiede uno per gruppo; tutti gli attributi richiesti dichiarati dal fruitore, i quali sono due gruppi di due, dei quali il fruitore ne possiede uno per gruppo; tutti gli attributi richiesti verificati dall’erogatore, i quali sono due gruppi di due, dei quali il fruitore ne possiede uno per gruppo; alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, va a buon fine.
    Given l'utente è un "admin" di "<enteErogatore>"
    Given due gruppi di due attributi certificati da "<enteCertificatore>", dei quali "<enteFruitore>" ne possiede uno per gruppo
    Given "<enteErogatore>" crea due gruppi di due attributi verificati
    Given due gruppi di due attributi dichiarati, dei quali "<enteFruitore>" ne possiede uno per gruppo
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "<enteErogatore>" verifica un attributo per ogni gruppo di attributi verificati a "<enteFruitore>"
    When l'utente richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "ACTIVE"

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path
  @nrt-minimal
  @agreement_activate4a @agreement-activate-refactor
  Scenario Outline: [AGREEMENT_ACTIVATE_04A] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato ACTIVE, ARCHIVED o SUSPENDED, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "GSP" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 400
    And la richiesta di fruizione è in stato "<statoAgreement>"

    Examples:
      | statoAgreement |
      | ACTIVE         |
      | ARCHIVED       |
      | SUSPENDED      |

  @deleghe1
  @agreement-activate-refactor
  Scenario: Un delegato alla fruizione sospende ed riattiva una finalità/richiesta di fruizione agendo come delegato e passando il delegationId
    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    When il delegato ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione
    Then si ottiene status code 200
    And l'utente è un "admin" dell'ente delegato
    # NON AGGENDO COME DELEGATO, DEVE APPROVARE LA RICHIESTA DI FRUIZIONE SENZA PASSARE IL DELEGATION-ID
    And "PA1" ha già approvato quella richiesta di fruizione
    And l'utente è un "admin" dell'ente delegato
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'ente delegato sospende quella finalità in stato "ACTIVE"
    And l'utente è un "admin" dell'ente delegato
    When l'utente delegato riattiva la finalità in stato "SUSPENDED" per quell'e-service
    When l'ente delegato richiede una operazione di sospensione di quella richiesta di fruizione
    And l'ente delegato ha già riattivato quella richiesta di fruizione come CONSUMER

  @deleghe1
  @agreement-activate-refactor
  Scenario: Un delegato sia all'erogazione che alla fruizione sospende ed approva una richiesta di fruizione passando il Delegation-id come discriminante per capire se agisce come delegato all'erogazione o alla fruizione - Delegato all'erogazione
    Given "PA2" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione automatica
    Given l'utente è un "admin" di "PA1"
    # CREAZIONE DELEGA IN FRUIZIONE VERSO PA1
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente "PA1" concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    # CREAZIONE DELEGA IN EROGAZIONE VERSO PA1
    And l'ente "PA1" concede la disponibilità a ricevere deleghe
    When l'ente "PA2" richiede la creazione di una delega per l'ente "PA1"
    And l'ente "PA1" accetta la delega
    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'ente delegato richiede una operazione di sospensione di quella richiesta di fruizione
    And l'ente delegato ha già riattivato quella richiesta di fruizione come PRODUCER


  @sad-path @nrt-minimal
  @agreement_activate4b @no-parallel @certifiedAttribute @agreement-activate-refactor
    #BUG: https://pagopa.atlassian.net/browse/PIN-7747
  Scenario Outline: [AGREEMENT_ACTIVATE_04B] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato MISSING_CERTIFIED_ATTRIBUTES, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "<enteErogatore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    When l'utente richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 400
    And la richiesta di fruizione è in stato "MISSING_CERTIFIED_ATTRIBUTES"

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path
  @nrt-minimal
  @agreement_activate4c @agreement-activate-refactor
  Scenario: [AGREEMENT_ACTIVATE_04C] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato REJECTED, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "GSP" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Given "PA1" ha già rifiutato quella richiesta di fruizione
    When l'utente richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 400
    And la richiesta di fruizione è in stato "REJECTED"

  @sad-path
  @nrt-minimal
  @agreement_activate5 @agreement-activate-refactor
  Scenario: [AGREEMENT_ACTIVATE_05] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato PENDING, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 403
    And la richiesta di fruizione è in stato "PENDING"

  @happy-path @nrt-minimal
  @agreement_activate6 @no-parallel @certifiedAttribute @agreement-activate-refactor
    #BUG: https://pagopa.atlassian.net/browse/PIN-7750
  Scenario Outline: [AGREEMENT_ACTIVATE_06] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato SUSPENDED (riattivazione), con uno o più attributi richiesti non posseduti dal fruitore, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, va a buon fine ma la richiesta di fruizione resta in stato "SUSPENDED"
    Given l'utente è un "admin" di "<enteErogatore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "SUSPENDED"
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "SUSPENDED"

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path
  @agreement-activate-refactor
  Scenario Outline: [AGREEMENTS_APPROVE_1] L'approvazione di una richiesta di fruizione con id non valido restituisce errore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente richiede una operazione di approvazione della richiesta di fruizione con id "<agreementId>"
    Then si ottiene status code <statusCode>
    And la richiesta di fruizione è in stato "PENDING"

    Examples:
      | agreementId | statusCode |
      | %null       | 400        |
      | %random     | 404        |

  @agreement-activate-refactor
  Scenario: [AGREEMENTS_APPROVE_2] Un delegato all'erogazione attiva una richiesta di fruizione in stato PENDING per conto dell'erogatore
    Given "PA2" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    And l'ente delegato "PA1"
    And l'ente "PA1" concede la disponibilità a ricevere deleghe
    And l'ente delegante "PA2"
    And l'ente "PA2" richiede la creazione di una delega per l'ente "PA1"
    And l'ente "PA1" accetta la delega
    And "GSP" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'ente delegato richiede una operazione di approvazione di quella richiesta di fruizione
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "ACTIVE"

  @agreement-activate-refactor
  Scenario: [AGREEMENTS_APPROVE_3] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato PENDING, alla richiesta di attivazione da parte di un utente di un tenant non autorizzato, si ottiene un errore
    Given l'utente è un "admin" di "PA3"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente richiede una operazione di approvazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 403
    And la richiesta di fruizione è in stato "PENDING"

  @agreement-activate-refactor
  Scenario: [AGREEMENTS_APPROVE_4] Un ente delegato con delega in erogazione non ancora accettata non può approvare una richiesta di fruizione in stato PENDING per conto dell'erogatore
    Given "PA2" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    And l'ente delegato "PA1"
    And l'ente "PA1" concede la disponibilità a ricevere deleghe
    And l'ente delegante "PA2"
    And l'ente "PA2" richiede la creazione di una delega per l'ente "PA1"
    And "GSP" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'ente delegato richiede una operazione di approvazione di quella richiesta di fruizione
    Then si ottiene status code 403
    And la richiesta di fruizione è in stato "PENDING"

  @sad-path
  @agreement-activate-refactor
  Scenario Outline: [AGREEMENTS_UNSUSPEND_1] La riattivazione di una richiesta di fruizione con id non valido restituisce errore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "<agreementId>"
    Then si ottiene status code <statusCode>
    And la richiesta di fruizione è in stato "SUSPENDED"

    Examples:
      | agreementId | statusCode |
      | %null       | 400        |
      | %random     | 404        |

  @sad-path
  @agreement-activate-refactor
  Scenario Outline: [AGREEMENTS_UNSUSPEND_2] La riattivazione di una richiesta di fruizione in stato differente da SUSPENDED restituisce errore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 400
    And la richiesta di fruizione è in stato "<statoAgreement>"

    Examples:
      | statoAgreement |
      | ACTIVE         |
      | ARCHIVED       |
      | PENDING        |

  @sad-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_3] La riattivazione di una richiesta di fruizione in stato REJECTED restituisce errore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "GSP" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    And "PA1" ha già rifiutato quella richiesta di fruizione
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 409
    And la richiesta di fruizione è in stato "REJECTED"

  @sad-path
  @agreement-activate-refactor
  Scenario Outline: [AGREEMENTS_UNSUSPEND_4] La riattivazione di una richiesta di fruizione in stato MISSING_CERTIFIED_ATTRIBUTES restituisce errore
    Given l'utente è un "admin" di "<enteErogatore>"
    And "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    And "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    And "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    And la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 400
    And la richiesta di fruizione è in stato "MISSING_CERTIFIED_ATTRIBUTES"

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @happy-path
  @agreement-activate-refactor
  Scenario Outline: [AGREEMENTS_UNSUSPEND_5] Una richiesta di fruizione sospesa dal producer o dal consumer può essere riattivata e tornare ACTIVE
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "<suspendingTenant>" ha già sospeso quella richiesta di fruizione come <suspendedBy>
    And l'utente è un "admin" di "<reactivatingTenant>"
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "ACTIVE"

    Examples:
      | suspendingTenant | suspendedBy | reactivatingTenant |
      | PA1              | PRODUCER    | PA1                |
      | PA2              | CONSUMER    | PA2                |

  @sad-path
  @agreement-activate-refactor
  Scenario Outline: [AGREEMENTS_UNSUSPEND_6] La riattivazione di una richiesta di fruizione da parte di un utente non autorizzato restituisce errore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "<suspendingTenant>" ha già sospeso quella richiesta di fruizione come <suspendedBy>
    And l'utente è un "<ruolo>" di "<ente>"
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 403
    And la richiesta di fruizione è in stato "SUSPENDED"

    Examples:
      | suspendingTenant | suspendedBy | ente | ruolo        |
      | PA1              | PRODUCER    | PA1  | api          |
      | PA1              | PRODUCER    | PA1  | security     |
      | PA1              | PRODUCER    | PA1  | support      |
      | PA1              | PRODUCER    | PA1  | api,security |
      | PA1              | PRODUCER    | PA2  | admin        |
      | PA2              | CONSUMER    | PA2  | api          |
      | PA2              | CONSUMER    | PA2  | security     |
      | PA2              | CONSUMER    | PA2  | support      |
      | PA2              | CONSUMER    | PA2  | api,security |
      | PA2              | CONSUMER    | PA1  | admin        |

  @happy-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_7] Un delegato all'erogazione riattiva una richiesta di fruizione in stato SUSPENDED per conto dell'erogatore
    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione automatica
    And l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And l'ente "PA2" accetta la delega
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'ente delegato richiede una operazione di sospensione di quella richiesta di fruizione
    When l'ente delegato richiede una operazione di riattivazione di quella richiesta di fruizione
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "ACTIVE"

  @happy-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_8] La sospensione e riattivazione di una richiesta di fruizione eseguita dal fruitore va a buon fine
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già sospeso quella richiesta di fruizione come PRODUCER
    And l'utente è un "admin" di "PA2"
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "ACTIVE"

  @happy-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_9] La sospensione e riattivazione di una richiesta di fruizione eseguita dall'erogatore dell'e-service va a buon fine
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    And l'utente è un "admin" di "PA1"
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "ACTIVE"

  @sad-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_10] Se una richiesta di fruizione viene sospesa dall'erogatore dell'e-service e il fruitore tenta di riattivarla, questa rimarrà in stato SUSPENDED
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    And l'utente è un "admin" di "PA2"
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "SUSPENDED"

  @sad-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_11] Se una richiesta di fruizione viene sospesa dall'erogatore e dal fruitore dell'e-service e il fruitore tenta di riattivarla, questa rimarrà in stato SUSPENDED
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    And "PA2" ha già sospeso quella richiesta di fruizione come CONSUMER
    And l'utente è un "admin" di "PA2"
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "SUSPENDED"

  @sad-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_12] Se una richiesta di fruizione viene sospesa dal fruitore e l'erogatore dell'e-service tenta di riattivarla, questa rimarrà in stato SUSPENDED
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già sospeso quella richiesta di fruizione come PRODUCER
    And l'utente è un "admin" di "PA1"
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "SUSPENDED"

  @sad-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_13] Se una richiesta di fruizione viene sospesa dal fruitore e dall'erogatore e l'erogatore dell'e-service tenta di riattivarla, questa rimarrà in stato SUSPENDED
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    And "PA2" ha già sospeso quella richiesta di fruizione come CONSUMER
    And l'utente è un "admin" di "PA1"
    When l'utente richiede una operazione di riattivazione della richiesta di fruizione con id "%actual"
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "SUSPENDED"

  @happy-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_14] Un delegato alla fruizione riattiva una richiesta di fruizione in stato SUSPENDED per conto del fruitore
    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione automatica
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'ente delegato richiede una operazione di sospensione di quella richiesta di fruizione
    When l'ente delegato richiede una operazione di riattivazione di quella richiesta di fruizione
    Then si ottiene status code 200
    And la richiesta di fruizione è in stato "ACTIVE"

  @sad-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_15] Un delegato all'erogazione con delega revocata NON può riattivare una richiesta di fruizione in stato SUSPENDED per conto dell'erogatore
    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione automatica
    And l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And l'ente "PA2" accetta la delega
    And "PA3" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'ente delegato richiede una operazione di sospensione di quella richiesta di fruizione
    And l'ente "PA1" con ruolo "admin" revoca la delega
    When l'ente delegato richiede una operazione di riattivazione di quella richiesta di fruizione
    Then si ottiene status code 403
    And la richiesta di fruizione è in stato "SUSPENDED"

  @sad-path
  @agreement-activate-refactor
  Scenario: [AGREEMENTS_UNSUSPEND_16] Un delegato alla fruizione con delega revocata NON può riattivare una richiesta di fruizione in stato SUSPENDED per conto del fruitore
    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione automatica
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'ente "PA3" concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'ente delegato richiede una operazione di sospensione di quella richiesta di fruizione
    And l'ente delegante con ruolo "admin" revoca la delega in fruizione
    When l'ente delegato richiede una operazione di riattivazione di quella richiesta di fruizione
    Then si ottiene status code 403
    And la richiesta di fruizione è in stato "SUSPENDED"

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario Outline: [CERT_DISCRETE_ATTR_AGREEMENT_1] Verifica della corretta associazione di una finalità su un e-service
  pubblicato con attributi certificati discreti.
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator   | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | <comparator> | <value> |                       |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA1"
    When l'utente crea una richiesta di fruizione
    Then si ottiene response status code 200
    Examples:
      | comparator | value                              |
      | GT         | $ATTR_CERT_DISCR_THRESHOLD(PA1,-1) |
      | EQ         | $ATTR_CERT_DISCR_THRESHOLD(PA1,0)  |
      | LT         | $ATTR_CERT_DISCR_THRESHOLD(PA1,1)  |
      | GTE        | $ATTR_CERT_DISCR_THRESHOLD(PA1,0)  |
      | LTE        | $ATTR_CERT_DISCR_THRESHOLD(PA1,0)  |
      | NE         | $ATTR_CERT_DISCR_THRESHOLD(PA1,1)  |

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_AGREEMENT_2] Fallimento della creazione di una nuova finalità per un e-service pubblicato
  se non vengono soddisfatti i requisiti degli attributi certificati discreti.
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value                                | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | LTE        | $ATTR_CERT_DISCR_THRESHOLD(PA1,-100) |                       |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA1"
    When l'utente crea una richiesta di fruizione
    Then si ottiene response status code 400

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_AGREEMENT_3] Fallimento della creazione di una nuova finalità per un e-service pubblicato
  se il fruitore non possiede l'attributo certificato discreto richiesto.
    Given l'utente è un "admin" di "PA3"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA3" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value                                | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | LTE        | $ATTR_CERT_DISCR_THRESHOLD(PA1,-100) |                       |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA2"
    And l'utente "PA2" non possiede nessun attributo certificato discreto
    When l'utente crea una richiesta di fruizione
    Then si ottiene response status code 400

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario Outline: [CERT_DISCRETE_ATTR_AGREEMENT_4] Validazione logiche in AND per gli attributi certificati discreti nella richiesta di fruizione
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator    | value    | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | <comparator1> | <value1> |                       |
      | CERTIFIED_DISCRETE | 1     | <comparator2> | <value2> |                       |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA1"
    When l'utente crea una richiesta di fruizione
    Then si ottiene response status code <expectedResult>
    Examples:
      | comparator1 | value1                               | comparator2 | value2                               | expectedResult |
      # Entrambi soddisfatti
      | GT          | $ATTR_CERT_DISCR_THRESHOLD(PA1,-100) | LT          | $ATTR_CERT_DISCR_THRESHOLD(PA1,100)  | 200            |
      # Secondo non soddisfatto
      | GT          | $ATTR_CERT_DISCR_THRESHOLD(PA1,-100) | LT          | $ATTR_CERT_DISCR_THRESHOLD(PA1,-100) | 400            |
      # Primo non soddisfatto
      | GT          | $ATTR_CERT_DISCR_THRESHOLD(PA1,100)  | LT          | $ATTR_CERT_DISCR_THRESHOLD(PA1,100)  | 400            |
      # Nessuno dei due soddisfatto
      | GT          | $ATTR_CERT_DISCR_THRESHOLD(PA1,100)  | LT          | $ATTR_CERT_DISCR_THRESHOLD(PA1,-100) | 400            |

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_AGREEMENT_5a] Validazione logiche in OR per gli attributi certificati discreti nella richiesta
  di fruizione: solo l'attributo certificato valida la richiesta di fruizione.
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value                             | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | EQ         | $ATTR_CERT_DISCR_THRESHOLD(PA1,1) |                       |
      | CERTIFIED          | 0     |            |                                   | 200                   |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    And l'utente assegna a "PA1" l'attributo certificato precedentemente creato
    And l'utente è un "admin" di "PA1"
    When l'utente crea una richiesta di fruizione
    Then si ottiene response status code 200

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_AGREEMENT_5b] Validazione logiche in OR per gli attributi certificati discreti nella richiesta
  di fruizione: solo l'attributo certificato discreto valida la richiesta di fruizione.
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value                                | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GT         | $ATTR_CERT_DISCR_THRESHOLD(PA1,-100) |                       |
      | CERTIFIED          | 0     |            |                                      | 200                   |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA1"
    When l'utente crea una richiesta di fruizione
    Then si ottiene response status code 200

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_AGREEMENT_5c] Validazione logiche in OR per gli attributi certificati discreti nella richiesta
  di fruizione: entrambi gli attributi validano la richiesta di fruizione
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value                                | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GT         | $ATTR_CERT_DISCR_THRESHOLD(PA1,-100) |                       |
      | CERTIFIED          | 0     |            |                                      | 200                   |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    And l'utente assegna a "PA1" l'attributo certificato precedentemente creato
    And l'utente è un "admin" di "PA1"
    When l'utente crea una richiesta di fruizione
    Then si ottiene response status code 200

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_AGREEMENT_5d] Validazione logiche in OR per gli attributi certificati discreti nella richiesta
  di fruizione: nessun attributo valida la richiesta di fruizione.
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value                               | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GT         | $ATTR_CERT_DISCR_THRESHOLD(PA1,100) |                       |
      | CERTIFIED          | 0     |            |                                     | 200                   |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA1"
    When l'utente crea una richiesta di fruizione
    Then si ottiene response status code 400
