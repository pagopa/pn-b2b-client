Feature: Gestione degli attributi certificati discreti degli e-services attraverso APIs M2M V3

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_1] Il recupero degli attributi certificati discreti assegnati all'e-service va a buon fine.

    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    Then la configurazione degli attributi certificati discreti del descrittore dell'eservice corrisponde a quella attesa

    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | PA1     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_GET_2] Il recupero degli attributi certificati discreti assegnati all'e-service non va a buon fine se l'ID dell'e-service non è valido.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente aggiunge i seguenti attributi al descrittore dell'e-service:
      | group | kind               | code  | comparator | value |
      | 0     | CERTIFIED_DISCRETE | CD001 | LTE        | 10    |
    When l'utente è un "<ruolo>" di "<ente>" con ruolo M2M <ruoloM2M>
    Then l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'eservice specificando un ID invalido per l'e-service
    And si ottiene lo status code 400
  
    Examples:
      | ente    | ruolo | ruoloM2M  |
      | PA1     | admin | m2m-admin |
      | PA1     | admin | m2m       |
      | Privato | admin | m2m-admin |
      | Privato | admin | m2m       |
