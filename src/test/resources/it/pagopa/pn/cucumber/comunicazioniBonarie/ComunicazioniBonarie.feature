Feature: comunicazioni bonarie

  @delayer1
  Scenario Outline: [DELAYER-TC1] Verifica la coerenza dell'algoritmo valutando la corretta applicazione dei limiti mittente, recapitista, stampa e la pianificazione per priorità
    Given vengono puliti i dati dalle tabelle target
    Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
      | seed                 | quantita |
      | tcRanking_2nd_890_   | 16       |
      | tcRanking_RS_2nd_    | 14       |
      | tcRanking_RS_890_    | 16       |
      | tcRanking_RS_        | 14       |
      | tcRanking_2nd_       | 14       |
      | tcRanking_890_       | 16       |
      | tcRankingRS_2nd_890_ | 20       |
    And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
      | senderId                 | comparative | limit |
      | ranking2nd_890~RS~P1     | esattamente | 0     |
      | ranking2nd_890~AR~P1     | esattamente | 0     |
      | ranking2nd_890~890~P1    | esattamente | 7     |
      | rankingRS_2nd~RS~P2      | esattamente | 0     |
      | rankingRS_2nd~AR~P2      | esattamente | 0     |
      | rankingRS_2nd~890~P2     | esattamente | 0     |
      | rankingRS_890~RS~P3      | esattamente | 0     |
      | rankingRS_890~AR~P3      | esattamente | 0     |
      | rankingRS_890~890~P3     | esattamente | 7     |
      | rankingRS~RS~P4          | esattamente | 0     |
      | rankingRS~AR~P4          | esattamente | 0     |
      | rankingRS~890~P4         | esattamente | 0     |
      | ranking2nd~RS~P5         | esattamente | 0     |
      | ranking2nd~AR~P5         | esattamente | 0     |
      | ranking2nd~890~P5        | esattamente | 0     |
      | ranking890~RS~P6         | esattamente | 0     |
      | ranking890~AR~P6         | esattamente | 0     |
      | ranking890~890~P6        | esattamente | 14    |
      | rankingRS_2nd_890~RS~P7  | esattamente | 0     |
      | rankingRS_2nd_890~AR~P7  | esattamente | 0     |
      | rankingRS_2nd_890~890~P7 | esattamente | 7     |
    And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
      | unifiedDeliveryDriverId         | comparative | limit |
      | driverRanking2nd_890~P1         | esattamente | 10    |
      | driverRanking2nd_890~CAP1_P1    | esattamente | 10    |
      | driverRankingRS_2nd~P2          | esattamente | 10    |
      | driverRankingRS_2nd~CAP1_P2     | esattamente | 10    |
      | driverRankingRS_890~P3          | esattamente | 10    |
      | driverRankingRS_890~CAP1_P3     | esattamente | 10    |
      | driverRankingRS~P4              | esattamente | 10    |
      | driverRankingRS~CAP1_P4         | esattamente | 10    |
      | driverRanking2nd~P5             | esattamente | 10    |
      | driverRanking2nd~CAP1_P5        | esattamente | 10    |
      | driverRanking890~P6             | esattamente | 10    |
      | driverRanking890~CAP1_P6        | esattamente | 10    |
      | driverRankingRS_2nd_890~P7      | esattamente | 10    |
      | driverRankingRS_2nd_890~CAP1_P7 | esattamente | 10    |
    And si verifica che la capacità disponibile settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
      | unifiedDeliveryDriverId         | comparative | limit |
      | driverRanking2nd_890~P1         | almeno      | 10    |
      | driverRanking2nd_890~CAP1_P1    | almeno      | 10    |
      | driverRankingRS_2nd~P2          | almeno      | 10    |
      | driverRankingRS_2nd~CAP1_P2     | almeno      | 10    |
      | driverRankingRS_890~P3          | almeno      | 10    |
      | driverRankingRS_890~CAP1_P3     | almeno      | 10    |
      | driverRankingRS~P4              | almeno      | 10    |
      | driverRankingRS~CAP1_P4         | almeno      | 10    |
      | driverRanking2nd~P5             | almeno      | 10    |
      | driverRanking2nd~CAP1_P5        | almeno      | 10    |
      | driverRanking890~P6             | almeno      | 10    |
      | driverRanking890~CAP1_P6        | almeno      | 10    |
      | driverRankingRS_2nd_890~P7      | almeno      | 10    |
      | driverRankingRS_2nd_890~CAP1_P7 | almeno      | 10    |
    And viene impostato il limite massimo di 40 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
    And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
    And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
    When viene avviata la step function BatchWorkflowStateMachine
    And vengono recuperate le notifiche al workflow step "EVALUATE_SENDER_LIMIT"
    And verifica che il processo fino al workflow step "EVALUATE_SENDER_LIMIT" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria         | ordinamentoCampo   |
      | RS                | prepareRequestDate |
      | SECONDO_TENTATIVO | prepareRequestDate |
      | ALTRO             | notificationSentAt |
    And vengono recuperate le notifiche al workflow step "EVALUATE_RESIDUAL_CAPACITY"
    And verifica che il processo fino al workflow step "EVALUATE_RESIDUAL_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria         | ordinamentoCampo   |
      | RS                | prepareRequestDate |
      | SECONDO_TENTATIVO | prepareRequestDate |
      | ALTRO             | notificationSentAt |
    And vengono recuperate le notifiche al workflow step "EVALUATE_DRIVER_CAPACITY"
    And verifica che il processo fino al workflow step "EVALUATE_DRIVER_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria         | ordinamentoCampo   |
      | RS                | prepareRequestDate |
      | SECONDO_TENTATIVO | prepareRequestDate |
      | ALTRO             | notificationSentAt |
    And vengono recuperate le notifiche al workflow step "EVALUATE_PRINT_CAPACITY"
    And verifica che il processo fino al workflow step "EVALUATE_PRINT_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria         | ordinamentoCampo   |
      | RS                | prepareRequestDate |
      | SECONDO_TENTATIVO | prepareRequestDate |
      | ALTRO             | notificationSentAt |
    Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
    And vengono simulate internamente le operazioni di DelayerToPaperChannelStateMachine
    And vengono avviate le 2 esecuzioni della step function DelayerToPaperChannelStateMachine
    And verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
    And verifica la corretta pianificazione di ogni test case

    Examples:
      | csv                   | TOT |
      | "tcRankingMerged.csv" | 4 |


  Scenario: [PREPARE-TC1-OK] Validazione nuovo service api prepare con tutti i parametri mandatori
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                            | receiverType | printType       | attachmentUrls               | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1001 | PF           | BN_FRONTE_RETRO | https://TestServer/allegato1 | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve una response con codice di stato 201

  Scenario: [PREPARE-TC2A-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls empty*
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls          | requestId                             |
      | [EMPTY]                 | ABCD-HILM-YKWX-202202-1_rec0_try1002A |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC2B-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls blank*
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls          | requestId                             |
      | [SOLO_SPAZI]            | ABCD-HILM-YKWX-202202-1_rec0_try1002B |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario: [PREPARE-TC2C-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls popolata con almeno un url a blank*
    Given inizializzata una comunicazione bonaria con i parametri:
      | attachmentUrls          | requestId                             |
      | [SOLO_SPAZI],http://xxx | ABCD-HILM-YKWX-202202-1_rec0_try1002B |
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

  Scenario: [PREPARE-TC29-KO] Validazione nuovo service api prepare con length header X-Client-Id mancante (valore NULL)
    Given inizializzata una comunicazione bonaria con i parametri:
      | requestId                            | xClientId |
      | UVXZ-HILM-YKWX-202202-1_rec0_try1029 | [null]    |
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400


