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
   # When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>" e iun "<iun>"
    Examples:
      | sequence                          | physicalAddress                   | iun                         |
      | OK_AR                             | Via@ok_AR                         | VXAY-LYAJ-EQXH-202508-G-1   |
      | OK-Retry_AR                       | Via@OK-Retry_AR                   | JNLR-MNUQ-TPJU-202508-K-1   |
      | FAIL-Discovery_AR                 | Via@fail-Discovery_AR             | JXEL-RHJU-DGVW-202508-P-1   |
      | FAIL_AR                           | Via@fail_AR                       | PYTX-MAKN-YUKE-202508-P-1   |
      | FAIL-Irreperibile_AR              | Via@FAIL-IRREPERIBILE_AR          | JQME-DLVK-LNPM-202508-Y-1   |
      | OK-Giacenza_AR                    | Via@OK-Giacenza_AR                | UJXL-GWNV-YTAN-202508-V-1   |
      | FAIL-Giacenza_AR                  | Via@FAIL-Giacenza_AR              | RKQN-DVXQ-EPND-202508-T-1   |
      | FAIL-CompiutaGiacenza_AR          | Via@FAIL-CompiutaGiacenza_AR      | VKTW-MZLP-RGRK-202508-W-1   |
      | OK-NonRendicontabile_AR           | Via@OK-NonRendicontabile_AR       | GNWM-DTGQ-YLUL-202508-Z-1   |
      | OK-CausaForzaMaggiore_AR          | Via@OK-CausaForzaMaggiore_AR      | LGUZ-XUGA-YJDJ-202508-V-1   |
      | FAIL_CON996_PCRETRY_FURTO_AR      | Via@FAIL_CON996_PCRETRY_FURTO_AR  | LHQR-NQJD-VGAQ-202508-Y-1   |
      | OK_AR_INVALID_DATETIME            | Via@OK_AR_INVALID_DATETIME        | XHWE-ZAJD-JAUE-202508-U-1   |
      | KO_AR_NO_EVENT_B                  | Via@KO_AR_NO_EVENT_B              | VMQD-AYMK-LQTR-202508-Z-1   |
      | OK_AR_TIMESTAMP_ERR               | Via@OK_AR_TIMESTAMP_ERR           | XGLY-VRGM-ZUPT-202508-M-1   |
      | OK_AR_NOT_ORDERED                 | Via@OK_AR_NOT_ORDERED             | GJRQ-MPZK-KJKM-202508-M-1   |
      | OK_GIACENZA_AR_2                  | Via@OK_GIACENZA_AR_2              | NUTU-ZKYA-VYQA-202508-K-1   |
      | OK_GIACENZA_AR_3                  | Via@OK_GIACENZA_AR_3              | RJKP-JLPQ-DKJY-202508-J-1   |
      | OK_GIACENZA_AR_4                  | Via@OK_GIACENZA_AR_4              | KYDH-QUMN-HERG-202508-D-1   |
      | OK_AR_BAD_EVENT                   | Via@OK_AR_BAD_EVENT               | HKPV-MHWT-KALU-202508-R-1   |

      | ok_RIR                            | Via@ok_RIR                        | YNTA-EGXP-MLKN-202508-Q-1   |
      | fail_RIR                          | Via@fail_RIR                      | NPLV-RWXM-JAKL-202508-D-1   |
      | OK-Retry_RIR                      | Via@OK-Retry_RIR                  | KDRZ-UAED-MGVA-202508-P-1   |