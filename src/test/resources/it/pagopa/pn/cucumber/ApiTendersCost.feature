Feature: Adeguamento gestione costi analogici

  Scenario: [Scenario-5] Recupero dei costi associati ad uno specifico CAP con TenderId errato
    Given viene creata una richiesta con valori di default
    When viene chiamata l'api di calcolo costi con tenderId "56ed074c-13b6-4d61-ba49-221953e6b60f"
    Then l'api ritorna status code 404
