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

  Scenario: [API-KEY_10] generazione con gruppo e cancellazione ApiKey_scenario positivo
    Given Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And vengono lette le apiKey esistenti
    And l'apiKey creata è presente tra quelle lette
    When viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    And vengono lette le apiKey esistenti
    Then l'apiKey non è più presente

  Scenario: [API-KEY_11] generazione senza gruppo e invio notifica senza gruppo ApiKey_scenario positivo
    Given Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | group              | NULL             |
    And viene settato il taxId della notifica con quello dell'apikey
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b
    Then l'invio della notifica non ha prodotto errori
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  Scenario: [API-KEY_12] generazione senza gruppo e invio notifica con gruppo ApiKey_scenario positivo
    Given Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | group              | NULL             |
    And viene settato il taxId della notifica con quello dell'apikey
    And viene settato il primo gruppo valido per il comune "Comune_1"
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b
    Then l'invio della notifica non ha prodotto errori
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  Scenario: [API-KEY_13] generazione con gruppo e invio notifica senza gruppo ApiKey_scenario negativo
    Given Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | group              | NULL             |
    And viene settato il taxId della notifica con quello dell'apikey
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b
    Then l'invio della notifica ha sollevato un errore "400"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  Scenario: [API-KEY_14] generazione con gruppo e invio notifica con lo stesso gruppo ApiKey_scenario positivo
    Given Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And viene settato il gruppo della notifica con quello dell'apikey
    And viene settato il taxId della notifica con quello dell'apikey
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b
    Then l'invio della notifica non ha prodotto errori
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  Scenario: [API-KEY_15] generazione con gruppo e invio notifica con un gruppo differente ApiKey_scenario negativo
    Given Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And viene settato il taxId della notifica con quello dell'apikey
    And viene settato un gruppo differente da quello utilizzato nell'apikey per il comune "Comune_1"
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b
    Then l'invio della notifica ha sollevato un errore "400"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata


  Scenario: [API-KEY_16] generazione con gruppo e invio notifica con gruppo e lettura notifica senza gruppo ApiKey_scenario positivo
    Given Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And viene settato il gruppo della notifica con quello dell'apikey
    And viene settato il taxId della notifica con quello dell'apikey
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @ignore
  Scenario: [API-KEY_17] generazione con gruppo e invio notifica con gruppo  e lettura notifica gruppo diverso ApiKey_scenario negativo
    Given Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And viene settato il gruppo della notifica con quello dell'apikey
    And viene settato il taxId della notifica con quello dell'apikey
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_1" con gruppo differente del invio notifica
    And viene impostata l'apikey appena generata
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  Scenario: [API-KEY_18] generazione senza gruppo e invio notifica senza gruppo  e lettura notifica senza gruppo  ApiKey_scenario positivo
    Given Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | group              | NULL             |
    And viene settato il taxId della notifica con quello dell'apikey
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    
  Scenario: [API-KEY_19] generazione con gruppo e invio notifica con gruppo e lettura notifica con gruppo ApiKey_scenario positivo
    Given Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And viene settato il gruppo della notifica con quello dell'apikey
    And viene settato il taxId della notifica con quello dell'apikey
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_1" con gruppo uguale del invio notifica
    And viene impostata l'apikey appena generata
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata



  @ignore
  Scenario: [API-KEY_20] generazione senza gruppo e invio notifica con lettura notifica con apikey con gruppo ApiKey_scenario negativo
    Given Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | group              | NULL             |
    And viene settato il taxId della notifica con quello dell'apikey
    And viene settato il primo gruppo valido per il comune "Comune_1"
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    When Viene creata una nuova apiKey per il comune "Comune_1" con il primo gruppo disponibile
    And viene impostata l'apikey appena generata
    Then la notifica viene recuperata dal sistema tramite codice IUN
    And l'operazione ha prodotto un errore con status code "403"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
    #Verificare corretto comportamento

  @ignore
  Scenario: [API-KEY_100] generazione con gruppo non valido ApiKey_scenario negativo
    Given Viene generata una nuova apiKey con il gruppo "AAAAAAAAAA"
    Then l'operazione ha sollevato un errore con status code "400"



