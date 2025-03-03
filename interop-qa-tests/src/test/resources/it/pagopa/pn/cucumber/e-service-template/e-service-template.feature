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

    # TODO 28/02/2025 al momento questo è implicito nel passo "l'utente effettua la riattivazione...", e anche
    # in quelli di pubblicazione e sospensione del template. Considerare di:
    # -   riformulare il nome dei suddetti passi in qualcosa come "l'utente effettua la riattivazione dell'e-service template con successo"
    # -   introdurre le variante del tipo "l'utente tenta la riattivazione dell'e-service template" in cui NON viene fatto il check dei cambiamenti;
    # I primi verrebbero usati nelle clausole Given, i secondi nelle When
    And l'e-service template è in stato di PUBLISHED

    Examples:
      | ruolo       |
      | admin       |
      | api         |

  Scenario Outline: [INCARICATO-EST-010] La modifica di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche all'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo       | stato     |
      | security    | DRAFT     |
      | api,security| DRAFT     |
      | support     | DRAFT     |
      | security    | PUBLISHED |
      | api,security| PUBLISHED |
      | support     | PUBLISHED |
      | security    | SUSPENDED |
      | api,security| SUSPENDED |
      | support     | SUSPENDED |

  Scenario Outline: [INCARICATO-EST-011] La modifica di un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche all'e-service template
    Then si ottiene status code 200
    And le modifiche al template sono state applicate correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario Outline: [INCARICATO-EST-012] La modifica di un e-service template in stato PUBLISHED o SUSPENDED non può essere fatta attraverso l'uso della api generica
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche all'e-service template

    # note: status code da accertare
    Then si ottiene status code 403

    Examples:
      | ruolo   | stato |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario: [INCARICATO-EST-013] La modifica di un e-service template in stato DRAFT non può essere fatta specificando lo stesso nome
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA1"
    And l'utente tenta di modificare l'e-service template specificando lo stesso nome
    Then si ottiene status code 403

  Scenario: [INCARICATO-EST-014] La modifica di un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA2"
    And l'utente tenta delle modifiche all'e-service template
    Then si ottiene status code 403

  Scenario: [INCARICATO-EST-015] La modifica di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta delle modifiche a un e-service template inesistente
    Then si ottiene status code 404

  Scenario Outline: [INCARICATO-EST-016] La modifica di una versione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche alla versione dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo       | stato     |
      | security    | DRAFT     |
      | api,security| DRAFT     |
      | support     | DRAFT     |
      | security    | PUBLISHED |
      | api,security| PUBLISHED |
      | support     | PUBLISHED |
      | security    | SUSPENDED |
      | api,security| SUSPENDED |
      | support     | SUSPENDED |

  Scenario Outline: [INCARICATO-EST-017] La modifica di una versione di un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche alla versione dell'e-service template
    Then si ottiene status code 200
    And le modifiche alla versione sono state applicate correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

    # TODO il testo "attraverso l'uso della api generica" è stato copiato da uno scenario precedente, assicurarsi che abbia senso anche qui
  Scenario Outline: [INCARICATO-EST-018] La modifica di una versione un e-service template in stato PUBLISHED o SUSPENDED non può essere fatta attraverso l'uso della api generica
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche alla versione dell'e-service template

    # note: status code da accertare
    Then si ottiene status code 403

    Examples:
      | ruolo   | stato |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario: [INCARICATO-EST-019] La modifica della versione di un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA2"
    And l'utente tenta delle modifiche alla versione dell'e-service template
    Then si ottiene status code 403

  Scenario: [INCARICATO-EST-020] La modifica della versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta delle modifiche alla versione di un e-service template inesistente
    Then si ottiene status code 404

    #TODO smistare gli scenari in file .feature più piccoli. Possibili divisioni:
      # test che rigurdano il ciclo di vita del template (creazione, pubblicazione, sospensione, riattivazione, cancellazione)
      # test che riguardano le modifiche a risorse esistenti

