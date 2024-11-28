Feature: avanzamento b2b notifica PF analogico con chiamata a National Registry (INAD-IPA-INIPEC)
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


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_7597_1_3] Invio Notifica mono destinatario a PF con recupero del domicilio digitale - INAD Scaduto - Mock
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | TSTGNN80A01F839X |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
   # Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
  #  And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_ANALOG_76]  Invio notifica  mono destinatario a PF analogica  con restituzione indirizzo fisico italiano da ANPR - Mock
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | FRMTTR76M06B715E |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" dalla PA "Comune_Multi"

  @workflowAnalogico @realNR @uatEnvCondition
  Scenario: [B2B_TIMELINE_ANALOG_76_1]  PA mittente: invio notifica analogica con restituzione indirizzo fisico italiano da ANPR Real
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | STTSGT90A01H501J |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" dalla PA "Comune_Multi"


  @workflowAnalogico @realNR @uatEnvCondition
  Scenario: [B2B_TIMELINE_ANALOG_76_2]  PA mittente: invio notifica analogica con restituzione indirizzo fisico estero da ANPR Real
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TTVSGT90A01H501H |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" dalla PA "Comune_Multi"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_ANALOG_76_21]  Invio notifica mono destinatario a PF analogica con restituzione indirizzo fisico estero da ANPR - Mock
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | STRNVC80A01H501A |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" dalla PA "Comune_Multi"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_ANALOG_76_3]  Invio notifica mono destinatario a PF analogica con restituzione indirizzo fisico italiano non trovato da ANPR - Mock
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | FNTLCU80T25F205R |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @workflowAnalogico @realNR
  Scenario: [B2B_TIMELINE_ANALOG_76_4]  PA mittente: invio notifica analogica con restituzione indirizzo fisico italiano non trovato da ANPR Real
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | NNTNRZ80A01H501D |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"



  @workflowAnalogico @mockNR
  Scenario: [B2B-TEST_1] Invio notifica mono destinatario a PF in stato “irreperibile totale” INAD non Trovato - Mock
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di MILANO |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | MNTMRA03M71C615V |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @workflowAnalogico @realNR
  Scenario: [B2B-TEST_1_1] Invio Notifica mono destinatario a PF con recupero del domicilio digitale  INAD Real KO
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di MILANO |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | NNTNRZ80A01H501D |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

