Feature: Test API Availability in Use of E-Service
  #TODO 27/01/2025 in molti test si lascia che a fungere da erogatore sia il delegante stesso, spostare l'erogazione su una terza PA
  #TODO 24/01/2025 rivedere codici di errori, es. molti non sono 400 ma 409
  #TODO 23/01/2025 rivedere così da unire gli step 'l'ente delegato "PA2"' e 'l'utente è un "admin" dell'ente delegato'
  #TODO 23/01/2025 rivedere così da non dovere definire gli utenti più volte nello stesso scenario (i.e. non ripetere gli step come 'l'ente delegato "PA2"')
  #TODO 23/01/2025 rivedere nomi e descrizioni così fa aver maggior coerenza stilistica (es. se in due test diversi si usano le espressioni
        #"il test deve produrre un errore" e "il test deve fallire" sceglierne una e usarla per entrambi
  #TODO 24/01/2025 al momento in quasi tutti i test vengono fatti controlli per ogni ruolo possibile, verificare che sia indispensabile o se invece basta farlo con il ruolo necessario (admin)
  #TODO 24/01/2025 rivedere le descrizioni di ciascun scenario così che siano chiare ed esaustive
  #TODO 24/01/2025 verificare il corretto uso delle clausole Given, When e Then

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
    Given l'ente delegato "PA2"
    And l'utente è un "<ruolo>" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    When l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_47
  Scenario Outline: Richiamare l’API di creazione di una delega da parte di un fruitore delegante verso un altro ente delegato
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "<ruolo>" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    When l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_48
  Scenario Outline: La creazione di una delega in fruizione verso un ente che non ha dato la propria disponibilità a ricevere deleghe in fruizione deve fallire
    Given l'ente delegante "PA1"
    And l'utente è un "<ruolo>" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegato "PA2"
    #And l'ente delegato non è disponibile ad accettare deleghe     <--  si ritiene implicito per l'assenza dello step di concessione della disponibilità
    When l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  # @TC_INCARICATO_49 rimosso a seguito di revisione perché non ritenuto più pertinente

  @TC_INCARICATO_50
  Scenario Outline: Richiamare l’API di accettazione di una delega in stato WAITING_FOR_APPROVAL
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'utente è un "<ruolo>" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_51
  Scenario Outline: Richiamare l’API di accettazione di una delega in stato "revocata" deve produrre un errore
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'ente delegante con ruolo "admin" revoca la delega in fruizione
    When l'utente è un "<ruolo>" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        409 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_52
  Scenario Outline: Richiamare l’API di accettazione di una delega in stato rifiutata
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    When l'utente è un "<ruolo>" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_53
  Scenario Outline: Richiamare l’API di rifiuto di una delega in stato WAITING_FOR_APPROVAL
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "<ruolo>" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_54
  Scenario Outline: Richiamare l’API di rifiuto su una delega in stato REVOKED
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'ente delegante con ruolo "admin" revoca la delega in fruizione
    When l'utente è un "<ruolo>" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        409 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_55
  Scenario Outline: Richiamare l’API di rifiuto di una delega da parte del delegante: non permessa in quanto il rifiuto è una facoltà esclusiva del delegato
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "<ruolo>" dell'ente delegante
    And l'ente delegante rifiuta la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        403 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_56
  Scenario Outline: Richiamare l’API di rifiuto di una delega in stato ACTIVE: non è permesso rifiutare una delega già precedentemente accettata
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "<ruolo>" dell'ente delegato
    When l'ente delegato rifiuta la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        409 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_57
  Scenario Outline: Richiamare l’API di creazione fruizione da parte di un delegato alla fruizione, specificando la delega corretta
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "<ruolo>" dell'ente delegato
    When il delegato ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |


  @TC_INCARICATO_58
  Scenario Outline: Richiamare l’API di creazione di una richiesta di fruizione, specificando una delega inesistente
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "<ruolo>" dell'ente delegato
    When il delegato ha già creato e inviato una richiesta di fruizione indicando una delega inesistente
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_58_BIS
  Scenario Outline: Richiamare l’API di creazione di una richiesta di fruizione, specificando una delega su cui non si ha autorità
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "<ruolo>" dell'ente delegato

      # TODO Capire come implementare questo passo al livello tecnico
    When il delegato ha già creato e inviato una richiesta di fruizione indicando una delega che non gli appartiene

    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_59
  Scenario Outline: Richiamare l’API di accettazione di una richiesta di fruizione fatta da un delegato
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione
    And l'utente è un "<ruolo>" dell'ente delegante
    When il delegante ha già approvato quella richiesta di fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |


  @TC_INCARICATO_60
  Scenario Outline: Richiamare l’API di rifiuto di una richiesta di fruizione fatta da un delegato
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione
    And l'utente è un "<ruolo>" dell'ente delegante
    When il delegante ha già rifiutato quella richiesta di fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_62
  Scenario Outline: Richiamare l’API di creazione di una finalità da parte di un delegato alla fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "<ruolo>" dell'ente delegato
    When il delegato ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_63
  Scenario Outline: Richiamare l’API di creazione di un client da parte del delegato alla fruizione
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione con client del delegato utilizzabile
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" dell'ente delegato
    And il delegato ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "<ruolo>" dell'ente delegato
    When l'utente richiede la creazione di un client "CONSUMER"
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_64
  Scenario Outline: Richiamare l’API di associazione di un client creato dal delegato ad una finalità creata dal delegato
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione con client del delegato utilizzabile
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And il delegato ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "<ruolo>" dell'ente delegato
    And l'utente richiede la creazione di un client "CONSUMER"
    When l'utente richiede l'associazione della finalità al client
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_64_BIS
  Scenario Outline: Richiamare l’API di associazione di un client creato dal delegato ad una finalità creata dal delegante
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione con client del delegato utilizzabile
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "<ruolo>" dell'ente delegato
    And l'utente richiede la creazione di un client "CONSUMER"
    When l'utente richiede l'associazione della finalità al client
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        403 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_65
  Scenario Outline: Richiamare l’API di associazione di un client creato dal delegato NON avendo i permessi di associare un client da delegato all' e-service
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "<ruolo>" dell'ente delegato
    And l'utente richiede la creazione di un client "CONSUMER"
    When l'utente richiede l'associazione della finalità al client
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        403 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_66
  Scenario Outline: Il delegato richiama l’API di associazione di un client NON precedentemente creato
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "<ruolo>" dell'ente delegato
    When l'utente richiede l'associazione della finalità a un client inesistente
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        404 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_67
  Scenario Outline: Il delegante può creare una finalità per un e-service che ha dato in delega
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And il delegante ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "<ruolo>" dell'ente delegante
    When il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_68
  Scenario Outline: Richiamare l’API di creazione di un client da parte del delegante alla fruizione
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "<ruolo>" dell'ente delegante
    When l'utente richiede la creazione di un client "CONSUMER"
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_69
  Scenario Outline: Richiamare l’API di associazione di un client da parte del delegante alla fruizione
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "admin" dell'ente delegante
    And l'utente richiede la creazione di un client "CONSUMER"
    When l'utente è un "<ruolo>" dell'ente delegante
    And l'utente richiede l'associazione della finalità al client
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

    #TODO
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
  Scenario Outline: Il delegante richiama l’API di associazione di un client NON precedentemente creato
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    When l'utente è un "<ruolo>" dell'ente delegante
    And l'utente richiede l'associazione della finalità a un client inesistente
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        404 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_72
  Scenario Outline: Richiamare l’API revoca della delega
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'ente delegante con ruolo "<ruolo>" revoca la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        204 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_73
  Scenario Outline: Richiamare l’API di revoca di una delega in stato REFUSED
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    And l'ente delegante con ruolo "<ruolo>" revoca la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        409 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  @TC_INCARICATO_74
  Scenario Outline: Il richiamo della API di revoca della delega da parte del delegato deve fallire
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato con ruolo "<ruolo>" revoca la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        403 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

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
