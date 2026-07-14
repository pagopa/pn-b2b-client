@m2mEservices
@m2m-eservice-template-verified-attributes
Feature: Gestione degli attributi verificati degli e-service templates templates attraverso APIs M2M V2

  # TODO aggiornare gli "Scenario intorno a ..." di questo e di tutti i file feature
  #   del rilascio di ottobre, escluso eservices-verified-attributes.feature

  @m2m-v3-204-to-200
  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_ADD_01] Un utente con ruolo M2M-ADMIN può aggiungere degli attributi verificati a una versione di un e-service template (Parte2#Scenario intorno a 197)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 4 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 1 attributo verificato con successo
    And "PA1" porta la versione dell'e-service template in stato <stato>
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    When l'utente tenta di aggiungere gli attributi verificati numeri da 2 a 3 al gruppo dell'e-service template
    Then si ottiene http status code 204

    # Verifica che il risultato sia coerente e che non sia stato modificato l'attributo caricato in fase di creazione
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And gli attributi verificati sono stati aggiunti correttamente al gruppo dell'e-service template
    And i precedenti attributi verificati del gruppo dell'e-service template sono rimasti invariati
    When l'utente tenta di aggiungere l'attributo verificato numero 4 al gruppo dell'e-service template
    Then si ottiene http status code 204

    # Verifica che il risultato sia coerente e che non siano stati modificati gli attributi aggiunti in precedenza
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And gli attributi verificati sono stati aggiunti correttamente al gruppo dell'e-service template
    And i precedenti attributi verificati del gruppo dell'e-service template sono rimasti invariati
    Examples:
      | mode        | stato       |
      | erogazione  | DRAFT       |
      | erogazione  | PUBLISHED   |
      | erogazione  | SUSPENDED   |
      | erogazione  | DEPRECATED  |

    @e-service-template-receive-m2m
    Examples:
      | mode        | stato       |
      | ricezione  | DRAFT       |
      | ricezione  | PUBLISHED   |
      | ricezione  | SUSPENDED   |
      | ricezione  | DEPRECATED  |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_ADD_03] Un utente con ruolo M2M-ADMIN NON può aggiungere degli attributi verificati a una versione di un e-service template che non gli appartiene (Parte2#Scenario intorno a 115)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 2 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 1 attributo verificato con successo
    And "PA1" porta la versione dell'e-service template in stato PUBLISHED
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di aggiungere l'attributo verificato numero 2 al gruppo dell'e-service template
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And gli attributi verificati del gruppo dell'e-service template sono rimasti invariati
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_ADD_04] Un utente con ruolo M2M-ADMIN NON può aggiungere degli attributi verificati a una versione di un e-service template indicando degli identificativi inesistenti (Parte2#Scenario intorno a 119)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 2 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 1 attributo verificato con successo
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]

    When l'utente tenta di aggiungere l'attributo verificato numero 2 al gruppo dell'e-service template indicando un e-service template id inesistente
    Then si ottiene lo status code 404
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And gli attributi verificati del gruppo dell'e-service template sono rimasti invariati

    When l'utente tenta di aggiungere l'attributo verificato numero 2 al gruppo dell'e-service template indicando un descriptor id inesistente
    Then si ottiene lo status code 404
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And gli attributi verificati del gruppo dell'e-service template sono rimasti invariati

    When l'utente tenta di aggiungere l'attributo verificato numero 2 al gruppo dell'e-service template indicando un group index inesistente
    Then si ottiene lo status code 404
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And gli attributi verificati del gruppo dell'e-service template sono rimasti invariati

    When l'utente tenta di aggiungere degli attributi verificati al gruppo dell'e-service template indicando degli attribute ids inesistenti
    Then si ottiene lo status code 404
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And gli attributi verificati del gruppo dell'e-service template sono rimasti invariati
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_ADD_05] Un utente NON può aggiungere degli attributi verificati a una versione di un e-service template indicando un auth. token non valido (Parte2#Scenario intorno a 120)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 2 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 1 attributo verificato con successo
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di aggiungere l'attributo verificato numero 2 al gruppo dell'e-service template
    Then si ottiene lo status code 401
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And gli attributi verificati del gruppo dell'e-service template sono rimasti invariati
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_ADD_06] Un utente con ruolo M2M NON può aggiungere degli attributi verificati a una versione di un e-service template (Parte2#Scenario intorno a 121)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 2 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 1 attributo verificato con successo
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di aggiungere l'attributo verificato numero 2 al gruppo dell'e-service template
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And gli attributi verificati del gruppo dell'e-service template sono rimasti invariati
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  # NOTA 27/10/2025: scenario attualmente non presente in SRS
  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_ADD_07] Un utente con ruolo M2M-ADMIN NON può aggiungere degli attributi NON verificati a una versione di un e-service template (Parte2#Scenario intorno a 197)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 1 attributi dichiarati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 0 attributi verificati con successo
    And "PA1" porta la versione dell'e-service template in stato PUBLISHED
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    When l'utente tenta di aggiungere l'attributo dichiarato numero 1 al gruppo dell'e-service template
    Then si ottiene lo status code 404
    And [si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]
    And gli attributi verificati del gruppo dell'e-service template sono rimasti invariati
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_LIST_01_A] Un utente con ruolo M2M o M2M-ADMIN può leggere gli attributi verificati di una versione di un e-service template (Parte2#Scenario intorno a 244)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 2 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 2 attributi verificati con successo
    And "PA1" porta la versione dell'e-service template in stato <stato>

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template
    Then si ottiene lo status code 200
    And gli attributi verificati ottenuti sono coerenti con quelli aggiunti nel template

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template
    Then si ottiene lo status code 200
    And gli attributi verificati ottenuti sono coerenti con quelli aggiunti nel template

    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template
    Then si ottiene lo status code 200
    And gli attributi verificati ottenuti sono coerenti con quelli aggiunti nel template
    Examples:
      | mode        | stato       |
      | erogazione  | PUBLISHED   |
      | erogazione  | SUSPENDED   |
      | erogazione  | DEPRECATED  |

    @e-service-template-receive-m2m
    Examples:
      | mode        | stato       |
      | ricezione  | PUBLISHED   |
      | ricezione  | SUSPENDED   |
      | ricezione  | DEPRECATED  |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_LIST_01_B] Un utente con ruolo M2M o M2M-ADMIN può leggere gli attributi verificati di una versione di un e-service template in stato DRAFT solo se appartiene all'ente creatore (Parte2#Scenario intorno a 244)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 2 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 2 attributi verificati con successo

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template
    Then si ottiene lo status code 200
    And gli attributi verificati ottenuti sono coerenti con quelli aggiunti nel template

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template
    Then si ottiene lo status code 200
    And gli attributi verificati ottenuti sono coerenti con quelli aggiunti nel template

    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template
    Then si ottiene lo status code 404

    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_LIST_02] Un utente con ruolo M2M o M2M-ADMIN NON può leggere gli attributi verificati di una versione di un e-service template indicando degli identificativi inesistenti (Parte2#Scenario intorno a 247)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 1 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 1 attributo verificato con successo

    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template indicando un e-service template id inesistente
    Then si ottiene lo status code 404
    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template indicando un descriptor id inesistente
    Then si ottiene lo status code 404

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template indicando un e-service template id inesistente
    Then si ottiene lo status code 404
    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template indicando un descriptor id inesistente
    Then si ottiene lo status code 404
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_LIST_03] Un utente NON può leggere gli attributi verificati di una versione di un e-service template indicando un auth. token non valido (Parte2#Scenario intorno a 246)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 1 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 1 attributo verificato con successo
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template
    Then si ottiene lo status code 401
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

    # TODO edit/aggiunta scenari di POST: test con secondo gruppo

  @m2m-v3-204-to-200
  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_DELETE_01] Un utente con ruolo M2M-ADMIN può rimuovere gli attributi verificati di una versione di un e-service template in stato DRAFT (Parte2#Scenario intorno a 268)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 2 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 2 attributi verificati con successo
    When l'utente tenta di rimuovere l'attributo verificato numero 2 dal gruppo dell'e-service template
    Then si ottiene http status code 200
    And è stato rimosso dall'e-service template solo l'attributo verificato numero 2
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_DELETE_02] Un utente con ruolo M2M NON può rimuovere gli attributi verificati di una versione di un e-service template (Parte2#Scenario intorno a 270)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 2 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 2 attributi verificati con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di rimuovere l'attributo verificato numero 2 dal gruppo dell'e-service template
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin

    # TODO in caso di refactor delle snapshot, andrebbe accorpato con lo step "gli attributi verificati del gruppo dell'e-service template sono rimasti invariati"
    And gli attributi verificati del gruppo nel template sono rimasti invariati
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_DELETE_03] Un utente NON può rimuovere gli attributi verificati di una versione di un e-service template indicando degli identificativi inesistenti o appartenenti ad attributi già rimossi (Parte2#Scenario intorno a 271, 273)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 2 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 2 attributo verificato con successo

    When l'utente tenta di rimuovere l'attributo verificato numero 1 dal gruppo dell'e-service template indicando un e-service template id inesistente
    Then si ottiene lo status code 404
    And gli attributi verificati del gruppo nel template sono rimasti invariati
    When l'utente tenta di rimuovere l'attributo verificato numero 1 dal gruppo dell'e-service template indicando un descriptor id inesistente
    Then si ottiene lo status code 404
    And gli attributi verificati del gruppo nel template sono rimasti invariati
    When l'utente tenta di rimuovere l'attributo verificato numero 1 dal gruppo dell'e-service template indicando un group index inesistente
    Then si ottiene lo status code 404
    And gli attributi verificati del gruppo nel template sono rimasti invariati
    When l'utente tenta di rimuovere un attributo verificato dal gruppo dell'e-service template indicando un attribute id inesistente
    Then si ottiene lo status code 404
    And gli attributi verificati del gruppo nel template sono rimasti invariati

    # verifica che la rimozione di un attributo già eliminato fallisca
    Given l'utente rimuove l'attributo verificato numero 1 dal gruppo dell'e-service template con successo
    When l'utente tenta di rimuovere l'attributo verificato già eliminato numero 1 dal gruppo dell'e-service template
    Then si ottiene lo status code 404
    And gli attributi verificati del gruppo nel template sono rimasti invariati
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_DELETE_04] Un utente NON può rimuovere gli attributi verificati di una versione di un e-service template indicando un auth. token non valido (Parte2#Scenario intorno a 272)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 1 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 1 attributo verificato con successo
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di rimuovere l'attributo verificato numero 1 dal gruppo dell'e-service template
    Then si ottiene lo status code 401
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And gli attributi verificati del gruppo nel template sono rimasti invariati
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_DELETE_05] Un utente NON può rimuovere gli attributi verificati di una versione di un e-service template che non gli appartiene (Parte2#Scenario intorno a 274)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 2 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 2 attributi verificati con successo
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di rimuovere l'attributo verificato numero 2 dal gruppo dell'e-service template
    Then si ottiene lo status code 404
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And gli attributi verificati del gruppo nel template sono rimasti invariati
    Examples:
      | mode        |
      | erogazione  |

    @e-service-template-receive-m2m
    Examples:
      | mode        |
      | ricezione   |

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ES_TEMPLATES_VERIFIED_ATTRIBUTES_DELETE_06] Un utente NON può rimuovere gli attributi verificati da una versione di un e-service template in stato diverso da DRAFT (Parte2#Scenario intorno a 275)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    And l'utente crea 1 attributi verificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi associati all'e-service template contenente 1 attributi verificati con successo
    And "PA1" porta la versione dell'e-service template in stato <stato>
    When l'utente tenta di rimuovere l'attributo verificato numero 1 dal gruppo dell'e-service template
    Then si ottiene lo status code 400
    And gli attributi verificati del gruppo nel template sono rimasti invariati
    Examples:
      | mode        | stato       |
      | erogazione  | PUBLISHED   |
      | erogazione  | SUSPENDED   |
      | erogazione  | DEPRECATED  |

    @e-service-template-receive-m2m
    Examples:
      | mode        | stato       |
      | ricezione  | PUBLISHED   |
      | ricezione  | SUSPENDED   |
      | ricezione  | DEPRECATED  |