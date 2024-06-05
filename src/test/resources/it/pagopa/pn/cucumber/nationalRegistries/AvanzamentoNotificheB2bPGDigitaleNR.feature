Feature: avanzamento b2b notifica  digitale PG con chiamata a National Registry (INAD-IPA-INIPEC)


  @dev @workflowDigitale @testLite @mockNR
  Scenario: [B2B_TIMELINE_7915_1] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA Trovato - Mock
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


  @dev @workflowDigitale @testLite @mockNR
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


  @dev @workflowDigitale @testLite @mockNR
  Scenario: [B2B_TIMELINE_7915_3] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA non Trovato - INIPEC non Trovato - INAD Trovato - Mock
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


     #OK------->atocaloreirpino@pec.it
  #92051670641 Servizio Reale
  @uat @workflowDigitale @realNR
  Scenario: [B2B_TIMELINE_7597_2_1] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde OK
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PG               |
      | taxId           | 92051670641      |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


     #Controllare ---KO serve una PIVA che va sui servizi reali e dove Fallisce IPA e  INIPEC restituisce la PEC (ATTUALMENTE TUTTE LE PG RESTITUISCONO UN 200 OK PER IPA)..
  @dev @workflowDigitale @ignore
  Scenario: [B2B_TIMELINE_7597_2_4] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde KO e viene fatta chiamata a INIPEC
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PG               |
      | taxId           | 03498760374      |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK---------->aslnapoli1centro@pec.aslna1centro.it
  @uat @workflowDigitale @realNR
  Scenario: [B2B_TIMELINE_7597_2_5] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde OK e non viene fatta chiamata a INIPEC
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PG               |
      | taxId           | 06328131211      |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #3 (MITTENTE) Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde lista vuota e parte chiamata INIPEC
  #Accedere a PN mittente e inviare una notifica mono destinatario PF non inserendo alcun domicilio digitale (ne piattaforma ne speciale)
  #La notifica viene inviata ed è presente in elenco
  #La notifica prosegue per via digitale, in quanto viene trovato l’indirizzo da National Registry

  #OK------------>
  @workflowDigitale  @mockNR
  Scenario: [B2B_TIMELINE_7597_3] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde lista vuota e parte chiamata INIPEC Trovato - Mock
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PG               |
      | taxId           | 70472431207      |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


  @workflowDigitale  @mockNR
  Scenario: [B2B_TIMELINE_7597_3_1] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde lista vuota e parte chiamata INIPEC Real
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PG               |
      | taxId           | 85001990176      |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

    #Controllare ---KO serve una PIVA che va sui servizi reali e dove Fallisce IPA e  INIPEC mentre INAD restituisce la PEC (ATTUALMENTE TUTTE LE PG RESTITUISCONO UN 200 OK PER IPA)..
  @dev @workflowDigitale  @ignore
  Scenario: [B2B_TIMELINE_7597_5] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PG               |
      | taxId           | 13022491008      |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"





