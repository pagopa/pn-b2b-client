Feature: avanzamento notifiche webhook b2b

  Scenario: [B2B-STREAM_STATUS_1] Creazione stream notifica
    Given nuovo stream "stream-test" con eventType "STATUS"
    When viene creato il nuovo stream
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And viene cancellato lo stream creato
    And viene verificata al corretta cancellazione

  Scenario: [B2B-STREAM_TIMELINE_1] Creazione stream notifica
    Given nuovo stream "stream-test" con eventType "TIMELINE"
    When viene creato il nuovo stream
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And viene cancellato lo stream creato
    And viene verificata al corretta cancellazione

  @clean
  Scenario: [B2B-STREAM_TIMELINE_2] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
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
    Then vengono letti gli eventi dello stream fino allo stato "ACCEPTED"


  @clean
  Scenario: [B2B-STREAM_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
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
    Then vengono letti gli eventi dello stream fino all'elemento di timeline "REQUEST_ACCEPTED"


  #@clean
  #Scenario: [B2B-STREAM_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
   # Given viene generata una nuova notifica
    #  | subject | invio notifica con cucumber |
     # | senderDenomination | Comune di milano |
    #And destinatario della notifica
    #  | denomination | Cristoforo Colombo |
    #  | taxId | CLMCST42R12D969Z |
    #  | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    #And nuovo stream "stream-test" con eventType "TIMELINE"
    #And viene creato il nuovo stream
    #When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    #Then vengono letti gli eventi dello stream fino all'elemento di timeline "AAR_GENERATION"


  #@svil @clean
  #Scenario: [B2B-STREAM_TIMELINE_5] Invio notifica digitale ed attesa elemento di timeline SEND_COURTESY_MESSAGE positivo
   # Given viene generata una nuova notifica
    #  | subject | invio notifica con cucumber |
     # | senderDenomination | Comune di milano |
   # And destinatario della notifica
    #  | denomination | Cristoforo Colombo |
     # | taxId | CLMCST42R12D969Z |
     # | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    #And nuovo stream "stream-test" con eventType "TIMELINE"
    #And viene creato il nuovo stream
    #When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    #Then vengono letti gli eventi dello stream fino all'elemento di timeline "SEND_COURTESY_MESSAGE"



  @clean @svil
  Scenario: [B2B-STREAM_TIMELINE_6_SVIL] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
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
    Then vengono letti gli eventi dello stream fino all'elemento di timeline "GET_ADDRESS"


  @clean
  Scenario: [B2B-STREAM_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
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


  @clean
  Scenario: [B2B-STREAM_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
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
    Then vengono letti gli eventi dello stream fino all'elemento di timeline "SEND_DIGITAL_DOMICILE"


  @clean
  Scenario: [B2B-STREAM_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo
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
    And vengono letti gli eventi dello stream fino allo stato "DELIVERING"
    And il destinatario legge la notifica
    Then si verifica nello stream che la notifica abbia lo stato VIEWED


  @clean
  Scenario: [B2B-STREAM_TIMELINE_10] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo
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
    And vengono letti gli eventi dello stream fino allo stato "DELIVERING"
    And il destinatario legge la notifica
    Then vengono letti gli eventi dello stream fino all'elemento di timeline "NOTIFICATION_VIEWED"


  @svil @clean
  Scenario: [B2B-STREAM_TIMELINE_11_SVIL] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
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
    Then vengono letti gli eventi dello stream fino allo stato "DELIVERED"



  @svil @clean
  Scenario: [B2B-STREAM_TIMELINE_12_SVIL] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo
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
    Then vengono letti gli eventi dello stream fino allo stato "DELIVERED"
    And il destinatario legge la notifica
    Then si verifica nello stream che la notifica abbia lo stato VIEWED



  @svil @clean
  Scenario: [B2B-STREAM_TIMELINE_13_SVIL] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo
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
    Then vengono letti gli eventi dello stream fino allo stato "DELIVERED"
    And il destinatario legge la notifica
    Then vengono letti gli eventi dello stream fino all'elemento di timeline "NOTIFICATION_VIEWED"


  #@svil @clean
  #Scenario: [B2B-STREAM_TIMELINE_14_SVIL] Invio notifica digitale ed attesa stato CANCELLED_scenario negativo
    #Given viene generata una nuova notifica
     # | subject | invio notifica con cucumber |
     # | senderDenomination | Comune di milano |
   # And destinatario della notifica
    #  | denomination | Cristoforo Colombo |
     # | taxId | CLMCST42R12D969Z |
    #  | digitalDomicile_address | errore-email@test.test |
  #  And nuovo stream "stream-test" con eventType "TIMELINE"
   # And viene creato il nuovo stream
   # When la notifica viene inviata
   # Then vengono letti gli eventi dello stream fino allo stato "CANCELLED"
