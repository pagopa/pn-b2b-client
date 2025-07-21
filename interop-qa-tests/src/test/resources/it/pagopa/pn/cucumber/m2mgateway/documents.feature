@m2mEservices
Feature: Gestione dei documenti attraverso APIs M2M V2

  Scenario: [M2MG_DOCUMENTS_01] Un utente con ruolo M2M-ADMIN può effettuare il caricamento di un'interfaccia di un e-service in stato DRAFT (Parte2#Scenario 47)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 200
    And l'interfaccia è stata caricata con successo

  Scenario: [M2MG_DOCUMENTS_02] Un utente con ruolo M2M non può effettuare il caricamento di un'interfaccia di un e-service (Parte2#Scenario 49)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 401
    And l'interfaccia non è stata caricata

  Scenario: [M2MG_DOCUMENTS_03] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service indicando degli identificativi inesistenti (Parte2#Scenario 50)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento di un'interfaccia di un e-service inesistente
    Then si ottiene lo status code 404
    And l'interfaccia non è stata caricata
    When l'utente tenta di effettuare il caricamento di un'interfaccia di un e-service descriptor inesistente
    Then si ottiene lo status code 404
    And l'interfaccia non è stata caricata

  Scenario: [M2MG_DOCUMENTS_04] Non può essere effettuato il caricamento di un'interfaccia di un e-service specificando un token non valido (Parte2#Scenario 51)
    Given viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare il caricamento di un'interfaccia di un e-service inesistente
    Then si ottiene lo status code 401
    And l'interfaccia non è stata caricata

  Scenario: [M2MG_DOCUMENTS_05] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service per il quale è stata già caricata un'interfaccia (Parte2#Scenario 52)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 409
    And l'interfaccia non è stata caricata

  Scenario: [M2MG_DOCUMENTS_06] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service per il quale è stata già caricata un'interfaccia con lo stesso nome (Parte2#Scenario 53)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente effettua il caricamento dell'interfaccia dell'e-service con successo
    When l'utente tenta di effettuare il caricamento di un'interfaccia con lo stesso nome
    Then si ottiene lo status code 409
    And l'interfaccia non è stata caricata

  Scenario Outline: [M2MG_DOCUMENTS_07] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service in stato diverso da DRAFT (Parte2#Scenario 54)
    Given "PA1" ha già creato un e-service con un descrittore in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 400
    And l'interfaccia non è stata caricata
    Examples:
      | stato                 |
      | SUSPENDED             |
      | PUBLISHED             |
      | DEPRECATED            |
      | ARCHIVED              |
      | WAITING_FOR_APPROVAL  |

  Scenario: [M2MG_DOCUMENTS_08] Un utente con ruolo M2M-ADMIN non può effettuare il caricamento di un'interfaccia di un e-service se non è il creatore dello stesso (Parte2#Scenario 55)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'interfaccia non è stata caricata