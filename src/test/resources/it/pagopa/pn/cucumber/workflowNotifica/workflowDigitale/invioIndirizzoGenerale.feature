Feature: avanzamento b2b notifica difgitale con indirizzo generale con chiamata a National Registry (INAD-IPA-INIPEC)

  #DOMICILIO DIGITALE:
  #PF ---> INAD
  #PG ---> IPA/INIPEC

  #DOMICILIO FISICO:
  #PF ---> ANPR
  #PG ---> REGISTRO DELLE IMPRESE

  #1 (MITTENTE) Invio Notifica mono destinatario a PF con recupero del domicilio digitale - INAD OK
  #Accedere a PN mittente e inviare una notifica mono destinatario PF non inserendo alcun domicilio digitale (ne piattaforma ne speciale)
  #La notifica viene inviata ed è presente in elenco
  #La notifica prosegue per via digitale, in quanto viene trovato l’indirizzo da National Registry

  #Recupero del domicilio digitale di una Persona Fisica (INAD)
  #Inviare una notifica in ambiente UAT al destinatario (PF) con CF: MDEPLG67E41Z354G, inserendo come domicilio digitale la keyword “@fail.it"

  #Risultato:  La notifica viene inviata correttamente al domicilio digitale corrispondente al destinatario inserito (test@pec.it), recuperato tramite INAD.
 #OK---------->
  @uat @workflowDigitale @realNR
  #[B2B_TIMELINE_7597_1]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_1] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - INAD OK
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | taxId | TRVVCN73H02L259I  |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK------------>
  @workflowDigitale @mockNR
  #[B2B_TIMELINE_7597_1_1]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_2] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - INAD OK
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | taxId | JHKRFU96H15F068N  |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #2 (MITTENTE) Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA risponde OK
  #Accedere a PN mittente e inviare una notifica mono destinatario PF non inserendo alcun domicilio digitale (ne piattaforma ne speciale)
  #La notifica viene inviata ed è presente in elenco
  #La notifica prosegue per via digitale, in quanto viene trovato l’indirizzo da National Registry
  #IPA puo essere chiamata per recuperare il domicilio digitale di una PF ????
  #VMEZ-JEPN-JGDH-202309-Q-1 ----Controllare KO verificare se esiste un CF per PF per accedere IPA servizio reale....
  #Mock PPPPLT80A01H501V

  #OK------------>
  @workflowDigitale @mockNR
  #[B2B_TIMELINE_7597_2]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_3]  Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA risponde OK
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | taxId | PPPPLT80A01H501V |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK------->atocaloreirpino@pec.it
  #92051670641 Servizio Reale
  @uat @workflowDigitale @realNR
  #[B2B_TIMELINE_7597_2_1]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_4] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde OK
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | recipientType    | PG               |
      | taxId | 92051670641 |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK------------>
  @workflowDigitale @mockNR
  #[B2B_TIMELINE_7597_2_2]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_5] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde OK
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | recipientType    | PF               |
      | taxId | PRVMNL80A01F205M |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK------------>
  @workflowDigitale @mockNR
  #[B2B_TIMELINE_7597_2_3]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_6] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA risponde KO e viene fatta chiamata a INIPEC
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | recipientType    | PF               |
      | taxId | CTNMCP34B16H501T |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #Controllare ---KO serve una PIVA che va sui servizi reali e dove Fallisce IPA e  INIPEC restituisce la PEC (ATTUALMENTE TUTTE LE PG RESTITUISCONO UN 200 OK PER IPA)..
  @dev @ignore
  #[B2B_TIMELINE_7597_2_4]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_7] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde KO e viene fatta chiamata a INIPEC
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | recipientType    | PG               |
      | taxId | 03498760374 |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK---------->aslnapoli1centro@pec.aslna1centro.it
  @uat @workflowDigitale @realNR
  #[B2B_TIMELINE_7597_2_5]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_8] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde OK e non viene fatta chiamata a INIPEC
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | recipientType    | PG               |
      | taxId | 06328131211 |
      | digitalDomicile         | NULL      |
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
  #[B2B_TIMELINE_7597_3]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_9] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde lista vuota e parte chiamata INIPEC
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | recipientType    | PG              |
      | taxId | 70472431207                |
      | digitalDomicile         | NULL     |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

 #4 (MITTENTE) Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
  #Accedere a PN mittente e inviare una notifica mono destinatario PF non inserendo alcun domicilio digitale (ne piattaforma ne speciale)
  #La notifica viene inviata ed è presente in elenco
  #La notifica prosegue per via digitale, in quanto viene trovato l’indirizzo da National Registry

 #Esempio: CF RMSLSO31M04Z404R (mock server)
 # La notifica prosegue per via digitale, in quanto viene trovato l’indirizzo da National Registry

  #OK------------>
  @workflowDigitale @mockNR
  #[B2B_TIMELINE_7597_4]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_10] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | taxId | RMSLSO31M04Z404R |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #Controllare ---KO serve una PIVA che va sui servizi reali e dove Fallisce IPA e  INIPEC mentre INAD restituisce la PEC (ATTUALMENTE TUTTE LE PG RESTITUISCONO UN 200 OK PER IPA)..

  @dev  @ignore
  #[B2B_TIMELINE_7597_5]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_11] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | recipientType    | PG               |
      | taxId | 13022491008                 |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK------------>test@pec.it
  @uat @workflowDigitale @realNR
  #[B2B_TIMELINE_7597_4_1]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_12] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | taxId | FRNGRG88A64A794S |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"


  @dev @mockNR
  #[B2B_TIMELINE_23]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_13] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE sia presente il campo Digital Address scenario positivo PN-5992
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
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_14] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE sia presente il campo Digital Address scenario positivo PN-5992
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | digitalDomicile_address | test@fail.it |
      | taxId | TRVVCN73H02L259 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry


  @dev
  #[B2B_TIMELINE_PG_16]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_15] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE sia presente il campo Digital Address scenario positivo PN-5992
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry

  @dev
  #[B2B_TIMELINE_PG_12]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_16] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_CALL_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"

  @dev @ignore
  #[B2B_TIMELINE_PG_13]
  Scenario: [B2B_TIMELINE_DIGITAL_GENERAL_17] Invio notifica digitale ed attesa elemento di timeline PUBLIC_REGISTRY_RESPONSE_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"