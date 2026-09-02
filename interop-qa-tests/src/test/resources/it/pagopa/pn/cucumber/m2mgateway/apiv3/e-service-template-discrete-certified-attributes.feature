Feature: Gestione degli attributi certificati discreti degli e-service template attraverso APIs M2M V3

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_ESERVICE_TEMPLATE_CREATE_1] Il recupero degli attributi certificati discreti assegnati al template e-service va a buon fine.
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    And l'utente aggiunge i seguenti attributi all'e-service template creato:
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
