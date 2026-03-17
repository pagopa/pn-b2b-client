@m2m-apiv3-client-keychains
Feature: Gestione dei client keychains - API v3

  Scenario Outline: [CREATE_CLIENT_KEYCHAINS_KEY_1] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente richiede l'aggiunta di un admin di "PA1" al client
    And si ottiene response status code 200
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del client-keychains con:
      | key   | name   | alg   | use   | keychainId |
      | <key> | <name> | <alg> | <use> | %actual    |
    Then si ottiene response status code <statusCode>

    Examples:
      | key    | name   | alg    | use    | statusCode |
      | %valid | %valid | %valid | %valid | 200        |
      | %null  | %valid | %valid | %valid | 400        |
      | %valid | %null  | %valid | %valid | 400        |
      | %valid | %valid | %null  | %valid | 400        |
      | %valid | %valid | %valid | %null  | 400        |