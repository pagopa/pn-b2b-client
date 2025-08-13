Feature: Resa al mittente di una notifica


  @paperTracker
  Scenario Outline: [PAPER_TRACKER_TEST_TEMPORANEI_CONFRONTO_DRY_OUTPUT]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    Then si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline
    Examples:
      | physicalAddress              |
      | Via@ok_AR                    |
      | Via@fail_AR                  |
      | Via@OK-Retry_AR              |
      | Via@OK-Giacenza_AR           |
      | Via@FAIL-Giacenza_AR         |
      | Via@FAIL-IRREPERIBILE_AR     |
      | Via@FAIL-CompiutaGiacenza_AR |
      | Via@fail-Discovery_AR        |

  # Questo test deve utilizzare sequence che devono generare degli errori specifici (che al momento ancora non esistono)
  # dopodiché deve verificare che l'errore generato sia presente e recuperabile dalla nuova api
  @paperTracker
  Scenario Outline: [PAPER_TRACKER_TEST_TEMPORANEI_SEQUENCE_ERROR]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    Then si verifica il corretto salvataggio dell'errore su PnPaperTrackingsError
    Examples:
      | physicalAddress |
      | Via@todoError1  |
      | Via@todoError2  |
      | Via@todoError3  |
      | Via@todoError4  |
      | Via@todoError5  |
      | Via@todoError6  |

  #TODO: questo scenario andrà incluso nell'NRT totale
  @paperTracker
  Scenario Outline: [PAPER_TRACKER_TEST_PERMANENTI_CONTROLLO_SEQUENCE]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Examples:
      | sequence                 | physicalAddress              |
      | OK_AR                    | Via@ok_AR                    |
      | FAIL_AR                  | Via@fail_AR                  |
      | OK-Retry_AR              | Via@OK-Retry_AR              |
      | OK-Giacenza_AR           | Via@OK-Giacenza_AR           |
      | FAIL-Giacenza_AR         | Via@FAIL-Giacenza_AR         |
      | FAIL-Irreperibile_AR     | Via@FAIL-IRREPERIBILE_AR     |
      | FAIL-CompiutaGiacenza_AR | Via@FAIL-CompiutaGiacenza_AR |
      | FAIL-Discovery_AR        | Via@fail-Discovery_AR        |