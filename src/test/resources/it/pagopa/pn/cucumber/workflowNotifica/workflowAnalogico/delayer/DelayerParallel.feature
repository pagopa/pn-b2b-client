# CAP/Province non devono collidere
# Non devono essere testate le capacità di stampa
# aggiungere l'ID del test (testo tra le prime [...]) dentro a DelayerParallelTest
# delete una tantum nel @BeforeAll (non in Background: verrebbe ripetuto ogni scenario)

Feature: Delayer — test paralleli (BatchWorkflow + DelayerToPaperChannel)

  @delayerParallel @delayer1
  Scenario Outline: [DELAYER-TC1] Verifica la coerenza dell'algoritmo valutando la corretta applicazione dei limiti mittente, recapitista, stampa e la pianificazione per priorità
    Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
      | seed                 | quantita |
      | tcRanking_2nd_890_   | 16       |
      | tcRanking_RS_2nd_    | 14       |
      | tcRanking_RS_890_    | 16       |
      | tcRanking_RS_        | 14       |
      | tcRanking_2nd_       | 14       |
      | tcRanking_890_       | 16       |
      | tcRankingRS_2nd_890_ | 21       |
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
    And viene impostato il limite massimo di 180000 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
    And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
    And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
    When viene avviata la step function BatchWorkflowStateMachine
    And vengono recuperate le notifiche al workflow step "EVALUATE_SENDER_LIMIT"
    And verifica che il processo fino al workflow step "EVALUATE_SENDER_LIMIT" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria             | ordinamentoCampo   |
      | RS                    | prepareRequestDate |
      | SECONDO_TENTATIVO     | prepareRequestDate |
      | ALTRO                 | notificationSentAt |
      | COMUNICAZIONE_BONARIE | prepareRequestDate |
    And vengono recuperate le notifiche al workflow step "EVALUATE_RESIDUAL_CAPACITY"
    And verifica che il processo fino al workflow step "EVALUATE_RESIDUAL_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria             | ordinamentoCampo   |
      | RS                    | prepareRequestDate |
      | SECONDO_TENTATIVO     | prepareRequestDate |
      | ALTRO                 | notificationSentAt |
      | COMUNICAZIONE_BONARIE | prepareRequestDate |
    And vengono recuperate le notifiche al workflow step "EVALUATE_DRIVER_CAPACITY"
    And verifica che il processo fino al workflow step "EVALUATE_DRIVER_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria             | ordinamentoCampo   |
      | RS                    | prepareRequestDate |
      | SECONDO_TENTATIVO     | prepareRequestDate |
      | ALTRO                 | notificationSentAt |
      | COMUNICAZIONE_BONARIE | prepareRequestDate |
    And vengono recuperate le notifiche al workflow step "EVALUATE_PRINT_CAPACITY"
    And verifica che il processo fino al workflow step "EVALUATE_PRINT_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria             | ordinamentoCampo   |
      | RS                    | prepareRequestDate |
      | SECONDO_TENTATIVO     | prepareRequestDate |
      | ALTRO                 | notificationSentAt |
      | COMUNICAZIONE_BONARIE | prepareRequestDate |
    Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
    And vengono simulate internamente le operazioni di DelayerToPaperChannelStateMachine
    And vengono avviate le 1 esecuzioni della step function DelayerToPaperChannelStateMachine
    And verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
    And verifica la corretta pianificazione di ogni test case
    Examples:
      | csv                   | TOT |
      | "tcRankingMerged.csv" | 111 |

  @delayerParallel @delayer2
  Scenario Outline: [DELAYER-TC2] Verifica la gestione di un mittente non censito
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
      | infinityDriverP8~P8      | esattamente | 35000 |
      | infinityDriverP8~CAP1_P8 | esattamente | 35000 |
    And si verifica che la capacità disponibile settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
      | unifiedDeliveryDriverId  | comparative | limit |
      | infinityDriverP8~P8      | esattamente | 35000 |
      | infinityDriverP8~CAP1_P8 | esattamente | 35000 |
    And viene impostato il limite massimo di 180000 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
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
    And verifica la corretta pianificazione di ogni test case
    And vengono simulate internamente le operazioni di DelayerToPaperChannelStateMachine
    And vengono avviate le 1 esecuzioni della step function DelayerToPaperChannelStateMachine
    And verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
    And verifica che le spedizioni spostate alla settimana successiva siano lo stesso valore
    And verifica la corretta pianificazione di ogni test case
    Examples:
      | csv                  | TOT |
      | "tcSenderUnknow.csv" | 15  |

  #La capacità di recapito viene suddivisa prendendo la capacità di recapito della provincia e suddividendola per i CAP.
  @delayerParallel @delayer3
  Scenario Outline: [DELAYER-TC3] Verifica la corretta gestione della capacità di recapito aggregata
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
    And viene impostato il limite massimo di 180000 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
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
    And vengono avviate le 1 esecuzioni della step function DelayerToPaperChannelStateMachine
    And verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
    And verifica la corretta pianificazione di ogni test case
    Examples:
      | csv                 | TOT |
      | "tcSplitSender.csv" | 14  |

  #BUG: https://pagopa.atlassian.net/browse/PN-15504
  @delayerParallel @delayer4
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
    And viene impostato il limite massimo di 180000 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
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
    And verifica che non esistano notifiche al workflow step "EVALUATE_PRINT_CAPACITY" per il seed "tcZeroDriver_"
    Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
    And vengono simulate internamente le operazioni di DelayerToPaperChannelStateMachine
    And vengono avviate le 1 esecuzioni della step function DelayerToPaperChannelStateMachine
    And verifica che non esistano notifiche al workflow step "SENT_TO_PREPARE_PHASE_2" per il seed "tcZeroDriver_"
    And verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
    And verifica la corretta pianificazione di ogni test case
    Examples:
      | csv                | TOT |
      | "tcZeroDriver.csv" | 15  |

  #BUG: https://pagopa.atlassian.net/browse/PN-16324
  @delayerParallel @delayer5
  Scenario Outline: [DELAYER-TC5] Verifica la gestione di province e cap non censiti
    Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
      | seed                 | quantita |
      | tcProvCapNonCensite_ | 15       |
    And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
      | senderId       | comparative | limit |
      | unknow~RS~P01  | esattamente | 0     |
      | unknow~AR~01   | esattamente | 0     |
      | unknow~890~P01 | esattamente | 0     |
      | unknow~RS~P11  | esattamente | 0     |
      | unknow~AR~11   | esattamente | 0     |
      | unknow~890~P11 | esattamente | 0     |
    And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
      | unifiedDeliveryDriverId | comparative | limit |
      | unknow~P01              | esattamente | 0     |
      | unknow~CAP1_P01         | esattamente | 0     |
      | unknow~CAP2_P01         | esattamente | 0     |
      | unknow~CAP11_P01        | esattamente | 0     |
      | unknow~P11              | esattamente | 0     |
      | unknow~CAP1_P11         | esattamente | 0     |
      | unknow~CAP2_P11         | esattamente | 0     |
      | unknow~CAP11_P11        | esattamente | 0     |
    And viene impostato il limite massimo di 180000 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
    And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
    And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
    When viene avviata la step function BatchWorkflowStateMachine
    And vengono recuperate le notifiche al workflow step "EVALUATE_SENDER_LIMIT"
    And verifica che il processo fino al workflow step "EVALUATE_SENDER_LIMIT" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria         | ordinamentoCampo   |
      | RS                | prepareRequestDate |
      | SECONDO_TENTATIVO | prepareRequestDate |
      | ALTRO             | notificationSentAt |
    And verifica che non esistano notifiche al workflow step "EVALUATE_RESIDUAL_CAPACITY" per il seed "tcProvCapNonCensite_"
    And vengono recuperate le notifiche al workflow step "EVALUATE_RESIDUAL_CAPACITY"
    And verifica che il processo fino al workflow step "EVALUATE_RESIDUAL_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria         | ordinamentoCampo   |
      | RS                | prepareRequestDate |
      | SECONDO_TENTATIVO | prepareRequestDate |
      | ALTRO             | notificationSentAt |
    And verifica che non esistano notifiche al workflow step "EVALUATE_DRIVER_CAPACITY" per il seed "tcProvCapNonCensite_"
    And vengono recuperate le notifiche al workflow step "EVALUATE_DRIVER_CAPACITY"
    And verifica che il processo fino al workflow step "EVALUATE_DRIVER_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
      | categoria         | ordinamentoCampo   |
      | RS                | prepareRequestDate |
      | SECONDO_TENTATIVO | prepareRequestDate |
      | ALTRO             | notificationSentAt |
    And verifica che non esistano notifiche al workflow step "EVALUATE_PRINT_CAPACITY" per il seed "tcProvCapNonCensite_"
    Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
    And vengono simulate internamente le operazioni di DelayerToPaperChannelStateMachine
    And vengono avviate le 1 esecuzioni della step function DelayerToPaperChannelStateMachine
    And verifica che non esistano notifiche al workflow step "SENT_TO_PREPARE_PHASE_2" per il seed "tcProvCapNonCensite_"
    And verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
    And verifica la corretta pianificazione di ogni test case
    Examples:
      | csv                       | TOT |
      | "tcProvCapNonCensite.csv" | 15  |
