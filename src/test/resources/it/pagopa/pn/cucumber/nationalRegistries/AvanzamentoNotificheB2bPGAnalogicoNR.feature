Feature: avanzamento b2b notifica PG analogico con chiamata a National Registry (INAD-IPA-INIPEC)
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



  @dev @workflowAnalogico @testLite @mockNR
  Scenario: [B2B_TIMELINE_7915_4] Invio Notifica mono destinatario a PG con recupero del domicilio fisico - Registro Imprese Trovato - Mock
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


  @dev @workflowAnalogico @testLite @mockNR
  Scenario: [B2B_TIMELINE_7915_5] Invio Notifica mono destinatario a PG con recupero del domicilio fisico - Registro Imprese non Trovato - Mock
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

  @dev @workflowAnalogico @testLite @realNR
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



  @workflowAnalogico @mockNR
  Scenario: [B2B-TEST_1_2] Invio Notifica mono destinatario a PG in stato “irreperibile totale” PG IPA non Trovato INFO non Trovato INAD non Trovato - Mock
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di MILANO |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | 05722930657 |
      | digitalDomicile | NULL |
      | recipientType   | PG                 |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"