@capofila
Feature: Creazione di una delega in erogazione

  Background:
    Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe

  #TC-4: Un utente con ruolo admin può creare una delega
  #TC-5: Un utente con ruolo diverso da admin NON può creare una delega
  #TC-31: Una delega può essere creata dal delegante se delegato da la disponibilità a ricevere la delega
  Scenario Outline: [TC_CAPOFILA_4_5] Il richiamo dell’API di creazione di una delega possa essere compiuto da un utente di livello operatore amministrativo (admin)
    Given l'utente è un "<ruolo>" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega per l'ente "PA2"
    Then si ottiene status code <statusCode>

    Examples:
      | ruolo        | statusCode |
      | admin        |        200 |
      | api          |        403 |
      | security     |        403 |
      | api,security |        403 |
      | support      |        403 |


  Scenario Outline: [TC_CAPOFILA_RIFIUTO_PENDING] Il rifiuto di una delega in stato di pending possa essere compiuto solo da un utente con ruolo admin
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And un utente dell'ente <funzione> con ruolo "<ruolo>"
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegato concede la disponibilità a ricevere deleghe
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    And la delega è stata creata correttamente
    When l'utente rifiuta la delega
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo        | funzione  | statusCode  |
      # Scenario: 26
      # Esito: coerente
      | admin        | delegato  | 200         |
      # Scenario: 6
      # Esito: coerente
      | api          | delegato  | 403         |
      # Scenario: 6
      # Esito: coerente
      | security     | delegato  | 403         |
      # Scenario: 6
      # Esito: coerente
      | api,security | delegato  | 403         |
      # Scenario: 6
      # Esito: coerente
      | support      | delegato  | 403         |
      # Scenario: 28
      # Esito: incoerente, 403, "Unauthorized"
      | admin        | delegante | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api          | delegante | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | security     | delegante | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api,security | delegante | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | support      | delegante | 403         |

  Scenario Outline: [TC_CAPOFILA_RIFIUTO_DELEGA_ACCETTATA] Il rifiuto di una delega già accettata non possa essere compiuto da nessun utente indipentendemente dal ruolo
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And un utente dell'ente <funzione> con ruolo "<ruolo>"
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegato concede la disponibilità a ricevere deleghe
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    And la delega è stata creata correttamente
    And l'ente delegato ha accettato la delega
    When l'utente rifiuta la delega
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo        | funzione  | statusCode  |
      # Scenario: 29
      # Esito: coerente
      | admin        | delegato  | 409         |
      # Scenario: <mancante>
      # Esito: si ottiene 403
      | api          | delegato  | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403
      | security     | delegato  | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403
      | api,security | delegato  | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403
      | support      | delegato  | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Operation restricted to delegate"
      | admin        | delegante | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api          | delegante | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | security     | delegante | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | api,security | delegante | 403         |
      # Scenario: <mancante>
      # Esito: si ottiene 403 "Unauthorized"
      | support      | delegante | 403         |

  #TC-7: L'accettazione di una delega NON può essere fatta da un utente con ruolo diverso da ADMIN
  #TC-8: La revoca di una delega NON può essere fatta da un utente con ruolo diverso da ADMIN
  #TC-13: L'accettazione di una delega può essere fatta da un utente con ruolo ADMIN
  #TC-14: La revoca di una delega può essere fatta da un utente con ruolo ADMIN
  Scenario Outline: [TC_CAPOFILA_ACCETTA_REVOCA_DELEGA] L'accettazione e la revoca di una delega non può essere effettuata da un utente diverso da admin
    Given l'utente è un "<ruolo>" di "PA2"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata creata correttamente
    And l'utente accetta la delega
    Then si ottiene status code <statusCode>
    # Revoca della delega accettata
    When l'ente "PA1" con ruolo "<ruolo>" revoca la delega
    Then si ottiene status code <statusCode>
    Examples:
      | ruolo        | statusCode |
      | admin        |        200 |
      | api          |        403 |
      | security     |        403 |
      | api,security |        403 |
      | support      |        403 |

  #TC-21: Delegato con ruolo admin non può revocare la delega
  Scenario: [TC_CAPOFILA_DELEGATO_REVOCA] La revoca di una delega in stato PENDING non può essere effettuata da un delegato con ruolo admin
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegato concede la disponibilità a ricevere deleghe
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    And la delega è stata creata correttamente
    And un utente dell'ente delegato con ruolo "admin"
    When l'ente "PA2" con ruolo "admin" revoca la delega
    Then si ottiene lo status code 403

  #TC-12: L'API di disponibilità NON puà essere invocata da un utente admin di un tenant NON PA
  Scenario: [TC_CAPOFILA_PRIVATO] La revoca di una delega in stato PENDING non può essere effettuata da un delegato con ruolo admin
    Given l'ente delegante "PA1"
    And l'ente delegato "Privato"
    And l'ente delegante ha già creato e pubblicato 1 e-service
    And l'ente delegato concede la disponibilità a ricevere deleghe
    Then si ottiene lo status code 403

  #TC-11: La disponibilità di una delega può essere fatta soltanto da un utente con ruolo ADMIN
  Scenario Outline: [TC_CAPOFILA_DISPONIBILITA_DELEGHE] L'accettazione e la revoca di una delega non può essere effettuata da un utente diverso da admin
    Given l'utente è un "<ruolo>" di "PA2"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente concede la disponibilità a ricevere le deleghe
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo        | statusCode |
      | admin        |        200 |
      | api          |        403 |
      | security     |        403 |
      | api,security |        403 |
      | support      |        403 |

  Scenario: [TC_CAPOFILA_35] Un delegante può delegare un solo ente per volta per un e-service
    Given l'utente è un "admin" di "PA1"
    Given l'ente "GSP" rimuove la disponibilità a ricevere deleghe
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata creata correttamente
    And l'ente "PA2" accetta la delega
    And l'ente "GSP" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega per l'ente "GSP"
    Then si ottiene status code 409