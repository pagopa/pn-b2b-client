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
    Then viene verificato che l'elemento di timeline "SCHEDULE_REFINEMENT" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_SUCCESS_WORKFLOW"
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "REFINEMENT" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |

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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "DIGITAL_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And viene verificato che l'elemento di timeline "PREPARE_SIMPLE_REGISTERED_LETTER" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And viene verificato che l'elemento di timeline "SEND_SIMPLE_REGISTERED_LETTER" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |

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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And viene verificato che l'elemento di timeline "SEND_ANALOG_DOMICILE" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |