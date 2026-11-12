Feature: pn-paper-tracker - allegati non conformi alla macchina a stati corretti dal recapitista
  # Copre il reinvio corretto di un esito B dopo un primo invio con un allegato non previsto (prodotto AR).

  @paperTrackerARRunMode @trackerErrors @alwaysRun @work-item:QA-19092
  Scenario: [PAPER_TRACKER_ALLEGATI_NON_CONFORMI_AR_1] Il recapitista invia RECRN002B con un AR invece del Plico e poi lo corregge inviando il Plico:
  l'allegato non conforme genera solo un WARNING e il feedback viene elaborato
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
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002A  | M05                  |              |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002B  |                      | AR           |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002B  |                      | Plico        |              |
    Then viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | attachment_1 | attachment_2 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002C  |                      |              |              |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline               | true      |
      | details                    | NOT_NULL  |
      | details_deliveryDetailCode | RECRN002C |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
    And genera la key da utilizzare per invocare l'API per il prodotto: "AR"
    And si verifica che su PaperTrackingsError sia presente un errore con categoria "ATTACHMENTS_ERROR", causa "INVALID_VALUES" e tipo "WARNING"
