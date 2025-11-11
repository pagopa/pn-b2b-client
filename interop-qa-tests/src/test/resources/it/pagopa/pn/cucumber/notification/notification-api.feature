Feature: API CRUD Notifiche

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
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di eliminare le notifiche recuperate
    And si ottiene lo status code 204
    Then le notifiche create sono state eliminate

  Scenario: [NOTIFICATION_BULK_DELETE_2] Eliminazione massiva di notifiche con token invalido (Scenario 4)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    Given viene impostato per l'utente un token non valido
    And l'utente tenta di eliminare le notifiche recuperate
    Then si ottiene lo status code 401
    And nessuna notifica è stata eliminata

  Scenario: [NOTIFICATION_BULK_DELETE_3] Eliminazione massiva di notifiche con ID inesistente (Scenario 5)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di eliminare delle notifiche con almeno un id inesistente
    Then si ottiene lo status code 404
    And nessuna notifica è stata eliminata

  Scenario: [NOTIFICATION_BULK_DELETE_4] Eliminazione massiva di notifiche con ID invalido (Scenario 6)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di eliminare delle notifiche con almeno un id invalido
    Then si ottiene lo status code 400
    And nessuna notifica è stata eliminata

  Scenario Outline: [NOTIFICATION_BULK_DELETE_5] Eliminazione massiva di notifiche con ruoli non autorizzati (Scenario 7)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 2 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    Given l'utente è un "<role>" di "PA1"
    When l'utente tenta di eliminare le notifiche recuperate
    Then si ottiene lo status code 403
    And nessuna notifica è stata eliminata

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_BULK_READ_1] Lettura massiva di determinate notifiche (Scenario 8)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 200
    And l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "read"

  Scenario: [NOTIFICATION_BULK_READ_2] Lettura massivo con token invalido (Scenario 9)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And viene impostato per l'utente un token non valido
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    And l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "unread"

  Scenario: [NOTIFICATION_BULK_READ_3] Lettura massiva di notifiche con ID inesistente (Scenario 10)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    When l'utente tenta di leggere le notifiche recuperate specificando almeno un id inesistente
    Then si ottiene lo status code 404
    And l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "unread"

  Scenario: [NOTIFICATION_BULK_READ_4] Lettura massiva di notifiche con ID invalido (Scenario 11)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    And l'utente tenta di recuperare la lista di notifiche create
    When l'utente tenta di leggere le notifiche recuperate specificando almeno un id invalido
    Then si ottiene lo status code 400
    And l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "unread"

  Scenario Outline: [NOTIFICATION_BULK_READ_5] Lettura massivo con ruolo non autorizzato (Scenario 12)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "<role>" di "PA1"
    And l'utente tenta di leggere le notifiche recuperate
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    And l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "unread"

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_SINGLE_READ_1] Lettura di una specifica notifica (Scenario 13)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere la notifica recuperata specificando un id valido
    And si ottiene lo status code 204
    Then le notifiche recuperate sono nello stato "read"

  Scenario: [NOTIFICATION_SINGLE_READ_2] Doppia lettura di una specifica notifica (Scenario 14)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere la notifica recuperata specificando un id valido
    And si ottiene lo status code 204
    And l'utente tenta di recuperare la lista di notifiche create
    And le notifiche recuperate sono nello stato "read"
    And l'utente tenta di leggere la notifica recuperata specificando un id valido
    And si ottiene lo status code 204
    And l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "read"

  Scenario: [NOTIFICATION_SINGLE_READ_3] Lettura di una specifica notifica con token invalido (Scenario 15)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And viene impostato per l'utente un token non valido
    And l'utente tenta di leggere la notifica recuperata specificando un id valido
    And si ottiene lo status code 401
    When l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "unread"

  Scenario: [NOTIFICATION_SINGLE_READ_4] Lettura di una specifica notifica con id inesistente (Scenario 16)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere la notifica recuperata specificando un id inesistente
    And si ottiene lo status code 404
    When l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "unread"

  Scenario: [NOTIFICATION_SINGLE_READ_5] Lettura di una specifica notifica con id invalido (Scenario 17)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di leggere la notifica recuperata specificando un id invalido
    And si ottiene lo status code 400
    When l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "unread"

  Scenario Outline: [NOTIFICATION_SINGLE_READ_6] Lettura di una specifica notifica con ruolo non autorizzato (Scenario 18)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "<role>" di "PA1"
    And l'utente tenta di leggere la notifica recuperata specificando un id valido
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "unread"

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_SINGLE_READ_7] Lettura di una specifica notifica con un utente diverso dal destinatario (Scenario 19)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "admin" di "PA2"
    And l'utente tenta di leggere la notifica recuperata specificando un id valido
    And si ottiene lo status code 404
    And l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche recuperate sono nello stato "unread"

  Scenario: [NOTIFICATION_SINGLE_DELETE_1] Eliminazione di una specifica notifica (Scenario 20)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di eliminare la notifica recuperata specificando un id valido
    And si ottiene lo status code 204
    Then le notifiche create sono state eliminate

  Scenario: [NOTIFICATION_SINGLE_DELETE_2] Eliminazione di una specifica notifica con token invalido (Scenario 21)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And viene impostato per l'utente un token non valido
    And l'utente tenta di eliminare la notifica recuperata specificando un id valido
    And si ottiene lo status code 401
    Then nessuna notifica è stata eliminata

  Scenario: [NOTIFICATION_SINGLE_DELETE_3] Eliminazione di una specifica notifica con id inesistente (Scenario 22)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di eliminare la notifica recuperata specificando un id inesistente
    And si ottiene lo status code 404
    Then nessuna notifica è stata eliminata

  Scenario: [NOTIFICATION_SINGLE_DELETE_4] Eliminazione di una specifica notifica con id invalido (Scenario 23)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di eliminare la notifica recuperata specificando un id invalido
    And si ottiene lo status code 400
    Then nessuna notifica è stata eliminata

  Scenario Outline: [NOTIFICATION_SINGLE_DELETE_5] Eliminazione di una specifica notifica con ruolo non autorizzato (Scenario 24)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "<role>" di "PA1"
    And l'utente tenta di eliminare la notifica recuperata specificando un id invalido
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    Then nessuna notifica è stata eliminata

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_SINGLE_DELETE_6] Doppia eliminazione di una specifica notifica (Scenario 25)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente tenta di eliminare la notifica recuperata specificando un id valido
    And si ottiene lo status code 204
    Then le notifiche create sono state eliminate
    And l'utente tenta di eliminare la notifica recuperata specificando un id valido
    And si ottiene lo status code 404

  Scenario: [NOTIFICATION_SINGLE_DELETE_7] Eliminazione di una specifica notifica con un utente diverso dal destinatario (Scenario 26)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And l'utente è un "admin" di "PA2"
    And l'utente tenta di eliminare la notifica recuperata specificando un id valido
    And si ottiene lo status code 404
    And l'utente è un "admin" di "PA1"
    Then nessuna notifica è stata eliminata




