Feature: Ricerca delle notifiche ricevute lato delegato

  #CASO DI TEST 5.1 - tutti i campi (obbligatori e opzionali) valorizzati correttamente
  #Nota: la response è tipizzata su LegalNotificationSearchResponse, quindi le notifiche bonarie
  #non possono mai comparire nell'elenco lato delegato: non serve un test dedicato, è garantito dal contratto API.
  @ricercaNotifiche @useB2B @deleghe2
  Scenario: [RICERCA_RICEVUTE_DELEGATO_B2B_1] Come delegato ricerco le notifiche ricevute con tutti i filtri valorizzati
  e le notifiche bonarie non devono essere presenti nell'elenco delle notifiche ricevute
    Given "CucumberSpaB2B" rifiuta se presente la delega ricevuta "GherkinSrlB2B"
    Given "CucumberSpaB2B" viene delegato da "GherkinSrlB2B"
    And "CucumberSpaB2B" accetta la delega "GherkinSrlB2B"

    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | MessaMora     |
      | recipientType   | PG            |
      | taxId           | 12666810299   |
      | denomination    | GherkinSrlB2B |
      | messageId       | ${NEW-IT}     |
      | digitalDomicile | tu@gmail.com  |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"

    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrlB2B
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then "CucumberSpaB2B" visualizza l'elenco delle notifiche del delegante "GherkinSrlB2B" per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
      | iunMatch  | :actualIun     |
      | mandateId | :mandateId     |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL      |
      | mandateId         | :mandateId |
      | iun               | :actualIun |
      | itemsFound        | 1          |
    Then "CucumberSpaB2B" visualizza l'elenco delle notifiche del delegante "GherkinSrlB2B" per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL |




  #CASO DI TEST 5.3 - tutti i campi (obbligatori e opzionali) valorizzati correttamente
  #Nota: la response è tipizzata su LegalNotificationSearchResponse, quindi le notifiche bonarie
  #non possono mai comparire nell'elenco lato delegato: non serve un test dedicato, è garantito dal contratto API.
  @ricercaNotifiche @useB2B @deleghe2
  Scenario Outline: [RICERCA_RICEVUTE_DELEGATO_PG_2] Come delegato ricerco le notifiche ricevute con tutti i filtri valorizzati
    Given "<delegato>" rifiuta se presente la delega ricevuta "<delegatore>"
    Given "<delegato>" viene delegato da "<delegatore>"
    And "<delegato>" accetta la delega "<delegatore>"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario <delegatore>
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then "<delegato>" visualizza l'elenco delle notifiche del delegante "<delegatore>" per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
      | iunMatch  | :actualIun     |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
#      | communicationType | LEGAL      |
      | iun | :actualIun |
    Then "<delegato>" visualizza l'elenco delle notifiche del delegante "<delegatore>" per comune "Comune_Multi"
      | startDate   | $DATE_ADD(-1D) |
      | endDate     | $DATE_ADD(1D)  |
      | status      | EFFECTIVE_DATE |
      | recipientId | :recipientUid  |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | sentAt             | $DATE_ADD(-1D), $DATE_ADD(1D) |
      | notificationStatus | EFFECTIVE_DATE                |
      | recipients         | :recipientId                  |
    # Parte commentata poichè affetta dal seguente bug di prod: https://pagopa.atlassian.net/browse/PN-20903
#    Then "<delegato>" visualizza l'elenco delle notifiche del delegante "<delegatore>" per comune "Comune_Multi"
#      | startDate | $DATE_ADD(-1D) |
#      | endDate   | $DATE_ADD(1D)  |
#      | group     | :group         |
#    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
#      | sentAt | $DATE_ADD(-1D), $DATE_ADD(1D) |
#      | group  | CONSISTENT                    |
    Then "<delegato>" visualizza l'elenco delle notifiche del delegante "<delegatore>" per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
      | senderId  | :senderId      |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | sender | $NOT_EMPTY |
    Examples:
      | delegato       | delegatore    |
      | CucumberSpa    | GherkinSrl    |
      | CucumberSpaB2B | GherkinSrlB2B |


  #CASO DI TEST 5.3/5.4 - paginazione con più risultati
  @ricercaNotifiche @useB2B @deleghe2
  Scenario: [RICERCA_RICEVUTE_DELEGATO_PG_4] Come delegato recupero le notifiche ricevute del delegante sfogliando tutte le pagine dei risultati
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given vengono create 5 notifiche con destinatario GherkinSrl per la pa "Comune_Multi" e si aspetta che raggiungano l'elemento di timeline della notifica "REQUEST_ACCEPTED"
      | subject            | invio notifica paginazione |
      | senderDenomination | comune di milano           |
    Then "CucumberSpa" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_Multi"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(1D)  |
      | size      | 1              |
    And si sfogliano tutte le pagine della ricerca lato destinatario e si verifica che vengano raccolte almeno 5 notifiche


  #CASO DI TEST 5.1 - campo obbligatorio non valorizzato -> 400 KO
  @ricercaNotifiche @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_DELEGATO_PG_3] Come destinatario non riesco a ricercare le notifiche ricevute se manca un campo obbligatorio
    Then "CucumberSpa" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_Multi"
      | <campo> | NULL |
    Then si verifica che venga ritornato un errore di tipo "BAD REQUEST"
    Examples:
      | campo           |
      | xPagopaPnUid    |
      | xPagopaPnCxType |
      | xPagopaPnCxId   |
      | startDate       |
      | endDate         |
