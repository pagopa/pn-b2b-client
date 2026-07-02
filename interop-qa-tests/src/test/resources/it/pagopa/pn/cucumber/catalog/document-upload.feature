@document @document_upload
Feature: Caricamento di un documento di interfaccia
  Tutti gli utenti autorizzati di enti erogatori possono caricare un documento di interfaccia ai propri descrittori

  @nrt-minimal
  @document_upload1
  Scenario Outline: [DESCRIPTOR_UPLOAD_1] Per un e-service che eroga con una determinata tecnologia e che ha un solo descrittore, il quale è in uno dei sequenti stati: (PUBLISHED, DRAFT, DEPRECATED, SUSPENDED), alla richiesta di caricamento di un documento di interfaccia coerente con la tecnologia, da parte di un utente autorizzato, l'operazione avrà successo solo per lo stato DRAFT, altrimenti restituirà errore.
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in stato "<statoDescrittore>" e tecnologia "REST"
    When l'utente carica un documento di interfaccia di tipo "yaml"
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | statoDescrittore | risultato |
      | GSP  | admin        | DRAFT            |       200 |
      | GSP  | api          | DRAFT            |       200 |
      | GSP  | api,security | DRAFT            |       200 |
      | PA1  | admin        | DRAFT            |       200 |
      | PA1  | api          | DRAFT            |       200 |
      | PA1  | api,security | DRAFT            |       200 |

    @sad-path
    Examples:
      | ente | ruolo        | statoDescrittore | risultato |
      | GSP  | security     | DRAFT            |       403 |
      | GSP  | support      | DRAFT            |       403 |
      | PA1  | security     | DRAFT            |       403 |
      | PA1  | support      | DRAFT            |       403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | statoDescrittore | risultato |
      | GSP  | reviewer     | DRAFT            |       403 |
      | GSP  | viewer       | DRAFT            |       403 |
      | PA2  | reviewer     | DRAFT            |       403 |
      | PA2  | viewer       | DRAFT            |       403 |

    @sad-path
    Examples: # Test sugli stati
      | ente | ruolo | statoDescrittore | risultato |
      | PA1  | admin | PUBLISHED        |       409 |
      | PA1  | admin | SUSPENDED        |       409 |
      | PA1  | admin | DEPRECATED       |       409 |
      | PA1  | admin | ARCHIVED         |       409 |

  @nrt-minimal
  @document_upload2
  Scenario Outline: [DESCRIPTOR_UPLOAD_2] Per un e-service che eroga con una determinata tecnologia e che ha un solo descrittore, il quale è in stato DRAFT, alla richiesta di caricamento di un documento di interfaccia coerente con la tecnologia, da parte di un utente autorizzato, l'operazione avrà successo altrimenti restituirà errore.
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT" e tecnologia "<technology>"
    When l'utente carica un documento di interfaccia di tipo "<tipoFile>"
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | technology | tipoFile | risultato |
      | REST       | yaml     |       200 |
      | REST       | json     |       200 |
      | SOAP       | wsdl     |       200 |
      | SOAP       | xml      |       200 |

    @sad-path
    Examples:
      | technology | tipoFile | risultato |
      | REST       | wsdl     |       400 |
      | REST       | xml      |       400 |
      | SOAP       | yaml     |       400 |
      | SOAP       | json     |       400 |

  @sad-path
  @nrt-minimal
  @document_upload3
  Scenario Outline: [DESCRIPTOR_UPLOAD_3] Per un e-service che eroga con una determinata tecnologia e che ha un solo descrittore, il quale è in stato DRAFT, alla richiesta di caricamento di un documento di interfaccia coerente con la tecnologia, ma contenente il termine localhost, l'operazione restituirà errore.
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT" e tecnologia "<technology>"
    When l'utente carica un documento di interfaccia di tipo "<tipoFile>" che contiene il termine localhost
    Then si ottiene status code 403

    Examples:
      | technology | tipoFile |
      | REST       | yaml     |
      | REST       | json     |
      | SOAP       | wsdl     |
      | SOAP       | xml      |

  @sad-path
  @nrt-minimal
  @document_upload4
  Scenario: [DESCRIPTOR_UPLOAD_4] Per un e-service che ha un solo descrittore, il quale è in stato DRAFT, e per il quale è già stato caricato un documento di interfaccia, alla richiesta di caricamento di un nuovo documento di interfaccia, l’operazione restituirà errore.
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    Given "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'utente carica un documento di interfaccia di tipo "yaml"
    Then si ottiene status code 409

  @sad-path
  @nrt-minimal
  @document_upload5
  Scenario: [DESCRIPTOR_UPLOAD_5] Per un e-service che ha un solo descrittore, il quale è in stato DRAFT, e per il quale è già stato caricato un documento, alla richiesta di caricamento di un nuovo documento con lo stesso nome, l’operazione restituirà errore.
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    Given "PA1" ha già caricato un documento con nome "test" in quel descrittore
    When l'utente carica un documento con nome "test" in quel descrittore
    Then si ottiene status code 409

  @invalid-yaml
  Scenario: [DESCRIPTOR_UPLOAD_6] Per un e-service che ha un solo descrittore, il quale è in stato DRAFT, e per il quale viene caricato un documento di interfaccia senza versione, l’operazione restituirà errore.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente carica un documento di interfaccia di tipo YAML "senza versione"
    Then si ottiene status code 400

  @invalid-yaml
  Scenario: [DESCRIPTOR_UPLOAD_7] Per un e-service che ha un solo descrittore, il quale è in stato DRAFT, e per il quale viene caricato un documento di interfaccia senza versione, l’operazione restituirà errore.
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente carica un documento di interfaccia di tipo YAML "con versione obsoleta"
    Then si ottiene status code 400

  # PIN-9920 PST 2.1 - serverUrls description handling in interface documents
  @pin-9920
  @happy-path
  Scenario: [PIN-9920 PST 2.1] Creazione e-service con interfaccia REST contenente serverUrls con description
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT" e tecnologia "REST"
    When l'utente carica un'interfaccia "REST" con serverUrls che contengono descrizione
    Then si ottiene status code 200

  @pin-9920
  @happy-path
  Scenario: [PIN-9920 PST 2.1] Creazione e-service con interfaccia REST contenente serverUrls senza description
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT" e tecnologia "REST"
    When l'utente carica un'interfaccia "REST" con serverUrls senza descrizione
    Then si ottiene status code 200

  @pin-9920
  @sad-path
  Scenario: [PIN-9920 PST 2.1] Creazione e-service con interfaccia REST con serverUrls array vuoto
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT" e tecnologia "REST"
    When l'utente carica un'interfaccia "REST" con serverUrls array vuoto
    Then si ottiene status code 400

  @pin-9920
  @sad-path
  Scenario: [PIN-9920 PST 2.1] Creazione e-service con interfaccia REST senza serverUrls
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT" e tecnologia "REST"
    When l'utente carica un'interfaccia "REST" senza serverUrls
    Then si ottiene status code 400
