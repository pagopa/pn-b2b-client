@e-service-template
Feature: Test API of e-service template
  #Background:
  #  Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe in fruizione
  #  And l'ente "PA1" rimuove la disponibilità a ricevere deleghe in fruizione
  #  And l'ente "GSP2" rimuove la disponibilità a ricevere deleghe in fruizione

  Scenario Outline: [INCARICATO-EST-001] La creazione di un e-service template NON può essere fatta da parte un ente NON erogatore di e-service
    Given l'utente è un "<ruolo>" di "PA1"
    When l'utente effettua la creazione di un e-service template
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        403 |
      | api         |        403 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

  Scenario Outline: [INCARICATO-EST-002] La creazione di un e-service template può essere fatta da parte un ente erogatore di e-service in veste di ADMIN o API
    Given l'utente è un "<ruolo>" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    When l'utente effettua la creazione di un e-service template
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo       | statusCode |
      | admin       |        200 |
      | api         |        200 |
      | security    |        403 |
      | api,security|        403 |
      | support     |        403 |

    #TODO: test in cui vengono specificati tutti i campi nella api di creazione
    #TODO: test dove vengono specificati meno dei campi obbligatori nella api di creazione

    #TODO test in cui vengono specificati tutti i campi nella api di creazione, oggetto Versione
    #TODO: test dove vengono specificati meno dei campi obbligatori nella api di creazione, oggetto Versione