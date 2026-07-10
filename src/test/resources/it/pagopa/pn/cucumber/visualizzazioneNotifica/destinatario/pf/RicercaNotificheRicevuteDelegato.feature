Feature: Ricerca delle notifiche ricevute lato delegato

    #CASO DI TEST 5.1 - tutti i campi (obbligatori e opzionali) valorizzati correttamente
  #Nota: la response è tipizzata su LegalNotificationSearchResponse, quindi le notifiche bonarie
  #non possono mai comparire nell'elenco lato delegato: non serve un test dedicato, è garantito dal contratto API.
  @letturaDestinatario @useB2B
  Scenario: [RICERCA_RICEVUTE_DELEGATO_1] Come delegato ricerco le notifiche ricevute con tutti i filtri valorizzati
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then "CucumberSpa" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_1"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-10 |
      | iunMatch  | :actualIun |
      | mandateId | :mandateId |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
#      | communicationType | LEGAL      |
      | mandateId         | :mandateId |