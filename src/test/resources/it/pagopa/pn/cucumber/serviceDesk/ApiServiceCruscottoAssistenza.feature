Feature: Api Service Cruscotto Assistenza

  #CE02.1 Come operatore devo accedere all’elenco di tutte le PA che hanno effettuato on boarding
  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.1_1] Invocazione del servizio e verifica restituzione array di oggetti con nome e id della PA
    Given l'operatore richiede l'elenco di tutte le PA che hanno effettuato on boarding
    Then Il servizio risponde con esito positivo con la lista delle PA


  # API-SERVICE-CA_CE02.2_2
  # API-SERVICE-CA_CE02.2_3
  # API-SERVICE-CA_CE02.2_6
  # API-SERVICE-CA_CE02.2_4
  # API-SERVICE-CA_CE02.2_7,
  # API-SERVICE-CA_CE02.2_10,
  # API-SERVICE-CA_CE02.2_11,
  # API-SERVICE-CA_CE02.2_13,
  # API-SERVICE-CA_CE02.2_13
  #API-SERVICE-CA_CE02.2_15
  # API-SERVICE-CA_CE02.2_16
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2] Come operatore devo accedere all’elenco di tutti i messaggi di cortesia inviati con:
  1) taxId vuoto
  2) non formalmente corretto
  3) ma senza endDate - senza startDate - con recipientType vuoto -senza recipientType - recipientType valorizzato correttamente ma senza taxId - searchPageSize = 0 - searchPageSize = 51 - con endDate < startDate - searchNextPagesKey = 51
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "<ERROR>"
    Examples:
      | TAX_ID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   | ERROR |
      | VUOTO         | PF             | NULL             | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | ERRATO        | PF             | NULL             | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | Mario Gherkin | NULL           | NULL             | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | Mario Gherkin | NULL           | NULL             | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | NULL          | PF             | NULL             | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | Mario Gherkin | PF             | 0                | NULL                 | 2023-10-01 | 2023-12-01 | 400   |
      | Mario Gherkin | PF             | 51               | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | Mario Gherkin | PF             | 50               | 51                   | 2023-01-01 | 2023-12-01 | 500   |
      | Mario Gherkin | PF             | 50               | 51                   | 2023-01-01 | 2023-12-01 | 500   |
      | Mario Gherkin | PF             | 50               | NULL                 | NULL       | 2023-12-01 | 400   |
      | Mario Gherkin | PF             | 50               | NULL                 | 2023-01-01 | NULL       | 400   |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]|
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER_MIN","element":"_searchNotificationsFromTaxId.size","detail":"must be greater than or equal to 1"}]}
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER_MAX","element":"_searchNotificationsFromTaxId.size","detail":"must be less than or equal to 50"}]}
   #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'startDate' when calling searchNotificationsFromTaxId null
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'endDate' when calling searchNotificationsFromTaxId null


  # API-SERVICE-PG-CA_CE02.2_8,
  # API-SERVICE-PG-CA_CE02.2_8,
  # API-SERVICE-CA_CE02.2_9,
  # API-SERVICE-CA_CE02.2_12
  #Response 200 OK
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_8] PG - PF Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    Examples:
      | TAX_ID         | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Cucumber | PF             | 10               | NULL                 | 2024-01-01 | 2024-08-30 |
      | CucumberSpa    | PG             | 10               | NULL                 | 2024-01-01 | 2024-08-30 |
      | Mario Cucumber | PF             | 1                | NULL                 | 2024-01-01 | 2024-08-30 |
      | Mario Cucumber | PF             | 50               | NULL                 | 2024-01-01 | 2024-08-30 |


  # API-SERVICE-CA_CE02.3_17         #"errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}
  # API-SERVICE-CA_CE02.3_17_1       #"errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]}"
  # API-SERVICE-CA_CE02.3_18         #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 0 and 32"}]
  # API-SERVICE-PG-CA_CE02.3_18      #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 0 and 32"}]
  # API-SERVICE-CA_CE02.3_19         #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]
  # API-SERVICE-CA_CE02.3_21         #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_KO] Come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con:
  1) taxId vuoto
  2) taxId null
  3) taxId errato (PF)
  4) taxId errato (PG)
  5) recipientType vuoto
  6) recipientType null
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAX_ID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"
    Examples:
      | TAX_ID        | RECIPIENT_TYPE |
      | VUOTO         | PF             |
      | NULL          | PF             |
      | ERRATO        | PF             |
      | ERRATO        | PG             |
      | Mario Gherkin | VUOTO          |
      | Mario Gherkin | NULL           |


  # API-SERVICE-PG-CA_CE02.3_23
  # API-SERVICE-PG-CA_CE02.3_24
  #  Response 200 OK
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_OK] Invocazione del servizio con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAX_ID>" e recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente
    Examples:
      | TAX_ID        | RECIPIENT_TYPE |
      | Mario Gherkin | PF             |
      | CucumberSpa   | PG             |


  # API-SERVICE-CA_CE02.4_24      #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]
  # API-SERVICE-CA_CE02.4_24_1    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]
  # API-SERVICE-CA_CE02.4_25      #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}
  # API-SERVICE-CA_CE02.4_26      #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]}
  # API-SERVICE-CA_CE02.4_28      #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]}
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_KO] Come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con:
  1) taxId vuoto
  2) taxId null
  3) taxId errato
  4) recipientType vuoto
  5) recipientType null
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"
    Examples:
      | TAX_ID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | VUOTO         | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
      | NULL          | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
      | ERRATO        | PF             | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
      | Mario Gherkin | VUOTO          | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
      | Mario Gherkin | NULL           | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |


  # API-SERVICE-CA_CE02.4_30
  # API-SERVICE-PG-CA_CE02.4_30
  #Response 200 OK
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_OK] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    Examples:
      | TAX_ID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
      | CucumberSpa   | PG             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |


  # API-SERVICE-CA_CE02.5_39        #405 Method Not Allowed
  # API-SERVICE-CA_CE02.5_39_1      #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getNotificationFromIUN null
  # API-SERVICE-CA_CE02.5_40        #404 Not Found
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_KO] Come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) con:
  1) IUN vuoto
  2) IUN null
  3) IUN inesistente
    Given come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) "<IUN>"
    Then il servizio risponde con errore "<ERROR>"
    Examples:
      | IUN                       | ERROR |
      | VUOTO                     | 405   |
      | NULL                      | 400   |
      | JRDT-XAPH-JQYW-202312-J-1 | 404   |


  # API-SERVICE-CA_CE02.5_42
  # API-SERVICE-PG-CA_CE02.5_42
  #Response 200 OK
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_OK] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio notifica
    And Il servizio risponde correttamente
    Examples:
      | TAX_ID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 10               | NULL                 | 2023-01-01 | 2023-12-01 |
      | CucumberSpa   | PG             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |


  # API-SERVICE-CA_CE02.6_43        #Response 405 METHOD_NOT_ALLOWED
  # API-SERVICE-CA_CE02.6_44        #Response 404 NOT_FOUND
  # API-SERVICE-CA_CE02.6_46        #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}
  # API-SERVICE-CA_CE02.6_46_1      #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]}
  # API-SERVICE-CA_CE02.6_47        #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}
  # API-SERVICE-CA_CE02.6_48        #Response 404 NOT_FOUND (PF)
  # API-SERVICE-PG-CA_CE02.6_48     #Response 404 NOT_FOUND (PG)
  @cruscottoAssistenza
  Scenario Outline:  [API-SERVICE-CA_CE02.6_KO] Come operatore devo accedere alla storia (timeline) di una notifica di cui conosco l’identificativo (IUN) con:
  1) taxId valorizzato, IUN vuoto
  2) taxId valorizzato, IUN inesistente
  3) taxId vuoto, IUN corretto
  4) taxId null, IUN corretto
  5) IUN corretto, taxId errato
  6) IUN corretto, taxId non corrispondente al destinatario della notifica (PF)
  7) IUN corretto, taxId non corrispondente al destinatario della notifica (PG)
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "<taxIdRecupero>" e iun "<IUN>"
    And il servizio risponde con errore "<ERROR>"
    Examples:
      | TAX_ID        | taxIdRecupero  | IUN                       | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   | ERROR |
      | Mario Gherkin | Mario Gherkin  | VUOTO                     | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | Mario Gherkin | Mario Gherkin  | JRDT-ZAPH-JQYW-202312-J-1 | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 404   |
      | Mario Gherkin | VUOTO          |                           | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | Mario Gherkin | NULL           |                           | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | Mario Gherkin | ERRATO         |                           | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | Mario Gherkin | Mario Cucumber |                           | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 404   |
      | CucumberSpa   | GherkinSrl     |                           | PG             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 404   |


  # API-SERVICE-CA_CE02.6_49
  # API-SERVICE-PG-CA_CE02.6_49
  #Response 200 OK
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_OK] Invocazione del servizio con IUN (notifica mono destinatario)  corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "<TAX_ID>" e iun ""
    Then Il servizio risponde correttamente
    Examples:
      | TAX_ID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
      | CucumberSpa   | PG             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |


  #CE02.7 Come operatore devo accedere alla storia (timeline) di una notifica multi-destinatario di cui conosco l’identificativo (IUN)
  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_50] Invocazione del servizio con IUN vuoto
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "Mario Gherkin" e iun "VUOTO" per il  destinatario 0
    And il servizio risponde con errore "400"

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_50_1] Invocazione del servizio con IUN null
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "Mario Gherkin" e iun "NULL" per il  destinatario 0
    And il servizio risponde con errore "400"
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getTimelineOfIUNAndTaxId null

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_51] Invocazione del servizio con IUN inesistente
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "Mario Gherkin" e iun "JRDT-XAPH-JQYW-202312-J-1" per il  destinatario 0
    And il servizio risponde con errore "404"
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_53] Invocazione del servizio con IUN corretto ma taxId vuoto
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "VUOTO" e iun "JRDT-XAPH-JQYW-202312-J-1" per il  destinatario 0
    And il servizio risponde con errore "400"
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_54] Invocazione del servizio con IUN corretto ma taxId non formalmente corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "ERRATO" e iun "JRDT-XAPH-JQYW-202312-J-1" per il  destinatario 0
    And il servizio risponde con errore "400"
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_55] Invocazione del servizio con IUN corretto ma taxId non corrispondente al destinatario della notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "ADA" e iun "NO_SET" per il  destinatario 0
    And il servizio risponde con errore "404"
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_56] Invocazione del servizio con IUN (notifica multi destinatario) corretto e verifica risposta
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "Mario Gherkin" e iun "NO_SET" per il  destinatario 0
    And invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "Mario Cucumber" e iun "NO_SET" per il  destinatario 1
    And Il servizio risponde correttamente

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_56_1] Invocazione del servizio con IUN (notifica mono destinatario) corretto e verifica risposta PN-9995
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then verifica IsMultiRecipients nel dettaglio notifica


  # API-SERVICE-CA_CE02.8_57      #Errore: 405 METHOD_NOT_ALLOWED 405 Method Not Allowed
  # API-SERVICE-CA_CE02.8_57_1    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getDocumentsOfIUN null
  # API-SERVICE-CA_CE02.8_58      #Response 404 NOT_FOUND
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_KO] Come operatore devo effettuare un check sulla disponibilità (nel momento della verifica), validità e dimensione degli allegati. (cancellazione a 120 gg) con:
  1) IUN vuoto
  2) IUN null
  3) IUN inesistente
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAX_ID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "<ERROR>"
    Examples:
      | IUN                       | TAX_ID        | RECIPIENT_TYPE | ERROR |
      | VUOTO                     | Mario Gherkin | PF             | 400   |
      | NULL                      | Mario Gherkin | PF             | 400   |
      | JZTK-MGAH-TVKL-202311-X-1 | Mario Gherkin | PF             | 404   |

  # Response 200 OK
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_60] Invocazione del servizio con IUN esistente (notifica emessa < 120 gg) e verifica risposta
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAX_ID>"  recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente con presenza di allegati "true"
    Examples:
      | IUN    | TAX_ID        | RECIPIENT_TYPE |
      | NO_SET | Mario Gherkin | PF             |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_58_1] Invocazione del servizio con IUN esistente e notifica annullata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAX_ID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"
    Examples:
      | IUN    | TAX_ID        | RECIPIENT_TYPE |
      | NO_SET | Mario Gherkin | PF             |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.8_60] Invocazione del servizio con IUN esistente (notifica emessa < 120 gg) e verifica risposta
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAX_ID>"  recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente con presenza di allegati "true"
    Examples:
      | IUN    | TAX_ID      | RECIPIENT_TYPE |
      | NO_SET | CucumberSpa | PG             |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_61] Invocazione del servizio con IUN esistente (notifica emessa > 120 gg) e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "NO_SET" e taxId "<TAX_ID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "410"
    Examples:
      | TAX_ID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | NULL             | NULL                 | 2024-04-01 | 2024-04-30 |
    #Errore: 400 BAD_REQUEST 400 Bad Request: [{"type":null,"status":400,"title":"ERROR_ON_DELIVERY_CLIENT","detail":"See logs for details in PN-SERVICE-DESK","traceId":"Root=1-6585bfef-39e3629554030a8d73dcf647","timestamp":"2023-12-22T16:57:19.95458306Z","errors":[]}] null


  # API-SERVICE-CA_CE02.8_62
  # API-SERVICE-PG-CA_CE02.8_62
  # API-SERVICE-CA_CE02.8_63
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.8_KO_2] Invocazione del servizio con:
  1) IUN esistente, recipientType corretto ma con recipientTaxId non corrispondente al destinatario della notifica (PF)
  2) IUN esistente, recipientType corretto ma con recipientTaxId non corrispondente al destinatario della notifica (PG)
  3) IUN esistente, ma recipientType non coerente rispetto al recipientTaxId della notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario <DESTINATARIO>
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAX_ID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"
    Examples:
      | DESTINATARIO   | IUN    | TAX_ID         | RECIPIENT_TYPE |
      | CucumberSpa    | NO_SET | Mario Cucumber | PF             |
      | Mario Cucumber | NO_SET | CucumberSpa    | PG             |
      | Mario Cucumber | NO_SET | Mario Gherkin  | PG             |
      #Response 404 NOT_FOUND


  #CE02.11 Come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale
  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.11_79] Invocazione del servizio con paId vuoto
    Given come operatore devo accedere alla lista di notifiche depositate che rientrano nei seguenti criteri:
      | paId           | VUOTO      |
      | searchPageSize | 1          |
      | startDate      | 2023-01-01 |
      | endDate        | 2023-12-01 |
    Then il servizio risponde con errore "400"
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"id","detail":"size must be between 1 and 50"}]}]

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.11_80] Invocazione del servizio con paId inesistente
    Given come operatore devo accedere alla lista di notifiche depositate che rientrano nei seguenti criteri:
      | paId           | 4db941cf-17e1-4751-9b7b |
      | searchPageSize | 1                       |
      | startDate      | 2023-01-01              |
      | endDate        | 2023-12-01              |
    Then Il servizio risponde correttamente
    #  Response 200 OK
    #{"results":[],"moreResult":false,"nextPagesKey":[]}

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.11_84] Invocazione del servizio con paId correttamente valorizzato e verifica risposta
    Given l'operatore richiede l'elenco di tutte le PA che hanno effettuato on boarding
    And Il servizio risponde con esito positivo con la lista delle PA
    And come operatore devo accedere alla lista di notifiche depositate che rientrano nei seguenti criteri:
      | paId           | NO_SET     |
      | searchPageSize | 1          |
      | startDate      | 2023-01-01 |
      | endDate        | 2023-12-01 |
    Then Il servizio risponde correttamente
    #  Response 200 OK a95dace4-4a47-4149-a814-0e669113ce40
    #{"results":[],"moreResult":false,"nextPagesKey":[]}


  # API-SERVICE-CA_CE02.12_90     #Errore: 405 METHOD_NOT_ALLOWED 405 Method Not Allowed
  # API-SERVICE-CA_CE02.12_90_1   #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getNotificationFromIUN null
  # API-SERVICE-CA_CE02.12_91     #Response 404 NOT_FOUND
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_KO] Come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) fornito dall'Ente mittente con:
  1) IUN vuoto
  2) IUN null
  3) IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun "<IUN>"
    Then il servizio risponde con errore "<ERROR>"
    Examples:
      | TAX_ID        | IUN                       | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   | ERROR |
      | Mario Gherkin | VUOTO                     | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 405   |
      | Mario Gherkin | NULL                      | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | Mario Gherkin | JZTK-MGAH-TVZL-202311-X-1 | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 404   |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_93] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun ""
    Then Il servizio risponde correttamente
    Examples:
      | TAX_ID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
  #Response 200 OK


  # API-SERVICE-CA_CE02.13_94     #code":"PN_GENERIC_INVALIDPARAMETER_SIZE","element":"_getNotificationFromIUN.iun","detail":"size must be between 25 and 25"
  # API-SERVICE-CA_CE02.13_94_1   #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getTimelineOfIUN null
  # API-SERVICE-CA_CE02.13_95     #Response 404 NOT_FOUND
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_KO] Come operatore devo accedere alla storia (timeline) di una notifica di cui conosco l’identificativo (IUN) fornito dall'Ente mittente con:
  1) IUN vuoto
  2) IUN null
  3) IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun "<IUN>"
    Then il servizio risponde con errore "<ERROR>"
    Examples:
      | IUN                       | TAX_ID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   | ERROR |
      | VUOTO                     | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | NULL                      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 400   |
      | JZTK-MGAH-TVKL-202311-X-1 | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 | 404   |


  #Response 200 OK
  # API-SERVICE-CA_CE02.13_97
  # API-SERVICE-PG-CA_CE02.13_97
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_OK] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAX_ID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun ""
    Then Il servizio risponde correttamente
    Examples:
      | TAX_ID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
      | CucumberSpa   | PG             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |


  #CE02.14 Come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.14_98] Invocazione del servizio con paId vuoto
    Given  come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma "<paID>"
    Then il servizio risponde con errore "400"
    Examples:
      | paID  |
      | VUOTO |
  #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER_SIZE","element":"_getApiKeys.paId","detail":"size must be between 1 and 50"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.14_99] Invocazione del servizio con paId inesistente
    Given  come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma "<paID>"
    Then Il servizio risponde correttamente con presenza delle apiKey
    Examples:
      | paID           |
      | 4db941cf-17e1- |
    #Response 200 OK

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.14_100] Invocazione del servizio con paId correttamente valorizzato e verifica risposta
    Given l'operatore richiede l'elenco di tutte le PA che hanno effettuato on boarding
    And Il servizio risponde con esito positivo con la lista delle PA
    When  come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma "<paID>"
    Then Il servizio risponde correttamente con presenza delle apiKey
    Examples:
      | paID                                 |
      | a95dace4-4a47-4149-a814-0e669113ce40 |
    #Response 200 OK

#026e8c72-7944-4dcd-8668-f596447fec6d MILANO

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE03.01_101] Impostare nuova tipologia di Audit Log
    Then viene verificato che esiste un audit log "<audit-log>" in "2y"
    Examples:
      | audit-log                  |
      | AUD_CA_SEARCH_NOTIFICATION |
      | AUD_CA_VIEW_USERPROFILE    |
      | AUD_CA_VIEW_NOTIFICATION   |
      | AUD_CA_VIEW_AK             |
      | AUD_CA_VIEW_ONBOARDING     |
      | AUD_CA_DOC_AVAILABLE       |

  # AUD_CA_SEARCH_NOTIFICATION (ricerca notifiche)
  # AUD_CA_VIEW_USERPROFILE (recupero profilo utente)
  # AUD_CA_VIEW_NOTIFICATION (visualizzazione dettaglio notifica)
  # AUD_CA_VIEW_AK (visualizzazione lista api key)
  # AUD_CA_VIEW_ONBOARDING (visualizzazione lista delle PA onboardate)
  # AUD_CA_DOC_AVAILABLE (disponibilità documenti della notifica)

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE03.01_102] Impostare nuova tipologia di Audit Log
    And viene verificato che non esiste un audit log "<audit-log>" in "5y"
    Examples:
      | audit-log                  |
      | AUD_CA_SEARCH_NOTIFICATION |
      | AUD_CA_VIEW_USERPROFILE    |
      | AUD_CA_VIEW_NOTIFICATION   |
      | AUD_CA_VIEW_AK             |
      | AUD_CA_VIEW_ONBOARDING     |
      | AUD_CA_DOC_AVAILABLE       |

  # AUD_CA_SEARCH_NOTIFICATION (ricerca notifiche)
  # AUD_CA_VIEW_USERPROFILE (recupero profilo utente)
  # AUD_CA_VIEW_NOTIFICATION (visualizzazione dettaglio notifica)
  # AUD_CA_VIEW_AK (visualizzazione lista api key)
  # AUD_CA_VIEW_ONBOARDING (visualizzazione lista delle PA onboardate)
  # AUD_CA_DOC_AVAILABLE (disponibilità documenti della notifica)

  @serviceDeskRefinement @cruscottoAssistenza
  Scenario: [SERVICE_DESK_TIMELINE_REFINEMENT_1] verifica presenza elemento REFINEMENT nella response di service desk
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene chiamato service desk e si controlla la presenza dell'elemento "REFINEMENT" nella response

  @serviceDeskRefinement @cruscottoAssistenza
  Scenario: [SERVICE_DESK_TIMELINE_REFINEMENT_1_A] verifica presenza elemento REFINEMENT nella response di service desk
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | taxId              | RSSMRA85T10A562S            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene chiamato service desk e si controlla la presenza dell'elemento "REFINEMENT" nella response

  #https://pagopa.atlassian.net/browse/PN-15961
  @serviceDeskRefinement @cruscottoAssistenza
  Scenario: [SERVICE_DESK_TIMELINE_RETURNED_TO_SENDER_1] verifica presenza elemento RETURNED_TO_SENDER nella response di service desk
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" per l'utente 0
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" per l'utente 0
    And esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" per l'utente 0
    Then vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    #controllo API service-desk
    And viene chiamato service desk e si controlla la presenza dell'elemento "ANALOG_WORKFLOW_RECIPIENT_DECEASED" nella response
    And si verifica che lo stato della notifica recuperata sia: "RETURNED_TO_SENDER"
    And come operatore devo accedere alla lista di notifiche depositate che rientrano nei seguenti criteri:
      | paId      | 4db941cf-17e1-4751-9b7b |
      | startDate | TODAY                   |
      | endDate   | LAST_TEN_MINUTES        |
    Then Il servizio risponde correttamente

  @serviceDeskRefinement @cruscottoAssistenza
  Scenario: [EVOLUTIVE_CRUSCOTTO_ASSISTENZA_REWORK] Verificare che il nuovo elemento di timeline NOTIFICATION_TIMELINE_REWORKED generato in seguito ad una richiesta di correzione sia visibile da serviceDesk
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" al tentativo "ATTEMPT_0"
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino allo stato della notifica "VIEWED"
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun | attemptId | pcRetry   | recIndex   | expectedStatusCode | expectedDeliveryFailureCause | reason   |
      |     | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F          | M01                          | REASON30 |
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED" entro 15 secondi controllando ogni 3 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_TIMELINE_REWORKED"
    #controllo API service-desk
    And viene chiamato service desk e si controlla la presenza dell'elemento "NOTIFICATION_TIMELINE_REWORKED" nella response
    And si verifica che lo stato della notifica recuperata sia: "VIEWED"

  @evolutiveCruscottoAssistenza @addressBook1
  Scenario: [EVOLUTIVE_CRUSCOTTO_ASSISTENZA_1] Recupero del profilo destinatario che ha effettuato modifiche solo al recapito email
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    And viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
    And viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    When come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Galileo Galilei" e recipientType  "PF"
    Then controllo che i timestamp di creazione e modifica del recapito "cortesia" "email" siano "diversi" tra di loro

  @evolutiveCruscottoAssistenza @addressBook1
  Scenario: [EVOLUTIVE_CRUSCOTTO_ASSISTENZA_2] Recupero del profilo destinatario che non ha effettuato modifiche al recapito email
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    And viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    When come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Galileo Galilei" e recipientType  "PF"
    Then controllo che i timestamp di creazione e modifica del recapito "cortesia" "email" siano "uguali" tra di loro

  @evolutiveCruscottoAssistenza @addressBook1
  Scenario: [EVOLUTIVE_CRUSCOTTO_ASSISTENZA_3] Recupero del profilo destinatario che non ha effettuato modifiche al recapito legale PEC
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    When come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Galileo Galilei" e recipientType  "PF"
    Then controllo che i timestamp di creazione e modifica del recapito "legale" "PEC" siano "uguali" tra di loro

  @evolutiveCruscottoAssistenza @addressBook1
  Scenario: [EVOLUTIVE_CRUSCOTTO_ASSISTENZA_4] Recupero del profilo destinatario che ha effettuato modifiche al recapito legale PEC
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it"
    And viene controllato che sia presente la pec verificate "example2@pecSuccess.it" inserita per il comune "default"
    When come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Galileo Galilei" e recipientType  "PF"
    Then controllo che i timestamp di creazione e modifica del recapito "legale" "PEC" siano "diverse" tra di loro

  @evolutiveCruscottoAssistenza @addressBook1
  Scenario: [EVOLUTIVE_CRUSCOTTO_ASSISTENZA_5] Recupero del profilo destinatario che ha selezionato il Domicilio Digitale come recapito legale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And viene attivato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    When come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Galileo Galilei" e recipientType  "PF"
    Then controllo che i timestamp di creazione e modifica del recapito "legale" "SERCQ" siano "uguali" tra di loro
    And viene disabilitato il servizio SERCQ SEND per la PA "default"

  @evolutiveCruscottoAssistenza @addressBook1
  Scenario: [EVOLUTIVE_CRUSCOTTO_ASSISTENZA_6] Recupero del profilo destinatario che ha rimosso il Domicilio Digitale come recapito legale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene disabilitato il servizio SERCQ SEND per il comune "default"
    When come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Galileo Galilei" e recipientType  "PF"
    Then controllo che i timestamp di creazione e modifica del recapito "legale" "SERCQ" siano "vuoti" tra di loro

  @evolutiveCruscottoAssistenza
  Scenario Outline: [EVOLUTIVE_CRUSCOTTO_ASSISTENZA_7] Recupero del dettaglio della notifica con i pagamenti associati con l’utilizzo di uno IUN vuoto - IUN inesistente - IUN non associato allo User
    When come operatore devo accedere ai dettagli dei pagamenti di una notifica con uno iun "<IUN>" associata all' utente "<USER>" con uid "<UID>"
    Then il servizio risponde con errore "<ERROR>"
    Examples:
      | USER           | IUN                      | UID      | ERROR |
      | Mario Gherkin  | NON VALIDO               | corretto | 400   |
      | Mario Gherkin  | VUOTO                    | corretto | 400   |
      | Mario Gherkin  | INESISTENTE              | corretto | 404   |
      |                | NOTIFICA SENZA PAGAMENTI | corretto | 400   |
      | ERRATO         | NOTIFICA SENZA PAGAMENTI | corretto | 400   |
      | Mario Cucumber | NOTIFICA SENZA PAGAMENTI | corretto | 400   |
      | Mario Gherkin  | NOTIFICA SENZA PAGAMENTI | vuoto    | 400   |

  @evolutiveCruscottoAssistenza
  Scenario Outline: [EVOLUTIVE_CRUSCOTTO_ASSISTENZA_8] Recupero del dettaglio della notifica con i pagamenti associati con l’utilizzo di uno IUN associato ad una notifica di pagamento pagoPA - f24 - notifica semplice
   #IUN delle notifiche specifiche
    When come operatore devo accedere ai dettagli dei pagamenti di una notifica con uno iun "<IUN>" associata all' utente "<USER>" con uid "corretto"
    Then controllo che la risposta del servizio contenta una lista "<LIST_TYPE>"
    Examples:
      | USER          | IUN                          | LIST_TYPE |
      | Mario Gherkin | ASSOCIATO A PAGAMENTO PAGOPA | COMPILATA |
      | Mario Gherkin | NOTIFICA SENZA PAGAMENTI     | VUOTA     |
      | Mario Gherkin | ASSOCIATO A PAGAMENTO F24    | VUOTA     |
