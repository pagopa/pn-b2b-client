Feature: finalità agevolata, purpose template ASSOCIAZIONE ES

  #23(OK)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_GET_ASSOCIATED_ES_OK] Recupero degli eService associati a una finalità agevolata (OK)
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
  Scenario: [PURPOSE_TEMPLATE_GET_ASSOCIATED_ES_404] Recupero degli eService associati a una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    When si effettua la get degli e-service associati al purpose template inesistente
    Then si ottiene lo status code 404

  #25(OK)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_GET_ASSOCIATED_ES_WITH_FLAG_OK] Recupero degli eService associati a una finalità agevolata con flag PersonalData avente valore uguale a quello dell'eService (OK)
    Given "PA2" ha già creato e pubblicato 1 e-service con personalData <personalData>
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template con handlePersonalData <personalData>
    And il purpose template creato viene associato all'e-service
    And si ottiene lo status code 200
    When si effettua la get degli e-service associati al purpose template creato
    Then la lista di e-service associati contiene l'e-service atteso
    Examples:
      | personalData |
      | true         |
      | false        |


  #26(OK)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_ES_OK] Associazione di un eService a una finalità agevolata in stato DRAFT o PUBLISHED (OK)
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
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_ES_WRONG_STATE] Associazione di un eService a una finalità agevolata in stato diverso da DRAFT o PUBLISHED (error 409)
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
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_ES_NO_ADMIN] Associazione di un eService a una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "<ente>"
    And il purpose template creato viene associato all'e-service
    Then si ottiene lo status code 403
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

  #29(KO)
  # 27 01 2026: In osservanza a https://pagopa.atlassian.net/browse/PIN-8190 il codice restituito è stato mutato 403 -> 404
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_ASSOCIATE_ES_NO_CREATOR] Associazione di un eService a una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 404)
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And il purpose template creato viene associato all'e-service
    Then si ottiene lo status code 404

  #30(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_ASSOCIATE_ES_404] Associazione di un eService a una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When il purpose template inesistente viene associato all'e-service
    Then si ottiene lo status code 404

  #31(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario Outline: [PURPOSE_TEMPLATE_ASSOCIATE_ES_ALREADY_ASSOCIATED] Associazione di un eService a una finalità agevolata che risulta già associata a tale eService (error 409)
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
  Scenario Outline: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_OK] Disassociazione di un eService da una finalità agevolata (OK)
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
  Scenario Outline: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_WRONG_STATE] Disassociazione di un eService da una finalità agevolata in stato diverso da DRAFT o PUBLISHED (error 409)
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    And il purpose template viene gradualmente spostato in stato <state>
    When il purpose template creato viene disassociato dall'e-service
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | SUSPENDED |
      | ARCHIVED  |

  #34(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_ALREADY_UNLINKED] Disassociazione di un eService da una finalità agevolata che risulta già disassociata da tale eService (error 409)
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
  Scenario Outline: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_NO_ADMIN] Disassociazione di un eService da una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    When l'utente è un "<ruolo>" di "<ente>"
    And il purpose template creato viene disassociato dall'e-service
    Then si ottiene lo status code 403
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

  #36(KO)
  # 27 01 2026: In osservanza a https://pagopa.atlassian.net/browse/PIN-8190 il codice restituito è stato mutato 403 -> 404
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_NO_CREATOR] Disassociazione di un eService da una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 404)
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    Given l'utente è un "admin" di "GSP"
    And il purpose template creato viene disassociato dall'e-service
    Then si ottiene lo status code 404

  #37(KO)
  @purposeTemplate @purposeTemplateEservice
  Scenario: [PURPOSE_TEMPLATE_DISASSOCIATE_ES_404] Disassociazione di un eService da una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA2"
    And "PA2" ha già creato e pubblicato 1 e-service
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And il purpose template creato viene associato all'e-service
    Then il purpose template inesistente viene disassociato dall'e-service
    Then si ottiene lo status code 404


