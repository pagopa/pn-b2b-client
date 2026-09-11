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
  Scenario: [CON_F_TWO_04_01_A] Verifica nuovi eventi REC016, REC018, CON09B per AR
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@OK-AR_REC016_REC018_CON09B |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "REC016"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "REC018"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON09B"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0

  @consolidatoreFase2
  Scenario: [CON_F_TWO_04_01_B] Verifica nuovi eventi REC016, REC018, CON09B per 890
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@OK-890_REC016_REC018_CON09B |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "REC016"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "REC018"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON09B"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0

  @consolidatoreFase2
  Scenario: [CON_F_TWO_04_01_C] Verifica nuovi eventi REC991 AR
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@OFAIL-AR_REC991 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "REC991"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0

  @consolidatoreFase2
  Scenario: [CON_F_TWO_04_01_B] Verifica nuovi eventi REC016, REC018, CON09B per 890
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@OK-890_REC016_REC018_CON09B |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "REC016"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "REC018"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON09B"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0


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

    Examples:
      | id     | provincia | cap   | localita | localitaAggiuntiva | indirizzo  | indirizzoAggiuntivo | stato | statusCode |
      | ID-001 | RM        | 00100 | ROMA     | CENTRO             | VIA ROMA 1 | SCALA A              | IT    | 200        |


