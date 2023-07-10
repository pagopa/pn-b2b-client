Feature: costo notifica con workflow analogico per multi destinatario RS

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"


  @dev @costoCart @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_1_AAR] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | <CAP>        |
      | payment_pagoPaForm      | NULL         |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO |
      | 01100 | 323   |
      | 00118 | 298   |
      | 12071 | 380   |

  @dev @costoCart @ignore
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | <CAP>        |
      | payment_pagoPaForm      | NULL         |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO |
      | 70122 | 262   |
      | 60011 | 327   |
      | 60122 | 381   |

  @dev @costoCart @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_3_AAR] Invio notifica verifica costo con FSU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | ZONE_1        |
      | physicalAddress_province     | Paris        |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_address      | Via@ok_RIS   |
      | payment_pagoPaForm           | NULL         |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "691" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1


  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_4] Invio notifica e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | ZONE_1       |
      | physicalAddress_province     | Paris        |
      | physicalAddress_address      | Via@ok_RIS   |
      | payment_pagoPaForm           | NULL         |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @costoCart @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_5_AAR] Invio notifica con allegato e verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm      | SI           |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | <CAP>        |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO |
      | 01100 | 323   |
      | 00118 | 298   |
      | 12071 | 380   |



  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RS_MULTI_6] Invio notifica con allegato e verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm      | SI           |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1


  @dev @costoCart @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_7_AAR] Invio notifica verifica con allegato e costo con FSU + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI           |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | ZONE_1          |
      | physicalAddress_province     | Paris        |
      | physicalAddress_address      | Via@ok_RIS   |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "691" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_8] Invio notifica con allegato e verifica costo con FSU + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI           |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | 75007        |
      | physicalAddress_province     | Paris        |
      | physicalAddress_address      | Via@ok_RIS   |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1



  @dev @costoCart @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_9_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | <CAP>          |
      | payment_pagoPaForm      | NULL         |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO |
      | 70122 | 262   |
      | 60011 | 327   |
      | 60122 | 381   |

  @dev @ignore
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_10] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
      | physicalAddress_zip     | <CAP>         |
      | payment_pagoPaForm      | NULL         |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO |
      | 70122 | 0     |
      | 60011 | 0     |
      | 60122 | 0     |


  @dev @costoCart @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_11_AAR] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 88010          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
      | payment_pagoPaForm           | NULL           |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "302" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_12] Invio notifica e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 88010          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @costoCart @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_13_AAR] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm      | SI           |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_zip     | <CAP>          |
      | physicalAddress_address | Via@ok_RS    |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO |
      | 70122 | 262   |
      | 60011 | 327   |
      | 60122 | 381   |

  @dev @ignore
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_14] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm      | SI           |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_zip     | <CAP>          |
      | physicalAddress_address | Via@ok_RS    |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO |
      | 70122 | 0     |
      | 60011 | 0     |
      | 60122 | 0     |

  @dev  @costoCart @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_15_AAR] Invio notifica verifica con e allegato costo con RECAPITISTA + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 88010          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "302" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_16] Invio notifica con allegato e verifica costo con RECAPITISTA + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 88010          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
