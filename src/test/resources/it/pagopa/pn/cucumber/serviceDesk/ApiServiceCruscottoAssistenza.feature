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
      | TAXIID   |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | VUOTO |         PF   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_3] Invocazione del servizio con taxId non formalmente corretto
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID                                |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | CPNTMS85T15H703WCPNTMS85T15H703W! |         PF   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_4] Invocazione del servizio con recipientType vuoto
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | Mario Gherkin |         NULL   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]

  #Caso di test Eliminato poiché da un CF non siamo in grado di sapere se è un PF o un PG, ci dobbiamo fidare di quello che ci passa il client..
  @cruscottoAssistenza @ignore
  Scenario Outline: [API-SERVICE-CA_CE02.2_5] Invocazione del servizio con taxId valorizzato correttamente ma non corrispondente al recipientType inserito
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE|
      | Mario Gherkin |         PG   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_6] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | Mario Gherkin |         NULL   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_7] Invocazione del servizio con recipientType valorizzato correttamente ma senza taxId
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID   |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | NULL |         PF   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_8] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |         PF   |   10             |       NULL            |   2023-10-01   |   2023-12-01 |
    #Response 200 OK
    #[{"iun":"YXDE-PDQZ-DHZW-202311-A-1","sender":"Comune di milano","sentAt":"2023-11-29T09:27:06.333793133Z","subject":"invio notifica con cucumber","iunStatus":"VIEWED","courtesyMessages":[{"sentTimestamp":"2023-11-29T09:29:05.788781256Z","channel":"EMAIL"}]},{"iun":"LREZ-MULE-XJZL-202311-A-1","sender":"Comune di Palermo","sentAt":"2023-11-30T16:56:45.398160982Z","subject":"invio notifica con cucumber","iunStatus":"VIEWED","courtesyMessages":[{"sentTimestamp":"2023-11-30T16:57:45.457554824Z","channel":"EMAIL"}]},{"iun":"QAMW-TXLA-ZPYT-202311-T-1","sender":"Comune di milano","sentAt":"2023-11-28T15:59:05.349388931Z","subject":"invio notifica con cucumber","iunStatus":"VIEWED","courtesyMessages":[{"sentTimestamp":"2023-11-28T16:00:04.663841265Z","channel":"EMAIL"}]},{"iun":"GTKY-XEQE-NXME-202311-N-1","sender":"Comune di Palermo","sentAt":"2023-11-29T08:48:13.300799797Z","subject":"invio notifica con cucumber","iunStatus":"EFFECTIVE_DATE","courtesyMessages":[{"sentTimestamp":"2023-11-29T08:49:55.10127002Z","channel":"EMAIL"}]},{"iun":"VXUX-HKXL-QAXJ-202311-Q-1","sender":"Comune di Palermo","sentAt":"2023-11-30T16:57:17.420316746Z","subject":"invio notifica con cucumber","iunStatus":"VIEWED","courtesyMessages":[{"sentTimestamp":"2023-11-30T17:00:01.27283474Z","channel":"EMAIL"}]},{"iun":"ZKML-KEML-QMTR-202311-M-1","sender":"Comune di Palermo","sentAt":"2023-11-29T08:47:35.798421129Z","subject":"invio notifica con cucumber","iunStatus":"EFFECTIVE_DATE","courtesyMessages":[{"sentTimestamp":"2023-11-29T08:49:57.088108988Z","channel":"EMAIL"}]},{"iun":"QPGU-UGXV-QHZA-202311-E-1","sender":"Comune di Palermo","sentAt":"2023-11-29T08:46:01.387057404Z","subject":"invio notifica con cucumber","iunStatus":"VIEWED","courtesyMessages":[{"sentTimestamp":"2023-11-29T08:47:47.145148127Z","channel":"EMAIL"}]},{"iun":"YWNV-WNAX-WQWV-202311-Z-1","sender":"Comune di Palermo","sentAt":"2023-11-29T08:48:49.031265741Z","subject":"invio notifica con cucumber","iunStatus":"VIEWED","courtesyMessages":[{"sentTimestamp":"2023-11-29T08:49:53.770870008Z","channel":"EMAIL"}]},{"iun":"JZTK-MGAH-TRKL-202311-X-1","sender":"Comune di Palermo","sentAt":"2023-11-30T17:07:49.923928415Z","subject":"invio notifica con cucumber","iunStatus":"VIEWED","courtesyMessages":[{"sentTimestamp":"2023-11-30T17:09:10.929489467Z","channel":"EMAIL"}]},{"iun":"EDZH-XWEV-XNVH-202311-E-1","sender":"Comune di milano","sentAt":"2023-11-28T14:56:08.108035245Z","subject":"invio notifica con cucumber","iunStatus":"EFFECTIVE_DATE","courtesyMessages":[{"sentTimestamp":"2023-11-28T14:57:54.112360046Z","channel":"EMAIL"}]}],"moreResult":true,"nextPagesKey":["eyJlayI6IlBGLTM3NjU0NTYxLTQ0NmEtNGM4OC1iMzI4LTY2OTlhODMyMmIzMyMjMjAyMzExIiwiaWsiOnsiaXVuX3JlY2lwaWVudElkIjoiRURaSC1YV0VWLVhOVkgtMjAyMzExLUUtMSMjUEYtMzc2NTQ1NjEtNDQ2YS00Yzg4LWIzMjgtNjY5OWE4MzIyYjMzIiwicmVjaXBpZW50SWRfY3JlYXRpb25Nb250aCI6IlBGLTM3NjU0NTYxLTQ0NmEtNGM4OC1iMzI4LTY2OTlhODMyMmIzMyMjMjAyMzExIiwic2VudEF0IjoiMjAyMy0xMS0yOFQxNDo1NjowOC4xMDgwMzUyNDVaIn19"]}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.2_8] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID      |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CucumberSpa |         PG   |   10             |       NULL            |   2023-10-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_9] Invocazione del servizio con taxId e recipientType corretti e  1 <searchPageSize> 50
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-10-01   |   2023-12-01 |
    #Response 200 OK
    #{"results":[{"iun":"JZTK-MGAH-TRKL-202311-X-1","sender":"Comune di Palermo","sentAt":"2023-11-30T17:07:49.923928415Z","subject":"invio notifica con cucumber","iunStatus":"VIEWED","courtesyMessages":[{"sentTimestamp":"2023-11-30T17:09:10.929489467Z","channel":"EMAIL"}]}],"moreResult":true,"nextPagesKey":["eyJlayI6IlBGLTM3NjU0NTYxLTQ0NmEtNGM4OC1iMzI4LTY2OTlhODMyMmIzMyMjMjAyMzExIiwiaWsiOnsiaXVuX3JlY2lwaWVudElkIjoiSlpUSy1NR0FILVRSS0wtMjAyMzExLVgtMSMjUEYtMzc2NTQ1NjEtNDQ2YS00Yzg4LWIzMjgtNjY5OWE4MzIyYjMzIiwicmVjaXBpZW50SWRfY3JlYXRpb25Nb250aCI6IlBGLTM3NjU0NTYxLTQ0NmEtNGM4OC1iMzI4LTY2OTlhODMyMmIzMyMjMjAyMzExIiwic2VudEF0IjoiMjAyMy0xMS0zMFQxNzowNzo0OS45MjM5Mjg0MTVaIn19"]}


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_10] Invocazione del servizio con taxId e recipientType corretti e  searchPageSize = 0
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   0             |       NULL            |   2023-10-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER_MIN","element":"_searchNotificationsFromTaxId.size","detail":"must be greater than or equal to 1"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_11] Invocazione del servizio con taxId e recipientType corretti e  searchPageSize = 51
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   51             |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER_MAX","element":"_searchNotificationsFromTaxId.size","detail":"must be less than or equal to 50"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_12] Invocazione del servizio con taxId e recipientType corretti e  searchNextPagesKey = 50
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   50             |       NULL            |   2023-01-01   |   2023-12-01 |
      #Response 200 OK

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_13] Invocazione del servizio con taxId e recipientType corretti e  searchNextPagesKey = 51
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "500"

    Examples:
      | TAXIID           |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   50             |       51            |   2023-01-01   |   2023-12-01 |



  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_14] Invocazione del servizio con taxId e recipientType corretti ma con endDate < startDate
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "500"

    Examples:
      | TAXIID           |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY|START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   50             |       NULL            |  2023-12-01    |  2023-01-01  |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_15] Invocazione del servizio con taxId e recipientType corretti, con endDate valorizzata ma senza startDate
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE |END_DATE      |
      | Mario Gherkin |   PF         |   50             |       NULL            |   NULL    |   2023-12-01 |

     #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'startDate' when calling searchNotificationsFromTaxId null

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_16] Invocazione del servizio con taxId e recipientType corretti, con startDate valorizzata ma senza endDate
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE    |END_DATE|
      | Mario Gherkin |   PF         |   50             |       NULL            |   2023-01-01 |   NULL |
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'endDate' when calling searchNotificationsFromTaxId null


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_16_1] Invocazione del servizio con taxId e recipientType corretti, con startDate  ed endDate  valorizzati correttamente
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID           |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |


   #CE02.3 Come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_17] Invocazione del servizio con taxId vuoto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID   |RECIPIENT_TYPE|
      | VUOTO |   PF         |
   #"errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_17_1] Invocazione del servizio con taxId vuoto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID   |RECIPIENT_TYPE|
      | NULL |   PF         |
  #"errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]}"

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_18] Invocazione del servizio con taxId non formalmente corretto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID                                |RECIPIENT_TYPE|
      | CPNTMS85T15H703WCPNTMS85T15H703W! |   PF         |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 0 and 32"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.3_18] Invocazione del servizio con taxId non formalmente corretto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID                                |RECIPIENT_TYPE|
      | 1234567899999999999999999999999999999 |   pg         |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 0 and 32"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_19] Invocazione del servizio con recipientType vuoto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID           |RECIPIENT_TYPE  |
      | Mario Gherkin |   VUOTO         |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]

    #TODO CONTROLLARE NON ARRIVA IL 400............
  @cruscottoAssistenza @ignore
  Scenario Outline: [API-SERVICE-CA_CE02.3_20] Invocazione del servizio con taxId valorizzato correttamente ma non corrispondente al recipientType inserito
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|
      | Mario Gherkin |   PG         |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_21] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |
      | Mario Gherkin |   NULL         |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_22] Invocazione del servizio con recipientType valorizzato correttamente ma senza taxId
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID    |RECIPIENT_TYPE|
      | NULL  |   PF         |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_23] Invocazione del servizio con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|
      | Mario Gherkin |   PF         |
    #  Response 200 OK
    #{"userAddresses":[{"courtesyAddressType":"COURTESY","courtesyValue":"testEmail@email.it","courtesyChannelType":"EMAIL","legalAddressType":null,"legalValue":null,"legalChannelType":null}],"delegatorMandates":[],"delegateMandates":[{"mandateId":"1006cff3-ffde-4811-ac29-c420b6f38ce2","taxId":"FRMTTR76M06B715E","recipientType":"PF","dateFrom":"2023-08-21T22:00:00Z","dateTo":"2023-12-21T22:59:59Z","delegatorInternalId":"PF-aa0c4556-5a6f-45b1-800c-0f4f3c5a57b6","delegateInternalId":"PF-37654561-446a-4c88-b328-6699a8322b33"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.3_23] Invocazione del servizio con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID      |RECIPIENT_TYPE|
      | CucumberSpa |   PG         |

  #CE02.4 Come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche
    #TODO simile CE02.2............
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_24] Invocazione del servizio con taxId vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID   |RECIPIENT_TYPE|SEARCH_PAGE_SIZE  |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | VUOTO |   PF            |   1              |       NULL            |   2023-01-01   |   2023-12-01 |
  #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_24_1] Invocazione del servizio con taxId vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID   |RECIPIENT_TYPE|SEARCH_PAGE_SIZE  |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | NULL |   PF            |   1              |       NULL            |   2023-01-01   |   2023-12-01 |
  #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_25] Invocazione del servizio con taxId non formalmente corretto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID                                |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | CPNTMS85T15H703WCPNTMS85T15H703W! |         PF   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_26] Invocazione del servizio con recipientType vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID           |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE     |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | Mario Gherkin |   VUOTO         |   NULL              |       NULL            |   2023-01-01   |   2023-12-01 |
  #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]}

  @cruscottoAssistenza @ignore
  Scenario Outline: [API-SERVICE-CA_CE02.4_27] Invocazione del servizio con taxId valorizzato correttamente ma non corrispondente al recipientType inserito
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE     |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | Mario Gherkin |   PG           |   NULL              |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_28] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE     |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | Mario Gherkin |   NULL         |   NULL              |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"recipientType","detail":"must not be null"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_29] Invocazione del servizio con recipientType valorizzato correttamente ma senza taxId
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID   |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE     |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | NULL |   PF           |   NULL              |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_30] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Response 200 OK
    #{"results":[{"iun":"JZTK-MGAH-TRKL-202311-X-1","sender":"Comune di Palermo","sentAt":"2023-11-30T17:07:49.923928415Z","subject":"invio notifica con cucumber","iunStatus":"VIEWED","courtesyMessages":[{"sentTimestamp":"2023-11-30T17:09:10.929489467Z","channel":"EMAIL"}]}],"moreResult":true,"nextPagesKey":["eyJlayI6IlBGLTM3NjU0NTYxLTQ0NmEtNGM4OC1iMzI4LTY2OTlhODMyMmIzMyMjMjAyMzExIiwiaWsiOnsiaXVuX3JlY2lwaWVudElkIjoiSlpUSy1NR0FILVRSS0wtMjAyMzExLVgtMSMjUEYtMzc2NTQ1NjEtNDQ2YS00Yzg4LWIzMjgtNjY5OWE4MzIyYjMzIiwicmVjaXBpZW50SWRfY3JlYXRpb25Nb250aCI6IlBGLTM3NjU0NTYxLTQ0NmEtNGM4OC1iMzI4LTY2OTlhODMyMmIzMyMjMjAyMzExIiwic2VudEF0IjoiMjAyMy0xMS0zMFQxNzowNzo0OS45MjM5Mjg0MTVaIn19"]}"


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.4_30] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CucumberSpa |   PG         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |


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
      | IUN   |
      | NULL |
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getNotificationFromIUN null

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_40] Invocazione del servizio con IUN inesistente
    Given come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) "<IUN>"
    Then il servizio risponde con errore "404"

    Examples:
      | IUN   |
      | JRDT-XAPH-JQYW-202312-J-1 |
    #404 Not Found

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_42] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio notifica
    And Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Response 200 OK
    #{"results":[{"iun":"JZTK-MGAH-TRKL-202311-X-1","sender":"Comune di Palermo","sentAt":"2023-11-30T17:07:49.923928415Z","subject":"invio notifica con cucumber","iunStatus":"VIEWED","courtesyMessages":[{"sentTimestamp":"2023-11-30T17:09:10.929489467Z","channel":"EMAIL"}]}],"moreResult":true,"nextPagesKey":["eyJlayI6IlBGLTM3NjU0NTYxLTQ0NmEtNGM4OC1iMzI4LTY2OTlhODMyMmIzMyMjMjAyMzExIiwiaWsiOnsiaXVuX3JlY2lwaWVudElkIjoiSlpUSy1NR0FILVRSS0wtMjAyMzExLVgtMSMjUEYtMzc2NTQ1NjEtNDQ2YS00Yzg4LWIzMjgtNjY5OWE4MzIyYjMzIiwicmVjaXBpZW50SWRfY3JlYXRpb25Nb250aCI6IlBGLTM3NjU0NTYxLTQ0NmEtNGM4OC1iMzI4LTY2OTlhODMyMmIzMyMjMjAyMzExIiwic2VudEF0IjoiMjAyMy0xMS0zMFQxNzowNzo0OS45MjM5Mjg0MTVaIn19"]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.5_42] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio notifica
    And Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CucumberSpa |   PG         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |


    #CE02.6 Come operatore devo accedere alla storia (timeline) di una notifica di cui conosco l’identificativo (IUN)
  @cruscottoAssistenza
  Scenario Outline:  [API-SERVICE-CA_CE02.6_43] Invocazione del servizio con taxId valorizzato ma IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "Mario Gherkin" e iun "VUOTO"
    And il servizio risponde con errore "405"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Response 405 METHOD_NOT_ALLOWED


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_44] Invocazione del servizio con taxId valorizzato ma IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "CLMCST42R12D969Z" e iun "JRDT-ZAPH-JQYW-202312-J-1"
    And il servizio risponde con errore "404"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_46] Invocazione del servizio con IUN corretto ma taxId vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "VUOTO" e iun ""
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_46_1] Invocazione del servizio con IUN corretto ma taxId null
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "NULL" e iun ""
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"must not be null"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_47] Invocazione del servizio con IUN corretto ma taxId non formalmente corretto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "CPNTMS85T15H703WCPNTMS85T15H703W!" e iun ""
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_48] Invocazione del servizio con IUN corretto ma taxId non corrispondente al destinatario della notifica
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "FRMTTR76M06B715E" e iun ""
    And il servizio risponde con errore "404"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.6_48] Invocazione del servizio con IUN corretto ma taxId non corrispondente al destinatario della notifica
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "GherkinSrl" e iun ""
    And il servizio risponde con errore "404"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CucumberSpa |   PG         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_49] Invocazione del servizio con IUN (notifica mono destinatario)  corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "Mario Gherkin" e iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
  #Response 200 OK
  #{"iunStatus":"VIEWED","timeline":[{"timestamp":"2023-11-30T17:08:58.883629621Z","category":"REQUEST_ACCEPTED","detail":{"recIndex":null,"digitalAddress":null,"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:09:09.996615193Z","category":"AAR_GENERATION","detail":{"recIndex":0,"digitalAddress":null,"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":"safestorage://PN_AAR-f73787409cb5444bbf74e1800c98b1c9.pdf","physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":1,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:09:10.929510447Z","category":"SEND_COURTESY_MESSAGE","detail":{"recIndex":0,"digitalAddress":{"type":"EMAIL","address":"testEmail@email.it"},"sendDate":"2023-11-30T17:09:10.929489467Z","ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:09:12.488491026Z","category":"SEND_DIGITAL_DOMICILE","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"pectest@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":0,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:09:55Z","category":"SEND_DIGITAL_FEEDBACK","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"pectest@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":"","deliveryDetailCode":"C003","shouldRetry":null,"notificationDate":"2023-11-30T17:09:55Z","sendingReceipts":[{"id":null,"system":null}],"responseStatus":"OK","generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:10:00Z","category":"SEND_DIGITAL_PROGRESS","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"pectest@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":0,"deliveryFailureCause":null,"deliveryDetailCode":"C001","shouldRetry":false,"notificationDate":"2023-11-30T17:10:39.33033623Z","sendingReceipts":[{"id":null,"system":null}],"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:10:49.993328022Z","category":"DIGITAL_SUCCESS_WORKFLOW","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"pectest@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-01T16:51:05.045852077Z","category":"NOTIFICATION_VIEWED","detail":{"recIndex":0,"digitalAddress":null,"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}}]}"

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.6_49] Invocazione del servizio con IUN (notifica mono destinatario)  corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "CucumberSpa" e iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID      |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CucumberSpa |   PG         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |

  #CE02.7 Come operatore devo accedere alla storia (timeline) di una notifica multi-destinatario di cui conosco l’identificativo (IUN)

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_50] Invocazione del servizio con IUN vuoto
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
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
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
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
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
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
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
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
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "CPNTMS85T15H703WCPNTMS85T15H703W!" e iun "JRDT-XAPH-JQYW-202312-J-1" per il  destinatario 0
    And il servizio risponde con errore "400"
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"taxId","detail":"size must be between 11 and 32"}]}

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_55] Invocazione del servizio con IUN corretto ma taxId non corrispondente al destinatario della notifica
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "LVLDAA85T50G702B" e iun "" per il  destinatario 0
    And il servizio risponde con errore "404"
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_56] Invocazione del servizio con IUN (notifica multi destinatario) corretto e verifica risposta
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "Mario Gherkin" e iun "" per il  destinatario 0
    And invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "Mario Cucumber" e iun "" per il  destinatario 1
    And Il servizio risponde correttamente

    #Ogni Destinatario vede la sua Timeline...
    #{"iunStatus":"DELIVERING","timeline":[{"timestamp":"2023-12-19T11:41:49.354582409Z","category":"REQUEST_ACCEPTED","detail":{"recIndex":null,"digitalAddress":null,"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:42:00.266540445Z","category":"AAR_GENERATION","detail":{"recIndex":0,"digitalAddress":null,"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":"safestorage://PN_AAR-5e0a15a77477431db191dfd06c75a52d.pdf","physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":1,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:42:00.770697781Z","category":"SEND_COURTESY_MESSAGE","detail":{"recIndex":0,"digitalAddress":{"type":"EMAIL","address":"testEmail@email.it"},"sendDate":"2023-12-19T11:42:00.770637828Z","ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:42:02.106562226Z","category":"SEND_DIGITAL_DOMICILE","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"notifichedigitali-dev@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":0,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:42:12.152676917Z","category":"SEND_DIGITAL_PROGRESS","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"notifichedigitali-dev@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":0,"deliveryFailureCause":null,"deliveryDetailCode":"C001","shouldRetry":false,"notificationDate":"2023-12-19T11:42:13.566363577Z","sendingReceipts":[{"id":null,"system":null}],"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:42:22.503627394Z","category":"SEND_DIGITAL_FEEDBACK","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"notifichedigitali-dev@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":"C003","shouldRetry":null,"notificationDate":"2023-12-19T11:42:22.503627394Z","sendingReceipts":[{"id":null,"system":null}],"responseStatus":"OK","generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:42:42.252383431Z","category":"DIGITAL_SUCCESS_WORKFLOW","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"notifichedigitali-dev@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}}]}
    #{"iunStatus":"DELIVERING","timeline":[{"timestamp":"2023-12-19T11:41:49.354582409Z","category":"REQUEST_ACCEPTED","detail":{"recIndex":null,"digitalAddress":null,"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:42:01.228245603Z","category":"AAR_GENERATION","detail":{"recIndex":1,"digitalAddress":null,"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":"safestorage://PN_AAR-34a6e5764ce64885895902e7d74c7a2b.pdf","physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":1,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:42:01.504117265Z","category":"SEND_COURTESY_MESSAGE","detail":{"recIndex":1,"digitalAddress":{"type":"EMAIL","address":"provaemail@test.it"},"sendDate":"2023-12-19T11:42:01.504091704Z","ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:43:03.108604398Z","category":"SEND_DIGITAL_DOMICILE","detail":{"recIndex":1,"digitalAddress":{"type":"PEC","address":"notifichedigitali-dev@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":0,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:43:13.188496219Z","category":"SEND_DIGITAL_PROGRESS","detail":{"recIndex":1,"digitalAddress":{"type":"PEC","address":"notifichedigitali-dev@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":0,"deliveryFailureCause":null,"deliveryDetailCode":"C001","shouldRetry":false,"notificationDate":"2023-12-19T11:43:14.452538079Z","sendingReceipts":[{"id":null,"system":null}],"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-19T11:43:23.429757519Z","category":"SEND_DIGITAL_FEEDBACK","detail":{"recIndex":1,"digitalAddress":{"type":"PEC","address":"notifichedigitali-dev@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":"C003","shouldRetry":null,"notificationDate":"2023-12-19T11:43:23.429757519Z","sendingReceipts":[{"id":null,"system":null}],"responseStatus":"OK","generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}}]}"

    #CE02.8 Come operatore devo effettuare un check sulla disponibilità (nel momento della verifica), validità e dimensione degli allegati. (cancellazione a 120 gg)
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_57] Invocazione del servizio con IUN vuoto
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "405"

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  VUOTO                    | Mario Gherkin |   PF         |
    #Errore: 405 METHOD_NOT_ALLOWED 405 Method Not Allowed

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_57_1] Invocazione del servizio con IUN null
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  NULL                    | Mario Gherkin |   PF         |
  #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getDocumentsOfIUN null

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_58] Invocazione del servizio con IUN inesistente
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  JZTK-MGAH-TVKL-202311-X-1| Mario Gherkin |   PF         |
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_60] Invocazione del servizio con IUN esistente (notifica emessa < 120 gg) e verifica risposta
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  NO_SET                   | Mario Gherkin |   PF         |
    # Response 200 OK
    #{"documentsAvailable":true,"totalSize":3028,"documents":[{"filename":"JZTK-MGAH-TRKL-202311-X-1__null.pdf","contentType":"application/pdf","contentLength":3028}]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.8_60] Invocazione del servizio con IUN esistente (notifica emessa < 120 gg) e verifica risposta
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  NO_SET                   | CucumberSpa |   PG         |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_61] Invocazione del servizio con IUN esistente (notifica emessa > 120 gg) e verifica risposta
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  DNEN-JVYZ-RHWV-202304-M-1| Mario Gherkin |   PF         |

    #Errore: 400 BAD_REQUEST 400 Bad Request: [{"type":null,"status":400,"title":"ERROR_ON_DELIVERY_CLIENT","detail":"See logs for details in PN-SERVICE-DESK","traceId":"Root=1-658186d3-09993dbb0f08a2f731ac7238","timestamp":"2023-12-19T12:04:35.888715266Z","errors":[]}] null


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_62] Invocazione del servizio con IUN esistente, recipientType corretto ma con recipientTaxId non corrispondente al destinatario della notifica
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      |         IUN               | TAXIID           |RECIPIENT_TYPE|
      |  JZTK-MGAH-TRKL-202311-X-1| Mario Cucumber |   PF         |
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.8_62] Invocazione del servizio con IUN esistente, recipientType corretto ma con recipientTaxId non corrispondente al destinatario della notifica
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      |         IUN               | TAXIID           |RECIPIENT_TYPE|
      |  JZTK-MGAH-TRKL-202311-X-1| CucumberSpa |   PG         |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_63] Invocazione del servizio con IUN esistente, ma recipientType non coerente rispetto al recipientTaxId della notifica
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      |         IUN               | TAXIID           |RECIPIENT_TYPE|
      |  JZTK-MGAH-TRKL-202311-X-1| Mario Gherkin |   PG         |
    #Response 404 NOT_FOUND


#CE02.9 Come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come “delegato” di una persona fisica o di una persona giuridica.
 #TODO Fatta segnalazione ricerca la notifica partendo da ieri........e non da oggi
  @deleghe1  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.9_64] Invocazione del servizio e verifica risposta
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And Il servizio risponde correttamente

    Examples:
      | TAXIID           |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE      |MANDATE_ID  |    INTERNAL_ID     |
      | Mario Gherkin |   PF         |   1             |       NULL            |   NULL   |   NULL       |    NO_SET  |          NO_SET    |
    #Mario Gherkin - DELEGATO - CLMCST42R12D969Z
    #Mario Cucumber -DELEGANTE - FRMTTR76M06B715E


  @deleghe2  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.9_64] Invocazione del servizio e verifica risposta
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And Il servizio risponde correttamente

    Examples:
      | TAXIID           |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE      |MANDATE_ID  |    INTERNAL_ID     |
      | CucumberSpa |   PG         |   1             |       NULL            |   NULL   |   NULL       |    NO_SET  |          NO_SET    |

  #Response 500 INTERNAL_SERVER_ERROR

  @deleghe1  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.9_65] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica ma senza searchMandateId
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE      |MANDATE_ID  |  INTERNAL_ID       |
      | Mario Gherkin |   PF         |   1             |       NULL            |   NULL   |   NULL       |    NULL    |      NO_SET    |
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'mandateId' when calling searchNotificationsAsDelegateFromInternalId null

  @deleghe1  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.9_66] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica, con searchMandateId ma senza searchDelegateInternalId
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE      |MANDATE_ID  |      INTERNAL_ID |
      | Mario Gherkin |   PF         |   1             |       NULL            |   NULL   |   NULL       |    NO_SET  |          NULL    |
    # Errore: 400 BAD_REQUEST 400 Missing the required parameter 'delegateInternalId' when calling searchNotificationsAsDelegateFromInternalId null

   #TODO Fatta segnalazione ricerca la notifica partendo da ieri........e non da oggi
  @deleghe1  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.9_67] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica, ma con searchMandateId non coerente con il searchDelegateInternalId
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE      |MANDATE_ID                            |      INTERNAL_ID     |
      | Mario Gherkin |   PF         |   1             |       NULL            |   NULL   |   NULL       |    z7942f2e-1037-4ed9-8ca6-a6f7923bf4a7  |          NO_SET      |

    # Response 500 INTERNAL_SERVER_ERROR

  #CE02.10 Come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario ma sono state “trattate” da altro utente da lui “delegato”
    #TODO Fatta segnalazione ricerca la notifica partendo da ieri........e non da oggi
  @deleghe1  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.10_74] Invocazione del servizio e verifica risposta
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegante" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE      |MANDATE_ID  |    INTERNAL_ID     |
      | Mario Cucumber |   PF         |   1             |       NULL            |   NULL   |   NULL       |    NO_SET  |          NO_SET    |
    # Response 500 INTERNAL_SERVER_ERROR

  #CE02.11 Come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.11_79] Invocazione del servizio con paId vuoto
    Given  come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale con paId "<paID>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      |  paID  |SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY|START_DATE      |END_DATE      |
      | VUOTO  |   1             |       NULL         |   2023-01-01   |   2023-12-01 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"id","detail":"size must be between 1 and 50"}]}]

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.11_80] Invocazione del servizio con paId inesistente
    Given  come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale con paId "<paID>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      |  paID                                 |SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY|START_DATE      |END_DATE      |
      | 4db941cf-17e1-4751-9b7b  |   1             |       NULL         |   2023-01-01   |   2023-12-01 |
    #  Response 200 OK
    #{"results":[],"moreResult":false,"nextPagesKey":[]}

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.11_84] Invocazione del servizio con paId correttamente valorizzato e verifica risposta
    Given  come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale con paId "<paID>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      |  paID                                 |SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY|  START_DATE    |  END_DATE    |
      | 4db741cf-17e1-4751-9b7b-7675ccca472b  |   1             |       NULL         |   2023-01-01   |   2023-12-01 |
    #  Response 200 OK
    #{"results":[],"moreResult":false,"nextPagesKey":[]}

  #CE02.12 Come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) fornito dall'Ente mittente
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_90] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun "VUOTO"
    Then il servizio risponde con errore "405"

    Examples:
      | TAXID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Errore: 405 METHOD_NOT_ALLOWED 405 Method Not Allowed

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_90_1] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun "NULL"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getNotificationFromIUN null

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_91] Invocazione del servizio con IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun "JZTK-MGAH-TVZL-202311-X-1"
    Then il servizio risponde con errore "404"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Response 404 NOT_FOUND

 # CE02.13 Come operatore devo accedere alla storia (timeline) di una notifica di cui conosco l’identificativo (IUN) fornito dall'Ente mittente
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_93] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID           |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
  #Response 200 OK
  #"{"abstract":"Abstract della notifica","paProtocolNumber":"302130124983943038","subject":"invio notifica con cucumber","isMultiRecipients":true,"hasDocuments":true,"physicalCommunicationType":"REGISTERED_LETTER_890","senderDenomination":"Comune di Palermo","senderTaxId":"80016350821","amount":null,"paymentExpirationDate":null,"sentAt":"2023-11-30T17:07:49.923928415Z","hasPayments":true}"

 @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_94] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun "VUOTO"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
      #code":"PN_GENERIC_INVALIDPARAMETER_SIZE","element":"_getNotificationFromIUN.iun","detail":"size must be between 25 and 25"

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_94_1] Invocazione del servizio con IUN null
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun "NULL"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'iun' when calling getTimelineOfIUN null

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_95] Invocazione del servizio con IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun "JZTK-MGAH-TVKL-202311-X-1"
    Then il servizio risponde con errore "404"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Response 404 NOT_FOUND

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_97] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | Mario Gherkin |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |
    #Response 200 OK
    #{"iunStatus":"VIEWED","timeline":[{"timestamp":"2023-11-30T17:08:58.883629621Z","category":"REQUEST_ACCEPTED","detail":{"recIndex":null,"digitalAddress":null,"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:09:09.996615193Z","category":"AAR_GENERATION","detail":{"recIndex":0,"digitalAddress":null,"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":"safestorage://PN_AAR-f73787409cb5444bbf74e1800c98b1c9.pdf","physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":1,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:09:10.929510447Z","category":"SEND_COURTESY_MESSAGE","detail":{"recIndex":0,"digitalAddress":{"type":"EMAIL","address":"testEmail@email.it"},"sendDate":"2023-11-30T17:09:10.929489467Z","ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:09:12.488491026Z","category":"SEND_DIGITAL_DOMICILE","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"pectest@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":0,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:09:55Z","category":"SEND_DIGITAL_FEEDBACK","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"pectest@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":"","deliveryDetailCode":"C003","shouldRetry":null,"notificationDate":"2023-11-30T17:09:55Z","sendingReceipts":[{"id":null,"system":null}],"responseStatus":"OK","generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:10:00Z","category":"SEND_DIGITAL_PROGRESS","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"pectest@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":"SPECIAL","sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":0,"deliveryFailureCause":null,"deliveryDetailCode":"C001","shouldRetry":false,"notificationDate":"2023-11-30T17:10:39.33033623Z","sendingReceipts":[{"id":null,"system":null}],"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-11-30T17:10:49.993328022Z","category":"DIGITAL_SUCCESS_WORKFLOW","detail":{"recIndex":0,"digitalAddress":{"type":"PEC","address":"pectest@pec.pagopa.it"},"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}},{"timestamp":"2023-12-01T16:51:05.045852077Z","category":"NOTIFICATION_VIEWED","detail":{"recIndex":0,"digitalAddress":null,"sendDate":null,"ioSendMessageResult":null,"digitalAddressSource":null,"sentAttemptMade":null,"lastAttemptDate":null,"retryNumber":null,"deliveryFailureCause":null,"deliveryDetailCode":null,"shouldRetry":null,"notificationDate":null,"sendingReceipts":null,"responseStatus":null,"generatedAarUrl":null,"physicalAddress":null,"productType":null,"analogCost":null,"numberOfPages":null,"envelopeWeight":null,"prepareRequestId":null,"notificationCost":null,"raddType":null,"raddTransactionId":null,"delegateInfo":null,"foundAddress":null,"failureCause":null,"serviceLevel":null,"relatedRequestId":null,"attachments":null,"sendRequestId":null,"registeredLetterCode":null,"newAddress":null,"legalFactGenerationDate":null,"reasonCode":null,"reason":null}}]}


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.13_97] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CucumberSpa |   PG         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |

   #CE02.14 Come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.14_98] Invocazione del servizio con paId vuoto
    Given  come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma "<paID>"
    Then il servizio risponde con errore "400"

    Examples:
      |  paID  |
      | VUOTO  |
  #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER_SIZE","element":"_getApiKeys.paId","detail":"size must be between 1 and 50"}]}

    #TODO Verificare il comportamento corretto...
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.14_99] Invocazione del servizio con paId inesistente
    Given  come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma "<paID>"
    Then Il servizio risponde correttamente

    Examples:
      |  paID  |
      | 4db941cf-17e1-  |
    #Response 200 OK
    #{"type":null,"status":200,"title":"ERROR_ON_KEYS_MANAGER_CLIENT","detail":"See logs for details in PN-SERVICE-DESK","traceId":"Root=1-6581a796-07d32d7277fad01f3dc8d5cd","timestamp":"2023-12-19T14:24:23.034470445Z","errors":[]}"

  #TODO Verificare il comportamento corretto...
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.14_100] Invocazione del servizio con paId correttamente valorizzato e verifica risposta
    Given  come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma "<paID>"
    Then Il servizio risponde correttamente

    Examples:
      |  paID  |
      | 4db741cf-17e1-4751-9b7b-7675ccca472b  |
    #Response 200 OK
    #{"type":null,"status":200,"title":"ERROR_ON_KEYS_MANAGER_CLIENT","detail":"See logs for details in PN-SERVICE-DESK","traceId":"Root=1-6581a796-07d32d7277fad01f3dc8d5cd","timestamp":"2023-12-19T14:24:23.034470445Z","errors":[]}"


#8c9ed305-f1ab-4031-b3f0-5241216d0635 MILANO