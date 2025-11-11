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
    And lista di notifiche viene restituita e combacia con i record creati
    And l'utente tenta di eliminare le notifiche create
    And si ottiene lo status code 204
    And l'utente tenta di recuperare la lista di notifiche
    Then le notifiche sono state eliminate

  Scenario: [NOTIFICATION_BULK_DELETE_2] Eliminazione massiva di notifiche con token invalido (Scenario 4)
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare la lista di notifiche
    Given viene impostato per l'utente un token non valido
    When l'utente tenta di eliminare tutte le notifiche
    Then si ottiene lo status code 401
    And nessuna notifica è stata eliminata

  Scenario: [NOTIFICATION_BULK_DELETE_3] Eliminazione massiva di notifiche con ID inesistente (Scenario 5)
    Given l'utente è un "admin" di "PA1"
    And l'utente tenta di recuperare la lista di notifiche
    When l'utente tenta di eliminare una notifica con id inesistente
    Then si ottiene lo status code 404
    And nessuna notifica è stata eliminata

  Scenario: [NOTIFICATION_BULK_DELETE_4] Eliminazione massiva di notifiche con ID invalido (Scenario 6)
    Given l'utente è un "admin" di "PA1"
    And l'utente tenta di recuperare la lista di notifiche
    When l'utente tenta di eliminare una notifica con id invalido
    Then si ottiene lo status code 400
    And nessuna notifica è stata eliminata

  Scenario Outline: [NOTIFICATION_BULK_DELETE_5] Eliminazione massiva di notifiche con ruoli non autorizzati (Scenario 7)
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta di recuperare la lista di notifiche
    Given l'utente è un "<role>" di "PA1"
    When l'utente tenta di eliminare tutte le notifiche
    Then si ottiene lo status code 403
    And nessuna notifica è stata eliminata

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_BULK_READ_1] Lettura massiva di determinate notifiche (Scenario 8)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And lista di notifiche viene restituita e combacia con i record creati
    And l'utente tenta di leggere le notifiche create
    And si ottiene lo status code 200
    And l'utente tenta di recuperare la lista di notifiche
    Then le notifiche create sono state lette

  Scenario: [NOTIFICATION_BULK_READ_2] Lettura massivo con token invalido (Scenario 9)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And lista di notifiche viene restituita e combacia con i record
    And viene impostato per l'utente un token non valido
    And l'utente tenta di leggere le notifiche create
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    And l'utente tenta di recuperare la lista di notifiche
    Then le notifiche create non sono state lette

  Scenario: [NOTIFICATION_BULK_READ_3] Lettura massiva di notifiche con ID inesistente (Scenario 10)
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta di leggere una notifica con id inesistente
    Then si ottiene lo status code 404

  Scenario: [NOTIFICATION_BULK_READ_4] Lettura massiva di notifiche con ID invalido (Scenario 11)
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta di leggere una notifica con id invalido
    Then si ottiene lo status code 400

  Scenario Outline: [NOTIFICATION_BULK_READ_5] Lettura massivo con ruolo non autorizzato (Scenario 12)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 2 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And lista di notifiche viene restituita e combacia con i record
    And l'utente è un "<role>" di "PA1"
    And si ottiene lo status code 401
    And l'utente è un "admin" di "PA1"
    And l'utente tenta di recuperare la lista di notifiche create
    Then le notifiche create non sono state lette

    Examples:
      | role    |
      | support |

  Scenario: [NOTIFICATION_SINGLE_READ_1] Lettura di una specifica notifica (Scenario 13)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And lista di notifiche viene restituita e combacia con i record creati
    And l'utente tenta di leggere la notifica creata
    And si ottiene lo status code 204
    And l'utente tenta di recuperare la lista di notifiche
    Then la notifica creata è stata letta

  Scenario: [NOTIFICATION_SINGLE_READ_2] Doppia lettura di una specifica notifica (Scenario 14)
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già generato 1 notifiche
    When l'utente tenta di recuperare la lista di notifiche create
    And lista di notifiche viene restituita e combacia con i record creati
    And l'utente tenta di leggere la notifica creata
    And si ottiene lo status code 204
    And l'utente tenta di recuperare la lista di notifiche create
    Then la notifica creata è stata letta
    And l'utente tenta di leggere la notifica creata
    And si ottiene lo status code 204
    And l'utente tenta di recuperare la lista di notifiche create
    Then la notifica creata è stata letta



