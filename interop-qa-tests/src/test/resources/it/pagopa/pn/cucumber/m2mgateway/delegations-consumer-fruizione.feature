@m2m-delegations-consumer
Feature: Gestione delle deleghe in fruizione M2M

  @m2m-parte2-settembre @deleghe2
  Scenario Outline: [M2M_DELEGATIONS_CONSUMER_01] Un utente con ruolo M2M-ADMIN o M2M può recuperare i dettagli di una delega in fruizione (Parte2#Scenario intorno a 236)
    Given l'utente è un "admin" di "PA2"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione

    # Delega in stato WAITING_FOR_APPROVAL
    And l'utente è un "admin" dell'ente delegante
    Given l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di reperire i dettagli della delega in fruizione
    Then si ottiene lo status code 200
    And i dettagli della delega in fruizione sono coerenti con quanto atteso da una delega in stato WAITING_FOR_APPROVAL

    ## Delega in stato REJECTED
    Given l'utente è un "admin" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di reperire i dettagli della delega in fruizione
    Then si ottiene lo status code 200
    And i dettagli della delega in fruizione sono coerenti con quanto atteso da una delega in stato REJECTED

    # Delega in stato ACTIVE
    Given l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato con successo
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di reperire i dettagli della delega in fruizione
    Then si ottiene lo status code 200
    And i dettagli della delega in fruizione sono coerenti con quanto atteso da una delega in stato ACTIVE

    # Delega in stato REVOKED
    Given l'utente è un "admin" dell'ente delegante
    And l'ente delegante con ruolo "admin" revoca la delega in fruizione con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di reperire i dettagli della delega in fruizione
    Then si ottiene lo status code 200
    And i dettagli della delega in fruizione sono coerenti con quanto atteso da una delega in stato REVOKED

    Examples:
      | ruolo     |
      | m2m-admin |
      | m2m       |

  @m2m-parte2-settembre
  Scenario: [M2M_DELEGATIONS_CONSUMER_02] Un utente NON può recuperare i dettagli di una delega in fruizione specificando un auth. token non valido (Parte2#Scenario intorno a 238)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di reperire i dettagli di una delega in fruizione inesistente
    Then si ottiene lo status code 401

  @m2m-parte2-settembre
  Scenario: [M2M_DELEGATIONS_CONSUMER_03] Un utente NON può recuperare i dettagli di una delega in fruizione inesistente (Parte2#Scenario intorno a 239)
    Given l'utente è un "admin" di "PA2"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di reperire i dettagli di una delega in fruizione inesistente
    Then si ottiene lo status code 404