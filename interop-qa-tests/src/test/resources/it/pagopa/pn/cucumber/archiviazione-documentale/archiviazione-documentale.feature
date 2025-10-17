Feature: Archiviazione documentale e verifica firma/marca temporale

  Background:
    Given il sistema è correttamente configurato
    And il bucket S3 di documenti "interop-application-documents-<envName>-es1" è accessibile
    And il bucket S3 WORM di documenti firmati "interop-signed-application-documents-<envName>-es1" è accessibile
    And il servizio SafeStorage è disponibile

  # TC001 - Attivazione richiesta di fruizione (puo essere considerato come sottoinsieme di TC002 e quindi unito)
  @tc001
  Scenario: Attivazione richiesta di fruizione - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then la richiesta di fruizione assume lo stato "ACTIVE"
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"

  # TC002 - Cambio stato richiesta di fruizione
  @tc002
 Scenario Outline: Cambio stato richiesta di fruizione - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "<tipoApprovazione>"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"

    Examples:
      | statoAgreement | tipoApprovazione |
      | PENDING        | MANUAL           |
      | ACTIVE         | AUTOMATIC        |
      | SUSPENDED      | AUTOMATIC        |
      | ARCHIVED       | AUTOMATIC        |

   # TC003 - Attivazione analisi del rischio di una finalità
  @tc003
  Scenario: Attivazione analisi del rischio - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato e pubblicato 1 e-service
    Given "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente scarica il documento di analisi del rischio
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"

  # TC004 - Sospensione analisi del rischio di una finalità
  @tc004
  Scenario: Sospensione analisi del rischio - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente sospende quella finalità in stato "ACTIVE"
    Then si ottiene status code 200
    And l'utente scarica il documento di analisi del rischio
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"

  # TC005 - Attivazione delega
  @tc005
  Scenario: Attivazione delega - archiviazione PDF firmato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And un utente dell'ente delegato con ruolo "admin"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    And l'ente "PA2" accetta la delega
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"

  # TC006 - Revoca delega
  @tc006
  Scenario: Revoca delega - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA2"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And l'utente accetta la delega
    Then si ottiene lo status code 200
    When l'ente "PA1" con ruolo "admin" revoca la delega
    Then si ottiene lo status code 200
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"

  # TC007 - Attivazione chiave pubblica
  @tc007
  Scenario: Attivazione chiave pubblica - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    When l'utente richiede il caricamento di una chiave pubblica di tipo "RSA"
    Then si ottiene status code 204
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"

  # TC008 - Cancellazione chiave pubblica
  @tc008
  Scenario: Cancellazione chiave pubblica - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When l'utente richiede una operazione di cancellazione della chiave di quel client
    Then si ottiene status code 204
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"

  # TC009 - Modifica e-service
  @tc009
  Scenario: Modifica versione del descrittore di un e-service - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già creato un e-service contenente anche il primo descrittore
    Given l'utente ha già pubblicato quel descrittore
    When l'utente crea una versione in bozza per quell'e-service
    Then si ottiene status code 200 e il descrittore contiene i campi del precedente
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"

  # TC010 - Cambio stato e-service
  @tc010
  Scenario Outline: Lettura descrittore di un e-service in tutti gli stati - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoVersione>"
    When l'utente richiede la lettura di quel descrittore
    Then si ottiene status code 200
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"

    Examples:
      | statoVersione |
      | DRAFT         |
      | PUBLISHED     |
      | SUSPENDED     |
      | DEPRECATED    |
      | ARCHIVED      |

  # TC011 - Generazione voucher e archiviazione JWT firmato
  @tc011
  Scenario: Generazione voucher - archiviazione JWT firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    Given "PA1" ha già creato 1 client "CONSUMER"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given "PA1" ha già associato la finalità a quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When l'utente richiede la generazione del voucher
    Then si ottiene la corretta generazione del voucher
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "pdf"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "pdf"
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 "interop-application-documents-<envName>-es1" l'esistenza del file unsigned "regex" con estensione "zip"
    And verifica nel bucket S3 WORM "interop-application-documents-<envName>-es1" l'esistenza del file signed "regex" con estensione "zip"


    # TC012 - Archiviazione degli eventi di piattaforma: SONO ASSOCIABILI AI TEST STANDARD

     # | tipoEvento                  |
     # | creazione chiavi            |
     # | cancellazione chiavi        |
     # | cancellazione client        |
     # | attivazione richiesta       |
     # | cambio stato richiesta      |
     # | attivazione finalità        |
     # | cambio stato finalità       |
     # | creazione descrittore       |
     # | cambio stato descrittore    |
     # | attivazione delega          |
     # | revoca delega               |