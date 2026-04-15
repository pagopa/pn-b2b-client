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
    | iun                     | requestId                         | receiverType | printType       | attachmentUrls
    | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>]
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un codice di stato di successo

  Scenario : [PREPARE-TC2-KO] Validazione nuovo service api prepare con un
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | []
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400

  Scenario : [PREPARE-TC3-KO] Validazione nuovo service api prepare con requestId identica ad altra richiesta [TC1] già presa in carico
  ma il corpo della richiesta presenta parametri differenti rispetto alla precedente
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PG           | BN_FRONTE_RETRO | [<url-da-inserire>]
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 409

  Scenario : [PREPARE-TC4-OK] Validazione nuovo service api prepare per richiesta già elaborata in precedenza [TC1]
    Given inizializzata una comunicazione bonaria con i parametri:
      | iun                     | requestId                         | receiverType | printType       | attachmentUrls
      | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | [<url-da-inserire>]
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un codice di stato di successo