Feature: Resa al mittente di una notifica


#  @paperTracker
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_1] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline per lo iun: "<iun>"
    Examples:
      | physicalAddress              |  iun                    |
#      | Via@ok_AR                    |AVQA-PMEN-DUYM-202509-K-1|
#      | Via@fail_AR                  |ATPG-KZXH-AXNL-202509-V-1|
#      | Via@OK-Retry_AR              |DLJQ-DRAY-QJWG-202509-A-1|
#      | Via@OK-Giacenza_AR           |LRVX-GLPY-HMPH-202509-J-1|
#      | Via@FAIL-Giacenza_AR         |YJXP-KEUX-LXUW-202509-T-1|
#      | Via@FAIL-IRREPERIBILE_AR     |UGLN-KLEG-TNEQ-202509-W-1|
#      | Via@FAIL-CompiutaGiacenza_AR |QLJM-WTYG-NLJD-202509-T-1|
#      | Via@fail-Discovery_AR        |XJGU-QAPH-EAPA-202509-K-1|

      | Via@ok_RIR                   | WYVJ-YEAZ-EYHE-202509-N-1   |
      | Via@fail_RIR                 | WJQL-KYVW-YRKL-202509-L-1   |
      | Via@OK-Retry_RIR             | XTYT-ZTRX-LYNU-202509-A-1   |

  # Questo test deve utilizzare sequence che devono generare degli errori specifici (che al momento ancora non esistono)
  # dopodiché deve verificare che l'errore generato sia presente e recuperabile dalla nuova api
#  @paperTracker
  Scenario Outline: [PAPER_TRACKER_TEMPORARY_TEST_2]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    #da aggiungere anche controllo della timeline e questo test diventa permanente
#    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Then si verifica il corretto salvataggio dell'errore su PnPaperTrackingsError con category: <category> e flowThrow: "<flowThrow>" e iun: "<iun>"
    Examples:
      | sequence                      | physicalAddress                   | category                | flowThrow             | iun                       |
      | FAIL_CON996_PCRETRY_FURTO_AR  | Via@FAIL_CON996_PCRETRY_FURTO_AR  | EMPTY_STRING            |  NOT_RETRYABLE_EVENT_HANDLER  | LAYL-DAYU-YMVK-202509-E-1 |
      | OK_AR_TIMESTAMP_ERR           | Via@OK_AR_TIMESTAMP_ERR           | DATE_ERROR              |  SEQUENCE_VALIDATION  | WQDT-ETVQ-YKDA-202509-X-1 |
      | OK_AR_NOT_ORDERED             | Via@OK_AR_NOT_ORDERED             | STATUS_CODE_ERROR       |  SEQUENCE_VALIDATION  | UPYU-HLDK-HDQU-202509-U-1 |
      | OK_AR_BAD_EVENT               | Via@OK_AR_BAD_EVENT               | TRACKING_ID_NOT_FOUND   |  SEQUENCE_VALIDATION  | UEZH-UAGE-PXKN-202509-P-1 |
      | OK_AR_NO_EVENT_B               | Via@OK_AR_NO_EVENT_B             | STATUS_CODE_ERROR       |  SEQUENCE_VALIDATION  | MPLQ-QWEN-XAJA-202509-K-1 |
      | OK_AR_INVALID_DATETIME               | Via@OK_AR_NO_EVENT_B             | STATUS_CODE_ERROR       |  SEQUENCE_VALIDATION  | THZA-PXNP-XQVP-202509-J-1 |


  #TODO: questo scenario andrà incluso nell'NRT totale
  @paperTracker
  Scenario Outline: [PAPER_TRACKER_VERIFY_TIMELINE_3]
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <physicalAddress> |
      | digitalDomicile         | NULL              |
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>" e iun "<iun>"
    Examples:
      | sequence                          | physicalAddress                   | iun                         |
      | OK_AR                             | Via@ok_AR                         | AVQA-PMEN-DUYM-202509-K-1   |
      | OK-Retry_AR                       | Via@OK-Retry_AR                   | DLJQ-DRAY-QJWG-202509-A-1   |
      | FAIL-Discovery_AR                 | Via@fail-Discovery_AR             | XJGU-QAPH-EAPA-202509-K-1   |
      | FAIL_AR                           | Via@fail_AR                       | ATPG-KZXH-AXNL-202509-V-1   |
      | FAIL-Irreperibile_AR              | Via@FAIL-IRREPERIBILE_AR          | UGLN-KLEG-TNEQ-202509-W-1   |
      | OK-Giacenza_AR                    | Via@OK-Giacenza_AR                | LRVX-GLPY-HMPH-202509-J-1   |
      | FAIL-Giacenza_AR                  | Via@FAIL-Giacenza_AR              | YJXP-KEUX-LXUW-202509-T-1   |
      | FAIL-CompiutaGiacenza_AR          | Via@FAIL-CompiutaGiacenza_AR      | QLJM-WTYG-NLJD-202509-T-1   |
      | OK-NonRendicontabile_AR           | Via@OK-NonRendicontabile_AR       | QJTX-HUPK-QWVA-202509-R-1   |
      | OK-CausaForzaMaggiore_AR          | Via@OK-CausaForzaMaggiore_AR      | RXAM-JATD-DZTA-202509-Z-1   |
      | FAIL_CON996_PCRETRY_FURTO_AR      | Via@FAIL_CON996_PCRETRY_FURTO_AR  | LAYL-DAYU-YMVK-202509-E-1   |
      | OK_AR_INVALID_DATETIME            | Via@OK_AR_INVALID_DATETIME        | THZA-PXNP-XQVP-202509-J-1   |
      | OK_AR_NO_EVENT_B                  | Via@OK_AR_NO_EVENT_B              | MPLQ-QWEN-XAJA-202509-K-1   |
      | OK_AR_TIMESTAMP_ERR               | Via@OK_AR_TIMESTAMP_ERR           | WQDT-ETVQ-YKDA-202509-X-1   |
      | OK_AR_NOT_ORDERED                 | Via@OK_AR_NOT_ORDERED             | UPYU-HLDK-HDQU-202509-U-1   |
      | OK_GIACENZA_AR_2                  | Via@OK_GIACENZA_AR_2              | WUWA-DGZT-PMZA-202509-N-1   |
      | OK_GIACENZA_AR_3                  | Via@OK_GIACENZA_AR_3              | HZKN-NYRL-AVEQ-202509-E-1   |
      | OK_GIACENZA_AR_4                  | Via@OK_GIACENZA_AR_4              | MYGN-HAQA-TXHW-202509-T-1   |
      | OK_AR_BAD_EVENT                   | Via@OK_AR_BAD_EVENT               | UEZH-UAGE-PXKN-202509-P-1   |
      | ok_RIR                            | Via@ok_RIR                        | WYVJ-YEAZ-EYHE-202509-N-1   |
      | fail_RIR                          | Via@fail_RIR                      | WJQL-KYVW-YRKL-202509-L-1   |
      | OK-Retry_RIR                      | Via@OK-Retry_RIR                  | XTYT-ZTRX-LYNU-202509-A-1   |