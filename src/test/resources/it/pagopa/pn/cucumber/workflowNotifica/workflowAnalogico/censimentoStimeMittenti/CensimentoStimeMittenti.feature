Feature: Censimento stime mittenti

  Scenario:[UNIT_TEST_1] Test degli stepper
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/t0_tc_modulo_commessa_gennaio_25.json  |
      | classpath:/t0_tc_modulo_commessa_febbraio_25.json |
    And si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti per la provincia "P1"

  @censimentoStimeMittenti1
  Scenario: [SM_01] Verifica la gestione del caricamento delle commesse per il calcolo delle stime mittenti
    Given viene caricato su SafeStorage il documento "classpath:/t0_tc_modulo_commessa_gennaio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And viene caricato su SafeStorage il documento "classpath:/t0_tc_modulo_commessa_febbraio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/t0_tc_modulo_commessa_gennaio_25.json  |
      | classpath:/t0_tc_modulo_commessa_febbraio_25.json |
    And si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti per la provincia "P1"
    When viene caricato su SafeStorage il documento "classpath:/t1_tc_modulo_commessa_febbraio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/t0_tc_modulo_commessa_gennaio_25.json  |
      | classpath:/t1_tc_modulo_commessa_febbraio_25.json |
    And si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti per la provincia "P1"
    And viene caricato su SafeStorage il documento "classpath:/t1_tc_modulo_commessa_gennaio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/t1_tc_modulo_commessa_gennaio_25.json  |
      | classpath:/t1_tc_modulo_commessa_febbraio_25.json |
    Then si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti per la provincia "P1"

  @controlloCalcoloStimeMittenti
  Scenario: [SM_02] Verifica il calcolo delle stime settimanali provinciali a partire dai dati delle stime mensili regionali
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_primo_trimestre_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/t0_tc_modulo_commessa_gennaio_26.json  |
      | classpath:/t0_tc_modulo_commessa_febbraio_26.json |
      | classpath:/t0_tc_modulo_commessa_marzo_26.json    |
    When vengono recuperate le stime mittenti da "01-2026" a "03-2026" per la provincia "P1"
    Then si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti per la provincia "P1"

  @controlloCalcoloStimeMittenti
  Scenario Outline: [SM_03] Verifica del calcolo della percentuale garantita al mittente
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_primo_trimestre_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/t0_tc_modulo_commessa_gennaio_26.json  |
      | classpath:/t0_tc_modulo_commessa_febbraio_26.json |
      | classpath:/t0_tc_modulo_commessa_marzo_26.json    |
    When vengono recuperate le stime mittenti da "01-2026" a "03-2026" per la provincia "P1"
    Given vengono puliti i dati dalle tabelle target
    Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
      | seed                 | quantita | deliveryWeek |
      | tcRanking_2nd_890_   | 16       | 2026-02-09   |
      | tcRanking_RS_2nd_    | 14       |              |
      | tcRanking_RS_890_    | 16       |              |
      | tcRanking_RS_        | 14       |              |
      | tcRanking_2nd_       | 14       |              |
      | tcRanking_890_       | 16       |              |
      | tcRankingRS_2nd_890_ | 20       |              |
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
    And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
    And vengono simulate internamente le operazioni di BatchWorkflowStateMachine
    And viene avviata la step function BatchWorkflowStateMachine con deliveryDate: "2026-02-09"
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
    And viene verificato il limite garantito per la pa: "ranking2nd_890" relativo a provincia: "P1", prodotto: "890" e deliveryDate: "2026-02-09"
    And viene verificato il limite garantito per la pa: "ranking890" relativo a provincia: "P6", prodotto: "890" e deliveryDate: "2026-02-09"

    Examples:
      | csv                   | TOT |
      | "tcRankingMerged.csv" | 110 |



