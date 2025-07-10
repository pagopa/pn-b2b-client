@m2m-attributes
Feature: Gestione degli attributes

  Scenario Outline: [M2MG_CERTIFIEDATTRIBUTES_1] Recupero del dettaglio di un attributo certificato con utente autorizzato (Scenario 61)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di recuperare il record di certifiedAttribute creato
    Then si ottiene lo status code 200
    And certifiedAttribute viene restituito e combacia con il record creato
    Examples:
      | ruolo     |
      | m2m       |
      | m2m-admin |

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_3] Accesso negato al dettaglio di un attributo certificato con token non valido (Scenario 63)
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare certifiedAttribute con un id inesistente
    Then si ottiene lo status code 401
    And certifiedAttribute non restituito

  Scenario Outline: [M2MG_CERTIFIEDATTRIBUTES_4] Errore nel recupero del dettaglio di un attributo certificato con attributeId inesistente (Scenario 64)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare certifiedAttribute con un id inesistente
    Then si ottiene lo status code 404
    And certifiedAttribute non restituito
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_5] Creazione di un attributo certificato con utente M2M-ADMIN (Scenario 20)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    And si ottiene lo status code 201
    When l'utente tenta di recuperare il record di certifiedAttribute creato
    Then certifiedAttribute viene restituito e combacia con il record creato

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_6] Accesso negato alla creazione di un attributo certificato con utente M2M (Scenario 41)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code 403
    And certifiedAttribute non restituito


  # Da qui in poi test di "API V2 Parte 2" https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1812562407/DRAFT+SRS+API+V2+Parte+2#Scenari-di-test
  Scenario Outline: [M2MG_CERTIFIEDATTRIBUTES_7] La lista degli attributi certificati può essere visionata da un utente con ruolo M2M o M2M-ADMIN (Parte2#Scenario 9)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    And viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    When l'utente tenta di recuperare la lista di certifiedAttribute
    Then si ottiene lo status code 200
    And la lista di certifiedAttribute è presente solo se lo status code è 200
    When l'utente tenta di recuperare la lista di certifiedAttribute filtrata per nome
    Then si ottiene lo status code 200
    And la lista ottenuta contiene l'attributo certificato creato
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  Scenario: [M2MG_CERTIFIEDATTRIBUTES_7] La lista degli attributi certificati può essere visionata da un utente con token non valido (Parte2#Scenario 11)
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare la lista di certifiedAttribute
    Then si ottiene lo status code 401

  Scenario Outline: [M2MG_DECLAREDATTRIBUTES_1] Recupero del dettaglio di un attributo dichiarato con utente autorizzato (Parte2#Scenario 1)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo dichiarato
      | name | description | code |
      |      |             |      |
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di recuperare il record di declaredAttribute creato
    Then si ottiene lo status code 200
    And declaredAttribute viene restituito e combacia con il record creato
    Examples:
      | ruolo     |
      | m2m       |
      | m2m-admin |

  Scenario: [M2MG_DECLAREDATTRIBUTES_3] Accesso negato al dettaglio di un attributo dichiarato con token non valido (Parte2#Scenario 3)
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare declaredAttribute con un id inesistente
    Then si ottiene lo status code 401
    And declaredAttribute non restituito

  Scenario Outline: [M2MG_DECLAREDATTRIBUTES_4] Errore nel recupero del dettaglio di un attributo dichiarato con attributeId inesistente (Parte2#Scenario 4)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare declaredAttribute con un id inesistente
    Then si ottiene lo status code 404
    And declaredAttribute non restituito
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  Scenario: [M2MG_DECLAREDATTRIBUTES_5] Creazione di un attributo dichiarato con utente M2M-ADMIN
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene effettuata la creazione dell'attributo dichiarato
      | name | description | code |
      |      |             |      |
    And si ottiene lo status code 201
    When l'utente tenta di recuperare il record di declaredAttribute creato
    Then declaredAttribute viene restituito e combacia con il record creato

  Scenario: [M2MG_DECLAREDATTRIBUTES_6] Accesso negato alla creazione di un attributo dichiarato con utente M2M
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When viene effettuata la creazione dell'attributo dichiarato
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code 403
    And declaredAttribute non restituito

  Scenario Outline: [M2MG_VERIFIEDATTRIBUTES_1] Recupero del dettaglio di un attributo verificato con utente autorizzato (Parte2#Scenario 5)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo verificato
      | name | description | code |
      |      |             |      |
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di recuperare il record di verifiedAttribute creato
    Then si ottiene lo status code 200
    And verifiedAttribute viene restituito e combacia con il record creato
    Examples:
      | ruolo     |
      | m2m       |
      | m2m-admin |

  Scenario: [M2MG_VERIFIEDATTRIBUTES_3] Accesso negato al dettaglio di un attributo verificato con token non valido (Parte2#Scenario 7)
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare verifiedAttribute con un id inesistente
    Then si ottiene lo status code 401
    And verifiedAttribute non restituito

  Scenario Outline: [M2MG_VERIFIEDATTRIBUTES_4] Errore nel recupero del dettaglio di un attributo verificato con attributeId inesistente (Parte2#Scenario 8)
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare verifiedAttribute con un id inesistente
    Then si ottiene lo status code 404
    And verifiedAttribute non restituito
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  Scenario: [M2MG_VERIFIEDATTRIBUTES_5] Creazione di un attributo verificato con utente M2M-ADMIN
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene effettuata la creazione dell'attributo verificato
      | name | description | code |
      |      |             |      |
    And si ottiene lo status code 201
    When l'utente tenta di recuperare il record di verifiedAttribute creato
    Then verifiedAttribute viene restituito e combacia con il record creato

  Scenario: [M2MG_VERIFIEDATTRIBUTES_6] Accesso negato alla creazione di un attributo verificato con utente M2M
    Given "PA1" ha già creato e pubblicato 1 e-services
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When viene effettuata la creazione dell'attributo verificato
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code 403
    And verifiedAttribute non restituito

