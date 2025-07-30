@m2mEservices
Feature: Gestione dei documenti attraverso APIs M2M V2

  Scenario: [M2MG_DOCUMENTS_01] Un utente con ruolo M2M-ADMIN può effettuare il caricamento di un'interfaccia di un e-service in stato DRAFT (Parte2#Scenario intorno a 47)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 200
    And è presente un'interfaccia per l'e-service

  Scenario: [M2MG_DOCUMENTS_02] Un utente con ruolo M2M non può effettuare il caricamento di un'interfaccia di un e-service (Parte2#Scenario intorno a 49)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 401
    And non è presente alcuna interfaccia per l'e-service

  Scenario: [M2MG_DOCUMENTS_03] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service indicando degli identificativi inesistenti (Parte2#Scenario intorno a 50)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento di un'interfaccia di un e-service inesistente
    Then si ottiene lo status code 404
    And non è presente alcuna interfaccia per l'e-service
    When l'utente tenta di effettuare il caricamento di un'interfaccia di un e-service descriptor inesistente
    Then si ottiene lo status code 404
    And non è presente alcuna interfaccia per l'e-service

  Scenario: [M2MG_DOCUMENTS_04] Non può essere effettuato il caricamento di un'interfaccia di un e-service specificando un token non valido (Parte2#Scenario intorno a 51)
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare il caricamento di un'interfaccia di un e-service inesistente
    Then si ottiene lo status code 401

  Scenario: [M2MG_DOCUMENTS_05] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service per il quale è stata già caricata un'interfaccia (Parte2#Scenario intorno a 52)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 409

  Scenario Outline: [M2MG_DOCUMENTS_07] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service in stato diverso da DRAFT (Parte2#Scenario intorno a 54)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 400
    And non è presente alcuna interfaccia per l'e-service
    Examples:
      | stato                 |
      | SUSPENDED             |
      | PUBLISHED             |
      | DEPRECATED            |
      | ARCHIVED              |
      | WAITING_FOR_APPROVAL  |

  Scenario: [M2MG_DOCUMENTS_08] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service se non è il creatore dello stesso (Parte2#Scenario intorno a 55)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then non è presente alcuna interfaccia per l'e-service

  Scenario: [M2MG_DOCUMENTS_09] Un utente con ruolo M2M-ADMIN può effettuare la cancellazione di un'interfaccia di un e-service in stato DRAFT (Parte2#Scenario intorno a 56)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    When l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 200
    And non è presente alcuna interfaccia per l'e-service

  Scenario: [M2MG_DOCUMENTS_10] Un utente con ruolo M2M non può effettuare la cancellazione di un'interfaccia di un e-service (Parte2#Scenario intorno a 58)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then è presente un'interfaccia per l'e-service

  Scenario: [M2MG_DOCUMENTS_11] Un utente con ruolo M2M-ADMIN non può effettuare la cancellazione di un'interfaccia indicando un e-service o un descriptor inesistente (Parte2#Scenario intorno a 59)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la cancellazione di un'interfaccia di un e-service inesistente
    Then si ottiene lo status code 404
    When l'utente tenta di effettuare la cancellazione di un'interfaccia di un e-service descriptor inesistente
    Then si ottiene lo status code 404

  Scenario: [M2MG_DOCUMENTS_12] Non può essere effettuata la cancellazione di un'interfaccia di un e-service specificando un token non valido (Parte2#Scenario intorno a 60)
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la cancellazione di un'interfaccia di un e-service inesistente
    Then si ottiene lo status code 401

  Scenario: [M2MG_DOCUMENTS_13] Un utente con ruolo M2M-ADMIN non può effettuare la cancellazione di un'interfaccia già eliminata (Parte2#Scenario intorno a 61)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    And l'utente effettua la cancellazione dell'interfaccia dell'e-service con successo
    When l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 404
    And non è presente alcuna interfaccia per l'e-service

  Scenario: [M2MG_DOCUMENTS_14] Un utente con ruolo M2M-ADMIN non può effettuare la cancellazione di un'interfaccia di un e-service se non è il creatore dello stesso (Parte2#Scenario intorno a 61)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 403
    And è presente un'interfaccia per l'e-service

  Scenario Outline: [M2MG_DOCUMENTS_15] Un utente non può effettuare la cancellazione di un'interfaccia di un e-service in stato diverso da DRAFT (Parte2#Scenario intorno a 62)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    When l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 400
    And è presente un'interfaccia per l'e-service
    Examples:
      | stato                 |
      | SUSPENDED             |
      | PUBLISHED             |
      | DEPRECATED            |
      | ARCHIVED              |

  Scenario: [M2MG_DOCUMENTS_16] Un utente non può effettuare la cancellazione di un'interfaccia di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 62)
    # tutta questa prima parte serve per ricondursi allo stato WAITING_FOR_APPROVAL, ripresa da [TC_CAPOFILA_PUB_1]
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    And l'ente "PA2" accetta la delega
    And l'utente è un "admin" di "PA2"
    And l'utente pubblica l'e-service

    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 400
    And è presente un'interfaccia per l'e-service

    # TODO essendo coinvolto il processo di delega, occorrerà un test specifico per questo stato. Ricercare ticket aperti che coinvolgono
    # questo stato e rocondursi ai corrispettivi scenari, dovrebbero poter fare da template.
    #  | WAITING_FOR_APPROVAL  |
