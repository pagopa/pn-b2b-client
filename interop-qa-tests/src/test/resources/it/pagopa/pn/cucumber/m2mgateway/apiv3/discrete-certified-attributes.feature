Feature: Gestione degli attributi certificati discreti attraverso APIs M2M V3

  Scenario Outline: [M2M_CERTIFIED_DISCRETE_ATTRIBUTES_1] La creazione di un attributo certificato discreto è consentita
  alle utenze admin degli enti certificatori.

    Given l'utente è un "admin" di "<ente>" con ruolo M2M <ruolo-m2m>
    And viene effettuata la creazione dell'attributo certificato discreto
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code <risultato>

    Examples:
      | ente    | ruolo-m2m | risultato |
      | PA1     | m2m-admin | 201       |
      | PA1     | m2m       | 403       |
      | Privato | m2m-admin | 403       |
      | Privato | m2m       | 403       |
