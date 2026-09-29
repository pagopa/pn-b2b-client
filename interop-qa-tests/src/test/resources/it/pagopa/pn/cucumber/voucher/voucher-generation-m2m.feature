@voucher
Feature: Generazione del voucher m2m richiesta da un Ente

  @voucher_generation_m2m1
  Scenario: [VOUCHER_GENERATION_M2M_1] La generazione del JWT va a buon fine quando i parametri sono validi
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "API"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When l'utente richiede la generazione del voucher M2M
    Then si ottiene la corretta generazione del voucher

  # NOTE 27/05/2025: non presente nel parco test originale in TS. E' correlato all' SRS
  # Amministratore Client API https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1627324417/SRS+Amministratore+Client+API
  # nel quale è stata aggiunta la facoltà di specificare un amministratore del client, con
  # conseguente aggiunta del ruolo "m2m-admin" all'interno del voucher qualora la sua generazione
  # venisse effettuata dall'amministratore del client.
  @voucher_generation_m2m1_admin
  Scenario: [VOUCHER-M2M-ADMIN-1] La generazione del JWT per un client admin va a buon fine quando i parametri sono validi
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "API"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given l'utente effettua la modifica dell'amministratore del client indicando se stesso con successo
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When l'utente richiede la generazione del voucher M2M
    Then si ottiene la corretta generazione del voucher m2m admin

  @voucher_generation_m2m2
  Scenario: [VOUCHER_GENERATION_M2M_2] La generazione del JWT va a buon fine quando viene aggiunta una nuova chiave al client
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "API"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When l'utente richiede la generazione del voucher M2M
    Then si ottiene la corretta generazione del voucher

  @voucher_generation_m2m3
  Scenario: [VOUCHER_GENERATION_M2M_3] La generazione del JWT va a buon fine quando viene rimossa una chiave dal client
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "API"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    Given un "admin" di "PA1" ha aggiunto una nuova chiave pubblica al client
    Given "PA1" rimuove quella nuova chiave dal client 
    When l'utente richiede la generazione del voucher M2M
    Then si ottiene la corretta generazione del voucher

@voucher_generation_m2m4
  Scenario: [VOUCHER_GENERATION_M2M_4] La generazione del JWT fallisce quando l’unica chiave presente viene rimossa dal client
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "API"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    Given "PA1" rimuove quella chiave dal client 
    When l'utente richiede la generazione del voucher M2M
    Then si ottiene status code 400

@voucher_generation_m2m5
Scenario: [VOUCHER_GENERATION_M2M_5] La generazione del JWT fallisce quando una delle chiavi nel client viene rimossa
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "API"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    Given un "admin" di "PA1" ha aggiunto una nuova chiave pubblica al client
    Given "PA1" rimuove quella chiave dal client
    When l'utente richiede la generazione del voucher M2M
    Then si ottiene status code 400

@voucher_generation_m2m6
Scenario: [VOUCHER_GENERATION_M2M_6] La generazione del JWT fallisce quando la chiave non è associata a un client
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "API"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When l'utente richiede la generazione del voucher M2M con una chiave associata a nessun client
    Then si ottiene status code 400

@voucher_generation_m2m7
Scenario: [VOUCHER_GENERATION_M2M_7] La generazione del JWT fallisce quando la chiave non è associata al client richiesto
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "API"
    Given "PA1" ha già creato 1 nuovo client "API"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel nuovo client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel nuovo client
    When l'utente richiede la generazione del voucher M2M indicando il primo client ma con la chiave caricata nel secondo
    Then si ottiene status code 400

@voucher_generation_m2m8
Scenario: [VOUCHER_GENERATION_M2M_8] La generazione del JWT fallisce quando il client viene cancellato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "API"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    Given "PA1" cancella quel client
    When l'utente richiede la generazione del voucher M2M
    Then si ottiene status code 400

