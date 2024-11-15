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


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_PG_ANALOG_890_3] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_890_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Analogic e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@fail-Discovery_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    #And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
    #  | details | NOT_NULL |
    #  | details_recIndex | 0 |
    #  | details_sentAttemptMade | 0 |
    #  | details_deliveryDetailCode | CON080 |


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


  @workflowAnalogico @mockNR
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
