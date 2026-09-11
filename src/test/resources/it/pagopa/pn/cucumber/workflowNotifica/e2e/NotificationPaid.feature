Feature: Il pagamento esterno non sostituisce la consegna della notifica

  # Regola di business
  # PAYMENT conferma l'estinzione del debito, ma non dimostra che il destinatario
  # abbia visualizzato la notifica su SEND. Il solo PAYMENT non blocca il flusso;
  # NOTIFICATION_VIEWED e la cancellazione continuano invece a bloccare gli invii successivi.
  #
  # Perimetro: nuovi flussi standard, un destinatario e un avviso PagoPA.
  # Esclusi: restart del pregresso e timeout analogico.

  @e2e @postPaymentWorkflow
  Scenario Outline: [E2E-WF-POST-PAYMENT-1.1] Il pagamento non blocca il primo invio analogico <prodotto>
    Given viene generata una nuova notifica
      | subject               | pagamento esterno prima del primo invio |
      | senderDenomination    | Comune di Milano                       |
      | feePolicy             | DELIVERY_MODE                          |
      | physicalCommunication | <prodotto>                             |
    And destinatario
      | denomination            | Leonardo da Vinci |
      | taxId                   | DVNLRD52D15M059P  |
      | digitalDomicile         | NULL              |
      | physicalAddress_address | <indirizzo>        |
      | payment_pagoPaForm      | SI                |
      | payment_f24             | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And l'evento "SEND_ANALOG_DOMICILE" al tentativo 0 è successivo all'evento "PAYMENT"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


    Examples:
      | prodotto               | indirizzo                      |
      | AR_REGISTERED_LETTER   | Via@OK_AR                      |
      | REGISTERED_LETTER_890  | Via@OK-CompiutaGiacenza_890    |

  @e2e @postPaymentWorkflow
  Scenario Outline: [E2E-WF-POST-PAYMENT-1.2] Il pagamento non blocca il secondo tentativo analogico <prodotto>
    Given viene generata una nuova notifica
      | subject               | pagamento esterno prima del secondo tentativo |
      | senderDenomination    | Comune di Milano                             |
      | feePolicy             | DELIVERY_MODE                                |
      | physicalCommunication | <prodotto>                                   |
    And destinatario
      | denomination            | Leonardo da Vinci |
      | taxId                   | DVNLRD52D15M059P  |
      | digitalDomicile         | NULL              |
      | physicalAddress_address | <indirizzo>        |
      | payment_pagoPaForm      | SI                |
      | payment_f24             | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    Then viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline            | true     |
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |
      | details_responseStatus  | KO       |
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And l'evento "SEND_ANALOG_DOMICILE" al tentativo 1 è successivo all'evento "PAYMENT"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" al tentativo "ATTEMPT_1"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

    Examples:
      | prodotto               | indirizzo                |
      | AR_REGISTERED_LETTER   | Via@FAIL-Discovery_AR     |
      | REGISTERED_LETTER_890  | Via@FAIL-Discovery_890    |

  @e2e @postPaymentWorkflow
  Scenario: [E2E-WF-POST-PAYMENT-1.3] Il pagamento non blocca la raccomandata semplice dopo il fallimento digitale
    Given viene generata una nuova notifica
      | subject            | pagamento esterno prima della raccomandata semplice |
      | senderDenomination | Comune di Milano                                  |
      | feePolicy          | DELIVERY_MODE                                     |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@ok_RS          |
      | payment_pagoPaForm      | SI                 |
      | payment_f24             | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_SIMPLE_REGISTERED_LETTER"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And l'evento "SEND_SIMPLE_REGISTERED_LETTER" è successivo all'evento "PAYMENT"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

  @e2e @postPaymentWorkflow @regression
  Scenario Outline: [E2E-WF-POST-PAYMENT-2.1] La visualizzazione blocca il primo invio analogico <prodotto> dopo il pagamento
    Given viene generata una nuova notifica
      | subject               | visualizzazione prima del primo invio |
      | senderDenomination    | Comune di Milano                     |
      | feePolicy             | DELIVERY_MODE                        |
      | physicalCommunication | <prodotto>                           |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | digitalDomicile         | NULL              |
      | physicalAddress_address | <indirizzo>        |
      | payment_pagoPaForm      | SI                |
      | payment_f24             | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT"
    And la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then viene controllato che l'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE" non esiste
    Then viene controllato che l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" non esiste

    Examples:
      | prodotto               | indirizzo     |
      | AR_REGISTERED_LETTER   | Via@OK_AR     |
      | REGISTERED_LETTER_890  | Via@ok_890    |

  @e2e @postPaymentWorkflow @regression
  Scenario Outline: [E2E-WF-POST-PAYMENT-2.2] La visualizzazione blocca il secondo tentativo analogico <prodotto> dopo il pagamento
    Given viene generata una nuova notifica
      | subject               | visualizzazione prima del secondo tentativo |
      | senderDenomination    | Comune di Milano                           |
      | feePolicy             | DELIVERY_MODE                              |
      | physicalCommunication | <prodotto>                                 |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | digitalDomicile         | NULL              |
      | physicalAddress_address | <indirizzo>        |
      | payment_pagoPaForm      | SI                |
      | payment_f24             | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline            | true     |
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |
      | details_responseStatus  | KO       |
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT"
    And la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_DOMICILE" della notifica sia di 1
    Then viene verificato che non esista l'elemento "PREPARE_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"

    Examples:
      | prodotto               | indirizzo                |
      | AR_REGISTERED_LETTER   | Via@FAIL-Discovery_AR     |
      | REGISTERED_LETTER_890  | Via@FAIL-Discovery_890    |

  @e2e @postPaymentWorkflow @regression
  Scenario: [E2E-WF-POST-PAYMENT-2.3] La visualizzazione blocca la raccomandata semplice dopo il pagamento
    Given viene generata una nuova notifica
      | subject            | visualizzazione prima della raccomandata semplice |
      | senderDenomination | Comune di Milano                                  |
      | feePolicy          | DELIVERY_MODE                                     |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@ok_RS          |
      | payment_pagoPaForm      | SI                 |
      | payment_f24             | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT"
    And la notifica può essere correttamente recuperata da "Cristoforo Colombo"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then viene controllato che l'elemento di timeline della notifica "PREPARE_SIMPLE_REGISTERED_LETTER" non esiste
    Then viene controllato che l'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" non esiste

  @e2e @postPaymentWorkflow @regression
  Scenario Outline: [E2E-WF-POST-PAYMENT-3.1] L'annullamento blocca il primo invio analogico <prodotto> dopo il pagamento
    Given viene generata una nuova notifica
      | subject               | annullamento prima del primo invio |
      | senderDenomination    | Comune di Milano                  |
      | feePolicy             | DELIVERY_MODE                     |
      | physicalCommunication | <prodotto>                        |
    And destinatario
      | denomination            | Leonardo da Vinci |
      | taxId                   | DVNLRD52D15M059P  |
      | digitalDomicile         | NULL              |
      | physicalAddress_address | <indirizzo>        |
      | payment_pagoPaForm      | SI                |
      | payment_f24             | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then viene controllato che l'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE" non esiste
    Then viene controllato che l'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" non esiste

    Examples:
      | prodotto               | indirizzo     |
      | AR_REGISTERED_LETTER   | Via@OK_AR     |
      | REGISTERED_LETTER_890  | Via@ok_890    |

  @e2e @postPaymentWorkflow @regression
  Scenario Outline: [E2E-WF-POST-PAYMENT-3.2] L'annullamento blocca il secondo tentativo analogico <prodotto> dopo il pagamento
    Given viene generata una nuova notifica
      | subject               | annullamento prima del secondo tentativo |
      | senderDenomination    | Comune di Milano                       |
      | feePolicy             | DELIVERY_MODE                          |
      | physicalCommunication | <prodotto>                             |
    And destinatario
      | denomination            | Leonardo da Vinci |
      | taxId                   | DVNLRD52D15M059P  |
      | digitalDomicile         | NULL              |
      | physicalAddress_address | <indirizzo>        |
      | payment_pagoPaForm      | SI                |
      | payment_f24             | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    Then viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline            | true     |
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |
      | details_responseStatus  | KO       |
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PAYMENT"
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And viene verificato che il numero di elementi di timeline "SEND_ANALOG_DOMICILE" della notifica sia di 1
    Then viene verificato che non esista l'elemento "PREPARE_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"

    Examples:
      | prodotto               | indirizzo                |
      | AR_REGISTERED_LETTER   | Via@FAIL-Discovery_AR     |
      | REGISTERED_LETTER_890  | Via@FAIL-Discovery_890    |

  @e2e @postPaymentWorkflow @regression
  Scenario: [E2E-WF-POST-PAYMENT-3.3] L'annullamento blocca la raccomandata semplice dopo il pagamento
    Given viene generata una nuova notifica
      | subject            | annullamento prima della raccomandata semplice |
      | senderDenomination | Comune di Milano                              |
      | feePolicy          | DELIVERY_MODE                                 |
    And destinatario
      | denomination            | Cristoforo Colombo |
      | taxId                   | CLMCST42R12D969Z   |
      | digitalDomicile_address | test@fail.it       |
      | physicalAddress_address | Via@ok_RS          |
      | payment_pagoPaForm      | SI                 |
      | payment_f24             | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And l'avviso pagopa viene pagato correttamente
    And si attende il corretto pagamento della notifica
    And la notifica può essere annullata dal sistema tramite codice IUN
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then viene controllato che l'elemento di timeline della notifica "PREPARE_SIMPLE_REGISTERED_LETTER" non esiste
    Then viene controllato che l'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER" non esiste
