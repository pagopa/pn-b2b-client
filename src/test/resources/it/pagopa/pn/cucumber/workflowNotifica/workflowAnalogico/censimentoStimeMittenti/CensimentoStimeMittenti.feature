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

  Scenario: [SM_] Verifica il calcolo delle stime settimanali provinciali a partire dai dati delle stime mensili regionali
#    Given vengono caricate le stime di tutto l'anno dei mittenti che hanno spedito alla regione "LAZIO"
    And vengono applicati localmente i seguenti moduli commessa per la provincia "P1":
      | classpath:/t0_tc_modulo_commessa_gennaio_26.json |
      | classpath:/t0_tc_modulo_commessa_febbraio_26.json |
      | classpath:/t0_tc_modulo_commessa_marzo_26.json  |
    When vengono recuperate le stime mittenti da "01-2026" a "03-2026" per la provincia "P1"
    Then si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti per la provincia "P1"


  Scenario: [SM_04] Verifica del calcolo della percentuale garantita al mittente
#    Given viene avviata la step function BatchWorkflowStateMachine con deliveryDate: "2026-01-05"
#    And vengono recuperate le somme delle stime mittenti per la deliveryDate: "2026-01-05"
    And viene recuperato il limite percentuale garantito per la deliveryDate: "2026-01-05"



