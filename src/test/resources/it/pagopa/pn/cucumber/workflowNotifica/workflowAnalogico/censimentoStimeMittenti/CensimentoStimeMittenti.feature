Feature: Censimento stime mittenti

  Scenario:[UNIT_TEST_1] Test degli stepper
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/t0_tc_modulo_commessa_gennaio_25.json  |
      | classpath:/t0_tc_modulo_commessa_febbraio_25.json |
    And si verifica che la tabella pn-PaperDeliverySenderLimit contenga i nuovi limiti mittenti per la provincia "P1"

  @controlloCalcoloStimeMittenti
  Scenario: [SM_02] Verifica il calcolo delle stime settimanali provinciali a partire dai dati delle stime mensili regionali
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_primo_trimestre_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/t0_tc_modulo_commessa_gennaio_26.json  |
      | classpath:/t0_tc_modulo_commessa_febbraio_26.json |
      | classpath:/t0_tc_modulo_commessa_marzo_26.json    |
    When vengono recuperate le stime mittenti da "01-2026" a "03-2026" per la provincia "P1"
    Then si verifica che la tabella pn-PaperDeliverySenderLimit contenga i nuovi limiti mittenti per la provincia "P1"

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
      | tcRanking_2nd_890_   | 16       | 2026-02-02   |
      | tcRanking_RS_2nd_    | 14       |              |
      | tcRanking_RS_890_    | 16       |              |
      | tcRanking_RS_        | 14       |              |
      | tcRanking_2nd_       | 14       |              |
      | tcRanking_890_       | 16       |              |
      | tcRankingRS_2nd_890_ | 21       |              |
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
    And viene avviata la step function BatchWorkflowStateMachine con deliveryDate: "2026-02-02"
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
    And viene verificato che il limite garantito per la pa: "ranking2nd_890" relativo a provincia: "P1", prodotto: "890" sia corretto
    Examples:
      | csv                   | TOT |
      | "tcRankingMerged.csv" | 111 |


  @censimentoStimeMittenti
  Scenario: [TC_CENSIMENTO_SETTIMANA_CAVALLO] Verifica del calcolo delle stime settimanali a cavallo di due mesi e dell'aggiornamento delle stime a seguito di modifiche sui moduli commessa
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_aprile_26.zip"
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_maggio_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_ranking2nd_890.json |
    Then si verifica che la tabella pn-PaperDeliverySenderLimit contenga i nuovi limiti mittenti per la provincia "P1"
#    And si verifica se la settimana del: "2026-04-27" è interamente compresa all'interno del mese
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse sia:
      | numberOfShipments           | 7 |
      | firstWeekNumberOfShipments  | 4 |
      | secondWeekNumberOfShipments | 3 |
    #Viene caricato nuovamente il modulo commesse di aprile per verificare che per la settimana a cavallo venga cambiata soltanto
    #la stima della commessa relativa al mese di aprile e non quella di maggio
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_aprile_26_modified.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_P1_aprile_modified.json |
    Then si verifica che la tabella pn-PaperDeliverySenderLimit contenga i nuovi limiti mittenti per la provincia "P1"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse sia:
      | numberOfShipments           | 15 |
      | firstWeekNumberOfShipments  | 12 |
      # secondWeekNumberOfShipments rimane invariato in quanto la modifica è stata fatta solo sul modulo commessa di APRILE
      | secondWeekNumberOfShipments | 3  |
    #Viene caricato nuovamente il modulo commesse di maggio per verificare che per la settimana a cavallo venga cambiata soltanto
    #la stima della commessa relativa al mese di maggio e non quella di aprile
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_maggio_26_modified.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_P1_maggio_modified.json |
    Then si verifica che la tabella pn-PaperDeliverySenderLimit contenga i nuovi limiti mittenti per la provincia "P1"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse sia:
      | numberOfShipments           | 21 |
      # firstWeekNumberOfShipments rimane invariato in quanto la modifica è stata fatta solo sul modulo commessa di MAGGIO
      | firstWeekNumberOfShipments  | 12 |
      | secondWeekNumberOfShipments | 9  |
    # Ripristino delle commesse originali
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_aprile_26.zip"
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_maggio_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_ranking2nd_890.json |
    Then si verifica che la tabella pn-PaperDeliverySenderLimit contenga i nuovi limiti mittenti per la provincia "P1"

  @censimentoStimeMittenti
  Scenario: [TC_CENSIMENTO_RICARICO_MESE_CAVALLO] Verifica che il ricaricamento di una commessa dello stesso mese aggiorni originalEstimate e monthlyEstimate anche per la settimana a cavallo con il mese successivo
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_giugno_26.zip"
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_luglio_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_P1_giugno.json |
      | classpath:/modulo_commessa_P1_luglio.json |
    Then si verifica che la tabella pn-PaperDeliverySenderLimit contenga i nuovi limiti mittenti per la provincia "P1"
    #La settimana "2026-06-29" è a cavallo tra giugno (2 giorni: 29-30) e luglio (5 giorni: 01-05): essendo il lunedì
    #di riferimento compreso in giugno, monthlyEstimate e originalEstimate di quella riga devono riportare i valori
    #della commessa di GIUGNO, mentre weeklyEstimate somma il contributo di entrambi i mesi
    #Viene ricaricata la commessa di giugno con stime diverse per lo stesso ente: weeklyEstimate, monthlyEstimate e
    #originalEstimate della settimana a cavallo devono aggiornarsi prendendo il valore della nuova commessa di giugno,
    #mentre la parte di luglio non deve subire variazioni
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_giugno_26_modified.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_P1_giugno_modified.json |
      | classpath:/modulo_commessa_P1_luglio.json          |
    Then si verifica che la tabella pn-PaperDeliverySenderLimit contenga i nuovi limiti mittenti per la provincia "P1"

  @censimentoStimeMittenti
  Scenario: [TC_CENSIMENTO_STIME_MOCK_1] Verifica che il caricamento di moduli commessa MOCK non influisca sulle tabelle reali
    #Carichi una commessa reale e ne verifichi il corretto caricamento con i giusti limiti
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_aprile_26.zip"
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_maggio_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_ranking2nd_890.json |
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse sia:
      | numberOfShipments           | 7 |
      | firstWeekNumberOfShipments  | 4 |
      | secondWeekNumberOfShipments | 3 |
    Then si verifica che la tabella pn-PaperDeliverySenderLimit contenga i nuovi limiti mittenti per la provincia "P1"
    #Carico una commessa MOCK e ne verifico il corretto caricamento con i giusti limiti soltanto nella tabella MOCK e non in quella reale
    Given vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_aprile_26.zip"
    Given vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_maggio_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_ranking2nd_890_mock_aprile.json |
    Then si verifica che la tabella pn-PaperDeliverySenderLimitMock contenga i nuovi limiti mittenti per la provincia "P1"
    Then si verifica che la tabella pn-PaperDeliverySenderLimit non contenga i nuovi limiti mittenti per la provincia "P1"
    Then per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse mock sia:
      | numberOfShipments           | 46 |
      | firstWeekNumberOfShipments  | 27 |
      | secondWeekNumberOfShipments | 19 |
    #Verifico che la tabella reale non sia stata modificata dal caricamento della commessa MOCK
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse sia:
      | numberOfShipments           | 7 |
      | firstWeekNumberOfShipments  | 4 |
      | secondWeekNumberOfShipments | 3 |

