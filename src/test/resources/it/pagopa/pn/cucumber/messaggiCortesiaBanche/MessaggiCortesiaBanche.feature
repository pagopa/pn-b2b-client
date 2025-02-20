Feature: Messaggi di cortesia Banche

  Scenario: Viene invocato l'endpoint EMD di sendMessage
    Given viene settato il token
    When viene invocato l'endpoint con i seguenti parametri
    | internalRecipientId                     | recipientId             | senderDescription   | originId                      | associatedPayment |
    | "5b334d4a-0gt7-24ac-9c7b-354e2d44w5tr"  | "RSSMRA85T10A562S"      | "Comune di Milano"  | "VEAJ-PTPD-NZDQ-202501-Y-1"   | true              |

    Then si ottiene status code 200