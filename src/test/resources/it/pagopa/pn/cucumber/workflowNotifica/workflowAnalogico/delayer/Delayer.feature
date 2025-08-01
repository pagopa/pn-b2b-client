  # Gli elementi usciti dalla PREPARE fase 1 alla settimana W vengono inseriti nella pn-DelayerPaperDelivey con la deliveryDate che punta a W+1
  # Dal punto di vista del test siamo nel POV della valutazione, quindi è come se le notifiche fossero state caricate in tabella la settimana scorsa (W)
  # con la deliveryDate alla W+1(corrente) e ora, settimana W+1 le stiamo valutando
  Feature: Gestione notifiche tramite algoritmo del microservizio ritardatore e Lambda di test

    Scenario Outline: Test di prova
      Given il CSV <csv> contiene <TOT> notifiche cosi distribuite:
        | categoria              | quantita            |
        | RS                     | <RS>                |
        | SECONDO_TENTATIVO      | <SECONDO_TENTATIVO> |
        | ALTRO                  | <ALTRO>             |
        | RECAPITISTI_UTILIZZATI | 1                   |
        | MITTENTI_UTILIZZATI    | 1                   |
      And si presuppone che il limite mittente settimanale (paId-product_type-province-deliveryDate) sia esattamente <senderLimit>
      And si presuppone che il limite recapitista unificato settimanale (unifiedDeliveryDriver-provincia-deliveryDate) sia almeno <driverCapacity>
      And si presuppone che la capacità di stampa giornaliera sia esattamente <printCapacity>
      And il processo valutato fino al workflow step "EVALUATE_DRIVER_CAPACITY" ha rispettato i criteri di ranking:
        | categoria         | ordinamentoCampo   |
        | RS                | prepareRequestDate |
        | SECONDO_TENTATIVO | prepareRequestDate |
        | ALTRO             | notificationSentAt |

      Examples:
        | csv                     | RS | SECONDO_TENTATIVO | ALTRO | TOT | senderLimit | driverCapacity | printCapacity | nEvaluateSenderLimit | nEvaluateDriverCapacity | nEvaluatePrintCapacity | nSentPhase2 | nCongelate |
        | "tc01_priorita_2nd.csv" | 0  | 5                 | 5     | 10  | 10          | 8              | 180000        | 10                   | 10                      | 8                      | 8           | 2          |


    @delayer
    Scenario Outline: [DELAYER-TC01] Le notifiche sono pianificate secondo i criteri di ranking
      Given il CSV <csv> contiene <TOT> notifiche cosi distribuite:
        | categoria              | quantita            |
        | RS                     | <RS>                |
        | SECONDO_TENTATIVO      | <SECONDO_TENTATIVO> |
        | ALTRO                  | <ALTRO>             |
        | RECAPITISTI_UTILIZZATI | 1                   |
        | MITTENTI_UTILIZZATI    | 1                   |
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



