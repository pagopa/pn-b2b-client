@m2mEservices
Feature: Gestione degli eServices attraverso APIs M2M

  @happy-path
  Scenario Outline: [M2MG_ESERVICES_1] RED - La lista degli eServices può essere visionata da un utente con ruolo M2M o M2M-ADMIN (Scenario 4)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di eService
    And si ottiene lo status code 200
    Then la lista di eService è presente solo se lo status code è 200
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @happy-path
  Scenario Outline: [M2MG_ESERVICES_2] RED - Recupero corretto della lista degli eServices con utente autorizzato (Scenario 81)
    Given "PA2" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA2" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di eService
    And si ottiene lo status code 200
    Then lista di eService viene restituita
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @sad-path
  Scenario: [M2MG_ESERVICES_3] RED - Accesso negato alla lista degli eServices con token non valido (Scenario 82)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare la lista di eService
    And si ottiene lo status code 401
    Then lista di eService non restituita

  @happy-path
  Scenario Outline: [M2MG_ESERVICES_4] Un utente con ruolo M2M o M2M-ADMIN può visualizzare un eService specifico (Scenario 5)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di descriptor usando l'eserviceId creato
    Then si ottiene lo status code 200
    Then la lista di descriptor è presente solo se lo status code è 200
    Examples:
      | ruolo        | ruolo-m2m |
      | admin        | m2m       |
      | api          | m2m       |
      | security     | m2m       |
      | api,security | m2m-admin |
      | support      | m2m-admin |

  @happy-path
  Scenario Outline: [M2MG_ESERVICES_5] Recupero del dettaglio di un eService con utente autorizzato (Scenario 83)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di eService
    Then si ottiene lo status code 200
    And eService viene restituito
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @sad-path
  Scenario: [M2MG_ESERVICES_7] Accesso negato al dettaglio di un eService con token non valido (Scenario 85)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare il record di eService creato
    Then si ottiene lo status code 401
    And eService non restituito

  @sad-path
  Scenario Outline: [M2MG_ESERVICES_8] Errore nel recupero del dettaglio di un eService inesistente (Scenario 86)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare eService con un id inesistente
    Then si ottiene lo status code 404
    And eService non restituito
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @happy-path
  Scenario Outline: [M2MG_ESERVICES_9] RED - La lista dei descriptors di un eService può essere visualizzata da un utente con ruolo M2M o M2M-ADMIN (Scenario 6)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di descriptor usando l'eserviceId creato
    Then si ottiene lo status code 200
    Then la lista di descriptor è presente solo se lo status code è 200
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @happy-path
  Scenario Outline: [M2MG_ESERVICES_9_B] RED - Recupero corretto della lista dei descriptors per un eService con utente autorizzato (Scenario 87)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di descriptor usando l'eserviceId creato
    Then si ottiene lo status code 200
    And lista di descriptor viene restituita
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @sad-path
  Scenario Outline: [M2MG_ESERVICES_11] RED - Accesso negato alla lista dei descriptors con token non valido (Scenario 89)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare la lista di descriptor usando l'eserviceId creato
    Then si ottiene lo status code 401
    And lista di descriptor non restituita
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @sad-path
  Scenario Outline: [M2MG_ESERVICES_12] RED - Errore nel recupero della lista dei descriptors con eserviceId inesistente (Scenario 90)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di descriptor con un eserviceId inesistente
    Then si ottiene lo status code 404
    And lista di descriptor non restituita
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @happy-path
  Scenario Outline: [M2MG_ESERVICES_13] Recupero del descriptor di un eService con utente autorizzato (Scenario 7)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare il record di descriptor creato
    Then si ottiene lo status code 200
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @happy-path
  Scenario Outline: [M2MG_ESERVICES_14] Recupero corretto di un descriptor per uno specifico eService (Scenario 91)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare il record di descriptor creato
    Then si ottiene lo status code 200
    And descriptor viene restituito
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @sad-path
  Scenario: [M2MG_ESERVICES_16] Accesso negato al recupero di un descriptor con token non valido (Scenario 93)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare il record di descriptor creato
    Then si ottiene lo status code 401
    And descriptor non restituito

  @sad-path
  Scenario Outline: [M2MG_ESERVICES_17] Errore nel recupero di un descriptor con eserviceId e descriptorId inesistenti (Scenario 94)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare descriptor con un id inesistente
    Then si ottiene lo status code 404
    And descriptor non restituito
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  Scenario: [M2M_ESERVICES_18] Un e-service può essere visionato anche da un ente diverso dal creatore
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin

    When l'utente tenta di recuperare il record di eService creato
    Then si ottiene lo status code 200

    When l'utente tenta di recuperare il record di descriptor creato
    Then si ottiene lo status code 200

  # Da qui in poi test di "API V2 Parte 2" https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1812562407/DRAFT+SRS+API+V2+Parte+2#Scenari-di-test
  @m2m-v3-204-to-200
  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_ESERVICES_18] Un utente con ruolo M2M-ADMIN può effettuare la cancellazione di un e-service (Parte2#Scenario intorno a 32)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la cancellazione dell'e-service
    Then si ottiene http status code 200
    When l'utente tenta di recuperare l'e-service creato
    Then si ottiene lo status code 404


#  Forma precedente di seguito, il 401 è stato segnalato come non corretto
#  https://pagopa.atlassian.net/browse/PIN-8604
#  Scenario: [M2MG_ESERVICES_19] Un utente con ruolo M2M non può effettuare la cancellazione di un e-service (Parte2#Scenario intorno a 34)
#    Given "PA1" ha già creato e pubblicato 1 e-services
#    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
#    When l'utente tenta di effettuare la cancellazione dell'e-service
#    Then si ottiene lo status code 401

  Scenario: [M2MG_ESERVICES_19] Un utente con ruolo M2M non può effettuare la cancellazione di un e-service (Parte2#Scenario intorno a 34)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la cancellazione dell'e-service
    Then si ottiene lo status code 403

  Scenario: [M2MG_ESERVICES_20] La cancellazione di un e-service non può essere effettuata specificando un id inesistente (Parte2#Scenario intorno a 35)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la cancellazione di un e-service inesistente
    Then si ottiene lo status code 404

  Scenario: [M2MG_ESERVICES_21] La cancellazione di un e-service non può essere effettuata specificando un token non valido (Parte2#Scenario intorno a 36)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la cancellazione di un e-service inesistente
    Then si ottiene status code 401

  Scenario: [M2MG_ESERVICES_22] La cancellazione di un e-service precedentemente rimosso non può essere effettuata (Parte2#Scenario intorno a 37)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua la cancellazione dell'e-service con successo
    When l'utente tenta di effettuare la cancellazione dell'e-service
    Then si ottiene lo status code 404

  Scenario: [M2MG_ESERVICES_23] La cancellazione di un e-service non può essere effettuata da un ente diverso dal creatore dell'e-service (Parte2#Scenario intorno a 38)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'e-service
    Then si ottiene lo status code 403

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_ESERVICES_24] Un utente con ruolo M2M-ADMIN può effettuare riattivazione di un e-service in stato SUSPENDED (Parte2#Scenario intorno a 39)
    Given "PA1" ha già creato un e-service con un descrittore in stato "SUSPENDED"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la riattivazione dell'e-service
    Then si ottiene lo status code 200
    And l'e-service è stato riattivato con successo

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_ESERVICES_25] Un utente con ruolo M2M non può effettuare riattivazione di un e-service (Parte2#Scenario intorno a 41)
    Given "PA1" ha già creato un e-service con un descrittore in stato "SUSPENDED"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la riattivazione dell'e-service
    Then si ottiene lo status code 403

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_ESERVICES_26] Un utente con ruolo M2M-ADMIN non può effettuare riattivazione di un e-service indicando degli identificativi inesistenti (Parte2#Scenario intorno a 42)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la riattivazione di un e-service inesistente
    Then si ottiene lo status code 404

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_ESERVICES_27] Un utente con ruolo M2M-ADMIN non può effettuare riattivazione di un e-service specificando un token non valido (Parte2#Scenario intorno a 43)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la cancellazione di un e-service inesistente
    Then si ottiene lo status code 401

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_ESERVICES_28] Un utente con ruolo M2M-ADMIN non può effettuare riattivazione di un e-service in stato PUBLISHED (Parte2#Scenario intorno a 44)
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la riattivazione dell'e-service
    Then si ottiene lo status code 409

  @m2m-parte2-agosto-rilascio1
  Scenario Outline: [M2MG_ESERVICES_29_A] Un utente con ruolo M2M-ADMIN non può effettuare riattivazione di un e-service in stato diverso da ACTIVE o SUSPENDED (Parte2#Scenario intorno a 45)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la riattivazione dell'e-service
    Then si ottiene lo status code 409
    Examples:
      | stato      |
      | DRAFT      |
      | PUBLISHED  |
      | DEPRECATED |
      | ARCHIVED   |

  @m2m-parte2-agosto-rilascio1 @deleghe2
  Scenario: [M2MG_ESERVICES_29_B] Un utente con ruolo M2M-ADMIN non può effettuare riattivazione di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 45)
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la riattivazione dell'e-service
    Then si ottiene lo status code 409

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_ESERVICES_30] Un utente con ruolo M2M-ADMIN non può effettuare riattivazione di un e-service se non è il creatore dello stesso (Parte2#Scenario intorno a 46)
    Given "PA1" ha già creato un e-service con un descrittore in stato "SUSPENDED"
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la riattivazione dell'e-service
    Then si ottiene lo status code 403

  ## EService Patch
  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_31] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale di un e-service in stato DRAFT (Parte2#Scenario intorno a 71)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale dell'e-service
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    When l'utente tenta di effettuare la modifica parziale dell'e-service specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_32] Un utente con ruolo M2M NON può effettuare una modifica parziale di un e-service (Parte2#Scenario intorno a 73)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_33] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service inesistente (Parte2#Scenario intorno a 74)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale di un e-service inesistente
    Then si ottiene lo status code 404

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_34] Un utente NON può effettuare una modifica parziale di un e-service indicando un token non valido (Parte2#Scenario intorno a 75)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin

#    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la modifica parziale dell'e-service con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service non ha subito modifiche

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_35_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service in stato diverso da DRAFT (Parte2#Scenario intorno a 76)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale dell'e-service
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |
      | ARCHIVED   |

  @m2m-patch
  @m2m-parte2-agosto-rilascio2 @deleghe2
  Scenario: [M2MG_ESERVICES_35_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 76)
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale dell'e-service
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_36] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service che non gli appartiene (Parte2#Scenario intorno a 77)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA2" con ruolo m2m-admin tenta di effettuare la modifica parziale dell'e-service
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'e-service non ha subito modifiche

  ## EService Patch Delegation
  #test originariamente creato per APIv2 Parte 2 poi modificato (esteso) per la feature "Abilitazione deleghe su E-Service pubblicati"
  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_37] Un utente con ruolo M2M-ADMIN e appartenente ad un ente di tipo PA, può effettuare una modifica parziale della delega di un e-service in uno degli stati permessi
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED       | true                | true                    |
      | PUBLISHED       | true                | false                   |
      | PUBLISHED       | true                | %null                   |
      | PUBLISHED       | false               | %null                   |
      | PUBLISHED       | %null               | false                   |
      | PUBLISHED       | %null               | %null                   |

      | SUSPENDED       | true                | true                    |
      | SUSPENDED       | true                | false                   |
      | SUSPENDED       | true                | %null                   |
      | SUSPENDED       | false               | %null                   |
      | SUSPENDED       | %null               | false                   |
      | SUSPENDED       | %null               | %null                   |

      | DEPRECATED      | true                | true                    |
      | DEPRECATED      | true                | false                   |
      | DEPRECATED      | true                | %null                   |
      | DEPRECATED      | false               | %null                   |
      | DEPRECATED      | %null               | false                   |
      | DEPRECATED      | %null               | %null                   |

  #test originariamente creato per APIv2 Parte 2 poi modificato (esteso) per la feature "Abilitazione deleghe su E-Service pubblicati"
  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_38] Un utente con ruolo M2M NON può effettuare una modifica parziale della delega di un e-service
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "true"
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState |
      | PUBLISHED       |
      | DEPRECATED      |
      | SUSPENDED       |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_39] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service inesistente (Parte2#Scenario intorno a 88)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega di un e-service inesistente
    Then si ottiene lo status code 404

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_40] Un utente NON può effettuare una modifica parziale della delega di un e-service indicando un token non valido (Parte2#Scenario intorno a 89)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service non ha subito modifiche
    Examples:
      | descriptorState |
      | PUBLISHED       |
      | DEPRECATED      |
      | SUSPENDED       |

    # Corretto 400 in 409
  # 09/03/2026 ticket https://pagopa.atlassian.net/browse/QA-10948: al momento non è possibile archiviare un e-service
  #test originariamente creato per APIv2 Parte 2 poi modificato (esteso) per la feature "Abilitazione deleghe su E-Service pubblicati"
  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  @m2m-patch
  Scenario Outline: [M2MG_ESERVICES_41_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service in uno stato DRAFT
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "true"
    Then si ottiene lo status code 409
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState |
      | DRAFT           |

  # Per interazioni con un altro bug si è chiarito che quando il ruolo non è esatto il codice di riferimento è il 403
  # https://pagopa.atlassian.net/browse/PIN-8604
  #Scenario: [M2MG_ESERVICES_41_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service in stato WAITING_FOR_APPROVAL
  #  Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
  #  And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
  #  When l'utente tenta di effettuare la modifica parziale della delega dell'e-service
  #  Then si ottiene lo status code 400
  #  And l'e-service non ha subito modifiche
  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  @deleghe2
  @m2m-patch
  #test originariamente creato per APIv2 Parte 2 poi modificato (esteso) per la feature "Abilitazione deleghe su E-Service pubblicati"
  Scenario: [M2MG_ESERVICES_41_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "true"
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche

  #test originariamente creato per APIv2 Parte 2 poi modificato (esteso) per la feature "Abilitazione deleghe su E-Service pubblicati"
  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_42_1] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service che non gli appartiene e per cui non possiede la delega in erogazione
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "true"
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState |
      | PUBLISHED       |
      | DEPRECATED      |
      | SUSPENDED       |

  #test originariamente creato per APIv2 Parte 2 poi modificato (esteso) per la feature "Abilitazione deleghe su E-Service pubblicati"
  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_42_2] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service indicando le informazioni già presenti (Parte2#Scenario intorno a 91)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service senza apportare cambiamenti
    Then si ottiene lo status code 200
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState |
      | PUBLISHED       |
      | DEPRECATED      |
      | SUSPENDED       |

  ## EService Patch Name
  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_43] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale del nome di un e-service in uno degli stati permessi (Parte2#Scenario intorno a 78)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 400
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_44] Un utente con ruolo M2M NON può effettuare una modifica parziale del nome di un e-service (Parte2#Scenario intorno a 80)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_45] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del nome di un e-service inesistente (Parte2#Scenario intorno a 81)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome di un e-service inesistente
    Then si ottiene lo status code 404

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_46] Un utente NON può effettuare una modifica parziale del nome di un e-service indicando un token non valido (Parte2#Scenario intorno a 82)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service con un token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service non ha subito modifiche
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |

  # 09/03/2026 ticket https://pagopa.atlassian.net/browse/QA-10948: al momento non è possibile archiviare un e-service
  @m2m-patch
  Scenario Outline: [M2MG_ESERVICES_47_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del nome di un e-service in stato DRAFT o ARCHIVED
    Given "PA1" ha già creato un e-service in stato <stato>
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service
    Then si ottiene lo status code 409
    And l'e-service non ha subito modifiche
    Examples:
      | stato |
      | DRAFT |
    #  | ARCHIVED    |

  @deleghe2
  @m2m-patch
  Scenario: [M2MG_ESERVICES_47_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del nome di un e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_48] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del nome di un e-service che non gli appartiene (Parte2#Scenario intorno a 83)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |

  # Ticket aperto https://pagopa.atlassian.net/browse/PIN-7526
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_49] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del nome di un e-service indicando le informazioni già presenti (Parte2#Scenario intorno a 84)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service senza apportare cambiamenti
    Then si ottiene lo status code 409
    And l'e-service non ha subito modifiche
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |

  @m2m-patch
  ## EService Patch Description
  Scenario Outline: [M2MG_ESERVICES_44] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale della descrizione di un e-service in uno degli stati permessi (Parte2#Scenario intorno a 92)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |

  @m2m-patch
  Scenario Outline: [M2MG_ESERVICES_45] Un utente con ruolo M2M NON può effettuare una modifica parziale della descrizione di un e-service (Parte2#Scenario intorno a 94)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |

  @m2m-patch
  Scenario: [M2MG_ESERVICES_46_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service inesistente (Parte2#Scenario intorno a 95)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione di un e-service inesistente
    Then si ottiene lo status code 404

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_47] Un utente NON può effettuare una modifica parziale della descrizione di un e-service indicando un token non valido (Parte2#Scenario intorno a 96)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service non ha subito modifiche
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |

  # 09/03/2026 ticket https://pagopa.atlassian.net/browse/QA-10948: al momento non è possibile archiviare un e-service
  @m2m-patch
  Scenario Outline: [M2MG_ESERVICES_48_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service in stato DRAFT o ARCHIVED
    Given "PA1" ha già creato un e-service in stato <stato>
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 409
    And l'e-service non ha subito modifiche
    Examples:
      | stato |
      | DRAFT |
    #  | ARCHIVED    |

  @deleghe2
  @m2m-patch
  Scenario: [M2MG_ESERVICES_48_B] Un utente dell'ente creatore con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche

  @deleghe2
  @m2m-patch
  Scenario: [M2MG_ESERVICES_48_C] Un utente delegato con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 409
    And l'e-service non ha subito modifiche

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_49_2] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service che non gli appartiene (Parte2#Scenario intorno a 97)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |

  # Ticket aperto https://pagopa.atlassian.net/browse/PIN-7526
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_50] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service indicando le informazioni già presenti (Parte2#Scenario intorno a 98)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service senza apportare cambiamenti
    Then si ottiene lo status code 409
    And l'e-service non ha subito modifiche
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |

  # EService Patch Descriptor
  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_01] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale del descriptor di un e-service in stato DRAFT (Parte2#Scenario intorno a 99)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service
    Then si ottiene lo status code 200
    And l'e-service descriptor restituito è coerente con le modifiche effettuate
    And l'e-service descriptor è stato parzialmente modificato correttamente
    When l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 200
    And l'e-service descriptor restituito è coerente con le modifiche effettuate
    And l'e-service descriptor è stato parzialmente modificato correttamente

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_02] Un utente con ruolo M2M NON può effettuare una modifica parziale del descriptor di un e-service (Parte2#Scenario intorno a 101)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service
    Then si ottiene lo status code 403
    And l'e-service descriptor non ha subito modifiche

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_03] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del descriptor di un e-service inesistente (Parte2#Scenario intorno a 102)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del descriptor di un e-service inesistente
    Then si ottiene lo status code 404

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_04] Un utente NON può effettuare una modifica parziale del descriptor di un e-service indicando un token non valido (Parte2#Scenario intorno a 103)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
#    And viene impostato per l'utente un token m2m non valido
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service descriptor non ha subito modifiche

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_DESCRIPTORS_05_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del descriptor di un e-service in stato non DRAFT (Parte2#Scenario intorno a 104)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service
    Then si ottiene lo status code 400
    And l'e-service descriptor non ha subito modifiche
    Examples:
      | stato      |
      | PUBLISHED  |
      | DEPRECATED |
      | SUSPENDED  |
      | ARCHIVED   |

  @m2m-patch
  @m2m-parte2-agosto-rilascio2 @deleghe2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_05_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del descriptor di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 104)
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service
    Then si ottiene lo status code 400
    And l'e-service descriptor non ha subito modifiche

  @m2m-patch
  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del descriptor di un e-service che non gli appartiene (Parte2#Scenario intorno a 105)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA2" con ruolo m2m-admin tenta di effettuare la modifica parziale del descriptor dell'e-service
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'e-service descriptor non ha subito modifiche

  # EService Patch Quotas
  @m2m-patch
  @m2m-parte2-settembre
  Scenario Outline: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_01] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale delle quote di un descriptor di un e-service in stato PUBLISHED o SUSPENDED (Parte2#Scenario intorno a 229)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<state>"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service
    Then si ottiene lo status code 200
    And l'e-service descriptor restituito è coerente con le modifiche effettuate
    And l'e-service descriptor è stato parzialmente modificato correttamente
    When l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 200
    And l'e-service descriptor restituito è coerente con le modifiche effettuate
    And l'e-service descriptor è stato parzialmente modificato correttamente
    Examples:
      | state     |
      | PUBLISHED |
      | SUSPENDED |

  @m2m-patch
  @m2m-parte2-settembre
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_02] Un utente con ruolo M2M NON può effettuare una modifica parziale delle quote di un descriptor di un e-service (Parte2#Scenario intorno a 231)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service
    Then si ottiene lo status code 403
    And l'e-service descriptor non ha subito modifiche

  @m2m-parte2-settembre
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_03] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote di un descriptor di un e-service inesistente (Parte2#Scenario intorno a 232)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote di un descriptor di un e-service inesistente
    Then si ottiene lo status code 404

  @m2m-patch
  @m2m-parte2-settembre
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_04] Un utente NON può effettuare una modifica parziale delle quote di un descriptor di un e-service indicando un token non valido (Parte2#Scenario intorno a 233)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service descriptor non ha subito modifiche

  @m2m-patch
  @m2m-parte2-settembre
  Scenario Outline: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_05_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote di un descriptor di un e-service in stato DRAFT, DEPRECATED o ARCHIVED (Parte2#Scenario intorno a 234)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service
    Then si ottiene lo status code 400
    And l'e-service descriptor non ha subito modifiche
    Examples:
      | stato    |
      | DRAFT    |
      | ARCHIVED |

  @m2m-patch
  @m2m-parte2-settembre @deleghe2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_05_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote di un descriptor di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 234)
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service
    Then si ottiene lo status code 400
    And l'e-service descriptor non ha subito modifiche

  @m2m-patch
  @m2m-parte2-settembre
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote di un descriptor di un e-service che non gli appartiene (Parte2#Scenario intorno a 235)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA2" con ruolo m2m-admin tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service
    Then si ottiene lo status code 404
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'e-service descriptor non ha subito modifiche

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_1A] Un utente con ruolo M2M-ADMIN e appartenente ad un ente di tipo GSP, può effettuare una modifica parziale della delega di un e-service in uno degli stati permessi
    Given "GSP" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED       | true                | true                    |
      | PUBLISHED       | true                | false                   |
      | PUBLISHED       | false               | false                   |
      | PUBLISHED       | true                | %null                   |
      | PUBLISHED       | false               | %null                   |
      | PUBLISHED       | %null               | false                   |
      | PUBLISHED       | %null               | %null                   |

      | SUSPENDED       | true                | true                    |
      | SUSPENDED       | true                | false                   |
      | SUSPENDED       | false               | false                   |
      | SUSPENDED       | true                | %null                   |
      | SUSPENDED       | false               | %null                   |
      | SUSPENDED       | %null               | false                   |
      | SUSPENDED       | %null               | %null                   |

      | DEPRECATED      | true                | true                    |
      | DEPRECATED      | true                | false                   |
      | DEPRECATED      | false               | false                   |
      | DEPRECATED      | true                | %null                   |
      | DEPRECATED      | false               | %null                   |
      | DEPRECATED      | %null               | false                   |
      | DEPRECATED      | %null               | %null                   |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_1B] Un utente con ruolo M2M-ADMIN e appartenente ad un ente di tipo PRIVATE, può effettuare una modifica parziale della delega di un e-service in uno degli stati permessi
    Given "Privato" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "Privato" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED       | true                | true                    |
      | PUBLISHED       | true                | false                   |
      | PUBLISHED       | false               | false                   |
      | PUBLISHED       | true                | %null                   |
      | PUBLISHED       | false               | %null                   |
      | PUBLISHED       | %null               | false                   |
      | PUBLISHED       | %null               | %null                   |

      | SUSPENDED       | true                | true                    |
      | SUSPENDED       | true                | false                   |
      | SUSPENDED       | false               | false                   |
      | SUSPENDED       | true                | %null                   |
      | SUSPENDED       | false               | %null                   |
      | SUSPENDED       | %null               | false                   |
      | SUSPENDED       | %null               | %null                   |

      | DEPRECATED      | true                | true                    |
      | DEPRECATED      | true                | false                   |
      | DEPRECATED      | false               | false                   |
      | DEPRECATED      | true                | %null                   |
      | DEPRECATED      | false               | %null                   |
      | DEPRECATED      | %null               | false                   |
      | DEPRECATED      | %null               | %null                   |

  #test cui richiesta dal client non viene effettuata poichè parametro obbligatorio settato a null
  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_2] Un utente con ruolo M2M-ADMIN NON può modificare le flag di delega di un e-service se non specifica l'id dell'e-service
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale non specificando l'id dell'e-service
    Then si ottiene lo status code 400

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_3A] Un utente con ruolo M2M-ADMIN e appartenente ad un ente di tipo PA NON può modificare la flag di delega tecnica di un e-service ottenendo uno stato non permesso
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED       | false               | true                    |
      | SUSPENDED       | false               | true                    |
      | DEPRECATED      | false               | true                    |
      #considerando che lo stato delle flag alla creazione dell'e-service è isConsumerDelegable=false e isClientAccessDelegable=false
      | PUBLISHED       | %null               | true                    |
      | SUSPENDED       | %null               | true                    |
      | DEPRECATED      | %null               | true                    |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_3B] Un utente con ruolo M2M-ADMIN e appartenente ad un ente di tipo GSP NON può modificare la flag di delega tecnica di un e-service ottenendo uno stato non permesso
    Given "GSP" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED       | false               | true                    |
      | SUSPENDED       | false               | true                    |
      | DEPRECATED      | false               | true                    |
      #considerando che lo stato delle flag alla creazione dell'e-service è isConsumerDelegable=false e isClientAccessDelegable=false
      | PUBLISHED       | %null               | true                    |
      | SUSPENDED       | %null               | true                    |
      | DEPRECATED      | %null               | true                    |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_3C] Un utente con ruolo M2M-ADMIN e appartenente ad un ente di tipo PRIVATE NON può modificare la flag di delega tecnica di un e-service ottenendo uno stato non permesso
    Given "Privato" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "Privato" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED       | false               | true                    |
      | SUSPENDED       | false               | true                    |
      | DEPRECATED      | false               | true                    |
      #considerando che lo stato delle flag alla creazione dell'e-service è isConsumerDelegable=false e isClientAccessDelegable=false
      | PUBLISHED       | %null               | true                    |
      | SUSPENDED       | %null               | true                    |
      | DEPRECATED      | %null               | true                    |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_4A] Un utente con ruolo M2M-ADMIN e appartenente ad un ente di tipo PA NON può modificare la flag di delega amministrativa di un e-service ottenendo uno stato non permesso
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "false" e quella tecnica a "%null"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState |
      #considerando che lo stato delle flag alla creazione dell'e-service è isConsumerDelegable=true e isClientAccessDelegable=true
      | PUBLISHED       |
      | SUSPENDED       |
      | DEPRECATED      |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_4B] Un utente con ruolo M2M-ADMIN e appartenente ad un ente di tipo GSP NON può modificare la flag di delega amministrativa di un e-service ottenendo uno stato non permesso
    Given "GSP" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "false" e quella tecnica a "%null"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState |
      #considerando che lo stato delle flag alla creazione dell'e-service è isConsumerDelegable=true e isClientAccessDelegable=true
      | PUBLISHED       |
      | SUSPENDED       |
      | DEPRECATED      |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_4C] Un utente con ruolo M2M-ADMIN e appartenente ad un ente di tipo PRIVATE NON può modificare la flag di delega amministrativa di un e-service ottenendo uno stato non permesso
    Given "Privato" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'utente è un "admin" di "Privato" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "false" e quella tecnica a "%null"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState |
      #considerando che lo stato delle flag alla creazione dell'e-service è isConsumerDelegable=true e isClientAccessDelegable=true
      | PUBLISHED       |
      | SUSPENDED       |
      | DEPRECATED      |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_5] Un utente con ruolo M2M-ADMIN può modificare i flag di delega in fruizione dell'e-service di cui possiede la delega in erogazione
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED       | true                | true                    |
      | PUBLISHED       | false               | false                   |
      | PUBLISHED       | true                | false                   |
      | PUBLISHED       | true                | %null                   |
      | PUBLISHED       | false               | %null                   |
      | PUBLISHED       | %null               | false                   |
      | PUBLISHED       | %null               | %null                   |

      | SUSPENDED       | true                | true                    |
      | SUSPENDED       | false               | false                   |
      | SUSPENDED       | true                | false                   |
      | SUSPENDED       | true                | %null                   |
      | SUSPENDED       | false               | %null                   |
      | SUSPENDED       | %null               | false                   |
      | SUSPENDED       | %null               | %null                   |

      | DEPRECATED      | true                | true                    |
      | DEPRECATED      | false               | false                   |
      | DEPRECATED      | true                | false                   |
      | DEPRECATED      | true                | %null                   |
      | DEPRECATED      | false               | %null                   |
      | DEPRECATED      | %null               | false                   |
      | DEPRECATED      | %null               | %null                   |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_6] Un utente con ruolo M2M-ADMIN NON può modificare i flag di delega in fruizione dell'e-service di cui possiede la delega in erogazione
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale non specificando l'id dell'e-service
    Then si ottiene lo status code 400

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_7] Un utente con ruolo M2M-ADMIN NON può modificare i flag di delega in fruizione dell'e-service di cui possiede la delega in erogazione, ottenendo uno stato non permesso
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED       | false               | true                    |
      | SUSPENDED       | false               | true                    |
      | DEPRECATED      | false               | true                    |
      #considerando che lo stato delle flag alla creazione dell'e-service è isConsumerDelegable=false e isClientAccessDelegable=false
      | PUBLISHED       | %null               | true                    |
      | SUSPENDED       | %null               | true                    |
      | DEPRECATED      | %null               | true                    |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_8] Un utente con ruolo M2M-ADMIN, delegato all'erogazione, NON può modificare le flag di delega amministrativa di un e-service ottenendo uno stato non permesso
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "false" e quella tecnica a "%null"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState |
      #considerando che lo stato delle flag alla creazione dell'e-service è isConsumerDelegable=false e isClientAccessDelegable=false
      | PUBLISHED       |
      | SUSPENDED       |
      | DEPRECATED      |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_9] Un utente con ruolo M2M, delegato all'erogazione, NON può effettuare una modifica parziale della delega di un e-service
    Given "PA1" ha già creato un e-service con un descrittore in stato "<descriptorState>" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "true"
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState |
      | PUBLISHED       |
      | DEPRECATED      |
      | SUSPENDED       |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_10] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale della delega di un e-service in modalità "RECEIVE" in uno degli stati permessi
    #isConsumerDelegable=false e isClientAccessDelegable=false
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "<descriptorState>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED       | true                | true                    |
      | PUBLISHED       | true                | false                   |
      | PUBLISHED       | true                | %null                   |
      | PUBLISHED       | false               | %null                   |
      | PUBLISHED       | %null               | false                   |
      | PUBLISHED       | %null               | %null                   |
      | PUBLISHED       | false               | false                   |

      | SUSPENDED       | true                | true                    |
      | SUSPENDED       | true                | false                   |
      | SUSPENDED       | true                | %null                   |
      | SUSPENDED       | false               | %null                   |
      | SUSPENDED       | %null               | false                   |
      | SUSPENDED       | %null               | %null                   |
      | SUSPENDED       | false               | false                   |

      | DEPRECATED      | true                | true                    |
      | DEPRECATED      | true                | false                   |
      | DEPRECATED      | true                | %null                   |
      | DEPRECATED      | false               | %null                   |
      | DEPRECATED      | %null               | false                   |
      | DEPRECATED      | %null               | %null                   |
      | DEPRECATED      | false               | false                   |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_UPDATE_DELEGATION_11] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service in modalità "RECEIVE" ottenendo lo stato non permesso
    #isConsumerDelegable=false e isClientAccessDelegable=false
    Given "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "<descriptorState>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | descriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED       | false               | true                    |
      | SUSPENDED       | false               | true                    |
      | DEPRECATED      | false               | true                    |
      | PUBLISHED       | %null               | true                    |
      | SUSPENDED       | %null               | true                    |
      | DEPRECATED      | %null               | true                    |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_DISABLE_DELEGATION_1] Un utente NON può creare una delega in fruizione per un e-service cui flag di delega amministrativa viene disabilitata dopo la pubblicazione dell'e-service
    Given "<producerTenant>" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'utente è un "admin" di "<producerTenant>" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "false" e quella tecnica a "false"
    And l'e-service è stato parzialmente modificato correttamente
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un m2m-admin dell'ente delegante
    When l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato
    Then si ottiene lo status code 400
    Examples:
      | producerTenant |
      | PA1            |
      | GSP            |
      | Privato        |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_DISABLE_DELEGATION_2] Un utente con delega in fruizione preesistente alla disabilitazione del flag di delega amministrativa può creare una richiesta di fruizione in delega
    Given "<producerTenant>" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "true" e delega tecnica a "false"
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un m2m-admin dell'ente delegante
    And l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato
    And la delega è stata inoltrata correttamente
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione con successo
    And l'utente è un "admin" di "<producerTenant>" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "false" e quella tecnica a "false"
    And l'e-service è stato parzialmente modificato correttamente
    When il delegato ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And l'utente è un "admin" dell'ente delegato
    And l'utente inoltra quella richiesta di fruizione
    Then si ottiene lo status code 200
    And la richiesta di fruizione è passata in stato "ACTIVE"
    Examples:
      | producerTenant |
      | PA1            |
      | GSP            |
      | Privato        |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_DISABLE_DELEGATION_3] Un accordo di fruizione in delega preesistente alla disabilitazione del flag di delega amministrativa resta attivo
    Given "<producerTenant>" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "true" e delega tecnica a "false"
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un m2m-admin dell'ente delegante
    And l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato
    And la delega è stata inoltrata correttamente
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione con successo
    And il delegato ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And l'utente inoltra quella richiesta di fruizione
    And la richiesta di fruizione è passata in stato "ACTIVE"
    And l'utente è un "admin" di "<producerTenant>" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "false" e quella tecnica a "false"
    And l'e-service è stato parzialmente modificato correttamente
    And l'utente è un "admin" dell'ente delegato
    Then la richiesta di fruizione è in stato "ACTIVE"
    Examples:
      | producerTenant |
      | PA1            |
      | GSP            |
      | Privato        |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_DISABLE_DELEGATION_4] Un utente delegato in fruizione NON può associare un proprio client a una finalità in delega se la delega tecnica su un e-service in stato PUBLISHED viene disabilitata dopo la creazione della delega in fruizione
    Given "<producerTenant>" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un m2m-admin dell'ente delegante
    And l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato
    And la delega è stata inoltrata correttamente
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione con successo
    And il delegato ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And l'utente inoltra quella richiesta di fruizione
    And la richiesta di fruizione è passata in stato "ACTIVE"
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And il delegato ha già creato 1 client "CONSUMER"
    And l'utente è un "admin" di "<producerTenant>" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "false"
    And l'e-service è stato parzialmente modificato correttamente
    And l'utente è un "admin" dell'ente delegato
    When l'utente richiede l'associazione della finalità al client
    Then si ottiene status code 400
    And l'associazione tra finalita e client non è presente
    Examples:
      | producerTenant |
      | PA1            |
      | GSP            |
      | Privato        |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_DISABLE_DELEGATION_5] Un utente delegato in fruizione NON può associare un proprio client a una finalità in delega quando la delega tecnica viene disabilitata su un e-service in stato PUBLISHED
    Given "<producerTenant>" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'utente è un "admin" di "<producerTenant>" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "false"
    And l'e-service è stato parzialmente modificato correttamente
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un m2m-admin dell'ente delegante
    And l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato
    And la delega è stata inoltrata correttamente
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione con successo
    And il delegato ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And l'utente inoltra quella richiesta di fruizione
    And la richiesta di fruizione è passata in stato "ACTIVE"
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And il delegato ha già creato 1 client "CONSUMER"
    When l'utente richiede l'associazione della finalità al client
    Then si ottiene status code 400
    And l'associazione tra finalita e client non è presente
    Examples:
      | producerTenant |
      | PA1            |
      | GSP            |
      | Privato        |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_DISABLE_DELEGATION_6] Un'associazione client-finalità preesistente alla disabilitazione del flag di delega tecnica rimane attiva
    Given "<producerTenant>" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un m2m-admin dell'ente delegante
    And l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato
    And la delega è stata inoltrata correttamente
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione con successo
    And il delegato ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And l'utente inoltra quella richiesta di fruizione
    And la richiesta di fruizione è passata in stato "ACTIVE"
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And il delegato ha già creato 1 client "CONSUMER"
    And "PA3" associa la finalità al client creato con successo
    And l'utente è un "admin" di "<producerTenant>" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "false"
    And l'e-service è stato parzialmente modificato correttamente
    And l'utente è un "admin" dell'ente delegato
    Then l'associazione tra finalita e client è presente
    Examples:
      | producerTenant |
      | PA1            |
      | GSP            |
      | Privato        |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_ENABLE_DELEGATION_1] Un utente può creare una delega in fruizione per un e-service cui flag di delega amministrativa viene abilitata dopo la pubblicazione dell'e-service
    Given "<producerTenant>" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "<producerTenant>" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "false"
    And l'e-service è stato parzialmente modificato correttamente
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un m2m-admin dell'ente delegante
    When l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato
    Then si ottiene lo status code 200
    And la delega è stata inoltrata correttamente
    Examples:
      | producerTenant |
      | PA1            |
      | GSP            |
      | Privato        |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_ENABLE_DELEGATION_2] Un utente delegato in fruizione può associare un proprio client a una finalità in delega quando la delega tecnica viene abilitata su un e-service in stato PUBLISHED
    Given "<producerTenant>" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "true" e delega tecnica a "false"
    And l'utente è un "admin" di "<producerTenant>" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "true"
    And l'e-service è stato parzialmente modificato correttamente
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un m2m-admin dell'ente delegante
    And l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato
    And la delega è stata inoltrata correttamente
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione con successo
    And il delegato ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And l'utente inoltra quella richiesta di fruizione
    And la richiesta di fruizione è passata in stato "ACTIVE"
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And il delegato ha già creato 1 client "CONSUMER"
    When "PA3" associa la finalità al client creato con successo
    Then si ottiene status code 204
    Examples:
      | producerTenant |
      | PA1            |
      | GSP            |
      | Privato        |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path
  Scenario Outline: [M2M_ESERVICE_PUBLISHED_ENABLE_DELEGATION_3] Un utente delegato in fruizione può associare un proprio client a una finalità creata in delega se la delega tecnica su un e-service in stato PUBLISHED viene abilitata dopo la creazione della delega in fruizione
    Given "<producerTenant>" ha già creato un e-service con un descrittore in stato "PUBLISHED" e impostando delega amministrativa a "true" e delega tecnica a "false"
    And l'ente delegante "PA2"
    And l'ente delegato "PA3"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un m2m-admin dell'ente delegante
    And l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato
    And la delega è stata inoltrata correttamente
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione con successo
    And il delegato ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And l'utente inoltra quella richiesta di fruizione
    And la richiesta di fruizione è passata in stato "ACTIVE"
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And il delegato ha già creato 1 client "CONSUMER"
    And l'utente è un "admin" di "<producerTenant>" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "true"
    And l'e-service è stato parzialmente modificato correttamente
    When "PA3" associa la finalità al client creato con successo
    Then si ottiene status code 204
    Examples:
      | producerTenant |
      | PA1            |
      | GSP            |
      | Privato        |

  @eservice_description_max_length
  @happy-path
  Scenario: [ESERVICE_CREATION_DESCRIPTION_MAX_LENGTH_3] Un utente crea un e-service utilizzando la descrizione della lunghezza massima possibile con M2M
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta la creazione dell'e-service con la seguente configurazione:
      | description-length | 400 |
    Then l'utente è un "admin" di "PA1"
    And l'e-service creato ha una descrizione di 400 caratteri

  @eservice_description_max_length
  @sad-path
  Scenario: [ESERVICE_CREATION_DESCRIPTION_MAX_LENGTH_4] La creazione dell'e-service non va a buon fine se viene superata la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta la creazione dell'e-service con la seguente configurazione:
      | description-length | 401 |
    Then si ottiene status code 400

  @eservice_description_max_length
  @happy-path
  Scenario: [ESERVICE_DESCRIPTION_UPDATE_MAXLENGTH_6] Un utente aggiorna un e-service in stato DRAFT utilizzando la descrizione della lunghezza massima possibile
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service con la configurazione predefinita
    When l'utente tenta di effettuare la modifica parziale dell'e-service in stato DRAFT specificando una descrizione di lunghezza pari a 400 caratteri
    And l'utente è un "admin" di "PA1"
    Then l'e-service creato ha una descrizione di 400 caratteri

  @eservice_description_max_length
  @sad-path
  Scenario: [ESERVICE_DESCRIPTION_UPDATE_MAXLENGTH_7] L'aggiornamento dell'e-service in stato DRAFT non va a buon fine se viene superata la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service con la configurazione predefinita
    When l'utente tenta di effettuare la modifica parziale dell'e-service in stato DRAFT specificando una descrizione di lunghezza pari a 401 caratteri
    Then si ottiene status code 400

  @eservice_description_max_length
  @happy-path
  Scenario: [ESERVICE_DESCRIPTION_UPDATE_MAXLENGTH_8] Un utente aggiorna un e-service in stato PUBLISHED utilizzando la descrizione della lunghezza massima possibile
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica della descrizione dell'e-service specificando una descrizione di lunghezza pari a 400 caratteri
    Then l'utente è un "admin" di "PA1"
    And l'e-service creato ha una descrizione di 400 caratteri

  @eservice_description_max_length
  @sad-path
  Scenario: [ESERVICE_DESCRIPTION_UPDATE_MAXLENGTH_9] L'aggiornamento di un e-service in stato PUBLISHED non va a buon fine se viene superata la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica della descrizione dell'e-service specificando una descrizione di lunghezza pari a 401 caratteri
    Then si ottiene status code 400

  Scenario: [ESERVICE_UPLOAD_01] Per un e-service in stato DRAFT è possibile allegare tutti i file del tipo previsto dalla piattaforma.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di caricare uno alla volta il seguente insieme di documenti
      | pdf |
      | json |
      | md   |
      | xsd  |
      | yml  |
      | yaml |
      | txt  |
      | wsdl |
    Then tutti i tentativi di caricamento hanno esito positivo

  Scenario: [ESERVICE_UPLOAD_02] Per un e-service in stato DRAFT non è possibile allegare un file se questo
  è di tipo non previsto dalla piattaforma: se il file non ha estensione consentita OPPURE se il file è
  riconosciuto come intrinsecamente non consentito - attraverso controllo su "magic byte" fatto dal backend -
  allora il caricamento del file fallisce.
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di caricare uno alla volta i seguenti tipi documenti, con l'estensione specificata
      | documento | estensione |
      # tipi di file non permessi, con estensione non permessa: ogni file conserva la propria estensione
      | html      | html       |
      | sh        | sh         |
      | bat       | bat        |
      | cmd       | cmd        |
      | js        | js         |
      | bash      | bash       |
      | ps1       | ps1        |
      | png       | png        |
      | docx      | docx        |
      | zip       | zip        |
      | msi       | msi        |
      | exe       | exe        |
      # tipi di file non permessi, con estensione permessa (magic byte riconoscibile)
      | png       | pdf        |
      | docx      | pdf        |
      | zip       | pdf        |
      | msi       | pdf        |
      | exe       | pdf        |
      # tipi di file permessi, con estensione non permessa
      | pdf       | exe        |
      | json      | exe        |
      | md        | exe        |
      | xsd       | exe        |
      | yml       | exe        |
      | yaml      | exe        |
      | txt       | exe        |
      | wsdl      | exe        |
      # tipi di file permessi, senza estensione
      | pdf       |            |
      # tipo di file non permesso, con estensione non permessa
      | html      | exe        |
      # tipo di file permesso, con doppia estensione non permessa
      | pdf       | pdf.exe    |
    Then tutti i tentativi di caricamento hanno esito negativo