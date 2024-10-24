Feature: Interop Tracing feature

  Scenario: [INTEROP-TRACING-01] Inserimento di un nuovo file CSV di tracing giornaliero
    Given l'ente "" prepara il file CSV "CORRETTO"
    When viene sottomesso il file CSV in data "2024-10-22"
    Then il caricamento del csv viene effettuato senza errori
    # SCENARIO 15
    When viene sovrascritto il tracing aggiunto in precedenza con il csv: "CORRETTO"
    Then viene verificato che la sovrascrittura viene effettuata con errori
    And viene recuperata la lista di tracing con stato "COMPLETED"
    And si verifica che il tracing sia presente tra quelli ritornati

  Scenario: [INTEROP-TRACING-02] Inserimento di un file CSV di tracing giornaliero per una data già presente
    # PRIMO CARICAMENTO
    Given l'ente "" prepara il file CSV "CORRETTO"
    When viene sottomesso il file CSV in data "2024-10-22"
    # SECONDO CARICAMENTO
    Given l'ente "" prepara il file CSV "CORRETTO"
    When viene sottomesso il file CSV in data "2024-10-22"
    Then la chiamata fallisce con status code: 400

  Scenario: [INTEROP-TRACING-03] Inserimento di un nuovo file CSV di tracing giornaliero
    Given l'ente "" prepara il file CSV "ERRATO"
    When viene sottomesso il file CSV in data "2024-10-22"
    Then il caricamento del csv viene effettuato con errori
    # SCENARIO 9
    When viene recuperato il dettaglio del tracing con errori
    Then il dettaglio ritorna gli errori aspettati
    # SCENARIO 11
    When gli errori riscontrati vengono corretti passando il csv "CORRETTO"
    Then viene recuperata la lista di tracing con stato "COMPLETED"

  Scenario Outline: [INTEROP-TRACING-04] Recupero lista tracing con filtro stato
    When viene recuperata la lista di tracing con stato "<status>"
    Then la risposta contiene soltanto i tracing con stato "<status>"
    Examples:
      | status    |
      | MISSING   |
      | COMPLETED |
      | PENDING   |
      | ERROR     |

  Scenario: [INTEROP-TRACING-05] Recupero lista tracing per utenza dove non sia stato mai inserito alcun file
#    Given l'ente "" effettua le chiamate
    When viene recuperata la lista di tracing con uno stato tra i seguenti
      | ERROR     |
      | MISSING   |
    Then non viene trovato nessun tracing caricato

  Scenario: [INTEROP-TRACING-06] Recupero dettaglio errori presenti nel file tracing con identificativo non esistente
    When viene recuperato il dettaglio degli errori per il tracing ""
    Then la chiamata fallisce con status code: 400

  Scenario: [INTEROP-TRACING-07] Invio del file CSV tracing contenente errori utilizzando l'identificativo del file di tracing già in errore
    Given l'ente "" prepara il file CSV "ERRATO"
    When viene sottomesso il file CSV in data "2024-10-22"
    Then il caricamento del csv viene effettuato con errori
    And gli errori riscontrati vengono corretti passando il csv "ERRATO"
    And viene recuperata la lista di tracing con stato "ERROR"
    And si verifica che il tracing sia presente tra quelli ritornati

  Scenario: [INTEROP-TRACING-08] Invio del file CSV tracing corretto utilizzando l'identificativo del file di tracing non esistente
    And vengono corretti gli errori riscontrati per il tracingId "aaaa-bbbb-cccc"
    Then la chiamata fallisce con status code: 404

  Scenario: [INTEROP-TRACING-09] Invio del file CSV tracing per una stessa data e già in stato completato, in sostituzione a quello già presente
    Given l'ente "" prepara il file CSV "CORRETTO"
    When viene sottomesso il file CSV in data "2024-10-23"
    Then il caricamento del csv viene effettuato senza errori
    When viene sovrascritto il tracing aggiunto in precedenza con il csv: "ERRATO"
    And viene verificato che la sovrascrittura viene effettuata con errori
    And viene recuperata la lista di tracing con stato "ERROR"
    And si verifica che il tracing sia presente tra quelli ritornati

  Scenario: [INTEROP-TRACING-10] Invio del file CSV tracing per una stessa data e già in stato completato, in sostituzione a quello già presente
    When viene sovrascritto il tracing con id: "aaaa-bbbb-cccc"
    Then la chiamata fallisce con status code: 404

  Scenario: [INTEROP-TRACING-11] Invio del file CSV tracing per una stessa data e già in stato completato, in sostituzione a quello già presente
    When viene invocato l'endpoint di health con successo

  Scenario: [INTEROP-TRACING-12] Invio del file CSV tracing mancante utilizzando l'identificativo del file di tracing non inserito per una determinata data
    Given viene recuperata la lista di tracing con stato "MISSING"
    When viene inviato il csv per la data mancante
    Then il caricamento del csv viene effettuato senza errori