Feature: Censimento stime mittenti

  Scenario:[UNIT_TEST_1] Test degli stepper
    And vengono recuperate le stime mittenti da "1-2025" a "2-2025" per la provincia "P1"


  Scenario: [SM_01] Verifica il calcolo delle settimane a cavallo tra due mesi
    Given viene caricato su SafeStorage il documento "classpath:/t0_modulo_commessa_gennaio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And viene caricato su SafeStorage il documento "classpath:/t0_commessa_gennaio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/t0_modulo_commessa_gennaio_25.json |
      | classpath:/t0_commessa_gennaio_25.json        |
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
