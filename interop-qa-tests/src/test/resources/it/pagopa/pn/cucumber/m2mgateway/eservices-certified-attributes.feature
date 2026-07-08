@m2mEservices
@m2m-eservice-certified-attributes
Feature: Gestione degli attributi certificati degli e-services attraverso APIs M2M V2

  # TODO: strategia basata su snapshots - di cui gli step tra parentesi quadre sono perno - da rivedere
  @m2m-v3-204-to-200
  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_ADD_01] Un utente con ruolo M2M-ADMIN può aggiungere degli attributi certificati a una versione di un e-service (Parte2#Scenario intorno a 197)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 4 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And "PA1" porta il descrittore dell'e-service in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    When l'utente tenta di aggiungere gli attributi certificati numeri da 2 a 3 al gruppo dell'e-service
    Then si ottiene http status code 204

    # Verifica che il risultato sia coerente e che non sia stato modificato l'attributo caricato in fase di creazione
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati sono stati aggiunti correttamente al gruppo dell'e-service
    And i precedenti attributi certificati del gruppo dell'e-service sono rimasti invariati
    When l'utente tenta di aggiungere l'attributo certificato numero 4 al gruppo dell'e-service
    Then si ottiene http status code 204

    # Verifica che il risultato sia coerente e che non siano stati modificati gli attributi aggiunti in precedenza
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati sono stati aggiunti correttamente al gruppo dell'e-service
    And i precedenti attributi certificati del gruppo dell'e-service sono rimasti invariati
    Examples:
      | stato       |
      | DRAFT       |
      | PUBLISHED   |
      | SUSPENDED   |
      | DEPRECATED  |

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_ADD_02_A] Un utente con ruolo M2M-ADMIN NON può aggiungere degli attributi certificati a una versione di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 197)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And "PA1" porta il descrittore dell'e-service in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    When l'utente tenta di aggiungere l'attributo certificato numero 2 al gruppo dell'e-service
    Then si ottiene lo status code 400
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati del gruppo dell'e-service sono rimasti invariati

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_ADD_02_B] Un utente con ruolo M2M-ADMIN NON può aggiungere degli attributi certificati a una versione di un e-service in stato ARCHIVED (Parte2#Scenario intorno a 114)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And "PA1" porta il descrittore dell'e-service in stato "ARCHIVED"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    When l'utente tenta di aggiungere l'attributo certificato numero 2 al gruppo dell'e-service
    Then si ottiene lo status code 400
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati del gruppo dell'e-service sono rimasti invariati

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_ADD_03] Un utente con ruolo M2M-ADMIN NON può aggiungere degli attributi certificati a una versione di un e-service che non gli appartiene (Parte2#Scenario intorno a 115)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And "PA1" porta il descrittore dell'e-service in stato "PUBLISHED"
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di aggiungere l'attributo certificato numero 2 al gruppo dell'e-service
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati del gruppo dell'e-service sono rimasti invariati

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_ADD_04] Un utente con ruolo M2M-ADMIN NON può aggiungere degli attributi certificati a una versione di un e-service indicando degli identificativi inesistenti (Parte2#Scenario intorno a 119)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]

    When l'utente tenta di aggiungere l'attributo certificato numero 2 al gruppo dell'e-service indicando un e-service id inesistente
    Then si ottiene lo status code 404
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati del gruppo dell'e-service sono rimasti invariati

    When l'utente tenta di aggiungere l'attributo certificato numero 2 al gruppo dell'e-service indicando un descriptor id inesistente
    Then si ottiene lo status code 404
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati del gruppo dell'e-service sono rimasti invariati

    When l'utente tenta di aggiungere l'attributo certificato numero 2 al gruppo dell'e-service indicando un group index inesistente
    Then si ottiene lo status code 404
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati del gruppo dell'e-service sono rimasti invariati

    When l'utente tenta di aggiungere degli attributi certificati al gruppo dell'e-service indicando degli attribute ids inesistenti
    Then si ottiene lo status code 404
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati del gruppo dell'e-service sono rimasti invariati

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_ADD_05] Un utente NON può aggiungere degli attributi certificati a una versione di un e-service indicando un auth. token non valido (Parte2#Scenario intorno a 120)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di aggiungere l'attributo certificato numero 2 al gruppo dell'e-service
    Then si ottiene lo status code 401
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati del gruppo dell'e-service sono rimasti invariati

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_ADD_06] Un utente con ruolo M2M NON può aggiungere degli attributi certificati a una versione di un e-service (Parte2#Scenario intorno a 121)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di aggiungere l'attributo certificato numero 2 al gruppo dell'e-service
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati del gruppo dell'e-service sono rimasti invariati

  # NOTA 27/10/2025: scenario attualmente non presente in SRS
  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_ADD_07] Un utente con ruolo M2M-ADMIN NON può aggiungere degli attributi NON certificati a una versione di un e-service (Parte2#Scenario intorno a 197)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 1 attributi dichiarati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 0 attributi certificati con successo
    And "PA1" porta il descrittore dell'e-service in stato "PUBLISHED"
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    When l'utente tenta di aggiungere l'attributo dichiarato numero 1 al gruppo dell'e-service
    Then si ottiene lo status code 404
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati del gruppo dell'e-service sono rimasti invariati

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_LIST_01_A] Un utente con ruolo M2M o M2M-ADMIN può leggere gli attributi certificati di una versione di un e-service (Parte2#Scenario intorno a 244)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 2 attributi certificati con successo
    And "PA1" porta il descrittore dell'e-service in stato "<stato>"

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 200
    And gli attributi certificati ottenuti sono coerenti con quelli aggiunti

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 200
    And gli attributi certificati ottenuti sono coerenti con quelli aggiunti

    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 200
    And gli attributi certificati ottenuti sono coerenti con quelli aggiunti
    Examples:
      | stato       |
      | PUBLISHED   |
      | SUSPENDED   |
      | DEPRECATED  |
      | ARCHIVED    |

  @m2m-parte2-ottobre
  @deleghe2
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_LIST_01_B] Un utente con ruolo M2M o M2M-ADMIN può leggere gli attributi certificati di una versione di un e-service in stato WAITING_FOR_APPROVAL solo se appartiene all'ente creatore  (Parte2#Scenario intorno a 244)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 2 attributo certificato con successo
    And "PA1" porta il descrittore dell'e-service in stato WAITING_FOR_APPROVAL usando "PA2" come delegato

    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 200
    And gli attributi certificati ottenuti sono coerenti con quelli aggiunti

    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 200
    And gli attributi certificati ottenuti sono coerenti con quelli aggiunti

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 200
    And gli attributi certificati ottenuti sono coerenti con quelli aggiunti

    Given l'utente è un "admin" di "GSP" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 404

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_LIST_01_C] Un utente con ruolo M2M o M2M-ADMIN può leggere gli attributi certificati di una versione di un e-service in stato DRAFT solo se appartiene all'ente creatore (Parte2#Scenario intorno a 244)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 2 attributi certificati con successo

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 200
    And gli attributi certificati ottenuti sono coerenti con quelli aggiunti

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 200
    And gli attributi certificati ottenuti sono coerenti con quelli aggiunti

    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 404
    And gli attributi certificati ottenuti sono coerenti con quelli aggiunti

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_LIST_02] Un utente con ruolo M2M o M2M-ADMIN NON può leggere gli attributi certificati di una versione di un e-service indicando degli identificativi inesistenti (Parte2#Scenario intorno a 247)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 1 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo

    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service indicando un e-service id inesistente
    Then si ottiene lo status code 404
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service indicando un descriptor id inesistente
    Then si ottiene lo status code 404

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service indicando un e-service id inesistente
    Then si ottiene lo status code 404
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service indicando un descriptor id inesistente
    Then si ottiene lo status code 404

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_LIST_03] Un utente NON può leggere gli attributi certificati di una versione di un e-service indicando un auth. token non valido (Parte2#Scenario intorno a 246)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 1 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service
    Then si ottiene lo status code 401

  @m2m-v3-204-to-200
  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_DELETE_01] Un utente con ruolo M2M-ADMIN può rimuovere gli attributi certificati di una versione di un e-service in stato DRAFT (Parte2#Scenario intorno a 268)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 2 attributi certificati con successo
    When l'utente tenta di rimuovere l'attributo certificato numero 2 dal gruppo dell'e-service
    Then si ottiene http status code 200
    And è stato rimosso dall'e-service solo l'attributo certificato numero 2

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_DELETE_02] Un utente con ruolo M2M NON può rimuovere gli attributi certificati di una versione di un e-service (Parte2#Scenario intorno a 270)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 2 attributi certificati con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di rimuovere l'attributo certificato numero 2 dal gruppo dell'e-service
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin

    # TODO in caso di refactor delle snapshot, andrebbe accorpato con lo step "gli attributi certificati del gruppo dell'e-service sono rimasti invariati"
    And gli attributi certificati del gruppo sono rimasti invariati

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_DELETE_03] Un utente NON può rimuovere gli attributi certificati di una versione di un e-service indicando degli identificativi inesistenti o appartenenti ad attributi già rimossi (Parte2#Scenario intorno a 271, 273)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 2 attributi certificati con successo

    When l'utente tenta di rimuovere l'attributo certificato numero 1 dal gruppo dell'e-service indicando un e-service id inesistente
    Then si ottiene lo status code 404
    And gli attributi certificati del gruppo sono rimasti invariati
    When l'utente tenta di rimuovere l'attributo certificato numero 1 dal gruppo dell'e-service indicando un descriptor id inesistente
    Then si ottiene lo status code 404
    And gli attributi certificati del gruppo sono rimasti invariati
    When l'utente tenta di rimuovere l'attributo certificato numero 1 dal gruppo dell'e-service indicando un group index inesistente
    Then si ottiene lo status code 404
    And gli attributi certificati del gruppo sono rimasti invariati
    When l'utente tenta di rimuovere un attributo certificato dal gruppo dell'e-service indicando un attribute id inesistente
    Then si ottiene lo status code 404
    And gli attributi certificati del gruppo sono rimasti invariati

    # verifica che la rimozione di un attributo già eliminato fallisca
    Given l'utente rimuove l'attributo certificato numero 1 dal gruppo dell'e-service con successo
    When l'utente tenta di rimuovere l'attributo certificato già eliminato numero 1 dal gruppo dell'e-service
    Then si ottiene lo status code 404
    And gli attributi certificati del gruppo sono rimasti invariati

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_DELETE_04] Un utente NON può rimuovere gli attributi certificati di una versione di un e-service indicando un auth. token non valido (Parte2#Scenario intorno a 272)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 1 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di rimuovere l'attributo certificato numero 1 dal gruppo dell'e-service
    Then si ottiene lo status code 401
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And gli attributi certificati del gruppo sono rimasti invariati

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_DELETE_05] Un utente NON può rimuovere gli attributi certificati di una versione di un e-service che non gli appartiene (Parte2#Scenario intorno a 274)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 2 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 2 attributi certificati con successo
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di rimuovere l'attributo certificato numero 2 dal gruppo dell'e-service
    Then si ottiene lo status code 404
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And gli attributi certificati del gruppo sono rimasti invariati

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_DELETE_06_A] Un utente NON può rimuovere gli attributi certificati da una versione di un e-service in stato diverso da DRAFT (Parte2#Scenario intorno a 275)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 1 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributi certificati con successo
    And "PA1" porta il descrittore dell'e-service in stato "<stato>"
    When l'utente tenta di rimuovere l'attributo certificato numero 1 dal gruppo dell'e-service
    Then si ottiene lo status code 400
    And gli attributi certificati del gruppo sono rimasti invariati
    Examples:
      | stato       |
      | PUBLISHED   |
      | SUSPENDED   |
      | DEPRECATED  |
      | ARCHIVED    |

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_DELETE_06_B] Un utente NON può rimuovere gli attributi certificati da una versione di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 275)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente è un "admin" di "PA1"
    And l'utente crea 1 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributi certificati con successo
    And "PA1" porta il descrittore dell'e-service in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di rimuovere l'attributo certificato numero 1 dal gruppo dell'e-service
    Then si ottiene lo status code 400
    And gli attributi certificati del gruppo sono rimasti invariati