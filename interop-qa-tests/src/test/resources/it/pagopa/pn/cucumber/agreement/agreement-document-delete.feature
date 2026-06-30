@agreement
Feature: Cancellazione di un documento allegato alla richiesta di fruizione
  Tutti gli utenti autorizzati possono cancellare un documento allegato alla richiesta di fruizione in stato DRAFT

  @nrt-minimal
  @agreement_document_delete1
  Scenario Outline: [AGREEMENT_DOCUMENT_DELETE_01] Un utente con sufficienti permessi, per una richiesta di fruizione precedentemente creata, la quale è in stato DRAFT, cancella un documento associato alla richiesta di fruizione. La richiesta va a buon fine.
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "<ente>" ha già creato una richiesta di fruizione in stato "DRAFT" con un documento allegato
    When l'utente cancella il documento allegato a quella richiesta di fruizione
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

  @sad-path
  @nrt-minimal
  @agreement_document_delete2a @wait_for_fix @IMN-310
  Scenario Outline: [AGREEMENT_DOCUMENT_DELETE_02A] Un utente con sufficienti permessi, per una richiesta di fruizione precedentemente creata, la quale è in stato PENDING, ACTIVE, SUSPENDED, ARCHIVED, cancella un documento associato alla richiesta di fruizione. Ottiene un errore.
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "<tipoApprovazione>"
    Given "PA1" ha già creato una richiesta di fruizione in stato "<statoAgreement>" con un documento allegato
    When l'utente cancella il documento allegato a quella richiesta di fruizione
    Then si ottiene status code 403

    Examples:
      | statoAgreement | tipoApprovazione |
      | PENDING        | MANUAL           |
      | ACTIVE         | AUTOMATIC        |
      | SUSPENDED      | AUTOMATIC        |
      | ARCHIVED       | AUTOMATIC        |

  @sad-path @nrt-minimal
  @agreement_document_delete2b @certifiedAttribute
  Scenario Outline: [AGREEMENT_DOCUMENT_DELETE_02B] Un utente con sufficienti permessi, per una richiesta di fruizione precedentemente creata, la quale è in stato MISSING_CERTIFIED_ATTRIBUTES, cancella un documento associato alla richiesta di fruizione. Ottiene un errore.
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha già creato una richiesta di fruizione in stato "DRAFT" con un documento allegato
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    When l'utente cancella il documento allegato a quella richiesta di fruizione
    Then si ottiene status code 403

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |
