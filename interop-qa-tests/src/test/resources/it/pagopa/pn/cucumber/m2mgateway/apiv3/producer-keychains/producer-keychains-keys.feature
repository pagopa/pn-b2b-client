@m2m-apiv3-producer-keychains @m2m-v3-204-to-200
Feature: Gestione dei producer keychains - API v3

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_KEY_1] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
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

  @m2m-v3-204-to-200
  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_KEY_2] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "<tenant>" con ruolo M2M m2m-admin
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId   |
      | %valid | %valid | %valid | %valid | <keychainId> |
    Then si ottiene response status code <statusCode>

    Examples:
      | keychainId | tenant | statusCode |
      | %actual    | PA2    | 403        |
      | %random    | PA1    | 404        |

  Scenario: [CREATE_PRODUCER_KEYCHAINS_KEY_3] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    Then si ottiene response status code 403

  Scenario: [CREATE_PRODUCER_KEYCHAINS_KEY_4] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene response status code 200
    And l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key     | name    | alg     | use     | keychainId |
      | %actual | %actual | %actual | %actual | %actual    |
    Then si ottiene response status code 409

  Scenario: [CREATE_PRODUCER_KEYCHAINS_KEY_5] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    Then si ottiene response status code 401

  Scenario: [CREATE_PRODUCER_KEYCHAINS_KEY_6] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header di autenticazione DPoP
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    Then si ottiene response status code 400

  Scenario: [CREATE_PRODUCER_KEYCHAINS_KEY_7] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header DPoP proof
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    Then si ottiene response status code 400

  Scenario Outline: [GET_PRODUCER_KEY_1] Recupero della chiave pubblica di uno specifico portachiavi erogatore tramite il suo Key ID (kid)
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1"
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then viene recuperata la producer-key con kid "<kid>"
    And si ottiene response status code <statusCode>

    Examples:
      | kid     | statusCode |
     # Happy path
      | %actual | 200        |

    # Kid invalido
      | %random | 404        |
      | %null   | 400        |

  Scenario: [GET_PRODUCER_KEY_2] Recupero della chiave pubblica di uno specifico portachiavi erogatore tramite il suo Key ID (kid)
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1"
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    Then viene recuperata la producer-key con kid "%actual"
    And si ottiene response status code 401

  Scenario: [GET_PRODUCER_KEY_3] Recupero della chiave pubblica di uno specifico portachiavi erogatore tramite il suo Key ID (kid)
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1"
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header di autenticazione DPoP
    Then viene recuperata la producer-key con kid "%actual"
    And si ottiene response status code 400

  Scenario: [GET_PRODUCER_KEY_4] Recupero della chiave pubblica di uno specifico portachiavi erogatore tramite il suo Key ID (kid)
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1"
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header DPoP proof
    Then viene recuperata la producer-key con kid "%actual"
    And si ottiene response status code 400

  Scenario Outline: [DELETE_PRODUCER_KEY_1] Recupero della chiave pubblica di uno specifico portachiavi erogatore tramite il suo Key ID (kid)
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene response status code 200
    Given l'utente è un "admin" di "<tenant>" con ruolo M2M m2m-admin
    Then viene eliminata la producer-key con keychainId "<keychainId>", kid "<kid>"
    And si ottiene http status code <statusCode>

    Examples:
      | kid     | keychainId | tenant | statusCode |
    # Happy path
      | %actual | %actual    | PA1    | 204        |

    # Tenant richiedente non associato al keychain
      | %actual | %actual    | PA2    | 403        |

     # Kid/Keychain invalido
      | %random | %actual    | PA1    | 404        |
      | %null   | %actual    | PA1    | 400        |
      | %actual | %random    | PA1    | 404        |
      | %actual | %null      | PA1    | 400        |

  Scenario: [DELETE_PRODUCER_KEY_2] Recupero della chiave pubblica di uno specifico portachiavi erogatore tramite il suo Key ID (kid)
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    Then viene eliminata la producer-key con keychainId "%actual", kid "%actual"
    And si ottiene response status code 401

  Scenario: [DELETE_PRODUCER_KEY_3] Recupero della chiave pubblica di uno specifico portachiavi erogatore tramite il suo Key ID (kid)
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header di autenticazione DPoP
    Then viene eliminata la producer-key con keychainId "%actual", kid "%actual"
    And si ottiene response status code 400

  Scenario: [DELETE_PRODUCER_KEY_4] Recupero della chiave pubblica di uno specifico portachiavi erogatore tramite il suo Key ID (kid)
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header DPoP proof
    Then viene eliminata la producer-key con keychainId "%actual", kid "%actual"
    And si ottiene response status code 400

  Scenario Outline: [M2M_V3_CREATE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_1] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "<tenant>" con ruolo M2M <m2mRoles>
    When viene associato l'utente "<userId>" alla producer keychain "<producerKeychainId>"
    Then si ottiene response status code <statusCode>

    Examples:
      | tenant | userId                               | producerKeychainId | m2mRoles  | statusCode |
      | PA1    | %actual                              | %actual            | m2m-admin | 200        |
      | PA1    | %null                                | %actual            | m2m-admin | 400        |
      | PA1    | %actual                              | %null              | m2m-admin | 400        |
      | PA1    | %actual                              | %random            | m2m-admin | 404        |

    #userId valido ma inesistente -> 404
      | PA1    | %random                              | %actual            | m2m-admin | 404        |

    #producerKeychainId appartenente ad un tenant differente da quello del chiamante
      | PA2    | %actual                              | %actual            | m2m-admin | 404        |
    #userId appartenente ad un tenant differente da quello in cui è presente il producerKeychain
      | PA1    | c27e3508-3d26-4b6b-9c73-54cb38e6fe1b | %actual            | m2m-admin | 404        |

    # utente non autorizzato
      | PA1    | %actual                              | %actual            | m2m       | 403        |

  Scenario: [M2M_V3_CREATE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_2] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When viene associato l'utente "%actual" alla producer keychain "%actual"
    Then si ottiene response status code 401

  Scenario: [M2M_V3_CREATE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_3] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header di autenticazione DPoP
    When viene associato l'utente "%actual" alla producer keychain "%actual"
    Then si ottiene response status code 400

  Scenario: [M2M_V3_CREATE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_4] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header DPoP proof
    When viene associato l'utente "%actual" alla producer keychain "%actual"
    Then si ottiene response status code 400

  Scenario Outline: [M2M_V3_GET_PRODUCER_KEYCHAINS_USERS_1] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "<tenant>" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    When viene invocata l'API di recupero utenze associate alla producer keychain "<producerKeychainId>" con limit "<limit>" offset "<offset>"
    Then si ottiene response status code <statusCode>

    Examples:
      | tenant | producerKeychainId | limit | offset | statusCode |
      | PA1    | %actual            | 10    | 0      | 200        |
      | PA1    | %null              | 10    | 0      | 400        |
      | PA1    | %actual            | %null | 0      | 400        |
      | PA1    | %actual            | -1    | 0      | 400        |
      | PA1    | %actual            | 51    | 0      | 400        |
      | PA1    | %actual            | 10    | %null  | 400        |
      | PA1    | %actual            | 10    | -1     | 400        |
      | PA1    | %random            | 10    | 0      | 404        |
      | PA2    | %actual            | 10    | 0      | 403        |

  Scenario: [M2M_V3_GET_PRODUCER_KEYCHAINS_USERS_2] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When viene invocata l'API di recupero utenze associate alla producer keychain "%actual" con limit "10" offset "0"
    Then si ottiene response status code 401

  Scenario: [M2M_V3_GET_PRODUCER_KEYCHAINS_USERS_3] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header di autenticazione DPoP
    When viene invocata l'API di recupero utenze associate alla producer keychain "%actual" con limit "10" offset "0"
    Then si ottiene response status code 400

  Scenario: [M2M_V3_GET_PRODUCER_KEYCHAINS_USERS_4] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header DPoP proof
    When viene invocata l'API di recupero utenze associate alla producer keychain "%actual" con limit "10" offset "0"
    Then si ottiene response status code 400


  Scenario: [M2M_V3_GET_PRODUCER_KEYCHAINS_USERS_VERIFICATION] Verifica che gli utenti restituiti, associati al producer keychain specificato, appartengano al tenant del richiedente
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    When viene invocata l'API di recupero utenze associate alla producer keychain "%actual" con limit "10" offset "0"
    Then si ottiene response status code 200
    And l'utente è un "admin" di "PA1"
    When viene invocata l'API di recupero utenze per l'istituzione: "PA1"
    Then si verifica che la chiamata a selfcare abbia ritornato uno status code: 200
    And si verifica che le utenze recuperate siano presenti nella lista di utenti appartenenti al tenant del chiamante

  @m2m-v3-204-to-200
  Scenario Outline: [M2M_V3_DELETE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_1] Eliminazione associazione tra utenza e producer keychain specificati
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene status code 200
    Given l'utente è un "admin" di "<tenant>" con ruolo M2M <m2mRoles>
    When l'utente elimina l'associazione tra l'utenza con userId "<userId>" e la producer keychain "<producerKeychainId>"
    Then si ottiene response status code <statusCode>

    Examples:
      | tenant | userId                               | producerKeychainId | m2mRoles  | statusCode |
      | PA1    | %actual                              | %actual            | m2m-admin | 200        |
      | PA1    | %actual                              | %null              | m2m-admin | 400        |
      | PA1    | %null                                | %actual            | m2m-admin | 400        |

      #userId valido ma inesistente -> 404
      | PA1    | %random                              | %actual            | m2m-admin | 404        |
      #producerKeychainId valido ma inesistente -> 404
      | PA1    | %actual                              | %random            | m2m-admin | 404        |

       #producerKeychainId appartenente ad un tenant differente da quello del chiamante
      | PA2    | %actual                              | %actual            | m2m-admin | 404        |
      #userId appartenente ad un tenant differente da quello in cui è presente il producerKeychain
      | PA1    | c27e3508-3d26-4b6b-9c73-54cb38e6fe1b | %actual            | m2m-admin | 404        |

      | PA1    | %actual                              | %actual            | m2m       | 403        |

  Scenario: [M2M_V3_DELETE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_2] Eliminazione associazione tra utenza e producer keychain specificati
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente elimina l'associazione tra l'utenza con userId "%actual" e la producer keychain "%actual"
    Then si ottiene response status code 401

  Scenario: [M2M_V3_DELETE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_3] Eliminazione associazione tra utenza e producer keychain specificati
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header di autenticazione DPoP
    When l'utente elimina l'associazione tra l'utenza con userId "%actual" e la producer keychain "%actual"
    Then si ottiene response status code 400

  Scenario: [M2M_V3_DELETE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_4] Eliminazione associazione tra utenza e producer keychain specificati
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And si ottiene response status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene response status code 200
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene rimosso l'header DPoP proof
    When l'utente elimina l'associazione tra l'utenza con userId "%actual" e la producer keychain "%actual"
    Then si ottiene response status code 400
