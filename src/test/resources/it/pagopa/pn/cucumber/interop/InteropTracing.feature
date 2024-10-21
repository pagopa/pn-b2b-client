Feature: Interop Tracing feature

  Scenario: [] Inserimento di un nuovo file CSV di tracing giornaliero
    Given l'ente "" prepara un nuovo file CSV
    When viene sottomesso il file CSV
    Then il caricamento del csv viene effettuato con successo
