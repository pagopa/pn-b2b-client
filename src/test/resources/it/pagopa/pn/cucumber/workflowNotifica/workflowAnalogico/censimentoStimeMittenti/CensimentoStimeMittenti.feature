Feature: Censimento stime mittenti


  Scenario: [SM_01] Verifica il calcolo delle settimane a cavallo tra due mesi
    Given viene caricato su SafeStorage il documento "classpath:/t0_modulo_commessa_gennaio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And viene caricato su SafeStorage il documento "classpath:/t0_commessa_gennaio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And vengono recuperate le stime mittenti da "1-2025" a "2-2025" per la provincia "P0"
    When viene caricato su SafeStorage il documento "classpath:/modulo_commessa_gennaio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And viene applicato localmente il nuovo modulo commessa "classpath:/modulo_commessa_gennaio_25.json"
    And si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti
    And viene caricato su SafeStorage il documento "classpath:/modulo_commessa_febbraio_25.json" con contentType "application/json" di tipo "PN_SERVICE_ORDER" e status "SAVED"
    And si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti
