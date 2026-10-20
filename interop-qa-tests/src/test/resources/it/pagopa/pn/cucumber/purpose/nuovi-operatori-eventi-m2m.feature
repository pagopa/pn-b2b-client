@purpose @nuovi-operatori @m2m-events @m2m-ra-review @m2m-ra-events
Feature: Eventi di piattaforma delle assegnazioni RA via M2M

  # CDT 7.8: azione tramite m2m-admin, consultazione per il solo proprietario.
  # Il tipo di evento dipende dalla modalità di destinazione.
  # Nessuna verifica di notifiche App/Mail, conteggi globali o exactly-once.
  Scenario Outline: [M2M_RA_FIRST_ASSIGNMENT_EVENT_01] Evento della prima assegnazione in <mode>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un amministratore del profilo API M2M dell'ente "GSP"
    And si memorizza il riferimento temporale degli eventi prima della modifica
    When l'utente tenta di assegnare alla finalità i seguenti valutatori: <reviewerFinali>, in modalità <mode>
    Then si ottiene status code 204
    And "GSP" visualizza l'evento <evento> con:
      | field     | value      |
      | purposeId | :purposeId |

    Examples:
      | mode                        | reviewerFinali | evento                                    |
      | AdminWritesAdminSigns       | nessuno        | PurposeRiskAnalysisSelfAssignedV2         |
      | AdminWritesReviewerSigns    | 1              | PurposeRiskAnalysisWorkflowCreatedV2      |
      | ReviewerWritesReviewerSigns | 1              | PurposeRiskAnalysisAssignedV2             |

  # CDT 7.8: aggiunta/rimozione nelle due modalità con reviewer e sei cambi mode.
  # La baseline immediatamente prima dell'azione esclude gli eventi del setup BFF.
  Scenario Outline: [M2M_RA_ASSIGNMENT_CHANGE_EVENT_01] <modifica> da <modeIniziale> a <modeFinale>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un amministratore del profilo API BFF dell'ente "GSP"
    And l'utente tenta di assegnare alla finalità i seguenti valutatori: <reviewerIniziali>, in modalità <modeIniziale>
    And si ottiene status code 204
    And l'utente è un amministratore del profilo API M2M dell'ente "GSP"
    And si memorizza il riferimento temporale degli eventi prima della modifica
    When l'utente tenta di assegnare alla finalità i seguenti valutatori: <reviewerFinali>, in modalità <modeFinale>
    Then si ottiene status code 204
    And "GSP" visualizza l'evento <evento> con:
      | field     | value      |
      | purposeId | :purposeId |

    Examples:
      | modifica      | modeIniziale                | reviewerIniziali | modeFinale                  | reviewerFinali | evento                              |
      | aggiunta      | AdminWritesReviewerSigns    | 1                | AdminWritesReviewerSigns    | 1,2            | PurposeRiskAnalysisWorkflowCreatedV2 |
      | rimozione     | AdminWritesReviewerSigns    | 1,2              | AdminWritesReviewerSigns    | 1              | PurposeRiskAnalysisWorkflowCreatedV2 |
      | aggiunta      | ReviewerWritesReviewerSigns | 1                | ReviewerWritesReviewerSigns | 1,2            | PurposeRiskAnalysisAssignedV2        |
      | rimozione     | ReviewerWritesReviewerSigns | 1,2              | ReviewerWritesReviewerSigns | 1              | PurposeRiskAnalysisAssignedV2        |
      | cambio mode   | AdminWritesAdminSigns       | nessuno          | AdminWritesReviewerSigns    | 1              | PurposeRiskAnalysisWorkflowCreatedV2 |
      | cambio mode   | AdminWritesAdminSigns       | nessuno          | ReviewerWritesReviewerSigns | 1              | PurposeRiskAnalysisAssignedV2        |
      | cambio mode   | AdminWritesReviewerSigns    | 1                | AdminWritesAdminSigns       | nessuno        | PurposeRiskAnalysisSelfAssignedV2    |
      | cambio mode   | AdminWritesReviewerSigns    | 1                | ReviewerWritesReviewerSigns | 1              | PurposeRiskAnalysisAssignedV2        |
      | cambio mode   | ReviewerWritesReviewerSigns | 1                | AdminWritesAdminSigns       | nessuno        | PurposeRiskAnalysisSelfAssignedV2    |
      | cambio mode   | ReviewerWritesReviewerSigns | 1                | AdminWritesReviewerSigns    | 1              | PurposeRiskAnalysisWorkflowCreatedV2 |
