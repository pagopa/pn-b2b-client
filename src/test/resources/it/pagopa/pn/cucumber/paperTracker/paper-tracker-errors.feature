Feature: Casi di test relativi al nuovo microservizio pn-paper-tracker per il prodotto 890


  @prova
  Scenario Outline: [PAPER_TRACKER_ERROR_AR_TRY] Si verifica che per le sequence AR in cui è previsto un errore, l'errore sia effettivamente presente
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<sequence>"
    Examples:
      | sequence                       | physicalAddress                    | deliveryDetailCode | category                  | flowThrow                   | pcRetry |
      | FAIL_Consolidatore-AR         | Via@OK_AR_INVALID_DATETIME         | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |
      | OK_AR-EventAfterC               | Via@OK_AR_NO_EVENT_B               | RECRN001A          | STATUS_CODE_ERROR         | SEQUENCE_VALIDATION         | 0       |
      | OK_AR-EventNotMatchProduct          | Via@FAIL_Consolidatore-AR          | CON996             | NOT_RETRYABLE_EVENT_ERROR | NOT_RETRYABLE_EVENT_HANDLER | 1       |
      | OK_AR-NoDemat | Via@FAIL_ConsolidatoreIndirizzo-AR | CON997             | NOT_RETRYABLE_EVENT_ERROR | NOT_RETRYABLE_EVENT_HANDLER | 0       |
      | OK_890-OCR-FAIL         | Via@FAIL_CON996_PCRETRY_AR         | CON996             | NOT_RETRYABLE_EVENT_ERROR | NOT_RETRYABLE_EVENT_HANDLER | 1       |
      | OK-REC008_890-E            | Via@OK_AR_TIMESTAMP_ERR            | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |
      | FAIL_CON996_MAX_PCRETRY_AR            | Via@OK_AR_TIMESTAMP_ERR            | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |
      | OK_AR_INVALID_DATETIME            | Via@OK_AR_TIMESTAMP_ERR            | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |
      | OK-CAUSE-EVENTO-NO-MAPPA            | Via@OK_AR_TIMESTAMP_ERR            | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |
      | FAIL-CAUSE-EVENTO-NO-LISTA            | Via@OK_AR_TIMESTAMP_ERR            | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |
      | OK-Giacenza-lte10-NoARCAD_890            | Via@OK_AR_TIMESTAMP_ERR            | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |
      | FAIL_890-BadAttachment            | Via@OK_AR_TIMESTAMP_ERR            | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |
      | OK_AR_OCR_FAIL            | Via@OK_AR_TIMESTAMP_ERR            | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |




  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_ERROR_AR] Si verifica che per le sequence AR in cui è previsto un errore, l'errore sia effettivamente presente
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>"
    Then si controlla che non ci siano eventi duplicati
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore con category: <category>, flowThrow: "<flowThrow>" per la sequence: "<physicalAddress>" e pcRetry: "<pcRetry>"
    Examples:
      | sequence                       | physicalAddress                    | deliveryDetailCode | category                  | flowThrow                   | pcRetry |
      | OK_AR_INVALID_DATETIME         | Via@OK_AR_INVALID_DATETIME         | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |
      | OK_AR_NO_EVENT_B               | Via@OK_AR_NO_EVENT_B               | RECRN001A          | STATUS_CODE_ERROR         | SEQUENCE_VALIDATION         | 0       |
      | FAIL_Consolidatore-AR          | Via@FAIL_Consolidatore-AR          | CON996             | NOT_RETRYABLE_EVENT_ERROR | NOT_RETRYABLE_EVENT_HANDLER | 1       |
      | FAIL_ConsolidatoreIndirizzo-AR | Via@FAIL_ConsolidatoreIndirizzo-AR | CON997             | NOT_RETRYABLE_EVENT_ERROR | NOT_RETRYABLE_EVENT_HANDLER | 0       |
      | FAIL_CON996_PCRETRY_AR         | Via@FAIL_CON996_PCRETRY_AR         | CON996             | NOT_RETRYABLE_EVENT_ERROR | NOT_RETRYABLE_EVENT_HANDLER | 1       |
      | OK_AR_TIMESTAMP_ERR            | Via@OK_AR_TIMESTAMP_ERR            | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |

#      | sequence                       | deliveryDetailCode | expectedError                  |
#      | FAIL_Consolidatore-AR          | CON996             | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_1\",\"created\":\"2026-03-04T16:18:18.336923561Z\",\"errorCategory\":\"NOT_RETRYABLE_EVENT_ERROR\",\"details\":{\"cause\":null,\"message\":\"Scartato PDF\"},\"flowThrow\":\"NOT_RETRYABLE_EVENT_HANDLER\",\"eventThrow\":\"CON996\",\"eventIdThrow\":\"2eefbb95-6354-4891-a4d8-306fac63ac86\",\"productType\":\"AR\",\"type\":\"WARNING\"}"|
#      | OK_AR_INVALID_DATETIME         | RECRN001B          | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-04T16:08:47.936365600Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":null,\"message\":\"Invalid business timestamps\"},\"flowThrow\":\"VALUES_NOT_MATCHING\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"8f0370bf-7a0b-4a82-a158-3da87d0a61eb\",\"productType\":\"AR\",\"type\":\"ERROR\"}"|
#      | OK_AR-EventAfterC              | RECRN001A          | "" |
#      | OK_AR-EventNotMatchProduct     | RECRN001B          | "" |
#      | OK_AR-NoDemat                  | RECRN001B          | "" |
#      | FAIL_CON996_MAX_PCRETRY_AR | CON996      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_JPEZ-JZPK-AGXR-202603-T-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T18:07:55.893027160Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"STOCK_890_REFINEMENT_MISSING\",\"message\":\"invalid AWAITING_REFINEMENT state for stock 890\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG008C\",\"eventIdThrow\":\"be0ca54d-900b-4bb0-ab40-800b806a7f10\",\"productType\":\"890\",\"type\":\"ERROR\"}" |
#   da vedere perché non da errore   | FAIL-CompiutaGiacenza_AR | CON996      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_JPEZ-JZPK-AGXR-202603-T-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T18:07:55.893027160Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"STOCK_890_REFINEMENT_MISSING\",\"message\":\"invalid AWAITING_REFINEMENT state for stock 890\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG008C\",\"eventIdThrow\":\"be0ca54d-900b-4bb0-ab40-800b806a7f10\",\"productType\":\"890\",\"type\":\"ERROR\"}" |
#      | OK_AR_OCR_FAIL | RECRN001B      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-04T16:13:47.659685999Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\"},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"579ab377-b59d-4fb9-a32e-5759e536d5c7\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |



  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_ERROR_890] Si verifica che per le sequence AR in cui è previsto un errore, l'errore sia effettivamente presente
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>"
    Then si controlla che non ci siano eventi duplicati
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore con category: <category>, flowThrow: "<flowThrow>" per la sequence: "<physicalAddress>" e pcRetry: "<pcRetry>"
    Examples:
      | sequence                       | physicalAddress                    | deliveryDetailCode | category                  | flowThrow                   | pcRetry |
      | OK_AR_INVALID_DATETIME         | Via@OK_AR_INVALID_DATETIME         | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |
      | OK_AR_NO_EVENT_B               | Via@OK_AR_NO_EVENT_B               | RECRN001A          | STATUS_CODE_ERROR         | SEQUENCE_VALIDATION         | 0       |
      | FAIL_Consolidatore-AR          | Via@FAIL_Consolidatore-AR          | CON996             | NOT_RETRYABLE_EVENT_ERROR | NOT_RETRYABLE_EVENT_HANDLER | 1       |
      | FAIL_ConsolidatoreIndirizzo-AR | Via@FAIL_ConsolidatoreIndirizzo-AR | CON997             | NOT_RETRYABLE_EVENT_ERROR | NOT_RETRYABLE_EVENT_HANDLER | 0       |
      | FAIL_CON996_PCRETRY_AR         | Via@FAIL_CON996_PCRETRY_AR         | CON996             | NOT_RETRYABLE_EVENT_ERROR | NOT_RETRYABLE_EVENT_HANDLER | 1       |
      | OK_AR_TIMESTAMP_ERR            | Via@OK_AR_TIMESTAMP_ERR            | RECRN001B          | DATE_ERROR                | SEQUENCE_VALIDATION         | 0       |

#      | sequence                       | deliveryDetailCode | expectedError                  |
#      | OK_890-OCR-FAIL          | CON996             | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_1\",\"created\":\"2026-03-04T16:18:18.336923561Z\",\"errorCategory\":\"NOT_RETRYABLE_EVENT_ERROR\",\"details\":{\"cause\":null,\"message\":\"Scartato PDF\"},\"flowThrow\":\"NOT_RETRYABLE_EVENT_HANDLER\",\"eventThrow\":\"CON996\",\"eventIdThrow\":\"2eefbb95-6354-4891-a4d8-306fac63ac86\",\"productType\":\"AR\",\"type\":\"WARNING\"}"|
#      | OK-REC008_890-E | RECAG012      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_JPEZ-JZPK-AGXR-202603-T-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T18:07:55.893027160Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"STOCK_890_REFINEMENT_MISSING\",\"message\":\"invalid AWAITING_REFINEMENT state for stock 890\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG008C\",\"eventIdThrow\":\"be0ca54d-900b-4bb0-ab40-800b806a7f10\",\"productType\":\"890\",\"type\":\"ERROR\"}" |
#      | Via@OK-CAUSE-EVENTO-NO-MAPPA       | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_VRWG-GNTY-UMUT-202603-G-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T18:07:42.624754671Z\",\"errorCategory\":\"DELIVERY_FAILURE_CAUSE_ERROR\",\"details\":{\"cause\":null,\"message\":\"Invalid deliveryFailureCause: F01\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"dd6bf2bd-f3fc-49c7-ba47-78790433cd3b\",\"productType\":\"890\",\"type\":\"ERROR\"}" |
#      | Via@OK-Giacenza-lte10-NoARCAD_890       | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_VRWG-GNTY-UMUT-202603-G-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T18:07:42.624754671Z\",\"errorCategory\":\"DELIVERY_FAILURE_CAUSE_ERROR\",\"details\":{\"cause\":null,\"message\":\"Invalid deliveryFailureCause: F01\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"dd6bf2bd-f3fc-49c7-ba47-78790433cd3b\",\"productType\":\"890\",\"type\":\"ERROR\"}" |
#      | Via@FAIL_890-BadAttachment       | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_VRWG-GNTY-UMUT-202603-G-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T18:07:42.624754671Z\",\"errorCategory\":\"DELIVERY_FAILURE_CAUSE_ERROR\",\"details\":{\"cause\":null,\"message\":\"Invalid deliveryFailureCause: F01\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"dd6bf2bd-f3fc-49c7-ba47-78790433cd3b\",\"productType\":\"890\",\"type\":\"ERROR\"}" |


#  NON FATTIBILE
#  @paperTrackerARRunMode
#  Scenario Outline: [PAPER_TRACKER_ERROR_1] Viene creato un evento con timestamp null e si verifica che si ottiene un errore del tipo DATE_ERROR - INVALID_VALUES
##    Given viene generata una nuova notifica
##      | subject               | invio notifica con cucumber |
##      | senderDenomination    | Comune di Palermo           |
##      | physicalCommunication | AR_REGISTERED_LETTER        |
##    And destinatario Mario Cucumber e:
##      | physicalAddress_address | Via@OK_AR_BLOCKED |
##      | digitalDomicile         | NULL              |
##    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
##    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
#    And imposto lo iun di SharedSteps a "DTWA-GDPT-EVGM-202603-P-1" e la pa a "Comune_Multi"
##    Then viene invocato il consolidatore con i seguenti dati:
##      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
##      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              |
#    Then viene invocato il consolidatore con i seguenti dati:
#      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | timestamp |
#      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              | <null>    |
#    Then viene invocato il consolidatore con i seguenti dati:
#      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
#      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              |
#    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
#    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: "<expectedError>"
#    Examples:
#      | expectedError          |
#      | OK_AR_INVALID_DATETIME |

    #  "additionalDetails": {
#  "affectedEvents": [
  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_ERROR_2] Viene creato un evento con registeredLetterCode differente dagli altri e si verifica che si ottiene un errore del tipo REGISTERED_LETTER_CODE_ERROR - VALUES_NOT_MATCHING
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_BLOCKED |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode             |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              | f234b2b0e6a0413cbcab5a41ae6f94d2 |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode             |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              | aaaaaaaaaaaa413cbcab5a41ae6f94d2 |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode             |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              | f234b2b0e6a0413cbcab5a41ae6f94d2 |
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T11:15:06.259165821Z\",\"errorCategory\":\"REGISTERED_LETTER_CODE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Registered letter codes do not match in sequence: [f234b2b0e6a0413cbcab5a41ae6f94d2, aaaaaaaaaaaa413cbcab5a41ae6f94d2, f234b2b0e6a0413cbcab5a41ae6f94d2]\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-06T11:15:02Z\",\"statusCode\":\"RECRN001A\"},{\"statusTimestamp\":\"2026-03-06T11:15:02Z\",\"statusCode\":\"RECRN001B\"},{\"statusTimestamp\":\"2026-03-06T11:15:02Z\",\"statusCode\":\"RECRN001C\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"b4cb008b-d581-4f66-8078-87d478aa9832\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |

#  "additionalDetails": {
#  "affectedEvents": [
  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_ERROR_3] Viene creato un evento con registeredLetterCode uguale a null e si verifica che si ottiene un errore del tipo REGISTERED_LETTER_CODE_ERROR - INVALID_VALUES
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber |
#      | senderDenomination    | Comune di Palermo           |
#      | physicalCommunication | AR_REGISTERED_LETTER        |
#    And destinatario Mario Cucumber e:
#      | physicalAddress_address | Via@OK_AR_BLOCKED |
#      | digitalDomicile         | NULL              |
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And imposto lo iun di SharedSteps a "MEZL-YTPM-GHXQ-202603-J-1" e la pa a "Comune_Multi"
#    Then viene invocato il consolidatore con i seguenti dati:
#      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode |
#      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              | <null>               |
#    Then viene invocato il consolidatore con i seguenti dati:
#      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode             |
#      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              | f234b2b0e6a0413cbcab5a41ae6f94d2 |
#    Then viene invocato il consolidatore con i seguenti dati:
#      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode             |
#      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              | f234b2b0e6a0413cbcab5a41ae6f94d2 |
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T14:38:52.967336996Z\",\"errorCategory\":\"REGISTERED_LETTER_CODE_ERROR\",\"details\":{\"cause\":\"INVALID_VALUES\",\"message\":\"Registered letter code is null or empty in one or more events\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-06T14:38:48Z\",\"statusCode\":\"RECRN001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"ebfdcb41-3c6b-45b2-99cc-99468db5314d\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |




