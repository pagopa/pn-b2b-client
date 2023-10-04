Feature: costo notifica con workflow analogico per persona giuridica RS

  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_RS_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_address      | Via@ok_RS      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE |
      | 60040 | 404   | COLLEPONI    | AN       |


  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_RS_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_address      | Via@ok_RS      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE |
      | 60040 | 0     | COLLEPONI    | AN       |

  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_3] Invio notifica verifica costo con ZONE_1 + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | ZONE_1       |
      | physicalAddress_province     | Paris        |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_address      | Via@ok_RIS   |
      | payment_pagoPaForm           | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "742" della notifica


  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_4] Invio notifica e verifica costo con ZONE_1 + @OK_RIS + FLAT_RATE positivo
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


  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_RS_5] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_address      | Via@ok_RS      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE |
      | 00119 | 315   | ROMA           | RM       |
      | 60122 | 296   | ANCONA         | AN       |
      | 60012 | 346   | MONTERADO      | AN       |
      | 70122 | 275   | BARI           | BA       |
      | 03100 | 342   | FROSINONE      | FR       |
      | 00013 | 404   | CASTELCHIODATO | RM       |


  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_RS_6] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_address      | Via@ok_RS      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
      | payment_pagoPaForm           | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE |
      | 00119 | 0     | ROMA           | RM       |
      | 60122 | 0     | ANCONA         | AN       |
      | 60012 | 0     | MONTERADO      | AN       |
      | 70122 | 0     | BARI           | BA       |
      | 03100 | 0     | FROSINONE      | FR       |
      | 00013 | 0     | CASTELCHIODATO | RM       |

  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_7] Invio notifica e verifica costo con ZONA_2 + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | ZONA_2         |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
      | payment_pagoPaForm           | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "858" della notifica

  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_8] Invio notifica e verifica costo con ZONA_2 + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | ZONA_2         |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_9] Invio notifica e verifica costo con ZONA_3 + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | AUSTRALIA    |
      | physicalAddress_municipality | Hobart       |
      | physicalAddress_zip          | ZONA_3       |
      | physicalAddress_province     | Tasmania     |
      | physicalAddress_address      | Via@ok_RIS   |
      | payment_pagoPaForm           | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "915" della notifica

  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_RIS_10] Invio notifica e verifica costo con ZONA_3 + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | AUSTRALIA    |
      | physicalAddress_municipality | Hobart       |
      | physicalAddress_zip          | ZONA_3       |
      | physicalAddress_province     | Tasmania     |
      | physicalAddress_address      | Via@ok_RIS   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo = "0" della notifica

