#TODO: da chiarire se mancano i doc: RiskAnalysisDocumentAdded, AgreementContractDocumentAdded
Feature: Archiviazione documentale e verifica firma/marca temporale

  Scenario: [TRIGGER]
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    Then si ottiene status code 200

    Scenario: [GET_ALL]
      Given recupera gli ultimi 10 file nel bucket "interop-signed-application-documents-qa-es1/interop-qa-documents-signer/2025/12/11/"

  Scenario: [AGREEMENT_DOC_ARCHIVE_1] Attivazione richiesta di fruizione - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given l'utente è un "admin" di "PA2"
    Given "PA2" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente inoltra quella richiesta di fruizione
    Then la richiesta di fruizione assume lo stato "ACTIVE"
    And verifica che a fronte dell'evento AgreementActivated venga generato nell'opportuno bucket S3 STANDARD un AGREEMENT_CONTRACT_DOC
    And verifica che il file contenga le opportune informazioni
    And verifica che a fronte dell'evento AgreementActivated venga generato nell'opportuno bucket S3 WORM un AGREEMENT_CONTRACT_DOC
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica che il file contenga le opportune informazioni
    And verifica che a fronte dell'evento AgreementActivated venga generato nell'opportuno bucket S3 STANDARD un AGREEMENT_ACTIVATE_EVENTS_LOG
    And verifica che il file contenga le opportune informazioni
    And verifica che a fronte dell'evento AgreementActivated venga generato nell'opportuno bucket S3 WORM un AGREEMENT_ACTIVATE_EVENTS_LOG
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica che il file contenga le opportune informazioni

  Scenario Outline: [AGREEMENT_DOC_ARCHIVE_2] Cambio stato richiesta di fruizione - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    Given "PA1" ha una richiesta di fruizione in stato "<statoAgreement>" per quell'e-service
    And verifica che a fronte dell'evento <event> venga generato nell'opportuno bucket S3 STANDARD un <file>
    And verifica che il file contenga le opportune informazioni
    And verifica che a fronte dell'evento <event> venga generato nell'opportuno bucket S3 WORM un <file>
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica che il file contenga le opportune informazioni

    Examples:
      | statoAgreement | event                        | file                                       |
      | SUSPENDED      | AgreementSuspendedByConsumer | AGREEMENT_SUSPENDED_BY_CONSUMER_EVENTS_LOG |
      | ARCHIVED       | AgreementArchivedByConsumer  | AGREEMENT_ARCHIVED_BY_CONSUMER_EVENTS_LOG  |

  Scenario: [PURPOSE_DOC_ARCHIVE_1] Attivazione nuova versione finalità - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente scarica il documento di analisi del rischio
    Then si ottiene status code 200
    And verifica che a fronte dell'evento PurposeActivated venga generato nell'opportuno bucket S3 STANDARD un RISK_ANALYSIS_DOC
    And verifica che il file contenga le opportune informazioni
    And verifica che a fronte dell'evento PurposeActivated venga generato nell'opportuno bucket S3 WORM un RISK_ANALYSIS_DOC
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica che il file contenga le opportune informazioni
    And verifica che a fronte dell'evento PurposeActivated venga generato nell'opportuno bucket S3 STANDARD un PURPOSE_ACTIVATE_EVENTS_LOG
    And verifica che il file contenga le opportune informazioni
    And verifica che a fronte dell'evento PurposeActivated venga generato nell'opportuno bucket S3 WORM un PURPOSE_ACTIVATE_EVENTS_LOG
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica che il file contenga le opportune informazioni

  Scenario: [PURPOSE_DOC_ARCHIVE_2] Attivazione nuova versione finalità - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente scarica il documento di analisi del rischio
    When l'utente aggiorna la stima di carico per quella finalità restando entro la soglia
    And si ottiene status code 200 e la nuova versione della finalità è stata creata in stato "ACTIVE" con la nuova stima di carico
    When l'utente scarica il documento di analisi del rischio
    And verifica che a fronte dell'evento NewPurposeVersionActivated venga generato nell'opportuno bucket S3 STANDARD un RISK_ANALYSIS_DOC
    And verifica che il file contenga le opportune informazioni
    And verifica che a fronte dell'evento NewPurposeVersionActivated venga generato nell'opportuno bucket S3 WORM un RISK_ANALYSIS_DOC
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica che il file contenga le opportune informazioni
    And verifica che a fronte dell'evento NewPurposeVersionActivated venga generato nell'opportuno bucket S3 STANDARD un NEW_PURPOSE_VERSION_ACTIVATE_EVENTS_LOG
    And verifica che il file contenga le opportune informazioni
    And verifica che a fronte dell'evento NewPurposeVersionActivated venga generato nell'opportuno bucket S3 WORM un NEW_PURPOSE_VERSION_ACTIVATE_EVENTS_LOG
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica che il file contenga le opportune informazioni

  Scenario: [DELEGATION_DOC_ARCHIVE_1] Delega in fruizione - archiviazione PDF firmato
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    Then si ottiene status code 200
    And verifica nel bucket S3 UNSIGNED l'esistenza del file ConsumerDelegationApproved
    And verifica nel bucket S3 SIGNED l'esistenza del file ConsumerDelegationApprovedSigned
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 UNSIGNED l'esistenza del file ConsumerDelegationApprovedEvent
    And verifica che il file contenga le opportune informazioni
    And verifica nel bucket S3 SIGNED l'esistenza del file ConsumerDelegationApprovedEventSigned
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica che il file contenga le opportune informazioni

  Scenario: [DELEGATION_DOC_ARCHIVE_2] Delega in erogazione - archiviazione PDF firmato
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And un utente dell'ente delegato con ruolo "admin"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato
    And l'ente "PA2" accetta la delega
    And verifica nel bucket S3 UNSIGNED l'esistenza del file ProducerDelegationApproved
    And verifica nel bucket S3 SIGNED l'esistenza del file ProducerDelegationApproved
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 UNSIGNED l'esistenza del file ProducerDelegationApprovedEvent
    And verifica nel bucket S3 SIGNED l'esistenza del file ProducerDelegationApprovedEvent
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione

  Scenario: [DELEGATION_DOC_ARCHIVE_3] Rifiuto delega in fruizione - archiviazione PDF firmato
    Given "GSP" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA1"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA2"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato rifiuta la delega in fruizione
    And verifica nel bucket S3 UNSIGNED l'esistenza del file ConsumerDelegationRevoked
    And verifica nel bucket S3 SIGNED l'esistenza del file ConsumerDelegationRevoked
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 UNSIGNED l'esistenza del file ConsumerDelegationRevokedEvent
    And verifica nel bucket S3 SIGNED l'esistenza del file ConsumerDelegationRevokedEvent
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione

  Scenario: [DELEGATION_DOC_ARCHIVE_4] Rifiuto delega in erogazione - archiviazione PDF firmato
    Given l'utente è un "admin" di "PA2"
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    When l'ente "PA1" richiede la creazione di una delega per l'ente "PA2"
    And l'utente accetta la delega
    Then si ottiene lo status code 200
    When l'ente "PA1" con ruolo "admin" revoca la delega
    Then si ottiene lo status code 200
    And verifica nel bucket S3 UNSIGNED l'esistenza del file ProducerDelegationRevoked
    And verifica nel bucket S3 SIGNED l'esistenza del file ProducerDelegationRevoked
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
    And verifica nel bucket S3 UNSIGNED l'esistenza del file ProducerDelegationRevokedEvent
    And verifica nel bucket S3 SIGNED l'esistenza del file ProducerDelegationRevokedEvent
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione

  Scenario: [KEY_EVENT_ARCHIVE_1] Creazione chiave pubblica - archiviazione ZIP firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    When l'utente richiede il caricamento di una chiave pubblica di tipo "RSA"
    Then si ottiene status code 204
    And verifica nel bucket S3 UNSIGNED l'esistenza del file KeysAddedEvent
    And verifica nel bucket S3 SIGNED l'esistenza del file KeysAddedEvent
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione

  Scenario: [KEY_EVENT_ARCHIVE_2] Eliminazione chiave pubblica - archiviazione ZIP firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    Given "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When l'utente richiede una operazione di cancellazione della chiave di quel client
    Then si ottiene status code 204
    And verifica nel bucket S3 UNSIGNED l'esistenza del file KeyDeletedEvent
    And verifica nel bucket S3 SIGNED l'esistenza del file KeyDeletedEvent
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione

  Scenario Outline:[ESERVICE_EVENT_ARCHIVE_1] Upgrade descrittore eservice - archiviazione ZIP firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoVersione>"
    And verifica nel bucket S3 UNSIGNED l'esistenza del file DescriptorEserviceUpgradedEvent
    And verifica nel bucket S3 SIGNED l'esistenza del file DescriptorEserviceUpgradedEvent
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione

    Examples:
      | statoVersione |
      | PUBLISHED     |
      | SUSPENDED     |
      | DEPRECATED    |
      | ARCHIVED      |
      | DRAFT         |

  Scenario: [VOUCHER_EVENT_ARCHIVE_1] Generazione voucher - archiviazione ZIP firmato
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
    And verifica nel bucket S3 UNSIGNED l'esistenza del file VoucherEvent
    And verifica nel bucket S3 SIGNED l'esistenza del file VoucherEvent
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione

  Scenario Outline: [PURPOSE_EVENT_ARCHIVE_1] Purpose upgrade - archiviazione ZIP firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "<state>" per quell'eservice
    Then si ottiene status code 200
    And verifica nel bucket S3 UNSIGNED l'esistenza del file PurposeUpgradedEvent
    And verifica nel bucket S3 SIGNED l'esistenza del file PurposeUpgradedEvent
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione

    Examples:
      | state                |
      | ACTIVE               |
      | SUSPENDED            |
      | REJECTED             |
      | WAITING_FOR_APPROVAL |
      | ARCHIVED             |
      | DRAFT                |

  Scenario Outline: [AGREEMENT_EVENT_ARCHIVE_1] Agreement upgrade - archiviazione ZIP firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    Given "PA1" ha una richiesta di fruizione in stato "<state>" per quell'e-service
    Then si ottiene status code 200
    And verifica nel bucket S3 UNSIGNED l'esistenza del file AgreementUpgradedEvent
    And verifica nel bucket S3 SIGNED l'esistenza del file AgreementUpgradedEvent
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione

    Examples:
      | state                        |
      | ACTIVE                       |
      | SUSPENDED                    |
      | REJECTED                     |
      | MISSING_CERTIFIED_ATTRIBUTES |
      | ARCHIVED                     |
      | DRAFT                        |
      | PENDING                      |

  Scenario: [CLIENT_EVENT_ARCHIVE_1] Eliminazione client - archiviazione ZIP firmato
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    When l'utente richiede una operazione di cancellazione di quel client
    Then si ottiene status code 204
    And verifica nel bucket S3 UNSIGNED l'esistenza del file ClientDeletedEvent
    And verifica nel bucket S3 SIGNED l'esistenza del file ClientDeletedEvent
    And verifica che il file nel bucket WORM abbia la proprietà "Retain until date" pari a 10 anni dalla data di creazione
