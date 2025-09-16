@pari-institution-list
Feature: PARI - Portale registro dei beni

  Background:
    Given vengono generati tutti i token JWT necessari

  Scenario: [TC-INSTITUTION-1] Lato INVITALIA viene recuperata la lista di produttori abilitati al caricamento di prodotti
    Given viene usata l'utenza: INVITALIA
#    Given l'utente "ACCETTA" i TOS
    When viene recuperata la lista di istituzioni
    Then si controlla che la lista ritornata sia popolata correttamente
    # Si recupera il dettaglio per una sola istituzione tra le precedenti recuperate
    When viene recuperato il dettaglio di una specifica istituzione tra quelle recuperate precedentemente
    Then si controlla che il dettaglio dell'istituzione ritornata abbia tutti i campi validi
    # Si recupera il dettaglio passando un istitutionId non valido
    When si tenta di recuperare il dettaglio di una specifica istituzione con id: "aaa-bbb"
    Then la chiamata ha restituito status code: 400

  Scenario: [TC-INSTITUTION-2] Lato PRODUTTORE si verifica che non è possibile chiamare l'API per recuperare la lista di produttori abilitati al caricamento dei prodotti
    Given viene usata l'utenza: PRODUTTORE_1
    When viene recuperata la lista di istituzioni
    Then la chiamata ha restituito status code: 403
    # Si recupera il dettaglio per una sola istituzione
    When si tenta di recuperare il dettaglio di una specifica istituzione con id: "9ceffb6e-3f85-4a8c-8600-be475e319940"
    Then la chiamata ha restituito status code: 403

  Scenario: [TC-INSTITUTION-3] Lato INVITALIA viene recuperata la lista prodotti e le informazioni relative ad uno specifico prodotto
    Given viene usata l'utenza: INVITALIA
    When viene recuperata la lista di istituzioni
    Then viene recuperata la lista prodotti di una specifica istituzione tra quelle recuperate precedentemente
    And si verifica che il prodotto ritornato abbia tutti i campi validi

