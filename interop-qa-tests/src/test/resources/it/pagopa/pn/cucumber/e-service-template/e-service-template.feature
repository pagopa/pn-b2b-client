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


    #TODO: test in cui vengono specificati tutti i campi nella api di creazione
    #TODO: test dove vengono specificati meno dei campi obbligatori nella api di creazione

    #TODO test in cui vengono specificati tutti i campi nella api di creazione, oggetto Versione
    #TODO: test dove vengono specificati meno dei campi obbligatori nella api di creazione, oggetto Versione