Feature: avanzamento notifiche webhook b2b

  #Background:
   # Given vengono cancellati tutti gli stream presenti del "Comune_1" con versione "V10"
  Scenario: [B2B-STREAM_STATUS_7777] Creazione stream notifica
    Given vengono cancellati tutti gli stream presenti del "Comune_1" con versione "V10" - ONLY FOR DEBUG


  @oldWebhookTest
  Scenario: [B2B-STREAM_STATUS_1] Creazione stream notifica
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS" con versione "V10"
    When si crea il nuovo stream per il "Comune_1" con versione "V10"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
    And si cancella lo stream creato per il "Comune_1" con versione "V10"
    And viene verificata la corretta cancellazione con versione "V10"


  @oldWebhookTest
  Scenario: [B2B-STREAM_TIMELINE_1] Creazione stream notifica
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    When si crea il nuovo stream per il "Comune_1" con versione "V10"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione "V10"
    And si cancella lo stream creato per il "Comune_1" con versione "V10"
    And viene verificata la corretta cancellazione con versione "V10"

  @webhook1
  Scenario: [B2B-STREAM_TIMELINE_2] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "ACCEPTED"

  @clean @webhook1
  Scenario: [B2B-STREAM_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED"


  @oldWebhookTest
  Scenario: [B2B-STREAM_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "AAR_GENERATION"

  @clean @webhook1
  Scenario: [B2B-STREAM_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "GET_ADDRESS"


  @oldWebhookTest
  Scenario: [B2B-STREAM_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERING"


  @oldWebhookTest
  Scenario: [B2B-STREAM_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_DOMICILE"


  @oldWebhookTest
  Scenario: [B2B-STREAM_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERING"
    And "Mario Gherkin" legge la notifica
    Then si verifica nello stream del "Comune_1" che la notifica abbia lo stato VIEWED

  @clean @testLite @webhook1
  Scenario: [B2B-STREAM_TIMELINE_10] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERING"
    And "Mario Gherkin" legge la notifica
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "NOTIFICATION_VIEWED"


  @oldWebhookTest
  Scenario: [B2B-STREAM_TIMELINE_11] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERED"

  @clean @webhook1
  Scenario: [B2B-STREAM_TIMELINE_12] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERED"
    And "Mario Gherkin" legge la notifica
    Then si verifica nello stream del "Comune_1" che la notifica abbia lo stato VIEWED

  @clean @webhook1
  Scenario: [B2B-STREAM_TIMELINE_13] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERED"
    And "Mario Gherkin" legge la notifica
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "NOTIFICATION_VIEWED"

  @clean @webhook1
  Scenario: [B2B-STREAM_TIMELINE_14] Creazione multi stream notifica
    Given si predispongono 6 nuovi stream denominati "stream-test" con eventType "STATUS" con versione "V10"
    When si creano i nuovi stream per il "Comune_1" con versione "V10"
    Then l'ultima creazione ha prodotto un errore con status code "409"

  @clean @webhook1
  Scenario: [B2B-STREAM_TIMELINE_15] Creazione multi stream notifica
    Given si predispongono 6 nuovi stream denominati "stream-test" con eventType "TIMELINE" con versione "V10"
    When si creano i nuovi stream per il "Comune_1" con versione "V10"
    Then l'ultima creazione ha prodotto un errore con status code "409"

  @clean @dev @webhook1
  Scenario: [B2B-STREAM_TIMELINE_16] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "DIGITAL_FAILURE_WORKFLOW"

  @clean @dev @ignore @webhook1
  Scenario: [B2B-STREAM_TIMELINE_17] Invio notifica digitale ed attesa elemento di timeline NOT_HANDLED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "NOT_HANDLED"

  @clean @dev @webhook1
  Scenario: [B2B-STREAM_TIMELINE_19] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK"

  @clean @dev @webhook1
  Scenario: [B2B-STREAM_TIMELINE_20] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_PROGRESS"

  @clean @dev @webhook1
  Scenario: [B2B-STREAM_TIMELINE_21] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "PUBLIC_REGISTRY_CALL"

  @clean @dev @webhook1
  Scenario: [B2B-STREAM_TIMELINE_22] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "PUBLIC_REGISTRY_RESPONSE"

  @clean @dev @webhook1
  Scenario: [B2B-STREAM_TIMELINE_23] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_1" e si attende che lo stato diventi REFUSED
    And si verifica che la notifica non viene accettata causa "ALLEGATO"
    Then vengono letti gli eventi dello stream del "Comune_1" con la verifica di Allegato non trovato

  @clean @webhook1
  Scenario: [B2B-STREAM_TIMELINE_24_7878] Invio notifiche digitali e controllo che vengano letti 50 eventi nel webhook
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano                 |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And vengono letti gli eventi dello stream che contenga 50 eventi


  @clean @dev @webhook1
  Scenario: [B2B-STREAM_TIMELINE_25] Invio notifica digitale ed attesa elemento di timeline PAYMENT
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24        | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And l'avviso pagopa viene pagato correttamente
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "PAYMENT"


  @oldWebhookTest
  Scenario: [B2B-STREAM_TIMELINE_26] Invio notifica digitale ed attesa elemento di timeline REFINEMENT e verifica corretteza data PN-9059
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And l'avviso pagopa viene pagato correttamente
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REFINEMENT"
    And Si verifica che l'elemento di timeline REFINEMENT abbia il timestamp uguale a quella presente nel webhook
