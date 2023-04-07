Feature: costo notifica con workflow analogico per multi destinatario RS

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_1] Invio notifica e verifica costo con FCU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "233" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_2] Invio notifica e verifica costo con FCU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_3] Invio notifica e verifica costo con FCU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_State        | Francia    |
      | physicalAddress_municipality | Parigi     |
      | physicalAddress_zip          | 75007      |
      | physicalAddress_province     | Paris      |
      | physicalAddress_address      | Via@ok_RIS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "223" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_4] Invio notifica e verifica costo con FCU + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Francia    |
      | physicalAddress_municipality | Parigi     |
      | physicalAddress_zip          | 75007      |
      | physicalAddress_province     | Paris      |
      | physicalAddress_address      | Via@ok_RIS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_5] Invio notifica e verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "223" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_6] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_7] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm      | SI        |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "355" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_8] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm      | SI        |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_9] Invio notifica verifica con e allegato costo con FSU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "325" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_10] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
      | physicalAddress_zip     | 39100     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "212" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_AR |
      | physicalAddress_zip     | 38121     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_13] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "302" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_14] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIs |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_15] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm      | SI        |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_zip     | 39100     |
      | physicalAddress_address | Via@ok_RS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "212" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm      | SI        |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_zip     | 39100     |
      | physicalAddress_address | Via@ok_RS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_17] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI         |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "413" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_MULTI_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI         |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
