Feature: avanzamento notifiche b2b multi destinatario con persona fisica e giuridica

  Scenario: [B2B_TIMELINE_MULTI_PF_PG_01] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"

  Scenario: [B2B_TIMELINE_MULTI_PF_PG_02] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"

  Scenario: [B2B_TIMELINE_MULTI_PF_PG_03] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"

  Scenario: [B2B_TIMELINE_MULTI_PF_PG_04] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"

  Scenario: [B2B_TIMELINE_MULTI_PF_PG_05] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"


  @dev
  Scenario: [B2B_TIMELINE_MULTI_PF_PF_06] Invio notifica multidestinatario con pagamento destinatario 0 e 1 scenario  positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | taxId | LVLDAA85T50G702B |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    And l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 1
    #pagamento doppio

  Scenario: [B2B_TIMELINE_MULTI_PF_PF_07] Invio notifica multidestinatario con pagamento destinatario 0 e non del destinatario 1 scenario  positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | taxId | LVLDAA85T50G702B |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica dell'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
    And non vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 1

  Scenario: [B2B_TIMELINE_MULTI_PF_PF_08] Invio notifica multidestinatario con pagamento destinatario 1 e non del destinatario 0 scenario  positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | taxId | LVLDAA85T50G702B |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente dall'utente 1
    And si attende il corretto pagamento della notifica dell'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 1
    And non vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT" per l'utente 0
