  # Gli elementi usciti dalla PREPARE fase 1 alla settimana W vengono inseriti nella pn-DelayerPaperDelivey con la deliveryDate che punta a W+1
  # Dal punto di vista del test siamo nel POV della valutazione, quindi è come se le notifiche fossero state caricate in tabella la settimana scorsa (W)
  # con la deliveryDate alla W+1(corrente) e ora, settimana W+1 le stiamo valutando
  Feature: Gestione notifiche tramite algoritmo del microservizio ritardatore e Lambda di test

    Scenario Outline: [TEST] Verifica dell'algoritmo in locale
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed          | quantita |
        | tcZeroDriver_ | 15       |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId       | comparative | limit |
        | unknow~RS~P10  | esattamente | 0     |
        | unknow~AR~P10  | esattamente | 0     |
        | unknow~890~P10 | esattamente | 0     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | zeroDriverP10~P10       | esattamente | 0     |
        | zeroDriverP10~CAP1_P10  | esattamente | 0     |
      And si presuppone che la capacità di stampa giornaliera sia esattamente 180000
      And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
      And vengono simulate internamente le operazioni di DelayerToPaperChannelStateMachine
      And verifica che il processo fino al workflow step "SENT_TO_PREPARE_PHASE_2" abbia rispettato i criteri di ranking per almeno un test case:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |

      Examples:
        | csv                | TOT |
        | "tcZeroDriver.csv" | 15  |
      
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
      And si presuppone che la capacità di stampa giornaliera sia esattamente 5
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
      And viene avviata la step function DelayerToPaperChannelStateMachine
      And vengono recuperate le notifiche al workflow step "SENT_TO_PREPARE_PHASE_2"
      And verifica che il processo fino al workflow step "SENT_TO_PREPARE_PHASE_2" abbia rispettato i criteri di ranking per almeno un test case:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      # Non è possibile controllare che DelayerToPaperChannelStateMachine ricarichi correttamente gli opportuni elementi.
      # La Step Function viene eseguita una sola volta al giorno e processa un numero di elementi pari alla capacità di stampa.
      # Per verificarne il comportamento occorrerebbe quindi:
      # simulare più esecuzioni (es. notifiche_congelate_dalla_seconda_function/capacita_stampa volte) per coprire l’intero ciclo, ma questo attualmente
      # porterebbe facilmente a risultati falsati poichè le function, se eseguite in parallelo, potrebbero portare a risultati errati
      # ed inoltre non c'è modo di verificare se l'i-esima esecuzione sia andata a buon fine
      #Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      And verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                   | TOT |
        | "tcRankingMerged.csv" | 110 |

    @delayer2
    Scenario Outline: [DELAYER-TC2] Verifica la gestione di un mittente non censito
      Given vengono puliti i dati dalle tabelle target
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed            | quantita |
        | tcSenderUnknow_ | 15       |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId      | comparative | limit |
        | unknow~RS~P8  | esattamente | 0     |
        | unknow~AR~P8  | esattamente | 0     |
        | unknow~890~P8 | esattamente | 0     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId  | comparative | limit |
        | infinityDriverP8~P8      | almeno      | 15    |
        | infinityDriverP8~CAP1_P8 | almeno      | 15    |
      And si verifica che il limite settimanale utilizzato dai recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId  | comparative | limit |
        | infinityDriverP8~P8      | inferiore   | 1000  |
        | infinityDriverP8~CAP1_P8 | inferiore   | 1000  |
      And si presuppone che la capacità di stampa giornaliera sia esattamente 0
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
      And viene avviata la step function DelayerToPaperChannelStateMachine
      And verifica che non esistano notifiche al workflow step "SENT_TO_PREPARE_PHASE_2" per il seed "tcSenderUnknow_"
      And vengono recuperate le notifiche al workflow step "SENT_TO_PREPARE_PHASE_2"
      And verifica che il processo fino al workflow step "SENT_TO_PREPARE_PHASE_2" abbia rispettato i criteri di ranking per almeno un test case:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      # Non è possibile controllare che DelayerToPaperChannelStateMachine ricarichi correttamente gli opportuni elementi.
      # La Step Function viene eseguita una sola volta al giorno e processa un numero di elementi pari alla capacità di stampa.
      # Per verificarne il comportamento occorrerebbe quindi:
      # simulare più esecuzioni (es. notifiche_congelate_dalla_seconda_function/capacita_stampa volte) per coprire l’intero ciclo, ma questo attualmente
      # porterebbe facilmente a risultati falsati poichè le function, se eseguite in parallelo, potrebbero portare a risultati errati
      # ed inoltre non c'è modo di verificare se l'i-esima esecuzione sia andata a buon fine
      #Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      And verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                  | TOT |
        | "tcSenderUnknow.csv" | 15  |

    @delayer3
    Scenario Outline: [DELAYER-TC3] Verifica la corretta gestione della capacità di recapito aggregata
      Given vengono puliti i dati dalle tabelle target
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed           | quantita |
        | tcSplitSender_ | 14       |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId                   | comparative | limit |
        | splitSender1CAP1_P9~RS~P9  | almeno      | 0     |
        | splitSender1CAP1_P9~AR~P9  | almeno      | 0     |
        | splitSender1CAP1_P9~890~P9 | almeno      | 10    |
        | splitSender1CAP2_P9~RS~P9  | almeno      | 0     |
        | splitSender1CAP2_P9~AR~P9  | almeno      | 0     |
        | splitSender1CAP2_P9~890~P9 | almeno      | 4     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId     | comparative | limit |
        | splitDriver1CAP1_P9~P9      | esattamente | 11    |
        | splitDriver1CAP1_P9~CAP1_P9 | esattamente | 7     |
        | splitDriver1CAP1_P9~CAP2_P9 | esattamente | 4     |
      And si verifica che la capacità disponibile settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId     | comparative | limit |
        | splitDriver1CAP1_P9~P9      | almeno      | 11    |
        | splitDriver1CAP1_P9~CAP1_P9 | almeno      | 7     |
        | splitDriver1CAP1_P9~CAP2_P9 | almeno      | 4     |
      And si presuppone che la capacità di stampa giornaliera sia esattamente 180000
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
      And viene avviata la step function DelayerToPaperChannelStateMachine
      And vengono recuperate le notifiche al workflow step "SENT_TO_PREPARE_PHASE_2"
      And verifica che il processo fino al workflow step "SENT_TO_PREPARE_PHASE_2" abbia rispettato i criteri di ranking per almeno un test case:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      # Non è possibile controllare che DelayerToPaperChannelStateMachine ricarichi correttamente gli opportuni elementi.
      # La Step Function viene eseguita una sola volta al giorno e processa un numero di elementi pari alla capacità di stampa.
      # Per verificarne il comportamento occorrerebbe quindi:
      # simulare più esecuzioni (es. notifiche_congelate_dalla_seconda_function/capacita_stampa volte) per coprire l’intero ciclo, ma questo attualmente
      # porterebbe facilmente a risultati falsati poichè le function, se eseguite in parallelo, potrebbero portare a risultati errati
      # ed inoltre non c'è modo di verificare se l'i-esima esecuzione sia andata a buon fine
      #Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      And verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                 | TOT |
        | "tcSplitSender.csv" | 14  |
      
    @delayer4
      #BUG: https://pagopa.atlassian.net/browse/PN-15504
    Scenario Outline: [DELAYER-TC4] Verifica la gestione di una capacity driver nulla
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed          | quantita |
        | tcZeroDriver_ | 15       |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId       | comparative | limit |
        | unknow~RS~P10  | esattamente | 0     |
        | unknow~AR~P10  | esattamente | 0     |
        | unknow~890~P10 | esattamente | 0     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | zeroDriverP10~P10       | esattamente | 0     |
        | zeroDriverP10~CAP1_P10  | esattamente | 0     |
      And si verifica che il limite settimanale utilizzato dai recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | zeroDriverP10~P10       | esattamente | 0     |
        | zeroDriverP10~CAP1_P10  | esattamente | 0     |
      And si presuppone che la capacità di stampa giornaliera sia esattamente 180000
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
      And viene avviata la step function DelayerToPaperChannelStateMachine
      And verifica che non esistano notifiche al workflow step "SENT_TO_PREPARE_PHASE_2" per il seed "tcSenderUnknow_"
      And vengono recuperate le notifiche al workflow step "SENT_TO_PREPARE_PHASE_2"
      And verifica che il processo fino al workflow step "SENT_TO_PREPARE_PHASE_2" abbia rispettato i criteri di ranking per almeno un test case:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      # Non è possibile controllare che DelayerToPaperChannelStateMachine ricarichi correttamente gli opportuni elementi.
      # La Step Function viene eseguita una sola volta al giorno e processa un numero di elementi pari alla capacità di stampa.
      # Per verificarne il comportamento occorrerebbe quindi:
      # simulare più esecuzioni (es. notifiche_congelate_dalla_seconda_function/capacita_stampa volte) per coprire l’intero ciclo, ma questo attualmente
      # porterebbe facilmente a risultati falsati poichè le function, se eseguite in parallelo, potrebbero portare a risultati errati
      # ed inoltre non c'è modo di verificare se l'i-esima esecuzione sia andata a buon fine
      #Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      And verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                | TOT |
        | "tcZeroDriver.csv" | 15  |
