Feature: invio notifiche b2b


  @B2Btest @SmokeTest @integrationTest
  Scenario: [B2B-PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @B2Btest
  Scenario: [B2B-PA-SEND_2] Invio notifiche digitali mono destinatario (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | idempotenceToken | AME2E3626070001.1  |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.2"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica

  @B2Btest
  Scenario: [B2B-PA-SEND_3] invio notifiche digitali mono destinatario (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | idempotenceToken | AME2E3626070001.1  |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.1"
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "409"


  @B2Btest @integrationTest
  Scenario: [B2B-PA-SEND_4] invio notifiche digitali mono destinatario (p.fisica)_scenario positivo
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

  @SmokeTest
  Scenario: [B2B-PA-SEND_5] invio notifiche digitali mono destinatario (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "409"


  @B2Btest @SmokeTest @integrationTest
  Scenario: [B2B-PA-SEND_6] download documento notificato_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | document | SI |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "NOTIFICA"
    Then il download si conclude correttamente

  @SmokeTest
  Scenario: [B2B-PA-SEND_7] download documento pagopa_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente


  Scenario: [B2B-PA-SEND_8] download documento f24_standard_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente

  @SmokeTest @integrationTest
  Scenario: [B2B-PA-SEND_9] invio notifiche digitali mono destinatario senza physicalAddress (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber e:
      | physicalAddress | NULL |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  Scenario: [B2B-PA-SEND_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    When si tenta il recupero della notifica dal sistema tramite codice IUN "IUNUGYD-XHEZ-KLRM-202208-X-0"
    Then l'operazione ha prodotto un errore con status code "404"


  Scenario: [B2B-PA-SEND_11] Invio notifica digitale mono destinatario Flat_rate_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | FLAT_RATE |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica

  @integrationTest
  Scenario: [B2B-PA-SEND_12] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo
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


  Scenario: [B2B-PA-SEND_13] verifica retention time dei documenti per la notifica inviata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE"

  Scenario: [B2B-PA-SEND_14] verifica retention time pagopaForm per la notifica inviata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"

  @SmokeTest
  Scenario: [B2B-PA-SEND_15] verifica retention time dei documenti pre-caricati
    Given viene effettuato il pre-caricamento di un documento
    Then viene effettuato un controllo sulla durata della retention di "ATTO OPPONIBILE" precaricato

  @SmokeTest
  Scenario: [B2B-PA-SEND_16] verifica retention time  pagopaForm pre-caricato
    Given viene effettuato il pre-caricamento di un allegato
    Then viene effettuato un controllo sulla durata della retention di "PAGOPA" precaricato

  Scenario: [B2B-PA-SEND_17] Invio notifica digitale mono destinatario senza taxonomyCode (verifica Default)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | taxonomyCode |   NULL   |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene controllato la presenza del taxonomyCode


  Scenario: [B2B-PA-SEND_18] Invio notifica digitale mono destinatario con taxonomyCode (verifica Default)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | taxonomyCode |   010202N   |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene controllato la presenza del taxonomyCode


  @SmokeTest
  Scenario: [B2B-PA-SEND_19] Invio notifica digitale mono destinatario con payment senza PagopaForm_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
      | payment_pagoPaForm | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @integrationTest
  Scenario: [B2B-PA-SEND_20] Invio notifica digitale mono destinatario con pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | 77777777777 |
    When la notifica viene inviata dal "Comune_Multi"
    Then si verifica la corretta acquisizione della richiesta di invio notifica


  @integrationTest
  Scenario: [B2B-PA-SEND_21] Invio notifica digitale mono destinatario con pagamento
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    When la notifica viene inviata dal "Comune_Multi"
    Then si verifica la corretta acquisizione della richiesta di invio notifica



