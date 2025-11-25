Feature: finalità agevolata, purpose template ANNOTATION DOCUMENT

    #81-82-83-84-85
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    When vengono caricati <docNumber> documenti <casistica> associati all'annotazione esistente
    Then si ottiene lo status code <statusCode>
    Examples:
      | docNumber | casistica                  | statusCode |
      | 2         | "DIVERSI CON NOME DIVERSO" | 200        |
      | 2         | "UGUALI CON NOME DIVERSO"  | 409        |
      | 2         | "DIVERSI CON NOME UGUALE"  | 409        |
      | 1         | "DI TIPO NON PDF"          | 409        |
      | 3         | "DIVERSI CON NOME DIVERSO" | 409        |

  #87
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    When il purpose template viene gradualmente spostato in stato <state>
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  #88 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    When l'utente è un "<ruolo>" di "PA1"
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #89 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    When l'utente è un "admin" di "GSP"
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    Then si ottiene lo status code 403

  #90 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    When vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione inesistente
    Then si ottiene lo status code 404

  #91 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene eliminata l'annotazione esistente per il purpose template
    Then si ottiene lo status code 204

  #92 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When il purpose template viene gradualmente spostato in stato <state>
    And viene eliminata l'annotazione esistente per il purpose template
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  #93 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When l'utente è un "<ruolo>" di "PA1"
    And viene eliminata l'annotazione esistente per il purpose template
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #94 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When l'utente è un "admin" di "GSP"
    And viene eliminata l'annotazione esistente per il purpose template
    Then si ottiene lo status code 403

  #95 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_ALREADY_DELETED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene eliminata l'annotazione esistente per il purpose template
    And viene eliminata l'annotazione esistente per il purpose template
    Then si ottiene lo status code 404

  #96 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene eliminata l'annotazione inesistente per il purpose template
    Then si ottiene lo status code 404

  #97 (OK)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene eliminato il documento esistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 204

  #98 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When il purpose template viene gradualmente spostato in stato <state>
    And viene eliminato il documento esistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  #99 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When l'utente è un "<ruolo>" di "PA1"
    And viene eliminato il documento esistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #100 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When l'utente è un "admin" di "GSP"
    And viene eliminato il documento esistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 403

  #101 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_ALREADY_DELETED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    And viene eliminato il documento esistente dell'annotazione precedentemente creata
    When viene eliminato il documento esistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 404

  #102 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene eliminato il documento inesistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 404

  #103 (OK)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_ANNOTATION_DOCUMENT_GET_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene recuperato il documento esistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 200

  #104 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_ANNOTATION_DOCUMENT_GET_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene "creata" un'annotazione con testo entro i 50 caratteri per il purpose template esistente
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene recuperato il documento inesistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 404
