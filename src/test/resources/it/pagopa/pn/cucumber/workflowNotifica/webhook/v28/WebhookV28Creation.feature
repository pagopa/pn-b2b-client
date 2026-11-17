Feature: verifica creazione stream

  #https://pagopa.atlassian.net/browse/PN-19952
  # Test che verifica che se viene creato uno stream con waitForAccepted a true e filtro status che non contiene REQUEST_ACCEPTED allora lo stream non viene creato
  # e viene ritornato un errore 403
  @webhookV28 @precondition @cleanWebhook @webhook3
  Scenario: [B2B-STREAM_PN_19952_1_KO] Creazione di uno stream con waitForAccepted a true e filtro status che non contiene REQUEST_ACCEPTED.
  Si verifica che la creazione dello stream ritorni un errore 403.
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V28"
    And si predispongono 5 nuovi stream denominati "stream-test" con eventType "STATUS" con versione "V28"
    And agli stream versione "V28" si setta il campo waitForAccepted a "true"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    When si crea il nuovo stream per il "Comune_Multi" con versione "V28" e filtro status "DELIVERING"
    Then l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
