@m2m-apiv3-producer-keychains
  #TODO: Check Agid-JWT-Signature, Digest
Feature: Gestione dei producer keychais - API v3

  Scenario Outline: [M2M_V3_CREATE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    Given l'utente è un "admin" di "<tenant>" con ruolo M2M <m2mRoles>
    When l'utente associa l'utenza con userId "<userId>" alla producer keychain "<producerKeychainId>"
    Then si ottiene status code <statusCode>

    #TODO: da implementare -> i 2 status 401
    Examples:
      | tenant | userId  | producerKeychainId | m2mRoles  | statusCode |
      | PA1    | %actual | %actual            | m2m-admin | 204        |
      | PA1    | %null   | %actual            | m2m-admin | 400        |
      | PA1    | %actual | %null              | m2m-admin | 400        |
      | PA1    | %actual | %random            | m2m-admin | 404        |

    #userId valido ma inesistente -> 404
      | PA1    | %random | %actual            | m2m-admin | 404        |

    #userId valido ma appartenente ad un altro tenant -> 404
      | PA2    | %actual | %actual            | m2m-admin | 404        |

    # utente non autorizzato
      | PA1    | %actual | %actual            | m2m       | 403        |

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_KEY_1] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And l'utente associa l'utenza con userId "%actual" alla producer keychain "%actual"
    And si ottiene status code 200
    When l'utente crea una nuova chiave di tipo "<keyType>" all'interno del producer-keychains con:
      | key   | name   | alg   | use   | keychainId |
      | <key> | <name> | <alg> | <use> | %actual    |
    Then si ottiene status code <statusCode>

    Examples:
      | keyType | key    | name   | alg    | use    | statusCode |
      | EC      | %valid | %valid | %valid | %valid | 200        |
      | EC      | %null  | %valid | %valid | %valid | 400        |
      | EC      | %valid | %null  | %valid | %valid | 400        |
      | EC      | %valid | %valid | %null  | %valid | 400        |
      | EC      | %valid | %valid | %valid | %null  | 400        |
      | RSA     | %valid | %valid | %valid | %valid | 200        |
      | RSA     | %null  | %valid | %valid | %valid | 400        |
      | RSA     | %valid | %null  | %valid | %valid | 400        |
      | RSA     | %valid | %valid | %null  | %valid | 400        |
      | RSA     | %valid | %valid | %valid | %null  | 400        |

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_KEY_2] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And l'utente associa l'utenza con userId "%actual" alla producer keychain "%actual"
    And si ottiene status code 200
    Given l'utente è un "admin" di "<tenant>" con ruolo M2M m2m-admin
    When l'utente crea una nuova chiave di tipo "<keyType>" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId   |
      | %valid | %valid | %valid | %valid | <keychainId> |
    Then si ottiene status code <statusCode>

    Examples:
      | keyType | keychainId | tenant | statusCode |
      | EC      | %actual    | PA2    | 404        |
      | EC      | %random    | PA1    | 404        |
      | RSA     | %actual    | PA12   | 404        |
      | RSA     | %random    | PA1    | 404        |

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_KEY_3] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And l'utente associa l'utenza con userId "%actual" alla producer keychain "%actual"
    And si ottiene status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M <m2mRoles>
    When l'utente crea una nuova chiave di tipo "<keyType>" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    Then si ottiene status code <statusCode>

    #TODO: da implementare -> 401, 429
    Examples:
      | keyType | m2mRoles | statusCode |
      | EC      | m2m      | 403        |
      | RSA     | m2m      | 403        |

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_KEY_4] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And l'utente associa l'utenza con userId "%actual" alla producer keychain "%actual"
    And si ottiene status code 200
    When l'utente crea una nuova chiave di tipo "<keyType>" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene status code 200
    And l'utente crea una nuova chiave di tipo "<keyType>" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    Then si ottiene status code 409

    Examples:
      | keyType |
      | EC      |
      | RSA     |

