Feature: Interop Tracing
  # Tutti i test di tracing richiedono di settare il profilo "extra-qa"
  # I test da INTEROP-TRACING-13-x in avanti, richiedono di settare i token per l'ambiente Extra QA su AWS credentials

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-01] Inserimento di un nuovo file CSV di tracing giornaliero
    Un aderente invia il file CSV che traccia le richieste API ricevute ed effettuate. L'invio va eseguito ogni giorno
    con un file CSV che riguarda il giorno precedente, ma sono disponibili anche i giorni senza tracciato non marcati
    con stato MISSING.

    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV valido e minimale per una data disponibile
    When viene inviato il file CSV "PREPARATO"
    Then si attende che il file di tracing caricato passi in stato "COMPLETED"

    # SCENARIO 15
    # Sostituzione con un nuovo file CSV di tracing per una data con un tracciato già presente
    When viene sovrascritto il tracing aggiunto in precedenza con il csv "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    And viene recuperata la lista di tracing con stato "COMPLETED"
    Then si verifica che il tracing sia presente tra quelli ritornati

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-02] Inserimento di un nuovo file CSV di tracing per una data con un tracciato già presente
    Un aderente prova ad inviare invece di sostituire un file CSV di tracing già presente per una data e la richiesta
    viene rifiutata.

    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV valido e minimale per una data disponibile
    # PRIMO CARICAMENTO
    And viene inviato il file CSV "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    # SECONDO CARICAMENTO
    When viene inviato il file CSV "PREPARATO"
    Then il file CSV di tracing viene rifiutato perché già esistente

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-03] Inserimento di un nuovo file CSV di tracing giornaliero contenente errori
    Un aderente può inviare un file CSV di tracing che viene ricevuto con successo anche se contiene errori, in seguito,
    viene processato e se contiene errori viene marcato con lo stato ERROR.

    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV con tutti i campi errati per una data disponibile
    When viene inviato il file CSV "PREPARATO"
    Then si attende che il file di tracing caricato passi in stato "ERROR"

    # SCENARIO 9
    # Recupero del dettaglio degli errori contenuti in un file tracing inviato e marcato con ERROR
    When viene recuperato il dettaglio del tracing con errori
    Then il dettaglio ritorna gli errori aspettati

    # SCENARIO 11
    # Correzione del file CSV di tracing per un giorno con un tracing errato usando l'ID del tracing errato
    When vengono corretti tutti i campi del file CSV preparato
    And gli errori riscontrati vengono corretti passando il csv "PREPARATO"
    Then si attende che il file di tracing caricato passi in stato "COMPLETED"

  @interopTracingCsv
  Scenario Outline: [INTEROP-TRACING-04] Recupero della lista dei tracing inviati applicando il filtro sullo stato
    Given l'utenza "TENANT1" effettua le chiamate
    When viene recuperata la lista di tracing con stato "<status>"
    Then la risposta contiene soltanto i tracing con stato "<status>"
    Examples:
      | status    |
      | MISSING   |
      | COMPLETED |
#     | PENDING   | Normalmente non presente, sotto caso spostato al test che invia un CSV molto grande
      | ERROR     |
      | WARNING   |

  # Questo test va eseguito usando un'utenza con cui non sono mai stati caricati file di tracing
  @interopTracingCsv @ignore
  Scenario: [INTEROP-TRACING-05] Recupero della lista dei tracing inviati per un utente che non ha mai inviato alcun file
    Given l'utenza "TENANT2" effettua le chiamate
    When viene recuperata la lista di tracing con uno stato tra i seguenti
      | ERROR     |
      | MISSING   |
    Then non viene trovato nessun tracing caricato

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-06] Recupero del dettaglio degli errori contenuti in un file tracing con identificativo non esistente
    Given l'utenza "TENANT1" effettua le chiamate
    When viene recuperato il dettaglio degli errori per il tracing "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
    Then la richiesta fallisce perché la risorsa non viene trovata

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-07] Invio del file CSV tracing contenente errori utilizzando l'identificativo del file di tracing già in errore
    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV con un purpose ID vuoto per una data disponibile
    And viene inviato il file CSV "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "ERROR"
    When gli errori riscontrati vengono corretti passando il csv "PREPARATO"
    Then si attende che il file di tracing caricato passi in stato "ERROR"

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-08] Correzione del file CSV di tracing utilizzando un identificativo del file di tracing non esistente
    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV valido e minimale per una data disponibile
    When vengono corretti gli errori riscontrati per il tracingId "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
    Then la richiesta fallisce perché la risorsa non viene trovata

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-09] Correzione del file CSV di tracing per un giorno con un tracing corretto e completo, usando un file CSV con errori
    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV valido e minimale per una data disponibile
    And viene inviato il file CSV "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    When viene svuotato il purpose ID del primo record del file CSV preparato
    And viene sovrascritto il tracing aggiunto in precedenza con il csv "PREPARATO"
    Then si attende che il file di tracing caricato passi in stato "ERROR"

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-10] Sostituzione del file CSV di tracing utilizzando un identificativo del file di tracing non esistente
    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV valido e minimale per una data già presente
    When viene sovrascritto il tracing con id: "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
    Then la richiesta fallisce perché la risorsa non viene trovata

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-11] Verifica stato health di endpoint
    When viene invocato endpoint per lo stato health con successo

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-12] Inserimento di un nuovo file CSV di tracing giornaliero per una data marcata con MISSING tracing
    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV valido e minimale per un giorno in stato "MISSING"
    When viene inviato il file CSV "PREPARATO"
    Then si attende che il file di tracing caricato passi in stato "COMPLETED"

  @interopTracingCsv
  Scenario Outline: [INTEROP-TRACING-13-1] Chiamate a Tracing con path con carattere percent-encoded non valido
    Given l'utenza "TENANT1" effettua le chiamate
    When viene chiamato tracing con <metodo> e <subpath> contenente un carattere percent-encoded non valido
    Then la richiesta fallisce con <esito>
    Examples:
    | metodo | subpath  | esito       |
    | GET    | endpoint | not found   |
    | GET    | id       | bad request |
    | POST   | endpoint | not found   |
    | POST   | id       | bad request |

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-14-1] Verifica arricchimento dati per l'inserimento di un nuovo file CSV di tracing
    Quando il servizio Tracing riceve un file CSV di tracing valido viene arricchito con altri dati producendo un
    ulteriore file CSV arricchito.

    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV valido e minimale per una data disponibile
    When viene inviato il file CSV "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    Then si attende che il file di tracing venga arricchito con altri dati

    When viene sovrascritto il tracing aggiunto in precedenza con il csv "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    Then si attende che il file di tracing venga arricchito con altri dati

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-14-2] Verifica arricchimento dati per l'inserimento di un nuovo pesante file CSV di tracing
    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV valido da 70 MB per una data disponibile
    When viene inviato il file CSV "PREPARATO"

    # SCENARIO 4 - Sotto caso stato PENDING
    # Recupero della lista dei tracing inviati applicando il filtro sullo stato PENDING
    When viene recuperata la lista di tracing con stato "PENDING"
    Then la risposta contiene soltanto i tracing con stato "PENDING"

    # Prosecuzione 14.2
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    Then si attende fino a 5 minuti che il file di tracing arricchito venga generato

    When viene sovrascritto il tracing aggiunto in precedenza con il csv "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "COMPLETED"
    Then si attende fino a 5 minuti che il file di tracing arricchito venga generato

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-15-1] Invio di un file CSV di tracing con header del CSV errato
    Given l'utenza "TENANT1" effettua le chiamate
    And viene recuperata la prima data disponibile per un invio del file CSV
    When viene inviato il file CSV "ERRATO_HEADER_CAMPO_MANCANTE"
    Then si attende che il file di tracing caricato passi in stato "ERROR"
    And si attende che l'invio in ERROR sia registrato come header CSV non valido
    And nessun file CSV di tracing viene arricchito

    When viene recuperata la prima data disponibile per un invio del file CSV
    And viene inviato il file CSV "ERRATO_HEADER_NOME_CAMPO"
    Then si attende che il file di tracing caricato passi in stato "ERROR"
    And si attende che l'invio in ERROR sia registrato come header CSV non valido
    And nessun file CSV di tracing viene arricchito

    When viene recuperata la prima data disponibile per un invio del file CSV
    And viene inviato il file CSV "ERRATO_HEADER_DOPPIA_VIRGOLA"
    Then si attende che il file di tracing caricato passi in stato "ERROR"
    And si attende che l'invio in ERROR sia registrato come header CSV non valido
    And nessun file CSV di tracing viene arricchito

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-15-2] Verifica che l'invio di un file CSV con un record errato non generi l'arricchimento dati
    Quando il servizio Tracing riceve un file CSV di tracing valido ma con anche un solo record errato, viene registrato
    nel CSV degli errori e non viene generato alcun file CSV arricchito.

    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV con un codice HTTP non valido per una data disponibile
    When viene inviato il file CSV "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "ERROR"
    Then nessun file CSV di tracing viene arricchito
    And si attende che il record con codice HTTP non valido sia tracciato negli errori

    When viene sovrascritto il tracing aggiunto in precedenza con il csv "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "ERROR"
    Then nessun file CSV di tracing viene arricchito
    And si attende che il record con codice HTTP non valido sia tracciato negli errori

  @interopTracingCsv
  Scenario: [INTEROP-TRACING-15-3] Verifica lo stato WARNING per l'inserimento di un nuovo file CSV di tracing con purpose ID non conforme
    Lo stato WARNING ha priorità più bassa dello stato ERROR, quindi si invia un file CSV di tracing corretto ma con un
    purpose ID che esista in PDND ma non riferibile all'aderente che ha inviato il file. Il purpose ID risulterà non
    conforme e il tracciato sarà segnato con lo stato WARNING.

    Given l'utenza "TENANT1" effettua le chiamate
    And viene preparato un file CSV valido con un purpose ID non conforme per una data disponibile
    When viene inviato il file CSV "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "WARNING"
    Then si attende che l'invio in WARNING sia registrato come purpose ID non conforme all'utenza

    When viene sovrascritto il tracing aggiunto in precedenza con il csv "PREPARATO"
    And si attende che il file di tracing caricato passi in stato "WARNING"
    Then si attende che l'invio in WARNING sia registrato come purpose ID non conforme all'utenza
