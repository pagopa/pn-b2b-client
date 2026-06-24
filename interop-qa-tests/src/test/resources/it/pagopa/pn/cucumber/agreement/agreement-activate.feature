@agreement
Feature: Attivazione richiesta di fruizione
  Tutti gli utenti autorizzati di enti PA e GSP possono attivare una richiesta di fruizione

  @nrt-minimal
  @agreement_activate1 @resource_intensive @certifiedAttribute
  Scenario Outline: [AGREEMENT_ACTIVATE_01] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato PENDING (prima attivazione), con tutti gli attributi richiesti certificati, tutti gli attributi richiesti dichiarati dal fruitore, e tutti gli attributi richiesti verificati dall’erogatore, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, va a buon fine
    Given l'utente è un "<ruolo>" di "<enteErogatore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteFruitore>" ha già dichiarato un attributo
    Given "<enteFruitore>" ha già creato un attributo verificato
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "<enteErogatore>" ha già verificato l'attributo verificato a "<enteFruitore>"
    When l'utente richiede una operazione di attivazione di quella richiesta di fruizione
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo        | risultato |
      | PA1          | PA2               | GSP           | admin        |       200 |
      | GSP          | PA2               | PA1           | admin        |       200 |

    @sad-path
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo        | risultato |
      | PA1          | PA2               | GSP           | api          |       403 |
      | PA1          | PA2               | GSP           | security     |       403 |
      | PA1          | PA2               | GSP           | support      |       403 |
      | PA1          | PA2               | GSP           | api,security |       403 |
      | GSP          | PA2               | PA1           | api          |       403 |
      | GSP          | PA2               | PA1           | security     |       403 |
      | GSP          | PA2               | PA1           | support      |       403 |
      | GSP          | PA2               | PA1           | api,security |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo        | risultato |
      | PA1          | PA2               | GSP           | reviewer     |       403 |
      | PA1          | PA2               | GSP           | viewer       |       403 |

  @happy-path @nrt-minimal
  @agreement_activate2 @no-parallel @certifiedAttribute
  Scenario Outline: [AGREEMENT_ACTIVATE_02] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato SUSPENDED (riattivazione), con tutti gli attributi richiesti certificati, tutti gli attributi richiesti dichiarati dal fruitore, e tutti gli attributi richiesti verificati dall’erogatore, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, va a buon fine.
    Given l'utente è un "admin" di "<enteErogatore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteFruitore>" ha già dichiarato un attributo
    Given "<enteFruitore>" ha già creato un attributo verificato
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC"
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "<enteErogatore>" ha già verificato l'attributo verificato a "<enteFruitore>"
    Given "<enteErogatore>" ha già approvato quella richiesta di fruizione
    Given "<enteErogatore>" ha già sospeso quella richiesta di fruizione come PRODUCER
    Given "<enteFruitore>" ha già sospeso quella richiesta di fruizione come CONSUMER
    When l'utente richiede una operazione di attivazione di quella richiesta di fruizione
    Then si ottiene status code 200

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @happy-path @nrt-minimal
  @agreement_activate3 @no-parallel
  Scenario Outline: [AGREEMENT_ACTIVATE_03] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato PENDING o SUSPENDED; con tutti gli attributi richiesti certificati, i quali sono due gruppi di due, dei quali il fruitore ne possiede uno per gruppo; tutti gli attributi richiesti dichiarati dal fruitore, i quali sono due gruppi di due, dei quali il fruitore ne possiede uno per gruppo; tutti gli attributi richiesti verificati dall’erogatore, i quali sono due gruppi di due, dei quali il fruitore ne possiede uno per gruppo; alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, va a buon fine.
    Given l'utente è un "admin" di "<enteErogatore>"
    Given due gruppi di due attributi certificati da "<enteCertificatore>", dei quali "<enteFruitore>" ne possiede uno per gruppo
    Given "<enteErogatore>" crea due gruppi di due attributi verificati
    Given due gruppi di due attributi dichiarati, dei quali "<enteFruitore>" ne possiede uno per gruppo
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "<enteErogatore>" verifica un attributo per ogni gruppo di attributi verificati a "<enteFruitore>"
    When l'utente richiede una operazione di attivazione di quella richiesta di fruizione
    Then si ottiene status code 200

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path
  @nrt-minimal
  @agreement_activate4a
  Scenario Outline: [AGREEMENT_ACTIVATE_04A] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato ACTIVE, ARCHIVED, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "GSP" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente richiede una operazione di attivazione di quella richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | statoAgreement |
      | ACTIVE         |
      | ARCHIVED       |

  @deleghe1
  Scenario: Un delegato alla fruizione sospende ed attiva una finalità/richiesta di fruizione agendo come delegato e passando il delegationId
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
    And l'ente delegato ha già approvato quella richiesta di fruizione

  @deleghe1
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
    And l'ente delegato ha già approvato quella richiesta di fruizione


  @sad-path @nrt-minimal
  @agreement_activate4b @no-parallel @certifiedAttribute
    #BUG: https://pagopa.atlassian.net/browse/PIN-7747
  Scenario Outline: [AGREEMENT_ACTIVATE_04B] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato MISSING_CERTIFIED_ATTRIBUTES, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "<enteErogatore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    When l'utente richiede una operazione di attivazione di quella richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path
  @nrt-minimal
  @agreement_activate4c
  Scenario: [AGREEMENT_ACTIVATE_04C] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato REJECTED, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "GSP" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Given "PA1" ha già rifiutato quella richiesta di fruizione
    When l'utente richiede una operazione di attivazione di quella richiesta di fruizione
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @agreement_activate5
  Scenario: [AGREEMENT_ACTIVATE_05] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato PENDING, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente richiede una operazione di attivazione di quella richiesta di fruizione
    Then si ottiene status code 403

  @happy-path @nrt-minimal
  @agreement_activate6 @no-parallel @certifiedAttribute
    #BUG: https://pagopa.atlassian.net/browse/PIN-7750
  Scenario Outline: [AGREEMENT_ACTIVATE_06] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato SUSPENDED (riattivazione), con uno o più attributi richiesti non posseduti dal fruitore, alla richiesta di attivazione da parte di un utente con sufficienti permessi dell’ente erogatore, va a buon fine ma la richiesta di fruizione resta in stato "SUSPENDED"
    Given l'utente è un "admin" di "<enteErogatore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "SUSPENDED"
    When l'utente richiede una operazione di attivazione di quella richiesta di fruizione
    Then si ottiene status code 200

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |
