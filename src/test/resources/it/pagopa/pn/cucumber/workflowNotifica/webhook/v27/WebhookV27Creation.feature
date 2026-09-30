Feature: verifica creazione stream

  Scenario: [ONLY_FOR_DEBUG] Cancellazione stream notifica
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    Given vengono cancellati tutti gli stream presenti del "Comune_1" con versione "V27"
    Given vengono cancellati tutti gli stream presenti del "Comune_2" con versione "V27"

  #--------------CREAZIONE DI UNO STREAM--------------------
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_1] Creazione per una PA di 10 nuovi stream notifica con eventType TIMELINE e senza gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si creano i nuovi stream per il "Comune_Multi" con versione "V27"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK" per il "Comune_Multi"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_1waitForAccepted] Creazione per una PA di 10 nuovi stream notifica con eventType TIMELINE e senza gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V27"
    And agli stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si creano i nuovi stream per il "Comune_Multi" con versione "V27" e filtro status "DEFAULT"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK" per il "Comune_Multi"
    And l'apiKey viene cancellata


  #https://pagopa.atlassian.net/browse/PN-19952
  # Test che verifica che se viene creato uno stream con waitForAccepted a true e filtro status che non contiene DEFAULT o REQUEST_ACCEPTED allora lo stream non viene creato
  # e viene ritornato un errore 403
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_PN_19952_1_KO] Creazione di uno stream con waitForAccepted a true e filtro status che non contiene DEFAULT o REQUEST_ACCEPTED.
  Si verifica che la creazione dello stream ritorni un errore 403.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 5 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V27"
    And agli stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Multi" con versione "V27" e filtro status "DELIVERING"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  #https://pagopa.atlassian.net/browse/PN-19952
  # Test che verifica che se viene creato uno stream con waitForAccepted a true e filtro status che contiene DEFAULT o REQUEST_ACCEPTED allora lo stream non viene creato con successo
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario Outline: [B2B-STREAM_PN_19952_2_OK] Creazione di uno stream con waitForAccepted a true e filtro status che contiene DEFAULT o REQUEST_ACCEPTED. Si verifica che lo stream venga creato con successo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 5 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V27"
    And agli stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Multi" con versione "V27" e filtro status "<statusFilter>"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK" per il "Comune_Multi"
    And l'apiKey viene cancellata
    Examples:
      | statusFilter                        |
      | DELIVERING,DEFAULT                  |
      | DELIVERING,REQUEST_ACCEPTED         |
      | DELIVERING,DEFAULT,REQUEST_ACCEPTED |

  @webhookV27 @precondition @cleanWebhook @webhook2
  #LIMITE SPECIFICO PER PA: n/a; LIMITE DEFAULT (pnConfigurations.MaxStreams  = 10)
  Scenario: [B2B-STREAM_ES1.1_2] Creazione per una PA di 11 nuovi stream notifica con eventType TIMELINE e senza gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si creano i nuovi stream per il "Comune_Multi" con versione "V27"
    When si predispone 1 nuovo stream denominato "stream-test-11" con eventType "TIMELINE" con versione "V27"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V27"
    Then l'operazione ha prodotto un errore con status code "409"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_3] Creazione per una PA di 10 nuovi stream notifica con eventType TIMELINE con gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si creano i nuovi stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_3waitForAccepted] Creazione per una PA di 10 nuovi stream notifica con eventType TIMELINE con gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V27"
    And agli stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si creano i nuovi stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  #LIMITE SPECIFICO PER PA: n/a; LIMITE DEFAULT (pnConfigurations.MaxStreams  = 10)
  Scenario: [B2B-STREAM_ES1.1_4] Creazione per una PA di 11 nuovi stream notifica con eventType TIMELINE con gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si creano i nuovi stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    When si predispone 1 nuovo stream denominato "stream-test-11" con eventType "TIMELINE" con versione "V27"
    And si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then l'operazione ha prodotto un errore con status code "409"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_5] Creazione per una PA di 10 nuovi stream notifica con eventType STATUS e senza gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si creano i nuovi stream per il "Comune_Multi" con versione "V27"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancellano gli stream creati per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_5waitForAccepted] Creazione per una PA di 10 nuovi stream notifica con eventType STATUS e senza gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "STATUS" con versione "V27"
    And agli stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si creano i nuovi stream per il "Comune_Multi" con versione "V27" e filtro status "DEFAULT"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancellano gli stream creati per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  #LIMITE SPECIFICO PER PA: n/a; LIMITE DEFAULT (pnConfigurations.MaxStreams  = 10)
  Scenario: [B2B-STREAM_ES1.1_6] Creazione per una PA di 11 nuovi stream notifica con eventType STATUS e senza gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si creano i nuovi stream per il "Comune_Multi" con versione "V27"
    When si predispone 1 nuovo stream denominato "stream-test-11" con eventType "STATUS" con versione "V27"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V27"
    Then l'operazione ha prodotto un errore con status code "409"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_7] Creazione per una PA di 10 nuovi stream notifica con eventType TIMELINE con gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si creano i nuovi stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_7waitForAccepted] Creazione per una PA di 10 nuovi stream notifica con eventType TIMELINE con gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "STATUS" con versione "V27"
    And agli stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si creano i nuovi stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  #LIMITE SPECIFICO PER PA: n/a; LIMITE DEFAULT (pnConfigurations.MaxStreams  = 10)
  Scenario: [B2B-STREAM_ES1.1_8] Creazione per una PA di 11 nuovi stream notifica con eventType STATUS con gruppo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V27"
    And si predispongono 10 nuovi stream denominati "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si creano i nuovi stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    When si predispone 1 nuovo stream denominato "stream-test-11" con eventType "STATUS" con versione "V27"
    And si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then l'operazione ha prodotto un errore con status code "409"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_9] Creazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_9waitForAccepted] Creazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And allo stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_11] Creazione di uno stream notifica con gruppo non appartenente alla PA dell'apiKey, con eventType "STATUS"  utilizzando un apikey con gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "ALTRA_PA" (caso errato)
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_12] Creazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con gruppo diverso.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "LAST" (caso errato)
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_13] Creazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con stesso gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_13waitForAccepted] Creazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con stesso gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And allo stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_16] Creazione di uno stream notifica con gruppo non appartenente alla PA dell'apiKey, con eventType "TIMELINE"  utilizzando un apikey con gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "ALTRA_PA" (caso errato)
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_17] Creazione di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey con gruppo diverso.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "LAST" (caso errato)
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_18] Creazione di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey con stesso gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_18waitForAccepted] Creazione di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey con stesso gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V27"
    And allo stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_19] Creazione di uno stream notifica senza gruppo, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Multi" con versione "V27"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_19waitForAccepted] Creazione di uno stream notifica senza gruppo, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And allo stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Multi" con versione "V27" e filtro status "DEFAULT"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_21] Creazione di uno stream notifica senza gruppo, con eventType "STATUS"  utilizzando un apikey con gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS" (caso errato)
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_22] Creazione di uno stream notifica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Multi" con versione "V27"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_22waitForAccepted] Creazione di uno stream notifica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V27"
    And allo stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Multi" con versione "V27" e filtro status "DEFAULT"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_24] Creazione di uno stream notifica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey con gruppo.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS" (caso errato)
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_25] Creazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_25waitForAccepted] Creazione di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey master.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And allo stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    Then si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_148] Creazione di uno stream notifica con gruppi appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "LAST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  # identico al test sopra, ma stavolta waitForAccepted viene impostato a true
  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_148waitForAccepted] Creazione di uno stream notifica con gruppi appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And allo stream versione "V27" si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "LAST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V27"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V27"
    And viene verificata la corretta cancellazione con versione "V27"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_149] Creazione di uno stream notifica con gruppi non appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream con versione "V27" per il "Comune_Multi" con un gruppo disponibile "ALL" (caso errato)
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV27 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_120] Consumo per una PA di uno stream che non esiste per la stessa PA
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V27"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    When si consuma lo stream che non esiste con la versione "V27" e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "404"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata