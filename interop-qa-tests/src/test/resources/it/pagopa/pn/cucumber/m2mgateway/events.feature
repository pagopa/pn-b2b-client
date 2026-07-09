@m2m-events
Feature: Eventi M2M

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_01] Verifica che il producer di un e-service in stato DRAFT può visualizzare l'evento dell'e-service
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA1" ha già creato un e-service in stato DRAFT
    Then "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
    And "PA1" visualizza l'evento EServiceDescriptorAdded con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_02] Verifica che un ente diverso dal creatore di un e-service in stato DRAFT non può visualizzare l'evento dell'e-service
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA1" ha già creato un e-service in stato DRAFT
    And "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
    And "PA1" visualizza l'evento EServiceDescriptorAdded con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |
    Then "PA2" non visualizza l'evento EServiceAdded appena trovato
    And "PA2" non visualizza l'evento EServiceDescriptorAdded appena trovato

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_03] Verifica che il creatore di un e-service in stato PUBLISHED può visualizzare gli eventi relativi alla creazione e pubblicazione dell'e-service
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

  @m2m-events-e-service @ko-nrt-08072026
  Scenario: [M2M_E-SERVICE_EVENTS_04] Verifica che il producer di un e-service in stato PUBLISHED, con delega in erogazione in attesa di approvazione verso un altro ente, visualizza gli eventi di creazione e pubblicazione senza producerDelegationId
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
    When "PA1" ha già creato un e-service in stato DRAFT
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" rifiuta la delega in erogazione con successo
    And l'utente è un "admin" di "PA1"
    And l'utente ha già pubblicato quel descrittore
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
  Scenario: [M2M_E-SERVICE_EVENTS_06] Verifica che il producer di un e-service creato in stato DRAFT, con delega in erogazione accettata da un altro ente e successivamente pubblicato dal delegato, visualizza gli eventi di creazione e pubblicazione relativi all'e-service con producerDelegationId valorizzato solo sull'evento di pubblicazione
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
      | producerDelegationId | %null       |
    And "PA1" visualizza l'evento EServiceDescriptorAdded con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |
    And "PA1" visualizza l'evento EServiceDescriptorApprovedByDelegator con:
      | field                | value                 |
      | eserviceId           | :eserviceId           |
      | descriptorId         | :descriptorId         |
      | producerDelegationId | :producerDelegationId |

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_07] Verifica che il creatore di un e-service creato in bozza e pubblicato dal delegato possa visualizzare tutti gli eventi correlati anche dopo la revoca della delega
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
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
    And l'ente "PA1" revoca la delega in erogazione con successo
    Then "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
    And "PA1" visualizza l'evento EServiceDescriptorApprovedByDelegator con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_09] Verifica che un ente diverso dall'ente creatore di un e-service in stato PUBLISHED visualizza solo l'evento di pubblicazione e non quello di creazione, con producerDelegationId assente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When "PA1" ha già creato e pubblicato 1 e-services
    And "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
    And "PA1" visualizza l'evento EServiceDescriptorAdded con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      | producerDelegationId | %null         |
    Then "PA2" non visualizza l'evento EServiceAdded appena trovato
    And "PA2" non visualizza l'evento EServiceDescriptorAdded appena trovato
    And "PA2" visualizza l'evento EServiceDescriptorPublished con:
      | field                | value                 |
      | eserviceId           | :eserviceId           |
      | descriptorId         | :descriptorId         |
      | producerDelegationId | :producerDelegationId |

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_10] Verifica che il client con delega non ancora accettata visualizzi solo l'evento di pubblicazione di un e-service di un producer
  Il producer di un e-service pubblica l'e-service, se un client ha ricevuto una delega in erogazione dal
  producer non ancora accettata, il client può visualizzare solo l'evento di pubblicazione e non quello di creazione.

    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA1" visualizza l'evento EServiceAdded con:
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
    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    Then "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" non visualizza l'evento EServiceDescriptorAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_11] Verifica che il client con delega rifiutata visualizzi solo l'evento di pubblicazione di un e-service di un producer
  Il producer di un e-service pubblica l'e-service, se un client ha ricevuto una delega in erogazione dal
  producer e l'ha rifiutata, il client può visualizzare solo l'evento di pubblicazione e non quello di creazione

    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA1" visualizza l'evento EServiceAdded con:
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
    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" rifiuta la delega in erogazione con successo
    Then "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" non visualizza l'evento EServiceDescriptorAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_12] Verifica che il client con delega accettata visualizzi l'evento di creazione e pubblicazione di un e-service di un producer
  Il producer di un e-service pubblica l'e-service, se un client ha ricevuto una delega in erogazione e l'ha accettata,
  il client può visualizzare gli eventi di creazione e pubblicazione che vede il producer.

    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
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
    And "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" non visualizza l'evento EServiceDescriptorAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_13] Verifica che il client con delega revocata visualizzi solo l'evento di pubblicazione di un e-service di un producer
  Il producer di un e-service pubblica l'e-service, se un client ha ricevuto una delega in erogazione dal
  producer ma poi è stata revocata, il client può visualizzare solo l'evento di pubblicazione e non quello di creazione.

    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    When l'ente "PA1" revoca la delega in erogazione con successo
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
    And "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" non visualizza l'evento EServiceDescriptorAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-agreement
  Scenario: [M2M_AGREEMENT_EVENTS_01] Verifica, creata una richiesta di fruizione, che l'evento di agreement di un e-service abbia la corretta visibilità per erogatore, fruitore e client generico
  Un erogatore crea un e-service delegabile, un fruitore fa richiesta di fruizione all'erogatore. Gli eventi
  AGREEMENT_ADDED e AGREEMENT_SUBMITTED per la richiesta di fruizione sono visibili al fruitore, l'erogatore vede solo
  AGREEMENT_SUBMITTED. Un generico client non vede alcun evento.

    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    When "PA2" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Then "PA2" visualizza l'evento AgreementAdded con:
      | field                | value        |
      | agreementId          | :agreementId |
      | producerDelegationId | %null        |
      | consumerDelegationId | %null        |
    And "PA2" visualizza l'evento AgreementSubmitted con:
      | field                | value        |
      | agreementId          | :agreementId |
      | producerDelegationId | %null        |
      | consumerDelegationId | %null        |
    And "PA1" non visualizza l'evento AgreementAdded precedente
    And "PA1" visualizza l'evento AgreementSubmitted precedente
    And "PA3" non visualizza l'evento AgreementAdded precedente
    And "PA3" non visualizza l'evento AgreementSubmitted precedente

  @m2m-events-agreement
  Scenario: [M2M_AGREEMENT_EVENTS_02] Verifica, in presenza di una delega di fruizione in stato di approvazione e creata una richiesta di fruizione, che l'evento di agreement di un e-service abbia la corretta visibilità per delegante, delegato in erogazione, erogatore e client generico
  Un erogatore crea un e-service delegabile, un delegante delega in erogazione un delegato, il delegato fa richiesta di
  fruizione all'erogatore. Gli eventi AGREEMENT_ADDED e AGREEMENT_SUBMITTED per la richiesta di fruizione sono visibili
  al creatore della richiesta , l'erogatore e il delegato all'erogazione vedono solo AGREEMENT_SUBMITTED.

    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    When "PA3" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Then "PA3" visualizza l'evento AgreementAdded con:
      | field                | value        |
      | agreementId          | :agreementId |
      | producerDelegationId | %null        |
    And "PA3" visualizza l'evento AgreementSubmitted con:
      | field                | value        |
      | agreementId          | :agreementId |
      | producerDelegationId | %null        |
    And "PA1" non visualizza l'evento AgreementAdded precedente
    And "PA1" visualizza l'evento AgreementSubmitted precedente
    And "PA2" non visualizza l'evento AgreementAdded precedente
    And "PA2" non visualizza l'evento AgreementSubmitted precedente

  @m2m-events-agreement
  Scenario: [M2M_AGREEMENT_EVENTS_03] Verifica, in presenza di una delega di fruizione rifiutata e creata una richiesta di fruizione, che l'evento di agreement di un e-service abbia la corretta visibilità per delegante, delegato in erogazione, erogatore e client generico
  Un erogatore crea un e-service, un delegante delega in erogazione un delegato, il delegato rifiuta la delega
  di erogazione, il delegato fa richiesta di fruizione all'erogatore. Gli eventi AGREEMENT_ADDED e AGREEMENT_SUBMITTED
  per la richiesta di fruizione sono visibili al richiedente, l'erogatore vede solo AGREEMENT_SUBMITTED.
  L'ente che ha rifiutato la delega e un generico ente non vedono nessuno degli eventi citati.

    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" rifiuta la delega in erogazione con successo
    When "PA3" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Then "PA3" visualizza l'evento AgreementAdded con:
      | field                | value        |
      | agreementId          | :agreementId |
      | producerDelegationId | %null        |
    And "PA3" visualizza l'evento AgreementSubmitted con:
      | field                | value        |
      | agreementId          | :agreementId |
      | producerDelegationId | %null        |
    And "PA1" non visualizza l'evento AgreementAdded precedente
    And "PA1" visualizza l'evento AgreementSubmitted precedente
    And "PA2" non visualizza l'evento AgreementAdded precedente
    And "PA2" non visualizza l'evento AgreementSubmitted precedente
    And "PA4" non visualizza l'evento AgreementAdded precedente
    And "PA4" non visualizza l'evento AgreementSubmitted precedente

  @m2m-events-agreement
  Scenario: [M2M_AGREEMENT_EVENTS_04] Verifica, in presenza di una delega in erogazione accettata e creata una richiesta di fruizione, che l'evento di agreement di un e-service abbia la corretta visibilità per delegante, delegato, erogatore e client generico
  Un erogatore crea un e-service, un delegante delega in erogazione un delegato, il delegato accetta la delega
  in erogazione, il delegato fa richiesta di fruizione. Gli eventi AGREEMENT_ADDEDe AGREEMENT_SUBMITTED per la richiesta di fruizione sono visibili al richiedente.
  Il delegante e il delegato vedono AGREEMENT_SUBMITTED. Un generico client non vede alcun evento.

    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    When "PA3" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Then "PA3" visualizza l'evento AgreementAdded con:
      | field       | value        |
      | agreementId | :agreementId |
    And "PA3" visualizza l'evento AgreementSubmitted con:
      | field       | value        |
      | agreementId | :agreementId |
    And "PA1" non visualizza l'evento AgreementAdded precedente
    And "PA1" visualizza l'evento AgreementSubmitted precedente
    And "PA2" non visualizza l'evento AgreementAdded precedente
    And "PA2" visualizza l'evento AgreementSubmitted precedente
    And "PA4" non visualizza l'evento AgreementAdded precedente
    And "PA4" non visualizza l'evento AgreementSubmitted precedente

  Scenario: [M2M_TEMPLATE_ESERVICE_CALLBACK_INTERFACE_1] Verifica dell'emissione dell'evento di tracciamento dopo l'aggiunta
  di un'interfaccia di callback a un template e-service asincrono.

    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di DRAFT
    And si ottiene response status code 200
    And l'e-service template creato è configurato come asincrono
    And l'utente modifica la versione dell'e-service template con:
      | voucherLifespan                               | 6000 |
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.maxResultSet          | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
    And si ottiene status code 200
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    When l'utente effettua l'aggiunta di un documento di tipo ASYNC_EXCHANGE_CALLBACK_INTERFACE alla versione dell'e-service template con successo
    And si ottiene status code 200
    Then "PA1" visualizza l'evento EServiceTemplateVersionAsyncExchangeCallbackInterfaceAdded con:
      | field                     | value                      |
      | eserviceTemplateId        | :eserviceTemplateId        |
      | eserviceTemplateVersionId | :eserviceTemplateVersionId |

  Scenario: [M2M_TEMPLATE_ESERVICE_CALLBACK_INTERFACE_2] Verifica dell'emissione dell'evento di tracciamento dopo la modifica
  di un'interfaccia di callback in un template e-service asincrono.

    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di DRAFT
    And si ottiene response status code 200
    And l'e-service template creato è configurato come asincrono
    And l'utente modifica la versione dell'e-service template con:
      | voucherLifespan                               | 6000 |
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.maxResultSet          | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
    And si ottiene status code 200
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua l'aggiunta di un documento di tipo ASYNC_EXCHANGE_CALLBACK_INTERFACE alla versione dell'e-service template con successo
    When l'utente tenta la modifica di un documento di tipo ASYNC_EXCHANGE_CALLBACK_INTERFACE
    And la modifica del documento di tipo ASYNC_EXCHANGE_CALLBACK_INTERFACE dell'e-service template è stata effettuata correttamente
    Then "PA1" visualizza l'evento EServiceTemplateVersionAsyncExchangeCallbackInterfaceUpdated con:
      | field                     | value                      |
      | eserviceTemplateId        | :eserviceTemplateId        |
      | eserviceTemplateVersionId | :eserviceTemplateVersionId |

  Scenario: [M2M_TEMPLATE_ESERVICE_CALLBACK_INTERFACE_3] Verifica dell'emissione dell'evento di tracciamento dopo l'eliminazione
  di un'interfaccia di callback in un template e-service asincrono.

    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template asincrono in modalità erogazione con tecnologia "REST" in stato di DRAFT
    And si ottiene response status code 200
    And l'e-service template creato è configurato come asincrono
    And l'utente modifica la versione dell'e-service template con:
      | voucherLifespan                               | 6000 |
      | asyncExchangeProperties.responseTime          | 100  |
      | asyncExchangeProperties.resourceAvailableTime | 100  |
      | asyncExchangeProperties.maxResultSet          | 100  |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
    And si ottiene status code 200
    And l'utente effettua l'aggiunta di un documento di tipo INTERFACE alla versione dell'e-service template con successo
    And l'utente effettua l'aggiunta di un documento di tipo ASYNC_EXCHANGE_CALLBACK_INTERFACE alla versione dell'e-service template con successo
    When l'utente effettua la cancellazione del documento di tipo ASYNC_EXCHANGE_CALLBACK_INTERFACE dall'e-service template con successo
    Then "PA1" visualizza l'evento EServiceTemplateVersionAsyncExchangeCallbackInterfaceDeleted con:
      | field                     | value                      |
      | eserviceTemplateId        | :eserviceTemplateId        |
      | eserviceTemplateVersionId | :eserviceTemplateVersionId |

  Scenario: [M2M_ESERVICE_CALLBACK_INTERFACE_1] Verifica dell'emissione dell'evento di tracciamento dopo l'aggiunta
  di un'interfaccia di callback a un e-service asincrono.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "DRAFT" con:
      | asyncExchangeProperties.responseTime          | 10   |
      | asyncExchangeProperties.resourceAvailableTime | 10   |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    When "PA1" ha già caricato un'interfaccia di callback per quel descrittore
    And si ottiene status code 200
    Then "PA1" visualizza l'evento EServiceDescriptorAsyncExchangeCallbackInterfaceAdded con:
      | field                | value                 |
      | eserviceId           | :eserviceId           |
      | descriptorId         | :descriptorId         |
      | producerDelegationId | :producerDelegationId |

  Scenario: [M2M_ESERVICE_CALLBACK_INTERFACE_2] Verifica dell'emissione dell'evento di tracciamento dopo la modifica
  di un'interfaccia di callback a un e-service asincrono.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "DRAFT" con:
      | asyncExchangeProperties.responseTime          | 10   |
      | asyncExchangeProperties.resourceAvailableTime | 10   |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And "PA1" ha già caricato un'interfaccia di callback per quel descrittore
    And si ottiene status code 200
    When l'utente aggiorna il nome dell'interfaccia di callback per quel descrittore
    And si ottiene status code 200
    Then "PA1" visualizza l'evento EServiceDescriptorAsyncExchangeCallbackInterfaceAdded con:
      | field                | value                 |
      | eserviceId           | :eserviceId           |
      | descriptorId         | :descriptorId         |
      | producerDelegationId | :producerDelegationId |

  Scenario: [M2M_ESERVICE_CALLBACK_INTERFACE_3] Verifica dell'emissione dell'evento di tracciamento dopo l'eliminazione
  di un'interfaccia di callback in un e-service asincrono.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service asincrono con un descrittore in stato "DRAFT" con:
      | asyncExchangeProperties.responseTime          | 10   |
      | asyncExchangeProperties.resourceAvailableTime | 10   |
      | asyncExchangeProperties.confirmation          | true |
      | asyncExchangeProperties.bulk                  | true |
      | asyncExchangeProperties.maxResultSet          | 50   |
    And si ottiene status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And "PA1" ha già caricato un'interfaccia di callback per quel descrittore
    And si ottiene status code 200
    When l'utente cancella quell'interfaccia di callback
    Then "PA1" visualizza l'evento EServiceDescriptorAsyncExchangeCallbackInterfaceDeleted con:
      | field                | value                 |
      | eserviceId           | :eserviceId           |
      | descriptorId         | :descriptorId         |
      | producerDelegationId | :producerDelegationId |
