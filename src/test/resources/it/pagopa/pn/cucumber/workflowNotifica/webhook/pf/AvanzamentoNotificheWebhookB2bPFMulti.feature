Feature: avanzamento notifiche webhook b2b multi

  Background:
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi"

  @cleanC3 @webhook2
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

  @cleanC3 @dev @webhook2
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

  @cleanC3 @dev @webhook2
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

  @cleanC3 @dev @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_4] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
    | subject | invio notifica con cucumber |
    | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
    | digitalDomicile_address | test@fail.it |
    And destinatario
      | taxId        | LVLDAA85T50G702B|
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "DIGITAL_FAILURE_WORKFLOW"

  @cleanC3 @webhook2
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

  @cleanC3 @webhook2
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

  @cleanC3 @webhook2
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


  @cleanC3 @dev @webhook2
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

  @cleanC3 @dev @webhook2
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

  @cleanC3 @dev @ignore @tbc @webhook2
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

  @cleanC3 @webhook2
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

  @cleanC3 @webhook2
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


  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_6] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_DIGITAL_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_DOMICILE"

  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_7] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_ANALOG_PROGRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_PROGRESS"

  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_8] Invio notifica digitale multi PG ed attesa elemento di timeline SEND_ANALOG_FEEDBACK positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_FEEDBACK"

  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_9] Invio notifica digitale ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_PROGRESS
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"

  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_10] Invio notifica digitale ed attesa elemento di timeline PAYMENT
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PAYMENT"


  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_11] Invio notifica digitale lettura evento SEND_SIMPLE_REGISTERED_LETTER_PROGRESS nel webhook con controllo data
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"
    And Si verifica che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" dello stream di "Comune_Multi" non abbia il timestamp uguale a quella della notifica

  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_12] Invio notifica digitale lettura evento PAYMENT nel webhook con controllo data
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "PAYMENT"
    And Si verifica che l'elemento di timeline "PAYMENT" dello stream di "Comune_Multi" non abbia il timestamp uguale a quella della notifica

  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_13]Invio notifica digitale lettura evento SEND_ANALOG_PROGRESS nel webhook con controllo data
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_PROGRESS"
    And Si verifica che l'elemento di timeline "SEND_ANALOG_PROGRESS" dello stream di "Comune_Multi" non abbia il timestamp uguale a quella della notifica


  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_14] Invio notifica digitale lettura evento SEND_ANALOG_FEEDBACK nel webhook con controllo data
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_address      | Via@ok_890     |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_FEEDBACK"
    And Si verifica che l'elemento di timeline "SEND_ANALOG_FEEDBACK" dello stream di "Comune_Multi" non abbia il timestamp uguale a quella della notifica

  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_15] Invio notifica digitale lettura evento SEND_DIGITAL_PROGRESS nel webhook con controllo data
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_DIGITAL_PROGRESS"
    And Si verifica che l'elemento di timeline "SEND_DIGITAL_PROGRESS" dello stream di "Comune_Multi" non abbia il timestamp uguale a quella della notifica


  @cleanC3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_16] Invio notifica digitale lettura evento SEND_DIGITAL_FEEDBACK nel webhook con controllo data
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK"
    And Si verifica che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" dello stream di "Comune_Multi" non abbia il timestamp uguale a quella della notifica


  @clean3 @webhook2
  Scenario: [B2B-STREAM-TIMELINE_MULTI_PF_17] Invio notifica digitale lettura evento NOTIFICATION_VIEWED nel webhook con controllo data
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_Multi"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "DELIVERING"
    And "Mario Gherkin" legge la notifica
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "NOTIFICATION_VIEWED"
    And Si verifica che l'elemento di timeline "NOTIFICATION_VIEWED" dello stream di "Comune_Multi" non abbia il timestamp uguale a quella della notifica

