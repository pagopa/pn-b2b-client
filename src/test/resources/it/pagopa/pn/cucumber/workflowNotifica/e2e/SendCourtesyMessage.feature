Feature: Invio messaggi cortesia e2e

  @e2e
  Scenario: [E2E-SEND-COURTESY-MESSAGE-4] Invio notifica mono destinatario con messaggio di cortesia non configurato
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Dino De Sauro
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_COURTESY_MESSAGE"