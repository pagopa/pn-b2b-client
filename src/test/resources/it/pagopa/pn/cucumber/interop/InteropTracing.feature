Feature: Interop Tracing feature

  Scenario: [1] Inserimento di un nuovo file CSV di tracing giornaliero
    Given l'ente "" prepara il file CSV "CORRETTO"
    When viene sottomesso il file CSV in data "2024-10-22"
    Then il caricamento del csv viene effettuato senza errori

  Scenario: [2] Inserimento di un file CSV di tracing giornaliero per una data già presente
    # PRIMO CARICAMENTO
    Given l'ente "" prepara il file CSV "CORRETTO"
    When viene sottomesso il file CSV in data "2024-10-22"
    # SECONDO CARICAMENTO
    Given l'ente "" prepara il file CSV "CORRETTO"
    When viene sottomesso il file CSV in data "2024-10-22"
    Then la chiamata fallisce con status code: 400

  Scenario: [3] Inserimento di un nuovo file CSV di tracing giornaliero
    Given l'ente "" prepara il file CSV "ERRATO"
    When viene sottomesso il file CSV in data "2024-10-22"
    Then il caricamento del csv viene effettuato con errori
    # SCENARIO 9
    When viene recuperato il dettaglio del tracing con errori
    Then il dettaglio ritorna gli errori aspettati


  Scenario Outline: [4-5-6-7] Recupero lista tracing con filtro stato
    When viene recuperata la lista di tracing con stato "<status>"
    Then la risposta contiene soltanto i tracing con stato "<status>"
    Examples:
      | status    |
      | MISSING   |
      | COMPLETED |
      | PENDING   |
      | ERROR     |

  Scenario: [8] Recupero lista tracing per utenza dove non sia stato mai inserito alcun file
#    Given l'ente "" effettua le chiamate
    When viene recuperata la lista di tracing con uno stato tra i seguenti
      | ERROR     |
      | MISSING   |
    Then non viene trovato nessun tracing caricato

  Scenario: [10] Recupero dettaglio errori presenti nel file tracing
    When viene recuperato il dettaglio degli errori per il tracing ""
    Then la chiamata fallisce con status code: 400


