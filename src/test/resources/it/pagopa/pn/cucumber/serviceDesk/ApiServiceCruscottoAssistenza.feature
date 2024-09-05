Feature: Api Service Cruscotto Assitenza

  #CE02.1 Come operatore devo accedere all’elenco di tutte le PA che hanno effettuato on boarding
  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.1_1] Invocazione del servizio e verifica restituzione array di oggetti con nome e id della PA
    Given l'operatore richiede l'elenco di tutte le PA che hanno effettuato on boarding
    Then Il servizio risponde con esito positivo con la lista delle PA

  #CE02.2 Come operatore devo accedere all’elenco di tutti i messaggi di cortesia inviati...
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_2] Invocazione del servizio con taxId vuoto
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | VUOTO  | PF             | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_3] Invocazione del servizio con taxId non formalmente corretto
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | ERRATO | PF             | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_4] Invocazione del servizio con recipientType vuoto
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | NULL           | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_6] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | NULL           | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_7] Invocazione del servizio con recipientType valorizzato correttamente ma senza taxId
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | NULL   | PF             | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_8] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 10               | NULL                 | 2023-10-01 | 2023-12-01 |
    #Response 200 OK

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.2_8] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID      | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | CucumberSpa | PG             | 10               | NULL                 | 2024-01-01 | 2024-08-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_9] Invocazione del servizio con taxId e recipientType corretti e  1 <searchPageSize> 50
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-10-01 | 2023-12-01 |
    #Response 200 OK


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_10] Invocazione del servizio con taxId e recipientType corretti e  searchPageSize = 0
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 0                | NULL                 | 2023-10-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER_MIN","element":"_searchNotificationsFromTaxId.size","detail":"must be greater than or equal to 1"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_11] Invocazione del servizio con taxId e recipientType corretti e  searchPageSize = 51
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 51               | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER_MAX","element":"_searchNotificationsFromTaxId.size","detail":"must be less than or equal to 50"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_12] Invocazione del servizio con taxId e recipientType corretti e  searchNextPagesKey = 50
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 50               | NULL                 | 2023-01-01 | 2023-12-01 |
      #Response 200 OK

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_13] Invocazione del servizio con taxId e recipientType corretti e  searchNextPagesKey = 51
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "500"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 50               | 51                   | 2023-01-01 | 2023-12-01 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_14] Invocazione del servizio con taxId e recipientType corretti ma con endDate < startDate
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "500"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 50               | NULL                 | 2023-12-01 | 2023-01-01 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_15] Invocazione del servizio con taxId e recipientType corretti, con endDate valorizzata ma senza startDate
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 50               | NULL                 | NULL       | 2023-12-01 |

     #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'startDate' when calling searchNotificationsFromTaxId null

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_16] Invocazione del servizio con taxId e recipientType corretti, con startDate valorizzata ma senza endDate
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE |
      | Mario Gherkin | PF             | 50               | NULL                 | 2023-01-01 | NULL     |
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'endDate' when calling searchNotificationsFromTaxId null


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_16_1] Invocazione del servizio con taxId e recipientType corretti, con startDate  ed endDate  valorizzati correttamente
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |


   #CE02.3 Come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_17] Invocazione del servizio con taxId vuoto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE |
      | VUOTO  | PF             |
   #"errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_17_1] Invocazione del servizio con taxId vuoto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE |
      | NULL   | PF             |
  #"errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]}"

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_18] Invocazione del servizio con taxId non formalmente corretto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE |
      | ERRATO | PF             |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 0 and 32"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.3_18] Invocazione del servizio con taxId non formalmente corretto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE |
      | ERRATO | PG             |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 0 and 32"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_19] Invocazione del servizio con recipientType vuoto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE |
      | Mario Gherkin | VUOTO          |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_21] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE |
      | Mario Gherkin | NULL           |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_22] Invocazione del servizio con recipientType valorizzato correttamente ma senza taxId
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE |
      | NULL   | PF             |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_23] Invocazione del servizio con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE |
      | Mario Gherkin | PF             |
    #  Response 200 OK

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.3_23] Invocazione del servizio con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID      | RECIPIENT_TYPE |
      | CucumberSpa | PG             |

  #CE02.4 Come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche
    #TODO simile CE02.2............
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_24] Invocazione del servizio con taxId vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | VUOTO  | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
  #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_24_1] Invocazione del servizio con taxId vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | NULL   | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
  #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_25] Invocazione del servizio con taxId non formalmente corretto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | ERRATO | PF             | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_26] Invocazione del servizio con recipientType vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | VUOTO          | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
  #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_28] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | NULL           | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_29] Invocazione del servizio con recipientType valorizzato correttamente ma senza taxId
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | NULL   | PF             | NULL             | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_30] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #Response 200 OK

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.4_30] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID      | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | CucumberSpa | PG             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |


    #CE02.5 Come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN)

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_39] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) "<IUN>"
    Then il servizio risponde con errore "405"

    Examples:
      | IUN   |
      | VUOTO |
    #405 Method Not Allowed

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_39_1] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) "<IUN>"
    Then il servizio risponde con errore "400"

    Examples:
      | IUN  |
      | NULL |
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getNotificationFromIUN null

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_40] Invocazione del servizio con IUN inesistente
    Given come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) "<IUN>"
    Then il servizio risponde con errore "404"

    Examples:
      | IUN                       |
      | JRDT-XAPH-JQYW-202312-J-1 |
    #404 Not Found

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_42] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio notifica
    And Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 10               | NULL                 | 2023-01-01 | 2023-12-01 |
    #Response 200 OK

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.5_42] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio notifica
    And Il servizio risponde correttamente

    Examples:
      | TAXIID      | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | CucumberSpa | PG             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |


    #CE02.6 Come operatore devo accedere alla storia (timeline) di una notifica di cui conosco l’identificativo (IUN)
  @cruscottoAssistenza
  Scenario Outline:  [API-SERVICE-CA_CE02.6_43] Invocazione del servizio con taxId valorizzato ma IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "Mario Gherkin" e iun "VUOTO"
    And il servizio risponde con errore "405"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #Response 405 METHOD_NOT_ALLOWED


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_44] Invocazione del servizio con taxId valorizzato ma IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "Mario Gherkin" e iun "JRDT-ZAPH-JQYW-202312-J-1"
    And il servizio risponde con errore "404"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_46] Invocazione del servizio con IUN corretto ma taxId vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "VUOTO" e iun ""
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_46_1] Invocazione del servizio con IUN corretto ma taxId null
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "NULL" e iun ""
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_47] Invocazione del servizio con IUN corretto ma taxId non formalmente corretto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "ERRATO" e iun ""
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_48] Invocazione del servizio con IUN corretto ma taxId non corrispondente al destinatario della notifica
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "Mario Cucumber" e iun ""
    And il servizio risponde con errore "404"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.6_48] Invocazione del servizio con IUN corretto ma taxId non corrispondente al destinatario della notifica
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "GherkinSrl" e iun ""
    And il servizio risponde con errore "404"

    Examples:
      | TAXIID      | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | CucumberSpa | PG             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_49] Invocazione del servizio con IUN (notifica mono destinatario)  corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "Mario Gherkin" e iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
  #Response 200 OK

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.6_49] Invocazione del servizio con IUN (notifica mono destinatario)  corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "CucumberSpa" e iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID      | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | CucumberSpa | PG             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |

  #CE02.7 Come operatore devo accedere alla storia (timeline) di una notifica multi-destinatario di cui conosco l’identificativo (IUN)

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_50] Invocazione del servizio con IUN vuoto
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "Mario Gherkin" e iun "VUOTO" per il  destinatario 0
    And il servizio risponde con errore "405"
    #Errore: 405 METHOD_NOT_ALLOWED 405 Method Not Allowed

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_50_1] Invocazione del servizio con IUN null
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
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
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then verifica IsMultiRecipients nel dettaglio notifica


    #Ogni Destinatario vede la sua Timeline...

    #CE02.8 Come operatore devo effettuare un check sulla disponibilità (nel momento della verifica), validità e dimensione degli allegati. (cancellazione a 120 gg)
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_57] Invocazione del servizio con IUN vuoto
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "405"

    Examples:
      | IUN   | TAXIID        | RECIPIENT_TYPE |
      | VUOTO | Mario Gherkin | PF             |
    #Errore: 405 METHOD_NOT_ALLOWED 405 Method Not Allowed

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_57_1] Invocazione del servizio con IUN null
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | IUN  | TAXIID        | RECIPIENT_TYPE |
      | NULL | Mario Gherkin | PF             |
  #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getDocumentsOfIUN null

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_58] Invocazione del servizio con IUN inesistente
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      | IUN                       | TAXIID        | RECIPIENT_TYPE |
      | JZTK-MGAH-TVKL-202311-X-1 | Mario Gherkin | PF             |
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_60] Invocazione del servizio con IUN esistente (notifica emessa < 120 gg) e verifica risposta
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente con presenza di allegati "true"

    Examples:
      | IUN    | TAXIID        | RECIPIENT_TYPE |
      | NO_SET | Mario Gherkin | PF             |
    # Response 200 OK


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_58_1] Invocazione del servizio con IUN esistente e notifica annullata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      | IUN    | TAXIID        | RECIPIENT_TYPE |
      | NO_SET | Mario Gherkin | PF             |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.8_60] Invocazione del servizio con IUN esistente (notifica emessa < 120 gg) e verifica risposta
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente con presenza di allegati "true"

    Examples:
      | IUN    | TAXIID      | RECIPIENT_TYPE |
      | NO_SET | CucumberSpa | PG             |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_61] Invocazione del servizio con IUN esistente (notifica emessa > 120 gg) e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "NO_SET" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | NULL             | NULL                 | 2024-01-01 | 2024-03-30 |
    #Errore: 400 BAD_REQUEST 400 Bad Request: [{"type":null,"status":400,"title":"ERROR_ON_DELIVERY_CLIENT","detail":"See logs for details in PN-SERVICE-DESK","traceId":"Root=1-6585bfef-39e3629554030a8d73dcf647","timestamp":"2023-12-22T16:57:19.95458306Z","errors":[]}] null


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_62] Invocazione del servizio con IUN esistente, recipientType corretto ma con recipientTaxId non corrispondente al destinatario della notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      | IUN    | TAXIID         | RECIPIENT_TYPE |
      | NO_SET | Mario Cucumber | PF             |
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.8_62] Invocazione del servizio con IUN esistente, recipientType corretto ma con recipientTaxId non corrispondente al destinatario della notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      | IUN    | TAXIID      | RECIPIENT_TYPE |
      | NO_SET | CucumberSpa | PG             |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_63] Invocazione del servizio con IUN esistente, ma recipientType non coerente rispetto al recipientTaxId della notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      | IUN    | TAXIID        | RECIPIENT_TYPE |
      | NO_SET | Mario Gherkin | PG             |
    #Response 404 NOT_FOUND


  #CE02.11 Come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.11_79] Invocazione del servizio con paId vuoto
    Given  come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale con paId "<paID>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | paID  | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | VUOTO | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"id","detail":"size must be between 1 and 50"}]}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.11_80] Invocazione del servizio con paId inesistente
    Given  come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale con paId "<paID>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | paID                    | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | 4db941cf-17e1-4751-9b7b | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #  Response 200 OK
    #{"results":[],"moreResult":false,"nextPagesKey":[]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.11_84] Invocazione del servizio con paId correttamente valorizzato e verifica risposta
    Given l'operatore richiede l'elenco di tutte le PA che hanno effettuato on boarding
    And Il servizio risponde con esito positivo con la lista delle PA
    When  come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale con paId "<paID>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | paID   | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | NO_SET | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #  Response 200 OK a95dace4-4a47-4149-a814-0e669113ce40
    #{"results":[],"moreResult":false,"nextPagesKey":[]}

  #CE02.12 Come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) fornito dall'Ente mittente
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_90] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun "VUOTO"
    Then il servizio risponde con errore "405"

    Examples:
      | TAXID         | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #Errore: 405 METHOD_NOT_ALLOWED 405 Method Not Allowed

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_90_1] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun "NULL"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXID         | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getNotificationFromIUN null

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_91] Invocazione del servizio con IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun "JZTK-MGAH-TVZL-202311-X-1"
    Then il servizio risponde con errore "404"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #Response 404 NOT_FOUND

 # CE02.13 Come operatore devo accedere alla storia (timeline) di una notifica di cui conosco l’identificativo (IUN) fornito dall'Ente mittente
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_93] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
  #Response 200 OK

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_94] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun "VUOTO"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
      #code":"PN_GENERIC_INVALIDPARAMETER_SIZE","element":"_getNotificationFromIUN.iun","detail":"size must be between 25 and 25"

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_94_1] Invocazione del servizio con IUN null
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun "NULL"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getTimelineOfIUN null

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_95] Invocazione del servizio con IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun "JZTK-MGAH-TVKL-202311-X-1"
    Then il servizio risponde con errore "404"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_97] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | Mario Gherkin | PF             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |
    #Response 200 OK

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.13_97] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID      | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE   |
      | CucumberSpa | PG             | 1                | NULL                 | 2023-01-01 | 2023-12-01 |

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
      | 026e8c72-7944-4dcd-8668-f596447fec6d |
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

