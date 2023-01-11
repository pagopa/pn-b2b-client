Feature: invio notifiche b2b per la persona giuridica

  Scenario: [B2B-PA-SEND_PG_1] Invio notifica digitale mono destinatario persona giuridica lettura tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  Scenario: [B2B-PA-SEND_PG_2] Invio notifiche digitali mono destinatario (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | idempotenceToken | AME2E3626070001.1  |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.2"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica


  Scenario: [B2B-PA-SEND_PG_3] invio notifiche digitali mono destinatario (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | idempotenceToken | AME2E3626070001.1  |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.1"
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "409"


  Scenario: [B2B-PA-SEND_PG_4] invio notifiche digitali mono destinatario (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica


  Scenario: [B2B-PA-SEND_PG_5] invio notifiche digitali mono destinatario (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "409"


  Scenario: [B2B-PA-SEND_PG_6] download documento notificato_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | document | SI |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "NOTIFICA"
    Then il download si conclude correttamente


  Scenario: [B2B-PA-SEND_PG_7] download documento pagopa_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Gherkin spa e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente


  Scenario: [B2B-PA-SEND_PG_8] download documento f24_standard_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Gherkin spa e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente


  Scenario: [B2B-PA-SEND_PG_9] invio notifiche digitali mono destinatario senza physicalAddress (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Gherkin spa e:
      | physicalAddress | NULL |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"


  Scenario: [B2B-PA-SEND_PG_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When si tenta il recupero della notifica dal sistema tramite codice IUN "IUNUGYD-XHEZ-KLRM-202208-X-0"
    Then l'operazione ha prodotto un errore con status code "404"


  Scenario: [B2B-PA-SEND_PG_11] Invio notifica digitale mono destinatario Flat_rate_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | FLAT_RATE |
    And destinatario Gherkin spa e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica


  Scenario: [B2B-PA-SEND_PG_12] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Gherkin spa e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica


  Scenario: [B2B-PA-SEND_PG_13] verifica retention time dei documenti per la notifica inviata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"


  Scenario: [B2B-PA-SEND_PG_14] verifica retention time pagopaForm per la notifica inviata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"


  Scenario: [B2B-PA-SEND_PG_15] Invio notifica digitale mono destinatario senza taxonomyCode (verifica Default)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | taxonomyCode |   NULL   |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene controllato la presenza del taxonomyCode


  Scenario: [B2B-PA-SEND_PG_16] Invio notifica digitale mono destinatario con taxonomyCode (verifica Default)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | taxonomyCode |   010202N   |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene controllato la presenza del taxonomyCode


  Scenario: [B2B-PA-SEND_PG_17] Invio notifica digitale mono destinatario con payment senza PagopaForm_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN



  Scenario: [B2B-PA-SEND_PG_18] Invio notifica digitale mono destinatario con pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | payment_creditorTaxId | 77777777777 |
    When la notifica viene inviata dal "Comune_Multi"
    Then si verifica la corretta acquisizione della richiesta di invio notifica



  Scenario: [B2B-PA-SEND_PG_19] Invio notifica digitale mono destinatario senza pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin spa e:
      | payment | NULL |
    When la notifica viene inviata dal "Comune_Multi"
    Then si verifica la corretta acquisizione della richiesta di invio notifica


  Scenario: [B2B-PA-SEND_PG_20] Invio notifica digitale mono destinatario senza pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
      | amount | 2550 |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    And l'importo della notifica è 2550


  Scenario: [B2B-PA-SEND_PG_21] Invio notifica digitale mono destinatario physicalCommunication-REGISTERED_LETTER_890_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  Scenario: [B2B-PA-SEND_PG_22] Invio notifica digitale mono destinatario physicalCommunication-AR_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  Scenario: [B2B-PA-SEND_PG_22] Invio notifica digitale mono destinatario e verifica stato_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | idempotenceToken | AME2E3626070001.3  |
    And destinatario Gherkin spa
    When la notifica viene inviata dal "Comune_1"
    Then viene verificato lo stato di accettazione con idempotenceToken e paProtocolNumber


  Scenario: [B2B-PA-SEND_PG_23] Invio notifica digitale mono destinatario e verifica stato_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | idempotenceToken | AME2E3626070001.3  |
    And destinatario Gherkin spa
    When la notifica viene inviata dal "Comune_1"
    Then viene verificato lo stato di accettazione con requestID


  Scenario: [B2B-PA-SEND_PG_24] Invio notifica digitale mono destinatario e controllo paProtocolNumber con diverse pa_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale paProtocolNumber
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica


  Scenario: [B2B-PA-SEND_PG_25] Invio notifica digitale mono destinatario e controllo paProtocolNumber con uguale pa_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale paProtocolNumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "409"


  Scenario: [B2B-PA-SEND_PG_26] invio notifiche digitali e controllo paProtocolNumber e idempotenceToken con diversa pa_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | idempotenceToken | AME2E3626070001.1  |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.1"
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica


