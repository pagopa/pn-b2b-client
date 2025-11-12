@m2m-delegations-producer
Feature: Gestione delle deleghe in erogazione M2M

  @m2m-parte2-settembre @deleghe2
  Scenario Outline: [M2M_DELEGATIONS_PRODUCER_01] Un utente con ruolo M2M-ADMIN o M2M può recuperare i dettagli di una delega in erogazione (Parte2#Scenario intorno a 236)
    Given l'utente è un "admin" di "PA2"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe

    # Delega in stato WAITING_FOR_APPROVAL
    Given l'ente "PA1" richiede la creazione di una delega per l'ente "PA2" con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di reperire i dettagli della delega in erogazione
    Then si ottiene lo status code 200
    And i dettagli della delega in erogazione sono coerenti con quanto atteso da una delega in stato WAITING_FOR_APPROVAL

    # Delega in stato REJECTED
    Given l'ente "PA2" rifiuta la delega con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di reperire i dettagli della delega in erogazione
    Then si ottiene lo status code 200
    And i dettagli della delega in erogazione sono coerenti con quanto atteso da una delega in stato REJECTED

    # Delega in stato ACTIVE
    Given l'ente "PA1" richiede la creazione di una delega per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di reperire i dettagli della delega in erogazione
    Then si ottiene lo status code 200
    And i dettagli della delega in erogazione sono coerenti con quanto atteso da una delega in stato ACTIVE

    # Delega in stato REVOKED
    Given l'ente "PA1" con ruolo "admin" revoca la delega con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di reperire i dettagli della delega in erogazione
    Then si ottiene lo status code 200
    And i dettagli della delega in erogazione sono coerenti con quanto atteso da una delega in stato REVOKED

    Examples:
      | ruolo     |
      | m2m-admin |
      | m2m       |

  @m2m-parte2-settembre
  Scenario: [M2M_DELEGATIONS_PRODUCER_02] Un utente NON può recuperare i dettagli di una delega in erogazione specificando un auth. token non valido (Parte2#Scenario intorno a 238)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di reperire i dettagli di una delega in erogazione inesistente
    Then si ottiene lo status code 401

  @m2m-parte2-settembre
  Scenario: [M2M_DELEGATIONS_PRODUCER_03] Un utente NON può recuperare i dettagli di una delega in erogazione inesistente (Parte2#Scenario intorno a 239)
    Given l'utente è un "admin" di "PA2"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di reperire i dettagli di una delega in erogazione inesistente
    Then si ottiene lo status code 404