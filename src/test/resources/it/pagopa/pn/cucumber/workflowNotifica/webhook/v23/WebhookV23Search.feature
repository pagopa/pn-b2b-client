Feature: ricerca di uno stream

  #--------------LETTURA METADATI DI UNO STREAM------------
  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_27] Lettura di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey con gruppo diverso.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con gruppo differente dallo stream
    When viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23" e apiKey aggiornata
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_28] Lettura di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con gruppo diverso..
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_Multi" con gruppo differente dallo stream
    And viene impostata l'apikey appena generata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23" e apiKey aggiornata
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_29] Lettura di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con stesso gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_30] Lettura di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    When viene impostata l'apikey appena generata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23" e apiKey aggiornata
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_33] Lettura di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey con gruppo diverso.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con gruppo differente dallo stream
    When viene impostata l'apikey appena generata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23" e apiKey aggiornata
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_34] Lettura di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey con stesso gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    And viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_35] Lettura di uno stream notifica senza gruppo, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    And viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_37] Lettura di uno stream notifica senza gruppo, con eventType "STATUS"  utilizzando un apikey con gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    When viene impostata l'apikey appena generata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23" e apiKey aggiornata
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_38] Lettura di uno stream notifica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    And viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_40] Lettura di uno stream notifica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey con gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    When viene impostata l'apikey appena generata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23" e apiKey aggiornata
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_122] Lettura per una PA di uno stream che non esiste per la stessa PA
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    When si legge lo stream che non esiste e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "400"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_156] Lettura di uno stream notifica con gruppi appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "LAST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    And viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_157] Lettura di uno stream notifica con gruppi non appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "UGUALI"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    When lo stream viene recuperato dal sistema tramite stream id con versione "V23" e apiKey aggiornata
    Then l'operazione non ha prodotto errori
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
