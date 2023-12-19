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
      | CLMCST42R12D969Z |         NULL   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza @ignore
  Scenario Outline: [API-SERVICE-CA_CE02.2_5] Invocazione del servizio con taxId valorizzato correttamente ma non corrispondente al recipientType inserito
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE|
      | CLMCST42R12D969Z |         PG   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_6] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | CLMCST42R12D969Z |         NULL   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_7] Invocazione del servizio con recipientType valorizzato correttamente ma senza taxId
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID   |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | NULL |         PF   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_8] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |         PF   |   10             |       NULL            |   2023-10-01   |   2023-12-01 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_9] Invocazione del servizio con taxId e recipientType corretti e  1 <searchPageSize> 50
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-10-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_10] Invocazione del servizio con taxId e recipientType corretti e  searchPageSize = 0
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   0             |       NULL            |   2023-10-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_11] Invocazione del servizio con taxId e recipientType corretti e  searchPageSize = 51
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   51             |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_12] Invocazione del servizio con taxId e recipientType corretti e  searchNextPagesKey = 50
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   50             |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_13] Invocazione del servizio con taxId e recipientType corretti e  searchNextPagesKey = 51
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       51            |   2023-01-01   |   2023-12-01 |

    #TODO Chiedere Info............
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_14] Invocazione del servizio con taxId e recipientType corretti ma con endDate > startDate
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   50             |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_14_1] Invocazione del servizio con taxId e recipientType corretti ma con endDate > startDate
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   50             |       NULL            |  2023-12-01    |  2023-01-01  |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_15] Invocazione del servizio con taxId e recipientType corretti, con endDate valorizzata ma senza startDate
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   50             |       NULL            |   NULL    |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_16] Invocazione del servizio con taxId e recipientType corretti, con startDate valorizzata ma senza endDate
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY    |START_DATE    |END_DATE|
      | CLMCST42R12D969Z |   PF         |   50             |       NULL            |   2023-01-01 |   NULL |


     #TODO valutare se inserire una altra Assertion per verifica se la lista dei risultati e coerente
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_16_1] Invocazione del servizio con taxId e recipientType corretti, con startDate  ed endDate  valorizzati correttamente
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |

   #CE02.3 Come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche
    #TODO valutare se inserire una altra Assertion per verifica se la lista dei risultati e coerente
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_17] Invocazione del servizio con taxId vuoto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID   |RECIPIENT_TYPE|
      | NULL |   PF         |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_18] Invocazione del servizio con taxId non formalmente corretto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID                                |RECIPIENT_TYPE|
      | CPNTMS85T15H703WCPNTMS85T15H703W! |   PF         |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_19] Invocazione del servizio con recipientType vuoto
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |
      | CLMCST42R12D969Z |   NULL         |

    #TODO CONTROLLARE NON ARRIVA IL 400............
  @cruscottoAssistenza @ignore
  Scenario Outline: [API-SERVICE-CA_CE02.3_20] Invocazione del servizio con taxId valorizzato correttamente ma non corrispondente al recipientType inserito
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|
      | CLMCST42R12D969Z |   PG         |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.3_21] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType
    Given come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |
      | CLMCST42R12D969Z |   NULL         |

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
      | CLMCST42R12D969Z |   PF         |

  #CE02.4 Come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche
    #TODO simile CE02.2............
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_24] Invocazione del servizio con taxId vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID   |RECIPIENT_TYPE|SEARCH_PAGE_SIZE  |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | NULL |   PF         |   NULL              |       NULL            |   2023-01-01   |   2023-12-01 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_25] Invocazione del servizio con taxId non formalmente corretto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID                                |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | CPNTMS85T15H703WCPNTMS85T15H703W! |         PF   |   NULL             |       NULL            |   2023-01-01   |   2023-12-01 |

    #TODO gestire il caso vuoto............
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_26] Invocazione del servizio con recipientType vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE     |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | CLMCST42R12D969Z |   NULL         |   NULL              |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza @ignore
  Scenario Outline: [API-SERVICE-CA_CE02.4_27] Invocazione del servizio con taxId valorizzato correttamente ma non corrispondente al recipientType inserito
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE     |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | CLMCST42R12D969Z |   PG           |   NULL              |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_28] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE     |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | CLMCST42R12D969Z |   NULL         |   NULL              |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_29] Invocazione del servizio con recipientTypevalorizzato correttamente ma senza taxId
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID   |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE     |SEARCH_NEXT_PAGE_KEY   |START_DATE  |END_DATE|
      | NULL |   PF           |   NULL              |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.4_30] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |


    #CE02.5 Come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN)
#TODO verificare il 405.................
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_39] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) "<IUN>"
    Then il servizio risponde con errore "405"

    Examples:
      | IUN   |
      | VUOTO |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_40] Invocazione del servizio con IUN inesistente
    Given come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) "<IUN>"
    Then il servizio risponde con errore "404"

    Examples:
      | IUN   |
      | JRDT-XAPH-JQYW-202312-J-1 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.5_42] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio notifica
    And Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |


    #CE02.6 Come operatore devo accedere alla storia (timeline) di una notifica di cui conosco l’identificativo (IUN)
  #TODO Verificare il 405..........
  @cruscottoAssistenza
  Scenario Outline:  [API-SERVICE-CA_CE02.6_43] Invocazione del servizio con taxId valorizzato ma IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "CLMCST42R12D969Z" e iun "VUOTO"
    And il servizio risponde con errore "405"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |



  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_44] Invocazione del servizio con taxId valorizzato ma IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "CLMCST42R12D969Z" e iun "JRDT-XAPH-JQYW-202312-J-1"
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_46] Invocazione del servizio con IUN corretto ma taxId vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "VUOTO" e iun ""
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_47] Invocazione del servizio con IUN corretto ma taxId non formalmente corretto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "CPNTMS85T15H703WCPNTMS85T15H703W!" e iun ""
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |

    #TODO DA verificare...........aspetto un 404....Chiedere
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_48] Invocazione del servizio con IUN corretto ma taxId non corrispondente al destinatario della notifica
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "FRMTTR76M06B715E" e iun ""
    #And il servizio risponde con errore "404"
    And Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |


#TODO Error 500 verificare
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.6_49] Invocazione del servizio con IUN (notifica mono destinatario)  corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente
    And invocazione servizio per recupero dettaglio timeline notifica con taxId "CLMCST42R12D969Z" e iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |


  #CE02.7 Come operatore devo accedere alla storia (timeline) di una notifica multi-destinatario di cui conosco l’identificativo (IUN)
  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_4933] Invocazione del servizio con IUN (notifica mono destinatario)  corretto e verifica risposta
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "CLMCST42R12D969Z" e iun "" per il  destinatario 0
    And Il servizio risponde correttamente




  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_50] Invocazione del servizio con IUN vuoto
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "CLMCST42R12D969Z" e iun "" per il  destinatario 0
    And il servizio risponde con errore "405"

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_51] Invocazione del servizio con IUN inesistente
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "CLMCST42R12D969Z" e iun "JRDT-XAPH-JQYW-202312-J-1" per il  destinatario 0
    And il servizio risponde con errore "400"

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


  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_55] Invocazione del servizio con IUN corretto ma taxId non corrispondente al destinatario della notifica
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "LVLDAA85T50G702B" e iun "JRDT-XAPH-JQYW-202312-J-1" per il  destinatario 0
    And il servizio risponde con errore "400"

#TODO Verificare il corretto funzionamento........
  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.7_56] Invocazione del servizio con IUN (notifica multi destinatario) corretto e verifica risposta
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"
    Then invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "CLMCST42R12D969Z" e iun "" per il  destinatario 0
    And invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId "FRMTTR76M06B715E" e iun "" per il  destinatario 1
    And Il servizio risponde correttamente


    #CE02.8 Come operatore devo effettuare un check sulla disponibilità (nel momento della verifica), validità e dimensione degli allegati. (cancellazione a 120 gg)
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_57] Invocazione del servizio con IUN vuoto
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "405"

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  VUOTO                    | CLMCST42R12D969Z |   PF         |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_58] Invocazione del servizio con IUN inesistente
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  JZTK-MGAH-TVKL-202311-X-1| CLMCST42R12D969Z |   PF         |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_60] Invocazione del servizio con IUN esistente (notifica emessa < 120 gg) e verifica risposta
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then Il servizio risponde correttamente

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  JZTK-MGAH-TRKL-202311-X-1| CLMCST42R12D969Z |   PF         |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_61] Invocazione del servizio con IUN esistente (notifica emessa > 120 gg) e verifica risposta
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "400"

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  DNEN-JVYZ-RHWV-202304-M-1| CLMCST42R12D969Z |   PF         |

    #TODO verificare se corretto il 404..........
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_62] Invocazione del servizio con IUN esistente, recipientType corretto ma con recipientTaxId non corrispondente al destinatario della notifica
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  JZTK-MGAH-TRKL-202311-X-1| CLMCST42R12D969Z |   PG         |

     #TODO verificare se corretto il 404..........Chiedere informazioni...
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.8_63] Invocazione del servizio con IUN esistente, ma recipientType non coerente rispetto al recipientTaxId della notifica
    Given come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN "<IUN>" e taxId "<TAXIID>"  recipientType  "<RECIPIENT_TYPE>"
    Then il servizio risponde con errore "404"

    Examples:
      |         IUN               | TAXIID               |RECIPIENT_TYPE|
      |  JZTK-MGAH-TRKL-202311-X-1| CLMCST42R12D969Z |   PG         |



#CE02.9 Come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come “delegato” di una persona fisica o di una persona giuridica.
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
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE      |MANDATE_ID  |    INTERNAL_ID     |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   NULL   |   NULL       |    NO_SET  |          NO_SET    |


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
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE      |MANDATE_ID  |  INTERNAL_ID       |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   NULL   |   NULL       |    NULL    |      NO_SET    |

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
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   NULL   |   NULL       |    NO_SET  |          NULL    |

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
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE      |MANDATE_ID                                |      INTERNAL_ID     |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   NULL   |   NULL       |    z7942f2e-1037-4ed9-8ca6-a6f7923bf4a7  |          NO_SET      |


  #CE02.10 Come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario ma sono state “trattate” da altro utente da lui “delegato”
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
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegante" di una persona fisica o di una persona giuridica con taxId "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE      |MANDATE_ID  |    INTERNAL_ID     |
      | FRMTTR76M06B715E |   PF         |   1             |       NULL            |   NULL   |   NULL       |    NO_SET  |          NO_SET    |

  #CE02.11 Come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.11_79] Invocazione del servizio con paId vuoto
    Given  come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale con paId "<paID>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      |  paID  |SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY|START_DATE      |END_DATE      |
      | VUOTO  |   1             |       NULL         |   2023-01-01   |   2023-12-01 |

#TODO Verificare il comportamento corretto..
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.11_80] Invocazione del servizio con paId inesistente
    Given  come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale con paId "<paID>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      |  paID                                 |SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY|START_DATE      |END_DATE      |
      | 4db941cf-17e1-4751-9b7b-7675ccca472b  |   1             |       NULL         |   2023-01-01   |   2023-12-01 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.11_84] Invocazione del servizio con paId correttamente valorizzato e verifica risposta
    Given  come operatore devo accedere alla lista di tutte le notifiche depositate da un ente (mittente) su Piattaforma Notifiche in un range temporale con paId "<paID>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde correttamente

    Examples:
      |  paID                                 |SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY|  START_DATE    |  END_DATE    |
      | 4db741cf-17e1-4751-9b7b-7675ccca472b  |   1             |       NULL         |   2023-01-01   |   2023-12-01 |


  #CE02.12 Come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo (IUN) fornito dall'Ente mittente
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_90] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun "VUOTO"
    Then il servizio risponde con errore "405"

    Examples:
      | CF               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_91] Invocazione del servizio con IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun "JZTK-MGAH-TVKL-202311-X-1"
    Then il servizio risponde con errore "404"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |

 # CE02.13 Come operatore devo accedere alla storia (timeline) di una notifica di cui conosco l’identificativo (IUN) fornito dall'Ente mittente
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.12_93] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero dettaglio notifica con iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |


 @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_94] Invocazione del servizio con IUN vuoto
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun "VUOTO"
    Then il servizio risponde con errore "400"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_95] Invocazione del servizio con IUN inesistente
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun "JZTK-MGAH-TVKL-202311-X-1"
    Then il servizio risponde con errore "404"

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.13_97] Invocazione del servizio con IUN corretto e verifica risposta
    Given come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    And Il servizio risponde correttamente
    When invocazione servizio per recupero timeline notifica con iun ""
    Then Il servizio risponde correttamente

    Examples:
      | TAXIID               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE      |END_DATE      |
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   2023-01-01   |   2023-12-01 |


   #CE02.14 Come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma
   #TODO Verificare il comportamento corretto...Errore 500
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.14_98] Invocazione del servizio con paId vuoto
    Given  come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma "<paID>"
    Then il servizio risponde con errore "400"

    Examples:
      |  paID  |
      | VUOTO  |

    #TODO Verificare il comportamento corretto...
  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.14_99] Invocazione del servizio con paId inesistente
    Given  come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma "<paID>"
    Then Il servizio risponde correttamente

    Examples:
      |  paID  |
      | 4db941cf-17e1-4751-9b7b-7675ccca472b  |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.14_100] Invocazione del servizio con paId correttamente valorizzato e verifica risposta
    Given  come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma "<paID>"
    Then Il servizio risponde correttamente

    Examples:
      |  paID  |
      | 4db741cf-17e1-4751-9b7b-7675ccca472b  |


#8c9ed305-f1ab-4031-b3f0-5241216d0635 MILANO