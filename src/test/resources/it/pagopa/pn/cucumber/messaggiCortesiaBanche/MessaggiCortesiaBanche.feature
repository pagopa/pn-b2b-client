Feature: Messaggi di cortesia Banche

  @bankCourtesyMessageEnabled
  Scenario Outline: [BANK_COURTESY_MESSAGE-1] Viene invocato l'endpoint EMD di sendMessage
    When viene invocato l'endpoint sendMessage con i seguenti parametri
    | internalRecipientId    | recipientId    | senderDescription    | originId     | associatedPayment    |
    | <internalRecipientId>  | <recipientId>  | <senderDescription>  | <originId>   | <associatedPayment>  |
    Then si ottiene status code <statusCode>
    Examples:
      | internalRecipientId                     | recipientId             | senderDescription   | originId                      | associatedPayment | statusCode  |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr    | RSSMRA85T10A562S        | Comune di Milano    | VEAJ-PTPD-NZDQ-202501-Y-1     | true              | 200         |
      |                                         | RSSMRA85T10A562S        | Comune di Milano    | VEAJ-PTPD-NZDQ-202501-Y-1     | true              | 400         |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr    |                         | Comune di Milano    | VEAJ-PTPD-NZDQ-202501-Y-1     | true              | 400         |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr    | RSSMRA85T10A562S        |                     | VEAJ-PTPD-NZDQ-202501-Y-1     | true              | 400         |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr    | RSSMRA85T10A562S        | Comune di Milano    |                               | true              | 400         |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr    | RSSMRA85T10A562S        | Comune di Milano    | VEAJ-PTPD-NZDQ-202501-Y-1     |                   | 400         |

  @bankCourtesyMessageEnabled
  Scenario Outline: [BANK_COURTESY_MESSAGE-2] Viene invocato l'endpoint EMD di /token/check-tpp
    When viene invocato l'endpoint tokenCheckTPP con retrievalId: "<retrievalId>"
    Then si ottiene status code <statusCode>
    Examples:
      | retrievalId                                           | statusCode  |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa    | 200         |
      |                                                       | 400         |

  @bankCourtesyMessageEnabled
  Scenario Outline: [BANK_COURTESY_MESSAGE-3] Viene invocato l'endpoint EMD di /emd/check-tpp
    When viene invocato l'endpoint emdCheckTPP con retrievalId: "<retrievalId>"
    Then si ottiene status code <statusCode>
    Examples:
      | retrievalId                                           | statusCode  |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa    | 200         |
      |                                                       | 400         |

  @bankCourtesyMessageEnabled
  Scenario Outline: [BANK_COURTESY_MESSAGE-4] Viene invocato l'endpoint EMD di /payment-url
    When viene invocato l'endpoint paymentUrl con i seguenti parametri
      | retrievalId |  <retrievalId> |
      | noticeCode  |  <noticeCode>  |
      | paTaxId     |  <paTaxId>     |
    Then si ottiene status code <statusCode>
    Examples:
      | retrievalId                                           | noticeCode              | paTaxId             | statusCode  |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa    | 302000100000019421      | 77777777777         | 200         |
      |                                                       | 302000100000019421      | 77777777777         | 400         |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa    |                         | 77777777777         | 400         |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa    | 302000100000019421      |                     | 400         |

  #  Scenario to validate calls to the following endpoints:
  #     GET /bff/v1/notifications/received/{iun}
  #     GET /bff/v1/notifications/received/{iun}/documents/{documentType}
  #     GET /bff/v1/notifications/received/{iun}/payments/{attachmentName}
  @bankCourtesyMessageEnabled
  Scenario: [BANK_COURTESY_MESSAGE-5] Vengono invocati gli endpoint di recupero notifica, lettura AAR e recupero avviso di pagamento da API web (BFF)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890     |
      | taxId                   | CLMCST42R12D969Z            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-CompiutaGiacenza_890 |
      | payment_pagoPaForm      | SI                          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    And lato destinatario viene recuperato AAR lato web dal destinatario "Mario Gherkin"
    And lato destinatario è possibile recuperare correttamente l'allegato "PAGOPA" dal destinatario "Mario Gherkin"

# =====================================================
#  Gruppo di tests in caso di feature flag disattivato
# =====================================================
  @bankCourtesyMessageDisabled
  Scenario: [BANK_COURTESY_MESSAGE-6] Viene invocato l'endpoint EMD di sendMessage quando la funzionalità è disattiva
    When viene invocato l'endpoint sendMessage con i seguenti parametri
      | internalRecipientId                     | recipientId             | senderDescription   | originId                      | associatedPayment |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr    | RSSMRA85T10A562S        | Comune di Milano    | VEAJ-PTPD-NZDQ-202501-Y-1     | true              |
    Then si ottiene status code 200
    And la risposta contiene "NO_CHANNELS_ENABLED"

  @bankCourtesyMessageDisabled
  Scenario: [BANK_COURTESY_MESSAGE-2] Viene invocato l'endpoint EMD di /token/check-tpp quando la funzionalità è disattiva
    When viene invocato l'endpoint tokenCheckTPP con retrievalId: "YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa"
    Then si ottiene status code 404

  @bankCourtesyMessageDisabled
  Scenario: [BANK_COURTESY_MESSAGE-3] Viene invocato l'endpoint EMD di /emd/check-tpp quando la funzionalità è disattiva
    When viene invocato l'endpoint emdCheckTPP con retrievalId: "YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa"
    Then si ottiene status code 404

  @bankCourtesyMessageDisabled
  Scenario: [BANK_COURTESY_MESSAGE-4] Viene invocato l'endpoint EMD di /payment-url quando la funzionalità è disattiva
    When viene invocato l'endpoint paymentUrl con i seguenti parametri
      | retrievalId |  YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa |
      | noticeCode  |  302000100000019421   |
      | paTaxId     |  77777777777          |
    Then si ottiene status code 500
