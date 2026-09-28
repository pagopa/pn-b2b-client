Feature: avanzamento notifiche webhook b2b V24

  #COMUNE 1
  @webhookV24 @precondition @webhook1
  Scenario: [B2B-STREAM_ES1.1_112] Creazione con replaceID di uno stream notifica senza gruppo uguale al precedente stream con eventType "TIMELINE" utilizzando un apikey master. (replacedStreamId settato) con controllo EventId incrementale e senza duplicati.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination | Galileo galileo  |
      | taxId        | GLLGLL64B15G702I |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V24"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V24" per il "Comune_1" con un gruppo disponibile "NO_GROUPS"
    And lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V24"
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    #TEST LETTURA REQUEST_ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED" con la versione "V24"
    #TEST LETTURA DIGITAL_SUCCESS_WORKFLOW
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "DIGITAL_SUCCESS_WORKFLOW" con la versione "V24"
    And viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati "V24"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  #https://pagopa.atlassian.net/browse/PN-18519
  @webhookV24 @precondition @cleanWebhook @webhook4
  Scenario: [B2B_STREAM_V24_STATUS_NOT_PRESENT] Si creano delle notifiche che hanno uno status che non è presente nella versione V24 e si verifica che lo stream possa essere consumato correttamente senza problemi
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V24"
    And Viene creata una nuova apiKey per il comune "Comune_Son" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Son" con versione "V24" e filtro status "DELIVERING"
    Given vengono create 49 notifiche con destinatario Mario Gherkin per la pa "Comune_Son" e si aspetta che raggiungano l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
      | subject                 | invio notifica con cucumber |
      | senderDenomination      | Comune di Palermo           |
      | physicalCommunication   | REGISTERED_LETTER_890       |
      | physicalAddress_address | @FAIL_DECEDUTO_890          |
      | digitalDomicile         | NULL                        |
    And vengono letti gli eventi dello stream che contenga 49 eventi con la versione "V24"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV24 @webhookHeader @precondition @cleanWebhook @webhook2
  Scenario Outline: [B2B-STREAM_RETRY_AFTER_V24] Creazione di stream con apiKey e controllo che il retry after dell'header venga modificato quando la consume restituisce elementi.
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Mario Gherkin    |
      | taxId                   | CLMCST42R12D969Z |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_AR        |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V24"
    And Viene creata una nuova apiKey per il comune "<paName>" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V24" per il "<paName>" con un gruppo disponibile "FIRST"
    When si effettua la consume dello stream versione "V24" salvando l'intera response
    Then l'header della response della consume con versione "V24" contiene il parametro "retry-after" con valore pari a "<retryAfterValue>"
    Given la notifica viene inviata tramite api b2b dal "<paName>" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    When si effettua la consume dello stream versione "V24" salvando l'intera response
    Then l'header della response della consume con versione "V24" contiene il parametro "retry-after" con valore pari a "0"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    Examples:
      | paName       | retryAfterValue |
      | Comune_Multi | 60000           |
      | Comune_1     | 70000           |
