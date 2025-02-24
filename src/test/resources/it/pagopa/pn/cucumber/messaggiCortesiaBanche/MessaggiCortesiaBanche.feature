Feature: Messaggi di cortesia Banche

  Scenario Outline: Viene invocato l'endpoint EMD di sendMessage
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
      | 5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr    | RSSMRA85T10A562S        | Comune di Milano    | VEAJ-PTPD-NZDQ-202501-Y-1     |                   | 200         |

  Scenario Outline: Viene invocato l'endpoint EMD di /token/check-tpp
    When viene invocato l'endpoint tokenCheckTPP con retrievalId: "<retrievalId>"
    Then si ottiene status code <statusCode>
    Examples:
      | retrievalId                                           | statusCode  |
      | YTWY-GAWU-XAGD-202502-E-1_OK_00aaa-0-bbccc222dddee    | 200         |
      |                                                       | 400         |

  Scenario Outline: Viene invocato l'endpoint EMD di /emd/check-tpp
    When viene invocato l'endpoint emdCheckTPP con retrievalId: "<retrievalId>"
    Then si ottiene status code <statusCode>
    Examples:
      | retrievalId                                           | statusCode  |
      | YTWY-GAWU-XAGD-202502-E-1_OK_00aaa-0-bbccc222dddee    | 200         |
      |                                                       | 400         |

  Scenario Outline: Viene invocato l'endpoint EMD di /payment-url
    When viene invocato l'endpoint paymentUrl con i seguenti parametri
      | retrievalId    | noticeCode    | paTaxId    |
      | <retrievalId>  | <noticeCode>  | <paTaxId>  |
    Then si ottiene status code <statusCode>
    Examples:
      | retrievalId                                           | noticeCode              | paTaxId             | statusCode  |
      | YTWY-GAWU-XAGD-202502-E-1_OK_00aaa-0-bbccc222dddee    | 302000100000019421      | 77777777777         | 200         |
      |                                                       | 302000100000019421      | 77777777777         | 400         |
      | YTWY-GAWU-XAGD-202502-E-1_OK_00aaa-0-bbccc222dddee    |                         | 77777777777         | 400         |
      | YTWY-GAWU-XAGD-202502-E-1_OK_00aaa-0-bbccc222dddee    | 302000100000019421      |                     | 400         |