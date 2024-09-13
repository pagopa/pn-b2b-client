Feature: Public key legal Person Authentication

  Scenario: [LEGAL-PERSON-AUTH_1] Amministratore PG censisce una chiave pubblica dopo averla cancellata
    Given l'amministratore "AMMINISTRATORE" censisce una chiave pubblica per la PG
    And l'amministratore "AMMINISTRATORE" blocca la chiave pubblica
    And l'amministratore "AMMINISTRATORE" cancella la chiave pubblica
    When l'amministratore "AMMINISTRATORE" censisce una chiave pubblica per la PG
    And l'amministratore "AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG che è "ATTIVA"