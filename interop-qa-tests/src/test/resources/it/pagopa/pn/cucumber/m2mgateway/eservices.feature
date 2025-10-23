@m2mEservices
Feature: Gestione degli eServices attraverso APIs M2M V2

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
  @m2m-false-negative
  Scenario: [M2MG_ESERVICES_3] RED - Accesso negato alla lista degli eServices con token non valido (Scenario 82)
    Given "PA1" ha già creato e pubblicato 1 e-services
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
  @m2m-false-negative
  Scenario: [M2MG_ESERVICES_7] Accesso negato al dettaglio di un eService con token non valido (Scenario 85)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare il record di eService creato
    Then si ottiene lo status code 401
    And eService non restituito

  @sad-path
  @m2m-false-negative
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
  @m2m-false-negative
  Scenario: [M2MG_ESERVICES_16] Accesso negato al recupero di un descriptor con token non valido (Scenario 93)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare il record di descriptor creato
    Then si ottiene lo status code 401
    And descriptor non restituito

  @sad-path
  @m2m-false-negative
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

  # Da qui in poi test di "API V2 Parte 2" https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1812562407/DRAFT+SRS+API+V2+Parte+2#Scenari-di-test
  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_ESERVICES_18] Un utente con ruolo M2M-ADMIN può effettuare la cancellazione di un e-service (Parte2#Scenario intorno a 32)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la cancellazione dell'e-service
    Then si ottiene lo status code 200
    When l'utente tenta di recuperare l'e-service creato
    Then si ottiene lo status code 404

  Scenario: [M2MG_ESERVICES_19] Un utente con ruolo M2M non può effettuare la cancellazione di un e-service (Parte2#Scenario intorno a 34)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la cancellazione dell'e-service
    Then si ottiene lo status code 401

  Scenario: [M2MG_ESERVICES_20] La cancellazione di un e-service non può essere effettuata specificando un id inesistente (Parte2#Scenario intorno a 35)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la cancellazione di un e-service inesistente
    Then si ottiene lo status code 404

  Scenario: [M2MG_ESERVICES_21] La cancellazione di un e-service non può essere effettuata specificando un token non valido (Parte2#Scenario intorno a 36)
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la cancellazione di un e-service inesistente
    Then si ottiene status code 401

  Scenario: [M2MG_ESERVICES_22] La cancellazione di un e-service precedentemente rimosso non può essere effettuata (Parte2#Scenario intorno a 37)
    Given "PA1" ha già creato e pubblicato 1 e-services
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
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la cancellazione di un e-service inesistente
    Then si ottiene lo status code 401

  # Ticket aperto https://pagopa.atlassian.net/browse/PIN-7410
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
    Then si ottiene lo status code 400
    Examples:
      | stato                 |
      | DRAFT                 |
      | PUBLISHED             |
      | DEPRECATED            |
      | ARCHIVED              |

  @m2m-parte2-agosto-rilascio1 @deleghe2
  Scenario: [M2MG_ESERVICES_29_B] Un utente con ruolo M2M-ADMIN non può effettuare riattivazione di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 45)
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la riattivazione dell'e-service
    Then si ottiene lo status code 400

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_ESERVICES_30] Un utente con ruolo M2M-ADMIN non può effettuare riattivazione di un e-service se non è il creatore dello stesso (Parte2#Scenario intorno a 46)
    Given "PA1" ha già creato un e-service con un descrittore in stato "SUSPENDED"
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la riattivazione dell'e-service
    Then si ottiene lo status code 403

  ## EService Patch
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

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_34] Un utente NON può effettuare una modifica parziale di un e-service indicando un token non valido (Parte2#Scenario intorno a 75)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin

#    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la modifica parziale dell'e-service con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service non ha subito modifiche

  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_35_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service in stato diverso da DRAFT (Parte2#Scenario intorno a 76)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale dell'e-service
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |
      | ARCHIVED    |

  @m2m-parte2-agosto-rilascio2 @deleghe2
  Scenario: [M2MG_ESERVICES_35_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 76)
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale dell'e-service
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_36] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service che non gli appartiene (Parte2#Scenario intorno a 77)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA2" con ruolo m2m-admin tenta di effettuare la modifica parziale dell'e-service
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'e-service non ha subito modifiche

  ## EService Patch Delegation
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_37] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale della delega di un e-service in uno degli stati permessi (Parte2#Scenario intorno a 85)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
#    And vengono recuperate e salvate le configurazioni attuali della delega dell'eservice
    # BUG: Possibile bug in quanto non è possibile fare la PATCH aggiornando un solo parametro!
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_38] Un utente con ruolo M2M NON può effettuare una modifica parziale della delega di un e-service (Parte2#Scenario intorno a 87)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_39] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service inesistente (Parte2#Scenario intorno a 88)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega di un e-service inesistente
    Then si ottiene lo status code 404

  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_40] Un utente NON può effettuare una modifica parziale della delega di un e-service indicando un token non valido (Parte2#Scenario intorno a 89)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
#    And viene impostato per l'utente un token m2m non valido
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  Scenario Outline: [M2MG_ESERVICES_41_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service in uno stato DRAFT o ARCHIVED
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | DRAFT       |
      | ARCHIVED    |

  @deleghe2
  Scenario: [M2MG_ESERVICES_41_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche

  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_42] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service che non gli appartiene (Parte2#Scenario intorno a 90)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_42_2] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service indicando le informazioni già presenti (Parte2#Scenario intorno a 91)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service senza apportare cambiamenti
    Then si ottiene lo status code 200
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  ## EService Patch Name
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
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_44] Un utente con ruolo M2M NON può effettuare una modifica parziale del nome di un e-service (Parte2#Scenario intorno a 80)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_45] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del nome di un e-service inesistente (Parte2#Scenario intorno a 81)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome di un e-service inesistente
    Then si ottiene lo status code 404

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
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  Scenario Outline: [M2MG_ESERVICES_47_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del nome di un e-service in stato DRAFT o ARCHIVED
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | DRAFT       |
      | ARCHIVED    |

  @deleghe2
  Scenario: [M2MG_ESERVICES_47_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del nome di un e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche

  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_48] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del nome di un e-service che non gli appartiene (Parte2#Scenario intorno a 83)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  # Ticket aperto https://pagopa.atlassian.net/browse/PIN-7526
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_49] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del nome di un e-service indicando le informazioni già presenti (Parte2#Scenario intorno a 84)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del nome dell'e-service senza apportare cambiamenti
    Then si ottiene lo status code 409
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  ## EService Patch Description
  Scenario Outline: [M2MG_ESERVICES_44] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale della descrizione di un e-service in uno degli stati permessi (Parte2#Scenario intorno a 92)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  Scenario Outline: [M2MG_ESERVICES_45] Un utente con ruolo M2M NON può effettuare una modifica parziale della descrizione di un e-service (Parte2#Scenario intorno a 94)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  Scenario: [M2MG_ESERVICES_46] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service inesistente (Parte2#Scenario intorno a 95)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione di un e-service inesistente
    Then si ottiene lo status code 404

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
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  Scenario Outline: [M2MG_ESERVICES_48_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service in stato DRAFT o ARCHIVED
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | DRAFT       |
      | ARCHIVED    |

  @deleghe2
  Scenario: [M2MG_ESERVICES_48_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche

  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_49_2] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service che non gli appartiene (Parte2#Scenario intorno a 97)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  # Ticket aperto https://pagopa.atlassian.net/browse/PIN-7526
  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_50] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della descrizione di un e-service indicando le informazioni già presenti (Parte2#Scenario intorno a 98)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service senza apportare cambiamenti
    Then si ottiene lo status code 409
    And l'e-service non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |

  # EService Patch Descriptor
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

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_04] Un utente NON può effettuare una modifica parziale del descriptor di un e-service indicando un token non valido (Parte2#Scenario intorno a 103)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
#    And viene impostato per l'utente un token m2m non valido
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service descriptor non ha subito modifiche

  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_ESERVICES_DESCRIPTORS_05_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del descriptor di un e-service in stato non DRAFT (Parte2#Scenario intorno a 104)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service
    Then si ottiene lo status code 400
    And l'e-service descriptor non ha subito modifiche
    Examples:
      | stato       |
      | PUBLISHED   |
      | DEPRECATED  |
      | SUSPENDED   |
      | ARCHIVED    |

  @m2m-parte2-agosto-rilascio2 @deleghe2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_05_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del descriptor di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 104)
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service
    Then si ottiene lo status code 400
    And l'e-service descriptor non ha subito modifiche

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale del descriptor di un e-service che non gli appartiene (Parte2#Scenario intorno a 105)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA2" con ruolo m2m-admin tenta di effettuare la modifica parziale del descriptor dell'e-service
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'e-service descriptor non ha subito modifiche

  # EService Patch Quotas
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

  @m2m-parte2-settembre
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_04] Un utente NON può effettuare una modifica parziale delle quote di un descriptor di un e-service indicando un token non valido (Parte2#Scenario intorno a 233)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service descriptor non ha subito modifiche

  @m2m-parte2-settembre
  Scenario Outline: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_05_A] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote di un descriptor di un e-service in stato DRAFT, DEPRECATED o ARCHIVED (Parte2#Scenario intorno a 234)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service
    Then si ottiene lo status code 400
    And l'e-service descriptor non ha subito modifiche
    Examples:
      | stato       |
      | DRAFT       |
      | ARCHIVED    |

  @m2m-parte2-settembre @deleghe2
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_05_B] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote di un descriptor di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 234)
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service
    Then si ottiene lo status code 400
    And l'e-service descriptor non ha subito modifiche

  @m2m-parte2-settembre
  Scenario: [M2MG_ESERVICES_DESCRIPTORS_QUOTAS_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote di un descriptor di un e-service che non gli appartiene (Parte2#Scenario intorno a 235)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA2" con ruolo m2m-admin tenta di effettuare la modifica parziale delle quote di un descriptor dell'e-service
    Then si ottiene lo status code 404
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'e-service descriptor non ha subito modifiche
