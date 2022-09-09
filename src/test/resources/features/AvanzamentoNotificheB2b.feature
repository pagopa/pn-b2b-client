Feature: avanzamento notifiche b2b

  Scenario: [B2B-STREAM_1] Creazione stream notifica
    Given nuovo stream "stream-test" con eventType "STATUS"
    When viene creato il nuovo stream
    Then lo stream è stato creato e viene correttamente recuperato dal sistema
