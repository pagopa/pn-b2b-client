@e-service-template
Feature: Test API of e-service template
  #Background:
  #  Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe in fruizione
  #  And l'ente "PA1" rimuove la disponibilità a ricevere deleghe in fruizione
  #  And l'ente "GSP2" rimuove la disponibilità a ricevere deleghe in fruizione

  Scenario Outline: [INTEROP-EST-001] La creazione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "<ruolo>" di "PA1"

    #TODO usare invece lo step sottostante e rimuovere questo, per ridurre le ambiguità e la presenza di step tra loro simili
    #"l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>"
    When l'utente effettua la creazione di un e-service template in modalità <modo>

    Then si ottiene response status code 403

    Examples:
      | ruolo         | modo         |
      | security      | erogazione   |
      | api,security  | erogazione   |
      | support       | erogazione   |
      | security      | ricezione    |
      | api,security  | ricezione    |
      | support       | ricezione    |

  Scenario Outline: [INTEROP-EST-002] La creazione di un e-service template può essere fatta da un ente in veste di ADMIN o API portando ad un template in stato DRAFT
    Given l'utente è un "<ruolo>" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità <modo>
    Then si ottiene response status code 200
    And l'e-service template è in stato di DRAFT
    Examples:
      | ruolo       | modo         |
      | admin       | erogazione   |
      | api         | erogazione   |
      | admin       | ricezione    |
      | api         | ricezione    |

  Scenario Outline: [INTEROP-EST-003] La creazione di un e-service template NON può riuscire se viene specificato il nome di un template già esistente
    Given l'utente è un "admin" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità <modo>
    And l'utente effettua la creazione di un e-service template in modalità <modo> usando lo stesso nome
    Then si ottiene response status code 403
    Examples:
      | modo     |
      | erogazione   |
      | erogazione   |
      | ricezione    |
      | ricezione    |

  Scenario Outline: [INTEROP-EST-006] La sospensione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione
    And l'utente effettua la pubblicazione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la sospensione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo       |
      | security    |
      | api,security|
      | support     |
      | security    |
      | api,security|
      | support     |

  Scenario Outline: [INTEROP-EST-007] La sospensione di un e-service template può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione
    And l'utente effettua la pubblicazione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la sospensione dell'e-service template
    Then si ottiene response status code 200
    And l'e-service template è in stato di SUSPENDED
    Examples:
      | ruolo       |
      | admin       |
      | api         |

  Scenario Outline: [INTEROP-EST-008] La riattivazione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione
    And l'utente effettua la pubblicazione dell'e-service template
    And l'utente effettua la sospensione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la riattivazione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo       |
      | security    |
      | api,security|
      | support     |
      | security    |
      | api,security|
      | support     |

  Scenario Outline: [INTEROP-EST-009] La riattivazione di un e-service template può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione
    And l'utente effettua la pubblicazione dell'e-service template
    And l'utente effettua la sospensione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la riattivazione dell'e-service template
    Then si ottiene response status code 200

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

  Scenario Outline: [INTEROP-EST-010] La modifica di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche all'e-service template
    Then si ottiene response status code 403
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

  Scenario Outline: [INTEROP-EST-011] La modifica di un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche all'e-service template
    Then si ottiene response status code 200
    And le modifiche al template sono state applicate correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario Outline: [INTEROP-EST-012] La modifica di un e-service template in stato PUBLISHED o SUSPENDED non può essere fatta attraverso l'uso della api generica
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche all'e-service template

    # note: status code da accertare
    Then si ottiene response status code 403

    Examples:
      | ruolo   | stato |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario: [INTEROP-EST-013] La modifica di un e-service template in stato DRAFT non può essere fatta specificando lo stesso nome
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta di modificare l'e-service template specificando lo stesso nome
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-014] La modifica di un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA2"
    And l'utente tenta delle modifiche all'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-015] La modifica di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta delle modifiche a un e-service template inesistente
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-016] La modifica di una versione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche alla versione dell'e-service template
    Then si ottiene response status code 403
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

  Scenario Outline: [INTEROP-EST-017] La modifica di una versione di un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche alla versione dell'e-service template
    Then si ottiene response status code 200
    And le modifiche alla versione sono state applicate correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

    # TODO il testo "attraverso l'uso della api generica" è stato copiato da uno scenario precedente, assicurarsi che abbia senso anche qui
  Scenario Outline: [INTEROP-EST-018] La modifica di una versione un e-service template in stato PUBLISHED o SUSPENDED non può essere fatta attraverso l'uso della api generica
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta delle modifiche alla versione dell'e-service template

    # note: status code da accertare
    Then si ottiene response status code 403

    Examples:
      | ruolo   | stato |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario: [INTEROP-EST-019] La modifica della versione di un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA2"
    And l'utente tenta delle modifiche alla versione dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-020] La modifica della versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta delle modifiche alla versione di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-021] L'aggiunta di una risk analysis a un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di una risk analysis all'e-service template
    Then si ottiene response status code 403
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

  Scenario Outline: [INTEROP-EST-022] L'aggiunta di una risk analysis a un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di una risk analysis all'e-service template
    Then si ottiene response status code 200
    And l'aggiunta della risk analysis all'e-service è stata effettuata correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario Outline: [INTEROP-EST-023] L'aggiunta di una risk analysis a un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di una risk analysis all'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo   | stato |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario Outline: [INTEROP-EST-024] L'aggiunta di una risk analysis a un e-service template in modalità erogazione non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di una risk analysis all'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario: [INTEROP-EST-025] L'aggiunta di una risk analysis a un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente è un "admin" di "PA2"
    And l'utente tenta l'aggiunta di una risk analysis all'e-service template
    Then si ottiene response status code 403

    #TODO scenario non presente fra i test richiesti, avvisare Stefano Netti
  Scenario: [INTEROP-EST-026] L'aggiunta di una risk analysis a un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta l'aggiunta di una risk analysis a un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-027] L'aggiunta di una risk analysis a un e-service template in stato DRAFT non può essere fatta specificando lo stesso nome di una risk analysis precedentemente creata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente tenta l'aggiunta di una risk analysis all'e-service template specificando lo stesso nome
    Then si ottiene response status code 409

  Scenario Outline: [INTEROP-EST-028] La cancellazione di una risk analysis di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione della risk analysis dell'e-service template
    Then si ottiene response status code 403
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

  Scenario Outline: [INTEROP-EST-029] La cancellazione di una risk analysis di un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione della risk analysis dell'e-service template
    Then si ottiene response status code 200
    And la cancellazione della risk analysis dell'e-service è stata effettuata correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario: [INTEROP-EST-030] La cancellazione di una risk analysis di un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la cancellazione della risk analysis dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-031] La cancellazione di una risk analysis inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta la cancellazione di una risk analysis inesistente nell'e-service template
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-032] La cancellazione di una risk analysis già eliminata non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    And l'utente effettua la cancellazione della risk analysis dell'e-service template con successo
    When l'utente tenta la cancellazione della risk analysis dell'e-service template
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-033] La modifica di una risk analysis di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica della risk analysis dell'e-service template
    Then si ottiene response status code 403
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

  Scenario Outline: [INTEROP-EST-034] La modifica di una risk analysis di un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica della risk analysis dell'e-service template
    Then si ottiene response status code 200

    # TODO step come questo possono essere riformulati in maniera più precisa: "la risk analysis ora corrisponde a quanto specificato nella modifica"
    And la modifica della risk analysis dell'e-service è stata effettuata correttamente

    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario: [INTEROP-EST-035] La modifica di una risk analysis di un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica della risk analysis dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-036] La modifica di una risk analysis inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta la modifica di una risk analysis inesistente nell'e-service template
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-037] La modifica di una risk analysis inserendo il nome di un'altra risk analysis esistente nell'e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente tenta la modifica di una risk analysis inserendo il nome di un'altra risk analysis
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-038] L'aggiunta di un documento/interfaccia a una versione di un e-service template NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template
    Then si ottiene response status code 403
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

  Scenario Outline: [INTEROP-EST-039] L'aggiunta di un documento/interfaccia a una versione di un e-service template in stato DRAFT può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template
    Then si ottiene response status code 200
    And l'aggiunta del documento di tipo <kind> alla versione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   | kind      |
      | admin   | DOCUMENT  |
      | api     | DOCUMENT  |
      | admin   | INTERFACE |
      | api     | INTERFACE |

  Scenario Outline: [INTEROP-EST-040] L'aggiunta di un'interfaccia a una versione di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo   | stato |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario: [INTEROP-EST-041] L'aggiunta di una seconda interfaccia a una versione di un e-service template in stato DRAFT non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    When l'utente tenta l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template
    Then si ottiene response status code 409

  Scenario: [INTEROP-EST-042] L'aggiunta di un documento a una versione di un e-service template in stato DRAFT non può essere fatta specificando lo stesso nome di un documento precedentemente aggiunto
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo DOCUMENT alla versione dell'e-service template con successo

    # ATTENZIONE 04/03/2025: al momento per "nome" si sta intendendo il parametro "prettyName"
    When l'utente tenta l'aggiunta di un documento di tipo DOCUMENT alla versione dell'e-service template specificando lo stesso nome

    Then si ottiene response status code 409

  Scenario Outline: [INTEROP-EST-043] L'aggiunta di un documento/interfaccia a una versione di un e-service template in stato DRAFT non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente è un "admin" di "PA2"
    And l'utente tenta l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

    #TODO scenario non presente fra i test richiesti, avvisare Stefano Netti
  Scenario Outline: [INTEROP-EST-044] L'aggiunta di un documento/interfaccia a una versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta l'aggiunta di un documento di tipo <kind> a un e-service template inesistente
    Then si ottiene response status code 404
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INTEROP-EST-045] L'aggiunta di un documento/interfaccia a una versione inesistente di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta l'aggiunta di un documento di tipo <kind> a una versione inesistente dell'e-service template
    Then si ottiene response status code 404
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INTEROP-EST-046] Il reperimento di un documento/interfaccia di un e-service template NON può essere fatto da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta il reperimento del documento dalla versione dell'e-service template
    Then si ottiene response status code 403
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

  Scenario Outline: [INTEROP-EST-047-DRA] Il reperimento di un documento/interfaccia di un e-service template in stato DRAFT può essere fatto da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta il reperimento del documento dalla versione dell'e-service template
    Then si ottiene response status code 200
    Examples:
      | ruolo   | kind      |
      | admin   | DOCUMENT  |
      | api     | DOCUMENT  |
      | admin   | INTERFACE |
      | api     | INTERFACE |

  Scenario Outline: [INTEROP-EST-047-PUB] Il reperimento di un documento/interfaccia di un e-service template in stato DRAFT può essere fatto da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    And l'utente effettua la pubblicazione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta il reperimento del documento dalla versione dell'e-service template
    Then si ottiene response status code 200
    Examples:
      | ruolo   | kind      |
      | admin   | DOCUMENT  |
      | api     | DOCUMENT  |
      | admin   | INTERFACE |
      | api     | INTERFACE |

  Scenario Outline: [INTEROP-EST-047-SUS] Il reperimento di un documento/interfaccia di un e-service template in stato DRAFT può essere fatto da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    And l'utente effettua la pubblicazione dell'e-service template
    And l'utente effettua la sospensione dell'e-service template
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta il reperimento del documento dalla versione dell'e-service template
    Then si ottiene response status code 200
    Examples:
      | ruolo   | kind      |
      | admin   | DOCUMENT  |
      | api     | DOCUMENT  |
      | admin   | INTERFACE |
      | api     | INTERFACE |

  Scenario Outline: [INTEROP-EST-048] Il reperimento di un documento/interfaccia di un e-service template non può essere fatto da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta il reperimento del documento dalla versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario: [INTEROP-EST-049] Il reperimento di un documento da un e-service template inesistente non può essere fatto
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta il reperimento di un documento da un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-050] Il reperimento di un documento/interfaccia inesistente da un e-service template non può essere fatto
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta il reperimento di un documento inesistente dalla versione dell'e-service template
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-051] La modifica di un documento/interfaccia di un e-service template in qualsiasi stato NON può essere fatta da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica del documento dell'e-service template
    Then si ottiene response status code 403
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

  Scenario Outline: [INTEROP-EST-052] La modifica di un documento/interfaccia di un e-service template in qualsiasi stato può essere fatta da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica del documento dell'e-service template
    Then si ottiene response status code 200
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

  Scenario Outline: [INTEROP-EST-053] La modifica di un documento/interfaccia di un e-service template non può essere fatta da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica del documento dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario: [INTEROP-EST-054] La modifica di un documento/interfaccia da un e-service template inesistente non può essere fatta
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la modifica di un documento da un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-055] La modifica di un documento da una versione inesistente di un e-service template non può essere fatta
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo DOCUMENT alla versione dell'e-service template con successo
    When l'utente tenta la modifica del documento da una versione inesistente dell'e-service template
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-056] La modifica di un documento/interfaccia inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta la modifica di un documento inesistente nell'e-service template
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-057] La modifica di un documento inserendo il nome di un altro documento esistente nell'e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind1> alla versione dell'e-service template con successo
    And l'utente effettua l'aggiunta di un documento di tipo <kind2> alla versione dell'e-service template con successo
    When l'utente tenta la modifica di un documento inserendo il nome di un altro documento
    Then si ottiene response status code 409
    Examples:
      | kind1     | kind2     |
      | DOCUMENT  | DOCUMENT  |
      #| INTERFACE | INTERFACE |  <-- combinazione impossibile, testata in uno scenartio precedente
      | DOCUMENT  | INTERFACE |
      | INTERFACE | DOCUMENT  |

  Scenario Outline: [INTEROP-EST-058] La cancellazione di un documento/interfaccia di un e-service template NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione del documento dell'e-service template
    Then si ottiene response status code 403
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

  Scenario Outline: [INTEROP-EST-059] La cancellazione di un documento/interfaccia di un e-service template può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione del documento dell'e-service template
    Then si ottiene response status code 200
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

  Scenario Outline: [INTEROP-EST-060] La cancellazione di un documento/interfaccia di un e-service template non può essere effettuata da una PA diversa da quella creatrice del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la cancellazione del documento dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INTEROP-EST-061] La cancellazione di un'interfaccia di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    When l'utente tenta la cancellazione del documento dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-062] La cancellazione di un documento/interfaccia inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    When l'utente tenta la cancellazione di un documento inesistente nell'e-service template
    Then si ottiene response status code 404
    Examples:
      | stato     |
      | DRAFT     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-063] La cancellazione di un documento/interfaccia da un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta la cancellazione di un documento da un e-service template inesistente
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-064] La cancellazione di un documento/interfaccia da una versione inesistente di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente tenta la cancellazione del documento da una versione inesistente nell'e-service template
    Then si ottiene response status code 404
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INTEROP-EST-065] La cancellazione di un documento/interfaccia già eliminato non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di <stato>
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    And l'utente effettua la cancellazione del documento dall'e-service template con successo
    When l'utente tenta la cancellazione del documento dell'e-service template
    Then si ottiene response status code 404
    Examples:
      | stato     | kind      |
      | DRAFT     | DOCUMENT  |
      | DRAFT     | INTERFACE |
      | PUBLISHED | DOCUMENT  |
      | SUSPENDED | DOCUMENT  |


  # TODO scenari di pubblicazione template...
  Scenario Outline: [INTEROP-EST-066] La pubblicazione di una versione di un e-service template in stato DRAFT, in modalità erogazione e con annesso un documento di interfaccia NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la pubblicazione della versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         |
      | security      |
      | api,security  |
      | support       |

  Scenario Outline: [INTEROP-EST-067] La pubblicazione di una versione di un e-service template in stato DRAFT, in modalità erogazione e con annesso un documento di interfaccia può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la pubblicazione della versione dell'e-service template
    Then si ottiene response status code 200
    And la pubblicazione della versione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo         |
      | admin         |
      | api           |

  Scenario Outline: [INTEROP-EST-068] La pubblicazione di una versione di un e-service template in stato DRAFT, in modalità ricezione, con annesso un documento di interfaccia e di una risk analysis NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la pubblicazione della versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         |
      | security      |
      | api,security  |
      | support       |

  Scenario Outline: [INTEROP-EST-069] La pubblicazione di una versione di un e-service template in stato DRAFT, in modalità ricezione, con annesso un documento di interfaccia e di una risk analysis può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la pubblicazione della versione dell'e-service template
    Then si ottiene response status code 200
    And la pubblicazione della versione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo         |
      | admin         |
      | api           |

  Scenario: [INTEROP-EST-070] La pubblicazione di una versione di un e-service template in stato DRAFT, in modalità ricezione, con annesso un documento di interfaccia ma SENZA una risk analysis non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    When l'utente tenta la pubblicazione della versione dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-071] La pubblicazione di una versione di un e-service template in stato DRAFT, in modalità erogazione e SENZA un documento di interfaccia non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta la pubblicazione della versione dell'e-service template
    Then si ottiene response status code 403

  Scenario Outline: [INTEROP-EST-072] La pubblicazione di una versione di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la pubblicazione della versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-073] La pubblicazione di una versione di un e-service template non può essere effettuata da un ente diverso dal creatore del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la pubblicazione della versione dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-074] La pubblicazione di una versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la pubblicazione di una versione di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-075] La pubblicazione di una versione inesistente di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    When l'utente tenta la pubblicazione di una versione inesistente di un e-service template
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-076] La pubblicazione di una versione di un e-service template già pubblicata non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente tenta la pubblicazione della versione dell'e-service template
    Then si ottiene response status code 409


  # NOTA: per molti degli scenari di cancellazione di una versione è necessario creare almeno 2 versioni, perché la cancellazione dell'unica versione presente comporta la cancellazione del template stesso

  Scenario Outline: [INTEROP-EST-077] La cancellazione di una versione di un e-service template in stato DRAFT può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione della versione dell'e-service template
    Then si ottiene response status code 200
    And la cancellazione della versione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario Outline: [INTEROP-EST-078] La cancellazione dell'unica versione presente in un e-service template comporta l'eliminazione del template stesso
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione della versione dell'e-service template
    Then si ottiene response status code 200
    And la cancellazione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario Outline: [INTEROP-EST-079] La cancellazione di una versione di un e-service template in stato DRAFT non può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la cancellazione della versione dell'e-service template
    Then si ottiene response status code 403
      # TODO in realtà sarebbe sensata anche la verifica di casi negativi come questo, del tipo: And la cancellazione della versione dell'e-service template non è stata effettuata

    Examples:
      | ruolo   |
      | security      |
      | api,security  |
      | support       |

  Scenario: [INTEROP-EST-080] La cancellazione di una versione di un e-service template in stato DRAFT non può essere effettuata da un ente diverso dal creatore del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la cancellazione della versione dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-081] La cancellazione di una versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la cancellazione di una versione di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-082] La cancellazione di una versione inesistente di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta la cancellazione di una versione inesistente dell'e-service template
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-083] La cancellazione di una versione già cancellata di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    And l'utente effettua la cancellazione della versione dell'e-service template con successo
    When l'utente tenta la cancellazione della versione dell'e-service template già cancellata
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-084] La cancellazione di una versione di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente tenta la cancellazione della versione dell'e-service template
    Then si ottiene response status code 403

  Scenario Outline: [INTEROP-EST-085] La sospensione di una versione di un e-service template in stato PUBLISHED può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la sospensione della versione dell'e-service template
    Then si ottiene response status code 200
    And la sospensione della versione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario Outline: [INTEROP-EST-086] La sospensione di una versione di un e-service template in stato DRAFT o SUSPENDED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la sospensione della versione dell'e-service template
    Then si ottiene response status code <status>
    Examples:
      | stato     | status |
      | DRAFT     | 403    |
      | SUSPENDED | 409    |

  Scenario Outline: [INTEROP-EST-087] La sospensione di una versione di un e-service template in stato PUBLISHED NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la sospensione della versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         |
      | security      |
      | api,security  |
      | support       |

  Scenario: [INTEROP-EST-088] La sospensione di una versione di un e-service template in stato PUBLISHED non può essere effettuata da un ente diverso dal creatore del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la sospensione della versione dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-089] La sospensione di una versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la sospensione della versione di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-090] La sospensione di una versione inesistente nell'e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente tenta la sospensione di una versione inesistente nell'e-service template
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-091] La riattivazione di una versione di un e-service template in stato SUSPENDED può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la riattivazione della versione dell'e-service template
    Then si ottiene response status code 200
    And la riattivazione della versione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario Outline: [INTEROP-EST-092] La riattivazione di una versione di un e-service template in stato DRAFT o PUBLISHED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la riattivazione della versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | DRAFT     |
      | PUBLISHED |

  Scenario Outline: [INTEROP-EST-093] La riattivazione di una versione di un e-service template in stato SUSPENDED NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la riattivazione della versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         |
      | security      |
      | api,security  |
      | support       |

  Scenario: [INTEROP-EST-094] La riattivazione di una versione di un e-service template in stato SUSPENDED non può essere effettuata da un ente diverso dal creatore del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la riattivazione della versione dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-095] La riattivazione di una versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la riattivazione di una versione di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-096] La riattivazione di una versione inesistente nell'e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    When l'utente tenta la riattivazione di una versione inesistente nell'e-service template
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-097] La modifica del nome di un e-service template in stato PUBLISHED o SUSPENDED può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica del nome dell'e-service template
    Then si ottiene response status code 200
    And la modifica del nome dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   | stato     |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario Outline: [INTEROP-EST-098] La modifica del nome di un e-service template in stato PUBLISHED o SUSPENDED NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica del nome dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         | stato     |
      | security      | PUBLISHED |
      | api,security  | PUBLISHED |
      | support       | PUBLISHED |
      | security      | SUSPENDED |
      | api,security  | SUSPENDED |
      | support       | SUSPENDED |

  Scenario: [INTEROP-EST-099] La modifica del nome di un e-service template in stato DRAFT non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta la modifica del nome dell'e-service template
    Then si ottiene response status code 403

  Scenario Outline: [INTEROP-EST-100] La modifica del nome di un e-service template in stato PUBLISHED o SUSPENDED specificando il nome già presente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica del nome dell'e-service template specificando lo stesso nome
    Then si ottiene response status code 409
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-101] La modifica del nome di un e-service template in stato PUBLISHED o SUSPENDED specificando la stringa vuota non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica del nome dell'e-service template specificando la stringa vuota
    Then si ottiene response status code 400
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-102] La modifica del nome di un e-service template in stato PUBLISHED o SUSPENDED specificando NULL non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica del nome dell'e-service template specificando NULL
    Then si ottiene response status code 400
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-103] La modifica del nome di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata da un ente diverso dal creatore del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica del nome dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-104] La modifica del nome di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la modifica del nome di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-105] La modifica della descrizione dello scopo di un e-service template in stato PUBLISHED o SUSPENDED può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica della descrizione dello scopo dell'e-service template
    Then si ottiene response status code 200
    And la modifica della descrizione dello scopo dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   | stato     |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario Outline: [INTEROP-EST-106] La modifica della descrizione dello scopo di un e-service template in stato PUBLISHED o SUSPENDED NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica della descrizione dello scopo dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         | stato     |
      | security      | PUBLISHED |
      | api,security  | PUBLISHED |
      | support       | PUBLISHED |
      | security      | SUSPENDED |
      | api,security  | SUSPENDED |
      | support       | SUSPENDED |

  Scenario: [INTEROP-EST-107] La modifica della descrizione dello scopo di un e-service template in stato DRAFT non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta la modifica della descrizione dello scopo dell'e-service template
    Then si ottiene response status code 403

  Scenario Outline: [INTEROP-EST-108] La modifica della descrizione dello scopo di un e-service template in stato PUBLISHED o SUSPENDED specificando la descrizione già presente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando la stessa descrizione
    Then si ottiene response status code 409
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-109] La modifica della descrizione dello scopo di un e-service template in stato PUBLISHED o SUSPENDED specificando la stringa vuota non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando la stringa vuota
    Then si ottiene response status code 400
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-110] La modifica della descrizione dello scopo di un e-service template in stato PUBLISHED o SUSPENDED specificando NULL non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando NULL
    Then si ottiene response status code 400
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-111] La modifica della descrizione dello scopo di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata da un ente diverso dal creatore del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica della descrizione dello scopo dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-112] La modifica della descrizione dello scopo di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la modifica della descrizione dello scopo di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-113] La modifica della descrizione di un e-service template in stato PUBLISHED o SUSPENDED può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica della descrizione dell'e-service template
    Then si ottiene response status code 200
    And la modifica della descrizione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   | stato     |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario Outline: [INTEROP-EST-114] La modifica della descrizione di un e-service template in stato PUBLISHED o SUSPENDED NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica della descrizione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         | stato     |
      | security      | PUBLISHED |
      | api,security  | PUBLISHED |
      | support       | PUBLISHED |
      | security      | SUSPENDED |
      | api,security  | SUSPENDED |
      | support       | SUSPENDED |

  Scenario: [INTEROP-EST-115] La modifica della descrizione di un e-service template in stato DRAFT non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta la modifica della descrizione dell'e-service template
    Then si ottiene response status code 403

  Scenario Outline: [INTEROP-EST-116] La modifica della descrizione di un e-service template in stato PUBLISHED o SUSPENDED specificando la descrizione già presente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica della descrizione dell'e-service template specificando la stessa descrizione
    Then si ottiene response status code 409
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-117] La modifica della descrizione di un e-service template in stato PUBLISHED o SUSPENDED specificando la stringa vuota non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica della descrizione dell'e-service template specificando la stringa vuota
    Then si ottiene response status code 400
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-118] La modifica della descrizione di un e-service template in stato PUBLISHED o SUSPENDED specificando NULL non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica della descrizione dell'e-service template specificando NULL
    Then si ottiene response status code 400
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-119] La modifica della descrizione di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata da un ente diverso dal creatore del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica della descrizione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-120] La modifica della descrizione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la modifica della descrizione di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-121] La modifica delle quote di una versione di un e-service template in stato PUBLISHED o SUSPENDED può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica delle quote della versione dell'e-service template
    Then si ottiene response status code 200
    And la modifica delle quote della versione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   | stato     |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario Outline: [INTEROP-EST-122] La modifica delle quote di una versione di un e-service template in stato PUBLISHED o SUSPENDED NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica delle quote della versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         | stato     |
      | security      | PUBLISHED |
      | api,security  | PUBLISHED |
      | support       | PUBLISHED |
      | security      | SUSPENDED |
      | api,security  | SUSPENDED |
      | support       | SUSPENDED |

  Scenario: [INTEROP-EST-123] La modifica delle quote di una versione di un e-service template in stato DRAFT non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta la modifica delle quote della versione dell'e-service template
    Then si ottiene response status code 403

  Scenario Outline: [INTEROP-EST-124] La modifica delle quote di una versione di un e-service template in stato PUBLISHED o SUSPENDED specificando un "dailyCallsTotal" inferiore a "dailyCallsPerConsumer"
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica delle quote della versione dell'e-service template specificando un "dailyCallsTotal" inferiore a "dailyCallsPerConsumer"
    Then si ottiene response status code 409
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-125] La modifica delle quote di una versione di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata da un ente diverso dal creatore del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica delle quote della versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-126] La modifica delle quote di una versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la modifica delle quote della versione di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-127] La modifica delle quote di una versione inesistente di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente tenta la modifica delle quote di una versione inesistente dell'e-service template
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-128] La modifica degli attributi di una versione di un e-service template in stato PUBLISHED o SUSPENDED può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>

    # Necessario per l'aggiunta degli attributi in vista della loro modifica
    And l'utente effettua delle modifiche alla versione dell'e-service template con successo

    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica degli attributi della versione dell'e-service template
    Then si ottiene response status code 200
    And la modifica degli attributi della versione dell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   | stato     |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario Outline: [INTEROP-EST-129] La modifica degli attributi di una versione di un e-service template in stato PUBLISHED o SUSPENDED NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    And l'utente effettua delle modifiche alla versione dell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica degli attributi della versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         | stato     |
      | security      | PUBLISHED |
      | api,security  | PUBLISHED |
      | support       | PUBLISHED |
      | security      | SUSPENDED |
      | api,security  | SUSPENDED |
      | support       | SUSPENDED |

  Scenario: [INTEROP-EST-130] La modifica degli attributi di una versione di un e-service template in stato DRAFT non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua delle modifiche alla versione dell'e-service template con successo
    When l'utente tenta la modifica degli attributi della versione dell'e-service template
    Then si ottiene response status code 403

  Scenario Outline: [INTEROP-EST-131] La modifica degli attributi di una versione di un e-service template in stato PUBLISHED o SUSPENDED non può coinvolgere l'aggiunta di nuovi attributi, ma solo la modifica di quelli già presenti
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    And l'utente effettua delle modifiche alla versione dell'e-service template con successo
    When l'utente tenta la modifica degli attributi della versione dell'e-service template aggiungendone di nuovi
    Then si ottiene response status code 409
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-132] La modifica degli attributi di una versione di un e-service template in stato PUBLISHED o SUSPENDED non può essere effettuata da un ente diverso dal creatore del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    And l'utente effettua delle modifiche alla versione dell'e-service template con successo
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica degli attributi della versione dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-133] La modifica degli attributi di una versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la modifica degli attributi della versione di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-134] La modifica degli attributi di una versione inesistente di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua delle modifiche alla versione dell'e-service template con successo
    When l'utente tenta la modifica degli attributi di una versione inesistente dell'e-service template
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-135] La creazione di una nuova versione di un e-service template in stato PUBLISHED o SUSPENDED può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 200
    And la creazione di una ulteriore versione nell'e-service template è stata effettuata correttamente
    Examples:
      | ruolo   | stato     |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |

  Scenario Outline: [INTEROP-EST-136] La creazione di una nuova versione di un e-service template NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         | stato     |
      | security      | PUBLISHED |
      | api,security  | PUBLISHED |
      | support       | PUBLISHED |
      | security      | SUSPENDED |
      | api,security  | SUSPENDED |
      | support       | SUSPENDED |

  Scenario: [INTEROP-EST-137] La creazione di una nuova versione di un e-service template in stato DRAFT non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 403

  Scenario Outline: [INTEROP-EST-138] La creazione di una nuova versione di un e-service template in stato PUBLISHED o SUSPENDED NON può essere effettuata da un ente differente rispetto al creatore dell'e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-139] La creazione di una nuova versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la creazione di una ulteriore versione in un e-service template inesistente
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-140] La visualizzazione del catalogo degli e-service template espone solo quelli in stato PUBLISHED e può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    When l'utente tenta la visualizzazione del catalogo degli e-service template
    Then si ottiene response status code 200
    And il catalogo degli e-service template contiene esattamente 1 elementi tutti in stato PUBLISHED
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario Outline: [INTEROP-EST-141] La visualizzazione del catalogo degli e-service template NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "<ruolo>" di "PA1"
    When l'utente tenta la visualizzazione del catalogo degli e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         |
      | security      |
      | api,security  |
      | support       |

  Scenario: [INTEROP-EST-142] La visualizzazione del catalogo degli e-service template espone solo quelli in stato PUBLISHED indipendentemente dall'ente chiamante
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    When l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente tenta la visualizzazione del catalogo degli e-service template
    Then si ottiene response status code 200
    And il catalogo degli e-service template contiene esattamente 2 elementi tutti in stato PUBLISHED

  Scenario: [INTEROP-EST-143] La visualizzazione del catalogo degli e-service template restituisce risultato vuoto in caso ci siano solo template in stato DRAFT o SUSPENDED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    When l'utente tenta la visualizzazione del catalogo degli e-service template
    Then si ottiene response status code 200
    And il catalogo degli e-service template è vuoto

  Scenario Outline: [INTEROP-EST-144] La visualizzazione dei dettagli un e-service template da parte dell'ente creatore rivela tutte le versioni presenti indipendentemente dallo stato, se l'ente è in veste di ADMIN o API
    Given l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente aggiunge all'e-service template una versione in stato DRAFT
    And l'utente aggiunge all'e-service template una versione in stato SUSPENDED
    When l'utente tenta la visualizzazione dei dettagli dell'e-service template
    Then si ottiene response status code 200
    And i dettagli dell'e-service template contengono esattamente 3 versioni
    Examples:
      | ruolo   |
      | admin   |
      | api     |

  Scenario: [INTEROP-EST-145] La visualizzazione dei dettagli un e-service template da parte di un ente diverso dal creatore rivela le versioni in stato PUBLISHED o SUSPENDED, se l'ente è in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente aggiunge all'e-service template una versione in stato DRAFT
    And l'utente aggiunge all'e-service template una versione in stato SUSPENDED
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la visualizzazione dei dettagli dell'e-service template
    Then si ottiene response status code 200
    And i dettagli dell'e-service template contengono esattamente 2 versioni

  Scenario: [INTEROP-EST-146] La visualizzazione dei dettagli un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la visualizzazione dei dettagli di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-147] La visualizzazione dei dettagli di un e-service template restituisce risultato vuoto in caso ci siano solo versioni in stato DRAFT
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT

    # OPERAZIONE LEGALE? Verificare che possano esserci più versioni in stato DRAFT
    And l'utente aggiunge all'e-service template una versione in stato DRAFT

    When l'utente tenta la visualizzazione dei dettagli dell'e-service template
    Then si ottiene response status code 200
    And i dettagli dell'e-service template contengono esattamente 0 versioni

  Scenario Outline: [INTEROP-EST-148] La visualizzazione dei dettagli della versione di un e-service template da parte dell'ente creatore può essere effettuata quale che sia lo stato della versione in questione, se l'ente è in veste di ADMIN o API
    Given l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la visualizzazione dei dettagli della versione dell'e-service template
    Then si ottiene response status code 200
    And i dettagli della versione dell'e-service template sono coerenti con quelli inseriti
    Examples:
      | ruolo   | stato     |
      | admin   | PUBLISHED |
      | api     | PUBLISHED |
      | admin   | SUSPENDED |
      | api     | SUSPENDED |
      | admin   | DRAFT     |
      | api     | DRAFT     |

  Scenario Outline: [INTEROP-EST-149] La visualizzazione dei dettagli della versione di un e-service template da parte di un ente diverso dal creatore può essere effettuata solo se lo stato della versione è PUBLISHED o SUSPENDED, se l'ente è in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la visualizzazione dei dettagli della versione dell'e-service template
    Then si ottiene response status code 200
    And i dettagli della versione dell'e-service template sono coerenti con quelli inseriti
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-150] La visualizzazione dei dettagli della versione di un e-service template inesistente non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la visualizzazione dei dettagli di una versione di un e-service template inesistente
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-151] La visualizzazione dei dettagli della versione di un e-service template da parte di un ente diverso dal creatore NON può essere effettuata se lo stato della versione è DRAFT
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la visualizzazione dei dettagli della versione dell'e-service template
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-152] La visualizzazione dell'elenco producers degli e-service templates da parte dell'ente creatore può essere effettuata per ogni stato dei template, se l'ente è in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la visualizzazione dell'elenco producers degli e-service templates
    Then si ottiene response status code 200
    And l'elenco producers degli e-service templates contiene esattamente 3 elementi
    Examples:
      | ruolo |
      | admin |
      | api   |

  Scenario Outline: [INTEROP-EST-153] La visualizzazione dell'elenco dei creatori di e-service templates attivi può essere effettuata se l'ente è in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la visualizzazione dell'elenco dei creatori di e-service templates attivi
    Then si ottiene response status code 200
    And l'unico ente presente nell'elenco dei creatori di e-service templates attivi è "PA1"
    Examples:
      | ruolo |
      | admin |
      | api   |

    # TODO L'api potrebbe semplicemente restituire risultato positivo ma vuoto anziché un errore, verificare
  Scenario: [INTEROP-EST-154] La visualizzazione dell'elenco dei creatori di e-service templates attivi non può essere effettuata se non ci sono templates attivi
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    When l'utente tenta la visualizzazione dell'elenco dei creatori di e-service templates attivi
    Then si ottiene response status code 404

  Scenario Outline: [INTEROP-EST-155] La creazione di un nuovo e-service a partire da un template attivo può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie
    Then si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    Examples:
      | ruolo |
      | admin |
      | api   |

  Scenario Outline: [INTEROP-EST-156] La creazione di un nuovo e-service a partire da un template attivo NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie
    Then si ottiene response status code 403
    Examples:
      | ruolo |
      | security      |
      | api,security  |
      | support       |

  Scenario Outline: [INTEROP-EST-157] La creazione di un nuovo e-service completamente specificato a partire da un template attivo può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la creazione di un nuovo e-service a partire dal template indicando tutte le specifiche
    Then si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT
    Examples:
      | ruolo |
      | admin |
      | api   |

  Scenario Outline: [INTEROP-EST-158] La creazione di un nuovo e-service a partire da un template in stato DRAFT o SUSPENDED non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie
    Then si ottiene response status code 400
    Examples:
      | stato     |
      | DRAFT     |
      | PUBLISHED |

  Scenario: [INTEROP-EST-159] La creazione di un nuovo e-service NON può essere effettuata a partire da un template inesistente
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente tenta la creazione di un nuovo e-service indicando un template inesistente
    Then si ottiene response status code 404

  # NOTA: un e-service creato a partire da un template è anche detto "istanza" del template
  Scenario Outline: [INTEROP-EST-160] L'aggiornamento di un'istanza di un template all'ultima versione dell'e-service template può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "<ruolo>" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    When l'utente tenta l'aggiornamento dell'istanza dell'e-service template all'ultima versione
    Then si ottiene response status code 200
    And il nuovo e-service riferito all'ultima versione dell'e-service template è stato creato correttamente
    Examples:
      | ruolo |
      | admin |
      | api   |

  Scenario Outline: [INTEROP-EST-161] L'aggiornamento di un'istanza di un template all'ultima versione dell'e-service template NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta l'aggiornamento dell'istanza dell'e-service template all'ultima versione
    Then si ottiene response status code 403
    Examples:
      | ruolo         |
      | security      |
      | api,security  |
      | support       |

  Scenario: [INTEROP-EST-162] L'aggiornamento di un'istanza inesistente di un template all'ultima versione dell'e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta l'aggiornamento di un'istanza inesistente dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-163] L'aggiornamento di un'istanza di un template all'ultima versione dell'e-service template non può essere effettuata se l'istanza fa già riferimento all'ultima versione del template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente tenta l'aggiornamento dell'istanza dell'e-service template all'ultima versione
    Then si ottiene response status code 409

  Scenario: [INTEROP-EST-164] L'aggiornamento di un'istanza di un template all'ultima versione dell'e-service template non può essere effettuata non indicando l'identificativo dell'e-service
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta l'aggiornamento di un'istanza dell'e-service template specificando un identificativo vuoto
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-165] La creazione di un e-service template indicando una specifica vuota dello stesso non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la creazione di un e-service template indicando una specifica vuota
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-166] La modifica di un e-service template indicando una specifica vuota dello stesso non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta di modificare l'e-service template indicando una specifica vuota
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-167] La modifica di una versione di un e-service template indicando una specifica vuota della stessa non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta di modificare la versione dell'e-service template indicando una specifica vuota
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-168] La creazione di una risk analysis da associare a un e-service template indicando una specifica vuota della stessa non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta la creazione di una risk analysis indicando una specifica vuota
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-169] La cancellazione di una risk analysis associata a un e-service template indicando un identificativo vuoto della stessa non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente tenta la cancellazione della risk analysis indicando un identificativo vuoto
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-170] La modifica di una risk analysis associata a un e-service template indicando una specifica vuota della stessa non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    And l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo
    When l'utente tenta la modifica della risk analysis dell'e-service template indicando una specifica vuota
    Then si ottiene response status code 400

  Scenario Outline: [INTEROP-EST-171] La cancellazione di un documento/interfaccia di un e-service template specificando un identificativo vuoto dello stesso non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente tenta la cancellazione del documento dell'e-service template indicando un identificato vuoto
    Then si ottiene response status code 400
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INTEROP-EST-172] L'aggiunta di un documento/interfaccia a una versione di un e-service template specificando un contenuto vuoto dello stesso non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente tenta l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template specificando un contenuto vuoto
    Then si ottiene response status code 400
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INTEROP-EST-173] Il reperimento di un documento/interfaccia di un e-service template indicando un identificativo vuoto non può essere effettuato
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente tenta il reperimento del documento dalla versione dell'e-service template indicando un identificativo vuoto
    Then si ottiene response status code 400
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario Outline: [INTEROP-EST-174] La modifica di un documento/interfaccia di un e-service template indicando una specifica vuota non può essere effettuato
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua l'aggiunta di un documento di tipo <kind> alla versione dell'e-service template con successo
    When l'utente tenta la modifica del documento dell'e-service template indicando una specifica vuota
    Then si ottiene response status code 400
    Examples:
      | kind      |
      | DOCUMENT  |
      | INTERFACE |

  Scenario: [INTEROP-EST-175] La pubblicazione di una versione di un e-service template indicando un identificativo vuoto della stessa non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta la pubblicazione di una versione di un e-service template indicando un identificativo vuoto
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-176] La cancellazione di una versione di un e-service template indicando un identificativo vuoto della stessa non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    When l'utente tenta la cancellazione della versione dell'e-service template indicando un identificativo vuoto
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-177] La sospensione di una versione di un e-service template indicando un identificativo vuoto della stessa non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente tenta la sospensione della versione dell'e-service template indicando un identificativo vuoto
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-178] La riattivazione di una versione di un e-service template indicando un identificativo vuoto della stessa non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di SUSPENDED
    When l'utente tenta la riattivazione della versione dell'e-service template indicando un identificativo vuoto
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-179] La modifica di un e-service template non può essere fatta specificando un nome vuoto
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta di modificare l'e-service template specificando un nome vuoto
    Then si ottiene response status code 400

  Scenario Outline: [INTEROP-EST-180] La modifica delle quote di una versione di un e-service template indicando una specifica vuota non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente tenta la modifica delle quote della versione dell'e-service template indicando una specifica vuota
    Then si ottiene response status code 400
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario Outline: [INTEROP-EST-181] La modifica degli attributi di una versione di un e-service template indicando una specifica vuota non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>

    # Necessario per l'aggiunta degli attributi in vista della loro modifica
    # TODO uno step linguisticamente più chiaro sarebbe preferibile
    And l'utente effettua delle modifiche alla versione dell'e-service template con successo

    When l'utente tenta la modifica degli attributi della versione dell'e-service template indicando una specifica vuota
    Then si ottiene response status code 400
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-182] La visualizzazione dei dettagli un e-service template indicando un identificativo vuoto non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta la visualizzazione dei dettagli dell'e-service template indicando un identificativo vuoto
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-183] La visualizzazione dei dettagli della versione di un e-service template indicando un identificativo vuoto non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente tenta la visualizzazione dei dettagli della versione dell'e-service template indicando un identificativo vuoto
    Then si ottiene response status code 400

  Scenario Outline: [INTEROP-EST-184] La visualizzazione dell'elenco di tutte le istanze di un e-service template attivo può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente effettua la creazione di un nuovo e-service in stato SUSPENDED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la visualizzazione dell'elenco di tutte le istanze dell'e-service template
    Then si ottiene response status code 200
    And sono state visualizzate 1 istanza in stato DRAFT, 1 in stato PUBLISHED e 1 in stato SUSPENDED
    Examples:
      | ruolo |
      | admin |
      | api   |

  Scenario Outline: [INTEROP-EST-185] La visualizzazione dell'elenco di tutte le istanze di un e-service template attivo NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la visualizzazione dell'elenco di tutte le istanze dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo         |
      | security      |
      | api,security  |
      | support       |

    # Lo scenario 208 è stato saltato vista l'impossibilità di poter creare un UUID vuoto lato Java.
    # Altri precedenti test simili sono stati implementati passando un UUID null, ma si è concordato
    # che il test risultante - producendo una chiamata HTTP che viene bloccata già dal client OpenApi
    # generato - non fornisce alcun valore aggiunto.

  Scenario: [INTEROP-EST-186] La visualizzazione dell'elenco di tutte le istanze di un e-service template attivo NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la visualizzazione dell'elenco di tutte le istanze di un e-service template inesistente
    Then si ottiene response status code 403

  Scenario Outline: [INTEROP-EST-187] La modifica dei campi di un'istanza in stato DRAFT di un e-service template può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica dei campi dell'istanza dell'e-service template
    Then si ottiene response status code 200
    And i campi dell'istanza dell'e-service template sono stati modificati correttamente
    Examples:
      | ruolo |
      | admin |
      | api   |

  Scenario Outline: [INTEROP-EST-188] La modifica dei campi di un'istanza di un e-service template NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica dei campi dell'istanza dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo |
      | security      |
      | api,security  |
      | support       |

  Scenario Outline: [INTEROP-EST-189] La modifica dei campi di un'istanza in stato PUBLISHED o SUSPENDED di un e-service template NON può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <stato> a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente tenta la modifica dei campi dell'istanza dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-190] La modifica dei campi di un'istanza di un e-service template avente una versione in stato DRAFT e una in stato PUBLISHED NON può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente effettua l'aggiunta di una versione in stato DRAFT all'e-service con successo
    When l'utente tenta la modifica dei campi dell'istanza dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-191] La modifica dei campi di un'istanza inesistente di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la modifica dei campi di un'istanza inesistente dell'e-service template
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-192] La modifica dei campi di un'istanza di un e-service template indicando una specifica vuota non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente tenta la modifica dei campi dell'istanza dell'e-service template indicando una specifica vuota
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-193] La modifica dei campi di un'istanza in stato DRAFT di un e-service template NON può essere effettuata da un ente diverso dal creatore dell'istanza
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica dei campi dell'istanza dell'e-service template
    Then si ottiene response status code 403

  Scenario Outline: [INTEROP-EST-194] La modifica del descriptor di un'istanza in stato DRAFT di un e-service template può essere effettuata da un ente in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica del descriptor dell'istanza dell'e-service template
    Then si ottiene response status code 200
    And il descriptor dell'istanza dell'e-service template è stato modificato correttamente
    Examples:
      | ruolo |
      | admin |
      | api   |

  Scenario Outline: [INTEROP-EST-195] La modifica del descriptor di un'istanza di un e-service template NON può essere effettuata da un ente NON in veste di ADMIN o API
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica del descriptor dell'istanza dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | ruolo |
      | security      |
      | api,security  |
      | support       |

  Scenario Outline: [INTEROP-EST-196] La modifica del descriptor di un'istanza in stato PUBLISHED o SUSPENDED di un e-service template NON può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <stato> a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente tenta la modifica del descriptor dell'istanza dell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  Scenario: [INTEROP-EST-197] La modifica del descriptor di un'istanza di un e-service template avente una versione in stato DRAFT e una in stato PUBLISHED NON può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente effettua l'aggiunta di una versione in stato DRAFT all'e-service con successo
    When l'utente tenta la modifica del descriptor dell'istanza dell'e-service template
    Then si ottiene response status code 403

  Scenario: [INTEROP-EST-198] La modifica di un descriptor inesistente di un e-service template non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente tenta la modifica di un descriptor inesistente dell'istanza dell'e-service template
    Then si ottiene response status code 404

  Scenario: [INTEROP-EST-199] La modifica del descriptor di un'istanza di un e-service template indicando una specifica vuota non può essere effettuata
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente tenta la modifica del descriptor dell'istanza dell'e-service template indicando una specifica vuota
    Then si ottiene response status code 400

  Scenario: [INTEROP-EST-200] La modifica del descriptor di un'istanza in stato DRAFT di un e-service template NON può essere effettuata da un ente diverso dal creatore dell'istanza
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT a partire dal template con successo indicando solo le specifiche strettamente necessarie
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica del descriptor dell'istanza dell'e-service template
    Then si ottiene response status code 403



  #TODO la maggior parte dei test sono fatti su template in mod. EROGAZIONE. Valutare che non sia il caso di testare per entrambe le modalità.

    #TODO smistare gli scenari in file .feature più piccoli e i relativi step in classi più piccole. Possibile divisione:
      # test che riguardano il template
      # test che riguardano la versione
      # test che riguardano la risk analysis
      # test che riguardano i documenti (nota: test di caricamento e lettura sono inter-dipendenti, la creazione non può essere verificata senza la lettura, e viceversa)
      # ...

    #TODO associare un tag per ogni risorsa testata: template, version, riskAnalysis, document...