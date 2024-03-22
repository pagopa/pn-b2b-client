Feature: eliminazione stream

    #--------------CANCELLAZIONE DI UNO STREAM------------

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_73] Cancellazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con gruppo diverso.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con gruppo differente dallo stream
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_74] Cancellazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con stesso gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_75] Cancellazione di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata



  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_78] Cancellazione di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey con gruppo diverso.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con gruppo differente dallo stream
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_79] Cancellazione di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey con stesso gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_80] Cancellazione di uno stream notifica senza gruppo, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_82] Cancellazione di uno stream notifica senza gruppo, con eventType "STATUS"  utilizzando un apikey con gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_83] Cancellazione di uno stream notifica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_85] Cancellazione di uno stream notifica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey con gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_152] Cancellazione di uno stream notifica con gruppi appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "LAST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_153] Cancellazione di uno stream notifica con gruppi non appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "UGUALI"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_119] Cancellazione per una PA di uno stream che non esiste per la stessa PA
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si cancella lo stream che non esiste e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "404"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

     #DELETE
  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_160] Creazione di uno stream senza gruppo con la V10 e  cancellazione dello stesso utilizzando un apikey con gruppi.-PN-10218
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si cancella lo stream creato per il "Comune_Multi" con versione "V10"
    Then l'operazione non ha prodotto errori