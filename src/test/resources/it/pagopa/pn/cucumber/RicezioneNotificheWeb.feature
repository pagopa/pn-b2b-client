Feature: Ricezione notifiche api web con invio tramite api B2B

  @SmokeTest
  Scenario: [WEB-PF-RECIPIENT_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Cristoforo Colombo
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal destinatario

  @SmokeTest
  Scenario: [WEB-PF-RECIPIENT_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Cristoforo Colombo
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then il documento notificato può essere correttamente recuperato

  @SmokeTest
  Scenario: [WEB-PF-RECIPIENT_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Cristoforo Colombo e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato


  Scenario: [WEB-PF-RECIPIENT_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
      | feePolicy | FLAT_RATE |
    And destinatario Cristoforo Colombo e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato

  Scenario: [WEB-PF-RECIPIENT_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
      | feePolicy | DELIVERY_MODE |
    And destinatario Cristoforo Colombo e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato

  Scenario: [WEB-PF-RECIPIENT_6] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
      | feePolicy | DELIVERY_MODE |
    And destinatario Cristoforo Colombo e:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si tenta il recupero dell'allegato "F24"
    Then il download ha prodotto un errore con status code "404"

  Scenario: [WEB-PF-RECIPIENT_7] Invio notifica digitale altro destinatario e recupero tramite codice IUN API WEB_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario
      | denomination | Mario Cucumber |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si tenta il recupero della notifica da parte del destinatario
    Then il recupero ha prodotto un errore con status code "500"

  @SmokeTest
  Scenario: [WEB-PF-RECIPIENT_8] Invio notifica digitale altro destinatario e recupero allegato F24_STANDARD_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination | Mario Cucumber |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si tenta il recupero dell'allegato "F24"
    Then il download ha prodotto un errore con status code "404"

  Scenario: [WEB-PF-RECIPIENT_9] Invio notifica digitale altro destinatario e recupero allegato F24_FLAT_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
      | feePolicy | FLAT_RATE |
    And destinatario
      | denomination | Mario Cucumber |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si tenta il recupero dell'allegato "F24"
    Then il download ha prodotto un errore con status code "404"

  @SmokeTest
  Scenario: [WEB-PF-RECIPIENT_10] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario
      | denomination | Mario Cucumber |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si tenta il recupero dell'allegato "PAGOPA"
    Then il download ha prodotto un errore con status code "404"

  Scenario: [WEB-PF-RECIPIENT_11] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario Cristoforo Colombo
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca
    |||


  Scenario: [WEB-PF-RECIPIENT_12] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario Cristoforo Colombo
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca
      | subjectRegExp | cucumber |


  Scenario: [WEB-PF-RECIPIENT_13] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario Cristoforo Colombo
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca
      | startDate | 01/01/2022 |
      | subjectRegExp | cucumber |

  @SmokeTest
  Scenario: [WEB-PF-RECIPIENT_14] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario Cristoforo Colombo
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca
      | startDate | 01/01/2022 |
      | endDate | 01/10/2030 |
      | iunMatch | ACTUAL |
      | subjectRegExp | cucumber |

  #Scenario in errore
  #Scenario: [WEB-PF-RECIPIENT_15] Invio notifica digitale mono destinatario e recupero tramite ricerca solo IUN API WEB_scenario positivo
   # Given viene generata una notifica per il test di ricezione
    #  | subject | invio notifica con cucumber |
     # | senderDenomination | comune di milano |
     # | senderTaxId | 01199250158 |
    #And destinatario Cristoforo Colombo
    #When la notifica viene inviata e si riceve il relativo codice IUN valorizzato
    #Then la notifica può essere correttamente recuperata con una ricerca
     # | iunMatch | ACTUAL |

