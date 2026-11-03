Feature: Censimento stime mittenti


#**************** SCENARIO 1 MESE SPECIFICATO *****************

# 	  	 x= marzo_20 (1.1A) MonthSent=2020_03   OrderMonth=2020_07
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_luglio_20.zip" anno 2020 mese 7

#		x= febbraio_20 (1.1B)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_marzo_20.zip" anno 2020 mese 3

# 		x= gennaio_20 (1.2A)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_febbraio_20.zip" anno 2020 mese 2

#		x= gennaio_20 (1.2A)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_maggio_20.zip" anno 2020 mese 5

#        x= novembre_20 (1.2C)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_dicembre_20.zip" anno 2020 mese 12

#		x= novembre_20 (1.2C)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_marzo_21.zip" anno 2021 mese 3

#        x= novembre_20 (1.2B)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_gennaio_21.zip" anno 2021 mese 1

#		x= novembre_20 (1.2B)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_aprile_21.zip" anno 2021 mese 4


#**************** SCENARIO 2 DUPLICATI *****************

#     x= aprile (2.1A) x= luglio (2.1B)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_agosto_20.zip" anno 2020 mese 8
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_agosto_20.zip" anno 2020 mese 8

#     x= luglio (2.1B)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_novembre_20.zip" anno 2020 mese 11


#     x= gennaio_23 (2.1C) MonthSent=2023_01  -   OrderMonth=2020_07
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_febbraio_23.zip" anno 2023 mese 3
#		MonthSent=2023_05
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_febbraio_23.zip" anno 2023 mese 3
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_maggio_23.zip" anno 2023 mese 5

#**************** SCENARIO 3 STATI DIVERSI DA COMPLETATO ********
#
#    	x = Febbraio 2022 (3.1A)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_giugno_22.zip" anno 2022 mese 6 #stato non completato.
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_marzo_22.zip" anno 2022 mese 3

#     	x = Marzo 2022 (3.1B)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_luglio_22.zip" anno 2022 mese 7
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_aprile_22.zip" anno 2022 mese 4 #stato non completato.

#   	X = Aprile 2022 (3.1C)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_maggio_22.zip" anno 2022 mese 5
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_agosto_22.zip" anno 2022 mese 8 #stato non completato.
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_agosto_22.zip" anno 2022 mese 8


#**************** SCENARIO 4 MESE CORRENTE ********

#     x= Novembre_26 mese corrente (4.1)
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_dicembre_26.zip" anno 2026 mese 12
Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_marzo_27.zip" anno 2026 mese 3

#     x= novembre_26 mese corrente (4.2)
#    	Eliminare "_dicembre_26.zip"
#    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_marzo_27.zip" anno 2026 mese 3


    # Non presenti: Aprile, Giugno, Ottobre 2020


# aprile n , Luglio s
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_01_1_A] Controllo su commessa con mese successivo mancante, esito negativo
    When eseguo la lambda sender order check per il mese "2020-03"
    Then il risultato del controllo è "KO"
    And le mensilità mancanti sono
      | X+1 (2026-04) |


# Marzo s , giugno n
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_01_1_B] Controllo su commessa con quarto mese successivo mancante, esito negativo
    When eseguo la lambda sender order check per il mese "2020-02"
    Then il risultato del controllo è "KO"
    And le mensilità mancanti sono
      | X+4 (2026-06) |


#giugno n , ottobre n
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_01_1_D] Controllo su commessa con entrambi i mesi mancanti, esito negativo
    When eseguo la lambda sender order check per il mese "2020-05"
    Then il risultato del controllo è "KO"
    And le mensilità mancanti sono
      | X+1 (2020-06) |
      | X+4 (2020-10) |



# Febbraio s, Maggio s
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_01_2_A] Controllo su commesse presenti, esito positivo
    When eseguo la lambda sender order check per il mese "2020-01"
    Then il risultato del controllo è "OK"



#se novembre -> dicembre s , marzo_27 s
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_01_2_C] Controllo su commessa con mese che ricade nell'anno successivo, esito positivo
    When eseguo la lambda sender order check per il mese "2020-11"
    Then il risultato del controllo è "OK"


#se dicembre -> gennaio s , aprile_27 s
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_01_2_B] Controllo su commessa con mese che ricade nell'anno successivo, esito positivo
    When eseguo la lambda sender order check per il mese "2020-12"
    Then il risultato del controllo è "OK"


#**********************************
# **** Duplicati
#**********************************


# maggio s , agosto x2
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_02_1_A] Controllo su commessa con mese successivo duplicato, esito positivo
    When eseguo la lambda sender order check per il mese "2026-04"
    Then il risultato del controllo è "OK"


# agosto x2  , novembre s
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_02_1_B] Controllo su commessa con quarto mese successivo duplicato, esito positivo
    When eseguo la lambda sender order check per il mese "2026-07"
    Then il risultato del controllo è "OK"


 # febbraio x2, maggio s
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_02_1_C] Controllo su commessa con mese successivo duplicato ma caricato in mese diverso, esito positivo
    When eseguo la lambda sender order check per il mese "2026-01"
    Then il risultato del controllo è "OK"


#**********************************************
# **** Mese corrente - nessun parametro in input
#***********************************************

  #se novembre -> dicembre s , marzo_27 s
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_04_1] Controllo su commessa senza specificare il mese, esito positivo
    When eseguo la lambda sender order check
    Then il risultato del controllo è "OK"


  #se novembre -> dicembre s , marzo_27 n
  @lambdaCheckPortFat @ignore
  Scenario: [CHECK_PORTFAT_04_2] Controllo su commessa senza specificare il mese, esito negativo
    When eseguo la lambda sender order check
    Then il risultato del controllo è "KO"


#**********************************
# **** Stati diversi da completato
#**********************************

  # febbaraio
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_03_1_A] Controllo su commessa con mese successivo mancante, esito negativo
    When eseguo la lambda sender order check per il mese "2022-02"
    Then il risultato del controllo è "KO"
    And le mensilità mancanti sono
      | X+1 (202x-06) |

  # marzo
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_03_1_B] Controllo su commessa con mese successivo mancante, esito negativo
    When eseguo la lambda sender order check per il mese "2022-03"
    Then il risultato del controllo è "KO"
    And le mensilità mancanti sono
      | X+1 (202x-04) |

  # aprile
  @lambdaCheckPortFat
  Scenario: [CHECK_PORTFAT_03_1_C] Controllo su commessa con mese successivo mancante, esito negativo
    When eseguo la lambda sender order check per il mese "2022-04"
    Then il risultato del controllo è "OK"


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

