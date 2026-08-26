Feature: Gestione degli attributi certificati discreti attraverso APIs M2M V3

  # CASO DI TEST 1.1

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_1] La creazione di un attributo certificato discreto va a buon fine se l'utente è un admin di un ente certificatore.

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

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_2] Il tentativo di recupero di un attributo certificato discreto con UUID non valido non va a buon fine.

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di recuperare certifiedDiscreteAttribute con un id invalido
    Then si ottiene lo status code 400
    And certifiedDiscreteAttribute non restituito

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_3] Accesso negato al dettaglio di un attributo certificato discreto con token non valido.

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare il record di certifiedDiscreteAttribute creato
    Then si ottiene lo status code 401
    And certifiedDiscreteAttribute non restituito

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_4] Il tentativo di recupero di un attributo certificato discreto con UUID inesistente non va a buon fine.

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di recuperare certifiedDiscreteAttribute con un id inesistente
    Then si ottiene lo status code 400
    And certifiedDiscreteAttribute non restituito


  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_5] La creazione di un attributo certificato discreto non va a buon fine se la richiesta http non è valida.
    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    When si tenta la creazione dell'attributo certificato discreto senza passare parametri nella richiesta
    Then si ottiene lo status code 400

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_6] La creazione di un attributo certificato discreto non va a buon fine se uno dei suoi attributi non è valido.

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

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_7] La creazione di un attributo certificato discreto non va a buon fine se ce n'è già uno con lo stesso nome.

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato discreto con successo
      | name            | description | code |
      | ATTR-DISCRETE-1 |             |      |
    When viene effettuata la creazione dell'attributo certificato discreto
      | name            | description | code |
      | ATTR-DISCRETE-1 |             |      |
    Then si ottiene lo status code 400

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_8] La creazione di un attributo certificato discreto non è consentita alle utenze che non sono admin o che non appartengano agli enti certificatori.

    Given l'utente è un "admin" di "<ente>" con ruolo M2M <ruolo-m2m>
    And viene effettuata la creazione dell'attributo certificato discreto
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code <risultato>

    Examples:
      | ente    | ruolo-m2m | risultato |
      | PA1     | m2m-admin | 201       |
      | PA1     | m2m       | 403       |
      | Privato | m2m-admin | 403       |
      | Privato | m2m       | 403       |
