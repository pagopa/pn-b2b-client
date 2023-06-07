Feature: Notifica pagata

  @e2e
  Scenario: [E2E-WF-INHIBITION-PAID-3] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "REFINEMENT"

  @e2e
  Scenario: [E2E-WF-INHIBITION-PAID-4] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RS |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "PREPARE_SIMPLE_REGISTERED_LETTER"
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"