@e-service-template-m2m
Feature: Test API M2M of e-service template

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_01] Un utente con ruolo m2m-admin può effettuare la riattivazione di un e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di SUSPENDED
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la riattivazione dell'e-service template
    Then si ottiene status code 200
    And la versione corrente dell'e-service template è in stato PUBLISHED
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   | <-- 30/09/2025 modalità receive non supportata

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_02] Un utente con ruolo m2m NON può effettuare la riattivazione di un e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di SUSPENDED
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di effettuare la riattivazione dell'e-service template
    Then si ottiene status code 403
    And la versione corrente dell'e-service template è in stato SUSPENDED
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   | <-- 30/09/2025 modalità receive non supportata

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_03] Un utente NON può effettuare la riattivazione di un e-service template indicando degli identificativi inesistenti
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di SUSPENDED
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin

    And l'utente tenta di effettuare la riattivazione di un e-service template inesistente
    Then si ottiene status code 404
    And la versione corrente dell'e-service template è in stato SUSPENDED

    And l'utente tenta di effettuare la riattivazione della versione di un e-service template inesistente
    Then si ottiene status code 404
    And la versione corrente dell'e-service template è in stato SUSPENDED
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   | <-- 30/09/2025 modalità receive non supportata

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_04] Un utente NON può effettuare la riattivazione di un e-service template indicando un auth token non valido
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di SUSPENDED
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di effettuare la riattivazione dell'e-service template
    Then si ottiene status code 401

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then la versione corrente dell'e-service template è in stato SUSPENDED

    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   | <-- 30/09/2025 modalità receive non supportata

  # Ticket aperto https://pagopa.atlassian.net/browse/PIN-7827
  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_05] Un utente con ruolo m2m-admin NON può effettuare la riattivazione di un e-service template in stato diverso da SUSPENDED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <state>
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la riattivazione dell'e-service template
    Then si ottiene status code <code>
    And la versione corrente dell'e-service template è in stato <state>
    Examples:
      | mode        | state       | code  |
      | erogazione  | DRAFT       | 400   |
      | erogazione  | PUBLISHED   | 409   |
      | erogazione  | DEPRECATED  | 400   |
    #  | ricezione   | DRAFT       | 400   | <-- 30/09/2025 modalità receive non supportata
    #  | ricezione   | PUBLISHED   | 409   |
    #  | ricezione   | DEPRECATED  | 400   |

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_06] Un utente con ruolo m2m-admin NON può effettuare la riattivazione di un e-service template se non appartiene all'ente creatore
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di SUSPENDED
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la riattivazione dell'e-service template
    Then si ottiene status code 403
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then la versione corrente dell'e-service template è in stato SUSPENDED
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   | <-- 30/09/2025 modalità receive non supportata

  @m2m-parte2-settembre
  @e-service-template-m2m-patch
  Scenario Outline: [INTEROP-EST-M2M-PATCH_01] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale di un e-service template in stato DRAFT (Parte2#Scenario intorno a 145)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale dell'e-service template
    Then si ottiene lo status code 200
    And l'e-service template restituito è coerente con le modifiche effettuate
    And l'e-service template è stato parzialmente modificato correttamente
    When l'utente tenta di effettuare la modifica parziale dell'e-service template specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 200
    And l'e-service template restituito è coerente con le modifiche effettuate
    And l'e-service template è stato parzialmente modificato correttamente
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-m2m-patch
  Scenario Outline: [INTEROP-EST-M2M-PATCH_02] Un utente con ruolo M2M NON può effettuare una modifica parziale di un e-service template (Parte2#Scenario intorno a 147)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale dell'e-service template
    Then si ottiene lo status code 403
    And l'e-service template non ha subito modifiche
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-m2m-patch
  Scenario: [INTEROP-EST-M2M-PATCH_03] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service template inesistente (Parte2#Scenario intorno a 148)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale di un e-service template inesistente
    Then si ottiene lo status code 404

  @m2m-parte2-settembre
  @e-service-template-m2m-patch
  Scenario Outline: [INTEROP-EST-M2M-PATCH_04] Un utente NON può effettuare una modifica parziale di un e-service template indicando un token non valido (Parte2#Scenario intorno a 149)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale dell'e-service template con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service template non ha subito modifiche
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-m2m-patch
  Scenario Outline: [INTEROP-EST-M2M-PATCH_05] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service template in stato diverso da DRAFT (Parte2#Scenario intorno a 150)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <stato>
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale dell'e-service template
    Then si ottiene lo status code 400
    And l'e-service template non ha subito modifiche
    Examples:
      | stato       | mode        |
      | PUBLISHED   | erogazione  |
      | DEPRECATED  | erogazione  |
      | SUSPENDED   | erogazione  |
      #| PUBLISHED   | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati
      #| DEPRECATED  | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati
      #| SUSPENDED   | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-m2m-patch
  Scenario Outline: [INTEROP-EST-M2M-PATCH_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service template che non gli appartiene (Parte2#Scenario intorno a 151)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA2" con ruolo m2m-admin tenta di effettuare la modifica parziale dell'e-service template
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service template non ha subito modifiche
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-version-m2m-patch
  Scenario Outline: [INTEROP-EST-VERSION-M2M-PATCH_01] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale di una versione di un e-service template in stato DRAFT (Parte2#Scenario intorno a 152)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template
    Then si ottiene lo status code 200
    And l'ultima versione dell'e-service template restituita è coerente con le modifiche effettuate
    And l'ultima versione dell'e-service template è stata parzialmente modificata correttamente
    When l'utente tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 200
    And l'ultima versione dell'e-service template restituita è coerente con le modifiche effettuate
    And l'ultima versione dell'e-service template è stata parzialmente modificata correttamente
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-version-m2m-patch
  Scenario Outline: [INTEROP-EST-VERSION-M2M-PATCH_02] Un utente con ruolo M2M NON può effettuare una modifica parziale di una versione di un e-service template (Parte2#Scenario intorno a 154)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template
    Then si ottiene lo status code 403
    And l'ultima versione dell'e-service template non ha subito modifiche
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-version-m2m-patch
  Scenario: [INTEROP-EST-VERSION-M2M-PATCH_03] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una versione di un e-service template inesistente (Parte2#Scenario intorno a 155)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale di una versione inesistente di un e-service template inesistente
    Then si ottiene lo status code 404

  @m2m-parte2-settembre
  @e-service-template-version-m2m-patch
  Scenario Outline: [INTEROP-EST-VERSION-M2M-PATCH_04] Un utente NON può effettuare una modifica parziale di una versione di un e-service template indicando un token non valido (Parte2#Scenario intorno a 156)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'ultima versione dell'e-service template non ha subito modifiche
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-version-m2m-patch
  Scenario Outline: [INTEROP-EST-VERSION-M2M-PATCH_05] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una versione di un e-service template in stato diverso da DRAFT (Parte2#Scenario intorno a 157)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <stato>
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template
    Then si ottiene lo status code 400
    And l'ultima versione dell'e-service template non ha subito modifiche
    Examples:
      | stato       | mode        |
      | PUBLISHED   | erogazione  |
      | DEPRECATED  | erogazione  |
      | SUSPENDED   | erogazione  |
      #| PUBLISHED   | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati
      #| DEPRECATED  | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati
      #| SUSPENDED   | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-version-m2m-patch
  Scenario: [INTEROP-EST-VERSION-M2M-PATCH_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una versione di un e-service template che non gli appartiene (Parte2#Scenario intorno a 158)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA2" con ruolo m2m-admin tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'ultima versione dell'e-service template non ha subito modifiche

  @m2m-parte2-settembre
  @e-service-template-version-quota-m2m-patch
  Scenario Outline: [INTEROP-EST-VERSION-QUOTAS-M2M-PATCH_01] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale delle quote una versione di un e-service template in stato DRAFT (Parte2#Scenario intorno a 180)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <state>
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote dell'ultima versione dell'e-service template
    Then si ottiene lo status code 200
    And l'ultima versione dell'e-service template restituita è coerente con le modifiche effettuate
    And l'ultima versione dell'e-service template è stata parzialmente modificata correttamente
    When l'utente tenta di effettuare la modifica parziale delle quote dell'ultima versione dell'e-service template specificando un sottoinsieme di informazioni
    Then si ottiene lo status code 200
    And l'ultima versione dell'e-service template restituita è coerente con le modifiche effettuate
    And l'ultima versione dell'e-service template è stata parzialmente modificata correttamente
    Examples:
      | mode        | state     |
      | erogazione  | PUBLISHED |
      #| ricezione   | PUBLISHED |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati
      | erogazione  | SUSPENDED |
      #| ricezione   | SUSPENDED |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-version-quota-m2m-patch
  Scenario Outline: [INTEROP-EST-VERSION-QUOTAS-M2M-PATCH_02] Un utente con ruolo M2M NON può effettuare una modifica parziale delle quote una versione di un e-service template (Parte2#Scenario intorno a 182)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <state>
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale delle quote dell'ultima versione dell'e-service template
    Then si ottiene lo status code 403
    And l'ultima versione dell'e-service template non ha subito modifiche
    Examples:
      | mode        | state     |
      | erogazione  | PUBLISHED |
      #| ricezione   | PUBLISHED |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati
      | erogazione  | SUSPENDED |
      #| ricezione   | SUSPENDED |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-version-quota-m2m-patch
  Scenario: [INTEROP-EST-VERSION-QUOTAS-M2M-PATCH_03] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote una versione di un e-service template inesistente (Parte2#Scenario intorno a 183)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote di una versione inesistente di un e-service template inesistente
    Then si ottiene lo status code 404

  @m2m-parte2-settembre
  @e-service-template-version-quota-m2m-patch
  Scenario Outline: [INTEROP-EST-VERSION-QUOTAS-M2M-PATCH_04] Un utente NON può effettuare una modifica parziale delle quote una versione di un e-service template indicando un token non valido (Parte2#Scenario intorno a 184)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <state>
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale delle quote dell'ultima versione dell'e-service template con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'ultima versione dell'e-service template non ha subito modifiche
    Examples:
      | mode        | state     |
      | erogazione  | PUBLISHED |
      #| ricezione   | PUBLISHED |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati
      | erogazione  | SUSPENDED |
      #| ricezione   | SUSPENDED |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-version-quota-m2m-patch
  Scenario Outline: [INTEROP-EST-VERSION-QUOTAS-M2M-PATCH_05] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote una versione di un e-service template in stato diverso da PUBLISHED o SUSPENDED (Parte2#Scenario intorno a 185)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <stato>
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote dell'ultima versione dell'e-service template
    Then si ottiene lo status code 400
    And l'ultima versione dell'e-service template non ha subito modifiche
    Examples:
      | stato       | mode        |
      | DRAFT       | erogazione  |
      | DEPRECATED  | erogazione  |
      #| DRAFT       | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati
      #| DEPRECATED  | ricezione   |  <-- 22/09/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-settembre
  @e-service-template-version-quota-m2m-patch
  Scenario: [INTEROP-EST-VERSION-QUOTAS-M2M-PATCH_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote una versione di un e-service template che non gli appartiene (Parte2#Scenario intorno a 186)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale delle quote dell'ultima versione dell'e-service template
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'ultima versione dell'e-service template non ha subito modifiche

  @m2m-parte2-ottobre
  @e-service-template-m2m-delete
  Scenario Outline: [INTEROP-EST-M2M-DELETE_01] Un utente con ruolo M2M-ADMIN può effettuare la cancellazione di un e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene lo status code 204
    And l'e-service template non esiste più

    # si verifica che il tentativo di eliminarlo nuovamente si concluda negativamente
    When l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene lo status code 404

    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione  | <-- 10/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-ottobre
  @e-service-template-m2m-delete
  Scenario Outline: [INTEROP-EST-M2M-DELETE_02] Un utente con ruolo M2M NON può effettuare la cancellazione di un e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene lo status code 403
    And l'e-service template esiste ancora
    Examples:
      | mode        |
      | erogazione  |
    #  | ricezione  | <-- 10/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-ottobre
  @e-service-template-m2m-delete
  Scenario: [INTEROP-EST-M2M-DELETE_03] Un utente NON può effettuare la cancellazione di un e-service template indicando un auth. token non valido
    Given viene impostato per l'utente un token m2m non valido
    And l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene lo status code 401

  @m2m-parte2-ottobre
  @e-service-template-m2m-delete
  Scenario: [INTEROP-EST-M2M-DELETE_04] Un utente con ruolo M2M-ADMIN può effettuare la cancellazione di un e-service template
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la cancellazione di un e-service template inesistente
    Then si ottiene lo status code 404

  @m2m-parte2-ottobre
  @e-service-template-m2m-delete
  Scenario: [INTEROP-EST-M2M-DELETE_05] Un utente con ruolo M2M-ADMIN può effettuare la cancellazione di un e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene lo status code 404
    And l'e-service template esiste ancora

  @m2m-parte2-ottobre
  @e-service-template-m2m-delete
  Scenario Outline: [INTEROP-EST-M2M-DELETE_07] Un utente con ruolo M2M-ADMIN NON può effettuare la cancellazione di un e-service template in stato non-DRAFT
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <state>
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene lo status code 403
    And l'e-service template esiste ancora
    Examples:
      | mode        | state       |
      | erogazione  | PUBLISHED   |
      | erogazione  | SUSPENDED   |
      | erogazione  | DEPRECATED  |
    #  | ricezione  | PUBLISHED   | <-- 10/2025 e-service template in mod. receive non ancora supportati
    #  | ricezione  | SUSPENDED   | <-- 10/2025 e-service template in mod. receive non ancora supportati
    #  | ricezione  | DEPRECATED  | <-- 10/2025 e-service template in mod. receive non ancora supportati

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create
  Scenario Outline: [INTEROP-EST-M2M-VERSION-CREATE_01] Un utente con ruolo M2M-ADMIN può effettuare la creazione di una versione di un e-service template in stato PUBLISHED o SUSPENDED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota del vecchio stato delle versioni dell'e-service template]
    And l'utente m2m tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 201
    And la nuova versione dell'e-service template è stata restituita correttamente
    And [si prende nota del nuovo stato delle versioni dell'e-service template]
    And l'ultima versione dell'e-service template è stata creata correttamente
    And la versione 1 dell'e-service template non ha subito modifiche
    And le versioni dell'e-service template sono un totale di 2
    Examples:
      | stato       |
      | PUBLISHED   |
      | SUSPENDED   |
      | DEPRECATED  |
    # TODO non certa la legittimità di "DEPRECATED", verificare

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create
  Scenario Outline: [INTEROP-EST-M2M-VERSION-CREATE_02] Un utente con ruolo M2M NON può effettuare la creazione di una versione di un e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And [si prende nota del vecchio stato delle versioni dell'e-service template]
    And l'utente m2m tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 403
    And [si prende nota del nuovo stato delle versioni dell'e-service template]
    And la versione 1 dell'e-service template non ha subito modifiche
    And le versioni dell'e-service template sono un totale di 1
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create
  Scenario: [INTEROP-EST-M2M-VERSION-CREATE_03] Un utente NON può effettuare la creazione di una versione di un e-service template in stato DRAFT
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente m2m tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 409

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create
  Scenario Outline: [INTEROP-EST-M2M-VERSION-CREATE_04] Un utente NON può effettuare la creazione di una versione di un e-service template che non gli appartiene
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    When l'utente è un "admin" di "PA2"
    And l'utente m2m tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create
  Scenario: [INTEROP-EST-M2M-VERSION-CREATE_05] Un utente NON può effettuare la creazione di una nuova versione di un e-service template inesistente
    Given l'utente è un "admin" di "PA1"
    When l'utente m2m tenta la creazione di una ulteriore versione di un e-service template inesistente
    Then si ottiene response status code 404

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create
  Scenario: [INTEROP-EST-M2M-VERSION-CREATE_06] Un utente NON può effettuare la creazione di una nuova versione di un e-service template specificando un auth. token non valido
    Given viene impostato per l'utente un token m2m non valido
    When l'utente m2m tenta la creazione di una ulteriore versione di un e-service template inesistente
    Then si ottiene response status code 401



  # TODO adeguare i test di quest'ultima POST agli scenari di Stefano Netti