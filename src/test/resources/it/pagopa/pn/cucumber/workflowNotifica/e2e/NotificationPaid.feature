Feature: Notifica pagata

  @e2e
  Scenario: [E2E-WF-INHIBITION-PAID-3] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
  Viene effettuato il pagamento subito dopo la generazione dell'evento di timeline SCHEDULE_REFINEMENT. Il pagamento non deve generare
  un evento di timeline REFINEMENT.
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
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
  Viene effettuato il pagamento subito dopo la generazione dell'evento di timeline DIGITAL_FAILURE_WORKFLOW. Il pagamento non deve generare
  un evento di timeline PREPARE_SIMPLE_REGISTERED_LETTER e SEND_SIMPLE_REGISTERED_LETTER.
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "PREPARE_SIMPLE_REGISTERED_LETTER"
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_SIMPLE_REGISTERED_LETTER"

  @e2e
  Scenario: [E2E-WF-INHIBITION-PAID-5] Casistica in cui la visualizzazione di una notifica inibisce parte del workflow di notifica.
  Viene effettuato il pagamento subito dopo che la notifica è stata accettata. Il pagamento non deve generare un evento di timeline SEND_ANALOG_DOMICILE.
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di Milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_RS |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    Then vengono letti gli eventi e verificho che l'utente 0 non abbia associato un evento "SEND_ANALOG_DOMICILE"