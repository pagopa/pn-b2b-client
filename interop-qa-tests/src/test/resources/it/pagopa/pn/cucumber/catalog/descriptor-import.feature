@descriptor
Feature: Import di un descrittore
  Tutti gli utenti autorizzati possono effettuare una richiesta di import di un descrittore di un e-service.
  Il controllo sui documenti da caricare e se il nome dell'eservice è già presente sono stati tralasciati in quanto già testati nei relativi endpoint dedicati

  @happy-path
  @nrt-minimal
  @descriptor_import1 @no-parallel
  Scenario Outline: [DESCRIPTOR_IMPORT_1] La richiesta di import di un descrittore di un e-service da parte di un utente autorizzato, dato un pacchetto correttamente strutturato, contenente due documenti correttamente mappati nel file di configurazione, va a buon fine e il descrittore viene correttamente creato in stato DRAFT con quei documenti
    Given l'utente è un "<ruolo>" di "<ente>"
    Given l'utente ha già un pacchetto correttamente strutturato con un eservice in mode "DELIVER"
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore
    Then si ottiene status code 200
    And il descrittore viene correttamente creato in stato DRAFT
    And i due documenti risultano correttamente caricati

    Examples:
      | ente | ruolo        |
      | PA1  | admin        |
      | PA1  | api          |
      | PA1  | api,security |
      | GSP  | admin        |
      | GSP  | api          |
      | GSP  | api,security |

  @happy-path @no-parallel
  Scenario Outline: [DESCRIPTOR_IMPORT_1_B1] La richiesta di import di un descrittore di un e-service da parte di un utente autorizzato, dato un pacchetto correttamente strutturato, contenente due documenti correttamente mappati nel file di configurazione, con nome dell'archivio e della main directory non coincidenti, va a buon fine e il descrittore viene correttamente creato in stato DRAFT con quei documenti
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già un pacchetto correttamente strutturato con un eservice <sincronia> in mode "DELIVER"
    Given il nome del pacchetto viene modificato in "nome_pacchetto_non_coincidente.zip"
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore
    Then si ottiene status code 200
    And il descrittore viene correttamente creato in stato DRAFT
    And i due documenti risultano correttamente caricati

    Examples:
      | sincronia |
      | sincrono  |

    # 28/08/2026 Al momento non riproducibile a causa di un'anomalia, rif. https://pagopaspa.slack.com/archives/C069AP16WG7/p1787912475573199
#      | asincrono |

  @happy-path @no-parallel
  Scenario Outline: [DESCRIPTOR_IMPORT_1_B2] La richiesta di import di un descrittore di un e-service da parte di un utente autorizzato, dato un pacchetto correttamente strutturato, contenente due documenti correttamente mappati nel file di configurazione, con nome dell'archivio e della main directory non coincidenti, va a buon fine e il descrittore viene correttamente creato in stato DRAFT con quei documenti
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già un pacchetto correttamente strutturato con un eservice <sincronia> in mode "RECEIVE"
    Given il nome del pacchetto viene modificato in "nome_pacchetto_non_coincidente.zip"
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore
    Then si ottiene status code 200
    And il descrittore viene correttamente creato in stato DRAFT
    And l'eservice contiene l'analisi del rischio

    Examples:
      | sincronia |
      | sincrono  |

    # 28/08/2026 Al momento non riproducibile a causa di un anomalia, rif. https://pagopaspa.slack.com/archives/C069AP16WG7/p1787912475573199
#      | asincrono |

  @sad-path
  @nrt-minimal
  @descriptor_import2 @no-parallel
  Scenario Outline: [DESCRIPTOR_IMPORT_2] La richiesta di import di un descrittore di un e-service da parte di un utente non autorizzato, dato un pacchetto correttamente strutturato, contenente due documenti correttamente mappati nel file di configurazione, non va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given l'utente ha già un pacchetto correttamente strutturato con un eservice in mode "DELIVER"
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore
    Then si ottiene status code 403

    Examples:
      | ente | ruolo    |
      | PA1  | security |
      | PA1  | support  |
      | GSP  | security |
      | GSP  | support  |

  @happy-path
  @nrt-minimal
  @descriptor_import3
  Scenario: [DESCRIPTOR_IMPORT_3] La richiesta di import di un descrittore di un e-service in erogazione inversa, dato un pacchetto correttamente strutturato, va a buon fine e il descrittore viene correttamente creato in stato DRAFT con l’analisi del rischio fornita dal pacchetto
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già un pacchetto correttamente strutturato con un eservice in mode "RECEIVE"
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore
    Then si ottiene status code 200
    And il descrittore viene correttamente creato in stato DRAFT
    And l'eservice contiene l'analisi del rischio

  @nrt-minimal
  @descriptor_import3b
  Scenario: [DESCRIPTOR_IMPORT_3_B] La richiesta di import di un descrittore di un e-service in erogazione inversa, dato un pacchetto con specificata una versione di risk analysis obsoleta, si conclude con esito negativo
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già un pacchetto con un eservice in mode RECEIVE ed una risk analysis obsoleta
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @descriptor_import4 @no-parallel
  Scenario: [DESCRIPTOR_IMPORT_4] La richiesta di import di un descrittore di un e-service, dato un pacchetto con il file di configurazione correttamente formattato ma con il nome del file errato, non va a buon fine.
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già un pacchetto correttamente strutturato con un eservice in mode "DELIVER"
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore con nome del file errato
    Then si ottiene status code 500

  @sad-path
  @nrt-minimal
  @descriptor_import5
  Scenario: [DESCRIPTOR_IMPORT_5] La richiesta di import di un descrittore di un e-service, dato un pacchetto con il file di configurazione non correttamente formattato (campi richiesti mancanti o json non valido), non va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già un pacchetto non correttamente strutturato con campi richiesti mancanti
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @descriptor_import6 @no-parallel
  Scenario: [DESCRIPTOR_IMPORT_6] La richiesta di import di un descrittore di un e-service, dato un pacchetto con il file di configurazione correttamente formattato ma contenente documenti (o file di interfaccia) che non esistono nel percorso previsto, non va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già un pacchetto non correttamente strutturato con documenti mancanti nel percorso previsto
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore
    Then si ottiene status code 400

  @sad-path
  @nrt-minimal
  @descriptor_import7
  Scenario: [DESCRIPTOR_IMPORT_7] La richiesta di import di un descrittore di un e-service, dato un pacchetto con il file di configurazione correttamente formattato, ma con file in cartella non previsti all’interno del file di configurazione, non va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già un pacchetto non correttamente strutturato con file non previsti
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore
    Then si ottiene status code 400

  @happy-path @no-parallel
  Scenario Outline: [DESCRIPTOR_IMPORT_8] La richiesta di import di un descrittore di un e-service da parte di un utente autorizzato NON va a buon fine se questo contiene un numero di cartelle diverso da 1
    Given l'utente è un "admin" di "PA1"
    Given l'utente ha già un pacchetto correttamente strutturato con un eservice <sincronia> in mode "<modalita>"
    Given il contenuto del pacchetto viene modificato così che contenga <numero_cartelle> cartelle correttamente create
    Given l'utente ha già richiesto una presignedURL per il caricamento del pacchetto
    Given è già stato caricato il pacchetto nella presignedURL
    When l'utente effettua una richiesta di import del descrittore
    Then si ottiene status code 400

    Examples:
      | sincronia | modalita | numero_cartelle |
      | sincrono  | DELIVER  | 0               |
      | sincrono  | DELIVER  | 2               |
      | sincrono  | RECEIVE  | 0               |
      | sincrono  | RECEIVE  | 2               |

  # 28/08/2026 Al momento non riproducibile a causa di un anomalia, rif. https://pagopaspa.slack.com/archives/C069AP16WG7/p1787912475573199
#      | asincrono | DELIVER  | 0               |
#      | asincrono | DELIVER  | 2               |
#      | asincrono | RECEIVE  | 0               |
#      | asincrono | RECEIVE  | 2               |

