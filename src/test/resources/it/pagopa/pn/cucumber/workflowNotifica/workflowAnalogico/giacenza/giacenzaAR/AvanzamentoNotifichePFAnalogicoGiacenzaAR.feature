Feature: avanzamento notifiche b2b con workflow cartaceo giacenza AR

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"


  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_AR_1] Invio Notifica Mono destinatario workflow cartaceo - Caso OK-Giacenza_AR- PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL               |
      | physicalAddress_address | via@OK-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN003C"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | CON080 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
#    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
#      | details | NOT_NULL |
#      | details_deliveryDetailCode | CON020 |
#      | details_recIndex | 0 |
#      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN003B |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "AR"}] |
#    "sequence":"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECRN010.5s-RECRN011.5s-RECRN003A.5s-RECRN003B[DOC:AR].5s-RECRN003C"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_AR_2] Invio Notifica Mono destinatario workflow cartaceo - Caso OK-Giacenza_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                 |
      | physicalAddress_address | Via@FAIL-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN004C"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | CON080 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
#    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
#    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
#      | details | NOT_NULL |
#      | details_deliveryDetailCode | CON020 |
#      | details_recIndex | 0 |
#      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN011 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN004B |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "Plico"}] |
#  @sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECRN010.5s-RECRN011.5s-RECRN004A.5s-RECRN004B[DOC:Plico].5s-RECRN004C"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_AR_3] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@FAIL-CompiutaGiacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN011"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005B" e verifica tipo DOC "Plico"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005C"
    #"sequence": "@sequence.5s-CON080.5s-RECRN010.5s-RECRN011.5s-RECRN005A.5s-RECRN005B[DOC:Plico].5s-RECRN005C"


  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_AR_4] Invio Notifica Mono destinatario workflow cartaceo - Caso OK-Giacenza-gt10_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@OK-Giacenza-gt10_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNRN012"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | CON080 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
#    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
#      | details | NOT_NULL |
#      | details_deliveryDetailCode | CON020 |
#      | details_recIndex | 0 |
#      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN011 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN003B |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "AR"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN003C |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
#  @sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECRN010.5s-RECRN011.80s-RECRN003A.5s-RECRN003B[DOC:AR].5s-RECRN003C"

  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_AR_5] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-Giacenza-gt10_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Giacenza-gt10_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNRN012"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | CON080 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
#    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
#      | details | NOT_NULL |
#      | details_deliveryDetailCode | CON020 |
#      | details_recIndex | 0 |
#      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN011 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN004B |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_deliveryDetailCode | RECRN004C |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
  #"sequence": "@sequence.5s-CON080.5s-RECRN010.5s-RECRN011.80s-RECRN004A.5s-RECRN004B[DOC:Plico].5s-RECRN004C"


  @dev @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_GIACENZA_AR_6] Invio Notifica Mono destinatario workflow cartaceo - Caso FAIL-CompiutaGiacenza_AR PN-5927
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                              |
      | physicalAddress_address | Via@FAIL-CompiutaGiacenza-gt10_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002C"
    #"@sequence.MANCANTE
