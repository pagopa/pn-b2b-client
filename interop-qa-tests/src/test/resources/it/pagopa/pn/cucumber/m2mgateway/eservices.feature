Feature: Gestione degli eServices

  Scenario Outline: [M2MG_ESERVICES_1] RED - La lista degli eServices può essere visionata da un utente con ruolo M2M o M2M-ADMIN (Scenario 4)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista completa degli eServices
    Then si ottiene lo status code <statusCode>
    And gli eServices sono stati recuperati correttamente
    Examples:
      | ruolo        | ruolo-m2m | statusCode |
      | admin        | m2m       | 200        |
      | api          | m2m       | 403        |
      | security     | m2m       | 403        |
      | api,security | m2m       | 403        |
      | support      | m2m       | 403        |

  Scenario: [M2MG_ESERVICES_2] RED - Recupero corretto della lista degli eServices con utente autorizzato (Scenario 81)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name       |
      | eservice-test-1 | Anagrafe   |
      | eservice-test-2 | Tributi    |
      | eservice-test-3 | Istruzione |
    When l'utente tenta di recuperare la lista completa degli eServices
    Then si ottiene lo status code 200
    And viene restituito l'elenco degli eServices

  Scenario: [M2MG_ESERVICES_3] RED - Accesso negato alla lista degli eServices con token non valido (Scenario 82)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente possiede un token non valido
    When l'utente tenta di recuperare la lista completa degli eServices
    Then si ottiene lo status code 401
    And l'elenco degli eServices non viene restituito

  Scenario Outline: [M2MG_ESERVICES_4] Un utente con ruolo M2M o M2M-ADMIN può visualizzare un eService specifico (Scenario 5)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    When l'utente tenta di recuperare il dettaglio dell'eService "eservice-test-1"
    Then si ottiene lo status code <statusCode>
    And viene restituito il dettaglio dell'eService richiesto
    Examples:
      | ruolo        | ruolo-m2m | statusCode |
      | admin        | m2m       | 200        |
      | api          | m2m       | 403        |
      | security     | m2m       | 403        |
      | api,security | m2m       | 403        |
      | support      | m2m       | 403        |

  Scenario: [M2MG_ESERVICES_5] Recupero del dettaglio di un eService con utente autorizzato (Scenario 83)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    When l'utente tenta di recuperare il dettaglio dell'eService "eservice-test-1"
    Then si ottiene lo status code 200
    And viene restituito il dettaglio dell'eService richiesto

  Scenario: [M2MG_ESERVICES_6] Accesso negato al dettaglio dell'eService con eserviceId invalido (Scenario 84)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    When l'utente tenta di recuperare il dettaglio dell'eService "null"
    Then si ottiene lo status code 400
    And il dettaglio dell'eService non viene restituito

  Scenario: [M2MG_ESERVICES_7] Accesso negato al dettaglio di un eService con token non valido (Scenario 85)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente possiede un token non valido
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    When l'utente tenta di recuperare il dettaglio dell'eService "eservice-test-1"
    Then il sistema restituisce lo status code 401
    And il dettaglio dell'eService non viene restituito

  Scenario: [M2MG_ESERVICES_8] Errore nel recupero del dettaglio di un eService inesistente (Scenario 86)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di recuperare il dettaglio dell'eService "id-inesistente"
    Then il sistema restituisce lo status code 404
    And il dettaglio dell'eService non viene restituito

  Scenario Outline: [M2MG_ESERVICES_9] RED - La lista dei descriptors di un eService può essere visualizzata da un utente con ruolo M2M o M2M-ADMIN (Scenario 6)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    When l'utente tenta di recuperare la lista dei descriptors dell'eService "eservice-test-1"
    Then si ottiene lo status code <statusCode>
    And i descriptors dell'eService sono stati recuperati correttamente
    Examples:
      | ruolo        | ruolo-m2m | statusCode |
      | admin        | m2m       | 200        |
      | api          | m2m       | 403        |
      | security     | m2m       | 403        |
      | api,security | m2m       | 403        |
      | support      | m2m       | 403        |

  Scenario: [M2MG_ESERVICES_9] RED - Recupero corretto della lista dei descriptors per un eService con utente autorizzato (Scenario 87)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    When l'utente tenta di recuperare la lista dei descriptors dell'eService "eservice-test-1"
    Then si ottiene lo status code 200
    And viene restituita la lista dei descriptors dell'eService

  Scenario: [M2MG_ESERVICES_10] RED - Accesso negato alla lista dei descriptors con eserviceId nullo (Scenario 88)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    When l'utente tenta di recuperare la lista dei descriptors dell'eService "null"
    Then si ottiene lo status code 400
    And la lista dei descriptors dell'eService non viene restituita

  Scenario: [M2MG_ESERVICES_11] RED - Accesso negato alla lista dei descriptors con token non valido (Scenario 89)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente possiede un token non valido
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    When l'utente tenta di recuperare la lista dei descriptors dell'eService "eservice-test-1"
    Then si ottiene lo status code 401
    And la lista dei descriptors dell'eService non viene restituita

  Scenario: [M2MG_ESERVICES_12] RED - Errore nel recupero della lista dei descriptors con eserviceId inesistente (Scenario 90)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    When l'utente tenta di recuperare la lista dei descriptors dell'eService "id-inesistente"
    Then si ottiene lo status code 404
    And la lista dei descriptors dell'eService non viene restituita

  Scenario: [M2MG_ESERVICES_13] Recupero del descriptor di un eService con utente autorizzato (Scenario 7)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    And viene effettuata la creazione dei descriptors:
      | descriptorId   | eserviceId      |
      | descriptor-001 | eservice-test-1 |
    When l'utente tenta di recuperare il descriptor "descriptor-001" dell'eService "eservice-test-1"
    Then si ottiene lo status code 200
    And viene restituito il dettaglio del descriptor richiesto

  Scenario: [M2MG_ESERVICES_14] Recupero corretto di un descriptor per uno specifico eService (Scenario 91)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    And viene effettuata la creazione dei descriptors:
      | descriptorId   | eserviceId      |
      | descriptor-001 | eservice-test-1 |
    When l'utente tenta di recuperare il descriptor "descriptor-001" dell'eService "eservice-test-1"
    Then si ottiene lo status code 200
    And viene restituito il dettaglio del descriptor richiesto

  Scenario: [M2MG_ESERVICES_15] Errore nel recupero di un descriptor con eserviceId e descriptorId nulli (Scenario 92)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    And viene effettuata la creazione dei descriptors:
      | descriptorId   | eserviceId      |
      | descriptor-001 | eservice-test-1 |
    When l'utente tenta di recuperare il descriptor "null" dell'eService "null"
    Then si ottiene lo status code 400
    And il dettaglio del descriptor non viene restituito

  Scenario: [M2MG_ESERVICES_16] Accesso negato al recupero di un descriptor con token non valido (Scenario 93)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente possiede un token non valido
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    And viene effettuata la creazione dei descriptors:
      | descriptorId   | eserviceId      |
      | descriptor-001 | eservice-test-1 |
    When l'utente tenta di recuperare il descriptor "descriptor-001" dell'eService "eservice-test-1"
    Then si ottiene lo status code 401
    And il dettaglio del descriptor non viene restituito

  Scenario: [M2MG_ESERVICES_17] Errore nel recupero di un descriptor con eserviceId e descriptorId inesistenti (Scenario 94)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene effettuata la creazione degli eServices:
      | eserviceId      | name     |
      | eservice-test-1 | Anagrafe |
    And viene effettuata la creazione dei descriptors:
      | descriptorId   | eserviceId      |
      | descriptor-001 | eservice-test-1 |
    When l'utente tenta di recuperare il descriptor "descriptor-999" dell'eService "eservice-999"
    Then si ottiene lo status code 404
    And il dettaglio del descriptor non viene restituito


