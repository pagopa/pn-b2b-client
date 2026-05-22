@manual-archiving-eservice
Feature: Archiviazione manuale di un descrittore

  Scenario Outline: [MANUAL_ARCHIVING_DESCRIPTOR_1] Un ente erogatore di un e-service in stato PUBLISHED può avviare il processo di archiviazione manuale della secondo e meno recente descrittore dell'e-service in questione
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
#    When l'utente archivia il primo e meno recente descrittore dell'e-service
    Then si ottiene status code 204
#    And il descrittore e in stato "ARCHIVING"

    Examples:
      | role         |
      | admin        |
      | api          |
      | api,security |


