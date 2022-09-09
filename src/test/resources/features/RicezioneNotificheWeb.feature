Feature: Ricezione notifiche web

  Scenario: [WEB-PF-RECIPIENT_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN API WEB_scenario positivo
    Given viene generata una notifica per il test di ricezione
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
    And destinatario Cristoforo Colombo
    When la notifica viene inviata e si riceve il relativo codice IUN valorizzato
    Then la notifica può essere correttamente recuperata dal destinatario

  Scenario: [WEB-PF-RECIPIENT_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo
    Given viene generata una notifica per il test di ricezione
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
    And destinatario Cristoforo Colombo
    When la notifica viene inviata e si riceve il relativo codice IUN valorizzato
    Then il documento notificato può essere correttamente recuperato

  Scenario: [WEB-PF-RECIPIENT_3] Invio notifica digitale mono destinatario e recupero allegato pagamento_scenario positivo
    Given viene generata una notifica per il test di ricezione
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | CFComuneMilano |
    And destinatario Cristoforo Colombo and:
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata e si riceve il relativo codice IUN valorizzato
    Then l'allegato di pagamento può essere correttamente recuperato
