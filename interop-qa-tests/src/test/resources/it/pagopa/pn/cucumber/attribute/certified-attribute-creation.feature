@attribute
Feature: Creazione attributo certificato
  Gli enti certificatori possono creare attributi certificati

  @nrt-minimal
  @certified_attribute_creation1
  Scenario Outline: [ATTRIBUTE_CREATION_1] Un utente admin di un ente certificatore può creare un attributo certificato
    Given l'utente è un "<ruolo>" di "<ente>"
    When l'utente crea un attributo certificato
    Then si ottiene status code <risultato>

    # NOTE 16/04/2025 Nonostante il commento che segue PA1 restituisce esito positivo in ambiente QA
    @happy-path
    Examples:
      | ente    | ruolo        | risultato |
      | PA1     | admin        |       200 |
      | PA2     | admin        |       200 |

    @sad-path
    Examples:
      | ente    | ruolo        | risultato |
      | PA1     | api          |       403 |
      | PA1     | security     |       403 |
      | PA1     | api,security |       403 |
      | PA1     | support      |       403 |
      | PA2     | api          |       403 |
      | PA2     | security     |       403 |
      | PA2     | api,security |       403 |
      | PA2     | support      |       403 |
      | Privato | admin        |       403 |
      | Privato | api          |       403 |
      | Privato | security     |       403 |
      | Privato | api,security |       403 |
      | Privato | support      |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente    | ruolo        | risultato |
      | PA2     | reviewer     |       403 |
      | PA2     | viewer       |       403 |
      | Privato | reviewer     |       403 |
      | Privato | viewer       |       403 |
