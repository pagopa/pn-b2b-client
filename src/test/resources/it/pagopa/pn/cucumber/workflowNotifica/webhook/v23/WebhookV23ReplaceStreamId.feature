Feature: replace streamID webhook

  @webhookV23 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.5_142] Creazione di uno stream notifica con gruppo, con eventType "TIMELINE" con V23 e replaceId di uno stream V10 senza gruppo e utilizzando un apikey con gruppo con la versione V23.(replacedStreamId di uno stream creato con la versione V10 settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
    Then si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And si crea il nuovo stream con versione "V23" per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS" con replaceId dello stream creato con la versione "V10" - Cross Versioning
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.5_142status] Creazione di uno stream notifica con gruppo, con eventType "STATUS" con V23 e replaceId di uno stream V10 senza gruppo e utilizzando un apikey con gruppo con la versione V23.(replacedStreamId di uno stream creato con la versione V10 settato).
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V10"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
    Then si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And si crea il nuovo stream con versione "V23" per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS" con replaceId dello stream creato con la versione "V10" - Cross Versioning
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
