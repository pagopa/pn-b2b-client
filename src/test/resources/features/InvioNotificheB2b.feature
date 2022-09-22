Feature: invio notifiche b2b

  @B2Btest
  Scenario: [B2B-PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
    And destinatario
      | denomination | Mario Cucumber |
    When la notifica viene inviata e si riceve una risposta
    Then la risposta di ricezione non presenta errori
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @B2Btest
  Scenario: [B2B-PA-SEND_2] Invio notifiche digitali mono destinatario (p.fisica)_scenario positivo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
      | idempotenceToken | AME2E3626070001.1  |
    And destinatario
      | denomination | Mario Cucumber |
    And la notifica viene inviata e si riceve una risposta
    And la risposta di ricezione non presenta errori
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.2"
    When la notifica viene inviata e si riceve una risposta
    Then la risposta di ricezione non presenta errori

  @B2Btest
  Scenario: [B2B-PA-SEND_3] invio notifiche digitali mono destinatario (p.fisica)_scenario negativo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | idempotenceToken | AME2E3626070001.1  |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
    And la notifica viene inviata e si riceve una risposta
    And la risposta di ricezione non presenta errori
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.1"
    When la notifica viene inviata
    Then l'operazione ha prodotto un errore con status code "409"

  @B2Btest
  Scenario: [B2B-PA-SEND_4] invio notifiche digitali mono destinatario (p.fisica)_scenario positivo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata e si riceve una risposta
    And la risposta di ricezione non presenta errori
    And viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
    When la notifica viene inviata e si riceve una risposta
    Then la risposta di ricezione non presenta errori

  Scenario: [B2B-PA-SEND_5] invio notifiche digitali mono destinatario (p.fisica)_scenario negativo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata e si riceve una risposta
    And la risposta di ricezione non presenta errori
    And viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso
    When la notifica viene inviata
    Then l'operazione ha prodotto un errore con status code "409"

  @B2Btest
  Scenario: [B2B-PA-SEND_6] download documento notificato_scenario positivo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
      | document | SI |
    And destinatario
      | denomination | Mario Cucumber |
    And la notifica viene inviata e si riceve una risposta
    And la risposta di ricezione non presenta errori
    When viene richiesto il download del documento "NOTIFICA"
    Then il download si conclude correttamente

  #Scenario in errore
  Scenario: [B2B-PA-SEND_7] invio notifica digitale mono destinatario (p.fisica)_scenario negativo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | aaa |
    When la notifica viene inviata
    Then l'operazione ha prodotto un errore con status code "400"

  Scenario: [B2B-PA-SEND_8] download documento pagopa_scenario positivo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
    And destinatario
      | denomination | Mario Cucumber |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    And la notifica viene inviata e si riceve una risposta
    And la risposta di ricezione non presenta errori
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente

  Scenario: [B2B-PA-SEND_9] download documento f24_flat_scenario positivo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
    And destinatario
      | denomination | Mario Cucumber |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    And la notifica viene inviata e si riceve una risposta
    And la risposta di ricezione non presenta errori
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente

  Scenario: [B2B-PA-SEND_10] download documento f24_standard_scenario positivo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination | Mario Cucumber |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    And la notifica viene inviata e si riceve una risposta
    And la risposta di ricezione non presenta errori
    When viene richiesto il download del documento "PAGOPA"
    Then il download si conclude correttamente

  Scenario: [B2B-PA-SEND_11] invio notifiche digitali mono destinatario senza physicalAddress (p.fisica)_scenario negativo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
    And destinatario
      | denomination | Mario Cucumber |
      | physicalAddress | NULL |
    When la notifica viene inviata
    Then l'operazione ha prodotto un errore con status code "400"

  Scenario: [B2B-PA-SEND_12] Invio notifica digitale mono destinatario e recupero tramite codice IUN_scenario negativo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Mario Cucumber |
    And la notifica viene inviata e si riceve una risposta
    And la risposta di ricezione non presenta errori
    When si tenta il recupero della notifica dal sistema tramite codice IUN "IUNUGYD-XHEZ-KLRM-202208-X-0"
    Then l'operazione ha prodotto un errore con status code "404"


  Scenario: [B2B-PA-SEND_13] Invio notifica digitale mono destinatario Flat_rate_scenario positivo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
      | feePolicy | FLAT_RATE |
    And destinatario
      | denomination | Mario Cucumber |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata e si riceve una risposta
    Then la risposta di ricezione non presenta errori

  Scenario: [B2B-PA-SEND_14] Invio notifica digitale mono destinatario Delivery_mode_scenario positivo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination | Mario Cucumber |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | NULL |
    When la notifica viene inviata e si riceve una risposta
    Then la risposta di ricezione non presenta errori

    #Scenario in errore
  Scenario: [B2B-PA-SEND_15] Invio notifica digitale mono destinatario DeliveryMode-Senza-F24_Standard_scenario negativo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
      | feePolicy | DELIVERY_MODE |
    And destinatario
      | denomination | Mario Cucumber |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata e si riceve una risposta
    Then l'operazione ha prodotto un errore con status code "400"

    #Scenario in errore
  Scenario: [B2B-PA-SEND_16] Invio notifica digitale mono destinatario FLAT_RATE-Senza-F24_FlatRate_scenario negativo
    Given viene generata una notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
      | feePolicy | FLAT_RATE |
    And destinatario
      | denomination | Mario Cucumber |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata e si riceve una risposta
    Then l'operazione ha prodotto un errore con status code "400"

