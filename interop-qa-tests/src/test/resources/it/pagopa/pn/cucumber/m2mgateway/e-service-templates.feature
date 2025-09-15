@e-service-template-m2m
Feature: Test API M2M of e-service template

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_1] Un utente con ruolo m2m-admin può effettuare la riattivazione di un e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di SUSPENDED
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la riattivazione dell'e-service template
    Then si ottiene status code 200
    And la versione corrente dell'e-service template è in stato PUBLISHED
    Examples:
      | mode        |
      | erogazione  |
      | ricezione   |

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_2] Un utente con ruolo m2m NON può effettuare la riattivazione di un e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di SUSPENDED
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m
    And l'utente tenta di effettuare la riattivazione dell'e-service template
    Then si ottiene status code 403
    And la versione corrente dell'e-service template è in stato SUSPENDED
    Examples:
      | mode        |
      | erogazione  |
      | ricezione   |

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_3] Un utente NON può effettuare la riattivazione di un e-service template indicando degli identificativi inesistenti
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di SUSPENDED
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin

    And l'utente tenta di effettuare la riattivazione di un e-service template inesistente
    Then si ottiene status code 404
    And la versione corrente dell'e-service template è in stato SUSPENDED

    And l'utente tenta di effettuare la riattivazione della versione di une-service template inesistente
    Then si ottiene status code 404
    And la versione corrente dell'e-service template è in stato SUSPENDED
    Examples:
      | mode        |
      | erogazione  |
      | ricezione   |

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_4] Un utente NON può effettuare la riattivazione di un e-service template indicando un auth token non valido
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
      | ricezione   |

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_5] Un utente con ruolo m2m-admin NON può effettuare la riattivazione di un e-service template in stato diverso da SUSPENDED
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
      | ricezione   | DRAFT       | 400   |
      | ricezione   | PUBLISHED   | 409   |
      | ricezione   | DEPRECATED  | 400   |

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_6] Un utente con ruolo m2m-admin NON può effettuare la riattivazione di un e-service template se non appartiene all'ente creatore
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
      | ricezione   |

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
      | ricezione   |

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
      | ricezione   |

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
      | ricezione   |

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
      | PUBLISHED   | ricezione   |
      | DEPRECATED  | ricezione   |
      | SUSPENDED   | ricezione   |

  @m2m-parte2-settembre
  @e-service-template-m2m-patch
  Scenario Outline: [INTEROP-EST-M2M-PATCH_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service template che non gli appartiene (Parte2#Scenario intorno a 151)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale dell'e-service template
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service template non ha subito modifiche
    Examples:
      | mode        |
      | erogazione  |
      | ricezione   |

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
      | ricezione   |

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
      | ricezione   |

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
      | ricezione   |

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
      | PUBLISHED   | ricezione   |
      | DEPRECATED  | ricezione   |
      | SUSPENDED   | ricezione   |

  @m2m-parte2-settembre
  @e-service-template-version-m2m-patch
  Scenario: [INTEROP-EST-VERSION-M2M-PATCH_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una versione di un e-service template che non gli appartiene (Parte2#Scenario intorno a 158)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template
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
      | ricezione   | PUBLISHED |
      | erogazione  | SUSPENDED |
      | ricezione   | SUSPENDED |

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
      | ricezione   | PUBLISHED |
      | erogazione  | SUSPENDED |
      | ricezione   | SUSPENDED |

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
      | ricezione   | PUBLISHED |
      | erogazione  | SUSPENDED |
      | ricezione   | SUSPENDED |

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
      | DRAFT       | ricezione   |
      | DEPRECATED  | ricezione   |

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