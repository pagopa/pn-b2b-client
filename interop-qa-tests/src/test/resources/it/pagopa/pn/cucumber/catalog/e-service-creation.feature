@eservice @eservice_creation
Feature: Creazione e-service
  Gli admin e gli operatori API di enti PA e GSP possono creare e-service

  @happy-path
  @nrt-minimal
  @eservice_creation1
  Scenario Outline: [ESERVICE_CREATION_1] Un utente con sufficienti permessi di un ente autorizzato crea un e-service
    Given l'utente è un "<ruolo>" di "<ente>"
    When l'utente crea un e-service
    Then si ottiene status code <risultato>

    Examples: 
      | ente    | ruolo        | risultato |
      | GSP     | admin        |       200 |
      | GSP     | api          |       200 |
      | GSP     | api,security |       200 |
      | PA1     | admin        |       200 |
      | PA1     | api          |       200 |
      | PA1     | api,security |       200 |
      | Privato | admin        |       200 |
      | Privato | api          |       200 |
      | Privato | api,security |       200 |

    @sad-path
    Examples:
      | ente    | ruolo        | risultato |
      | GSP     | security     |       403 |
      | GSP     | support      |       403 |
      | Privato | security     |       403 |
      | Privato | support      |       403 |
      | PA1     | security     |       403 |
      | PA1     | support      |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente    | ruolo        | risultato |
      | GSP     | reviewer     |       403 |
      | GSP     | viewer       |       403 |
      | Privato | reviewer     |       403 |
      | Privato | viewer       |       403 |
      | PA2     | reviewer     |       403 |
      | PA2     | viewer       |       403 |

  @sad-path
  @nrt-minimal
  @eservice_creation2
  Scenario: [ESERVICE_CREATION_2] Un utente autorizzato vuole creare due e-service con lo stesso nome
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già creato un e-service contenente anche il primo descrittore
    When l'utente crea un e-service con lo stesso nome
    Then si ottiene status code 409

  @happy-path
  @eservice_description_max_length
  Scenario: [ESERVICE_CREATION_DESCRIPTION_MAX_LENGTH_1] Un utente crea un e-service utilizzando la descrizione della lunghezza massima possibile
    Given l'utente è un "admin" di "PA1"
    When l'utente crea un e-service con una descrizione di 400 caratteri
    Then si ottiene status code 200
    And l'e-service creato ha una descrizione di 400 caratteri

  @sad-path
  @eservice_description_max_length
  Scenario: [ESERVICE_CREATION_DESCRIPTION_MAX_LENGTH_2] La creazione dell'e-service non va a buon fine se viene superata la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1"
    When l'utente crea un e-service con una descrizione di 401 caratteri
    Then si ottiene status code 400
