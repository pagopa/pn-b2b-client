Feature: costo notifica con workflow analogico per persona giuridica RS

  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PG_RS_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | <CAP>        |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 01100 | 323   |
      | 00118 | 298   |
      | 12071 | 380   |


  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PG_RS_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | <CAP>        |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 01100 | 0     |
      | 00118 | 0     |
      | 12071 | 0     |

  @dev @costoAnalogico @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_3] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | ZONE_1        |
      | physicalAddress_province     | Paris        |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_address      | Via@ok_RIS   |
      | payment_pagoPaForm           | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "691" della notifica


  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | ZONE_1       |
      | physicalAddress_province     | Paris        |
      | physicalAddress_address      | Via@ok_RIS   |
      | payment_pagoPaForm           | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica


  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PG_RS_5] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | <CAP>          |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 70122 | 262   |
      | 60011 | 327   |
      | 60122 | 281   |

  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_RS_6] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | <CAP>          |
      | payment_pagoPaForm      | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 70122 | 0     |
      | 60011 | 0     |
      | 60122 | 0     |

  @dev @costoAnalogico @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_7] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 60012          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
      | payment_pagoPaForm           | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "798" della notifica

  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_8] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 70124            |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

