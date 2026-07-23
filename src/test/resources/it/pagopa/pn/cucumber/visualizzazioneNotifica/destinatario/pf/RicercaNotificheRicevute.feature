Feature: Ricerca delle notifiche ricevute lato destinatario

#  @informalNotificationsMessageAttachment
#  Scenario: [NOTIFICHE_BONARIE_05_1] Come ente mittente Recupero i documenti di una notifica bonaria
#    Given mittente della notifica bonaria: "Comune_Multi"
#    And viene creata una nuova notifica bonaria con i seguenti parametri
#      | campaignId | campaign-1 |
#    And destinatario della notifica bonaria
#      | recipientType | PF                |
#      | taxId         | FRMTTR76M06B715E  |
#      | denomination  | Ettore Fieramosca |
#      | messageId     | ${IT}             |
#    When viene inviata una nuova notifica bonaria
#    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
#    And si tenta il recupero documento della notifica bonaria
#    Then il download risulta correttamente effettuato



  Background:
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | SoricalMessaMora |
    And destinatario della notifica bonaria
      | recipientType | PF               |
      | taxId         | FRMTTR76M06B715E |
      | denomination  | Mario Cucumber   |
      | messageId     | ${IT}            |

  #CASO DI TEST 4.1 - tutti i campi (obbligatori e opzionali) valorizzati correttamente
  @letturaDestinatario @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_1] Come destinatario <tipo> ricerco le notifiche ricevute con tutti i filtri valorizzati
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
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
      | endDate           | $DATE_ADD(1D) |
      | communicationType | ALL        |
      | size              | 50         |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL, INFORMAL |
    # Passando communicationType = INFORMAL, la notifica bonaria è presente nell'elenco delle notifiche ricevute
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D) |
      | communicationType | INFORMAL   |
      | size              | 50         |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | INFORMAL |
    # Passando communicationType = LEGAL, la notifica bonaria non è presente nell'elenco delle notifiche ricevute
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D) |
      | communicationType | LEGAL      |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL |
    # Passando communicationType assente, di default vengono cercate solo le LEGAL e la notifica bonaria non è presente nell'elenco delle notifiche ricevute
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D) |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL |

    Examples:
      | tipo | destinatario   |
      | PF   | Mario Cucumber |
      | PG   | CucumberSpa    |
      | PG   | CucumberSpaB2B |


  #CASO DI TEST 4.1 - campo obbligatorio non valorizzato -> 400 KO
  @letturaDestinatario @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_2] Come destinatario non riesco a ricercare le notifiche ricevute se manca un campo obbligatorio
    And "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | <campo> | NULL |
    Then si verifica che venga ritornato un errore di tipo "BAD REQUEST"
    Examples:
      | campo           |
      | xPagopaPnUid    |
      | xPagopaPnCxType |
      | xPagopaPnCxId   |
      | startDate       |
      | endDate         |
    Examples:
      | destinatario   |
      | Mario Cucumber |
      | CucumberSpa    |
      | CucumberSpaB2B |


  @deleghe1 @useB2B
  Scenario: [RICERCA_RICEVUTE_3] Un delegato riceve una notifica legale e la ricerca delle notifiche ricevute lato delegato restituisce le notifiche attese dei criteri di ricerca
    Given "Mario Cucumber" rifiuta se presente la delega ricevuta "Mario Gherkin"
    And "Mario Cucumber" viene delegato da "Mario Gherkin" per comune "Comune_Multi"
    And "Mario Cucumber" accetta la delega "Mario Gherkin"

    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"

    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D) |
      | iunMatch  | :actualIun |
    And Si verifica che il numero di notifiche restituite nella pagina sia 1
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL      |
      | iun               | :actualIun |
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D) |
      | senderId          | :senderId  |
      | communicationType | ALL        |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL, INFORMAL |
      | sender            | Comune di palermo         |
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(1D) |
      | senderId          | :senderId  |
      | communicationType | INFORMAL   |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | INFORMAL |
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D) |
      | group     | :group     |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | group | CONSISTENT |


  #CASO DI TEST 4.3/4.4 - ricerca per specifico mittente, IUN e gruppo lato destinatario
  @letturaDestinatario @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_4] Come destinatario <tipo> ricerco le notifiche ricevute filtrando per mittente, IUN e gruppo
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario <destinatario>
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D) |
      | senderId  | :senderId  |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | sender | Comune di Palermo |
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D) |
      | iunMatch  | :actualIun |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | iun | :actualIun |
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D) |
      | group     | :group     |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | group | CONSISTENT |

    Examples:
      | tipo | destinatario   |
      | PF   | Mario Cucumber |
      | PG   | CucumberSpa    |
      | PG   | CucumberSpaB2B |


  #CASO DI TEST 4.3/4.4 - paginazione con più risultati
  @letturaDestinatario @useB2B
  Scenario: [RICERCA_RICEVUTE_5] Come destinatario recupero le notifiche ricevute sfogliando tutte le pagine dei risultati
    Given vengono create 5 notifiche con destinatario Mario Cucumber per la pa "Comune_Multi" e si aspetta che raggiungano l'elemento di timeline della notifica "REQUEST_ACCEPTED"
      | subject            | invio notifica paginazione |
      | senderDenomination | Comune di Palermo          |
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D) |
      | size      | 1          |
    And si sfogliano tutte le pagine della ricerca lato destinatario e si verifica che vengano raccolte almeno 5 notifiche






