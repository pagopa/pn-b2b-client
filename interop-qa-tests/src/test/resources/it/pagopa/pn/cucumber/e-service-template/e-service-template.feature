@e-service-template
Feature: Test API of e-service template
  #Background:
  #  Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe in fruizione
  #  And l'ente "PA1" rimuove la disponibilità a ricevere deleghe in fruizione
  #  And l'ente "GSP2" rimuove la disponibilità a ricevere deleghe in fruizione

  Scenario Outline: [INCARICATO-EST-001] La creazione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "<ruolo>" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità <modalità>
    Then si ottiene status code 403
    Examples:
      | ruolo       | modalità     |
      | security    | erogazione   |
      | api,security| erogazione   |
      | support     | erogazione   |
      | security    | ricezione    |
      | api,security| ricezione    |
      | support     | ricezione    |

  Scenario Outline: [INCARICATO-EST-002] La creazione di un e-service template può essere fatta da un ente in veste di ADMIN o API portando ad un template in stato DRAFT
    Given l'utente è un "<ruolo>" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità <modalità>
    Then si ottiene status code 200
    And l'e-service template è in stato di DRAFT
    Examples:
      | ruolo       | modalità     |
      | admin       | erogazione   |
      | api         | erogazione   |
      | admin       | ricezione    |
      | api         | ricezione    |

  Scenario Outline: [INCARICATO-EST-003] La creazione di un e-service template NON può riuscire se viene specificato il nome di un template già esistente
    Given l'utente è un "admin" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità <modalità>
    And l'utente effettua la creazione di un e-service template in modalità <modalità> usando lo stesso nome
    Then si ottiene status code 403
    Examples:
      | modalità     |
      | erogazione   |
      | erogazione   |
      | ricezione    |
      | ricezione    |

  # La modalità di erogazione è ininfluente in questi scenari, si è scelto di porre "erogazione"
  Scenario Outline: [INCARICATO-EST-004] La pubblicazione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la pubblicazione dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo       |
      | security    |
      | api,security|
      | support     |
      | security    |
      | api,security|
      | support     |

  Scenario Outline: [INCARICATO-EST-005] La pubblicazione di un e-service template può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la pubblicazione dell'e-service template
    Then si ottiene status code 200
    And l'e-service template è in stato di PUBLISHED
    Examples:
      | ruolo       |
      | admin       |
      | api         |

  Scenario Outline: [INCARICATO-EST-006] La sospensione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione
    And l'utente effettua la pubblicazione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la sospensione dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo       |
      | security    |
      | api,security|
      | support     |
      | security    |
      | api,security|
      | support     |

  Scenario Outline: [INCARICATO-EST-007] La sospensione di un e-service template può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione
    And l'utente effettua la pubblicazione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la sospensione dell'e-service template
    Then si ottiene status code 200
    And l'e-service template è in stato di SUSPENDED
    Examples:
      | ruolo       |
      | admin       |
      | api         |

  Scenario Outline: [INCARICATO-EST-008] La riattivazione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione
    And l'utente effettua la pubblicazione dell'e-service template
    And l'utente effettua la sospensione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la riattivazione dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo       |
      | security    |
      | api,security|
      | support     |
      | security    |
      | api,security|
      | support     |

  Scenario Outline: [INCARICATO-EST-009] La riattivazione di un e-service template può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione
    And l'utente effettua la pubblicazione dell'e-service template
    And l'utente effettua la sospensione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la riattivazione dell'e-service template
    Then si ottiene status code 200
    And l'e-service template è in stato di PUBLISHED
    Examples:
      | ruolo       |
      | admin       |
      | api         |


    #TODO smistare gli scenari in file .feature più piccoli. Possibili divisioni:
      # test che rigurdano il ciclo di vista del template (creazione, pubblicazione, sospensione, riattivazione, cancellazione)
      # ...


    #TODO: test in cui vengono specificati tutti i campi nella api di creazione
    #TODO: test dove vengono specificati meno dei campi obbligatori nella api di creazione

    #TODO test in cui vengono specificati tutti i campi nella api di creazione, oggetto Versione
    #TODO: test dove vengono specificati meno dei campi obbligatori nella api di creazione, oggetto Versione