@m2m-events @nuovi-operatori
Feature: Eventi Nuovi Operatori
  # PST: Scenario 11 - Caso 11.2
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



