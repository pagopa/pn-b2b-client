Feature: SEND - Tracciatura dell'indagine in loco e della fonte dell'indirizzo per gli invii cartacei

  # PST - Tracciatura Indagine in loco e fonte secondo invio (Epic PN-18659)
  # I campi receiverAddressSourceType e hasOnSiteInvestigation vengono verificati sulla timeline di DynamoDB,
  # indipendentemente dalla versione delle API B2B che li espone (punto aperto P3).
  #
  # Dati di test:
  # PA abilitata al VAS -> Comune_Multi (il VAS si attiva solo con indirizzo fisico non valorizzato)
  # PF censita sui registri (ANPR) -> Mario Cucumber
  # PG censita sui registri (REGISTRO_IMPRESE) -> 01113570442
  # PF con indirizzo restituito da ANPR mock al secondo tentativo -> FRMTTR76M06B715E
  # PF senza indirizzo su ANPR mock -> FNTLCU80T25F205R
  #
  # Il valore di hasOnSiteInvestigation su SEND_ANALOG_FEEDBACK al tentativo 0 segue la panoramica e il diagramma dell'SRS:
  # va riallineato se il punto aperto P1 viene chiuso diversamente.

  # ====================================================================================================================
  # SCENARIO 1 - Notifiche legali con invio cartaceo
  # CASO DI TEST 1.1: Verificare che, lungo tutto il percorso di recapito cartaceo di una notifica legale, vengano tracciate
  # la fonte dell'indirizzo utilizzato e l'eventuale indagine in loco del postino.
  # ====================================================================================================================

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_A
  Scenario: [PAPER_SOURCE_LEGAL_01_1_A] Indirizzo fornito dal mittente e consegna al primo tentativo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 0 riporti
      | receiverAddressSourceType | MITTENTE |
      | hasOnSiteInvestigation    | NULL     |
    And si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 1 non esista

  @tracciaturaIndagineInLoco @ignoreUat @PAPER_SOURCE_LEGAL_01_1_B
  Scenario: [PAPER_SOURCE_LEGAL_01_1_B] Indirizzo verificato sui registri nazionali per una persona fisica
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 0 riporti
      | receiverAddressSourceType | ANPR |
      | hasOnSiteInvestigation    | NULL |

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_C
  Scenario: [PAPER_SOURCE_LEGAL_01_1_C] Indirizzo verificato sui registri nazionali per una persona giuridica
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 0 riporti
      | receiverAddressSourceType | REGISTRO_IMPRESE |
      | hasOnSiteInvestigation    | NULL             |

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_D
  Scenario: [PAPER_SOURCE_LEGAL_01_1_D] Primo tentativo fallito con nuovo indirizzo individuato dal postino e utilizzato
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 0 riporti
      | receiverAddressSourceType | MITTENTE |
    And si verifica che l'elemento di timeline "SEND_ANALOG_FEEDBACK" del destinatario 0 al tentativo 0 riporti
      | hasOnSiteInvestigation | true |
    And si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 1 riporti
      | receiverAddressSourceType | INDAGINE |
      | hasOnSiteInvestigation    | true     |

  # Combinazione da validare in ambiente: l'indirizzo individuato dal postino ha un CAP non valido,
  # quindi non supera la normalizzazione e si ricorre ad ANPR (mock) per il secondo tentativo
  @tracciaturaIndagineInLoco @mockNR @PAPER_SOURCE_LEGAL_01_1_E
  Scenario: [PAPER_SOURCE_LEGAL_01_1_E] Primo tentativo fallito con indirizzo individuato dal postino non utilizzabile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario
      | denomination            | Test 890 Fail Discovery                  |
      | taxId                   | FRMTTR76M06B715E                         |
      | digitalDomicile         | NULL                                     |
      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILEBADCAP_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then si verifica che l'elemento di timeline "SEND_ANALOG_FEEDBACK" del destinatario 0 al tentativo 0 riporti
      | hasOnSiteInvestigation | true |
    And si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 1 riporti
      | receiverAddressSourceType | ANPR |
      | hasOnSiteInvestigation    | true |

  @tracciaturaIndagineInLoco @mockNR @PAPER_SOURCE_LEGAL_01_1_F
  Scenario: [PAPER_SOURCE_LEGAL_01_1_F] Primo tentativo fallito senza indagine in loco e secondo invio verso l'indirizzo dei registri nazionali
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | FRMTTR76M06B715E         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si verifica che l'elemento di timeline "SEND_ANALOG_FEEDBACK" del destinatario 0 al tentativo 0 riporti
      | hasOnSiteInvestigation | false |
    And si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 1 riporti
      | receiverAddressSourceType | ANPR  |
      | hasOnSiteInvestigation    | false |

  @tracciaturaIndagineInLoco @mockNR @PAPER_SOURCE_LEGAL_01_1_G
  Scenario: [PAPER_SOURCE_LEGAL_01_1_G] Primo tentativo fallito senza alcun indirizzo alternativo disponibile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | FNTLCU80T25F205R         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 0 riporti
      | receiverAddressSourceType | MITTENTE |
    And si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 1 non esista

  @tracciaturaIndagineInLoco @ignoreUat @PAPER_SOURCE_LEGAL_01_1_H
  Scenario: [PAPER_SOURCE_LEGAL_01_1_H] Notifica multidestinatario con un solo destinatario verificato sui registri nazionali
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 0 al tentativo 0 riporti
      | receiverAddressSourceType | ANPR |
    And si verifica che l'elemento di timeline "SEND_ANALOG_DOMICILE" del destinatario 1 al tentativo 0 riporti
      | receiverAddressSourceType | MITTENTE |
