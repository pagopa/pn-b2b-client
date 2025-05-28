Feature: Gestione degli eServices template

  Scenario: [M2MG_ESERVICETEMPLATES_1] Recupero corretto delle versioni di un template e-service con utente autorizzato (Scenario 17)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione dei template e-service:
      | templateId      | name                |
      | template-test-1 | Template Sanitario  |
    When l'utente tenta di recuperare le versioni del template e-service "template-test-1"
    Then si ottiene lo status code 200
    And viene restituita la lista delle versioni del template e-service

  Scenario: [M2MG_ESERVICETEMPLATES_2] Recupero corretto della lista delle versioni di un template e-service (Scenario 177)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione dei template e-service:
      | templateId       | name                |
      | template-test-1  | Template Sanitario  |
    When l'utente tenta di recuperare le versioni del template e-service "template-test-1"
    Then si ottiene lo status code 200
    And viene restituita la lista delle versioni del template e-service

  Scenario: [M2MG_ESERVICETEMPLATES_3] Errore nel recupero delle versioni di un template e-service con templateId nullo (Scenario 178)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione dei template e-service:
      | templateId       | name                |
      | template-test-1  | Template Sanitario  |
    When l'utente tenta di recuperare le versioni del template e-service "null"
    Then si ottiene lo status code 400
    And la lista delle versioni del template e-service non viene restituita

  Scenario: [M2MG_ESERVICETEMPLATES_4] Errore nel recupero delle versioni di un template e-service con templateId inesistente (Scenario 179)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione dei template e-service:
      | templateId       | name                |
      | template-test-1  | Template Sanitario  |
    When l'utente tenta di recuperare le versioni del template e-service "template-999"
    Then si ottiene lo status code 404
    And la lista delle versioni del template e-service non viene restituita

  Scenario: [M2MG_ESERVICETEMPLATES_5] Accesso negato al recupero delle versioni di un template e-service con token non valido (Scenario 180)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And l'utente possiede un token non valido
    And viene effettuata la creazione dei template e-service:
      | templateId       | name                |
      | template-test-1  | Template Sanitario  |
    When l'utente tenta di recuperare le versioni del template e-service "template-test-1"
    Then si ottiene lo status code 401
    And la lista delle versioni del template e-service non viene restituita

