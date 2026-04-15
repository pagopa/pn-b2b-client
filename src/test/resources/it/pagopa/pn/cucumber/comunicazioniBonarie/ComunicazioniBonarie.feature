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


  Scenario : [PREPARE-TC1] Validazione nuovo service api prepare
    Given inizializzata una comunicazione bonaria con i parametri:
    | iun                     | requestId                         | receiverType | printType       | attachmentUrls
    | ABCD-HILM-YKWX-202202-1 | ABCD-HILM-YKWX-202202-1_rec0_try1 | PF           | BN_FRONTE_RETRO | []
    When si richiede la prepare della comunicazione bonaria
    Then si riceve un errore con codice di stato 400