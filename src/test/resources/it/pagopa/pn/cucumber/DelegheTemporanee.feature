#Per un corretto funzionamento della suite sono necessari diversi passaggi propedeutici:
  #1) su AWS, nella whiteList devono essere presente i taxId di Cristoforo Colombo (Mario Gherkin), Ettore Fieramosca (Mario Cucumber) e Galileo Galilei
  #2) è necessario che il tempo di scadenza di una notifica sia impostato a 7 minuti
  #3) Quando si esegue in locale, bisogna:
  #3.1) impostare il proprio IP tra quelli autorizzati sempre su AWS WAF (Web Authorization Filter). Cambia giornalmente
  # L'ip è recuperabile dai log CloudWatch, (cercare il gruppo denominato MandateMicroservicePublicIoAPI)
  #3.2) per poter richiamare il tool di generazione CIE è necessario:
  # 3.2.1 - aver scaricato AWS Toolkit (da cui impostare profilo e region)
  # 3.2.2 - cliccare le freccette verdi affianco allo scenario che si intende eseguire -> Modify run configuration -> AWS connection -> Selezionare pallino "Use the currently selected credential profile-region"
  #3.3) aprire il tunnel verso delivery

Feature: Deleghe Temporanee 15755

  Background:
    Given vengono settati i parametri per il tool CIE

  @delegheTemporanee @deleghe1
  #1-12-23-33-34(temp) ++ 11-30(perm)
  Scenario: [MANDATE_TEMP_HAPPY_PATH_1] Creazione e accettazione di una delega temporanea e visualizzazione notifica (scenario positivo)
    Given Mario Gherkin rifiuta l'eventuale delega permanente da parte di Mario Cucumber
    And viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
      | document           | SI                               |
      | payment_f24        | PAYMENT_F24_FLAT                 |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI VALIDI"
    And la delega temporanea è stata correttamente creata
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    And l'operazione non ha prodotto alcun errore
    Then la notifica può essere correttamente letta tramite appIo dal delegato Mario Gherkin
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega
    And il documento notificato può essere correttamente recuperato da "Mario Gherkin" con delega
    #35-36 (RIPROVO DOPO AVER FATTO SCADERE LA VALIDITA' DELLA DELEGA)
    When attendo 10 minuti affinché la "validità della delega" scada
    Then la notifica non può essere correttamente letta tramite appIo dal delegato Mario Gherkin
    Then la notifica non può essere correttamente letta da "Mario Gherkin" con delega
    And l'allegato "F24" non può essere correttamente recuperato da "Mario Gherkin" con delega
    And il documento notificato non può essere correttamente recuperato da "Mario Gherkin" con delega restituendo un errore "404"
    #DELEGA PERMANENTE (DOPO AVER TESTATO CON SUCCESSO QUELLA TEMPORANEA)
    Given Mario Gherkin rifiuta l'eventuale delega permanente da parte di Mario Cucumber
    When "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Then la lista di deleghe del delegato "Mario Gherkin" non contiene la delega temporanea creata
    And la lista di deleghe del delegante "Mario Cucumber" non contiene la delega temporanea creata

  @delegheTemporanee @deleghe1
  #2-13-24-32-33-34
  Scenario: [MANDATE_TEMP_HAPPY_PATH_2] Creazione e accettazione di una delega temporanea e visualizzazione notifica pur in presenza di delega permanente (scenario positivo)
    Given Mario Gherkin rifiuta l'eventuale delega permanente da parte di Mario Cucumber
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
      | document           | SI                               |
      | payment_f24        | PAYMENT_F24_FLAT                 |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI VALIDI"
    And la delega temporanea è stata correttamente creata
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    And l'operazione non ha prodotto alcun errore
    Then la notifica può essere correttamente letta tramite appIo dal delegato Mario Gherkin
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
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "QRCODE NON VALIDO"
    Then l'operazione restituisce codice 400

  #4
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_INEXISTENT_QR] Creazione senza successo di una delega temporanea (qrCode inesistente)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "QRCODE INESISTENTE"
    Then l'operazione restituisce codice 400

  #5
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_EMPTY_BODY] Creazione senza successo di una delega temporanea (empty request body)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "EMPTY REQUEST BODY"
    Then l'operazione restituisce codice 400

  #6
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_TAXID_NULL] Creazione senza successo di una delega temporanea (taxId null)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "TAXID NULL"
    Then l'operazione restituisce codice 400

  #7
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_DELEGA_ALREADY_EXISTENT] Creazione senza successo di una delega temporanea (delega già presente per coppia IUN-taxId); Viene fatta dunque scadere, si crea una nuova delega e la si accetta
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI VALIDI"
    And la delega temporanea è stata correttamente creata
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI VALIDI"
    Then l'operazione restituisce codice 409
    When attendo 5 minuti affinché la "finestra temporale per accettare la delega" scada
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    Then l'operazione restituisce codice 404
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    And l'operazione non ha prodotto alcun errore
    Then la notifica può essere correttamente letta tramite appIo dal delegato Mario Gherkin
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  #8
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_DELEGATE_SAME_AS_RECIPIENT] Creazione senza successo di una delega temporanea (destinatario e delegato coincidono)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When Mario Cucumber viene temporaneamente delegato da Mario Cucumber passando "DATI VALIDI"
    Then l'operazione restituisce codice 409

  #9 NON FATTIBILE (CREAZIONE FALLITA CAUSA TOKEN ERRATO: non sono previsti token)

  #10
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_OK_DESPITE_PERMANENT] Creazione con successo di una delega temporanea (nonostante ne esista una permanente)
    Given Mario Gherkin rifiuta l'eventuale delega permanente da parte di Mario Cucumber
    When "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI VALIDI"
    And la delega temporanea è stata correttamente creata

  #14
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_INEXISTENT_MANDATE] Accettazione senza successo di una delega temporanea (mandateId not found)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "MANDATE ID INESISTENTE"
    Then l'operazione restituisce codice 404

  #15
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_INVALID_MANDATE_ID] Accettazione senza successo di una delega temporanea (invalid mandateId)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "MANDATE ID NON VALIDO"
    Then l'operazione restituisce codice 400

  #16
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_EMPTY_MANDATE_ID] Accettazione senza successo di una delega temporanea (empty mandateId)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "MANDATE ID VUOTO"
    Then l'operazione restituisce codice 400

  #17
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_EMPTY_REQUEST] Accettazione senza successo di una delega temporanea (empty request body)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "EMPTY REQUEST BODY"
    Then l'operazione restituisce codice 400

  #38
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_CREATION_FAILED_DIFFERENT_TAXID_USERID] Creazione senza successo di una delega temporanea (taxId e lollipopUserId diversi)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "CX TAX ID E LOLLIPOP USER ID NON COINCIDENTI"
    Then l'operazione restituisce codice 403

  #20
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_BECAUSE_ALREADY_ACCEPTED] Accettazione senza successo di una delega temporanea già accettata
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    And l'operazione non ha prodotto alcun errore
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    Then l'operazione restituisce codice 400

  #21 NON FATTIBILE (ACCETTAZIONE FALLITA CAUSA TOKEN ERRATO: non sono previsti token)

  #22
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_BECAUSE_EXPIRED] Accettazione senza successo di una delega temporanea scaduta
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    When attendo 5 minuti affinché la "finestra temporale per accettare la delega" scada
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    Then l'operazione restituisce codice 404

  #25
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_WRONG_MRTD] Accettazione senza successo di una delega temporanea (MRTD CIE errato)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "MRTD DATA CIE ERRATO"
    Then l'operazione restituisce codice 422

  #26
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_CIE_EXPIRED] Accettazione senza successo di una delega temporanea (CIE scaduta)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI DI UNA CIE SCADUTA"
    Then l'operazione restituisce codice 422

  #27
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_CIE_NOT_OF_RECIPIENT] Accettazione senza successo di una delega temporanea (CIE diversa da destinatario)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI CIE DI UTENTE DIVERSO DAL DESTINATARIO"
    Then l'operazione restituisce codice 422

  #28
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_WRONG_NONCE] Accettazione senza successo di una delega temporanea (Nonce CIE errato)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "SIGNED NONCE ERRATO"
    Then l'operazione restituisce codice 422

  #29
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_WRONG_NIS] Accettazione senza successo di una delega temporanea (NIS CIE errato)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    When la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "NIS DATA CIE ERRATO"
    Then l'operazione restituisce codice 422

  #31
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_API_B2B] Accettazione senza successo di una delega temporanea (accettazione tramite api b2b)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    When "Mario Gherkin" tenta di accettare la delega temporanea richiamando l'api b2b
    Then l'operazione restituisce codice 400

  #37
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_120_GIORNI] Accettazione senza successo di una delega temporanea che permette la visualizzazione di una notifica più vecchia di 120 giorni
    Given "Comune_Multi" recupera lato web PA una notifica perfezionata inviata tra 200 e 120 giorni fa con destinatario Mario Cucumber
    When Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI VALIDI"
    And la delega temporanea è stata correttamente creata
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "DATI VALIDI"
    And l'operazione non ha prodotto alcun errore
    Then la notifica può essere correttamente letta tramite appIo dal delegato Mario Gherkin
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  #39
  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED_DIFFERENT_TAXID_USERID] Accettazione senza successo di una delega temporanea (taxId e userId non coincidono)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And Mario Gherkin viene temporaneamente delegato da Mario Cucumber passando "DATI_VALIDI"
    And la delega temporanea è stata correttamente creata
    And la delega temporanea di Mario Cucumber viene accettata da Mario Gherkin passando "CX TAX ID E LOLLIPOP USER ID NON COINCIDENTI"
    Then l'operazione restituisce codice 400

  Scenario: [DEBUGONLY]
    #STEP PER GENERARE DATI CIE
#    Given DEBUGONLY test cie utente "FRMTTR76M06B715E" nonce "00000" con "DATI CIE DI UTENTE DIVERSO DAL DESTINATARIO"
#    Given DEBUGONLY test cie utente "FRMTTR76M06B715E" nonce "00000" con "DATI DI UNA CIE SCADUTA"
    Given DEBUGONLY test cie utente "FRMTTR76M06B715E" nonce "00000" con "DATI VALIDI"
    #STEP PER RECUPERARE DA POST CREAZIONE (IN CASO QUALCOSA SCADA)
#    Given imposto lo iun di SharedSteps a "TODO" e la pa a "Comune_Multi"
#    And DEBUGONLY il mandate in uso è quello con id "ba5dc3ec-b04a-40cf-baaf-3e654fb6cce5" e verificationCode "87980"

