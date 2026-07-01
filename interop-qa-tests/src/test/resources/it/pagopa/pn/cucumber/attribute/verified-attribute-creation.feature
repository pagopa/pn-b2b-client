@attribute
Feature: Creazione attributo verificato
  Gli admin e gli operatori API di enti PA e GSP possono creare attributi verificati

  @nrt-minimal
  @verified_attribute_creation1
  Scenario Outline: [VERIFIED_ATTRIBUTE_CREATION_1] Un utente con sufficienti permessi di un ente autorizzato crea un attributo verificato
    Given l'utente è un "<ruolo>" di "<ente>"
    When l'utente crea un attributo verificato
    Then si ottiene status code <risultato>

    @happy-path
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
