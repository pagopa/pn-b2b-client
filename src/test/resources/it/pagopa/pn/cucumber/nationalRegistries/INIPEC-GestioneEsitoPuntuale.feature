Feature: Gestione puntuale per singolo CF degli esiti delle richieste in batch verso INIPEC


  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_1_A] Come PA invio una notifica verso una PG senza specificare un recapito digitale,
  quindi viene fatta una chiamata verso INIPEC per recuperarlo,
  la richiesta verso i registri è tracciata nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 19854070638 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la richiesta verso il servizio INIPEC per il cf "19854070638" risulta in stato WORKED
    And il batch associato alla richiesta verso INIPEC per il cf "19854070638" risulta in stato WORKED

  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false liberi-professionisti:on
  Scenario: [INIPEC_GESTIONE_ESITO_01_1_B] Come PA invio una notifica verso una PF per il quale si ha una chiamata al registro INIPEC,
  quindi la richiesta è tracciata nella tabella dedicata.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | PRPMGG80A41H501W |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la richiesta verso il servizio INAD per il cf "PRPMGG80A41H501W" risulta in stato WORKED
    And il batch associato alla richiesta verso INAD per il cf "PRPMGG80A41H501W" risulta in stato WORKED
    And la richiesta verso il servizio INIPEC per il cf "PRPMGG80A41H501W" risulta in stato WORKED
    And il batch associato alla richiesta verso INIPEC per il cf "PRPMGG80A41H501W" risulta in stato WORKED

  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_2_A] Come PA invio una notifica verso una PF senza specificare un recapito digitale,
  quindi viene fatta una chiamata verso INAD per recuperarlo,
  la richiesta verso i registri è tracciata nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | MLTMRA80A01H501Z |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la risposta sul registro INAD per il cf "MLTMRA80A01H501Z" non contiene recapiti
    And il batch associato alla richiesta verso INAD per il cf "MLTMRA80A01H501Z" risulta in stato WORKED

  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_2_B] Come PA invio una notifica verso una PF senza specificare un recapito digitale,
  quindi viene fatta una chiamata verso INIPEC per recuperarlo,
  non viene trovato nessun recapito per il destinatario,
  la richiesta e l'esito verso i registri sono tracciata nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | PRPMGG80A41H501W |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la risposta sul registro INAD per il cf "PRPMGG80A41H501W" non contiene recapiti
    And il batch associato alla richiesta verso INAD per il cf "PRPMGG80A41H501W" risulta in stato WORKED

  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_2_A2] Come PA invio una notifica verso una PG senza specificare un recapito digitale,
  quindi viene fatta una chiamata verso INIPEC per recuperarlo,
  non viene trovato nessun recapito per il destinatario,
  la richiesta e l'esito verso i registri sono tracciata nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 11109120292 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la risposta sul registro INIPEC per il cf "11109120292" non contiene recapiti
    And il batch associato alla richiesta verso INIPEC per il cf "11109120292" risulta in stato WORKED

  @inadMassive @mockNR @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_2_B2] Come PA invio una notifica verso una PG senza specificare un recapito digitale,
  quindi viene fatta una chiamata verso INIPEC per recuperarlo,
  non viene trovato nessun recapito per il destinatario,NF
  la richiesta e l'esito verso i registri sono tracciata nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 83016259471 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la risposta sul registro INIPEC per il cf "83016259471" non contiene recapiti
    And il batch associato alla richiesta verso INIPEC per il cf "83016259471" risulta in stato WORKED

  @inadMassive @mockNR @ignoreUAT @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_3_A] Come PA invio una notifica verso una PF senza specificare un recapito digitale, quindi viene fatta una chiamata verso INIPEC per recuperarlo,
  in risposta dal mock si avrà come risposta una pec professionale l'esito sono tracciata nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | TSTGNN80A01F839X |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la richiesta verso il servizio INAD per il cf "TSTGNN80A01F839X" risulta in stato WORKED
    And il batch associato alla richiesta verso INAD per il cf "TSTGNN80A01F839X" risulta in stato WORKED

  @inadMassive @mockNR @ignoreUAT @work-item:QA-18515 #EnableOldInipecFlow:false
  Scenario: [INIPEC_GESTIONE_ESITO_01_3_A2] Come PA invio una notifica verso una PG senza specificare un recapito digitale, quindi viene fatta una chiamata verso INIPEC per recuperarlo,
  in risposta dal mock si avrà un errore tecnico, la richiesta viene predisposta per un ulteriore tentativo,
  la richiesta e l'esito sono tracciata nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 17492068394 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And il retry della richiesta verso INIPEC per il cf "17492068394" risulta correttamente registrato

  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true;
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_02_1_A] Come PA invio una notifica verso una PF senza specificare un recapito digitale, quindi viene fatta una chiamata verso INAD per recuperarlo,
  la richiesta e l'esito della chiamata verso i registri sono tracciati nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | RSSJLN04B01D086S |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la richiesta verso il servizio INAD per il cf "RSSJLN04B01D086S" risulta in stato WORKED
    And il batch associato alla richiesta verso INAD per il cf "RSSJLN04B01D086S" risulta in stato WORKED

  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true;EnableOldInipecFlow:false
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_02_1_B] Come PA invio una notifica verso una PF senza specificare un recapito digitale, quindi viene fatta una chiamata verso INIPEC per recuperarlo,
  non è presente recapito su INIPEC quindi si ha la chiamata verso INAD.
  le richieste e gli esiti delle chiamate verso i registri sono tracciate nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 28223913097 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la richiesta verso il servizio INIPEC per il cf "28223913097" risulta in stato WORKED
    And il batch associato alla richiesta verso INIPEC per il cf "28223913097" risulta in stato WORKED
    And la richiesta verso il servizio INAD per il cf "28223913097" risulta in stato WORKED
    And il batch associato alla richiesta verso INAD per il cf "28223913097" risulta in stato WORKED


  @inadMassive @mockNR @ignoreUAT @work-item:QA-18515 #EnableInadMassiveApi:true
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_03_1_A] Come PA invio una notifica verso una PF senza specificare un recapito digitale, quindi viene fatta una chiamata verso INAD per recuperarlo,
  in risposta il mock restituisce un errore 500 sul polling, viene predisposto un ulteriore tentativo verso INAD,
  le richieste e gli esiti delle chiamate verso i registri sono tracciate nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | UBHXPB21T15M096C |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la richiesta verso il servizio INAD per il cf "UBHXPB21T15M096C" risulta in errore


  @inadMassive @mockNR @ignoreUAT @work-item:QA-18515 #EnableInadMassiveApi:true
   Scenario: [INAD_MASSIVE_GESTINE_ESITO_03_1_B1] Come PA invio una notifica verso una PG senza specificare un recapito digitale, quindi
  viene fatta una chiamata verso INIPEC con esito negativo e poi verso INAD per recuperarlo,
  in risposta il mock restituisce un errore 500 sul batc, viene predisposto un ulteriore tentativo,
  le richieste e gli esiti delle chiamate verso i registri sono tracciate nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | TYXYHK14A01A001Q |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And il retry della richiesta verso INAD per il cf "TYXYHK14A01A001Q" risulta correttamente registrato

  @inadMassive @mockNR @ignoreUAT @work-item:QA-18515 #EnableInadMassiveApi:true
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_03_1_A2] Come PA invio una notifica verso una PF senza specificare un recapito digitale, quindi
  viene fatta una chiamata verso INAD per recuperarlo,
  in risposta il mock restituisce un errore 400 sul polling, viene predisposto un ulteriore tentativo,
  le richieste e gli esiti delle chiamate verso i registri sono tracciate nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | BQMRRJ02E05P052P |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And il ritentativo della chiamata verso INAD per il cf "BQMRRJ02E05P052P" risulta correttamente registrato

  @inadMassive @mockNR @ignoreUAT @work-item:QA-18515 #EnableInadMassiveApi:true
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_03_1_A3] Come PA invio una notifica verso una PF senza specificare un recapito digitale, quindi
  viene fatta una chiamata verso INAD per recuperarlo,
  in risposta il mock restituisce un errore 400 sul batch, la richiesta risulterà con stato: in errore.,
  le richieste e gli esiti delle chiamate verso i registri sono tracciate nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | MTTBNN14A01A001N |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la richiesta verso il servizio INAD per il cf "MTTBNN14A01A001N" risulta in errore


  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true;EnableOldInipecFlow:false
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_04_1_A] Come PA invio una notifica verso una PF senza specificare un recapito digitale, quindi
  viene fatta una chiamata verso INAD senza recuperare indirizzi,
  le richieste e gli esiti delle chiamate verso i registri sono tracciate nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | CLMCST42R12D969Z |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la risposta sul registro INAD per il cf "CLMCST42R12D969Z" non contiene recapiti
    And il batch associato alla richiesta verso INAD per il cf "CLMCST42R12D969Z" risulta in stato WORKED

  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true;EnableOldInipecFlow:false liberi-professionisti:true
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_04_1_B] Come PA invio una notifica verso una PG senza specificare un recapito digitale, quindi
  viene fatta una chiamata verso INIPEC e successivamente verso INAD per recuperarlo,
  non viene recuperato nessun recapito,
  le richieste e gli esiti delle chiamate verso i registri sono tracciate nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 85385800199 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "GET_ADDRESS"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la risposta sul registro INIPEC per il cf "85385800199" non contiene recapiti
    And il batch associato alla richiesta verso INIPEC per il cf "85385800199" risulta in stato WORKED
    And la risposta sul registro INAD per il cf "85385800199" non contiene recapiti
    And il batch associato alla richiesta verso INAD per il cf "85385800199" risulta in stato WORKED


  @inadMassive @mockNR @work-item:QA-18515 #EnableInadMassiveApi:true;EnableOldInipecFlow:false
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_05_1] Come PA invio una notifica verso una PF senza specificare un recapito digitale, quindi
  viene fatta una chiamata verso INAD e successivamente verso INIPEC per recuperarlo,
  le richieste e gli esiti delle chiamate verso i registri sono tracciate nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | GNFRSS80A01G203W |
      | digitalDomicile | NULL             |
      | recipientType   | PF               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la risposta sul registro INAD per il cf "GNFRSS80A01G203W" non contiene recapiti
    And il batch associato alla richiesta verso INAD per il cf "GNFRSS80A01G203W" risulta in stato WORKED
    And la richiesta verso il servizio INIPEC per il cf "GNFRSS80A01G203W" risulta in stato WORKED
    And il batch associato alla richiesta verso INIPEC per il cf "GNFRSS80A01G203W" risulta in stato WORKED


  @inadMassive @mockNR @ignoreUAT @inadMassiveTrueInipecTrue @work-item:QA-18515 #EnableInadMassiveApi:true;EnableOldInipecFlow:true
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_06_1] Come PA invio una notifica verso una PG senza specificare un recapito digitale, quindi
  viene fatta una chiamata verso INIPEC e successivamente verso INAD per recuperarlo,
  le richieste e gli esiti delle chiamate verso i registri sono tracciate nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 39274018561 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la richiesta INIPEC per il cf "39274018561" risulta INVIATA al primo tentativo
    And recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la richiesta verso il servizio INAD per il cf "39274018561" risulta in stato WORKED
    And il batch associato alla richiesta verso INAD per il cf "39274018561" risulta in stato WORKED

  @inadMassive @mockNR @ignoreUAT @inadMassiveFalseInipecTrue @work-item:QA-18515 #EnableInadMassiveApi:false;EnableOldInipecFlow:true
  Scenario: [INAD_MASSIVE_GESTINE_ESITO_06_2] Come PA invio una notifica verso una PG senza specificare un recapito digitale, quindi
  viene fatta una chiamata verso INIPEC e successivamente verso INAD per recuperarlo,
  le richieste e gli esiti delle chiamate verso i registri sono tracciate nelle tabelle dedicate al monitoraggio.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario
      | taxId           | 39274018561 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE"
    Then recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And la richiesta INIPEC per il cf "39274018561" risulta INVIATA al primo tentativo
    And recupero l'id correlato alla richiesta dall'elemento di timeline "PUBLIC_REGISTRY_CALL"
    And risulta una richiesta INAD per il cf "39274018561"


  # COSA VERIFICHIAMO
  # Una notifica verso una PG priva di domicilio digitale fa interrogare INIPEC per recuperarne
  # la PEC. L'esito per ciascun CF viene tracciato su DynamoDB `pn-batchRequests` tramite i campi
  # `sendStatus` e `retry`. Gli scenari controllano che, a fronte di esiti INIPEC noti (pilotati da
  # CF "magici" del mock), il record finisca nello stato atteso. Mappatura esito -> stato su DB:
  #
  #   "risulta INVIATA al primo tentativo"        -> sendStatus = SENT,     retry = 0
  #   "risulta RIMESSA in retry"                  -> sendStatus = NOT_SENT,  retry > 0
  #   "non risulta alcuna richiesta INIPEC"       -> nessun record (né SENT né NOT_SENT)
  #   "risulta in ERROR e inviata in DLQ"         -> sendStatus = SENT_TO_DLQ, status = ERROR
  #                                                  + messaggio su pn-national_registry_gateway_inputs-DLQ
  #   "risulta con polling in ERROR e inviata in DLQ" -> campo batchId del record su pn-batchRequests,
  #                                                  pn-batchPolling (per batchId) con status = ERROR, retry = 3
  #                                                  + messaggio su pn-national_registry_gateway_inputs-DLQ
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


  @inipecDlq
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_DLQ_01] Notifica verso PG con errore sulla creazione del batch INIPEC (richiestaElencoPec): le request vengono rimesse in retry fino a 3 tentativi, poi vanno in ERROR e in DLQ
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    # 10762910015 -> fallimento sulla creazione del batch: retry delle request fino a 3, poi ERROR e invio in DLQ
    And destinatario
      | taxId           | 10762910015 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la richiesta INIPEC per il cf "10762910015" risulta in ERROR e inviata in DLQ


  @inipecDlq
  Scenario: [INIPEC_GESTIONE_ESITO_PUNTUALE_DLQ_02] Notifica verso PG con errore sul polling dell'esito INIPEC: il polling viene ripetuto fino a 3 tentativi, poi tutte le request vanno in ERROR e in DLQ
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    # 10433218194 -> fallimento sul polling: retry del polling fino a 3, allo scadere dei retry tutte le request vanno in ERROR e in DLQ
    And destinatario
      | taxId           | 10433218194 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    And salvo il timestamp corrente
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_CALL"
    Then la richiesta INIPEC per il cf "10433218194" risulta con polling in ERROR e inviata in DLQ
