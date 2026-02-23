@m2m-apiv3-users
Feature: Gestione utenti con API M2M V3

  Scenario Outline: [M2M_V3_GET_USERS_1] Recupero utenti appartenenti al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M <m2mRoles>
    And viene invocata l'API di recupero utenze appartenenti al tenant del richiedente con limit "<limit>" offset "<offset>" e roles "<roles>"
    Then si ottiene response status code <statusCode>

    Examples:
      | limit | offset | roles | m2mRoles  | statusCode |
      | 10    | 0      | %null | m2m-admin | 200        |
      | 10    | 0      | admin | m2m-admin | 200        |
      | %null | 0      | %null | m2m-admin | 400        |
      | 10    | %null  | %null | m2m-admin | 400        |
      | -1    | %null  | %null | m2m-admin | 400        |
      | 51    | %null  | %null | m2m-admin | 400        |
      | %null | -1     | %null | m2m-admin | 400        |
      | 10    | 0      | %null | m2m       | 403        |
    #da implementare -> i 2 status 401

  Scenario: [M2M_V3_GET_USERS_2] Verifica che gli utenti attivi restituiti appartengano al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene invocata l'API di recupero utenze appartenenti al tenant del richiedente con limit "50" offset "0" e roles "%null"
    Then si ottiene response status code 200
    Given l'utente è un "admin" di "PA1"
    When viene invocata l'API di recupero utenze per l'istituzione: "PA1"
    Then si verifica che la chiamata a selfcare abbia ritornato uno status code: 200
    And si verifica che le liste di utenze restituite coincidano


  Scenario: [M2M_V3_GET_USERS_3] Recupero utenti appartenenti al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    And viene invocata l'API di recupero utenze appartenenti al tenant del richiedente con limit "10" offset "0" e roles "%null"
    Then si ottiene response status code 401

  Scenario: [M2M_V3_GET_USERS_4] Recupero utenti appartenenti al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header Authorization con schema DPoP
    And viene invocata l'API di recupero utenze appartenenti al tenant del richiedente con limit "10" offset "0" e roles "%null"
    Then si ottiene response status code 400

  Scenario: [M2M_V3_GET_USERS_5] Recupero utenti appartenenti al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And E viene rimosso l'header DPoP contenente il proof
    And viene invocata l'API di recupero utenze appartenenti al tenant del richiedente con limit "10" offset "0" e roles "%null"
    Then si ottiene response status code 400

  Scenario Outline: [M2M_V3_GET_USER] Recupero utente specifico purchè appartenente al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M <m2mRoles>
    When viene invocata l'API per il recupero dell'utente "<userId>" purchè appartenente al tenant del richiedente
    Then si ottiene response status code <statusCode>

    Examples:
      | userId                               | m2mRoles  | statusCode |
      | %actual                              | m2m-admin | 200        |
      | %null                                | m2m-admin | 400        |
      #userId valido ma non presente in db
      | %random                              | m2m-admin | 404        |
      #userId appartenete ad un tenant differente
      | c27e3508-3d26-4b6b-9c73-54cb38e6fe1b | m2m-admin | 404        |

      | %actual                              | m2m       | 403        |
    #da implementare -> i 2 status 401

  Scenario: [M2M_V3_GET_USER_2] Recupero utenti appartenenti al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When viene invocata l'API per il recupero dell'utente "%actual" purchè appartenente al tenant del richiedente
    Then si ottiene response status code 401

  Scenario: [M2M_V3_GET_USER_3] Recupero utenti appartenenti al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header Authorization con schema DPoP
    When viene invocata l'API per il recupero dell'utente "%actual" purchè appartenente al tenant del richiedente
    Then si ottiene response status code 400

  Scenario: [M2M_V3_GET_USER_4] Recupero utenti appartenenti al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And E viene rimosso l'header DPoP contenente il proof
    When viene invocata l'API per il recupero dell'utente "%actual" purchè appartenente al tenant del richiedente
    Then si ottiene response status code 400


