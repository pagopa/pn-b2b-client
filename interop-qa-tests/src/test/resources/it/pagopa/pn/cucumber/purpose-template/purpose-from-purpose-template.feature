Feature: finalità agevolata, purpose from purpose template

  #107 (OK) 108 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_WITH_PERSONAL_DATA] Creazione di una finalità a partire da un template di finalità agevolata (successo quando i flag di personal data coincidono, errore altrimenti)
    Given "PA2" ha già creato e pubblicato 1 e-service con personalData <personalDataEservice>
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData <personalDataTemplate>
    And il purpose template creato viene spostato in stato PUBLISHED
    When si crea una finalità a partire dal purpose template creato
    Then si ottiene response status code <statusCode>
    Examples:
      | personalDataTemplate | personalDataEservice | statusCode |
      | true                 | true                 | 200        |
      | false                | false                | 200        |
      | true                 | false                | 400        |
      | false                | true                 | 400        |

  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_WITH_PERSONAL_DATA_1] Creazione di una finalità a partire da un template di finalità agevolata
    Given "PA2" ha già creato e pubblicato 1 e-service con personalData <personalDataEservice>
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData <personalDataTemplate>
    And il purpose template creato viene spostato in stato PUBLISHED
    When si crea una finalità a partire dal purpose template creato
    Then si ottiene response status code 200
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa
    Then si ottiene response status code 409

    Examples:
      | personalDataTemplate | personalDataEservice |
      | true                 | true                 |
      | false                | false                |

  #109 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_WRONG_STATE] Creazione di una finalità a partire da un template di finalità agevolata in stato diverso da PUBLISHED (error)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato SUSPENDED
    When si crea una finalità a partire dal purpose template sospeso
    Then si ottiene response status code 404

  #110 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_WRONG_INPUT] Creazione di una finalità a partire da un template di finalità agevolata (error: request senza campi obbligatori)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    When si crea una finalità a partire dal purpose template creato passando "DATI NULL"
    Then si ottiene response status code 400

  #111 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_404] Creazione di una finalità a partire da un template di finalità agevolata passando un'ID inesistente (error 404)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    When si crea una finalità a partire dal purpose template inesistente
    Then si ottiene response status code 404

  #112 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_NO_ADMIN] Creazione di una finalità a partire da un template di finalità agevolata da parte di un utente NON admin (error 403)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    When l'utente è un "<ruolo>" di "<ente>"
    And si crea una finalità a partire dal purpose template esistente
    Then si ottiene response status code 403
    Examples:
      | ente | ruolo    |
      | PA1  | api      |
      | PA1  | support  |
      | PA1  | security |

    @nuovi-operatori-update
    Examples:
      | ente | ruolo    |
      | PA2  | viewer   |

      # Non applicabile a "reviewer" perché il reviewer non può nemmeno fare il get dell'agreement, il quale è un'azione preliminare dello step "And si crea una finalità a partire dal purpose template esistente"
      # | PA2  | reviewer |

  #113 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_DUPLICATED_TITLE] Creazione di una finalità a partire da un template di finalità agevolata usando un titolo già esistente (error 409)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente passando "TITOLO ESISTENTE"
    When si crea una finalità a partire dal purpose template esistente passando "TITOLO ESISTENTE"
    Then si ottiene response status code 409

  #114 (OK)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_OK] Modifica di una finalità creata a partire da un template di finalità agevolata
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    When si modifica la finalità creata
    Then si ottiene response status code 200

  #115 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_WRONG_STATE] Modifica di una finalità creata a partire da un template di finalità agevolata, con la finalità in stato diverso da DRAFT (error 409)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And la finalità viene portata in stato <state>
    When si modifica la finalità creata
    Then si ottiene response status code 409
    Examples:
      | state     |
      | ACTIVE    |
      | SUSPENDED |
      | ARCHIVED  |

  #116 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_NULL_VALUES] Modifica di una finalità creata a partire da un template di finalità agevolata passando request con body non valido (error 409)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    When si modifica la finalità creata passando "<problemType>"
    Then si ottiene response status code 400
    Examples:
      | problemType      |
#      | VALORI NULL      |
      | EMPTY TITLE      |
      | ZERO DAILY CALLS |

  #117 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_404] Modifica di una finalità creata a partire da un template di finalità agevolata passando un ID inesistente (error 404)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    When si modifica la finalità inesistente
    Then si ottiene response status code 404

  #118 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario Outline: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_NO_ADMIN] Modifica di una finalità creata a partire da un template di finalità agevolata da parte di un utente NON admin (error 403)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    When l'utente è un "<ruolo>" di "<ente>"
    And si modifica la finalità creata
    Then si ottiene response status code 403
    Examples:
      | ente | ruolo    |
      | PA1  | api      |
      | PA1  | support  |
      | PA1  | security |

    @nuovi-operatori-update
    Examples:
      | ente | ruolo    |
      | PA2  | reviewer |
      | PA2  | viewer   |

  #119 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_NO_CREATOR] Modifica di una finalità creata a partire da un template di finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità (error 403)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    When l'utente è un "admin" di "GSP"
    And si modifica la finalità creata
    Then si ottiene response status code 403

  #120 (KO)
  @purposeTemplate @purposeFromPurposeTemplate
  Scenario: [PURPOSE_TEMPLATE_CREATE_PURPOSE_FROM_TEMPLATE_PATCH_EXISTING_TITLE] Modifica di una finalità creata a partire da un template di finalità agevolata, passando un titolo già associato ad un'altra finalità (error 409)
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When si modifica la finalità creata passando "TITLE ESISTENTE"
    Then si ottiene response status code 409

  @adeguamento-analisi-rischio
  Scenario Outline: [PURPOSE_TEMPLATE_PATCH_TK_1] A seguito del cambiamento di tenant kind si tenta di modificare una finalità
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "<ente>" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "<ente>"
    And viene creato un nuovo purpose template coerente con la tipologia dell'ente
    And il purpose template creato viene spostato in stato PUBLISHED
    And si crea una finalità a partire dal purpose template esistente
    And il tenant kind dell'ente "<ente>" viene impostato a "<kind>"
    When si modifica la finalità creata
    Then si ottiene response status code 200
    Examples:
      | ente    | kind        |
      | PA4     | PRIVATE     |
      | PA4     | GSP         |
      | GSP2    | PA          |
      | Privato | PA          |