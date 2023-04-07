Feature: costo notifica con workflow analogico per persona giuridica RS

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_1] Invio notifica e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "233" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_2] Invio notifica e verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_3] Invio notifica e verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "224" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI        |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "355" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI        |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_7] Invio notifica verifica con e allegato costo con FSU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "325" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
      | physicalAddress_zip     | 39100     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "212" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
      | physicalAddress_zip     | 39100     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "302" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI        |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_zip     | 39100     |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "313" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI        |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_zip     | 39100     |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm           | SI         |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "413" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm           | SI         |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | Brasile        |
      | physicalAddress_municipality | Florianópolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

