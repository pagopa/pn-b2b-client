Feature: avanzamento notifiche b2b con workflow cartaceo AR

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_2] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo PN-9059
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente 0
    And verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente 0 al tentativo 0


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_3] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo PN-9059
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And verifica date business in timeline COMPLETELY_UNREACHABLE per l'utente 0


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_4] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@fail_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "OK"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_5] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_fail_AR_NR_scenario positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@fail_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_6] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@fail-Discovery_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_7] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Giovanna D'Arco  |
      | taxId                   | DRCGNN12A46A326K |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_AR        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_8] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campi municipalityDetails e foreignState positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And viene verificato che nell'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE" siano configurati i campi municipalityDetails e foreignState


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_9] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il campo serviceLevel dell' evento di timeline "SEND_ANALOG_DOMICILE" sia valorizzato con "AR_REGISTERED_LETTER"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_10] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And viene verificato il campo serviceLevel dell' evento di timeline "PREPARE_ANALOG_DOMICILE" sia valorizzato con "AR_REGISTERED_LETTER"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_11] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And viene verificato il campo serviceLevel dell' evento di timeline "SEND_ANALOG_FEEDBACK" sia valorizzato con "AR_REGISTERED_LETTER"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_12] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_13] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_NR negativo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2                              |
      | taxId                   | DVNLRD52D15M059P                            |
      | digitalDomicile         | NULL                                        |
      | physicalAddress_address | Via NationalRegistries@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_14] Attesa elemento di timeline SEND_ANALOG_DOMICILE_scenario positivo PN-5283 Presente
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@fail-Discovery_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" e verifica indirizzo secondo tentativo "ATTEMPT_1"
    #verificato che al secondo tentativo l'indirizzo sia riportato in maiuscolo


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_15] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN015 AR momentaneamente non rendicontabile positivo PN-6079
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | denomination            | OK-CausaForzaMaggiore_AR     |
      | digitalDomicile         | NULL                         |
      | physicalAddress_address | via@OK-CausaForzaMaggiore_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN015"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001B" e verifica tipo DOC "AR"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    #"sequence": "@sequence.5s-CON080.5s-RECRN015.5s-RECRN001A.5s-RECRN001B[DOC:AR;DELAY:1s].5s-RECRN001C"

  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_16] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN013 AR momentaneamente non rendicontabile positivo PN-6079
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | via@OK-NonRendicontabile_AR |
      | taxId                   | DVNLRD52D15M059P            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-NonRendicontabile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080" tentativo "ATTEMPT_0.IDX_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN013"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080" tentativo "ATTEMPT_0.IDX_3"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001B" e verifica tipo DOC "AR"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    #"@sequence.5s-CON080.5s-RECRN013@retry.5s-CON080.5s-RECRN001A.5s-RECRN001B[DOC:AR].5s-RECRN001C"
    #vedere il discorso Retry presenza due volte di CON080


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_17] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-DiscoveryIrreperibile_AR_scenario positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                              |
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002E" e verifica tipo DOC "Plico" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002E" e verifica tipo DOC "Indagine" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F" e deliveryFailureCause "M01" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080" tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN002E" e verifica tipo DOC "Plico" tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F" e deliveryFailureCause "M03" tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_18] Attesa elemento di timeline PREPARE_ANALOG_DOMICILE_FAILURE con failureCode D00 non trovato
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | Test AR Fail 2              |
      | taxId                   | DVNLRD52D15M059P            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR 16 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D00"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"

  @mockNR  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_AR_19] Attesa elemento di timeline PREPARE_ANALOG_DOMICILE_FAILURE con failureCode D01 non valido
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | Test AR Fail 2               |
      | taxId                   | NNVFNC80A01H501G             |
      | digitalDomicile         | NULL                         |
      | physicalAddress_address | via @FAIL-Irreperibile_AR 16 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"


  @mockNR  @workflowAnalogico @mockNormalizzatore
  Scenario: [B2B_TIMELINE_ANALOG_AR_20] Attesa elemento di timeline PREPARE_ANALOG_DOMICILE_FAILURE con failureCode D02 coincidente
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination                        | Test AR Fail                 |
      | taxId                               | CNCGPP80A01H501J             |
      | digitalDomicile                     | NULL                         |
      | physicalAddress_address             | via @FAIL-Irreperibile_AR 16 |
      | physicalAddress_zip                 | 40121                        |
      | physicalAddress_municipality        | BOLOGNA                      |
      | physicalAddress_province            | BO                           |
      | physicalAddress_addressDetails      | 0_CHAR                       |
      | physicalAddress_municipalityDetails | 0_CHAR                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D02"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"

  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_RIR_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_RIR_2] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_RIR_scenario negativo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL         |
      | physicalAddress_address | Via@FAIL_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "OK"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_RIR_3] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_RIR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_RIR_4] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESSdeliveryDetailCode "RECRI001" scenario positivo PN-6634
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI001"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_RIR_5] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESS_deliveryDetailCode "RECRI002" scenario positivo PN-6634
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI002"
