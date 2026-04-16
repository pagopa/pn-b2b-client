Feature: comunicazioni bonarie

  @Annullamento
  Funzionalità: Gestione priorità coda messaggi

  @delayer1
  Scenario : [DELAYER-TC1] Verifica la coerenza dell'algoritmo valutando la corretta pianificazione per priorità
    Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
      | seed                 | quantita |
      | tcRanking_2nd_890_   | 16       |
      | tcRanking_RS_2nd_    | 14       |
      | tcRanking_RS_890_    | 16       |
      | tcRanking_RS_        | 14       |
      | tcRanking_2nd_       | 14       |
      | tcRanking_890_       | 16       |
      | tcRankingRS_2nd_890_ | 20       |
    When richiamo l'API di pianificazione di pn-delayer con i dati forniti
    Then verifico che il codice di risposta sia 201
    And controllo che le comunicazioni bonarie abbiano priorità più bassa (priority: 4) rispetto a quelle a valore legale.


  Scenario : [PREPARE-TC1-OK] Validazione nuovo service api prepare con tutti i parametri mandatori
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls    | proposalProductType
    | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un codice di stato di successo




  Scenario : [PREPARE-TC2-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls empty
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | []             | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 409

  Scenario : [PREPARE-TC3-KO] Validazione nuovo service api prepare con parametro (required) printType a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           |                 | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 409

  Scenario : [PREPARE-TC4-KO] Validazione nuovo service api prepare con parametro (required) receiverType a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 |              | BN_FRONTE_RETRO | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 409

  Scenario : [PREPARE-TC5-KO] Validazione nuovo service api prepare con parametro (required) iun a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      |                         | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 409

  Scenario : [PREPARE-TC6-KO] Validazione nuovo service api prepare con parametro (required) requestId a blank
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      | ABCD-HILM-YKWX-202202-1 |                                   |              | BN_FRONTE_RETRO | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 409




  Scenario : [PREPARE-TC2-KO] Validazione nuovo service api prepare con parametro (required) attachmentUrls null
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | null           | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario : [PREPARE-TC3-KO] Validazione nuovo service api prepare con parametro (required) printType null
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | null            | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario : [PREPARE-TC4-KO] Validazione nuovo service api prepare con parametro (required) receiverType null
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 |              | BN_FRONTE_RETRO | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario : [PREPARE-TC5-KO] Validazione nuovo service api prepare con parametro (required) iun null
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      |                         | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario : [PREPARE-TC6-KO] Validazione nuovo service api prepare con parametro (required) requestId null
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      | ABCD-HILM-YKWX-202202-1 |                                   |              | BN_FRONTE_RETRO | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400





  Scenario : [PREPARE-TC7-KO] Validazione nuovo service api prepare con requestId esistente (già preso in carico)
  ma il corpo della richiesta presenta dati diversi
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PG           | BN_FRONTE_RETRO | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 409

  Scenario : [PREPARE-TC8-OK] Validazione nuovo service api prepare per richiesta già elaborata in precedenza (caso idempotenza)
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>] | RS
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un codice di stato di successo



  Scenario : [PREPARE-TC9-KO] Validazione nuovo service api prepare con parametro (required) proposalProductType null
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>] | null
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario : [PREPARE-TC10-KO] Validazione nuovo service api prepare con valore enum non valido nel parametro proposalProductType
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>] | AR
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 500


  Scenario : [PREPARE-TC11-KO] Validazione nuovo service api prepare con formato data invalido nel parametro notificationSentAt
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType | notificationSentAt
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>] | RS                  | 2022-07----27T12:22:33.444Z
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 500


  Scenario : [PREPARE-TC12-OK] Validazione nuovo service api prepare con url non valido nel parametro attachmentUrls
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls      | proposalProductType | notificationSentAt
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try2 | PF           | BN_FRONTE_RETRO | [<url-non-valido>]  | RS                  | 2022-07-27T12:22:33.444Z
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un codice di stato di successo

  Scenario : [PREPARE-TC13-OK] Validazione nuovo service api prepare con molteplici url nel parametro attachmentUrls
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls                         | proposalProductType | notificationSentAt
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try3 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>,<url-da-inserire>]  | RS                  | 2022-07-27T12:22:33.444Z
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un codice di stato di successo

  Scenario : [PREPARE-TC14-OK] Validazione nuovo service api prepare con valore non censito nel parametro printType
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls       | proposalProductType | notificationSentAt
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try4 | PF           | BN_NON_CENSITO  | [<url-da-inserire>]  | RS                  | 2022-07-27T12:22:33.444Z
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un codice di stato di successo

  Scenario : [PREPARE-TC15-OK] Validazione nuovo service api prepare con valore non censito nel parametro receiverType
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls       | proposalProductType | notificationSentAt
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try5 | AA           | BN_FRONTE_RETRO | [<url-da-inserire>]  | RS                  | 2022-07-27T12:22:33.444Z
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un codice di stato di successo

  Scenario : [PREPARE-TC16-OK] Validazione nuovo service api prepare con requestId che non contiene il prefisso dello iun fornito
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls       | proposalProductType | notificationSentAt
      | ABCD-HILM-YKWX-202202-1 | UVXZ-HILM-YKWX-202202-1_rec0_try6 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>]  | RS                  | 2022-07-27T12:22:33.444Z
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un codice di stato di successo
