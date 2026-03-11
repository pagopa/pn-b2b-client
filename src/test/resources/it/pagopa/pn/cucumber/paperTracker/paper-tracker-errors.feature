Feature: Casi di test relativi al nuovo microservizio pn-paper-tracker per il prodotto 890

# da lanciare con OCR in DRY perché in questo caso gli errori con categoria OCR_VALIDATION
#  vengono considerati warning e non error
  @paperTrackerARRunMode @trackerErrors @ocrDRY
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence        | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | OK_890-OCR-FAIL | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:32.199663469Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\",\"additionalDetails\":{\"ocrDataResultPayload\":{\"predictedRefinementType\":\"\",\"validationType\":\"ai\",\"description\":\"validazione fallita\",\"validationStatus\":\"KO\"}}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"b287f2fe-f4fd-46d2-91df-b72076132682\",\"productType\":\"890\",\"type\":\"WARNING\"}" |

  # da lanciare con OCR in RUN perché in questo caso gli errori con categoria OCR_VALIDATION
  # vengono considerati ERROR e non WARNING
  @paperTracker890 @trackerErrors @ocrRun
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
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence        | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | OK_890-OCR-FAIL | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:32.199663469Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\",\"additionalDetails\":{\"ocrDataResultPayload\":{\"predictedRefinementType\":\"\",\"validationType\":\"ai\",\"description\":\"validazione fallita\",\"validationStatus\":\"KO\"}}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"b287f2fe-f4fd-46d2-91df-b72076132682\",\"productType\":\"890\",\"type\":\"ERROR\"}" |
# da provare il risultato atteso con OCR in RUN      | OK-Giacenza-lte10_890-OCR-FAIL | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:32.199663469Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\",\"additionalDetails\":{\"ocrDataResultPayload\":{\"predictedRefinementType\":\"\",\"validationType\":\"ai\",\"description\":\"validazione fallita\",\"validationStatus\":\"KO\"}}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"b287f2fe-f4fd-46d2-91df-b72076132682\",\"productType\":\"890\",\"type\":\"ERROR\"}" |


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
      | OK_AR-EventAfterC            | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T17:01:52.990505975Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUE_AFTER_REFINEMENT\",\"message\":\"Tracking in state DONE, statusCode RECRN001A: PREPARE_ANALOG_DOMICILE.IUN_ZETK-HUQD-LDTJ-202603-D-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"additionalDetails\":{\"statusTimestamp\":\"2026-03-06T17:01:50Z\",\"statusCode\":\"RECRN001A\"}},\"flowThrow\":null,\"eventThrow\":\"RECRN001A\",\"eventIdThrow\":\"3569e412-ac21-44ee-bc54-d6c92136ed27\",\"productType\":\"AR\",\"type\":\"WARNING\"}"                                                                               |
      | OK_AR-EventNotMatchProduct   | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:35.386693107Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Product type mismatch for trackingId PREPARE_ANALOG_DOMICILE.IUN_PZKT-HGWT-GYWT-202603-Z-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0: expected AR, but got 890\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-06T15:16:33Z\",\"statusCode\":\"RECAG001A\"}]}},\"eventThrow\":\"RECAG001A\",\"eventIdThrow\":\"c61d4935-339a-4626-8e91-901b2919e4cb\",\"productType\":\"AR\",\"type\":\"ERROR\"}"                                                               |
      | OK_AR-NoDemat                | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:20:40.321106838Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_FOUND\",\"message\":\"Necessary status code not found in events: [RECRN001B]\",\"additionalDetails\":{\"missingStatusCodes\":\"RECRN001B\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"8540a381-2102-4c57-83bd-67373a3f5b47\",\"productType\":\"AR\",\"type\":\"ERROR\"}"                                                                                                                                                                                     |
      | FAIL_Consolidatore-AR        | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_1\",\"created\":\"2026-03-06T15:16:38.043341956Z\",\"errorCategory\":\"NOT_RETRYABLE_EVENT_ERROR\",\"details\":{\"message\":\"Scartato PDF\",\"additionalDetails\":null},\"flowThrow\":\"NOT_RETRYABLE_EVENT_HANDLER\",\"eventThrow\":\"CON996\",\"eventIdThrow\":\"6bee834b-b904-40e8-836e-755741032fe8\",\"productType\":\"AR\",\"type\":\"WARNING\"}"                                                                                                                                                                                                                                                                                  |
      | OK_AR_INVALID_DATETIME       | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:34.355430449Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-06T15:16:31Z\",\"statusCode\":\"RECRN001C\"},{\"statusTimestamp\":\"2026-03-06T15:16:26Z\",\"statusCode\":\"RECRN001B\"},{\"statusTimestamp\":\"2026-03-06T15:16:19Z\",\"statusCode\":\"RECRN001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"1140908a-4f8d-4580-a42f-09458520f850\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |
      | FAIL_RECRN006_MAX_PCRETRY_AR | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:34.355430449Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-06T15:16:31Z\",\"statusCode\":\"RECRN001C\"},{\"statusTimestamp\":\"2026-03-06T15:16:26Z\",\"statusCode\":\"RECRN001B\"},{\"statusTimestamp\":\"2026-03-06T15:16:19Z\",\"statusCode\":\"RECRN001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"1140908a-4f8d-4580-a42f-09458520f850\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |
      | FAIL_CompiutaGiacenza_AR_ERR | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-10T13:32:35.095767839Z\",\"errorCategory\":\"RENDICONTAZIONE_SCARTATA\",\"details\":{\"cause\":\"GIACENZA_DATE_ERROR\",\"message\":\"RECRN005A getStatusTimestamp: 2026-03-10T13:32:18Z, RECRN010 getStatusTimestamp: 2026-03-10T13:32:05Z\",\"additionalDetails\":{\"recrn005aTimestamp\":\"2026-03-10T13:32:18Z\",\"recrn010Timestamp\":\"2026-03-10T13:32:05Z\"}},\"flowThrow\":\"FINAL_EVENT_BUILDING\",\"eventThrow\":\"RECRN005C\",\"eventIdThrow\":\"234b336e-af38-483c-9bd3-4d7d8cd3740b\",\"productType\":\"AR\",\"type\":\"ERROR\"}"                                                                  |


# --------------- Si testano gli errori per prodotto 890 che non cambiano il tipo in base a STRICTFINALVALIDATIONSTOCK890 --------------


  @paperTracker890 @alwaysRun @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_1]
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    And imposto lo iun di SharedSteps a "<iun>" e la pa a "Comune_Multi"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | iun                       | sequence        | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
# non genera errore atteso      |UXYH-DVDX-TPEN-202603-K-1| FAIL-CAUSE-EVENTO-NO-LISTA | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:35.386693107Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Product type mismatch for trackingId PREPARE_ANALOG_DOMICILE.IUN_PZKT-HGWT-GYWT-202603-Z-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0: expected AR, but got 890\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-06T15:16:33Z\",\"statusCode\":\"RECAG001A\"}]}},\"eventThrow\":\"RECAG001A\",\"eventIdThrow\":\"c61d4935-339a-4626-8e91-901b2919e4cb\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |
      | YLNU-LVPE-DMQN-202603-W-1 | OK-REC008_890-E | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:42.533203161Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"STOCK_890_REFINEMENT_MISSING\",\"message\":\"invalid AWAITING_REFINEMENT state for stock 890\",\"additionalDetails\":{\"statusTimestamp\":\"2026-03-06T15:56:30Z\",\"statusCode\":\"RECAG008C\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG008C\",\"eventIdThrow\":\"a91b4acc-51d6-4a52-8c91-b8b21cdd6223\",\"productType\":\"890\",\"type\":\"ERROR\"}" |


# --------------- Si testano gli errori per prodotto 890 che CAMBIANO il tipo in base a STRICTFINALVALIDATIONSTOCK890 == FALSE --------------

  @paperTracker890 @strictFinalValidationFalse @trackerErrors
  Scenario Outline: [PAPER_TRACKER_ERROR_890_2] Il tipo di errore ritornato contiene type WARNING invece che ERROR, a causa del fatto che strictFinalValidation è false
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    And imposto lo iun di SharedSteps a "<iun>" e la pa a "Comune_Multi"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
      | OK-Giacenza-lte10-NoARCAD_890 | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:35.386693107Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Product type mismatch for trackingId PREPARE_ANALOG_DOMICILE.IUN_PZKT-HGWT-GYWT-202603-Z-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0: expected AR, but got 890\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-06T15:16:33Z\",\"statusCode\":\"RECAG001A\"}]}},\"eventThrow\":\"RECAG001A\",\"eventIdThrow\":\"c61d4935-339a-4626-8e91-901b2919e4cb\",\"productType\":\"AR\",\"type\":\"WARNING\"}" |
      | FAIL_890-BadAttachment        | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:36.709420378Z\",\"errorCategory\":\"ATTACHMENTS_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Missed required attachments for the sequence validation: [Plico]\",\"additionalDetails\":{\"missingAttachments\":\"Plico\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG003C\",\"eventIdThrow\":\"e77035bd-78fd-4ba2-909e-5f7c3303512c\",\"productType\":\"890\",\"type\":\"WARNING\"}"                                                                                                              |
      | OK-CAUSE-EVENTO-NO-MAPPA      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:31.698620001Z\",\"errorCategory\":\"DELIVERY_FAILURE_CAUSE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid deliveryFailureCause: F01\",\"additionalDetails\":{\"affectedEvents\":[{\"deliveryFailureCause\":\"F01\",\"statusTimestamp\":\"2026-03-06T15:56:16Z\",\"statusCode\":\"RECAG001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"1c7bfd2b-5e88-4383-8f93-03fca35c397c\",\"productType\":\"890\",\"type\":\"WARNING\"}"                                 |
      | OK_890-NoAttachment           | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-09T11:08:10.482400710Z\",\"errorCategory\":\"ATTACHMENTS_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Missed required attachments for the sequence validation: [23L]\",\"additionalDetails\":{\"missingAttachments\":\"23L\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"518d05f3-561b-4741-852b-07aec9038e44\",\"productType\":\"890\",\"type\":\"WARNING\"}"                                                                                                                  |


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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                 | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
# non genera errori      |KAGH-JWQX-GRWR-202603-P-1| OK-Giacenza-lte10-NoARCAD_890 | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:35.386693107Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Product type mismatch for trackingId PREPARE_ANALOG_DOMICILE.IUN_PZKT-HGWT-GYWT-202603-Z-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0: expected AR, but got 890\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-06T15:16:33Z\",\"statusCode\":\"RECAG001A\"}]}},\"eventThrow\":\"RECAG001A\",\"eventIdThrow\":\"c61d4935-339a-4626-8e91-901b2919e4cb\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |
      | FAIL_890-BadAttachment   | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:36.709420378Z\",\"errorCategory\":\"ATTACHMENTS_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Missed required attachments for the sequence validation: [Plico]\",\"additionalDetails\":{\"missingAttachments\":\"Plico\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG003C\",\"eventIdThrow\":\"e77035bd-78fd-4ba2-909e-5f7c3303512c\",\"productType\":\"890\",\"type\":\"ERROR\"}"                                                                              |
      | OK-CAUSE-EVENTO-NO-MAPPA | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:31.698620001Z\",\"errorCategory\":\"DELIVERY_FAILURE_CAUSE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid deliveryFailureCause: F01\",\"additionalDetails\":{\"affectedEvents\":[{\"deliveryFailureCause\":\"F01\",\"statusTimestamp\":\"2026-03-06T15:56:16Z\",\"statusCode\":\"RECAG001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"1c7bfd2b-5e88-4383-8f93-03fca35c397c\",\"productType\":\"890\",\"type\":\"ERROR\"}" |
      | OK_890-NoAttachment      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-09T11:08:10.482400710Z\",\"errorCategory\":\"ATTACHMENTS_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Missed required attachments for the sequence validation: [23L]\",\"additionalDetails\":{\"missingAttachments\":\"23L\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG001C\",\"eventIdThrow\":\"518d05f3-561b-4741-852b-07aec9038e44\",\"productType\":\"890\",\"type\":\"ERROR\"}"                                                                                  |




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
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRI004C  |                      |              |              |
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



