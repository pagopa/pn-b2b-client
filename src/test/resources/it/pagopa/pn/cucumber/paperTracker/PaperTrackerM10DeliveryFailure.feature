Feature: SEND - Recapito FASE 2: Gestione causale di mancato recapito M10 (Indirizzo non leggibile) e logica di retry analogico

  # ====================================================================================================================
  # SCENARIO 1 - Accettazione e Mappatura Causale M10
  # CASO DI TEST 1.1: Accertare che l'invio di un evento di mancato recapito con causale M10 venga correttamente
  # validato e integrato nel tracciamento di spedizione per le famiglie conformi (RS, AR, 890, RIR, RIS).
  # ====================================================================================================================

  @paperTrackerM10 @paperTrackerRSRunMode
  Scenario: [MOCK_RECAPITO_M10_01_1_A] Ricezione evento di mancato recapito con causale M10 per Raccomandata Semplice (RS)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it     |
      | physicalAddress_address | Via@FAIL_RS-M10  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "RS"
    And si verifica che la risposta tracking per la sequence "FAIL_RS-M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECRS002C |
      | details_deliveryFailureCause | M10       |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_RS-M10"
    And si verifica che non ci siano errori per i trackingId richiesti

  @paperTrackerM10 @paperTrackerARRunMode
  Scenario: [MOCK_RECAPITO_M10_01_1_B] Ricezione evento di mancato recapito con causale M10 per Raccomandata A/R (AR)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_AR-M10 |
      | digitalDomicile         | NULL            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "FAIL_AR-M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECRN002C |
      | details_responseStatus       | KO        |
      | details_deliveryFailureCause | M10       |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_AR-M10"
    And si verifica che non ci siano errori per i trackingId richiesti

  @paperTrackerM10 @paperTrackerRunMode890
  Scenario: [MOCK_RECAPITO_M10_01_1_C] Ricezione evento di mancato recapito con causale M10 per Atti Giudiziari (890)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_890-M10 |
      | digitalDomicile         | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "FAIL_890-M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECAG003C |
      | details_responseStatus       | KO        |
      | details_deliveryFailureCause | M10       |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_890-M10"
    And si verifica che non ci siano errori per i trackingId richiesti

  @paperTrackerM10 @paperTrackerARRunMode
  Scenario: [MOCK_RECAPITO_M10_01_1_D] Ricezione evento di mancato recapito con causale M10 per Raccomandata Internazionale (RIR)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_RIR-M10 |
      | digitalDomicile         | NULL             |
      | physicalAddress_State   | MESSICO          |
      | physicalAddress_zip     | ZONE_2           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "FAIL_RIR-M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECRI004C |
      | details_responseStatus       | KO        |
      | details_deliveryFailureCause | M10       |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_RIR-M10"
    And si verifica che non ci siano errori per i trackingId richiesti

  @paperTrackerM10 @paperTrackerRISRunMode
  Scenario: [MOCK_RECAPITO_M10_01_1_E] Ricezione evento di mancato recapito con causale M10 per Raccomandata Internazionale Semplice (RIS)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_State        | FRANCIA          |
      | physicalAddress_municipality | Parigi           |
      | physicalAddress_zip          | ZONE_1           |
      | physicalAddress_province     | Paris            |
      | digitalDomicile_address      | test@fail.it     |
      | physicalAddress_address      | Via@FAIL_RIS-M10 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "RS"
    And si verifica che la risposta tracking per la sequence "FAIL_RIS-M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details                      | NOT_NULL   |
      | details_recIndex             | 0          |
      | details_sentAttemptMade      | 0          |
      | details_deliveryDetailCode   | RECRSI004C |
      | details_deliveryFailureCause | M10        |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_RIS-M10"
    And si verifica che non ci siano errori per i trackingId richiesti

  # ====================================================================================================================
  # SCENARIO 1 - Accettazione e Mappatura Causale M10
  # CASO DI TEST 1.2: Verificare che tentativi di rendicontare la causale M10 su eventi che non ne prevedono l'utilizzo
  # vengano intercettati e respinti dal validatore di stato (Negative Path: consegna o transito).
  # ====================================================================================================================

  @paperTrackerM10 @trackerErrors
  Scenario: [MOCK_RECAPITO_M10_01_2_A] Tentativo di rendicontare causale M10 su evento di avvenuta consegna
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_AR-M10-ILLEGAL-CONSEGNA |
      | digitalDomicile         | NULL                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:31.698620001Z\",\"errorCategory\":\"DELIVERY_FAILURE_CAUSE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid deliveryFailureCause: M10\",\"additionalDetails\":{\"affectedEvents\":[{\"deliveryFailureCause\":\"M10\",\"statusTimestamp\":\"2026-03-06T15:56:16Z\",\"statusCode\":\"RECRN001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"1c7bfd2b-5e88-4383-8f93-03fca35c397c\",\"productType\":\"AR\",\"type\":\"ERROR\"}"
    And si verifica che non sia presente nessun retry per il tracking

  @paperTrackerM10 @trackerErrors
  Scenario: [MOCK_RECAPITO_M10_01_2_B] Tentativo di rendicontare causale M10 su evento di transito o presa in carico
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_AR-M10-ILLEGAL-TRANSITO |
      | digitalDomicile         | NULL                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:56:31.698620001Z\",\"errorCategory\":\"DELIVERY_FAILURE_CAUSE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid deliveryFailureCause: M10\",\"additionalDetails\":{\"affectedEvents\":[{\"deliveryFailureCause\":\"M10\",\"statusTimestamp\":\"2026-03-06T15:56:16Z\",\"statusCode\":\"CON080\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"1c7bfd2b-5e88-4383-8f93-03fca35c397c\",\"productType\":\"AR\",\"type\":\"ERROR\"}"
    And si verifica che non sia presente nessun retry per il tracking

  # ====================================================================================================================
  # SCENARIO 2 - Validazione Preventiva del Record (Tripletta e OCR)
  # CASO DI TEST 2.1: Validazione corretta degli allegati demat. Sblocco step successivi verso il retry (con OCR
  # disabilitato, tag KO con OCR disabilitato, o OCR assente).
  # CASO DI TEST 2.2: Esito negativo (allegato mancante o scarto OCR con motore abilitato). Arresto catena di retry,
  # tracciamento errore in DynamoDB (INCONSISTENT_STATE o OCR_VALIDATION).
  # ====================================================================================================================

  @paperTrackerM10 @paperTrackerARRunMode
  Scenario: [MOCK_RECAPITO_M10_02_1_A] Validazione positiva dematerializzazione plico con OCR implicito per Raccomandata A/R (AR)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_AR-M10 |
      | digitalDomicile         | NULL            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "FAIL_AR-M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECRN002C |
      | details_responseStatus       | KO        |
      | details_deliveryFailureCause | M10       |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_AR-M10"
    And si verifica che non ci siano errori per i trackingId richiesti

  @paperTrackerM10 @paperTrackerAR @ocrDRY
  Scenario: [MOCK_RECAPITO_M10_02_1_B] Validazione dematerializzazione plico con tag OCR KO in modalita OCR disabilitata (DRY)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_AR-M10-OCR-KO |
      | digitalDomicile         | NULL                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "FAIL_AR-M10-OCR-KO" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECRN002C |
      | details_responseStatus       | KO        |
      | details_deliveryFailureCause | M10       |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_AR-M10-OCR-KO"
    And si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:35.246856627Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\",\"additionalDetails\":{\"ocrDataResultPayload\":{\"predictedRefinementType\":\"\",\"validationType\":\"ai\",\"description\":\"validazione fallita\",\"validationStatus\":\"KO\"}}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECRN002C\",\"eventIdThrow\":\"2f428c7d-99f5-490c-a9fd-d6132c1589a2\",\"productType\":\"AR\",\"type\":\"WARNING\"}"

  @paperTrackerM10 @paperTrackerRunMode890
  Scenario: [MOCK_RECAPITO_M10_02_1_C] Validazione positiva dematerializzazione plico per Atti Giudiziari (890)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_890-M10 |
      | digitalDomicile         | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "FAIL_890-M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECAG003C |
      | details_responseStatus       | KO        |
      | details_deliveryFailureCause | M10       |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_890-M10"
    And si verifica che non ci siano errori per i trackingId richiesti

  @paperTrackerM10 @paperTrackerRSRunMode
  Scenario: [MOCK_RECAPITO_M10_02_1_D] Validazione positiva dematerializzazione plico per Raccomandata Semplice (RS)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it    |
      | physicalAddress_address | Via@FAIL_RS-M10 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "RS"
    And si verifica che la risposta tracking per la sequence "FAIL_RS-M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECRS002C |
      | details_deliveryFailureCause | M10       |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_RS-M10"
    And si verifica che non ci siano errori per i trackingId richiesti

  @paperTrackerM10 @trackerErrors
  Scenario: [MOCK_RECAPITO_M10_02_2_A] Mancanza evento di dematerializzazione plico (INCONSISTENT_STATE) con arresto retry
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_AR-M10-NO-DEMAT |
      | digitalDomicile         | NULL                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:20:40.321106838Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_FOUND\",\"message\":\"Necessary status code not found in events: [RECRN002B]\",\"additionalDetails\":{\"missingStatusCodes\":\"RECRN002B\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN002C\",\"eventIdThrow\":\"8540a381-2102-4c57-83bd-67373a3f5b47\",\"productType\":\"AR\",\"type\":\"ERROR\"}"
    And si verifica che non sia presente nessun retry per il tracking

  @paperTrackerM10 @trackerErrors @ocrRun
  Scenario: [MOCK_RECAPITO_M10_02_2_B] Scarto validazione OCR con motore abilitato in modalita RUN (OCR_VALIDATION) con arresto retry
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_AR-M10-OCR-KO |
      | digitalDomicile         | NULL                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-06T15:16:35.246856627Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\",\"additionalDetails\":{\"ocrDataResultPayload\":{\"predictedRefinementType\":\"\",\"validationType\":\"ai\",\"description\":\"validazione fallita\",\"validationStatus\":\"KO\"}}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECRN002C\",\"eventIdThrow\":\"2f428c7d-99f5-490c-a9fd-d6132c1589a2\",\"productType\":\"AR\",\"type\":\"ERROR\"}"
    And si verifica che non sia presente nessun retry per il tracking

  # ====================================================================================================================
  # SCENARIO 3 - Orchestrazione della Logica di Retry (PCRETRY+1)
  # CASO DI TEST 3.1: Generazione nuovo tentativo con progressivo PCRETRY_1 mantenendo indirizzo e destinatario;
  # l'evento KO sale come PROGRESS in timeline. Doppio retry consecutivo fino al secondo retry (PCRETRY_2).
  # CASO DI TEST 3.2: Blocco tentativi al superamento limite (4 retry massimi) con MAX_RETRY_REACHED_ERROR; nessun retry
  # se la causale di KO e' diversa da M10 (es. deceduta/irreperibile assoluto).
  # ====================================================================================================================

  @paperTrackerM10 @paperTrackerARRunMode
  Scenario: [MOCK_RECAPITO_M10_03_1_A] Generazione primo retry analogico (PCRETRY_1) per Raccomandata A/R con successo al secondo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK-Retry_AR_M10 |
      | digitalDomicile         | NULL                |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "OK-Retry_AR_M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECRN002C |
      | details_deliveryFailureCause | M10       |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "OK-Retry_AR_M10"
    And si verifica che la risposta dell'API attempts contenga finalDematFound e paperDeliveryTimestamp

  @paperTrackerM10 @paperTrackerRunMode890
  Scenario: [MOCK_RECAPITO_M10_03_1_B] Generazione primo retry analogico (PCRETRY_1) per Atti Giudiziari (890) con successo al secondo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK-Retry_890_M10 |
      | digitalDomicile         | NULL                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "OK-Retry_890_M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECAG003C |
      | details_deliveryFailureCause | M10       |
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "OK-Retry_890_M10"
    And si verifica che la risposta dell'API attempts contenga finalDematFound e paperDeliveryTimestamp

  @paperTrackerM10 @paperTrackerARRunMode
  Scenario: [MOCK_RECAPITO_M10_03_1_C] Doppio retry consecutivo (PCRETRY_1 e PCRETRY_2) per Raccomandata A/R con successo al terzo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@OK-DoubleRetry_AR_M10 |
      | digitalDomicile         | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "OK-DoubleRetry_AR_M10" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "OK-DoubleRetry_AR_M10"
    And si verifica che la risposta dell'API attempts contenga finalDematFound e paperDeliveryTimestamp

  @paperTrackerM10 @trackerErrors
  Scenario: [MOCK_RECAPITO_M10_03_2_A] Blocco dei tentativi di retry al superamento del limite massimo consentito (MAX_RETRY_REACHED_ERROR)
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_AR-M10-MAX-PCRETRY |
      | digitalDomicile         | NULL                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_4\",\"created\":\"2026-03-11T12:30:13.838822572Z\",\"errorCategory\":\"MAX_RETRY_REACHED_ERROR\",\"details\":{\"message\":\"Retry not found for trackingId: PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_4\",\"additionalDetails\":null},\"flowThrow\":\"RETRY_PHASE\",\"eventThrow\":\"RECRN002C\",\"eventIdThrow\":\"bf6522f1-5d37-4d80-a5af-e0a1b86638b0\",\"productType\":\"AR\",\"type\":\"ERROR\"}"
    And si verifica che non sia presente nessun retry per il tracking

  @paperTrackerM10 @paperTrackerARRunMode
  Scenario: [MOCK_RECAPITO_M10_03_2_B] Esclusione della logica di retry analogico per causale di KO differente da M10
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | Via@FAIL_DECEDUTO_SLOW_AR |
      | digitalDomicile         | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "FAIL_DECEDUTO_SLOW_AR" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_DECEDUTO_SLOW_AR"
    And si verifica che non sia presente nessun retry per il tracking

