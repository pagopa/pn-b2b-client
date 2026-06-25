@crudNotification
Feature: API CRUD Notifiche

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

  Scenario: [NOTIFICATION_BULK_DELETE_1] Eliminazione massiva di determinate notifiche (Scenario 3)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di eliminare le notifiche recuperate
    And si ottiene lo status code 204
    Then le notifiche create sono state eliminate

  Scenario: [NOTIFICATION_BULK_DELETE_2] Eliminazione massiva di notifiche con token invalido (Scenario 4)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    Given viene impostato per l'utente un token non valido
    And l'utente tenta di eliminare le notifiche recuperate
    Then si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    And nessuna notifica è stata eliminata

    #BUG: https://pagopa.atlassian.net/browse/PIN-8920
  Scenario: [NOTIFICATION_BULK_DELETE_3] Eliminazione massiva di notifiche con ID inesistente (Scenario 5)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    When l'utente tenta di eliminare le notifiche recuperate specificando almeno un id inesistente
    Then si ottiene lo status code 200
    And le notifiche create sono state eliminate

    #NOTA: La classe UUID non permette la creazione di id malformati pertanto l'invio della request avrà sempre eccezione Java
    # Il test è stato eseguito manualmente con esito positivo il giorno 12/01/2026
  @ignore
  Scenario: [NOTIFICATION_BULK_DELETE_4] Eliminazione massiva di notifiche con ID invalido (Scenario 6)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    When l'utente tenta di eliminare le notifiche recuperate specificando almeno un id invalido
    Then si ottiene lo status code 400
    And nessuna notifica è stata eliminata

  Scenario Outline: [NOTIFICATION_BULK_DELETE_5] Eliminazione massiva di notifiche con ruoli non autorizzati (Scenario 7)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    Given l'utente è un "<role>" di "PA1"
    When l'utente tenta di eliminare le notifiche recuperate
    Then si ottiene lo status code 403
    And l'utente è un "admin" di "PA1"
    And nessuna notifica è stata eliminata

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_BULK_READ_1] Lettura massiva di determinate notifiche (Scenario 8)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 204
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "read"

  Scenario: [NOTIFICATION_BULK_READ_2] Lettura massivo con token invalido (Scenario 9)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And viene impostato per l'utente un token non valido
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

    #BUG: https://pagopa.atlassian.net/browse/PIN-8922
  Scenario: [NOTIFICATION_BULK_READ_3] Lettura massiva di notifiche con ID inesistente (Scenario 10)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    When l'utente tenta di leggere le notifiche recuperate specificando almeno un id inesistente
    Then si ottiene lo status code 200
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "read"

    #NOTA: La classe UUID non permette la creazione di id malformati pertanto l'invio della request avrà sempre eccezione Java
    # Il test è stato eseguito manualmente con esito positivo il giorno 12/01/2026
  @ignore
  Scenario: [NOTIFICATION_BULK_READ_4] Lettura massiva di notifiche con ID invalido (Scenario 11)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    When l'utente tenta di leggere le notifiche recuperate specificando almeno un id invalido
    Then si ottiene lo status code 400
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

  Scenario Outline: [NOTIFICATION_BULK_READ_5] Lettura massivo con ruolo non autorizzato (Scenario 12)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "<role>" di "PA1"
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 403
    And l'utente è un "admin" di "PA1"
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_SINGLE_READ_1] Lettura di una specifica notifica (Scenario 13)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 204
    Then la notifica recuperate è nello stato "read"

  Scenario: [NOTIFICATION_SINGLE_READ_2] Doppia lettura di una specifica notifica (Scenario 14)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 204
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    And le notifiche recuperate sono nello stato "read"
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 204
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "read"

  Scenario: [NOTIFICATION_SINGLE_READ_3] Lettura di una specifica notifica con token invalido (Scenario 15)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And viene impostato per l'utente un token non valido
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "unread"

  Scenario: [NOTIFICATION_SINGLE_READ_4] Lettura di una specifica notifica con id inesistente (Scenario 16)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere la notifica recuperata specificando un id inesistente
    And si ottiene lo status code 404
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "unread"

     #NOTA: La classe UUID non permette la creazione di id malformati pertanto l'invio della request avrà sempre eccezione Java
    # Il test è stato eseguito manualmente con esito positivo il giorno 12/01/2026
  @ignore
  Scenario: [NOTIFICATION_SINGLE_READ_5] Lettura di una specifica notifica con id invalido (Scenario 17)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere la notifica recuperata specificando un id invalido
    And si ottiene lo status code 400
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "unread"

  Scenario Outline: [NOTIFICATION_SINGLE_READ_6] Lettura di una specifica notifica con ruolo non autorizzato (Scenario 18)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "<role>" di "PA1"
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 403
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "unread"

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_SINGLE_READ_7] Lettura di una specifica notifica con un utente diverso dal destinatario (Scenario 19)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "admin" di "PA2"
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 404
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then la notifica recuperate è nello stato "unread"

  Scenario: [NOTIFICATION_SINGLE_DELETE_1] Eliminazione di una specifica notifica (Scenario 20)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 204
    Then le notifiche create sono state eliminate

  Scenario: [NOTIFICATION_SINGLE_DELETE_2] Eliminazione di una specifica notifica con token invalido (Scenario 21)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And viene impostato per l'utente un token non valido
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    Then nessuna notifica è stata eliminata

  Scenario: [NOTIFICATION_SINGLE_DELETE_3] Eliminazione di una specifica notifica con id inesistente (Scenario 22)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di eliminare la notifica recuperata specificando un id inesistente
    And si ottiene lo status code 404
    Then nessuna notifica è stata eliminata

     #NOTA: La classe UUID non permette la creazione di id malformati pertanto l'invio della request avrà sempre eccezione Java
    # Il test è stato eseguito manualmente con esito positivo il giorno 12/01/2026
  @ignore
  Scenario: [NOTIFICATION_SINGLE_DELETE_4] Eliminazione di una specifica notifica con id invalido (Scenario 23)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di eliminare la notifica recuperata specificando un id invalido
    And si ottiene lo status code 400
    Then nessuna notifica è stata eliminata

  Scenario Outline: [NOTIFICATION_SINGLE_DELETE_5] Eliminazione di una specifica notifica con ruolo non autorizzato (Scenario 24)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "<role>" di "PA1"
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 403
    And l'utente è un "admin" di "PA1"
    Then nessuna notifica è stata eliminata

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_SINGLE_DELETE_6] Doppia eliminazione di una specifica notifica (Scenario 25)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 204
    Then le notifiche create sono state eliminate
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 404

  Scenario: [NOTIFICATION_SINGLE_DELETE_7] Eliminazione di una specifica notifica con un utente diverso dal destinatario (Scenario 26)
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "admin" di "PA2"
    And l'utente tenta di eliminare la notifica recuperata
    And si ottiene lo status code 404
    And l'utente è un "admin" di "PA1"
    Then nessuna notifica è stata eliminata

  Scenario: [NOTIFICATION_BULK_UNREAD_1] Unread massivo di determinate notifiche
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 200
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    And le notifiche recuperate sono nello stato "read"
    When l'utente tenta di marcare come unread le notifiche recuperate
    And le notifiche recuperate sono nello stato "unread"

  Scenario: [NOTIFICATION_BULK_UNREAD_2] Unread massivo con token invalido
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And viene impostato per l'utente un token non valido
    And l'utente tenta di marcare come unread le notifiche recuperate
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

    #BUG: https://pagopa.atlassian.net/browse/PIN-8923
  Scenario: [NOTIFICATION_BULK_UNREAD_3] Unread massivo di notifiche con ID inesistente
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    When l'utente tenta di marcare come unread le notifiche recuperate specificando almeno un id inesistente
    Then si ottiene lo status code 200
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

     #NOTA: La classe UUID non permette la creazione di id malformati pertanto l'invio della request avrà sempre eccezione Java
    # Il test è stato eseguito manualmente con esito positivo il giorno 12/01/2026
  @ignore
  Scenario: [NOTIFICATION_BULK_UNREAD_4] Unread massivo di notifiche con ID invalido
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    When l'utente tenta di marcare come unread le notifiche recuperate specificando almeno un id invalido
    Then si ottiene lo status code 400
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

  Scenario Outline: [NOTIFICATION_BULK_UNREAD_5] Unread massivo con ruolo non autorizzato
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "<role>" di "PA1"
    And l'utente tenta di marcare come unread le notifiche recuperate
    And si ottiene lo status code 403
    And l'utente è un "admin" di "PA1"
    And l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_SINGLE_UNREAD_1] Unread di una specifica notifica
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 204
    And le notifiche recuperate sono nello stato "read"
    When l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 204
    Then le notifiche recuperate sono nello stato "unread"

  Scenario: [NOTIFICATION_SINGLE_UNREAD_2] Doppio unread di una specifica notifica
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere la notifica recuperata
    And si ottiene lo status code 204
    And le notifiche recuperate sono nello stato "read"
    When l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 204
    And le notifiche recuperate sono nello stato "unread"
    When l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 204
    Then le notifiche recuperate sono nello stato "unread"

  Scenario: [NOTIFICATION_SINGLE_UNREAD_3] Unread di una specifica notifica con token invalido
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And viene impostato per l'utente un token non valido
    And l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

  Scenario: [NOTIFICATION_SINGLE_UNREAD_4] Unread di una specifica notifica con id inesistente
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di marcare come unread la notifica recuperata specificando un id inesistente
    And si ottiene lo status code 404
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

     #NOTA: La classe UUID non permette la creazione di id malformati pertanto l'invio della request avrà sempre eccezione Java
    # Il test è stato eseguito manualmente con esito positivo il giorno 12/01/2026
  @ignore
  Scenario: [NOTIFICATION_SINGLE_UNREAD_5] Unread di una specifica notifica con id invalido
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di marcare come unread la notifica recuperata specificando un id invalido
    And si ottiene lo status code 400
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

  Scenario Outline: [NOTIFICATION_SINGLE_UNREAD_6] Unread di una specifica notifica con ruolo non autorizzato
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "<role>" di "PA1"
    And l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 403
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_SINGLE_UNREAD_7] Unread di una specifica notifica con un utente diverso dal destinatario
    Given l'utente è un "admin" di "PA1"
    And si attivano tutte le notifiche InApp per l'utente corrente
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "admin" di "PA2"
    And l'utente tenta di marcare come unread la notifica recuperata
    And si ottiene lo status code 404
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare lo stato aggiornato delle notifiche
    Then le notifiche recuperate sono nello stato "unread"

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
    Given l'utente è un "admin" di "PA2"
    And si tenta di recuperare la configurazione delle notifiche per tenant
    And si ottiene lo status code 200
    And la configurazione delle notifiche per tenant viene restituita
    And l'utente è un "<role>" di "PA2"
    When si tenta di modificare la configurazione delle notifiche per tenant
    And si ottiene lo status code 403
    And l'utente è un "admin" di "PA2"
    Then modifica non applicata

    Examples:
      | role    |
      | support |


     #NOTA: La classe Boolean non permette la creazione di valori malformati, se non null, pertanto l'invio della request porta sempre ad un 400
  @ignore
  Scenario: [TENANT_CONFIG_UPDATE_5] Configurazione delle notifiche per il tenant inibita per valore del body inesistente
    Given l'utente è un "admin" di "PA2"
    And si tenta di recuperare la configurazione delle notifiche per tenant
    And si ottiene lo status code 200
    And la configurazione delle notifiche per tenant viene restituita
    When si tenta di modificare la configurazione delle notifiche per tenant specificando un valore inesistente
    And si ottiene lo status code 404
    Then modifica non applicata

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

  Scenario: [USER_CONFIG_UPDATE_1] Viene correttamente fatto l'update della configurazione delle notifiche per user
    Given l'utente è un "admin" di "PA2"
    And si tenta di recuperare la configurazione delle notifiche per user
    And si ottiene lo status code 200
    And la configurazione delle notifiche per user viene restituita
    When  si tenta di modificare la configurazione delle notifiche per user
    And la response ha status code 204
    Then modifica viene applicata

  Scenario: [USER_CONFIG_UPDATE_2] Configurazione delle notifiche per user inibita per token invalido
    Given l'utente è un "admin" di "PA2"
    And si tenta di recuperare la configurazione delle notifiche per user
    And si ottiene lo status code 200
    And la configurazione delle notifiche per user viene restituita
    And viene impostato per l'utente un token non valido
    When  si tenta di modificare la configurazione delle notifiche per user
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA2"
    Then modifica non applicata

  Scenario: [USER_CONFIG_UPDATE_3] Configurazione delle notifiche per user inibita per body invalido
    Given l'utente è un "admin" di "PA2"
    And si tenta di recuperare la configurazione delle notifiche per user
    And si ottiene lo status code 200
    And la configurazione delle notifiche per user viene restituita
    When  si tenta di modificare la configurazione delle notifiche per user specificando un valore invalido
    And si ottiene lo status code 400
    Then modifica non applicata

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

     #NOTA: La classe Boolean non permette la creazione di valori malformati, se non null, pertanto l'invio della request porta sempre ad un 400
  @ignore
  Scenario: [USER_CONFIG_UPDATE_5] Configurazione delle notifiche per user inibita per valore del body inesistente
    Given l'utente è un "admin" di "PA2"
    And si tenta di recuperare la configurazione delle notifiche per user
    And si ottiene lo status code 200
    And la configurazione delle notifiche per user viene restituita
    When si tenta di modificare la configurazione delle notifiche per user specificando un valore inesistente
    And si ottiene lo status code 404
    And si tenta di recuperare la configurazione delle notifiche per user
    And si ottiene lo status code 200
    Then modifica non applicata