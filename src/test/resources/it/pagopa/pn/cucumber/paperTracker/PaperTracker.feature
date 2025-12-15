Feature: Casi di test relativi al nuovo microservizio pn-paper-tracker

  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_1] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And si verifica che la risposta trackings sia uguale a quella attesa "<sequenceName>"
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
    Examples:
      | sequenceName              |
      | ok_AR |
      | FAIL-Discovery_AR |
      | FAIL_AR |
      | FAIL-Irreperibile_AR |
      | OK-Giacenza_AR |
      | FAIL-Giacenza_AR |
      | FAIL-CompiutaGiacenza_AR |
      | OK-CausaForzaMaggiore_AR |
      | OK_AR_INVALID_DATETIME |
      | OK_AR_NO_EVENT_B |
      | OK_AR_TIMESTAMP_ERR |
      | OK_AR_NOT_ORDERED |
      | OK_GIACENZA_AR_2 |
      | OK_GIACENZA_AR_3 |
      | OK_GIACENZA_AR_4 |
      | OK_AR_BAD_EVENT |
      | OK_AR_ALL_CON |


  @paperTrackerAR @paperTrackerARRunMode
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_890_ERROR] Si verifica che gli statusCode mancanti nel tracker vengano salvati ma non gestiti
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK-GIACENZA-LTE10_890 |
      | digitalDomicile         | NULL              |
      | physicalCommunication | AR_REGISTERED_LETTER                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And si verifica che la risposta trackings sia uguale a quella attesa "OK-GIACENZA-LTE10_890"


  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_1_RIR] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName> |
      | digitalDomicile         | NULL              |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And si verifica che la risposta trackings sia uguale a quella attesa "<sequenceName>"
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
    Examples:
      | sequenceName              |
      | OK_RIR |
      | FAIL_RIR |
      | OK_RIR_INVALID_DATETIME |
      | OK_RIR_TIMESTAMP_ERR |
      | OK_RIR_NOT_ORDERED |

  @paperTrackerAR
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_1_A_RIR] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_RIR_NO_DEMAT |
      | digitalDomicile         | NULL              |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRI003A"
    And si verifica che la risposta trackings sia uguale a quella attesa "OK_RIR_NO_DEMAT"
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "OK_RIR_NO_DEMAT"

  @paperTrackerAR
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_1_B] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And si verifica che la risposta trackings sia uguale a quella attesa "FAIL-DiscoveryIrreperibile_AR"
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL-DiscoveryIrreperibile_AR"

  @paperTrackerAR
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_1_C] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL_IndirizzoInesistenteAR |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL       |
      | details_recIndex           | 0              |
      | details_sentAttemptMade    | 0              |
      | details_deliveryDetailCode | RECRN002C      |
      | details_responseStatus     | KO             |
      | details_deliveryFailureCause | M07          |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL        |
      | details_recIndex           | 0               |
      | details_sentAttemptMade    | 1               |
      | details_deliveryDetailCode | RECRN001C       |
      | details_responseStatus     | OK              |
    And si verifica che la risposta trackings sia uguale a quella attesa "FAIL_IndirizzoInesistenteAR"
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL_IndirizzoInesistenteAR"

  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_2] Per la sequence @OK-Retry_AR sono previsti due .PCRETRY
            si verifica che l'unione di entrambi dia gli stessi elementi presenti in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName>   |
      | digitalDomicile         | NULL                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si verifica che gli elementi di timeline per la sequence "<sequenceName>" coincidono con quelli su PnPaperTracker, PnPaperTrackerDryRunOutputs con PCRETRY 0, 1, 2
    And si verifica che la risposta dell'API attempts contenga finalDematFound e paperDeliveryTimestamp
    Examples:
      | sequenceName                        |
      | OK-Retry_AR                         |
      | OK-NonRendicontabile_AR             |
      | FAIL_CON996_PCRETRY_FURTO_AR |
      | OK_PCRETRY_CON996_AR |

  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_2_RIR] Per la sequence @OK-Retry_AR sono previsti due .PCRETRY
  si verifica che l'unione di entrambi dia gli stessi elementi presenti in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName>   |
      | digitalDomicile         | NULL                 |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then si verifica che gli elementi di timeline per la sequence "<sequenceName>" coincidono con quelli su PnPaperTracker, PnPaperTrackerDryRunOutputs con PCRETRY 0, 1, 2
    And si verifica che la risposta dell'API attempts contenga finalDematFound e paperDeliveryTimestamp
    Examples:
      | sequenceName                        |
      | OK-Retry_RIR |
      | FAIL_CON996_PCRETRY_FURTO_RIR |
      | OK_PCRETRY_CON996_RIR |


  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_3] Si verifica che i dati ritornati da /errors siano quelli attesi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then si verifica che su PaperTrackingsError ci sia un errore con category: <category>, flowThrow: "<flowThrow>" per la sequence: "<physicalAddress>" e pcRetry: "0"
    Examples:
      | physicalAddress                   | category                             | flowThrow                     |
      | Via@OK_AR_TIMESTAMP_ERR           | DATE_ERROR                           |  SEQUENCE_VALIDATION          |
      | Via@OK_AR_NO_EVENT_B              | STATUS_CODE_ERROR                    |  SEQUENCE_VALIDATION          |

  @paperTrackerAR
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_3_RIR] Si verifica che i dati ritornati da /errors siano quelli attesi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si verifica che su PaperTrackingsError ci sia un errore con category: <category>, flowThrow: "<flowThrow>" per la sequence: "<physicalAddress>" e pcRetry: "0"
    Examples:
      | physicalAddress                   | category                             | flowThrow                     |
      | Via@OK_RIR_INVALID_DATETIME       | DATE_ERROR                           |  SEQUENCE_VALIDATION          |
      | Via@FAIL_CON996_PCRETRY_RIR       | ATTACHMENTS_ERROR                    |  SEQUENCE_VALIDATION          |

  @paperTrackerAR
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_3_A] Si verifica che i dati ritornati da /errors siano quelli attesi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL_CON996_PCRETRY_AR |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON996"
    Then si verifica che su PaperTrackingsError ci sia un errore con category: NOT_RETRYABLE_EVENT_ERROR, flowThrow: "NOT_RETRYABLE_EVENT_HANDLER" per la sequence: "Via@FAIL_CON996_PCRETRY_AR" e pcRetry: "1"

  #questo scenario andrà incluso nell'NRT totale
  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_4]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<waitUntil>"
    Then si controlla che non ci siano eventi duplicati
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Examples:
      | sequence                          | physicalAddress                   | waitUntil               |
      | OK_AR                             | Via@ok_AR                         | ANALOG_SUCCESS_WORKFLOW |
      | OK-Retry_AR                       | Via@OK-Retry_AR                   | ANALOG_SUCCESS_WORKFLOW |
      | FAIL-Discovery_AR                 | Via@fail-Discovery_AR             | ANALOG_SUCCESS_WORKFLOW |
      | FAIL_AR                           | Via@fail_AR                       | ANALOG_SUCCESS_WORKFLOW |
      | FAIL-Irreperibile_AR              | Via@FAIL-IRREPERIBILE_AR          | ANALOG_SUCCESS_WORKFLOW |
      | OK-Giacenza_AR                    | Via@OK-Giacenza_AR                | ANALOG_SUCCESS_WORKFLOW |
      | FAIL-Giacenza_AR                  | Via@FAIL-Giacenza_AR              | ANALOG_SUCCESS_WORKFLOW |
      | FAIL-CompiutaGiacenza_AR          | Via@FAIL-CompiutaGiacenza_AR      | ANALOG_SUCCESS_WORKFLOW |
      | OK-NonRendicontabile_AR           | Via@OK-NonRendicontabile_AR       | ANALOG_SUCCESS_WORKFLOW |
      | OK-CausaForzaMaggiore_AR          | Via@OK-CausaForzaMaggiore_AR      | ANALOG_SUCCESS_WORKFLOW |
      | OK_AR_TIMESTAMP_ERR               | Via@OK_AR_TIMESTAMP_ERR           | ANALOG_SUCCESS_WORKFLOW |
      | OK_AR_NOT_ORDERED                 | Via@OK_AR_NOT_ORDERED             | ANALOG_SUCCESS_WORKFLOW |
      | OK_GIACENZA_AR_2                  | Via@OK_GIACENZA_AR_2              | ANALOG_SUCCESS_WORKFLOW |
      | OK_GIACENZA_AR_3                  | Via@OK_GIACENZA_AR_3              | ANALOG_SUCCESS_WORKFLOW |
      | OK_GIACENZA_AR_4                  | Via@OK_GIACENZA_AR_4              | ANALOG_SUCCESS_WORKFLOW |

      | OK_AR_BAD_EVENT                   | Via@OK_AR_BAD_EVENT               | ANALOG_SUCCESS_WORKFLOW |

      | FAIL_IndirizzoInesistenteAR                 | Via@FAIL_IndirizzoInesistenteAR                 | ANALOG_SUCCESS_WORKFLOW |
      | FAIL-DiscoveryIrreperibile_AR               | Via@FAIL-DiscoveryIrreperibile_AR               | ANALOG_FAILURE_WORKFLOW |

      | OK-WO-Giacenza_AR                           | Via@OK-WO-Giacenza_AR                           | ANALOG_SUCCESS_WORKFLOW |
      | OK-M_AR                                     | Via@OK-M_AR                                     | ANALOG_SUCCESS_WORKFLOW |
      | FAIL-Irreperibile_AR_SLOW                   | Via@FAIL-Irreperibile_AR_SLOW                   | ANALOG_SUCCESS_WORKFLOW |
      | OK_AR-CON020-7Z1P                           | Via@OK_AR-CON020-7Z1P                           | ANALOG_SUCCESS_WORKFLOW |
      | OK_AR-CON020-ZIP1P                          | Via@OK_AR-CON020-ZIP1P                          | ANALOG_SUCCESS_WORKFLOW |
      | OK_AR-CON020-7Z2P                           | Via@OK_AR-CON020-7Z2P                           | ANALOG_SUCCESS_WORKFLOW |
      | OK_AR-CON020-ZIP2P                          | Via@OK_AR-CON020-ZIP2P                          | ANALOG_SUCCESS_WORKFLOW |
      | OK_AR-CON020-7Z3P                           | Via@OK_AR-CON020-7Z3P                           | ANALOG_SUCCESS_WORKFLOW |
      | OK_AR-CON020-ZIP3P                          | Via@OK_AR-CON020-ZIP3P                          | ANALOG_SUCCESS_WORKFLOW |
      | OK-Giacenza_AR_ZIP                          | Via@OK-Giacenza_AR_ZIP                          | ANALOG_SUCCESS_WORKFLOW |

      | FAIL_DECEDUTO_SLOW_AR                       | Via@FAIL_DECEDUTO_SLOW_AR                       | ANALOG_WORKFLOW_RECIPIENT_DECEASED |
      | FAIL_DECEDUTO_AR                            | Via@FAIL_DECEDUTO_AR                            | ANALOG_WORKFLOW_RECIPIENT_DECEASED |
      | FAIL-CON996_PCRETRY_DECEDUTO-AR             | Via@FAIL-CON996_PCRETRY_DECEDUTO-AR             | ANALOG_WORKFLOW_RECIPIENT_DECEASED |
      | OK_AR_OCR_FAIL                              | Via@OK_AR_OCR_FAIL                              | ANALOG_SUCCESS_WORKFLOW |
      | OK_AR_OCR_PENDING                           | Via@OK_AR_OCR_PENDING                           | ANALOG_SUCCESS_WORKFLOW |

      | FAIL_CON996_PCRETRY_FURTO_AR      |Via@FAIL_CON996_PCRETRY_FURTO_AR   | ANALOG_SUCCESS_WORKFLOW |
      | OK_PCRETRY_CON996_AR              |Via@OK_PCRETRY_CON996_AR           | ANALOG_SUCCESS_WORKFLOW |
      | OK_AR_ALL_CON                     |Via@OK_AR_ALL_CON                  | ANALOG_SUCCESS_WORKFLOW |

  #questo scenario andrà incluso nell'NRT totale
  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_4_RIR]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<waitUntil>"
    Then si controlla che non ci siano eventi duplicati
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Examples:
      | sequence                          | physicalAddress                   | waitUntil               |
      | OK_RIR                            |Via@OK_RIR                         | ANALOG_SUCCESS_WORKFLOW |
     | FAIL_RIR                          |Via@FAIL_RIR                       | ANALOG_SUCCESS_WORKFLOW |
     | OK-Retry_RIR                      |Via@OK-Retry_RIR                   | ANALOG_SUCCESS_WORKFLOW |
      | OK_RIR_TIMESTAMP_ERR              |Via@OK_RIR_TIMESTAMP_ERR           | ANALOG_SUCCESS_WORKFLOW |
      | OK_RIR_NOT_ORDERED                |Via@OK_RIR_NOT_ORDERED             | ANALOG_SUCCESS_WORKFLOW |
      | FAIL_CON996_PCRETRY_FURTO_RIR     |Via@FAIL_CON996_PCRETRY_FURTO_RIR  | ANALOG_SUCCESS_WORKFLOW |
       | OK_PCRETRY_CON996_RIR             |Via@OK_PCRETRY_CON996_RIR          | ANALOG_SUCCESS_WORKFLOW |


  #questo scenario andrà incluso nell'NRT totale
  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_5]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>"
    Then si controlla che non ci siano eventi duplicati
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Examples:
      | sequence                          | physicalAddress                     | deliveryDetailCode |
      | OK_AR_INVALID_DATETIME            | Via@OK_AR_INVALID_DATETIME          | RECRN001B          |
      | OK_AR_NO_EVENT_B                  | Via@OK_AR_NO_EVENT_B                | RECRN001A          |
      | OK_AR_BLOCKED                     | Via@OK_AR_BLOCKED                   | CON020 |
      | FAIL_Consolidatore-AR             | Via@FAIL_Consolidatore-AR           | CON996 |
      | FAIL_ConsolidatoreIndirizzo-AR    | Via@FAIL_ConsolidatoreIndirizzo-AR  | CON997 |
      | FAIL_CON996_PCRETRY_AR            |Via@FAIL_CON996_PCRETRY_AR           | CON996 |


  #questo scenario andrà incluso nell'NRT totale
  @paperTrackerARRunMode
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_5_RIR]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<deliveryDetailCode>"
    Then si controlla che non ci siano eventi duplicati
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Examples:
      | sequence                          | physicalAddress                     | deliveryDetailCode |
      | OK_RIR_INVALID_DATETIME           |Via@OK_RIR_INVALID_DATETIME          | RECRI003B          |
      | OK_RIR_NO_DEMAT                   |Via@OK_RIR_NO_DEMAT                  | RECRI003A          |


  @paperTrackerARRunMode
  Scenario: [PAPER_TRACKER_VERIFY_TIMELINE_5_FAIL-WO_AR] Invio ad indirizzo di piattaforma fallimento al primo tentativo, successo al ritentativo e fallimento al secondo tentativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-WO_AR    |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi "ACCEPTED"
    Then viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                       |
      | details_recIndex           | 0                              |
      | details_deliveryDetailCode | CON020                         |
      | details_sentAttemptMade    | 0                              |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                       |
      | details_recIndex           | 0                              |
      | details_deliveryDetailCode | RECRN002B                      |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Plico"}]    |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                       |
      | details_recIndex           | 0                              |
      | details_deliveryDetailCode | RECRN002A                      |
      | details_sentAttemptMade    | 0                              |


