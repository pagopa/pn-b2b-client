Feature: Gestione degli attributes

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_1] Recupero del dettaglio di un attributo certificato con utente autorizzato (Scenario 61)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    When l'utente tenta di recuperare il dettaglio dell'attributo certificato
    Then si ottiene lo status code 200
    And viene restituito il dettaglio dell'attributo certificato

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_2] Errore nel recupero del dettaglio di un attributo certificato con attributeId nullo (Scenario 62)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    When l'utente tenta di recuperare il dettaglio dell'attributo certificato con id null
    Then si ottiene lo status code 400
    And non viene restituito il dettaglio dell'attributo certificato

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_3] Accesso negato al dettaglio di un attributo certificato con token non valido (Scenario 63)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m scaduto
    And viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    When l'utente tenta di recuperare il dettaglio dell'attributo certificato
    Then si ottiene lo status code 401
    And non viene restituito il dettaglio dell'attributo certificato

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_4] Errore nel recupero del dettaglio di un attributo certificato con attributeId inesistente (Scenario 64)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    When l'utente tenta di recuperare il dettaglio dell'attributo certificato con id invalido
    Then si ottiene lo status code 404
    And non viene restituito il dettaglio dell'attributo certificato

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_5] Creazione di un attributo certificato con utente M2M-ADMIN (Scenario 20)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code 201
    And viene restituito il dettaglio dell'attributo certificato

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_6] Accesso negato alla creazione di un attributo certificato con utente M2M (Scenario 41)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code 403
    And non viene restituito il dettaglio dell'attributo certificato


