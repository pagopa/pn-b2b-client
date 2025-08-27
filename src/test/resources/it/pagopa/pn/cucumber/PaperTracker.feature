Feature: Resa al mittente di una notifica


#  @paperTracker
  Scenario Outline: [PAPER_TRACKER_TEST_TEMPORANEI_CONFRONTO_DRY_OUTPUT]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then aaa si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline per la sequence: "OK-Retry_AR"
    Examples:
      | physicalAddress              |
#      | Via@ok_AR                    |
#      | Via@fail_AR                  |
#      | Via@OK-Retry_AR              |
#      | Via@OK-Giacenza_AR           |
#      | Via@FAIL-Giacenza_AR         |
#      | Via@FAIL-IRREPERIBILE_AR     |
#      | Via@FAIL-CompiutaGiacenza_AR |
       | Via@fail-Discovery_AR        |

  # Questo test deve utilizzare sequence che devono generare degli errori specifici (che al momento ancora non esistono)
  # dopodiché deve verificare che l'errore generato sia presente e recuperabile dalla nuova api
#  @paperTracker
  Scenario Outline: [PAPER_TRACKER_TEST_TEMPORANEI_SEQUENCE_ERROR]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    #da aggiungere anche controllo della timeline e questo test diventa permanente
#    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Then si verifica il corretto salvataggio dell'errore su PnPaperTrackingsError con category: "<category>" e flowThrow: "<flowThrow>"
    Examples:
      | sequence                      | physicalAddress                   | category                | flowThrow             |
      | FAIL_CON996_PCRETRY_FURTO_AR  | Via@FAIL_CON996_PCRETRY_FURTO_AR  | TRACKING_ID_NOT_FOUND   |  SEQUENCE_VALIDATION  |
      | OK_AR_TIMESTAMP_ERR           | Via@OK_AR_TIMESTAMP_ERR           | TRACKING_ID_NOT_FOUND   |  SEQUENCE_VALIDATION  |
      | OK_AR_NOT_ORDERED             | Via@OK_AR_NOT_ORDERED             | TRACKING_ID_NOT_FOUND   |  SEQUENCE_VALIDATION  |
      | OK_AR_BAD_EVENT               | Via@OK_AR_BAD_EVENT               | TRACKING_ID_NOT_FOUND   |  SEQUENCE_VALIDATION  |

  #TODO: questo scenario andrà incluso nell'NRT totale
  @paperTracker
  Scenario Outline: [PAPER_TRACKER_TEST_PERMANENTI_CONTROLLO_SEQUENCE]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<physicalAddress>"
    Examples:
      | sequence                 | physicalAddress              |
      | OK_AR                    | Via@ok_AR                    |
      | OK-Retry_AR              | Via@OK-Retry_AR              |
      | FAIL-Discovery_AR        | Via@fail-Discovery_AR        |
      | FAIL_AR                  | Via@fail_AR                  |
      | FAIL-Irreperibile_AR     | Via@FAIL-IRREPERIBILE_AR     |
      | OK-Giacenza_AR           | Via@OK-Giacenza_AR           |
      | FAIL-Giacenza_AR         | Via@FAIL-Giacenza_AR         |
      | FAIL-CompiutaGiacenza_AR | Via@FAIL-CompiutaGiacenza_AR |
      | FAIL-CompiutaGiacenza_AR | Via@OK-NonRendicontabile_AR |
      | FAIL-CompiutaGiacenza_AR | Via@OK-CausaForzaMaggiore_AR |
      | FAIL-CompiutaGiacenza_AR | Via@FAIL_CON996_PCRETRY_FURTO_AR |
      | FAIL-CompiutaGiacenza_AR | Via@OK_AR_INVALID_DATETIME |
      | FAIL-CompiutaGiacenza_AR | Via@KO_AR_NO_EVENT_B |
      | FAIL-CompiutaGiacenza_AR | Via@OK_AR_TIMESTAMP_ERR |
      | FAIL-CompiutaGiacenza_AR | Via@OK_AR_NOT_ORDERED |
      | FAIL-CompiutaGiacenza_AR | Via@OK_GIACENZA_AR_2 |
      | FAIL-CompiutaGiacenza_AR | Via@OK_GIACENZA_AR_3 |
      | FAIL-CompiutaGiacenza_AR | Via@OK_GIACENZA_AR_4 |
      | FAIL-CompiutaGiacenza_AR | Via@OK_AR_BAD_EVENT |

      | FAIL-Discovery_AR        | Via@ok_RIR        |
      | FAIL-Discovery_AR        | Via@fail_RIR        |
      | FAIL-Discovery_AR        | Via@OK-Retry_RIR        |