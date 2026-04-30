@validazioneComunicazioniBonarie
Feature: comunicazioni bonarie

  Scenario: [PREPARE-TC1-OK] Validazione nuovo service api prepare con tutti i parametri mandatori
    Given inizializzata una comunicazione bonaria con valori di default
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC2A-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls lista empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls |
      | [EMPTY]        |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC2B-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls popolata con almeno un url a empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls                       |
      | [EMPTY],https://TestServer/allegato1 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC2C-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls popolata con almeno un url a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls                            |
      | [SOLO_SPAZI],https://TestServer/allegato1 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC3A-KO] Validazione nuovo service api prepare con parametro (required) printType a empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | printType |
      | [EMPTY]   |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC3B-KO] Validazione nuovo service api prepare con parametro (required) printType a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | printType    |
      | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC4A-KO] Validazione nuovo service api prepare con parametro (required) receiverType empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | receiverType |
      | [EMPTY]      |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC4B-KO] Validazione nuovo service api prepare con parametro (required) receiverType blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | receiverType |
      | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC5A-KO] Validazione nuovo service api prepare con parametro (required) iun a empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun     |
      | [EMPTY] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC5B-KO] Validazione nuovo service api prepare con parametro (required) iun a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun          |
      | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC6A-KO] Validazione nuovo service api prepare con parametro (required) requestId a empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId |
      | [EMPTY]   |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC6B-KO] Validazione nuovo service api prepare con parametro (required) requestId a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId    |
      | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC7A-KO] Validazione nuovo service api prepare con parametro (required) proposalProductType a empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | proposalProductType |
      | [EMPTY]             |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC7B-KO] Validazione nuovo service api prepare con parametro (required) proposalProductType a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | proposalProductType |
      | [SOLO_SPAZI]        |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC8-KO] Validazione nuovo service api prepare con parametro (required) printType null
    Given inizializzata una comunicazione bonaria con i parametri:
      | printType |
      | [NULL]    |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC9-KO] Validazione nuovo service api prepare con parametro (required) receiverType null
    Given inizializzata una comunicazione bonaria con i parametri:
      | receiverType |
      | [NULL]       |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC10-KO] Validazione nuovo service api prepare con parametro (required) iun null
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun    |
      | [NULL] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC11-KO] Validazione nuovo service api prepare con parametro (required) requestId null
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId |
      | [NULL]    |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC11B-KO] Validazione nuovo service api prepare con parametro (required) proposalProductType null
    Given inizializzata una comunicazione bonaria con i parametri:
      | proposalProductType |
      | [NULL]              |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC11C-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls null
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls |
      | [NULL]         |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400


  Scenario: [PREPARE-TC12-KO] Validazione nuovo service api prepare con requestId esistente (già preso in carico)
  ma il corpo della richiesta presenta dati diversi
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                         | printType |
      | AAAA-BBBB-CCCC-202202-1_requestId | FRONTE    |
    When si richiede la prepare della comunicazione bonaria
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                         | printType    |
      | AAAA-BBBB-CCCC-202202-1_requestId | FRONTE_RETRO |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 409

  Scenario: [PREPARE-TC13-OK] Validazione nuovo service api prepare per richiesta già elaborata in precedenza (caso idempotenza)
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                         |
      | AAAA-BBBB-CCCC-202202-2_requestId |
    When si richiede la prepare della comunicazione bonaria
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                         |
      | AAAA-BBBB-CCCC-202202-2_requestId |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 200


  Scenario: [PREPARE-TC15-KO] Validazione nuovo service api prepare con valore enum non valido nel parametro proposalProductType
    Given inizializzata una comunicazione bonaria con i parametri:
      | proposalProductType |
      | AR                  |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400


  Scenario: [PREPARE-TC16-KO] Validazione nuovo service api prepare con formato data invalido nel parametro notificationSentAt
    Given inizializzata una comunicazione bonaria con i parametri:
      | notificationSentAt          |
      | 2022-07----27T12:22:33.444Z |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC18-OK] Validazione nuovo service api prepare con molteplici url nel parametro attachmentUrls
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls                                            |
      | https://TestServer/allegato1,https://TestServer/allegato2 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC19-KO] Validazione nuovo service api prepare con valore non censito nel parametro printType
    Given inizializzata una comunicazione bonaria con i parametri:
      | printType |
      | AR        |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC20-OK] Validazione nuovo service api prepare con valore non censito nel parametro receiverType
    Given inizializzata una comunicazione bonaria con i parametri:
      | receiverType |
      | AR           |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC21-OK] Validazione nuovo service api prepare con requestId che non contiene il prefisso dello iun fornito
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                |
      | QUESTO-E-INVENTATO |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC22-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.fullname mancante
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname | address  | city   |
      | [NULL]   | Via Roma | Milano |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC23-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.address mancante
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname    | address | city   |
      | Mario Rossi | [NULL]  | Milano |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC24-KO] Validazione nuovo service api prepare con parametro (required) receiverAddress.city mancante
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname    | address  | city   |
      | Mario Rossi | Via Roma | [NULL] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC22B-OK] Validazione nuovo service api prepare con parametro (required) receiverAddress.fullname empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | address  | city   | fullname |
      | Via Roma | Milano | [EMPTY]  |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC23B-OK] Validazione nuovo service api prepare con parametro (required) receiverAddress.address empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname    | city   | address |
      | Mario Rossi | Milano | [EMPTY] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC24B-OK] Validazione nuovo service api prepare con parametro (required) receiverAddress.city empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname    | address  | city    |
      | Mario Rossi | Via Roma | [EMPTY] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC22C-OK] Validazione nuovo service api prepare con parametro (required) receiverAddress.fullname blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | address  | city   | fullname     |
      | Via Roma | Milano | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC23C-OK] Validazione nuovo service api prepare con parametro (required) receiverAddress.address blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname    | city   | address      |
      | Mario Rossi | Milano | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC24C-OK] Validazione nuovo service api prepare con parametro (required) receiverAddress.city blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | fullname    | address  | city         |
      | Mario Rossi | Via Roma | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC25A-KO] Validazione nuovo service api prepare con header X-Client-Id empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | xClientId |
      | [EMPTY]   |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC25B-KO] Validazione nuovo service api prepare con header X-Client-Id blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | xClientId    |
      | [SOLO_SPAZI] |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC26-KO] Validazione nuovo service api prepare con length header X-Client-Id > 64
    Given inizializzata una comunicazione bonaria con i parametri:
      | xClientId                                                                 |
      | questoclientidèdavverotroppolungoenonrispettalalunghezzamassimaconsentita |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC27-OK] Validazione nuovo service api prepare cambiando valore header X-Client-Id e stesso corpo della richiesta
    Given inizializzata una comunicazione bonaria con i parametri:
      | xClientId     | requestId                         |
      | primoClientId | AAAA-BBBB-CCCC-202202-3_requestI3 |
    When si richiede la prepare della comunicazione bonaria
    Given inizializzata una comunicazione bonaria con i parametri:
      | xClientId               | requestId                         |
      | questoclientidècambiato | AAAA-BBBB-CCCC-202202-3_requestI3 |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 200

  Scenario: [PREPARE-TC28-OK] Validazione nuovo service api prepare con length header X-Client-Id uguale a 1
    Given inizializzata una comunicazione bonaria con i parametri:
      | xClientId |
      | x         |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC29-KO] Validazione nuovo service api prepare con header X-Client-Id mancante (null)
    Given inizializzata una comunicazione bonaria con i parametri:
      | xClientId |
      | [NULL]    |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400



