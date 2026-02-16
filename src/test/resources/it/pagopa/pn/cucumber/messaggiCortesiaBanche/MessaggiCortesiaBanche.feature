Feature: Messaggi di cortesia Banche

  @bankCourtesyMessageEnabled
  Scenario Outline: [BANK_COURTESY_MESSAGE-1] Viene invocato l'endpoint EMD di sendMessage
    When viene invocato l'endpoint sendMessage con i seguenti parametri
      | internalRecipientId   | recipientId   | senderDescription   | originId   | associatedPayment   | deliveryMode   | schedulingAnalogDate   |
      | <internalRecipientId> | <recipientId> | <senderDescription> | <originId> | <associatedPayment> | <deliveryMode> | <schedulingAnalogDate> |
    Then si ottiene status code <statusCode>
    Examples:
      | internalRecipientId                                 | recipientId      | senderDescription | originId                                           | associatedPayment | deliveryMode | schedulingAnalogDate | statusCode |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      |                                                     | RSSMRA85T10A562S | Comune di Milano  | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr                |                  | Comune di Milano  | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr                | RSSMRA85T10A562S |                   | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr                | RSSMRA85T10A562S | Comune di Milano  |                                                    | true              | ANALOG       | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr                | RSSMRA85T10A562S | Comune di Milano  | VEAJ-PTPD-NZDQ-202501-Y-1                          |                   | ANALOG       | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr                | RSSMRA85T10A562S | Comune di Milano  | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              |              | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr                | RSSMRA85T10A562S | Comune di Milano  | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       |                      | 400        |
      | 5                                                   | RSSMRA85T10A562S | Comune di Bolzano | V                                                  | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr5b334d4a-0gt7-2 | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1VEAJ-PTPD-NZDQ-202501-Y-1 | true              | ANALOG       | today                | 500        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5t3                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      |                                                     | RSSMRA85T10A562S | Comune di Bolzano |                                                    | true              | ANALOG       | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5t5                | RSSMRA85T10A562S | C                 | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5t6                | RSSMRA85T10A562S | TEXT_UTF8         | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5t7                | RSSMRA85T10A562S |                   | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5t8                | RSSMRA85T10A562S | TEXT_250          | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5t9                | RSSMRA85T10A562S | TEXT_251          | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 500        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr10                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr11                | RSSMRA85T10A562S | Comune di Bolzano | V                                                  | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr12                | RSSMRA85T10A562S | Comune di Bolzano | TEXT_100                                           | true              | ANALOG       | today                | 500        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr13                | RSSMRA85T10A562S | Comune di Bolzano | TEXT_101                                           | true              | ANALOG       | today                | 500        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr14                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr15                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr16                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr17                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          |                   | ANALOG       | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr18                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr19                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | DIGITAL      | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr20                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              |              | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr21                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr22                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | DIGITAL      | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr23                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | DIGITAL      |                      | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr24                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       |                      | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr25                | R                | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr26                |                  | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 400        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr27                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr28                | TEXT_101         | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 500        |
      | 5b334d4a-   0gt7-24ac-9c7b-354e2d44wr29             | RSSMRA    A562S  | Comune di Bolzano | VEAJ-PTPD-NZ   DQ-202501-Y-1                       | true              | ANALOG       | today                | 200        |
      | 5b33##a-0gt7-24ac-9c7b-354e2d44wr30                 | RSSMRA#0A562S    | Comune di Bolzano | VEAJ-P#PD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |
      | 5                                                   | RSSMRA85T10A562S | Comune di Bolzano | TEXT_100                                           | true              | ANALOG       | today                | 500        |
      | 1                                                   | RSSMRA85T10A562S | Comune di Bolzano | TEXT_98                                            | true              | ANALOG       | today                | 200        |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44wr30                | RSSMRA85T10A562S | Comune di Bolzano | VEAJ-PTPD-NZDQ-202501-Y-1                          | true              | ANALOG       | today                | 200        |


  @bankCourtesyMessageEnabled
  Scenario Outline: [BANK_COURTESY_MESSAGE-2] Viene invocato l'endpoint EMD di /token/check-tpp
    When viene invocato l'endpoint tokenCheckTPP con retrievalId: "<retrievalId>"
    Then si ottiene status code <statusCode>
    Examples:
      | retrievalId                                        | statusCode |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa | 200        |
      |                                                    | 400        |

  @bankCourtesyMessageEnabled
  Scenario Outline: [BANK_COURTESY_MESSAGE-3] Viene invocato l'endpoint EMD di /emd/check-tpp
    When viene invocato l'endpoint emdCheckTPP con retrievalId: "<retrievalId>"
    Then si ottiene status code <statusCode>
    Examples:
      | retrievalId                                        | statusCode |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa | 200        |
      |                                                    | 400        |

  @bankCourtesyMessageEnabled
  Scenario Outline: [BANK_COURTESY_MESSAGE-4] Viene invocato l'endpoint EMD di /payment-url
    When viene invocato l'endpoint paymentUrl con i seguenti parametri
      | retrievalId | <retrievalId> |
      | noticeCode  | <noticeCode>  |
      | paTaxId     | <paTaxId>     |
      | amount      | <amount>      |
    Then si ottiene status code <statusCode>
    Examples:
      | retrievalId                                        | noticeCode         | paTaxId     | amount | statusCode |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa | 302000100000019421 | 77777777777 | 10     | 200        |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa | 302000100000019421 | 77777777777 |        | 200        |
      |                                                    | 302000100000019421 | 77777777777 | 10     | 400        |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa |                    | 77777777777 | 10     | 400        |
      | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa | 302000100000019421 |             | 10     | 400        |

  #  Scenario to validate calls to the following endpoints:
  #     GET /bff/v1/notifications/received/{iun}
  #     GET /bff/v1/notifications/received/{iun}/documents/{documentType}
  #     GET /bff/v1/notifications/received/{iun}/payments/{attachmentName}
  @bankCourtesyMessageEnabled
  Scenario: [BANK_COURTESY_MESSAGE-5] Vengono invocati gli endpoint di recupero notifica, lettura AAR e recupero avviso di pagamento da API web (BFF)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-CompiutaGiacenza_890 |
      | payment_pagoPaForm      | SI                          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                          |
      | details_recIndex       | 0                                 |
      | details_digitalAddress | {"type": "TPP", "address": "APP"} |
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    And lato destinatario viene recuperato AAR lato web dal destinatario "Mario Gherkin"
    And lato destinatario è possibile recuperare correttamente l'allegato "PAGOPA" dal destinatario "Mario Gherkin"

# =====================================================
#  Gruppo di tests in caso di feature flag disattivato
# =====================================================
  @bankCourtesyMessageDisabled
  Scenario: [BANK_COURTESY_MESSAGE-6] Viene invocato l'endpoint EMD di sendMessage quando la funzionalità è disattiva
    When viene invocato l'endpoint sendMessage con i seguenti parametri
      | internalRecipientId                  | recipientId      | senderDescription | originId                  | associatedPayment | deliveryMode |
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr | RSSMRA85T10A562S | Comune di Milano  | VEAJ-PTPD-NZDQ-202501-Y-1 | true              | DIGITAL      |
    Then si ottiene status code 200
    And la risposta contiene outcome uguale a "NO_CHANNELS_ENABLED"

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
      | retrievalId | YTWY-GAWU-XAGD-202502-E-1~OK~13212-abvee1-3332-aaa |
      | noticeCode  | 302000100000019421                                 |
      | paTaxId     | 77777777777                                        |
    Then si ottiene status code 500
