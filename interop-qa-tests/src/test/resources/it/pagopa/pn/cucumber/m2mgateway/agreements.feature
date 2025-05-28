Feature: Gestione degli agreements
  Scenario Outline: [M2MG_AGREEMENTS_1] La lista degli agreements può essere visionata da un utente con ruolo M2M o M2M-ADMIN (Scenario 2)
    Given l'utente è un "<ruolo>" di "PA1" con ruolo M2M <ruolo-m2m>
    And l'utente è amministratore del client
    And viene effettuato la creazione di un agreement con successo
    When l'utente tenta di recuperare la lista completa degli agreements
    Then si ottiene lo status code <statusCode>
    And gli agreements sono stati recuperati correttamente
    Examples:
      | ruolo        | ruolo-m2m  | statusCode  |
      | admin        | m2m        | 200         |
      | api          | m2m        | 403         |
      | security     | m2m        | 403         |
      | api,security | m2m        | 403         |
      | support      | m2m        | 403         |

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
