@m2m-apiv3-users
Feature: Gestione utenti con API M2M V3

  Scenario Outline: [M2M_V3_GET_USERS] Recupero utenti appartenenti al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M <m2mRoles>
    And viene invocata l'API di recupero utenze appartenenti al tenant del richiedente con limit "<limit>" offset "<offset>" e roles "<roles>"
    Then si ottiene status code <statusCode>

    Examples:
      | limit | offset | roles | m2mRoles  | statusCode |
      | 10    | 0      | null  | m2m-admin | 200        |
      | 10    | 0      | admin | m2m-admin | 200        |
      | null  | 0      | null  | m2m-admin | 400        |
      | 10    | null   | null  | m2m-admin | 400        |
      | -1    | null   | null  | m2m-admin | 400        |
      | 51    | null   | null  | m2m-admin | 400        |
      | null  | -1     | null  | m2m-admin | 400        |
      | null  | -1     | null  | m2m       | 403        |
    #da implementare -> i 2 status 401

  Scenario: [M2M_V3_GET_USERS] Verifica che gli utenti attivi restituiti appartengano al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene invocata l'API di recupero utenze appartenenti al tenant del richiedente con limit "10" offset "0" e roles "null"
    Then si ottiene status code 200
    When viene invocata l'API di recupero utenze per l'istituzione: "PA1"
    Then si verifica che la chiamata a selfcare abbia ritornato uno status code: 200
    And si verifica che le liste di utenze restituite coincidano



