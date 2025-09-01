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
    Then si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline per lo iun: "<iun>"
    Examples:
      | physicalAddress              |  iun |
      | Via@ok_AR                    |TEKP-LVYE-ERGV-202508-U-1|
#      | Via@fail_AR                  |KEGT-WPNG-MVTN-202508-W-1|
#      | Via@OK-Retry_AR              |ZWNV-AVXN-YULP-202508-Y-1|
#      | Via@OK-Giacenza_AR           |UDEH-RPWQ-LDYN-202508-U-1|
#      | Via@FAIL-Giacenza_AR         |JDMA-UWAG-XGQM-202508-V-1|
#      | Via@FAIL-IRREPERIBILE_AR     |LAVM-TAEY-ATZW-202508-A-1|
#      | Via@FAIL-CompiutaGiacenza_AR |ADVM-KPUT-GYNK-202508-R-1|
#       | Via@fail-Discovery_AR        |HETU-EUZP-YKZL-202508-X-1|

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
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    #da aggiungere anche controllo della timeline e questo test diventa permanente
#    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    Then si verifica il corretto salvataggio dell'errore su PnPaperTrackingsError con category: <category> e flowThrow: "<flowThrow>" e iun: "<iun>"
    Examples:
      | sequence                      | physicalAddress                   | category                | flowThrow             | iun                       |
      | FAIL_CON996_PCRETRY_FURTO_AR  | Via@FAIL_CON996_PCRETRY_FURTO_AR  | TRACKING_ID_NOT_FOUND   |  SEQUENCE_VALIDATION  | VNYD-XTKN-JKYX-202508-G-1 |
      | OK_AR_TIMESTAMP_ERR           | Via@OK_AR_TIMESTAMP_ERR           | DATE_ERROR              |  SEQUENCE_VALIDATION  | DGZM-UTVT-WRHX-202508-H-1 |
      | OK_AR_NOT_ORDERED             | Via@OK_AR_NOT_ORDERED             | TRACKING_ID_NOT_FOUND   |  SEQUENCE_VALIDATION  | HYEV-LDKQ-LHEL-202508-T-1 |
      | OK_AR_BAD_EVENT               | Via@OK_AR_BAD_EVENT               | TRACKING_ID_NOT_FOUND   |  SEQUENCE_VALIDATION  | DETN-UGHR-LHDQ-202508-P-1 |
      | OK_AR_NO_EVENT_B               | Via@OK_AR_NO_EVENT_B             | TRACKING_ID_NOT_FOUND   |  SEQUENCE_VALIDATION  | DETN-UGHR-LHDQ-202508-P-1 |


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
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>" e iun "<iun>"
    Examples:
      | sequence                          | physicalAddress                   | iun                         |
      | OK_AR                             | Via@ok_AR                         | YJLZ-QZET-TYUT-202508-D-1   |
      | OK-Retry_AR                       | Via@OK-Retry_AR                   | LJTK-DLUN-WTVL-202508-E-1   |
      | FAIL-Discovery_AR                 | Via@fail-Discovery_AR             | GZMX-YAQW-UJHG-202508-L-1   |
      | FAIL_AR                           | Via@fail_AR                       | QPQE-TDGW-WYJY-202508-T-1   |
      | FAIL-Irreperibile_AR              | Via@FAIL-IRREPERIBILE_AR          | HJWJ-GNXW-YEPW-202508-P-1   |
      | OK-Giacenza_AR                    | Via@OK-Giacenza_AR                | RUTE-DUHX-MDZR-202508-K-1   |
      | FAIL-Giacenza_AR                  | Via@FAIL-Giacenza_AR              | QEQP-TGKM-MZAK-202508-K-1   |
      | FAIL-CompiutaGiacenza_AR          | Via@FAIL-CompiutaGiacenza_AR      | ADVM-KPUT-GYNK-202508-R-1   |
      | OK-NonRendicontabile_AR           | Via@OK-NonRendicontabile_AR       | KGRN-MHXL-LHJU-202508-J-1   |
      | OK-CausaForzaMaggiore_AR          | Via@OK-CausaForzaMaggiore_AR      | TGAU-TPQW-JXPW-202508-X-1   |
      | FAIL_CON996_PCRETRY_FURTO_AR      | Via@FAIL_CON996_PCRETRY_FURTO_AR  | VNYD-XTKN-JKYX-202508-G-1   |
      | OK_AR_INVALID_DATETIME            | Via@OK_AR_INVALID_DATETIME        | GUNV-DURQ-YKLW-202508-V-1   |
      | OK_AR_NO_EVENT_B                  | Via@OK_AR_NO_EVENT_B              | PUNP-DRJK-RVJV-202508-G-1   |
      | OK_AR_TIMESTAMP_ERR               | Via@OK_AR_TIMESTAMP_ERR           | DGZM-UTVT-WRHX-202508-H-1   |
      | OK_AR_NOT_ORDERED                 | Via@OK_AR_NOT_ORDERED             | HYEV-LDKQ-LHEL-202508-T-1   |
      | OK_GIACENZA_AR_2                  | Via@OK_GIACENZA_AR_2              | RXTU-MPZN-NENR-202508-G-1   |
      | OK_GIACENZA_AR_3                  | Via@OK_GIACENZA_AR_3              | DVZH-RQWL-YUAZ-202508-J-1   |
      | OK_GIACENZA_AR_4                  | Via@OK_GIACENZA_AR_4              | WKWZ-NYKU-GPAU-202508-A-1   |
      | OK_AR_BAD_EVENT                   | Via@OK_AR_BAD_EVENT               | DETN-UGHR-LHDQ-202508-P-1   |

      | ok_RIR                            | Via@ok_RIR                        | NYTX-RLJE-QNWN-202508-X-1   |
      | fail_RIR                          | Via@fail_RIR                      | GAQG-YLYX-HTPH-202508-X-1   |
      | OK-Retry_RIR                      | Via@OK-Retry_RIR                  | KWYM-HWYX-AHEW-202508-U-1   |