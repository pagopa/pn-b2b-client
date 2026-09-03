Feature: Gestione degli attributi certificati discreti attraverso APIs M2M V3

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_1] La creazione di un attributo certificato discreto va a buon fine se l'utente è un admin di un ente certificatore e può essere consultato anche da enti non certificatori.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto
      | name | description | code |
      |      |             |      |
    And si ottiene lo status code 201
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di recuperare il record di certifiedDiscreteAttribute creato
    And si ottiene lo status code 200
    Then certifiedDiscreteAttribute viene restituito e combacia con il record creato

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | GSP     | admin | m2m-admin |
      | GSP     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_2] Il tentativo di recupero di un attributo certificato discreto con UUID non valido non va a buon fine.
    Given l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    When l'utente tenta di recuperare certifiedDiscreteAttribute con un id invalido
    Then si ottiene lo status code 400
    And certifiedDiscreteAttribute non restituito

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | GSP     | admin | m2m-admin |
      | GSP     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_FUNC_READ_1] Il tentativo di recupero di un attributo certificato discreto con UUID non valido non va a buon fine.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo dichiarato
      | name | description | code |
      |      |             |      |
    When l'utente tenta di recuperare un attributo certificato discreto con un l'id dell'attributo dichiarato creato, senza ottenere alcun risultato
    Then si ottiene lo status code 404

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_FUNC_READ_2] Il tentativo di recupero di un attributo certificato discreto creato da un altro ente certificatore va a buon fine.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo dichiarato
      | name | description | code |
      |      |             |      |
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di recuperare il record di certifiedDiscreteAttribute creato
    Then certifiedDiscreteAttribute viene restituito e combacia con il record creato

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_3] Accesso negato al dettaglio di un attributo certificato discreto con token non valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare il record di certifiedDiscreteAttribute creato
    Then si ottiene lo status code 401
    And certifiedDiscreteAttribute non restituito

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_4] Il tentativo di recupero di un attributo certificato discreto con UUID inesistente non va a buon fine.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di recuperare certifiedDiscreteAttribute con un id inesistente
    Then si ottiene lo status code 400
    And certifiedDiscreteAttribute non restituito

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_LIST_1] Il recupero degli attributi certificati discreti creati va a buon fine.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di recuperare la pagina 1 della lista di certifiedDiscreteAttribute con un limite di 30 elementi
    Then si ottiene lo status code 200
    And la risposta contiene esattamente i 3 attributi certificati discreti creati

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | GSP     | admin | m2m-admin |
      | GSP     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_LIST_2] La richiesta dell'elenco degli attributi certificati discreti non va a buon fine se i parametri utilizzati non sono validi.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    And l'utente tenta di recuperare la pagina -1 della lista di certifiedDiscreteAttribute con un limite di -10 elementi
    Then si ottiene lo status code 400

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | GSP     | admin | m2m-admin |
      | GSP     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_LIST_3] Accesso negato all'elenco degli attributi certificati discreti con token non valido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare la pagina 1 della lista di certifiedDiscreteAttribute con un limite di 10 elementi
    Then si ottiene lo status code 401

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_CREATE_1] La creazione di un attributo certificato discreto non va a buon fine se uno dei suoi attributi non è valido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto
      | name   | description   | code   |
      | <name> | <description> | <code> |
    And si ottiene lo status code 400

    Examples:
      | name       | description | code      |
      | $SIZE(501) |             |           |
      | $EMPTY()   |             |           |
      |            | $SIZE(501)  |           |
      |            | $EMPTY()    |           |
      |            |             | $SIZE(65) |
      |            |             | $EMPTY()  |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_CREATE_2] La creazione di un attributo certificato discreto non va a buon fine se la richiesta http non è valida.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    When si tenta la creazione dell'attributo certificato discreto senza passare parametri nella richiesta
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_CREATE_3] La creazione di un attributo certificato discreto non va a buon fine con token non valido.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When viene effettuata la creazione dell'attributo certificato discreto
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code 401

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_CREATE_4] La creazione di un attributo certificato discreto non va a buon fine se ce n'è già uno con lo stesso nome.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name            | description | code |
      | ATTR-DISCRETE-1 |             |      |
    When viene effettuata la creazione dell'attributo certificato discreto
      | name            | description | code |
      | ATTR-DISCRETE-1 |             |      |
    Then si ottiene lo status code 409

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_CREATE_5] La creazione di un attributo certificato discreto non è consentita alle utenze che non sono admin o che non appartengano agli enti certificatori.
    Given l'utente è un "admin" di "<ente>" con ruolo M2M <ruolo-m2m>
    And viene effettuata la creazione dell'attributo certificato discreto
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code <risultato>

    Examples:
      | ente    | ruolo-m2m | risultato |
      | GSP     | m2m-admin | 201       |
      | GSP     | m2m       | 403       |
      | Privato | m2m-admin | 403       |
      | Privato | m2m       | 403       |
