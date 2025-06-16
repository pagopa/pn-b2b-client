@m2m-eservices
Feature: Gestione degli eServices

  Scenario Outline: [M2MG_ESERVICES_1] RED - La lista degli eServices può essere visionata da un utente con ruolo M2M o M2M-ADMIN (Scenario 4)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di eService
    And si ottiene lo status code <statusCode>
    Then la lista di eService è presente solo se lo status code è 200
    Examples:
      | ruolo | ruolo-m2m | statusCode |
      | admin | m2m       | 200        |
      | admin | m2m-admin | 200        |


  Scenario: [M2MG_ESERVICES_2] RED - Recupero corretto della lista degli eServices con utente autorizzato (Scenario 81)
    Given "PA2" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di recuperare la lista di eService
    And si ottiene lo status code 200
    Then lista di eService viene restituita

  @m2m-false-negative
  Scenario: [M2MG_ESERVICES_3] RED - Accesso negato alla lista degli eServices con token non valido (Scenario 82)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And viene impostato per l'utente un token m2m scaduto
    When l'utente tenta di recuperare la lista di eService
    And si ottiene lo status code 401
    Then lista di eService non restituita

  Scenario Outline: [M2MG_ESERVICES_4] Un utente con ruolo M2M o M2M-ADMIN può visualizzare un eService specifico (Scenario 5)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di descriptor usando l'eserviceId creato
    Then si ottiene lo status code <statusCode>
    Then la lista di descriptor è presente solo se lo status code è 200
    Examples:
      | ruolo        | ruolo-m2m | statusCode |
      | admin        | m2m       | 200        |
      | api          | m2m       | 200        |
      | security     | m2m       | 200        |
      | api,security | m2m-admin | 200        |
      | support      | m2m-admin | 200        |

  Scenario: [M2MG_ESERVICES_5] Recupero del dettaglio di un eService con utente autorizzato (Scenario 83)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di recuperare la lista di eService
    Then si ottiene lo status code 200
    And eService viene restituito

  @m2m-false-negative
  Scenario: [M2MG_ESERVICES_7] Accesso negato al dettaglio di un eService con token non valido (Scenario 85)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And viene impostato per l'utente un token m2m scaduto
    When l'utente tenta di recuperare il record di eService creato
    Then si ottiene lo status code 401
    And eService non restituito

  @m2m-false-negative
  Scenario: [M2MG_ESERVICES_8] Errore nel recupero del dettaglio di un eService inesistente (Scenario 86)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di recuperare eService con un id inesistente
    Then si ottiene lo status code 404
    And eService non restituito

  Scenario Outline: [M2MG_ESERVICES_9] RED - La lista dei descriptors di un eService può essere visualizzata da un utente con ruolo M2M o M2M-ADMIN (Scenario 6)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di descriptor usando l'eserviceId creato
    Then si ottiene lo status code <statusCode>
    Then la lista di descriptor è presente solo se lo status code è 200
    Examples:
      | ruolo | ruolo-m2m | statusCode |
      | admin | m2m       | 200        |
      | api   | m2m-admin | 200        |

  Scenario: [M2MG_ESERVICES_9] RED - Recupero corretto della lista dei descriptors per un eService con utente autorizzato (Scenario 87)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di recuperare la lista di descriptor usando l'eserviceId creato
    Then si ottiene lo status code 200
    And lista di descriptor viene restituita

  Scenario: [M2MG_ESERVICES_11] RED - Accesso negato alla lista dei descriptors con token non valido (Scenario 89)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And viene impostato per l'utente un token m2m scaduto
    When l'utente tenta di recuperare la lista di descriptor usando l'eserviceId creato
    Then si ottiene lo status code 401
    And lista di descriptor non restituita

  Scenario: [M2MG_ESERVICES_12] RED - Errore nel recupero della lista dei descriptors con eserviceId inesistente (Scenario 90)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di recuperare la lista di descriptor con un eserviceId inesistente
    Then si ottiene lo status code 404
    And lista di descriptor non restituita

  Scenario: [M2MG_ESERVICES_13] Recupero del descriptor di un eService con utente autorizzato (Scenario 7)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di recuperare il record di descriptor creato
    Then si ottiene lo status code 200

  Scenario: [M2MG_ESERVICES_14] Recupero corretto di un descriptor per uno specifico eService (Scenario 91)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di recuperare il record di descriptor creato
    Then si ottiene lo status code 200
    And descriptor viene restituito

  @m2m-false-negative
  Scenario: [M2MG_ESERVICES_16] Accesso negato al recupero di un descriptor con token non valido (Scenario 93)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And viene impostato per l'utente un token m2m scaduto
    When l'utente tenta di recuperare il record di descriptor creato
    Then si ottiene lo status code 401
    And descriptor non restituito

  @m2m-false-negative
  Scenario: [M2MG_ESERVICES_17] Errore nel recupero di un descriptor con eserviceId e descriptorId inesistenti (Scenario 94)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di recuperare descriptor con un id inesistente
    Then si ottiene lo status code 404
    And descriptor non restituito


