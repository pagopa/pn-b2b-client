Feature: comunicazioni bonarie

  Scenario: [PREPARE-TC1-OK] Validazione nuovo service api prepare con tutti i parametri mandatori
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                            | receiverType | printType       | attachmentUrls               | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1001 | PF           | BN_FRONTE_RETRO | https://TestServer/allegato1 | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC2A-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls lista empty*
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls          | requestId                             |
      | [EMPTY]                 | ABCD-HILM-YKWX-202202-1_rec0_try1002A |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC2B-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls popolata con almeno un url a empty*
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls                       | requestId                             |
      | [EMPTY],https://TestServer/allegato1 | ABCD-HILM-YKWX-202202-1_rec0_try1002B |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC2C-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls popolata con almeno un url a blank*
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls                            | requestId                             |
      | [SOLO_SPAZI],https://TestServer/allegato1 | ABCD-HILM-YKWX-202202-1_rec0_try1002C |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC3A-KO] Validazione nuovo service api prepare con parametro (required) printType a empty*
    Given inizializzata una comunicazione bonaria con i parametri:
      | printType | requestId                             |
      | [EMPTY]   | ABCD-HILM-YKWX-202202-1_rec0_try1003A |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC3B-KO] Validazione nuovo service api prepare con parametro (required) printType a blank*
    Given inizializzata una comunicazione bonaria con i parametri:
      | printType    | requestId                             |
      | [SOLO_SPAZI] | ABCD-HILM-YKWX-202202-1_rec0_try1003B |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC4A-KO] Validazione nuovo service api prepare con parametro (required) receiverType empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                             | receiverType |
      | ABCD-HILM-YKWX-202202-1_rec0_try1004A | [EMPTY]      |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC4B-KO] Validazione nuovo service api prepare con parametro (required) receiverType blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                             | receiverType |
      | ABCD-HILM-YKWX-202202-1_rec0_try1004B | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC5A-KO] Validazione nuovo service api prepare con parametro (required) iun a empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                             | iun     |
      | ABCD-HILM-YKWX-202202-1_rec0_try1005A | [EMPTY] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC5B-KO] Validazione nuovo service api prepare con parametro (required) iun a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                             | iun |
      | ABCD-HILM-YKWX-202202-1_rec0_try1005B | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC6A-KO] Validazione nuovo service api prepare con parametro (required) requestId a empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId |
      | [EMPTY]   |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 500

  Scenario: [PREPARE-TC6B-KO] Validazione nuovo service api prepare con parametro (required) requestId a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId    |
      | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC7A-KO] Validazione nuovo service api prepare con parametro (required) proposalProductType a empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                               | proposalProductType |
      | ABCD-HILM-YKWX-202202-1_rec0_try1007A   | [EMPTY]             |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 500

  Scenario: [PREPARE-TC7B-KO] Validazione nuovo service api prepare con parametro (required) proposalProductType a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                             | proposalProductType |
      | ABCD-HILM-YKWX-202202-1_rec0_try1007B | [SOLO_SPAZI]        |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 500

  Scenario: [PREPARE-TC7-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls null
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | attachmentUrls |
      | ABCD-HILM-YKWX-202202-1_rec0_try1007 | [null]         |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC8-KO] Validazione nuovo service api prepare con parametro (required) printType null
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | printType |
      | ABCD-HILM-YKWX-202202-1_rec0_try1008 | [null]    |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC9-KO] Validazione nuovo service api prepare con parametro (required) receiverType null
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | receiverType |
      | ABCD-HILM-YKWX-202202-1_rec0_try1009 | [null]       |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC10-KO] Validazione nuovo service api prepare con parametro (required) iun null
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                             | receiverType |
      | ABCD-HILM-YKWX-202202-1_rec0_try10010 | [null]       |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC11-KO] Validazione nuovo service api prepare con parametro (required) requestId null
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId |
      | [null]    |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC11B-KO] Validazione nuovo service api prepare con parametro (required) proposalProductType null
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                              | proposalProductType |
      | ABCD-HILM-YKWX-202202-1_rec0_try10011B | [null]              |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400



  Scenario: [PREPARE-TC12-KO] Validazione nuovo service api prepare con requestId esistente (già preso in carico)
  ma il corpo della richiesta presenta dati diversi
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                             | printType |
      | ABCD-HILM-YKWX-202202-1_rec0_try1001  | BN_FRONTE |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 409

  Scenario: [PREPARE-TC13-OK] Validazione nuovo service api prepare per richiesta già elaborata in precedenza (caso idempotenza)
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            |
      | ABCD-HILM-YKWX-202202-1_rec0_try1001 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 200


  Scenario: [PREPARE-TC15-KO] Validazione nuovo service api prepare con valore enum non valido nel parametro proposalProductType
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | proposalProductType |
      | ABCD-HILM-YKWX-202202-1_rec0_try1015 | AR                  |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 500


  Scenario: [PREPARE-TC16-KO] Validazione nuovo service api prepare con formato data invalido nel parametro notificationSentAt
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | notificationSentAt          |
      | ABCD-HILM-YKWX-202202-1_rec0_try1016 | 2022-07----27T12:22:33.444Z |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 500

  Scenario: [PREPARE-TC18-OK] Validazione nuovo service api prepare con molteplici url nel parametro attachmentUrls
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | attachmentUrls                                             |
      | ABCD-HILM-YKWX-202202-1_rec0_try1018 | https://TestServer/allegato1,https://TestServer/allegato2  |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC19-OK] Validazione nuovo service api prepare con valore non censito nel parametro printType
    Given inizializzata una comunicazione bonaria con i parametri:
      | printType | requestId                            |
      | AR        | ABCD-HILM-YKWX-202202-1_rec0_try1019 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 400

  Scenario: [PREPARE-TC20-OK] Validazione nuovo service api prepare con valore non censito nel parametro receiverType
    Given inizializzata una comunicazione bonaria con i parametri:
      | receiverType | requestId                            |
      | AR           | ABCD-HILM-YKWX-202202-1_rec0_try1020 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 400

  Scenario: [PREPARE-TC21-OK] Validazione nuovo service api prepare con requestId che non contiene il prefisso dello iun fornito
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                            |
      | ABCD-HILM-YKWX-202202-1 | UVXZ-HILM-YKWX-202202-1_rec0_try1021 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 400

  Scenario: [PREPARE-TC22-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.fullname mancante
    Given inizializzata una comunicazione bonaria con i parametri:
      | address                         | city   | requestId                            |
      | Via Roma                        | Milano | UVXZ-HILM-YKWX-202202-1_rec0_try1022 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC23-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.address mancante
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname                         | city   | requestId                            |
      | Mario Rossi                      | Milano | UVXZ-HILM-YKWX-202202-1_rec0_try1023 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC24-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.city mancante
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname                         | address   | requestId                            |
      | Mario Rossi                      | Via Roma  | UVXZ-HILM-YKWX-202202-1_rec0_try1024 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC22B-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.fullname empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | address                         | city   | requestId                             | fullname |
      | Via Roma                        | Milano | UVXZ-HILM-YKWX-202202-1_rec0_try1022B | [EMPTY]  |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC23B-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.address empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname                         | city   | requestId                             | address |
      | Mario Rossi                      | Milano | UVXZ-HILM-YKWX-202202-1_rec0_try1023B | [EMPTY] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC24B-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.city empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname                         | address   | requestId                             | city    |
      | Mario Rossi                      | Via Roma  | UVXZ-HILM-YKWX-202202-1_rec0_try1024B | [EMPTY] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC22C-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.fullname blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | address                         | city   | requestId                             | fullname |
      | Via Roma                        | Milano | UVXZ-HILM-YKWX-202202-1_rec0_try1022C | [EMPTY]  |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC23C-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.address blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname                         | city   | requestId                             | address  |
      | Mario Rossi                      | Milano | UVXZ-HILM-YKWX-202202-1_rec0_try1023C | [EMPTY]  |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC24C-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.city blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname                         | address   | requestId                             | city    |
      | Mario Rossi                      | Via Roma  | UVXZ-HILM-YKWX-202202-1_rec0_try1024C | [EMPTY] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC25A-KO] Validazione nuovo service api prepare con header X-Client-Id empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | xClientId |
      | UVXZ-HILM-YKWX-202202-1_rec0_try1025A | [EMPTY]  |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC25B-KO] Validazione nuovo service api prepare con header X-Client-Id blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                             | xClientId    |
      | UVXZ-HILM-YKWX-202202-1_rec0_try1025B | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC26-KO] Validazione nuovo service api prepare con length header X-Client-Id > 64
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | xClientId                                                                 |
      | UVXZ-HILM-YKWX-202202-1_rec0_try1026 | questoclientidèdavverotroppolungoenonrispettalalunghezzamassimaconsentita |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC27-OK] Validazione nuovo service api prepare cambiando valore header X-Client-Id e stesso corpo della richiesta
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | xClientId               |
      | UVXZ-HILM-YKWX-202202-1_rec0_try1027 | questoclientidècambiato |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC28-OK] Validazione nuovo service api prepare con length header X-Client-Id uguale a 1
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | xClientId |
      | UVXZ-HILM-YKWX-202202-1_rec0_try1028 | x         |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201



