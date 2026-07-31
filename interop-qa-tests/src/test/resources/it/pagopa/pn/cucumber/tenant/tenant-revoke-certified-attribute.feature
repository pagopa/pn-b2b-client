@tenant
Feature: Revoca di un attributo certificato posseduto da uno specifico aderente
  Tutti gli utenti autorizzati degli enti certificatori possono revocare uno degli attributi certificati che hanno assegnato precedentemente

  @nrt-minimal @tenant_revoke_certified_attribute1 @no-parallel @certifiedAttribute
  Scenario Outline: [TENANT_REVOKE_CERTIFIED_ATTRIBUTE_01] Per un attributo certificato precedentemente creato da un aderente, il quale ha la qualifica di ente certificatore (certifier), che lo assegna ad un altro ente, alla richiesta di revoca da parte di un utente con sufficienti permessi (admin), va a buon fine, altrimenti ottiene un errore
    Given l'utente è un "<ruolo>" di "PA2"
    Given "PA2" ha creato un attributo certificato e lo ha assegnato a "PA1"
    When l'utente revoca l'attributo precedentemente creato e assegnato
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo        | statusCode |
#      | admin        |        204 |
#      | api          |        403 |
#      | security     |        403 |
#      | support      |        403 |
      | api,security | 403        |

    @nuovi-operatori-update
    Examples:
      | ruolo    | statusCode |
      | reviewer | 403        |
      | viewer   | 403        |

  @nrt-minimal
  @tenant_revoke_certified_attribute2 @certifiedAttribute
  Scenario: [TENANT_REVOKE_CERTIFIED_ATTRIBUTE_02] Per un attributo certificato precedentemente creato da un primo aderente, il quale ha la qualifica di ente certificatore (certifier), che lo assegna ad un secondo ente, alla richiesta di revoca da parte di un utente con sufficienti permessi (admin) appartenente ad un terzo ente certificatore, ottiene un errore
    Given l'utente è un "admin" di "GSP2"
    Given "PA2" ha creato un attributo certificato e lo ha assegnato a "GSP"
    When l'utente revoca l'attributo precedentemente creato e assegnato
    Then si ottiene status code 403

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario Outline: [TENANT_REVOKE_CERTIFIED_DISCRETE_ATTRIBUTE_1] Verifica che solo l'admin di un ente certificatore possa
  revocare un attributo certificato discreto.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    When l'utente è un "<ruolo>" di "<ente>"
    And l'utente revoca a "PA1" l'attributo certificato discreto precedentemente creato e assegnato
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo        | ente    | statusCode |
      | admin        | GSP     | 200        |
      | api          | GSP     | 403        |
      | security     | GSP     | 403        |
      | support      | GSP     | 403        |
      | api,security | GSP     | 403        |
      | reviewer     | GSP     | 403        |
      | viewer       | GSP     | 403        |
      | admin        | Privato | 403        |

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [TENANT_REVOKE_CERTIFIED_DISCRETE_ATTRIBUTE_2] Verifica che il tentativo di revocare un attributo certificato discreto
  mai assegnato al tenant restituisca un errore di risorsa non trovata.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And si ottiene status code 200
    When l'utente revoca a "PA1" l'attributo certificato discreto precedentemente creato ma non associato
    Then si ottiene status code 404

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [TENANT_REVOKE_CERTIFIED_DISCRETE_ATTRIBUTE_3] Verifica che la richiesta di revoca di un attributo certificato
  discreto fallisca se rivolta a un ente inesistente
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    When l'utente revoca l'attributo certificato discreto precedentemente creato ad un ente non esistente
    Then si ottiene lo status code 404

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [TENANT_REVOKE_CERTIFIED_DISCRETE_ATTRIBUTE_4] Verifica che un secondo tentativo di revoca dello stesso attributo
  certificato discreto a un tenant fallisca.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    And l'utente revoca a "PA1" l'attributo certificato discreto precedentemente creato e assegnato
    And si ottiene lo status code 200
    When l'utente revoca a "PA1" l'attributo certificato discreto precedentemente creato e assegnato
    Then si ottiene lo status code 409
