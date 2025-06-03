Feature: Gestione degli agreements

  Scenario Outline: [M2M_AGREEMENTS_LIST_1] La lista degli agreements può essere visionata da un utente con ruolo M2M o M2M-ADMIN
    Given "PA1" ha già creato e pubblicato 5 e-services
    And l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    And "<ente>" ha un agreement m2m attivo per ciascun e-service di "PA1"
    When l'utente tenta di recuperare la lista completa degli agreements
    Then si ottiene lo status code 200
    And sono stati visualizzati correttamente 5 agreements
    Examples:
      | ruolo        | ruolo-m2m  |
      | admin        | m2m-admin  |
      | api          | m2m-admin  |
      | security     | m2m-admin  |
      | api,security | m2m-admin  |
      | support      | m2m-admin  |
      | admin        | m2m        |
      | api          | m2m        |
      | security     | m2m        |
      | api,security | m2m        |
      | support      | m2m        |

  Scenario: [M2MG_AGREEMENTS_2] Recupero dell’elenco delle richieste di fruizione con utente autorizzato (Scenario 65)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuato la creazione di un agreement con successo
    When l'utente tenta di recuperare la lista completa degli agreements
    Then si ottiene lo status code 200
    And gli agreements sono stati recuperati correttamente

  Scenario: [M2MG_AGREEMENTS_3] Accesso negato alla lista degli agreements con token non valido (Scenario 66)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente possiede un token non valido
    When l'utente tenta di recuperare la lista completa degli agreements
    Then si ottiene lo status code 401
    And gli agreements non sono stati recuperati correttamente
