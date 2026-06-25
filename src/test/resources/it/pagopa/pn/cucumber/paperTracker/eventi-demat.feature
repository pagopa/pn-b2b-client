@eventi_demat
Feature: Casi di test relativi alla nuova gestione degli eventi di dematerializzazione introdotta in paper-tracker
  in cui si va a rendere il ms più permissivo rendendolo compatibile con quanto implementato su pn-paper-channel.

  # ============ PRODOTTO AR ============

  Scenario Outline: [EVENTI_DEMAT_AR_1] Il sistema riceve un evento di dematerializzazione per il prodotto AR dopo l'evento finale (C o F)
  e il nuovo evento di dematerializzazione è visibile in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress_address> |
      | digitalDomicile         | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode                   | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | <details_deliveryDetailCode> |                      | Indagine     |              |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | <details_deliveryDetailCode>   |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |
    Examples:
      | physicalAddress_address      | details_deliveryDetailCode |
      | Via@OK_AR                    | RECRN001B                  |
      | Via@FAIL_AR                  | RECRN002B                  |
      | Via@FAIL-Irreperibile_AR     | RECRN002E                  |
      | Via@OK-Giacenza_AR           | RECRN003B                  |
      | Via@FAIL-Giacenza_AR         | RECRN004B                  |
      | Via@FAIL-CompiutaGiacenza_AR | RECRN005B                  |

  Scenario: [EVENTI_DEMAT_AR_2] Il sistema riceve due eventi di dematerializzazione RECRN001A e RECRN001B per il prodotto AR a seguito dell'evento finale
  ed i nuovi eventi di dematerializzazione sono visibili in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR_EVENTS_AFTER |
      | digitalDomicile         | NULL                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 1
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
      | details_deliveryDetailCode | RECRN001A |
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 2
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
      | details_deliveryDetailCode | RECRN001B |

  Scenario: [EVENTI_DEMAT_AR_3] Il sistema riceve due eventi di dematerializzazione RECRN002E e RECRN002D per il prodotto AR a seguito dell'evento finale
  ed i nuovi eventi di dematerializzazione sono visibili in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL_AR_EVENTS_AFTER |
      | digitalDomicile         | NULL                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 2
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
      | details_deliveryDetailCode | RECRN002E |
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 1
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
      | details_deliveryDetailCode | RECRN002D |


    # ============ PRODOTTO RIR ============

  Scenario Outline: [EVENTI_DEMAT_RIR_1] Il sistema riceve un evento di dematerializzazione per il prodotto RIR dopo l'evento finale (C)
  e il nuovo evento di dematerializzazione è visibile in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress_address> |
      | digitalDomicile         | NULL                      |
      | physicalAddress_State   | MESSICO                   |
      | physicalAddress_zip     | ZONE_2                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode                   | deliveryFailureCause | attachment_1 | attachment_2 |
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | <details_deliveryDetailCode> |                      | Indagine     |              |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | <details_deliveryDetailCode>   |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |
    Examples:
      | physicalAddress_address | details_deliveryDetailCode |
      | Via@OK_RIR              | RECRI003B                  |
      | Via@FAIL_RIR            | RECRI004B                  |

    # ============ PRODOTTO 890 ============

  Scenario Outline: [EVENTI_DEMAT_890_1] Il sistema riceve un evento di dematerializzazione per il prodotto 890 dopo l'evento finale (C)
  e il nuovo evento di dematerializzazione è visibile in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress_address> |
      | digitalDomicile         | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode                   | deliveryFailureCause | attachment_1   | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | <details_deliveryDetailCode> |                      | <documentType> |              |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | loadTimeline               | true                                 |
      | details                    | NOT_NULL                             |
      | details_deliveryDetailCode | <details_deliveryDetailCode>         |
      | details_recIndex           | 0                                    |
      | details_sentAttemptMade    | 0                                    |
      | details_attachments        | [{"documentType": "<documentType>"}] |
    Examples:
      | physicalAddress_address           | details_deliveryDetailCode | documentType |
      | Via@OK_890                        | RECAG001B                  | Indagine     |
      | Via@OK-PersonaAbilitata_890       | RECAG002B                  | Indagine     |
      | Via@FAIL_890                      | RECAG003B                  | Indagine     |
      | Via@OK-Giacenza-gt10_890          | RECAG011B                  | Indagine     |
      | Via@OK-Giacenza-lte10_890         | RECAG005B                  | Indagine     |
      | Via@OK-GiacenzaDelegato-lte10_890 | RECAG006B                  | Indagine     |
      | Via@FAIL-Giacenza-lte10_890       | RECAG007B                  | Indagine     |
      | Via@OK-CompiutaGiacenza_890       | RECAG008B                  | Indagine     |

  Scenario: [EVENTI_DEMAT_890_1.2] Il sistema riceve un evento di dematerializzazione per il prodotto 890 dopo l'evento finale (F)
  e il nuovo evento di dematerializzazione è visibile in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
      | digitalDomicile         | NULL                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | 890         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG003E  |                      | Indagine     |              |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | RECAG003E                      |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |

  Scenario: [EVENTI_DEMAT_890_3] Il sistema riceve degli eventi di dematerializzazione RECAG011A, RECAG011B, RECAG005B  per il prodotto 890 dopo l'evento finale
  ed essi sono visibili in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_890_GIACENZA_EVENTS_AFTER |
      | digitalDomicile         | NULL                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 1
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
      | details_deliveryDetailCode | RECAG011A |
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 1
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
      | details_deliveryDetailCode | RECAG011B |
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_PROGRESS" sia di 3
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
      | details_deliveryDetailCode | RECAG005B |


    # ============ PRODOTTO RS ============

  Scenario: [EVENTI_DEMAT_RS_1] Il sistema riceve un evento di dematerializzazione per il prodotto RS dopo l'evento finale (C)
  e il nuovo evento di dematerializzazione è visibile in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@FAIL_RS  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS002C"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RS          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRS002B  |                      | Indagine     |              |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | RECRS002B                      |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |

  Scenario: [EVENTI_DEMAT_RS_1.2] Il sistema riceve un evento di dematerializzazione per il prodotto RS dopo l'evento finale (C)
  e il nuovo evento di dematerializzazione è visibile in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it                           |
      | physicalAddress_address | Via@FAIL_RS_MANCATA_CONSEGNA_PGIACENZA |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS004C"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RS          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRS004B  |                      | Indagine     |              |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | RECRS004B                      |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |

  Scenario: [EVENTI_DEMAT_RS_3] Il sistema riceve un evento di dematerializzazione per il prodotto RS dopo l'evento finale (C)
  e il nuovo evento di dematerializzazione è visibile in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it                |
      | physicalAddress_address | Via@OK_RS_COMPIUTA_GIACENZA |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS005C"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RS          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRS005B  |                      | Indagine     |              |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | RECRS005B                      |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |

  Scenario: [EVENTI_DEMAT_RS_4] Il sistema riceve un evento di dematerializzazione per il prodotto RS dopo l'evento finale (F)
  e il nuovo evento di dematerializzazione è visibile in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it                      |
      | physicalAddress_address | Via@FAIL_RS_IRREPERIBILE_ASSOLUTO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS002F"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RS          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRS002E  |                      | Indagine     |              |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | RECRS002E                      |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |

    # ============ PRODOTTO RIS ============

  Scenario: [EVENTI_DEMAT_RIS_1] Il sistema riceve un evento di dematerializzazione per il prodotto RIS dopo l'evento finale (C)
  e il nuovo evento di dematerializzazione è visibile in timeline
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | ZONE_1       |
      | physicalAddress_province     | Paris        |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_address      | Via@FAIL_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRSI004C"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | RIS         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRSI004B |                      | Indagine     |              |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | RECRSI004B                     |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |