Feature: Verifica delle stampe a colori con successivo controllo manuale delle casistiche

  @rasterColorPrint
  Scenario Outline: [B2B-LEGALFACT_COLOR_CONTENT_VERIFY_1] Data una notifica analogica, con specifico allegato e inviato a specifici comuni
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | feePolicy             | DELIVERY_MODE               |
      | document              | <documentType>              |
      | physicalCommunication | <physicalCommunication>     |
    And destinatario
      | denomination                        | Test AR Fail     |
      | taxId                               | CNCGPP80A01H501J |
      | digitalDomicile                     | NULL             |
      | physicalAddress_address             | VIA_PN_2_CONS    |
      | physicalAddress_zip                 | <zip>            |
      | physicalAddress_municipality        | <municipality>   |
      | physicalAddress_province            | <province>       |
      | physicalAddress_addressDetails      | 0_CHAR           |
      | physicalAddress_municipalityDetails | 0_CHAR           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then stampa log dello IUN della notifica "<physicalCommunication>" con allegato "<documentType>" su comune "<municipality>"
    Examples:
      | documentType      | zip   | municipality | province | physicalCommunication |
      | ALLEGATO_1_BN     | 10124 | Torino       | TO       | AR_REGISTERED_LETTER  |
      | ALLEGATO_3_COLORI | 10124 | Torino       | TO       | REGISTERED_LETTER_890 |
      | ALLEGATO_2_BN     | 80124 | Napoli       | NA       | AR_REGISTERED_LETTER  |
      | ALLEGATO_4_COLORI | 80124 | Napoli       | NA       | REGISTERED_LETTER_890 |
      | ALLEGATO_1_BN     | 00124 | Roma         | RM       | AR_REGISTERED_LETTER  |
      | ALLEGATO_3_COLORI | 00124 | Roma         | RM       | REGISTERED_LETTER_890 |
      | ALLEGATO_2_BN     | 30124 | Venezia      | VE       | AR_REGISTERED_LETTER  |
      | ALLEGATO_4_COLORI | 30124 | Venezia      | VE       | REGISTERED_LETTER_890 |
      | ALLEGATO_1_BN     | 70124 | Bari         | BA       | AR_REGISTERED_LETTER  |
      | ALLEGATO_3_COLORI | 70124 | Bari         | BA       | REGISTERED_LETTER_890 |

  @rasterScartoCON996 @workflowAnalogico
  Scenario: [B2B-LEGALFACT_RASTER_1] Viene inviata una notifica con delivery detail code CON996 per il flusso di deceduto
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR deceduto                    |
      | taxId                   | CLMCST42R12D969Z                    |
      | digitalDomicile         | NULL                                |
      | physicalAddress_address | Via@FAIL-CON996_PCRETRY_DECEDUTO-AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | loadTimeline               | true     |
      | details                    | NOT_NULL |
      | details_deliveryDetailCode | CON996   |
      | details_recIndex           | 0        |
      | details_sentAttemptMade    | 0        |
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" per l'utente 0
    And viene verificato che non esista l'elemento "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"

  @rasterScartoCON996 @workflowAnalogico
  Scenario: [B2B-LEGALFACT_RASTER_2] Viene inviata una notifica per la quale si riceve l’evento di CON996, in seguito ad un un secondo tentativo termini con l’evento di refinement
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR CON996           |
      | taxId                   | CLMCST42R12D969Z         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@OK_PCRETRY_CON996_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON996"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C" tentativo "ATTEMPT_1"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 1         |
      | details_deliveryDetailCode | RECRN001C |
      | details_responseStatus     | OK        |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
