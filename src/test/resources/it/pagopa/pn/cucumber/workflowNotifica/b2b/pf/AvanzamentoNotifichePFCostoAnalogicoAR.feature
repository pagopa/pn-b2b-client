Feature: costo notifica con workflow analogico per persona fisica

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @dev @costoCart @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PF_AR_1] Invio notifica verifica costo con FSU + @OK_AR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_AR |
      |  physicalAddress_zip    |    <CAP>   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then viene verificato il costo = "<COSTO>" della notifica
    Examples:
      |CAP      | COSTO |
      | 18025   |  508  |
      | 00118   |  426  |
      | 01100   |  451  |



  @dev @costoCart @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PF_AR_2] Invio notifica verifica costo con FSU + @OK_AR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_AR |
      |  physicalAddress_zip    |    <CAP>   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      |CAP      | COSTO |
      | 00118   |  0  |
      | 01100   |  0  |
      | 18025   |  0  |


  @dev @costoCart @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PF_AR_3] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_AR |
      |  physicalAddress_zip    |    <CAP>   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then viene verificato il costo = "<COSTO>" della notifica
    Examples:
      |CAP      | COSTO |
      | 60121   |  381  |
      | 62010   |  421  |
      | 70121   |  351  |


  @dev @costoCart @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PF_AR_4] Invio notifica e verifica costo con RECAPITISTA + @OK_AR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_AR |
      |  physicalAddress_zip    |    <CAP>   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      |CAP      | COSTO |
      | 60121   |  0    |
      | 62010   |  0    |
      | 70121   |  0    |



  @dev @costoCart
  Scenario: [B2B_COSTO_ANALOG_PF_AR_5] Invio notifica e verifica costo con FSU + @OK_RIR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL           |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 88010          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIR     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then viene verificato il costo = "963" della notifica



  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PF_AR_6] Invio notifica con allegato e verifica costo con FSU + @OK_RIR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm           | SI             |
      | digitalDomicile              | NULL           |
      | physicalAddress_State        | BRASILE        |
      | physicalAddress_municipality | Florianopolis  |
      | physicalAddress_zip          | 88010          |
      | physicalAddress_province     | Santa Catarina |
      | physicalAddress_address      | Via@ok_RIR     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "0" della notifica


  @dev @costoCart
  Scenario: [B2B_COSTO_ANALOG_PF_AR_7] Invio notifica e verifica costo ZONA_1 + @OK_RIR + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL       |
      | physicalAddress_State        | FRANCIA    |
      | physicalAddress_municipality | Parigi     |
      | physicalAddress_zip          | 75007      |
      | physicalAddress_province     | Paris      |
      | physicalAddress_address      | Via@ok_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then viene verificato il costo = "856" della notifica

  @dev @ignore
  Scenario: [B2B_COSTO_ANALOG_PF_AR_8] Invio notifica e verifica costo ZONA_1 + @OK_RIR + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL       |
      | physicalAddress_State        | FRANCIA    |
      | physicalAddress_municipality | Parigi     |
      | physicalAddress_zip          | 75007      |
      | physicalAddress_province     | Paris      |
      | physicalAddress_address      | Via@ok_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "0" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "0" della notifica

