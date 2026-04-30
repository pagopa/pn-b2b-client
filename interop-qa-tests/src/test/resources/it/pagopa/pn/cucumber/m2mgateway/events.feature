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
    Then "PA2" non visualizza l'evento EServiceAdded appena trovato

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

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_04] Verifica che il producer di un e-service in stato PUBLISHED, con delega in erogazione in attesa di approvazione verso un altro ente,
  visualizza gli eventi di creazione e pubblicazione senza producerDelegationId
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
  Scenario: [M2M_E-SERVICE_EVENTS_07] Verifica che il creatore di un e-service creato in bozza e pubblicato dal delegato
  possa visualizzare tutti gli eventi correlati anche dopo la revoca della delega
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
    And l'ente "PA1" revoca la delega in erogazione con successo
    Then "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      | producerDelegationId | %null       |
    And "PA1" visualizza l'evento EServiceDescriptorApprovedByDelegator con:
      | field                | value                 |
      | eserviceId           | :eserviceId           |
      | descriptorId         | :descriptorId         |
      | producerDelegationId | :producerDelegationId |

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
  # TODO il test andrebbe eseguito con delega in erogazione e delega in fruizione

    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      # TODO Verificare l'assenza del campo producerDelegationId
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      # TODO Verificare l'assenza del campo producerDelegationId
    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    Then "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_11] Verifica che il client con delega rifiutata visualizzi solo l'evento di pubblicazione di un e-service di un producer
  Il producer di un e-service pubblica l'e-service, se un client ha ricevuto una delega in erogazione dal
  producer e l'ha rifiutata, il client può visualizzare solo l'evento di pubblicazione e non quello di creazione
  # TODO il test andrebbe eseguito con delega in erogazione e delega in fruizione

    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      # TODO Verificare l'assenza del campo producerDelegationId
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      # TODO Verificare l'assenza del campo producerDelegationId
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
      | field                | value                 |
      | eserviceId           | :eserviceId           |
      # TODO Verificare l'assenza del campo producerDelegationId
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field                | value                 |
      | eserviceId           | :eserviceId           |
      | descriptorId         | :descriptorId         |
      # TODO Verificare l'assenza del campo producerDelegationId
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
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA1" visualizza l'evento EServiceAdded con:
      | field                | value       |
      | eserviceId           | :eserviceId |
      # TODO Verificare l'assenza del campo producerDelegationId
    And "PA1" visualizza l'evento EServiceDescriptorPublished con:
      | field                | value         |
      | eserviceId           | :eserviceId   |
      | descriptorId         | :descriptorId |
      # TODO Verificare l'assenza del campo producerDelegationId
    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA1" revoca la delega in erogazione con successo
    Then "PA2" non visualizza l'evento EServiceAdded precedente
    And "PA2" visualizza l'evento EServiceDescriptorPublished precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_14] Verifica, creata una richiesta di fruizione, che l'evento di agreement di un e-service abbia la corretta visibilità per erogatore, fruitore e client generico
  Un erogatore crea un e-service delegabile, un fruitore fa richiesta di fruizione all'erogatore. Gli eventi
  AGREEMENT_ADDED e AGREEMENT_SUBMITTED per la richiesta di fruizione sono visibili al fruitore, l'erogatore vede solo
  AGREEMENT_SUBMITTED. Un generico client non vede alcun evento.

    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in fruizione
    When "PA2" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Then "PA2" visualizza l'evento AgreementAdded con:
      | field                 | value                 |
      | agreementId           | :agreementId          |
      #| producerDelegationId  | %null                 |
      #| consumerDelegationId  | %null                 |
    And "PA2" visualizza l'evento AgreementSubmitted con:
      | field                 | value                 |
      | agreementId           | :agreementId          |
      #| producerDelegationId  | %null                 |
      #| consumerDelegationId  | %null                 |
    And "PA1" non visualizza l'evento AgreementAdded precedente
    And "PA1" visualizza l'evento AgreementSubmitted precedente
    And "PA3" non visualizza l'evento AgreementAdded precedente
    And "PA3" non visualizza l'evento AgreementSubmitted precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_15] Verifica, in presenza di una delega di fruizione in stato di approvazione e creata una richiesta di fruizione, che l'evento di agreement di un e-service abbia la corretta visibilità per delegante, delegato, erogatore e client generico
  Un erogatore crea un e-service delegabile, un delegante delega in fruizione un delegato, il delegato fa richiesta di
  fruizione all'erogatore. Gli eventi AGREEMENT_ADDED e AGREEMENT_SUBMITTED per la richiesta di fruizione sono visibili
  al delegante e al delegato, l'erogatore vede solo AGREEMENT_SUBMITTED. Un generico client non vede alcun evento.

    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    And l'ente "PA3" concede la disponibilità a ricevere deleghe in fruizione
    And l'ente "PA2" ha inoltrato una richiesta di delega in fruizione all'ente "PA3"
    When "PA3" ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione
    Then "PA3" visualizza l'evento AgreementAdded con:
      | field                 | value                 |
      | agreementId           | :agreementId          |
      #| producerDelegationId  | %null                 |
      #| consumerDelegationId  | %null                 |
    And "PA3" visualizza l'evento AgreementSubmitted con:
      | field                 | value                 |
      | agreementId           | :agreementId          |
      #| producerDelegationId  | %null                 |
      #| consumerDelegationId  | %null                 |
    And "PA2" visualizza l'evento AgreementAdded precedente
    And "PA2" visualizza l'evento AgreementSubmitted precedente
    And "PA1" non visualizza l'evento AgreementAdded precedente
    And "PA1" visualizza l'evento AgreementSubmitted precedente
    And "PA4" non visualizza l'evento AgreementAdded precedente
    And "PA4" non visualizza l'evento AgreementSubmitted precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_16] Verifica, in presenza di una delega di fruizione rifiutata e creata una richiesta di fruizione, che l'evento di agreement di un e-service abbia la corretta visibilità per delegante, delegato, erogatore e client generico
  Un erogatore crea un e-service delegabile, un delegante delega in fruizione un delegato, il delegato rifiuta la delega
  di fruizione, il delegato fa richiesta di fruizione all'erogatore. Gli eventi AGREEMENT_ADDED e AGREEMENT_SUBMITTED
  per la richiesta di fruizione sono visibili al delegante e al delegato, l'erogatore vede solo AGREEMENT_SUBMITTED.
  Un generico client non vede alcun evento.

    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    And l'ente "PA3" concede la disponibilità a ricevere deleghe in fruizione
    And l'ente "PA2" ha inoltrato una richiesta di delega in fruizione all'ente "PA3"
    And l'ente "PA3" accetta la delega in fruizione con successo
    And "PA3" ha una richiesta di fruizione in stato "PENDING" per quell'e-service in qualità di delegato
    When l'ente "PA2" con ruolo "admin" revoca la delega in fruizione
    Then "PA2" visualizza l'evento AgreementAdded con:
      | field                 | value                 |
      | agreementId           | :agreementId          |
      | producerDelegationId  | %null                 |
      | consumerDelegationId  | %null                 |
    And "PA2" visualizza l'evento AgreementSubmitted con:
      | field                 | value                 |
      | agreementId           | :agreementId          |
      | producerDelegationId  | %null                 |
      | consumerDelegationId  | %null                 |
    And "PA3" non visualizza l'evento AgreementAdded precedente
    And "PA3" non visualizza l'evento AgreementSubmitted precedente
    And "PA1" non visualizza l'evento AgreementAdded precedente
    And "PA1" visualizza l'evento AgreementSubmitted precedente
    And "PA4" non visualizza l'evento AgreementAdded precedente
    And "PA4" non visualizza l'evento AgreementSubmitted precedente

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_17] Verifica, in presenza di una delega di fruizione accettata e creata una richiesta di fruizione, che l'evento di agreement di un e-service abbia la corretta visibilità per delegante, delegato, erogatore e client generico
  Un erogatore crea un e-service delegabile, un delegante delega in fruizione un delegato, il delegato accetta la delega
  di fruzione, il delegato fa richiesta di fruizione all'erogatore per conto del delegante. Gli eventi AGREEMENT_ADDED
  e AGREEMENT_SUBMITTED per la richiesta di fruizione sono visibili al delegante e al delegato, l'erogatore vede solo
  AGREEMENT_SUBMITTED. Un generico client non vede alcun evento.

    Given "PA1" ha già creato e pubblicato 1 e-service delegabile in fruizione con approvazione manuale
    And l'ente "PA3" concede la disponibilità a ricevere deleghe in fruizione
    And l'ente "PA2" ha inoltrato una richiesta di delega in fruizione all'ente "PA3"
    When l'ente "PA3" accetta la delega in fruizione con successo
    And "PA3" ha una richiesta di fruizione in stato "PENDING" per quell'e-service in qualità di delegato
    Then "PA3" visualizza l'evento AgreementAdded con:
      | field                 | value                 |
      | agreementId           | :agreementId          |
      | producerDelegationId  | %null                 |
      | consumerDelegationId  | :consumerDelegationId |
    And "PA3" visualizza l'evento AgreementSubmitted con:
      | field                 | value                 |
      | agreementId           | :agreementId          |
      | producerDelegationId  | %null                 |
      | consumerDelegationId  | :consumerDelegationId |
    And "PA2" visualizza l'evento AgreementAdded precedente
    And "PA2" visualizza l'evento AgreementSubmitted precedente
    And "PA1" non visualizza l'evento AgreementAdded precedente
    And "PA1" visualizza l'evento AgreementSubmitted precedente
    And "PA4" non visualizza l'evento AgreementAdded precedente
    And "PA4" non visualizza l'evento AgreementSubmitted precedente
