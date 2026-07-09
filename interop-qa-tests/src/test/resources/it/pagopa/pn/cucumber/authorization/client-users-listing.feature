@client
Feature: Listing utenti client
  Tutti gli utenti autorizzati o security possono leggere la lista dei membri di un client a cui sono associati

  @sad-path
  @nrt-minimal @ko-nrt-08072026
  Scenario Outline: [CLIENT_USER_LISTING_1] Un utente API richiede la lista dei membri del client. Ritorna errore 403.
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato 1 client "CONSUMER"
    Given "<ente>" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given "<ente>" ha già inserito l'utente con ruolo "security" come membro di quel client
    Given "<ente>" ha già inserito l'utente con ruolo "api,security" come membro di quel client
    When l'utente richiede una operazione di listing dei membri di quel client
    Then si ottiene status code 403

    Examples:
      | ente    | ruolo    |
      | GSP     | api      |
      | PA1     | api      |
      | Privato | api      |

    @nuovi-operatori-update
    Examples:
      | ente    | ruolo    |
      | GSP     | reviewer |
      | GSP     | viewer   |
      | PA2     | reviewer |
      | PA2     | viewer   |
      | Privato | reviewer |
      | Privato | viewer   |

  @happy-path
  @nrt-minimal
  Scenario Outline: [CLIENT_USER_LISTING_2] Un utente associato ad un client, di ruolo non API, richiede la lista dei membri del client stesso. L'operazione va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato 1 client "CONSUMER"
    Given "<ente>" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given "<ente>" ha già inserito l'utente con ruolo "security" come membro di quel client
    Given "<ente>" ha già inserito l'utente con ruolo "api,security" come membro di quel client
    When l'utente richiede una operazione di listing dei membri di quel client
    Then si ottiene status code 200 e la lista di 3 utenti

    Examples:
      | ente    | ruolo        |
      | GSP     | admin        |
      | GSP     | security     |
      | GSP     | support      |
      | GSP     | api,security |
      | PA1     | admin        |
      | PA1     | security     |
      | PA1     | support      |
      | PA1     | api,security |
      | Privato | admin        |
      | Privato | security     |
      | Privato | support      |
      | Privato | api,security |

  @happy-path
  @nrt-minimal
  Scenario: [CLIENT_USER_LISTING_3] Un utente con permessi admin; appartenente all'ente che ha creato il client; richiede la lista dei membri del client; non ci sono membri del client. L'operazione va a buon fine (scopo del test è verificare che, se non ci sono risultati, il server risponda con 200 e array vuoto e non con un errore)
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    When l'utente richiede una operazione di listing dei membri di quel client
    Then si ottiene status code 200 e la lista di 0 utenti
