@agreement
Feature: Creazione nuova richiesta di fruizione
  Tutti gli utenti autorizzati possono ottenere la lista dei fruitori dei propri e-service

  @nrt-minimal
  @agreement_creation1 @no-parallel @certifiedAttribute
  Scenario Outline: [AGREEMENT_CREATION_01] Un utente con sufficienti permessi (admin), il cui ente rispetta i requisiti (attributi certificati), senza altre richieste di fruizione per un e-service, crea una nuova richiesta di fruizione in bozza per l’ultima versione disponibile di quell'e-service, la quale è in stato PUBLISHED. La richiesta va a buon fine
    Given l'utente è un "<ruolo>" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    When l'utente crea una richiesta di fruizione in bozza per l'ultima versione di quell'e-service
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo        | risultato |
      | GSP          | PA2               | PA1           | admin        |       200 |
      | PA1          | PA2               | GSP           | admin        |       200 |
      | Privato      | PA2               | GSP           | admin        |       200 |

    @sad-path
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo        | risultato |
      | GSP          | PA2               | PA1           | api          |       403 |
      | GSP          | PA2               | PA1           | security     |       403 |
      | GSP          | PA2               | PA1           | support      |       403 |
      | GSP          | PA2               | PA1           | api,security |       403 |
      | PA1          | PA2               | GSP           | api          |       403 |
      | PA1          | PA2               | GSP           | security     |       403 |
      | PA1          | PA2               | GSP           | support      |       403 |
      | PA1          | PA2               | GSP           | api,security |       403 |
      | Privato      | PA2               | GSP           | api          |       403 |
      | Privato      | PA2               | GSP           | security     |       403 |
      | Privato      | PA2               | GSP           | support      |       403 |
      | Privato      | PA2               | GSP           | api,security |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | ruolo        | risultato |
      | GSP          | PA2               | PA1           | reviewer     |       403 |
      | GSP          | PA2               | PA1           | viewer       |       403 |
      | Privato      | PA2               | GSP           | reviewer     |       403 |
      | Privato      | PA2               | GSP           | viewer       |       403 |

  @happy-path @nrt-minimal
  @agreement_creation2a @certifiedAttribute
  Scenario Outline: [AGREEMENT_CREATION_02A] Un utente con sufficienti permessi, il cui ente rispetta i requisiti (attributi certificati), con altre richieste di fruizione in stato REJECTED per un e-service, crea una nuova richiesta di fruizione in bozza per l’ultima versione disponibile di quell'e-service, la quale è in stato PUBLISHED. La richiesta va a buon fine.
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione manuale
    Given "<enteFruitore>" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Given "<enteErogatore>" ha già rifiutato quella richiesta di fruizione
    When l'utente crea una richiesta di fruizione in bozza per l'ultima versione di quell'e-service
    Then si ottiene status code 200

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @happy-path @nrt-minimal
  @agreement_creation2b @no-parallel @certifiedAttribute
  Scenario Outline: [AGREEMENT_CREATION_02B] Un utente con sufficienti permessi il cui ente rispetta i requisiti (attributi certificati), con altre richieste di fruizione in stato ARCHIVED per un e-service, crea una nuova richiesta di fruizione in bozza per l’ultima versione disponibile di quell'e-service, la quale è in stato PUBLISHED. La richiesta va a buon fine.
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "ARCHIVED" per quell'e-service
    When l'utente crea una richiesta di fruizione in bozza per l'ultima versione di quell'e-service
    Then si ottiene status code 200

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @happy-path @nrt-minimal
  @agreement_creation3 @certifiedAttribute
  Scenario: [AGREEMENT_CREATION_03] Un utente con sufficienti permessi, il cui ente NON rispetta i requisiti (attributi certificati), senza altre richieste di fruizione per un e-service, crea una nuova richiesta di fruizione in bozza per l’ultima versione disponibile di quell'e-service, la quale è in stato PUBLISHED; l’e-service è erogato dal suo stesso ente. La richiesta va a buon fine.
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha creato un attributo certificato e non lo ha assegnato a "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    When l'utente crea una richiesta di fruizione in bozza per l'ultima versione di quell'e-service
    Then si ottiene status code 200

  @sad-path @nrt-minimal
  @agreement_creation4a @no-parallel @certifiedAttribute
  Scenario Outline: [AGREEMENT_CREATION_04A] Un utente con sufficienti permessi il cui ente rispetta i requisiti (attributi certificati), con una richiesta di fruizione in stato DRAFT, PENDING, ACTIVE o SUSPENDED per un e-service, crea una nuova richiesta di fruizione in bozza per l’ultima versione disponibile di quell'e-service, la quale è in stato PUBLISHED. Ottiene un errore.
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione <tipoApprovazione>
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    When l'utente crea una richiesta di fruizione in bozza per l'ultima versione di quell'e-service
    Then si ottiene status code 409

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore | statoAgreement | tipoApprovazione |
      | PA1          | PA2               | GSP           | DRAFT          | automatica       |
      | PA1          | PA2               | GSP           | PENDING        | manuale          |
      | PA1          | PA2               | GSP           | ACTIVE         | automatica       |
      | PA1          | PA2               | GSP           | SUSPENDED      | automatica       |

  @sad-path @nrt-minimal
  @agreement_creation4b @no-parallel @certifiedAttribute
  Scenario Outline: [AGREEMENT_CREATION_04B] Un utente con sufficienti permessi, il cui ente rispetta i requisiti (attributi certificati), con una richiesta di fruizione in stato MISSING_CERTIFIED_ATTRIBUTES per un e-service, crea una nuova richiesta di fruizione in bozza per l’ultima versione disponibile di quell'e-service, la quale è in stato PUBLISHED. Ottiene un errore.
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    Given "<enteCertificatore>" ha già revocato quell'attributo a "<enteFruitore>"
    Given la richiesta di fruizione è passata in stato "MISSING_CERTIFIED_ATTRIBUTES"
    When l'utente crea una richiesta di fruizione in bozza per l'ultima versione di quell'e-service
    Then si ottiene status code 409

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path @nrt-minimal
  @agreement_creation5 @no-parallel @certifiedAttribute
  Scenario Outline: [AGREEMENT_CREATION_05] Un utente con sufficienti permessi, il cui ente rispetta i requisiti (attributi certificati), senza altre richieste di fruizione per un e-service, crea una nuova richiesta di fruizione in bozza per la penultima versione disponibile di quell'e-service, la quale è in stato DEPRECATED. Ottiene un errore.
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "DEPRECATED" che richiede quell'attributo certificato con approvazione automatica
    When l'utente crea una richiesta di fruizione in bozza per la penultima versione di quell'e-service
    Then si ottiene status code 400

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path @nrt-minimal
  @agreement_creation6 @certifiedAttribute
  Scenario Outline: [AGREEMENT_CREATION_06] Un utente con sufficienti permessi, il cui ente rispetta i requisiti (attributi certificati), senza altre richieste di fruizione per un e-service, crea una nuova richiesta di fruizione in bozza per l’ultima versione disponibile di quell'e-service, la quale è in stato SUSPENDED. Ottiene un errore.
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "SUSPENDED" che richiede quell'attributo certificato con approvazione automatica
    When l'utente crea una richiesta di fruizione in bozza per l'ultima versione di quell'e-service
    Then si ottiene status code 400

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path @nrt-minimal
  @agreement_creation7 @certifiedAttribute
  Scenario Outline: [AGREEMENT_CREATION_07] Un utente con sufficienti permessi, il cui ente NON rispetta i requisiti (attributi certificati), senza altre richieste di fruizione per un e-service; crea una nuova richiesta di fruizione in bozza per l’ultima versione disponibile di quell'e-service, la quale è in stato PUBLISHED. Ottiene un errore.
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e non lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    When l'utente crea una richiesta di fruizione in bozza per l'ultima versione di quell'e-service
    Then si ottiene status code 400

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |

  @sad-path @nrt-minimal
  @agreement_creation8 @certifiedAttribute
  Scenario Outline: [AGREEMENT_CREATION_08] Un utente con sufficienti permessi, il cui ente rispetta i requisiti (attributi certificati), ha già una richiesta di fruizione per quell’e-service in stato ACTIVE. L’erogatore ha già creato una nuova versione dello stesso e-service, in stato PUBLISHED. L’utente del fruitore, crea una nuova bozza di richiesta di fruizione per questa nuova versione. Ottiene un errore.
    Given l'utente è un "admin" di "<enteFruitore>"
    Given "<enteCertificatore>" ha creato un attributo certificato e lo ha assegnato a "<enteFruitore>"
    Given "<enteErogatore>" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    Given "<enteFruitore>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "<enteErogatore>" ha già pubblicato una nuova versione per quell'e-service
    When l'utente crea una richiesta di fruizione in bozza per l'ultima versione di quell'e-service
    Then si ottiene status code 409

    Examples:
      | enteFruitore | enteCertificatore | enteErogatore |
      | PA1          | PA2               | GSP           |
