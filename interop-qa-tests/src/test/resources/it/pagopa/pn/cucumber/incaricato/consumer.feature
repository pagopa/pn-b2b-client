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
    And l'ente "PA4" rimuove la disponibilità a ricevere deleghe in fruizione

  @deleghe1
  Scenario Outline: [TC_INCARICATO_45] Verificare che il richiamo dell’API di disponibilità in fruizione di un e-service possa essere compiuto da un utente di tipo amministratore
    Given l'utente è un "<ruolo>" di "PA2"
    When l'ente "PA2" concede la disponibilità a ricevere deleghe in fruizione
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ruolo | statusCode |
      | admin | 200        |

    @sad-path
    Examples:
      | ruolo        | statusCode |
      | api          | 403        |
      | security     | 403        |
      | api,security | 403        |
      | support      | 403        |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ruolo    | statusCode |
      | reviewer | 403        |
      | viewer   | 403        |


  @happy-path @deleghe2
  Scenario: [TC_INCARICATO_46] Verificare che il richiamo dell’API di disponibilità in fruizione di un e-service, per il quale è già stata data disponibilità, possa essere effettuato nuovamente
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    When l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    Then si ottiene status code 200

  @deleghe2
  Scenario Outline: [TC_INCARICATO_47] Richiamare l’API di creazione di una delega da parte di un fruitore delegante verso un altro ente delegato
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "<enteDelegante>"
    When l'ente delegante con ruolo "<ruolo>" ha inoltrato una richiesta di delega in fruizione all'ente delegato
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ruolo | enteDelegante | statusCode |
      | admin | PA1           | 200        |

    @sad-path
    Examples:
      | ruolo        | enteDelegante | statusCode |
      | api          | PA1           | 403        |
      | security     | PA1           | 403        |
      | api,security | PA1           | 403        |
      | support      | PA1           | 403        |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ruolo    | enteDelegante | statusCode |
      | reviewer | PA4           | 403        |
      | viewer   | PA4           | 403        |

  @sad-path @deleghe2
  Scenario: [TC_INCARICATO_47_BIS] Richiamare l’API di creazione di una delega da parte di un fruitore delegante verso un altro ente delegato, su un e-service NON delegabile in fruizione
    Given "GSP" ha già creato e pubblicato 1 e-service
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    When l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    Then si ottiene status code 400

  @sad-path @deleghe1
  Scenario Outline: [TC_INCARICATO_48] La creazione di una delega in fruizione verso un ente che non ha dato la propria disponibilità a ricevere deleghe in fruizione deve fallire
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegante "<enteDelegante>"
    And l'utente è un "<ruolo>" dell'ente delegante
    And l'ente delegato "PA2"
    #And l'ente delegato non è disponibile ad accettare deleghe     <--  si ritiene implicito per l'assenza dello step di concessione della disponibilità
    When l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo        | enteDelegante | statusCode |
      | admin        | PA1           | 403        |
      | api          | PA1           | 403        |
      | security     | PA1           | 403        |
      | api,security | PA1           | 403        |
      | support      | PA1           | 403        |

    @nuovi-operatori-update
    Examples:
      | ruolo    | enteDelegante | statusCode |
      | reviewer | PA4           | 403        |
      | viewer   | PA4           | 403        |

  @deleghe2
  Scenario Outline: [TC_INCARICATO_50] Richiamare l’API di accettazione di una delega in stato WAITING_FOR_APPROVAL
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'utente è un "<ruolo>" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ruolo | statusCode |
      | admin | 200        |

    @sad-path
    Examples:
      | ruolo        | statusCode |
      | api          | 403        |
      | security     | 403        |
      | api,security | 403        |
      | support      | 403        |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ruolo    | statusCode |
      | reviewer | 403        |
      | viewer   | 403        |

  @sad-path @deleghe1
  Scenario: [TC_INCARICATO_51] Richiamare l’API di accettazione di una delega in stato "revocata" deve produrre un errore
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'ente delegante con ruolo "admin" revoca la delega in fruizione
    When l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    Then si ottiene status code 409

  @sad-path @deleghe1
  Scenario: [TC_INCARICATO_52] Richiamare l’API di accettazione di una delega in stato rifiutata
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    And l'ente delegato accetta la delega in fruizione
    Then si ottiene status code 409

  @deleghe1
  Scenario Outline: [TC_INCARICATO_53] Richiamare l’API di rifiuto di una delega in stato WAITING_FOR_APPROVAL
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "<enteDelegato>"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "<enteDelegante>"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "<ruolo>" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ruolo | enteDelegato | enteDelegante | statusCode |
      | admin | PA1          | PA2           | 200        |

    @sad-path
    Examples:
      | ruolo        | enteDelegato | enteDelegante | statusCode |
      | api          | PA1          | PA2           | 403        |
      | security     | PA1          | PA2           | 403        |
      | api,security | PA1          | PA2           | 403        |
      | support      | PA1          | PA2           | 403        |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ruolo    | enteDelegato | enteDelegante | statusCode |
      | reviewer | PA2          | PA4           | 403        |
      | viewer   | PA2          | PA4           | 403        |

  @sad-path @deleghe1
  Scenario: [TC_INCARICATO_54] Richiamare l’API di rifiuto su una delega in stato REVOKED
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'ente delegante con ruolo "admin" revoca la delega in fruizione
    When l'utente è un "admin" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    Then si ottiene status code 409

  @sad-path @deleghe1
  Scenario Outline: [TC_INCARICATO_55] Richiamare l’API di rifiuto di una delega da parte del delegante: non permessa in quanto il rifiuto è una facoltà esclusiva del delegato
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
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
      | ruolo        | statusCode |
      | admin        | 403        |
      | api          | 403        |
      | security     | 403        |
      | api,security | 403        |
      | support      | 403        |

    @nuovi-operatori-update
    Examples:
      | ruolo    | statusCode |
      | reviewer | 403        |
      | viewer   | 403        |

  @sad-path @deleghe1
  Scenario: [TC_INCARICATO_56] Richiamare l’API di rifiuto di una delega in stato ACTIVE: non è permesso rifiutare una delega già precedentemente accettata
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
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

  @happy-path @deleghe1
  Scenario: [TC_INCARICATO_57] Richiamare l’API di creazione fruizione da parte di un delegato alla fruizione, specificando la delega corretta
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    When il delegato ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione
    Then si ottiene status code 200
    And si recupera la lista dei delegatori e si verifica che non sia vuota
    And si recupera la lista dei delegatori con deleghe ATTIVE e si verifica che non sia vuota
    And viene recuperata la lista degli e-service delegati e si verifica che non sia vuota

  @sad-path
  Scenario Outline: [TC_INTEROP_NON-ADMIN_FRUITION_REQUEST] Un utente con ruolo NON amministratore NON può richiedere la fruizione di un e-service
    Given "GSP" ha già creato e pubblicato 1 e-service
    And l'utente è un "<ruolo>" di "PA2"
    When l'utente crea una richiesta di fruizione
    Then si ottiene status code 403
    Examples:
      | ruolo        |
      | api          |
      | security     |
      | api,security |
      | support      |

    @nuovi-operatori-update
    Examples:
      | ruolo    |
      | reviewer |
      | viewer   |

  # NOTA BUG: per un risultato di tipo 'not found' ci si aspetterebbe un 404, non 400
  # Response Body: {"type":"about:blank","title":"Delegation not found","status":400,"detail":"Delegation c3bb23e4-5b43-4cf9-88aa-704a5ebd0374 not found","correlationId":"30b9e0d1-372a-4105-af1e-8ae850b40d6a","errors":[{"code":"0026","detail":"Delegation c3bb23e4-5b43-4cf9-88aa-704a5ebd0374 not found"}]}
  @sad-path @deleghe1
  Scenario: [TC_INCARICATO_58] Richiamare l’API di creazione di una richiesta di fruizione, specificando una delega inesistente
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    When l'utente ha già creato una richiesta di fruizione indicando una delega inesistente
    Then si ottiene status code 400

  @sad-path @deleghe1
  Scenario: [TC_INCARICATO_58_BIS] L'ente delegante NON può creare una richiesta di fruizione per un e-service per il quale ha già creato una richiesta di fruizione
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    # Processo di produzione di una delega tra il delegante e un terzo ente
    And l'utente è un "admin" di "PA4"
    And l'ente "PA4" concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un "admin" dell'ente delegante
    When l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente terzo "PA4"
    Then si ottiene status code 409


  # NOTA BUG: per un risultato di tipo 'not found' ci si aspetterebbe un 404, non 400
  # Response body: {"type":"about:blank","title":"Delegation not found","status":400,"detail":"Delegation dd54ea4f-3922-4ab5-847c-35765fef8efe not found","correlationId":"b1303079-d942-4f0b-ab71-765d389645c7","errors":[{"code":"0026","detail":"Delegation dd54ea4f-3922-4ab5-847c-35765fef8efe not found"}]}
  @sad-path @deleghe1
  Scenario: [TC_INCARICATO_58_TRIS] Richiamare l’API di creazione di una richiesta di fruizione, specificando una delega che non compete né al delegante né al delegato
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    # Processo di produzione di una delega tra due enti diversi da delegante e delegato
    And l'utente è un "admin" di "PA4"
    And l'ente "PA4" concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un "admin" di "PA3"
    And l'ente "PA3" ha inoltrato una richiesta di delega in fruizione all'ente terzo "PA4"
    And l'utente è un "admin" dell'ente delegato

    When l'utente ha già creato una richiesta di fruizione indicando la delega dell'ente terzo
    Then si ottiene status code 400

  @happy-path @deleghe1
  Scenario Outline: [TC_INCARICATO_59] Richiamare l’API di accettazione di una richiesta di fruizione fatta da un delegato
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
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
    And "PA3" ha già approvato quella richiesta di fruizione
#    When il delegante ha già approvato quella richiesta di fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | statusCode |
      | 200        |
#      | api         |        403 |
#      | security    |        403 |
#      | api,security|        403 |
#      | support     |        403 |
#      | reviewer    |        403 |
#      | viewer      |        403 |

  @happy-path @deleghe1
  Scenario: [TC_INCARICATO_60] Richiamare l’API di rifiuto di una richiesta di fruizione fatta da un delegato
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione
    And "PA3" ha già rifiutato quella richiesta di fruizione
#    When il delegante ha già rifiutato quella richiesta di fruizione
    Then si ottiene status code 200

  @happy-path @deleghe1
  Scenario: [TC_INCARICATO_62] Richiamare l’API di creazione di una finalità da parte di un delegato alla fruizione
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" dell'ente delegato
      #Questo step va rivisto perché il consumer da passare nella richiesta di creazione finalità sembra debba essere quello del DELEGANTE - FATTO!
    When per conto del delegante, il delegato ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    Then si ottiene status code 200

  @happy-path @deleghe1
  Scenario Outline: [TC_INCARICATO_63] Richiamare l’API di creazione di un client da parte del delegato alla fruizione
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione con client del delegato utilizzabile
    And l'ente delegato "<enteDelegato>"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "<enteDelegante>"
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
      | ruolo | enteDelegato | enteDelegante | statusCode |
      | admin | PA1          | PA2           | 200        |

    @nuovi-operatori-update
    Examples:
      | ruolo        | enteDelegato | enteDelegante | statusCode |
      | reviewer     | PA2          | PA4           | 403        |
      | viewer       | PA2          | PA4           | 403        |
      | api          | PA2          | PA4           | 403        |
      | security     | PA2          | PA4           | 403        |
      | api,security | PA2          | PA4           | 403        |
      | support      | PA2          | PA4           | 403        |

  @happy-path @deleghe1 @associa-finalita-client
  Scenario: [TC_INCARICATO_64] Richiamare l’API di associazione di un client creato dal delegato ad una finalità creata dal delegato
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione con client del delegato utilizzabile
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

  @sad-path @deleghe1 @associa-finalita-client
  Scenario: [TC_INCARICATO_65] Richiamare l’API di associazione di un client creato dal delegato NON avendo i permessi di associare un client da delegato all' e-service
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
#    And l'utente è un "admin" dell'ente delegante
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" dell'ente delegato
    And il delegato ha già creato 1 client "CONSUMER"
    When l'utente richiede l'associazione della finalità al client
    Then si ottiene status code 400

  @sad-path @deleghe1
  Scenario: [TC_INCARICATO_66] Il delegato richiama l’API di associazione di un client NON precedentemente creato
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
#    And l'utente è un "admin" dell'ente delegante
    And per conto del delegante, il delegato ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" dell'ente delegato
    When l'utente richiede l'associazione della finalità a un client inesistente
    Then si ottiene status code 404

  @happy-path @deleghe1
  Scenario: [TC_INCARICATO_67] Il delegante può creare una finalità per un e-service che ha dato in delega
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    Then si ottiene status code 200

    # NOTA: caso già testato con i test di client-create.feature
  @happy-path @deleghe1
  Scenario: [TC_INCARICATO_68] Richiamare l’API di creazione di un client da parte del delegante alla fruizione
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
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

  @happy-path @deleghe1 @associa-finalita-client
  Scenario: [TC_INCARICATO_69] Richiamare l’API di associazione di un client da parte del delegante alla fruizione
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
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

  @happy-path @deleghe1 @associa-finalita-client
  Scenario: [TC_INCARICATO_70] Un ente delegante deve poter associare un proprio client ad una finalità creata da un ente delegato per un e-service a cui è delegato
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
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

  @sad-path @deleghe1
  Scenario Outline: [TC_INCARICATO_71] Il delegante richiama l’API di associazione di un client NON precedentemente creato
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given il delegante ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente è un "<ruolo>" dell'ente delegante
    And l'utente richiede l'associazione della finalità a un client inesistente
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo | statusCode |
      | admin | 404        |

  @deleghe1
  Scenario Outline: [TC_INCARICATO_72] Richiamare l’API revoca della delega
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'ente delegante con ruolo "<ruolo>" revoca la delega in fruizione
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ruolo | statusCode |
      | admin | 204        |

    @sad-path
    Examples:
      | ruolo        | statusCode |
      | api          | 403        |
      | security     | 403        |
      | api,security | 403        |
      | support      | 403        |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ruolo    | statusCode |
      | reviewer | 403        |
      | viewer   | 403        |

  @sad-path @deleghe1
  Scenario Outline: [TC_INCARICATO_73] Richiamare l’API di revoca di una delega in stato REFUSED
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
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
      | ruolo        | statusCode |
      | admin        | 409        |
      | api          | 403        |
      | security     | 403        |
      | api,security | 403        |
      | support      | 403        |

    @nuovi-operatori-update
    Examples:
      | ruolo    | statusCode |
      | reviewer | 403        |
      | viewer   | 403        |

  @sad-path @deleghe1
  Scenario Outline: [TC_INCARICATO_74] Il richiamo della API di revoca della delega da parte del delegato deve fallire
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "<enteDelegato>"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "<enteDelegante>"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato con ruolo "<ruolo>" revoca la delega in fruizione
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo        | enteDelegato | enteDelegante | statusCode |
      | admin        | PA1          | PA2           | 403        |
      | api          | PA1          | PA2           | 403        |
      | security     | PA1          | PA2           | 403        |
      | api,security | PA1          | PA2           | 403        |
      | support      | PA1          | PA2           | 403        |

    @nuovi-operatori-update
    Examples:
      | ruolo    | enteDelegato | enteDelegante | statusCode |
      | reviewer | PA2          | PA4           | 403        |
      | viewer   | PA2          | PA4           | 403        |

  @happy-path @deleghe2
  Scenario: [TC_INCARICATO_75] Richiamare l’API da parte del delegante per la revoca della delega alla fruizione al delegato in stato attivo
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
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

    # Ticket aperto https://pagopa.atlassian.net/browse/QA-9270
  @happy-path @deleghe2
  Scenario Outline: [TC_INCARICATO_76] Richiamare l’API di verifica archiviazione finalità e rimozione client associati in caso di revoca della delega - lato delegato
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
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
    And il <subject> controlla che la finalità sia stata archiviata
    Then si ottiene lo status code <statusCode>

    Examples:
      | subject   | statusCode |
      | delegato  | 403        |
      | delegante | 200        |

  @happy-path @deleghe1
  Scenario Outline: [TC_INCARICATO_77] Richiamare l’API di visualizzazione finalità precedentemente creata da parte del delegante, a seguito di revoca della delega - lato delegante
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    #And il delegante ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" dell'ente delegante
    And il delegante ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'ente delegante con ruolo "admin" revoca la delega in fruizione
    And l'utente è un "<ruolo>" dell'ente delegante
    When il delegante visualizza la finalità creata
    Then si ottiene status code 200

    Examples:
      | ruolo        |
      | admin        |
      | api          |
      | security     |
      | api,security |
      | support      |

  # Ticket aperto https://pagopa.atlassian.net/browse/QA-9270
  @happy-path @deleghe1
  Scenario: [TC_INCARICATO_78] Richiamare l’API di verifica richiesta di fruizione precedentemente creata da parte del delegato, a fronte della revoca della delega - lato delegante
    Given l'utente è un "admin" di "PA3"
    And "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    And il delegato ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" dell'ente delegante
    When l'ente delegante con ruolo "admin" revoca la delega in fruizione
    #lato delegante
    And il delegante controlla che la richiesta di fruizione sia stata archiviata

  @happy-path @deleghe2
  Scenario Outline: [TC_INCARICATO_85] Richiamare l’API di visualizzazione elenco deleghe conferite lato delegante
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "<ruolo>" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'ente delegante visualizza l'elenco delle deleghe conferite
    Then si ottiene status code 200

#invocazione endpoint negata per ruolo "api" a seguito della risoluzione del ticket https://pagopa.atlassian.net/browse/PIN-9962
    Examples:
      | ruolo        |
      | admin        |
      | security     |
      | api,security |
      | support      |

  @happy-path @deleghe2
  Scenario Outline: [TC_INCARICATO_86] Richiamare l’API di visualizzazione elenco deleghe ricevute lato delegato da parte di un utente amministratore o non-amministratore
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "<ruolo>" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'ente delegato visualizza l'elenco delle deleghe ricevute
    Then si ottiene status code 200

#invocazione endpoint negata per ruolo "api" a seguito della risoluzione del ticket https://pagopa.atlassian.net/browse/PIN-9962
    Examples:
      | ruolo        |
      | admin        |
      | security     |
      | api,security |
      | support      |

  @happy-path @deleghe2
  Scenario Outline: [TC_INCARICATO_87_88] Richiamare l’API di visualizzazione dettaglio delega conferita lato delegante, e di quella ricevuta lato delegato
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
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

  # NOTA BUG: messaggio d'errore impreciso: specifica "Active agreement" anche quando è in stato SUSPENDED
  # Response body: {"type":"about:blank","title":"Active agreement for this eservice and consumer exists","status":500,"detail":"Active agreement 7175e3ff-45fd-49e1-b517-33c206a873aa for eservice afc51671-9635-4db0-9b12-6da73ea9d87a and consumer 0e9e2dab-2e93-4f24-ba59-38d9f11198ca exists","correlationId":"c9fd4049-22c9-403c-b1ce-0962c8da9b58","errors":[{"code":"0015","detail":"Active agreement 7175e3ff-45fd-49e1-b517-33c206a873aa for eservice afc51671-9635-4db0-9b12-6da73ea9d87a and consumer 0e9e2dab-2e93-4f24-ba59-38d9f11198ca exists"}]}
  @sad-path @deleghe1
  Scenario Outline: [TC_INCARICATO_89] L'ente NON deve essere in grado di creare una delega per un e-service per il quale ha in corso una richiesta di fruizione in stato ACTIVE, SUSPENDED
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'utente è un "admin" di "PA1"
    And l'ente "PA1" concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato <statoFruizione> per quell'e-service
    When l'ente "PA2" ha inoltrato una richiesta di delega in fruizione all'ente "PA1"
    Then si ottiene status code <statusCode>
    Examples:
      | statusCode | statoFruizione |
      | 409        | "ACTIVE"       |
      | 409        | "SUSPENDED"    |

  # NOTA BUG: messaggio d'errore impreciso: specifica "Active agreement", anche se in questo caso è in stato PENDING
  # Response body: {"type":"about:blank","title":"Active agreement for this eservice and consumer exists","status":500,"detail":"Active agreement 7175e3ff-45fd-49e1-b517-33c206a873aa for eservice afc51671-9635-4db0-9b12-6da73ea9d87a and consumer 0e9e2dab-2e93-4f24-ba59-38d9f11198ca exists","correlationId":"c9fd4049-22c9-403c-b1ce-0962c8da9b58","errors":[{"code":"0015","detail":"Active agreement 7175e3ff-45fd-49e1-b517-33c206a873aa for eservice afc51671-9635-4db0-9b12-6da73ea9d87a and consumer 0e9e2dab-2e93-4f24-ba59-38d9f11198ca exists"}]}
  # NOTA DEV 10/02/2025: sarebbe il caso di trovare il modo di accorparlo con TC_INCARICATO_89
  @sad-path @deleghe1
  Scenario: [TC_INCARICATO_90] L'ente NON deve essere in grado di creare una delega per un e-service per il quale ha in corso una richiesta di fruizione in stato PENDING
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    And l'utente è un "admin" di "PA1"
    And l'ente "PA1" concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When l'ente "PA2" ha inoltrato una richiesta di delega in fruizione all'ente "PA1"
    Then si ottiene status code 409

  @hotfix-2.15
  Scenario Outline: [TC_INCARICATO_91] Disponendo di una finalità propria verso un certo e-service E di una finalità in delega verso lo stesso e-service, la lettura di entrambe le finalità deve aver successo
    Given "<ente>" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'utente è un "admin" di "PA1"
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice

    When l'utente è un "admin" di "PA1"
    And l'utente richiede la lettura della finalità numero 1
    Then si ottiene status code 200

    Given l'ente "PA1" concede la disponibilità a ricevere deleghe in fruizione
    And l'ente "PA2" ha inoltrato una richiesta di delega in fruizione all'ente "PA1"
    And l'ente "PA1" accetta la delega in fruizione con successo
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service in qualità di delegato
    And per conto di "PA2", "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice

    When l'utente è un "admin" di "PA1"
    And l'utente richiede la lettura della finalità numero 1
    Then si ottiene status code 200

    When l'utente richiede la lettura della finalità numero 2
    Then si ottiene status code 200
    Examples:
      | ente |
      | PA1  |
      | GSP  |

  @deleghe1
  @hotfix_QA-13870
  Scenario Outline: [TC_INCARICATO_45_B_1] Verificare che il richiamo dell’API di disponibilità di delega in fruizione di un e-service NON possa essere compiuto da un ente che non sia una pubblica amministrazione
    Given l'utente è un "admin" di "<ente>"
    When l'ente "<ente>" tenta di concedere la disponibilità a ricevere deleghe in fruizione
    Then si ottiene status code 403

    @happy-path
    Examples:
      | ente    |
      | GSP     |
      | Privato |

  @deleghe1
  @hotfix_QA-13870
  Scenario Outline: [TC_INCARICATO_CAPOFILA_1] Verificare che il richiamo dell’API di indisponibilità di delega di un e-service NON possa essere compiuto da un ente che non sia una pubblica amministrazione
    Given l'utente è un "admin" di "<ente>"
    When l'ente "<ente>" tenta di rimuovere la disponibilità a ricevere deleghe
    Then si ottiene status code 403

    @happy-path
    Examples:
      | ente    |
      | GSP     |
      | Privato |

  @hotfix_QA-13870
  Scenario Outline: [TC_INCARICATO_ESERVICE_1] Un ente della piattaforma che non è una Pubblica Amministrazione può creare un e-service delegabile in fruizione, sia al livello amministrativo che tecnico
    Given l'utente è un "admin" di "<ente>"
    When l'utente tenta di creare un e-service delegabile in fruizione con client del delegato utilizzabile
    Then si ottiene status code 200

    @happy-path
    Examples:
      | ente    |
      | GSP     |
      | Privato |

  @hotfix_QA-13870
  Scenario Outline: [TC_INCARICATO_ESERVICE_2] Un ente della piattaforma può delegare un e-service delegabile in fruizione solo se questo è una Pubblica amministrazione
    Given "<ente_creatore>" ha già creato e pubblicato 1 e-service delegabile in fruizione con client del delegato utilizzabile
    And l'ente delegante "<ente_delegante>"
    And l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    When l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    Then si ottiene status code <status_code>

    Examples:
      | ente_creatore | ente_delegante | status_code |
      | GSP           | Privato        | 403         |
      | Privato       | GSP            | 403         |
      | GSP           | PA2            | 200         |
      | Privato       | PA2            | 200         |
      | PA3           | PA2            | 200         |

  @sad-path
  Scenario: [DELEGATIONS_ACCESS_CONTROL_1] Un utente delegante con ruolo api non può visualizzare l'elenco delle deleghe conferite
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "api" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'ente delegante visualizza l'elenco delle deleghe conferite
    Then si ottiene status code 403

  @sad-path
  Scenario: [DELEGATIONS_ACCESS_CONTROL_2] Un utente delegato con ruolo api non può visualizzare l'elenco delle deleghe ricevute
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "api" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'ente delegato visualizza l'elenco delle deleghe ricevute
    Then si ottiene status code 403