#Lo scenario usa sempre uno stream V10, indipendentemente dalla versione della suite: per questo viene eseguito una sola volta.
Feature: eliminazione stream - stream creato con la V10

     #DELETE
  @webhookV10Fisso @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.1_160] Creazione di uno stream senza gruppo con la V10 e  cancellazione dello stesso utilizzando un apikey con gruppi.-PN-10218
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si cancella lo stream creato per il "Comune_Multi" con versione "V10"
    Then l'operazione non ha prodotto errori
