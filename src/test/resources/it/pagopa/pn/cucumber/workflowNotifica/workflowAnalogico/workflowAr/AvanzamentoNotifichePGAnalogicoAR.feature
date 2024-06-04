Feature: avanzamento notifiche analogico AR persona giuridica


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_AR_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_AR_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@fail_AR      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "OK"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_AR_3] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_AR_scenario positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@fail-Discovery_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_AR_4] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il campo serviceLevel dell' evento di timeline "SEND_ANALOG_DOMICILE" sia valorizzato con "AR_REGISTERED_LETTER"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_AR_5] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And viene verificato il campo serviceLevel dell' evento di timeline "PREPARE_ANALOG_DOMICILE" sia valorizzato con "AR_REGISTERED_LETTER"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_AR_6] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And viene verificato il campo serviceLevel dell' evento di timeline "SEND_ANALOG_FEEDBACK" sia valorizzato con "AR_REGISTERED_LETTER"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_AR_7] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @mockNR  @workflowAnalogico
  Scenario: [B2B_TIMELINE_PG_ANALOG_AR_8] Attesa elemento di timeline PREPARE_ANALOG_DOMICILE_FAILURE con failureCode D00 non trovato - PG
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2              |
      | recipientType           | PG                          |
      | taxId                   | 00749900049                 |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR 16 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D00"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"


  @ignore @workflowAnalogico
  Scenario: [B2B_TIMELINE_PG_ANALOG_AR_9] Attesa elemento di timeline PREPARE_ANALOG_DOMICILE_FAILURE con failureCode D01 non valido - PG
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2               |
      | recipientType           | PG                           |
      | taxId                   | 00749900049                  |
      | digitalDomicile         | NULL                         |
      | physicalAddress_address | via @FAIL-Irreperibile_AR 16 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"


  @ignore @workflowAnalogico
  Scenario: [B2B_TIMELINE_PG_ANALOG_AR_10] Attesa elemento di timeline PREPARE_ANALOG_DOMICILE_FAILURE con failureCode D02 coincidente - PG
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination                        | Test AR Fail               |
      | recipientType                       | PG                         |
      | taxId                               | 00749900049                |
      | digitalDomicile                     | NULL                       |
      | physicalAddress_address             | Via @FAIL-Irreperibile_890 |
      | physicalAddress_zip                 | 40121                      |
      | physicalAddress_municipality        | BOLOGNA                    |
      | physicalAddress_province            | BO                         |
      | physicalAddress_addressDetails      | NULL                       |
      | physicalAddress_municipalityDetails | NULL                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D02"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"



  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_RIR_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_RIR_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di milano                |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL         |
      | physicalAddress_address | Via@fail_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "OK"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_RIR_3] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di milano                |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL         |
      | physicalAddress_address | Via@fail_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
