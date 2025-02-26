@e-service-template
Feature: Test API of e-service template
  #Background:
  #  Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe in fruizione
  #  And l'ente "PA1" rimuove la disponibilità a ricevere deleghe in fruizione
  #  And l'ente "GSP2" rimuove la disponibilità a ricevere deleghe in fruizione

  Scenario Outline: [INCARICATO-EST-001] La creazione di un e-service template NON può essere fatta da parte un ente NON erogatore di e-service
    Given l'utente è un "admin" di "PA1"
    When l'utente effettua la creazione di un e-service template in modalità <modalità>
    Then si ottiene status code 403
    Examples:
      | modalità     |
      | erogazione   |
      | ricezione    |

  Scenario Outline: [INCARICATO-EST-002] La creazione di un e-service template può essere fatta da parte un ente erogatore di e-service in veste di ADMIN o API
    Given l'utente è un "<ruolo>" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    When l'utente effettua la creazione di un e-service template in modalità <modalità>
    Then si ottiene status code <statusCode>

    # TODO
    And l'e-service template è in stato di DRAFT

    Examples:
      | ruolo       | statusCode | modalità     |
      | admin       |        200 | erogazione   |
      | api         |        200 | erogazione   |
      | security    |        403 | erogazione   |
      | api,security|        403 | erogazione   |
      | support     |        403 | erogazione   |
      | admin       |        200 | ricezione    |
      | api         |        200 | ricezione    |
      | security    |        403 | ricezione    |
      | api,security|        403 | ricezione    |
      | support     |        403 | ricezione    |

    #TODO: test in cui vengono specificati tutti i campi nella api di creazione
    #TODO: test dove vengono specificati meno dei campi obbligatori nella api di creazione

    #TODO test in cui vengono specificati tutti i campi nella api di creazione, oggetto Versione
    #TODO: test dove vengono specificati meno dei campi obbligatori nella api di creazione, oggetto Versione