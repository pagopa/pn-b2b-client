@agreement
Feature: Lettura di un documento allegato alla richiesta di fruizione
  Tutti gli utenti autorizzati possono leggere un documento allegato alla richiesta di fruizione

  @nrt-minimal
  @agreement_document_read1
  Scenario Outline: [AGREEMENT_DOCUMENT_READ_01] Un utente con sufficienti permessi, per una richiesta di fruizione precedentemente creata, la quale è in stato DRAFT, relativa a un e-service pubblicato dallo stesso ente, alla richiesta di lettura di un documento allegato, la richiesta va a buon fine.
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato e pubblicato 1 e-service
    Given "<ente>" ha già creato una richiesta di fruizione in stato "DRAFT" con un documento allegato
    When l'utente richiede una operazione di lettura del documento allegato a quella richiesta di fruizione
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | admin        |       200 |
      | GSP  | support      |       200 |
      | PA1  | admin        |       200 |
      | PA1  | support      |       200 |

    @happy-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | viewer       |       200 |
      | PA2  | viewer       |       200 |

    @sad-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | api          |       403 |
      | GSP  | security     |       403 |
      | GSP  | api,security |       403 |
      | PA1  | api          |       403 |
      | PA1  | security     |       403 |
      | PA1  | api,security |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | reviewer     |       403 |

  @happy-path
  @nrt-minimal
  @agreement_document_read2a
  Scenario Outline: [AGREEMENT_DOCUMENT_READ_02A] Un utente con sufficienti permessi, per una richiesta di fruizione precedentemente creata, la quale è in stato PENDING, ACTIVE, SUSPENDED, ARCHIVED, alla richiesta di lettura di un documento allegato, la richiesta va a buon fine.
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "<tipoApprovazione>"
    Given "PA1" ha già creato una richiesta di fruizione in stato "<statoAgreement>" con un documento allegato
    When l'utente richiede una operazione di lettura del documento allegato a quella richiesta di fruizione
    Then si ottiene status code 200

    Examples:
      | statoAgreement | tipoApprovazione |
      | PENDING        | MANUAL           |
      | ACTIVE         | AUTOMATIC        |
      | SUSPENDED      | AUTOMATIC        |
      | ARCHIVED       | AUTOMATIC        |

  @happy-path @nrt-minimal
  @agreement_document_read2b @certifiedAttribute
  Scenario Outline: [AGREEMENT_DOCUMENT_READ_02B] Un utente con sufficienti permessi, per una richiesta di fruizione precedentemente creata, la quale è in stato MISSING_CERTIFIED_ATTRIBUTES, alla richiesta di lettura di un documento allegato, la richiesta va a buon fine.
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha già creato una richiesta di fruizione in stato "DRAFT" con un documento allegato
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    When l'utente richiede una operazione di lettura del documento allegato a quella richiesta di fruizione
    Then si ottiene status code 200

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |
