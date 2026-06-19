@m2m-events @nuovi-operatori
Feature: Eventi Nuovi Operatori

  # PST: Scenario 11 - Caso 11.2
  @nrt-minimal
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



