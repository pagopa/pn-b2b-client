Feature: finalità agevolata, purpose template ANNOTATION DOCUMENT

    #81-82-83-84-85
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS] Upload di documenti legati ad un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata (OK-KO)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    When vengono caricati <docNumber> documenti <casistica> associati all'annotazione esistente
    Then si ottiene lo status code <statusCode>
    Examples:
      | docNumber | casistica                  | statusCode |
      | 2         | "DIVERSI CON NOME DIVERSO" | 200        |
      | 2         | "UGUALI CON NOME DIVERSO"  | 409        |
      | 2         | "DIVERSI CON NOME UGUALE"  | 409        |
      | 1         | "DI TIPO NON PDF"          | 400        |
      | 3         | "DIVERSI CON NOME DIVERSO" | 409        |

  #87
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_WRONG_STATE] Upload di documenti legati ad un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata che si trova in stato diverso da DRAFT (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
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
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_NO_ADMIN] Upload di documenti legati ad un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    When l'utente è un "<ruolo>" di "<ente>"
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
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

  #89 (KO)
  # 27 01 2026: In osservanza a https://pagopa.atlassian.net/browse/PIN-8190 il codice restituito è stato mutato 403 -> 404
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_NO_CREATOR] Upload di documenti legati ad un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    When l'utente è un "admin" di "GSP"
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione invisibile
    Then si ottiene lo status code 404

  #90 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_UPLOAD_DOCS_404] Upload di documenti legati ad un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    When vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione inesistente
    Then si ottiene lo status code 404

  #91 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_OK] Eliminazione di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene eliminata l'annotazione esistente per il purpose template
    And l'eliminazione dell'annotation ha avuto successo

  #92 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_WRONG_STATE] Eliminazione di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata che si trova in stato diverso da DRAFT (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
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
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_NO_ADMIN] Eliminazione di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When l'utente è un "<ruolo>" di "<ente>"
    And viene eliminata l'annotazione esistente per il purpose template
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

  #94 (KO)
  # 27 01 2026: In osservanza a https://pagopa.atlassian.net/browse/PIN-8190 il codice restituito è stato mutato 403 -> 404
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_NO_CREATOR] Eliminazione di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When l'utente è un "admin" di "GSP"
    And viene eliminata l'annotazione esistente per il purpose template
    Then si ottiene lo status code 404

  #95 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_ALREADY_DELETED] Eliminazione di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata già eliminata in precedenza (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene eliminata l'annotazione esistente per il purpose template
    And l'eliminazione dell'annotation ha avuto successo
    And viene eliminata l'annotazione esistente per il purpose template
    Then si ottiene lo status code 404

  #96 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_404] Eliminazione di un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene eliminata l'annotazione inesistente per il purpose template
    Then si ottiene lo status code 404

  #97 (OK)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_OK] Eliminazione di un documento associato a un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene eliminato il documento esistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 204

  #98 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_WRONG_STATE] Eliminazione di un documento associato a un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata che si trova in stato diverso da DRAFT (error 409)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
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
  Scenario Outline: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_NO_ADMIN] Eliminazione di un documento associato a un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata da parte di un utente NON admin (error 403)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When l'utente è un "<ruolo>" di "<ente>"
    And viene eliminato il documento esistente dell'annotazione precedentemente creata
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

  #100 (KO)
  # 27 01 2026: In osservanza a https://pagopa.atlassian.net/browse/PIN-8190 il codice restituito è stato mutato 403 -> 404
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_NO_CREATOR] Eliminazione di un documento associato a un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata da parte di un utente non appartenente alla PA che ha creato la finalità agevolata (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When l'utente è un "admin" di "GSP"
    And viene eliminato il documento esistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 404

  #101 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_ALREADY_DELETED] Eliminazione di un documento associato a un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata, con documento già eliminato in precedenza (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    And viene eliminato il documento esistente dell'annotazione precedentemente creata con successo
    When viene eliminato il documento esistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 404

  #102 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_DELETE_ANNOTATION_DOCUMENT_404] Eliminazione di un documento associato a un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene eliminato il documento inesistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 404

  #103 (OK)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_ANNOTATION_DOCUMENT_GET_OK] Recupero di un documento associato a un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata (OK)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene recuperato il documento esistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 200

  #104 (KO)
  @purposeTemplate @purposeTemplateRiskAnalysisAnswerAnnotationDocument
  Scenario: [PURPOSE_TEMPLATE_RISK_ANALYSIS_ANSWER_ANNOTATION_DOCUMENT_GET_404] Recupero di un documento associato a un'annotazione associata a una risposta di analisi del rischio di una finalità agevolata passando un ID inesistente (error 404)
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 50 caratteri ad una risposta esistente del purpose template
    And si ottiene lo status code 200
    And vengono caricati 1 documenti "DIVERSI CON NOME DIVERSO" associati all'annotazione esistente
    When viene recuperato il documento inesistente dell'annotazione precedentemente creata
    Then si ottiene lo status code 404
