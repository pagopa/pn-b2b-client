Feature: Ricezione notifiche api web con invio tramite api B2B multi destinatario

  @testLigth
  Scenario: [WEB-MULTI-PF-RECIPIENT_1] Invio notifica digitale multi destinatario e recupero tramite codice IUN API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata da "Mario Gherkin"
    And la notifica può essere correttamente recuperata da "Mario Cucumber"

  @testLigth
  Scenario: [WEB-MULTI-PF-RECIPIENT_2] Invio notifica digitale multi destinatario e recupero documento notificato_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then il documento notificato può essere correttamente recuperato da "Mario Gherkin"
    And il documento notificato può essere correttamente recuperato da "Mario Cucumber"

  Scenario: [WEB-MULTI-PF-RECIPIENT_3] Invio notifica digitale multi destinatario e recupero allegato pagopa_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Gherkin"
    And l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Cucumber"

  @ignore
  Scenario: [WEB-MULTI-PF-RECIPIENT_4] Invio notifica digitale multi destinatario e recupero allegato F24_FLAT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | FLAT_RATE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin"
    And l'allegato "F24" può essere correttamente recuperato da "Mario Cucumber"

  @ignore
  Scenario: [WEB-MULTI-PF-RECIPIENT_5] Invio notifica digitale multi destinatario e recupero allegato F24_STANDARD_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin"
    And l'allegato "F24" può essere correttamente recuperato da "Mario Cucumber"

  @ignore
  Scenario: [WEB-MULTI-PF-RECIPIENT_6] Invio notifica digitale multi destinatario e recupero allegato F24_FLAT_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | FLAT_RATE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" tenta il recupero dell'allegato "F24"
    Then il download ha prodotto un errore con status code "404"
    And "Mario Cucumber" tenta il recupero dell'allegato "F24"
    And il download ha prodotto un errore con status code "404"

  @ignore
  Scenario: [WEB-MULTI-PF-RECIPIENT_7] Invio notifica digitale multi destinatario e recupero allegato F24_STANDARD_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" tenta il recupero dell'allegato "F24"
    Then il download ha prodotto un errore con status code "404"
    And "Mario Cucumber" tenta il recupero dell'allegato "F24"
    And il download ha prodotto un errore con status code "404"

  @ignore
  Scenario: [WEB-MULTI-PF-RECIPIENT_8] Invio notifica digitale multi destinatario e recupero allegato F24_STANDARD_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" tenta il recupero dell'allegato "F24"
    Then il download ha prodotto un errore con status code "404"
    And l'allegato "F24" può essere correttamente recuperato da "Mario Cucumber"

  @ignore
  Scenario: [WEB-MULTI-PF-RECIPIENT_9] Invio notifica digitale multi destinatario e recupero allegato F24_FLAT_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | FLAT_RATE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" tenta il recupero dell'allegato "F24"
    Then il download ha prodotto un errore con status code "404"
    And l'allegato "F24" può essere correttamente recuperato da "Mario Cucumber"

  @ignore
  Scenario: [WEB-MULTI-PF-RECIPIENT_10] Invio notifica digitale multi destinatario e recupero allegato pagopa_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | feePolicy | DELIVERY_MODE |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | NULL |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Cucumber"
    Then "Mario Gherkin" tenta il recupero dell'allegato "PAGOPA"
    And il download ha prodotto un errore con status code "404"

  Scenario: [WEB-MULTI-PF-RECIPIENT_11] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca da "Mario Gherkin"
      |||
    And la notifica può essere correttamente recuperata con una ricerca da "Mario Cucumber"
      |||

  Scenario: [WEB-MULTI-PF-RECIPIENT_12] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca da "Mario Gherkin"
      | subjectRegExp | cucumber |
    And la notifica può essere correttamente recuperata con una ricerca da "Mario Cucumber"
      | subjectRegExp | cucumber |

  Scenario: [WEB-MULTI-PF-RECIPIENT_13] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca da "Mario Gherkin"
      | startDate | 01/01/2023 |
      | subjectRegExp | cucumber |
    And la notifica può essere correttamente recuperata con una ricerca da "Mario Cucumber"
      | startDate | 01/01/2023 |
      | subjectRegExp | cucumber |

  Scenario: [WEB-MULTI-PF-RECIPIENT_14] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca da "Mario Gherkin"
      | startDate | 01/01/2023 |
      | endDate | 01/10/2030 |
      | iunMatch | ACTUAL |
      | subjectRegExp | cucumber |
    And la notifica può essere correttamente recuperata con una ricerca da "Mario Cucumber"
      | startDate | 01/01/2023 |
      | endDate | 01/10/2030 |
      | iunMatch | ACTUAL |
      | subjectRegExp | cucumber |

  Scenario: [WEB-MULTI-PF-RECIPIENT_15] Invio notifica digitale multi destinatario e recupero tramite ricerca API WEB_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica non viene recuperata con una ricerca da "Mario Gherkin"
      | startDate | 01/01/2030 |
      | endDate | 01/10/2033 |
      | subjectRegExp | cucumber |
    And la notifica può essere correttamente recuperata con una ricerca da "Mario Cucumber"
      | startDate | 01/01/2023 |
      | subjectRegExp | cucumber |
