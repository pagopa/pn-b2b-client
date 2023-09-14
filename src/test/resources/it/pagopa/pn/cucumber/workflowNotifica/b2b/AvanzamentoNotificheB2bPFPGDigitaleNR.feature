Feature: avanzamento b2b notifica  difgitale con chiamata a National Registry (INAD-IPA-INIPEC)
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
  @dev @workflowDigitale @servizioReale
  Scenario: [B2B_TIMELINE_7597_1] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - INAD OK
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Test digitale ok |
      | taxId | CRBFNC95E66G337I  |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"

  #OK------------>
  @dev @workflowDigitale @mock
  Scenario: [B2B_TIMELINE_7597_1_1] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - INAD OK
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
  @dev @workflowDigitale @mock
  Scenario: [B2B_TIMELINE_7597_2] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA risponde OK
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
  @dev @workflowDigitale @servizioReale
  Scenario: [B2B_TIMELINE_7597_2_1] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde OK
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
  @dev @workflowDigitale @mock
  Scenario: [B2B_TIMELINE_7597_2_2] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde OK
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
  @dev @workflowDigitale @mock
  Scenario: [B2B_TIMELINE_7597_2_3] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA risponde KO e viene fatta chiamata a INIPEC
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
  Scenario: [B2B_TIMELINE_7597_2_4] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde KO e viene fatta chiamata a INIPEC
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
  @dev @workflowDigitale @servizioReale
  Scenario: [B2B_TIMELINE_7597_2_5] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde OK e non viene fatta chiamata a INIPEC
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
  @dev @workflowDigitale  @mock
  Scenario: [B2B_TIMELINE_7597_3] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA risponde lista vuota e parte chiamata INIPEC
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
  @dev @workflowDigitale @mock
  Scenario: [B2B_TIMELINE_7597_4] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
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
  Scenario: [B2B_TIMELINE_7597_5] Invio Notifica mono destinatario a PG con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
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
  @dev @workflowDigitale @servizioReale
  Scenario: [B2B_TIMELINE_7597_4_1] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - IPA e INIPEC risponde lista vuota e parte chiamata INAD
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
