Feature: Gestione puntuale per singolo CF degli esiti delle richieste in batch verso INIPEC


  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_1_A] Come PA invio una notifica verso una PG per il quale si ha una chiamata al registro INIPEC,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 19854070638 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la richiesta verso il servizio INIPEC per il cf "19854070638" viene tracciata correttamente

  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_2_A] Come PA invio una notifica verso una PF per il quale si ha una chiamata al registro INIPEC,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | GNFRSS80A01G203W |
      | digitalDomicile | NULL             |
      | recipientType   | PG               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la risposta sul registro INIPEC per il cf "GNFRSS80A01G203W" risulta registrata con esito "ND"

  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_2_B] Come PA invio una notifica verso una PF per il quale si ha una chiamata al registro INIPEC,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | MRGVPC67R10H501Y |
      | digitalDomicile | NULL             |
      | recipientType   | PG               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la risposta sul registro INIPEC per il cf "MRGVPC67R10H501Y" risulta registrata con esito "NF"

  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_3_A] Come PA invio una notifica verso una PF per il quale si ha una chiamata al registro INIPEC,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | TSTGNN80A01F839X |
      | digitalDomicile | NULL             |
      | recipientType   | PG               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la risposta sul registro INIPEC per il cf "TSTGNN80A01F839X" risulta registrata con esito "ER"
    Then il ritentativo della chiamata verso INIPEC per il cf "TSTGNN80A01F839X" risulta correttamente registrato

  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_1_B] Come PA invio una notifica verso una PF per il quale si ha una chiamata al registro INIPEC,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | CTNMCP34B16H501T |
      | digitalDomicile | NULL             |
      | recipientType   | PG               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la richiesta verso il servizio INIPEC per il cf "CTNMCP34B16H501T" viene tracciata correttamente



#trova pg con nf er su inipec todo


  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true;
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_02_1_A] Come PA invio una notifica verso una PF per il quale si ha una chiamata al registro INAD,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | PPPPLT80A01H501V |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then la richiesta verso il servizio INAD per il cf "PPPPLT80A01H501V" viene tracciata correttamente


  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true;EnableOldInipecFlow:false
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_02_1_B] Come PA invio una notifica verso una PG per il quale si ha una chiamata al registro INAD,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 28223913097 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then la richiesta verso il servizio INIPEC per il cf "28223913097" viene tracciata correttamente
    Then la richiesta verso il servizio INAD per il cf "28223913097" viene tracciata correttamente


  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_03_1_A] Come PA invio una notifica verso una PF per il quale si ha un errore 500 sulla chiamata al registro INAD,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | CIACIA80A01H501X |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then il ritentativo della chiamata verso INAD per il cf "CIACIA80A01H501X" risulta correttamente registrato


  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_03_1_B1] Come PA invio una notifica verso una PG per il quale si ha un errore 500 sulla chiamata al registro INAD,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 62583510995 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then il ritentativo della chiamata verso INAD per il cf "62583510995" risulta correttamente registrato

  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_03_1_A2] Come PA invio una notifica verso una PF per il quale si ha un errore lista non trovata sulla chiamata al registro INAD,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | VDADGI80A01F839E |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then il ritentativo della chiamata verso INAD per il cf "VDADGI80A01F839E" risulta correttamente registrato

  @inadMassive @mockNR @work-item:QA-18515
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_03_1_A3] Come PA invio una notifica verso una PF per il quale si ha un errore 400 sulla chiamata al registro INAD,
  quindi la richiesta termina in errore senza retry.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | MTTBNN14A01A001N |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then la richiesta verso il servizio INAD per il cf "MTTBNN14A01A001N" risulta in errore




  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true;EnableOldInipecFlow:false
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_04_1_A] Come PA invio una notifica verso una PF per il quale si ha una chiamata al registro INAD senza recuperare indirizzi,
  la richiesta viene completata e tracciata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | CLMCST42R12D969Z |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then l'assenza di recapiti su INAD per il cf "CLMCST42R12D969Z" risulta correttamente registrata


  @inadMassive @mockNR @work-item:QA-18515
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_04_1_B] Come PA invio una notifica verso una PF per il quale si ha una chiamata al registro INAD senza recuperare indirizzi,
  la richiesta è riprogrammata e tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 85385800199 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then l'assenza di recapiti su INIPEC per il cf "85385800199" risulta correttamente registrata
    Then l'assenza di recapiti su INAD per il cf "85385800199" risulta correttamente registrata


  @inadMassive @mockNR @work-item:QA-18515
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_05_1] Come PA invio una notifica verso una PF per il quale si ha una chiamata al registro INAD.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | todo |
      | digitalDomicile | NULL |
      | recipientType   | PF   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then l'assenza di recapiti su INAD per il cf "todo" risulta correttamente registrata
    Then la richiesta verso il servizio INIPEC per il cf "todo" viene tracciata correttamente


  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true;EnableOldInipecFlow:true
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_06_1] Come PA invio una notifica verso una PF per il quale si ha una chiamata al registro INAD.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | todo |
      | digitalDomicile | NULL |
      | recipientType   | PG   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then la richiesta INIPEC per il cf "todo" risulta INVIATA al primo tentativo
    Then la richiesta verso il servizio INAD per il cf "todo" viene tracciata correttamente



  # COSA VERIFICHIAMO
  # Una notifica verso una PG priva di domicilio digitale fa interrogare INIPEC per recuperarne
  # la PEC. L'esito per ciascun CF viene tracciato su DynamoDB `pn-batchRequests` tramite i campi
  # `sendStatus` e `retry`. Gli scenari controllano che, a fronte di esiti INIPEC noti (pilotati da
  # CF "magici" del mock), il record finisca nello stato atteso. Mappatura esito -> stato su DB:
  #
  #   "risulta INVIATA al primo tentativo"        -> sendStatus = SENT,     retry = 0
  #   "risulta RIMESSA in retry"                  -> sendStatus = NOT_SENT,  retry > 0
  #   "non risulta alcuna richiesta INIPEC"       -> nessun record (né SENT né NOT_SENT)
  #

  @inipecGestionePuntuale @inipecParallel @mockNR
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_02.1] Notifica verso due PG con esiti errore temporaneo e domicilio trovato rispettivamente nel recupero del domicilio digitale presso INIPEC nuova versione
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 00845483379 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    # 17492068394 -> esito INIPEC atteso: statoImpresa=ER
    And destinatario
      | taxId           | 17492068394 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la richiesta INIPEC per il cf "00845483379" risulta INVIATA al primo tentativo
    Then la richiesta INIPEC per il cf "17492068394" risulta RIMESSA in retry


  @inipecGestionePuntuale @inipecParallel @mockNR
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_01] Notifica verso due PG con esiti posizione non trovata e posizione senza pec associata rispettivamente nel recupero del domicilio digitale presso INIPEC
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    # 39274018561 -> esito INIPEC atteso: "statoImpresa=ND
    And destinatario
      | taxId           | 39274018561 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    # 83016259471 -> esito INIPEC atteso: statoImpresa=NF
    And destinatario
      | taxId           | 83016259471 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la richiesta INIPEC per il cf "39274018561" risulta INVIATA al primo tentativo
    Then la richiesta INIPEC per il cf "83016259471" risulta INVIATA al primo tentativo


  @inipecGestionePuntuale @inipecIsolato @mockNR
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_02.2] Notifica verso due PG con esiti errore temporaneo e domicilio trovato rispettivamente nel recupero del domicilio digitale presso INIPEC vecchia versione
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 29527800386 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    # 10433218194 -> esito INIPEC atteso: se nel batch è contenuto questo CF numerico tutto il batch andrà in errore e non ci sarà la gestione puntuale per singolo codice fiscale
    And destinatario
      | taxId           | 10433218194 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then per i cf "29527800386" e "10433218194" non risulta alcuna richiesta INIPEC


  @inipecGestionePuntuale @mockNR
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_HF_01] Notifica multidestinatario di cui uno non recupera pec su inipec e quindi avrà fallback su inad
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | denomination    | Test digitale fallback INAD |
      | recipientType   | PG                          |
      | taxId           | 28223913097                 |
      | digitalDomicile | NULL                        |
    And destinatario
      | taxId           | 00845483379 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then risulta una richiesta INAD per il cf "28223913097"
