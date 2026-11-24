@certifiedDiscreteAttributeFlagOn
@certifiedDiscreteAttributeTenantUpdateCertifiedTest
Feature: Aggiornamento del valore discreto di un attributo certificato discreto di un tenant.

  Scenario: [TENANT_UPDATE_CERTIFIED_DISCRETE_ATTRIBUTE_1] L'aggiornamento di un attributo certificato discreto associato ad un tenant va a buon fine.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    When l'utente tenta la modifica dell'attributo certificato discreto precedentemente creato con un valore discreto di 150 a "PA1"
    Then si ottiene lo status code 204
    And l'attributo certificato discreto è assegnato a "PA1" e ha il valore discreto di 150

  Scenario: [TENANT_UPDATE_CERTIFIED_DISCRETE_ATTRIBUTE_1b] L'aggiornamento di un attributo certificato discreto associato ad un tenant non va a buon fine se l'ID utilizzato per l'attributo non è valido.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    When l'utente tenta di modificare l'attributo certificato discreto di "PA1" utilizzando un ID inesistente per l'attributo
    Then si ottiene lo status code 404

  Scenario: [TENANT_UPDATE_CERTIFIED_DISCRETE_ATTRIBUTE_1c] L'aggiornamento di un attributo certificato discreto associato ad un tenant non va a buon fine se l'ID utilizzato per il tenant non è valido.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    When l'utente tenta di modificare l'attributo certificato discreto di "PA1" utilizzando un ID inesistente per il tenant
    Then si ottiene lo status code 404

  Scenario: [TENANT_UPDATE_CERTIFIED_DISCRETE_ATTRIBUTE_1d] L'aggiornamento di un attributo certificato discreto associato ad un tenant non va a buon fine se l'ID utilizzato per l'attributo è quello di un'altra categoria di attributi.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    And GSP ha già creato 1 attributo CERTIFIED
    When l'utente tenta di modificare l'attributo certificato discreto di "PA1" utilizzando l'ID dell'attributo certificato creato
    Then si ottiene lo status code 404

  Scenario Outline: [TENANT_UPDATE_CERTIFIED_DISCRETE_ATTRIBUTE_1e] L'aggiornamento di un attributo certificato discreto associato ad un tenant non va a buon fine se il valore discreto non è ammesso.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    When l'utente tenta la modifica dell'attributo certificato discreto precedentemente creato con un valore discreto di <discreteValue> a "PA1"
    Then si ottiene lo status code <expectedResult>

    Examples:
      | discreteValue | expectedResult |
      | 1             | 204            |
      | 1000000000    | 204            |
      | 0             | 400            |
      | 1000000001    | 400            |

  Scenario: [TENANT_UPDATE_CERTIFIED_DISCRETE_ATTRIBUTE_2a] L'aggiornamento di un attributo certificato discreto assegnato ad un tenant non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    When viene impostato per l'utente un token non valido
    And l'utente tenta la modifica dell'attributo certificato discreto precedentemente creato con un valore discreto di 200 a "PA1"
    Then si ottiene lo status code 401

  Scenario Outline: [TENANT_UPDATE_CERTIFIED_DISCRETE_ATTRIBUTE_2b] L'aggiornamento di un attributo certificato discreto assegnato ad un tenant non va a buon fine se l'utente non è autorizzato.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA3" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    When l'utente è un "<ruolo>" di "<ente>"
    And l'utente tenta la modifica dell'attributo certificato discreto precedentemente creato con un valore discreto di 200 a "PA3"
    Then si ottiene lo status code 403

    Examples:
      | ente    | ruolo        |
      # Ente certificatore emittente
      | GSP     | api          |
      | GSP     | security     |
      | GSP     | api,security |
      | GSP     | support      |
      # Altro ente certificatore
      | PA1     | admin        |
      | PA1     | security     |
      | PA1     | api,security |
      | PA1     | support      |
      # Ente non certificatore
      | Privato | api          |
      | Privato | security     |
      | Privato | api,security |
      | Privato | support      |
      | Privato | admin        |

  Scenario: [TENANT_UPDATE_CERTIFIED_DISCRETE_ATTRIBUTE_3] L'aggiornamento di un attributo certificato discreto ad un tenant non va a buon fine se l'attributo è stato revocato.
    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    And l'utente revoca a "PA1" l'attributo certificato discreto precedentemente creato e assegnato
    And si ottiene lo status code 200
    When l'utente tenta la modifica dell'attributo certificato discreto precedentemente creato con un valore discreto di 200 a "PA1"
    Then si ottiene lo status code 409
