Feature: costo notifica con workflow analogico per persona giuridica

  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PG_1] Invio notifica e verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
      |  physicalAddress_zip    |    <CAP>   |
      | payment_pagoPaForm      | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 80060 | 544   |

  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_2] Invio notifica e verifica costo con FSU + @OK_AR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
      | physicalAddress_zip     | <CAP>     |
      | payment_pagoPaForm      | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 80060 | 0     |

  @dev @costoAnalogico @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_PG_3] Invio notifica e verifica costo con ZONA_2 + @OK_RIR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile              | NULL           |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | ZONA_2         |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIR     |
      | payment_pagoPaForm           | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1037" della notifica

  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_4] Invio notifica e verifica costo con ZONA_2 + @OK_RIR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile              | NULL           |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | ZONA_2          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIR     |
      | payment_pagoPaForm           | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "0" della notifica



  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PG_5] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
      | physicalAddress_zip     | <CAP>     |
      | payment_pagoPaForm      | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 00118 | 454   |
      | 00012 | 543   |
      | 60010 | 448   |
      | 60121 | 405   |
      | 70121 | 372   |
      | 80010 | 464   |
      | 80121 | 393   |
      | 81100 | 414   |
      | 04100 | 481   |


  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_6] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
      | physicalAddress_zip     | <CAP>       |
      | payment_pagoPaForm      | NULL      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 00118 | 0     |
      | 00012 | 0     |
      | 60010 | 0     |
      | 60121 | 0     |
      | 70121 | 0     |
      | 80010 | 0     |
      | 80121 | 0     |
      | 81100 | 0     |
      | 04100 | 0     |


  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_7] Invio notifica e verifica costo con ZONE_1 + @OK_RIR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile              | NULL       |
      | physicalAddress_State        | FRANCIA    |
      | physicalAddress_municipality | Parigi     |
      | physicalAddress_zip          | ZONE_1     |
      | physicalAddress_province     | Paris      |
      | physicalAddress_address      | Via@ok_RIR |
      | payment_pagoPaForm           | NULL       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "921" della notifica


  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_8] Invio notifica e verifica costo con ZONE_1 + @OK_RIR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile              | NULL       |
      | physicalAddress_State        | FRANCIA    |
      | physicalAddress_municipality | Parigi     |
      | physicalAddress_zip          | ZONE_1     |
      | physicalAddress_province     | Paris      |
      | physicalAddress_address      | Via@ok_RIR |
      | payment_pagoPaForm           | NULL       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "0" della notifica


  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_7] Invio notifica e verifica costo con ZONE_3 + @OK_RIR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile              | NULL       |
      | physicalAddress_State        | AUSTRALIA  |
      | physicalAddress_municipality | Hobart     |
      | physicalAddress_zip          | ZONE_3     |
      | physicalAddress_province     | Tasmania   |
      | physicalAddress_address      | Via@ok_RIR |
      | payment_pagoPaForm           | NULL       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "1095" della notifica

  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_PG_8] Invio notifica e verifica costo con ZONE_3 + @OK_RIR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile              | NULL       |
      | physicalAddress_State        | AUSTRALIA  |
      | physicalAddress_municipality | Hobart     |
      | physicalAddress_zip          | ZONE_3     |
      | physicalAddress_province     | Tasmania   |
      | physicalAddress_address      | Via@ok_RIR |
      | payment_pagoPaForm           | NULL       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "0" della notifica


