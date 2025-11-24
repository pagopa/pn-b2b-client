Feature: finalità agevolata, purpose template ANNOTATION

  #66-67 (OK-KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_LIMITS]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio <answerType> per il purpose template creato
    Then si ottiene lo status code <statusCode>
    Examples:
      | statusCode | answerType                            |
      | 200        | "ENTRO I LIMITI CONSENTITI FREE TEXT" |
      | 400        | "OLTRE I LIMITI CONSENTITI FREE TEXT" |
#      | 200        | "ENTRO I LIMITI CONSENTITI MULTI"     |
#      | 400        | "OLTRE I LIMITI CONSENTITI MULTI"     |

  #68 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template in stato <status>
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    Then si ottiene lo status code 409
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
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
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
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    Then si ottiene lo status code 403

  #71 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template inesistente
    Then si ottiene lo status code 404

  #72 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_HYPER_LINK]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "CONTENENTE HYPER LINK" per il purpose template creato
    Then si ottiene lo status code 400

  #73 (OK-KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_CREATE_ANNOTATION_LIMITS]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When viene "creata" un'annotazione con testo <range> i 2000 caratteri per il purpose template esistente
    Then si ottiene lo status code <statusCode>
    Examples:
      | range | statusCode |
      | entro | 200        |
      | oltre | 400        |

  #74-75 (OK-KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_LIMITS]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When viene "modificata" un'annotazione con testo <range> i 2000 caratteri per il purpose template esistente
    Then si ottiene lo status code <statusCode>
    Examples:
      | range | statusCode |
      | entro | 200        |
      | oltre | 400        |
    
  #76 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_WRONG_STATE]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When il purpose template viene gradualmente spostato in stato <ptState>
    When viene "modificata" un'annotazione con testo entro i 2000 caratteri per il purpose template esistente
    Then si ottiene lo status code 409
    Examples:
      | ptState   |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  #77 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_NO_ADMIN]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When l'utente è un "<ruolo>" di "PA1"
    When viene "modificata" un'annotazione con testo entro i 2000 caratteri per il purpose template esistente
    Then si ottiene lo status code 403
    Examples:
      | ruolo    |
      | api      |
      | support  |
      | security |

  #78 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_NO_CREATOR]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When l'utente è un "admin" di "GSP"
    When viene "modificata" un'annotazione con testo entro i 2000 caratteri per il purpose template esistente
    Then si ottiene lo status code 403

  #79 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_404]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When viene "modificata 404" un'annotazione con testo entro i 2000 caratteri per il purpose template esistente
    Then si ottiene lo status code 404

  #80 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_NO_TEXT]
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When viene "modificata" un'annotazione con testo entro i 0 caratteri per il purpose template esistente
    Then si ottiene lo status code 400