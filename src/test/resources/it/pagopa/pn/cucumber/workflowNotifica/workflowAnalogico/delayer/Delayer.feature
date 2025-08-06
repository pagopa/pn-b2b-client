  # Gli elementi usciti dalla PREPARE fase 1 alla settimana W vengono inseriti nella pn-DelayerPaperDelivey con la deliveryDate che punta a W+1
  # Dal punto di vista del test siamo nel POV della valutazione, quindi è come se le notifiche fossero state caricate in tabella la settimana scorsa (W)
  # con la deliveryDate alla W+1(corrente) e ora, settimana W+1 le stiamo valutando
  Feature: Gestione notifiche tramite algoritmo del microservizio ritardatore e Lambda di test

    Scenario Outline: prova metodo
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed            | quantita |
        | tcPriorityRs2nd | 30       |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId          | comparative | limit |
        | senderPaId1~RS~RM | esattamente | 15    |
        | senderPaId1~AR~RM | esattamente | 15    |
      And si verifica che il limite recapitista unificato settimanale (unifiedDeliveryDriver-province) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | Poste~RM                | almeno      | 30    |
      And si presuppone che la capacità di stampa giornaliera sia esattamente 180000
      #And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
      And viene simulato internamente l'algoritmo di pianificazione
      #When viene avviato l'algoritmo tramite lambda
      #And vengono recuperate le notifiche al workflow step "EVALUATE_SENDER_LIMIT"
      #And vengono recuperate le notifiche al workflow step "EVALUATE_DRIVER_CAPACITY"
      #And vengono recuperate le notifiche al workflow step "EVALUATE_PRINT_CAPACITY"
      #And vengono recuperate le notifiche al workflow step "SENT_TO_PREPARE_PHASE_2"
      And verifica che il processo fino al workflow step "SENT_TO_PREPARE_PHASE_2" abbia rispettato i criteri di ranking per almeno un test case:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |
      #Then verifica che le opportune notifiche siano state congelate e ricaricate con workflow step "EVALUATE_SENDER_LIMIT" e deliveryDate alla settimana seguente per almeno un test case
      Then verifica la corretta pianificazione di ogni test case

      Examples:
        | csv                        | TOT |
        | "tc01_priorita_rs_2nd.csv" | 30  |


    Scenario Outline: [REFACTORING] Refactor in un solo csv
      Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
        | seed              | quantita |
        | tcPriority2nd     | 30       |
        | tcPriorityRs2nd   | 30       |
        | tcPriorityRsAltr  | 30       |
        | tcPriorityRsInt   | 30       |
        | tcPriority2ndInt  | 30       |
        | tcPriorityAltrInt | 30       |
        | tcPriorityAll     | 30       |
      And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
        | senderId           | comparative | limit |
        | senderPaId1~AR~P1  | esattamente | 15    |
        | senderPaId1~890~P1 | esattamente | 15    |
        | senderPaId2~RS~P2  | esattamente | 15    |
        | senderPaId2~AR~P2  | esattamente | 15    |
        | senderPaId3~RS~P3  | esattamente | 15    |
        | senderPaId3~890~P3 | esattamente | 15    |
        | senderPaId4~RS~P4  | esattamente | 30    |
        | senderPaId5~AR~P5  | esattamente | 30    |
        | senderPaId6~890~P6 | esattamente | 30    |
        | senderPaId7~RS~P7  | esattamente | 10    |
        | senderPaId7~AR~P7  | esattamente | 10    |
        | senderPaId7~890~P7 | esattamente | 10    |
      And si verifica che il limite recapitista unificato settimanale (unifiedDeliveryDriver-province) sia:
        | unifiedDeliveryDriverId | comparative | limit |
        | Driver1~RM              | esattamente | 20    |
        | Driver2~RM              | esattamente | 20    |
        | Driver3~RM              | esattamente | 20    |
        | Driver4~RM              | esattamente | 20    |
        | Driver5~RM              | esattamente | 20    |
        | Driver6~RM              | esattamente | 20    |
        | Driver7~RM              | almeno      | 30    |
      And si presuppone che la capacità di stampa giornaliera sia esattamente 180000
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
        | csv              | TOT |
        | "tc01_total.csv" | 210 |


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



