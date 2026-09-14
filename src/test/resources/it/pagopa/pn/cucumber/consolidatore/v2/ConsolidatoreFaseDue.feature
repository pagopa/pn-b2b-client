Feature: Gara Consolidatore/Recapito FASE 2

#  @consolidatoreFase2
#  Scenario: [CON_F_TWO_01_01_A] Gestione errori di rendicontazione per AR (Happy Path)
#    Given viene generata una nuova notifica
#      | subject            | notifica analogica con cucumber |
#      | senderDenomination | Comune di palermo               |
#    And destinatario Mario Gherkin e:
#      | digitalDomicile         | NULL       |
#      | physicalAddress_address | Via@OK-AR_RETRY_CONS_1 |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    When viene verificato che lato utente l'elemento di timeline "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001A" non esista
#    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
#    And verifico che su DynamoDB è presente l'elemento "PREPARE_ANALOG_DOMICILE" nella tabella pn-paperTrackerError esclusivamente con category:
#      | DATE_ERROR |
#      | RENDICONTAZIONE_SCARTATA |
#
#
#  @consolidatoreFase2
#  Scenario: [CON_F_TWO_01_01_B] Gestione errori di rendicontazione per 890 (Happy Path)
#    Given viene generata una nuova notifica
#      | subject            | notifica analogica con cucumber |
#      | senderDenomination | Comune di palermo               |
#    And destinatario Mario Gherkin e:
#      | digitalDomicile         | NULL       |
#      | physicalAddress_address | Via@OK-AR_RETRY_CONS_2 |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    When viene verificato che lato utente l'elemento di timeline "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001A" non esista
#    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
#    And verifico che su DynamoDB è presente l'elemento "PREPARE_ANALOG_DOMICILE" nella tabella pn-paperTrackerError esclusivamente con category:
#      | DATE_ERROR |
#      | RENDICONTAZIONE_SCARTATA |


  @consolidatoreFase2
  Scenario: [CON_F_TWO_04_01_A] Verifica nuovi eventi REC016, REC018, CON09B, REC991 per AR
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@OK-AR_REC016_REC018_CON09B_REC991 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    Then si verifica che il deliveryDetailCode "REC016" non è presente in timeline
    And si verifica che il deliveryDetailCode "REC018" non è presente in timeline
    And si verifica che il deliveryDetailCode "CON09B" non è presente in timeline
    And si verifica che il deliveryDetailCode "REC991" non è presente in timeline

  @consolidatoreFase2
  Scenario: [CON_F_TWO_04_01_B] Verifica nuovi eventi REC016, REC018, CON09B, REC991 per 890
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@OK-890_REC016_REC018_CON09B_REC991 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    Then si verifica che il deliveryDetailCode "REC016" non è presente in timeline
    And si verifica che il deliveryDetailCode "REC018" non è presente in timeline
    And si verifica che il deliveryDetailCode "CON09B" non è presente in timeline
    And si verifica che il deliveryDetailCode "REC991" non è presente in timeline

  @consolidatoreFase2
  Scenario: [CON_F_TWO_04_01_C] Verifica nuovo evento RECAG010A per 890
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@OK-890_RECAG010A_COERENTE |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005C |
      | details_sentAttemptMade    | 0         |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON09B"
    Then si verifica che il deliveryDetailCode "RECAG010A" non è presente in timeline


  @consolidatoreFase2
  Scenario Outline: [CON_F_TWO_06_01] Normalizzazione sincrona con dati configurabili
    Given preparo una request di normalizzazione con:
      | id                   | <id>                   |
      | provincia            | <provincia>            |
      | cap                  | <cap>                  |
      | localita             | <localita>             |
      | localitaAggiuntiva   | <localitaAggiuntiva>   |
      | indirizzo            | <indirizzo>            |
      | indirizzoAggiuntivo  | <indirizzoAggiuntivo>  |
      | stato                | <stato>                |
    When invoco la normalizzazione sincrona
    Then lo status code della response è <statusCode>
    And il codice errore di normalizzazione è "<nErroreNorm>"
    Examples:
      | id     | provincia | cap   | localita              | localitaAggiuntiva        | indirizzo          | indirizzoAggiuntivo | stato  | statusCode | nErroreNorm |
      | test01 | PI        | 56022 | CASTELFRANCO DI SOTTO | LOCALITA' VILLA CAMPANILE | VIA ULIVI 85       |                     | ITALIA | 200        | null        |
      | test02 | PI        | 56022 |                       |                            | VIA ULIVI 85       |                     | ITALIA | 200        | 101         |
      | test03 | PI        | 56022 | CASTELFRANCO DI SOTTO | LOCALITA' VILLA CAMPANILE |                    |                     | ITALIA | 200        | 421         |
      | test04 | PI        | 56022 | LOCALITA INESISTENTE  |                            | VIA ULIVI 85       |                     | ITALIA | 200        | 102         |
      | test05 | PI        | 56022 | CASTELFRANCO DI SOTTO | LOCALITA' VILLA CAMPANILE | VIA INESISTENTE 99 |                     | ITALIA | 200        | 422         |
      | test06 | PI        | 56022 | CASTELFRANCO DI SOTTO | LOCALITA' VILLA CAMPANILE | VIA DELL'UNITÀ 85  |                     | ITALIA | 200        | 999         |


