Feature: Casi di test relativi al nuovo microservizio pn-paper-tracker per il prodotto 890

# da lanciare con OCR in DRY perché in questo caso gli errori con categoria OCR_VALIDATION
#  vengono considerati warning e non error
  @paperTrackerAR @trackerErrors @ocrDRY
  Scenario Outline: [PAPER_TRACKER_ERROR_AR-DRY] In caso di OCR in modalità DRY, si verifica che per le sequence AR in cui è previsto un errore, l'errore sia effettivamente presente ma con type WARNING invece che ERROR
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence       | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
      | OK_AR_OCR_FAIL | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:35.246856627Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\",\"additionalDetails\":{\"ocrDataResultPayload\":{\"predictedRefinementType\":\"\",\"validationType\":\"ai\",\"description\":\"validazione fallita\",\"validationStatus\":\"KO\"}}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"2f428c7d-99f5-490c-a9fd-d6132c1589a2\",\"productType\":\"AR\",\"type\":\"WARNING\"}" |

  # da lanciare con OCR in RUN perché in questo caso gli errori con categoria OCR_VALIDATION
  # vengono considerati ERROR e non WARNING
  @paperTrackerARRunMode @trackerErrors @ocrRun
  Scenario Outline: [PAPER_TRACKER_ERROR_AR-RUN] In caso di OCR in modalità RUN, si verifica che per le sequence AR in cui è previsto un errore, l'errore sia effettivamente presente ma con type ERROR invece che WARNING
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence       | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | OK_AR_OCR_FAIL | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:35.246856627Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\",\"additionalDetails\":{\"ocrDataResultPayload\":{\"predictedRefinementType\":\"\",\"validationType\":\"ai\",\"description\":\"validazione fallita\",\"validationStatus\":\"KO\"}}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"2f428c7d-99f5-490c-a9fd-d6132c1589a2\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |

  # da lanciare con OCR in DRY perché in questo caso gli errori con categoria OCR_VALIDATION
  # vengono considerati warning e non error
  @paperTracker890 @trackerErrors @ocrDRY
  Scenario Outline: [PAPER_TRACKER_ERROR_890-DRY] In caso di OCR in modalità DRY, si verifica che per le sequence 890 in cui è previsto un errore, l'errore sia effettivamente presente ma con type WARNING invece che ERROR
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence        | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | OK_890-OCR-FAIL | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:32.199663469Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\",\"additionalDetails\":{\"ocrDataResultPayload\":{\"predictedRefinementType\":\"\",\"validationType\":\"ai\",\"description\":\"validazione fallita\",\"validationStatus\":\"KO\"}}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"b287f2fe-f4fd-46d2-91df-b72076132682\",\"productType\":\"890\",\"type\":\"WARNING\"}" |

  # da lanciare con OCR in RUN perché in questo caso gli errori con categoria OCR_VALIDATION
  # vengono considerati ERROR e non WARNING
  @paperTrackerRunMode890 @trackerErrors @ocrRun
  Scenario Outline: [PAPER_TRACKER_ERROR_890-RUN] n caso di OCR in modalità RUN, si verifica che per le sequence 890 in cui è previsto un errore, l'errore sia effettivamente presente ma con type ERROR invece che WARNING
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence        | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | OK_890-OCR-FAIL | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:32.199663469Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\",\"additionalDetails\":{\"ocrDataResultPayload\":{\"predictedRefinementType\":\"\",\"validationType\":\"ai\",\"description\":\"validazione fallita\",\"validationStatus\":\"KO\"}}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"b287f2fe-f4fd-46d2-91df-b72076132682\",\"productType\":\"890\",\"type\":\"ERROR\"}" |

# --------------- Si testano gli errori per prodotto AR che non cambiano il tipo in base a STRICTFINALVALIDATIONSTOCK890 --------------

  @paperTrackerARRunMode @alwaysRun @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_AR_2]
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                     | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | OK_AR-EventAfterC            | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-20T08:37:16.472000447Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUE_AFTER_REFINEMENT\",\"message\":\"Tracking in state DONE, statusCode RECRN001A: PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"additionalDetails\":{\"statusTimestamp\":\"2026-03-20T08:37:13Z\",\"statusCode\":\"RECRN001A\"}},\"flowThrow\":\"CHECK_TRACKING_STATE\",\"eventThrow\":\"RECRN001A\",\"eventIdThrow\":\"eb4d9cce-b62b-4055-955f-04480f4dc31b\",\"productType\":\"AR\",\"type\":\"WARNING\"}"                                                                               |
      | OK_AR-EventNotMatchProduct   | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-20T08:51:57.541298799Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Product type mismatch for trackingId PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0: expected AR, but got 890\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-20T08:51:55Z\",\"statusCode\":\"RECAG001A\"}]}},\"flowThrow\":\"CHECK_TRACKING_PRODUCT\",\"eventThrow\":\"RECAG001A\",\"eventIdThrow\":\"aa37a8de-3ca0-44ee-8d87-1ac99d736702\",\"productType\":\"AR\",\"type\":\"WARNING\"}"                                        |
      | OK_AR-NoDemat                | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:20:40.321106838Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_FOUND\",\"message\":\"Necessary status code not found in events: [RECRN001B]\",\"additionalDetails\":{\"missingStatusCodes\":\"RECRN001B\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"8540a381-2102-4c57-83bd-67373a3f5b47\",\"productType\":\"AR\",\"type\":\"ERROR\"}"                                                                                                                                                                                     |
      | FAIL_Consolidatore-AR        | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_1\",\"created\":\"2026-03-11T12:20:00.839161059Z\",\"errorCategory\":\"NOT_RETRYABLE_EVENT_ERROR\",\"details\":{\"message\":\"Scartato PDF\",\"additionalDetails\":null},\"flowThrow\":\"NOT_RETRYABLE_EVENT_HANDLER\",\"eventThrow\":\"CON996\",\"eventIdThrow\":\"d0538f9f-6ff6-49b7-bb94-b20ea20d1aa5\",\"productType\":\"AR\",\"type\":\"WARNING\"}"                                                                                                                                                                                                                                                                                  |
      | OK_AR_INVALID_DATETIME       | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:34.355430449Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-06T15:16:31Z\",\"statusCode\":\"RECRN001C\"},{\"statusTimestamp\":\"2026-03-06T15:16:26Z\",\"statusCode\":\"RECRN001B\"},{\"statusTimestamp\":\"2026-03-06T15:16:19Z\",\"statusCode\":\"RECRN001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"1140908a-4f8d-4580-a42f-09458520f850\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |
      | FAIL_RECRN006_MAX_PCRETRY_AR | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_4\",\"created\":\"2026-03-11T12:30:13.838822572Z\",\"errorCategory\":\"MAX_RETRY_REACHED_ERROR\",\"details\":{\"message\":\"Retry not found for trackingId: PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_4\",\"additionalDetails\":null},\"flowThrow\":\"RETRY_PHASE\",\"eventThrow\":\"RECRN006\",\"eventIdThrow\":\"bf6522f1-5d37-4d80-a5af-e0a1b86638b0\",\"productType\":\"AR\",\"type\":\"ERROR\"}"                                                                                                                                                                                                                |
      | FAIL_CompiutaGiacenza_AR_ERR | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-10T13:32:35.095767839Z\",\"errorCategory\":\"RENDICONTAZIONE_SCARTATA\",\"details\":{\"cause\":\"GIACENZA_DATE_ERROR\",\"message\":\"RECRN005A getStatusTimestamp: <date>, RECRN010 getStatusTimestamp: <date>\",\"additionalDetails\":{\"recrn005aTimestamp\":\"2026-03-10T13:32:18Z\",\"recrn010Timestamp\":\"2026-03-10T13:32:05Z\"}},\"flowThrow\":\"FINAL_EVENT_BUILDING\",\"eventThrow\":\"RECRN005C\",\"eventIdThrow\":\"234b336e-af38-483c-9bd3-4d7d8cd3740b\",\"productType\":\"AR\",\"type\":\"ERROR\"}"                                                                                              |


# --------------- Si testano gli errori per prodotto 890 che non cambiano il tipo in base a STRICTFINALVALIDATIONSTOCK890 --------------

  @paperTrackerARRunMode @trackerErrors @ocrRun
  Scenario Outline: [PAPER_TRACKER_ERROR_AR_2_A] Viene verificato che in caso di errore INCONSISTENT_STATE - STOCK_890_REFINEMENT_ERROR se strictFinalValidationStock890 è true/false, venga generato un errore di tipo ERROR
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_BLOCKED |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG010   |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG011A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG011B  |                      | 23L          |              | OCR_KO               |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG012   |                      |              |              | OCR_KO               |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011B"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG005C  |                      |              |              | OCR_KO               |
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-17T18:03:26.527501319Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"STOCK_890_REFINEMENT_ERROR\",\"message\":\"Refinement process reached KO state, cannot proceed with final event validation\",\"additionalDetails\":{\"statusTimestamp\":\"2026-03-17T17:59:18Z\",\"statusCode\":\"RECAG005C\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG005C\",\"eventIdThrow\":\"df67092f-5cc1-4d8c-a149-c2a35c260b2c\",\"productType\":\"890\",\"type\":\"ERROR\"}" |

  @paperTrackerRunMode890 @trackerErrors @ocrRun
  Scenario Outline: [PAPER_TRACKER_ERROR_890-RUN.2] In caso di OCR in modalità RUN, si verifica che per la sequence OK-Giacenza-lte10_890-OCR-FAIL si verifichi un errore con type ERROR con category INCONSISTENT_STATE e cause STOCK_890_REFINEMENT_MISSING
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                       | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | OK-Giacenza-lte10_890-OCR-FAIL | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-27T14:00:15.885757053Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"STOCK_890_REFINEMENT_ERROR\",\"message\":\"Refinement process reached KO state, cannot proceed with final event validation\",\"additionalDetails\":{\"statusTimestamp\":\"2026-03-27T13:59:52Z\",\"statusCode\":\"RECAG005C\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG005C\",\"eventIdThrow\":\"87623121-90b2-4a17-84b2-bd593a23c978\",\"productType\":\"890\",\"type\":\"ERROR\"}" |

  @paperTrackerRunMode890 @alwaysRun @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_1]
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence        | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | OK-REC008_890-E | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:42.533203161Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"STOCK_890_REFINEMENT_MISSING\",\"message\":\"invalid AWAITING_REFINEMENT state for stock 890\",\"additionalDetails\":{\"statusTimestamp\":\"2026-03-06T15:56:30Z\",\"statusCode\":\"RECAG008C\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG008C\",\"eventIdThrow\":\"a91b4acc-51d6-4a52-8c91-b8b21cdd6223\",\"productType\":\"890\",\"type\":\"ERROR\"}" |

  @paperTrackerRunMode890 @alwaysRun @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_1.A] Si verifica che per le sequence OK-CAUSE-EVENTO-NO-MAPPA e OK_890-NoAttachment siano presenti gli errori attesi
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001A"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                 | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
      | OK-CAUSE-EVENTO-NO-MAPPA | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:31.698620001Z\",\"errorCategory\":\"DELIVERY_FAILURE_CAUSE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid deliveryFailureCause: F01\",\"additionalDetails\":{\"affectedEvents\":[{\"deliveryFailureCause\":\"F01\",\"statusTimestamp\":\"2026-03-06T15:56:16Z\",\"statusCode\":\"RECAG001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"1c7bfd2b-5e88-4383-8f93-03fca35c397c\",\"productType\":\"890\",\"type\":\"ERROR\"}" |
      | OK_890-NoAttachment      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-16T11:22:34.265292550Z\",\"errorCategory\":\"ATTACHMENTS_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Missed required attachments for the sequence validation: [23L]\",\"additionalDetails\":{\"missingAttachments\":[\"23L\"]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"4c67f8a2-16be-4994-a109-43ec1a054fa3\",\"productType\":\"890\",\"type\":\"ERROR\"}"                                                                                |

  @paperTrackerRunMode890 @alwaysRun @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_1.C]
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                 | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
      | OK-CAUSE-EVENTO-NO-MAPPA | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:31.698620001Z\",\"errorCategory\":\"DELIVERY_FAILURE_CAUSE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid deliveryFailureCause: F01\",\"additionalDetails\":{\"affectedEvents\":[{\"deliveryFailureCause\":\"F01\",\"statusTimestamp\":\"2026-03-06T15:56:16Z\",\"statusCode\":\"RECAG001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"1c7bfd2b-5e88-4383-8f93-03fca35c397c\",\"productType\":\"890\",\"type\":\"ERROR\"}" |
      | OK_890-NoAttachment      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-16T11:22:34.265292550Z\",\"errorCategory\":\"ATTACHMENTS_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Missed required attachments for the sequence validation: [23L]\",\"additionalDetails\":{\"missingAttachments\":[\"23L\"]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"4c67f8a2-16be-4994-a109-43ec1a054fa3\",\"productType\":\"890\",\"type\":\"ERROR\"}"                                                                                |

  @paperTrackerRunMode890 @alwaysRun @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_1.B] Si verifica che per la sequence OK_890_INVALID_DATETIME, in cui è previsto un errore di DATE_ERROR, l'errore sia effettivamente presente con type ERROR e con category DATE_ERROR
  non viene effettuata la validazione della risposta /trackings poiché non passerebbe a causa dell'errore di datetime, ma si verifica direttamente la presenza dell'errore in PaperTrackingsError
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_890_INVALID_DATETIME |
      | digitalDomicile         | NULL                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-13T14:22:52.310368102Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-13T14:22:50Z\",\"statusCode\":\"RECAG001C\"},{\"statusTimestamp\":\"2026-03-13T14:22:45Z\",\"statusCode\":\"RECAG001B\"},{\"statusTimestamp\":\"2026-03-13T14:22:40Z\",\"statusCode\":\"RECAG001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"1cf150b9-f18c-4abb-a5ae-6350677c43a1\",\"productType\":\"890\",\"type\":\"ERROR\"}" |

# --------------- Si testano gli errori per prodotto 890 che CAMBIANO il tipo in base a STRICTFINALVALIDATIONSTOCK890 == FALSE --------------

  @paperTrackerRunMode890 @strictFinalValidationFalse @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_2] Il tipo di errore ritornato contiene type WARNING invece che ERROR, a causa del fatto che strictFinalValidation è false
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
      | OK-Giacenza-lte10-NoARCAD_890 | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-16T11:23:14.636011832Z\",\"errorCategory\":\"ATTACHMENTS_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Missed required attachments for the sequence validation: [CAD, ARCAD]\",\"additionalDetails\":{\"missingAttachments\":[\"CAD\",\"ARCAD\"]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG005C\",\"eventIdThrow\":\"179bffce-2e30-4042-8aa7-2c1accd3bee1\",\"productType\":\"890\",\"type\":\"WARNING\"}" |

  @paperTrackerRunMode890 @strictFinalValidationFalse @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_2.B] Si verifica che per la sequence OK-Giacenza-INVALID_DATETIME_890, in cui è previsto un errore di DATE_ERROR,
  l'errore sia effettivamente presente con type WARNING e con category DATE_ERROR a causa del fatto che strictFinalValidation è false
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK-Giacenza-INVALID_DATETIME_890 |
      | digitalDomicile         | NULL                                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "OK-Giacenza-INVALID_DATETIME_890"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-16T11:28:12.401123047Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-16T11:28:09Z\",\"statusCode\":\"RECAG005C\"},{\"statusTimestamp\":\"2026-03-16T11:28:04Z\",\"statusCode\":\"RECAG005B\"},{\"statusTimestamp\":\"2026-03-16T11:27:58Z\",\"statusCode\":\"RECAG005A\"},{\"statusTimestamp\":\"2026-03-16T11:27:23Z\",\"statusCode\":\"RECAG011A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG005C\",\"eventIdThrow\":\"f1ef2a9e-98b8-4d13-ac93-7f4055e98420\",\"productType\":\"890\",\"type\":\"WARNING\"}" |

  @paperTrackerRunMode890 @strictFinalValidationFalse @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_2_A] Si verifica che andando a simulare una giacenza con attachment non validi si abbia un errore ATTACHMENTS_ERROR - INVALID_VALUES (FAIL_890-BadAttachment)
  ed un errore di tipo WARNING poiché strictFinalValidation è settato a false
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_BLOCKED |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG010   |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG011A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG012   |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG011B  |                      | 23L          |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG011B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG011B  |                      | ARCAD        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG005A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG005C  |                      |              |              |
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-16T21:00:34.953135302Z\",\"errorCategory\":\"ATTACHMENTS_ERROR\",\"details\":{\"cause\":\"INVALID_VALUES\",\"message\":\"Event RECAG011B contains invalid attachments: [AR]\",\"additionalDetails\":{\"invalidAttachments\":[\"AR\"]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG005C\",\"eventIdThrow\":\"f7266d4a-afef-4965-b8fb-dd69df1ab569\",\"productType\":\"890\",\"type\":\"WARNING\"}" |


# --------------- Si testano gli errori per prodotto 890 che CAMBIANO il tipo in base a STRICTFINALVALIDATIONSTOCK890 == TRUE --------------

  @paperTracker890 @strictFinalValidationTrue @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_3] Il tipo di errore ritornato contiene type ERROR invece che WARNING, a causa del fatto che strictFinalValidation è true
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence            | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | OK_890-NoAttachment | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-09T11:08:10.482400710Z\",\"errorCategory\":\"ATTACHMENTS_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Missed required attachments for the sequence validation: [23L]\",\"additionalDetails\":{\"missingAttachments\":\"23L\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"518d05f3-561b-4741-852b-07aec9038e44\",\"productType\":\"890\",\"type\":\"ERROR\"}" |

  @paperTracker890 @strictFinalValidationTrue @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_3] Si verifica che con la sequence OK-Giacenza-INVALID_DATETIME_890 e avendo la property strictFinalValidation a true,
  il tipo di errore ritornato contiene type ERROR invece che WARNING
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK-Giacenza-INVALID_DATETIME_890 |
      | digitalDomicile         | NULL                                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-17T08:31:51.022386721Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-17T08:31:48Z\",\"statusCode\":\"RECAG005C\"},{\"statusTimestamp\":\"2026-03-17T08:31:43Z\",\"statusCode\":\"RECAG005B\"},{\"statusTimestamp\":\"2026-03-17T08:31:37Z\",\"statusCode\":\"RECAG005A\"},{\"statusTimestamp\":\"2026-03-17T08:31:02Z\",\"statusCode\":\"RECAG011A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG005C\",\"eventIdThrow\":\"91fe5e8c-9740-4d81-8d34-ed36290b4cca\",\"productType\":\"890\",\"type\":\"ERROR\"}" |


  @paperTracker890 @strictFinalValidationTrue @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_2_ZZZ] Si verifica che andando a simulare una giacenza con attachment non validi si abbia un errore ATTACHMENTS_ERROR - INVALID_VALUES (FAIL_890-BadAttachment)
  ed un errore di tipo ERROR poiché strictFinalValidation è settato a true
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_BLOCKED |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG010   |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG011A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG012   |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG011B  |                      | 23L          |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG011B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG011B  |                      | ARCAD        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG005A  |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG005C  |                      |              |              |
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-16T21:00:34.953135302Z\",\"errorCategory\":\"ATTACHMENTS_ERROR\",\"details\":{\"cause\":\"INVALID_VALUES\",\"message\":\"Event RECAG011B contains invalid attachments: [AR]\",\"additionalDetails\":{\"invalidAttachments\":[\"AR\"]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG005C\",\"eventIdThrow\":\"f7266d4a-afef-4965-b8fb-dd69df1ab569\",\"productType\":\"890\",\"type\":\"ERROR\"}" |



# --------------- Si testano gli errori a seguito di inserimento tramite consolidatore  --------------

  @paperTrackerARRunMode @trackerErrors
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

  @paperTrackerARRunMode @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_3] Viene creato un evento con registeredLetterCode uguale a null e si verifica che si ottiene un errore del tipo REGISTERED_LETTER_CODE_ERROR - INVALID_VALUES
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
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |                      |              |              | <null>               |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode             |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001B  |                      | AR           |              | f234b2b0e6a0413cbcab5a41ae6f94d2 |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 | registeredLetterCode             |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |                      |              |              | f234b2b0e6a0413cbcab5a41ae6f94d2 |
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T14:38:52.967336996Z\",\"errorCategory\":\"REGISTERED_LETTER_CODE_ERROR\",\"details\":{\"cause\":\"INVALID_VALUES\",\"message\":\"Registered letter code is null or empty in one or more events\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-06T14:38:48Z\",\"statusCode\":\"RECRN001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"ebfdcb41-3c6b-45b2-99cc-99468db5314d\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |


  @paperTrackerARRunMode @trackerErrors @alwaysRun
  Scenario Outline: [PAPER_TRACKER_ERROR_4.A] Vengono creati eventi RECRI004B con deliveryFailureCause uguale a M01, M03, M04, M06, M07, M08, M09 e si verifica che si ottiene un SEND_ANALOG_FEEDBACK con responseStatus uguale a KO
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_BLOCKED |
      | digitalDomicile         | NULL              |
      | physicalAddress_State   | MESSICO           |
      | physicalAddress_zip     | ZONE_2            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI001   |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI002   |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause   | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI004A  | <deliveryFailureCause> |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause   | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI004B  | <deliveryFailureCause> | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI004C  |                      |              |              |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline               | true      |
      | details                    | NOT_NULL  |
      | details_deliveryDetailCode | RECRI004C |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
      | details_responseStatus     | KO        |
    Examples:
      | deliveryFailureCause |
      | M01                  |
      | M03                  |
      | M04                  |
      | M06                  |
      | M07                  |
      | M08                  |
      | M09                  |

  @paperTrackerARRunMode @trackerErrors @alwaysRun
  Scenario Outline: [PAPER_TRACKER_ERROR_4.B] Vengono creati eventi RECRI004B con deliveryFailureCause uguale a M02, M05 e si verifica che si ottiene un SEND_ANALOG_FEEDBACK con responseStatus uguale a OK
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_BLOCKED |
      | digitalDomicile         | NULL              |
      | physicalAddress_State   | MESSICO           |
      | physicalAddress_zip     | ZONE_2            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI001   |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI002   |                      |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause   | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI004A  | <deliveryFailureCause> |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause   | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI004B  | <deliveryFailureCause> | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause   | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI004C  | <deliveryFailureCause> |              |              |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline               | true      |
      | details                    | NOT_NULL  |
      | details_deliveryDetailCode | RECRI004C |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
      | details_responseStatus     | OK        |
    Examples:
      | deliveryFailureCause |
      | M02                  |
      | M05                  |



