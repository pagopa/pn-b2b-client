Feature: Ricerca delle notifiche ricevute lato destinatario

  #CASO DI TEST 4.1 - tutti i campi (obbligatori e opzionali) valorizzati correttamente
  @ricercaNotifiche @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_1] Come destinatario <tipo> ricerco le notifiche ricevute con tutti i filtri valorizzati
    Given mittente della notifica bonaria: "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalMessaMora  |
      | recipientType   | <tipo>            |
      | taxId           | <taxId>           |
      | denomination    | <destinatario>    |
      | messageId       | ${NEW-IT}         |
      | subject         | Test workflow     |
      | email           | NULL              |
      | digitalDomicile | <digitalDomicile> |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"

    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    # Passando communicationType = ALL si ottengono sia le notifiche bonarie che quelle legali
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
      | communicationType | ALL            |
      | size              | 50             |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL, INFORMAL |
    # Passando communicationType = INFORMAL, la notifica bonaria è presente nell'elenco delle notifiche ricevute
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
      | communicationType | INFORMAL       |
      | size              | 50             |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | INFORMAL |
    # Passando communicationType = LEGAL, la notifica bonaria non è presente nell'elenco delle notifiche ricevute
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
      | communicationType | LEGAL          |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL |
    # Passando communicationType assente, di default vengono cercate solo le LEGAL e la notifica bonaria non è presente nell'elenco delle notifiche ricevute
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL |

    Examples:
      | tipo | destinatario   | taxId            | digitalDomicile          |
      | PF   | Mario Cucumber | FRMTTR76M06B715E | NULL                     |
      | PG   | CucumberSpa    | 20517490320      | example@OK-pecSuccess.it |
      | PG   | CucumberSpaB2B | 20517490320      | example@OK-pecSuccess.it |


  #CASO DI TEST 4.1 - campo obbligatorio non valorizzato -> 400 KO
  @ricercaNotifiche @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_2] Come destinatario non riesco a ricercare le notifiche ricevute se manca un campo obbligatorio
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | <campo> | NULL |
    Then si verifica che venga ritornato un errore di tipo "BAD REQUEST"
    Examples:
      | campo           |
      | xPagopaPnUid    |
      | xPagopaPnCxType |
      | xPagopaPnCxId   |
      | startDate       |
      | endDate         |


  @deleghe4 @useB2B @ricercaNotifiche
  Scenario: [RICERCA_RICEVUTE_3] Un delegato riceve una notifica legale e la ricerca delle notifiche ricevute lato delegato restituisce le notifiche attese dei criteri di ricerca
    Given "Mario Cucumber" rifiuta se presente la delega ricevuta "Mario Gherkin"
    And "Mario Cucumber" viene delegato da "Mario Gherkin" per comune "Comune_Multi"
    And "Mario Cucumber" accetta la delega "Mario Gherkin"
    # Viene creata una notifica bonaria
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | SoricalMessaMora |
      | recipientType | PF               |
      | taxId         | CLMCST42R12D969Z |
      | denomination  | Mario Cucumber   |
      | messageId     | ${NEW-IT}        |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"

    # Viene creata una notifica legale
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"

    #Viene effettuata la ricerca delle notifiche ricevute lato delegato e si verifica che vengano restituite le sole notifiche legali
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
      | iunMatch  | :actualIun     |
      | senderId  | :senderId      |
      | mandateId | :mandateId     |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL      |
      | iun               | :actualIun |
      | mandateId         | :mandateId |
      | itemsFound        | 1          |

    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
      | communicationType | ALL            |
      | senderId          | :senderId      |
      | mandateId         | :mandateId     |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL      |
      | sender            | $NOT_EMPTY |

    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
      | group     | :group         |
      | senderId  | :senderId      |
      | mandateId | :mandateId     |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | group     | CONSISTENT |
      | mandateId | :mandateId |


  @deleghe4 @useB2B @ricercaNotifiche
  Scenario: [RICERCA_RICEVUTE_3aaa] Un delegato riceve una notifica legale e la ricerca delle notifiche ricevute lato delegato restituisce le sole notifiche legali
  ricevute dal delegato a partire dalla creazione della delega. Le notifiche bonarie non vengono restituite.
    Given "Mario Cucumber" rifiuta se presente la delega ricevuta "Mario Gherkin"
    And "Mario Cucumber" viene delegato da "Mario Gherkin" per comune "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | SoricalMessaMora        |
      | recipientType | PF                      |
      | taxId         | CLMCST42R12D969Z        |
      | denomination  | Mario Gherkin           |
      | messageId     | ${NEW-IT}               |
      | email         | tullio.test@virgilio.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"

    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" accetta la delega "Mario Gherkin"
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
      | iunMatch  | :actualIun     |
      | senderId  | :senderId      |
      | mandateId | :mandateId     |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL      |
      | iun               | :actualIun |
      | itemsFound        | 1          |

    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
      | communicationType | ALL            |
      | senderId          | :senderId      |
      | mandateId         | :mandateId     |

    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL      |
      | sender            | $NOT_EMPTY |

    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
      | communicationType | ALL            |
      | senderId          | :senderId      |

    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL, INFORMAL |
      | sender            | $NOT_EMPTY      |


  #CASO DI TEST 4.1 - un delegato non può cercare le notifiche bonarie del delegante: mandateId + communicationType INFORMAL deve restituire 400
  @deleghe4 @useB2B @ricercaNotifiche
  Scenario: [RICERCA_RICEVUTE_6] Un delegato non può cercare le notifiche bonarie del delegante tramite mandateId
    Given "Mario Cucumber" rifiuta se presente la delega ricevuta "Mario Gherkin"
    And "Mario Cucumber" viene delegato da "Mario Gherkin" per comune "Comune_Multi"
    And "Mario Cucumber" accetta la delega "Mario Gherkin"
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D)  |
      | mandateId         | :mandateId     |
      | communicationType | INFORMAL       |
    Then si verifica che venga ritornato un errore di tipo "BAD REQUEST"


  #CASO DI TEST 4.3/4.4 - ricerca per specifico mittente, IUN e gruppo lato destinatario
  @ricercaNotifiche @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_4] Come destinatario <tipo> ricerco le notifiche ricevute filtrando per mittente, IUN e gruppo
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario <destinatario>
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
      | senderId  | :senderId      |
      | size      | 2              |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | sender | $NOT_EMPTY |
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
      | iunMatch  | :actualIun     |
      | senderId  | :senderId      |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | iun    | :actualIun |
      | sender | $NOT_EMPTY |

    Examples:
      | tipo | destinatario   |
      | PF   | Mario Cucumber |
      | PG   | CucumberSpa    |
      | PG   | CucumberSpaB2B |


  #CASO DI TEST 4.3/4.4 - paginazione con più risultati
  @ricercaNotifiche @useB2B
  Scenario: [RICERCA_RICEVUTE_5] Come destinatario recupero le notifiche ricevute sfogliando tutte le pagine dei risultati
    Given vengono create 5 notifiche con destinatario Mario Cucumber per la pa "Comune_Multi" e si aspetta che raggiungano l'elemento di timeline della notifica "REQUEST_ACCEPTED"
      | subject            | invio notifica paginazione |
      | senderDenomination | Comune di Palermo          |
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
      | size      | 1              |
    And si sfogliano tutte le pagine della ricerca lato destinatario e si verifica che vengano raccolte almeno 5 notifiche






