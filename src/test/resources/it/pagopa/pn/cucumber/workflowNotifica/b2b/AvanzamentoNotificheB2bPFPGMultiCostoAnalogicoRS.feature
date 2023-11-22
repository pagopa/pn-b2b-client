Feature: costo notifica con workflow analogico per multi destinatario RS

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"


  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_1] Invio notifica verifica costo con FSU + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_address      | Via@ok_RS      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE |
      | 60040 | 404   | COLLEPONI    | AN       |


  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_2] Invio notifica verifica costo con FSU + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_address      | Via@ok_RS      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO | MUNICIPALITY | PROVINCE |
      | 60040 | 0     | COLLEPONI    | AN       |

  @dev @costoAnalogico @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_3] Invio notifica verifica costo con ZONE_1 + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | ZONE_1       |
      | physicalAddress_province     | Paris        |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_address      | Via@ok_RIS   |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "742" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1


  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_4] Invio notifica e verifica costo con ZONE_1 + @OK_RIS + FLAT_RATE positivo
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
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1

  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_5] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_address      | Via@ok_RS      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE |
      | 00119 | 315   | ROMA           | RM       |
      | 60122 | 296   | ANCONA         | AN       |
      | 60012 | 346   | MONTERADO      | AN       |
      | 70122 | 275   | BARI           | BA       |
      | 03100 | 342   | FROSINONE      | FR       |
      | 00013 | 404   | CASTELCHIODATO | RM       |

  @dev @costoAnalogico
  Scenario Outline: [B2B_COSTO_ANALOG_RS_MULTI_6] Invio notifica e verifica costo con RECAPITISTA + @OK_RS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_address      | Via@ok_RS      |
      | physicalAddress_municipality | <MUNICIPALITY> |
      | physicalAddress_province     | <PROVINCE>     |
      | physicalAddress_zip          | <CAP>          |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "<COSTO>" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
    Examples:
      | CAP   | COSTO | MUNICIPALITY   | PROVINCE |
      | 00119 | 0     | ROMA           | RM       |
      | 60122 | 0     | ANCONA         | AN       |
      | 60012 | 0     | MONTERADO      | AN       |
      | 70122 | 0     | BARI           | BA       |
      | 03100 | 0     | FROSINONE      | FR       |
      | 00013 | 0     | CASTELCHIODATO | RM       |

  @dev @costoAnalogico @costoCartAAR
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_7] Invio notifica e verifica costo con ZONA_2 + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | ZONA_2         |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "858" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_8] Invio notifica e verifica costo con ZONA_2 + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it   |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | ZONA_2         |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIS     |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1


  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_9] Invio notifica e verifica costo con ZONA_3 + @OK_RIS + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | AUSTRALIA    |
      | physicalAddress_municipality | Hobart       |
      | physicalAddress_zip          | ZONA_3       |
      | physicalAddress_province     | Tasmania     |
      | physicalAddress_address      | Via@ok_RIS   |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "915" della notifica per l'utente 0
    And viene verificato il costo = "100" della notifica per l'utente 1

  @dev @costoAnalogico
  Scenario: [B2B_COSTO_ANALOG_RIS_MULTI_10] Invio notifica e verifica costo con ZONA_3 + @OK_RIS + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
      | feePolicy          | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_State        | AUSTRALIA    |
      | physicalAddress_municipality | Hobart       |
      | physicalAddress_zip          | ZONA_3       |
      | physicalAddress_province     | Tasmania     |
      | physicalAddress_address      | Via@ok_RIS   |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 0
    And viene verificato il costo = "0" della notifica per l'utente 1
