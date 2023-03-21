Feature: Attributi utente

  Scenario: [B2B-PF-TOS_1] Viene recuperato il consenso TOS e verificato che sia accepted TOS_scenario positivo
    Given Viene richiesto l'ultimo consenso di tipo "TOS"
    Then Il recupero del consenso non ha prodotto errori
    And Il consenso è accettato

  @testLite
  Scenario: [USER-ATTR_1] inserimento pec errato
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento della pec "test@test@fail.@"
    Then l'inserimento ha prodotto un errore con status code "400"

  @testLite
  Scenario: [USER-ATTR_1] inserimento telefono errato
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento del numero di telefono "+0013894516888"
    Then l'inserimento ha prodotto un errore con status code "400"