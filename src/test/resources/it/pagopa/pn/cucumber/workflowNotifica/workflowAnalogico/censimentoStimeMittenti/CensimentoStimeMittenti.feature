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

  Scenario: [SM_02] Verifica il calcolo delle stime settimanali provinciali a partire dai dati delle stime mensili regionali
    Given vengono caricate le stime di tutto l'anno dei mittenti che hanno spedito alla regione "LAZIO"
    When viene recuperata la stima della settimana intera del primo mese che inizia di lunedì
    Then si verifica che la stima recupera corrisponda alla stima attesa
    When viene recuperata la stima della settimana a cavallo del primo mese che non inizia di lunedì
    Then si verifica che la stima recupera corrisponda alla stima attesa