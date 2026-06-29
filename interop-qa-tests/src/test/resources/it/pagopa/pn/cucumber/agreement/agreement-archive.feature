@agreement
Feature: Archiviazione richiesta di fruizione
  Tutti gli utenti autorizzati possono archiviare una richiesta di fruizione in stato ACTIVE o SUSPENDED

  @nrt-minimal
  @agreement_archive1a
  Scenario Outline: [AGREEMENT_ARCHIVE_01A] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE, alla richiesta di archiviazione da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente richiede una operazione di archiviazione della richiesta di fruizione
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente    | ruolo        | risultato |
      | PA1     | admin        |       204 |
      | GSP     | admin        |       204 |
      | Privato | admin        |       204 |

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
  @agreement_archive1b
  Scenario: [AGREEMENT_ARCHIVE_01B] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato SUSPENDED, alla richiesta di archiviazione da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "SUSPENDED" per quell'e-service
    When l'utente richiede una operazione di archiviazione della richiesta di fruizione
    Then si ottiene status code 204

  @sad-path
  @nrt-minimal
  @agreement_archive2
  Scenario Outline: [AGREEMENT_ARCHIVE_02] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE o SUSPENDED, alla richiesta di archiviazione da parte di un utente con sufficienti permessi dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente richiede una operazione di archiviazione della richiesta di fruizione
    Then si ottiene status code 403
    Examples:
      | statoAgreement |
      | ACTIVE         |
      | SUSPENDED      |

  @sad-path
  @nrt-minimal
  @agreement_archive3a
  Scenario Outline: [AGREEMENT_ARCHIVE_03A] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato DRAFT o ARCHIVED, alla richiesta di archiviazione da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente richiede una operazione di archiviazione della richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | statoAgreement |
      | DRAFT          |
      | ARCHIVED       |

  @sad-path
  @nrt-minimal
  @agreement_archive3b
  Scenario: [AGREEMENT_ARCHIVE_03B] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato PENDING alla richiesta di archiviazione da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente richiede una operazione di archiviazione della richiesta di fruizione
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @agreement_archive3c
  Scenario: [AGREEMENT_ARCHIVE_03C] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato REJECTED alla richiesta di archiviazione da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "PA2" ha già rifiutato quella richiesta di fruizione
    When l'utente richiede una operazione di archiviazione della richiesta di fruizione
    Then si ottiene status code 400

  @sad-path @nrt-minimal
  @agreement_archive3d @no-parallel @certifiedAttribute
  Scenario Outline: [AGREEMENT_ARCHIVE_03D] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato MISSING_CERTIFIED_ATTRIBUTES alla richiesta di archiviazione da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    When l'utente richiede una operazione di archiviazione della richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |
