Feature: avanzamento notifiche b2b con workflow cartaceo giacenza 890

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"


  @dev @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_1] Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode RECAG005C positivo PN-6093
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | via@OK-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG005C"


  @dev @ignore @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | denomination            | OK-Giacenza-gt10_890     |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | via@OK-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"


  @dev @ignore @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_3] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | denomination            | OK-GiacenzaDelegato-gt10_890     |
      | digitalDomicile         | NULL                             |
      | physicalAddress_address | via@OK-GiacenzaDelegato-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"


  @dev @ignore @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_4] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | denomination            | FAIL-Giacenza-gt10_890     |
      | digitalDomicile         | NULL                       |
      | physicalAddress_address | via@FAIL-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"


  @dev @ignore @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_5] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | denomination            | OK-CompiutaGiacenza_890     |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"


  @dev @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_6] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza-gt10_890     |
      | taxId                   | CLMCST42R12D969Z         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | via@OK-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG005C" e verifica data delay più 0
    #"sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.5s-RECAG012[DELAY:+10d].5s-RECAG011B[DOC:ARCAD;DOC:23L].5s-RECAG005A[DELAY:+20d].5s-RECAG005C[DELAY:+20d]"


  @dev @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_7] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-GiacenzaDelegato-gt10_890     |
      | taxId                   | CLMCST42R12D969Z                 |
      | digitalDomicile         | NULL                             |
      | physicalAddress_address | via@OK-GiacenzaDelegato-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011B" e verifica tipo DOC "23L"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG006C" e verifica data delay più 0
    #"sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.5s-RECAG012[DELAY:+10d].5s-RECAG011B[DOC:ARCAD;DOC:23L].5s-RECAG006A[DELAY:+25d].5s-RECAG006C[DELAY:+25d]"


  @dev @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_8] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | FAIL-Giacenza-gt10_890     |
      | taxId                   | CLMCST42R12D969Z           |
      | digitalDomicile         | NULL                       |
      | physicalAddress_address | via@FAIL-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011B" e verifica tipo DOC "23L"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B" e verifica tipo DOC "Plico"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007C" e verifica data delay più 0
    # "sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.5s-RECAG012[DELAY:+10d].5s-RECAG011B[DOC:ARCAD;DOC:23L].5s-RECAG007A[DELAY:+30d].5s-RECAG007B[DOC:Plico;DELAY:+30d].5s-RECAG007C[DELAY:+30d]"


  @dev @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_9] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890     |
      | taxId                   | CLMCST42R12D969Z            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008C" e verifica data delay più 0
    # "sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.5s-RECAG012[DELAY:+10d].5s-RECAG011B[DOC:ARCAD;DOC:23L].5s-RECAG008A[DELAY:+60d].5s-RECAG008B[DOC:Plico;DELAY:+60d].5s-RECAG008C[DELAY:+60d]"


  @dev @ignore @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_10] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | denomination            | OK-Giacenza-gt10_890     |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | via@OK-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @dev @giacenza890Complex @con020 @con020success
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_11] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode PNAG012 positivo PN-5820
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | FAIL-Giacenza-lte10_890     |
      | taxId                   | DVNLRD52D15M059P            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | Via@FAIL-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG007C" e verifica data delay più 0
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON080 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON020 |
      | details_sentAttemptMade | 0 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG007B |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "Plico"}] |
    #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.30s-RECAG007A.5s-RECAG007B[DOC:ARCAD;DOC:Plico].5s-RECAG007C"


  @dev @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_12] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-Giacenza-gt10-23L_890 PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                           |
      | physicalAddress_address | via@FAIL-Giacenza-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B" e verifica tipo DOC "23L" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B" e verifica tipo DOC "Plico" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007C" e verifica data delay più 0
  #"sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.60s-RECAG012.5s-RECAG011B[DOC:ARCAD].5s-RECAG007A.5s-RECAG007B[DOC:23L;DOC:Plico].5s-RECAG007C"


  @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_13] Attesa elemento di timeline REFINEMENT con physicalAddress OK-NO012-lte10
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL               |
      | physicalAddress_address | via@OK-NO012-lte10 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


  @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_14] Attesa elemento di timeline REFINEMENT con physicalAddress OK-NO012-gt10
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL              |
      | physicalAddress_address | via@OK-NO012-gt10 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


  @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_15] Attesa elemento di timeline REFINEMENT con physicalAddress OK-Giacenza-lte10_890-2
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL           |
      | physicalAddress_address | Via@OK-Giacenza-lte10_890-2 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


  @giacenza890Complex
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_16] Attesa elemento di timeline REFINEMENT con physicalAddress OK-Giacenza-lte10_890-3
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL           |
      | physicalAddress_address | Via@OK-Giacenza-lte10_890-3 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


  #{"sequenceName":"OK-MISSING-ARCAD-1","sequence":"@sequence.5s-CON080.5s-RECAG012.5s-RECAG011B[DOC:23L]"  },
  #{"sequenceName":"OK-MISSING-ARCAD-2","sequence":"@sequence.5s-CON080.5s-RECAG011B[DOC:23L].5s-RECAG012"  }
  @giacenza890Complex @ARCAD
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_17] PA - invio notifica 890 mono destinatario con sequence @OK-MISSING-ARCAD-1 -PN-9653
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL           |
      | physicalAddress_address | Via@OK-MISSING-ARCAD-1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011B" e verifica tipo DOC "23L"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


  @giacenza890Complex @ARCAD
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_18] PA - Invio notifica 890 multi destinatario (1 dest. con flusso digitale e 1 dest. con sequence @OK-MISSING-ARCAD-1) -PN-9653
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-MISSING-ARCAD-1 |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011B" e verifica tipo DOC "23L"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1


  @giacenza890Complex @ARCAD
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_19] PA - Invio notifica mono destinatario con sequence @OK-MISSING-ARCAD-2 -PN-9653
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL           |
      | physicalAddress_address | Via@OK-MISSING-ARCAD-2 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011B" e verifica tipo DOC "23L"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


  @giacenza890Complex @ARCAD
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_890_20] PA - Invio notifica 890 multi destinatario (1 dest. con flusso digitale e 1 dest. con sequence @OK-MISSING-ARCAD-2) -PN-9653
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-MISSING-ARCAD-2 |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011B" e verifica tipo DOC "23L"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1