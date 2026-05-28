@manual-archiving-eservice
Feature: Archiviazione manuale di un e-service

  Scenario Outline: [MANUAL_ARCHIVING_ESERVICE_1.1] Un ente erogatore di un e-service in stato PUBLISHED può avviare il processo di archiviazione manuale dell'e-service
    Given l'utente è un "<role>" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialFirstDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente archivia la vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 204
    And la vecchia versione dell'e-service è in stato "<finalFirstDescriptorState>"
    And la versione più recente dell'e-service è in stato "ARCHIVING"

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | role         | initialFirstDescriptorState | finalFirstDescriptorState |
      | admin        | PUBLISHED                   | ARCHIVING                 |
      | api          | PUBLISHED                   | ARCHIVING                 |
      | api,security | PUBLISHED                   | ARCHIVING                 |
    #primo descrittore in stato SUSPENDED
      | admin        | SUSPENDED                   | ARCHIVING_SUSPENDED       |
      | api          | SUSPENDED                   | ARCHIVING_SUSPENDED       |
      | api,security | SUSPENDED                   | ARCHIVING_SUSPENDED       |

