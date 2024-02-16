Feature: replace streamID webhook

  #--------------CREAZIONE CON REPLACED_STREAM_ID DI UNO STREAM--------------------
  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_101] Creazione con replaceID di uno stream notifica con gruppo uguale al precedente stream con eventType "STATUS" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_103]  Creazione con replaceID di uno stream notifica con gruppo  non appartenente alla PA, con eventType "STATUS" utilizzando un apikey master.(replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si creano i nuovi stream per il "Comune_1" con versione "V23" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "ALTRA_PA" e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_104]  Creazione con replaceID di uno stream notifica con gruppo diverso al precedente stream con eventType "STATUS" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "LAST" e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_105]  Creazione con replaceID di uno stream notifica con gruppo uguale al precedente stream con eventType "STATUS" utilizzando un apikey con gruppo. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_106] Creazione con replaceID di uno stream notifica con gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_108] Creazione con replaceID di uno stream notifica con gruppo  non appartenente alla PA, con eventType "TIMELINE" utilizzando un apikey master.(replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si creano i nuovi stream per il "Comune_1" con versione "V23" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "ALTRA_PA" e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_109]  Creazione con replaceID di uno stream notifica con gruppo diverso al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "LAST" e apiKey aggiornata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_110]  Creazione con replaceID di uno stream notifica con gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey con gruppo. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_111]  Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "STATUS" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "NO_GROUPS" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "NO_GROUPS" e apiKey aggiornata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_113]  Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "STATUS" utilizzando un apikey con gruppo. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "NO_GROUPS" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    When lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_114]  Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "NO_GROUPS" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "NO_GROUPS" e apiKey aggiornata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_115]  Creazione con replaceID di uno stream notifica con gruppo diverso al precedente stream con eventType "STATUS" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "LAST" e apiKey aggiornata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_116]  Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey con gruppo. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "NO_GROUPS" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    When lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_117]  Creazione per una PA di 10 nuovi stream notifica con eventType TIMELINE e senza gruppo, disabilitazione di uno stream e creazione di un nuovo stream.
    Given si predispone 10 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si creano i nuovi stream per il "Comune_1" con versione "V23" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And si disabilita lo stream creato con versione "V23" e apiKey aggiornata
    And l'operazione non ha prodotto errori
    And si predispone 10 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    When si creano i nuovi stream per il "Comune_1" con versione "V23" e apiKey aggiornata
    And l'operazione non ha prodotto errori
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.1_102]  Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream disabilitato con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And si crea il nuovo stream per il "Comune_1" con replaceId "NO_SET" con un gruppo disponibile "NO_GROUPS" e apiKey aggiornata
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And si disabilita lo stream creato con versione "V23"
    And l'operazione non ha prodotto errori
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "NO_GROUPS" e apiKey aggiornata
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @clean @webhook1
  Scenario: [B2B-STREAM_ES1.5_142] Creazione di uno stream notifica senza gruppo, con eventType "TIMELINE" utilizzando un apikey con gruppo con la versione V23.(replacedStreamId di uno stream creato con la versione V10 settato).
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    When si crea il nuovo stream per il "Comune_1" con replaceId "SET" con un gruppo disponibile "FIRST" e apiKey aggiornata
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata