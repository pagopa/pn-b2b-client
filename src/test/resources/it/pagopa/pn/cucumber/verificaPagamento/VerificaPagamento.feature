Feature: Verifica pagamento PagoPA

  @verificaPagamento
  Scenario Outline:  [PAYMENT-VERIFY_1] Check API verifica pagamento
    Given viene generata una nuova notifica di pagamento
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | feePolicy             | FLAT_RATE                   |
      | pagoPaIntMode         | SYNC                        |
      | taxonomyCode          | 010202N                     |
    And con destinatario Mario Gherkin e:
      | payment_creditorTaxId | <CREDITOR_TAX_ID>         |
      | payment_noticeCode    | <SUFFIX_NOTICE_CODE>      |
      | digitalDomicile       | testpagopa1@pec.pagopa.it |
    When la notifica viene inviata dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then il servizio di checkout restituisce:
      | status   | <STATUS>           |
      | detail   | <STATUS_DETAIL>    |
      | detailV2 | <STATUS_DETAIL_V2> |
    Examples:
      | SUFFIX_NOTICE_CODE | STATUS_DETAIL   | CREDITOR_TAX_ID | STATUS_DETAIL_V2      | STATUS  |
      | 30299              | PAYMENT_EXPIRED | 77777777777     | PAA_PAGAMENTO_SCADUTO | FAILURE |
      | 30244              | GENERIC_ERROR   | 77777777777     | Checkout not found    | FAILURE |
      | 21111672374967     | GENERIC_ERROR   | 77777777777     | Checkout not found    | FAILURE |
      | 30201              | GENERIC_ERROR   | 77777777771     | Checkout not found    | FAILURE |
