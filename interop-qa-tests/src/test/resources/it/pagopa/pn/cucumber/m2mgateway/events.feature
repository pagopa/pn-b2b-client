@m2m-events
Feature: Eventi M2M
  Background:
    Given l'ente "PA2" rimuove la disponibilità a ricevere deleghe

  @m2m-events-e-service
  Scenario: [M2M_E-SERVICE_EVENTS_01] A seguito di pubblicazione di un e-service vengono correttamente visualizzati gli
  eventi correlati in relazione alle regole di visibilità previste per l'attore che ne fa richiesta;
  inoltre, i campi relativi alle deleghe sono valorizzati soltanto in caso di delega attiva
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And ["PA1" prende nota dell'ultimo evento presente di tipo e-service]

    When "PA1" ha già creato e pubblicato 1 e-services
    Then "PA1" visualizza correttamente sia l'evento di creazione che quello di pubblicazione senza delega in erogazione
    And "PA2" visualizza correttamente l'evento di pubblicazione senza delega in erogazione
    And "PA2" non visualizza l'evento di creazione

#    When l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
#    Then "PA1" visualizza correttamente sia l'evento di creazione che quello di pubblicazione senza delega in erogazione
#    And "PA2" visualizza correttamente l'evento di pubblicazione senza delega in erogazione
#    And "PA2" non visualizza l'evento di creazione

#    When l'ente "PA2" rifiuta la delega in erogazione con successo
#    Then "PA1" visualizza correttamente sia l'evento di creazione che quello di pubblicazione senza delega in erogazione
#    And "PA2" visualizza correttamente l'evento di pubblicazione senza delega in erogazione
#    And "PA2" non visualizza l'evento di creazione

#    Given l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
#    When l'ente "PA2" accetta la delega in erogazione con successo
#    Then "PA1" visualizza correttamente sia l'evento di creazione che quello di pubblicazione con delega in erogazione
#    Then "PA2" visualizza correttamente sia l'evento di creazione che quello di pubblicazione con delega in erogazione

#    When l'ente "PA1" revoca la delega in erogazione con successo
#    Then "PA1" visualizza correttamente sia l'evento di creazione che quello di pubblicazione senza delega in erogazione
#    And "PA2" visualizza correttamente l'evento di pubblicazione senza delega in erogazione
#    And "PA2" non visualizza l'evento di creazione

  # FIXME solo per facilitare la creazione di token da utilizzare nei test manuali
  Scenario: Genera token
    Then l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And l'utente è un "admin" di "GSP" con ruolo M2M m2m-admin
    And l'utente è un "admin" di "GSP2" con ruolo M2M m2m-admin

