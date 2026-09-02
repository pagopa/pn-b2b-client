Feature: Gestione degli attributi certificati discreti degli e-service template attraverso APIs M2M V3

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_1] Il recupero degli attributi certificati discreti assegnati al template e-service va a buon fine.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    Then la configurazione degli attributi certificati discreti del template e-service corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | PA1     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_2] Il recupero degli attributi certificati discreti assegnati al template e-service non va a buon fine se l'ID dell'e-service non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del template e-service specificando un ID invalido per il template
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_3] Il recupero degli attributi certificati discreti assegnati al template e-service non va a buon fine se l'ID dell'e-service non esiste.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente tenta di recuperare gli attributi certificati discreti del template e-service specificando un ID inesistente per il template
    Then si ottiene lo status code 400

  Scenario: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_GET_4] Il recupero degli attributi certificati discreti assegnati al template e-service non va a buon fine se il token di autenticazione non è valido.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente crea e aggiunge i seguenti attributi all'e-service template creato:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di recuperare gli attributi certificati discreti del template e-service
    Then si ottiene lo status code 403

