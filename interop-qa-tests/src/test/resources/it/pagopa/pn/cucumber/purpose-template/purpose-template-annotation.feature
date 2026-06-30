Feature: finalità agevolata, purpose template ANNOTATION

  #66-67 (OK-KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_LIMITS] Creazione di una risposta di analisi del rischio da associare a una finalità agevolata (OK-KO)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio <answerType> per il purpose template creato
    Then si ottiene lo status code <statusCode>
    Examples:
      | statusCode | answerType                            |
      | 200        | "ENTRO I LIMITI CONSENTITI FREE TEXT" |
      | 400        | "OLTRE I LIMITI CONSENTITI FREE TEXT" |

  #68 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_WRONG_STATE] Creazione di una risposta di analisi del rischio da associare a una finalità agevolata in stato diverso da DRAFT (error 409)
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
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_NO_ADMIN] Creazione di una risposta di analisi del rischio da associare a una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "<ruolo>" di "<ente>"
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
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

  #70 (KO)
  # 27 01 2026: In osservanza a https://pagopa.atlassian.net/browse/PIN-8190 il codice restituito è stato mutato 403 -> 404
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_NO_CREATOR] Creazione di una risposta di analisi del rischio da associare a una finalità agevolata da parte di un utente che appartiene a una PA diversa da quella che ha creato la finalità agevolata (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When l'utente è un "admin" di "GSP"
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    Then si ottiene lo status code 404

  #71 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_404] Creazione di una risposta di analisi del rischio da associare a una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    When viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template inesistente
    Then si ottiene lo status code 404

  #72 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswer
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_HYPER_LINK] Creazione di una risposta di analisi del rischio da associare a una finalità agevolata inserendo un link nell'annotation (error 400)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When viene aggiunta un'annotazione con testo contenente hyper-link ad una risposta di analisi del rischio del purpose template
    Then si ottiene lo status code 400

  #73 (OK-KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_CREATE_ANNOTATION_LIMITS] Creazione di un'annotazione da associare a una risposta di analisi del rischio di una finalità agevolata (OK-KO)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When viene aggiunta un'annotazione con testo <range> i 2000 caratteri ad una risposta esistente del purpose template
    Then si ottiene lo status code <statusCode>
    Examples:
      | range | statusCode |
      | entro | 200        |
      | oltre | 400        |

  #74-75 (OK-KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_LIMITS] Modifica di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata (OK-KO)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When viene aggiunta un'annotazione con testo <range> i 2000 caratteri ad una risposta esistente del purpose template
    Then si ottiene lo status code <statusCode>
    Examples:
      | range | statusCode |
      | entro | 200        |
      | oltre | 400        |

  #76 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_WRONG_STATE] Modifica di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata in stato diverso da DRAFT (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When il purpose template viene gradualmente spostato in stato <ptState>
    When viene aggiunta un'annotazione con testo entro i 2000 caratteri ad una risposta esistente del purpose template
    Then si ottiene lo status code 409
    Examples:
      | ptState   |
      | PUBLISHED |
      | SUSPENDED |
      | ARCHIVED  |

  #77 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_NO_ADMIN] Modifica di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When l'utente è un "<ruolo>" di "<ente>"
    When viene aggiunta un'annotazione con testo entro i 2000 caratteri ad una risposta esistente del purpose template
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

  #78 (KO)
  # 27 01 2026: In osservanza a https://pagopa.atlassian.net/browse/PIN-8190 il codice restituito è stato mutato 403 -> 404
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_NO_CREATOR] Modifica di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When l'utente è un "admin" di "GSP"
    When viene aggiunta un'annotazione con testo entro i 2000 caratteri ad una risposta invisibile del purpose template
    Then si ottiene lo status code 404

  #79 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_404] Modifica di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When viene aggiunta un'annotazione con testo entro i 2000 caratteri ad una risposta inesistente del purpose template
    Then si ottiene lo status code 404

  #80 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotation
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPDATE_ANNOTATION_NO_TEXT] Modifica di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata con un testo di 0 caratteri (error 400)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When viene aggiunta un'annotazione con testo entro i 0 caratteri ad una risposta esistente del purpose template
    Then si ottiene lo status code 400