Feature: Gestione delle deleghe attraverso APIs M2M V2
  Background:
    # TODO 07/02/2025: considerare di generalizzare così da resettare TUTTI gli enti automaticamente
    Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe in fruizione
    And l'ente "PA1" rimuove la disponibilità a ricevere deleghe in fruizione
    And l'ente "PA4" rimuove la disponibilità a ricevere deleghe in fruizione

  @happy-path @deleghe2
  @m2m-incaricato
  Scenario: [M2M_DELEGATIONS_CONSUMER_1] Una delega in fruizione può essere creata da un utente con ruolo M2M-ADMIN (Scenario 37)
    Given "PA3" ha già creato e pubblicato 1 e-services delegabile in fruizione
    And l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un m2m-admin dell'ente delegante
    When l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato
    Then si ottiene lo status code 200
    And la delega è stata inoltrata correttamente