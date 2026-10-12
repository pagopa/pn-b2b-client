Feature: verifica creazione stream

  #https://pagopa.atlassian.net/browse/PN-19952
  # Test che verifica che se viene creato uno stream con waitForAccepted a true e filtro status che non contiene DEFAULT o REQUEST_ACCEPTED allora lo stream non viene creato
  # e viene ritornato un errore 403
  @webhookV29 @precondition @cleanWebhook @webhook2
  Scenario: [B2B-STREAM_PN_19952_1_KO] Creazione di uno stream con waitForAccepted a true e filtro status che non contiene DEFAULT o REQUEST_ACCEPTED.
  Si verifica che la creazione dello stream ritorni un errore 403.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V29"
    And si predispongono 5 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V29"
    And agli stream versione "V29" si setta il campo waitForAccepted introdotto con la versione 29 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Multi" con versione "V29" e filtro status "DELIVERING"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  #https://pagopa.atlassian.net/browse/PN-19952
  # Test che verifica che se viene creato uno stream con waitForAccepted a true e filtro status che contiene DEFAULT o REQUEST_ACCEPTED allora lo stream non viene creato con successo
  @webhookV29 @precondition @cleanWebhook @webhook2
  Scenario Outline: [B2B-STREAM_PN_19952_2_OK] Creazione di uno stream con waitForAccepted a true e filtro status che contiene DEFAULT o REQUEST_ACCEPTED. Si verifica che lo stream venga creato con successo.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V29"
    And si predispongono 5 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V29"
    And agli stream versione "V29" si setta il campo waitForAccepted introdotto con la versione 29 a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Multi" con versione "V29" e filtro status "<statusFilter>"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V29"
    And si cancella lo stream creato per il "Comune_Multi" con versione "V29"
    And viene verificata la corretta cancellazione con versione "V29"
    And viene modificato lo stato dell'apiKey in "BLOCK" per il "Comune_Multi"
    And l'apiKey viene cancellata
    Examples:
      | statusFilter                        |
      | DELIVERING,DEFAULT                  |
      | DELIVERING,REQUEST_ACCEPTED         |
      | DELIVERING,DEFAULT,REQUEST_ACCEPTED |
