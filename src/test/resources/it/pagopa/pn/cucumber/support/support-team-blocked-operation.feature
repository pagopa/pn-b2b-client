Feature: Test suite relativa ad utenze con ruolo di supporto

  Scenario Outline: [SUPPORTO_1] Verifica che un ente con ruolo di supporto non possa effettuare operazioni a lui non consentite
    When viene invocata la seguente API: "<api>" dal team supporto
    Then il ruolo supporto non ha accesso all'API e riceve un errore di autorizzazione
    Examples:
      | api                         |
      | newSentNotification         |
      | changeAdditionalLanguage    |
      | notificationCancellation    |
      | getApiKeys                  |
      | newApiKey                   |
      | changeStatusApiKey          |
      | deleteApiKeys               |

  Scenario Outline: [SUPPORTO_2] Verifica che un ente con ruolo di supporto possa effettuare operazioni a lui consentite
    When viene invocata la seguente API: "<api>" dal team supporto
    Then il ruolo supporto ha accesso all'API e riceve una risposta valida
    Examples:
      | api                         |
      | searchSentNotification      |
      | getSentNotification         |
      | getSentNotificationDocument |
#      | getSentNotificationPayment  |
#      | getDashboardData            |