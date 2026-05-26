Feature: Casi di test relativi al nuovo microservizio pn-paper-tracker

  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_1] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName> |
      | digitalDomicile         | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "<sequenceName>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
    And si verifica che non ci siano errori per i trackingId richiesti
    Examples:
      | sequenceName             |
      | ok_AR                    |
      | FAIL-Discovery_AR        |
      | FAIL_AR                  |
      | FAIL-Irreperibile_AR     |
      | OK-Giacenza_AR           |
      | FAIL-Giacenza_AR         |
      | FAIL-CompiutaGiacenza_AR |
      | OK-CausaForzaMaggiore_AR |
      | OK_AR_INVALID_DATETIME   |
      | OK_AR_NO_EVENT_B         |
      | OK_AR_TIMESTAMP_ERR      |
      | OK_AR_NOT_ORDERED        |
      | OK_GIACENZA_AR_2         |
      | OK_GIACENZA_AR_3         |
      | OK_GIACENZA_AR_4         |
      | OK_AR_BAD_EVENT          |
      | OK_AR_ALL_CON            |

  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_1_RIR] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName> |
      | digitalDomicile         | NULL               |
      | physicalAddress_State   | MESSICO            |
      | physicalAddress_zip     | ZONE_2             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "<sequenceName>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
    And si verifica che non ci siano errori per i trackingId richiesti
    Examples:
      | sequenceName            |
      | OK_RIR                  |
      | FAIL_RIR                |
      | OK_RIR_INVALID_DATETIME |
      | OK_RIR_TIMESTAMP_ERR    |
      | OK_RIR_NOT_ORDERED      |

  @paperTrackerAR
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_1_A_RIR] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_RIR_NO_DEMAT |
      | digitalDomicile         | NULL                |
      | physicalAddress_State   | MESSICO             |
      | physicalAddress_zip     | ZONE_2              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI003A"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "OK_RIR_NO_DEMAT" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "OK_RIR_NO_DEMAT"

  @paperTrackerAR
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_1_B] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL                              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "DiscoveryIrreperibile_AR" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL-DiscoveryIrreperibile_AR"

  @paperTrackerAR
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_1_C] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL_IndirizzoInesistenteAR |
      | digitalDomicile         | NULL                            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                      | NOT_NULL  |
      | details_recIndex             | 0         |
      | details_sentAttemptMade      | 0         |
      | details_deliveryDetailCode   | RECRN002C |
      | details_responseStatus       | KO        |
      | details_deliveryFailureCause | M07       |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 1         |
      | details_deliveryDetailCode | RECRN001C |
      | details_responseStatus     | OK        |
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "FAIL_IndirizzoInesistenteAR" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_IndirizzoInesistenteAR"

  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_2] Per la sequence @OK-Retry_AR sono previsti due .PCRETRY
  si verifica che l'unione di entrambi dia gli stessi elementi presenti in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName> |
      | digitalDomicile         | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "<sequenceName>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
    And si verifica che la risposta dell'API attempts contenga finalDematFound e paperDeliveryTimestamp
    Examples:
      | sequenceName                 |
      | OK-Retry_AR                  |
      | OK-NonRendicontabile_AR      |
      | FAIL_CON996_PCRETRY_FURTO_AR |
      | OK_PCRETRY_CON996_AR         |

  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_2_RIR] Per la sequence @OK-Retry_AR sono previsti due .PCRETRY
  si verifica che l'unione di entrambi dia gli stessi elementi presenti in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName> |
      | digitalDomicile         | NULL               |
      | physicalAddress_State   | MESSICO            |
      | physicalAddress_zip     | ZONE_2             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "<sequenceName>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
    And si verifica che la risposta dell'API attempts contenga finalDematFound e paperDeliveryTimestamp
    Examples:
      | sequenceName                  |
      | OK-Retry_RIR                  |
      | FAIL_CON996_PCRETRY_FURTO_RIR |
      | OK_PCRETRY_CON996_RIR         |


  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_3] Si verifica che i dati ritornati da /errors siano quelli attesi
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | physicalAddress     | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | OK_AR_TIMESTAMP_ERR | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-11T12:22:42.320116250Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-11T12:21:53Z\",\"statusCode\":\"RECRN001C\"},{\"statusTimestamp\":\"2026-03-11T12:22:27Z\",\"statusCode\":\"RECRN001B\"},{\"statusTimestamp\":\"2026-03-11T12:22:21Z\",\"statusCode\":\"RECRN001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"831da040-af67-47bc-a364-39220d116b3f\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |
      | OK_AR_NO_EVENT_B    | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-11T12:20:00.946408401Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_FOUND\",\"message\":\"Necessary status code not found in events: [RECRN001B]\",\"additionalDetails\":{\"missingStatusCodes\":\"RECRN001B\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"1362dc88-08d0-4a1e-84d6-7575db2cd6cb\",\"productType\":\"AR\",\"type\":\"ERROR\"}"                                                                                                                                                                                     |


  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_3_RIR] Si verifica che i dati ritornati da /errors siano quelli attesi
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
      | physicalAddress_State   | MESSICO           |
      | physicalAddress_zip     | ZONE_2            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | physicalAddress             | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | Via@OK_RIR_INVALID_DATETIME | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-27T13:59:53.172667452Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-27T13:59:50Z\",\"statusCode\":\"RECRI003C\"},{\"statusTimestamp\":\"2026-03-27T13:59:45Z\",\"statusCode\":\"RECRI003B\"},{\"statusTimestamp\":\"2026-03-27T13:59:40Z\",\"statusCode\":\"RECRI003A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRI003C\",\"eventIdThrow\":\"eb4ef8de-c317-451a-8878-1dea05a5ce09\",\"productType\":\"RIR\",\"type\":\"ERROR\"}" |
      | Via@FAIL_CON996_PCRETRY_RIR | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-04-02T10:06:33.971762139Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Product type mismatch for trackingId PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0: expected RIR, but got AR\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-04-02T10:06:20Z\",\"statusCode\":\"RECRN002C\"}]}},\"flowThrow\":\"CHECK_TRACKING_PRODUCT\",\"eventThrow\":\"RECRN002C\",\"eventIdThrow\":\"8f1589da-7bec-42b2-8250-cf004dfd943f\",\"productType\":\"RIR\",\"type\":\"WARNING\"}"                                        |

  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_3_A] Si verifica che i dati ritornati da /errors siano quelli attesi
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL_CON996_PCRETRY_AR |
      | digitalDomicile         | NULL                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON996"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-27T14:00:15.885757053Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"STOCK_890_REFINEMENT_ERROR\",\"message\":\"Refinement process reached KO state, cannot proceed with final event validation\",\"additionalDetails\":{\"statusTimestamp\":\"2026-03-27T13:59:52Z\",\"statusCode\":\"RECAG005C\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG005C\",\"eventIdThrow\":\"87623121-90b2-4a17-84b2-bd593a23c978\",\"productType\":\"890\",\"type\":\"ERROR\"}" |

  # ---------------- RUN MODE ----------------

  @paperTrackerARRunMode @ocrEnabled
  Scenario: [PAPER_TRACKER_VERIFY_TIMELINE_4] Viene verificato che gli elementi di timeline prodotto in caso di OCR attivo siano quelli previsti per la sequence OK_AR_OCR_PENDING
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_OCR_PENDING |
      | digitalDomicile         | NULL                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001B"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "OK_AR_OCR_PENDING_WITH_OCR_ENABLED"
    And si verifica che la risposta tracking per la sequence "OK_AR_OCR_PENDING_WITH_OCR_ENABLED" contenga tutti gli elementi attesi e che sia strutturalmente valida

  @paperTrackerARRunMode @ocrDisabled
  Scenario Outline: [PAPER_TRACKER_AR_OCR_DRY] Viene verificato che gli elementi di timeline sono presenti aspettando l'evento CON020
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Examples:
      | sequence          |
      | OK_AR_OCR_FAIL    |
      | OK_AR_OCR_PENDING |

  #questo scenario andrà incluso nell'NRT totale
  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_RUN_AR_4] Viene verificato che gli elementi di timeline sono presenti
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And si verifica che non ci siano errori per i trackingId richiesti
    And si verifica che non ci siano outputs per i trackingId richiesti
    Examples:
      | sequence                     | physicalAddress                  |
      | OK_AR                        | Via@ok_AR                        |
      | OK-Retry_AR                  | Via@OK-Retry_AR                  |
      | FAIL-Discovery_AR            | Via@fail-Discovery_AR            |
      | FAIL_AR                      | Via@fail_AR                      |
      | FAIL-Irreperibile_AR         | Via@FAIL-IRREPERIBILE_AR         |
      | OK-Giacenza_AR               | Via@OK-Giacenza_AR               |
      | FAIL-Giacenza_AR             | Via@FAIL-Giacenza_AR             |
      | FAIL-CompiutaGiacenza_AR     | Via@FAIL-CompiutaGiacenza_AR     |
      | OK-NonRendicontabile_AR      | Via@OK-NonRendicontabile_AR      |
      | OK-CausaForzaMaggiore_AR     | Via@OK-CausaForzaMaggiore_AR     |
      | OK_GIACENZA_AR_2             | Via@OK_GIACENZA_AR_2             |
      | OK_GIACENZA_AR_3             | Via@OK_GIACENZA_AR_3             |
      | OK_GIACENZA_AR_4             | Via@OK_GIACENZA_AR_4             |
      | OK_AR_BAD_EVENT              | Via@OK_AR_BAD_EVENT              |
      | FAIL_IndirizzoInesistenteAR  | Via@FAIL_IndirizzoInesistenteAR  |
      | OK-WO-Giacenza_AR            | Via@OK-WO-Giacenza_AR            |
      | FAIL-Irreperibile_AR_SLOW    | Via@FAIL-Irreperibile_AR_SLOW    |
      | OK_AR-CON020-7Z1P            | Via@OK_AR-CON020-7Z1P            |
      | OK_AR-CON020-ZIP1P           | Via@OK_AR-CON020-ZIP1P           |
      | OK_AR-CON020-7Z2P            | Via@OK_AR-CON020-7Z2P            |
      | OK_AR-CON020-ZIP2P           | Via@OK_AR-CON020-ZIP2P           |
      | OK_AR-CON020-7Z3P            | Via@OK_AR-CON020-7Z3P            |
      | OK_AR-CON020-ZIP3P           | Via@OK_AR-CON020-ZIP3P           |
      | FAIL_CON996_PCRETRY_FURTO_AR | Via@FAIL_CON996_PCRETRY_FURTO_AR |
      | OK_PCRETRY_CON996_AR         | Via@OK_PCRETRY_CON996_AR         |
      | OK_AR_ALL_CON                | Via@OK_AR_ALL_CON                |
      | OK-GiacenzaCorrected_AR      | Via@OK-GiacenzaCorrected_AR      |

  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_RUN_AR_4.A] Viene verificato che tutti gli elementi di timeline per la sequence OK_AR_NOT_ORDERED siano presenti
    e che ci sia un errore di tipo DUPLICATED_EVENT in PaperTrackingsError per l'evento RECRN001A
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_NOT_ORDERED |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "OK_AR_NOT_ORDERED"
    And si verifica che la risposta tracking per la sequence "OK_AR_NOT_ORDERED" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And si verifica che non ci siano outputs per i trackingId richiesti
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-04-29T07:27:17.130111793Z\",\"errorCategory\":\"DUPLICATED_EVENT\",\"details\":{\"message\":\"Duplicated event found for statusCode: RECRN001A\",\"additionalDetails\":{\"statusTimestamp\":\"2026-04-29T07:27:04Z\",\"statusCode\":\"RECRN001A\"}},\"flowThrow\":\"DUPLICATED_EVENT_VALIDATION\",\"eventThrow\":\"RECRN001A\",\"eventIdThrow\":\"17ad944a-b7e3-4c4b-a839-39c7b384b7df\",\"productType\":\"AR\",\"type\":\"WARNING\"}" |

  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_4] Viene verificato che gli elementi di timeline sono presenti per le sequence in cui non è previsto l'evento CON020
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<waitUntil>"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Examples:
      | sequence                        | waitUntil                          |
      | OK-M_AR                         | ANALOG_SUCCESS_WORKFLOW            |
      | OK-Giacenza_AR_ZIP              | ANALOG_SUCCESS_WORKFLOW            |
      | FAIL_DECEDUTO_SLOW_AR           | ANALOG_WORKFLOW_RECIPIENT_DECEASED |
      | FAIL_DECEDUTO_AR                | ANALOG_WORKFLOW_RECIPIENT_DECEASED |
      | FAIL-CON996_PCRETRY_DECEDUTO-AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED |
      | FAIL-DiscoveryIrreperibile_AR   | COMPLETELY_UNREACHABLE             |

  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_RUN_RIR_1] Viene verificato che gli elementi di timeline sono presenti per le sequence RIR
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
      | physicalAddress_State   | MESSICO        |
      | physicalAddress_zip     | ZONE_2         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    And si verifica che non ci siano errori per i trackingId richiesti
    And si verifica che non ci siano outputs per i trackingId richiesti
    Examples:
      | sequence                      |
      | OK_RIR                        |
      | FAIL_RIR                      |
      | OK-Retry_RIR                  |
      | OK_RIR_NOT_ORDERED            |
      | FAIL_CON996_PCRETRY_FURTO_RIR |
      | OK_PCRETRY_CON996_RIR         |


  @paperTrackerARRunMode
  Scenario: [PAPER_TRACKER_VERIFY_TIMELINE_5] Si verifica che per la sequence OK_AR_BLOCKED si arrivi al deliveryDetailCode CON020
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_BLOCKED |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "OK_AR_BLOCKED"
    And si verifica che la risposta tracking per la sequence "OK_AR_BLOCKED" contenga tutti gli elementi attesi e che sia strutturalmente valida


  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_ERROR_5.A] Si verifica che per le sequence AR in cui è previsto un errore, l'errore sia effettivamente presente
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence               | deliveryDetailCode | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | OK_AR_INVALID_DATETIME | RECRN001B          | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-11T12:15:58.311224640Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-11T12:15:55Z\",\"statusCode\":\"RECRN001C\"},{\"statusTimestamp\":\"2026-03-11T12:15:48Z\",\"statusCode\":\"RECRN001B\"},{\"statusTimestamp\":\"2026-03-11T12:15:42Z\",\"statusCode\":\"RECRN001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"2b19530f-24c1-4fd0-bee3-cce25e79dd8a\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |
      | OK_AR_NO_EVENT_B       | RECRN001A          | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-11T12:20:00.946408401Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"VALUES_NOT_FOUND\",\"message\":\"Necessary status code not found in events: [RECRN001B]\",\"additionalDetails\":{\"missingStatusCodes\":\"RECRN001B\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"1362dc88-08d0-4a1e-84d6-7575db2cd6cb\",\"productType\":\"AR\",\"type\":\"ERROR\"}"                                                                                                                                                                                     |
      | OK_AR_TIMESTAMP_ERR    | RECRN001B          | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-11T12:22:42.320116250Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-11T12:21:53Z\",\"statusCode\":\"RECRN001C\"},{\"statusTimestamp\":\"2026-03-11T12:22:27Z\",\"statusCode\":\"RECRN001B\"},{\"statusTimestamp\":\"2026-03-11T12:22:21Z\",\"statusCode\":\"RECRN001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"831da040-af67-47bc-a364-39220d116b3f\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |

  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_ERROR_5.B] Si verifica che per le sequence AR in cui sono previsti dei CON996 si sia verificato un errore
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | loadTimeline               | true     |
      | pollingTime                | 40000    |
      | numCheck                   | 20       |
      | details                    | NOT_NULL |
      | details_deliveryDetailCode | CON996   |
      | details_recIndex           | 0        |
      | details_sentAttemptMade    | 0        |
      | progressIndex              | 2        |
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence               | deliveryDetailCode | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                              |
      | FAIL_Consolidatore-AR  | CON996             | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_1\",\"created\":\"2026-03-11T12:25:03.346403708Z\",\"errorCategory\":\"NOT_RETRYABLE_EVENT_ERROR\",\"details\":{\"message\":\"Scartato PDF\",\"additionalDetails\":null},\"flowThrow\":\"NOT_RETRYABLE_EVENT_HANDLER\",\"eventThrow\":\"CON996\",\"eventIdThrow\":\"f7e8fb6c-58ab-4e03-a1a3-a7a6581f6305\",\"productType\":\"AR\",\"type\":\"WARNING\"}" |
      | FAIL_CON996_PCRETRY_AR | CON996             | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_1\",\"created\":\"2026-03-11T12:21:04.944109655Z\",\"errorCategory\":\"NOT_RETRYABLE_EVENT_ERROR\",\"details\":{\"message\":\"Scartato PDF\",\"additionalDetails\":null},\"flowThrow\":\"NOT_RETRYABLE_EVENT_HANDLER\",\"eventThrow\":\"CON996\",\"eventIdThrow\":\"7067b370-3487-4381-b80e-c0ebad9f9242\",\"productType\":\"AR\",\"type\":\"WARNING\"}" |

  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_ERROR_5.C] Si verifica che per le sequence FAIL_ConsolidatoreIndirizzo-AR in cui è previsto un errore, l'errore sia effettivamente presente
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
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                       | deliveryDetailCode | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | FAIL_ConsolidatoreIndirizzo-AR | CON997             | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-11T12:20:02.696448004Z\",\"errorCategory\":\"NOT_RETRYABLE_EVENT_ERROR\",\"details\":{\"message\":\"Scartato CAP/INTERNAZIONALE\",\"additionalDetails\":null},\"flowThrow\":\"NOT_RETRYABLE_EVENT_HANDLER\",\"eventThrow\":\"CON997\",\"eventIdThrow\":\"75737710-8caf-40b6-a361-2e4f53b75ce6\",\"productType\":\"AR\",\"type\":\"WARNING\"}" |


  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_5_RIR] Si verifica che per le sequence RIR in cui è previsto un errore, l'errore sia effettivamente presente
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
      | physicalAddress_State   | MESSICO        |
      | physicalAddress_zip     | ZONE_2         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI003B"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | OK_RIR_INVALID_DATETIME | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-11T09:53:06.505237565Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-11T09:53:04Z\",\"statusCode\":\"RECRI003C\"},{\"statusTimestamp\":\"2026-03-11T09:52:58Z\",\"statusCode\":\"RECRI003B\"},{\"statusTimestamp\":\"2026-03-11T09:52:51Z\",\"statusCode\":\"RECRI003A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRI003C\",\"eventIdThrow\":\"bd524a18-fded-48d1-8b84-0c17c02acefb\",\"productType\":\"RIR\",\"type\":\"ERROR\"}" |
      | OK_RIR_TIMESTAMP_ERR    | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-11T09:53:16.225928175Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-11T09:52:41Z\",\"statusCode\":\"RECRI003C\"},{\"statusTimestamp\":\"2026-03-11T09:53:03Z\",\"statusCode\":\"RECRI003B\"},{\"statusTimestamp\":\"2026-03-11T09:52:57Z\",\"statusCode\":\"RECRI003A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRI003C\",\"eventIdThrow\":\"d04a4e51-6a84-4469-8696-8f541c52de41\",\"productType\":\"RIR\",\"type\":\"ERROR\"}" |

  @paperTrackerARRunMode
  Scenario: [PAPER_TRACKER_VERIFY_TIMELINE_5.1_RIR] Si verifica che per la sequence OK_RIR_NO_DEMAT si arrivi al deliveryDetailCode RECRI003A
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_RIR_NO_DEMAT |
      | digitalDomicile         | NULL                |
      | physicalAddress_State   | MESSICO             |
      | physicalAddress_zip     | ZONE_2              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI003A"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "OK_RIR_NO_DEMAT"
    And si verifica che la risposta tracking per la sequence "OK_RIR_NO_DEMAT" contenga tutti gli elementi attesi e che sia strutturalmente valida


  @paperTrackerARRunMode
  Scenario: [PAPER_TRACKER_VERIFY_TIMELINE_5_FAIL-WO_AR] Invio ad indirizzo di piattaforma fallimento al primo tentativo, successo al ritentativo e fallimento al secondo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-WO_AR |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    Then viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECRN002B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECRN002A |
      | details_sentAttemptMade    | 0         |
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "FAIL-WO_AR" contenga tutti gli elementi attesi e che sia strutturalmente valida


  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_6_FAIL] Si verifica che per la sequence OK-TimestampCorrected_AR venga generato un errore di tipo DATE_ERROR con cause VALUES_NOT_MATCHING e che non ci siano eventi duplicati
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK-TimestampCorrected_AR |
      | digitalDomicile         | NULL                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001B"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "OK-TimestampCorrected_AR"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-13T15:12:56.837741793Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":\"VALUES_NOT_MATCHING\",\"message\":\"Invalid business timestamps\",\"additionalDetails\":{\"affectedEvents\":[{\"statusTimestamp\":\"2026-03-13T15:12:54Z\",\"statusCode\":\"RECRN001C\"},{\"statusTimestamp\":\"2026-03-13T15:12:49Z\",\"statusCode\":\"RECRN001B\"},{\"statusTimestamp\":\"2026-03-13T15:12:44Z\",\"statusCode\":\"RECRN001A\"}]}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"d6117f58-950e-4e9c-8b8c-acdb90a8aec5\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |


  @paperTrackerARRunMode
  Scenario: [PAPER_TRACKER_VERIFY_TIMELINE_7] Si verifica che per la sequence FAIL_FailureCauseCorrected_890 la notifica vada correttamente in REFINEMENT
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL_FailureCauseCorrected_890 |
      | digitalDomicile         | NULL                               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "FAIL_FailureCauseCorrected_890"
    And si verifica che non ci siano errori per i trackingId richiesti
