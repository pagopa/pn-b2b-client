@m2m-events
Feature: Eventi M2M
  Background:
    Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_01] A seguito di pubblicazione di un e-service vengono correttamente visualizzati gli
  eventi correlati in relazione alle regole di visibilità previste per l'attore che ne fa richiesta.
  In caso di attivazione a posteriori di una delega in erogazione, la visibilità e la struttura
  degli eventi precedentemente generati non deve subire mutamenti.
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And ["PA1" prende nota dell'ultimo evento presente di tipo e-service]

    When "PA1" ha già creato e pubblicato 1 e-services
    Then "PA1" visualizza correttamente sia l'evento di creazione che quello di pubblicazione senza delega in erogazione
    And "PA2" visualizza correttamente l'evento di pubblicazione senza delega in erogazione
    And "PA2" non visualizza l'evento di creazione

    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    Then "PA1" visualizza correttamente sia l'evento di creazione che quello di pubblicazione senza delega in erogazione
    And "PA2" visualizza correttamente l'evento di pubblicazione senza delega in erogazione
    And "PA2" non visualizza l'evento di creazione

    When l'ente "PA2" rifiuta la delega in erogazione con successo
    Then "PA1" visualizza correttamente sia l'evento di creazione che quello di pubblicazione senza delega in erogazione
    And "PA2" visualizza correttamente l'evento di pubblicazione senza delega in erogazione
    And "PA2" non visualizza l'evento di creazione

    Given l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    When l'ente "PA2" accetta la delega in erogazione con successo
    Then "PA1" visualizza correttamente sia l'evento di creazione che quello di pubblicazione senza delega in erogazione
    And "PA2" visualizza correttamente l'evento di pubblicazione senza delega in erogazione
    And "PA2" non visualizza l'evento di creazione

    When l'ente "PA1" revoca la delega in erogazione con successo
    Then "PA1" visualizza correttamente sia l'evento di creazione che quello di pubblicazione senza delega in erogazione
    And "PA2" visualizza correttamente l'evento di pubblicazione senza delega in erogazione
    And "PA2" non visualizza l'evento di creazione

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_02] In caso di delega in erogazione attiva, l'ente delegato deve
  aver visibilità su tutti gli eventi inerenti l'e-service scatenati dopo l'attivazione della
  delega; inoltre, il campo producerDelegationId deve essere valorizzato su quegli eventi.
  Se la delega viene revocata, la visibilità acquisita deve andare persa e suddetto campo non
  essere più visibile.
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And ["PA1" prende nota dell'ultimo evento presente di tipo e-service]
    And "PA1" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC"
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    When "PA2" aggiorna quell'e-service

    Then "PA1" visualizza correttamente l'evento di modifica dell'e-service con delega in erogazione
    And "PA2" visualizza correttamente l'evento di modifica dell'e-service con delega in erogazione

    When l'ente "PA1" revoca la delega in erogazione con successo
    Then "PA1" visualizza correttamente l'evento di modifica dell'e-service senza delega in erogazione
    And "PA2" non visualizza l'evento di modifica dell'e-service

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_03] In caso di delega in erogazione non attiva,
  l'ente delegato non deve aver visibilità sugli eventi inerenti l'e-service tipicamente visibili
  a un delegato con delega attiva; inoltre, il campo producerDelegationId non deve essere valorizzato.
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And ["PA1" prende nota dell'ultimo evento presente di tipo e-service]
    And "PA1" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC"
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    When "PA1" aggiorna quell'e-service
    Then "PA1" visualizza correttamente l'evento di modifica dell'e-service senza delega in erogazione
    And "PA2" non visualizza l'evento di modifica dell'e-service

