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