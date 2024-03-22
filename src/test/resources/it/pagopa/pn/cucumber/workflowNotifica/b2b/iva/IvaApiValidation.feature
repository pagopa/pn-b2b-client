Feature: Validazione campi per feature partitaIva

  @partitaIva
  Scenario Outline: [PARTITA-IVA_VALIDATION_1] Invio notifica 890 DELIVERY_MODE SYNC campi obligatori a null controllo riccezione errore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | vat                | <vat>                       |
      | paFee              | <paFee>                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
Examples:
  | vat  | paFee |
  | NULL | 10    |
  | 10   | NULL  |


  @partitaIva
  Scenario Outline: [PARTITA-IVA_VALIDATION_2] Invio notifica 890 DELIVERY_MODE con vat a null controllo riccezione errore
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | vat                | <vat>                       |
      | paFee              | <paFee>                     |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | via@ok_890  |
      | payment_creditorTaxId   | 77777777777 |
      | payment_pagoPaForm      | SI          |
      | payment_f24             | NULL        |
      | apply_cost_pagopa       | SI          |
      | payment_multy_number    | 1           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Then viene cancellata la posizione debitoria di "Mario Gherkin"
    Examples:
      | vat  | paFee |
      | NULL | 10    |
      | 10   | NULL  |

  @partitaIva
  Scenario Outline: [PARTITA-IVA_VALIDATION_3] Invio notifica 890 SYNC con 1 F24 con vat a null controllo riccezione errore
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | <vat>                       |
      | paFee                 | <paFee>                     |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via@ok_890                    |
      | payment_f24             | PAYMENT_F24_STANDARD          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | vat  | paFee |
      | NULL | 10    |
      | 10   | NULL  |

  @partitaIva
  Scenario Outline: [PARTITA-IVA_VALIDATION_4] Invio notifica 890 DELIVERY_MODE SYNC con controllo max e min campo vat
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | vat                | <iva>                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | iva |
      | -10 |
      | 101 |

  @partitaIva
  Scenario Outline: [PARTITA-IVA_VALIDATION_5] Invio notifica 890 DELIVERY_MODE SYNC con controllo max e min campo paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | <paFee>                     |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | paFee |
      | -10   |
      | 101   |
