Feature: avanzamento b2b notifica PF  difgitale con chiamata a National Registry (INAD-IPA-INIPEC)

  @uat @workflowDigitale @realNR
  Scenario: [B2B_TIMELINE_7597_1] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - INAD Real OK
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | TRVVCN73H02L259I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @workflowDigitale @mockNR
  Scenario: [B2B_TIMELINE_7597_1_1] Invio Notifica mono destinatario a PF con recupero del domicilio digitale – INAD Trovato - Mock
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | JHKRFU96H15F068N |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @workflowDigitale @mockNR
  Scenario: [B2B_TIMELINE_7597_1_2] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - INAD Mock KO
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | JPCRPP78D43F165N |
      | digitalDomicile | NULL             |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  #2 (MITTENTE) Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA risponde OK
  #Accedere a PN mittente e inviare una notifica mono destinatario PF non inserendo alcun domicilio digitale (ne piattaforma ne speciale)
  #La notifica viene inviata ed è presente in elenco
  #La notifica prosegue per via digitale, in quanto viene trovato l’indirizzo da National Registry
  #IPA puo essere chiamata per recuperare il domicilio digitale di una PF ????
  #VMEZ-JEPN-JGDH-202309-Q-1 ----Controllare KO verificare se esiste un CF per PF per accedere IPA servizio reale....
  #Mock PPPPLT80A01H501V

  #OK------------>
  @workflowDigitale @mockNR #da non considerare una volta che liberiProfessionisti ha il flag a true
  Scenario: [B2B_TIMELINE_7597_2] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA risponde OK
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | DRCMRA80A01H501L |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @liberiProfessionisti # scenario 10
  Scenario: [B2B_TIMELINE_RECAPITI_PF_3] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA risponde OK
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | DRCMRA80A01H501L |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "esempio@pec.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK------------>
  @workflowDigitale @mockNR
  Scenario: [B2B_TIMELINE_7597_2_2] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde OK
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PF               |
      | taxId           | PRVMNL80A01F205M |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK------------>
  @workflowDigitale @mockNR # da eliminare per lo scenario 1 quando il feature flag di liberi professionisti è a true
  Scenario: [B2B_TIMELINE_7597_2_3] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA risponde KO e viene fatta chiamata a INIPEC
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PF               |
      | taxId           | DRCMRA80A01H501L |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  @mockNR @liberiProfessionisti # scenario 1
  Scenario: [B2B_TIMELINE_RECAPITI_PF_4] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA risponde KO e viene fatta chiamata a INIPEC
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PF               |
      | taxId           | DRCMRA80A01H501L |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "esempio@pec.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

 #4 (MITTENTE) Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
  #Accedere a PN mittente e inviare una notifica mono destinatario PF non inserendo alcun domicilio digitale (ne piattaforma ne speciale)
  #La notifica viene inviata ed è presente in elenco
  #La notifica prosegue per via digitale, in quanto viene trovato l’indirizzo da National Registry

 #Esempio: CF RMSLSO31M04Z404R (mock server)
 # La notifica prosegue per via digitale, in quanto viene trovato l’indirizzo da National Registry
  #OK------------>
  @workflowDigitale @mockNR
  Scenario: [B2B_TIMELINE_7597_4] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | RMSLSO31M04Z404R |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK------------>test@pec.it
  @uat @workflowDigitale @realNR
  Scenario: [B2B_TIMELINE_7597_4_1] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | FRNGRG88A64A794S |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
