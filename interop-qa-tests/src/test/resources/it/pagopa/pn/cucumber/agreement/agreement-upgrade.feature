@agreement
Feature: Upgrade di una richiesta di fruizione
  Tutti gli utenti autorizzati possono effettuare l'upgrade di una propria richiesta di fruizione

  @nrt-minimal
  @agreement_upgrade1a @no-parallel
  Scenario Outline: [UPGRADE_AGREEMENT_1] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE, e associata ad una versione di e-service antecedente all’ultima versione pubblicata, alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
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

  @happy-path
  @nrt-minimal
  @agreement_upgrade1b
  Scenario: [UPGRADE_AGREEMENT_2] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato SUSPENDED, e associata ad una versione di e-service antecedente all’ultima versione pubblicata, alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "SUSPENDED" per quell'e-service
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene status code 200

  @sad-path
  @nrt-minimal
  @agreement_upgrade2
  Scenario Outline: [UPGRADE_AGREEMENT_3] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE o SUSPENDED, e associata all’ultima versione di e-service pubblicata, alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | statoAgreement |
      | ACTIVE         |
      | SUSPENDED      |

  @sad-path
  @nrt-minimal
  @agreement_upgrade3a
  Scenario: [UPGRADE_AGREEMENT_4] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato REJECTED, e associata ad una versione di e-service antecedente all’ultima versione di e-service pubblicata, alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    Given "PA1" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Given "PA2" ha già rifiutato quella richiesta di fruizione
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene status code 400

  @sad-path @nrt-minimal
  @agreement_upgrade3b @certifiedAttribute
  Scenario Outline: [UPGRADE_AGREEMENT_5] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato MISSING_CERTIFIED_ATTRIBUTES e associata ad una versione di e-service antecedente all’ultima versione di e-service pubblicata, alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    Given "<enteErogatore>" ha già pubblicato una nuova versione per quell'e-service richiedendo gli stessi attributi certificati
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path
  @nrt-minimal
  @agreement_upgrade3c
  Scenario Outline: [UPGRADE_AGREEMENT_6] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato PENDING, DRAFT o ARCHIVED, e associata ad una versione di e-service antecedente all’ultima versione di e-service pubblicata, alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "<tipoApprovazione>"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | statoAgreement | tipoApprovazione |
      | PENDING        | MANUAL           |
      | DRAFT          | AUTOMATIC        |
      | ARCHIVED       | AUTOMATIC        |

  @sad-path @nrt-minimal
  @agreement_upgrade4 @wait_for_fix @IMN-351 @certifiedAttribute
  Scenario Outline: [UPGRADE_AGREEMENT_7] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE o SUSPENDED, e associata ad una versione di e-service antecedente all’ultima versione pubblicata, all'interno della nuova versione SONO cambiati gli attributi rispetto alla versione precedente, ed il fruitore non ne possegga uno o più tra quelli CERTIFICATI, alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "GSP" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    Given "PA2" ha creato un attributo certificato e non lo ha assegnato a "PA1"
    Given "GSP" ha già pubblicato una nuova versione per quell'e-service richiedendo gli stessi attributi certificati
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | statoAgreement |
      | ACTIVE         |
      | SUSPENDED      |

  @happy-path
  @nrt-minimal
  @agreement_upgrade5a
  Scenario Outline: [UPGRADE_AGREEMENT_8] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE o SUSPENDED, e associata ad una versione di e-service antecedente all’ultima versione pubblicata, all'interno della nuova versione SONO cambiati gli attributi rispetto alla versione precedente, ed il fruitore non ne possegga uno o più tra quelli DICHIARATI, alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service che richiede un attributo "DECLARED" che "PA1" non possiede
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene status code 200 ed è stata creata una nuova richiesta di fruizione in DRAFT

    Examples:
      | statoAgreement |
      | ACTIVE         |
      | SUSPENDED      |

  @happy-path
  @nrt-minimal
  @agreement_upgrade5b
  Scenario Outline: [UPGRADE_AGREEMENT_9] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE o SUSPENDED, e associata ad una versione di e-service antecedente all’ultima versione pubblicata, all'interno della nuova versione SONO cambiati gli attributi rispetto alla versione precedente, ed il fruitore non ne possegga uno o più tra quelli VERIFICATI, alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service che richiede un attributo "VERIFIED" che "PA1" non possiede
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene status code 200 ed è stata creata una nuova richiesta di fruizione in DRAFT

    Examples:
      | statoAgreement |
      | ACTIVE         |
      | SUSPENDED      |

  @happy-path
  @nrt-minimal
  @agreement_upgrade6
  Scenario Outline: [UPGRADE_AGREEMENT_10] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE o SUSPENDED, e associata ad una versione di e-service due indietro rispetto all’ultima versione pubblicata, nella quale nuova versione non sono cambiati gli attributi rispetto alle versioni precedenti, alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, va a buon fine.
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene status code 200 e la nuova richiesta di fruizione è associata alla versione 3 dell'eservice

    Examples:
      | statoAgreement |
      | ACTIVE         |
      | SUSPENDED      |

  @sad-path
  @nrt-minimal
  @agreement_upgrade7
  Scenario Outline: [UPGRADE_AGREEMENT_11] Per una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore, la quale è in stato ACTIVE o SUSPENDED, e associata ad una versione di e-service due indietro rispetto all’ultima versione pubblicata, all'interno della versione 2 non sono sono cambiati gli attributi rispetto alla versione 1; all'interno della versione 3 sono cambiati gli attributi rispetto alla versione 2 e il fruitore NON possiede almeno uno dei CERTIFICATI; alla richiesta di aggiornamento da parte di un utente con sufficienti permessi dell’ente fruitore, dà errore
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service
    Given "PA2" ha già pubblicato una nuova versione per quell'e-service che richiede un attributo "CERTIFIED" che "PA1" non possiede
    When l'utente richiede un'operazione di upgrade di quella richiesta di fruizione
    Then si ottiene status code 400

    Examples:
      | statoAgreement |
      | ACTIVE         |
      | SUSPENDED      |
