Feature: avanzamento notifiche b2b

  Scenario: [B2B_TIMELINE_1] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "ACCEPTED"


  Scenario: [B2B_TIMELINE_2] Invio notifica digitale ed attesa elemento di timeline REQUEST_ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"


  Scenario: [B2B_TIMELINE_3] Invio notifica digitale ed attesa elemento di timeline AAR_GENERATION positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"


  Scenario: [B2B_TIMELINE_4] Invio notifica digitale ed attesa elemento di timeline GET_ADDRESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"


  Scenario: [B2B_TIMELINE_5] Invio notifica digitale ed attesa stato DELIVERING_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"


  Scenario: [B2B_TIMELINE_6] Invio notifica digitale ed attesa elemento di timeline SEND_DIGITAL_DOMICILE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"


  Scenario: [B2B_TIMELINE_7] Invio notifica digitale ed attesa stato DELIVERING-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And il destinatario legge la notifica
    Then si verifica che la notifica abbia lo stato VIEWED


  Scenario: [B2B_TIMELINE_8] Invio notifica digitale ed attesa elemento di timeline DELIVERING-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERING"
    And il destinatario legge la notifica
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"


  Scenario: [B2B_TIMELINE_9] Invio notifica digitale ed attesa stato DELIVERED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"


  Scenario: [B2B_TIMELINE_10] Invio notifica digitale ed attesa stato DELIVERED-VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And il destinatario legge la notifica
    Then si verifica che la notifica abbia lo stato VIEWED


  Scenario: [B2B_TIMELINE_11] Invio notifica digitale ed attesa elemento di timeline DELIVERED-NOTIFICATION_VIEWED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And il destinatario legge la notifica
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"


  Scenario: [B2B-PA-PAY_1] Invio e visualizzazione notifica e verifica amount e effectiveDate
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | senderTaxId | 01199250158 |
      | feePolicy | DELIVERY_MODE |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And il destinatario legge la notifica
    Then vengono verificati costo = "200" e data di perfezionamento della notifica

  Scenario: [B2B-PA-PAY_2] Invio notifica e verifica amount
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | senderTaxId | 01199250158 |
      | feePolicy | DELIVERY_MODE |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "200" della notifica

  Scenario: [B2B-PA-PAY_3] Invio notifica FLAT e verifica amount
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | senderTaxId | 01199250158 |
      | feePolicy | FLAT_RATE |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica


  Scenario: [B2B_PA_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then la PA richiede il download dell'attestazione opponibile "SENDER_ACK"

  Scenario: [B2B_PA_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @ignore
  Scenario: [B2B_PA_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then la PA richiede il download dell'attestazione opponibile "PEC_RECEIPT"

  Scenario: [B2B_PA_LEGALFACT_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And il destinatario legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then la PA richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"


  Scenario: [B2B_PA_LEGALFACT_IO_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then viene richiesto tramite appIO il download dell'attestazione opponibile "SENDER_ACK"

  Scenario: [B2B_PA_LEGALFACT_IO_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then viene richiesto tramite appIO il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @ignore
  Scenario: [B2B_PA_LEGALFACT_IO_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then viene richiesto tramite appIO il download dell'attestazione opponibile "PEC_RECEIPT"

  Scenario: [B2B_PA_LEGALFACT_IO_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario della notifica
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    When la notifica viene inviata e si attende che lo stato diventi ACCEPTED
    And il destinatario legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then viene richiesto tramite appIO il download dell'attestazione opponibile "RECIPIENT_ACCESS"