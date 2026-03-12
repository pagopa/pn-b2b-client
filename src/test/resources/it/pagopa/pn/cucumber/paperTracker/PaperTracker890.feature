Feature: Casi di test relativi al nuovo microservizio pn-paper-tracker per il prodotto 890


# Test da lanciare in modalità DRY-RUN con filtro ec: ATTIVO
  @paperTracker890
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_1_890] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs per il prodotto 890
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And si verifica che la risposta tracking per la sequence "<sequenceName>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
    And si verifica che non ci siano errori per i trackingId richiesti
    Examples:
      | sequenceName                     |
      | OK_890                           |
      | OK-PersonaAbilitata_890          |
      | FAIL_890                         |
      | FAIL_IndirizzoInesatto890        |
      | FAIL-Discovery_890               |
#      | FAIL-DiscoveryIrreperibileBadCAP_890  |
      | OK-Giacenza-lte10_890            |
      | OK-Giacenza-gt10_890             |
      | OK-Giacenza-gt10-23L_890         |
      | OK-GiacenzaDelegato-lte10_890    |
      | OK-GiacenzaDelegato-gt10-23L_890 |
      | FAIL-Giacenza-gt10_890           |
      | FAIL-Giacenza-gt10-23L_890       |
      | OK-CompiutaGiacenza_890          |
      | OK-CausaForzaMaggiore_890        |
      | OK-Giacenza-gt10_890_ZIP         |
      | OK_890_ZIP                       |
      | OK-GiacenzaCAD-lte10_890         |

    # Test da lanciare in modalità DRY-RUN con filtro ec: ATTIVO
  @paperTracker890
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_2_890] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs per il prodotto 890
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK-GiacenzaDelegato-gt10_890 |
      | digitalDomicile         | NULL                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "OK-GiacenzaDelegato-gt10_890" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "OK-GiacenzaDelegato-gt10_890"

  @paperTracker890
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_3_890] Per la sequence FAIL-EVENTO-INESISTENTE verifico che l'ultimo evento di SEND_ANALOG_PROGRESS contenga RECAG001B
  e controllo la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs per il prodotto 890
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-EVENTO-INESISTENTE |
      | digitalDomicile         | NULL                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "FAIL-EVENTO-INESISTENTE" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "FAIL-EVENTO-INESISTENTE"

  @paperTracker890
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_3_890] Si verifica che i dati ritornati da /errors siano quelli attesi
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    Then si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: <expectedError>
    Examples:
      | physicalAddress           | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | Via@FAIL-Irreperibile_890 | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T11:57:28.720084096Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_DRY_RUN_MODE\",\"message\":\"CommandId: PREPARE_ANALOG_DOMICILE.IUN_XETQ-TLHY-YVMH-202603-V-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0#a9d6b6e1-336f-466d-a52b-a231237ec7cc#Plico\",\"additionalDetails\":{}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECAG003F\",\"eventIdThrow\":\"a9d6b6e1-336f-466d-a52b-a231237ec7cc\",\"productType\":\"890\",\"type\":null}" |
      | Via@FAIL-Giacenza-lte10_890 | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T11:57:28.720084096Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_DRY_RUN_MODE\",\"message\":\"CommandId: PREPARE_ANALOG_DOMICILE.IUN_XETQ-TLHY-YVMH-202603-V-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0#a9d6b6e1-336f-466d-a52b-a231237ec7cc#Plico\",\"additionalDetails\":{}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECAG003F\",\"eventIdThrow\":\"a9d6b6e1-336f-466d-a52b-a231237ec7cc\",\"productType\":\"890\",\"type\":null}" |
      | Via@OK-REC008_890-E | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T11:57:28.720084096Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_DRY_RUN_MODE\",\"message\":\"CommandId: PREPARE_ANALOG_DOMICILE.IUN_XETQ-TLHY-YVMH-202603-V-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0#a9d6b6e1-336f-466d-a52b-a231237ec7cc#Plico\",\"additionalDetails\":{}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECAG003F\",\"eventIdThrow\":\"a9d6b6e1-336f-466d-a52b-a231237ec7cc\",\"productType\":\"890\",\"type\":null}" |
      | Via@FAIL-DiscoveryIrreperibile_890 | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T11:57:28.720084096Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_DRY_RUN_MODE\",\"message\":\"CommandId: PREPARE_ANALOG_DOMICILE.IUN_XETQ-TLHY-YVMH-202603-V-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0#a9d6b6e1-336f-466d-a52b-a231237ec7cc#Plico\",\"additionalDetails\":{}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECAG003F\",\"eventIdThrow\":\"a9d6b6e1-336f-466d-a52b-a231237ec7cc\",\"productType\":\"890\",\"type\":null}" |
      | Via@OK-CAUSE-EVENTO-NO-MAPPA | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-05T11:57:28.720084096Z\",\"errorCategory\":\"OCR_VALIDATION\",\"details\":{\"cause\":\"OCR_DRY_RUN_MODE\",\"message\":\"CommandId: PREPARE_ANALOG_DOMICILE.IUN_XETQ-TLHY-YVMH-202603-V-1.RECINDEX_0.ATTEMPT_0.PCRETRY_0#a9d6b6e1-336f-466d-a52b-a231237ec7cc#Plico\",\"additionalDetails\":{}},\"flowThrow\":\"DEMAT_VALIDATION\",\"eventThrow\":\"RECAG003F\",\"eventIdThrow\":\"a9d6b6e1-336f-466d-a52b-a231237ec7cc\",\"productType\":\"890\",\"type\":null}" |

  @paperTracker890
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_4_890] Si verifica la correttezza della risposta di /trackings per le sequence che generano un errore
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName> |
      | digitalDomicile         | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequenceName>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Examples:
      | sequenceName                   |
      | FAIL-Irreperibile_890          |
      | FAIL-Giacenza-lte10_890        |
      | OK-REC008_890-E                |
      | FAIL-DiscoveryIrreperibile_890 |


  @paperTracker890
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_3_890] Per la sequence @OK-Retry_AR sono previsti due .PCRETRY
  si verifica che l'unione di entrambi dia gli stessi elementi presenti in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequenceName> |
      | digitalDomicile         | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si verifica che la risposta tracking per la sequence "<sequenceName>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
    Examples:
      | sequenceName             |
      | OK-Retry_890             |
      | OK-NonRendicontabile_890 |

  @paperTrackerRunMode890
  Scenario: [PAPER_TRACKER_RUN_MODE_890_1] Viene verificato che tutti gli elementi desiderati per la sequence OK-GIACENZA-LTE10_890  siano generati
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK-GIACENZA-LTE10_890 |
      | digitalDomicile         | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
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
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG005B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG005B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG012A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG012 |
      | details_sentAttemptMade    | 0        |
      | details_responseStatus     | OK       |
    And si verifica che non ci siano errori per i trackingId richiesti
    And si verifica che non ci siano outputs per i trackingId richiesti

  @paperTrackerRunMode890
  Scenario: [PAPER_TRACKER_RUN_MODE_890_2] Viene verificato che tutti gli elementi desiderati per la sequence OK-GIACENZA-GT10_890 siano generati
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK-GIACENZA-GT10_890 |
      | digitalDomicile         | NULL                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
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
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG012A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG011B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG011B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG012 |
      | details_sentAttemptMade    | 0        |
      | details_responseStatus     | OK       |


  @paperTrackerRunMode890
  Scenario: [PAPER_TRACKER_RUN_MODE_890_3] Viene verificato che tutti gli elementi desiderati per la sequence FAIL-Giacenza-gt10-23L_890 siano generati
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-Giacenza-gt10-23L_890 |
      | digitalDomicile         | NULL                           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
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
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG012A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG011B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG007A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG007A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG007B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG007B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG007C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG012 |
      | details_sentAttemptMade    | 0        |
      | details_responseStatus     | OK       |

  @paperTrackerRunMode890
  Scenario: [PAPER_TRACKER_RUN_MODE_890_4] Viene verificato che tutti gli elementi desiderati per la sequence FAIL-Giacenza-gt10_890 siano generati
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-Giacenza-gt10_890 |
      | digitalDomicile         | NULL                       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
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
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG012A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG011B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG007A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG007A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG007B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG007C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG012 |
      | details_sentAttemptMade    | 0        |
      | details_responseStatus     | OK       |

  @paperTrackerRunMode890
  Scenario: [PAPER_TRACKER_RUN_MODE_890_5] Viene verificato che tutti gli elementi desiderati per la sequence FAIL-Giacenza-lte10_890 siano generati
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-Giacenza-lte10_890 |
      | digitalDomicile         | NULL                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B"
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
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG007A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG007B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "Plico"}] |

  @paperTrackerRunMode890
  Scenario: [PAPER_TRACKER_RUN_MODE_890_6] Viene verificato che tutti gli elementi desiderati per la sequence FAIL-DiscoveryIrreperibile_890 siano generati
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_890 |
      | digitalDomicile         | NULL                               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003E"
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
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG003D |
      | details_sentAttemptMade    | 0         |
      | details_failureCause       | M03       |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                       |
      | details_recIndex           | 0                              |
      | details_deliveryDetailCode | RECAG003E                      |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG003E                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG003F |
      | details_sentAttemptMade    | 0         |
      | details_failureCause       | M03       |
    Then viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 1        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 1        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG003D |
      | details_sentAttemptMade    | 1         |
      | details_failureCause       | M03       |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG003E                   |
      | details_sentAttemptMade    | 1                           |
      | details_attachments        | [{"documentType": "Plico"}] |

  @paperTrackerRunMode890
  Scenario: [PAPER_TRACKER_RUN_MODE_890_7] Viene verificato che tutti gli elementi desiderati per la sequence FAIL-Irreperibile_890 siano generati
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
      | digitalDomicile         | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG003E"
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
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG003D |
      | details_sentAttemptMade    | 0         |
      | details_failureCause       | M03       |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG003E                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "Plico"}] |


# Test da lanciare in modalità RUN con filtro ec: DISATTIVO
  @paperTrackerRunMode890
  Scenario Outline: [PAPER_TRACKER_RUN_890_1] Si verifica che gli elementi di timeline attesi siano generati correttamente nella modalità RUN
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@<sequence> |
      | digitalDomicile         | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Examples:
      | sequence                         |
      | OK_890                           |
      | OK-PersonaAbilitata_890          |
      | FAIL_890                         |
      | FAIL_IndirizzoInesatto890        |
      | FAIL-Discovery_890               |
      | OK-Retry_890                     |
      | OK-Giacenza-gt10-23L_890         |
      | OK-GiacenzaDelegato-lte10_890    |
      | OK-GiacenzaDelegato-gt10_890     |
      | OK-GiacenzaDelegato-gt10-23L_890 |
      | OK-CompiutaGiacenza_890          |
      | OK-NonRendicontabile_890         |
      | OK-CausaForzaMaggiore_890        |
      | OK-REC008_890-E                  |
      | OK-Giacenza-gt10_890_ZIP         |
      | OK_890_ZIP                       |
      | OK-GiacenzaCAD-lte10_890         |

  @paperTrackerRunMode890
  Scenario: [PAPER_TRACKER_RUN_MODE_890_9] Viene verificato che tutti gli elementi desiderati per la sequence FAIL-EVENTO-INESISTENTE siano generati
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-EVENTO-INESISTENTE |
      | digitalDomicile         | NULL                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B"
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
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG001A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG001B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |

  @paperTrackerRunMode890
  Scenario: [PAPER_TRACKER_RUN_MODE_890_10] Viene verificato che tutti gli elementi desiderati per la sequence OK-CAUSE-EVENTO-NO-MAPPA siano generati
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK-CAUSE-EVENTO-NO-MAPPA |
      | digitalDomicile         | NULL                         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B"
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
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG001A |
      | details_sentAttemptMade    | 0         |
      | details_failureCause       | F01       |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG001B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |

