Feature: verifica validazione asincrona

  @dev
  Scenario: [B2B-PA-ASYNC_VALIDATION_1] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage  scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Mario Cucumber
    Then la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi REFUSED
