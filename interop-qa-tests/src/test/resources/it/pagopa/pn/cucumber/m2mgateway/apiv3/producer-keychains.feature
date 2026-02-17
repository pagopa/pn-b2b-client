@m2m-apiv3-producer-keychains
  #TODO: Check Agid-JWT-Signature, Digest
Feature: Gestione dei producer keychains - API v3

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_KEY_1] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And viene associato l'utente "%actual" alla producer keychain "%actual"
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
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene status code 200
    Given l'utente è un "admin" di "<tenant>" con ruolo M2M m2m-admin
    When l'utente crea una nuova chiave di tipo "<keyType>" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId   |
      | %valid | %valid | %valid | %valid | <keychainId> |
    Then si ottiene status code <statusCode>

    Examples:
      | keyType | keychainId | tenant | statusCode |
      | EC      | %actual    | PA2    | 404        |
      | EC      | %random    | PA1    | 400        |
      | RSA     | %actual    | PA2    | 404        |
      | RSA     | %random    | PA1    | 400        |

  Scenario Outline: [CREATE_PRODUCER_KEYCHAINS_KEY_3] Creazione nuova chiave pubblica all’interno di uno specifico portachiavi erogatore
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And viene associato l'utente "%actual" alla producer keychain "%actual"
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
    And viene associato l'utente "%actual" alla producer keychain "%actual"
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

  Scenario Outline: [GET_PRODUCER_KEY] Recupero della chiave pubblica di uno specifico portachiavi erogatore tramite il suo Key ID (kid)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene status code 200
    When l'utente crea una nuova chiave di tipo "<keyType>" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene status code 200
    Then viene recuperata la producer-key con kid "<kid>"
    And si ottiene status code <statusCode>

    #TODO: da implementare -> 401, 429
    Examples:
    # Happy path (EC)
      | keyType | kid         | statusCode |
      | EC      | %actual     | 200        |

    # Kid invalido (EC)
      | EC      | %random     | 404        |
      | EC      | invalid-kid | 400        |

     # Happy path (RSA)
      | RSA     | %actual     | 200        |

    # Kid invalido (RSA)
      | RSA     | %random     | 404        |
      | RSA     | invalid-kid | 400        |

  Scenario Outline: [DELETE_PRODUCER_KEY] Recupero della chiave pubblica di uno specifico portachiavi erogatore tramite il suo Key ID (kid)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene status code 200
    When l'utente crea una nuova chiave di tipo "<keyType>" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And si ottiene status code 200
    Given l'utente è un "admin" di "<tenant>" con ruolo M2M m2m-admin
    Then viene eliminata la producer-key con keychainId "<keychainId>", kid "<kid>"
    And si ottiene status code <statusCode>

    #TODO: da implementare -> 401, 429
    Examples:
    # Happy path (EC)
      | keyType | kid         | keychainId | tenant | statusCode |
      | EC      | %actual     | %actual    | PA1    | 204        |

    # Tenant richiedente non associato al keychain (EC)
      | EC      | %actual     | %actual    | PA2    | 404        |

    # Kid/Keychain invalido (EC)
      | EC      | %random     | %actual    | PA1    | 404        |
      | EC      | invalid-kid | %actual    | PA1    | 400        |
      | EC      | %actual     | %random    | PA1    | 400        |
      | EC      | %actual     | %null      | PA1    | 400        |

    # Happy path (RSA)
      | RSA     | %actual     | %actual    | PA1    | 204        |

    # Tenant richiedente non associato al keychain (RSA)
      | RSA     | %actual     | %actual    | PA2    | 404        |


     # Kid/Keychain invalido (RSA)
      | RSA     | %random     | %actual    | PA1    | 404        |
      | RSA     | invalid-kid | %actual    | PA1    | 400        |
      | RSA     | %actual     | %random    | PA1    | 400        |
      | RSA     | %actual     | %null      | PA1    | 400        |

  Scenario Outline: [M2M_V3_CREATE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_1] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    Given l'utente è un "admin" di "<tenant>" con ruolo M2M <m2mRoles>
    When viene associato l'utente "<userId>" alla producer keychain "<producerKeychainId>"
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


  Scenario: [M2M_V3_CREATE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_2] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene status code 200
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene status code 404

  Scenario Outline: [M2M_V3_GET_PRODUCER_KEYCHAINS_USERS] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    When viene invocata l'API di recupero utenze associate alla producer keychain "<producerKeychainId>" con limit "<limit>" offset "<offset>"
    Then si ottiene status code <statusCode>

    #da implementare -> i 2 status 401
    Examples:
      | producerKeychainId | limit | offset | statusCode |
      | %actual            | 10    | 0      | 200        |
      | %null              | 10    | 0      | 400        |
      | %actual            | %null | 0      | 400        |
      | %actual            | -1    | 0      | 400        |
      | %actual            | 51    | 0      | 400        |
      | %actual            | 10    | %null  | 400        |
      | %actual            | 10    | -1     | 400        |
      | %random            | 10    | 0      | 404        |

  Scenario: [M2M_V3_GET_PRODUCER_KEYCHAINS_USERS_VERIFICATION] Verifica che gli utenti restituiti, associati al producer keychain specificato, appartengano al tenant del richiedente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    When viene invocata l'API di recupero utenze associate alla producer keychain "%actual" con limit "10" offset "0"
    Then si ottiene status code 200
    When viene invocata l'API di recupero utenze per l'istituzione: "PA1"
    Then si verifica che la chiamata a selfcare abbia ritornato uno status code: 200
    And si verifica che le utenze recuperate siano presenti nella lista di utenti appartenenti al tenant del chiamante

  Scenario Outline: [M2M_V3_DELETE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_1] Eliminazione associazione tra utenza e producer keychain specificati
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    Given l'utente è un "admin" di "<tenant>" con ruolo M2M <m2mRoles>
    When l'utente elimina l'associazione tra l'utenza con userId "<userId>" e la producer keychain "<producerKeychainId>"
    Then si ottiene status code <statusCode>

    #da implementare -> i 2 status 401
    Examples:
      | tenant | userId  | producerKeychainId | m2mRoles  | statusCode |
      | PA1    | %actual | %actual            | m2m-admin | 204        |
      | PA1    | %actual | %null              | m2m-admin | 400        |
      | PA1    | %null   | %actual            | m2m-admin | 400        |

      #userId valido ma inesistente -> 404
      | PA1    | %random | %actual            | m2m-admin | 404        |
      #producerKeychainId valido ma inesistente -> 404
      | PA1    | %actual | %random            | m2m-admin | 404        |

      #userId valido ma appartenente ad un altro tenant -> 404
      | PA2    | %actual | %actual            | m2m-admin | 404        |

      | PA1    | %actual | %actual            | m2m       | 403        |

  Scenario : [M2M_V3_DELETE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION_2] Eliminazione associazione tra utenza e producer keychain specificati
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And si ottiene status code 200
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente elimina l'associazione tra l'utenza con userId "%actual" e la producer keychain "%actual"
    Then si ottiene status code 404