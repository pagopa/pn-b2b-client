Feature: Resa al mittente di una notifica


  @paperTracker
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_1] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
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
    Then si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline
    Examples:
      | physicalAddress              |
      | Via@ok_AR                    |
#      | Via@fail_AR                  |
#      | Via@OK-Giacenza_AR           |
#      | Via@FAIL-Giacenza_AR         |
#      | Via@FAIL-IRREPERIBILE_AR     |
#      | Via@FAIL-CompiutaGiacenza_AR |
#      | Via@fail-Discovery_AR        |


  #@paperTracker
  Scenario: [PAPER_TRACKER_TEMPORARY_TEST_2] Per la sequence @OK-Retry_AR sono previsti due .PCRETRY
            si verifica che l'unione di entrambi dia gli stessi elementi presenti in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK-Retry_AR   |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then si verifica che gli elementi di timeline coincidono con quelli su PnPaperTracker, PnPaperTrackerDryRunOutputs con PCRETRY 0 e 1


  # Questo test deve utilizzare sequence che devono generare degli errori specifici (che al momento ancora non esistono)
  # dopodiché deve verificare che l'errore generato sia presente e recuperabile dalla nuova api
  #@paperTracker
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_2]
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
    #Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Then si verifica il corretto salvataggio dell'errore su PnPaperTrackingsError con category: <category> e flowThrow: "<flowThrow>"
    Examples:
      | physicalAddress                   | category                             | flowThrow                     |
      | Via@FAIL_CON996_PCRETRY_FURTO_AR  | NOT_RETRYABLE_EVENT_ERROR            |  NOT_RETRYABLE_EVENT_HANDLER  |
      | Via@OK_AR_TIMESTAMP_ERR           | STATUS_CODE_ERROR                    |  SEQUENCE_VALIDATION          |
      | Via@OK_AR_NO_EVENT_B              | STATUS_CODE_ERROR                    |  SEQUENCE_VALIDATION          |
      | Via@OK_AR_NO_EVENT_B              | DATE_ERROR                           |  SEQUENCE_VALIDATION          |


  #TODO: questo scenario andrà incluso nell'NRT totale
  @paperTrackerNonDryRun
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_3]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>" e iun "<iun>"
    Examples:
      | sequence                          | physicalAddress                   | iun                         |
      | OK_AR                             | Via@ok_AR                         | DUWA-TAMQ-DHPG-202509-Q-1   |
      | OK-Retry_AR                       | Via@OK-Retry_AR                   | YAKW-KGWK-LPUA-202509-R-1   |
      | FAIL-Discovery_AR                 | Via@fail-Discovery_AR             | UZAX-XTYA-HKXV-202509-P-1   |
      | FAIL_AR                           | Via@fail_AR                       | NUKW-GLZK-RZTP-202509-H-1   |
      | FAIL-Irreperibile_AR              | Via@FAIL-IRREPERIBILE_AR          | KJYV-GDKR-JEPT-202509-K-1   |
      | OK-Giacenza_AR                    | Via@OK-Giacenza_AR                | QXAN-JATN-DZED-202509-X-1   |
      | FAIL-Giacenza_AR                  | Via@FAIL-Giacenza_AR              | NGPV-REAP-ETNK-202509-D-1   |
      | FAIL-CompiutaGiacenza_AR          | Via@FAIL-CompiutaGiacenza_AR      | VMKU-VXJK-LUDA-202509-Y-1   |
      | OK-NonRendicontabile_AR           | Via@OK-NonRendicontabile_AR       | UJEY-NXVP-GQMQ-202509-T-1   |
      | OK-CausaForzaMaggiore_AR          | Via@OK-CausaForzaMaggiore_AR      | QXHV-QMDH-JWXE-202509-D-1   |
      | FAIL_CON996_PCRETRY_FURTO_AR      | Via@FAIL_CON996_PCRETRY_FURTO_AR  | DWZG-NTVK-YGMQ-202509-R-1   |
      | OK_AR_INVALID_DATETIME            | Via@OK_AR_INVALID_DATETIME        | UAZH-TYZH-AETD-202509-X-1   |
      | OK_AR_NO_EVENT_B                  | Via@OK_AR_NO_EVENT_B              | EPGQ-GWZR-DEUA-202509-G-1   |
      | OK_AR_TIMESTAMP_ERR               | Via@OK_AR_TIMESTAMP_ERR           | LVHY-QPQK-GMKE-202509-Q-1   |
      | OK_AR_NOT_ORDERED                 | Via@OK_AR_NOT_ORDERED             | HPQR-VWJV-AYVT-202509-H-1   |
      | OK_GIACENZA_AR_2                  | Via@OK_GIACENZA_AR_2              | NAWP-GXDZ-RWLG-202509-G-1   |
      | OK_GIACENZA_AR_3                  | Via@OK_GIACENZA_AR_3              | MYAV-EPRU-YJMD-202509-D-1   |
      | OK_GIACENZA_AR_4                  | Via@OK_GIACENZA_AR_4              | QYAT-NEXH-VZDX-202509-W-1   |
      | OK_AR_BAD_EVENT                   | Via@OK_AR_BAD_EVENT               | LKHT-VMQP-MDZA-202509-R-1   |
