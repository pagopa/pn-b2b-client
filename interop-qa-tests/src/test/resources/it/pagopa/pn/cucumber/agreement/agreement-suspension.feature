@agreement
Feature: Sospensione richiesta di fruizione
  Tutti gli utenti autorizzati possono sospendere una richiesta di fruizione

  @nrt-minimal
  @agreement_suspension1
  Scenario Outline: [AGREEMENT_SUSPENSION_01] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE, alla richiesta di sospensione da parte di un utente con sufficienti permessi dell’ente fruitore, che non coincide con l’ente erogatore, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente richiede una operazione di sospensione di quella richiesta di fruizione
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente    | ruolo        | risultato |
      | PA1     | admin        |       200 |
      | GSP     | admin        |       200 |
      | Privato | admin        |       200 |

    @sad-path
    Examples:
      | ente    | ruolo        | risultato |
      | PA1     | api          |       403 |
      | PA1     | security     |       403 |
      | PA1     | support      |       403 |
      | PA1     | api,security |       403 |
      | GSP     | api          |       403 |
      | GSP     | security     |       403 |
      | GSP     | support      |       403 |
      | GSP     | api,security |       403 |
      | Privato | api          |       403 |
      | Privato | security     |       403 |
      | Privato | support      |       403 |
      | Privato | api,security |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente    | ruolo        | risultato |
      | GSP     | reviewer     |       403 |
      | GSP     | viewer       |       403 |
      | Privato | reviewer     |       403 |
      | Privato | viewer       |       403 |

  @happy-path
  @nrt-minimal
  @agreement_suspension2
  Scenario: [AGREEMENT_SUSPENSION_02] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE, alla richiesta di sospensione da parte di un utente con sufficienti permessi dell’ente erogatore, che non coincide con l’ente fruitore, va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente richiede una operazione di sospensione di quella richiesta di fruizione
    Then si ottiene status code 200

  @happy-path
  @nrt-minimal
  @agreement_suspension3
  Scenario: [AGREEMENT_SUSPENSION_03] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE, alla richiesta di sospensione da parte di un utente con sufficienti permessi dell’ente erogatore, che coincide con l’ente fruitore, va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente richiede una operazione di sospensione di quella richiesta di fruizione
    Then si ottiene status code 200

  @nrt-minimal
  @agreement_suspension4a
  Scenario Outline: [AGREEMENT_SUSPENSION_04A] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato NON ACTIVE (PENDING, DRAFT, SUSPENDED, ARCHIVED), alla richiesta di sospensione da parte di un utente con sufficienti permessi, ottiene un errore. Nel caso in cui lo stato sia SUSPENDED il risultato sarà 200 per implementazione del pattern di idempotenza.
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "<tipoApprovazione>"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente richiede una operazione di sospensione di quella richiesta di fruizione
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | statoAgreement | tipoApprovazione | risultato |
      | SUSPENDED      | AUTOMATIC        |       200 |

    @sad-path
    Examples:
      | statoAgreement | tipoApprovazione | risultato |
      | DRAFT          | AUTOMATIC        |       400 |
      | PENDING        | MANUAL           |       400 |
      | ARCHIVED       | AUTOMATIC        |       400 |

  @sad-path
  @nrt-minimal
  @agreement_suspension4b
  Scenario: [AGREEMENT_SUSPENSION_04B] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato REJECTED, alla richiesta di sospensione da parte di un utente con sufficienti permessi, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Given "PA2" ha già rifiutato quella richiesta di fruizione
    When l'utente richiede una operazione di sospensione di quella richiesta di fruizione
    Then si ottiene status code 400

  @sad-path @nrt-minimal
  @agreement_suspension4c @certifiedAttribute
  Scenario Outline: [AGREEMENT_SUSPENSION_04C] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato MISSING_CERTIFIED_ATTRIBUTES, alla richiesta di sospensione da parte di un utente con sufficienti permessi, ottiene un errore
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    When l'utente richiede una operazione di sospensione di quella richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |
