Feature: Download legalFact analogico

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_1] Invio notifica con @fail_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_2] Invio notifica con @ok_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_3] Invio notifica con @fail_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di milano |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Cucumber Analogic e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_4] Invio notifica con @ok_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Cucumber Analogic e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS"
