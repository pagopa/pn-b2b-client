Feature: Recupero degli eventi di pagamento pagoPA

  # Regole di business
  # - pn-kafka-bridge consuma gli eventi di pagamento pubblicati da pagoPA. Gli eventi privi dei campi obbligatori
  #   o non deserializzabili finiscono nella DLQ dei pagamenti come PnErrorEvent; quelli malformati oltre 1MB
  #   vengono scartati con un log FATAL.
  # - pn-notification-cost-service correla l'evento all'avviso censito su pn-PaymentInfo (creditorTaxId##noticeCode):
  #   i pagamenti di avvisi sconosciuti a SEND vengono ignorati senza effetti.
  # - Notifiche legali: pn-delivery-push registra in timeline PAYMENT_NOTICE_PAID (elemento diagnostico, non esposto).
  #   L'API B2B con cui l'ente comunica il pagamento continua a produrre NOTIFICATION_PAID.
  # - Comunicazioni bonarie: pn-workflow-manager registra PAYMENT_NOTICE_PAID e PAYMENT. Il workflow si conclude
  #   per avvenuto pagamento solo se la campagna ha stopOnPaid = true; una campagna senza stopOnPaid equivale a false.
  # - Lo stesso pagamento comunicato sia da App IO sia da pagoPA produce un solo elemento PAYMENT, mentre
  #   PAYMENT_NOTICE_PAID viene scritto sempre e solo dal flusso pagoPA.
  # - Un pagamento su una notifica in stato terminale non ne modifica lo stato. Il pagamento di una notifica legale
  #   annullata viene scartato: con lo stato CANCELLED lo IUN viene rimosso da pn-PaymentInfo.
  #
  # Campagne di test: le campagne con stopOnPaid verranno create dal team SEND al rilascio del flag (i test non possono
  # crearle: il client delle campagne è in sola lettura). Gli scenari che le usano sono template con nomi provvisori:
  #   StopOnPaid-QA       stopOnPaid = true,  stessa escalation di BonarieAllChannels (EMAIL PT4M -> PEC -> SMS, nessun feedback)
  #   NoStopOnPaid-QA     stopOnPaid = false, stessa escalation di BonarieAllChannels
  #   StopOnPaid-IO-QA    stopOnPaid = true,  primo canale IO senza feedback desiderati, poi EMAIL
  # Alla creazione vanno aggiornati i nomi e, per StopOnPaid-IO-QA, la finestra di escalation degli step di workflow
  # (oggi tarata sui 4 minuti dell'EMAIL).
  # 01_2_C usa BonarieAllChannels perché in TEST e UAT non ha l'attributo stopOnPaid. Al rilascio va verificato che
  # la migrazione delle campagne non lo valorizzi: in DEV tutte le campagne lo hanno ricevuto (false), e in quel caso
  # serve una campagna creata senza l'attributo.
  #
  # Campagna FattOrd: in TEST e UAT ha desiredFeedback [READ, PAID] sul canale IO. Con US-4 PAID non è più un valore
  # ammesso (SRS 13.14) e la campagna verrà migrata (in DEV lo è già: [READ] e stopOnPaid = true); finché non lo è, la
  # lettura della campagna può fallire e gli scenari che la usano (01_5_B, 02_1, 04_2_A) non sono eseguibili. Nessuno
  # di questi dipende da PAID o da stopOnPaid: dopo la migrazione vanno solo rieseguiti.
  #
  # Gli scenari non ancora eseguibili sono marcati @ignore (esclusi dai runner) e con i tag delle dipendenze mancanti:
  #   @attesaKafkaMock          mock del Nodo pagoPA di pn-external-channels non ancora disponibile in tutti gli ambienti
  #   @attesaPaymentNoticePaid  elemento di timeline PAYMENT_NOTICE_PAID non ancora rilasciato
  #   @attesaCommunicationType  campo communicationType su pn-PaymentInfo non ancora rilasciato
  #   @attesaStopOnPaid         flag stopOnPaid non rilasciato; campagne di test non ancora create (vedi sopra)
  #   @attesaDlqPagamenti       DLQ dei pagamenti di pn-kafka-bridge non ancora disponibile in tutti gli ambienti.
  #                             Ogni evento scartato fa scattare l'allarme sulla DLQ, che non ha consumer e conserva i messaggi 14 giorni: gli scenari
  #                             con questo tag vanno eseguiti d'accordo con il team che riceve gli allarmi
  #   @verificaCanaleIo         simulazione del pagamento da App IO tramite il mock di pn-io-connector da validare
  #   @attesaStreamPaymentNoticePaid
  #                             PAYMENT_NOTICE_PAID come elemento diagnostico di pn-stream (US-6) non ancora rilasciato
  #   @attesaMetadatoPaid       attributo paid dei metadati di ricerca delle bonarie (US-7) non ancora rilasciato

  # =========================================================================================
  # SCENARIO 1 - Invio eventi di pagamento per bonarie e legali
  # =========================================================================================

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaPaymentNoticePaid @attesaStreamPaymentNoticePaid @webhook3 @cleanWebhook @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_BASIC_01_1_A] Il pagamento pagoPA di una notifica legale viene registrato in timeline
    Given si predispone 1 nuovo stream denominato "stream-payment-events" con eventType "TIMELINE" con versione "V30"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    And il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    And la notifica legale ha concluso il proprio workflow
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And SEND non registra il pagamento pagoPA come pagamento comunicato dall'ente
    And il pagamento pagoPA non è esposto nella timeline della notifica
    And il pagamento pagoPA non è esposto negli eventi dello stream dell'ente
    And lo stato della notifica legale non cambia dopo il pagamento

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaPaymentNoticePaid @attesaStopOnPaid @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_BASIC_01_2_A] Il pagamento pagoPA conclude il workflow della bonaria se la campagna lo prevede
    # Template: StopOnPaid-QA (stopOnPaid = true) verrà creata dal team SEND al rilascio del flag (vedi intestazione)
    Given la campagna "StopOnPaid-QA" del "Comune_Multi" conclude il workflow al pagamento
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA e recapiti EMAIL e PEC nella campagna "StopOnPaid-QA"
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And la comunicazione bonaria risulta pagata
    And il pagamento pagoPA non è esposto nella timeline della notifica
    And il workflow della comunicazione bonaria si interrompe al pagamento senza proseguire sugli altri canali

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaPaymentNoticePaid @attesaStopOnPaid @attesaMetadatoPaid @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_BASIC_01_2_B] Il pagamento pagoPA non conclude il workflow della bonaria se la campagna non lo prevede
    # Template: NoStopOnPaid-QA (stopOnPaid = false) verrà creata dal team SEND al rilascio del flag (vedi intestazione)
    Given la campagna "NoStopOnPaid-QA" del "Comune_Multi" non conclude il workflow al pagamento
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA e recapiti EMAIL e PEC nella campagna "NoStopOnPaid-QA"
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And la comunicazione bonaria risulta pagata
    And i metadati di ricerca della comunicazione bonaria la indicano come pagata
    And il pagamento pagoPA non è esposto nella timeline della notifica
    And il workflow della comunicazione bonaria prosegue sugli altri canali dopo il pagamento

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaPaymentNoticePaid @attesaStopOnPaid @attesaStreamPaymentNoticePaid @webhook3 @cleanWebhook @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_BASIC_01_2_C] Il pagamento pagoPA non conclude il workflow di una bonaria di una campagna senza stopOnPaid
    Given la campagna "BonarieAllChannels" del "Comune_Multi" non specifica se concludere il workflow al pagamento
    And si predispone 1 nuovo stream denominato "stream-payment-events" con eventType "TIMELINE" con versione "V30"
    And allo stream versione "V30" si setta il campo communicationType a "INFORMAL"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA e recapiti EMAIL e PEC nella campagna "BonarieAllChannels"
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And la comunicazione bonaria risulta pagata
    And il pagamento pagoPA non è esposto nella timeline della notifica
    And il pagamento pagoPA non è esposto negli eventi dello stream dell'ente
    And il workflow della comunicazione bonaria prosegue sugli altri canali dopo il pagamento

  @ingestionPagamentiPagoPA @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_BASIC_01_3_A] Il pagamento comunicato dall'ente tramite API B2B continua a produrre NOTIFICATION_PAID
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    When l'ente comunica il pagamento dell'avviso tramite API B2B
    Then la notifica risulta pagata
    And il pagamento risulta comunicato dall'ente

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaPaymentNoticePaid @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_BASIC_01_4_A] Il pagamento di un avviso censito prima del rilascio viene trattato come legale
    Given una notifica legale il cui avviso è stato censito prima dell'introduzione del tipo comunicazione
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica

  @ingestionPagamentiPagoPA @ignore @attesaCommunicationType @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_BASIC_01_5_A] L'avviso di una notifica legale è censito come posizione debitoria legale
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    Then la posizione debitoria dell'avviso è censita come "LEGAL"

  @ingestionPagamentiPagoPA @ignore @attesaCommunicationType @webhook3 @cleanWebhook @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_BASIC_01_5_B] L'avviso di una comunicazione bonaria è censito come posizione debitoria bonaria
    Given si predispone 1 nuovo stream denominato "stream-payment-events" con eventType "TIMELINE" con versione "V30"
    And allo stream versione "V30" si setta il campo communicationType a "INFORMAL"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "FattOrd"
    Then la posizione debitoria dell'avviso è censita come "INFORMAL"
    And SEND registra la validazione della posizione debitoria nella timeline della comunicazione bonaria
    And la validazione della posizione debitoria non è esposta nella timeline della notifica
    And la validazione della posizione debitoria non è esposta negli eventi dello stream dell'ente
    And SEND non registra costi di notificazione per la comunicazione bonaria

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_BASIC_01_6_A] Il pagamento di un avviso sconosciuto a SEND viene ignorato
    When pagoPA comunica il pagamento di un avviso sconosciuto a SEND
    Then SEND scarta il pagamento perché l'avviso non è censito
    And l'evento non è presente nella coda degli eventi di pagamento malformati
    And SEND ignora il pagamento senza registrare errori

  @ingestionPagamentiPagoPA @ignore @attesaStopOnPaid @verificaCanaleIo @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_BASIC_01_7_A] Il pagamento da App IO conclude il workflow della bonaria se la campagna lo prevede
    # Template: StopOnPaid-IO-QA non esiste ancora (vedi intestazione). Il canale IO non deve avere READ tra i feedback
    # desiderati, perché la sequenza OK_READ_THEN_PAID legge il messaggio prima di pagarlo: la chiusura sarebbe per lettura.
    # La durata del canale IO deve superare i 90 secondi del PAID del mock più un margine, così il pagamento arriva
    # prima dell'escalation.
    Given la campagna "StopOnPaid-IO-QA" del "Comune_Multi" conclude il workflow al pagamento
    And il destinatario paga gli avvisi anche tramite App IO
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "StopOnPaid-IO-QA"
    When App IO comunica il pagamento dell'avviso
    Then App IO ha comunicato il pagamento dell'avviso
    And l'elemento PAYMENT della comunicazione bonaria è stato scritto dal pagamento comunicato da "App IO"
    And il workflow della comunicazione bonaria si interrompe al pagamento senza proseguire sugli altri canali

  # =========================================================================================
  # SCENARIO 2 - Race condition App IO e Kafka
  # =========================================================================================

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaPaymentNoticePaid @verificaCanaleIo @work-item:QA-18904
  Scenario Outline: [PAYMENT_EVENTS_RACE_02_1_<variante>] Lo stesso pagamento comunicato da App IO e da pagoPA genera un solo elemento PAYMENT e l'elemento PAYMENT_NOTICE_PAID (<primo> poi <secondo>)
    Given il destinatario paga gli avvisi anche tramite App IO
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "FattOrd"
    When il pagamento dell'avviso viene comunicato prima da "<primo>" e poi da "<secondo>"
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And App IO ha comunicato il pagamento dell'avviso
    And nella timeline della comunicazione bonaria è presente un solo elemento "PAYMENT"
    And l'elemento PAYMENT della comunicazione bonaria è stato scritto dal pagamento comunicato da "<primo>"

    Examples:
      | variante | primo  | secondo |
      | A        | pagoPA | App IO  |
      | B        | App IO | pagoPA  |

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaPaymentNoticePaid @attesaStopOnPaid @verificaCanaleIo @work-item:QA-18904
  Scenario Outline: [PAYMENT_EVENTS_RACE_02_2_<variante>] Se App IO e pagoPA chiedono entrambi la chiusura del workflow la bonaria si chiude una sola volta (<primo> poi <secondo>)
    # Template: StopOnPaid-IO-QA non esiste ancora (vedi intestazione), con gli stessi vincoli di BASIC_01_7_A.
    # Variante A: da verificare al primo run che pn-io-connector continui a interrogare App IO dopo la chiusura del
    # workflow, altrimenti il PAID del mock non arriva e il secondo WORKFLOW_DONE non viene mai richiesto.
    Given la campagna "StopOnPaid-IO-QA" del "Comune_Multi" conclude il workflow al pagamento
    And il destinatario paga gli avvisi anche tramite App IO
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "StopOnPaid-IO-QA"
    When il pagamento dell'avviso viene comunicato prima da "<primo>" e poi da "<secondo>"
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And App IO ha comunicato il pagamento dell'avviso
    And nella timeline della notifica è presente un solo pagamento pagoPA
    And nella timeline della comunicazione bonaria è presente un solo elemento "PAYMENT"
    And nella timeline della comunicazione bonaria è presente un solo elemento "WORKFLOW_DONE_REACHED"
    And il workflow della comunicazione bonaria si interrompe al pagamento senza proseguire sugli altri canali

    Examples:
      | variante | primo  | secondo |
      | A        | pagoPA | App IO  |
      | B        | App IO | pagoPA  |

  # =========================================================================================
  # SCENARIO 3 - Dati corrotti
  # =========================================================================================

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaDlqPagamenti @work-item:QA-18904
  Scenario Outline: [PAYMENT_EVENTS_DATA_03_1_A] Un evento di pagamento malformato viene scartato nella DLQ: <anomalia>
    When pagoPA pubblica un evento di pagamento con "<anomalia>"
    Then l'evento malformato viene scartato nella coda degli eventi di pagamento malformati

    Examples:
      | anomalia                                    |
      | numero avviso mancante                      |
      | codice fiscale dell'ente creditore mancante |
      | data di pagamento mancante                  |

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaPaymentNoticePaid @attesaDlqPagamenti @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_DATA_03_2_A] Un evento malformato non blocca l'elaborazione dei pagamenti successivi
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    When pagoPA pubblica un evento di pagamento con "data di pagamento mancante"
    And pagoPA comunica il pagamento dell'avviso
    Then l'evento malformato viene scartato nella coda degli eventi di pagamento malformati
    And SEND registra il pagamento pagoPA nella timeline della notifica

  # =========================================================================================
  # SCENARIO 4 - Stati limite in Timeline
  # =========================================================================================

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaPaymentNoticePaid @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_ENDED_FLOW_04_1_A] Il pagamento di una notifica legale annullata viene scartato e non ne modifica lo stato
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    And il "Comune_Multi" ha annullato la notifica legale
    When pagoPA comunica il pagamento dell'avviso
    Then SEND scarta il pagamento perché l'avviso non è censito
    And SEND non registra il pagamento pagoPA nella timeline della notifica
    And la notifica legale resta nello stato "CANCELLED"

  @ingestionPagamentiPagoPA @ignore @attesaKafkaMock @attesaPaymentNoticePaid @work-item:QA-18904
  Scenario: [PAYMENT_EVENTS_ENDED_FLOW_04_2_A] Il pagamento di una comunicazione bonaria conclusa non ne modifica lo stato
    Given il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "FattOrd"
    And la comunicazione bonaria ha concluso il proprio workflow
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And la comunicazione bonaria resta nello stato "COMPLETED_REACHED"
