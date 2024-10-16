Feature: avanzamento notifiche analogico RS persona giuridica

  @dev @workflowDigitale
    # [B2B_TIMELINE_PG_RS_2]
  Scenario Outline: [B2B_TIMELINE_PG_RS_1] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | <physical_address> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    Examples:
      | physical_address |
      | Via@ok_RS |
      | Via@ok-Retry_RS |

  @dev @workflowDigitale
  Scenario: [B2B_TIMELINE_PG_RS_3] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RS  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"

  @dev @workflowDigitale
  Scenario: [B2B_TIMELINE_PG_RIS_1] Invio notifica digitale ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RIS   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"

  @dev
  #[B2B_TIMELINE_PG_8]
  Scenario: [B2B_TIMELINE_PG_RS_4] Invio notifica digitale ed attesa elemento di timeline DIGITAL_FAILURE_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Analogic e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_SIMPLE_REGISTERED_LETTER"

  @dev
  #[B2B_TIMELINE_PG_9]
  Scenario: [B2B_TIMELINE_PG_RS_5] Invio notifica digitale ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Analogic e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"

  @dev @workflowDigitale
  Scenario: [B2B_TIMELINE_PG_RS_6] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@fail_RS  |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS002B" e verifica tipo DOC "Plico"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS002C" e deliveryFailureCause "M07"
#"sequence": "@sequence.5s-CON080.5s-RECRS002A[FAILCAUSE:M07].5s-RECRS002B[DOC:Plico].5s-RECRS002C"

  @dev @workflowDigitale
  Scenario: [B2B_TIMELINE_PG_RS_7] Invio notifica ed attesa elemento di timeline SEND_SIMPLE_REGISTERED_LETTER_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it    |
      | physicalAddress_address | Via@ok-Retry_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS006" e deliveryFailureCause "F03"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" con deliveryDetailCode "RECRS001C"
#"sequence": "@sequence.5s-CON080.5s-RECRS006[FAILCAUSE:F03]@retry.5s-CON080.5s-RECRS001C"