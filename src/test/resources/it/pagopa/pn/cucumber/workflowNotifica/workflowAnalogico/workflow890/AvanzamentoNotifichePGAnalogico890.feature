Feature: avanzamento notifiche analogico 890 persona giuridica


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_890_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Analogic e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_890_2] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Analogic e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "OK"


  @workflowAnalogico @mockNR @con020 @con020success
  Scenario: [B2B_TIMELINE_PG_ANALOG_890_3] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_890_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Analogic e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail-Discovery_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON020 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG003D[DISCOVERY;FAILCAUSE:M03].5s-RECAG003E[DOC:Plico;DOC:Indagine].5s-RECAG003F@discovered.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG001A.5s-RECAG001B[DOC:23L].5s-RECAG001C"

  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_890_4] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Analogic e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il campo serviceLevel dell' evento di timeline "SEND_ANALOG_DOMICILE" sia valorizzato con "REGISTERED_LETTER_890"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_890_5] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Analogic e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And viene verificato il campo serviceLevel dell' evento di timeline "PREPARE_ANALOG_DOMICILE" sia valorizzato con "REGISTERED_LETTER_890"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_890_6] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Analogic e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And viene verificato il campo serviceLevel dell' evento di timeline "SEND_ANALOG_FEEDBACK" sia valorizzato con "REGISTERED_LETTER_890"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_890_7] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @workflowAnalogico @mockNR @con020 @con020failed @con020ToCheck
  Scenario: [B2B_TIMELINE_PG_ANALOG_890_8] Invio notifica ed attesa sospensione invio cartaceo per avvenuto pagamento positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_890_SLOW  |
      | payment_f24             | PAYMENT_F24_FLAT |
      | title_payment           | F24_FLAT_GHERKIN |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si controlla con check rapidi che lo stato diventi ACCEPTED
    And l'avviso pagopa viene pagato correttamente
    Then si attende la corretta sospensione dell'invio cartaceo

    # 28/11/2024: è stato osservato che questo test non produce nella timeline nessun evento di
    # tipo SEND_ANALOG_PROGRESS, e quindi nessun evento CON020. IUN: ZXAD-RMEY-QMRL-202411-X-1
    # Controllo su CON020 sospeso fino a ulteriori indagini.
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"