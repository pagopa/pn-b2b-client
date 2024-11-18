Feature: Download legalFact analogico

  @dev @legalFact
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

  @dev @legalFact
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

  @dev @legalFact @con020 @con020success
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"

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
    
  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_11_TEST] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then la PA richiede il download dell'attestazione opponibile "COMPLETELY_UNREACHABLE"

  @dev
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_12_TEST] Invio notifica presenza allegato in corrispondenza dello stato "Aggiornamento sull'invio cartaceo" e download atto opponibile collegato a SEND_ANALOG_PROGRESS positivo PN-6090
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | via@OK-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003B"
    Then la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003B"


  @legalFact
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_13] Invio notifica con @fail_RS e richiesta atto opponibile DIGITAL_FAILURE_WORKFLOW con ente diverso dalla notifica - PN-8702
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RS  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    Then l'ente "Comune_1" richiede l'attestazione opponibile "DIGITAL_DELIVERY_FAILURE"
    And l'operazione ha prodotto un errore con status code "404"


  @legalFact
  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_14] Invio notifica con @ok_890 e richiesta atto opponibile SEND_ANALOG_PROGRESS con ente diverso dalla notifica - PN-8702
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B"
    Then l'ente "Comune_1" richiede l'attestazione opponibile "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B"
    And l'operazione ha prodotto un errore con status code "404"


  Scenario: [B2B_PA_ANALOGICO_LEGALFACT_15] Invio notifica e richiesta atto opponibile COMPLETELY_UNREACHABLE con ente diverso dalla notifica - PN-8702
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then l'ente "Comune_1" richiede l'attestazione opponibile "COMPLETELY_UNREACHABLE"
    And l'operazione ha prodotto un errore con status code "404"