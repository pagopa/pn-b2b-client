Feature: Validazione campi per feature partitaIva


  Scenario Outline: [PARTITA-IVA_VALIDATION_1] Invio notifica 890 con controllo max e min campo vat
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | vat                | <iva>                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi REFUSED
    Examples:
      | iva |
      | -10 |
      | 101 |

  Scenario Outline: [PARTITA-IVA_VALIDATION_2] Invio notifica 890 con controllo max e min campo paFee
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | <paFee>                     |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi REFUSED
    Examples:
      | paFee |
      | -10   |
      | 101   |


  Scenario: [PARTITA-IVA_VALIDATION_3] Invio notifica 890 DELIVERY_MODE con vat a null controllo riccezione errore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | vat                | null                        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi REFUSED


  Scenario: [PARTITA-IVA_VALIDATION_4] Invio notifica 890 DELIVERY_MODE con paFee a null controllo riccezione errore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | null                        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi REFUSED