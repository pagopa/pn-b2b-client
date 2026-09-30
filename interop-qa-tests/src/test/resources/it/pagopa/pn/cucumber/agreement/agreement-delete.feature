@agreement
Feature: Cancellazione richiesta di fruizione
  Tutti gli utenti autorizzati possono cancellare una richiesta di fruizione in stato DRAFT o MISSING_CERTIFIED_ATTRIBUTES

  @nrt-minimal
  @agreement_delete1a
  Scenario Outline: [AGREEMENT_DELETE_01A] Per una richiesta di fruizione precedentemente creata dall’ente, la quale è in stato DRAFT, alla richiesta di cancellazione da parte di un utente con sufficienti permessi, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "<ente>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente richiede una operazione di cancellazione della richiesta di fruizione
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente    | ruolo        | risultato |
      | GSP     | admin        |       204 |
      | PA1     | admin        |       204 |
      | Privato | admin        |       204 |

    @sad-path
    Examples:
      | ente    | ruolo        | risultato |
      | GSP     | api          |       403 |
      | GSP     | security     |       403 |
      | GSP     | support      |       403 |
      | GSP     | api,security |       403 |
      | PA1     | api          |       403 |
      | PA1     | security     |       403 |
      | PA1     | support      |       403 |
      | PA1     | api,security |       403 |
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

  @happy-path @nrt-minimal
  @agreement_delete1b @certifiedAttribute
  Scenario Outline: [AGREEMENT_DELETE_01B] Per una richiesta di fruizione precedentemente creata dall’ente, la quale è in stato MISSING_CERTIFIED_ATTRIBUTES, alla richiesta di cancellazione da parte di un utente con sufficienti permessi, va a buon fine
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    When l'utente richiede una operazione di cancellazione della richiesta di fruizione
    Then si ottiene status code 204

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path
  @nrt-minimal
  @agreement_delete2a
  Scenario Outline: [AGREEMENT_DELETE_02A] Per una richiesta di fruizione precedentemente creata dall’ente, la quale è in stato PENDING, ACTIVE, SUSPENDED o ARCHIVED, alla richiesta di cancellazione da parte di un utente con sufficienti permessi, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "<tipoApprovazione>"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente richiede una operazione di cancellazione della richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | statoAgreement | tipoApprovazione |
      | ACTIVE         | AUTOMATIC        |
      | SUSPENDED      | AUTOMATIC        |
      | ARCHIVED       | AUTOMATIC        |

  @sad-path
  @nrt-minimal
  @agreement_delete2b
  Scenario: [AGREEMENT_DELETE_02B] Per una richiesta di fruizione precedentemente creata dall’ente, la quale è in stato REJECTED, alla richiesta di cancellazione da parte di un utente con sufficienti permessi, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "PA2" ha già rifiutato quella richiesta di fruizione
    When l'utente richiede una operazione di cancellazione della richiesta di fruizione
    Then si ottiene status code 400
