Feature: invio notifiche b2b per la persona giuridica

  @testLite
  Scenario: [B2B-PA-SEND_PG_1] Invio notifica digitale mono destinatario persona giuridica lettura tramite codice IUN (p.giuridica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @testLite
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

  Scenario: [B2B-PA-SEND_PG_3] invio notifiche digitali mono destinatario (p.giuridica)_scenario negativo
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

  Scenario: [B2B-PA-SEND_PG_4] invio notifiche digitali mono destinatario (p.giuridica)_scenario positivo
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


  Scenario: [B2B-PA-SEND_PG_5] invio notifiche digitali mono destinatario (p.giuridica)_scenario negativo
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

  @ignore
  Scenario: [B2B-PA-SEND_PG_9] invio notifiche digitali mono destinatario senza physicalAddress (p.giuridica)_scenario negativo
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
    When si tenta il recupero della notifica dal sistema tramite codice IUN "UGYD-XHEZ-KLRM-202208-X-0"
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



