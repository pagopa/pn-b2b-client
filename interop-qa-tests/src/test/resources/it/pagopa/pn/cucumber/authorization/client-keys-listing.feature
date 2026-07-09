@client
Feature: Listing chiavi client
  Tutti gli utenti autorizzati, security o support possono leggere la lista delle chiavi di un client a cui sono associati

  @happy-path
  @nrt-minimal
  Scenario Outline: [CLIENT_KEYS_LISTING_1] Un utente admin o security; appartenente all'ente che ha creato il client; il quale utente è membro del client; richiede l’elenco delle chiavi caricate per il client. L’operazione va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato 1 client "CONSUMER"
    Given "<ente>" ha già inserito l'utente con ruolo "<ruolo>" come membro di quel client
    Given un "<ruolo>" di "<ente>" ha caricato una chiave pubblica in quel client
    Given un "<ruolo>" di "<ente>" ha caricato una chiave pubblica in quel client
    Given un "<ruolo>" di "<ente>" ha caricato una chiave pubblica in quel client
    When l'utente richiede una operazione di listing delle chiavi di quel client
    Then si ottiene status code 200 e la lista di 3 chiavi

    Examples:
      | ente    | ruolo    |
      | GSP     | admin    |
      | PA1     | admin    |
      | Privato | admin    |
      | GSP     | security |
      | PA1     | security |
      | Privato | security |

  @happy-path
  @nrt-minimal
  Scenario Outline: [CLIENT_KEYS_LISTING_2] Un utente admin o support; appartenente all'ente che ha creato il client; il quale utente non è membro del client; richiede l’elenco delle chiavi caricate per il client. L’operazione va a buon fine
    Given l'utente è un "<ruolo>" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    Given "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    Given un "security" di "PA1" ha caricato una chiave pubblica in quel client
    Given un "security" di "PA1" ha caricato una chiave pubblica in quel client
    Given un "security" di "PA1" ha caricato una chiave pubblica in quel client
    When l'utente richiede una operazione di listing delle chiavi di quel client
    Then si ottiene status code 200 e la lista di 3 chiavi

    Examples:
      | ruolo        |
      | admin        |
      | support      |

  @sad-path
  @nrt-minimal
  @wait_for_fix
  Scenario Outline: [CLIENT_KEYS_LISTING_3] Un utente api, security o api/security; appartenente all'ente che ha creato il client; il quale utente non è membro del client; richiede l’elenco delle chiavi caricate per il client. L’operazione non va a buon fine
    Given l'utente è un "<ruolo>" di "PA2"
    Given "PA2" ha già creato 1 client "CONSUMER"
    Given "PA2" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "PA2" ha caricato una chiave pubblica in quel client
    Given un "admin" di "PA2" ha caricato una chiave pubblica in quel client
    Given un "admin" di "PA2" ha caricato una chiave pubblica in quel client
    When l'utente richiede una operazione di listing delle chiavi di quel client
    Then si ottiene status code 403

    Examples:
      | ruolo        |
      | api          |
      | security     |
      | api,security |

    @nuovi-operatori-update
    Examples:
      | ruolo        |
      | reviewer     |
      | viewer       |

  @happy-path
  @nrt-minimal @ko-nrt-08072026
  Scenario: [CLIENT_KEYS_LISTING_4] Un utente admin; appartenente all'ente che ha creato il client; richiede l’elenco delle chiavi caricate per il client da uno specifico utente. L’operazione va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    Given un "security" di "PA1" ha caricato una chiave pubblica in quel client
    Given un "security" di "PA1" ha caricato una chiave pubblica in quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica in quel client
    When l'utente richiede una operazione di listing delle chiavi di quel client create dall'utente "security"
    Then si ottiene status code 200 e la lista di 2 chiavi

  @happy-path
  @nrt-minimal
  Scenario: [CLIENT_KEYS_LISTING_5] Un utente admin; appartenente all'ente che ha creato il client; richiede l’elenco delle chiavi caricate per il client; nel client non ci sono chiavi. L’operazione va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    When l'utente richiede una operazione di listing delle chiavi di quel client create dall'utente "security"
    Then si ottiene status code 200 e la lista di 0 chiavi
