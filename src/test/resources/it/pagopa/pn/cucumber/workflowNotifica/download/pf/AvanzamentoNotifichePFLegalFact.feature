Feature: Download legalFact

  @SmokeTest @legalFact
  Scenario: [B2B_PA_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then la PA richiede il download dell'attestazione opponibile "SENDER_ACK"

  @legalFact
  Scenario: [B2B_PA_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @legalFact
  Scenario: [B2B_PA_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then la PA richiede il download dell'attestazione opponibile "PEC_RECEIPT"

    #TODO Test PN-8185 necessita pec reale per la corretta esecuzione..pectest@pec.pagopa.it
  Scenario: [B2B_PA_LEGALFACT_3_1] Invio notifica e download atto opponibile PEC_RECEIPT e verifica estensione ricevuta pec formato eml scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      |digitalDomicile_address|destinatario@certificatanoprod.notifichedigitali.it|
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then la PA richiede il download dell'attestazione opponibile PEC_RECEIPT

 #TODO Test PN-8185  necessita pec reale per la corretta esecuzione..pectest@pec.pagopa.it
  Scenario: [B2B_WEB-RECIPIENT_LEGALFACT_3_1] Invio notifica e download atto opponibile PEC_RECEIPT e verifica estensione ricevuta pec formato eml scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      |digitalDomicile_address|destinatario@certificatanoprod.notifichedigitali.it|
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile PEC_RECEIPT

  @legalFact
  Scenario: [B2B_PA_LEGALFACT_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then la PA richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"

  @legalFact  @appIo
  Scenario: [B2B_PA_LEGALFACT_IO_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then viene richiesto tramite appIO il download dell'attestazione opponibile "SENDER_ACK"

  @legalFact  @appIo
  Scenario: [B2B_PA_LEGALFACT_IO_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then viene richiesto tramite appIO il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @legalFact  @appIo
  Scenario: [B2B_PA_LEGALFACT_IO_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then viene richiesto tramite appIO il download dell'attestazione opponibile "PEC_RECEIPT"

  @legalFact  @appIo
  Scenario: [B2B_PA_LEGALFACT_IO_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then viene richiesto tramite appIO il download dell'attestazione opponibile "RECIPIENT_ACCESS"

  @legalFact
  Scenario: [B2B_WEB-RECIPIENT_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "SENDER_ACK"

  @legalFact
  Scenario: [B2B_WEB-RECIPIENT_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @legalFact
  Scenario: [B2B_WEB-RECIPIENT_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "PEC_RECEIPT"

  @legalFact
  Scenario: [B2B_WEB-RECIPIENT_LEGALFACT_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then "Mario Gherkin" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"

  @legalFact
  Scenario: [B2B_PA_LEGALFACT_KEY_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then viene verificato che la chiave dell'attestazione opponibile "SENDER_ACK" è "PN_LEGAL_FACTS"

  @legalFact
  Scenario: [B2B_PA_LEGALFACT_KEY_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then viene verificato che la chiave dell'attestazione opponibile "DIGITAL_DELIVERY" è "PN_LEGAL_FACTS"

  @legalFact
  Scenario: [B2B_PA_LEGALFACT_KEY_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then viene verificato che la chiave dell'attestazione opponibile "PEC_RECEIPT" è "PN_EXTERNAL_LEGAL_FACTS"

  @legalFact
  Scenario: [B2B_PA_LEGALFACT_KEY_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then viene verificato che la chiave dell'attestazione opponibile "RECIPIENT_ACCESS" è "PN_LEGAL_FACTS"


  Scenario: [B2B_PA_LEGALFACT_5] Invio notifica e download atto opponibile SENDER_ACK_scenario senza legalFactType positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then la PA richiede il download dell'attestazione opponibile "SENDER_ACK" senza legalFactType



  Scenario: [B2B_PA_LEGALFACT_6] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario senza legalFactType positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY" senza legalFactType


  Scenario: [B2B_PA_LEGALFACT_7] Invio notifica e download atto opponibile PEC_RECEIPT_scenario senza legalFactType positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then la PA richiede il download dell'attestazione opponibile "PEC_RECEIPT" senza legalFactType


  Scenario: [B2B_PA_LEGALFACT_8] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario senza legalFactType positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then la PA richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS" senza legalFactType


  @legalFact
  Scenario: [B2B_PA_LEGALFACT_9] Invio notifica e richiesta atto opponibile PEC_RECEIPT con ente diverso dalla notifica - PN-8702
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then l'ente "Comune_Multi" richiede l'attestazione opponibile "PEC_RECEIPT"
    And l'operazione ha prodotto un errore con status code "404"


  Scenario: [B2B_PA_LEGALFACT_10] Invio notifica e richiesta atto opponibile RECIPIENT_ACCESS con ente diverso dalla notifica - PN-8702
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then l'ente "Comune_Multi" richiede l'attestazione opponibile "RECIPIENT_ACCESS"
    And l'operazione ha prodotto un errore con status code "404"


  @legalFact
  Scenario: [B2B_PA_LEGALFACT_11] Invio notifica e richiesta atto opponibile DIGITAL_DELIVERY con ente diverso dalla notifica - PN-8702
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then l'ente "Comune_Multi" richiede l'attestazione opponibile "DIGITAL_DELIVERY"
    And l'operazione ha prodotto un errore con status code "404"


  Scenario: [B2B_PA_LEGALFACT_12] Invio notifica e richiesta atto opponibile SENDER_ACK con ente diverso dalla notifica - PN-8702
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then l'ente "Comune_Multi" richiede l'attestazione opponibile "SENDER_ACK"
    And l'operazione ha prodotto un errore con status code "404"