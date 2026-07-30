@tenant
Feature: Assegnazione di un attributo certificato ad un aderente
  Tutti gli utenti autorizzati degli enti certificatori possono assegnare un attributo certificato

  @nrt-minimal
  @tenant_assign_certified_attribute1
  Scenario Outline: [TENANT_ASSIGN_CERTIFIED_ATTRIBUTE_1] Per un attributo certificato precedentemente creato da un aderente, il quale ha la qualifica di ente certificatore (certifier), alla richiesta di assegnazione dell’attributo ad un altro ente da parte di un utente con sufficienti permessi (admin), va a buon fine
    Given l'utente è un "<ruolo>" di "PA2"
    Given PA2 ha già creato 1 attributo CERTIFIED
    When l'utente assegna a "PA1" l'attributo certificato precedentemente creato
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo        | statusCode |
      | admin        | 204        |
      | api          | 403        |
      | security     | 403        |
      | support      | 403        |
      | api,security | 403        |

    @nuovi-operatori-update
    Examples:
      | ruolo    | statusCode |
      | reviewer | 403        |
      | viewer   | 403        |

  @nrt-minimal
  @tenant_assign_certified_attribute2 @wait_for_fix @PIN-5037
  Scenario: [TENANT_ASSIGN_CERTIFIED_ATTRIBUTE_2] Per un attributo certificato precedentemente creato da un aderente, il quale ha la qualifica di ente certificatore (certifier), alla richiesta di assegnazione dell’attributo all’ente stesso da parte di un utente con sufficienti permessi (admin), ottiene un errore.
    Given l'utente è un "admin" di "PA2"
    Given PA2 ha già creato 1 attributo CERTIFIED
    When l'utente assegna a "PA2" l'attributo certificato precedentemente creato
    Then si ottiene status code 204

  @nrt-minimal
  @tenant_assign_certified_attribute3
  Scenario: [TENANT_ASSIGN_CERTIFIED_ATTRIBUTE_3] Per un attributo certificato precedentemente creato da un primo aderente, alla richiesta di assegnazione dell’attributo ad un secondo ente da parte di un utente con sufficienti permessi (admin), il quale admin appartiene ad un terzo aderente, il quale ha la qualifica di ente certificatore (certifier), ottiene un errore.
    Given l'utente è un "admin" di "GSP2"
    Given PA2 ha già creato 1 attributo CERTIFIED
    When l'utente assegna a "GSP" l'attributo certificato precedentemente creato
    Then si ottiene status code 403

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario Outline: [TENANT_ASSIGN_CERTIFIED_DISCRETE_ATTRIBUTE_1] Assegnazione attributo certificato discreto a un tenant.
  Verifica l'esito dell'assegnazione di un attributo certificato discreto a un tenant da parte dell'admin di un ente certificatore,
  al variare del valore discreto inserito.

    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    When l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di <discreteValue>
    Then si ottiene lo status code <expectedResult>

    Examples:
      | discreteValue | expectedResult |
      | 1             | 200            |
      | 1000000000    | 200            |
      | 0             | 400            |
      | 1000000001    | 400            |

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario Outline: [TENANT_ASSIGN_CERTIFIED_DISCRETE_ATTRIBUTE_2] L'attributo certificato discreto può essere assegnato
  soltanto da un admin di un ente certifcatore.

    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    When l'utente è un "<ruolo>" di "<ente>"
    When l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    Then si ottiene lo status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo | risultato |
      | GSP  | admin | 200       |

    @sad-path
    Examples:
      | ente    | ruolo        | risultato |
      | GSP     | api          | 403       |
      | GSP     | security     | 403       |
      | GSP     | api,security | 403       |
      | GSP     | reviewer     | 403       |
      | GSP     | viewer       | 403       |
      | Privato | admin        | 403       |

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [TENANT_ASSIGN_CERTIFIED_DISCRETE_ATTRIBUTE_3] La riassegnazione del medesimo attributo certificato discreto
  ad un ente non va a buon fine.

    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    And si ottiene lo status code 200
    When l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 200
    Then si ottiene lo status code 409
