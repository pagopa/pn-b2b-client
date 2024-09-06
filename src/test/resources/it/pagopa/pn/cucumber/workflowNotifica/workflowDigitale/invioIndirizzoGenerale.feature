Feature: avanzamento b2b notifica digitale con indirizzo generale con chiamata a National Registry (INAD-IPA-INIPEC)


  @dev @mockNR
  #[B2B_TIMELINE_23]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_1] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE sia presente il campo Digital Address scenario positivo PN-5992
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | digitalDomicile_address | test@fail.it |
      | taxId | JHKRFU96H15F068N |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry

  @dev @realNR
  #[B2B_TIMELINE_23_1]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_2] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE sia presente il campo Digital Address scenario positivo PN-5992
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | digitalDomicile_address | test@fail.it |
      | taxId | TRVVCN73H02L259 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry


  @dev
  #[B2B_TIMELINE_PG_16], [B2B_TIMELINE_PG_13], [B2B_TIMELINE_DIGITAL_GENERAL_5]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_3] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE sia presente il campo Digital Address scenario positivo PN-5992
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry

  @dev
  #[B2B_TIMELINE_PG_12]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_4] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
