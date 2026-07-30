Feature: Ricerca delle notifiche legali e bonarie ricevute lato mittente

    #######################
    # SCENARIO 1 - Verifica nuovi dati sulla tabella pn-NotificationsMetadata
    #######################

  #CASO DI TEST 1.1
  @ricercaNotifiche
  Scenario: [DYNAMO_NOTIFICATIONS_METADATA_1] Verifica gli attributi communicationType/campaignId/viewed/delivered/desiredFeedback su pn-NotificationsMetadata
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And si verifica sulla tabella pn-NotificationsMetadata che per lo IUN ":actualIun" e il destinatario con taxId ":recipientId" di tipo "PF" gli attributi siano:
      | communicationType | LEGAL |
      | campaignId        | $NULL |
      | viewed            | $NULL |
      | delivered         | $NULL |
      | desiredFeedback   | $NULL |
#
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | SoricalMessaMora |
    And destinatario della notifica bonaria
      | recipientType | PF               |
      | taxId         | FRMTTR76M06B715E |
      | denomination  | Mario Cucumber   |
      | messageId     | ${IT}            |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica sulla tabella pn-NotificationsMetadata che per lo IUN ":informal_iun" e il destinatario con taxId ":informal_recipientId" di tipo "PF" gli attributi siano:
      | communicationType | INFORMAL         |
      | campaignId        | SoricalMessaMora |
      | viewed            | BOOLEAN          |
      | delivered         | BOOLEAN          |
      | desiredFeedback   | BOOLEAN          |


  #CASO DI TEST 2.1 - tutti i campi (obbligatori e opzionali) valorizzati correttamente
  @ricercaNotifiche
  Scenario: [MITTENTE_RICERCA_NOTIFICHE_1_A] Come mittente recupero le notifiche inviate filtrando per tutti i campi obbligatori e opzionali
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
      | communicationType | LEGAL          |
      | size              | 50             |
      | senderId          | :senderId      |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | communicationType | LEGAL                         |
      | sentAt            | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | sender            | $NOT_EMPTY                    |
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | sentAt            | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | communicationType | LEGAL                         |
      | size              | 50                            |
      | senderId          | :senderId                     |
      | recipientId       | :recipientId                  |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | communicationType | LEGAL                         |
      | sentAt            | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | sender            | $NOT_EMPTY                    |
      | recipients        | CLMCST42R12D969Z              |
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | communicationType | LEGAL                         |
      | sentAt            | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | size              | 50                            |
      | senderId          | :senderId                     |
      | iunMatch          | :actualIun                    |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | communicationType | LEGAL                         |
      | sentAt            | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | sender            | $NOT_EMPTY                    |
      | iun               | :actualIun                    |
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | communicationType | LEGAL                         |
      | sentAt            | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | size              | 50                            |
      | senderId          | :senderId                     |
      | status            | EFFECTIVE_DATE                |
    # si verifica che anche passando communicationType=INFORMAL vengano recuperate le notifiche inviate dal mittente "Comune_Multi" con communicationType=LEGAL
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | communicationType  | LEGAL                         |
      | sentAt             | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | sender             | $NOT_EMPTY                    |
      | notificationStatus | EFFECTIVE_DATE                |
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | communicationType | INFORMAL                      |
      | sentAt            | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | size              | 50                            |
      | senderId          | :senderId                     |
      | status            | EFFECTIVE_DATE                |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | communicationType  | LEGAL                         |
      | sentAt             | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | sender             | $NOT_EMPTY                    |
      | notificationStatus | EFFECTIVE_DATE                |

  @ricercaNotifiche
  Scenario: [MITTENTE_RICERCA_NOTIFICHE_1_B] Viene creata una notifica legale con due destinatari e si verifica che venga ritornata una sola notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | sentAt   | $DATE_ADD(-1D), $DATE_ADD(1D) |
#      | communicationType | INFORMAL               |
      | size     | 50                            |
      | senderId | :senderId                     |
      | iunMatch | :actualIun                    |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt     | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | sender     | $NOT_EMPTY                    |
      | iun        | :actualIun                    |
      | itemsFound | 1                             |


  @ricercaNotifiche
  Scenario: [MITTENTE_RICERCA_NOTIFICHE_1_C] Viene inviata una notifica legale con gruppo e si recuperano le notifiche inviate dal mittente filtrando per gruppo
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
      | communicationType | INFORMAL       |
      | size              | 50             |
      | senderId          | :senderId      |
      | xPagopaPnCxGroups | :group         |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | group  | CONSISTENT                    |


  @ricercaNotifiche
  Scenario Outline: [MITTENTE_RICERCA_NOTIFICHE_1_D] Si tenta il recupero delle notifiche inviate dal mittente quando manca un campo obbligatorio
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | <campo> | $NULL |
    Then si verifica che sia stato restituito un errore di tipo "BAD REQUEST"
    Examples:
      | campo           |
      | xPagopaPnUid    |
      | xPagopaPnCxType |
      | xPagopaPnCxId   |
      | startDate       |
      | endDate         |


  #CASO DI TEST 2.2 - ricerca per specifico destinatario persona giuridica
  @ricercaNotifiche
  Scenario: [MITTENTE_RICERCA_NOTIFICHE_1_E] Come mittente recupero le notifiche inviate filtrando per uno specifico destinatario persona giuridica
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | sentAt      | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | size        | 50                            |
      | senderId    | :senderId                     |
      | recipientId | :recipientId                  |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt     | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | recipients | :recipientId_0                |


  #CASO DI TEST 2.1/2.2 - paginazione con più risultati
  @ricercaNotifiche
  Scenario: [MITTENTE_RICERCA_NOTIFICHE_1_F] Come mittente recupero le notifiche inviate sfogliando tutte le pagine dei risultati
    Given vengono create 5 notifiche con destinatario Mario Gherkin per la pa "Comune_Multi" e si aspetta che raggiungano l'elemento di timeline della notifica "REQUEST_ACCEPTED"
      | subject            | invio notifica paginazione |
      | senderDenomination | Comune di Palermo          |
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | sentAt   | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | size     | 1                             |
      | senderId | :senderId                     |
    And si sfogliano tutte le pagine della ricerca lato mittente e si verifica che vengano raccolte almeno 5 notifiche


    #######################
    # Comunicazioni Bonarie
    #######################

  @ricercaNotifiche
  Scenario: [MITTENTE_RICERCA_NOTIFICHE_BONARIE_2.A] Vengono inviate due notifiche bonarie con esiti differenti
  e si recuperano le notifiche inviate dal mittente filtrando per specifici criteri
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | SoricalMessaMora |
    And destinatario della notifica bonaria
      | messageId       | ${IT}                   |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
      | phone_number    | NULL                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    #Da aggiungere lo step per leggere gli eventi fino all'elemento di timeline della notifica "UNDELIVERABLE"
    #tramite API /received implementato in un nuovo branch
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"

    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | SoricalFattOrd |
    And destinatario della notifica bonaria
      | messageId       | ${IT}             |
      | subject         | Test workflow     |
      | recipientType   | PF                |
      | taxId           | FRMTTR76M06B715E  |
      | denomination    | Ettore Fieramosca |
      | email           | NULL              |
      | digitalDomicile | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "UNDELIVERABLE"
    #Da aggiungere lo step per leggere gli eventi fino all'elemento di timeline della notifica "UNDELIVERABLE"
    #tramite API /received implementato in un nuovo branch

    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate  | $DATE_ADD(-1D)     |
      | endDate    | $DATE_ADD(1D)      |
      | campaignId | SoricalMessaMora   |
      | senderId   | :informal_senderId |
      | size       | 50                 |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt     | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | campaignId | SoricalMessaMora              |
    #    ricerca per specifico stato
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate  | $DATE_ADD(-1D)     |
      | endDate    | $DATE_ADD(1D)      |
      | campaignId | SoricalMessaMora   |
      | status     | COMPLETED_REACHED  |
      | senderId   | :informal_senderId |
      | size       | 50                 |
      | delivered  | true               |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt             | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | notificationStatus | COMPLETED_REACHED             |
    #    ricerca per specifico stato UNDELIVERABLE
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate  | $DATE_ADD(-1D)     |
      | endDate    | $DATE_ADD(1D)      |
      | campaignId | SoricalFattOrd     |
      | status     | UNDELIVERABLE      |
      | senderId   | :informal_senderId |
      | size       | 50                 |
      | viewed     | false              |
      | delivered  | false              |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt             | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | notificationStatus | UNDELIVERABLE                 |
      | campaignId         | SoricalFattOrd                |
      | viewed             | false                         |
      | delivered          | false                         |
#    ricerca per specifico esito
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate  | $DATE_ADD(-1D)     |
      | endDate    | $DATE_ADD(1D)      |
      | campaignId | SoricalMessaMora   |
      | senderId   | :informal_senderId |
      | delivered  | true               |
      | size       | 50                 |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt    | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | delivered | true                          |

#    ricerca per specifico destinatario PF e specifica campagna
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate   | $DATE_ADD(-1D)        |
      | endDate     | $DATE_ADD(1D)         |
      | campaignId  | SoricalMessaMora      |
      | recipientId | :informal_recipientId |
      | senderId    | :informal_senderId    |
      | size        | 50                    |
      | delivered   | true                  |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt     | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | campaignId | SoricalMessaMora              |
      | recipients | :informal_recipientId         |

    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | SoricalFattOrd |
    And destinatario della notifica bonaria
      | recipientType   | PG            |
      | taxId           | 12666810299   |
      | denomination    | GherkinSrlB2B |
      | messageId       | ${IT}         |
      | digitalDomicile | tu@gmail.com  |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"

    #    ricerca per specifica campagna e specifico IUN
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate  | $DATE_ADD(-1D)     |
      | endDate    | $DATE_ADD(1D)      |
      | campaignId | SoricalFattOrd     |
      | iunMatch   | :informal_iun      |
      | senderId   | :informal_senderId |
      | size       | 50                 |
      | delivered  | true               |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | iun    | :informal_iun                 |
#    ricerca per specifico destinatario PG e specifica campagna
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate   | $DATE_ADD(-1D)        |
      | endDate     | $DATE_ADD(1D)         |
      | campaignId  | SoricalFattOrd        |
      | recipientId | :informal_recipientId |
      | senderId    | :informal_senderId    |
      | size        | 50                    |
      | delivered   | true                  |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt     | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | campaignId | SoricalFattOrd                |
      | recipients | :informal_recipientId         |
#    ricerca per specifico gruppo
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate         | $DATE_ADD(-1D)     |
      | endDate           | $DATE_ADD(1D)      |
      | campaignId        | SoricalFattOrd     |
      | xPagopaPnCxGroups | :informal_group    |
      | senderId          | :informal_senderId |
      | size              | 50                 |
      | delivered         | true               |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | group  | CONSISTENT                    |


  #CASO DI TEST 3.2 - paginazione con più risultati
  @ricercaNotifiche
  Scenario: [MITTENTE_RICERCA_NOTIFICHE_BONARIE_2.C] Come mittente recupero le notifiche bonarie inviate sfogliando tutte le pagine dei risultati
    Given vengono create 5 notifiche bonarie per la pa "Comune_Multi" con campagna "SoricalMessaMora"
      | recipientType | PF               |
      | taxId         | FRMTTR76M06B715E |
      | denomination  | Mario Cucumber   |
      | messageId     | ${IT}            |
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate  | $DATE_ADD(-1D)     |
      | endDate    | $DATE_ADD(1D)      |
      | campaignId | SoricalMessaMora   |
      | size       | 1                  |
      | senderId   | :informal_senderId |
      | delivered  | true               |
    And si sfogliano tutte le pagine della ricerca lato mittente e si verifica che vengano raccolte almeno 5 notifiche

  @ricercaNotifiche
  Scenario Outline: [MITTENTE_RICERCA_NOTIFICHE_BONARIE_2.B] Si tenta il recupero delle notifiche bonarie inviate dal mittente quando manca un campo obbligatorio
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | <campo> | $NULL |
    Then si verifica che sia stato restituito un errore di tipo "BAD REQUEST"
    Examples:
      | campo           |
      | xPagopaPnUid    |
      | xPagopaPnCxType |
      | xPagopaPnCxId   |
      | startDate       |
      | endDate         |
      | campaignId      |


    #######################
    # SCENARIO 6 - Ricerca notifiche da parte di servicedesk - NRT
    #######################

  #CASO DI TEST 6.1/6.2 - ricerca per mittente e destinatario persona fisica, con filtro gruppo
  @ricercaNotifiche
  Scenario: [SERVICEDESK_RICERCA_NOTIFICHE_1] Il servicedesk recupera le notifiche legali filtrando per mittente, destinatario persona fisica e gruppo
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono recuperate le notifiche da servicedesk
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
#      | recipientId       | :recipientUid   |
      | recipientIdOpaque | false          |
      | senderId          | :senderId      |
      | status            | ACCEPTED       |
      | cxType            | PF             |
      | size              | 50             |
    And l'elenco delle notifiche recuperate da servicedesk rispettare i seguenti criteri:
      | sender | $NOT_EMPTY |
    And vengono recuperate le notifiche da servicedesk
      | startDate   | $DATE_ADD(-1D) |
      | endDate     | $DATE_ADD(1D)  |
      | recipientId | :recipientUid  |
      | size        | 50             |
    And l'elenco delle notifiche recuperate da servicedesk rispettare i seguenti criteri:
      | sender     | $NOT_EMPTY       |
      | recipients | CLMCST42R12D969Z |


  #CASO DI TEST 6.2 - ricerca per mittente e destinatario persona giuridica
  @ricercaNotifiche
  Scenario: [SERVICEDESK_RICERCA_NOTIFICHE_2] Il servicedesk recupera le notifiche legali filtrando per mittente e destinatario persona giuridica
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono recuperate le notifiche da servicedesk
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
      | recipientIdOpaque | false          |
      | senderId          | :senderId      |
      | cxType            | PG             |
      | size              | 50             |
    And l'elenco delle notifiche recuperate da servicedesk rispettare i seguenti criteri:
      | sender | $NOT_EMPTY |


  #CASO DI TEST 6.2 - paginazione con più risultati
  @ricercaNotifiche
  Scenario: [SERVICEDESK_RICERCA_NOTIFICHE_3] Il servicedesk recupera le notifiche legali sfogliando tutte le pagine dei risultati
    Given vengono create 5 notifiche con destinatario Mario Gherkin per la pa "Comune_Multi" e si aspetta che raggiungano l'elemento di timeline della notifica "REQUEST_ACCEPTED"
      | subject            | invio notifica paginazione |
      | senderDenomination | Comune di Palermo          |
    And vengono recuperate le notifiche da servicedesk
      | startDate   | $DATE_ADD(-1D) |
      | endDate     | $DATE_ADD(1D)  |
      | recipientId | :recipientUid  |
      | size        | 1              |
    And si sfogliano tutte le pagine della ricerca da servicedesk e si verifica che vengano raccolte almeno 5 notifiche


  #CASO DI TEST 6.1 - campo obbligatorio non valorizzato -> 400 KO
  @ricercaNotifiche
  Scenario Outline: [SERVICEDESK_RICERCA_NOTIFICHE_4] Il servicedesk non riesce a recuperare le notifiche se manca un campo obbligatorio
    And vengono recuperate le notifiche da servicedesk
      | <campo> | $NULL |
    Then si verifica che la ricerca notifiche da servicedesk abbia prodotto un errore di tipo "BAD REQUEST"
    Examples:
      | campo     |
      | startDate |
      | endDate   |

