Feature: avanzamento notifiche webhook b2b

  Background:
    Given vengono cancellati tutti gli stream presenti del "Comune_1"

  Scenario: [B2B-STREAM_STATUS_1] Creazione stream notifica
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS"
    When si crea il nuovo stream per il "Comune_1"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And si cancella lo stream creato
    And viene verificata la corretta cancellazione

  Scenario: [B2B-STREAM_TIMELINE_1] Creazione stream notifica
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    When si crea il nuovo stream per il "Comune_1"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And si cancella lo stream creato
    And viene verificata la corretta cancellazione

  @clean
  Scenario: [B2B-STREAM_TIMELINE_2] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "ACCEPTED"

  @clean
  Scenario: [B2B-STREAM_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED"


  @clean
  Scenario: [B2B-STREAM_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "AAR_GENERATION"


  @clean
  Scenario: [B2B-STREAM_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "GET_ADDRESS"

  @clean
  Scenario: [B2B-STREAM_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERING"

  @clean
  Scenario: [B2B-STREAM_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_DOMICILE"

  @clean
  Scenario: [B2B-STREAM_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERING"
    And "Mario Gherkin" legge la notifica
    Then si verifica nello stream del "Comune_1" che la notifica abbia lo stato VIEWED

  @clean
  Scenario: [B2B-STREAM_TIMELINE_10] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERING"
    And "Mario Gherkin" legge la notifica
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "NOTIFICATION_VIEWED"

  @clean
  Scenario: [B2B-STREAM_TIMELINE_11] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERED"

  @clean
  Scenario: [B2B-STREAM_TIMELINE_12] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERED"
    And "Mario Gherkin" legge la notifica
    Then si verifica nello stream del "Comune_1" che la notifica abbia lo stato VIEWED

  @clean
  Scenario: [B2B-STREAM_TIMELINE_13] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERED"
    And "Mario Gherkin" legge la notifica
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "NOTIFICATION_VIEWED"

  @clean
  Scenario: [B2B-STREAM_TIMELINE_14] Creazione multi stream notifica
    Given si predispongono 6 nuovi stream denominati "stream-test" con eventType "STATUS"
    When si creano i nuovi stream per il "Comune_1"
    Then l'ultima creazione ha prodotto un errore con status code "409"

  @clean
  Scenario: [B2B-STREAM_TIMELINE_15] Creazione multi stream notifica
    Given si predispongono 6 nuovi stream denominati "stream-test" con eventType "TIMELINE"
    When si creano i nuovi stream per il "Comune_1"
    Then l'ultima creazione ha prodotto un errore con status code "409"

  @clean @dev
  Scenario: [B2B-STREAM_TIMELINE_16] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "DIGITAL_FAILURE_WORKFLOW"

  @clean @dev
  Scenario: [B2B-STREAM_TIMELINE_17] Invio notifica digitale ed attesa elemento di timeline NOT_HANDLED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "NOT_HANDLED"


  @clean @dev
  Scenario: [B2B-STREAM_TIMELINE_19] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK"

  @clean @dev
  Scenario: [B2B-STREAM_TIMELINE_20] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_PROGRESS"


  @clean @dev
  Scenario: [B2B-STREAM_TIMELINE_21] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "PUBLIC_REGISTRY_CALL"

  @clean @dev
  Scenario: [B2B-STREAM_TIMELINE_22] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "PUBLIC_REGISTRY_RESPONSE"
