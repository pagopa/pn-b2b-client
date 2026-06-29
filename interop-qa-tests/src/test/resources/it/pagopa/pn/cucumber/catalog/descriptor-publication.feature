@descriptor
Feature: Pubblicazione di un descrittore
  Tutti gli utenti autorizzati di enti erogatori possono pubblicare i propri descrittori

  @nrt-minimal
  @descriptor_publication1
  Scenario Outline: [DESCRIPTOR_PUBBLICATION_1] Per un e-service creato in modalità "DELIVER" che ha un solo descrittore, il quale è in stato DRAFT, con tutti i parametri richiesti inseriti e formattati correttamente, alla richiesta di pubblicazione, la bozza viene pubblicata correttamente
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    Given "<ente>" ha già caricato un'interfaccia per quel descrittore
    When l'utente pubblica quel descrittore
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | admin        |       204 |
      | GSP  | api          |       204 |
      | GSP  | api,security |       204 |
      | PA1  | admin        |       204 |
      | PA1  | api          |       204 |
      | PA1  | api,security |       204 |

    @sad-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | security     |       403 |
      | GSP  | support      |       403 |
      | PA1  | security     |       403 |
      | PA1  | support      |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | reviewer     |       403 |
      | GSP  | viewer       |       403 |
      | PA2  | reviewer     |       403 |
      | PA2  | viewer       |       403 |

  @sad-path
  @nrt-minimal
  @descriptor_publication2
  Scenario Outline: [DESCRIPTOR_PUBBLICATION_2] Per un e-service creato in modalità "DELIVER" che ha un solo descrittore, il quale non è in stato DRAFT, alla richiesta di pubblicazione, si ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoVersione>"
    When l'utente pubblica quel descrittore
    Then si ottiene status code 400

    Examples: 
      | statoVersione |
      | PUBLISHED     |
      | SUSPENDED     |
      | DEPRECATED    |
      | ARCHIVED      |

  @sad-path
  @nrt-minimal
  @descriptor_publication3
  Scenario: [DESCRIPTOR_PUBBLICATION_3] Per un e-service creato in modalità "RECEIVE" che ha un solo descrittore, il quale è in stato DRAFT, con tutti i parametri richiesti inseriti e formattati correttamente, senza nessuna analisi del rischio inserita, alla richiesta di pubblicazione, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    Given "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'utente pubblica quel descrittore
    Then si ottiene status code 400

  @sad-path @nrt-minimal
  @descriptor_publication4
  Scenario: [DESCRIPTOR_PUBBLICATION_4] Per un e-service creato in modalità "RECEIVE" che ha un solo descrittore, il quale è in stato DRAFT, con tutti i parametri richiesti inseriti e formattati correttamente, e con un’analisi del rischio compilata solo parzialmente, alla richiesta di pubblicazione, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    Given "PA1" ha già caricato un'interfaccia per quel descrittore
    Given l'utente ha compilato parzialmente l'analisi del rischio
    When l'utente pubblica quel descrittore
    Then si ottiene status code 400

  @adeguamento-analisi-rischio
  Scenario Outline: [DESCRIPTOR_TK_PUBBLICATION_1_A] A seguito del cambiamento di tenant kind si tenta di pubblicare un proprio e-service in bozza ad erogazione inversa, con analisi del rischio coerente con il kind iniziale
    Given l'utente è un "admin" di "<ente>"
    And "<ente>" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    And "<ente>" ha già caricato un'interfaccia per quel descrittore
    And l'utente aggiunge con successo un'analisi del rischio coerente con il tenant kind "<kind_iniziale>"
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind_target>"
    When l'utente pubblica quel descrittore
    Then si ottiene status code 400
    Examples:
      | ente    | kind_iniziale | kind_target |
      | PA4     | PA            | PRIVATE     |
      | PA4     | PA            | GSP         |
      | GSP2    | GSP           | PA          |
      | Privato | PRIVATE       | PA          |

  @adeguamento-analisi-rischio
  Scenario Outline: [DESCRIPTOR_TK_PUBBLICATION_1_B2] A seguito del cambiamento di tenant kind si tenta di pubblicare un proprio e-service in bozza ad erogazione inversa, con analisi del rischio coerente con il kind finale
    Given l'utente è un "admin" di "<ente>"
    And "<ente>" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "DRAFT"
    And "<ente>" ha già caricato un'interfaccia per quel descrittore
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind_target>"
    And l'utente aggiunge con successo un'analisi del rischio coerente con il tenant kind "<kind_target>"
    When l'utente pubblica quel descrittore
    Then si ottiene status code 200
    And il descrittore risulta in stato "PUBLISHED"
    Examples:
      | ente    | kind_target |
      | PA4     | PRIVATE     |
      | PA4     | GSP         |
      | GSP2    | PA          |
      | Privato | PA          |

  # FIXME rimuovere
  #@debug
  Scenario: Utile solo a innescare il reset dei tenant kind
    Given l'utente è un "admin" di "PA1"
