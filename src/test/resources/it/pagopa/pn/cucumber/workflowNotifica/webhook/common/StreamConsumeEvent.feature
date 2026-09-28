#Scenari validi per tutte le versioni dello stream: la versione non è indicata negli step, la definisce la suite che esegue il feature.
#I tag @streamVxx indicano le sole versioni per cui lo scenario è valido; @webhookStream vale per tutte.
Feature: avanzamento notifiche webhook b2b V29

  #COMUNE 1
  @streamV25 @streamV26 @precondition @cleanWebhook @webhook1
  Scenario: [B2B-STREAM_ES1.1_112] Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato) con controllo EventId incrementale e senza duplicati.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination | Galileo galileo  |
      | taxId        | GLLGLL64B15G702I |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_1" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    #TEST LETTURA REQUEST_ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED"
    #TEST LETTURA DIGITAL_SUCCESS_WORKFLOW
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW"
    And viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  #COMUNE 1
  @streamV27 @streamV28 @streamV29 @precondition @cleanWebhook @webhook1
  Scenario: [B2B-STREAM_ES1.1_112] Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato) con controllo EventId incrementale e senza duplicati.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination | Galileo galileo  |
      | taxId        | GLLGLL64B15G702I |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And allo stream si setta il campo waitForAccepted introdotto con la versione 27 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_1" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    #TEST LETTURA ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "ACCEPTED"
    #TEST LETTURA REQUEST_ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED"
    #TEST LETTURA DIGITAL_SUCCESS_WORKFLOW
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW"
    And viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati
    #TEST VERIFICA CORRISPONDENZA ELEMENTO DI TIMELINE STREAM
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_COURTESY_MESSAGE" e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_DOMICILE" e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK" e apiKey aggiornata con position 0
    #TODO: lo step seguente fallisce a causa di una discrepanza tra i details b2b e quelli webhook (deliveryFailureCause B2b = "", deliveryFailureCause Webhook = null)
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    #TEST VERIFICA REFINEMENT
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REFINEMENT" e apiKey aggiornata con position 0
    #TODO: fixato lo step di sopra, il test fallirebbe quando confronta il timestamp di questi elementi
    And Si verifica che l'elemento di timeline "SCHEDULE_REFINEMENT" "abbia" il timestamp uguale a quello di "REFINEMENT" presente nel webhook
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @streamV23 @streamV27 @streamV28 @streamV29 @precondition @cleanWebhook @webhook1
  Scenario: [B2B-STREAM_ES1.3_127] Consumo di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con stesso gruppo.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS"
    And Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_1" con un gruppo disponibile "FIRST"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @streamV24 @streamV25 @streamV26 @precondition @cleanWebhook @webhook1
  Scenario: [B2B-STREAM_ES1.3_127] Consumo di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con stesso gruppo.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS"
    And Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_1" con un gruppo disponibile "FIRST"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @streamV23 @streamV27 @streamV28 @streamV29 @precondition @cleanWebhook @webhook1
  Scenario: [B2B-STREAM_ES1.1_158] Consumo di uno stream notifica con gruppi appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_1" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_1" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED" e apiKey aggiornata con position 0
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @streamV24 @streamV25 @streamV26 @precondition @webhook1
  Scenario: [B2B-STREAM_ES1.1_158] Consumo di uno stream notifica con gruppi appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "REQUEST_ACCEPTED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

    #COMUNE 2

  @streamV23 @streamV27 @streamV28 @streamV29 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.3_128] Consumo di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verona            |
    And destinatario
      | denomination | Galileo galileo  |
      | taxId        | GLLGLL64B15G702I |
      | payment      | NULL             |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_2" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2" con un gruppo disponibile "FIRST"
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "REQUEST_ACCEPTED" e apiKey aggiornata con position 0
    #TEST DEANONIMIZZAZIONE
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "SEND_COURTESY_MESSAGE" e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline senza delega digitale
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "SEND_DIGITAL_DOMICILE" e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline senza delega digitale
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK" e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline senza delega digitale
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline senza delega digitale
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

    #COMUNE 2

  @streamV24 @streamV25 @streamV26 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.3_128] Consumo di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verona            |
    And destinatario
      | denomination | Galileo galileo  |
      | taxId        | GLLGLL64B15G702I |
      | payment      | NULL             |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_2" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2" con un gruppo disponibile "FIRST"
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookStream @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.3_125_1] Consumo di uno stream notifica disabilitato senza gruppo, con eventType "STATUS"  utilizzando un apikey master (caso errato).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verono            |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS"
    And Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2"
    And si disabilita lo stream creato per il comune "Comune_2"
    And l'operazione non ha prodotto errori
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    Then si verifica che non siano presenti eventi nello stream del "Comune_2"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @streamV23 @streamV27 @streamV28 @streamV29 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.2_124] Verifica corretta scrittura degli eventi di una notifica creata con un apikey master, dove l’evento stesso deve essere salvato solo negli stream senza gruppi.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verona            |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_2" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And si predispone 1 nuovo stream denominato "stream-test1" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "REQUEST_ACCEPTED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @streamV24 @streamV25 @streamV26 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.2_124] Verifica corretta scrittura degli eventi di una notifica creata con un apikey master, dove l’evento stesso deve essere salvato solo negli stream senza gruppi.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verona            |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_2" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And si predispone 1 nuovo stream denominato "stream-test1" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "REQUEST_ACCEPTED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  #COMUNE MULTI

  @streamV23 @streamV27 @streamV28 @streamV29 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.3_50_1] Consumo di uno stream notifica analogica senza gruppo, con eventType "TIMELINE"  utilizzando un apikey master e verifica corrispondenza tra i detail del webhook e quelli della timeline.
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination                        | Mario Gherkin    |
      | taxId                               | CLMCST42R12D969Z |
      | digitalDomicile                     | NULL             |
      | physicalAddress_address             | Via@ok_AR        |
      | physicalAddress_municipalityDetails | FRAZIONE         |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PREPARE_ANALOG_DOMICILE" e apiKey aggiornata con position 0
    #TODO: lo step seguente fallisce a causa di una discrepanza tra i details b2b e quelli webhook (municipalityDetails B2b = "", municipalityDetails Webhook = null)
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_DOMICILE" e apiKey aggiornata con position 0
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" e apiKey aggiornata con position 0
    #TODO: lo step seguente fallisce a causa di una discrepanza tra i details b2b e quelli webhook (municipalityDetails B2b = "", municipalityDetails Webhook = null)
    And verifica corrispondenza tra i detail del webhook e quelli della timeline
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @streamV23 @streamV27 @streamV28 @streamV29 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES3.1_146_2]  Lettura e verifica de-anonimizzazione con un apiKey con gruppo degli eventi di timeline di una notifica analogica inviata con un apikey con gruppo e salvati in uno stream dell'ente con gruppo (Stesso gruppo)
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Mario Gherkin    |
      | taxId                   | CLMCST42R12D969Z |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_AR        |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con un gruppo disponibile "FIRST"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PREPARE_ANALOG_DOMICILE" e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline senza delega analogico
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_DOMICILE" e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline senza delega analogico
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" e apiKey aggiornata con position 0
    And verifica deanonimizzazione degli eventi di timeline senza delega analogico
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @streamV23 @streamV27 @streamV28 @streamV29 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_ES1.2_123] Creazione di stream con apiKey con gruppi differenti e verifica corretta scrittura degli eventi di notifiche create con le stesse apiKey.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And si predispone 1 nuovo stream denominato "stream-test1" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "REQUEST_ACCEPTED" e apiKey aggiornata con position 1
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

 #--------------LETTURA NUOVO EVENTO DI TIMELINE DI UNO STREAM------------DA ATTIVARE

  #SERVE INTEGRAZIONE CON RADD
  @streamV23 @streamV27 @streamV28 @streamV29 @precondition @cleanWebhook @ignore
  Scenario: [B2B-STREAM_ES1.4_136] Lettura degli eventi di timeline  dello stream senza gruppo con visualizzazione del nuovo evento di timeline utilizzando un apikey abilitata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_1"
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And Il cittadino Mario Cucumber come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "NOTIFICATION_RADD_RETRIEVED" e apiKey aggiornata con position 0
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

 #SERVE INTEGRAZIONE CON RADD
  @streamV23 @streamV27 @streamV28 @streamV29 @precondition @cleanWebhook @ignore
  Scenario: [B2B-STREAM_ES1.4_137] Lettura degli eventi di timeline  dello stream con gruppo con visualizzazione  del nuovo evento di timeline utilizzando un apikey abilitata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And Viene creata una nuova apiKey per il comune "Comune_2" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2" con un gruppo disponibile "FIRST"
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    And Il cittadino Mario Cucumber come destinatario 0 mostra il QRCode "corretto"
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
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "NOTIFICATION_RADD_RETRIEVED" e apiKey aggiornata con position 0
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
