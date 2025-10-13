Feature: Casi di test relativi al nuovo microservizio pn-paper-tracker

  @paperTracker
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And si verifica che la risposta trackings sia uguale a quella attesa "<sequenceName>" iun "iun"
    Then si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline per la sequence: "<sequenceName>" iun "iun"
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

      | OK_RIR |
      | FAIL_RIR |
      | OK_RIR_NO_DEMAT |
      | OK_RIR_INVALID_DATETIME |
      | OK_RIR_TIMESTAMP_ERR |
      | OK_RIR_NOT_ORDERED |


  @paperTracker
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
    Then si verifica che gli elementi di timeline per la sequence "<sequenceName>" coincidono con quelli su PnPaperTracker, PnPaperTrackerDryRunOutputs con PCRETRY 0 e 1
    And si verifica che la risposta dell'API attempts contenga finalDematFound e paperDeliveryTimestamp
    Examples:
      | sequenceName                        |
      | OK-Retry_AR                         |
      | OK-NonRendicontabile_AR             |
      | OK-Retry_RIR |
      #EGEY-JQJU-HPJZ-202510-A-1
      | FAIL_CON996_PCRETRY_FURTO_AR |
  #WYPN-XKMK-LGVL-202510-H-1
      | OK_PCRETRY_CON996_AR |
  #XQEP-DGTY-ARDG-202510-T-1
      | FAIL_CON996_PCRETRY_FURTO_RIR |
    #VQGA-LWAE-UHJA-202510-Z-1
      | OK_PCRETRY_CON996_RIR |
    #VPAT-UKVT-XNLX-202510-Y-1



  @paperTracker
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
   #da aggiungere anche controllo della timeline e questo test diventa permanente
    #Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Then si verifica il corretto salvataggio dell'errore su PnPaperTrackingsError con category: <category> e flowThrow: "<flowThrow>" "<physicalAddress>"
    Examples:
      | physicalAddress                   | category                             | flowThrow                     |
      | Via@OK_AR_TIMESTAMP_ERR           | DATE_ERROR                           |  SEQUENCE_VALIDATION          |
      | Via@OK_AR_NO_EVENT_B              | STATUS_CODE_ERROR                    |  SEQUENCE_VALIDATION          |
      | Via@OK_RIR_INVALID_DATETIME       | DATE_ERROR                           |  SEQUENCE_VALIDATION          |

      | Via@FAIL_CON996_PCRETRY_RIR       | ATTACHMENTS_ERROR                    |  SEQUENCE_VALIDATION          |
      | Via@FAIL_CON996_PCRETRY_AR       | NOT_RETRYABLE_EVENT_ERROR             |  NOT_RETRYABLE_EVENT_ERROR    |


  #TODO: questo scenario andrà incluso nell'NRT totale
  @paperTrackerRunMode
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



      | FAIL_IndirizzoInesistenteAR                 | Via@FAIL_IndirizzoInesistenteAR                 | ANALOG_FAILURE_WORKFLOW |
      | FAIL-DiscoveryIrreperibile_AR               | Via@FAIL-DiscoveryIrreperibile_AR               | ANALOG_FAILURE_WORKFLOW |


      | FAIL-WO_AR                                  | Via@FAIL-WO_AR                                  | ANALOG_SUCCESS_WORKFLOW |
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



      | OK_RIR                            |Via@OK_RIR                         | ANALOG_SUCCESS_WORKFLOW |
      | FAIL_RIR                          |Via@FAIL_RIR                       | ANALOG_SUCCESS_WORKFLOW |
      | OK-Retry_RIR                      |Via@OK-Retry_RIR                   | ANALOG_SUCCESS_WORKFLOW |
      | OK_RIR_TIMESTAMP_ERR              |Via@OK_RIR_TIMESTAMP_ERR           | ANALOG_SUCCESS_WORKFLOW |
      | OK_RIR_NOT_ORDERED                |Via@OK_RIR_NOT_ORDERED             | ANALOG_SUCCESS_WORKFLOW |
      | FAIL_CON996_PCRETRY_FURTO_RIR     |Via@FAIL_CON996_PCRETRY_FURTO_RIR  | ANALOG_SUCCESS_WORKFLOW |
      | OK_PCRETRY_CON996_RIR             |Via@OK_PCRETRY_CON996_RIR          | ANALOG_SUCCESS_WORKFLOW |
      | FAIL_CON996_PCRETRY_FURTO_AR      |Via@FAIL_CON996_PCRETRY_FURTO_AR   | ANALOG_SUCCESS_WORKFLOW |
      | OK_PCRETRY_CON996_AR              |Via@OK_PCRETRY_CON996_AR           | ANALOG_SUCCESS_WORKFLOW |




  #TODO: questo scenario andrà incluso nell'NRT totale
  @paperTrackerRunMode
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
      | OK_RIR_INVALID_DATETIME           |Via@OK_RIR_INVALID_DATETIME          | RECRI003B          |
      | OK_AR_BLOCKED                     | Via@OK_AR_BLOCKED                   | CON020 |
      | FAIL_Consolidatore-AR             | Via@FAIL_Consolidatore-AR           | CON996 |
      | FAIL_ConsolidatoreIndirizzo-AR    | Via@FAIL_ConsolidatoreIndirizzo-AR  | CON997 |
      | OK_RIR_NO_DEMAT                   |Via@OK_RIR_NO_DEMAT                  | RECRI003A |
      | FAIL_CON996_PCRETRY_AR            |Via@FAIL_CON996_PCRETRY_AR           | CON996 |

