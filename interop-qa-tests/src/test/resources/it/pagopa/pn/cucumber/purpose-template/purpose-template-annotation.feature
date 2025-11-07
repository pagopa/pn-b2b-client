Feature: finalità agevolata, purpose template ANNOTATION

  #TODO todo Matteo -> 63-64 rimangono da fare

  #66-67 (OK-KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_LIMITS]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio <answerType> per il purpose template creato
    Then si ottiene lo status code <statusCode>
    Examples:
      | statusCode | answerType                  |
      | 200        | "ENTRO I LIMITI CONSENTITI" |
      | 400        | "OLTRE I LIMITI CONSENTITI" |

  #68 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <status>
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    Then si ottiene lo status code 400
    Examples:
      | status    |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |
    
  #69 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "PA1"
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #70 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    Then si ottiene lo status code 403

  #71 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template inesistente
    Then si ottiene lo status code 404

  #72 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_HYPER_LINK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "CONTENENTE HYPER LINK" per il purpose template creato
    Then si ottiene lo status code 400

  #73 (OK-KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_CREATE_ANNOTATION_LIMITS]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When viene "creata" un'annotation <annotationType> per il purpose template esistente
    Then si ottiene lo status code <statusCode>
    Examples:
      | statusCode | annotationType              |
      | 200        | "ENTRO I LIMITI CONSENTITI" |
      | 400        | "OLTRE I LIMITI CONSENTITI" |

  #74-75 (OK-KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_LIMITS]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When viene "modificata" un'annotation <annotationType> per il purpose template esistente
    Then si ottiene lo status code <statusCode>
    Examples:
      | annotationType              | statusCode |
      | "ENTRO I LIMITI CONSENTITI" | 200        |
      | "OLTRE I LIMITI CONSENTITI" | 400        |
    
  #76 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When il purpose template creato viene spostato in stato <ptState>
    And viene "modificata" un'annotation "ENTRO I LIMITI CONSENTITI" per il purpose template esistente
    Then si ottiene lo status code 400
    Examples:
      | ptState   |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  #77 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When l'utente è un "<ruolo>" di "PA1"
    And viene "modificata" un'annotation "ENTRO I LIMITI CONSENTITI" per il purpose template esistente
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #78 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When l'utente è un "admin" di "GSP"
    And viene "modificata" un'annotation "ENTRO I LIMITI CONSENTITI" per il purpose template esistente
    Then si ottiene lo status code 403

  #79 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When viene "modificata 404" un'annotation "ENTRO I LIMITI CONSENTITI" per il purpose template esistente
    Then si ottiene lo status code 404

  #80 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_NO_TEXT]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When viene "modificata" un'annotation "RIMUOVENDO IL TESTO" per il purpose template esistente
    Then si ottiene lo status code 400

  #81-82-83-84-85
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When vengono caricati <docNumber> documenti <casistica> associati all'annotation esistente
    Then si ottiene lo status code <statusCode>
    Examples:
      | docNumber | casistica                  | statusCode |
      | 2         | "DIVERSI CON NOME DIVERSO" | 200        |
      | 2         | "UGUALI CON NOME DIVERSO"  | 409        |
      | 2         | "DIVERSI CON NOME UGUALE"  | 409        |
      | 1         | "DI TIPO NON PDF"          | 409        |
      | 3         | "DIVERSI CON NOME DIVERSO" | 409        |

  #87
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When il purpose template creato viene spostato in stato <state>
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    Then si ottiene lo status code 400
    Examples:
      | state     |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  #88 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When l'utente è un "<ruolo>" di "PA1"
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #89 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When l'utente è un "admin" di "GSP"
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    Then si ottiene lo status code 403

  #90 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    When vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation inesistente
    Then si ottiene lo status code 404

  #91 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When viene eliminata l'annotation esistente per il purpose template
    Then si ottiene lo status code 204

  #92 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When il purpose template creato viene spostato in stato <state>
    And viene eliminata l'annotation esistente per il purpose template
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  #93 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When l'utente è un "<ruolo>" di "PA1"
    And viene eliminata l'annotation esistente per il purpose template
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #94 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When l'utente è un "admin" di "GSP"
    And viene eliminata l'annotation esistente per il purpose template
    Then si ottiene lo status code 403

  #95 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_ALREADY_DELETED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When viene eliminata l'annotation esistente per il purpose template
    And viene eliminata l'annotation esistente per il purpose template
    Then si ottiene lo status code 404

  #96 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When viene eliminata l'annotation inesistente per il purpose template
    Then si ottiene lo status code 404

  #97 (OK)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When viene eliminato il documento esistente dell'annotation precedentemente creata
    Then si ottiene lo status code 204

  #98 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When il purpose template creato viene spostato in stato <state>
    And viene eliminato il documento esistente dell'annotation precedentemente creata
    Then si ottiene lo status code 409
    Examples:
      | state     |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  #99 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When l'utente è un "<ruolo>" di "PA1"
    And viene eliminato il documento esistente dell'annotation precedentemente creata
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #100 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When l'utente è un "admin" di "GSP"
    And viene eliminato il documento esistente dell'annotation precedentemente creata
    Then si ottiene lo status code 403

  #101 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_ALREADY_DELETED]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    And viene eliminato il documento esistente dell'annotation precedentemente creata
    When viene eliminato il documento esistente dell'annotation precedentemente creata
    Then si ottiene lo status code 404

  #102 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When viene eliminato il documento inesistente dell'annotation precedentemente creata
    Then si ottiene lo status code 404

  #103 (OK)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_GET_OK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When viene recuperato il documento esistente dell'annotation precedentemente creata
    Then si ottiene lo status code 200

  #104 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysyAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_GET_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI" per il purpose template creato
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotation esistente
    When viene recuperato il documento inesistente dell'annotation precedentemente creata
    Then si ottiene lo status code 400
