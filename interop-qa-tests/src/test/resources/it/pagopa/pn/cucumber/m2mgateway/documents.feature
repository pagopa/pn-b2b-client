@m2mEservices
Feature: Gestione dei documenti attraverso APIs M2M V2
  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_01] Un utente con ruolo M2M-ADMIN può effettuare il caricamento di un'interfaccia di un e-service in stato DRAFT (Parte2#Scenario intorno a 47)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 200
    And è presente un'interfaccia per l'e-service

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_02] Un utente con ruolo M2M non può effettuare il caricamento di un'interfaccia di un e-service (Parte2#Scenario intorno a 49)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 403
    And non è presente alcuna interfaccia per l'e-service

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_03] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service indicando degli identificativi inesistenti (Parte2#Scenario intorno a 50)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento di un'interfaccia di un e-service inesistente
    Then si ottiene lo status code 404
    And non è presente alcuna interfaccia per l'e-service

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_04] Non può essere effettuato il caricamento di un'interfaccia di un e-service specificando un token non valido (Parte2#Scenario intorno a 51)
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare il caricamento di un'interfaccia di un e-service inesistente
    Then si ottiene lo status code 401

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_05] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service per il quale è stata già caricata un'interfaccia (Parte2#Scenario intorno a 52)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 409

  @m2m-parte2-agosto-rilascio1
  Scenario Outline: [M2MG_DOCUMENTS_07_A] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service in stato diverso da DRAFT (Parte2#Scenario intorno a 54)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 400
    And non è presente alcuna interfaccia per l'e-service
    Examples:
      | stato       |
      | SUSPENDED   |
      | PUBLISHED   |
      | DEPRECATED  |
      | ARCHIVED    |


  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_07_B] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 54)
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 400
    And non è presente alcuna interfaccia per l'e-service

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_08] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service se non è il creatore dello stesso (Parte2#Scenario intorno a 55)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then non è presente alcuna interfaccia per l'e-service

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_09] Un utente con ruolo M2M-ADMIN può effettuare la cancellazione di un'interfaccia di un e-service in stato DRAFT (Parte2#Scenario intorno a 56)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    When l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 200
    And non è presente alcuna interfaccia per l'e-service

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_10] Un utente con ruolo M2M non può effettuare la cancellazione di un'interfaccia di un e-service (Parte2#Scenario intorno a 58)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then è presente un'interfaccia per l'e-service

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_11] Un utente con ruolo M2M-ADMIN non può effettuare la cancellazione di un'interfaccia indicando un e-service o un descriptor inesistente (Parte2#Scenario intorno a 59)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la cancellazione di un'interfaccia di un e-service inesistente
    Then si ottiene lo status code 404

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_12] Non può essere effettuata la cancellazione di un'interfaccia di un e-service specificando un token non valido (Parte2#Scenario intorno a 60)
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la cancellazione di un'interfaccia di un e-service inesistente
    Then si ottiene lo status code 401

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_13] Un utente con ruolo M2M-ADMIN non può effettuare la cancellazione di un'interfaccia già eliminata (Parte2#Scenario intorno a 61)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    And l'utente effettua la cancellazione dell'interfaccia dell'e-service con successo
    When l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 404
    And non è presente alcuna interfaccia per l'e-service

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_14] Un utente con ruolo M2M-ADMIN non può effettuare la cancellazione di un'interfaccia di un e-service se non è il creatore dello stesso (Parte2#Scenario intorno a 61)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 403
    And è presente un'interfaccia per l'e-service

  @m2m-parte2-agosto-rilascio1
  Scenario Outline: [M2MG_DOCUMENTS_15] Un utente non può effettuare la cancellazione di un'interfaccia di un e-service in stato diverso da DRAFT (Parte2#Scenario intorno a 62)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 400
    And è presente un'interfaccia per l'e-service
    Examples:
      | stato                 |
      | SUSPENDED             |
      | PUBLISHED             |
      | DEPRECATED            |
      | ARCHIVED              |

  @m2m-parte2-agosto-rilascio1
  Scenario: [M2MG_DOCUMENTS_16] Un utente non può effettuare la cancellazione di un'interfaccia di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 62)
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service
    Then si ottiene lo status code 400
    And è presente un'interfaccia per l'e-service

  @m2m-parte2-agosto-rilascio2
  Scenario Outline: [M2MG_DOCUMENTS_17] Un utente può reperire la lista dei metadati dei documenti associati ad un e-service in stato PUBLISHED
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e 2 documenti già caricati
    And l'utente è un "admin" di "PA1" con ruolo M2M <ruolo>
    When l'utente tenta di recuperare i metadati dei documenti associati all'e-service
    Then si ottiene lo status code 200
    And i metadati dei documenti ottenuti sono coerenti con quelli caricati

    # Verifica che possano essere recuperati anche da enti non proprietari dell'e-service
    And l'utente è un "admin" di "PA2" con ruolo M2M <ruolo>
    When l'utente tenta di recuperare i metadati dei documenti associati all'e-service
    Then si ottiene lo status code 200
    And i metadati dei documenti ottenuti sono coerenti con quelli caricati
    Examples:
      | ruolo     |
      | m2m       |
      | m2m-admin |

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_DOCUMENTS_18] Un utente NON può reperire la lista dei metadati dei documenti associati ad un e-service in stato PUBLISHED usando un token non valido
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e 2 documenti già caricati
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare i metadati dei documenti associati all'e-service
    Then si ottiene lo status code 401

  @m2m-parte2-agosto-rilascio2
  Scenario: [M2MG_DOCUMENTS_19] Un utente NON può reperire la lista dei metadati dei documenti associati ad un e-service o ad un descriptor inesistenti in stato PUBLISHED
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e 2 documenti già caricati
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di recuperare i metadati dei documenti di un e-service inesistenti
    Then si ottiene lo status code 404
    When l'utente tenta di recuperare i metadati dei documenti di un descriptor inesistenti
    Then si ottiene lo status code 404