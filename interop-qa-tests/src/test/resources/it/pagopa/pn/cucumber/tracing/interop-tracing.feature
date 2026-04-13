Feature: Interop Tracing feature

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-01] Inserimento di un nuovo file CSV di tracing giornaliero
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    When viene inviato il file CSV "CORRETTO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    # SCENARIO 15
    When viene sovrascritto il tracing aggiunto in precedenza con il csv: "CORRETTO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    And viene recuperata la lista di tracing con stato "COMPLETED"
    And si verifica che il tracing sia presente tra quelli ritornati

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-02] Inserimento di un file CSV di tracing giornaliero per una data già presente
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    # PRIMO CARICAMENTO
    When viene inviato il file CSV "CORRETTO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    # SECONDO CARICAMENTO
    When viene inviato il file CSV "CORRETTO"
    Then il file CSV di tracing viene rifiutato perché già esistente

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-03] Inserimento di un nuovo file CSV di tracing giornaliero
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    When viene inviato il file CSV "ERRATO"
    And si attende che il file di tracing caricato passi in stato "ERROR"
    # SCENARIO 9
    When viene recuperato il dettaglio del tracing con errori
    Then il dettaglio ritorna gli errori aspettati
    # SCENARIO 11
    When gli errori riscontrati vengono corretti passando il csv "CORRETTO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"

  @interopTracingCsv
  Scenario Outline: [INTEROP-TRACING-04] Recupero lista tracing con filtro stato
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    When viene recuperata la lista di tracing con stato "<status>"
    Then la risposta contiene soltanto i tracing con stato "<status>"
    Examples:
      | status    |
      | MISSING   |
      | COMPLETED |
#      | PENDING   | Line commented since no pending tracings are present
      | ERROR     |

  # Questo test va eseguito usando un'utenza con cui non sono mai stati caricati file di tracing
  @interopTracingCsv @ignore
  Scenario: [INTEROP-TRACING-05] Recupero lista tracing per utenza dove non sia stato mai inserito alcun file
    Given l'utenza "TENANT2" effettua le chiamate
    When viene recuperata la lista di tracing con uno stato tra i seguenti
      | ERROR     |
      | MISSING   |
    Then non viene trovato nessun tracing caricato

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-06] Recupero dettaglio errori presenti nel file tracing con identificativo non esistente
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    When viene recuperato il dettaglio degli errori per il tracing "bb09726e-5783-4713-aebf-7b5b688bcccc"
    Then la chiamata fallisce perché la risorsa non viene trovata

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-07] Invio del file CSV tracing contenente errori utilizzando l'identificativo del file di tracing già in errore
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    When viene inviato il file CSV "ERRATO"
    And si attende che il file di tracing caricato passi in stato "ERROR"
    And gli errori riscontrati vengono corretti passando il csv "ERRATO"
    Then si attende che il file di tracing caricato passi in stato "ERROR"

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-08] Invio del file CSV tracing corretto utilizzando l'identificativo del file di tracing non esistente
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    And vengono corretti gli errori riscontrati per il tracingId "bb09726e-5783-4713-aebf-7b5b688bcccc"
    Then la chiamata fallisce perché la risorsa non viene trovata

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-09] Invio del file CSV tracing, per una stessa data e già in stato completato, in sostituzione a quello già presente, il quale però contiene errori
    Given l'utenza "TENANT1" effettua le chiamate
    When viene inviato il file CSV "CORRETTO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    When viene sovrascritto il tracing aggiunto in precedenza con il csv: "ERRATO"
    And si attende che il file di tracing caricato passi in stato "ERROR"

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-10] Invio del file CSV tracing per una stessa data e già in stato completato, in sostituzione a quello già presente
    Given l'utenza "TENANT1" effettua le chiamate
    #And viene aggiornato il file CSV con la prima data disponibile
    When viene sovrascritto il tracing con id: "bb09726e-5783-4713-aebf-7b5b688bcccc"
    Then la chiamata fallisce perché la risorsa non viene trovata

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-11] Verifica stato endpoint di health
    When viene invocato l'endpoint di health con successo

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-12] Invio del file CSV tracing mancante utilizzando l'identificativo del file di tracing non inserito per una determinata data
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    And viene recuperata la lista di tracing con stato "MISSING"
    When viene inviato il csv "CORRETTO" per la data mancante
    And viene recuperato il file di tracing appena caricato e si verifica che lo stato sia "COMPLETED"

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-13-1] Path con caratteri percent-encoded non validi
    Given l'utenza "TENANT1" effettua le chiamate
    When viene chiamato tracing con un path contenente un carattere percent-encoded non valido

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-14-1] Verifica arricchimento dati per l'inserimento di un nuovo file CSV di tracing
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    When viene inviato il file CSV "CORRETTO"
    Then si attende che il file di tracing caricato passi in stato "PENDING"
    # COMPLETED
    And si attende che il file di tracing venga arricchito con altri dati

    When viene sovrascritto il tracing aggiunto in precedenza con il csv: "CORRETTO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    And viene recuperata la lista di tracing con stato "COMPLETED"
    Then si verifica che il tracing sia presente tra quelli ritornati
    And si attende che il file di tracing venga arricchito con altri dati

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-14-2] Verifica arricchimento dati per l'inserimento di un nuovo pesante file CSV di tracing
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    When viene inviato il file CSV "CORRETTO_PESANTE"
    Then si attende che il file di tracing caricato passi in stato "COMPLETED"
    And si attende che il file di tracing arricchito venga generato

    When viene sovrascritto il tracing aggiunto in precedenza con il csv: "CORRETTO_PESANTE"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    And viene recuperata la lista di tracing con stato "COMPLETED"
    Then si verifica che il tracing sia presente tra quelli ritornati
    And si attende che il file di tracing arricchito venga generato

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-15-1] Invio di un file CSV di tracing con header errato
    Given l'utenza "TENANT1" effettua le chiamate
    And viene recuperata la prima data disponibile per un invio del file CSV
    When viene inviato il file CSV "ERRATO_HEADER_CAMPO_MANCANTE"
    Then si attende che il file di tracing caricato passi in stato "ERROR"
    And nessun file csv di tracing viene memorizzato, arricchito o raccolti i record errati

    When viene inviato il file CSV "ERRATO_HEADER_NOME_CAMPO"
    Then si attende che il file di tracing caricato passi in stato "ERROR"
    And nessun file csv di tracing viene memorizzato, arricchito o raccolti i record errati

    When viene inviato il file CSV "ERRATO_HEADER_DOPPIA_VIRGOLA"
    Then si attende che il file di tracing caricato passi in stato "ERROR"
    And nessun file csv di tracing viene memorizzato, arricchito o raccolti i record errati

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-15-2] Verifica arricchimento dati per l'inserimento di un nuovo file CSV di tracing con alcuni record errati
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    When viene inviato il file CSV "CORRETTO_CON_RECORD_ERRATI"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    And si attende che il file di tracing arricchito venga generato
    And si attende che i record errati vengano tracciati negli errori

    When viene sovrascritto il tracing aggiunto in precedenza con il csv: "CORRETTO_CON_RECORD_ERRATI"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    And si attende che il file di tracing arricchito venga generato
    And si attende che i record errati vengano tracciati negli errori

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-15-3] Verifica il tracciamento dei WARNING per l'inserimento di un nuovo file CSV di tracing con purpose_id non conforme
    Given l'utenza "TENANT1" effettua le chiamate
    And viene aggiornato il file CSV con la prima data disponibile
    When viene inviato il file CSV "CORRETTO_CON_PURPOSE_NON_CONFORMI"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    And si attende che il file di tracing arricchito venga generato
    And si attende che i record con purpose non conformi vengano tracciati con warning

    When viene sovrascritto il tracing aggiunto in precedenza con il csv: "CORRETTO_CON_PURPOSE_NON_CONFORMI"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    And si attende che il file di tracing arricchito venga generato
    And si attende che i record con purpose non conformi vengano tracciati con warning
