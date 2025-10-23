Feature: Deleghe Temporanee 15755

  Scenario: TODO_REMOVE_ONLY_FOR_TESTING_AND_DEBUGGING
    Given calcolo il qrCode dello notifica con iun ""

  @delegheTemporanee
  #1-12-23-33-34(temp) ++ 11-30(perm)
  Scenario: [MANDATE_TEMP_HAPPY_PATH_1] Creazione e accettazione di una delega temporanea e visualizzazione notifica (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
      | document           | SI                               |
      | payment_f24        | PAYMENT_F24_FLAT                 |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "DATI VALIDI"
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    And l'operazione non ha prodotto alcun errore
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega
    And il documento notificato può essere correttamente recuperato da "Mario Gherkin" con delega
    #35-36 (RIPROVO DOPO AVER FATTO SCADERE LA DELEGA)
    When la delega viene fatta scadere
    Then la notifica non può essere correttamente letta da "Mario Gherkin" con delega
    And l'allegato "F24" non può essere correttamente recuperato da "Mario Gherkin" con delega
    And il documento notificato non può essere correttamente recuperato da "Mario Gherkin" con delega restituendo un errore "404"
    #DELEGA PERMANENTE (DOPO AVER TESTATO CON SUCCESSO QUELLA TEMPORANEA)
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Then la lista di deleghe del delegato "Mario Gherkin" non contiene la delega temporanea creata
    And la lista di deleghe del delegante "Mario Cucumber" non contiene la delega temporanea creata

  @delegheTemporanee
  #2-13-24-32-33-34
  Scenario: [MANDATE_TEMP_HAPPY_PATH_2] Creazione e accettazione di una delega temporanea e visualizzazione notifica pur in presenza di delega permanente (scenario positivo)
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
      | document           | SI                               |
      | payment_f24        | PAYMENT_F24_FLAT                 |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "DATI VALIDI"
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    And l'operazione non ha prodotto alcun errore
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega
    And il documento notificato può essere correttamente recuperato da "Mario Gherkin" con delega
    And la lista di deleghe del delegato "Mario Gherkin" non contiene la delega temporanea creata
    And la lista di deleghe del delegante "Mario Cucumber" non contiene la delega temporanea creata

  #3
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_INVALID_QR] Creazione senza successo di una delega temporanea (invalid qrCode)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "QRCODE NON VALIDO"
    Then l'operazione restituisce codice 400

  #4
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_INEXISTENT_QR] Creazione senza successo di una delega temporanea (qrCode inesistente)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "QRCODE INESISTENTE"
    Then l'operazione restituisce codice 400

  #5
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_EMPTY_BODY] Creazione senza successo di una delega temporanea (empty request body)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "EMPTY REQUEST BODY"
    Then l'operazione restituisce codice 400

  #6
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_TAXID_NULL] Creazione senza successo di una delega temporanea (taxId null)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "TAXID NULL"
    Then l'operazione restituisce codice 400

  #7
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_DELEGA_ALREADY_EXISTENT] Creazione senza successo di una delega temporanea (delega già presente per coppia IUN-taxId)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "DATI VALIDI"
    And la delega temporanea è stata correttamente creata
    When Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "DATI VALIDI"
    Then l'operazione restituisce codice 409

  #8
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_DELEGATE_SAME_AS_RECIPIENT] Creazione senza successo di una delega temporanea (destinatario e delegato coincidono)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When Mario Cucumber viene temporaneamente delegato da "Mario Cucumber" passando "DATI VALIDI"
    Then l'operazione restituisce codice 409

  #9 NON FATTIBILE (CREAZIONE FALLITA CAUSA TOKEN ERRATO: non sono previsti token)

  #10
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_OK_DESPITE_PERMANENT] Creazione con successo di una delega temporanea (nonostante ne esista una permanente)
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When Mario Cucumber viene temporaneamente delegato da "Mario Cucumber" passando "DATI VALIDI"
    And la delega temporanea è stata correttamente creata

  #38
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_DIFFERENT_TAXID_USERID] Creazione senza successo di una delega temporanea (taxId e lollipopUserId diversi)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "CX TAX ID E LOLLIPOP USER ID NON COINCIDENTI"
    Then l'operazione restituisce codice 403

  #20
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_BECAUSE_ALREADY_ACCEPTED] Accettazione senza successo di una delega temporanea già accettata
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    And l'operazione non ha prodotto alcun errore
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    Then l'operazione restituisce codice 400

  #21 non fattibile (chiamata non prevede token)

  #22
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_BECAUSE_ALREADY_ACCEPTED] Accettazione senza successo di una delega temporanea già accettata
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    When la delega viene fatta scadere
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    Then l'operazione restituisce codice 404

  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_ALL_CASES] Accettazione senza successo di una delega temporanea
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    #14
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "MANDATE ID INESISTENTE"
    Then l'operazione restituisce codice 404
    #15
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "MANDATE ID NON VALIDO"
    Then l'operazione restituisce codice 400
    #16
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "MANDATE ID VUOTO"
    Then l'operazione restituisce codice 400
    #17
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "EMPTY REQUEST BODY"
    Then l'operazione restituisce codice 400
    #18 TODO: signedNonce, nisData, mrtdData inesistenti
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "HEADER E BODY INESISTENTI"
    Then l'operazione restituisce codice 400
    #19 TODO: signedNonce, nisData, mrtdData non validi
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "HEADER E BODY NON VALIDI"
    Then l'operazione restituisce codice 400
    #25 CIE non valida (TODO: ovvero ???)
    #26
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI DI UNA CIE SCADUTA"
    Then l'operazione restituisce codice 422
    #27
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI CIE DI UTENTE DIVERSO DAL DESTINATARIO"
    Then l'operazione restituisce codice 422
    #28
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "SIGNED NONCE ERRATO"
    Then l'operazione restituisce codice 422
    #29
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "NIS DATA CIE ERRATO"
    Then l'operazione restituisce codice 422
    #31
    When "Mario Gherkin" tenta di accettare la delega temporanea richiamando l'api b2b
    Then l'operazione restituisce codice 400
    #39
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "CX TAX ID E LOLLIPOP USER ID NON COINCIDENTI"
    Then l'operazione restituisce codice 403

  #37
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_120_GIORNI] Accettazione senza successo di una delega temporanea che permette la visualizzazione di una notifica più vecchia di 120 giorni
    Given "Comune_Multi" recupera lato web PA una notifica vecchia 120 o più giorni inviata a Mario Cucumber
    When Mario Gherkin viene temporaneamente delegato da "Mario Cucumber" passando "DATI VALIDI"
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    And l'operazione non ha prodotto alcun errore
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega