Feature: Gestione degli agreements
  Scenario Outline: [M2MG_AGREEMENTS_1] La lista degli agreements può essere visionata da un utente con ruolo M2M o M2M-ADMIN
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