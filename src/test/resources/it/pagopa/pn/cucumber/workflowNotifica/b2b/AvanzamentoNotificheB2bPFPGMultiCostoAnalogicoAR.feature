Feature: costo notifica con workflow analogico per multi destinatario

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @dev
  Scenario: [B2B_COSTO_ANALOG_MULTI_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And viene verificato il costo = "267" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev
  Scenario: [B2B_COSTO_ANALOG_MULTI_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And viene verificato il costo = "167" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev
  Scenario: [B2B_COSTO_ANALOG_MULTI_3] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIR     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And viene verificato il costo = "332" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev
  Scenario: [B2B_COSTO_ANALOG_MULTI_4] Invio notifica e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIR     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And viene verificato il costo = "232" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev
  Scenario: [B2B_COSTO_ANALOG_MULTI_5] Invio notifica con allegato e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm      | SI        |
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And viene verificato il costo = "400" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev
  Scenario: [B2B_COSTO_ANALOG_MULTI_6] Invio notifica con allegato e verifica costo con FSU + @OK_AR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm      | SI        |
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And viene verificato il costo = "300" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev
  Scenario: [B2B_COSTO_ANALOG_MULTI_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile              | NULL           |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIR     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And viene verificato il costo = "565" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev
  Scenario: [B2B_COSTO_ANALOG_MULTI_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile              | NULL           |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIR     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And viene verificato il costo = "465" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
