Feature: avanzamento notifiche webhook b2b V23

  ##ONLY FOR DUBUG AND STRESS TEST
  Scenario: test lettura
    And vengono letti gli eventi dello stream con id "00d62a5b-2c90-484a-920a-f75b160bf123" v23


  Scenario: creazione stream
    And si predispone 700 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"


  Scenario: creazione stream e consumo stream
    And si predispone 700 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And vengono letti tutti gli eventi degli stream v23 creati per il test di carico per 120 minuti


  Scenario: [B2B-STREAM_ES123123124] test lettura
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination  | Galileo galileo   |
      | taxId         | GLLGLL64B15G702I  |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED


#COMUNE 1
  @webhookV23 @precondition @cleanWebhook @webhook1
  Scenario: [B2B-STREAM_ES1.1_112] Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato) con controllo EventId incrementale e senza duplicati.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination  | Galileo galileo   |
      | taxId         | GLLGLL64B15G702I  |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_1" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    #TEST LETTURA ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "ACCEPTED" con la versione V23
    #TEST LETTURA REQUEST_ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione V23
    #TEST LETTURA DIGITAL_SUCCESS_WORKFLOW
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" con la versione V23
    And viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati V23
    #TEST VERIFICA CORRISPONDENZA ELEMENTO DI TIMELINE STREAM
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_COURTESY_MESSAGE" con versione V23 e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_DOMICILE" con versione V23 e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK" con versione V23 e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" con versione V23 e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    #TEST VERIFICA REFINEMENT
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REFINEMENT" con versione V23 e apiKey aggiornata con position 0
    And Si verifica che l'elemento di timeline REFINEMENT abbia il timestamp uguale a quella presente nel webhook con la versione V23
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @precondition @cleanWebhook @webhook1
  Scenario: [B2B-STREAM_ES1.3_127] Consumo di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con stesso gruppo.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_1" con un gruppo disponibile "FIRST"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "ACCEPTED" con la versione V23
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione V23
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @precondition @cleanWebhook @webhook1
  Scenario: [B2B-STREAM_ES1.1_158] Consumo di uno stream notifica con gruppi appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_1" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED" con versione V23 e apiKey aggiornata con position 0
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata



    #COMUNE 2

  @webhookV23 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.3_128] Consumo di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verona            |
    And destinatario
      | denomination  | Galileo galileo   |
      | taxId         | GLLGLL64B15G702I  |
      | payment       | NULL              |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_2" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_2" con un gruppo disponibile "FIRST"
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "REQUEST_ACCEPTED" con versione V23 e apiKey aggiornata con position 0
    #TEST DEANONIMIZZAZIONE
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "SEND_COURTESY_MESSAGE" con versione V23 e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline con delega "NO" digitale
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "SEND_DIGITAL_DOMICILE" con versione V23 e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline con delega "NO" digitale
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK" con versione V23 e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline con delega "NO" digitale
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" con versione V23 e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline con delega "NO" digitale
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.3_125_1] Consumo di uno stream notifica disabilitato senza gruppo, con eventType "STATUS"  utilizzando un apikey master (caso errato).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verono            |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2" con versione "V23"
    And si disabilita lo stream V23 creato per il comune "Comune_2"
    And l'operazione non ha prodotto errori
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    When si verifica che non siano presenti eventi nello stream v23 del "Comune_2"
    Then non ci sono nuovi eventi nello stream
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.2_124] Verifica corretta scrittura degli eventi di una notifica creata con un apikey master, dove l’evento stesso deve essere salvato solo negli stream senza gruppi.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verona            |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_2" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_2" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And si predispone 1 nuovo stream denominato "stream-test1" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione V23
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.2_126] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo e verifica legalfactIds valorizzato -PN-10278
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verona            |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_2" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "REQUEST_ACCEPTED"
    And viene verificato che il campo legalfactIds sia valorizzato nel EventStream


  #COMUNE MULTI

  @webhookV23 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.3_50_1] Consumo di uno stream notifica analogica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey master e verifica corrispondenza tra i detail del webhook e quelli della timeline.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Giovanna D'Arco |
      | taxId | DRCGNN12A46A326K |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PREPARE_ANALOG_DOMICILE" con versione V23 e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_DOMICILE" con versione V23 e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" con versione V23 e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES3.1_146_2]  Lettura e verifica de-anonimizzazione con un apiKey con gruppo degli eventi di timeline di una notifica analogica inviata con un apikey con gruppo e salvati in uno stream dell'ente con gruppo (Stesso gruppo)
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Giovanna D'Arco |
      | taxId | DRCGNN12A46A326K |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_AR |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PREPARE_ANALOG_DOMICILE" con versione V23 e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline con delega "NO" analogico
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_DOMICILE" con versione V23 e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline con delega "NO" analogico
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" con versione V23 e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline con delega "NO" analogico
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV23 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.2_123] Creazione di stream con apiKey con gruppi differenti e verifica corretta scrittura degli eventi di notifiche create con le stesse apiKey.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V23"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And si predispone 1 nuovo stream denominato "stream-test1" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V23"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "REQUEST_ACCEPTED" con versione V23 e apiKey aggiornata con position 1
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV23 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.3_162] Creazione di uno stream senza gruppo con la V10 e  lettura Eventi di timeline o di cambio di stato con la versione V10 utilizzando un apikey con gruppi. -PN-10218.
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "REQUEST_ACCEPTED"



 #--------------LETTURA NUOVO EVENTO DI TIMELINE DI UNO STREAM------------DA ATTIVARE

  #SERVE INTEGRAZIONE CON RADD
  @webhookV23 @precondition @cleanWebhook @ignore
  Scenario: [B2B-STREAM_ES1.4_136] Lettura degli eventi di timeline  dello stream senza gruppo con visualizzazione del nuovo evento di timeline utilzzando un apikey abilitata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_1" con versione "V23"
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "NOTIFICATION_RADD_RETRIEVED" con versione V23 e apiKey aggiornata con position 0
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


 #SERVE INTEGRAZIONE CON RADD
  @webhookV23 @precondition @cleanWebhook @ignore
  Scenario: [B2B-STREAM_ES1.4_137] Lettura degli eventi di timeline  dello stream con gruppo con visualizzazione  del nuovo evento di timeline utilzzando un apikey abilitata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_2" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream V23 per il "Comune_2" con un gruppo disponibile "FIRST"
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "NOTIFICATION_RADD_RETRIEVED" con versione V23 e apiKey aggiornata con position 0
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


 #SERVE INTEGRAZIONE CON RADD
  @webhookV23 @precondition @cleanWebhook @ignore
  Scenario: [B2B-STREAM_ES3.1_145] Creazione di uno stream senza gruppo con la V10 e  lettura di nuovi eventi di timeline con un apikey abilitata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10" e filtro di timeline "NOTIFICATION_RADD_RETRIEVED"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    Then verifica non presenza di eventi nello stream del "Comune_1"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata









