#Scenari che usano solo stream V10: non dipendono dalla versione della suite, quindi sono eseguiti una sola volta e non in ogni suite.
Feature: ricerca di uno stream - stream creato con la V10

  @webhookV10Fisso @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_163] Creazione di uno stream senza gruppo con la V10 e recupero metadati utilizzando un apikey con gruppi. -PN-10218.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V10"
    And si predispongono 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
