@m2m-apiv3-producer-keychains
Feature: Gestione dei producer keychains - API v3

  Scenario Outline: [M2M_V3_CREATE_PRODUCER_KEYCHAINS_USERS_ASSOCIATION] Associazione utenze a producer keychain
    Given l'utente è un "admin" di "PA1" con ruolo M2M <m2mRoles>
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    When l'utente associa l'utenza con userId "<userId>" alla producer keychain "<producerKeychainId>"
    Then si ottiene status code <statusCode>

#da implementare -> i 2 status 401
    Examples:
      | userId                               | producerKeychainId | m2mRoles  | statusCode |
      | c7dc1a86-31f6-4fe9-89cd-184201e29d75 | PKCreata           | m2m-admin | 204        |
      | null                                 | PKCreata           | m2m-admin | 400        |
      | c7dc1a86-31f6-4fe9-89cd-184201e29d75 | null               | m2m-admin | 400        |
      | c7dc1a86-31f6-4fe9-89cd-184201e29d75 | PKCNonEsistente    | m2m-admin | 404        |

      #userId valido ma inesistente -> 404
      | 56a84b7b-dce4-4b3f-a8ae-14926c55f02e | PKCreata           | m2m-admin | 404        |

      #userId valido ma appartenente ad un altro tenant -> 404
      | e490f02e-9429-4b38-bb11-ddb8a561fb62 | PKCreata           | m2m-admin | 404        |

      | 17a84b7b-dce6-4b8f-a1ae-85926c55f02e | PKC1               | m2m       | 403        |
