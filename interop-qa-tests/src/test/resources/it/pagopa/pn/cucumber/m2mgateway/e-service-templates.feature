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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_04] Un utente NON può effettuare la riattivazione di un e-service template indicando un auth token non valido
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di SUSPENDED
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene impostato per l'utente un token m2m non valido
    And l'utente tenta di effettuare la riattivazione dell'e-service template
    Then si ottiene status code 401

    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then la versione corrente dell'e-service template è in stato SUSPENDED

    Examples:
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-parte2-settembre
  @e-service-template-m2m-unsuspend @ko-nrt-08072026
  Scenario Outline: [INTEROP-EST-M2M-UNSUSPEND_05] Un utente con ruolo m2m-admin NON può effettuare la riattivazione di un e-service template in stato diverso da SUSPENDED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <state>
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la riattivazione dell'e-service template
    Then si ottiene status code <code>
    And la versione corrente dell'e-service template è in stato <state>
    Examples:
      | mode       | state      | code |
      | erogazione | DRAFT      | 409  |
      | erogazione | PUBLISHED  | 409  |
      | erogazione | DEPRECATED | 409  |

    @e-service-template-receive-m2m
    Examples:
      | mode      | state      | code |
      | ricezione | DRAFT      | 409  |
      | ricezione | PUBLISHED  | 409  |
      | ricezione | DEPRECATED | 409  |

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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-patch
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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-patch
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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-parte2-settembre
  @e-service-template-m2m-patch
  Scenario: [INTEROP-EST-M2M-PATCH_03] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di un e-service template inesistente (Parte2#Scenario intorno a 148)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale di un e-service template inesistente
    Then si ottiene lo status code 404

  @m2m-patch
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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-patch
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
      | stato      | mode       |
      | PUBLISHED  | erogazione |
      | DEPRECATED | erogazione |
      | SUSPENDED  | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | stato      | mode      |
      | PUBLISHED  | ricezione |
      | DEPRECATED | ricezione |
      | SUSPENDED  | ricezione |

  @m2m-patch
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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-patch
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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-patch
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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-parte2-settembre
  @e-service-template-version-m2m-patch
  Scenario: [INTEROP-EST-VERSION-M2M-PATCH_03] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una versione di un e-service template inesistente (Parte2#Scenario intorno a 155)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale di una versione inesistente di un e-service template inesistente
    Then si ottiene lo status code 404

  @m2m-patch
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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-patch
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
      | stato      | mode       |
      | PUBLISHED  | erogazione |
      | DEPRECATED | erogazione |
      | SUSPENDED  | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | stato      | mode      |
      | PUBLISHED  | ricezione |
      | DEPRECATED | ricezione |
      | SUSPENDED  | ricezione |

  @m2m-patch
  @m2m-parte2-settembre
  @e-service-template-version-m2m-patch @ko-nrt-08072026
  Scenario: [INTEROP-EST-VERSION-M2M-PATCH_06] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale di una versione di un e-service template che non gli appartiene (Parte2#Scenario intorno a 158)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA2" con ruolo m2m-admin tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template
    Then si ottiene lo status code 403
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'ultima versione dell'e-service template non ha subito modifiche

  @m2m-patch
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
      | mode       | state     |
      | erogazione | PUBLISHED |
      | erogazione | SUSPENDED |

    @e-service-template-receive-m2m
    Examples:
      | mode      | state     |
      | ricezione | PUBLISHED |
      | ricezione | SUSPENDED |

  @m2m-patch
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
      | mode       | state     |
      | erogazione | PUBLISHED |
      | erogazione | SUSPENDED |

    @e-service-template-receive-m2m
    Examples:
      | mode      | state     |
      | ricezione | PUBLISHED |
      | ricezione | SUSPENDED |

  @m2m-parte2-settembre
  @e-service-template-version-quota-m2m-patch
  Scenario: [INTEROP-EST-VERSION-QUOTAS-M2M-PATCH_03] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale delle quote una versione di un e-service template inesistente (Parte2#Scenario intorno a 183)
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale delle quote di una versione inesistente di un e-service template inesistente
    Then si ottiene lo status code 404

  @m2m-patch
  @m2m-parte2-settembre
  @e-service-template-version-quota-m2m-patch @ko-nrt-08072026
  Scenario Outline: [INTEROP-EST-VERSION-QUOTAS-M2M-PATCH_04] Un utente NON può effettuare una modifica parziale delle quote una versione di un e-service template indicando un token non valido (Parte2#Scenario intorno a 184)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <state>
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale delle quote dell'ultima versione dell'e-service template con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'ultima versione dell'e-service template non ha subito modifiche
    Examples:
      | mode       | state     |
      | erogazione | PUBLISHED |
      | erogazione | SUSPENDED |

    @e-service-template-receive-m2m
    Examples:
      | mode      | state     |
      | ricezione | PUBLISHED |
      | ricezione | SUSPENDED |

  @m2m-patch
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
      | stato      | mode       |
      | DRAFT      | erogazione |
      | DEPRECATED | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | stato      | mode      |
      | DRAFT      | ricezione |
      | DEPRECATED | ricezione |

  @m2m-patch
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

  @m2m-v3-204-to-200
  @m2m-parte2-ottobre
  @e-service-template-m2m-delete
  Scenario Outline: [INTEROP-EST-M2M-DELETE_01] Un utente con ruolo M2M-ADMIN può effettuare la cancellazione di un e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di DRAFT
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene http status code 204
    And l'e-service template non esiste più

    # si verifica che il tentativo di eliminarlo nuovamente si concluda negativamente
    When l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene lo status code 404

    Examples:
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

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
      | mode       |
      | erogazione |

    @e-service-template-receive-m2m
    Examples:
      | mode      |
      | ricezione |

  @m2m-parte2-ottobre
  @e-service-template-m2m-delete
  Scenario: [INTEROP-EST-M2M-DELETE_03] Un utente NON può effettuare la cancellazione di un e-service template indicando un auth. token non valido
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente tenta di effettuare la cancellazione di un e-service template inesistente
    Then si ottiene lo status code 401

  @m2m-parte2-ottobre
  @e-service-template-m2m-delete
  Scenario: [INTEROP-EST-M2M-DELETE_04] Un utente con ruolo M2M-ADMIN NON può effettuare la cancellazione di un e-service template inesistente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la cancellazione di un e-service template inesistente
    Then si ottiene lo status code 404

  # Ticket aperto: https://pagopa.atlassian.net/browse/PIN-8052
  @m2m-parte2-ottobre
  @e-service-template-m2m-delete @ko-nrt-08072026
  Scenario: [INTEROP-EST-M2M-DELETE_05_A] Un utente con ruolo M2M-ADMIN NON può effettuare la cancellazione di un e-service template in stato DRAFT che non gli appartiene
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene lo status code 404
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'e-service template esiste ancora

  @m2m-parte2-ottobre
  @e-service-template-m2m-delete
  Scenario Outline: [INTEROP-EST-M2M-DELETE_05_B] Un utente con ruolo M2M-ADMIN NON può effettuare la cancellazione di un e-service template in stato non-DRAFT che non gli appartiene
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <state>
    When l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene lo status code 403
    # non si torna all'utente PA1 perché PA2 DEVE poter visualizzare il template di PA1
    And l'e-service template esiste ancora
    Examples:
      | mode       | state      |
      | erogazione | PUBLISHED  |
      | erogazione | SUSPENDED  |
      | erogazione | DEPRECATED |

  @m2m-parte2-ottobre
  @e-service-template-m2m-delete @ko-nrt-08072026
  Scenario Outline: [INTEROP-EST-M2M-DELETE_06] Un utente con ruolo M2M-ADMIN NON può effettuare la cancellazione di un e-service template in stato non-DRAFT
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di <state>
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la cancellazione dell'e-service template
    Then si ottiene lo status code 409
    And l'e-service template esiste ancora
    Examples:
      | mode       | state      |
      | erogazione | PUBLISHED  |
      | erogazione | SUSPENDED  |
      | erogazione | DEPRECATED |

    @e-service-template-receive-m2m
    Examples:
      | mode      | state      |
      | ricezione | PUBLISHED  |
      | ricezione | SUSPENDED  |
      | ricezione | DEPRECATED |

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create
  Scenario Outline: [INTEROP-EST-M2M-VERSION-CREATE_01_A] Un utente con ruolo M2M-ADMIN può effettuare la creazione di una versione di un e-service template la cui precedente versione è in un qualsiasi stato diverso da DRAFT
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
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create
  Scenario: [INTEROP-EST-M2M-VERSION-CREATE_01_B] Un utente con ruolo M2M-ADMIN può effettuare la creazione di una versione di un e-service template in cui una precedente versione è in stato DEPRECATED
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DEPRECATED
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota del vecchio stato delle versioni dell'e-service template]
    And l'utente m2m tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 201
    And la nuova versione dell'e-service template è stata restituita correttamente
    And [si prende nota del nuovo stato delle versioni dell'e-service template]
    And l'ultima versione dell'e-service template è stata creata correttamente
    And la versione 1 dell'e-service template non ha subito modifiche
    And la versione 2 dell'e-service template non ha subito modifiche
    And le versioni dell'e-service template sono un totale di 3
      # 3 perché l'avere una versione in stato DEPRECATED ha implicato la pubblicazione di una seconda versione già all'inizio

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
  Scenario: [INTEROP-EST-M2M-VERSION-CREATE_03] Un utente NON può effettuare la creazione di una versione di un e-service template la cui precedente versione è in stato DRAFT
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 409

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create @ko-nrt-08072026
  Scenario Outline: [INTEROP-EST-M2M-VERSION-CREATE_04] Un utente NON può effettuare la creazione di una versione di un e-service template che non gli appartiene
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di <stato>
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente m2m tenta la creazione di una ulteriore versione nell'e-service template
    Then si ottiene response status code 403
    Examples:
      | stato     |
      | PUBLISHED |
      | SUSPENDED |

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create
  Scenario: [INTEROP-EST-M2M-VERSION-CREATE_05] Un utente NON può effettuare la creazione di una nuova versione di un e-service template inesistente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m tenta la creazione di una ulteriore versione di un e-service template inesistente
    Then si ottiene response status code 404

  @m2m-parte2-ottobre
  @e-service-template-m2m-version-create
  Scenario: [INTEROP-EST-M2M-VERSION-CREATE_06] Un utente NON può effettuare la creazione di una nuova versione di un e-service template specificando un auth. token non valido
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene impostato per l'utente un token m2m non valido
    When l'utente m2m tenta la creazione di una ulteriore versione di un e-service template inesistente
    Then si ottiene response status code 401

  @happy-path
  @e-service-template-m2m-version-get
  Scenario Outline: [M2MG_ESERVICETEMPLATES_1] Recupero corretto delle versioni di un template e-service con utente autorizzato (Scenario 17)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M <role>
    And l'utente tenta di recuperare le versioni dell'e-service template
    Then si ottiene lo status code 200
    And le versioni dell'e-service template sono un totale di 2
    Examples:
      | role      |
      | m2m       |
      | m2m-admin |

  @sad-path
  @e-service-template-m2m-version-get
  Scenario: [M2MG_ESERVICETEMPLATES_4] Errore nel recupero delle versioni di un template e-service con templateId inesistente (Scenario 179)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di recuperare le versioni dell'e-service template indicando un template id inesistente
    Then si ottiene lo status code 404

  @sad-path
  @e-service-template-m2m-version-get
  Scenario: [M2MG_ESERVICETEMPLATES_5] Accesso negato al recupero delle versioni di un template e-service con token non valido (Scenario 180)
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo
    When viene impostato per l'utente un token m2m non valido
    When l'utente tenta di recuperare le versioni dell'e-service template indicando un template id inesistente
    Then si ottiene lo status code 401

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @happy-path @ko-nrt-08072026
  Scenario Outline: [M2M_ESERVICE_TEMPLATE_INSTANCE_PUBLISHED_UPDATE_DELEGATION_1] Un utente con ruolo M2M-ADMIN può effettuare una modifica parziale della delega di un e-service template instance precedentemente creato in uno degli stati permessi
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <eServiceDescriptorState> partire dal template e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 200
    And l'e-service restituito è coerente con le modifiche effettuate
    And l'e-service è stato parzialmente modificato correttamente
    Examples:
      | eServiceDescriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED               | true                | true                    |
      | PUBLISHED               | false               | false                   |
      | PUBLISHED               | true                | false                   |
      | PUBLISHED               | true                | %null                   |
      | PUBLISHED               | false               | %null                   |
      | PUBLISHED               | %null               | false                   |

      | SUSPENDED               | true                | true                    |
      | SUSPENDED               | false               | false                   |
      | SUSPENDED               | true                | false                   |
      | SUSPENDED               | true                | %null                   |
      | SUSPENDED               | false               | %null                   |
      | SUSPENDED               | %null               | false                   |

      | DEPRECATED              | true                | true                    |
      | DEPRECATED              | false               | false                   |
      | DEPRECATED              | true                | false                   |
      | DEPRECATED              | true                | %null                   |
      | DEPRECATED              | false               | %null                   |
      | DEPRECATED              | %null               | false                   |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path @ko-nrt-08072026
  Scenario Outline: [M2M_ESERVICE_TEMPLATE_INSTANCE_PUBLISHED_UPDATE_DELEGATION_2] Un utente con ruolo M2M-ADMIN NON può modificare le flag di delega tecnica di un e-service template instance precedentemente creato ottenendo uno stato non permesso
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <eServiceDescriptorState> partire dal template e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "<isConsumerDelegable>" e quella tecnica a "<isClientAccessDelegable>"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | eServiceDescriptorState | isConsumerDelegable | isClientAccessDelegable |
      | PUBLISHED               | false               | true                    |
      | SUSPENDED               | false               | true                    |
      | DEPRECATED              | false               | true                    |
      #considerando che lo stato delle flag alla creazione dell'e-service è isConsumerDelegable=false e isClientAccessDelegable=false
      | PUBLISHED               | %null               | true                    |
      | SUSPENDED               | %null               | true                    |
      | DEPRECATED              | %null               | true                    |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_TEMPLATE_INSTANCE_PUBLISHED_UPDATE_DELEGATION_3] Un utente con ruolo M2M-ADMIN NON può modificare le flag di delega amministrativa di un e-service template instance precedentemente creato ottenendo uno stato non permesso
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <eServiceDescriptorState> partire dal template e impostando delega amministrativa a "true" e delega tecnica a "true"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "false" e quella tecnica a "%null"
    Then si ottiene lo status code 400
    And l'e-service non ha subito modifiche
    Examples:
      | eServiceDescriptorState |
      #considerando che lo stato delle flag alla creazione dell'e-service è isConsumerDelegable=true e isClientAccessDelegable=true
      | PUBLISHED               |
      | SUSPENDED               |
      | DEPRECATED              |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path @ko-nrt-08072026
  Scenario Outline: [M2M_ESERVICE_TEMPLATE_INSTANCE_PUBLISHED_UPDATE_DELEGATION_4] Un utente con ruolo M2M-ADMIN NON può modificare le flag di delega di un e-service template instance precedentemente creato avendo un token non valido
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <eServiceDescriptorState> partire dal template e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service con token non valido
    Then si ottiene lo status code 401
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then l'e-service non ha subito modifiche
    Examples:
      | eServiceDescriptorState |
      | PUBLISHED               |
      | SUSPENDED               |
      | DEPRECATED              |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_TEMPLATE_INSTANCE_PUBLISHED_UPDATE_DELEGATION_5] Un utente con ruolo M2M NON può modificare le flag di delega di un e-service template instance precedentemente creato
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <eServiceDescriptorState> partire dal template e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "true"
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | eServiceDescriptorState |
      | PUBLISHED               |
      | SUSPENDED               |
      | DEPRECATED              |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario Outline: [M2M_ESERVICE_TEMPLATE_INSTANCE_PUBLISHED_UPDATE_DELEGATION_6] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service template instance precedentemente creato se non gli appartiene e per cui non possiede la delega in erogazione
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato <eServiceDescriptorState> partire dal template e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "true"
    Then si ottiene lo status code 403
    And l'e-service non ha subito modifiche
    Examples:
      | eServiceDescriptorState |
      | PUBLISHED               |
      | SUSPENDED               |
      | DEPRECATED              |

  @eservice_published_delegation
  @eservice_published_delegation_m2m_v3
  @sad-path
  Scenario: [M2M_ESERVICE_TEMPLATE_INSTANCE_PUBLISHED_UPDATE_DELEGATION_7] Un utente con ruolo M2M-ADMIN NON può effettuare una modifica parziale della delega di un e-service template instance precedentemente creato che si trova in stato DRAFT
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    And l'utente effettua la creazione di un nuovo e-service in stato DRAFT partire dal template e impostando delega amministrativa a "false" e delega tecnica a "false"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a "true" e quella tecnica a "true"
    Then si ottiene lo status code 409
    And l'e-service non ha subito modifiche

  @eservice_description_max_length
  @happy-path
  Scenario: [ESERVICE_TEMPLATE_CREATE_DESCRIPTION_MAX_LENGTH_5] La creazione di un e-service template va a buon fine utilizzando la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta la creazione del template e-service con la seguente configurazione:
      | description-length | 400 |
    And si ottiene status code 200
    Then l'utente è un "admin" di "PA1"
    And l'e-service template creato ha una descrizione di 400 caratteri

  @eservice_description_max_length
  @sad-path
  Scenario: [ESERVICE_TEMPLATE_CREATE_DESCRIPTION_MAX_LENGTH_6] La creazione di un e-service template non va a buon fine se viene superata la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta la creazione del template e-service con la seguente configurazione:
      | description-length | 401 |
    Then si ottiene status code 400

  @eservice_description_max_length
  @happy-path
  Scenario: [ESERVICE_TEMPLATE_UPDATE_DESCRIPTION_MAXLENGTH_10] Un utente aggiorna un e-service template in stato DRAFT utilizzando la descrizione della lunghezza massima possibile
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    When l'utente tenta la modifica della descrizione dell'e-service template in stato DRAFT con una descrizione di 400 caratteri
    And si ottiene status code 200
    Then l'utente è un "admin" di "PA1"
    And l'e-service template creato ha una descrizione di 400 caratteri

  @eservice_description_max_length
  @sad-path
  Scenario: [ESERVICE_TEMPLATE_UPDATE_DESCRIPTION_MAXLENGTH_11] L'aggiornamento dell'e-service template in stato DRAFT non va a buon fine se viene superata la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta la creazione dell'e-service template con la configurazione predefinita
    When l'utente tenta la modifica della descrizione dell'e-service template in stato DRAFT con una descrizione di 401 caratteri
    Then si ottiene status code 400

  @eservice_description_max_length
  @happy-path
  Scenario: [ESERVICE_TEMPLATE_UPDATE_DESCRIPTION_MAXLENGTH_12] Un utente aggiorna un e-service template in stato PUBLISHED utilizzando la descrizione della lunghezza massima possibile
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta la modifica della descrizione dell'e-service template in stato PUBLISHED con una descrizione di 400 caratteri
    And si ottiene status code 200
    Then l'utente è un "admin" di "PA1"
    And l'e-service template creato ha una descrizione di 400 caratteri

  @eservice_description_max_length
  @sad-path
  Scenario: [ESERVICE_TEMPLATE_UPDATE_DESCRIPTION_MAXLENGTH_13] L'aggiornamento di un e-service in stato PUBLISHED non va a buon fine se viene superata la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di PUBLISHED
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta la modifica della descrizione dell'e-service template in stato PUBLISHED con una descrizione di 401 caratteri
    Then si ottiene status code 400

  # PIN-10005
  @eservice_description_max_length
  @sad-path
  Scenario: [ESERVICE_TEMPLATE_UPDATE_DESCRIPTION_MAXLENGTH_14] L'aggiornamento della descrizione della versione di un e-service template in stato DRAFT non va a buon fine se si utilizza la lunghezza massima consentita
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta delle modifiche alla versione dell'e-service template con una descrizione di lunghezza 400
    Then si ottiene status code 400

  @eservice_description_max_length
  Scenario: [ESERVICE_TEMPLATE_UPDATE_DESCRIPTION_MAXLENGTH_15] L'aggiornamento di una versione e-service in stato DRAFT non va a buon fine se viene superata la dimensione massima consentita per la descrizione
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità ricezione in stato di DRAFT
    When l'utente tenta delle modifiche alla versione dell'e-service template con una descrizione di lunghezza 401
    Then si ottiene status code 400
