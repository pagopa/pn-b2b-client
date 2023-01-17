Feature: test di integrazione della pubblica amministrazione

  ###TC-INVIO-01
  @integrationTest @TC-INVIO-01
  Scenario: [TC-PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @integrationTest @TC-INVIO-01
  Scenario: [TC-PA-SEND_2] invio notifiche digitali mono destinatario (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica

  @integrationTest @TC-INVIO-01
  Scenario: [TC-PA-SEND_3] download documento notificato_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | document | SI |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "NOTIFICA"
    Then il download si conclude correttamente

  @integrationTest @TC-INVIO-01
  Scenario: [TC-PA-SEND_4] invio notifiche digitali mono destinatario senza physicalAddress (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber e:
      | physicalAddress | NULL |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  @integrationTest @TC-INVIO-01
  Scenario: [TC-PA-SEND_5] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica

  @integrationTest @TC-INVIO-01
  Scenario: [TC-PA-SEND_6] Invio notifica digitale mono destinatario con pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della richiesta di invio notifica


  @integrationTest @TC-INVIO-01
  Scenario: [TC-PA-SEND_7] Invio notifica digitale mono destinatario senza pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della richiesta di invio notifica


    ###TC-INVIO-02
  @integrationTest @TC-INVIO-02
  Scenario: [TC-STREAM_TIMELINE_0.1] Creazione stream notifica
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "STATUS"
    When si crea il nuovo stream
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And si cancella lo stream creato
    And viene verificata la corretta cancellazione

  @integrationTest @TC-INVIO-02
  Scenario: [TC-STREAM_TIMELINE_0.2] Creazione stream notifica
    Given si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    When si crea il nuovo stream per il "Comune_1"
    Then lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id
    And si cancella lo stream creato
    And viene verificata la corretta cancellazione

  @clean @integrationTest @TC-INVIO-02
  Scenario: [TC-STREAM_TIMELINE_1] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "ACCEPTED"

  @clean @integrationTest @TC-INVIO-02
  Scenario: [TC-STREAM_TIMELINE_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "REQUEST_ACCEPTED"

  @clean @integrationTest @TC-INVIO-02
  Scenario: [TC-STREAM_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "AAR_GENERATION"

  @clean @integrationTest @TC-INVIO-02
  Scenario: [TC-STREAM_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "GET_ADDRESS"

  @clean @integrationTest @TC-INVIO-02
  Scenario: [TC-STREAM_TIMELINE_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERING"

  @clean @integrationTest @TC-INVIO-02
  Scenario: [TC-STREAM_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_DOMICILE"

  @clean @integrationTest @TC-INVIO-02
  Scenario: [TC-STREAM_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE"
    And si crea il nuovo stream per il "Comune_1"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "DELIVERED"


    ###TC-INVIO-03

  @SmokeTest @integrationTest @TC-INVIO-03
  Scenario: [TC_PA_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then la PA richiede il download dell'attestazione opponibile "SENDER_ACK"

  @integrationTest @TC-INVIO-03
  Scenario: [TC_PA_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @integrationTest @TC-INVIO-03
  Scenario: [TC_PA_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then la PA richiede il download dell'attestazione opponibile "PEC_RECEIPT"



    ###TC-PAGAMENTO-01

  @integrationTest @TC-PAGAMENTO-01
  Scenario: [TC-PA-PAY_1] Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "200" della notifica