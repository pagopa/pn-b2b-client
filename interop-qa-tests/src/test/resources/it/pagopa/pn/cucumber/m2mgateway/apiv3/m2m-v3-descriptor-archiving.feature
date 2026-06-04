@m2m-v3-manual-archiving-eservice
Feature: Archiviazione manuale di un descrittore

  Scenario Outline: [M2M_V3_MANUAL_ARCHIVING_DESCRIPTOR_1.1] Un ente erogatore di un e-service può avviare via M2M v3 il processo di archiviazione manuale del primo e meno recente descrittore dell'e-service in questione
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "<initialDescriptorState>"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già pubblicato una nuova versione per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And l'utente avvia il processo di archiviazione della vecchia versione con id "%actual" dell'e-service con id "%actual"
    Then si ottiene response status code 200
#    And il processo di archiviazione della vecchia versione dell'e-service è avvenuto con successo
    And la vecchia versione dell'e-service è in stato "<finalDescriptorState>"

    #quando il primo descrittore smetterà di essere il più recente, il suo stato passerà da PUBLISHED a DEPRECATED
    Examples:
      | initialDescriptorState | finalDescriptorState |
      | PUBLISHED              | ARCHIVING            |
      | SUSPENDED              | ARCHIVING_SUSPENDED  |
