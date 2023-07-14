Feature: costo notifica con workflow analogico per persona giuridica 890

  @dev @costoAnalogico @costoCartAAR
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
      |CAP      | COSTO |
      | 00010   |  842  |
      | 12071   |  859  |
      | 62010   |  909  |
      | 06024   |  1026  |


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
      | 00010 | 0     |
      | 12071 | 0     |
      | 62010 | 0     |
      | 06024 | 0     |






  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PG_890_5] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
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
      |CAP      | COSTO |
      | 64010   |  887  |
      | 33028   |  890  |


  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_890_6] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo
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
      | 64010 | 0     |
      | 33028 | 0     |


  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PG_890_7] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI         |
      | digitalDomicile         | NULL       |
      | physicalAddress_zip     | <CAP>      |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      |CAP      | COSTO |
      | 64010   |  887  |
      | 33028   |  890  |


  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_PG_890_8] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Cucumber Analogic e:
      | payment_pagoPaForm      | SI         |
      | digitalDomicile         | NULL       |
      | physicalAddress_zip     | <CAP>      |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 64010 | 0     |
      | 33028 | 0     |