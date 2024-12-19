Feature: Creazione di una delega e gestione delle richieste di fruizione

  Background:
    Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe

  Scenario: [TC_CAPOFILA_33] La creazione di una delega in erogazione NON può essere compiuto da un utente ADMIN se l’aderente non si è reso disponibile ad accettare deleghe
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato e pubblicato 1 e-service
    When l'utente richiede la creazione di una delega per l'ente "PA1"
    Then si ottiene status code 403

  #TC-35: Delegato da la disponibilità a ricevere deleghe
  #TC-55: Delegato può rifiutare una finalità in stato pending
  #TC-59: Delegato NON può sospendere finalità in stato pending
  Scenario: [TC_CAPOFILA_35_55_59] Un delegato all’erogazione che gestisce finalità per conto del delegante può rifiutare e non sospendere una finalità in stato pending
    Given l'utente è un "admin" di "PA2"
    Given "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata creata correttamente
    And l'ente "PA2" accetta la delega
    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "GSP" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    When l'utente sospende quella finalità in stato "WAITING_FOR_APPROVAL"
    Then si ottiene status code 400
    When l'utente rifiuta la finalità aggiungendo una motivazione
    Then si ottiene status code 200

  #TC-58: Delegato attiva finalità in stato pending
  #TC-59: Delegato NON può sospendere finalità in stato pending
  #TC-60: Delegato può sospendere una finalità ATTIVA
  #TC-61: Delegato NON può rifiutare una finalità ATTIVA
  Scenario Outline: [TC_CAPOFILA_58_59_60_61] Un delegato all’erogazione che gestisce finalità per conto del delegante può attivare/sospendere finalità in pending o attive
    Given l'utente è un "admin" di "PA2"
    Given "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata creata correttamente
    And l'ente "PA2" accetta la delega
    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "GSP" ha già creato 1 finalità in stato "<finalizationState>" per quell'eservice
    # Attivazione finalità (200 solo se finalità in PENDING)
    When l'utente attiva la finalità in stato "<finalizationState>" per quell'e-service
    Then si ottiene status code <activationStatusCode>
    # Rifiuto finalità (200 solo se finalità in PENDING)
    When l'utente rifiuta la finalità aggiungendo una motivazione
    Then si ottiene status code 400
    # Sospensione finalità (200 solo se finalità in ACTIVE)
    When l'utente sospende quella finalità in stato "<finalizationState>"
    Then si ottiene status code 200
    Examples:
      | finalizationState                 | activationStatusCode |
      | ACTIVE                            | 403                  |
      | WAITING_FOR_APPROVAL              | 200                  |

  #TC-22: Delegante può revocare una delega in stato attivo
  #TC-23: Delegato NON può revocare una delega in stato attivo
  #TC-37: Delegato da la disponibilità a ricevere deleghe
  #TC-66: Delegato accetta richiesta di fruizione e-service
  Scenario: [TC_CAPOFILA_22_23_37_66] Un delegato all'erogazione accetta la richieste di fruizione di un e-service per conto di un delegante
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata creata correttamente
    And l'ente "PA2" accetta la delega
    Given "GSP" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    And "PA2" ha già approvato quella richiesta di fruizione
    #revoca di una delega in stato attivo da parte del delegato
    And l'ente "PA2" con ruolo "admin" revoca la delega
    Then si ottiene status code 403
    #revoca di una delega in stato attivo da parte del delegante
    And l'ente "PA1" con ruolo "admin" revoca la delega
    Then si ottiene status code 200

  Scenario: [TC_CAPOFILA_67] Un delegato all'erogazione accetta la richieste di fruizione di un e-service per conto di un delegante
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata creata correttamente
    And l'ente "PA2" accetta la delega
    Given "GSP" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    And "PA2" ha già rifiutato quella richiesta di fruizione

