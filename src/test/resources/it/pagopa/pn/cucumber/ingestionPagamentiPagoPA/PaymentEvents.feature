Feature: Recupero degli eventi di pagamento pagoPA

  # Scope: SEND riceve da pagoPA gli eventi di pagamento degli avvisi e li correla alle notifiche legali e alle
  # comunicazioni bonarie che li contengono.
  #
  # Copertura:
  # - BASIC: registrazione del pagamento su legali e bonarie, conclusione del workflow bonario secondo la campagna,
  #   non regressione del pagamento comunicato dall'ente, avvisi censiti prima del rilascio, avvisi sconosciuti a SEND
  # - RACE: stesso pagamento comunicato da App IO e da pagoPA, nei due ordini
  # - DUPLICATE: stesso avviso pagato da più canali o usato da più notifiche
  # - DATA: eventi malformati scartati senza bloccare i successivi
  # - ENDED_FLOW: pagamento su legale annullata o bonaria conclusa
  #
  # Limiti non coperti:
  # - notifiche con più destinatari o più avvisi
  # - eventi non JSON o con campi diversi da avviso, ente, data e importo
  # - creazione delle campagne: gli scenari verificano solo che la campagna nominata abbia la configurazione attesa
  #
  # Runner dedicati, da attivare su richiesta:
  # - PaymentEventsDlqTest (@paymentEventsDlq): gli scenari DATA lasciano gli eventi scartati nella coda di errore,
  #   che fa scattare un allarme a chi la presidia; vanno eseguiti d'accordo con quel team
  # - PaymentEventsLegacyNoticeTest (@paymentEventsLegacyNotice): 01_4_A cerca un avviso storico non pagato scorrendo
  #   le posizioni debitorie dell'ambiente, una ricerca che si allunga a ogni esecuzione
  #
  # Blocchi attuali (2026-11-18); lo scenario bloccato è @ignore:
  # - nessuna campagna priva della configurazione di chiusura al pagamento: 01_2_C

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

  @paymentEventsLegacyNotice @work-item:QA-18499
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

  @paymentEventsDlq @work-item:QA-18499
  Scenario Outline: [PAYMENT_EVENTS_DATA_03_1_A] Un evento di pagamento malformato viene scartato: <anomalia>
    When pagoPA pubblica un evento di pagamento con "<anomalia>"
    Then l'evento malformato viene scartato nella coda degli eventi di pagamento malformati

    Examples:
      | anomalia                                    |
      | numero avviso mancante                      |
      | codice fiscale dell'ente creditore mancante |
      | data di pagamento mancante                  |

  @paymentEventsDlq @work-item:QA-18499
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
