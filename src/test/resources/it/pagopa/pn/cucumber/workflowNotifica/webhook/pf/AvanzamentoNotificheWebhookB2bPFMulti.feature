Feature: avanzamento notifiche webhook b2b multi

  Background:
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi"

  @cleanC3
  Scenario: [B2B-STREAM-TIMELINE_MULTI_1] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
    | subject | invio notifica con cucumber |
    | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "GET_ADDRESS"

  @cleanC3 @dev
  Scenario: [B2B-STREAM-TIMELINE_MULTI_2] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo
    Given viene generata una nuova notifica
    | subject | invio notifica con cucumber |
    | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
    | digitalDomicile_address | test@fail.it |
    And destinatario Mario Cucumber e:
    | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PUBLIC_REGISTRY_CALL"

  @cleanC3 @dev
  Scenario: [B2B-STREAM-TIMELINE_MULTI_3] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo
    Given viene generata una nuova notifica
    | subject | invio notifica con cucumber |
    | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
    | digitalDomicile_address | test@fail.it |
    And destinatario Mario Cucumber e:
    | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PUBLIC_REGISTRY_RESPONSE"

  @cleanC3 @dev
  Scenario: [B2B-STREAM-TIMELINE_MULTI_4] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
    | subject | invio notifica con cucumber |
    | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
    | digitalDomicile_address | test@fail.it |
    And destinatario Mario Cucumber e:
    | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "DIGITAL_FAILURE_WORKFLOW"

  @cleanC3
  Scenario: [B2B-STREAM-TIMELINE_MULTI_5] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo
    Given viene generata una nuova notifica
    | subject | invio notifica con cucumber |
    | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_DIGITAL_PROGRESS"

  @cleanC3
  Scenario: [B2B-STREAM-TIMELINE_MULTI_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
    | subject | invio notifica con cucumber |
    | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK"

  @cleanC3
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PG_1] Invio notifica digitale multi PG ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "GET_ADDRESS"


  @cleanC3 @dev
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PG_2] Invio notifica digitale multi PG ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    And destinatario Cucumber srl e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PUBLIC_REGISTRY_CALL"

  @cleanC3 @dev
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PG_3] Invio notifica digitale multi PG ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    And destinatario Cucumber srl e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PUBLIC_REGISTRY_RESPONSE"

  @cleanC3 @dev @ignore @tbc
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PG_4] Invio notifica digitale multi PG ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    And destinatario Cucumber srl e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "DIGITAL_FAILURE_WORKFLOW"

  @cleanC3
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PG_5] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_DIGITAL_PROGRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_DIGITAL_PROGRESS"

  @cleanC3
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PG_6] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK"