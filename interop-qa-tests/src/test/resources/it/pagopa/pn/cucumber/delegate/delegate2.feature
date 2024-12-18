Feature: Creazione di una delega

  Background:
    Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe

  Scenario: [TC-33] Il richiamo dell’API di creazione di una delega in erogazione NON possa essere compiuto da un utente di livello operatore amministrativo (admin) con “ruolo” di delegante se l’aderente non si è reso disponibile ad accettare deleghe
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha già creato e pubblicato 1 e-service
    When l'ente "PA2" richiede la creazione di una delega per l'ente "PA1"
    Then si ottiene status code 403

  #Scenario: [TC-35] NON APPLICABILE

  #Scenario: [TC-37] Un delegato all’erogazione che gestisce finalità per conto del delegante (flusso 7) possa rifiutare, richiamando l’apposita API, una richiesta di erogazione di una finalità in stato pending

  Scenario: [TC-55] Un delegato all’erogazione che gestisce finalità per conto del delegante (flusso 7) possa rifiutare, richiamando l’apposita API, una richiesta di erogazione di una finalità in stato pending
    Given l'utente è un "admin" di "PA2"
    Given "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata correttamente creata
    And l'ente "PA2" accetta la delega
    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "GSP" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    When l'utente rifiuta la finalità aggiungendo una motivazione
    Then si ottiene status code 200

  Scenario: [TC-58] Un delegato all’erogazione che gestisce finalità per conto del delegante (flusso 7) possa sospendere, richiamando l’apposita API, una richiesta di erogazione di una finalità in stato attiva
    Given l'utente è un "admin" di "PA2"
    Given "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata correttamente creata
    And l'ente "PA2" accetta la delega
    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "GSP" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    When l'utente attiva la finalità in stato "WAITING_FOR_APPROVAL" per quell'e-service
    Then si ottiene status code 200

  Scenario Outline: [TC-60-59] Un delegato all’erogazione che gestisce finalità per conto del delegante (flusso 7) possa sospendere, richiamando l’apposita API, una richiesta di erogazione di una finalità in stato attiva
    Given l'utente è un "admin" di "PA2"
    Given "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata correttamente creata
    And l'ente "PA2" accetta la delega
    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "GSP" ha già creato 1 finalità in stato "<state>" per quell'eservice
    When l'utente sospende quella finalità in stato "<state>"
    Then si ottiene status code <statusCode>

    Examples:
      | state                 | statusCode  |
      | ACTIVE                | 200         |
      | WAITING_FOR_APPROVAL  |  400        |

#TODO
#  Scenario: [TC-61] Un delegato all'erogazione accetta la richieste di fruizione di un e-service per conto di un delegante, richiamando la corretta API
#    Given l'utente è un "admin" di "PA1"
#    Given "PA1" ha già creato e pubblicato 1 e-service
#    And l'ente "PA2" concede la disponibilità a ricevere deleghe
#    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
#    And l'ente "PA2" accetta la delega
#
#    Given "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
#    Given "GSP" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
#    Given "PA2" ha già rifiutato l'aggiornamento della stima di carico per quella finalità

  Scenario: [TC-66] Un delegato all'erogazione accetta la richieste di fruizione di un e-service per conto di un delegante, richiamando la corretta API
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata correttamente creata
    And l'ente "PA2" accetta la delega
    Given "GSP" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    And "PA2" ha già approvato quella richiesta di fruizione
    #Test case 23: revoca di una delega in stato attivo da parte del delegato
    And l'ente "PA2" revoca la delega
    Then si ottiene status code 403
    #Test case 22: revoca di una delega in stato attivo da parte del delegante
    And l'ente "PA1" revoca la delega
    Then si ottiene status code 200

  Scenario: [TC-67] Un delegato all'erogazione accetta la richieste di fruizione di un e-service per conto di un delegante, richiamando la corretta API
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'utente richiede la creazione di una delega per l'ente "PA2"
    And la delega è stata correttamente creata
    And l'ente "PA2" accetta la delega
    Given "GSP" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    And "PA2" ha già rifiutato quella richiesta di fruizione

