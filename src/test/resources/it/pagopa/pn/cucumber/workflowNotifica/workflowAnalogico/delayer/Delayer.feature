  # Gli elementi usciti dalla PREPARE fase 1 alla settimana W vengono inseriti nella pn-DelayerPaperDelivey con la deliveryDate che punta a W+1
  # Dal punto di vista del test siamo nel POV della valutazione, quindi è come se le notifiche fossero state caricate in tabella la settimana scorsa (W)
  # con la deliveryDate alla W+1(corrente) e ora, settimana W+1 le stiamo valutando
  Feature: Gestione notifiche tramite algoritmo del microservizio ritardatore e Lambda di test


    Scenario Outline: [Prova] Refactor in un solo csv
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
      And si presuppone che la capacità di stampa giornaliera sia esattamente 180000
      #And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      And viene simulato internamente l'algoritmo di pianificazione
      #When viene avviato l'algoritmo tramite lambda
      And vengono recuperate le notifiche al workflow step "EVALUATE_SENDER_LIMIT"
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
      Then verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                   | TOT |
        | "tcRankingMerged.csv" | 110 |


    Scenario Outline: [DELAYER-RANKING] Refactor in un solo csv
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed                 | quantita |
        | tcRanking_2nd_890_   | 16       |
        | tcRanking_RS_2nd_    | 14       |
        | tcRanking_RS_890_    | 16       |
        | tcRanking_RS_        | 14       |
        | tcRanking_2nd_       | 14       |
        | tcRanking_890_       | 16       |
        | tcRankingRS_2nd_890_ | 20       |
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
      And si verifica che il limite settimanale utilizzato dai recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId         | comparative | limit |
        | driverRanking2nd_890~P1         | esattamente | 0     |
        | driverRanking2nd_890~CAP1_P1    | esattamente | 0     |
        | driverRankingRS_2nd~P2          | esattamente | 0     |
        | driverRankingRS_2nd~CAP1_P2     | esattamente | 0     |
        | driverRankingRS_890~P3          | esattamente | 0     |
        | driverRankingRS_890~CAP1_P3     | esattamente | 0     |
        | driverRankingRS~P4              | esattamente | 0     |
        | driverRankingRS~CAP1_P4         | esattamente | 0     |
        | driverRanking2nd~P5             | esattamente | 0     |
        | driverRanking2nd~CAP1_P5        | esattamente | 0     |
        | driverRanking890~P6             | esattamente | 0     |
        | driverRanking890~CAP1_P6        | esattamente | 0     |
        | driverRankingRS_2nd_890~P7      | esattamente | 0     |
        | driverRankingRS_2nd_890~CAP1_P7 | esattamente | 0     |
      And si presuppone che la capacità di stampa giornaliera sia esattamente 5
      And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      And viene simulato internamente l'algoritmo di pianificazione
      When viene avviato l'algoritmo tramite lambda
      And vengono recuperate le notifiche al workflow step "EVALUATE_SENDER_LIMIT"
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
      And vengono recuperate le notifiche al workflow step "SENT_TO_PREPARE_PHASE_2"
      And verifica che il processo fino al workflow step "SENT_TO_PREPARE_PHASE_2" abbia rispettato i criteri di ranking per almeno un test case:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      Then verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                   | TOT |
        | "tcRankingMerged.csv" | 110 |


    @delayer
    Scenario Outline: [DELAYER-TC01] Le notifiche sono pianificate secondo i criteri di ranking
      Given il CSV <csv> contiene <TOT> notifiche cosi distribuite:
        | categoria              | quantita            |
        | RS                     | <RS>                |
        | SECONDO_TENTATIVO      | <SECONDO_TENTATIVO> |
        | ALTRO                  | <ALTRO>             |
        | RECAPITISTI_UTILIZZATI | 1                   |
        | MITTENTI_UTILIZZATI    | 1                   |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId           | comparative | limit |
        | senderPaId1~RS~RM  | almeno      | 10    |
        | senderPaId1~AR~RM  | almeno      | 10    |
        | senderPaId1~890~RM | almeno      | 10    |
      And si presuppone che il limite mittente settimanale (paId-product_type-province-deliveryDate) sia esattamente <senderLimit>
      And si presuppone che il limite recapitista unificato settimanale (unifiedDeliveryDriver-provincia-deliveryDate) sia almeno <driverCapacity>
      And si presuppone che la capacità di stampa giornaliera sia esattamente <printCapacity>
      And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      When viene avviato l'algoritmo tramite lambda
      And esattamente <nEvaluateSenderLimit> notifiche sono al workflow step "EVALUATE_SENDER_LIMIT"
      And esattamente <nEvaluateDriverCapacity> notifiche sono al workflow step "EVALUATE_DRIVER_CAPACITY"
      And verifica che la capacità disponibile per ogni tripla (unifiedDeliveryDriver-provincia-deliveryDate) sia esattamente <driverCapacity>
      And il processo valutato fino al workflow step "EVALUATE_DRIVER_CAPACITY" ha rispettato i criteri di ranking:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      And esattamente <nEvaluatePrintCapacity> notifiche sono al workflow step "EVALUATE_PRINT_CAPACITY"
      And il processo valutato fino al workflow step "EVALUATE_PRINT_CAPACITY" ha rispettato i criteri di ranking:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      And esattamente <nSentPhase2> notifiche sono al workflow step "SENT_TO_PREPARE_PHASE_2"
      Then esattamente <nCongelate> notifiche sono state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente
      And il processo valutato fino al workflow step "SENT_TO_PREPARE_PHASE_2" ha rispettato i criteri di ranking:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |

      Examples:
        | csv                           | RS | SECONDO_TENTATIVO | ALTRO | TOT | senderLimit | driverCapacity | printCapacity | nEvaluateSenderLimit | nEvaluateDriverCapacity | nEvaluatePrintCapacity | nSentPhase2 | nCongelate |
        | "tc01_priorita_2nd.csv"       | 0  | 15                | 15    | 30  | 30          | 20             | 180000        | 30                   | 30                      | 20                     | 20          | 10         |
        | "tc01_priorita_rs.csv"        | 15 | 15                | 0     | 30  | 30          | 20             | 180000        | 30                   | 30                      | 20                     | 20          | 10         |
        | "tc01_priorita_rs_int.csv"    | 30 | 0                 | 0     | 30  | 30          | 20             | 180000        | 30                   | 30                      | 20                     | 20          | 10         |
        | "tc01_priorita_2nd_int.csv"   | 0  | 30                | 0     | 30  | 30          | 20             | 180000        | 30                   | 30                      | 20                     | 20          | 10         |
        | "tc01_priorita_altro_int.csv" | 0  | 0                 | 30    | 30  | 30          | 20             | 180000        | 30                   | 30                      | 20                     | 20          | 10         |


    @delayer
    Scenario Outline: [DELAYER-TC02] Rispetto dei limiti settimanali mittenti, recapitisti, di stampa e racking
      Given il CSV <csv> contiene <TOT> notifiche cosi distribuite:
        | categoria              | quantita            |
        | RS                     | <RS>                |
        | SECONDO_TENTATIVO      | <SECONDO_TENTATIVO> |
        | ALTRO                  | <ALTRO>             |
        | RECAPITISTI_UTILIZZATI | 1                   |
        | MITTENTI_UTILIZZATI    | 1                   |
      And si presuppone che il limite mittente settimanale (paId-product_type-province-deliveryDate) sia esattamente <senderLimit>
      And si presuppone che il limite recapitista unificato settimanale (unifiedDeliveryDriver-provincia-deliveryDate) sia esattamente <driverCapacity>
      And si presuppone che la capacità di stampa giornaliera sia esattamente <printCapacity>
      And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      When viene avviato l'algoritmo tramite lambda
      And esattamente <nEvaluateSenderLimit> notifiche sono al workflow step "EVALUATE_SENDER_LIMIT"
      And esattamente <nEvaluateDriverCapacity> notifiche sono al workflow step "EVALUATE_DRIVER_CAPACITY"
      And verifica che la capacità disponibile per ogni tripla (unifiedDeliveryDriver-provincia-deliveryDate) sia esattamente <driverCapacity>
      And il processo valutato fino al workflow step "EVALUATE_DRIVER_CAPACITY" ha rispettato i criteri di ranking:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      And esattamente <nEvaluatePrintCapacity> notifiche sono al workflow step "EVALUATE_PRINT_CAPACITY"
      And il processo valutato fino al workflow step "EVALUATE_PRINT_CAPACITY" ha rispettato i criteri di ranking:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      And esattamente <nSentPhase2> notifiche sono al workflow step "SENT_TO_PREPARE_PHASE_2"
      Then esattamente <nCongelate> notifiche sono state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente
      And il processo valutato fino al workflow step "SENT_TO_PREPARE_PHASE_2" ha rispettato i criteri di ranking:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |

      Examples:
        | csv                             | RS | SECONDO_TENTATIVO | ALTRO | TOT | senderLimit | driverCapacity | printCapacity | nEvaluateSenderLimit | nEvaluateDriverCapacity | nEvaluatePrintCapacity | nSentPhase2 | nCongelate |
        | "tc02_send_limit_same_prod.csv" | 0  | 0                 | 30    | 30  | 20          | 30             | 180000        | 30                   | 20                      | 20                     | 20          | 10         |
        | "tc02_driver_capacity.csv"      | 0  | 0                 | 30    | 30  | 30          | 0              | 180000        | 30                   | 30                      | 0                      | 0           | 30         |
        | "tc02_print_capacity.csv"       | 0  | 0                 | 30    | 30  | 30          | 30             | 0             | 30                   | 30                      | 30                     | 0           | 0          |
        | "tc02_send_limit_diff_prod.csv" | 0  | 0                 | 30    | 30  | 30          | 30             | 180000        | 30                   | 30                      | 30                     | 30          | 0          |


    @delayer
    Scenario Outline: [DELAYER-TC03] Le notifiche dei mittenti non censiti sono elaborate solo in base alla capacità residua
      Given il CSV <csv> contiene <TOT> notifiche cosi distribuite:
        | categoria              | quantita            |
        | RS                     | <RS>                |
        | SECONDO_TENTATIVO      | <SECONDO_TENTATIVO> |
        | ALTRO                  | <ALTRO>             |
        | RECAPITISTI_UTILIZZATI | 1                   |
      And si presuppone che il limite mittente settimanale (paId-product_type-province-deliveryDate) sia esattamente <senderLimit>
      And si presuppone che il limite recapitista unificato settimanale (unifiedDeliveryDriver-provincia-deliveryDate) sia esattamente <driverCapacity>
      And si presuppone che la capacità di stampa giornaliera sia esattamente <printCapacity>
      And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      When viene avviato l'algoritmo tramite lambda
      And esattamente <nEvaluateSenderLimit> notifiche sono al workflow step "EVALUATE_SENDER_LIMIT"
      And esattamente <nEvaluateDriverCapacity> notifiche sono al workflow step "EVALUATE_DRIVER_CAPACITY"
      And verifica che la capacità disponibile per ogni tripla (unifiedDeliveryDriver-provincia-deliveryDate) sia esattamente <driverCapacity>
      And il processo valutato fino al workflow step "EVALUATE_DRIVER_CAPACITY" ha rispettato i criteri di ranking:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      And esattamente <nEvaluatePrintCapacity> notifiche sono al workflow step "EVALUATE_PRINT_CAPACITY"
      And il processo valutato fino al workflow step "EVALUATE_PRINT_CAPACITY" ha rispettato i criteri di ranking:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      And esattamente <nSentPhase2> notifiche sono al workflow step "SENT_TO_PREPARE_PHASE_2"
      Then esattamente <nCongelate> notifiche sono state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente
      And il processo valutato fino al workflow step "SENT_TO_PREPARE_PHASE_2" ha rispettato i criteri di ranking:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |

      Examples:
        | csv                          | RS | SECONDO_TENTATIVO | ALTRO | TOT | senderLimit | driverCapacity | printCapacity | nEvaluateSenderLimit | nEvaluateDriverCapacity | nEvaluatePrintCapacity | nSentPhase2 | nCongelate |
        | "tc03_1send_1unknow.csv"     | 10 | 10                | 10    | 30  | 0           | 20             | 180000        | 30                   | 20                      | 20                     | 20          | 10         |
        | "tc03_2send_1unknowRS10.csv" | 15 | 0                 | 20    | 30  | 10          | 30             | 180000        | 30                   | 30                      | 30                     | 30          | 5          |

    @delayer
      #TODO: come gestiscono i caricamenti errati ?
    Scenario Outline: [DELAYER-TC04] Le notifiche con dati errati vengono scartate senza bloccare l'elaborazione
      Given il CSV <csv> contiene <TOT> notifiche cosi distribuite:
        | categoria              | quantita            |
        | RS                     | <RS>                |
        | SECONDO_TENTATIVO      | <SECONDO_TENTATIVO> |
        | ALTRO                  | <ALTRO>             |
        | RECAPITISTI_UTILIZZATI | 1                   |
      And si presuppone che il limite mittente settimanale (paId-product_type-province-deliveryDate) sia esattamente <senderLimit>
      And si presuppone che il limite recapitista unificato settimanale (unifiedDeliveryDriver-provincia-deliveryDate) sia esattamente <driverCapacity>
      And si presuppone che la capacità di stampa giornaliera sia esattamente <printCapacity>
      And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      When viene avviato l'algoritmo tramite lambda
      And esattamente <nEvaluateSenderLimit> notifiche sono al workflow step "EVALUATE_SENDER_LIMIT"

      Examples:
        | csv             | RS | SECONDO_TENTATIVO | ALTRO | TOT | senderLimit | driverCapacity | printCapacity | nEvaluateSenderLimit | nEvaluateDriverCapacity | nEvaluatePrintCapacity | nSentPhase2 | nCongelate |
        | "tc04_fail.csv" | 10 | 10                | 10    | 30  | 0           | 20             | 180000        | 30                   | 20                      | 20                     | 20          | 10         |



