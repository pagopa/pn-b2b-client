@delayerMockTables
Feature: Delayer — pulizia tabelle mock del Simulatore tramite DELETE_MOCK_TABLES

  @delayerMockTables1
  Scenario: [DELAYER-MOCK-TABLES-1] Verifica che DELETE_MOCK_TABLES rimuova completamente i dati dalle tabelle mock
    Given vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_aprile_26.zip"
    And vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_maggio_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_ranking2nd_890_mock_aprile.json |
    And si verifica che la tabella pn-PaperDeliverySenderLimitMock contenga i nuovi limiti mittenti per la provincia "P1"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse mock sia:
      | numberOfShipments           | 46 |
      | firstWeekNumberOfShipments  | 27 |
      | secondWeekNumberOfShipments | 19 |
    When vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES
    Then non devono esistere limiti mittente mock per la deliveryDate "2026-04-27" e la provincia "P1"
    And non devono esistere contatori mock per la deliveryDate "2026-04-27", la provincia "P1" e il prodotto "890"
    And si verifica che la tabella pn-PaperDeliverySenderLimitMock non contenga i nuovi limiti mittenti per la provincia "P1"

  @delayerMockTables2
  Scenario: [DELAYER-MOCK-TABLES-2] Verifica che le tabelle mock ricreate siano nuovamente utilizzabili dal Simulatore con la configurazione attesa
    Given vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES
    And non devono esistere limiti mittente mock per la deliveryDate "2026-04-27" e la provincia "P1"
    When vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_aprile_26.zip"
    And vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_maggio_26.zip"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/modulo_commessa_ranking2nd_890_mock_aprile.json |
    Then si verifica che la tabella pn-PaperDeliverySenderLimitMock contenga i nuovi limiti mittenti per la provincia "P1"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse mock sia:
      | numberOfShipments           | 46 |
      | firstWeekNumberOfShipments  | 27 |
      | secondWeekNumberOfShipments | 19 |

  @delayerMockTables3
  Scenario: [DELAYER-MOCK-TABLES-3] Verifica che DELETE_MOCK_TABLES invocata su tabelle mock già vuote completi correttamente il ciclo di delete/create
    Given vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES
    And non devono esistere limiti mittente mock per la deliveryDate "2026-04-27" e la provincia "P1"
    When vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES
    Then non devono esistere limiti mittente mock per la deliveryDate "2026-04-27" e la provincia "P1"
    And non devono esistere contatori mock per la deliveryDate "2026-04-27", la provincia "P1" e il prodotto "890"

  @delayerMockTables4
  Scenario: [DELAYER-MOCK-TABLES-4] Verifica che DELETE_MOCK_TABLES non modifichi le tabelle reali
    Given vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_aprile_26.zip"
    And vengono caricati i moduli commessa come file zip su portfat: "portfatt_modulo_commessa_maggio_26.zip"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse sia:
      | numberOfShipments           | 7 |
      | firstWeekNumberOfShipments  | 4 |
      | secondWeekNumberOfShipments | 3 |
    And vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_aprile_26.zip"
    And vengono caricati i moduli commessa mock tramite il seguente zip: "portfatt_modulo_commessa_mock_maggio_26.zip"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse mock sia:
      | numberOfShipments           | 46 |
      | firstWeekNumberOfShipments  | 27 |
      | secondWeekNumberOfShipments | 19 |
    When vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES
    Then non devono esistere contatori mock per la deliveryDate "2026-04-27", la provincia "P1" e il prodotto "890"
    And per la settimana "2026-04-27", per il prodotto "890" per la provincia "P1" si verifica che la somma delle commesse sia:
      | numberOfShipments           | 7 |
      | firstWeekNumberOfShipments  | 4 |
      | secondWeekNumberOfShipments | 3 |
