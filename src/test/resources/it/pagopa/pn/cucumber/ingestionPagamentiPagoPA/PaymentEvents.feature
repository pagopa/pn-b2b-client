Feature: Recupero degli eventi di pagamento pagoPA

  # Scope: SEND riceve da pagoPA (via Kafka, pn-kafka-bridge) gli eventi di pagamento degli avvisi e li correla alle
  # notifiche legali e alle comunicazioni bonarie che li contengono. Gli eventi sono pubblicati con il mock del Nodo
  # pagoPA di pn-external-channels; il pagamento da App IO è simulato dal mock di pn-io-connector.
  #
  # Copertura:
  # - BASIC: registrazione del pagamento su legali e bonarie, conclusione del workflow bonario secondo stopOnPaid,
  #   non regressione dell'API B2B dell'ente, avvisi censiti prima del rilascio, tipo della posizione debitoria,
  #   avvisi sconosciuti a SEND
  # - RACE: stesso pagamento comunicato da App IO e da pagoPA, nei due ordini
  # - DATA: eventi malformati scartati nella DLQ senza bloccare i successivi
  # - ENDED_FLOW: pagamento su legale annullata o bonaria conclusa
  #
  # Limiti non coperti:
  # - bonarie e legali con più destinatari o più avvisi: ogni scenario usa un destinatario e un avviso
  # - eventi non JSON o oltre 1MB (scarto con log FATAL) e campi diversi da avviso, ente, data e importo: il mock
  #   pubblica solo eventi JSON con questi quattro campi e canale di pagamento fisso
  # - canale di pagamento nell'elemento PAYMENT scritto da pagoPA: il mock lo fissa a IO
  # - creazione e modifica delle campagne: il client è in sola lettura, gli scenari verificano soltanto che la campagna
  #   nominata abbia la configurazione attesa (sostituibile per ambiente con -Dpn.payment-events.campaign.<nome>=<id>)
  #
  # Blocchi attuali (2026-11-18); lo scenario bloccato è @ignore:
  # - campagne con stopOnPaid true: in TEST esistono solo quelle delle famiglie FattOrd e Reminder (primo canale IO),
  #   usate da 01_7_A e 02_2 (FattOrd) dove il destinatario è su App IO e il canale IO attende fino a 8 minuti; per usarne
  #   altre -Dpn.payment-events.campaign.<nome>=<id>
  # - nessuna campagna priva dell'attributo stopOnPaid (in DEV tutte lo valorizzano, BonarieAllChannels a false): 01_2_C
  # Nota: la DLQ dei pagamenti non ha consumer e conserva i messaggi 14 giorni, quindi 03_1_A e 03_2_A tengono in
  # allarme la coda (in TEST l'allarme era già in ALARM dal 2026-11-17): eseguirli d'accordo con il team che lo riceve.

  # BASIC_01 - Pagamenti su notifiche legali e comunicazioni bonarie

  @ingestionPagamentiPagoPA @cleanWebhook @work-item:QA-18499
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

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_BASIC_01_2_A] Il pagamento pagoPA conclude il workflow della bonaria se la campagna lo prevede
    Given la campagna "Reminder" del "Comune_Multi" conclude il workflow al pagamento
    And il destinatario è raggiungibile su App IO ma non paga gli avvisi tramite App IO
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA e recapiti EMAIL e PEC nella campagna "Reminder"
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And la comunicazione bonaria risulta pagata
    And il pagamento pagoPA non è esposto nella timeline della notifica
    And il workflow della comunicazione bonaria si interrompe al pagamento senza proseguire sugli altri canali

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_BASIC_01_2_B] Il pagamento pagoPA non conclude il workflow della bonaria se la campagna non lo prevede
    Given la campagna "BonarieAllChannels" del "Comune_Multi" non conclude il workflow al pagamento
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA e recapiti EMAIL e PEC nella campagna "BonarieAllChannels"
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And la comunicazione bonaria risulta pagata
    And i metadati di ricerca della comunicazione bonaria la indicano come pagata
    And il pagamento pagoPA non è esposto nella timeline della notifica
    And il workflow della comunicazione bonaria prosegue sugli altri canali dopo il pagamento

  # @ignore (2026-11-18): lo scenario richiede una campagna di Comune_Multi priva dell'attributo stopOnPaid (assente =
  # false, REQ-INF-05), ma in TEST tutte le campagne di pn-Campaigns lo valorizzano (BonarieAllChannels è a false) e non
  # esistono campagne di altri mittenti. Il client è in sola lettura: va tolto @ignore quando il team SEND ne crea una
  # (nome sostituibile con -Dpn.payment-events.campaign.BonarieAllChannels=<id>).
  @ingestionPagamentiPagoPA @ignore @cleanWebhook @work-item:QA-18499
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

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_BASIC_01_3_A] Il pagamento comunicato dall'ente tramite API B2B continua a produrre NOTIFICATION_PAID
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    When l'ente comunica il pagamento dell'avviso tramite API B2B
    Then la notifica risulta pagata
    And il pagamento risulta comunicato dall'ente

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_BASIC_01_4_A] Il pagamento di un avviso censito prima del rilascio viene trattato come legale
    Given una notifica legale il cui avviso è stato censito prima dell'introduzione del tipo comunicazione
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_BASIC_01_5_A] L'avviso di una notifica legale è censito come posizione debitoria legale
    When il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    Then la posizione debitoria dell'avviso è censita come "LEGAL"

  @ingestionPagamentiPagoPA @cleanWebhook @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_BASIC_01_5_B] L'avviso di una comunicazione bonaria è censito come posizione debitoria bonaria
    Given si predispone 1 nuovo stream denominato "stream-payment-events" con eventType "TIMELINE" con versione "V30"
    And allo stream versione "V30" si setta il campo communicationType a "INFORMAL"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V30"
    When il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "FattOrd"
    Then la posizione debitoria dell'avviso è censita come "INFORMAL"
    And SEND registra la validazione della posizione debitoria nella timeline della comunicazione bonaria
    And la validazione della posizione debitoria non è esposta nella timeline della notifica
    And la validazione della posizione debitoria non è esposta negli eventi dello stream dell'ente
    And SEND non registra costi di notificazione per la comunicazione bonaria

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_BASIC_01_6_A] Il pagamento di un avviso sconosciuto a SEND viene ignorato
    When pagoPA comunica il pagamento di un avviso sconosciuto a SEND
    Then SEND scarta il pagamento perché l'avviso non è censito
    And l'evento non è presente nella coda degli eventi di pagamento malformati
    And SEND ignora il pagamento senza registrare errori

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_BASIC_01_7_A] Il pagamento da App IO conclude il workflow della bonaria se la campagna lo prevede
    Given la campagna "FattOrd" del "Comune_Multi" conclude il workflow al pagamento
    And il destinatario paga gli avvisi anche tramite App IO
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "FattOrd"
    When App IO comunica il pagamento dell'avviso
    Then App IO ha comunicato il pagamento dell'avviso
    And l'elemento PAYMENT della comunicazione bonaria è stato scritto dal pagamento comunicato da "App IO"
    And il workflow della comunicazione bonaria si interrompe al pagamento senza proseguire sugli altri canali

  # RACE_02 - Stesso pagamento da App IO e da pagoPA

  @ingestionPagamentiPagoPA @work-item:QA-18499
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

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario Outline: [PAYMENT_EVENTS_RACE_02_2_<variante>] Se App IO e pagoPA chiedono entrambi la chiusura del workflow la bonaria si chiude una sola volta (<primo> poi <secondo>)
    Given la campagna "FattOrd" del "Comune_Multi" conclude il workflow al pagamento
    And il destinatario paga gli avvisi anche tramite App IO
    And il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "FattOrd"
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

  # DUPLICATE_05 - Stesso avviso di una notifica legale pagato da pagoPA e comunicato dall'ente via B2B

  # L'ente può comunicare il pagamento anche se pagoPA lo ha già pubblicato (o prima che l'evento arrivi): gli id dei due
  # elementi sono diversi, quindi coesistono (SRS §6.5). Atteso da confermare con il team in fase di collaudo.
  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario Outline: [PAYMENT_EVENTS_DUPLICATE_05_1_<variante>] Il pagamento di una notifica legale comunicato da pagoPA e dall'ente viene registrato una sola volta per canale (<primo> poi <secondo>)
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    And la notifica legale ha concluso il proprio workflow
    When il pagamento dell'avviso legale viene comunicato prima da "<primo>" e poi da "<secondo>"
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And la notifica risulta pagata
    And il pagamento risulta comunicato dall'ente
    And nella timeline della notifica è presente un solo pagamento pagoPA
    And il pagamento pagoPA non è esposto nella timeline della notifica

    Examples:
      | variante | primo  | secondo |
      | A        | pagoPA | ente    |
      | B        | ente   | pagoPA  |

  # Un avviso (creditorTaxId + codice avviso) non può essere usato da due notifiche attive, legali o bonarie: la
  # seconda creazione è rifiutata con 409, quindi il pagamento pagoPA ha sempre una sola notifica di destinazione.
  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_DUPLICATE_05_3_A] Un avviso già usato da una notifica legale viene rifiutato su una seconda notifica legale
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    When il "Comune_Multi" tenta di notificare a Mario Gherkin una seconda notifica legale con lo stesso avviso pagoPA
    Then SEND rifiuta la seconda notifica perché l'avviso è già utilizzato

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_DUPLICATE_05_3_B] Un avviso già usato da una comunicazione bonaria viene rifiutato su una seconda comunicazione bonaria
    Given il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "FattOrd"
    When il "Comune_Multi" tenta di inviare una seconda comunicazione bonaria con lo stesso avviso pagoPA nella campagna "FattOrd"
    Then SEND rifiuta la seconda notifica perché l'avviso è già utilizzato

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_DUPLICATE_05_3_C] Un avviso già usato da una notifica legale viene rifiutato su una comunicazione bonaria
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA il cui codice termina con 00
    When il "Comune_Multi" tenta di inviare una seconda comunicazione bonaria con lo stesso avviso pagoPA nella campagna "FattOrd"
    Then SEND rifiuta la seconda notifica perché l'avviso è già utilizzato

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_DUPLICATE_05_3_D] Un avviso già usato da una comunicazione bonaria viene rifiutato su una notifica legale
    Given il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "FattOrd"
    When il "Comune_Multi" tenta di notificare a Mario Gherkin una seconda notifica legale con lo stesso avviso pagoPA
    Then SEND rifiuta la seconda notifica perché l'avviso è già utilizzato

  # Lo stesso avviso non può stare in due notifiche attive (409 alla seconda), ma si libera con l'annullamento: l'ente può
  # annullare e rinotificare con lo stesso avviso. Il pagamento deve andare alla nuova notifica e non alla annullata.
  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_DUPLICATE_05_2_A] Il pagamento di un avviso riusato dopo l'annullamento viene registrato solo sulla nuova notifica
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    And il "Comune_Multi" ha annullato la notifica legale
    And il "Comune_Multi" notifica a Mario Gherkin una seconda notifica legale con lo stesso avviso pagoPA
    And la notifica legale ha concluso il proprio workflow
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And nella timeline della notifica è presente un solo pagamento pagoPA
    And la prima notifica legale resta annullata senza pagamento pagoPA

  # DATA_03 - Eventi malformati

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario Outline: [PAYMENT_EVENTS_DATA_03_1_A] Un evento di pagamento malformato viene scartato nella DLQ: <anomalia>
    When pagoPA pubblica un evento di pagamento con "<anomalia>"
    Then l'evento malformato viene scartato nella coda degli eventi di pagamento malformati

    Examples:
      | anomalia                                    |
      | numero avviso mancante                      |
      | codice fiscale dell'ente creditore mancante |
      | data di pagamento mancante                  |

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_DATA_03_2_A] Un evento malformato non blocca l'elaborazione dei pagamenti successivi
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    When pagoPA pubblica un evento di pagamento con "data di pagamento mancante"
    And pagoPA comunica il pagamento dell'avviso
    Then l'evento malformato viene scartato nella coda degli eventi di pagamento malformati
    And SEND registra il pagamento pagoPA nella timeline della notifica

  # ENDED_FLOW_04 - Pagamenti su notifiche in stato terminale

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_ENDED_FLOW_04_1_A] Il pagamento di una notifica legale annullata viene scartato e non ne modifica lo stato
    Given il "Comune_Multi" ha notificato a Mario Gherkin una notifica legale con avviso pagoPA
    And il "Comune_Multi" ha annullato la notifica legale
    When pagoPA comunica il pagamento dell'avviso
    Then SEND scarta il pagamento perché l'avviso non è censito
    And SEND non registra il pagamento pagoPA nella timeline della notifica
    And la notifica legale resta nello stato "CANCELLED"

  @ingestionPagamentiPagoPA @work-item:QA-18499
  Scenario: [PAYMENT_EVENTS_ENDED_FLOW_04_2_A] Il pagamento di una comunicazione bonaria conclusa non ne modifica lo stato
    Given il "Comune_Multi" ha inviato una comunicazione bonaria con avviso pagoPA nella campagna "FattOrd"
    And la comunicazione bonaria ha concluso il proprio workflow
    When pagoPA comunica il pagamento dell'avviso
    Then SEND registra il pagamento pagoPA nella timeline della notifica
    And la comunicazione bonaria resta nello stato "COMPLETED_REACHED"
