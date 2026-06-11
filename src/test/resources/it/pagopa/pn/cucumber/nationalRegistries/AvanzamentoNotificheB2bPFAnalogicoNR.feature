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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
   # Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
  #  And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "OK" e digitalAddressSource "GENERAL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_ANALOG_76]  Invio notifica mono destinatario a PF analogica con restituzione indirizzo fisico italiano da ANPR - Mock (successo al secondo tentativo)
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | FRMTTR76M06B715E         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline            | true     |
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |
      | details_responseStatus  | KO       |
      | details_physicalAddress | {}       |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline            | true                                                                    |
      | details                 | NOT_NULL                                                                |
      | details_recIndex        | 0                                                                       |
      | details_sentAttemptMade | 1                                                                       |
      | details_responseStatus  | OK                                                                      |
      | details_physicalAddress | {"address": "Via Umbria 5 L", "municipality": "PADOVA", "zip": "35127"} |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" dalla PA "Comune_Multi"

  @workflowAnalogico @realNR @uatEnvCondition
  Scenario: [B2B_TIMELINE_ANALOG_76_1]  PA mittente: invio notifica analogica con restituzione indirizzo fisico italiano da ANPR Real
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | STTSGT90A01H501J         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" dalla PA "Comune_Multi"


  @workflowAnalogico @realNR @uatEnvCondition
  Scenario: [B2B_TIMELINE_ANALOG_76_2]  PA mittente: invio notifica analogica con restituzione indirizzo fisico estero da ANPR Real
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | TTVSGT90A01H501H         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" dalla PA "Comune_Multi"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_ANALOG_76_21]  Invio notifica mono destinatario a PF analogica con restituzione indirizzo fisico estero da ANPR - Mock
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | STRNVC80A01H501A         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED" dalla PA "Comune_Multi"


  @workflowAnalogico @mockNR
  Scenario: [B2B_TIMELINE_ANALOG_76_3]  Invio notifica mono destinatario a PF analogica con restituzione indirizzo fisico italiano non trovato da ANPR - Mock
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | FNTLCU80T25F205R         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @workflowAnalogico @realNR
  Scenario: [B2B_TIMELINE_ANALOG_76_4]  PA mittente: invio notifica analogica con restituzione indirizzo fisico italiano non trovato da ANPR Real
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @workflowAnalogico @mockNR
  Scenario: [B2B-TEST_1] Invio notifica mono destinatario a PF in stato “irreperibile totale” INAD non Trovato - Mock
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di MILANO                |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @workflowAnalogico @realNR
  Scenario: [B2B-TEST_1_1] Invio Notifica mono destinatario a PF con recupero del domicilio digitale  INAD Real KO
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di MILANO                |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

#  1. CF: XVRPLN98S16L781X  -> campo Via/Indirizzo non valorizzato;
#  2. CF: XVRCSR87M15L781X  -> campo Città/Località non valorizzato;
#  3. CF: XVRSFN76E31L781N  -> campi Via/Indirizzo e Città/Località non valorizzati.

#indirizzi esteri:
#  1. XXIFBN99A01D612K no address e municipality
#  2. XVRLVC90A01H501P no address
#  3. XVRGPL80A01L781A no municipality

#  ANPR REALE Italiano / no address KRSJSM88S03H501A

  @workflowAnalogico @mockNR @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_1] AR-Validazione sulla deduplica al secondo tentativo con VIA non valorizzato-nazionale
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Matteo Rossi             |
      | taxId                   | XVRPLN98S16L781X         |
      | recipientType           | PF                       |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE_CREATION_REQUEST"

  @workflowAnalogico @mockNR @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_2] AR-Validazione sulla deduplica al secondo tentativo con citta non valorizzato-nazionale
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Matteo Rossi             |
      | taxId                   | XVRCSR87M15L781X         |
      | recipientType           | PF                       |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE_CREATION_REQUEST"

  @workflowAnalogico @mockNR @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_3] AR-Validazione sulla deduplica al secondo tentativo con citta e via non valorizzati-nazionale
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Matteo Rossi             |
      | taxId                   | XVRSFN76E31L781N         |
      | recipientType           | PF                       |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE_CREATION_REQUEST"

  @workflowAnalogico @mockNR @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_B] Multidestinatario Validazione sulla deduplica al secondo tentativo con campo mancante
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | Matteo Rossi              |
      | taxId                   | XVRPLN98S16L781X          |
      | recipientType           | PF                        |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | TSTGNN80A01F839X |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 1

  @workflowAnalogico @mockNR @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_C] 890 - Validazione sulla deduplica al secondo tentativo con VIA non valorizzato
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | Matteo Rossi              |
      | taxId                   | XVRPLN98S16L781X          |
      | recipientType           | PF                        |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"

  @workflowAnalogico @mockNR @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_5] Validazione sulla deduplica al secondo tentativo con VIA non valorizzato-estero
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | Matteo Rossi              |
      | taxId                   | XXIFBN99A01D612K          |
      | recipientType           | PF                        |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE_CREATION_REQUEST"

  @workflowAnalogico @mockNR @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_6] Validazione sulla deduplica al secondo tentativo con citta non valorizzato-estero
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | Matteo Rossi              |
      | taxId                   | XVRLVC90A01H501P          |
      | recipientType           | PF                        |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE_CREATION_REQUEST"

  @workflowAnalogico @mockNR @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_7] Validazione sulla deduplica al secondo tentativo con citta e via non valorizzati-estero
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | Matteo Rossi              |
      | taxId                   | XVRGPL80A01L781A          |
      | recipientType           | PF                        |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE_CREATION_REQUEST"


  # seguono test sulla validazione normalizzatore batch -> NECESSARIO VAS ATTIVO

  @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_8] Validazione sulla deduplica VAS indirizzo nazionale
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_AR        |
    And destinatario
      | denomination    | PF Censito campi-mancanti |
      | taxId           | XVRPLN98S16L781X          |
      | digitalDomicile | NULL                      |
      | physicalAddress | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                 |
      | details                | NOT_NULL                             |
      | details_refusalReasons | [{"errorCode": "NOT_VALID_ADDRESS"}] |

  @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_9] Validazione sulla deduplica VAS indirizzo nazionale
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_AR        |
    And destinatario
      | denomination    | PF Censito campi-mancanti |
      | taxId           | XVRCSR87M15L781X          |
      | digitalDomicile | NULL                      |
      | physicalAddress | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                 |
      | details                | NOT_NULL                             |
      | details_refusalReasons | [{"errorCode": "NOT_VALID_ADDRESS"}] |

  @validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_10] Validazione sulla deduplica VAS indirizzo nazionale
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_AR        |
    And destinatario
      | denomination    | PF Censito campi-mancanti |
      | taxId           | XVRSFN76E31L781N          |
      | digitalDomicile | NULL                      |
      | physicalAddress | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                 |
      | details                | NOT_NULL                             |
      | details_refusalReasons | [{"errorCode": "NOT_VALID_ADDRESS"}] |

  #@validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_11] Validazione sulla deduplica VAS indirizzo estero
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_AR        |
    And destinatario
      | denomination    | PF Censito campi-mancanti |
      | taxId           | XVRGPL80A01L781A          |
      | digitalDomicile | NULL                      |
      | physicalAddress | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                 |
      | details                | NOT_NULL                             |
      | details_refusalReasons | [{"errorCode": "NOT_VALID_ADDRESS"}] |

  #@validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_12] Validazione sulla deduplica VAS indirizzo estero
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_AR        |
    And destinatario
      | denomination    | PF Censito campi-mancanti |
      | taxId           | XVRLVC90A01H501P          |
      | digitalDomicile | NULL                      |
      | physicalAddress | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                 |
      | details                | NOT_NULL                             |
      | details_refusalReasons | [{"errorCode": "NOT_VALID_ADDRESS"}] |

  #@validazioneDeduplica
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_13] Validazione sulla deduplica VAS indirizzo estero
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_AR        |
    And destinatario
      | denomination    | PF Censito campi-mancanti |
      | taxId           | XXIFBN99A01D612K          |
      | digitalDomicile | NULL                      |
      | physicalAddress | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then viene verificato che l'elemento di timeline "REQUEST_REFUSED" esista
      | loadTimeline           | true                                 |
      | details                | NOT_NULL                             |
      | details_refusalReasons | [{"errorCode": "NOT_VALID_ADDRESS"}] |


#  seguono test sulla deduplica su ANPR reale - solo ambiente UAT

  @validazioneDeduplicaUAT @realNR
  Scenario: [B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_IT_NAD_UAT] Validazione sulla deduplica al secondo tentativo con citta non valorizzato-estero-ANPR reale
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | Matteo Rossi              |
      | taxId                   | KRSJSM88S03H501A          |
      | recipientType           | PF                        |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE_FAILURE" con failureCause "D01"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE_CREATION_REQUEST"


