Feature: Test API Availability in Use of E-Service

  @TC_INCARICATO_45
  Scenario Outline: Verificare che il richiamo dell’API di disponibilità in fruizione di un e-service possa essere compiuto da un utente di tipo amministratore
    Given l'utente è un "<ruolo>" di "PA1"
    When l'ente "PA1" concede la disponibilità a ricevere deleghe in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_46
  Scenario Outline: Verificare che il richiamo dell’API di disponibilità in fruizione di un e-service, per il quale è già stata data disponibilità, non possa essere compiuto da un utente di tipo amministratore
    Given l'utente è un "<ruolo>" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente è un "<ruolo>" di "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in fruizione
    When l'ente "PA2" concede la disponibilità a ricevere deleghe in fruizione
    Then si ottiene status code <statusCode>
    
    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_47
  Scenario Outline: Richiamare l’API di creazione di una delega da parte di un fruitore delegante verso un altro ente
    Given l'utente è un "<ruolo>" di "PA1"
    And l'utente ha già creato un e-service che accetta richieste di fruizione in delega
    When Richiamare l’API di creazione di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_48
  Scenario Outline: Richiamare l’API di creazione di una delega non permessa
    Given l'utente è un "<ruolo>" di "PA1"
    And l’aderente non è disponibile ad accettare deleghe
    When Richiamare l’API di creazione di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_49
  Scenario Outline: Richiamare l’API di creazione di una delega già delegato
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver già inserito una delega in fruizione per un e-service verso un aderente
    When Richiamare l’API di creazione di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_50
  Scenario Outline: Richiamare l’API di accettazione di una delega in stato pending
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega in stato pending
    When Richiamare l’API di accettazione di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_51
  Scenario Outline: Richiamare l’API di accettazione di una delega in stato revocata
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega in stato revocata
    When Richiamare l’API di accettazione di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_52
  Scenario Outline: Richiamare l’API di accettazione di una delega in stato rifiutata
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega in stato rifiutata
    When Richiamare l’API di accettazione di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_53
  Scenario Outline: Richiamare l’API di rifiuto di una delega in stato pending
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega in stato pending
    When Richiamare l’API di rifiuto di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_54
  Scenario Outline: Richiamare l’API di rifiuto di una delega non permessa
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega in stato revocata
    When Richiamare l’API di rifiuto di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_55
  Scenario Outline: Richiamare l’API di rifiuto di una delega non permessa
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega in stato attivo
    When Richiamare l’API di rifiuto di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_56
  Scenario: Richiamare l’API di creazione fruizione da parte di un delegato alla fruizione
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di creazione fruizione
    Then La richiesta di fruizione viene correttamente inoltrata e risulta in stato di approvazione

  @TC_INCARICATO_57
  Scenario Outline: Richiamare l’API di creazione fruizione da parte di un delegato alla fruizione - delega non presente
    Given l'utente è un "<ruolo>" di "PA1"
    And Nessuna delega presente
    When Richiamare l’API di creazione fruizione
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_58
  Scenario Outline: Richiamare l’API di accettazione di una richiesta di fruizione
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una richiesta di fruizione in stato pending
    When Richiamare l’API di accettazione di una richiesta di fruizione
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_59
  Scenario Outline: Richiamare l’API di rifiuto di una richiesta di fruizione
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una richiesta di fruizione in stato pending
    When Richiamare l’API di rifiuto di una richiesta di fruizione
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_60
  Scenario Outline: Richiamare l’API di accettazione di una richiesta di fruizione non esistente
    Given l'utente è un "<ruolo>" di "PA1"
    And Nessuna richiesta di fruizione presente
    When Richiamare l’API di accettazione di una richiesta di fruizione
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_61
  Scenario Outline: Richiamare l’API di creazione della finalità per la fruizione
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di creazione della finalità
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_62
  Scenario Outline: Richiamare l’API di creazione di un client da parte di un delegato alla fruizione
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di creazione di un client
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_63
  Scenario Outline: Richiamare l’API di associazione di un client alla propria finalità
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una finalità
    When Richiamare l’API di associazione di un client alla propria finalità
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_64
  Scenario Outline: Richiamare l’API di associazione di un client non esistente
    Given l'utente è un "<ruolo>" di "PA1"
    And Nessun client presente
    When Richiamare l’API di associazione di un client alla propria finalità
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_65
  Scenario Outline: Richiamare l’API di creazione della finalità per la fruizione
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di creazione della finalità
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_66
  Scenario Outline: Richiamare l’API di revoca della delega
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega in stato attivo
    When Richiamare l’API di revoca della delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_67
  Scenario Outline: Richiamare l’API di visualizzazione finalità
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una finalità
    When Richiamare l’API di visualizzazione finalità
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_68
  Scenario Outline: Richiamare l’API di visualizzazione dettagli delega
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di visualizzazione dettagli delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_69
  Scenario Outline: Richiamare l’API di visualizzazione elenco deleghe
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato delle deleghe
    When Richiamare l’API di visualizzazione elenco deleghe
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_70
  Scenario Outline: Richiamare l’API di visualizzazione elenco deleghe ricevute
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver ricevuto delle deleghe
    When Richiamare l’API di visualizzazione elenco deleghe ricevute
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_71
  Scenario Outline: Richiamare l’API di visualizzazione dettagli delega ricevuta
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver ricevuto una delega
    When Richiamare l’API di visualizzazione dettagli delega ricevuta
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_72
  Scenario Outline: Richiamare l’API di visualizzazione elenco deleghe conferite
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato delle deleghe
    When Richiamare l’API di visualizzazione elenco deleghe conferite
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_73
  Scenario Outline: Richiamare l’API di visualizzazione elenco deleghe ricevute
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver ricevuto delle deleghe
    When Richiamare l’API di visualizzazione elenco deleghe ricevute
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_74
  Scenario Outline: Richiamare l’API di visualizzazione dettagli delega conferita
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di visualizzazione dettagli delega conferita
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_75
  Scenario Outline: Richiamare l’API di visualizzazione dettagli delega ricevuta
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver ricevuto una delega
    When Richiamare l’API di visualizzazione dettagli delega ricevuta
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_76
  Scenario Outline: Richiamare l’API di visualizzazione elenco deleghe conferite
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato delle deleghe
    When Richiamare l’API di visualizzazione elenco deleghe conferite
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_77
  Scenario Outline: Richiamare l’API di visualizzazione elenco deleghe ricevute
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver ricevuto delle deleghe
    When Richiamare l’API di visualizzazione elenco deleghe ricevute
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_78
  Scenario Outline: Richiamare l’API di visualizzazione dettagli delega conferita
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di visualizzazione dettagli delega conferita
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_79
  Scenario Outline: Richiamare l’API di visualizzazione dettagli delega ricevuta
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver ricevuto una delega
    When Richiamare l’API di visualizzazione dettagli delega ricevuta
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_80
  Scenario Outline: Richiamare l’API di visualizzazione elenco deleghe da un utente non amministratore
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato delle deleghe
    When Richiamare l’API di visualizzazione elenco deleghe
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_81
  Scenario Outline: Richiamare l’API di visualizzazione del dettaglio della delega da un utente non amministratore
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di visualizzazione del dettaglio della delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_82
  Scenario Outline: Richiamare l’API di accettazione di una delega da parte di un delegato in stato pending - utente non amministratore
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega in stato pending
    When Richiamare l’API di accettazione di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_83
  Scenario Outline: Richiamare l’API di rifiuto di una delega da parte di un delegato in stato pending - utente non amministratore
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega in stato pending
    When Richiamare l’API di rifiuto di una delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_84
  Scenario Outline: Richiamare l’API da parte del delegante per la revoca della delega alla fruizione al delegato in stato attivo - utente non amministratore
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega in stato attivo
    When Richiamare l’API da parte del delegante per la revoca della delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_85
  Scenario Outline: Richiamare l’API di visualizzazione elenco deleghe conferite lato delegante
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di visualizzazione elenco deleghe conferite
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_86
  Scenario Outline: Richiamare l’API di visualizzazione elenco deleghe ricevute lato delegato
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver ricevuto una delega
    When Richiamare l’API di visualizzazione elenco deleghe ricevute
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_87
  Scenario Outline: Richiamare l’API di visualizzazione dettaglio delega conferita lato delegante
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di visualizzazione dettaglio delega conferita
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_88
  Scenario Outline: Richiamare l’API di visualizzazione dettaglio delega ricevuta lato delegato
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver ricevuto una delega
    When Richiamare l’API di visualizzazione dettaglio delega ricevuta
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |

  @TC_INCARICATO_89
  Scenario Outline: Richiamare l’API di visualizzazione elenco deleghe da un utente non amministratore
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato delle deleghe
    When Richiamare l’API di visualizzazione elenco deleghe
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |

  @TC_INCARICATO_90
  Scenario Outline: Richiamare l’API di visualizzazione del dettaglio della delega da un utente non amministratore
    Given l'utente è un "<ruolo>" di "PA1"
    And Aver creato una delega
    When Richiamare l’API di visualizzazione del dettaglio della delega
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |
