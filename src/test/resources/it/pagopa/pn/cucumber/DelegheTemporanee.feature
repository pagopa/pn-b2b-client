Feature: Deleghe Temporanee 15755

  @delegheTemporanee
  #1-12-23-33-34(temp) ++ 11-30(perm)
  Scenario: [MANDATE_TEMP_HAPPY_PATH_1] Creazione e accettazione di una delega temporanea e visualizzazione notifica (scenario positivo)
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    #TODO aggiungere allegati e documenti di pagamento
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When "Mario Gherkin" viene temporaneamente delegato da "Mario Cucumber"
    Then l'operazione di delega temporanea restituisce codice "200"
    When la delega temporanea viene accettata da "Mario Gherkin"
    Then "Mario Gherkin" può visualizzare il dettaglio della notifica
    And "Mario Gherkin" può scaricare i documenti di pagamento allegati
    And "Mario Gherkin" può scaricare i documenti allegati
    #DELEGA PERMANENTE (DOPO AVER TESTATO CON SUCCESSO QUELLA TEMPORANEA)
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    When "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    #TODO scrivere meglio, il senso è che l'api /mandate/api/v1/mandates-by-delegate deve mostrare solo quelle permanenti
    Then la delega temporanea non figura tra le deleghe del delegato "Mario Gherkin"
    #TODO scrivere meglio, il senso è che l'api /mandate/api/v1/mandates-by-delegator deve mostrare solo quelle permanenti
    Then la delega temporanea non figura tra le deleghe del delegante "Mario Cucumber"

  @delegheTemporanee
  #2-13-24-32-33-34
  Scenario: [MANDATE_TEMP_HAPPY_PATH_2] Creazione e accettazione di una delega temporanea e visualizzazione notifica pur in presenza di delega permanente (scenario positivo)
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    #TODO aggiungere allegati e documenti di pagamento
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When "Mario Gherkin" viene temporaneamente delegato da "Mario Cucumber"
    Then l'operazione di delega temporanea restituisce codice "200"
    When la delega temporanea viene accettata da "Mario Gherkin"
    #TODO scrivere meglio, il senso è che l'api /mandate/api/v1/mandates-by-delegate deve mostrare solo quelle permanenti
    Then la delega temporanea non figura tra le deleghe del delegato "Mario Gherkin"
    #TODO scrivere meglio, il senso è che l'api /mandate/api/v1/mandates-by-delegator deve mostrare solo quelle permanenti
    Then la delega temporanea non figura tra le deleghe del delegante "Mario Cucumber"
    Then "Mario Gherkin" può visualizzare il dettaglio della notifica
    And "Mario Gherkin" può scaricare i documenti di pagamento allegati
    And "Mario Gherkin" può scaricare i documenti allegati

  @delegheTemporanee
  #3-4-5-6-7-8-9-38
  Scenario: [MANDATE_TEMP_CREATION_FAILED] Creazione senza successo di una delega temporanea
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #/mandate/api/v1/io/mandate
    #3
    When "Mario Gherkin" viene temporaneamente delegato da "Mario Cucumber" passando "QRCODE NON VALIDO"
    Then l'operazione di delega temporanea restituisce codice "400"
    #4
    When "Mario Gherkin" viene temporaneamente delegato da "Mario Cucumber" passando "QRCODE INESISTENTE"
    Then l'operazione di delega temporanea restituisce codice "400"
    #5
    When "Mario Gherkin" viene temporaneamente delegato da "Mario Cucumber" passando "HEADER E BODY VUOTI"
    Then l'operazione di delega temporanea restituisce codice "401"
    #6
    When "Mario Gherkin" viene temporaneamente delegato da "Mario Cucumber" passando "HEADER CON PARAMETRI OPZIONALI VUOTI"
    Then l'operazione di delega temporanea restituisce codice "400"
    #7 TODO (DELEGA TEMP GIA' ESISTENTE)
    When "Mario Gherkin" viene temporaneamente delegato da "Mario Cucumber""
    Then l'operazione di delega temporanea restituisce codice "400"
    #8 (DELEGATO COINCIDE CON DESTINATARIO)
    When "Mario Cucumber" viene temporaneamente delegato da "Mario Cucumber"
    Then l'operazione di delega temporanea restituisce codice "409"
    #9 TODO (PRIMA SETTARE TOKEN NON VALIDO)...inoltre, perchè 401 e non 403 ?
    When "Mario Gherkin" viene temporaneamente delegato da "Mario Cucumber"
    Then l'operazione di delega temporanea restituisce codice "401"
    #38
    When "Mario Gherkin" viene temporaneamente delegato da "Mario Cucumber" passando "CX TAX ID E LOLLIPOP USER ID NON COINCIDENTI"
    Then l'operazione di delega temporanea restituisce codice "403"

  @delegheTemporanee
  Scenario: [MANDATE_TEMP_ACCEPTATION_FAILED] Accettazione senza successo di una delega temporanea
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When "Mario Gherkin" viene temporaneamente delegato da "Mario Cucumber"
    And l'operazione di delega temporanea restituisce codice "200"
    #14
    And la delega temporanea viene accettata da "Mario Gherkin" passando "MANDATE ID INESISTENTE"
    Then l'accettazione della delega restituisce codice "404"
    #15
    And la delega temporanea viene accettata da "Mario Gherkin" passando "MANDATE ID NON VALIDO"
    Then l'accettazione della delega restituisce codice "400"
    #16
    And la delega temporanea viene accettata da "Mario Gherkin" passando "MANDATE ID VUOTO"
    Then l'accettazione della delega restituisce codice "400"
    #17
    And la delega temporanea viene accettata da "Mario Gherkin" passando "HEADER E BODY VUOTI"
    Then l'accettazione della delega restituisce codice "400"
    #18 TODO: signedNonce, nisData, mrtdData inesistenti
    And la delega temporanea viene accettata da "Mario Gherkin" passando "HEADER E BODY INESISTENTI"
    Then l'accettazione della delega restituisce codice "400"
    #19 TODO: signedNonce, nisData, mrtdData non validi
    And la delega temporanea viene accettata da "Mario Gherkin" passando "HEADER E BODY NON VALIDI"
    Then l'accettazione della delega restituisce codice "400"
    #20 temporanea accettata, si prova ad accettarla di nuovo, va in 400
    #21 TODO (PRIMA SETTARE TOKEN NON VALIDO)...inoltre, perchè 401 e non 403 ?
    And la delega temporanea viene accettata da "Mario Gherkin"
    Then l'accettazione della delega restituisce codice "401"
    #22 TODO (PRIMA RI-SETTARE TOKEN VALIDO, poi mettere il thread in pausa per aspettare che scada)
    And la delega temporanea viene accettata da "Mario Gherkin"
    Then l'accettazione della delega restituisce codice "404"
    #25 CIE non valida
    #26 CIE scaduta
    #27 CIE differente da destinatario notifica
    #28 Nonce diverso da quello usato in fase di creazioneDelega
    #29 Nonce non firmato correttamente
    #30 usata api b2b /mandate/api/v1/mandate/{mandateId}/accept
    #39 x-pagopa-cx-taxid e x-pagopa-lollipop-user-id con valori differenti
