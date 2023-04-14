Feature: invio notifiche b2b per la persona giuridica

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
    And destinatario Gherkin spa
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

  @testLite
  Scenario: [B2B-PA-SEND_PG_27] Invio notifica  mono destinatario con documenti pre-caricati non trovati su safestorage  scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b senza preload allegato dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica che la notifica non viene accettata
