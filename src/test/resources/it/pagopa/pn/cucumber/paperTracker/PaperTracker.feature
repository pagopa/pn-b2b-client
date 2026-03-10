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
    Then si verifica che su PaperTrackingsError ci sia un errore con category: <category>, flowThrow: "<flowThrow>" per la sequence: "<physicalAddress>" e pcRetry: "0"
    Examples:
      | physicalAddress         | category          | flowThrow           |
      | Via@OK_AR_TIMESTAMP_ERR | DATE_ERROR        | SEQUENCE_VALIDATION |
      | Via@OK_AR_NO_EVENT_B    | STATUS_CODE_ERROR | SEQUENCE_VALIDATION |

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
    Then si verifica che su PaperTrackingsError ci sia un errore con category: <category>, flowThrow: "<flowThrow>" per la sequence: "<physicalAddress>" e pcRetry: "0"
    Examples:
      | physicalAddress             | category          | flowThrow           |
      | Via@OK_RIR_INVALID_DATETIME | DATE_ERROR        | SEQUENCE_VALIDATION |
      | Via@FAIL_CON996_PCRETRY_RIR | ATTACHMENTS_ERROR | SEQUENCE_VALIDATION |

  @paperTrackerAR
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_3_A] Si verifica che i dati ritornati da /errors siano quelli attesi
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
    Then si verifica che su PaperTrackingsError ci sia un errore con category: NOT_RETRYABLE_EVENT_ERROR, flowThrow: "NOT_RETRYABLE_EVENT_HANDLER" per la sequence: "Via@FAIL_CON996_PCRETRY_AR" e pcRetry: "1"

  # ---------------- RUN MODE ----------------

  @paperTrackerARRunMode @ocrEnabled
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_4] Viene verificato che gli elementi di timeline prodotto in caso di OCR attivo siano quelli previsti per la sequence OK_AR_OCR_FAIL e che si sia verificato un errore
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_OCR_FAIL |
      | digitalDomicile         | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001B"
    Then si controlla che non ci siano eventi duplicati
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "OK_AR_OCR_FAIL_WITH_OCR_ENABLED"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "OK_AR_OCR_FAIL_WITH_OCR_ENABLED" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                              |
      | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_WQNQ-JDYQ-GXTH-202603-H-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-09T11:12:45.795847982Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_KO\",\"message\":\"validazione fallita\"},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"0fa002e9-5af0-467d-bc6f-450d47c25d9d\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |

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
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_4] Viene verificato che gli elementi di timeline sono presenti aspettando l'evento CON020
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
    Examples:
      | sequence          | physicalAddress       |
      | OK_AR_OCR_FAIL    | Via@OK_AR_OCR_FAIL    |
      | OK_AR_OCR_PENDING | Via@OK_AR_OCR_PENDING |

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
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Examples:
      | sequence                      | physicalAddress                   |
      | OK_AR                         | Via@ok_AR                         |
      | OK-Retry_AR                   | Via@OK-Retry_AR                   |
      | FAIL-Discovery_AR             | Via@fail-Discovery_AR             |
      | FAIL_AR                       | Via@fail_AR                       |
      | FAIL-Irreperibile_AR          | Via@FAIL-IRREPERIBILE_AR          |
      | OK-Giacenza_AR                | Via@OK-Giacenza_AR                |
      | FAIL-Giacenza_AR              | Via@FAIL-Giacenza_AR              |
      | FAIL-CompiutaGiacenza_AR      | Via@FAIL-CompiutaGiacenza_AR      |
      | OK-NonRendicontabile_AR       | Via@OK-NonRendicontabile_AR       |
      | OK-CausaForzaMaggiore_AR      | Via@OK-CausaForzaMaggiore_AR      |
      | OK_AR_NOT_ORDERED             | Via@OK_AR_NOT_ORDERED             |
      | OK_GIACENZA_AR_2              | Via@OK_GIACENZA_AR_2              |
      | OK_GIACENZA_AR_3              | Via@OK_GIACENZA_AR_3              |
      | OK_GIACENZA_AR_4              | Via@OK_GIACENZA_AR_4              |
      | OK_AR_BAD_EVENT               | Via@OK_AR_BAD_EVENT               |
      | FAIL_IndirizzoInesistenteAR   | Via@FAIL_IndirizzoInesistenteAR   |
      | FAIL-DiscoveryIrreperibile_AR | Via@FAIL-DiscoveryIrreperibile_AR |
      | OK-WO-Giacenza_AR             | Via@OK-WO-Giacenza_AR             |
      | FAIL-Irreperibile_AR_SLOW     | Via@FAIL-Irreperibile_AR_SLOW     |
      | OK_AR-CON020-7Z1P             | Via@OK_AR-CON020-7Z1P             |
      | OK_AR-CON020-ZIP1P            | Via@OK_AR-CON020-ZIP1P            |
      | OK_AR-CON020-7Z2P             | Via@OK_AR-CON020-7Z2P             |
      | OK_AR-CON020-ZIP2P            | Via@OK_AR-CON020-ZIP2P            |
      | OK_AR-CON020-7Z3P             | Via@OK_AR-CON020-7Z3P             |
      | OK_AR-CON020-ZIP3P            | Via@OK_AR-CON020-ZIP3P            |
      | FAIL_CON996_PCRETRY_FURTO_AR  | Via@FAIL_CON996_PCRETRY_FURTO_AR  |
      | OK_PCRETRY_CON996_AR          | Via@OK_PCRETRY_CON996_AR          |
      | OK_AR_ALL_CON                 | Via@OK_AR_ALL_CON                 |

  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_4] Viene verificato che gli elementi di timeline sono presenti per le sequence in cui non è previsto l'evento CON020
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<waitUntil>"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Examples:
      | sequence                        | physicalAddress                     | waitUntil                          |
      | OK-M_AR                         | Via@OK-M_AR                         | ANALOG_SUCCESS_WORKFLOW            |
      | OK-Giacenza_AR_ZIP              | Via@OK-Giacenza_AR_ZIP              | ANALOG_SUCCESS_WORKFLOW            |
      | FAIL_DECEDUTO_SLOW_AR           | Via@FAIL_DECEDUTO_SLOW_AR           | ANALOG_WORKFLOW_RECIPIENT_DECEASED |
      | FAIL_DECEDUTO_AR                | Via@FAIL_DECEDUTO_AR                | ANALOG_WORKFLOW_RECIPIENT_DECEASED |
      | FAIL-CON996_PCRETRY_DECEDUTO-AR | Via@FAIL-CON996_PCRETRY_DECEDUTO-AR | ANALOG_WORKFLOW_RECIPIENT_DECEASED |

  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_RUN_RIR_1] Viene verificato che gli elementi di timeline sono presenti per le sequence RIR
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Examples:
      | sequence                      | physicalAddress                   |
      | OK_RIR                        | Via@OK_RIR                        |
      | FAIL_RIR                      | Via@FAIL_RIR                      |
      | OK-Retry_RIR                  | Via@OK-Retry_RIR                  |
      | OK_RIR_NOT_ORDERED            | Via@OK_RIR_NOT_ORDERED            |
      | FAIL_CON996_PCRETRY_FURTO_RIR | Via@FAIL_CON996_PCRETRY_FURTO_RIR |
      | OK_PCRETRY_CON996_RIR         | Via@OK_PCRETRY_CON996_RIR         |


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
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_5] Si verifica che per le sequence AR in cui è previsto un errore, l'errore sia effettivamente presente
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
#    Then si verifica che su PaperTrackingsError ci sia un errore con category: <category>, flowThrow: "<flowThrow>" per la sequence: "<physicalAddress>" e pcRetry: "<pcRetry>"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                       | deliveryDetailCode | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | OK_AR_INVALID_DATETIME         | RECRN001B          | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-09T11:07:51.895934914Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":null,\"message\":\"Invalid business timestamps\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"5ada7994-0575-4b44-a878-5cbb662cefc9\",\"productType\":\"AR\",\"type\":\"ERROR\"}"                                   |
      | OK_AR_NO_EVENT_B               | RECRN001A          | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-09T11:11:45.551353363Z\",\"errorCategory\":\"STATUS_CODE_ERROR\",\"details\":{\"cause\":null,\"message\":\"Necessary status code not found in events: [RECRN001B]\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"af46bc56-2e04-4f7a-9bb3-95e97493c7d8\",\"productType\":\"AR\",\"type\":\"ERROR\"}" |
      | FAIL_Consolidatore-AR          | CON996             | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_1\",\"created\":\"2026-03-09T11:12:12.294835525Z\",\"errorCategory\":\"NOT_RETRYABLE_EVENT_ERROR\",\"details\":{\"cause\":null,\"message\":\"Scartato PDF\"},\"flowThrow\":\"NOT_RETRYABLE_EVENT_HANDLER\",\"eventThrow\":\"CON996\",\"eventIdThrow\":\"94d06559-ab88-4c94-b4fe-5ea057086dfa\",\"productType\":\"AR\",\"type\":\"WARNING\"}"                            |
      | FAIL_ConsolidatoreIndirizzo-AR | CON997             | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-09T11:12:13.809058431Z\",\"errorCategory\":\"NOT_RETRYABLE_EVENT_ERROR\",\"details\":{\"cause\":null,\"message\":\"Scartato CAP/INTERNAZIONALE\"},\"flowThrow\":\"NOT_RETRYABLE_EVENT_HANDLER\",\"eventThrow\":\"CON997\",\"eventIdThrow\":\"9386447d-5bc6-4c45-bf00-764788221867\",\"productType\":\"AR\",\"type\":\"WARNING\"}"             |
      | FAIL_CON996_PCRETRY_AR         | CON996             | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_1\",\"created\":\"2026-03-09T11:12:13.187073715Z\",\"errorCategory\":\"NOT_RETRYABLE_EVENT_ERROR\",\"details\":{\"cause\":null,\"message\":\"Scartato PDF\"},\"flowThrow\":\"NOT_RETRYABLE_EVENT_HANDLER\",\"eventThrow\":\"CON996\",\"eventIdThrow\":\"49e5fce1-5caf-48ee-9a45-57868d56fe47\",\"productType\":\"AR\",\"type\":\"WARNING\"}"                            |
      | OK_AR_TIMESTAMP_ERR            | RECRN001B          | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-09T11:12:55.691243136Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":null,\"message\":\"Invalid business timestamps\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRN001C\",\"eventIdThrow\":\"72db65b0-968f-47f3-a086-b8e1c16e5f10\",\"productType\":\"AR\",\"type\":\"ERROR\"}"                                   |


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
#    Then si verifica che su PaperTrackingsError ci sia un errore con category: <category>, flowThrow: "<flowThrow>" per la sequence: "<physicalAddress>" e pcRetry: "0"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | sequence                | expectedError                                                                                                                                                                                                                                                                                                                                                                                                            |
      | OK_RIR_INVALID_DATETIME | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-09T11:12:57.689684185Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":null,\"message\":\"Invalid business timestamps\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRI003C\",\"eventIdThrow\":\"92e7f08f-d23c-4412-88b8-8c6b0bfb3f2a\",\"productType\":\"RIR\",\"type\":\"ERROR\"}" |
      | OK_RIR_TIMESTAMP_ERR    | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-09T11:08:00.129478888Z\",\"errorCategory\":\"DATE_ERROR\",\"details\":{\"cause\":null,\"message\":\"Invalid business timestamps\"},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECRI003C\",\"eventIdThrow\":\"38953192-9b77-4cc3-b91d-c66d76c04d98\",\"productType\":\"RIR\",\"type\":\"ERROR\"}" |

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

