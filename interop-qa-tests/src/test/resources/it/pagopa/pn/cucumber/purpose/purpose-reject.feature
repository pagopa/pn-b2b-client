@purpose
Feature: Rifiuto di una versione di una finalità
  Tutti gli utenti autorizzati di enti erogatori possono rifiutare una nuova versione di una finalità

  @nrt-minimal
  @purpose_reject1
  Scenario Outline: [PURPOSE_REJECT_1] Per una finalità precedentemente creata e presentata da un fruitore sopra una delle soglie dell’e-service dell’erogatore, la quale prima versione è quindi in stato WAITING_FOR_APPROVAL, alla richiesta di rifiuto con motivazione da parte di un utente con sufficienti permessi (admin) dell’ente erogatore, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato e pubblicato 1 e-service
    Given "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    When l'utente rifiuta la finalità aggiungendo una motivazione
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | risultato |
      | PA1  | admin        |       204 |
      | GSP  | admin        |       204 |

    @sad-path
    Examples:
      | ente | ruolo        | risultato |
      | PA1  | api          |       403 |
      | PA1  | security     |       403 |
      | PA1  | api,security |       403 |
      | PA1  | support      |       403 |
      | GSP  | api          |       403 |
      | GSP  | security     |       403 |
      | GSP  | api,security |       403 |
      | GSP  | support      |       403 |

  @happy-path
  @nrt-minimal
  @purpose_reject2
  Scenario: [PURPOSE_REJECT_2] Per una finalità precedentemente creata e in stato ACTIVE o SUSPENDED, sulla quale è successivamente presentata una richiesta di cambio piano sopra una delle soglie dell’e-service dell’erogatore, la quale versione successiva alla prima è quindi in stato WAITING_FOR_APPROVAL, alla richiesta di rifiuto con motivazione da parte di un utente con sufficienti permessi (admin) dell’ente erogatore, va a buon fine. La versione precedente della finalità rimane comunque nello stato nella quale si trovava prima del rifiuto
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 1 e-service
    Given "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    Given "PA2" ha già richiesto l'aggiornamento della stima di carico superando i limiti di quell'e-service
    When l'utente rifiuta la finalità aggiungendo una motivazione
    Then si ottiene status code 204
    And la versione precedente della finalità rimane nello stato in cui si trovava prima del rifiuto

  @sad-path
  @nrt-minimal
  @purpose_reject3 @fixed_in_node
  Scenario Outline: [PURPOSE_REJECT_3] Per una finalità precedentemente creata da un fruitore in stato NON WAITING_FOR_APPROVAL (DRAFT, ACTIVE, SUSPENDED, ARCHIVED), alla richiesta di rifiuto con motivazione da parte di un utente con sufficienti permessi (admin) dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 1 e-service
    Given "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA2" ha già creato 1 finalità in stato "<statoFinalita>" per quell'eservice
    When l'utente rifiuta la finalità aggiungendo una motivazione
    Then si ottiene status code 400

    Examples:
      | statoFinalita |
      | DRAFT         |
      | ACTIVE        |
      | SUSPENDED     |
      | ARCHIVED      |

  @sad-path
  @nrt-minimal
  @purpose_reject4 @fixed_in_node
  Scenario: [PURPOSE_REJECT_4] Per una finalità precedentemente creata e presentata da un fruitore sopra una delle soglie dell’e-service dell’erogatore, la quale prima versione è quindi in stato WAITING_FOR_APPROVAL, alla richiesta di rifiuto SENZA motivazione da parte di un utente con sufficienti permessi (admin) dell’ente erogatore, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 1 e-service
    Given "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    When l'utente rifiuta la finalità senza una motivazione
    Then si ottiene status code 400

  @adeguamento-analisi-rischio
  Scenario Outline: [PURPOSE_REJECT_TK_1] A seguito del cambiamento di tenant kind si tenta di respingere una finalità in attesa di approvazione
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "<ente>" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "<ente>" ha già richiesto l'aggiornamento della stima di carico superando i limiti di quell'e-service
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind>"
    When l'utente è un "admin" di "PA2"
    And l'utente rifiuta la finalità aggiungendo una motivazione
    Then si ottiene status code 204
    And la versione precedente della finalità rimane nello stato in cui si trovava prima del rifiuto
    Examples:
      | ente    | kind    |
      | PA4     | PRIVATE |
      | PA4     | GSP     |
      | GSP2    | PA      |
      | Privato | PA      |