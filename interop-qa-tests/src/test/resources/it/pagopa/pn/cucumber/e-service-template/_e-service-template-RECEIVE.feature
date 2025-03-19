# 19/03/2025
  # Contesto:
  # Parte degli scenari di test previsti per la feature e-service template prevedeva la
  # manipolazione di template per e-service in modalità RECEIVE, con tutte le funzionalità
  # annesse e connesse (per esempio la creazione, la modifica e la rimozione di una
  # risk analysis associata al template, presente appunto solo per template di e-service
  # in modalità RECEIVE).

  # Questione:
  # La manipolazione di template per e-service in modalità RECEIVE è slittata fino a data
  # da destinarsi, dunque tutte le funzionalità annesse e connesse non fanno più parte
  # degli scenari di test. Si raccolgono qui quindi tutti gli scenari che erano stati
  # implementati prima della rimozione degli e-service template in modalità RECEIVE.
  # Si collocano qui, nella loro forma originale, pure gli scenari che coinvolgono ANCHE
  # e-service template in modalità RECEIVE e che sono stati ri-parametrizzati per
  # prevedere solo e-service template in modalità DELIVER.

  # NOTA: lo slittamento della manipolazione di template per e-service in modalità RECEIVE
  # è avvenuto prima che si potessero testare quasi tutti gli scenari qui presenti, e dunque
  # non è garantito il funzionamento di nessuno di essi.


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
    Then si ottiene response status code 409
    Examples:
      | modo         |
      | erogazione   |
      | ricezione    |

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
    # x | api     |

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
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
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