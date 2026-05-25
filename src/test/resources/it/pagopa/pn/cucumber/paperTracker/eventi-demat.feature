@eventi_demat
Feature: Casi di test relativi alla nuova gestione degli eventi di dematerializzazione introdotta in paper-tracker
  in cui si va a rendere il ms più permissivo rendendolo compatibile con quanto implementato su pn-paper-channel.

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
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | <details_deliveryDetailCode> |                      | <documentType> |              |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | loadTimeline               | true                                 |
      | details                    | NOT_NULL                             |
      | details_deliveryDetailCode | <details_deliveryDetailCode>         |
      | details_recIndex           | 0                                    |
      | details_sentAttemptMade    | 0                                    |
      | details_attachments        | [{"documentType": "<documentType>"}] |
    Examples:
      | physicalAddress_address           | details_deliveryDetailCode | documentType |
      | Via@OK_890                        | RECAG001B                  | AR           |
      | Via@OK-PersonaAbilitata_890       | RECAG002B                  | AR           |
      | Via@FAIL_890                      | RECAG003B                  | Indagine     |
      | Via@OK-Giacenza-gt10_890          | RECAG011B                  | AR           |
      | Via@OK-Giacenza-lte10_890         | RECAG005B                  | AR           |
      | Via@OK-GiacenzaDelegato-lte10_890 | RECAG006B                  | AR           |
      | Via@FAIL-Giacenza-lte10_890       | RECAG007B                  | AR           |
      | Via@OK-CompiutaGiacenza_890       | RECAG008B                  | Indagine     |

  Scenario: [EVENTI_DEMAT_890_2] Il sistema riceve un evento di dematerializzazione per il prodotto 890 dopo l'evento finale (F)
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
      | RIR         | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECAG003E  |                      | Indagine     |              |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | RECAG003E                      |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |

  Scenario Outline: [EVENTI_DEMAT_RS_1] Il sistema riceve un evento di dematerializzazione per il prodotto RS dopo l'evento finale (C)
  e il nuovo evento di dematerializzazione è visibile in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it              |
      | physicalAddress_address | <physicalAddress_address> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS002C"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode                   | deliveryFailureCause | attachment_1 | attachment_2 |
      | RS          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | <details_deliveryDetailCode> |                      | Indagine     |              |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | <details_deliveryDetailCode>   |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |
    Examples:
      | physicalAddress_address                | details_deliveryDetailCode |
      | Via@FAIL_RS                            | RECRS002B                  |
      | Via@FAIL_RS_MANCATA_CONSEGNA_PGIACENZA | RECRS004B                  |
      | Via@OK_RS_COMPIUTA_GIACENZA            | RECRS005B                  |

  Scenario: [EVENTI_DEMAT_RS_2] Il sistema riceve un evento di dematerializzazione per il prodotto RS dopo l'evento finale (F)
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
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | loadTimeline               | true                           |
      | details                    | NOT_NULL                       |
      | details_deliveryDetailCode | RECRSI004B                     |
      | details_recIndex           | 0                              |
      | details_sentAttemptMade    | 0                              |
      | details_attachments        | [{"documentType": "Indagine"}] |