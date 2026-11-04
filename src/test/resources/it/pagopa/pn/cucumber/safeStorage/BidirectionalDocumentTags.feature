# PST: PST QA-18588 Collegamento originale-trasformato
Feature: Collegamento tra documento originale e documento trasformato SafeStorage

  # Quando i canali esterni trasformano il documento di una notifica cartacea prima dell'invio al
  # recapitista (rasterizzazione o normalizzazione), registrano in SafeStorage il collegamento nei
  # due sensi: sul documento della notifica il riferimento al documento trasformato, sul documento
  # trasformato il riferimento al documento della notifica.
  #
  # Il documento trasformato e' quello che il recapitista simulato riceve al posto del documento
  # della notifica. Le notifiche hanno un solo documento e nessun allegato di pagamento, cosi' il
  # documento trasformato e' uno solo e il riscontro e' univoco. La richiesta ricevuta dal
  # recapitista si legge dopo l'esito della spedizione in timeline (SEND_ANALOG_FEEDBACK).
  #
  # Rasterizzazione: il recapitista segnala un errore di valutazione della stampabilita (CON996) e
  # la spedizione viene ripetuta chiedendo la rasterizzazione, come in B2B-LEGALFACT_RASTER_2.
  # Normalizzazione: l'ente mittente deve essere abilitato alla normalizzazione nell'ambiente di
  # prova (prerequisito PR4 del PST). Finche' l'ente non e' indicato, la combinazione resta
  # esclusa con @ignore e il mittente DA_DEFINIRE_PR4 va sostituito.
  #
  # Il caso 3.1 del PST (errore nella registrazione del collegamento) e' manuale e non e' in
  # questa feature.

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

  # L'informazione di prova viene associata subito dopo l'accettazione: se il documento della
  # notifica riporta gia' il riferimento al documento trasformato, l'esecuzione non e' valida e
  # lo scenario viene interrotto senza esito (skipped), da ripetere.
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
