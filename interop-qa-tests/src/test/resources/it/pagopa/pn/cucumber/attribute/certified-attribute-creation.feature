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


  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario Outline: [DISCRETE_ATTRIBUTE_CREATION_1] Verifica dell'esito della creazione di un attributo discreto in base
  all'ente e al ruolo dell'utente.

    Given l'utente è un "<ruolo>" di "<ente>"
    When l'utente crea un attributo certificato discreto
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo | risultato |
      | GSP  | admin | 200       |

    @sad-path
    Examples:
      | ente    | ruolo        | risultato |
      | GSP     | api          | 403       |
      | GSP     | security     | 403       |
      | GSP     | api,security | 403       |
      | GSP     | reviewer     | 403       |
      | GSP     | viewer       | 403       |
      | Privato | admin        | 403       |

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [DISCRETE_ATTRIBUTE_CREATION_2] La creazione di un attributo certificato discreto non va a buon fine se ce n'è
  già uno con lo stesso nome.

    Given l'utente è un "admin" di "PA1"
    When l'utente tenta di creare due attributi certificati discreti con lo stesso nome
    Then si ottiene status code 409

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [DISCRETE_ATTRIBUTE_CREATION_3] La creazione di un attributo certificato discreto va a buon fine anche se esiste
  un attributo certificato con lo stesso nome.

    Given l'utente è un "admin" di "PA1"
    And l'utente crea un attributo certificato
    When l'utente crea un attributo certificato discreto utilizzando lo stesso nome dell'ultimo attributo certificato creato
    Then si ottiene status code 200
