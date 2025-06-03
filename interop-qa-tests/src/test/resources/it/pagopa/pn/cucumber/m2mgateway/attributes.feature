Feature: Gestione degli attributes

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_1] Recupero del dettaglio di un attributo certificato con utente autorizzato (Scenario 61)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione degli attributi certificati:
      | attributeId     | name                |
      | attr-cert-001   | Attributo Fiscale   |
    When l'utente tenta di recuperare il dettaglio dell'attributo certificato "attr-cert-001"
    Then si ottiene lo status code 200
    And viene restituito il dettaglio dell'attributo certificato richiesto

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_2] Errore nel recupero del dettaglio di un attributo certificato con attributeId nullo (Scenario 62)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione degli attributi certificati:
      | attributeId     | name                |
      | attr-cert-001   | Attributo Fiscale   |
    When l'utente tenta di recuperare il dettaglio dell'attributo certificato "null"
    Then si ottiene lo status code 400
    And il dettaglio dell'attributo certificato non viene restituito

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_3] Accesso negato al dettaglio di un attributo certificato con token non valido (Scenario 63)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente possiede un token non valido
    And viene effettuata la creazione degli attributi certificati:
      | attributeId     | name                |
      | attr-cert-001   | Attributo Fiscale   |
    When l'utente tenta di recuperare il dettaglio dell'attributo certificato "attr-cert-001"
    Then si ottiene lo status code 401
    And il dettaglio dell'attributo certificato non viene restituito

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_4] Errore nel recupero del dettaglio di un attributo certificato con attributeId inesistente (Scenario 64)
    Given l'utente è un "admin" di "PA1" con ruolo M2M "m2m"
    And l'utente è amministratore del client
    And viene effettuata la creazione degli attributi certificati:
      | attributeId     | name                |
      | attr-cert-001   | Attributo Fiscale   |
    When l'utente tenta di recuperare il dettaglio dell'attributo certificato "attr-cert-999"
    Then si ottiene lo status code 404
    And il dettaglio dell'attributo certificato non viene restituito

