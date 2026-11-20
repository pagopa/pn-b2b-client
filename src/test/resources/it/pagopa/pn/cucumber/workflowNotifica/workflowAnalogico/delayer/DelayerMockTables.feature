@delayerMockTables
Feature: Delayer — pulizia tabelle mock del Simulatore tramite DELETE_MOCK_TABLES

  # Il popolamento delle sei tabelle mock coinvolte da DELETE_MOCK_TABLES avviene tramite:
  # - INSERT_MOCK_SENDER_LIMITS -> pn-PaperDeliverySenderLimitMock, pn-PaperDeliveryCountersMock
  # - INSERT_MOCK_CAPACITIES    -> pn-PaperDeliveryDriverCapacitiesMock
  # - IMPORT_DATA (mock)        -> pn-DelayerPaperDeliveryMock
  # - RUN_ALGORITHM (mock)      -> pn-PaperDeliveryDriverUsedCapacitiesMock, pn-PaperDeliveryUsedSenderLimitMock
  # I limiti mittente utilizzati sono scritti alla settimana precedente la deliveryDate (cfr. checkSenderLimitForPA)
  # Lo svuotamento è verificato sull'intera tabella mock (Scan DynamoDB con Limit=1, costo fisso), non su una singola deliveryDate/provincia

  @delayerMockTables @work-item:QA-18551
  Scenario: [DELAYER-MOCK-TABLES-1] Verifica che DELETE_MOCK_TABLES rimuova completamente i dati dalle tabelle mock
    Si popolano tutte e sei le tabelle mock del Simulatore (limiti mittente, contatori, capacità dichiarate,
    spedizioni, capacità utilizzate e limiti mittente utilizzati) e se ne verifica la presenza dei dati.
    Dopo l'invocazione di DELETE_MOCK_TABLES si verifica che nessuna delle sei tabelle contenga più alcun record.

    Given vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_aprile_26.zip"
    And vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_maggio_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_ranking2nd_890_mock_aprile.json |
    And si verifica che la tabella pn-PaperDeliverySenderLimitMock contenga i nuovi limiti mittenti per la provincia "P1"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse mock sia:
      | numberOfShipments           | 46 |
      | firstWeekNumberOfShipments  | 27 |
      | secondWeekNumberOfShipments | 19 |
    And vengono caricate le capacità dei recapitisti mock tramite il file "Capacity_v2.csv"
    And il CSV "tcSenderPriority.csv" è importato da S3 nella pn-DelayerPaperDeliveryMock tramite lambda di test con deliveryWeek "2026-04-27"
    And viene avviata la step function BatchWorkflowStateMachine sulle tabelle mock con deliveryDate "2026-04-27" e capacità di stampa giornaliera 10
    And devono esistere capacità dichiarate mock per la deliveryDate "2026-04-27" e la provincia "P1"
    And devono esistere spedizioni mock in pn-DelayerPaperDeliveryMock per la deliveryDate "2026-04-27"
    And devono esistere capacità utilizzate mock per la deliveryDate "2026-04-27", il recapitista "infinityDriverP1" e la geokey "P1"
    And devono esistere limiti mittente utilizzati mock per la deliveryDate "2026-04-20" e la provincia "P1"
    When vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES
    Then la tabella "pn-PaperDeliverySenderLimitMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryCountersMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryDriverCapacitiesMock" non contiene alcun record
    And la tabella "pn-DelayerPaperDeliveryMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryDriverUsedCapacitiesMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryUsedSenderLimitMock" non contiene alcun record

  @delayerMockTables @work-item:QA-18551
  Scenario: [DELAYER-MOCK-TABLES-2] Verifica che le tabelle mock ricreate siano nuovamente utilizzabili dal Simulatore con la configurazione attesa
    Si parte da tabelle mock appena eliminate e ricreate tramite DELETE_MOCK_TABLES.
    Si riesegue il flusso completo del Simulatore (INSERT_MOCK_SENDER_LIMITS, INSERT_MOCK_CAPACITIES, IMPORT_DATA e RUN_ALGORITHM)
    e si verifica che le tabelle ricreate vengano popolate correttamente, con gli stessi risultati attesi su tabelle mai cancellate.

    Given vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES
    And la tabella "pn-PaperDeliverySenderLimitMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryCountersMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryDriverCapacitiesMock" non contiene alcun record
    And la tabella "pn-DelayerPaperDeliveryMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryDriverUsedCapacitiesMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryUsedSenderLimitMock" non contiene alcun record
    And la tabella "pn-PaperDeliverySenderLimitMock" non contiene alcun record
    When vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_aprile_26.zip"
    And vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_maggio_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_ranking2nd_890_mock_aprile.json |
    And vengono caricate le capacità dei recapitisti mock tramite il file "Capacity_v2.csv"
    And il CSV "tcSenderPriority.csv" è importato da S3 nella pn-DelayerPaperDeliveryMock tramite lambda di test con deliveryWeek "2026-04-27"
    And viene avviata la step function BatchWorkflowStateMachine sulle tabelle mock con deliveryDate "2026-04-27" e capacità di stampa giornaliera 10
    Then si verifica che la tabella pn-PaperDeliverySenderLimitMock contenga i nuovi limiti mittenti per la provincia "P1"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse mock sia:
      | numberOfShipments           | 46 |
      | firstWeekNumberOfShipments  | 27 |
      | secondWeekNumberOfShipments | 19 |
    And devono esistere capacità dichiarate mock per la deliveryDate "2026-04-27" e la provincia "P1"
    And devono esistere spedizioni mock in pn-DelayerPaperDeliveryMock per la deliveryDate "2026-04-27"
    And devono esistere capacità utilizzate mock per la deliveryDate "2026-04-27", il recapitista "infinityDriverP1" e la geokey "P1"
    And devono esistere limiti mittente utilizzati mock per la deliveryDate "2026-04-20" e la provincia "P1"

  @delayerMockTables @work-item:QA-18551
  Scenario: [DELAYER-MOCK-TABLES-3] Verifica che DELETE_MOCK_TABLES invocata su tabelle mock già vuote completi correttamente il ciclo di delete/create
    Si invoca DELETE_MOCK_TABLES due volte di seguito: la seconda invocazione avviene su tabelle mock già vuote.
    Si verifica che il ciclo di delete/create venga completato senza errori e che nessuna delle sei tabelle mock contenga alcun record.

    Given vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES
    And la tabella "pn-PaperDeliverySenderLimitMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryCountersMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryDriverCapacitiesMock" non contiene alcun record
    And la tabella "pn-DelayerPaperDeliveryMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryDriverUsedCapacitiesMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryUsedSenderLimitMock" non contiene alcun record
    And la tabella "pn-PaperDeliverySenderLimitMock" non contiene alcun record
    When vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES
    Then la tabella "pn-PaperDeliverySenderLimitMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryCountersMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryDriverCapacitiesMock" non contiene alcun record
    And la tabella "pn-DelayerPaperDeliveryMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryDriverUsedCapacitiesMock" non contiene alcun record
    And la tabella "pn-PaperDeliveryUsedSenderLimitMock" non contiene alcun record

  @delayerMockTables @work-item:QA-18551
  Scenario: [DELAYER-MOCK-TABLES-4] Verifica che DELETE_MOCK_TABLES non modifichi le tabelle reali
    Si caricano per la stessa settimana e provincia sia i moduli commessa reali (7 spedizioni 890 su P1)
    sia quelli mock (46 spedizioni 890 su P1), e si importa un CSV di spedizioni sulle tabelle reali tramite IMPORT_DATA
    così da popolare anche pn-DelayerPaperDelivery e pn-PaperDeliveryUsedSenderLimit.
    Prima di DELETE_MOCK_TABLES si conteggiano i record delle sei tabelle reali speculari a quelle mock
    (pn-PaperDeliverySenderLimit, pn-PaperDeliveryCounters, pn-PaperDeliveryDriverCapacities, pn-DelayerPaperDelivery,
    pn-PaperDeliveryDriverUsedCapacities, pn-PaperDeliveryUsedSenderLimit).
    Dopo DELETE_MOCK_TABLES si verifica che i contatori mock siano stati rimossi, che i contatori reali restino invariati
    e che per ciascuna delle sei tabelle reali il numero di record sia uguale o maggiore a quello conteggiato prima,
    a conferma che l'operazione agisce esclusivamente sulle tabelle *Mock.

    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_aprile_26.zip"
    And vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_maggio_26.zip"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse sia:
      | numberOfShipments           | 7 |
      | firstWeekNumberOfShipments  | 4 |
      | secondWeekNumberOfShipments | 3 |
    And il CSV "tcSenderPriority.csv" è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test con deliveryWeek "2026-04-27"
    And vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_aprile_26.zip"
    And vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_maggio_26.zip"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse mock sia:
      | numberOfShipments           | 46 |
      | firstWeekNumberOfShipments  | 27 |
      | secondWeekNumberOfShipments | 19 |
    And vengono conteggiati i record delle tabelle reali per la deliveryDate "2026-04-27", la provincia "P1", il prodotto "890" e il recapitista "infinityDriverP1"
    When vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES
    Then la tabella "pn-PaperDeliveryCountersMock" non contiene alcun record
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse sia:
      | numberOfShipments           | 7 |
      | firstWeekNumberOfShipments  | 4 |
      | secondWeekNumberOfShipments | 3 |
    And il numero di record delle tabelle reali per la deliveryDate "2026-04-27", la provincia "P1", il prodotto "890" e il recapitista "infinityDriverP1" è uguale o maggiore a quello conteggiato prima di DELETE_MOCK_TABLES
