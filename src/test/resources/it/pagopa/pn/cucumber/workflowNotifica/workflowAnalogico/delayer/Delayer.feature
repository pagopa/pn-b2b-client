  # Gli elementi usciti dalla PREPARE fase 1 alla settimana W vengono inseriti nella pn-DelayerPaperDelivey con la deliveryDate che punta a W+1
  # Dal punto di vista del test siamo nel POV della valutazione, quindi è come se le notifiche fossero state caricate in tabella la settimana scorsa (W)
  # con la deliveryDate alla W+1(corrente) e ora, settimana W+1 le stiamo valutando
  Feature: Gestione notifiche tramite algoritmo del microservizio ritardatore e Lambda di test

    Scenario: [DELETE]
      Given vengono puliti i dati dalle tabelle target

    Scenario Outline: [DELAYER-TC17tes] Verifica che priorità 100 della settimana successiva preceda priorità 80 congelata dalla settimana precedente
      And il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed                    | quantita |
        | tcSenderPriorityFrozen_ | <TOT>    |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId              | comparative | limit |
        | ranking2nd_890~890~P1 | esattamente | 7     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId      | comparative | limit |
        | driverRanking2nd_890~P1      | esattamente | 10    |
        | driverRanking2nd_890~CAP1_P1 | esattamente | 10    |
      And viene impostato il limite massimo di 5 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
      And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
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
      #And vengono recuperate le notifiche al workflow step "EVALUATE_DRIVER_CAPACITY"
      #And verifica che il processo fino al workflow step "EVALUATE_DRIVER_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
      #  | categoria         | ordinamentoCampo   |
      #  | RS                | prepareRequestDate |
      #  | SECONDO_TENTATIVO | prepareRequestDate |
      #  | ALTRO             | notificationSentAt |
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
        | csv                             | TOT |
        | "tcSenderPriorityFrozenW12.csv" | 11  |


    @delayer6
      #Lo scenario testa il corretto funzionamento della prima parte della lambda, pertanto si utilizzano mittenti non censiti e, non conoscendo a priori il driver,
      #il confronto tra actual ed expected per lo stato EVALUATE_RESIDUAL_CAPACITY non considererà il campo unifiedDriverDelivery
      #BUG: https://pagopa.atlassian.net/browse/PN-16640
    Scenario Outline: [VALIDATION_PN-16640] Validazione dell'uso della cache nel job mittenti
      Given vengono puliti i dati dalle tabelle target
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed       | quantita |
        | tcMassivo_ | 3000     |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId      | comparative | limit |
        | unknow~RS~NA  | almeno      | 0     |
        | unknow~AR~NA  | almeno      | 0     |
        | unknow~890~NA | almeno      | 0     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | Fulmine~NA              | esattamente | 0     |
        | Poste~NA                | esattamente | 0     |
        | Fulmine~80010           | esattamente | 0     |
        | Poste~80010             | esattamente | 0     |
        | Poste~80011             | esattamente | 0     |
        | Poste~80012             | esattamente | 0     |
        | Poste~80013             | esattamente | 0     |
        | Fulmine~80016           | esattamente | 0     |
        | Fulmine~80017           | esattamente | 0     |
        | Poste~80017             | esattamente | 0     |
        | Fulmine~80018           | esattamente | 0     |
        | Poste~80019             | esattamente | 0     |
        | Poste~80020             | esattamente | 0     |
        | Fulmine~80021           | esattamente | 0     |
        | Poste~80021             | esattamente | 0     |
        | Fulmine~80022           | esattamente | 0     |
        | Poste~80022             | esattamente | 0     |
        | Fulmine~80023           | esattamente | 0     |
        | Fulmine~80024           | esattamente | 0     |
        | Poste~80024             | esattamente | 0     |
        | Fulmine~80026           | esattamente | 0     |
        | Poste~80026             | esattamente | 0     |
        | Poste~80028             | esattamente | 0     |
        | Fulmine~80029           | esattamente | 0     |
        | Poste~80029             | esattamente | 0     |
        | Fulmine~80030           | esattamente | 0     |
        | Poste~80030             | esattamente | 0     |
        | Fulmine~80035           | esattamente | 0     |
        | Poste~80035             | esattamente | 0     |
        | Fulmine~80036           | esattamente | 0     |
        | Fulmine~80038           | esattamente | 0     |
        | Fulmine~80039           | esattamente | 0     |
        | Poste~80039             | esattamente | 0     |
        | Poste~80040             | esattamente | 0     |
        | Fulmine~80041           | esattamente | 0     |
        | Poste~80041             | esattamente | 0     |
        | Fulmine~80044           | esattamente | 0     |
        | Poste~80045             | esattamente | 0     |
        | Fulmine~80046           | esattamente | 0     |
        | Poste~80046             | esattamente | 0     |
        | Poste~80047             | esattamente | 0     |
        | Fulmine~80049           | esattamente | 0     |
        | Poste~80049             | esattamente | 0     |
        | Fulmine~80050           | esattamente | 0     |
        | Poste~80053             | esattamente | 0     |
        | Poste~80054             | esattamente | 0     |
        | Fulmine~80055           | esattamente | 0     |
        | Poste~80055             | esattamente | 0     |
        | Poste~80056             | esattamente | 0     |
        | Fulmine~80057           | esattamente | 0     |
        | Fulmine~80058           | esattamente | 0     |
        | Fulmine~80059           | esattamente | 0     |
        | Poste~80062             | esattamente | 0     |
        | Poste~80063             | esattamente | 0     |
        | Poste~80065             | esattamente | 0     |
        | Poste~80067             | esattamente | 0     |
        | Fulmine~80069           | esattamente | 0     |
        | Poste~80069             | esattamente | 0     |
        | Fulmine~80070           | esattamente | 0     |
        | Poste~80072             | esattamente | 0     |
        | Poste~80073             | esattamente | 0     |
        | Poste~80075             | esattamente | 0     |
        | Poste~80075             | esattamente | 0     |
        | Poste~80077             | esattamente | 0     |
        | Poste~80077             | esattamente | 0     |
        | Fulmine~80078           | esattamente | 0     |
        | Poste~80078             | esattamente | 0     |
        | Poste~80081             | esattamente | 0     |
        | Poste~80122             | esattamente | 0     |
        | Fulmine~80123           | esattamente | 0     |
        | Poste~80124             | esattamente | 0     |
        | Fulmine~80125           | esattamente | 0     |
        | Fulmine~80126           | esattamente | 0     |
        | Poste~80126             | esattamente | 0     |
        | Fulmine~80128           | esattamente | 0     |
        | Poste~80128             | esattamente | 0     |
        | Fulmine~80129           | esattamente | 0     |
        | Fulmine~80131           | esattamente | 0     |
        | Fulmine~80132           | esattamente | 0     |
        | Poste~80132             | esattamente | 0     |
        | Fulmine~80134           | esattamente | 0     |
        | Poste~80134             | esattamente | 0     |
        | Poste~80135             | esattamente | 0     |
        | Poste~80136             | esattamente | 0     |
        | Fulmine~80137           | esattamente | 0     |
        | Poste~80137             | esattamente | 0     |
        | Fulmine~80139           | esattamente | 0     |
        | Poste~80139             | esattamente | 0     |
        | Fulmine~80141           | esattamente | 0     |
        | Poste~80141             | esattamente | 0     |
        | Poste~80143             | esattamente | 0     |
        | Fulmine~80144           | esattamente | 0     |
        | Poste~80144             | esattamente | 0     |
        | Fulmine~80145           | esattamente | 0     |
        | Poste~80146             | esattamente | 0     |
        | Fulmine~80147           | esattamente | 0     |
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
      And verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                   | TOT  |
        | "spedizioni_3000.csv" | 3000 |


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
        | infinityDriverP8~P8      | esattamente | 35000 |
        | infinityDriverP8~CAP1_P8 | esattamente | 35000 |
      And si verifica che la capacità disponibile settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId  | comparative | limit |
        | infinityDriverP8~P8      | esattamente | 35000 |
        | infinityDriverP8~CAP1_P8 | esattamente | 35000 |
      And viene impostato il limite massimo di 0 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
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
      And vengono avviate le 2 esecuzioni della step function DelayerToPaperChannelStateMachine
      And verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      And verifica che le spedizioni spostate alla settimana successiva siano lo stesso valore
      And verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                  | TOT |
        | "tcSenderUnknow.csv" | 15  |

    @delayer9
    Scenario Outline: [DELAYER-TC9] Verifica che la StepFunction sia in grado di gestire correttamente più di 5000 spedizioni.
      Given vengono puliti i dati dalle tabelle target
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed            | quantita |
        | tcSenderUnknow_ | 5010     |
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
      And viene impostato il limite massimo di 10588 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
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
        | csv                       | TOT  |
        | "tcSenderUnknow_5010.csv" | 5010 |

    @delayer3
    #La capacità di recapito viene suddivisa prendendo la capacità di recapito della provincia e suddividendola per i CAP. La suddivisone non è paritaria ma dipende
    #dal numero di abitanti del comune, nel caso di test la densità è la medesima
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


    @delayer4
      #BUG: https://pagopa.atlassian.net/browse/PN-15504
    Scenario Outline: [DELAYER-TC4] Verifica la gestione di una capacity driver nulla
      Given vengono puliti i dati dalle tabelle target
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
      And viene avviata la step function DelayerToPaperChannelStateMachine
      And verifica che non esistano notifiche al workflow step "SENT_TO_PREPARE_PHASE_2" per il seed "tcZeroDriver_"
      And verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      And verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                | TOT |
        | "tcZeroDriver.csv" | 15  |


    @delayer5
      #BUG: https://pagopa.atlassian.net/browse/PN-16324
    Scenario Outline: [DELAYER-TC5] Verifica la gestione di province e cap non censiti
      Given vengono puliti i dati dalle tabelle target
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
      And viene avviata la step function DelayerToPaperChannelStateMachine
      And verifica che non esistano notifiche al workflow step "SENT_TO_PREPARE_PHASE_2" per il seed "tcProvCapNonCensite_"
      And verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      And verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                       | TOT |
        | "tcProvCapNonCensite.csv" | 15  |

    @delayer7
    Scenario Outline: [DELAYER-TC7] Verifica che la seconda step function, una volta raggiunta la capacità di stampa settimanale, non processi ulteriori spedizioni
      Given vengono puliti i dati dalle tabelle target
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed                   | quantita |
        | tcWeeklyPrintCapacity_ | 9        |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId     | comparative | limit |
        | unknow~RS~P8 | esattamente | 0     |
        | unknow~AR~P8 | esattamente | 0     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId  | comparative | limit |
        | infinityDriverP8~P8      | almeno      | 9     |
        | infinityDriverP8~CAP1_P8 | almeno      | 9     |
      And si verifica che la capacità disponibile settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId  | comparative | limit |
        | infinityDriverP8~P8      | almeno      | 9     |
        | infinityDriverP8~CAP1_P8 | almeno      | 9     |
      And viene impostata la capacità di stampa settimanale in modo che sia esattamente 7
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
      And vengono avviate le 9 esecuzioni della step function DelayerToPaperChannelStateMachine
      Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      And verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                         | TOT |
        | "tcWeeklyPrintCapacity.csv" | 9   |


    @delayer9
    Scenario Outline: [DELAYER-TC9] Verifica che la StepFunction sia in grado di gestire correttamente più di 5000 spedizioni.
      Given vengono puliti i dati dalle tabelle target
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed            | quantita |
        | tcSenderUnknow_ | 5010     |
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
      And viene impostato il limite massimo di 10588 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
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
        | csv                       | TOT  |
        | "tcSenderUnknow_5010.csv" | 5010 |


    # Per il driver: zeroDriver è stata modificata la capacity a 10 per il periodo 2025-12-29T00:00:00.000Z - 2026-01-04T23:59:59.999Z
    # si verifica che la capacity ritornata per quella settiamana sia esattamente quella attesa: 10
    @delayer10
    Scenario Outline: [DELAYER-TC10] A seguito di un aggiornamento della capacity per il driver: zeroDriver verifica che la capacità ritornata sia esattamente quella attesa: 10.
      Given vengono puliti i dati dalle tabelle target
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed          | quantita | deliveryWeek |
        | tcZeroDriver_ | 15       | 2025-12-29   |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId       | comparative | limit |
        | unknow~RS~P10  | esattamente | 0     |
        | unknow~AR~P10  | esattamente | 0     |
        | unknow~890~P10 | esattamente | 0     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | zeroDriverP10~P10       | esattamente | 10    |
        | zeroDriverP10~CAP1_P10  | esattamente | 10    |
      And si verifica che il limite settimanale utilizzato dai recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | zeroDriverP10~P10       | esattamente | 10    |
        | zeroDriverP10~CAP1_P10  | esattamente | 10    |
      Examples:
        | csv                | TOT |
        | "tcZeroDriver.csv" | 15  |

    # Per il driver: zeroDriver è stata modificata la capacity a 10 per il periodo 2025-12-29T00:00:00.000Z - 2026-01-04T23:59:59.999Z
    # si verifica che la capacity ritornata per una settimana diversa da quella modificata precedentemente sia quella di default: 0.
    @delayer11
    Scenario Outline: [DELAYER-TC4.B] Verifica la gestione di una capacity driver nulla
      Given vengono puliti i dati dalle tabelle target
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
      Examples:
        | csv                | TOT |
        | "tcZeroDriver.csv" | 15  |

    @delayer12
    #La capacità di recapito di un driver non cambia se la spedizione viene annullata prima che essa venga pianificata
    #Esempio:
    #- martedì 2 dicembre viene inviata al delayer la spedizione (la PREPARE)
    #- il delayer valuterà la spedizione lunedì 8 dicembre
    #- venerdì 5 dicembre viene cancellata la notifica
    #- l'algoritmo non pianificherà la spedizione
    Scenario: [DELAYER-TC12] Viene verificata che la capacità di recapito di un driver non cambi in caso di spedizione annullata prima della pianificazione
      Given vengono puliti i dati dalle tabelle target
      Given viene generata una nuova notifica
        | subject               | invio notifica con cucumber |
        | senderDenomination    | Comune di milano            |
        | physicalCommunication | AR_REGISTERED_LETTER        |
      And destinatario
        | denomination                 | Test digitale ok |
        | taxId                        | DVNLRD52D15M059P |
        | digitalDomicile              | NULL             |
        | physicalAddress_municipality | Napoli           |
        | physicalAddress_province     | NA               |
        | physicalAddress_zip          | 80124            |
      When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
      Given il CSV "notificationCancelled.csv" contiene 1 notifiche distribuite tra i seguenti test case:
        | seed                     | quantita | deliveryWeek |
        | tcNotificationCancelled_ | 1        | NEXT_MONDAY  |
      Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"
      And la notifica può essere annullata dal sistema tramite codice IUN
      And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
#      si procede con annullamento notifica
      And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | Fulmine~NA              | almeno      | 1     |
        | Fulmine~80124           | almeno      | 1     |
      And si verifica che la capacità disponibile settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | Fulmine~NA              | almeno      | 1     |
        | Fulmine~80124           | almeno      | 1     |
      And viene impostato il limite massimo di 180000 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
      And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
      When viene avviata la step function BatchWorkflowStateMachine con deliveryDate in avanti di 1 settimane
      And viene verificata che la capacità disponibile per i seguenti driver sia decrementata di: 0
        | unifiedDeliveryDriverId |
        | Fulmine~80124           |

    @delayer13
    #La capacità di recapito di un driver non cambia se la spedizione viene congelata e poi successivamente annullata ma comunque prima che essa venga pianificata
    #Esempio:
    #- martedì 2 dicembre viene inviata al delayer la spedizione (la PREPARE)
    #- lunedì 8 dicembre l'algoritmo posticipa la pianificazione della spedizione al lunedì successivo (15 dicembre)
    #- perché non c'è capacità di recapito e/o di stampa
    #- martedì 16 dicembre viene cancellata la notifica
    #- l'algoritmo non pianificherà la spedizione (e di conseguenza non consumerà capacità di recapito e di stampa)
    Scenario: [DELAYER-TC13] Viene verificata che la capacità di recapito di un driver non cambi in caso di spedizione congelata ma annullata prima della pianificazione
      Given vengono puliti i dati dalle tabelle target
      Given il CSV "tcCancelNotificationFrozen.csv" contiene 13 notifiche distribuite tra i seguenti test case:
        | seed                        | quantita | deliveryWeek |
        | tcCancelNotificationFrozen_ | 13       | NEXT_MONDAY  |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId    | comparative | limit |
        | Poste~NA    | almeno      | 1     |
        | Poste~80125 | almeno      | 1     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | Poste~NA                | almeno      | 1     |
        | Poste~80125             | almeno      | 1     |
      And si verifica che la capacità disponibile settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | Poste~NA                | almeno      | 1     |
        | Poste~80125             | almeno      | 1     |
      Given viene generata una nuova notifica
        | subject            | invio notifica con cucumber |
        | senderDenomination | Comune di milano            |
      And destinatario
        | denomination                 | Test digitale ok  |
        | taxId                        | DVNLRD52D15M059P  |
        | digitalDomicile              | NULL              |
        | physicalAddress_address      | Via@OK_AR_BLOCKED |
        | physicalAddress_municipality | Napoli            |
        | physicalAddress_province     | NA                |
        | physicalAddress_zip          | 80125             |
      When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
      And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
      And viene impostato il limite massimo di 180000 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
      And il CSV "tcCancelNotificationFrozen.csv" è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
      When viene avviata la step function BatchWorkflowStateMachine con deliveryDate in avanti di 1 settimane
      #si procede con annullamento notifica
      And la notifica può essere annullata dal sistema tramite codice IUN
      And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
      And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
      When viene avviata la step function BatchWorkflowStateMachine con deliveryDate in avanti di 2 settimane
      And imposto la deliveryWeek in avanti di 2 settimane
      And viene verificata che la capacità utilizzata per i seguenti driver sia uguale a: 3
        | unifiedDeliveryDriverId |
        | Poste~80125             |

    @delayer14
    Scenario Outline: [DELAYER-TC14] Verifica che la pulizia delle tabelle target rimuova completamente i dati di test
      Given vengono puliti i dati dalle tabelle target
      And il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed          | quantita | deliveryWeek |
        | tcZeroDriver_ | 15       | 2025-12-29   |
      And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      When vengono puliti i dati dalle tabelle target
      Then non devono esistere record in pn-DelayerPaperDelivery per la deliveryDate "2025-12-29"
      And non deve esistere capacità usata alla deliveryDate "2025-12-29"
        | unifiedDeliveryDriverId |
        | Poste~80125             |
      And non devono esistere contatori per la deliveryDate "2025-12-29"
      And non devono esistere limiti mittente per la deliveryDate "2025-12-29" e pk "unknow~RS~P10"
      Examples:
        | csv                | TOT |
        | "tcZeroDriver.csv" | 15  |

    @delayer15
    Scenario: [DELAYER-TC15] Verifica riordinamento per senderPriority e fairness globale tra PA
      Given vengono puliti i dati dalle tabelle target
      Given il CSV "tcSenderPriority.csv" contiene 10 notifiche distribuite tra i seguenti test case:
        | seed              | quantita |
        | tcSenderPriority_ | 10       |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId              | comparative | limit |
        | ranking2nd_890~890~P1 | esattamente | 7     |
        | rankingRS_2nd~890~P2  | esattamente | 0     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId      | comparative | limit |
        | driverRanking2nd_890~P1      | esattamente | 10    |
        | driverRanking2nd_890~CAP1_P1 | esattamente | 10    |
        | driverRankingRS_2nd~P2       | esattamente | 10    |
        | driverRankingRS_2nd~CAP1_P2  | esattamente | 10    |
      And si verifica che la capacità disponibile settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId      | comparative | limit |
        | driverRanking2nd_890~P1      | esattamente | 10    |
        | driverRanking2nd_890~CAP1_P1 | esattamente | 10    |
        | driverRankingRS_2nd~P2       | esattamente | 10    |
        | driverRankingRS_2nd~CAP1_P2  | esattamente | 10    |
      And viene impostato il limite massimo di 20 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
#      And si presuppone che la capacità di stampa giornaliera sia esattamente 10
      And il CSV "tcSenderPriority.csv" è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
      When viene avviata la step function BatchWorkflowStateMachine
      And vengono recuperate le notifiche al workflow step "EVALUATE_SENDER_LIMIT"
      And verifica che il processo fino al workflow step "EVALUATE_SENDER_LIMIT" abbia rispettato i criteri di ranking per almeno un test case:
        | seed              |
        | tcSenderPriority_ |

    @delayer16
    Scenario: [DELAYER-TC16] Verifica che priorità 100 della settimana successiva preceda priorità 80 congelata dalla settimana precedente
      Given vengono puliti i dati dalle tabelle target
      # simulo una notifica posticipata con priorità 80
      Given il CSV "tcSenderPriorityFrozenW1.csv" contiene 8 notifiche distribuite tra i seguenti test case:
        | seed                    | quantita |
        | tcSenderPriorityFrozen_ | 8        |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId              | comparative | limit |
        | ranking2nd_890~890~P1 | esattamente | 7     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId      | comparative | limit |
        | driverRanking2nd_890~P1      | esattamente | 10    |
        | driverRanking2nd_890~CAP1_P1 | esattamente | 10    |
      And si verifica che la capacità disponibile settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId      | comparative | limit |
        | driverRanking2nd_890~P1      | esattamente | 10    |
        | driverRanking2nd_890~CAP1_P1 | esattamente | 10    |
      And si presuppone che la capacità di stampa giornaliera sia esattamente 1
      And il CSV "tcSenderPriorityFrozenW1.csv" è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
      When viene avviata la step function BatchWorkflowStateMachine
      And vengono recuperate le notifiche al workflow step "EVALUATE_PRINT_CAPACITY"
      And verifica che il processo fino al workflow step "EVALUATE_PRINT_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
        | seed                      |
        | tcSenderPriorityFrozenW1_ |
      Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      And vengono simulate internamente le operazioni di DelayerToPaperChannelStateMachine
      And vengono avviate le 1 esecuzioni della step function DelayerToPaperChannelStateMachine
      And verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case

      # la notifica posticipata viene inserita nel csv nella settimana successiva
      And sposto la simulazione in avanti di 1 settimane
      #TODO: viene ricaricata la notifica tcSenderPriorityFrozen_18 forzata come "frozen" dallo step precedente. Lo stesso effetto lo si puo ottenere facendo eseguire la seconda step function tarata per bene con la capacità di stampa oppure inviando 11 notifiche. O, meglio ancora, questa esecuzione puo essere riassunta inserendo nel csv iniziale una notifica con la sender priority desiderata e assumere che rappresenti una rimandata dalla settimana precedente a questa corrente.
      #TODO: nota bene, la sender priority è uguale per tutte le notifiche dunque stiamo testando l'as-is: l'ordinamento per i timestamp. Di fatto questo scenario è coperto meglio da quasliasi altro test.
      Given il CSV "tcSenderPriorityFrozenW2.csv" contiene 4 notifiche distribuite tra i seguenti test case:
        | seed                    | quantita | deliveryWeek |
        | tcSenderPriorityFrozen_ | 5        | NEXT_MONDAY  |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId              | comparative | limit |
        | ranking2nd_890~890~P1 | esattamente | 7     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId      | comparative | limit |
        | driverRanking2nd_890~P1      | esattamente | 10    |
        | driverRanking2nd_890~CAP1_P1 | esattamente | 10    |
      And si presuppone che la capacità di stampa giornaliera sia esattamente 1
      And il CSV "tcSenderPriorityFrozenW2.csv" è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
      When viene avviata la step function BatchWorkflowStateMachine con deliveryDate in avanti di 1 settimane
      And vengono recuperate le notifiche al workflow step "EVALUATE_SENDER_LIMIT"
      And verifica che il processo fino al workflow step "EVALUATE_SENDER_LIMIT" abbia rispettato i criteri di ranking per almeno un test case:
        | seed                    |
        | tcSenderPriorityFrozen_ |

    @delayer17
    Scenario Outline: [DELAYER-TC17] Verifica che priorità 100 della settimana successiva preceda priorità 80 congelata dalla settimana precedente
      Given vengono puliti i dati dalle tabelle target
      And il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed                    | quantita |
        | tcSenderPriorityFrozen_ | <TOT>    |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId              | comparative | limit |
        | ranking2nd_890~890~P1 | esattamente | 7     |
      And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId      | comparative | limit |
        | driverRanking2nd_890~P1      | esattamente | 10    |
        | driverRanking2nd_890~CAP1_P1 | esattamente | 10    |
      And si verifica che la capacità disponibile settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
        | unifiedDeliveryDriverId      | comparative | limit |
        | driverRanking2nd_890~P1      | esattamente | 10    |
        | driverRanking2nd_890~CAP1_P1 | esattamente | 10    |
      And viene impostato il limite massimo di 5 spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine
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
      #TODO: decommentare quando su test di risolve il problema dei limiti mittenti (non vengono applicati correttamente, andiamo sempre in RESIDUAL)
      #And vengono recuperate le notifiche al workflow step "EVALUATE_DRIVER_CAPACITY"
      #And verifica che il processo fino al workflow step "EVALUATE_DRIVER_CAPACITY" abbia rispettato i criteri di ranking per almeno un test case:
      #  | categoria         | ordinamentoCampo   |
      #  | RS                | prepareRequestDate |
      #  | SECONDO_TENTATIVO | prepareRequestDate |
      #  | ALTRO             | notificationSentAt |
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
        | csv                             | TOT |
        | "tcSenderPriorityFrozenW12.csv" | 11  |