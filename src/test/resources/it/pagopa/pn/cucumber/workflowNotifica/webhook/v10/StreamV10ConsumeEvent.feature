#Scenari che usano solo stream V10: non dipendono dalla versione della suite, quindi sono eseguiti una sola volta e non in ogni suite.
Feature: avanzamento notifiche webhook b2b - stream creato con la V10

  @webhookV10Fisso @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.2_126] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo e verifica legalfactIds valorizzato -PN-10278
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verona            |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_2" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione "V10"
    And viene verificato che il campo legalFactIds sia valorizzato nel EventStream con la versione "V10"

  @webhookV10Fisso @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.3_162] Creazione di uno stream senza gruppo con la V10 e  lettura Eventi di timeline o di cambio di stato con la versione V10 utilizzando un apikey con gruppi. -PN-10218.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione "V10"

 #SERVE INTEGRAZIONE CON RADD
  @webhookV10Fisso @precondition @cleanWebhook @ignore
  Scenario: [B2B-STREAM_ES3.1_145] Creazione di uno stream senza gruppo con la V10 e  lettura di nuovi eventi di timeline con un apikey abilitata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10" e filtro di timeline "NOTIFICATION_RADD_RETRIEVED"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
    And Il cittadino Mario Cucumber come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    Then si verifica che non siano presenti eventi nello stream con versione "V10" del "Comune_1"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
