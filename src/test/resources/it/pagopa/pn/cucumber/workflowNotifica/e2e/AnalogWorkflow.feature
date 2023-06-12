Feature: Workflow analogico

  @e2e
  Scenario: [E2E-WF-ANALOG-1] Invio notifica con percorso analogico 890 con verifica elementi di timeline SEND_ANALOG_PROGRESS con legalFactId di category ANALOG_DELIVERY
  e documentType 23L, SEND_ANALOG_FEEDBACK e ANALOG_SUCCESS_WORKFLOW
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0 con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0, con deliveryDetailCode "RECAG001B", legalFactId con category "ANALOG_DELIVERY" e documentType "23L"
    And viene letta la timeline fino all'elemento "SEND_ANALOG_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
    And viene letta la timeline fino all'elemento "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-2] Invio notifica con percorso analogico. Successo giacenza delegato 890 (OK-GiacenzaDelegato-lte10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-PersonaAbilitata_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0 con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0, con deliveryDetailCode "RECAG002B", legalFactId con category "ANALOG_DELIVERY" e documentType "23L"
    And viene letta la timeline fino all'elemento "SEND_ANALOG_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG002C |
    And viene letta la timeline fino all'elemento "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-3] Invio notifica con percorso analogico 890. Successo al secondo tentativo invio 890.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Retry_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0 con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0, con deliveryDetailCode "RECAG004" e con deliveryFailureCause "F01 F02 F03 F04"
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0 con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0, con deliveryDetailCode "RECAG001B", legalFactId con category "ANALOG_DELIVERY" e documentType "23L"
    And viene letta la timeline fino all'elemento "SEND_ANALOG_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
    And viene letta la timeline fino all'elemento "ANALOG_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    Then viene verficato che il numero di elementi di timeline "PREPARE_ANALOG_DOMICILE" della notifica sia di 2

  @e2e
  Scenario: [E2E-WF-ANALOG-4] Invio notifica con percorso analogico. Successo invio RS (OK_RS)
    Given viene generata una nuova notifica
      | subject | notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Leonardo |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And viene letta la timeline fino all'elemento "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |

  @e2e
  Scenario: [E2E-WF-ANALOG-5] Invio notifica con percorso analogico. Successo invio RS (OK_RS) in cui la notifica viene visualizzata prima
  dell’evento SEND_SIMPLE_REGISTERED_LETTER
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Mario Gherkin |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata da "Mario Gherkin"
    Then viene verficato che il numero di elementi di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" della notifica sia di 0

  @e2e
  Scenario: [E2E-WF-ANALOG-6] Invio notifica con percorso analogico. Successo al secondo tentativo invio RS (OK-Retry_RS).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Leonardo |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@OK-Retry_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And viene letta la timeline fino all'elemento "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene letta la timeline fino all'elemento "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    Then viene verficato che il numero di elementi di timeline "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" della notifica sia di 2

  @e2e
  Scenario: [E2E-WF-ANALOG-7] Invio notifica con percorso analogico. Successo giacenza lte 890 (OK-Giacenza-lte10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0 con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0, con deliveryDetailCode "RECAG005B", legalFactId con category "ANALOG_DELIVERY" e documentType "23L"
    And viene letta la timeline fino all'elemento "SEND_ANALOG_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0 con deliveryDetailCode "RECAG005C"

  @e2e
  Scenario: [E2E-WF-ANALOG-8] Invio notifica con percorso analogico. Successo giacenza gt 890 (OK-Giacenza-gt10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0 con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0, con deliveryDetailCode "RECAG011B", legalFactId con category "ANALOG_DELIVERY" e documentType "23L"
    And viene letta la timeline fino all'elemento "SEND_ANALOG_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0 con deliveryDetailCode "RECAG005C"

  @e2e
  Scenario: [E2E-WF-ANALOG-9] Invio notifica con percorso analogico. Successo giacenza 890 gt 23L(OK-Giacenza-gt10-23L_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario
      | denomination | Leonardo |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0 con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0, con deliveryDetailCode "RECAG005B", legalFactId con category "ANALOG_DELIVERY" e documentType "23L"
    And viene letta la timeline fino all'elemento "SEND_ANALOG_FEEDBACK"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And vengono letti gli eventi fino all'elemento di timeline "SEND_ANALOG_PROGRESS" della notifica per il destinatario 0 con deliveryDetailCode "RECAG005C"

