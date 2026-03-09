Feature: Casi di test relativi al nuovo microservizio pn-paper-tracker per il prodotto RS/RIS

  # ---------------- DRY RUN MODE ----------------
  @paperTrackerRSDryRunMode
  Scenario Outline: [PAPER_TRACKER_DRY_RUN_RS_1] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs per il prodotto RS
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@<sequenceName> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<sequenceName>"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "RS"
    And si verifica che la risposta tracking per la sequence "<sequenceName>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
    Examples:
      | sequenceName                       |
      | OK_RS                              |
      | FAIL_RS                            |
      | OK-Retry_RS                        |
      | OK_RS-CON020                       |
      | FAIL_DECEDUTO_RS                   |
      | FAIL_RS_IRREPERIBILE_ASSOLUTO      |
      | FAIL_RS_MANCATA_CONSEGNA_PGIACENZA |
      | OK_RS_COMPIUTA_GIACENZA            |
      | OK_RS_CONSEGNA_PGIACENZA           |

  @paperTrackerRISDryRunMode
  Scenario Outline: [PAPER_TRACKER_DRY_RUN_RIS_1] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs per il prodotto RIS
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_State        | FRANCIA            |
      | physicalAddress_municipality | Parigi             |
      | physicalAddress_zip          | ZONE_1             |
      | physicalAddress_province     | Paris              |
      | digitalDomicile_address      | test@fail.it       |
      | physicalAddress_address      | Via@<sequenceName> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "<sequenceName>"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And genera la key da utilizzare per invocare l'API per il prodotto: "RS"
    And si verifica che la risposta tracking per la sequence "<sequenceName>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Then si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: "<sequenceName>"
    Examples:
      | sequenceName |
      | OK_RIS       |
      | FAIL_RIS     |

  # ---------------- RUN MODE ----------------
  @paperTrackerRSRunMode
  Scenario Outline: [PAPER_TRACKER_RUN_RS_1] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs per il prodotto RS
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@<sequenceName> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "RS"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Examples:
      | sequenceName                       |
      | OK_RS                              |
      | FAIL_RS                            |
      | OK-Retry_RS                        |
      | OK_RS-CON020                       |
      | FAIL_DECEDUTO_RS                   |
      | FAIL_RS_IRREPERIBILE_ASSOLUTO      |
      | FAIL_RS_MANCATA_CONSEGNA_PGIACENZA |
      | OK_RS_COMPIUTA_GIACENZA            |
      | OK_RS_CONSEGNA_PGIACENZA           |

  @paperTrackerRISRunMode
  Scenario Outline: [PAPER_TRACKER_RUN_RIS_1] Verifica la correttezza dei dati presenti all'interno delle tabelle Tracker, DryRunOutputs per il prodotto RIS
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_State        | FRANCIA            |
      | physicalAddress_municipality | Parigi             |
      | physicalAddress_zip          | ZONE_1             |
      | physicalAddress_province     | Paris              |
      | digitalDomicile_address      | test@fail.it       |
      | physicalAddress_address      | Via@<sequenceName> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then si controlla che non ci siano eventi duplicati
    And genera la key da utilizzare per invocare l'API per il prodotto: "RS"
    And si controlla che siano presenti tutti gli eventi relativi alla sequence "<sequence>"
    And si verifica che la risposta tracking per la sequence "<sequence>" contenga tutti gli elementi attesi e che sia strutturalmente valida
    Examples:
      | sequenceName |
      | OK_RIS       |
      | FAIL_RIS     |

