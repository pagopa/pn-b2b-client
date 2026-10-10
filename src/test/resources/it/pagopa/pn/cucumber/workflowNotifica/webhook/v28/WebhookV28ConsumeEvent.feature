Feature: avanzamento notifiche webhook b2b V28

  @webhookV28 @webhookHeader @precondition @cleanWebhook @webhook2
  Scenario Outline: [B2B-STREAM_RETRY_AFTER_V28] Creazione di stream con apiKey e controllo che il retry after dell'header venga modificato quando la consume restituisce elementi.
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Mario Gherkin    |
      | taxId                   | CLMCST42R12D969Z |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_AR        |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V28"
    And Viene creata una nuova apiKey per il comune "<paName>" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V28" per il "<paName>" con un gruppo disponibile "FIRST"
    When si effettua la consume dello stream versione "V28" salvando l'intera response
    Then l'header della response della consume con versione "V28" contiene il parametro "retry-after" con valore pari a "<retryAfterValue>"
    Given la notifica viene inviata tramite api b2b dal "<paName>" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    When si effettua la consume dello stream versione "V28" salvando l'intera response
    Then l'header della response della consume con versione "V28" contiene il parametro "retry-after" con valore pari a "0"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    Examples:
      | paName       | retryAfterValue |
      | Comune_Multi | 60000           |
      | Comune_1     | 70000           |
