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
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                                  |
      | physicalAddress_address | Via@OK-AR_REC016_REC018_CON09B_REC991 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    Then si verifica che il deliveryDetailCode "REC016" non è presente in timeline
    And si verifica che il deliveryDetailCode "REC018" non è presente in timeline
    And si verifica che il deliveryDetailCode "CON09B" non è presente in timeline
    And si verifica che il deliveryDetailCode "REC991" non è presente in timeline
    And si verifica che su DynamoDB è presente l'elemento "PREPARE_ANALOG_DOMICILE" nella tabella pn-PaperTrackings con statusCodes "REC016,REC018,CON09B,REC991"

  @consolidatoreFase2
  Scenario: [CON_F_TWO_04_01_B] Verifica nuovi eventi REC016, REC018, CON09B, REC991 per 890
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                                   |
      | physicalAddress_address | Via@OK-890_REC016_REC018_CON09B_REC991 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" per l'utente 0
    Then si verifica che il deliveryDetailCode "REC016" non è presente in timeline
    And si verifica che il deliveryDetailCode "REC018" non è presente in timeline
    And si verifica che il deliveryDetailCode "CON09B" non è presente in timeline
    And si verifica che il deliveryDetailCode "REC991" non è presente in timeline
    And si verifica che su DynamoDB è presente l'elemento "PREPARE_ANALOG_DOMICILE" nella tabella pn-PaperTrackings con statusCodes "REC016,REC018,CON09B,REC991"

  @consolidatoreFase2
  Scenario: [CON_F_TWO_04_01_C] Verifica nuovo evento RECAG010A per 890
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                 |
      | physicalAddress_address | Via@OK-890_RECAG010A |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" per l'utente 0
    Then si verifica che il deliveryDetailCode "RECAG010A" non è presente in timeline
    And si verifica che su DynamoDB è presente l'elemento "PREPARE_ANALOG_DOMICILE" nella tabella pn-PaperTrackings con statusCodes "RECAG010A"


  @consolidatoreFase2
  Scenario Outline: [CON_F_TWO_06_01] Normalizzazione sincrona con dati configurabili
    Given preparo una request di normalizzazione con:
      | addressRow    | <addressRow>    |
      | addressRow2   | <addressRow2>   |
      | cap           | <cap>           |
      | city          | <city>          |
      | city2         | <city2>         |
      | pr            | <pr>            |
      | country       | <country>       |
      | nameRow2      | <nameRow2>      |
    When invoco la normalizzazione sincrona
    Then lo status code della response è <statusCode>

    Examples:
      | addressRow         | addressRow2 | cap   | city                 | city2  | pr | country | nameRow2 | statusCode |
      | VIA ROMA 1         | SCALA A     | 00118 | ROMA                 | CENTRO | RM | ITALIA  | null     | 200        |
      | VIA ROMA 1         | SCALA A     | 00118 |                      |        | RM | ITALIA  | null     | 200        |
      |                    |             | 00118 | ROMA                 | CENTRO | RM | ITALIA  | null     | 200        |
      | VIA ROMA 1         | SCALA A     | 00118 | LOCALITA INESISTENTE |        | RM | ITALIA  | null     | 200        |
      | VIA INESISTENTE 99 | SCALA A     | 00118 | ROMA                 | CENTRO | RM | ITALIA  | null     | 200        |
      | VIA ROMÀ 1         | SCALA A     | 00118 | ROMA                 | CENTRO | RM | ITALIA  | null     | 200        |

