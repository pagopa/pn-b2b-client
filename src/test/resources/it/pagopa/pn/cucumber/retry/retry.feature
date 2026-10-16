Feature: Template engine

  @retry
  Scenario: [RETRY_1] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - lingua errata - lingua vuota
    When recupero il template per "attestazione opponibile a terzi di notifica presa in carico" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @retry
  Scenario: [RETRY_2] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - lingua errata - lingua vuota
    When recupero il template per "attestazione opponibile a terzi di notifica presa in carico" in lingua "null"
    Then verifico che la chiamata sia andata in "200" error