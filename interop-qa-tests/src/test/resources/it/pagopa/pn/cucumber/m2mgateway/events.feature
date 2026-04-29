@m2m-events
Feature: Eventi M2M

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_01] Verifica che il producer di un e-service in stato DRAFT può visualizzare l'evento dell'e-service
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA1" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC"
    Then "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
      # TODO Verificare l'esistenza dei campi: id, eventTimestamp, descriptorId

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_02] Verifica che un client diverso dal producer di un e-service in stato DRAFT non può visualizzare l'evento dell'e-service
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA1" ha già creato un e-service in stato DRAFT
    And "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
    Then "PA2" non visualizza l'evento EServiceAdded appena trovato

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_03] Verifica che il producer di un e-service in stato PUBLISHED può visualizzare gli eventi relativi alla creazione e pubblicazione dell'e-service
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA1" ha già creato e pubblicato 1 e-services
    Then "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
    And "PA1" visualizza l'evento EServiceDescriptorAdded con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_04] Verifica che il producer di un e-service in stato PUBLISHED, con delega in erogazione in attesa di approvazione, visualizza gli eventi di creazione e pubblicazione senza producerDelegationId
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA1" ha già creato e pubblicato 1 e-services
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    Then "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
    And "PA1" visualizza l'evento EServiceDescriptorAdded con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_05] Verifica che il producer di un e-service in stato PUBLISHED, con delega in erogazione rifiutata, visualizza gli eventi di creazione e pubblicazione senza producerDelegationId
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA1" ha già creato e pubblicato 1 e-services
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" rifiuta la delega in erogazione con successo
    Then "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | descriptorId         | %null       |
      | producerDelegationId | %null       |
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_06] Verifica che il producer di un e-service creato in stato DRAFT, con delega in erogazione accettata da un altro ente
  e successivamente pubblicato dal delegato, visualizza gli eventi di creazione e pubblicazione relativi all'e-service con producerDelegationId valorizzato solo sull'evento di pubblicazione
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service in stato DRAFT
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente pubblica l'e-service
    And l'e-service è in stato "WAITING_FOR_APPROVAL"
    And l'utente è un "admin" di "PA1"
    And l'utente approva la pubblicazione dell'e-service
    And l'e-service è in stato "PUBLISHED"
    Then "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | descriptorId         | %null       |
      | producerDelegationId | %null       |
    And "PA1" visualizza l'evento EServiceDescriptorApprovedByDelegator con:
      | field                | value                 |
      | eserviceId           | :eserviceId           |
      | descriptorId         | :descriptorId         |
      | producerDelegationId | :producerDelegationId |

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_10] Verifica che il client con delega non ancora accettata visualizzi solo l'evento di pubblicazione di un e-service di un producer
  Il producer di un e-service pubblica l'e-service, se un client ha ricevuto una delega in erogazione dal
  producer non ancora accettata, il client può visualizzare solo l'evento di pubblicazione e non quello di creazione.
    # TODO il test andrebbe eseguito con delega in erogazione e delega in fruizione

    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA1" visualizza l'evento EServiceAdded con:
      | field      | value       |
      | eserviceId | :eserviceId |
      # TODO Verificare l'assenza del campo producerDelegationId
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field        | value         |
      | eserviceId   | :eserviceId   |
      | descriptorId | :descriptorId |
      # TODO Verificare l'assenza del campo producerDelegationId
      # TODO Verificare l'esistenza dei campi: id, eventTimestamp
    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    Then "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_11] Verifica che il client con delega rifiutata visualizzi solo l'evento di pubblicazione di un e-service di un producer
  Il producer di un e-service pubblica l'e-service, se un client ha ricevuto una delega in erogazione dal
  producer e l'ha rifiutata, il client può visualizzare solo l'evento di pubblicazione e non quello di creazione.
    # TODO il test andrebbe eseguito con delega in erogazione e delega in fruizione

    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA1" visualizza l'evento EServiceAdded con:
      | field      | value       |
      | eserviceId | :eserviceId |
      # TODO Verificare l'assenza del campo producerDelegationId
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field        | value         |
      | eserviceId   | :eserviceId   |
      | descriptorId | :descriptorId |
      # TODO Verificare l'assenza del campo producerDelegationId
      # TODO Verificare l'esistenza dei campi: id, eventTimestamp
    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" rifiuta la delega in erogazione con successo
    Then "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_12] Verifica che il client con delega accettata visualizzi l'evento di creazione e pubblicazione di un e-service di un producer
  Il producer di un e-service pubblica l'e-service, se un client ha ricevuto una delega in erogazione dal
  producer e l'ha accettata, il client può visualizzare tutti gli eventi, in particolare creazione e pubblicazione.
  # TODO il test andrebbe eseguito con delega in erogazione e delega in fruizione

    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA1" visualizza l'evento EServiceAdded con:
      | field      | value       |
      | eserviceId | :eserviceId |
      # TODO Verificare l'assenza del campo producerDelegationId
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field        | value         |
      | eserviceId   | :eserviceId   |
      | descriptorId | :descriptorId |
      # TODO Verificare l'assenza del campo producerDelegationId
      # TODO Verificare l'esistenza dei campi: id, eventTimestamp
    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    # Fallisce la seguente istruzione: perché?
    Then "PA2" visualizza l'evento EServiceAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_13] Verifica che il client con delega revocata visualizzi solo l'evento di pubblicazione di un e-service di un producer
  Il producer di un e-service pubblica l'e-service, se un client ha ricevuto una delega in erogazione dal
  producer ma poi è stata revocata, il client può visualizzare solo l'evento di pubblicazione e non quello di creazione.
  # TODO il test andrebbe eseguito con delega in erogazione e delega in fruizione

    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-services
    And "PA1" visualizza l'evento EServiceAdded con:
      | field      | value       |
      | eserviceId | :eserviceId |
      # TODO Verificare l'assenza del campo producerDelegationId
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field        | value         |
      | eserviceId   | :eserviceId   |
      | descriptorId | :descriptorId |
      # TODO Verificare l'assenza del campo producerDelegationId
      # TODO Verificare l'esistenza dei campi: id, eventTimestamp
    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA1" revoca la delega in erogazione con successo
    Then "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_14] Verifica, creata una delega, che l'evento di agreement di un e-service abbia la corretta visibilità per consumer, erogatore e client generico
  A seguito della creazione di una richiesta di fruizione di un e-service, il consumer può visualizzare sia l'evento
  AGREEMENT_ADDED che AGREEMENT_SUBMITTED, mentre il producer che ha fatto la delega in fruizione vede solo l'evento
  AGREEMENT_SUBMITTED. Un generico client non vede nessuno dei due eventi.

    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in fruizione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-services
    When l'ente "PA1" ha inoltrato una richiesta di delega in fruizione all'ente terzo "PA2"
    And l'ente "PA2" accetta la delega in fruizione con successo
    Then "PA2" visualizza l'evento AgreementAdded con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |
      # TODO verificare l'esistenza di id, eventTimestamp
    And "PA2" visualizza l'evento AgreementSubmitted con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |
      # TODO verificare l'esistenza di id, eventTimestamp
    And "PA1" non visualizza l'evento AgreementAdded precedente
    And "PA1" visualizza l'evento AgreementSubmitted precedente
    And l'utente è un "admin" di "PA3" con ruolo M2M m2m-admin
    And "PA3" non visualizza l'evento AgreementAdded precedente
    And "PA3" non visualizza l'evento AgreementSubmitted precedente


  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_nn] A seguito di pubblicazione di un e-service vengono correttamente visualizzati gli
  eventi correlati in relazione alle regole di visibilità previste per l'attore che ne fa richiesta.
  In caso di attivazione a posteriori di una delega in erogazione, la visibilità e la struttura
  degli eventi precedentemente generati non deve subire mutamenti.
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA1" ha già creato e pubblicato 1 e-services

    And "PA1" visualizza l'evento EServiceAdded con:
      | field      | value       |
      | eserviceId | :eserviceId |
    And "PA2" non visualizza l'evento EServiceAdded appena trovato
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field        | value         |
      | eserviceId   | :eserviceId   |
      | descriptorId | :descriptorId |
    And "PA2" visualizza l'evento EServiceDescriptorPublished appena trovato

    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And "PA1" visualizza l'evento EServiceAdded precedente
    And "PA1" visualizza l'evento EServiceDescriptorPublished precedente
    And "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

    When l'ente "PA2" rifiuta la delega in erogazione con successo
    And "PA1" visualizza l'evento EServiceAdded precedente
    And "PA1" visualizza l'evento EServiceDescriptorPublished precedente
    And "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And "PA1" visualizza l'evento EServiceAdded precedente
    And "PA1" visualizza l'evento EServiceDescriptorPublished precedente
    And "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

    Then l'ente "PA1" revoca la delega in erogazione con successo
    And "PA1" visualizza l'evento EServiceAdded precedente
    And "PA1" visualizza l'evento EServiceDescriptorPublished precedente
    And "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_nn] In caso di delega in erogazione attiva, l'ente delegato deve
  aver visibilità su tutti gli eventi inerenti l'e-service scatenati dopo l'attivazione della
  delega; inoltre, il campo producerDelegationId deve essere valorizzato su quegli eventi.
  Se la delega viene revocata, la visibilità acquisita deve andare persa e suddetto campo non
  essere più visibile.
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC"
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    When "PA2" aggiorna quell'e-service
    Then "PA2" visualizza l'evento DraftEServiceUpdated con:
      | field                | value                 |
      | eserviceId           | :eserviceId           |
      | producerDelegationId | :producerDelegationId |
    And "PA1" visualizza l'evento DraftEServiceUpdated appena trovato
    When l'ente "PA1" revoca la delega in erogazione con successo
    Then "PA1" visualizza l'evento DraftEServiceUpdated con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
    And "PA2" non visualizza l'evento DraftEServiceUpdated appena trovato

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_nn] In caso di delega in erogazione non attiva,
  l'ente delegato non deve aver visibilità sugli eventi inerenti l'e-service tipicamente visibili
  a un delegato con delega attiva; inoltre, il campo producerDelegationId non deve essere valorizzato.
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC"
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    When "PA1" aggiorna quell'e-service
    Then "PA1" visualizza l'evento DraftEServiceUpdated con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
    And "PA2" non visualizza l'evento DraftEServiceUpdated appena trovato
