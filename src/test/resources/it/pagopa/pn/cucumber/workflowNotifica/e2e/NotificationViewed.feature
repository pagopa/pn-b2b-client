Feature: Notifica visualizzata

  @e2e
  Scenario: [E2E-NOTIFICATION-VIEWED-4] A valle della visualizzazione della notifica non deve essere generato un nuovo elemento di timeline NOTIFICATION VIEWED
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    And viene effettuato un controllo sulla durata della retention di "PAGOPA"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then viene verficato che il numero di elementi di timeline "NOTIFICATION_VIEWED" della notifica sia di 1

  @e2e
  Scenario: [E2E-WF-INHIBITION-3] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
    La notifica viene letta subito dopo la generazione dell'evento di timeline SCHEDULE_REFINEMENT. Questa lettura non deve generare
    un evento di timeline REFINEMENT.
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "REFINEMENT"

  @e2e
  Scenario: [E2E-WF-INHIBITION-4] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
  La notifica viene letta subito dopo la generazione dell'evento di timeline SCHEDULE_ANALOG_WORKFLOW. Questa lettura non deve generare
  un evento di timeline PREPARE_SIMPLE_REGISTERED_LETTER e SEND_SIMPLE_REGISTERED_LETTER.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "PREPARE_SIMPLE_REGISTERED_LETTER"
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"

  @e2e
  Scenario: [E2E-WF-INHIBITION-5] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
  La notifica viene letta subito dopo essere stata accettata. Questa lettura non deve generare un evento di timeline SEND_ANALOG_DOMICILE.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_ANALOG_DOMICILE"