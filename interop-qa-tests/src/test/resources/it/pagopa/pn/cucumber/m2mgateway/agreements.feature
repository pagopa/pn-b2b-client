@m2m-agreements
Feature: Gestione degli agreements attraverso APIs M2M V2

  @happy-path
  Scenario Outline: [M2M_AGREEMENTS_LIST_1] La lista degli agreements può essere visionata da un utente con ruolo M2M o M2M-ADMIN
    Given "PA1" ha già creato e pubblicato 5 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    And "<ente>" ha un agreement m2m attivo per ciascun e-service di "PA1"
    When l'utente tenta di recuperare una lista di 5 agreements creati
    Then si ottiene lo status code 200
    And sono stati visualizzati correttamente 5 agreements creati
    Examples:
      | ruolo-m2m  |
      | m2m-admin  |
      | m2m        |

  @sad-path
  Scenario: [M2M_AGREEMENTS_LIST_2] La lista degli agreements NON può essere visionata da un utente che ha presentato un token m2m scaduto
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha un agreement m2m attivo per ciascun e-service di "PA1"
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare una lista di 1 agreements creati
    Then si ottiene lo status code 401

  # Da qui in poi test di "API V2 Parte 2" https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1812562407/DRAFT+SRS+API+V2+Parte+2#Scenari-di-test
  @m2m-agreements-parte2-luglio @ko-nrt-08072026
  Scenario Outline: [M2M_AGREEMENTS_PURPOSES_1] La lista delle finalità correlate a un agreement può essere visualizzata da un utente con ruolo M2M-ADMIN o M2M (Parte2#Scenario 12)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 2 finalità in stato "ACTIVE" per quell'eservice
    When l'utente è un "admin" di "PA2" con ruolo M2M <ruolo-m2m>
    And l'utente tenta di ottenere la lista delle finalità correlate alla richiesta di fruizione
    Then si ottiene status code 200
    And le finalità vengono correttamente visualizzate
    Examples:
      | ruolo-m2m  |
      | m2m-admin  |
      | m2m        |

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_AGREEMENTS_PURPOSES_2] La lista delle finalità correlate a un agreement non può essere visualizzata specificando un token non valido (Parte2#Scenario 14)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di ottenere la lista delle finalità correlate a una richiesta di fruizione inesistente
    Then si ottiene status code 401

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_AGREEMENTS_PURPOSES_3] La lista delle finalità correlate a un agreement non può essere visualizzata specificando un id inesistente (Parte2#Scenario 15)
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di ottenere la lista delle finalità correlate a una richiesta di fruizione inesistente
    Then si ottiene status code 404

  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2M_AGREEMENTS_DOCUMENTS_1] La lista dei documenti correlati a un agreement può essere visualizzata da un utente con ruolo M2M-ADMIN o M2M (Parte2#Scenario 16)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    And l'utente carica un documento allegato a quella richiesta di fruizione con successo
    When l'utente è un "admin" di "PA2" con ruolo M2M <ruolo-m2m>
    And l'utente tenta di ottenere la lista dei documenti correlati alla richiesta di fruizione
    Then si ottiene status code 200
    And i documenti vengono correttamente visualizzati
    Examples:
      | ruolo-m2m  |
      | m2m-admin  |
      | m2m        |

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_AGREEMENTS_DOCUMENTS_2] La lista dei documenti correlati a un agreement non può essere visualizzata specificando un token non valido (Parte2#Scenario 18)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di ottenere la lista dei documenti correlati a una richiesta di fruizione inesistente
    Then si ottiene status code 401

  @m2m-agreements-parte2-luglio
  Scenario: [M2M_AGREEMENTS_DOCUMENTS_3] La lista dei documenti correlati a un agreement non può essere visualizzata specificando un id inesistente (Parte2#Scenario 19)
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di ottenere la lista dei documenti correlati a una richiesta di fruizione inesistente
    Then si ottiene status code 404
