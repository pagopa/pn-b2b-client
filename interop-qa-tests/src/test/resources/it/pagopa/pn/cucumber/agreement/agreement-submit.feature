@agreement
Feature: Inoltro della richiesta di fruizione
Tutti gli utenti autorizzati possono inoltrare una richiesta di fruizione

  @nrt-minimal
  @agreement_submit1
  Scenario Outline: [AGREEMENT_SUBMIT_1] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato DRAFT, associata ad un e-service nella sua ultima versione pubblicata, la quale è in stato PUBLISHED, all'inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "<ente>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente    | ruolo        | risultato |
      | GSP     | admin        |       200 |
      | PA1     | admin        |       200 |
      | Privato | admin        |       200 |

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
  @agreement_submit2 @wait_for_fix @IMN-309
  Scenario: [AGREEMENT_SUBMIT_2] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato DRAFT, associata ad un e-service nella sua ultima versione pubblicata, la quale è in stato SUSPENDED, all'inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi dell’ente fruitore, dà errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "PA2" ha già sospeso quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then si ottiene status code 400

  @happy-path @nrt-minimal
  @agreement_submit3 @certifiedAttribute
  Scenario: [AGREEMENT_SUBMIT_3]  Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato DRAFT, associata ad un e-service nella sua ultima versione pubblicata, con NON tutti gli attributi richiesti certificati, all’inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi dell’ente fruitore, che è anche l’erogatore dell’e-service, va a buon fine
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha creato un attributo certificato e non lo ha assegnato a "PA2"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "PA2" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then si ottiene status code 200

  @happy-path
  @nrt-minimal
  @agreement_submit4
  Scenario: [AGREEMENT_SUBMIT_4] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato DRAFT, associata ad un e-service nella sua ultima versione pubblicata, la quale versione ha attivazione manuale delle richieste di fruizione e che non richiede attributi, all’inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi (admin) dell’ente fruitore, va a buon fine, e la richiesta di fruizione passa in stato PENDING
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then la richiesta di fruizione assume lo stato "PENDING"

  @happy-path
  @nrt-minimal
  @agreement_submit5
  Scenario: [AGREEMENT_SUBMIT_5] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato DRAFT, associata ad un e-service nella sua ultima versione pubblicata, la quale versione ha attivazione automatica delle richieste di fruizione (agreementApprovalPolicy = AUTOMATIC), e che non richiede attributi, all’inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi (admin) dell’ente fruitore, va a buon fine, e la richiesta di fruizione passa in stato ACTIVE
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then la richiesta di fruizione assume lo stato "ACTIVE"

  @happy-path
  @nrt-minimal
  @agreement_submit6
  Scenario: [AGREEMENT_SUBMIT_6] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato DRAFT, associata ad un e-service nella sua ultima versione pubblicata, la quale versione ha attivazione automatica delle richieste di fruizione con almeno un attributo verificato che il fruitore NON possiede, all’inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine, e la richiesta di fruizione passa in stato PENDING
    Given l'utente è un "admin" di "PA1"
    Given "GSP" ha già creato un attributo verificato
    Given "GSP" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then la richiesta di fruizione assume lo stato "PENDING"

  @sad-path
  @nrt-minimal
  @agreement_submit7a
  Scenario Outline: [AGREEMENT_SUBMIT_7A] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato PENDING, ACTIVE, SUSPENDED, ARCHIVED, associata ad un e-service nella sua ultima versione pubblicata, all’inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi (admin) dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "<tipoApprovazione>"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then si ottiene status code 400

    Examples: 
      | statoAgreement | tipoApprovazione |
      | PENDING        | MANUAL           |
      | ACTIVE         | AUTOMATIC        |
      | SUSPENDED      | AUTOMATIC        |
      | ARCHIVED       | AUTOMATIC        |

  @sad-path
  @nrt-minimal
  @agreement_submit7b
  Scenario: [AGREEMENT_SUBMIT_7B] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato REJECTED associata ad un e-service nella sua ultima versione pubblicata, all’inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi (admin) dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "PA2" ha già rifiutato quella richiesta di fruizione
    When l'utente inoltra quella richiesta di fruizione
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @agreement_submit8
  Scenario: [AGREEMENT_SUBMIT_08] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato DRAFT, associata ad un e-service nella sua ultima versione pubblicata, MA NON tutti gli attributi richiesti dichiarati dal fruitore, all’inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi (admin) dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" non possiede uno specifico attributo dichiarato
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then si ottiene status code 400

  @sad-path @nrt-minimal
  @agreement_submit9 @certifiedAttribute
  Scenario Outline: [AGREEMENT_SUBMIT_09] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato MISSING_CERTIFIED_ATTRIBUTES, associata ad un e-service nella sua ultima versione pubblicata, con NON tutti gli attributi richiesti certificati, all’inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi (admin) dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    When l'utente inoltra quella richiesta di fruizione
    Then si ottiene status code 400

    Examples: 
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path
  @nrt-minimal
  @agreement_submit10
  Scenario: [AGREEMENT_SUBMIT_10] Per una richiesta di fruizione precedentemente creata da un fruitore, la quale è in stato DRAFT, associata ad un e-service NON nella sua ultima versione pubblicata, all’inoltro della richiesta di fruizione da parte di un utente con sufficienti permessi (admin) dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then si ottiene status code 400
