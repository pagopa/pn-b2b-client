@m2m-attributes
Feature: Gestione degli attributes attraverso APIs M2M V2

  @happy-path
  Scenario Outline: [M2MG_CERTIFIEDATTRIBUTES_1] Recupero del dettaglio di un attributo certificato con utente autorizzato (Scenario 61)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato con successo
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

  @sad-path
  Scenario: [M2MG_CERTIFIEDATTRIBUTES_3] Accesso negato al dettaglio di un attributo certificato con token non valido (Scenario 63)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare certifiedAttribute con un id inesistente
    Then si ottiene lo status code 401
    And certifiedAttribute non restituito

  @sad-path
  Scenario Outline: [M2MG_CERTIFIEDATTRIBUTES_4] Errore nel recupero del dettaglio di un attributo certificato con attributeId inesistente (Scenario 64)
    Given l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare certifiedAttribute con un id inesistente
    Then si ottiene lo status code 404
    And certifiedAttribute non restituito
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @happy-path
  Scenario: [M2MG_CERTIFIEDATTRIBUTES_5] Creazione di un attributo certificato con utente M2M-ADMIN (Scenario 20)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    And si ottiene lo status code 201
    When l'utente tenta di recuperare il record di certifiedAttribute creato
    Then certifiedAttribute viene restituito e combacia con il record creato

  @sad-path
  Scenario: [M2MG_CERTIFIEDATTRIBUTES_6] Accesso negato alla creazione di un attributo certificato con utente M2M (Scenario 41)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When viene effettuata la creazione dell'attributo certificato
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code 403
    And certifiedAttribute non restituito


  # Da qui in poi test di "API V2 Parte 2" https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1812562407/DRAFT+SRS+API+V2+Parte+2#Scenari-di-test
  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2MG_CERTIFIEDATTRIBUTES_7] La lista degli attributi certificati può essere visionata da un utente con ruolo M2M o M2M-ADMIN (Parte2#Scenario 9)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene effettuata la creazione dell'attributo certificato con successo
      | name | description | code |
      |      |             |      |
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    And l'utente tenta di recuperare la lista di certifiedAttribute
    Then si ottiene lo status code 200
    And la risposta contiene almeno 1 attributo certificato
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @m2m-agreements-parte2-luglio
  Scenario: [M2MG_CERTIFIEDATTRIBUTES_8] La lista degli attributi certificati può essere visionata da un utente con token non valido (Parte2#Scenario 11)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare la lista di certifiedAttribute
    Then si ottiene lo status code 401

  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2MG_DECLAREDATTRIBUTES_1] Recupero del dettaglio di un attributo dichiarato con utente autorizzato (Parte2#Scenario 1)
    Given l'utente è un "admin" di "PA1"
    And l'utente crea un attributo dichiarato
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di recuperare l'attributo dichiarato creato
    Then si ottiene lo status code 200
    And l'attributo dichiarato è stato creato correttamente
    Examples:
      | ruolo     |
      | m2m       |
      | m2m-admin |

  @m2m-agreements-parte2-luglio
  Scenario: [M2MG_DECLAREDATTRIBUTES_3] Accesso negato al dettaglio di un attributo dichiarato con token non valido (Parte2#Scenario 3)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare declaredAttribute con un id inesistente
    Then si ottiene lo status code 401
    And declaredAttribute non restituito

  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2MG_DECLAREDATTRIBUTES_4] Errore nel recupero del dettaglio di un attributo dichiarato con attributeId inesistente (Parte2#Scenario 4)
    Given l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare declaredAttribute con un id inesistente
    Then si ottiene lo status code 404
    And declaredAttribute non restituito
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @m2m-agreements-parte2-luglio
  Scenario: [M2MG_DECLAREDATTRIBUTES_5] Creazione di un attributo dichiarato con utente M2M-ADMIN
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene effettuata la creazione dell'attributo dichiarato
      | name | description | code |
      |      |             |      |
    And si ottiene lo status code 201
    When l'utente tenta di recuperare il record di declaredAttribute creato
    Then declaredAttribute viene restituito e combacia con il record creato

  @m2m-agreements-parte2-luglio
  Scenario: [M2MG_DECLAREDATTRIBUTES_6] Accesso negato alla creazione di un attributo dichiarato con utente M2M
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When viene effettuata la creazione dell'attributo dichiarato
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code 403
    And declaredAttribute non restituito

  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2MG_VERIFIEDATTRIBUTES_1] Recupero del dettaglio di un attributo verificato con utente autorizzato (Parte2#Scenario 5)
    Given l'utente è un "admin" di "PA1"
    And l'utente crea un attributo verificato
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    And l'utente tenta di recuperare l'attributo verificato creato
    Then si ottiene lo status code 200
    And l'attributo verificato è stato creato correttamente
    Examples:
      | ruolo     |
      | m2m       |
      | m2m-admin |

  @m2m-agreements-parte2-luglio
  Scenario: [M2MG_VERIFIEDATTRIBUTES_3] Accesso negato al dettaglio di un attributo verificato con token non valido (Parte2#Scenario 7)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare verifiedAttribute con un id inesistente
    Then si ottiene lo status code 401
    And verifiedAttribute non restituito

  @m2m-agreements-parte2-luglio
  Scenario Outline: [M2MG_VERIFIEDATTRIBUTES_4] Errore nel recupero del dettaglio di un attributo verificato con attributeId inesistente (Parte2#Scenario 8)
    Given l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare verifiedAttribute con un id inesistente
    Then si ottiene lo status code 404
    And verifiedAttribute non restituito
    Examples:
      | ruolo-m2m |
      | m2m       |
      | m2m-admin |

  @m2m-agreements-parte2-luglio
  Scenario: [M2MG_VERIFIEDATTRIBUTES_5] Creazione di un attributo verificato con utente M2M-ADMIN
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene effettuata la creazione dell'attributo verificato
      | name | description | code |
      |      |             |      |
    And si ottiene lo status code 201
    When l'utente tenta di recuperare il record di verifiedAttribute creato
    Then verifiedAttribute viene restituito e combacia con il record creato

  @m2m-agreements-parte2-luglio
  Scenario: [M2MG_VERIFIEDATTRIBUTES_6] Accesso negato alla creazione di un attributo verificato con utente M2M
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When viene effettuata la creazione dell'attributo verificato
      | name | description | code |
      |      |             |      |
    Then si ottiene lo status code 403
    And verifiedAttribute non restituito

  # DEV. NOTE 05/08/2025: l'assenza di polling attivo per le APIs in test dovrebbe essere
  # compensata dal tempo occupato dagli step intermedi necessari. Considerare la possibilità che
  # NON sia così qualora il test fallisse.
  @m2m-parte2-agosto-rilascio1
  Scenario Outline: [M2MG_VERIFIEDATTRIBUTES_7]
          (Parte 1) Un utente con ruolo M2M o M2M-ADMIN può recuperare la lista degli enti che hanno verificato un certo attributo associato al proprio ente di appartenenza.
          (Parte 2) Inoltre, la revoca dell'attributo da parte di uno degli enti coinvolti implica il suo inserimento nella lista dei revokers, e la sua rimozione dalla lista dei verifiers.
    #Parte 1
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    And "GSP" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "GSP" ha già verificato l'attributo verificato a "PA1"
    When l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    And l'utente tenta di recuperare la lista di enti che hanno verificato l'attributo
    Then si ottiene lo status code 200
    And la lista degli enti che hanno verificato l'attributo è
      | PA2   |
      | GSP   |

    #Parte 2
    Given "GSP" revoca l'attributo precedentemente verificato
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo-m2m>
    When l'utente tenta di recuperare la lista di enti che hanno verificato l'attributo
    Then la lista degli enti che hanno verificato l'attributo è
      | PA2   |
    When l'utente tenta di recuperare la lista di enti che hanno revocato l'attributo
    Then la lista degli enti che hanno revocato l'attributo è
      | GSP   |
    Examples:
      | ruolo-m2m |
      | m2m-admin |
      | m2m       |

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_VERIFIEDATTRIBUTES_8] Accesso negato alla lista degli enti verificatori di un attributo con token non valido
    Given "PA2" ha già creato un attributo verificato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare la lista di enti che hanno verificato l'attributo indicando un ente inesistente
    Then si ottiene lo status code 401

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_VERIFIEDATTRIBUTES_9] Un utente non può recuperare la lista degli enti che hanno verificato un certo attributo verificato indicando identificativi inesistenti
    Given "PA2" ha già creato un attributo verificato
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di recuperare la lista di enti che hanno verificato l'attributo indicando un ente inesistente
    Then si ottiene lo status code 404

  # Ticket chiuso https://pagopa.atlassian.net/browse/PIN-7419
  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_VERIFIEDATTRIBUTES_12] Un utente con ruolo M2M-ADMIN può recuperare la lista degli enti che hanno verificato un attributo associato ad un ente proprio o terzo.
    Given "GSP" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di recuperare la lista di enti che hanno verificato l'attributo all'ente "PA1"
    Then si ottiene lo status code 200
    When l'utente è un "admin" di "GSP2" con ruolo M2M m2m-admin
    And l'utente tenta di recuperare la lista di enti che hanno verificato l'attributo all'ente "PA1"
    Then si ottiene lo status code 200

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_VERIFIEDATTRIBUTES_10] Accesso negato alla lista degli enti revocatori di un attributo con token non valido
    Given "PA2" ha già creato un attributo verificato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare la lista di enti che hanno revocato l'attributo indicando un ente inesistente
    Then si ottiene lo status code 401

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_VERIFIEDATTRIBUTES_11] Un utente non può recuperare la lista degli enti che hanno verificato un certo attributo verificato indicando identificativi inesistenti
    Given "PA2" ha già creato un attributo verificato
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di recuperare la lista di enti che hanno revocato l'attributo indicando un ente inesistente
    Then si ottiene lo status code 404

  # Ticket chiuso https://pagopa.atlassian.net/browse/PIN-7420
  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_VERIFIEDATTRIBUTES_13] Un utente con ruolo M2M-ADMIN può recuperare la lista degli enti che hanno revocato un attributo associato ad un ente proprio o terzo.
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    And "PA2" revoca l'attributo precedentemente verificato
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di recuperare la lista di enti che hanno revocato l'attributo all'ente "PA1"
    Then si ottiene lo status code 200
    When l'utente è un "admin" di "GSP2" con ruolo M2M m2m-admin
    And l'utente tenta di recuperare la lista di enti che hanno revocato l'attributo all'ente "PA1"
    Then si ottiene lo status code 200