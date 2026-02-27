@purposeTemplate
@m2m-purpose-templates
Feature: Gestione purpose templates attraverso APIs M2M V2

  @purpose-template-m2m-patch
  Scenario: [INTEROP-PT-M2M-PATCH_01] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale di un purpose template in stato DRAFT (Parte2#Scenario intorno a 145)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del purpose template
    Then si ottiene lo status code 200
    And il purpose template restituito è coerente con le modifiche effettuate
    And il purpose template è stato parzialmente modificato correttamente
    When l'utente tenta di effettuare la modifica parziale del purpose template specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 200
    And il purpose template restituito è coerente con le modifiche effettuate
    And il purpose template è stato parzialmente modificato correttamente
    When l'utente tenta di effettuare la modifica parziale del purpose template specificando un insieme vuoto di informazioni
    Then si ottiene lo status code 200
    And il purpose template restituito è coerente con le modifiche effettuate
    And il purpose template è stato parzialmente modificato correttamente

  @purpose-template-m2m-patch
  Scenario: [INTEROP-PT-M2M-PATCH_02] Un utente con ruolo M2M NON può effettuare una modifica parziale di un purpose template (Parte2#Scenario intorno a 147)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale del purpose template
    Then si ottiene lo status code 403
    And il purpose template non ha subito modifiche

  @purpose-template-m2m-patch
  Scenario: [INTEROP-PT-M2M-PATCH_03] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un purpose template inesistente (Parte2#Scenario intorno a 148)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale di un purpose template inesistente
    Then si ottiene lo status code 404

  @purpose-template-m2m-patch
  Scenario: [INTEROP-PT-M2M-PATCH_04] Un utente NON può effettuare una modifica parziale di un purpose template indicando un token non valido (Parte2#Scenario intorno a 149)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale del purpose template con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then il purpose template non ha subito modifiche

  @purpose-template-m2m-patch
  Scenario Outline: [INTEROP-PT-M2M-PATCH_05] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un purpose template in stato diverso da DRAFT (Parte2#Scenario intorno a 150)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene correttamente spostato in stato <stato>
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale del purpose template
    Then si ottiene lo status code 409
    And il purpose template non ha subito modifiche
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  @purpose-template-m2m-patch
  Scenario: [INTEROP-PT-M2M-PATCH_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un purpose template che non gli appartiene (Parte2#Scenario intorno a 151)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA2" con ruolo m2m-admin tenta di effettuare la modifica parziale del purpose template
    Then si ottiene lo status code 404
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then il purpose template non ha subito modifiche