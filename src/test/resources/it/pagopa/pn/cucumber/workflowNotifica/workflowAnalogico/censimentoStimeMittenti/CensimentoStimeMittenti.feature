Feature: Censimento stime mittenti


  Scenario: [SM_01] Verifica il calcolo delle settimane a cavallo tra due mesi
    Given viene caricato su SafeStorage il documento "classpath:/t0_modulo_commessa_gennaio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And viene caricato su SafeStorage il documento "classpath:/t0_commessa_gennaio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P0":
      | path                                          |
      | classpath:/t0_modulo_commessa_gennaio_25.json |
      | classpath:/t0_commessa_gennaio_25.json        |
    And si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti per la provincia "P0"
    When viene caricato su SafeStorage il documento "classpath:/modulo_commessa_gennaio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P0":
      | path                                       |
      | classpath:/modulo_commessa_gennaio_25.json |
    And si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti per la provincia "P0"
    And viene caricato su SafeStorage il documento "classpath:/modulo_commessa_febbraio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P0":
      | path                                        |
      | classpath:/modulo_commessa_febbraio_25.json |
    And si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti per la provincia "P0"
