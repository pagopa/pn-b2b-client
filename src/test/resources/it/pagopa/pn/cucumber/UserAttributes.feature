Feature: Attributi utente

  Scenario: [B2B-PF-TOS_1] Viene recuperato il consenso TOS e verificato che sia accepted TOS_scenario positivo
    Given Viene richiesto l'ultimo consenso di tipo "TOS"
    Then Il recupero del consenso non ha prodotto errori
    And Il consenso è accettato
