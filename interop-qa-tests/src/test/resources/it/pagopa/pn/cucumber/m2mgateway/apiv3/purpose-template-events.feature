@m2m-purpose-template-events
Feature: Eventi M2M di Purpose Template

  Scenario: [M2M_PURPOSE_TEMPLATE_EVENTS_01] L'evento di creazione in DRAFT di purpose template deve essere visibile solo all'owner della risorsa
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene creato un nuovo purpose template
    And "PA1" visualizza l'evento PurposeTemplateAdded con:
      | field             | value              |
      | purposeTemplateId | :purposeTemplateId |
    And "PA2" non visualizza l'evento PurposeTemplateAdded appena trovato

  Scenario Outline: [M2M_PURPOSE_TEMPLATE_EVENTS_02] L'evento di PUBLISHED, ARCHIVED, SUSPENDED di un purpose template deve essere visibile a tutti
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene creato un nuovo purpose template
    And il purpose template creato viene correttamente spostato in stato <stato>
    And "PA1" visualizza l'evento <evento> con:
      | field             | value              |
      | purposeTemplateId | :purposeTemplateId |
    And "PA2" visualizza l'evento <evento> appena trovato

    Examples:
      | stato     | evento                   |
      | PUBLISHED | PurposeTemplatePublished |
      | SUSPENDED | PurposeTemplateSuspended |
      | ARCHIVED  | PurposeTemplateArchived  |

  Scenario: [M2M_PURPOSE_TEMPLATE_EVENTS_03] L'evento di UNSOSPENDED di un purpose template deve essere visibile a tutti
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene creato un nuovo purpose template in stato SUSPENDED
    And il purpose template creato viene riattivato
    And "PA1" visualizza l'evento PurposeTemplateUnsuspended con:
      | field             | value              |
      | purposeTemplateId | :purposeTemplateId |
    And "PA2" visualizza l'evento PurposeTemplateUnsuspended appena trovato

  Scenario: [M2M_PURPOSE_TEMPLATE_EVENTS_04] L'evento di generazione del template della Risk Analysis di un purpose template deve essere visibile a tutti
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene creato un nuovo purpose template in stato PUBLISHED
    And il purpose template creato viene riattivato
    And "PA1" visualizza l'evento PurposeTemplatePublished con:
      | field             | value              |
      | purposeTemplateId | :purposeTemplateId |
    And "PA2" visualizza l'evento PurposeTemplatePublished appena trovato
    And "PA1" visualizza l'evento RiskAnalysisTemplateDocumentGenerated con:
      | field             | value              |
      | purposeTemplateId | :purposeTemplateId |
    And "PA2" visualizza l'evento RiskAnalysisTemplateDocumentGenerated appena trovato
    And "PA1" vede 1 volte l'evento RiskAnalysisTemplateDocumentGenerated con:
      | field             | value              |
      | purposeTemplateId | :purposeTemplateId |
    And "PA2" vede 1 volte l'evento RiskAnalysisTemplateDocumentGenerated con:
      | field             | value              |
      | purposeTemplateId | :purposeTemplateId |
