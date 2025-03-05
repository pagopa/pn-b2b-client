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

  Scenario Outline: [INCARICATO-EST-021] L'aggiunta di una risk analysis a un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di una risk analysis all'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo         | stato     |
      | security      | DRAFT     |
      | api,security  | DRAFT     |
      | support       | DRAFT     |
      | security      | PUBLISHED |
      | api,security  | PUBLISHED |
      | support       | PUBLISHED |
      | security      | SUSPENDED |
      | api,security  | SUSPENDED |
      | support       | SUSPENDED |

  Scenario Outline: [INCARICATO-EST-022] L'aggiunta di una risk analysis a un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di una risk analysis all'e-service template
    Then si ottiene status code 200
    And l'aggiunta della risk analysis all'e-service è stata effettuata correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario Outline: [INCARICATO-EST-023] L'aggiunta di una risk analysis a un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di una risk analysis all'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo   | stato |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario Outline: [INCARICATO-EST-024] L'aggiunta di una risk analysis a un e-service template in modalità erogazione non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di una risk analysis all'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario: [INCARICATO-EST-025] L'aggiunta di una risk analysis a un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente è un "admin" di "PA2"
    And l'utente tenta l'aggiunta di una risk analysis all'e-service template
    Then si ottiene status code 403

    #TODO scenario non presente fra i test richiesti, avvisare Stefano Netti
  Scenario: [INCARICATO-EST-026] L'aggiunta di una risk analysis a un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta l'aggiunta di una risk analysis a un e-service template inesistente
    Then si ottiene status code 404

  Scenario: [INCARICATO-EST-027] L'aggiunta di una risk analysis a un e-service template in stato DRAFT non può essere fatta specificando lo stesso nome di una risk analysis precedentemente creata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente tenta l'aggiunta di una risk analysis all'e-service template specificando lo stesso nome
    Then si ottiene status code 409

  Scenario Outline: [INCARICATO-EST-028] La cancellazione di una risk analysis di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione della risk analysis dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo         | stato     |
      | security      | DRAFT     |
      | api,security  | DRAFT     |
      | support       | DRAFT     |
      | security      | PUBLISHED |
      | api,security  | PUBLISHED |
      | support       | PUBLISHED |
      | security      | SUSPENDED |
      | api,security  | SUSPENDED |
      | support       | SUSPENDED |

  Scenario Outline: [INCARICATO-EST-029] La cancellazione di una risk analysis di un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione della risk analysis dell'e-service template
    Then si ottiene status code 200
    And la cancellazione della risk analysis dell'e-service è stata effettuata correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario: [INCARICATO-EST-030] La cancellazione di una risk analysis di un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la cancellazione della risk analysis dell'e-service template
    Then si ottiene status code 403

  Scenario: [INCARICATO-EST-031] La cancellazione di una risk analysis inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta la cancellazione di una risk analysis inesistente nell'e-service template
    Then si ottiene status code 404

  Scenario: [INCARICATO-EST-032] La cancellazione di una risk analysis già eliminata non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    And l'utente effettua la cancellazione della risk analysis dell'e-service template con successo
    When l'utente tenta la cancellazione della risk analysis dell'e-service template
    Then si ottiene status code 404

  Scenario Outline: [INCARICATO-EST-033] La modifica di una risk analysis di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica della risk analysis dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo         | stato     |
      | security      | DRAFT     |
      | api,security  | DRAFT     |
      | support       | DRAFT     |
      | security      | PUBLISHED |
      | api,security  | PUBLISHED |
      | support       | PUBLISHED |
      | security      | SUSPENDED |
      | api,security  | SUSPENDED |
      | support       | SUSPENDED |

  Scenario Outline: [INCARICATO-EST-034] La modifica di una risk analysis di un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica della risk analysis dell'e-service template
    Then si ottiene status code 200

    # TODO step come questo possono essere riformulati in maniera più precisa: "la risk analysis ora corrisponde a quanto specificato nella modifica"
    And la modifica della risk analysis dell'e-service è stata effettuata correttamente

    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario: [INCARICATO-EST-035] La modifica di una risk analysis di un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica della risk analysis dell'e-service template
    Then si ottiene status code 403

  Scenario: [INCARICATO-EST-036] La modifica di una risk analysis inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta la modifica di una risk analysis inesistente nell'e-service template
    Then si ottiene status code 404

  Scenario: [INCARICATO-EST-037] La modifica di una risk analysis inserendo il nome di un'altra risk analysis esistente nell'e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente tenta la modifica di una risk analysis inserendo il nome di un'altra risk analysis
    Then si ottiene status code 404

  Scenario Outline: [INCARICATO-EST-038] L'aggiunta di un documento/interfaccia a una versione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo         | stato     | kind      |
      | security      | DRAFT     | DOCUMENT  |
      | api,security  | DRAFT     | DOCUMENT  |
      | support       | DRAFT     | DOCUMENT  |
      | security      | PUBLISHED | DOCUMENT  |
      | api,security  | PUBLISHED | DOCUMENT  |
      | support       | PUBLISHED | DOCUMENT  |
      | security      | SUSPENDED | DOCUMENT  |
      | api,security  | SUSPENDED | DOCUMENT  |
      | support       | SUSPENDED | DOCUMENT  |
      | security      | DRAFT     | INTERFACE |
      | api,security  | DRAFT     | INTERFACE |
      | support       | DRAFT     | INTERFACE |
      | security      | PUBLISHED | INTERFACE |
      | api,security  | PUBLISHED | INTERFACE |
      | support       | PUBLISHED | INTERFACE |
      | security      | SUSPENDED | INTERFACE |
      | api,security  | SUSPENDED | INTERFACE |
      | support       | SUSPENDED | INTERFACE |

  Scenario Outline: [INCARICATO-EST-039] L'aggiunta di un documento/interfaccia a una versione di un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template
    Then si ottiene status code 200
    And l'aggiunta del documento di tipo <kind> alla versione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   | kind      |
      | admin   | DOCUMENT  |
      | api     | DOCUMENT  |
      | admin   | INTERFACE |
      | api     | INTERFACE |

  Scenario Outline: [INCARICATO-EST-040] L'aggiunta di un'interfaccia a una versione di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo   | stato |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario: [INCARICATO-EST-041] L'aggiunta di una seconda interfaccia a una versione di un e-service template in stato DRAFT non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    When l'utente tenta l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template
    Then si ottiene status code 409

  Scenario: [INCARICATO-EST-042] L'aggiunta di un documento a una versione di un e-service template in stato DRAFT non può essere fatta specificando lo stesso nome di un documento precedentemente aggiunto
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo DOCUMENT alla versione dell'e-service template con successo

    # ATTENZIONE 04/03/2025: al momento per "nome" si sta intendendo il parametro "prettyName"
    When l'utente tenta l'aggiunta di un documento di tipo DOCUMENT alla versione dell'e-service template specificando lo stesso nome

    Then si ottiene status code 409

  Scenario Outline: [INCARICATO-EST-043] L'aggiunta di un documento/interfaccia a una versione di un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente è un "admin" di "PA2"
    And l'utente tenta l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template
    Then si ottiene status code 403
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

    #TODO scenario non presente fra i test richiesti, avvisare Stefano Netti
  Scenario Outline: [INCARICATO-EST-044] L'aggiunta di un documento/interfaccia a una versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta l'aggiunta di un documento di tipo <kind> a un e-service template inesistente
    Then si ottiene status code 404
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INCARICATO-EST-045] L'aggiunta di un documento/interfaccia a una versione inesistente di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta l'aggiunta di un documento di tipo <kind> a una versione inesistente dell'e-service template
    Then si ottiene status code 404
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INCARICATO-EST-046] Il reperimento di un documento/interfaccia di un e-service template NON può essere fatto da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta il reperimento del documento dalla versione dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo       | stato     | kind      |
      | security    | DRAFT     | DOCUMENT  |
      | api,security| DRAFT     | DOCUMENT  |
      | support     | DRAFT     | DOCUMENT  |
      | security    | PUBLISHED | DOCUMENT  |
      | api,security| PUBLISHED | DOCUMENT  |
      | support     | PUBLISHED | DOCUMENT  |
      | security    | SUSPENDED | DOCUMENT  |
      | api,security| SUSPENDED | DOCUMENT  |
      | support     | SUSPENDED | DOCUMENT  |
      | security    | DRAFT     | INTERFACE |
      | api,security| DRAFT     | INTERFACE |
      | support     | DRAFT     | INTERFACE |
      | security    | PUBLISHED | INTERFACE |
      | api,security| PUBLISHED | INTERFACE |
      | support     | PUBLISHED | INTERFACE |
      | security    | SUSPENDED | INTERFACE |
      | api,security| SUSPENDED | INTERFACE |
      | support     | SUSPENDED | INTERFACE |

  Scenario Outline: [INCARICATO-EST-047-DRA] Il reperimento di un documento/interfaccia di un e-service template in stato DRAFT può essere fatto da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta il reperimento del documento dalla versione dell'e-service template
    Then si ottiene status code 200
    Examples:
      | ruolo   | kind      |
      | admin   | DOCUMENT  |
      | api     | DOCUMENT  |
      | admin   | INTERFACE |
      | api     | INTERFACE |

  Scenario Outline: [INCARICATO-EST-047-PUB] Il reperimento di un documento/interfaccia di un e-service template in stato DRAFT può essere fatto da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    And l'utente effettua la pubblicazione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta il reperimento del documento dalla versione dell'e-service template
    Then si ottiene status code 200
    Examples:
      | ruolo   | kind      |
      | admin   | DOCUMENT  |
      | api     | DOCUMENT  |
      | admin   | INTERFACE |
      | api     | INTERFACE |

  Scenario Outline: [INCARICATO-EST-047-SUS] Il reperimento di un documento/interfaccia di un e-service template in stato DRAFT può essere fatto da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    And l'utente effettua la pubblicazione dell'e-service template
    And l'utente effettua la sospensione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta il reperimento del documento dalla versione dell'e-service template
    Then si ottiene status code 200
    Examples:
      | ruolo   | kind      |
      | admin   | DOCUMENT  |
      | api     | DOCUMENT  |
      | admin   | INTERFACE |
      | api     | INTERFACE |

  Scenario Outline: [INCARICATO-EST-048] Il reperimento di un documento/interfaccia di un e-service template non può essere fatto da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta il reperimento del documento dalla versione dell'e-service template
    Then si ottiene status code 403
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario: [INCARICATO-EST-049] Il reperimento di un documento da un e-service template inesistente non può essere fatto
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta il reperimento di un documento da un e-service template inesistente
    Then si ottiene status code 404

  Scenario: [INCARICATO-EST-050] Il reperimento di un documento/interfaccia inesistente da un e-service template non può essere fatto
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta il reperimento di un documento inesistente dalla versione dell'e-service template
    Then si ottiene status code 404

  Scenario Outline: [INCARICATO-EST-051] La modifica di un documento/interfaccia di un e-service template in qualsiasi stato NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica del documento dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo         | stato     | kind      |
      | security      | DRAFT     | DOCUMENT  |
      | api,security  | DRAFT     | DOCUMENT  |
      | support       | DRAFT     | DOCUMENT  |
      | security      | PUBLISHED | DOCUMENT  |
      | api,security  | PUBLISHED | DOCUMENT  |
      | support       | PUBLISHED | DOCUMENT  |
      | security      | SUSPENDED | DOCUMENT  |
      | api,security  | SUSPENDED | DOCUMENT  |
      | support       | SUSPENDED | DOCUMENT  |
      | security      | DRAFT     | INTERFACE |
      | api,security  | DRAFT     | INTERFACE |
      | support       | DRAFT     | INTERFACE |
      | security      | PUBLISHED | INTERFACE |
      | api,security  | PUBLISHED | INTERFACE |
      | support       | PUBLISHED | INTERFACE |
      | security      | SUSPENDED | INTERFACE |
      | api,security  | SUSPENDED | INTERFACE |
      | support       | SUSPENDED | INTERFACE |

  Scenario Outline: [INCARICATO-EST-052] La modifica di un documento/interfaccia di un e-service template in qualsiasi stato può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica del documento dell'e-service template
    Then si ottiene status code 200
    And la modifica del documento dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   | stato     | kind      |
      | admin   | DRAFT     | DOCUMENT  |
      | api     | DRAFT     | DOCUMENT  |
      | admin   | PUBLISHED | DOCUMENT  |
      | api     | PUBLISHED | DOCUMENT  |
      | admin   | SUSPENDED | DOCUMENT  |
      | api     | SUSPENDED | DOCUMENT  |
      | admin   | DRAFT     | INTERFACE |
      | api     | DRAFT     | INTERFACE |
      | admin   | PUBLISHED | INTERFACE |
      | api     | PUBLISHED | INTERFACE |
      | admin   | SUSPENDED | INTERFACE |
      | api     | SUSPENDED | INTERFACE |

  Scenario Outline: [INCARICATO-EST-053] La modifica di un documento/interfaccia di un e-service template non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica del documento dell'e-service template
    Then si ottiene status code 403
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario: [INCARICATO-EST-054] La modifica di un documento/interfaccia da un e-service template inesistente non può essere fatta
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la modifica di un documento da un e-service template inesistente
    Then si ottiene status code 404

  Scenario: [INCARICATO-EST-055] La modifica di un documento da una versione inesistente di un e-service template non può essere fatta
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo DOCUMENT alla versione dell'e-service template con successo
    When l'utente tenta la modifica del documento da una versione inesistente dell'e-service template
    Then si ottiene status code 404

  Scenario: [INCARICATO-EST-056] La modifica di un documento/interfaccia inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta la modifica di un documento inesistente nell'e-service template
    Then si ottiene status code 404

  Scenario Outline: [INCARICATO-EST-057] La modifica di un documento inserendo il nome di un altro documento esistente nell'e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind1> alla versione dell'e-service template con successo
    And l'utente effettua l'aggiunta di un documento di tipo <kind2> alla versione dell'e-service template con successo
    When l'utente tenta la modifica di un documento inserendo il nome di un altro documento
    Then si ottiene status code 409
    Examples:
      | kind1     | kind2     |
      | DOCUMENT  | DOCUMENT  |
      #| INTERFACE | INTERFACE |  <-- combinazione impossibile, testata in uno scenartio precedente
      | DOCUMENT  | INTERFACE |
      | INTERFACE | DOCUMENT  |

  Scenario Outline: [INCARICATO-EST-058] La cancellazione di un documento/interfaccia di un e-service template NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione del documento dell'e-service template
    Then si ottiene status code 403
    Examples:
      | ruolo         | stato     | kind      |
      | security      | DRAFT     | DOCUMENT  |
      | api,security  | DRAFT     | DOCUMENT  |
      | support       | DRAFT     | DOCUMENT  |
      | security      | PUBLISHED | DOCUMENT  |
      | api,security  | PUBLISHED | DOCUMENT  |
      | support       | PUBLISHED | DOCUMENT  |
      | security      | SUSPENDED | DOCUMENT  |
      | api,security  | SUSPENDED | DOCUMENT  |
      | support       | SUSPENDED | DOCUMENT  |
      | security      | DRAFT     | INTERFACE |
      | api,security  | DRAFT     | INTERFACE |
      | support       | DRAFT     | INTERFACE |
      | security      | PUBLISHED | INTERFACE |
      | api,security  | PUBLISHED | INTERFACE |
      | support       | PUBLISHED | INTERFACE |
      | security      | SUSPENDED | INTERFACE |
      | api,security  | SUSPENDED | INTERFACE |
      | support       | SUSPENDED | INTERFACE |

  Scenario Outline: [INCARICATO-EST-059] La cancellazione di un documento/interfaccia di un e-service template può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione del documento dell'e-service template
    Then si ottiene status code 200
    And la cancellazione del documento dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   | stato     | kind      |
      | admin   | DRAFT     | DOCUMENT  |
      | api     | DRAFT     | DOCUMENT  |
      | admin   | PUBLISHED | DOCUMENT  |
      | api     | PUBLISHED | DOCUMENT  |
      | admin   | SUSPENDED | DOCUMENT  |
      | api     | SUSPENDED | DOCUMENT  |
      | admin   | DRAFT     | INTERFACE |
      | api     | DRAFT     | INTERFACE |
      # la cancellazione di INTERFACE in stato published o suspended non può essere effettuata

  Scenario Outline: [INCARICATO-EST-060] La cancellazione di un documento/interfaccia di un e-service template non può essere effettuata da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la cancellazione del documento dell'e-service template
    Then si ottiene status code 403
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INCARICATO-EST-061] La cancellazione di un'interfaccia di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    When l'utente tenta la cancellazione del documento dell'e-service template
    Then si ottiene status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INCARICATO-EST-062] La cancellazione di un documento/interfaccia inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    When l'utente tenta la cancellazione di un documento inesistente nell'e-service template
    Then si ottiene status code 404
    Examples:
      | stato     |
      | DRAFT     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INCARICATO-EST-063] La cancellazione di un documento/interfaccia da un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta la cancellazione di un documento da un e-service template inesistente
    Then si ottiene status code 404

  Scenario Outline: [INCARICATO-EST-064] La cancellazione di un documento/interfaccia da una versione inesistente di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente tenta la cancellazione del documento da una versione inesistente nell'e-service template
    Then si ottiene status code 404
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INCARICATO-EST-065] La cancellazione di un documento/interfaccia già eliminato non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    And l'utente effettua la cancellazione del documento dall'e-service template con successo
    When l'utente tenta la cancellazione del documento dell'e-service template
    Then si ottiene status code 404
    Examples:
      | stato     | kind      |
      | DRAFT     | DOCUMENT  |
      | DRAFT     | INTERFACE |
      | PUBLISHED | DOCUMENT  |
      | SUSPENDED | DOCUMENT  |



  #TODO la maggior parte dei test sono fatti su template in mod. RICEZIONE. Valutare che non sia il caso di testare per entrambe le modalità.

    #TODO smistare gli scenari in file .feature più piccoli e/o i relativi step in classi più piccole. Possibile divisione:
      # test che rigurdano il ciclo di vita del template (creazione, pubblicazione, sospensione, riattivazione, cancellazione)
      # test che riguardano la versione
      # test che riguardano la risk anlysis
      # test che riguardano i documenti (nota: test di caricamento e lettura sono inter-dipendenti, la creazione non può essere verificata senza la lettura, e viceversa)
      # ...

    #TODO associare un tag per ogni risorsa testata: template, version, riskAnalysis, document...