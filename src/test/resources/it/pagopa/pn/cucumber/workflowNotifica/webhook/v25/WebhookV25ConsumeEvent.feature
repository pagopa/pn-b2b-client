Feature: avanzamento notifiche webhook b2b V25

  #https://pagopa.atlassian.net/browse/PN-18519
  @webhookV25 @precondition @cleanWebhook @webhook4
  Scenario: [B2B_STREAM_V25_STATUS_NOT_PRESENT] Si creano delle notifiche che hanno uno status che non è presente nella versione V25 e si verifica che lo stream possa essere consumato correttamente senza problemi
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V25"
    And Viene creata una nuova apiKey per il comune "Comune_Son" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Son" con versione "V25" e filtro status "DELIVERING"
    Given vengono create 49 notifiche con destinatario Mario Gherkin per la pa "Comune_Son" e si aspetta che raggiungano l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
      | subject                 | invio notifica con cucumber |
      | senderDenomination      | Comune di Palermo           |
      | physicalCommunication   | REGISTERED_LETTER_890       |
      | physicalAddress_address | @FAIL_DECEDUTO_890          |
      | digitalDomicile         | NULL                        |
    And vengono letti gli eventi dello stream che contenga 49 eventi con la versione "V25"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @webhookV25 @webhookHeader @precondition @cleanWebhook @webhook2
  Scenario Outline: [B2B-STREAM_RETRY_AFTER_V25] Creazione di stream con apiKey e controllo che il retry after dell'header venga modificato quando la consume restituisce elementi.
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Mario Gherkin    |
      | taxId                   | CLMCST42R12D969Z |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_AR        |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V25"
    And Viene creata una nuova apiKey per il comune "<paName>" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V25" per il "<paName>" con un gruppo disponibile "FIRST"
    When si effettua la consume dello stream versione "V25" salvando l'intera response
    Then l'header della response della consume con versione "V25" contiene il parametro "retry-after" con valore pari a "<retryAfterValue>"
    Given la notifica viene inviata tramite api b2b dal "<paName>" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    When si effettua la consume dello stream versione "V25" salvando l'intera response
    Then l'header della response della consume con versione "V25" contiene il parametro "retry-after" con valore pari a "0"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    Examples:
      | paName       | retryAfterValue |
      | Comune_Multi | 60000           |
      | Comune_1     | 70000           |
