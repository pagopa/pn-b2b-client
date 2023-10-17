Feature: avanzamento notifiche b2b con workflow cartaceo

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"


  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_40] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | OK-Giacenza-gt10_890 |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@OK-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG005C" e verifica data delay più 0
    #"sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.5s-RECAG012[DELAY:+10d].5s-RECAG011B[DOC:ARCAD;DOC:23L].5s-RECAG005A[DELAY:+20d].5s-RECAG005C[DELAY:+20d]"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_41] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | OK-GiacenzaDelegato-gt10_890 |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@OK-GiacenzaDelegato-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG006C" e verifica data delay più 0
    #"sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.5s-RECAG012[DELAY:+10d].5s-RECAG011B[DOC:ARCAD;DOC:23L].5s-RECAG006A[DELAY:+25d].5s-RECAG006C[DELAY:+25d]"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_42] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | FAIL-Giacenza-gt10_890 |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@FAIL-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007C" e verifica data delay più 0
    # "sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.5s-RECAG012[DELAY:+10d].5s-RECAG011B[DOC:ARCAD;DOC:23L].5s-RECAG007A[DELAY:+30d].5s-RECAG007B[DOC:Plico;DELAY:+30d].5s-RECAG007C[DELAY:+30d]"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_43] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | OK-CompiutaGiacenza_890 |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008C" e verifica data delay più 0
    # "sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.5s-RECAG012[DELAY:+10d].5s-RECAG011B[DOC:ARCAD;DOC:23L].5s-RECAG008A[DELAY:+60d].5s-RECAG008B[DOC:Plico;DELAY:+60d].5s-RECAG008C[DELAY:+60d]"

  @dev @ignore @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_44] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | OK-Giacenza-gt10_890 |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@OK-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @dev @ignore @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_45] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN015 AR momentaneamente non rendicontabile positivo PN-6079
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | OK-CausaForzaMaggiore_AR |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@OK-CausaForzaMaggiore_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN015"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001B" e verifica tipo DOC "AR"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    #"sequence": "@sequence.5s-CON080.5s-RECRN015.5s-RECRN001A.5s-RECRN001B[DOC:AR;DELAY:1s].5s-RECRN001C"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_46] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN013 AR momentaneamente non rendicontabile positivo PN-6079
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination            | via@OK-NonRendicontabile_AR |
      | taxId                   | DVNLRD52D15M059P            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-NonRendicontabile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080" tentativo "ATTEMPT_0.IDX_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN013"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080" tentativo "ATTEMPT_0.IDX_3"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001B" e verifica tipo DOC "AR"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    #"@sequence.5s-CON080.5s-RECRN013@retry.5s-CON080.5s-RECRN001A.5s-RECRN001B[DOC:AR].5s-RECRN001C"
    #vedere il discorso Retry presenza due volte di CON080


  #CON080 SEND_ANALOG_PROGRESS.IUN_YUTY-ZREA-ALHG-202306-V-1.RECINDEX_0.ATTEMPT_0.IDX_1
  #RECRN013
  #SEND_ANALOG_PROGRESS.IUN_YUTY-ZREA-ALHG-202306-V-1.RECINDEX_0.ATTEMPT_0.IDX_3  CON080

 # RECRN001B

  #RECRN001C


  @dev @ignore @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_47] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN015 890 momentaneamente non rendicontabile positivo PN-6079
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | OK-CausaForzaMaggiore_890 |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@OK-CausaForzaMaggiore_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG015"
   # And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B" e verifica tipo DOC "23L"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    #"@sequence.5s-CON080.5s-RECAG015.5s-RECAG001A.5s-RECAG001B[DOC:23L].5s-RECAG001C"

  @dev @ignore @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_48] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN013 890 momentaneamente non rendicontabile positivo PN-6079
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | OK-NonRendicontabile_890 |
      | taxId | LVLDAA85T50G702B |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@OK-NonRendicontabile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG013"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B" e verifica tipo DOC "23L"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    #"@sequence.5s-CON080.5s-RECAG013@retry.5s-CON080.5s-RECAG001A.5s-RECAG001B[DOC:23L].5s-RECAG001C"



  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_49] Invio Notifica Mono destinatario workflow cartaceo - Caso OK-Giacenza_AR- PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL               |
      | physicalAddress_address | via@OK-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
   # And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN010"
   # And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN011"
   # And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003B" e verifica tipo DOC "AR"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN003C"
    #"sequence": "@sequence.5s-CON080.5s-RECRN010.5s-RECRN011.5s-RECRN003A.5s-RECRN003B[DOC:AR].5s-RECRN003C"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_50] Invio Notifica Mono destinatario workflow cartaceo - Caso OK-Giacenza_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                 |
      | physicalAddress_address | Via@FAIL-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
   # And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN010"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN011"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN004A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN004B" e verifica tipo DOC "Plico"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN004C"
    #"@sequence.5s-CON080.5s-RECRN010.5s-RECRN011.5s-RECRN004A.5s-RECRN004B[DOC:Plico].5s-RECRN004C"



  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_51] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@FAIL-CompiutaGiacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN010"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN011"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005B" e verifica tipo DOC "Plico"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN005C"
    #"@sequence.5s-CON080.5s-RECRN010.5s-RECRN011.5s-RECRN005A.5s-RECRN005B[DOC:Plico].5s-RECRN005C"


  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_52] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination            | FAIL-Giacenza-lte10_890     |
      | taxId                   | DVNLRD52D15M059P            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | Via@FAIL-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B" e verifica tipo DOC "Plico"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG007C" e verifica data delay più 0
  #"@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.5s-RECAG012[DELAY:+5d].5s-RECAG007A[DELAY:+5d].5s-RECAG007B[DOC:ARCAD;DOC:Plico;DELAY:+5d].5s-RECAG007C[DELAY:+5d]"
  #Da verificare--------------


  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_53] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-DiscoveryIrreperibile_AR_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
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


  #"1. Creare una notifica che preveda il percorso analogico con indirizzo Via@FAIL-DiscoveryIrreperibile_AR
  #2. SEND_ANALOG_PROGRESS con deliveryDetailCode CON080
  #3. SEND_ANALOG_PROGRESS con deliveryDetailCode RECRN002E e docType Plico Indagine
  #4. SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN002F M01
  #5. SEND_ANALOG_PROGRESS con deliveryDetailCode CON080
  #6. SEND_ANALOG_PROGRESS con deliveryDetailCode RECRN002E e docType Plico
  #7. SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN002F M03
  #8. ANALOG_FAILURE_WORKFLOW"


  #OK-Giacenza-gt10_AR
  #FAIL-Giacenza-gt10_AR,
  #FAIL-CompiutaGiacenza-gt10_AR
  #FAIL-CompiutaGiacenza_AR
  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_54] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@FAIL-CompiutaGiacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN010"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN011"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005B" e verifica tipo DOC "Plico"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005C"
    #"sequence": "@sequence.5s-CON080.5s-RECRN010.5s-RECRN011.5s-RECRN005A.5s-RECRN005B[DOC:Plico].5s-RECRN005C"


  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_55] Invio Notifica Mono destinatario workflow cartaceo - Caso OK-Giacenza-gt10_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@OK-Giacenza-gt10_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN010"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN011"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNRN012"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003B" e verifica tipo DOC "AR"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003C"
    #"sequence": "@sequence.5s-CON080.5s-RECRN010.5s-RECRN011.80s-RECRN003A.5s-RECRN003B[DOC:AR].5s-RECRN003C"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_56] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-Giacenza-gt10_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@FAIL-Giacenza-gt10_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN011"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNRN012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN004B" e verifica tipo DOC "Plico"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN004C"
  #"sequence": "@sequence.5s-CON080.5s-RECRN010.5s-RECRN011.80s-RECRN004A.5s-RECRN004B[DOC:Plico].5s-RECRN004C"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_57] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@FAIL-CompiutaGiacenza-gt10_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002C"
    #"@sequence.MANCANTE


  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_58] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-DiscoveryIrreperibile_890_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003E" e verifica tipo DOC "Plico" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG003F" e deliveryFailureCause "M03" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080" tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003E" e verifica tipo DOC "Plico" tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG003F" e deliveryFailureCause "M03" tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    #"@sequence.5s-CON080.5s-RECAG003D[DISCOVERY;FAILCAUSE:M03].5s-RECAG003E[DOC:Plico;DOC:Indagine].5s-RECAG003F@discovered.5s-CON080.5s-RECAG003D[FAILCAUSE:M03].5s-RECAG003E[DOC:Plico].5s-RECAG003F"


  #RECAG003E
  #deliveryFailureCause: M03
  #deliveryDetailCode: RECAG003F

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_59] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-Giacenza-gt10-23L_890 PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | via@FAIL-Giacenza-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B" e verifica tipo DOC "Plico" tentativo "ATTEMPT_0.IDX_3"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B" e verifica tipo DOC "23L" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007C" e verifica data delay più 0
  #"sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.60s-RECAG012.5s-RECAG011B[DOC:ARCAD].5s-RECAG007A.5s-RECAG007B[DOC:23L;DOC:Plico].5s-RECAG007C"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_60] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESSdeliveryDetailCode "RECRI001" scenario positivo PN-6634
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI001"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_61] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESS_deliveryDetailCode "RECRI002" scenario positivo PN-6634
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI002"



  @irreperibile_7623_Test @irreperibile_7623_Uat @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_62] Attesa elemento di timeline PREPARE_ANALOG_DOMICILE_FAILURE con failureCode D00 non trovato
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination            | Test AR Fail 2              |
      | taxId                   | DVNLRD52D15M059P            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR 16 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D00"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"

  @irreperibile_7623_Test  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_63] Attesa elemento di timeline PREPARE_ANALOG_DOMICILE_FAILURE con failureCode D01 non valido
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | NNVFNC80A01H501G |
      | digitalDomicile | NULL |
      | physicalAddress_address | via @FAIL-Irreperibile_AR 16 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"



  @irreperibile_7623_Test  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_64] Attesa elemento di timeline PREPARE_ANALOG_DOMICILE_FAILURE con failureCode D02 coincidente
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination                        | Test AR Fail                 |
      | taxId                               | CNCGPP80A01H501J             |
      | digitalDomicile                     | NULL                         |
      | physicalAddress_address             | via @FAIL-Irreperibile_AR 16 |
      | physicalAddress_zip                 | 40121                        |
      | physicalAddress_municipality        | BOLOGNA                      |
      | physicalAddress_province            | BO                           |
      | physicalAddress_addressDetails      | NULL                         |
      | physicalAddress_municipalityDetails | NULL                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D02"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"

  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_65] Attesa elemento di timeline REFINEMENT con physicalAddress OK-WO-011B
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL           |
      | physicalAddress_address | via@OK-WO-011B |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_66] Attesa elemento di timeline REFINEMENT con physicalAddress OK-NO012-lte10
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL               |
      | physicalAddress_address | via@OK-NO012-lte10 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_67] Attesa elemento di timeline REFINEMENT con physicalAddress OK-NO012-gt10
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL              |
      | physicalAddress_address | via@OK-NO012-gt10 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"