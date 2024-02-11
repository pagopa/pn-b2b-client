Feature: avanzamento notifiche webhook b2b per persona giuridica

  Background:
    Given vengono cancellati tutti gli stream presenti del "Comune_2" con versione "V22"

  @cleanC2 @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_1] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino allo stato "ACCEPTED" con la versione V22

  @cleanC2 @testLite @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "REQUEST_ACCEPTED"

  @cleanC2 @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "AAR_GENERATION" con la versione V22

  @cleanC2  @testLite @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "GET_ADDRESS" con la versione V22

  @cleanC2 @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino allo stato "DELIVERING" con la versione V22

  @cleanC2 @testLite @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "SEND_DIGITAL_DOMICILE" con la versione V22

  @cleanC2 @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino allo stato "DELIVERED" con la versione V22

  @cleanC2 @dev @ignore @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_8] Invio notifica digitale ed attesa elemento di timeline PREPARE_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "PREPARE_SIMPLE_REGISTERED_LETTER" con la versione V22

  @cleanC2 @dev @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_9] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK" con la versione V22

  @cleanC2 @dev @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_10] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "SEND_DIGITAL_PROGRESS" con la versione V22

  @cleanC2 @dev @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_11] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "PUBLIC_REGISTRY_CALL" con la versione V22

  @cleanC2 @dev @ignore @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_12] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_2" fino all'elemento di timeline "PUBLIC_REGISTRY_RESPONSE" con la versione V22

  @cleanC2 @dev @webhook3
  Scenario: [B2B-STREAM_V22_TIMELINE_PG_13] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V22"
    And si crea il nuovo stream per il "Comune_2" con versione "V22"
    When la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_2" e si attende che lo stato diventi REFUSED
    And si verifica che la notifica non viene accettata causa "ALLEGATO"
    Then vengono letti gli eventi dello stream del "Comune_2" con la verifica di Allegato non trovato con la versione V22