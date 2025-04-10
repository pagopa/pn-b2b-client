Feature: Verifica pagamento PagoPA

  @verificaPagamento
  Scenario: [PAYMENT-VERIFY_1] Check API verifica pagamento - CODICE AVVISO SCADUTO 30299
    Given viene generata una nuova notifica di pagamento
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | feePolicy             | FLAT_RATE                   |
      | pagoPaIntMode         | SYNC                        |
      | taxonomyCode          | 010202N                     |
    And con destinatario Mario Gherkin e:
      | payment_creditorTaxId | 77777777777                 |
      | payment_noticeCode    | 30299                       |
      | digitalDomicile       | testpagopa1@pec.pagopa.it   |
    When la notifica viene inviata dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then il servizio di checkout restituisce:
      | status   | FAILURE               |
      | detail   | PAYMENT_EXPIRED       |
      | detailV2 | PAA_PAGAMENTO_SCADUTO |

  @verificaPagamento
  Scenario: [PAYMENT-VERIFY_2] Check API verifica pagamento - CODICE AVVISO UNKNOW 30244
    Given viene generata una nuova notifica di pagamento
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | feePolicy             | FLAT_RATE                   |
      | pagoPaIntMode         | SYNC                        |
      | taxonomyCode          | 010202N                     |
    And con destinatario Mario Gherkin e:
      | payment_creditorTaxId | 77777777777                 |
      | payment_noticeCode    | 30244                       |
      | digitalDomicile       | testpagopa1@pec.pagopa.it   |
    When la notifica viene inviata dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then il servizio di checkout restituisce:
      | status   | FAILURE            |
      | detail   | GENERIC_ERROR      |
      | detailV2 | Checkout not found |


  @verificaPagamento
  Scenario: [PAYMENT-VERIFY_3] Check API verifica pagamento - CODICE AVVISO NON VALIDO 21111672374967
    Given viene generata una nuova notifica di pagamento
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | feePolicy             | FLAT_RATE                   |
      | pagoPaIntMode         | SYNC                        |
      | taxonomyCode          | 010202N                     |
    And con destinatario Mario Gherkin e:
      | payment_creditorTaxId | 77777777777                 |
      | payment_noticeCode    | 21111672374967              |
      | digitalDomicile       | testpagopa1@pec.pagopa.it   |
    When la notifica viene inviata dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then il servizio di checkout restituisce:
      | status   | FAILURE            |
      | detail   | GENERIC_ERROR      |
      | detailV2 | Checkout not found |

  @verificaPagamento
  Scenario: [PAYMENT-VERIFY_4] Check API verifica pagamento - TAXID NON VALIDO CODICE AVVISO VALIDO 30201
    Given viene generata una nuova notifica di pagamento
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | feePolicy             | FLAT_RATE                   |
      | pagoPaIntMode         | SYNC                        |
      | taxonomyCode          | 010202N                     |
    And con destinatario Mario Gherkin e:
      | payment_creditorTaxId | 77777777771                 |
      | payment_noticeCode    | 30201                       |
      | digitalDomicile       | testpagopa1@pec.pagopa.it   |
    When la notifica viene inviata dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then il servizio di checkout restituisce:
      | status   | FAILURE            |
      | detail   | GENERIC_ERROR      |
      | detailV2 | Checkout not found |


