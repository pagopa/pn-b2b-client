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
  Scenario Outline: [DESCRIPTOR_TK_PUBBLICATION_1] A seguito del cambiamento di tenant kind si tenta di pubblicare un proprio e-service in bozza
    Given l'utente è un "admin" di "<ente>"
    And "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind>"
    When l'utente attiva il descrittore di quell'e-service
    Then si ottiene status code 200
    And il descrittore risulta in stato "PUBLISHED"
    Examples:
      | ente    | kind    |
      | PA4     | PRIVATE |
      | PA4     | GSP     |
      | GSP2    | PA      |
      | GSP2    | PRIVATE |
      | Privato | PA      |
      | Privato | GSP     |
