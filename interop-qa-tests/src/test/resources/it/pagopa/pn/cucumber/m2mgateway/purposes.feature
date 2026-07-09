@m2m-purposes
Feature: Gestione purposes attraverso APIs M2M V2

  @happy-path
  Scenario Outline: [M2M_PURPOSES_LIST_1] La lista delle finalità può essere visionata da un utente con ruolo M2M o M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 5 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m_2>
    When l'utente tenta di recuperare una lista di 5 finalità create
    Then si ottiene status code 200
    And sono state visualizzate correttamente 5 finalità create
    Examples:
      | ruolo-m2m_2 |
      | m2m         |
      | m2m-admin   |

  @sad-path
  Scenario: [M2M_PURPOSES_LIST_2] Accesso negato alla lista delle finalità con token non valido
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 5 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare una lista di 5 finalità create
    Then si ottiene status code 401

  @happy-path
  Scenario: [M2M_PURPOSES_VERSIONS_1] La creazione di una nuova versione di una finalità può essere effettuata solo da un utente con ruolo M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di creare una nuova versione della finalità aggiornando la stima di carico
    Then si ottiene status code 200
    And la nuova versione della finalità è stata creata correttamente

  @sad-path
  Scenario: [M2M_PURPOSES_VERSIONS_2] La creazione di una nuova versione di una finalità NON può essere effettuata da un utente con ruolo diverso da M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m
    And l'utente tenta di creare una nuova versione della finalità aggiornando la stima di carico
    Then si ottiene status code 403

  @happy-path
  Scenario Outline: [M2M_PURPOSES_VERSIONS_3] La lista delle versioni di una finalità può essere visualizzata da un utente con ruolo M2M o M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente crea una nuova versione della finalità con successo aggiornando la stima di carico
    When l'utente è un "admin" di "PA2" con ruolo M2M <ruolo_m2m>
    And l'utente tenta di visualizzare la lista delle versioni della finalità
    Then si ottiene status code 200
    And sono state visualizzate correttamente 2 versioni della finalità
    Examples:
      | ruolo_m2m |
      | m2m       |
      | m2m-admin |

  @sad-path
  Scenario: [M2M_PURPOSES_VERSIONS_4] La lista delle versioni di una finalità NON può essere visualizzata indicando un auth token non valido
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di visualizzare la lista delle versioni della finalità
    Then si ottiene status code 401

  @sad-path
  Scenario: [M2M_PURPOSES_VERSIONS_5] La lista delle versioni di una finalità inesistente NON può essere visualizzata
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di visualizzare la lista delle versioni di una finalità inesistente
    Then si ottiene status code 404

  @happy-path
  Scenario Outline: [M2M_PURPOSES_VERSIONS_6] Una determinata versione di una finalità può essere visualizzata da un utente con ruolo M2M o M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente crea una nuova versione della finalità con successo aggiornando la stima di carico
    When l'utente è un "admin" di "PA2" con ruolo M2M <ruolo_m2m>
    And l'utente tenta di visualizzare la nuova versione della finalità
    Then si ottiene status code 200
    And la nuova versione della finalità è stata visualizzata correttamente
    Examples:
      | ruolo_m2m |
      | m2m       |
      | m2m-admin |

  @sad-path
  Scenario: [M2M_PURPOSES_VERSIONS_7] Una determinata versione di una finalità NON può essere visualizzata indicando un auth token non valido
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di visualizzare la nuova versione della finalità
    Then si ottiene status code 401

  @sad-path
  Scenario: [M2M_PURPOSES_VERSIONS_8] Una versione inesistente di una finalità inesistente NON può essere visualizzata
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di visualizzare una versione inesistente di una finalità inesistente
    Then si ottiene status code 404

  @sad-path
  Scenario: [M2M_PURPOSES_VERSIONS_9] Una versione inesistente di una finalità esistente NON può essere visualizzata
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di visualizzare una versione inesistente della finalità esistente
    Then si ottiene status code 404

  @happy-path
  Scenario: [M2M_PURPOSES_ACTIVATE_1] Una finalità in stato DRAFT può essere attivata da un utente con ruolo M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta l'attivazione della finalità
    Then si ottiene status code 200

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è stata attivata correttamente

  @sad-path
  Scenario: [M2M_PURPOSES_ACTIVATE_2] Una finalità in stato DRAFT NON può essere attivata da un utente con ruolo M2M
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta l'attivazione della finalità
    Then si ottiene status code 403

  @sad-path
  Scenario: [M2M_PURPOSES_ACTIVATE_3] Una finalità inesistente NON può essere attivata
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta l'attivazione di una finalità inesistente
    Then si ottiene status code 404

  @sad-path
  Scenario: [M2M_PURPOSES_ACTIVATE_4] Una finalità in stato DRAFT NON può essere attivata specificando un auth token non valido
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta l'attivazione della finalità
    Then si ottiene status code 401

  @sad-path
  Scenario Outline: [M2M_PURPOSES_ACTIVATE_5] Una finalità in uno stato diverso da DRAFT NON può essere attivata
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "<state>" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta l'attivazione della finalità
    Then si ottiene status code 409
    Examples:
      | state                |
      | ACTIVE               |
      | SUSPENDED            |
      | REJECTED             |
      | WAITING_FOR_APPROVAL |
      | ARCHIVED             |

  @sad-path
  Scenario: [M2M_PURPOSES_ACTIVATE_6] Una finalità in stato DRAFT NON può essere attivata da parte di un ente diverso dal creatore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta l'attivazione della finalità
    Then si ottiene status code 403

  @happy-path
  Scenario: [M2M_PURPOSES_SUSPEND_1] Una finalità in stato ACTIVE può essere sospesa da un utente con ruolo M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta la sospensione della finalità
    Then si ottiene lo status code 200

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato SUSPENDED

  @sad-path
  Scenario: [M2M_PURPOSES_SUSPEND_2] Una finalità in stato ACTIVE NON può essere sospesa da un utente con ruolo M2M
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta la sospensione della finalità
    Then si ottiene lo status code 403

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato ACTIVE

  @sad-path
  Scenario: [M2M_PURPOSES_SUSPEND_3] Sospensione fallita di una finalità con purposeId inesistente
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta la sospensione di una finalità inesistente
    Then si ottiene lo status code 404

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato ACTIVE

  @sad-path
  Scenario: [M2M_PURPOSES_SUSPEND_4] Una finalità NON può essere sospesa specificando un token non valido
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta la sospensione della finalità
    Then si ottiene lo status code 401

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato ACTIVE

  # Ticket associati (a cui si deve l'eterogeneità dei codici di risposta previsti)
    # https://pagopa.atlassian.net/browse/PIN-6999
    # https://pagopa.atlassian.net/browse/PIN-7024
  @ko-nrt-08072026
  Scenario Outline: [M2M_PURPOSES_SUSPEND_5_A] Una finalità in stato diverso da ACTIVE NON può essere sospesa
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "<state>" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta la sospensione della finalità
    Then si ottiene lo status code <code>

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And la finalità è in stato <state>

    @happy-path
    Examples:
      | state     | code |
      | SUSPENDED | 200  |

    @sad-path
    Examples:
      | state                | code |
      | DRAFT                | 400  |
      | WAITING_FOR_APPROVAL | 409  |
      | ARCHIVED             | 400  |

  @sad-path
  Scenario: [M2M_PURPOSES_SUSPEND_5_B] Una finalità in stato REJECTED NON può essere sospesa
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And "PA1" ha già rifiutato l'aggiornamento della stima di carico per quella finalità
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta la sospensione della finalità
    Then si ottiene lo status code 409

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato REJECTED

  @sad-path
  Scenario: [M2M_PURPOSES_SUSPEND_6] Una finalità NON può essere sospesa da utente che non è né erogatore né fruitore
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    When l'utente tenta la sospensione della finalità
    Then si ottiene lo status code 403

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And la finalità è in stato ACTIVE

  @sad-path @ko-nrt-08072026
  Scenario Outline: [M2MG_PURPOSES_33] Archiviazione di una finalità non consentita con ruolo M2M (Scenario 52)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "<stato>" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di archiviare purpose
    Then si ottiene lo status code 403

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And purpose in stato <stato>
    Examples:
      | stato     |
      | ACTIVE    |
      | SUSPENDED |

  @happy-path @ko-nrt-08072026
  Scenario Outline: [M2MG_PURPOSES_34] Archiviazione di una finalità in stato <stato> con utente autorizzato (Scenario 119)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "<stato>" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di archiviare purpose
    Then si ottiene lo status code 200

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato ARCHIVED
    Examples:
      | stato     |
      | ACTIVE    |
      | SUSPENDED |

  @sad-path
  Scenario: [M2MG_PURPOSES_36] Archiviazione fallita di una finalità inesistente (Scenario 121)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di archiviare purpose con un id inesistente
    Then si ottiene lo status code 404

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato ACTIVE

  @sad-path
  Scenario: [M2MG_PURPOSES_37] Archiviazione fallita di una finalità con token non valido (Scenario 122)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di archiviare purpose
    Then si ottiene lo status code 401

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato ACTIVE

  @sad-path
  Scenario Outline: [M2MG_PURPOSES_39_A] Archiviazione fallita di una finalità in stato non valido (Scenario 124)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "<stato>" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di archiviare purpose
    Then si ottiene lo status code <code>

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And purpose in stato <stato>

    Examples:
      | stato                | code |
      | DRAFT                | 400  |
      | ARCHIVED             | 400  |
      | WAITING_FOR_APPROVAL | 409  |

  @sad-path
  Scenario: [M2MG_PURPOSES_39_B] Archiviazione fallita di una finalità in stato REJECTED (Scenario 124)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And "PA1" ha già rifiutato l'aggiornamento della stima di carico per quella finalità
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di archiviare purpose
    Then si ottiene lo status code 409

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And purpose in stato REJECTED

  @sad-path
  Scenario Outline: [M2MG_PURPOSES_40] Archiviazione negata da utente non creatore (Scenario 125)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "<stato>" per quell'eservice
    And l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    When l'utente tenta di archiviare purpose
    Then si ottiene lo status code 403

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    # così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And purpose in stato <stato>
    Examples:
      | stato     |
      | ACTIVE    |
      | SUSPENDED |

  @sad-path
  Scenario: [M2MG_PURPOSES_42] Approvazione negata per utente con ruolo M2M (Scenario 53)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di approvare purpose
    Then si ottiene lo status code 403

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato WAITING_FOR_APPROVAL

  @happy-path
  Scenario: [M2MG_PURPOSES_43] Approvazione di una finalità in stato waiting for approval con utente autorizzato (Scenario 126)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di approvare purpose
    Then si ottiene lo status code 200

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato ACTIVE

  @sad-path
  Scenario: [M2MG_PURPOSES_45] Errore approvazione finalità con purposeId inesistente (Scenario 128)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di approvare purpose con un id inesistente
    Then si ottiene lo status code 404
     # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And la finalità è in stato WAITING_FOR_APPROVAL

  @sad-path
  Scenario: [M2MG_PURPOSES_46] Approvazione fallita di una finalità con token non valido (Scenario 129)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di approvare purpose
    Then si ottiene lo status code 401
     # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And la finalità è in stato WAITING_FOR_APPROVAL

  @sad-path
  Scenario Outline: [M2MG_PURPOSES_48_A] Approvazione fallita di una finalità in stato non valido (Scenario 131)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "<stato>" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di approvare purpose
    Then si ottiene lo status code 409
    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And purpose in stato <stato>
    Examples:
      | stato     |
      | ACTIVE    |
      | SUSPENDED |
      | ARCHIVED  |
      | DRAFT     |

  @sad-path
  Scenario: [M2MG_PURPOSES_48_B] Approvazione fallita di una finalità in stato REJECTED (Scenario 131)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And "PA1" ha già rifiutato l'aggiornamento della stima di carico per quella finalità
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di approvare purpose
    Then si ottiene lo status code 409

    # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
    #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And purpose in stato REJECTED

  @sad-path
  Scenario: [M2MG_PURPOSES_49] Approvazione negata da utente non erogatore (Scenario 132)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di approvare purpose
    Then si ottiene lo status code 403
     # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And la finalità è in stato WAITING_FOR_APPROVAL

  @sad-path
  Scenario: [M2MG_PURPOSES_51] Riattivazione negata per utente con ruolo M2M (Scenario 55)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di riattivare purpose
    Then si ottiene lo status code 403
     # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And la finalità è in stato SUSPENDED

  @happy-path
  Scenario: [M2MG_PURPOSES_52] Riattivazione di una finalità in stato sospeso con utente autorizzato (Scenario 133)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di riattivare purpose
    Then si ottiene lo status code 200
     # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And la finalità è in stato ACTIVE

  @sad-path
  Scenario: [M2MG_PURPOSES_54] Riattivazione fallita di una finalità con purposeId inesistente (Scenario 135)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di riattivare purpose con un id inesistente
    Then si ottiene lo status code 404

     # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"

    And la finalità è in stato SUSPENDED

  @sad-path @ko-nrt-08072026
  Scenario: [M2MG_PURPOSES_55] Riattivazione fallita di una finalità con token non valido (Scenario 136)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di riattivare purpose
    Then si ottiene lo status code 401
     # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And la finalità è in stato SUSPENDED

  @sad-path
  Scenario Outline: [M2MG_PURPOSES_57_A] Riattivazione fallita di una finalità in stato non sospeso (Scenario 138)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "<stato>" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di riattivare purpose
    Then si ottiene lo status code 409
      # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And purpose in stato <stato>
    Examples:
      | stato                |
      | ACTIVE               |
      | DRAFT                |
      | ARCHIVED             |
      | WAITING_FOR_APPROVAL |

  @sad-path
  Scenario: [M2MG_PURPOSES_57_B] Riattivazione fallita di una finalità in stato REJECTED (Scenario 138)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And "PA1" ha già rifiutato l'aggiornamento della stima di carico per quella finalità
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di riattivare purpose
    Then si ottiene lo status code 409
      # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And purpose in stato REJECTED

  @sad-path
  Scenario: [M2MG_PURPOSES_58] Riattivazione negata da utente non erogatore e non fruitore (Scenario 139)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    And l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    When l'utente tenta di riattivare purpose
    Then si ottiene lo status code 403
      # TODO temporaneo, rimuovere quando sarà risolto il bug della API m2m di GET purpose,
        #  così da evitare di dover ri-produrre un token per poter usare la API bff
    Given l'utente è un "admin" di "PA2"
    And la finalità è in stato SUSPENDED

  # Da qui in poi test di "API V2 Parte 2" https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1812562407/DRAFT+SRS+API+V2+Parte+2#Scenari-di-test
  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2M_PURPOSES_AGREEMENT_1] La richiesta di fruizione correlata a una finalità può essere visualizzata da un utente con ruolo M2M-ADMIN o M2M (Parte2#Scenario 20)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente è un "admin" di "PA2" con ruolo M2M <ruolo-m2m>
    And l'utente tenta di ottenere la richiesta di fruizione correlata alla finalità
    Then si ottiene status code 200
    And la richiesta di fruizione è stata correttamente visualizzata in stato "ACTIVE"
    Examples:
      | ruolo-m2m |
      | m2m-admin |
      | m2m       |

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_PURPOSES_AGREEMENT_2] La richiesta di fruizione correlata a una finalità non può essere visualizzata specificando un token non valido (Parte2#Scenario 22)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di ottenere la richiesta di fruizione correlata a una finalità inesistente
    Then si ottiene status code 401

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_PURPOSES_AGREEMENT_3] La richiesta di fruizione correlata a una finalità non può essere visualizzata specificando un id inesistente (Parte2#Scenario 23)
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di ottenere la richiesta di fruizione correlata a una finalità inesistente
    Then si ottiene status code 404

  @m2m-agreements-parte2-luglio @ko-nrt-08072026
  Scenario Outline: [M2M_PURPOSES_DOCUMENT_1] Il documento dell'analisi del rischio correlato a una finalità può essere visualizzato da un utente con ruolo M2M-ADMIN o M2M (Parte2#Scenario 24)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente è un "admin" di "PA2" con ruolo M2M <ruolo-m2m>
    And l'utente tenta di ottenere il documento dell'analisi del rischio correlato alla finalità
    Then si ottiene status code 200
    And il file restituito non è vuoto
    Examples:
      | ruolo-m2m |
      | m2m-admin |
      | m2m       |

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_PURPOSES_DOCUMENT_2] Il documento dell'analisi del rischio correlato a una finalità non può essere visualizzato specificando un token non valido (Parte2#Scenario 26)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di ottenere il documento dell'analisi del rischio correlato a una finalità inesistente
    Then si ottiene status code 401

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_PURPOSES_DOCUMENT_3] Il documento dell'analisi del rischio correlato a una finalità non può essere visualizzato specificando un id inesistente (Parte2#Scenario 27)
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di ottenere il documento dell'analisi del rischio correlato a una finalità inesistente
    Then si ottiene status code 404

  @m2m-parte2-agosto
  @m2m-parte2-agosto-rilascio2
  @purpose-m2m-patch
  @m2m-patch
  Scenario: [M2M_PURPOSES_PATCH_1] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale di una finalità in stato DRAFT (Parte2#Scenario intorno a 127)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della finalità
    Then si ottiene lo status code 200
    And la finalità restituita è coerente con le modifiche effettuate
    And la finalità è stata parzialmente modificata correttamente
    When l'utente tenta di effettuare la modifica parziale della finalità specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 200
    And la finalità restituita è coerente con le modifiche effettuate
    And la finalità è stata parzialmente modificata correttamente

  @m2m-patch
  @purpose-m2m-patch @ko-nrt-08072026
  Scenario Outline: [M2M_PATCH_DRAFT_PURPOSE_1.1] - Casi negativi
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When viene aggiornato il draft purpose con purposeId "<purposeId>" e title "<title>", description "<description>", isFreeOfCharge "<isFreeOfCharge>", freeOfChargeReason "<freeOfChargeReason>", riskAnalysisForm "<riskAnalysisForm>", dailyCalls "<dailyCalls>"
    Then si ottiene lo status code <statusCode>

    Examples:
      | purposeId | title                                                         | description                                                                                                                                                                                                                                                  | isFreeOfCharge | freeOfChargeReason | riskAnalysisForm | dailyCalls | statusCode |
    # title troppo corto (< 5)
      | %actual   | abcd                                                          | descrizione valida                                                                                                                                                                                                                                           | true           | reason             | actual           | 10         | 400        |

    # title troppo lungo (> 60)
      | %actual   | xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx | descrizione valida                                                                                                                                                                                                                                           | true           | reason             | actual           | 10         | 400        |

    # description troppo corta (< 10)
      | %actual   | titolo valido                                                 | short                                                                                                                                                                                                                                                        | true           | reason             | actual           | 10         | 400        |

    # description troppo lunga (> 250)
      | %actual   | titolo valido                                                 | xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx | true           | reason             | actual           | 10         | 400        |

    # dailyCalls < minimum (1)
      | %actual   | titolo valido                                                 | descrizione valida                                                                                                                                                                                                                                           | true           | reason             | actual           | 0          | 400        |

    # dailyCalls > maximum (1_000_000_000)
      | %actual   | titolo valido                                                 | descrizione valida                                                                                                                                                                                                                                           | true           | reason             | actual           | 1000000001 | 400        |

    Examples:
      | purposeId                            | title         | description        | isFreeOfCharge | freeOfChargeReason | riskAnalysisForm | dailyCalls | statusCode |
    # UUID valido ma non presente a sistema
      | %random                              | titolo valido | descrizione valida | true           | reason             | actual           | 10         | 404        |

    # UUID valido ma sicuramente inesistente
      | 00000000-0000-0000-0000-000000000000 | titolo valido | descrizione valida | true           | reason             | actual           | 10         | 404        |

  @m2m-patch
  @purpose-m2m-patch
  Scenario Outline: [M2M_PATCH_DRAFT_PURPOSE_1.2] - Risk analysis invalida
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When viene aggiornato il draft purpose con purposeId "<purposeId>" e title "<title>", description "<description>", isFreeOfCharge "<isFreeOfCharge>", freeOfChargeReason "<freeOfChargeReason>", riskAnalysisForm "<riskAnalysisForm>", dailyCalls "<dailyCalls>"
    Then si ottiene lo status code <statusCode>

    Examples:
      | purposeId | title         | description        | isFreeOfCharge | freeOfChargeReason | riskAnalysisForm | dailyCalls | statusCode |

    # riskAnalysisForm semanticamente invalido
      | %actual   | titolo valido | descrizione valida | true           | reason             | %invalid         | 10         | 200        |


  # Aggiunto a posteriori della stesura degli scenari di test per verificare l'affermazione
  # "Il controllo completo della validità della RA viene applicato in fase di attivazione (da Draft a Active)."
  # in https://pagopa.atlassian.net/browse/PIN-9164?focusedCommentId=291410
  @m2m-patch
  @purpose-m2m-patch
  Scenario: [M2M_PURPOSE_PUBLISH_INVALID_RA] - L'attivazione di una finalità contenente una risk analysis errata deve condurre ad un errore
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And viene aggiornato il draft purpose con purposeId "%actual" e title "titolo valido", description "descrizione valida", isFreeOfCharge "true", freeOfChargeReason "reason", riskAnalysisForm "%invalid", dailyCalls "10"
    When l'utente tenta l'attivazione della finalità
    Then si ottiene lo status code 400

  @m2m-parte2-agosto
  @m2m-parte2-agosto-rilascio2
  @purpose-m2m-patch
  @m2m-patch
  Scenario: [M2M_PURPOSES_PATCH_2] Un utente con ruolo M2M NON può effettuare una modifica parziale di una finalità (Parte2#Scenario intorno a 129)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale della finalità
    Then si ottiene lo status code 403
    And la finalità non ha subito modifiche

  @m2m-parte2-agosto
  @m2m-parte2-agosto-rilascio2
  @purpose-m2m-patch
  @m2m-patch
  Scenario: [M2M_PURPOSES_PATCH_3] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una finalità inesistente (Parte2#Scenario intorno a 130)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale di una finalità inesistente
    Then si ottiene lo status code 404

  @m2m-parte2-agosto
  @m2m-parte2-agosto-rilascio2
  @purpose-m2m-patch
  @m2m-patch
  Scenario: [M2M_PURPOSES_PATCH_4] Un utente NON può effettuare una modifica parziale di una finalità indicando un token non valido (Parte2#Scenario intorno a 131)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale della finalità con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    Then la finalità non ha subito modifiche

  @m2m-parte2-agosto
  @m2m-parte2-agosto-rilascio2
  @purpose-m2m-patch
  @m2m-patch @ko-nrt-08072026
  Scenario Outline: [M2M_PURPOSES_PATCH_5] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una finalità in stato diverso da DRAFT (Parte2#Scenario intorno a 132)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "<stato>" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della finalità
    Then si ottiene lo status code 400
    And la finalità non ha subito modifiche
    Examples:
      | stato                |
      | ACTIVE               |
      | SUSPENDED            |
      | REJECTED             |
      | ARCHIVED             |
      | WAITING_FOR_APPROVAL |

  @m2m-parte2-agosto
  @m2m-parte2-agosto-rilascio2
  @purpose-m2m-patch
  @reversePurpose
  @m2m-patch
  Scenario: [M2M_PURPOSES_PATCH_6] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una finalità che non gli appartiene (Parte2#Scenario intorno a 133)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When "GSP" con ruolo m2m-admin tenta di effettuare la modifica parziale della finalità
    Then si ottiene lo status code 403
    And la finalità non ha subito modifiche

  @m2m-patch
  @m2m-parte2-settembre @reversePurpose
  Scenario: [M2M_REVERSE_PURPOSE_PATCH_1] Un utente con ruolo M2M-ADMIN può effettuare la modifica parziale di una finalità associata ad un e-service ad erogazione inversa
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato una finalità in stato "DRAFT" per quell'eservice associando quell'analisi del rischio creata dall'erogatore
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa
    Then si ottiene status code 200
    And la finalità restituita è coerente con le modifiche effettuate
    And la finalità è stata parzialmente modificata correttamente
    When l'utente tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 200
    And la finalità restituita è coerente con le modifiche effettuate
    And la finalità è stata parzialmente modificata correttamente

  @m2m-patch @ko-nrt-08072026
  Scenario Outline: [M2M_PATCH_REVERSE_PURPOSE_1.1] - Casi negativi (vincoli OpenAPI)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato una finalità in stato "DRAFT" per quell'eservice associando quell'analisi del rischio creata dall'erogatore
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And viene aggiornata la finalità ad erogazione inversa con purposeId "<purposeId>" e title "<title>", description "<description>", isFreeOfCharge "<isFreeOfCharge>", freeOfChargeReason "<freeOfChargeReason>", dailyCalls "<dailyCalls>"
    Then si ottiene lo status code <statusCode>

    Examples:
      | purposeId                            | title                                                         | description                                                                                                                                                                                                                                                 | isFreeOfCharge | freeOfChargeReason | dailyCalls | statusCode |
    # title troppo corto (< 5)
      | %actual                              | abcd                                                          | descrizione valida                                                                                                                                                                                                                                          | true           | reason             | 10         | 400        |
    # title troppo lungo (> 60)
      | %actual                              | xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx | descrizione valida                                                                                                                                                                                                                                          | true           | reason             | 10         | 400        |

    # description troppo corta (< 10)
      | %actual                              | titolo valido                                                 | short                                                                                                                                                                                                                                                       | true           | reason             | 10         | 400        |
    # description troppo lunga (> 250)
      | %actual                              | titolo valido                                                 | xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx | true           | reason             | 10         | 400        |

    # dailyCalls sotto minimo (< 1)
      | %actual                              | titolo valido                                                 | descrizione valida                                                                                                                                                                                                                                          | true           | reason             | 0          | 400        |
    # dailyCalls sopra massimo (> 1_000_000_000)
      | %actual                              | titolo valido                                                 | descrizione valida                                                                                                                                                                                                                                          | true           | reason             | 1000000001 | 400        |

    # purposeId inesistente (UUID valido ma non presente)
      | %random                              | titolo valido                                                 | descrizione valida                                                                                                                                                                                                                                          | true           | reason             | 10         | 404        |
    # purposeId sicuramente inesistente
      | 00000000-0000-0000-0000-000000000000 | titolo valido                                                 | descrizione valida                                                                                                                                                                                                                                          | true           | reason             | 10         | 404        |

  @m2m-patch
  @m2m-parte2-settembre @reversePurpose
  Scenario: [M2M_REVERSE_PURPOSE_PATCH_2] Un utente con ruolo M2M NON può effettuare una modifica parziale di una finalità associata ad un e-service ad erogazione inversa
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato una finalità in stato "DRAFT" per quell'eservice associando quell'analisi del rischio creata dall'erogatore
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa
    Then si ottiene lo status code 403
    And la finalità non ha subito modifiche

  @m2m-parte2-settembre @reversePurpose
  Scenario: [M2M_REVERSE_PURPOSE_PATCH_3] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una finalità inesistente associabile ad un e-service ad erogazione inversa inesistente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale di una finalità ad erogazione inversa inesistente
    Then si ottiene lo status code 404

  @m2m-patch
  @m2m-parte2-settembre @reversePurpose
  Scenario: [M2M_REVERSE_PURPOSE_PATCH_4] Un utente NON può effettuare una modifica parziale di una finalità associata ad un e-service ad erogazione inversa indicando un token non valido
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato una finalità in stato "DRAFT" per quell'eservice associando quell'analisi del rischio creata dall'erogatore
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    Then la finalità non ha subito modifiche

  # Ticket aperto https://pagopa.atlassian.net/browse/PIN-7808
  @m2m-patch
  @m2m-parte2-settembre @reversePurpose @ko-nrt-08072026
  Scenario Outline: [M2M_REVERSE_PURPOSE_PATCH_5] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una finalità associata ad un e-service ad erogazione inversa in stato diverso da DRAFT
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato una finalità in stato "<stato>" per quell'eservice associando quell'analisi del rischio creata dall'erogatore
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa
    Then si ottiene lo status code 400
    And la finalità non ha subito modifiche
    Examples:
      | stato                |
      | ACTIVE               |
      | SUSPENDED            |
      | REJECTED             |
      | ARCHIVED             |
      | WAITING_FOR_APPROVAL |

  @m2m-patch
  @m2m-parte2-settembre @reversePurpose
  Scenario: [M2M_REVERSE_PURPOSE_PATCH_6] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una finalità associata ad un e-service ad erogazione inversa che non gli appartiene
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in modalità "RECEIVE" con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato una finalità in stato "DRAFT" per quell'eservice associando quell'analisi del rischio creata dall'erogatore
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When "GSP" con ruolo m2m-admin tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa
    Then si ottiene lo status code 403
    And la finalità non ha subito modifiche
