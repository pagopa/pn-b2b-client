@m2m-apiv3-agreements
Feature: Gestione richieste di fruizione con API M2M V3

  Scenario: [M2MV3_AGREEMENT_ARCHIVE_01] Un utente m2m-admin è in grado di archiviare una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di archiviare la richiesta di fruizione
    Then si ottiene status code 200
    And la richiesta di fruizione si trova in stato "ARCHIVED"

  Scenario: [M2MV3_AGREEMENT_ARCHIVE_02] Un utente m2m NON è in grado di archiviare una richiesta di fruizione precedentemente creata da un fruitore e attivata da un erogatore
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di archiviare la richiesta di fruizione
    Then si ottiene status code 403
    And la richiesta di fruizione si trova in stato "ACTIVE"