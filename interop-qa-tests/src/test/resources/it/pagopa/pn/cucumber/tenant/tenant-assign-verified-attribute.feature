@tenant
Feature: Assegnazione di un attributo verificato ad un aderente
  Tutti gli utenti autorizzati di enti che possono erogare eservice possono assegnare un attributo verificato

  @nrt-minimal
  @tenant_assign_verified_attribute1
  Scenario Outline: [TENANT_ASSIGN_VERIFIED_ATTRIBUTE_01] Per un attributo verificato precedentemente creato da un primo aderente, alla richiesta di assegnazione dell’attributo senza data di scadenza ad un secondo aderente da parte di un utente con sufficienti permessi (admin) appartenente al primo aderente, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un attributo verificato
    Given "<ente>" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente assegna a "PA2" l'attributo verificato precedentemente creato
    Then si ottiene status code <statusCode>

    Examples:
      | ente | ruolo        | statusCode |
      | PA1  | admin        |        204 |
      | PA1  | api          |        403 |
      | PA1  | security     |        403 |
      | PA1  | support      |        403 |
      | PA1  | api,security |        403 |
      | GSP  | admin        |        204 |
      | GSP  | api          |        403 |
      | GSP  | security     |        403 |
      | GSP  | support      |        403 |
      | GSP  | api,security |        403 |

    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | statusCode |
      | GSP  | reviewer     |        403 |
      | GSP  | viewer       |        403 |

  @nrt-minimal
  @tenant_assign_verified_attribute2
  Scenario: [TENANT_ASSIGN_VERIFIED_ATTRIBUTE_02] Per un attributo verificato precedentemente creato da un primo aderente, alla richiesta di assegnazione dell’attributo senza data di scadenza ad un secondo aderente da parte di un utente con sufficienti permessi (admin) appartenente al terzo aderente, va a buon fine. Spiega: gli attributi verificati possono essere assegnati indipendentemente da chi li ha creati
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un attributo verificato
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "GSP" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente assegna a "GSP" l'attributo verificato precedentemente creato
    Then si ottiene status code 204

  @nrt-minimal
  @tenant_assign_verified_attribute3
  Scenario: [TENANT_ASSIGN_VERIFIED_ATTRIBUTE_03] Per un attributo verificato precedentemente creato da un primo aderente, alla richiesta di assegnazione dell’attributo con data di scadenza nel futuro ad un secondo aderente da parte di un utente con sufficienti permessi (admin) appartenente al primo aderente, va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un attributo verificato
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente assegna a "PA2" l'attributo verificato precedentemente creato con data di scadenza nel futuro
    Then si ottiene status code 204

  @nrt-minimal
  @tenant_assign_verified_attribute4 @wait_for_fix @PIN-5198
  Scenario: [TENANT_ASSIGN_VERIFIED_ATTRIBUTE_04] Per un attributo verificato precedentemente creato da un primo aderente, alla richiesta di assegnazione dell’attributo con data di scadenza nel passato ad un secondo aderente da parte di un utente con sufficienti permessi (admin) appartenente al primo aderente, ottiene un errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un attributo verificato
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente assegna a "PA2" l'attributo verificato precedentemente creato con data di scadenza nel passato
    Then si ottiene status code 400

  @nrt-minimal
  @tenant_assign_verified_attribute5
  Scenario: [TENANT_ASSIGN_VERIFIED_ATTRIBUTE_05] Per un attributo verificato precedentemente creato da un aderente, alla richiesta di assegnazione dell’attributo senza scadenza al proprio ente da parte di un utente con sufficienti permessi (admin), ottiene un errore. Spiega: un ente non può autoassegnarsi attributi verificati
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un attributo verificato
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'utente assegna a "PA1" l'attributo verificato precedentemente creato
    Then si ottiene status code 403
