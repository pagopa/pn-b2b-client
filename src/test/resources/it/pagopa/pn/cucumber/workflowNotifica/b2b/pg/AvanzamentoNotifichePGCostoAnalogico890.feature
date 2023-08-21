Feature: costo notifica con workflow analogico per persona giuridica 890

  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_890_1] Invio notifica e verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | physicalAddress_zip     | <CAP>      |
      | payment_pagoPaForm      | NULL       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 05010 | 1105  |


  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_890_2] Invio notifica e verifica costo con FSU + @OK_890 + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | physicalAddress_zip     | <CAP>      |
      | payment_pagoPaForm      | NULL       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 05010 | 0     |


  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_890_3] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | physicalAddress_zip     | <CAP>      |
      | payment_pagoPaForm      | NULL       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 70010 | 861   |
      | 00010 | 906   |
      | 60010 | 979   |
      | 64010 | 954   |
      | 06031 | 957   |


  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_890_4] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | physicalAddress_zip     | <CAP>      |
      | payment_pagoPaForm      | NULL       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 70010 | 0     |
      | 00010 | 0     |
      | 60010 | 0     |
      | 64010 | 0     |
      | 06031 | 0     |
