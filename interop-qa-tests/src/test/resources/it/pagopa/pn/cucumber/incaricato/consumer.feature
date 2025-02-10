@incaricato
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

  Background:
    # TODO 07/02/2025: considerare di generalizzare così da resettare TUTTI gli enti automaticamente
    Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe in fruizione
    And l'ente "PA1" rimuove la disponibilità a ricevere deleghe in fruizione

  Scenario Outline: [TC_INCARICATO_45] Verificare che il richiamo dell’API di disponibilità in fruizione di un e-service possa essere compiuto da un utente di tipo amministratore
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

    Scenario: [TC_INCARICATO_46] Verificare che il richiamo dell’API di disponibilità in fruizione di un e-service, per il quale è già stata data disponibilità, non possa essere compiuto da un utente di tipo amministratore
      Given l'ente delegato "PA2"
      And l'utente è un "admin" dell'ente delegato
      And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
      When l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
      Then si ottiene status code 409

      # NOTA BUG: se viene creata una delega per un e-service NON delegabile in fruizione, lo status d'errore restituito è 500.  #TODO identificare o elaborare test che testi proprio questo
    Scenario Outline: [TC_INCARICATO_47] Richiamare l’API di creazione di una delega da parte di un fruitore delegante verso un altro ente delegato
      Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
      Given l'ente delegato "PA2"
      And l'utente è un "admin" dell'ente delegato
      And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
      And l'ente delegante "PA1"
      And l'utente è un "<ruolo>" dell'ente delegante
      When l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
      Then si ottiene status code <statusCode>
      Examples:
        | ruolo       | statusCode |
        | admin       |        200 |
        | api         |        403 |
        | security    |        403 |
        | api,security|        403 |
        | support     |        403 |

    Scenario Outline: [TC_INCARICATO_48] La creazione di una delega in fruizione verso un ente che non ha dato la propria disponibilità a ricevere deleghe in fruizione deve fallire
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegante "PA1"
    And l'utente è un "<ruolo>" dell'ente delegante
    And l'ente delegato "PA2"
    #And l'ente delegato non è disponibile ad accettare deleghe     <--  si ritiene implicito per l'assenza dello step di concessione della disponibilità
    When l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        403 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  # @TC_INCARICATO_49 rimosso a seguito di revisione perché non ritenuto più pertinente

    Scenario Outline: [TC_INCARICATO_50] Richiamare l’API di accettazione di una delega in stato WAITING_FOR_APPROVAL
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
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

    Scenario: [TC_INCARICATO_51] Richiamare l’API di accettazione di una delega in stato "revocata" deve produrre un errore
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'ente delegante con ruolo "admin" revoca la delega in fruizione
    When l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    Then si ottiene status code 403

    Scenario: [TC_INCARICATO_52] Richiamare l’API di accettazione di una delega in stato rifiutata
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    When l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    Then si ottiene status code 403

    Scenario Outline: [TC_INCARICATO_53] Richiamare l’API di rifiuto di una delega in stato WAITING_FOR_APPROVAL
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
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

    Scenario: [TC_INCARICATO_54] Richiamare l’API di rifiuto su una delega in stato REVOKED
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'ente delegante con ruolo "admin" revoca la delega in fruizione
    When l'utente è un "admin" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    Then si ottiene status code 403

    Scenario Outline: [TC_INCARICATO_55] Richiamare l’API di rifiuto di una delega da parte del delegante: non permessa in quanto il rifiuto è una facoltà esclusiva del delegato
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
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

    Scenario: [TC_INCARICATO_56] Richiamare l’API di rifiuto di una delega in stato ACTIVE: non è permesso rifiutare una delega già precedentemente accettata
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "admin" dell'ente delegato
    When l'ente delegato rifiuta la delega in fruizione
    Then si ottiene status code 409

    Scenario Outline: [TC_INCARICATO_57] Richiamare l’API di creazione fruizione da parte di un delegato alla fruizione, specificando la delega corretta
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "<ruolo>" dell'ente delegato
    When il delegato ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione
    #When il delegato ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |


    Scenario Outline: [TC_INCARICATO_58] Richiamare l’API di creazione di una richiesta di fruizione, specificando una delega inesistente
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
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

    Scenario Outline: [TC_INCARICATO_58_BIS] Richiamare l’API di creazione di una richiesta di fruizione, specificando una delega che non compete al delegato
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "<ruolo>" dell'ente delegato

    # Processo di produzione di una delega tra il delegante e un terzo ente
    And l'utente è un "admin" di "GSP2"
    And l'ente "GSP2" concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente terzo "GSP2"

    When il delegato ha già creato e inviato una richiesta di fruizione indicando la delega dell'ente terzo
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

    Scenario Outline: [TC_INCARICATO_58_TRIS] Richiamare l’API di creazione di una richiesta di fruizione, specificando una delega che non compete né al delegante né al delegato
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "<ruolo>" dell'ente delegato

    # Processo di produzione di una delega tra due enti diversi da delegante e delegato
    And l'utente è un "admin" di "GSP2"
    And l'ente "GSP2" concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un "admin" di "Privato"
    And l'ente "Privato" ha inoltrato una richiesta di delega in fruizione all'ente terzo "GSP2"

    When il delegato ha già creato e inviato una richiesta di fruizione indicando la delega dell'ente terzo
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        400 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

    Scenario Outline: [TC_INCARICATO_59] Richiamare l’API di accettazione di una richiesta di fruizione fatta da un delegato
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    #    Given "PA1" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And il delegato ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione
#    And l'utente è un "<ruolo>" dell'ente delegante
     And "GSP" ha già approvato quella richiesta di fruizione
#    When il delegante ha già approvato quella richiesta di fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
#      | api         |        403 |
#      | security    |        403 |
#      | api,security|        403 |
#      | support     |        403 |


    Scenario Outline: [TC_INCARICATO_60] Richiamare l’API di rifiuto di una richiesta di fruizione fatta da un delegato
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione
    And l'utente è un "<ruolo>" dell'ente delegante
     And "GSP" ha già rifiutato quella richiesta di fruizione
#    When il delegante ha già rifiutato quella richiesta di fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
#      | api         |        403 |
#      | security    |        403 |
#      | api,security|        403 |
#      | support     |        403 |

    Scenario Outline: [TC_INCARICATO_62] Richiamare l’API di creazione di una finalità da parte di un delegato alla fruizione
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "<ruolo>" dell'ente delegato
      #Questo step va rivisto perché il consumer da passare nella richiesta di creazione finalità sembra debba essere quello del DELEGANTE - FATTO!
    When per conto del delegante, il delegato ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
#      | api         |        403 |
#      | security    |        403 |
#      | api,security|        403 |
#      | support     |        403 |

    Scenario Outline: [TC_INCARICATO_63] Richiamare l’API di creazione di un client da parte del delegato alla fruizione
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
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
#    And il delegato ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "<ruolo>" dell'ente delegato
    When l'utente richiede la creazione di un client "CONSUMER"
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
#      | api         |        403 |
#      | security    |        403 |
#      | api,security|        403 |
#      | support     |        403 |

    Scenario: [TC_INCARICATO_64] Richiamare l’API di associazione di un client creato dal delegato ad una finalità creata dal delegato
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
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" dell'ente delegato
    And il delegato ha già creato 1 client "CONSUMER"
    When l'utente richiede l'associazione della finalità al client
    Then si ottiene status code 200

    Scenario: [TC_INCARICATO_64_BIS] Richiamare l’API di associazione di un client creato dal delegato ad una finalità creata dal delegante
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
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" dell'ente delegato
    And il delegato ha già creato 1 client "CONSUMER"
    When l'utente richiede l'associazione della finalità al client
    Then si ottiene status code 403

    Scenario: [TC_INCARICATO_65] Richiamare l’API di associazione di un client creato dal delegato NON avendo i permessi di associare un client da delegato all' e-service
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
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" dell'ente delegato
    And il delegato ha già creato 1 client "CONSUMER"
    When l'utente richiede l'associazione della finalità al client
    Then si ottiene status code 403

    Scenario: [TC_INCARICATO_66] Il delegato richiama l’API di associazione di un client NON precedentemente creato
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
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" dell'ente delegato
    When l'utente richiede l'associazione della finalità a un client inesistente
    Then si ottiene status code 404

    # NOTA lo step di creazione finalità sembra non sia permesso dal delegante (riga 466)
    Scenario: [TC_INCARICATO_67] Il delegante può creare una finalità per un e-service che ha dato in delega
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And il delegante ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" dell'ente delegante
    And per conto del delegato, il delegante ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    Then si ottiene status code 200

    # NOTA: caso già testato con i test di client-create.feature
    Scenario: [TC_INCARICATO_68] Richiamare l’API di creazione di un client da parte del delegante alla fruizione
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "admin" dell'ente delegante
    When l'utente richiede la creazione di un client "CONSUMER"
    Then si ottiene status code 200

    Scenario: [TC_INCARICATO_69] Richiamare l’API di associazione di un client da parte del delegante alla fruizione
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha già creato 1 client "CONSUMER"
    When l'utente è un "admin" dell'ente delegante
    And l'utente richiede l'associazione della finalità al client
    Then si ottiene status code 200

    Scenario: [TC_INCARICATO_70] Un ente delegante deve poter associare un proprio client ad una finalità creata da un ente delegato per un e-service a cui è delegato
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
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha già creato 1 client "CONSUMER"
    And l'utente richiede l'associazione della finalità al client
    Then si ottiene status code 200

    Scenario Outline: [TC_INCARICATO_71] Il delegante richiama l’API di associazione di un client NON precedentemente creato
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
    And il delegante ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given il delegante ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente è un "<ruolo>" dell'ente delegante
    And l'utente richiede l'associazione della finalità a un client inesistente
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        404 |

    Scenario Outline: [TC_INCARICATO_72] Richiamare l’API revoca della delega
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
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

    # NOTA: il primo caso con ruolo admin torna status code 500, non proprio il massimo
    Scenario Outline: [TC_INCARICATO_73] Richiamare l’API di revoca di una delega in stato REFUSED
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    And l'ente delegante con ruolo "<ruolo>" revoca la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        500 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

    Scenario Outline: [TC_INCARICATO_74] Il richiamo della API di revoca della delega da parte del delegato deve fallire
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
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

    Scenario: [TC_INCARICATO_75] Richiamare l’API da parte del delegante per la revoca della delega alla fruizione al delegato in stato attivo
    Given l'utente è un "admin" di "GSP"
    And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    When l'ente delegante con ruolo "admin" revoca la delega in fruizione
    Then si ottiene status code 200

    Scenario: [TC_INCARICATO_76] Richiamare l’API di verifica archiviazione finalità e rimozione client associati in caso di revoca della delega - lato delegato
      Given l'utente è un "admin" di "GSP"
      And "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
      Given l'ente delegato "PA2"
      And l'utente è un "admin" dell'ente delegato
      And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
      And l'ente delegante "PA1"
      And l'utente è un "admin" dell'ente delegante
      And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
      And l'utente è un "admin" dell'ente delegato
      And l'ente delegato accetta la delega in fruizione
      And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
      And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
      And l'utente è un "admin" dell'ente delegante
      When l'ente delegante con ruolo "admin" revoca la delega in fruizione
      And il delegato controlla che la finalità sia stata archiviata

      #da rivedere
    Scenario Outline: [TC_INCARICATO_77] Richiamare l’API di visualizzazione elenco deleghe ricevute
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
    And l'utente è un "<ruolo>" dell'ente delegante
    And il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'ente delegante con ruolo "<ruolo>" revoca la delega in fruizione
    When il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    Then si ottiene status code <statusCode>

    Examples:
        | ruolo       | statusCode |
        | admin       |        200 |
        | api         |        403 |
        | security    |        403 |
        | api,security|        403 |
        | support     |        403 |

    #da rivedere
  Scenario Outline: [TC_INCARICATO_78] Richiamare l’API di visualizzazione dettagli delega conferita
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
    And l'utente è un "<ruolo>" dell'ente delegante
    And il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'ente delegante con ruolo "<ruolo>" revoca la delega in fruizione
    # TODO: Controllare RICHIESTA di fruizione in stato "WAITING_FOR_APPROVAL"
    When il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    Then si ottiene status code <statusCode>

    Examples:
        | ruolo       | statusCode |
        | admin       |        200 |
        | api         |        403 |
        | security    |        403 |
        | api,security|        403 |
        | support     |        403 |

    #da rivedere
  Scenario Outline: [TC_INCARICATO_79] Richiamare l’API di visualizzazione dettagli delega ricevuta
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
    And il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'ente delegante con ruolo "<ruolo>" revoca la delega in fruizione
    # TODO: Controllare RICHIESTA di fruizione in stato "WAITING_FOR_APPROVAL"
    When il delegato ha già creato 0 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    Then si ottiene status code <statusCode>

    Examples:
        | ruolo       | statusCode |
        | admin       |        200 |
        | api         |        403 |
        | security    |        403 |
        | api,security|        403 |
        | support     |        403 |

#    Scenario Outline: [TC_INCARICATO_80] Richiamare l’API di visualizzazione elenco deleghe da un utente non amministratore
#    Given l'ente delegato "PA1"
#    And l'utente è un "admin" dell'ente delegato
#    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
#    And l'ente delegante "PA2"
#    And l'utente è un "admin" dell'ente delegante
#    And l'ente delegante ha già creato e pubblicato 1 e-service
#    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
#    And l'utente è un "admin" dell'ente delegato
#    And l'ente delegato accetta la delega in fruizione
#    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
#    And l'utente è un "<ruolo>" dell'ente delegato
#    # TODO: Controllare DISPONIBILITA di fruizione in stato "WAITING_FOR_APPROVAL"
#    When il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
#    Then si ottiene status code <statusCode>
#
#    Examples:
#        | ruolo       | statusCode |
#        | admin       |        200 |
#        | api         |        403 |
#        | security    |        403 |
#        | api,security|        403 |
#        | support     |        403 |
#
#    Scenario Outline: [TC_INCARICATO_81] Richiamare l’API di visualizzazione del dettaglio della delega da un utente non amministratore
#    Given l'ente delegato "PA2"
#    And l'utente è un "admin" dell'ente delegato
#    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
#    And l'ente delegante "PA1"
#    And l'utente è un "<ruolo>" dell'ente delegante
#    And l'ente delegante ha già creato e pubblicato 1 e-service
#    When l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
#    Then si ottiene status code <statusCode>
#
#    Examples:
#      | ruolo       | statusCode |
#      | admin       |        200 |
#      | api         |        403 |
#      | security    |        403 |
#      | api,security|        403 |
#      | support     |        403 |
#
#    Scenario Outline: [TC_INCARICATO_82] Richiamare l’API di accettazione di una delega da parte di un delegato in stato pending - utente non amministratore
#    Given l'ente delegato "PA2"
#    And l'utente è un "admin" dell'ente delegato
#    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
#    And l'ente delegante "PA1"
#    And l'utente è un "admin" dell'ente delegante
#    And l'ente delegante ha già creato e pubblicato 1 e-service
#    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
#    When l'utente è un "<ruolo>" dell'ente delegato
#    And l'ente delegato accetta la delega in fruizione
#    Then si ottiene status code <statusCode>
#
#    Examples:
#      | ruolo       | statusCode |
#      | admin       |        200 |
#      | api         |        403 |
#      | security    |        403 |
#      | api,security|        403 |
#      | support     |        403 |
#
#    Scenario Outline: [TC_INCARICATO_83] Richiamare l’API di rifiuto di una delega da parte di un delegato in stato pending - utente non amministratore
#    Given l'ente delegato "PA1"
#    And l'utente è un "admin" dell'ente delegato
#    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
#    And l'ente delegante "PA2"
#    And l'utente è un "admin" dell'ente delegante
#    And l'ente delegante ha già creato e pubblicato 1 e-service
#    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
#    And l'utente è un "<ruolo>" dell'ente delegato
#    And l'ente delegato rifiuta la delega in fruizione
#    Then si ottiene status code <statusCode>
#
#    Examples:
#      | ruolo       | statusCode |
#      | admin       |        200 |
#      | api         |        403 |
#      | security    |        403 |
#      | api,security|        403 |
#      | support     |        403 |
#
#    Scenario Outline: [TC_INCARICATO_84] Richiamare l’API da parte del delegante per la revoca della delega alla fruizione al delegato in stato attivo - utente non amministratore
#    Given l'ente delegato "PA2"
#    And l'utente è un "admin" dell'ente delegato
#    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
#    And l'ente delegante "PA1"
#    And l'utente è un "admin" dell'ente delegante
#    And l'ente delegante ha già creato e pubblicato 1 e-service
#    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
#    And l'utente è un "<ruolo>" dell'ente delegato
#    And l'ente delegato accetta la delega in fruizione
#    When l'ente delegante con ruolo "<ruolo>" revoca la delega in fruizione
#    Then si ottiene status code <statusCode>
#
#    Examples:
#      | ruolo       | statusCode |
#      | admin       |        200 |
#      | api         |        403 |
#      | security    |        403 |
#      | api,security|        403 |
#      | support     |        403 |

  # TODO 10/02/2025: chiedere conferma che gli altri ruoli possano ottenere questa informazione
  Scenario Outline: [TC_INCARICATO_85] Richiamare l’API di visualizzazione elenco deleghe conferite lato delegante
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "<ruolo>" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'ente delegante visualizza l'elenco delle deleghe conferite
    Then si ottiene status code 200

    Examples:
      | ruolo        |
      | admin        |
      | api          |
      | security     |
      | api,security |
      | support      |

  Scenario Outline: [TC_INCARICATO_86] Richiamare l’API di visualizzazione elenco deleghe ricevute lato delegato da parte di un utente amministratore o non-amministratore
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "<ruolo>" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'ente delegato visualizza l'elenco delle deleghe ricevute
    Then si ottiene status code 200

    Examples:
      | ruolo        |
      | admin        |
      | api          |
      | security     |
      | api,security |
      | support      |

  Scenario Outline: [TC_INCARICATO_87_88] Richiamare l’API di visualizzazione dettaglio delega conferita lato delegante, e di quella ricevuta lato delegato
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato

    # lato delegante
    Given l'utente è un "<ruolo>" dell'ente delegante
    When l'utente visualizza il dettaglio della delega creata
    Then si ottiene status code 200

    # lato delegato
    Given l'utente è un "<ruolo>" dell'ente delegato
    When l'utente visualizza il dettaglio della delega creata
    Then si ottiene status code 200

    Examples:
      | ruolo        |
      | admin        |
      | api          |
      | security     |
      | api,security |
      | support      |

  # NOTA BUG: il test fallisce con status code 500
  # Response body: {"type":"about:blank","title":"Active agreement for this eservice and consumer exists","status":500,"detail":"Active agreement 7175e3ff-45fd-49e1-b517-33c206a873aa for eservice afc51671-9635-4db0-9b12-6da73ea9d87a and consumer 0e9e2dab-2e93-4f24-ba59-38d9f11198ca exists","correlationId":"c9fd4049-22c9-403c-b1ce-0962c8da9b58","errors":[{"code":"0015","detail":"Active agreement 7175e3ff-45fd-49e1-b517-33c206a873aa for eservice afc51671-9635-4db0-9b12-6da73ea9d87a and consumer 0e9e2dab-2e93-4f24-ba59-38d9f11198ca exists"}]}
  Scenario Outline: [TC_INCARICATO_89] L'ente NON deve essere in grado di creare una delega per un e-service per il quale ha in corso una richiesta di fruizione in stato ACTIVE, SUSPENDED
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'utente è un "admin" di "PA1"
    And l'ente "PA1" concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato <statoFruizione> per quell'e-service
    When l'ente "PA2" ha inoltrato una richiesta di delega in fruizione all'ente "PA1"
    Then si ottiene status code <statusCode>
    Examples:
      | statusCode | statoFruizione |
      |        403 | "ACTIVE"       |
      |        403 | "SUSPENDED"    |
      |        403 | "PENDING"      |

  # NOTA BUG: il test fallisce con status code 500
  # Response body: {"type":"about:blank","title":"Active agreement for this eservice and consumer exists","status":500,"detail":"Active agreement 7175e3ff-45fd-49e1-b517-33c206a873aa for eservice afc51671-9635-4db0-9b12-6da73ea9d87a and consumer 0e9e2dab-2e93-4f24-ba59-38d9f11198ca exists","correlationId":"c9fd4049-22c9-403c-b1ce-0962c8da9b58","errors":[{"code":"0015","detail":"Active agreement 7175e3ff-45fd-49e1-b517-33c206a873aa for eservice afc51671-9635-4db0-9b12-6da73ea9d87a and consumer 0e9e2dab-2e93-4f24-ba59-38d9f11198ca exists"}]}
  # NOTA DEV 10/02/2025: sarebbe il caso di trovare il modo di accorparlo con TC_INCARICATO_90
  Scenario: [TC_INCARICATO_90] L'ente NON deve essere in grado di creare una delega per un e-service per il quale ha in corso una richiesta di fruizione in stato PENDING
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    And l'utente è un "admin" di "PA1"
    And l'ente "PA1" concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'ente "PA2" ha inoltrato una richiesta di delega in fruizione all'ente "PA1"
    Then si ottiene status code 403