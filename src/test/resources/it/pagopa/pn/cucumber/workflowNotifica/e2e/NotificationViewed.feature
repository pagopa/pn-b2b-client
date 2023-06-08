Feature: Notifica visualizzata

  @e2e @ignore
  Scenario: [E2E-WF-INHIBITION-3] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    Then vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "REFINEMENT"

  @e2e @ignore
  Scenario: [E2E-WF-INHIBITION-4] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    Then vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "PREPARE_SIMPLE_REGISTERED_LETTER"
    Then vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"

  @e2e @ignore
  Scenario: [E2E-WF-INHIBITION-5] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
    And destinatario Mario Cucumber e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    Then vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "SEND_ANALOG_DOMICILE"