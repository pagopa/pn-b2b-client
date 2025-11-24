Feature: finalità agevolata, purpose template ASSOCIAZIONE ES

  #23(OK)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_GET_ASSOCIATED_ES_OK]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    And si ottiene lo status code 200
    When si effettua la get degli e-service associati al purpose template creato
    Then la lista di e-service associati contiene l'e-service atteso

  #24(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_GET_ASSOCIATED_ES_404]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    When si effettua la get degli e-service associati al purpose template inesistente
    Then si ottiene lo status code 404

  #25(OK)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_GET_ASSOCIATED_ES_WITH_FLAG_OK]
    Given "PA2" ha già creato e pubblicato 1 e-service con personalData <personalData>
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData <personalData>
    And il purpose template creato viene associato a un e-service con personalData <personalData>
    And si ottiene lo status code 200
    When si effettua la get degli e-service associati al purpose template creato
    Then la lista di e-service associati contiene l'e-service atteso
    Examples:
      | personalData |
      | true         |
      | false        |


  #26(OK)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_ES_OK]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    Then il purpose template creato viene associato all'e-service
    Examples:
      | state     |
      | DRAFT     |
      | PUBLISHED |

  #27(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_ES_WRONG_STATE]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    When il purpose template creato viene associato all'e-service
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | SUSPENDED |
      | ARCHIVED  |

  #28(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_ES_NO_ADMIN]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And il purpose template creato viene associato all'e-service
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #29(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_ASSOCIATE_ES_NO_CREATOR]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene associato all'e-service
    Then si ottiene lo status code 403

  #30(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_ASSOCIATE_ES_404]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When il purpose template inesistente viene associato all'e-service
    Then si ottiene lo status code 404

  #31(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_ES_ALREADY_ASSOCIATED]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    And il purpose template creato viene associato all'e-service
    When il purpose template creato viene associato all'e-service
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | DRAFT     |
      | PUBLISHED |

  #32(OK)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_OK]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <state>
    And il purpose template creato viene associato all'e-service
    Then il purpose template creato viene disassociato dall'e-service
    Examples:
      | state     |
      | DRAFT     |
      | PUBLISHED |

  #33(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_WRONG_STATE]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    And il purpose template creato viene spostato in stato <state>
    When il purpose template creato viene disassociato dall'e-service
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | SUSPENDED |
      | ARCHIVED  |

  #34(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_ALREADY_UNLINKED]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    And il purpose template creato viene disassociato dall'e-service
    When il purpose template creato viene disassociato dall'e-service
    Then si ottiene lo status code 409

  #35(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_NO_ADMIN]
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

  #36(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_NO_CREATOR]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    Given l'utente è un "admin" di "GSP"
    And il purpose template creato viene disassociato dall'e-service
    Then si ottiene lo status code 403

  #37(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_404]
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    Then il purpose template inesistente viene disassociato dall'e-service
    Then si ottiene lo status code 404


