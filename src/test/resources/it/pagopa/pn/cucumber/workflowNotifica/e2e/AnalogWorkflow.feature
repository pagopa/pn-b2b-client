Feature: Workflow analogico

  @e2e @ignore
  Scenario: [E2E_WF_ANALOG_1] Invio notifica con percorso analogico 890 con verifica elementi di timeline SEND_ANALOG_PROGRESS con legalFactId di category ANALOG_DELIVERY
    e documentType 23L, SEND_ANALOG_FEEDBACK e ANALOG_SUCCESS_WORKFLOW
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica con deliveryDetailCode "RECAG001B", legalFactId con category "ANALOG_DELIVERY" e documentType "23L"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG001C"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"