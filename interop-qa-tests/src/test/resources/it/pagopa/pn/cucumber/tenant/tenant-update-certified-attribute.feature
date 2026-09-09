Feature: Aggiornamento del valore discreto di un attributo certificato discreto di un tenant.

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario Outline: [TENANT_UPDATE_CERTIFIED_DISCRETE_ATTRIBUTE_1] Aggiornamento di un attributo certificato discreto ad un tenant.
  Verifica l'esito dell'aggiornamento di un attributo certificato discreto a un tenant da parte dell'admin di un ente certificatore.

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

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [TENANT_UPDATE_CERTIFIED_DISCRETE_ATTRIBUTE_2] L'aggiornamento di un attributo certificato discreto ad un tenant non va a buon fine se l'attrbuto è stato revocato.
  Verifica l'esito dell'aggiornamento di un attributo certificato discreto a un tenant da parte dell'admin di un ente certificatore.

    Given l'utente è un "admin" di "GSP"
    And GSP ha già creato 1 attributo CERTIFIED_DISCRETE
    And l'utente assegna a "PA1" l'attributo certificato discreto precedentemente creato con un valore discreto di 100
    And l'utente revoca a "PA1" l'attributo certificato discreto precedentemente creato e assegnato
    And si ottiene lo status code 200
    When l'utente tenta la modifica dell'attributo certificato discreto precedentemente creato con un valore discreto di 200 a "PA1"
    Then si ottiene lo status code 409
