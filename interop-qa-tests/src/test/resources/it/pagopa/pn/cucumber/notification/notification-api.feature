@crudNotification
Feature: API CRUD Notifiche

  @ignore
  Scenario Outline: [TRIGGER_MANUALE] Disattiva le notifiche
    Given l'utente è un "admin" di "<tenant>"
    And si disabilitano tutte le notifiche InApp per l'utente corrente

    Examples:
      | tenant |
      | PA1    |
      | PA2    |

  Scenario: [NOTIFICATION_GET_ALL_1] Viene recuperata la lista delle notifiche (Scenario 1)
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare la lista di notifiche
    And si ottiene lo status code 200
    Then lista di notifiche viene restituita

  Scenario: [NOTIFICATION_GET_ALL_2] Lista di notifiche recuperabile solo con token valido (Scenario 2)
    Given viene impostato per l'utente un token non valido
    When l'utente tenta di recuperare la lista di notifiche
    And si ottiene lo status code 401
    Then lista di notifiche non restituita

  Scenario Outline: [NOTIFICATION_BULK_DELETE_1] Eliminazione massiva di determinate notifiche
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    And l'utente tenta di recuperare la lista di notifiche create

    # TOKEN INVALIDO
    When viene impostato per l'utente un token non valido
    And l'utente tenta di eliminare le notifiche recuperate
    Then si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    And nessuna notifica è stata eliminata

    # RUOLO NON AUTORIZZATO
    When l'utente è un "<role>" di "PA1"
    And l'utente tenta di eliminare le notifiche recuperate
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1"
    And nessuna notifica è stata eliminata

    # ELIMINAZIONE UTILIZZANDO ANCHE UN ID INESISTENTE
    When l'utente è un "admin" di "PA1"
    When l'utente tenta di eliminare le notifiche recuperate specificando almeno un id inesistente
    Then si ottiene lo status code 204
    And le notifiche create sono state eliminate

    # SECONDA ELIMINAZIONE
    When l'utente è un "admin" di "PA1"
    And l'utente tenta di eliminare le notifiche recuperate
    And si ottiene lo status code 204
    Then le notifiche create sono state eliminate

    Examples:
      | role    |
      | support |

  Scenario Outline: [NOTIFICATION_BULK_READ_1] Lettura massiva di determinate notifiche (Scenario 8)
    Given l'utente è un "admin" di "PA2"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA2" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create

    # Lettura massivo con token invalido
    When viene impostato per l'utente un token non valido
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA2"
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

    # Lettura massivo con ruolo non autorizzato
    And l'utente è un "<role>" di "PA2"
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 403
    And l'utente è un "admin" di "PA2"
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

    # Lettura totale inserendo id inesistenti
    When l'utente è un "admin" di "PA2"
    When l'utente tenta di leggere le notifiche recuperate specificando almeno un id inesistente
    Then si ottiene lo status code 200
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "read"

    # Seconda lettura totale
    When l'utente è un "admin" di "PA2"
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 204
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "read"

    Examples:
      | role    |
      | support |

  Scenario Outline: [NOTIFICATION_SINGLE_READ_1] Lettura/Unread/Eliminazione di una specifica notifica (Scenario 13)
    Given l'utente è un "admin" di "PA3"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA3" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create

    # Lettura/Unread/Eliminazione Token invalido
    When viene impostato per l'utente un token non valido
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 401
    And l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 401
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 401
    And l'utente tenta di marcare come unread le notifiche recuperate
    And si ottiene lo status code 401
    And l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA3"
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "unread"
    And nessuna notifica è stata eliminata

    # Lettura/Unread/Eliminazione Id inesistente
    And l'utente tenta di leggere la notifica recuperata specificando un id inesistente
    And si ottiene lo status code 404
    And l'utente tenta di marcare come unread la notifica recuperata specificando un id inesistente
    And si ottiene lo status code 404
    And l'utente tenta di eliminare la notifica recuperata specificando un id inesistente
    And si ottiene lo status code 404
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "unread"
    Then nessuna notifica è stata eliminata

    # Lettura/Unread/Eliminazione Ruolo non autorizzato
    And l'utente è un "<role>" di "PA3"
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 403
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 403
    And l'utente tenta di marcare come unread le notifiche recuperate
    And si ottiene lo status code 403
    And l'utente è un "admin" di "PA3"
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "unread"
    And nessuna notifica è stata eliminata

    # Lettura/Unread/Eliminazione con un utente diverso dal destinatario
    When l'utente è un "admin" di "PA1"
    And l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 404
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 404
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 404
    And l'utente è un "admin" di "PA3"
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "unread"
    And nessuna notifica è stata eliminata

    # Lettura della notifica
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 204
    Then la notifica recuperate è nello stato "read"

    # Doppia Lettura della notifica
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 204
    Then la notifica recuperate è nello stato "read"

    # Unread della notifica
    When l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 204
    Then le notifiche recuperate sono nello stato "unread"

    # Doppio Unread della notifica
    When l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 204
    Then le notifiche recuperate sono nello stato "unread"

    # Lettura della notifica
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 204
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "read"

    # Eliminazione della notifica
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 204
    Then le notifiche create sono state eliminate

    # Doppia eliminazione
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 404

    # Bulk unread
    Given "PA3" ha già generato 2 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 200
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    And le notifiche recuperate sono nello stato "read"

    # Bulk unread con almeno un id inesistente
    When l'utente tenta di marcare come unread le notifiche recuperate specificando almeno un id inesistente
    Then si ottiene lo status code 200
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

    # Secondo Bulk unread
    When l'utente tenta di marcare come unread le notifiche recuperate
    And le notifiche recuperate sono nello stato "unread"

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_COUNT_1] Viene recuperato il numero di notifiche destinate all’utente raggruppate per sezioni e sottosezioni
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    When l'utente tenta di recuperare il count delle notifiche
    And si ottiene lo status code 200
    Then count delle notifiche viene restituito

  Scenario: [NOTIFICATION_COUNT_2] Numero di notifiche destinate all’utente raggruppate per sezioni e sottosezioni recuperabile solo con token valido
    Given viene impostato per l'utente un token non valido
    When l'utente tenta di recuperare il count delle notifiche
    And si ottiene lo status code 401
    Then count delle notifiche non restituito

  Scenario: [TENANT_CONFIG_READ_1] Viene correttamente recuperata la configurazione delle notifiche per il tenant
    Given l'utente è un "admin" di "PA2"
    When si tenta di recuperare la configurazione delle notifiche per tenant
    And si ottiene lo status code 200
    Then la configurazione delle notifiche per tenant viene restituita

  Scenario: [TENANT_CONFIG_READ_2] Configurazione delle notifiche per il tenant recuperabile solo con token valido
    Given viene impostato per l'utente un token non valido
    When si tenta di recuperare la configurazione delle notifiche per tenant
    And si ottiene lo status code 401
    Then la configurazione delle notifiche per tenant non restituita

  Scenario: [TENANT_CONFIG_UPDATE_1] Viene correttamente fatto l'update della configurazione delle notifiche per il tenant
    Given l'utente è un "admin" di "PA2"
    And si tenta di recuperare la configurazione delle notifiche per tenant
    And si ottiene lo status code 200
    And la configurazione delle notifiche per tenant viene restituita
    When si tenta di modificare la configurazione delle notifiche per tenant
    And la response ha status code 204
    Then modifica viene applicata

  Scenario: [TENANT_CONFIG_UPDATE_2] Configurazione delle notifiche per il tenant inibita per token invalido
    Given l'utente è un "admin" di "PA2"
    And si tenta di recuperare la configurazione delle notifiche per tenant
    And si ottiene lo status code 200
    And la configurazione delle notifiche per tenant viene restituita
    And viene impostato per l'utente un token non valido
    When si tenta di modificare la configurazione delle notifiche per tenant
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA2"
    Then modifica non applicata

  Scenario: [TENANT_CONFIG_UPDATE_3] Configurazione delle notifiche per il tenant inibita per body invalido
    Given l'utente è un "admin" di "PA2"
    And si tenta di recuperare la configurazione delle notifiche per tenant
    And si ottiene lo status code 200
    And la configurazione delle notifiche per tenant viene restituita
    When si tenta di modificare la configurazione delle notifiche per tenant specificando un valore invalido
    And si ottiene lo status code 400
    Then modifica non applicata

  Scenario Outline: [TENANT_CONFIG_UPDATE_4] Configurazione delle notifiche per ruolo non autorizzato
    Given l'utente è un "admin" di "PA1"
    And si tenta di recuperare la configurazione delle notifiche per tenant
    And si ottiene lo status code 200
    And la configurazione delle notifiche per tenant viene restituita
    And l'utente è un "<role>" di "PA1"
    When si tenta di modificare la configurazione delle notifiche per tenant
    And si ottiene lo status code 403
    And l'utente è un "admin" di "PA1"
    Then modifica non applicata

    Examples:
      | role    |
      | support |

  Scenario: [USER_CONFIG_READ_1] Viene correttamente recuperata la configurazione delle notifiche per user
    Given l'utente è un "admin" di "PA2"
    When si tenta di recuperare la configurazione delle notifiche per user
    And si ottiene lo status code 200
    Then la configurazione delle notifiche per user viene restituita

  Scenario: [USER_CONFIG_READ_2] Configurazione delle notifiche per user recuperabile solo con token valido
    Given viene impostato per l'utente un token non valido
    When si tenta di recuperare la configurazione delle notifiche per user
    And si ottiene lo status code 401
    Then la configurazione delle notifiche per user non restituita

  Scenario: [USER_CONFIG_UPDATE_2] Configurazione delle notifiche per user inibita per token invalido
    Given l'utente è un "admin" di "GSP"
    And si tenta di recuperare la configurazione delle notifiche per user
    And si ottiene lo status code 200
    And la configurazione delle notifiche per user viene restituita

    And viene impostato per l'utente un token non valido
    When si tenta di modificare la configurazione delle notifiche per user
    And si ottiene lo status code 401
    When l'utente è un "admin" di "GSP"
    Then modifica non applicata

    When si tenta di modificare la configurazione delle notifiche per user specificando un valore invalido
    And si ottiene lo status code 400
    Then modifica non applicata

    When  si tenta di modificare la configurazione delle notifiche per user
    And la response ha status code 204
    Then modifica viene applicata

  Scenario Outline: [USER_CONFIG_UPDATE_4] Configurazione delle notifiche per user inibita per ruolo non autorizzato
    Given l'utente è un "admin" di "PA2"
    And si tenta di recuperare la configurazione delle notifiche per user
    And si ottiene lo status code 200
    And la configurazione delle notifiche per user viene restituita
    And l'utente è un "<role>" di "PA2"
    When si tenta di modificare la configurazione delle notifiche per user
    And si ottiene lo status code 403
    And l'utente è un "admin" di "PA2"
    Then modifica non applicata

    Examples:
      | role    |
      | support |