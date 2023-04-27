Feature: Download legalFact analogico

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_1] Invio notifica con @fail_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RS  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_2] Invio notifica con @ok_RS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_3] Invio notifica con @fail_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@fail_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002B"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002B"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_4] Invio notifica con @ok_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_5] Invio notifica con @fail_RIS e download atto opponibile collegato a DIGITAL_FAILURE_WORKFLOW positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    Then la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_6] Invio notifica con @ok_RIR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI003B"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI003B"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_7] Invio notifica con @fail_RIR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL         |
      | physicalAddress_address | Via@fail_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI004B"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI004B"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_8] Invio notifica con @fail_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL         |
      | physicalAddress_address | Via@fail_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003B"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003B"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_9_TEST] Invio notifica con @FAIL-Discovery_AR e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002E"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002E"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_10_TEST] Invio notifica con @FAIL-Discovery_890 e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@FAIL-Discovery_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003E"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003E"