Feature: disabilitazione stream

  #--------------DISABILITAZIONE DI UNO STREAM------------

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_58] Disabilitazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con gruppo diverso.
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
    When si disabilita lo stream creato per il comune "Comune_Multi" con versione V23 e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_59] Disabilitazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con stesso gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si disabilita lo stream V23 creato per il comune "Comune_Multi"
    Then l'operazione non ha prodotto errori
    And si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    And viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_60] Disabilitazione di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
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
    When si disabilita lo stream creato per il comune "Comune_Multi" con versione V23 e apiKey aggiornata
    Then l'operazione non ha prodotto errori
    And si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    And viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_63] Disabilitazione di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey con gruppo diverso.
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
    When si disabilita lo stream creato per il comune "Comune_Multi" con versione V23 e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_64] Disabilitazione di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey con stesso gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si disabilita lo stream V23 creato per il comune "Comune_Multi"
    Then l'operazione non ha prodotto errori
    And si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    And viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata



  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_65] Disabilitazione di uno stream notifica senza gruppo, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si disabilita lo stream V23 creato per il comune "Comune_Multi"
    Then l'operazione non ha prodotto errori
    And si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    And viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_67] Disabilitazione di uno stream notifica senza gruppo, con eventType "STATUS"  utilizzando un apikey con gruppo.
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
    When si disabilita lo stream creato per il comune "Comune_Multi" con versione V23 e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_68] Disabilitazione di uno stream notifica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si disabilita lo stream V23 creato per il comune "Comune_Multi"
    Then l'operazione non ha prodotto errori
    And si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    And viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_70] Disabilitazione di uno stream notifica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey con gruppo.
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
    When si disabilita lo stream creato per il comune "Comune_Multi" con versione V23 e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_150] Disabilitazione di uno stream notifica con gruppi appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "LAST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si disabilita lo stream creato per il comune "Comune_Multi" con versione V23 e apiKey aggiornata
    Then l'operazione non ha prodotto errori
    And si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    And viene verificata la corretta cancellazione con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_151] Disabilitazione di uno stream notifica con gruppi non appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "UGUALI"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    When si disabilita lo stream creato per il comune "Comune_Multi" con versione V23 e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_118] Disabilitazione per una PA di uno stream che non esiste per la stessa PA
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si disabilita lo stream che non esiste e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "404"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_61] Disabilitazione di uno stream notifica senza gruppo già disabilitato, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si disabilita lo stream V23 creato per il comune "Comune_Multi"
    Then l'operazione non ha prodotto errori
    When si disabilita lo stream V23 creato per il comune "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_77] Cancellazione di uno stream notifica senza gruppo già disabilitato, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si disabilita lo stream V23 creato per il comune "Comune_Multi"
    Then l'operazione non ha prodotto errori
    When si cancella lo stream creato per il "Comune_Multi" con versione "V23"
    Then l'operazione non ha prodotto errori
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_98] Aggiornamento di uno stream notifica senza gruppo già disabilitato, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si disabilita lo stream V23 creato per il comune "Comune_Multi"
    Then l'operazione non ha prodotto errori
    When si aggiorna lo stream creato con versione "V23"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_32] Lettura di uno stream notifica senza gruppo già disabilitato, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si disabilita lo stream V23 creato per il comune "Comune_Multi"
    Then l'operazione non ha prodotto errori
    When lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    Then l'operazione non ha prodotto errori
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata