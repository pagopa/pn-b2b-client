Feature: finalità agevolata, purpose template ASSOCIAZIONE EC

  #21(OK)
  Scenario: [PURPOSE_TEMPLATE_GET_ASSOCIATED_EC_OK]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    When si effettua la get degli e-service associati al purpose template creato
    Then la lista di e-service associati contiene l'e-service atteso

  #22(KO)
  Scenario: [PURPOSE_TEMPLATE_GET_ASSOCIATED_EC_KO]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    When si effettua la get degli e-service associati al purpose template inesistente
    Then si ottiene lo status code 404

  #23-24(OK)
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_EC_OK]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    Then il purpose template creato viene associato all'e-service
    Examples:
      | state  |
      | DRAFT  |
      | ACTIVE |

  #25(KO)
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_EC_KO]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene associato all'e-service
    Then si ottiene lo status code 400
    Examples:
      | state     |
      | SUSPENDED |
      | ARCHIVED  |

  #26(KO)
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_EC_KO_NO_ADMIN]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene associato all'e-service
    Then si ottiene lo status code <statusCode>
    Examples:
      | ruolo    | statusCode |
      | api      | 403        |
      | support  | 403        |
      | security | 403        |

  #27(KO)
  Scenario: [PURPOSE_TEMPLATE_ASSOCIATE_EC_KO_NO_CREATOR]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene associato all'e-service
    Then si ottiene lo status code 403

  #28(KO)
  Scenario: [PURPOSE_TEMPLATE_ASSOCIATE_EC_KO_404]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When il purpose template inesistente viene associato all'e-service
    Then si ottiene lo status code 404

  #29(KO)
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_EC_KO_ALREADY_ASSOCIATED]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    And il purpose template creato viene associato all'e-service
    When il purpose template creato viene associato all'e-service
    Then si ottiene lo status code 409
    Examples:
      | state  |
      | DRAFT  |
      | ACTIVE |

  #30(OK)
  Scenario Outline: [PURPOSE_TEMPLATE_DISASSOCIATE_EC_OK]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    And il purpose template creato viene associato all'e-service
    Then il purpose template creato viene disassociato dall'e-service
    Examples:
      | state  |
      | DRAFT  |
      | ACTIVE |

  #31(KO)
  Scenario Outline: [PURPOSE_TEMPLATE_DISASSOCIATE_EC_KO_WRONG_STATE]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    And il purpose template creato viene spostato in stato <state>
    When il purpose template creato viene disassociato dall'e-service
    Then si ottiene lo status code 400
    Examples:
      | state     |
      | SUSPENDED |
      | ARCHIVED  |

  #32(KO)
  Scenario: [PURPOSE_TEMPLATE_DISASSOCIATE_EC_KO_ALREADY_UNLINKED]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    And il purpose template creato viene disassociato dall'e-service
    When il purpose template creato viene disassociato dall'e-service
    Then si ottiene lo status code 400

  #33(KO)
  Scenario Outline: [PURPOSE_TEMPLATE_DISASSOCIATE_EC_KO_NO_ADMIN]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene disassociato dall'e-service
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #34(KO)
  Scenario: [PURPOSE_TEMPLATE_DISASSOCIATE_EC_KO_NO_CREATOR]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    Given l'utente è un "admin" di "GSP"
    And il purpose template creato viene disassociato dall'e-service
    Then si ottiene lo status code 403

  #35(KO)
  Scenario: [PURPOSE_TEMPLATE_DISASSOCIATE_EC_OK]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    Then il purpose template inesistente viene disassociato dall'e-service
    Then si ottiene lo status code 404


