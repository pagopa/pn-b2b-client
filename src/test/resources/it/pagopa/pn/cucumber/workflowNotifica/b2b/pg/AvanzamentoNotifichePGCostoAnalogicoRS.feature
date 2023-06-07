Feature: costo notifica con workflow analogico per persona giuridica RS

  @dev @costoCart
  Scenario: [B2B_COSTO_ANALOG_PG_RS_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "233" della notifica

  @dev @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RS_1_AAR] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "233" della notifica

  @dev @costoCart
  Scenario: [B2B_COSTO_ANALOG_PG_RS_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RS_2_AAR] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @costoCart
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | 75007        |
      | physicalAddress_province     | Paris        |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_address      | Via@ok_RIS   |
      | payment_pagoPaForm           | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "223" della notifica

  @dev @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_3_AAR] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | 75007        |
      | physicalAddress_province     | Paris        |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_address      | Via@ok_RIS   |
      | payment_pagoPaForm           | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "223" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | 75007        |
      | physicalAddress_province     | Paris        |
      | physicalAddress_address      | Via@ok_RIS   |
      | payment_pagoPaForm           | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @costoCart
  Scenario: [B2B_COSTO_ANALOG_PG_RS_5] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI           |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "233" della notifica

  @dev @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RS_5_AAR] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI           |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "233" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI           |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_7_AAR] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm           | SI           |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | 75007        |
      | physicalAddress_province     | Paris        |
      | physicalAddress_address      | Via@ok_RIS   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "223" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm           | SI           |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | 75007        |
      | physicalAddress_province     | Paris        |
      | physicalAddress_address      | Via@ok_RIS   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @costoCart
  Scenario: [B2B_COSTO_ANALOG_PG_RS_9] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | 39100        |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "212" della notifica

  @dev @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RS_9_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | 39100        |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "212" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | 39100        |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @costoCart
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_11] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
      | payment_pagoPaForm           | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "302" della notifica

  @dev @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_11_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
      | payment_pagoPaForm           | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "302" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @costoCart
  Scenario: [B2B_COSTO_ANALOG_PG_RS_13] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI           |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_zip     | 39100        |
      | physicalAddress_address | Via@ok_RS    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "212" della notifica

  @dev @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RS_13_AAR] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI           |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_zip     | 39100        |
      | physicalAddress_address | Via@ok_RS    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "212" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RS_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI           |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_zip     | 39100        |
      | physicalAddress_address | Via@ok_RS    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @costoCart
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_15] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "302" della notifica

  @dev @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_15_AAR] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "100" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "302" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 75007          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica