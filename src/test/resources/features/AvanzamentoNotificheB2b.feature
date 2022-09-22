Feature: avanzamento notifiche b2b

  Scenario: [B2B-STREAM_STATUS_1] Creazione stream notifica
    Given nuovo stream "stream-test" con eventType "STATUS"
    When viene creato il nuovo stream
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id

  Scenario: [B2B-STREAM_TIMELINE_1] Creazione stream notifica
    Given nuovo stream "stream-test" con eventType "TIMELINE"
    When viene creato il nuovo stream
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id

  Scenario: [B2B-STREAM_TIMELINE_2] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    And nuovo stream "stream-test" con eventType "TIMELINE"
    And viene creato il nuovo stream
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream fino allo stato "DELIVERING"

