Feature: Ricerca delle notifiche ricevute lato delegato

  #CASO DI TEST 5.1 - tutti i campi (obbligatori e opzionali) valorizzati correttamente
  #Nota: la response è tipizzata su LegalNotificationSearchResponse, quindi le notifiche bonarie
  #non possono mai comparire nell'elenco lato delegato: non serve un test dedicato, è garantito dal contratto API.
  @letturaDestinatario @useB2B
  Scenario: [RICERCA_RICEVUTE_DELEGATO_B2B_1] Come delegato ricerco le notifiche ricevute con tutti i filtri valorizzati
    Given "CucumberSpaB2B" rifiuta se presente la delega ricevuta "GherkinSrlB2B"
    Given "CucumberSpaB2B" viene delegato da "GherkinSrlB2B"
    And "CucumberSpaB2B" accetta la delega "GherkinSrlB2B"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrlB2B
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then "GherkinSrlB2B" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-21 |
      | iunMatch  | :actualIun |
#      | mandateId | :mandateId |
    Then "CucumberSpaB2B" visualizza l'elenco delle notifiche del delegante "GherkinSrlB2B" per comune "Comune_Multi"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-21 |
      | iunMatch  | :actualIun |
#      | mandateId | :mandateId |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
#      | communicationType | LEGAL      |
      | mandateId | :mandateId |


  #CASO DI TEST 5.3 - tutti i campi (obbligatori e opzionali) valorizzati correttamente
  #Nota: la response è tipizzata su LegalNotificationSearchResponse, quindi le notifiche bonarie
  #non possono mai comparire nell'elenco lato delegato: non serve un test dedicato, è garantito dal contratto API.
  @letturaDestinatario @useB2B
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
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-21 |
      | iunMatch  | :actualIun |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
#      | communicationType | LEGAL      |
      | iun | :actualIun |
    Then "<delegato>" visualizza l'elenco delle notifiche del delegante "<delegatore>" per comune "Comune_Multi"
      | startDate   | 2026-07-07     |
      | endDate     | 2026-07-21     |
      | status      | EFFECTIVE_DATE |
      | recipientId | :recipientUid  |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | sentAt             | 2026-07-07, 2026-07-21 |
      | notificationStatus | EFFECTIVE_DATE         |
      | recipients        | :recipientId          |
    Then "<delegato>" visualizza l'elenco delle notifiche del delegante "<delegatore>" per comune "Comune_Multi"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-21 |
      | group     | :group     |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | sentAt | 2026-07-07, 2026-07-21 |
      | group  | :group                 |
    Examples:
      | delegato       | delegatore |
      | CucumberSpa    | GherkinSrl    |
#      | CucumberSpaB2B | GherkinSrlB2B |


  #CASO DI TEST 5.1 - campo obbligatorio non valorizzato -> 400 KO
  @letturaDestinatario @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_DELEGATO_PG_3] Come destinatario non riesco a ricercare le notifiche ricevute se manca un campo obbligatorio
    Then "<destinatario>" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_Multi"
      | <campo> | NULL |
    Then si verifica che venga ritornato un errore di tipo "BAD REQUEST"
    Examples:
      | campo           | destinatario |
      | xPagopaPnUid    | CucumberSpa  |
      | xPagopaPnCxType | CucumberSpa  |
      | xPagopaPnCxId   | CucumberSpa  |
      | startDate       | CucumberSpa  |
      | endDate         | CucumberSpa  |
