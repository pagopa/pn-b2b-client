Feature: Analog send e2e

  @e2e @ignore
  Scenario: [B2B_ANALOG_SEND_1] Invio ad indirizzo fisico successo al primo tentativo
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Dino Sauro |
      | taxId | DSRDNI00A01A225I |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 30    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-COMPIUTAGIACENZA_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
      | details_physicalAddress | {"address": "VIA@OK-COMPIUTAGIACENZA_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_responseStatus | OK |
    And viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | details | NOT_NULL |
      | details_recIndex | 0 |

  @e2e @ignore
  Scenario: [B2B_ANALOG_SEND_2] Invio ad indirizzo fisico fallimento al primo tentativo e successo al secondo tentativo
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Dino Sauro |
      | taxId | DSRDNI00A01A225I |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Retry_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    Then viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_responseStatus | KO |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECAG001C |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_responseStatus | OK |

  @e2e @ignore
  Scenario: [B2B_ANALOG_SEND_3] Invio ad indirizzo fisico fallimento al primo tentativo e al secondo tentativo
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Dino Sauro |
      | taxId | DSRDNI00A01A225I |
      | digitalDomicile | NULL |
      | physicalAddress_address | @FAIL-DiscoveryIrreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REQUEST_ACCEPTED"
      | NULL | NULL |
    Then viene verificato che l'elemento di timeline "COMPLETELY_UNREACHABLE" esista
      | loadTimeline | true |
      | pollingTime | 40000 |
      | numCheck    | 20     |
      | legalFactsIds | [{"category": "ANALOG_FAILURE_DELIVERY"}] |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "ANALOG_FAILURE_WORKFLOW" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
      | details_physicalAddress | {"address": "@FAIL-DiscoveryIrreperibile_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_responseStatus | KO |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECAG001C |
      | details_physicalAddress | {"address": "@FAIL-DiscoveryIrreperibile_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_responseStatus | KO |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_FAILURE_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "REFINEMENT"
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |