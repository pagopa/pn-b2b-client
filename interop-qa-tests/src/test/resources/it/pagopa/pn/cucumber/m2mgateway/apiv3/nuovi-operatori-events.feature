@m2m-events @nuovi-operatori
Feature: Eventi Nuovi Operatori
  # KO ticket aperto https://pagopa.atlassian.net/browse/PIN-10410
  # PST: Scenario 11 - Caso 11.2
  @nuovi-operatori-ko
  Scenario: [RWRS_ASSEGNAZIONE_11_2_EVENTO] Assegnazione valutatore con emissione evento PurposeRiskAnalysisAssignedV2
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns"
    Then si ottiene status code 200
    And "PA2" visualizza l'evento PurposeRiskAnalysisAssignedV2 con:
      | field     | value      |
      | purposeId | :purposeId |

  # PST: Scenario 12 - Caso 12.2
  # KO
  @nuovi-operatori-ko
  Scenario: [RWRS_COMPILAZIONE_12_2_EVENTO] Compilazione analisi del rischio con emissione evento PurposeRiskAnalysisFormEditedV2
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
    When il valutatore assegnato compila l'analisi del rischio della finalità
    Then si ottiene status code 200
    And "PA2" visualizza l'evento PurposeRiskAnalysisFormEditedV2 con:
      | field     | value      |
      | purposeId | :purposeId |

  # PST: Scenario 14 - Caso 14.3
  # KO
  @nuovi-operatori-ko
  Scenario: [RWRS_CONVALIDA_14_3_EVENTO] Convalida analisi del rischio con emissione evento PurposeRiskAnalysisSignedV2
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
    And il valutatore assegnato compila l'analisi del rischio della finalità
    When il valutatore assegnato convalida l'analisi del rischio della finalità
    Then si ottiene status code 200
    And "PA2" visualizza l'evento PurposeRiskAnalysisSignedV2 con:
      | field     | value      |
      | purposeId | :purposeId |

  # PST: Scenario 16 - Caso 16.2
  # KO
  @nuovi-operatori-ko
  Scenario: [AWRS_ASSEGNAZIONE_16_2_EVENTO] Assegnazione reviewer in modalita AdminWritesReviewerSigns con emissione evento PurposeRiskAnalysisWorkflowCreatedV2
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns"
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "DRAFT"
    And "PA2" visualizza l'evento PurposeRiskAnalysisWorkflowCreatedV2 con:
      | field     | value      |
      | purposeId | :purposeId |

  # PST: Scenario 18 - Caso 18.4
  Scenario: [AWRS_SUBMIT_18_4_EVENTO] Submit analisi del rischio con emissione evento PurposeRiskAnalysisSubmittedV2
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    When l'utente invia il submit dell'analisi del rischio della finalità
    Then si ottiene status code 200
    And "PA2" visualizza l'evento PurposeRiskAnalysisSubmittedV2 con:
      | field     | value      |
      | purposeId | :purposeId |

  # PST: Scenario 19 - Caso 19.2
  Scenario: [AWRS_RIFIUTO_19_2_EVENTO] Rifiuto analisi del rischio con emissione evento PurposeRiskAnalysisRejectedV2
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    And l'utente invia il submit dell'analisi del rischio della finalità
    When un reviewer assegnato rifiuta l'analisi del rischio
    Then si ottiene status code 200
    And lo stato della compilazione dell'analisi del rischio è "REJECTED"
    And "PA2" visualizza l'evento PurposeRiskAnalysisRejectedV2 con:
      | field     | value      |
      | purposeId | :purposeId |