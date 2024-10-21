Feature: Gestione Feedback Analogici Duplicati


  @workflowAnalogico
  Scenario: [B2B_FEEDBACK_ANALOG_1] Invio notifica Analogica - Vengono recepiti gli eventi fino al suo perfezionamento
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @workflowAnalogico
  Scenario: [B2B_FEEDBACK_ANALOG_2] Invio notifica Analogica - Caso avanzamento spedizione e ricezione eventi di PROGRESS
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | REGISTERED_LETTER_890        |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL |
      | physicalAddress_address | Via@OK_START_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino allo stato della notifica "DELIVERING"

  @workflowAnalogico
  Scenario: [B2B_FEEDBACK_ANALOG_3] Invio notifica Analogica mediante sequence con evento finale KO - Ricezione di blocco di eventi successivi (RECRN002D, RECRN002E, RECRN002F)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@FAIL_duplicate_feedback |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then viene verificato che per l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" non ci siano duplicati

  @workflowAnalogico
  Scenario: [B2B_FEEDBACK_ANALOG_4] Invio notifica Analogica mediante sequence con evento finale KO - Ricezione del singolo evento successivo (RECRN002C)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@FAIL_duplicate_final_event |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then viene verificato che per l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" non ci siano duplicati

  @workflowAnalogico
  Scenario: [B2B_FEEDBACK_ANALOG_5] Invio notifica Analogica mediante sequence con evento finale OK - Ricezione di blocco di eventi successivi (RECRN002D, RECRN002E, RECRN002F)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@OK_duplicate_feedback |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then viene verificato che per l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" non ci siano duplicati

  @workflowAnalogico
  Scenario: [B2B_FEEDBACK_ANALOG_6] Invio notifica Analogica mediante sequence con evento finale OK - Ricezione del singolo evente successivo (RECRN001C)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@OK_duplicate_final_event |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then viene verificato che per l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" non ci siano duplicati