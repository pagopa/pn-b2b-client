@tenant
Feature: Revoca di un attributo verificato posseduto da uno specifico aderente
  Tutti gli utenti autorizzati degli enti erogatori possono revocare uno degli attributi verificati che hanno assegnato precedentemente

  @nrt-minimal
  @tenant_revoke_verified_attribute1 @no-parallel
  Scenario Outline: [TENANT_REVOKE_VERIFIED_ATTRIBUTE_01] Per un attributo precedentemente verificato da un primo aderente ad un secondo aderente, alla richiesta di revoca da parte di un utente con sufficienti permessi (admin) appartenente al primo aderente, va a buon fine
    Given l'utente è un "<ruolo>" di "PA2"
    Given "PA2" ha già creato un attributo verificato
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "PA2" ha già verificato l'attributo verificato a "PA1"
    When l'utente revoca l'attributo precedentemente verificato
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo        | statusCode |
      | admin        |        204 |
      | api          |        403 |
      | security     |        403 |
      | support      |        403 |
      | api,security |        403 |

    @nuovi-operatori-update
    Examples:
      | ruolo        | statusCode |
      | reviewer     |        403 |
      | viewer       |        403 |

  @nrt-minimal
  @tenant_revoke_verified_attribute2 @no-parallel
  Scenario: [TENANT_REVOKE_VERIFIED_ATTRIBUTE_02] Per un attributo precedentemente verificato da un primo aderente ad un secondo aderente, e poi successivamente verificato da un terzo aderente sempre al secondo aderente, alla richiesta di revoca da parte di un utente con sufficienti permessi (admin) appartenente al primo aderente, va a buon fine. Inoltre, l’istanza dell’attributo verificato dal terzo aderente rimane verificata
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato un attributo verificato
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "PA2" ha già verificato l'attributo verificato a "PA1"
    Given "GSP" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA1" ha un'altra richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "GSP" ha già verificato l'attributo verificato a "PA1" sull'altra richiesta di fruizione
    When l'utente revoca l'attributo precedentemente verificato
    Then si ottiene status code 204
    And l'attributo di "PA1" rimane verificato da "GSP"
    And l'attributo di "PA1" risulta revocato da "PA2"

  @nrt-minimal @wait_for_fix
  @tenant_revoke_verified_attribute3 @no-parallel
  Scenario: [TENANT_REVOKE_VERIFIED_ATTRIBUTE_03] Revoca di un attributo verificato e richiesta di fruizione non accettabile
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un attributo verificato
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA1" ha già verificato l'attributo verificato a "PA2"
    When l'utente revoca l'attributo precedentemente verificato
    And si ottiene status code 204
    And l'attributo di "PA2" risulta revocato da "PA1"
    Then "PA1" approva quella richiesta di fruizione ma l'operazione è vietata

  @nrt-minimal @wait_for_fix
  @tenant_revoke_verified_attribute4 @no-parallel
  Scenario: [TENANT_REVOKE_VERIFIED_ATTRIBUTE_04] Revoca di un attributo verificato dopo l'accettazione della richiesta di fruizione, che viene sospesa dalla piattaforma
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un attributo verificato
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA1" ha già verificato l'attributo verificato a "PA2"
    And "PA1" approva quella richiesta di fruizione
    When l'utente revoca l'attributo precedentemente verificato
    And si ottiene status code 204
    And l'attributo di "PA2" risulta revocato da "PA1"
    Then la richiesta di fruizione è in stato "SUSPENDED"

  @nrt-minimal @wait_for_fix
  @tenant_revoke_verified_attribute5 @no-parallel
  Scenario: [TENANT_REVOKE_VERIFIED_ATTRIBUTE_05] Per un attributo verificato richiesto da un erogatore non delegato è presente il delegation ID alla revoca dell'attributo
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un attributo verificato
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA1" ha già verificato l'attributo verificato a "PA2"
    When il delegato all'erogazione revoca l'attributo precedentemente verificato
    Then si ottiene status code 400

  @nrt-minimal @wait_for_fix
  @tenant_revoke_verified_attribute6 @no-parallel @deleghe2
  Scenario: [TENANT_REVOKE_VERIFIED_ATTRIBUTE_06] Per un attributo verificato richiesto da un erogatore delegato è presente il delegation ID alla revoca dell'attributo
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un attributo verificato
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And "PA3" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And il delegato all'erogazione assegna a "PA3" l'attributo verificato precedentemente creato
    When il delegato all'erogazione revoca l'attributo precedentemente verificato
    Then si ottiene status code 200

  @nrt-minimal @wait_for_fix
  @tenant_revoke_verified_attribute7 @no-parallel @deleghe2
  Scenario: [TENANT_REVOKE_VERIFIED_ATTRIBUTE_07] Per un attributo verificato richiesto da un erogatore delegato non è presente il delegation ID alla revoca dell'attributo
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un attributo verificato
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And "PA3" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And l'utente è un "admin" di "PA2"
    And il delegato all'erogazione assegna a "PA3" l'attributo verificato precedentemente creato
    When l'utente revoca l'attributo precedentemente verificato
    Then si ottiene status code 400
