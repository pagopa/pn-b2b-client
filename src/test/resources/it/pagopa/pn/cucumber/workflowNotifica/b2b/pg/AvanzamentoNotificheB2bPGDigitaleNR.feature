Feature: avanzamento b2b notifica  digitale PG con chiamata a National Registry (INAD-IPA-INIPEC)


  @dev @testLite @mockNR
  Scenario: [B2B_TIMELINE_7915_1] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA OK Mock
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | 10959831008      |
      | digitalDomicile | NULL             |
      | recipientType   | PG               |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


  @dev @testLite @mockNR
  Scenario: [B2B_TIMELINE_7915_2] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA KO - INIPEC OK
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | 05370920653      |
      | digitalDomicile | NULL             |
      | recipientType   | PG               |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


  @dev @testLite @mockNR
  Scenario: [B2B_TIMELINE_7915_3] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA KO - INIPEC KO - INAD OK Mock
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | 00883601007      |
      | digitalDomicile | NULL             |
      | recipientType   | PG               |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


  @dev @testLite @mockNR
  Scenario: [B2B_TIMELINE_7915_4] Invio Notifica mono destinatario a PG con recupero del domicilio fisico - Registro Imprese OK Mock
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination            | Test analogico ok         |
      | recipientType           | PG                        |
      | taxId                   | 05722930657               |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @dev @testLite @mockNR
  Scenario: [B2B_TIMELINE_7915_5] Invio Notifica mono destinatario a PG con recupero del domicilio fisico - Registro Imprese non Trovato KO Mock
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination            | Test digitale ok          |
      | recipientType           | PG                        |
      | taxId                   | 00749900049               |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dev @testLite @realNR
  Scenario: [B2B_TIMELINE_7915_5_xxx] Invio Notifica mono destinatario a PG con recupero del domicilio fisico - caso KO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination            | Test digitale ok          |
      | recipientType           | PG                        |
      | taxId                   | 18172040182               |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

