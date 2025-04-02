Feature: avanzamento notifiche webhook b2b V25

  #COMUNE 1
  @webhookV25 @precondition @cleanWebhook @webhook1
  Scenario: [B2B-STREAM_ES1.1_112] Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato) con controllo EventId incrementale e senza duplicati.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination | Galileo galileo  |
      | taxId        | GLLGLL64B15G702I |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V25"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V25" per il "Comune_1" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V25"
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    #TEST LETTURA REQUEST_ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione "V25"
    #TEST LETTURA DIGITAL_SUCCESS_WORKFLOW
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" con la versione "V25"
    And viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati "V25"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV25 @precondition @cleanWebhook @webhook1
  Scenario: [B2B-STREAM_ES1.3_127] Consumo di uno stream notifica con gruppo, con eventType "STATUS"  utilizzando un apikey con stesso gruppo.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V25"
    And Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V25" per il "Comune_1" con un gruppo disponibile "FIRST"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione "V25"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV25 @precondition @webhook1
  Scenario: [B2B-STREAM_ES1.1_158] Consumo di uno stream notifica con gruppi appartenenti ad un sottinsieme dei gruppi dell'apikey utilizzata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V25"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" con due gruppi
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V25" per il "Comune_Multi" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V25"
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione "V25"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

    #COMUNE 2

  @webhookV25 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.3_128] Consumo di uno stream notifica con gruppo, con eventType "TIMELINE"  utilizzando un apikey master.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verona            |
    And destinatario
      | denomination | Galileo galileo  |
      | taxId        | GLLGLL64B15G702I |
      | payment      | NULL             |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V25"
    And Viene creata una nuova apiKey per il comune "Comune_2" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V25" per il "Comune_2" con un gruppo disponibile "FIRST"
    And la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV25 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.3_125_1] Consumo di uno stream notifica disabilitato senza gruppo, con eventType "STATUS"  utilizzando un apikey master (caso errato).
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verono            |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V25"
    And Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2" con versione "V25"
    And si disabilita lo stream "V25" creato per il comune "Comune_2"
    And l'operazione non ha prodotto errori
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    Then si verifica che non siano presenti eventi nello stream con versione "V25" del "Comune_2"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  @webhookV25 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_ES1.2_124] Verifica corretta scrittura degli eventi di una notifica creata con un apikey master, dove l’evento stesso deve essere salvato solo negli stream senza gruppi.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Verona            |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V25"
    And Viene creata una nuova apiKey per il comune "Comune_2" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V25" per il "Comune_2" con un gruppo disponibile "FIRST"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V25"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And si predispone 1 nuovo stream denominato "stream-test1" con eventType "TIMELINE" con versione "V25"
    And Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_2" con versione "V25"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V25"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione "V25"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
