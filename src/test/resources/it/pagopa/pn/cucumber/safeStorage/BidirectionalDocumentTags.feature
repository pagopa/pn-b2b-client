# PST: QA-18588 Collegamento originale-trasformato
Feature: Collegamento tra documento originale e documento trasformato SafeStorage

  # Quando il documento di una notifica cartacea viene rasterizzato o normalizzato, SafeStorage
  # collega i due documenti: l'originale punta al trasformato (multivalore, un riferimento per ogni
  # trasformazione) e il trasformato punta all'originale.
  # Il trasformato è il documento ricevuto dal recapitista simulato. La rasterizzazione si ottiene
  # con il retry dopo CON996, come in B2B-LEGALFACT_RASTER_2; la normalizzazione richiede un
  # mittente abilitato nell'ambiente.

  @e2e @bidirectionalTags
  Scenario Outline: [SS-BIDIRECTIONAL-TAGS-1.1] Il documento originale riporta il riferimento al documento trasformato
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR trasformazione |
      | taxId                   | CLMCST42R12D969Z       |
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | <indirizzo>            |
      | payment                 | NULL                   |
    When la notifica viene inviata tramite api b2b dal "<mittente>" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And il recapitista ha ricevuto per il destinatario 0 il documento della notifica trasformato tramite "<trasformazione>"
    Then il documento della notifica riporta il riferimento al documento trasformato

    Examples:
      | trasformazione  | mittente     | indirizzo                |
      | rasterizzazione | Comune_Multi | Via@OK_PCRETRY_CON996_AR |

    @ignore
    Examples:
      | trasformazione  | mittente        | indirizzo |
      | normalizzazione | DA_DEFINIRE_PR4 | Via@ok_AR |

  @e2e @bidirectionalTags
  Scenario Outline: [SS-BIDIRECTIONAL-TAGS-1.2] Il documento trasformato riporta il riferimento al documento originale
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR trasformazione |
      | taxId                   | CLMCST42R12D969Z       |
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | <indirizzo>            |
      | payment                 | NULL                   |
    When la notifica viene inviata tramite api b2b dal "<mittente>" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And il recapitista ha ricevuto per il destinatario 0 il documento della notifica trasformato tramite "<trasformazione>"
    Then il documento trasformato riporta il riferimento al documento della notifica

    Examples:
      | trasformazione  | mittente     | indirizzo                |
      | rasterizzazione | Comune_Multi | Via@OK_PCRETRY_CON996_AR |

    @ignore
    Examples:
      | trasformazione  | mittente        | indirizzo |
      | normalizzazione | DA_DEFINIRE_PR4 | Via@ok_AR |

  # Il tag di prova va associato prima della trasformazione: se all'accettazione il collegamento
  # è già presente, lo scenario viene saltato.
  @e2e @bidirectionalTags
  Scenario Outline: [SS-BIDIRECTIONAL-TAGS-2.1] Il collegamento non cancella né modifica le informazioni già presenti sul documento originale
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR trasformazione |
      | taxId                   | CLMCST42R12D969Z       |
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | <indirizzo>            |
      | payment                 | NULL                   |
    When la notifica viene inviata tramite api b2b dal "<mittente>" e si attende che lo stato diventi "ACCEPTED"
    And al documento della notifica viene associata un'informazione di prova
    And si registrano le informazioni associate al documento della notifica, ancora privo del riferimento al documento trasformato
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And il recapitista ha ricevuto per il destinatario 0 il documento della notifica trasformato tramite "<trasformazione>"
    Then il documento della notifica conserva le informazioni registrate
    And il documento della notifica riporta il riferimento al documento trasformato

    Examples:
      | trasformazione  | mittente     | indirizzo                |
      | rasterizzazione | Comune_Multi | Via@OK_PCRETRY_CON996_AR |

    @ignore
    Examples:
      | trasformazione  | mittente        | indirizzo |
      | normalizzazione | DA_DEFINIRE_PR4 | Via@ok_AR |
