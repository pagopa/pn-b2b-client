Feature: apiKey manager

  Scenario: [API-KEY_1] Lettura apiKey generate_scenario positivo
    Given vengono lette le apiKey esistenti
    Then la lettura è avvenuta correttamente


  Scenario: [API-KEY_2] generazione e cancellazione ApiKey_scenario positivo
    Given Viene creata una nuova apiKey
    And vengono lette le apiKey esistenti
    And l'apiKey creata è presente tra quelle lette
    When viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And vengono lette le apiKey esistenti
    Then l'apiKey non è più presente


  Scenario: [API-KEY_3] generazione e cancellazione ApiKey_scenario negativo
    Given Viene creata una nuova apiKey
    And vengono lette le apiKey esistenti
    And l'apiKey creata è presente tra quelle lette
    And si tenta la cancellazione dell'apiKey
    Then l'operazione ha sollevato un errore con status code "409"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  Scenario: [API-KEY_4] generazione e verifica stato ApiKey_scenario positivo
    Given Viene creata una nuova apiKey
    And vengono lette le apiKey esistenti
    And l'apiKey creata è presente tra quelle lette
    When viene modificato lo stato dell'apiKey in "BLOCK"
    And vengono lette le apiKey esistenti
    Then si verifica lo stato dell'apikey "BLOCKED"
    And l'apiKey viene cancellata


  Scenario: [API-KEY_5] generazione e verifica stato ApiKey_scenario positivo
    Given Viene creata una nuova apiKey
    And vengono lette le apiKey esistenti
    And l'apiKey creata è presente tra quelle lette
    Then si verifica lo stato dell'apikey "ENABLED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  Scenario: [API-KEY_6] generazione e verifica stato ApiKey_scenario positivo
    Given Viene creata una nuova apiKey
    And vengono lette le apiKey esistenti
    And l'apiKey creata è presente tra quelle lette
    When viene modificato lo stato dell'apiKey in "ROTATE"
    And vengono lette le apiKey esistenti
    Then si verifica lo stato dell'apikey "ROTATED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  Scenario: [API-KEY_7] generazione e verifica stato ApiKey_scenario positivo
    Given Viene creata una nuova apiKey
    And vengono lette le apiKey esistenti
    And l'apiKey creata è presente tra quelle lette
    Then si verifica lo stato dell'apikey "ENABLED"
    When viene modificato lo stato dell'apiKey in "BLOCK"
    And vengono lette le apiKey esistenti
    Then si verifica lo stato dell'apikey "BLOCKED"
    Then viene modificato lo stato dell'apiKey in "ENABLE"
    And vengono lette le apiKey esistenti
    Then si verifica lo stato dell'apikey "ENABLED"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  Scenario: [API-KEY_8] generazione e test ApiKey_scenario positivo
    Given Viene creata una nuova apiKey
    And vengono lette le apiKey esistenti
    And l'apiKey creata è presente tra quelle lette
    And si verifica lo stato dell'apikey "ENABLED"
    When viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata dal "Comune_1"
    Then l'invio della notifica non ha prodotto errori
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  Scenario: [API-KEY_9] generazione e test ApiKey_scenario positivo
    Given Viene creata una nuova apiKey
    And vengono lette le apiKey esistenti
    And l'apiKey creata è presente tra quelle lette
    And viene modificato lo stato dell'apiKey in "BLOCK"
    When viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata dal "Comune_1"
    Then l'invio della notifica ha sollevato un errore di autenticazione "403"
    And l'apiKey viene cancellata

