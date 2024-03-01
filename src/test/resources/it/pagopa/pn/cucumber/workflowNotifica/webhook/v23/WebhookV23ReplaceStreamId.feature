Feature: replace streamID webhook

  #--------------CREAZIONE CON REPLACED_STREAM_ID DI UNO STREAM--------------------
  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_101] Creazione con replaceID di uno stream notifica con gruppo uguale al precedente stream con eventType "STATUS" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_103]  Creazione con replaceID di uno stream notifica con gruppo  non appartenente alla PA, con eventType "STATUS" utilizzando un apikey master.(replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "ALTRA_PA"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_104]  Creazione con replaceID di uno stream notifica con gruppo diverso al precedente stream con eventType "STATUS" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "LAST"
    Then l'operazione non ha prodotto errori
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_105]  Creazione con replaceID di uno stream notifica con gruppo uguale al precedente stream con eventType "STATUS" utilizzando un apikey con gruppo. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_106] Creazione con replaceID di uno stream notifica con gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_108] Creazione con replaceID di uno stream notifica con gruppo  non appartenente alla PA, con eventType "TIMELINE" utilizzando un apikey master.(replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "ALTRA_PA"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_109]  Creazione con replaceID di uno stream notifica con gruppo diverso al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "LAST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_110]  Creazione con replaceID di uno stream notifica con gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey con gruppo. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "FIRST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_111]  Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "STATUS" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "NO_GROUPS"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_113]  Creazione con replaceID di uno stream notifica senza gruppo con uno stream con gruppo con eventType "STATUS" utilizzando un apikey con gruppo. (replacedStreamId settato)..
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "FIRST"
    When lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_114]  Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "NO_GROUPS"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_115]  Creazione con replaceID di uno stream notifica con gruppo diverso al precedente stream con eventType "STATUS" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "LAST"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_116]  Creazione con replaceID di uno stream notifica senza gruppo con uno stream con gruppo con eventType "TIMELINE" utilizzando un apikey con gruppo. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "FIRST"
    When lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

#CONTROLLARE IL CORRETTO COMPORTAMENTO
  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_117]  Creazione per una PA di 10 nuovi stream notifica con eventType TIMELINE e senza gruppo, disabilitazione di uno stream e creazione di un nuovo stream.
    Given si predispone 10 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si creano i nuovi stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And si disabilita lo stream creato per il comune "Comune_Multi" con versione V23 e apiKey aggiornata
    And l'operazione non ha prodotto errori
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    When si creano i nuovi stream per il "Comune_Multi" con versione "V23"
    Then l'operazione non ha prodotto errori
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_102]  Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream disabilitato con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And si disabilita lo stream V23 creato per il comune "Comune_Multi"
    And l'operazione non ha prodotto errori
    When si crea il nuovo stream V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "NO_GROUPS"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.5_142] Creazione di uno stream notifica con gruppo, con eventType "TIMELINE" con V23 e repleceId di uno stream V10 senza gruppo e utilizzando un apikey con gruppo con la versione V23.(replacedStreamId di uno stream creato con la versione V10 settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    Then si crea il nuovo stream V10_V23 per il "Comune_Multi" con replaceId con un gruppo disponibile "NO_GROUPS"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata