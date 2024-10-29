Feature: Interop Tracing feature

  Background:
    Given viene aggiornato il file CSV con la prima data disponibile

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-01] Inserimento di un nuovo file CSV di tracing giornaliero
    When viene sottomesso il file CSV "CORRETTO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    # SCENARIO 15
    When viene sovrascritto il tracing aggiunto in precedenza con il csv: "CORRETTO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    And viene recuperata la lista di tracing con stato "COMPLETED"
    And si verifica che il tracing sia presente tra quelli ritornati

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-02] Inserimento di un file CSV di tracing giornaliero per una data già presente
    # PRIMO CARICAMENTO
    When viene sottomesso il file CSV "CORRETTO"
    # SECONDO CARICAMENTO
    When viene sottomesso il file CSV "CORRETTO"
    Then la chiamata fallisce con status code: 400

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-03] Inserimento di un nuovo file CSV di tracing giornaliero
    When viene sottomesso il file CSV "ERRATO"
    And si attende che il file di tracing caricato passi in stato "ERROR"
    # SCENARIO 9
    When viene recuperato il dettaglio del tracing con errori
    Then il dettaglio ritorna gli errori aspettati
    # SCENARIO 11
    When gli errori riscontrati vengono corretti passando il csv "CORRETTO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"

  @interopTracingCsv
  Scenario Outline: [INTEROP-TRACING-04] Recupero lista tracing con filtro stato
    When viene recuperata la lista di tracing con stato "<status>"
    Then la risposta contiene soltanto i tracing con stato "<status>"
    Examples:
      | status    |
      | MISSING   |
      | COMPLETED |
      | PENDING   |
      | ERROR     |

  @interopTracingCsv @ignore
  Scenario: [INTEROP-TRACING-05] Recupero lista tracing per utenza dove non sia stato mai inserito alcun file
    When viene recuperata la lista di tracing con uno stato tra i seguenti
      | ERROR     |
      | MISSING   |
    Then non viene trovato nessun tracing caricato

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-06] Recupero dettaglio errori presenti nel file tracing con identificativo non esistente
    When viene recuperato il dettaglio degli errori per il tracing "bb09726e-5783-4713-aebf-7b5b688bcccc"
    Then la chiamata fallisce con status code: 400

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-07] Invio del file CSV tracing contenente errori utilizzando l'identificativo del file di tracing già in errore
    When viene sottomesso il file CSV "ERRATO"
    And si attende che il file di tracing caricato passi in stato "ERROR"
    And gli errori riscontrati vengono corretti passando il csv "ERRATO"
    Then si attende che il file di tracing caricato passi in stato "ERROR"

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-08] Invio del file CSV tracing corretto utilizzando l'identificativo del file di tracing non esistente
    And vengono corretti gli errori riscontrati per il tracingId "bb09726e-5783-4713-aebf-7b5b688bcccc"
    Then la chiamata fallisce con status code: 404

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-09] Invio del file CSV tracing, per una stessa data e già in stato completato, in sostituzione a quello già presente, il quale però contiene errori
    When viene sottomesso il file CSV "CORRETTO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    When viene sovrascritto il tracing aggiunto in precedenza con il csv: "ERRATO"
    And si attende che il file di tracing caricato passi in stato "ERROR"

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-10] Invio del file CSV tracing per una stessa data e già in stato completato, in sostituzione a quello già presente
    When viene sovrascritto il tracing con id: "bb09726e-5783-4713-aebf-7b5b688bcccc"
    Then la chiamata fallisce con status code: 404

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-11] Invio del file CSV tracing per una stessa data e già in stato completato, in sostituzione a quello già presente
    When viene invocato l'endpoint di health con successo

  @interopTracingCsv @ignore
  Scenario: [INTEROP-TRACING-12] Invio del file CSV tracing mancante utilizzando l'identificativo del file di tracing non inserito per una determinata data
    Given viene recuperata la lista di tracing con stato "MISSING"
    When viene inviato il csv "CORRETTO" per la data mancante
    And si attende che il file di tracing caricato passi in stato "COMPLETED"