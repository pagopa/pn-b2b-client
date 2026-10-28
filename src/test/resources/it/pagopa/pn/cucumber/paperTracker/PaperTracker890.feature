Feature: Casi di test relativi al nuovo microservizio pn-paper-tracker per il prodotto 890


# Test da lanciare in modalità DRY-RUN con filtro ec: ATTIVO
  @paperTracker890
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_1_890] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs per il prodotto 890
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
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
#    And si verifica che non ci siano errori per i trackingId richiesti
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
      | physicalAddress             | expectedError                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | Via@FAIL-Giacenza-lte10_890 | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-12T17:57:32.481465576Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"STOCK_890_REFINEMENT_MISSING\",\"message\":\"invalid AWAITING_REFINEMENT state for stock 890\",\"additionalDetails\":{\"statusTimestamp\":\"2026-03-12T17:56:48Z\",\"statusCode\":\"RECAG007C\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG007C\",\"eventIdThrow\":\"32b076c0-748f-4c30-9150-28e8ae2c4ba8\",\"productType\":\"890\",\"type\":\"ERROR\"}" |
      | Via@OK-REC008_890-E         | "{\"trackingId\":\"PREPARE_ANALOG_DOMICILE.IUN_<iun>.RECINDEX_0.ATTEMPT_0.PCRETRY_0\",\"created\":\"2026-03-12T13:18:09.075689686Z\",\"errorCategory\":\"INCONSISTENT_STATE\",\"details\":{\"cause\":\"STOCK_890_REFINEMENT_MISSING\",\"message\":\"invalid AWAITING_REFINEMENT state for stock 890\",\"additionalDetails\":{\"statusTimestamp\":\"2026-03-12T13:17:55Z\",\"statusCode\":\"RECAG008C\"}},\"flowThrow\":\"SEQUENCE_VALIDATION\",\"eventThrow\":\"RECAG008C\",\"eventIdThrow\":\"7b800b45-e404-41b3-b111-7fd6a6e6d86e\",\"productType\":\"890\",\"type\":\"ERROR\"}" |

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
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
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
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    Then si controlla che non ci siano eventi duplicati
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
      | FAIL-Irreperibile_890            |
      | FAIL-DiscoveryIrreperibile_890   |

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

  @paperTrackerRunMode890
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_4_890_11] Per la sequence FAIL-Giacenza-lte10_890_NO23L sono previsti due RECAG012 e questo in timeline deve produrre la presenza di un solo elemento di tipo RECAG012A e controllo la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs per il prodotto 890
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-Giacenza-lte10_890_NO23L |
      | digitalDomicile         | NULL                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B" e verifica tipo DOC "Plico"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B" e verifica tipo DOC "ARCAD"
    And genera la key da utilizzare per invocare l'API per il prodotto: "890"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "FAIL-Giacenza-lte10_890_NO23L"
