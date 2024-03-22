Feature: verifica feature aoo/uo con apikeyManager

  @AOO_UO
  Scenario: [API-KEY_25] generazione senza gruppo e mancata presenza nel ente padre
    Given Viene creata una nuova apiKey per il comune "Comune_Root" senza gruppo
    And viene impostata l'apikey appena generata
    Then Si cambia al comune "Comune_Son"
    And vengono lette le apiKey esistenti
    And l'apiKey non è più presente
    Then Si cambia al comune "Comune_Root"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @AOO_UO
  Scenario: [API-KEY_26] generazione senza gruppo e mancata presenza nel ente figlio
    Given Viene creata una nuova apiKey per il comune "Comune_Son" senza gruppo
    And viene impostata l'apikey appena generata
    Then Si cambia al comune "Comune_Root"
    And vengono lette le apiKey esistenti
    And l'apiKey non è più presente
    Then Si cambia al comune "Comune_Son"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata
