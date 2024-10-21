Feature: avanzamento b2b notifica multi destinatario analogico AR

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi e verifico che l'utente 1 non abbia associato un evento "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@fail_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "OK" per l'utente 0


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_3] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@fail-Discovery_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0


  @mockNR @workflowAnalogico @mockNormalizzatore
  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_4] Attesa elemento di timeline PREPARE_ANALOG_DOMICILE_FAILURE con failureCode D02 coincidente - caso Multi
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination                        | Test AR Fail                 |
      | taxId                               | CNCGPP80A01H501J             |
      | digitalDomicile                     | NULL                         |
      | physicalAddress_address             | Via @FAIL-Irreperibile_AR 16 |
      | physicalAddress_zip                 | 40121                        |
      | physicalAddress_municipality        | BOLOGNA                      |
      | physicalAddress_province            | BO                           |
      | physicalAddress_addressDetails      | 0_CHAR                       |
      | physicalAddress_municipalityDetails | 0_CHAR                       |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D02" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW" per l'utente 0


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_5] Invio notifica multidestinatario complettamente irreperibile e successo digitale e controllo date business PN-9059
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    Then verifica date business in timeline COMPLETELY_UNREACHABLE per l'utente 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 1


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_6] Invio notifica multidestinatario completamente irreperibile e successo digitale con lettura notifica analogica e non presenza REFINEMENT PN-9059
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Leonardo Da Vinci             |
      | taxId                   | DVNLRD52D15M059P              |
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR_SLOW |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE" per l'utente 0
    Then "Leonardo Da Vinci" legge la notifica
    And viene verificato che l'elemento di timeline "REFINEMENT" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 1


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_7] Invio notifica multidestinatario completamente irreperibile e fallimento digitale e controllo date business PN-9059
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Leonardo Da Vinci        |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    And verifica date business in timeline COMPLETELY_UNREACHABLE per l'utente 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 1


  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_8] Invio notifica multidestinatario ritardo analogico ,2 tentativi analogici e successo digitale e controllo date business PN-9059
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                           |
      | physicalAddress_address | Via@OK-RITARDO_PERFEZIONAMENTO |
    And destinatario
      | denomination            | Leonardo Da Vinci     |
      | taxId                   | DVNLRD52D15M059P      |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 2
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 2
    Then verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente 0 al tentativo 0
    And verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente 1 al tentativo 1
    Then si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 1
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 2


  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_9] Invio notifica multidestinatario ritardo analogico ,2 tentativi analogici e fallimento digitale e controllo date business PN-9059
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                           |
      | physicalAddress_address | Via@OK-RITARDO_PERFEZIONAMENTO |
    And destinatario
      | denomination            | Leonardo Da Vinci     |
      | taxId                   | DVNLRD52D15M059P      |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW" per l'utente 2
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 2
    Then verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente 0 al tentativo 0
    And verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente 1 al tentativo 1
    Then si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 1
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 2


  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_10] Invio notifica multidestinatario ritardo analogico e fallimento digitale e controllo date business PN-9059
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                           |
      | physicalAddress_address | Via@OK-RITARDO_PERFEZIONAMENTO |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    And verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente 0 al tentativo 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 1


  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_11] Invio notifica multidestinatario ritardo analogico e successo digitale e controllo date business PN-9059
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                           |
      | physicalAddress_address | Via@OK-RITARDO_PERFEZIONAMENTO |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    And verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente 0 al tentativo 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 1


  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_12] Invio notifica multidestinatario ritardo analogico ,2 tentativi e controllo date business PN-9059
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                           |
      | physicalAddress_address | Via@OK-RITARDO_PERFEZIONAMENTO |
    And destinatario
      | denomination            | Leonardo Da Vinci     |
      | taxId                   | DVNLRD52D15M059P      |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 2
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 2
    Then verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente 0 al tentativo 0
    And verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente 1 al tentativo 1
    Then si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 1
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 2


  Scenario: [B2B_TIMELINE_MULTI_ANALOG_AR_13] Invio notifica multidestinatario ritardo analogicoe controllo notificationCost non settato PN-9488
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                           |
      | physicalAddress_address | Via@OK-RITARDO_PERFEZIONAMENTO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    Then "Mario Gherkin" legge la notifica dopo i 10 giorni
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" abbia notificationCost ugauale a "null"



  @workflowAnalogico
  Scenario: [B2B_TIMELINE_MULTI_ANALOG_RIR_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RIR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi e verifico che l'utente 1 non abbia associato un evento "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_MULTI_ANALOG_RIR_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_RIR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "OK" per l'utente 0