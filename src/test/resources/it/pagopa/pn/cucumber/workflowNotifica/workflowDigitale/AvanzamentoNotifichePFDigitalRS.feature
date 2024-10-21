Feature: avanzamento notifiche b2b con workflow cartaceo RS/RIR

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @dev @workflowDigitale
  #[B2B_TIMELINE_RS_2], [B2B_TIMELINE_RIS_1]
  Scenario Outline: [B2B_TIMELINE_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | <physicalAddress> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    Examples:
      | physicalAddress |
      | Via@ok_RS       |
      | Via@ok-Retry_RS |
      | Via@ok_RIS |

  @dev
  #[B2B_TIMELINE_12], [B2B_TIMELINE_13], [B2B_TIMELINE_RS_6]
  Scenario Outline: [B2B_TIMELINE_RS_5] Invio notifica digitale ed attesa relativo elemento di timeline - scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal <comune> e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica <timelineEvent>
    Examples:
      | comune | timelineEvent |
      | "Comune_Multi" | "PREPARE_SIMPLE_REGISTERED_LETTER" |
      | "Comune_1"     | "SEND_SIMPLE_REGISTERED_LETTER"    |

  Scenario:  [B2B_TIMELINE_RS_7]Invio notifica ed attesa elemento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode RECRS001C positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS001C"
  #"sequence": "@sequence.5s-CON080.5s-RECRS001C"

#  Il test viene ignorato poiché il mock genera una sequence non valida con deliveryDetailCode diverso
  @dev @workflowDigitale @ignore
  Scenario: [B2B_TIMELINE_RIS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | ZONE_1       |
      | physicalAddress_province     | Paris        |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_address      | Via@ok_RIS   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRSI003C"
    #"sequence": "@sequence.5s-CON080.5s-RECRSI001.5s-RECRSI002.5s-RECRSI003C"

  #- RECRSI003C --> c'è il mock @OK_RIS ma non c'è il controllo della sua presenza
  #- RECRSI004B --> presente in un test e2e con mock @FAIL_RIS

  @dev  @workflowDigitale
  Scenario: [B2B_TIMELINE_RIS_4] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK_scenario negativo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRSI004B" e verifica tipo DOC "Plico"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRSI004C"

  #"sequence": "@sequence.5s-CON080.5s-RECRSI001.5s-RECRSI002.5s-RECRSI004A.5s-RECRSI004B[DOC:Plico].5s-RECRSI004C"

